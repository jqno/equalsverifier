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
### Added
- Full support for Java 11. ([Issue 197](https://github.com/jqno/equalsverifier/issues/197))
- Re-usable EqualsVerifier configurations: see [the manual](/equalsverifier/manual/reusing-configurations).
- `#report()` method to ask EqualsVerifier for a report, instead of making it fail a test.
- `#withGenericPrefabValues()` method added to supply values for specific generic types: see [here](/equalsverifier/errormessages/recursive-datastructure#generics).

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
- `@Transient` fields are not ignored, as discussed in the [manual](/equalsverifier/manual/jpa-entities). ([Issue 196](https://github.com/jqno/equalsverifier/issues/196))


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
- EqualsVerifier throws ReflectionException when it's unable to read annotations on fields in certain situations. (Issue 14, [Comment 21](https://github.com/jqno/equalsverifier/issues/114#issuecomment-206463710))


<a name="1.x"/>


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

