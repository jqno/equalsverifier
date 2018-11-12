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
### Added
- Prefab values for various `java.time` classes, to avoid illegal reflective access warnings on Java 9+. ([Issue 217](https://github.com/jqno/equalsverifier/issues/217))


## [3.0.1] - 2018-10-29
### Added
- Prefab value for `java.net.URI`, to avoid illegal reflective access warnings on Java 9+. ([Issue 214](https://github.com/jqno/equalsverifier/issues/214))

### Changed
- Updated internal ASM dependency to version 7.0, so Java 11 support is no longer experimental.


## [3.0] - 2018-09-26
### Added
- Full support for Java 11. ([Issue 197](https://github.com/jqno/equalsverifier/issues/197))
- Re-usable EqualsVerifier configurations: see [the manual](/equalsverifier/manual/reusing-configurations).
- `#report()` method to ask EqualsVerifier for a report, instead of making it fail a test.
- `#withGenericPrefabValues()` method added to supply values for specific generic types: see [here](/equalsverifier/errormessages/recursive-datastructure#generics).
- Prefab values for `java.util.function.Supplier`, `java.util.concurrent.atomic.Atomic*` and some RMI-specific classes. ([Issue 207](https://github.com/jqno/equalsverifier/issues/207))

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


<a name="2.x"/>

## [2.5.2] - 2018-08-17
### Added
- Prefab values for `Inet4Address`, `Inet6Address`, `java.sql.Date`, `java.sql.Time` and `java.sql.Timestamp`.


## [2.5.1] - 2018-08-05
### Fixed
- `#withIgnoredFields` on stateless fields. ([Issue 203](https://github.com/jqno/equalsverifier/issues/203))


## [2.5] - 2018-07-30
### Added
- `Warning.ALL_NONFINAL_FIELDS_SHOULD_BE_USED` when only the final fields are relevant to your `equals` method. ([Issue 200](https://github.com/jqno/equalsverifier/issues/200))

### Fixed
- Using both abstract methods and lazy iniitalizers gave an unexplained `NullPointerException`. ([Issue 201](https://github.com/jqno/equalsverifier/issues/201))
- `ReflectionException` when using empty enums in your class. ([Issue 199](https://github.com/jqno/equalsverifier/issues/199))


## [2.4.8] - 2018-07-04
### Added
- Better support for Java 11. ([Issue 197](https://github.com/jqno/equalsverifier/issues/197))
- Prefab values for Java NIO buffers. ([Issue 198](https://github.com/jqno/equalsverifier/issues/198))


## [2.4.7] - 2018-06-20
### Added
- Prefab values for `java.lang.Thread`, to avoid illegal reflective access warnings on Java 9+. ([Issue 193](https://github.com/jqno/equalsverifier/issues/193))

### Changed
- Performance improvements because part of the processing of annotations is now cached. ([Issue 190](https://github.com/jqno/equalsverifier/issues/190); thanks Андрей!)

### Fixed
- using `@Transient` to implicitly ignore fields, as discussed in the [manual](/equalsverifier/manual/jpa-entities). ([Issue 196](https://github.com/jqno/equalsverifier/issues/196))


## [2.4.6] - 2018-04-28
### Fixed
- An AWT frame popped up while EqualsVerifier is running. ([Issue 192](https://github.com/jqno/equalsverifier/issues/192))


## [2.4.5] - 2018-03-20
### Added
- Support for Java 10 GA. ([Issue 189](https://github.com/jqno/equalsverifier/issues/189); thanks Vincent!)


## [2.4.4] - 2018-03-12
### Added
- Initial support for Java 10.
- Support for building and releasing EqualsVerifier from Java 9.


## [2.4.3] - 2018-02-03
### Fixed
- Support for Guava 24's Multisets. ([Issue 185](https://github.com/jqno/equalsverifier/issues/185); thanks Stephan!)


## [2.4.2] - 2018-01-20
### Added
- Prefab values for AWT classes to avoid illegal reflective access warnings on Java 9+ (and recursive datastructure warnings). ([Issue 183](https://github.com/jqno/equalsverifier/issues/183))


## [2.4.1] - 2018-01-12
### Fixed
- EqualsVerifier's test suite (and probably your own EqualsVerifier tests!) now run on Java 9 without any illegal reflective access warnings. ([Issue 172](https://github.com/jqno/equalsverifier/issues/172))


## [2.4] - 2017-11-11
### Added
- An official Java 9 module name: `nl.jqno.equalsverifier`.
- Initial support for Java 10 Early Access. ([Issue 177](https://github.com/jqno/equalsverifier/issues/177); thanks Vincent!)


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

