---
title: "JPA Entity: direct reference to field ... used in equals instead of getter"
---
For fields with a mapping annotation like `@OneToMany`, `@ManyToOne` or `@ManyToMany`, you might run into this error:

    JPA Entity: direct reference to field employee used in equals
      instead of getter getEmployee.

This error occurs when the field is used directly in `equals` or `hashCode`:

{% highlight java %}
@ManyToOne
private Employee employee;

public boolean equals(Object other) {
    // ...
    return Objects.equals(employee, that.employee);
}
{% endhighlight %}

This is problematic, because the field `employee` might not be materialized yet. In other words, JPA may not have queried the `employee` yet and the reference could still be null. This might lead to incorrect results when calling `equals` or `hashCode`. JPA will materialize the field when the getter is called, but not when the field is referenced directly. Therefore, the field should always be referenced through its getter, in `equals` and `hashCode`:

{% highlight java %}
public boolean equals(Object other) {
    // ...
    return Objects.equals(getEmployee(), that.getEmployee());
}

public int hashCode() {
    return Objects.hash(getEmployee());
}
{% endhighlight %}

If you have a reason, you can disable this check by suppressing `Warning.JPA_GETTER`. Also, EqualsVerifier assumes you use the JavaBeans convention to name your fields and getters. If you use a different convention, you can use `#withFieldnameToGetterConverter()` to override that.

See the [manual page about JPA entities](/equalsverifier/manual/jpa-entities), specifically the section on Materialized fields, for more details.
