---
title: Error messages explained
permalink: /errormessages/
---
## Introduction

Did EqualsVerifier give an error message that you didn't understand? I try to make the error messages as clear as possible, but unfortunately Java's stacktraces leave little room for explanation sometimes. This page should help make sense of some of EqualsVerifier's error messages. Note that this is not intended to be an exhaustive list, buf if you found a message confusing please let me know.

Before you continue, please check your class's `toString` method. Since EqualsVerifier relies on `toString` when generating error messages, overriding it helps with getting better error messages. I recommend including the name of the class, and a summary of all the fields relevant to the equals contract. For example:

{% highlight java %}
@Override public String toString() {
    return getClass().getSimpleName() + ": x=" + x + ", y=" + y;
}
// "Point: x=1, y=2"
{% endhighlight %}

## The error messages
This is not a complete list. I'll add to it as needed, so if you need help with an error message, please [file an issue](https://github.com/jqno/equalsverifier/issues) or let me know on the [discussion group](https://github.com/jqno/equalsverifier/discussions), and I'll add an explanation as soon as possible.

* [Abstract delegation](/equalsverifier/errormessages/abstract-delegation)
* [BigDecimal equality](/equalsverifier/errormessages/bigdecimal-equality)
* [ClassCastException: java.lang.Object cannot be cast to …](/equalsverifier/errormessages/classcastexception)
* [Class is not accessible via the Java Module system. Consider opening the module that contains it.](/equalsverifier/errormessages/class-or-field-is-not-accessible-jpms)
* [Coverage is not 100%](/equalsverifier/errormessages/coverage-is-not-100-percent)
* [Double: equals doesn't use Double.compare for field foo](/equalsverifier/errormessages/double-equals-doesnt-use-doublecompare-for-field-foo)
* [Field foo of type Bar is not accessible via the Java Module System.](/equalsverifier/errormessages/class-or-field-is-not-accessible-jpms)
* [Float: equals doesn't use Float.compare for field foo](/equalsverifier/errormessages/float-equals-doesnt-use-floatcompare-for-field-foo)
* [Java _x_ is not supported by the current version of Byte Buddy which officially supports Java _y_](/equalsverifier/errormessages/byte-buddy)
* [JPA Entity: direct reference to field foo used instead of getter](/equalsverifier/errormessages/jpa-direct-reference-instead-of-getter)
* [Mutability: equals depends on mutable field](/equalsverifier/errormessages/mutability-equals-depends-on-mutable-field)
* [NoClassDefFoundError](/equalsverifier/errormessages/noclassdeffounderror)
* [Non-nullity: equals/hashCode/toString throws NullPointerException](/equalsverifier/errormessages/non-nullity-equals-hashcode-tostring-throws-nullpointerexception)
* [Precondition: two objects are equal to each other](/equalsverifier/errormessages/precondition-two-objects-are-equal-to-each-other)
* [Record: failed to run constructor](/equalsverifier/errormessages/record-failed-to-run-constructor)
* [Recursive datastructure](/equalsverifier/errormessages/recursive-datastructure)
* [Redefined superclass: object should not equal superclass instance](/equalsverifier/errormessages/redefined-superclass-object-should-not-equal-superclass-instance)
* [Significant fields: equals does not use foo, or it is stateless](/equalsverifier/errormessages/significant-fields-equals-does-not-use-foo-or-it-is-stateless)
* [Significant fields: equals relies on foo, but hashCode does not](/equalsverifier/errormessages/significant-fields-equals-relies-on-foo-but-hashcode-does-not)
* [Subclass: equals is not final](/equalsverifier/errormessages/subclass-equals-is-not-final)
* [Subclass: … equals subclass instance …](/equalsverifier/errormessages/subclass-equals-subclass-instance)
* [Subclass: object is not equal to an instance of a trivial subclass with equal fields](/equalsverifier/errormessages/subclass-object-is-not-equal-to-an-instance-of-a-trivial-subclass-with-equal-fields)
* [Symmetry: … does not equal superclass instance …](/equalsverifier/errormessages/symmetry-does-not-equal-superclass-instance)
* [Transient field foo should not be included in equals/hashCode contract](/equalsverifier/errormessages/transient-field)
* [Unable to make field foo accessible: module bar does not "opens bar" to baz](/equalsverifier/errormessages/unable-to-make-field-accessible)
* [Unsupported class file major version _x_](/equalsverifier/errormessages/unsupported-class-file-major-version)
