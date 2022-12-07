---
title: JPA entities
permalink: /manual/jpa-entities/
---
EqualsVerifier has support for JPA entities.

### Entities
JPA entities are mutable by design. Since adding `.suppress(Warning.NONFINAL_FIELDS)` to each test can get cumbersome, EqualsVerifier will do this implicitly for each class marked with an `@Entity`, `@Embeddable` or `@MappedSuperclass` annotation.

JPA entities are also not allowed to be final, and even a final `equals` or `hashCode` method [is problematic](https://stackoverflow.com/questions/6608222/does-a-final-method-prevent-hibernate-from-creating-a-proxy-for-such-an-entity). Therefore, EqualsVerifier will not enforce these for JPA entities, like it normally would. Note that this means that your class will be vulnerable to subclasses [breaking `equals`](/equalsverifier/manual/final).


### Ids
By default, EqualsVerifier assumes that your entities have a [business or natural key](https://en.wikipedia.org/wiki/Natural_key). Consequently, all fields that are marked with the `@Id` annotation are assumed not to participate in the class's `equals` and `hashCode` methods. For all other fields, EqualsVerifier behaves as usual.

EqualsVerifier also supports Hibernate's `@NaturalId` annotation. If it detects the presence of this annotation in a class, it will assume that _only_ the fields marked with `@NaturalId` participate in `equals` and `hashCode`, and that all other fields (including the ones marked with `@Id`) do not.

If your class has a [surrogate key](https://en.wikipedia.org/wiki/Surrogate_key), you can tell EqualsVerifier by suppressing `Warning.SURROGATE_KEY`. When this warning is suppressed, EqualsVerifier assumes that _only_ the field or fields marked with `@Id` participate in `equals` and `hashCode`, and that none of the other fields do.

When `@NaturalId` is present or when `Warning.SURROGATE_KEY` is suppressed, there is no need to call `#withOnlyTheseFields` or `#withIgnoredFields`.

EqualsVerifier will not only detect these annotations when they are placed on a field, but also when they are placed on the field's corresponding accessor method.

If your class has a business key, but no separate field to serve as `@Id`, you can tell EqualsVerifier by suppressing `Warning.SURROGATE_OR_BUSINESS_KEY`. For instance, if your entity models a person, and the field `socialSecurityNumber` is marked with `@Id`, you can use `Warning.SURROGATE_OR_BUSINESS_KEY` to include `socialSecurityNumber` and other fields like `name` and `birthDate` in `equals` and `hashCode`.

In order to meet the consistency requirements when implementing a class with a surrogate key, some argue that it [is necessary to make the `hashCode` constant](https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/). EqualsVerifier still requires a 'normal' `hashCode` implementation. If you want a constant `hashCode`, you can suppress `Warning.STRICT_HASHCODE`.


### Ids and new objects
A common pattern in JPA when deciding whether two objects are equal, is to look at their fields only if the object hasn't been persisted yet. If it has been persisted, the field has an id, and then the fields are ignored and only the id is used to decide. Such an `equals` method might look like this:

{% highlight java %}
@Override
public boolean equals(Object obj) {
    if (!(obj instanceof Foo)) {
        return false;
    }
    Foo other = (Foo)obj;
    if (id == 0L && other.id == 0L) {
        return super.equals(obj);
    }
    return id == other.id;
}
{% endhighlight %}

You might see an error message such as this one:

    Reflexivity: object does not equal an identical copy of itself:
      Foo@123456
    If this is intentional, consider suppressing Warning.IDENTICAL_COPY

In that case, you can call `suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)`.

(`Warning.IDENTICAL_COPY`, which the error message suggests, is not appropriate in this case because that is meant for classes which have no state at all.)


### Transient fields
Since fields marked with the `@Transient` annotation are not persisted, they should generally not participate in `equals` and `hashCode` either. Therefore, EqualsVerifier will implicitly call [`withIgnoredFields`](/equalsverifier/manual/ignoring-fields) for these fields.

If they do participate, EqualsVerifier will fail the test. This behavior can be avoided by suppressing `Warning.TRANSIENT_FIELDS`.


### Abstract superclass
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


### Disabling JPA checking
If, for some reason, you don't want EqualsVerifier to look at JPA's annotations, you can disable them like this:

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
        .withIgnoredAnnotations(Entity.class, Id.class, Embeddable.class, MappedSuperclass.class, Transient.class)
        .verify();
{% endhighlight %}

Of course, you only need to include the annotations that you actually use. If any of the classes you specify isn't an annotation, EqualsVerifier throws an exception.

