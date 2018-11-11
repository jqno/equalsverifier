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


<a name="1.x"/>


[Unreleased]: https://github.com/jqno/equalsverifier/compare/equalsverifier-3.0.2...HEAD
[3.0.2]: https://github.com/jqno/equalsverifier/compare/equalsverifier-3.0.1...equalsverifier-3.0.2
[3.0.1]: https://github.com/jqno/equalsverifier/compare/equalsverifier-3.0...equalsverifier-3.0.1
[3.0]: https://github.com/jqno/equalsverifier/compare/equalsverifier-2.5.2...equalsverifier-3.0

