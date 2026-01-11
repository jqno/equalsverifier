# Contributing to EqualsVerifier

Thank you for your interest in contributing to EqualsVerifier! We welcome all contributions, from bug reports to new features and documentation improvements.

## How to Contribute

1. **Report Bugs or Request Features:**
   - If you find a bug or have a feature request, please [open an issue on GitHub](https://github.com/jqno/equalsverifier/issues).
2. **Submit a Pull Request:**
   - If it's a larger feature, please [open an issue on GitHub](https://github.com/jqno/equalsverifier/issues) first so we can discuss the approach that best fits within EqualsVerifeier.
   - Fork the repository and create your feature branch from `main`.
   - Ensure your changes adhere to the existing code style. We use Spotless for code formatting (`just format` or `mvn spotless:apply`), or import the formatter config into your IDE (`build/eclipse-formatter-config.xml`)
   - Write clear, concise commit messages.
   - Ensure all tests pass (`just test` or `mvn test`).
   - Consider adding new tests for your changes, especially for new features or bug fixes.
   - **Testing Style:** We follow the arrange/act/assert pattern in our unit tests, but without explicit comments delineating these sections. We use AssertJ for writing assertions.
   - For significant changes, update the documentation in the `docs/` directory. Generate the documentation website with `docker compose up` from the `docs/` directory.
   - Open a [pull request](https://github.com/jqno/equalsverifier/pulls) against the `main` branch.

## Code of Conduct

Take a look at the [code of conduct](CODE_OF_CONDUCT.md).

## Key Technologies

- **Language:** Java 17
- **Build Tool:** Maven (with a `justfile` for common tasks)
- **Testing:** JUnit 6, AssertJ
- **Static Analysis:** Spotless, Checkstyle, ErrorProne
- **Code Coverage:** JaCoCo
- **Mutation Testing:** PITest
- **Architectural Testing:** ArchUnit
- **Reflection/Bytecode:** ByteBuddy, Objenesis

## Essential Commands

The `justfile` (Just is a [command runner](https://just.systems/man/en/)) provides shortcuts for common development tasks:

- **Build full project:** `just verify` (or `mvn clean verify`)
- **Run Tests:** `just test` (or `mvn test`)
- **Format Code:** `just format` (or `mvn spotless:apply`)
- **Run PITest:** `just pitest` (or `mvn clean test org.pitest:pitest-maven:mutationCoverage`)
- **Install in local Maven cache:** `just local-install` (or `mvn install -Prelease`)

## Code Structure

- **`equalsverifier-parent`:** Root Maven project.
- **`equalsverifier-core`:** Contains the main library logic (`src/main/java/nl/jqno/equalsverifier/`).
- **`equalsverifier-testhelpers`:** Provides utilities for testing the library itself.
- **`equalsverifier-N` modules:** Contains code that integrates with features from JDK _N_ and that will be folded into the multi-release jar files, and tests that exercies them.
- **`equalsverifier-test-*` modules:** For different integrations (e.g., Mockito, Kotlin).
- **`equalsverifier-aggregator`, `equalsverifier-release-*` modules:** For building and verifying the multi-release jar files that get released.

## Important Considerations

- **Multi-JDK Support:** Be aware of Java version compatibility.
- **Internal vs. API:** Distinguish between public API (`nl.jqno.equalsverifier.*`) and internal implementations (`nl.jqno.equalsverifier.internal.*`).
- **Reflection & Bytecode:** EqualsVerifier uses reflection and bytecode manipulation; exercise caution when modifying related code.
- **Kotlin & Mockito:** EqualsVerifier integrates with these tools, but does not use them except for testing the integration.

## Website

The `docs/` directory contains the project documentation website. It's a static suite built with [Jekyll](https://jekyllrb.com/) and deployed to [jqno.nl/equalsverifier](https://jqno.nl/equalsverifier).

To generate the website:

- Using Docker: start the server by running `docker-compose up`.
- Using Jekyll: install the Ruby 3.x toolchain and run `bundle exec jekyll serve --watch`.

Note that the page uses the [TilburgsAns](https://www.tilburgsans.nl/) font but references it from the main site at [jqno.nl](https://jqno.nl). In development, it will fall back to a `sans-serif` font. See the font license [here](assets/tilburgsans/Ans%20Font%20License-AFL.pdf).

## Maven profiles

There are several Maven profiles that can be enabled or disabled:

| profile                      | activation                                   | purpose                                                                                                                                                        |
| ---------------------------- | -------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `modules-jdk-N`              | JDK _N_ and up                               | Build all modules and build releasable artifacts.                                                                                                              |
| `static-analysis`            | `disableStaticAnalysis` property must be off | Run static analysis checks. This only happens on a recent JDK. Can be disabled by running `mvn verify -DdisableStaticAnalysis`.                                |
| `static-analysis-checkstyle` | -                                            | Run Checkstyle checks, for Checkstyle's regression CI.                                                                                                         |
| `argline-preview`            | `preview` property must be on                | Enable Java preview features. Can be activated by running `mvn verify -Dpreview`.                                                                              |
| `argline-experimental`       | `experimental` property must be on           | Enables ByteBuddy experimental features; useful for testing EqualsVerifier on Early Access JDK builds. Can be activated by running `mvn verify -Dexperimental` |
| `pitest`                     | `pitest` property must be on                 | Used by PITest integration on GitHub. Can be activated by running `mvn verify -Dpitest`.                                                                       |
| `release`                    | Must be activated manually                   | Mixes in the modules that are needed to make a release. Running `mvn verify -Prelease` tests the release, but doesn't actually deploy it.                      |

## Signed JAR

The `lib/` folder in the `equalsverifier-test-core` module contains a local Maven repository containing a signed JAR, used to test potential ClassLoader issues. Here's how to install a JAR into it:

```sh
mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
                        -Dfile=&lt;path-to-file> -DgroupId=&lt;myGroup> \
                        -DartifactId=&lt;myArtifactId> -Dversion=&lt;myVersion> \
                        -Dpackaging=&lt;myPackaging> -DcreateChecksum=true \
                        -DlocalRepositoryPath=equalsverifier-test-core/lib
```

The signed JAR itself can be found in [this repo](https://github.com/jqno/equalsverifier-signedjar-test).
