# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

* Jump to [version 3.x](#3.x)
* Jump to [version 2.x](#2.x)
* Jump to [version 1.x](#1.x)


## [Unreleased]


<a name="3.x"/>

## [3.0.2] - 2018-10-31
### Fixed
- `java.time` fields cause illegal reflective access warning on Java 9+: added prefab values. [Issue 217](https://github.com/jqno/equalsverifier/issues/217))


## [3.0.1] - 2018-10-29
### Fixed
- `java.net.URI` fields cause illegal reflective access warning on Java 9+: added prefab values. ([Issue 214](https://github.com/jqno/equalsverifier/issues/214))
- Java 11 support is no longer experimental because internal ASM dependency is updated to version 7.0.
- Updated internal ASM dependency to version 7.0, so Java 11 support is no longer experimental.


## [3.0] - 2018-09-26
If you're upgrading from EqualsVerifier 2.x, please see the [migration guide](http://jqno.nl/equalsverifier/migration2to3).

### Added
- Full support for Java 11. ([Issue 197](https://github.com/jqno/equalsverifier/issues/197))
- Re-usable EqualsVerifier configurations: see [the manual](http://jqno.nl/equalsverifier/manual/reusing-configurations).
- `#report()` method to ask EqualsVerifier for a report, instead of making it fail a test.
- `#withGenericPrefabValues()` method added to supply values for specific generic types: see [here](http://jqno.nl/equalsverifier/errormessages/recursive-datastructure#generics).

### Changed
- Error messages are more readable, because:
  - the name of a failing class is mentioned more clearly in the error message ([Issue 202](https://github.com/jqno/equalsverifier/issues/202));
  - the error message no longer edits out EqualsVerifier-internal calls from the stack trace;
  - the layout of the text in the error messages has been improved.
- Perfomance improvements. ([Issue 190](https://github.com/jqno/equalsverifier/issues/190); see also [this tweet](https://twitter.com/jqno/status/1002562042862231552))
- Code base now uses Java 8 language features.
- JavaDoc has been updated.

### Removed
- Support for Java 7.
- `Warning.ANNOTATION`, because it's no longer needed.
- The annoying license and copyright headers in each file. (Don't worry: EqualsVerifier is still licensed under the Apache 2.0 license!)

### Fixed
- `java.util.function.Supplier`, `java.util.concurrent.atomic.Atomic*` and some RMI-specific fields cause illegal reflective access warnings on Java 9+: added prefab values. ([Issue 207](https://github.com/jqno/equalsverifier/issues/207))


<a name="2.x"/>

## [2.5.2] - 2018-08-17
### Fixed
- `Inet4Address`, `Inet6Address`, `java.sql.Date`, `java.sql.Time` and `java.sql.Timestamp` cause illegal reflective access warnings on Java 9+: added prefab values.


## [2.5.1] - 2018-08-05
### Fixed
- AssertionError when class under test has stateless fields: you can now use `#withIgnoredFields()`. ([Issue 203](https://github.com/jqno/equalsverifier/issues/203))


## [2.5] - 2018-07-30
### Added
- `Warning.ALL_NONFINAL_FIELDS_SHOULD_BE_USED` when only the final fields are relevant to your `equals` method. ([Issue 200](https://github.com/jqno/equalsverifier/issues/200))

### Fixed
- Using both abstract methods and lazy iniitalizers throws an unexplained `NullPointerException`. ([Issue 201](https://github.com/jqno/equalsverifier/issues/201))
- Empty enum fields throw `ReflectionException`. ([Issue 199](https://github.com/jqno/equalsverifier/issues/199))


## [2.4.8] - 2018-07-04
### Fixed
- Java NIO buffer fields cause Significant field error. ([Issue 198](https://github.com/jqno/equalsverifier/issues/198))
- Java 11 compatibility. ([Issue 197](https://github.com/jqno/equalsverifier/issues/197))


## [2.4.7] - 2018-06-20
### Changed
- Performance improvements, because part of the processing of annotations is now cached. ([Issue 190](https://github.com/jqno/equalsverifier/issues/190); thanks Андрей!)

### Fixed
- `java.lang.Thread` fields cause illegal reflective access warning on Java 9+: added prefab values. ([Issue 193](https://github.com/jqno/equalsverifier/issues/193))
- `@Transient` fields are not ignored, as discussed in the [manual](http://jqno.nl/equalsverifier/manual/jpa-entities). ([Issue 196](https://github.com/jqno/equalsverifier/issues/196))


## [2.4.6] - 2018-04-28
### Fixed
- An AWT frame pops up while EqualsVerifier is running. ([Issue 192](https://github.com/jqno/equalsverifier/issues/192))


## [2.4.5] - 2018-03-20
### Changed
- Updates internal Byte Buddy dependency to support Java 10 GA. ([Issue 189](https://github.com/jqno/equalsverifier/issues/189); thanks Vincent!)


## [2.4.4] - 2018-03-12
### Changed
- Initial support for Java 10.
- Support for building and releasing EqualsVerifier from Java 9.


## [2.4.3] - 2018-02-03
### Fixed
- Support for Guava 24's Multisets. ([Issue 185](https://github.com/jqno/equalsverifier/issues/185); thanks Stephan!)


## [2.4.2] - 2018-01-20
### Fixed
- AWT classes cause illegal reflective access warnings on Java 9+, and recursive data structure errors: prefab values added. ([Issue 183](https://github.com/jqno/equalsverifier/issues/183))


## [2.4.1] - 2018-01-12
### Fixed
- EqualsVerifier's test suite (and probably your own EqualsVerifier tests!) now run on Java 9 without any illegal reflective access warnings. ([Issue 172](https://github.com/jqno/equalsverifier/issues/172))


## [2.4] - 2017-11-11
### Added
- An official Java 9 module name: `nl.jqno.equalsverifier`.
- Initial support for Java 10 Early Access. ([Issue 177](https://github.com/jqno/equalsverifier/issues/177); thanks Vincent!)


## [2.3.3] - 2017-08-24
### Fixed
- Chaining calls to `#withIgnoredFields` and `#withOnlyTheseFields` throws IllegalArgumentException. ([Issue 171](https://github.com/jqno/equalsverifier/issues/171); thanks Nathan!)
- Having a field from the Java library's `org.w3c.dom` package throws IllegalArgumentException. ([Issue 174](https://github.com/jqno/equalsverifier/issues/174))

### Changed
- EqualsVerifier's jar file is smaller again due to better Maven support of Java 9. (Issue 152, [Comment 18](https://github.com/jqno/equalsverifier/issues/152#issuecomment-324149215))


## [2.3.2] - 2017-08-06
### Changed
- Support for Java 9. ([Issue 152](https://github.com/jqno/equalsverifier/issues/152))


## [2.3.1] - 2017-07-03
### Changed
- Non-final `equals` and `hashCode` in JPA entities no longer cause an error. ([Issue 170](https://github.com/jqno/equalsverifier/issues/170))


## [2.3] - 2017-05-24
### Added
- `#withIgnoredAnnotations` to disable specific annotations. For instance, you can disable `@Nonnull` to gain 100% test coverage on `equals` methods generated by Lombok. (Mailinglist threads [1](https://groups.google.com/forum/#!topic/equalsverifier/zQJfwmYO44I) and [2](https://groups.google.com/forum/#!topic/equalsverifier/INHQpvw66zw); for more info see ['Coverage is not 100%'](http://www.jqno.nl/equalsverifier/errormessages/coverage-is-not-100-percent/))
- Support for classes derived from classes that live in a signed jar. ([Issue 163](https://github.com/jqno/equalsverifier/issues/163))

### Fixed
- The `MANIFEST.MF` file is back in the EqualsVerifier jar. ([Issue 169](https://github.com/jqno/equalsverifier/issues/169))


## [2.2.2] - 2017-04-03
### Fixed
- `java.inet.InetAddress` fields cause "Cannot inject classes into the bootstrap class loader" error. ([Issue 168](https://github.com/jqno/equalsverifier/issues/168))
- Fields of other types also cause class loader errors. ([Also issue 168](https://github.com/jqno/equalsverifier/issues/168))


## [2.2.1] - 2017-01-29
### Fixed
- Fields of `Map` type where a single value enum is used as key. ([Issue 166](https://github.com/jqno/equalsverifier/issues/166))


## [2.2] - 2017-01-14
### Added
- `#withNonnullFields()` to be more specific about which fields may be null. ([Issue 134](https://github.com/jqno/equalsverifier/issues/134))

### Changed
- Error messages are more helpful.

### Fixed
- Uninitialized static arrays cause NullPointerException. ([Issue 165](https://github.com/jqno/equalsverifier/issues/165))


## [2.1.8] - 2016-12-13
### Fixed
- Classes that implement an abstract class that calls an abstract method in its `equals` or `hashCode` method, throw AbstractMethodError when EqualsVerifier is called with `#usingGetClass()`. ([Issue 161](https://github.com/jqno/equalsverifier/issues/161))


## [2.1.7] - 2016-11-18
### Fixed
- Single value enums cause Significant fields error. ([Issue 157](https://github.com/jqno/equalsverifier/issues/157); thanks Stephan!)


## [2.1.6] - 2016-10-01
### Fixed
- Annotations which are available at compile-time but not at run-time sometimes throw NullPointerException. ([Issue 153](https://github.com/jqno/equalsverifier/issues/153) and [Issue 154](https://github.com/jqno/equalsverifier/issues/154))


## [2.1.5] - 2016-08-06
### Fixed
- `java.util.Vector` and `java.util.Stack` fields throw ArrayIndexOutOfBoundsException. ([Issue 151](https://github.com/jqno/equalsverifier/issues/151))


## [2.1.4] - 2016-07-25
### Fixed
- Full generics support for Guava's `Range`. ([Issue 150](https://github.com/jqno/equalsverifier/issues/150); thanks Stephan!)


## [2.1.3] - 2016-07-17
### Fixed
- Older versions of Google Guava cause ReflectionException. ([Issue 149](https://github.com/jqno/equalsverifier/issues/149))


## [2.1.2] - 2016-06-20
### Fixed
- Race condition when EqualsVerifier is run concurrently. ([Issue 148](https://github.com/jqno/equalsverifier/issues/148); thanks Borys!)


## [2.1.1] - 2016-06-14
### Changed
- Updated ByteBuddy dependency. ([Issue 145](https://github.com/jqno/equalsverifier/issues/145); thanks Vincent!)

### Fixed
- In certain situations, for example when running unit tests with coverage in IntelliJ, VerifyErrors are thrown. ([Issue 147](https://github.com/jqno/equalsverifier/issues/147))


## [2.1] - 2016-05-22
### Added
- `Warning.STRICT_HASHCODE` to let EqualsVerifier allow `hashCode` methods that don't use all the fields that are also used in `equals`, or even constant `hashCode`s. ([Issue 142](https://github.com/jqno/equalsverifier/issues/142))

### Fixed
- Unexpected behaviour when a class's `equals` or `hashCode` asserts on the length of its array field. ([Issue 143](https://github.com/jqno/equalsverifier/issues/143))
- Objenesis's meta-data carries into EqualsVerifier's jar file's `META-INF` folder. ([Issue 144](https://github.com/jqno/equalsverifier/issues/144))
- EqualsVerifier throws ReflectionException when it's unable to read annotations on fields in certain situations. (Issue 114, [Comment 21](https://github.com/jqno/equalsverifier/issues/114#issuecomment-206463710))


## [2.0.2] - 2016-04-03
### Fixed
- Classes that implement an abstract class that calls an abstract method in its `equals` or `hashCode` method, throws AbstractMethodError. ([Issue 138](https://github.com/jqno/equalsverifier/issues/138))
- `javax.naming.Reference` throws IllegalStateException on certain JVMs. ([Issue 114](https://github.com/jqno/equalsverifier/issues/114))


## [2.0.1] - 2016-03-13
### Changed
- `com.google.code.findbugs.annotations` dependency now has `provided` scope. ([Issue 135](https://github.com/jqno/equalsverifier/issues/135); thanks Stephan!)

### Fixed
- Classes that have a static final reference to a recursive data structure, without adding a prefab value.
- Classes that have a generic parameter that extends `Comparable` throw IllegalArgumentException. ([Issue 136](https://github.com/jqno/equalsverifier/issues/136))


## [2.0] - 2016-03-06
If you're upgrading from EqualsVerifier 1.x, please see the [migration guide](http://jqno.nl/equalsverifier/migration1to2).

### Added
- `#withIgnoredFields()` to disregard specific fields, while expecting all remaining fields to be used in `equals`.
- `#withOnlyTheseFields()` to expect that the given fields are used, and that the remaining fields are not used. ([Issue 128](https://github.com/jqno/equalsverifier/issues/128))
- `Warning.ALL_FIELDS_SHOULD_BE_USED` to suppress Significant field errors.
- EqualsVerifier fails when `equals` isn't overridden (i.e., inherited directly from `Object`). ([Issue 66](https://github.com/jqno/equalsverifier/issues/66))
  - `Warning.INHERITED_DIRECTLY_FROM_OBJECT` to revert back to the old behaviour.

### Changed
- 'All fields should be used' is now the default behaviour. ([Issue 65](https://github.com/jqno/equalsverifier/issues/65))
- `#forRelaxedEqualExamples()` implicitly suppresses `Warning.ALL_FIELDS_SHOULD_BE_USED`.
- Suppressing `Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY` implicitly suppresses `Warning.ALL_FIELDS_SHOULD_BE_USED`.
- EqualsVerifier puts more useful information on the stack trace if it fails.
- Replaced internal dependency to CGLib with Byte Buddy, for better support of Java 8. ([Issue 115](https://github.com/jqno/equalsverifier/issues/115))
- Improved EqualsVerifier's code quality by adding Checkstyle and FindBugs, and doing mutation tests with PIT.

### Removed
- Support for Java 6.
- `EqualsVerifier#forExamples()`. Use `#forClass()` or `#forRelaxedEqualExamples()` instead.
- `EqualsVerifier#debug()`. (It didn't do anything anymore, anyway.)

### Fixed
- Referring to the generic contents of containers such as `List` and `Optional` in the implementation of `equals` and `hashCode` can throw ClassCastException. ([Issue 84](https://github.com/jqno/equalsverifier/issues/84))


<a name="1.x"/>

## [1.7.8] - 2016-12-26
### Fixed
- EqualsVerifier throws RuntimeException when used together with Java 8's `-parameters` compiler option.


## [1.7.7] - 2016-01-18
### Fixed
- EqualsVerifier throws NullPointerException when used together with Cobertura. ([Issue 132](https://github.com/jqno/equalsverifier/issues/132))


## [1.7.6] - 2015-12-21
### Changed
- EqualsVerifier gives 100% mutation coverage with [PIT](http://pitest.org) on your `equals` and `hashCode` methods. ([Issue 131](https://github.com/jqno/equalsverifier/issues/131))

### Fixed
- Implementing an interface that defines `equals` causes Reflexivity error. ([Issue 130](https://github.com/jqno/equalsverifier/issues/130))


## [1.7.5] - 2015-08-29
### Changed
- EqualsVerifier gives Symmetry errors if the symmetry violation only occurs in the subclass of a versioned entity. (Issue 123, [Comment 10](https://github.com/jqno/equalsverifier/issues/123#issuecomment-134201349))
- EqualsVerifier gives a warning when the id check on a versioned entity is implemented incorrectly. (Issue 123, [Comment 17](https://github.com/jqno/equalsverifier/issues/123#issuecomment-133151013))
- Suppressing `Warning.NONFINAL_FIELDS` on classes marked `@Embeddable` and `@MappedSuperclass` is no longer needed. ([Issue 124](https://github.com/jqno/equalsverifier/issues/124) and Issue 123, [Comment 15](https://github.com/jqno/equalsverifier/issues/123#issuecomment-134168785))

### Fixed
- EqualsVerifier throws NullPointerException on singleton enum fields. ([Issue 125](https://github.com/jqno/equalsverifier/issues/125))


## [1.7.4] - 2015-08-17
### Fixed
- JavaFX fields (on Java 8) cause RecursionException: added prefab values. ([Issue 120](https://github.com/jqno/equalsverifier/issues/120))
- `javax.naming.Reference` fields cause ClassCastException: added prefab value. ([Issue 118](https://github.com/jqno/equalsverifier/issues/118))
- SBT throws ClassNotFoundException when running EqualsVerifier. ([Issue 119](https://github.com/jqno/equalsverifier/issues/119))
- Reporting on subclasses of versioned entities. ([Issue 123](https://github.com/jqno/equalsverifier/issues/123))


## [1.7.3] - 2015-07-18
### Changed
- `cachedHashCode` field can now have protected or default visibility for `#withCachedHashCode()`. ([Issue 110](https://github.com/jqno/equalsverifier/issues/110))
- Several behind-the-scenes improvements :).

### Fixed
- Static final fields with value `null` throw NullPointerException. ([Issue 116](https://github.com/jqno/equalsverifier/issues/116))


## [1.7.2] - 2015-03-28
### Added
- Support for Eclipse's JDT null annotations, including the Java 8 style type annotations.

### Changed
- All internal dependencies have been updated. ([Issue 107](https://github.com/jqno/equalsverifier/issues/107))

### Fixed
- Static fields with an empty array throw ArrayIndexOutOfBoundsException. ([Issue 106](https://github.com/jqno/equalsverifier/issues/106))
- Several potential bugs, using [PIT](http://pitest.org).


## [1.7.1] - 2015-03-11
### Fixed
- EqualsVerifier throws Reflexivity errors (which then need to be suppressed with version 1.7's `Warning.REFERENCE_EQUALITY`) on
  - certain interfaces, and classes which don't redefine `equals`. (issue 104, [comment 6](https://github.com/jqno/equalsverifier/issues/104#issuecomment-87377326))
  - static final lambda fields. ([Issue 105](https://github.com/jqno/equalsverifier/issues/105))


## [1.7] - 2015-03-04
### Added
- `#withCachedHashCode()` to verify classes that cache their `hashCode`. ([Issue 60](https://github.com/jqno/equalsverifier/issues/60); thanks Niall!)
- `Warning.NO_EXAMPLE_FOR_CACHED_HASHCODE` to avoid giving an example value for `#withCachedHashCode()`. ([Issue 60](https://github.com/jqno/equalsverifier/issues/60))
- An error message when you accidentally use `==` instead of `equals` on an object field inside the `equals` method. ([Issue 104](https://github.com/jqno/equalsverifier/issues/104))
- `Warning.REFERENCE_EQUALITY` to suppress the above error. ([Issue 104](https://github.com/jqno/equalsverifier/issues/104))

### Fixed
- Several Java Collections interface classes, including `SortedSet` and `TreeSet`, throw AbstractMethodError: added prefab values. ([Issue 103](https://github.com/jqno/equalsverifier/issues/103))


## [1.6] - 2015-01-17
### Added
- Support for default `@Nonnull` annotations. ([Issue 50](https://github.com/jqno/equalsverifier/issues/50))
  - Using FindBugs's `@DefaultAnnotation` or `@DefaultAnnotationForFields`,
  - Using a JSR 305 annotation (see [this StackOverflow question](http://stackoverflow.com/q/11776302/127863)),
  - Placing them either on the class or on the package,
  - With the ability to override the default annotation by placing `@Nullable` or `@CheckForNull` on a field.
- Check for `hashCode` consistency when object is stateless. ([Issue 97](https://github.com/jqno/equalsverifier/issues/97))
- Support for classes where equality is fully defined in a superclass, for example using Apache Commons's [`EqualsBuilder.reflectionEquals`](http://commons.apache.org/proper/commons-lang/javadocs/api-3.3.2/org/apache/commons/lang3/builder/EqualsBuilder.html#reflectionEquals%28java.lang.Object,%20java.lang.Object,%20boolean%29). ([Issue 102](https://github.com/jqno/equalsverifier/issues/102))

### Fixed
- Stateless classes cause Precondition error. ([Issue 46](https://github.com/jqno/equalsverifier/issues/46))
- Classes with stateless fields cause Precondition error. ([Issue 100](https://github.com/jqno/equalsverifier/issues/100) and [Issue 101](https://github.com/jqno/equalsverifier/issues/101)) 


## [1.5.1] - 2014-12-05
### Changed
- EqualsVerifier now build with Maven instead of ANT+Ivy.

### Fixed
- Dependency issues when incompatible versions of ASM and/or CGLib are on the classpath: EqualsVerifier is now shipped as an "uber jar" which contains all its dependencies inside. ([Issue 96](https://github.com/jqno/equalsverifier/issues/96))
- EqualsVerifier throws ReflectionException with older versions of Google Guava on the classpath. ([Issue 98](https://github.com/jqno/equalsverifier/issues/98))


## [1.5] - 2014-08-20
### Added
- Support for Java 8! Classes containing Java 8 language features are now supported, and prefab values for new Java 8 API classes have been added. ([Issue 92](https://github.com/jqno/equalsverifier/issues/92))

### Changed
- Improved error messages when `equals` or `hashCode` are themselves abstract.
- Heavily refactored EqualsVerifier's unit tests to make them easier to find and understand.

### Fixed
- Classes from [Joda-Time](http://www.joda.org/joda-time/) and [Google Guava](https://code.google.com/p/guava-libraries/) throw AbstractMethodError: added prefab values. ([Issue 83](https://github.com/jqno/equalsverifier/issues/83))
- Multi-dimensional arrays cause incorrect error messages. ([Issue 90](https://github.com/jqno/equalsverifier/issues/90) and [Issue 94](https://github.com/jqno/equalsverifier/issues/94))
- `Arrays#equals` on `Object[]` fields doesn't work; only `Arrays#deepEquals()` does. ([Issue 94](https://github.com/jqno/equalsverifier/issues/94))
- Using `#allFieldsShouldBeUsed()` requires overriding `equals`. ([Issue 95](https://github.com/jqno/equalsverifier/issues/95); thanks Dean!)


## [1.4.1] - 2014-03-18
### Changed
- Improved EqualsVerifier's unit tests for reflexivity and symmetry, to catch small mistakes such as the one in [Issue 88](https://github.com/jqno/equalsverifier/issues/88).

### Fixed
- Verifying an enum causes Identical Copy and Symmetry errors. ([Issue 87](https://github.com/jqno/equalsverifier/issues/87))

## [1.4] - 2013-12-27
### Added
- `#allFieldsShouldBeUsedExcept()` to specifically ignore certain fields. ([Issue 82](https://github.com/jqno/equalsverifier/issues/82))

### Changed
- EqualsVerifier now covers 100% of your `equals` and `hashCode` methods. ([FAQ](http://jqno.nl/equalsverifier/faq#coverage))
- Error messages around abstract delegation are clearer. This clarifies especially classes that contain Joda-Time `LocalDate` fields.

### Fixed
- Classes that have an array field but don't declare an `equals` method cause an error.
- `java.util.BitSet` fields cause ArrayIndexOutOfBoundsException: added prefab value. ([Issue 86](https://github.com/jqno/equalsverifier/issues/86))


## [1.3.1] - 2013-06-09
### Added
- `Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY` which can be suppressed when testing "versioned entity" classes where a zero id field indicates that the object is new. ([Issue 80](https://github.com/jqno/equalsverifier/issues/80))

### Changed
- Explanations for EqualsVerifier's error messages can now be read on the [new EqualsVerifier website](http://jqno.nl/equalsverifier/errormessages) at [jqno.nl](http://jqno.nl/equalsverifier), which matches EqualsVerifier's fully qualified name: `nl.jqno.equalsverifier.EqualsVerifier`.
- Transitivity errors are more accurate. ([Issue 78](https://github.com/jqno/equalsverifier/issues/78))

### Fixed
- Exceptions thrown in `#toString()` break EqualsVerifier. ([Issue 79](https://github.com/jqno/equalsverifier/issues/79))
- `java.util.UUID` fields cause Recursion error: added prefab value. ([Issue 81](https://github.com/jqno/equalsverifier/issues/81))

### Deprecated
- `#debug()`, because relevant exceptions are now included as a cause in the stack trace.


## 1.3 - 2013-06-09

Please don't use version 1.3; [it's a broken release](https://jqno.nl/post/2013/06/11/what-happened-to-equalsverifier-1-3/). Use 1.3.1 instead.


## [1.2] - 2013-03-26
### Changed
- EqualsVerifier no longer fails on single value enums (and hence, singletons), even if they don't count for `equals` and `hashCode`. ([Issue 74](https://github.com/jqno/equalsverifier/issues/74))
- EqualsVerifier can now be built using Ant 1.9. ([Issue 76](https://github.com/jqno/equalsverifier/issues/76))
### Fixed
- Non-transitive `equals` methods (using `||`) pass. ([Issue 75](https://github.com/jqno/equalsverifier/issues/75); blog post about [the problem](http://www.jqno.nl/post/2013/02/17/reaction-to-cedric-beusts-equals-challenge/) and [the solution](http://www.jqno.nl/post/2013/03/26/on-transitivity))
- Errors when the `toString` method of the class under test throws exceptions.
- Classes containing null fields throw NullPointerExample in `#forRelaxedEqualExamples`. ([Issue 73](https://github.com/jqno/equalsverifier/issues/73))
- Some false negatives when running EqualsVerifier's test suite. ([Issue 77](https://github.com/jqno/equalsverifier/issues/77))


## [1.1.4] - 2013-01-14
### Changed
- Fork EqualsVerifier on GitHub! The code has been moved to GitHub. The project frontpage will remain on Google Code.
- Error messages on Abstract Delegation are more informative: EqualsVerifier now also mentions the name of the abstract method that was called.

### Fixed
- Issue when the class under test has a superclass that has no declarations for `equals` and `hashCode`. ([Issue 63](https://github.com/jqno/equalsverifier/issues/63))
- No error when using `#allFieldsShouldBeUsed()` and the class under test has fields, but no declarations for `equals` and `hashCode` (and hence doesn't use these fields). ([Issue 67](https://github.com/jqno/equalsverifier/issues/67))


## [1.1.3] - 2012-04-21
### Fixed
- `#allFieldsShouldBeUsed()` includes static fields. EqualsVerifier will now no longer complain if these static fields aren't used in your `equals` and `hashCode` methods. ([Issue 57](https://github.com/jqno/equalsverifier/issues/57))
- EqualsVerifier passes when an `equals` method works incorrectly if a field in the class under test is null. ([Issue 59](https://github.com/jqno/equalsverifier/issues/59))


## [1.1.2] - 2012-03-01
### Fixed
- EqualsVerifier _recursively_ changes the value of non-final static fields, causing other tests to fail. (Issue 55, [Comment 8](http://github.com/jqno/equalsverifier/issues/55#issuecomment-87376895))


## [1.1.1] - 2012-02-21
### Added
- `Warning.IDENTICAL_COPY`, to suppress when you want to use reference equality in an overridden `equals` method. ([Issue 56](https://github.com/jqno/equalsverifier/issues/56))


## [1.1] - 2012-02-11
### Added
- `#allFieldsShouldBeUsed()` to get a warning if you forgot to include a field in `equals` or `hashCode` method. ([Issue 53](https://github.com/jqno/equalsverifier/issues/53))

### Fixed
- EqualsVerifier passes when you include a reference-equality check (`this == obj`), followed by an incorrect instanceof check in your `equals` method. EqualsVerifier will now notice if you do an instanceof check for a class other than the one that you are testing. (Issue 55, [Comment 2](http://github.com/jqno/equalsverifier/issues/55#issuecomment-87376889))
- EqualsVerifier changes the values of non-final static fields, causing other tests to fail. ([Issue 52](https://github.com/jqno/equalsverifier/issues/52) and [Issue 55](https://github.com/jqno/equalsverifier/issues/55))
- `File`, `List`, `Set`, `Map` and `Collection` fields give unjustified warnings: added prefab values. ([Issue 49](https://github.com/jqno/equalsverifier/issues/49) and [Issue 51](https://github.com/jqno/equalsverifier/issues/51))


## [1.0.2] - 2011-08-14
### Changed
- EqualsVerifier is now in Maven Central! ([Issue 36](https://github.com/jqno/equalsverifier/issues/36))

### Fixed
- EqualsVerifier passes when you forget to include an `instanceof` check or a `getClass()` check in your `equals` method. ([Issue 47](https://github.com/jqno/equalsverifier/issues/47))
- EqualsVerifier throws AbstractMethodError on a class whose superclass has abstract declarations for `equals` and `hashCode`. ([Issue 48](https://github.com/jqno/equalsverifier/issues/48))


## [1.0.1] - 2011-04-17
### Added
- `Warning.ANNOTATION` to disable annotation processing. This is useful for dynamically generated classes. ([Issue 41](https://github.com/jqno/equalsverifier/issues/41))

### Fixed
- Issue when EqualsVerifier tests a class that is a subclass of one of the classes from `rt.jar`, which are loaded by the system classloader. ([Issue 43](https://github.com/jqno/equalsverifier/issues/43))
- EqualsVerifier fails on classes with `float` or `double` fields but no `equals` method. ([Issue 44](https://github.com/jqno/equalsverifier/issues/44))
- `Throwable` and `Exception` fields cause Recursion error: added prefab values. ([Issue 45](https://github.com/jqno/equalsverifier/issues/45))


## [1.0] - 2011-02-23
### Added
- Support for annotations. Use any of the following annotations on your classes and fields: EqualsVerifier will know what to do!
  - `@Immutable`: EqualsVerifier will not complain about non-final fields if your class is `@Immutable`. (It doesn't matter in which package the annotation is defined; `javax.annotations.concurrent.Immutable`, `net.jcip.annotations.Immutable` or your own implementation will all work fine.)
  - `@Nonnull`, `@NonNull` and `@NotNull`: EqualsVerifier will not check for potential `NullPointerException`s for any field marked with any of these annotations. (Again: the source package doesn't matter.) This is similar to calling `#suppress(Warning.NULL_FIELDS)`, but on a per-field basis, instead of all-or-nothing. ([Issue 28](https://github.com/jqno/equalsverifier/issues/28))
  - `@Entity`: EqualsVerifier will not complain about non-final fields if your class is a JPA Entity. Note that this only works for `javax.persistence.Entity`, not for `Entity` annotations from other packages. ([Issue 37](https://github.com/jqno/equalsverifier/issues/37))
  - `@Transient`: Fields marked with this annotation (again, only from `javax.persistence.Transient`), will be treated the same as fields marked with Java's `transient` modifier; i.e., EqualsVerifier will complain if they are used in the `equals` contract.

### Changed
- EqualsVerifier now shows the name of the field that throws a potential NullPointerException. ([Issue 39](https://github.com/jqno/equalsverifier/issues/39))

### Fixed
- EqualsVerifier detects recursive data structures where there are none. ([Issue 34](https://github.com/jqno/equalsverifier/issues/34))
- `BigDecimal` and `BigInteger` fields cause Recursion error: added prefab values. ([Issue 34](https://github.com/jqno/equalsverifier/issues/34))


## [0.7] - 2010-11-15
### Added
- `#usingGetClass()` to test `equals` methods that use a call to `getClass()` instead of an `instanceof` check to determine the type of the object passed in.

### Changed
- EqualsVerifier warns when you use a transient field in your `equals` or `hashCode` method. Don't worry, you can suppress this warning too.
- Error messages contain a link to the [Error messages](http://jqno.nl/equalsverifier/errormessages) page to get more help.
- The back end is almost completely re-written.

### Fixed
- Certain Java API classes, like `Date`, `GregorianCalendar` and `Pattern`, cause Recursion error: added prefab values.
- Many Javadoc improvements (including the one in [Issue 32](https://github.com/jqno/equalsverifier/issues/32)).


## [0.6.5] - 2010-08-05
### Changed
- EqualsVerifier now contains an internal list of prefab values for a wide variety of Java API types, to avoid getting 'Recursive datastructure' errors all the time. ([Issue 30](https://github.com/jqno/equalsverifier/issues/30))
- Error messages when `equals`, `hashCode` or `toString` throws something other than a NullPointerException when one of the fields is `null`, are now more helpful. ([Issue 31](https://github.com/jqno/equalsverifier/issues/31))


## [0.6.4] - 2010-06-13
### Fixed
- Error message that a certain field is used in `equals` but not in `hashCode` or the other way around, when really `Arrays.hashCode()` or `Arrays.deepHashCode()` should have been used. ([Issue 27](https://github.com/jqno/equalsverifier/issues/27))
- Many non-nullity errors on `toString`.


## [0.6.3] - 2010-05-18
### Fixed
- EqualsVerifier gives hashCode error on classes that don't override `equals` or `hashCode`. ([Issue 23](https://github.com/jqno/equalsverifier/issues/23))
- IllegalAccessException when EqualsVerifier is used together with the [EclEmma](http://www.eclemma.org/) code coverage tool. ([Issue 22](https://github.com/jqno/equalsverifier/issues/22))
- Classes that contain (indirect) references to non-static inner classes cause recursive data structure errors. ([Issue 21](https://github.com/jqno/equalsverifier/issues/21))


## [0.6.2] - 2010-02-11
### Fixed
- Regression with `#withPrefabValues()` for recursive data structure fields in superclasses. ([Issue 20](https://github.com/jqno/equalsverifier/issues/20))


## [0.6.1] - 2010-02-06
### Changed
- Renamed `#verbose()` to `#debug()` to access `#verify()` more quickly using autocompletion in IDEs.

### Fixed
- Regression with `#withPrefabValues()` for fields which delegate to abstract methods. ([Issue 14](https://github.com/jqno/equalsverifier/issues/14))


## [0.6] - 2010-01-30
### Changed
- The API is more consistend. `#with(Feature)` is now `#suppress(Warning)`, which feels more Java-y. Former features that were not warnings, are now proper methods on `EqualsVerifier`: `#verbose()` and `#withRedefinedSuperclass()`.
- Error messages have been improved:
  - many messages now span multiple lines for improved readability;
  - hashCodes are printed (where relevant);
  - unexpected exceptions are no longer eaten by EqualsVerifier, so they can be read without calling `#verbose()`;
  - calls to abstract methods from within `equals` and `hashCode`, which cannot be resolved, are now detected and properly reported. ([Issue 14](https://github.com/jqno/equalsverifier/issues/14))

### Fixed
- EqualsVerifier fails on classes that contain fields whose `equals` methods might throw NullPointerExceptions. ([Issue 19](https://github.com/jqno/equalsverifier/issues/19))
- EqualsVerifier detects recursive data structures where there are none. ([Issue 18](https://github.com/jqno/equalsverifier/issues/18))
- `Class` fields throw IllegalAccessError: added prefab value. ([Issue 17](https://github.com/jqno/equalsverifier/issues/17))


## [0.5] - 2009-09-01
### Changed
- EqualsVerifier is now compiled with debug information, so you can step through the source in Eclipse after adding the EqualsVerifier jar to your project. ([Issue 11](https://github.com/jqno/equalsverifier/issues/11))

### Fixed
- Fields of interface of abstract class types (such as Lists and other Collection types) throw InstantiationError. ([Issue 12](https://github.com/jqno/equalsverifier/issues/12))


## [0.4] - 2009-08-29
### Added
- `#withRelaxedEqualExamples()` to verify equality rules that are more relaxed than simple field-by-field comparisons. ([Issue 9](https://github.com/jqno/equalsverifier/issues/9))


## [0.3] - 2009-08-01
### Changed
- EqualsVerifier now recursively instantiates objects so `#withPrefabValues()` is now only needed for actual recursive data structures. ([Issue 7](https://github.com/jqno/equalsverifier/issues/7))


## [0.2] - 2009-07-03
### Changed
- EqualsVerifier now checks if `Arrays.deepEquals()` was used for multidimensional and `Object` arrays. ([Issue 3](https://github.com/jqno/equalsverifier/issues/3))
- Optional features are now accessed through a clean enum instead of through lots of different method calls. ([Issue 4](https://github.com/jqno/equalsverifier/issues/4))
- EqualsVerifier can now be used in unit test frameworks other than JUnit 4. ([Issue 5](https://github.com/jqno/equalsverifier/issues/5))
- Stack traces are now printed to `System.err`. ([Issue 6](https://github.com/jqno/equalsverifier/issues/6))

### Fixed
- Can't use the _fields are never null_ feature on classes instantiated with `#forClass()`. ([Issue 1](https://github.com/jqno/equalsverifier/issues/1))


## [0.1] - 2009-06-01
You can now use EqualsVerifier!


[Unreleased]: https://github.com/jqno/equalsverifier/compare/equalsverifier-3.0.2...HEAD

[3.0.2]: https://github.com/jqno/equalsverifier/compare/equalsverifier-3.0.1...equalsverifier-3.0.2
[3.0.1]: https://github.com/jqno/equalsverifier/compare/equalsverifier-3.0...equalsverifier-3.0.1
[3.0]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.5.2...equalsverifier-3.0

[2.5.2]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.5.1...equalsverifier-2.5.2
[2.5.1]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.5...equalsverifier-2.5.1
[2.5]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.4.8...equalsverifier-2.5

[2.4.8]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.4.7...equalsverifier-2.4.8
[2.4.7]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.4.6...equalsverifier-2.4.7
[2.4.6]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.4.5...equalsverifier-2.4.6
[2.4.5]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.4.4...equalsverifier-2.4.5
[2.4.4]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.4.3...equalsverifier-2.4.4
[2.4.3]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.4.2...equalsverifier-2.4.3
[2.4.2]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.4.1...equalsverifier-2.4.2
[2.4.1]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.4...equalsverifier-2.4.1
[2.4]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.3.3...equalsverifier-2.4

[2.3.3]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.3.2...equalsverifier-2.3.3
[2.3.2]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.3.1...equalsverifier-2.3.2
[2.3.1]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.3...equalsverifier-2.3.1
[2.3]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.2.2...equalsverifier-2.3

[2.2.2]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.2.1...equalsverifier-2.2.2
[2.2.1]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.2...equalsverifier-2.2.1
[2.2]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.1.8...equalsverifier-2.2

[2.1.8]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.1.7...equalsverifier-2.1.8
[2.1.7]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.1.6...equalsverifier-2.1.7
[2.1.6]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.1.5...equalsverifier-2.1.6
[2.1.5]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.1.4...equalsverifier-2.1.5
[2.1.4]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.1.3...equalsverifier-2.1.4
[2.1.3]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.1.2...equalsverifier-2.1.3
[2.1.2]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.1.1...equalsverifier-2.1.2
[2.1.1]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.1...equalsverifier-2.1.1
[2.1]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.0.2...equalsverifier-2.1

[2.0.2]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.0.1...equalsverifier-2.0.2
[2.0.1]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.0...equalsverifier-2.0.1
[2.0]: https://github.com/jqno/equalsverifier/compare/equalsverifier-1.7.8...equalsverifier-2.0

[1.7.8]: https://github.com/jqno/equalsverifier/compare/equalsverifier-1.7.7...equalsverifier-1.7.8
[1.7.7]: https://github.com/jqno/equalsverifier/compare/equalsverifier-1.7.6...equalsverifier-1.7.7
[1.7.6]: https://github.com/jqno/equalsverifier/compare/equalsverifier-1.7.5...equalsverifier-1.7.6
[1.7.5]: https://github.com/jqno/equalsverifier/compare/equalsverifier-1.7.4...equalsverifier-1.7.5
[1.7.4]: https://github.com/jqno/equalsverifier/compare/equalsverifier-1.7.3...equalsverifier-1.7.4
[1.7.3]: https://github.com/jqno/equalsverifier/compare/equalsverifier-1.7.2...equalsverifier-1.7.3
[1.7.2]: https://github.com/jqno/equalsverifier/compare/equalsverifier-1.7.1...equalsverifier-1.7.2
[1.7.1]: https://github.com/jqno/equalsverifier/compare/equalsverifier-1.7...equalsverifier-1.7.1
[1.7]: https://github.com/jqno/equalsverifier/compare/equalsverifier-1.6...equalsverifier-1.7

[1.6]: https://github.com/jqno/equalsverifier/compare/equalsverifier-1.5.1...equalsverifier-1.6

[1.5.1]: https://github.com/jqno/equalsverifier/compare/version_1_5...equalsverifier-1.5.1
[1.5]: https://github.com/jqno/equalsverifier/compare/version_1_4_1...version_1_5

[1.4.1]: https://github.com/jqno/equalsverifier/compare/version_1_4...version_1_4_1
[1.4]: https://github.com/jqno/equalsverifier/compare/version_1_3_1...version_1_4

[1.3.1]: https://github.com/jqno/equalsverifier/compare/version_1_2...version_1_3_1

[1.2]: https://github.com/jqno/equalsverifier/compare/version_1_1_4...version_1_2

[1.1.4]: https://github.com/jqno/equalsverifier/compare/e18f544...version_1_1_4
[1.1.3]: https://github.com/jqno/equalsverifier/compare/version_1_1_2...version_1_1_3
[1.1.2]: https://github.com/jqno/equalsverifier/compare/version_1_1_1...version_1_1_2
[1.1.1]: https://github.com/jqno/equalsverifier/compare/version_1_1...version_1_1_1
[1.1]: https://github.com/jqno/equalsverifier/compare/version_1_0_2...version_1_1

[1.0.2]: https://github.com/jqno/equalsverifier/compare/version_1_0_1...version_1_0_2
[1.0.1]: https://github.com/jqno/equalsverifier/compare/version_1_0...version_1_0_1
[1.0]: https://github.com/jqno/equalsverifier/compare/version_0_7...version_1_0

[0.7]: https://github.com/jqno/equalsverifier/compare/version_0_6_5...version_0_7

[0.6.5]: https://github.com/jqno/equalsverifier/compare/version_0_6_4...version_0_6_5
[0.6.4]: https://github.com/jqno/equalsverifier/compare/version_0_6_3...version_0_6_4
[0.6.3]: https://github.com/jqno/equalsverifier/compare/version_0_6_2...version_0_6_3
[0.6.2]: https://github.com/jqno/equalsverifier/compare/version_0_6_1...version_0_6_2
[0.6.1]: https://github.com/jqno/equalsverifier/compare/version_0_6...version_0_6_1
[0.6]: https://github.com/jqno/equalsverifier/compare/version_0_5...version_0_6

[0.5]: https://github.com/jqno/equalsverifier/commits/version_0_5
[0.4]: https://github.com/jqno/equalsverifier/compare/8b2b6f8...a30ff4b 
[0.3]: https://github.com/jqno/equalsverifier/compare/version_0_2...version_0_3
[0.2]: https://github.com/jqno/equalsverifier/compare/version_0_1...version_0_2
[0.1]: https://github.com/jqno/equalsverifier/commits/version_0_1

