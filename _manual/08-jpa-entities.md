---
title: JPA entities
permalink: /manual/jpa-entities/
---
EqualsVerifier has some support for JPA entities.

### Entities
JPA entities are mutable by design. Since adding `.suppress(Warning.NONFINAL_FIELDS)` to each test can get cumbersome, EqualsVerifier will do this implicitly for each class marked with an `@Entity`, `@Embeddable` or `@MappedSuperclass` annotation.


### Transient fields
Since fields marked with the `@Transient` annotation are not persisted, they should generally not participate in `equals` and `hashCode` either. Therefore, EqualsVerifier will implicitly call [`withIgnoredFields`](/equalsverifier/manual/ignoring-fields) for these fields.

If they do participate, EqualsVerifier will fail the test. This behavior can be avoided by suppressing `Warning.TRANSIENT_FIELDS`.


### IDs and new objects
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

