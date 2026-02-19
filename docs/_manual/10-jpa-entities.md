---
title: JPA entities
permalink: /manual/jpa-entities/
---
EqualsVerifier has support for JPA entities.

## Entities

JPA entities are mutable by design. Since adding `.suppress(Warning.NONFINAL_FIELDS)` to each test can get cumbersome, EqualsVerifier will do this implicitly for each class marked with an `@Entity`, `@Embeddable` or `@MappedSuperclass` annotation.

JPA entities are also not allowed to be final, and even a final `equals` or `hashCode` method [is problematic](https://stackoverflow.com/questions/6608222/does-a-final-method-prevent-hibernate-from-creating-a-proxy-for-such-an-entity). Therefore, EqualsVerifier will not enforce these for JPA entities, like it normally would. Note that this means that your class will be vulnerable to subclasses [breaking `equals`](/equalsverifier/manual/final).

## Ids

By default, EqualsVerifier assumes that your entities have a [business or natural key](https://en.wikipedia.org/wiki/Natural_key). Consequently, all fields that are marked with the `@Id` annotation are assumed not to participate in the class's `equals` and `hashCode` methods. For all other fields, EqualsVerifier behaves as usual.

EqualsVerifier also supports Hibernate's `@NaturalId` annotation. If it detects the presence of this annotation in a class, it will assume that _only_ the fields marked with `@NaturalId` participate in `equals` and `hashCode`, and that all other fields (including the ones marked with `@Id`) do not.

If your class has a [surrogate key](https://en.wikipedia.org/wiki/Surrogate_key), you can tell EqualsVerifier by suppressing `Warning.SURROGATE_KEY`. When this warning is suppressed, EqualsVerifier assumes that _only_ the field or fields marked with `@Id` participate in `equals` and `hashCode`, and that none of the other fields do.

If your class has a surrogate key marked with `@GeneratedValue`, EqualsVerifier enforces that you call the id's getter in `equals` and `hashCode`, instead of referencing the field directly. This is because when JPA generates an id, it may only reflect this in the object when the getter is called. The underlying field may still be null, even though an id was generated. To avoid this, `equals` and `hashCode` should always call the getter in this situation.

When `@NaturalId` is present or when `Warning.SURROGATE_KEY` is suppressed, there is no need to call `#withOnlyTheseFields` or `#withIgnoredFields`.

EqualsVerifier will not only detect these annotations when they are placed on a field, but also when they are placed on the field's corresponding accessor method.

If your class has a business key, but no separate field to serve as `@Id`, you can tell EqualsVerifier by suppressing `Warning.SURROGATE_OR_BUSINESS_KEY`. For instance, if your entity models a person, and the field `socialSecurityNumber` is marked with `@Id`, you can use `Warning.SURROGATE_OR_BUSINESS_KEY` to include `socialSecurityNumber` and other fields like `name` and `birthDate` in `equals` and `hashCode`.

In order to meet the consistency requirements when implementing a class with a surrogate key, some argue that it [is necessary to make the `hashCode` constant](https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/). EqualsVerifier still requires a 'normal' `hashCode` implementation. If you want a constant `hashCode`, you can suppress `Warning.STRICT_HASHCODE`.

## Ids and new objects

A common pattern in JPA when deciding whether two objects are equal, is to look at their fields only if the object hasn't been persisted yet. If it has been persisted, the field has an id, and then the fields are ignored and only the id is used to decide. Such an `equals` method might look like this:

{% highlight java %}
@Override
public boolean equals(Object obj) {
    if (!(obj instanceof Foo)) {
        return false;
    }
    Foo other = (Foo)obj;
    if (id == 0L && other.id == 0L) {
        return false;
    }
    return id == other.id;
}
{% endhighlight %}

You might see an error message such as this one:

    Reflexivity: entity does not equal an identical copy of itself:
      Foo@123456
    If this is intentional, consider suppressing Warning.IDENTICAL_COPYFOR_VERSIONED_ENTITY

In that case, you can call `suppress(Warning.SURROGATE_KEY, Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)`: `SURROGATE_KEY` because the identity is based on the entity's keys, and `IDENTICAL_COPY_FOR_VERSIONED_ENTITY` to allow the (small) breach in reflexivity.

Similarly, if the entity has a business key, like so:

{% highlight java %}
@Override
public boolean equals(Object obj) {
    if (!(obj instanceof Foo)) {
        return false;
    }
    Foo other = (Foo)obj;
    if (id == 0L && other.id == 0L) {
        return false;
    }
    return Objects.equals(someField, other.someField);
}
{% endhighlight %}

You will get the same error message. In this case you can simply call `suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)`, without also suppressing `Warning.SURROGATE_KEY`.

## Materialized fields

Some fields have a mapping annotation that links them with data from a different database table or entity. These annotations include `@OneToMany`, `@ManyToOne` and `@ManyToMany`. In certain situations, you can have an instance where these fields are not materialized yet. In other words, they're not fetched from the database, and their content is undefined. Most often, this happens when they have `fetchType = FetchType.LAZY`, but even with `FetchType.EAGER`, it can happen that they are not yet materialized. This also applies to fields with `@Basic(fetchType = FetchType.LAZY)`. JPA will materialize this data on demand. For example, when the getter for such a field is called, JPA is triggered and queries the data. However, this trigger does not happen when the field is referenced directly.

Therefore, when these fields are used in `equals` and `hashCode`, it's important to call their getter method instead of referencing the field directly. Otherwise, the data may not be materialized, and it's possible that calling `equals` on two equal objects returns `false`, because one instance doesn't have the content yet while the other does.

EqualsVerifier checks for these fields that their getter is used. If they're referenced directly, EqualsVerifier will fail. Note that this can be disabled by suppressing `Warning.JPA_GETTER`.

Note also that the getter must be non-final, otherwise EqualsVerifier is unable to perform this check. If that's problematic, you can disable the check completely by suppressing `Warning.JPA_GETTER`.

By default, EqualsVerifier assumes that the JavaBeans conventions are used to determine the name of the getter. For example, if a field is called `employee`, it assumes that the getter is called `getEmployee()`. If your project uses a different convention, you can use `#withFieldnameToGetterConverter()` to override that behavior.

For example, if in your project, a field must have a prefix, like so: `m_employee`, but the getter is still `getEmployee()`, you might call EqualsVerifier like this:

{% highlight java %}
EqualsVerifier
    .forClass(Foo.class)
    .withFieldnameToGetterConverter(
        fn -> "get" + Character.toUpperCase(fn.charAt(2)) + fn.substring(3)
    )
    .verify();
{% endhighlight %}

This will chop off the `m_` prefix, uppercase the first letter, and prepend the word `get`.

## Transient fields

Since fields marked with the `@Transient` annotation are not persisted, they should generally not participate in `equals` and `hashCode` either. Therefore, EqualsVerifier will implicitly call [`withIgnoredFields`](/equalsverifier/manual/ignoring-fields) for these fields.

If they do participate, EqualsVerifier will fail the test. This behavior can be avoided by suppressing `Warning.TRANSIENT_FIELDS`.

## Abstract superclass

A frequent pattern is to have an abstract superclass for all entities, and implement `equals` and `hashCode` there for all concrete entity implementations. Usually the objects are compared by their ids only. The fields are left out of the comparison, even is the object is new.

In that case, the easiest way to make it work, is by making the `equals` and `hashCode` methods in the abstract class final, like this:

{% highlight java %}
@MappedSuperclass
abstract class AbstractEntity {
    @Id
    @Column(...)
    private Long id;

    public Long getId() {
        return id;
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity)) {
            return false;
        }
        return Objects.equals(getId(), ((AbstractEntity)obj).getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(getId());
    }
}

@Entity
class Foo extends AbstractEntity {
    @Column(...)
    private String name;
}
{% endhighlight %}

You don't have to tell EqualsVerifier anything about inheritance this way. You do have to tell it to only inspect `id` when testing `Foo`, though:

{% highlight java %}
@Test
public void testAbstractEntity() {
    EqualsVerifier.forClass(AbstractEntity.class)
            .verify();
}

@Test
public void testFoo() {
    EqualsVerifier.forClass(Foo.class)
            .withOnlyTheseFields("id")
            .verify();
}
{% endhighlight %}

If you want to have an abstract superclass implementing `equals`, but also include the subclass's fields in it, I recommend reading the [page about inheritance](/equalsverifier/manual/inheritance) and taking it from there.

## Disabling JPA checking

If, for some reason, you don't want EqualsVerifier to look at JPA's annotations, you can disable them like this:

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
        .withIgnoredAnnotations(Entity.class, Id.class, Embeddable.class, MappedSuperclass.class, Transient.class)
        .verify();
{% endhighlight %}

Of course, you only need to include the annotations that you actually use. If any of the classes you specify isn't an annotation, EqualsVerifier throws an exception.

