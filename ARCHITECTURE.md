# Architecture

## Build

To build EqualsVerifier, you need [Maven](https://maven.apache.org/). Just call `mvn` from the command-line, without any parameters, and you're done. Alternatively, you can use any IDE with Maven support.

There are several Maven profiles that can be enabled or disabled:

| profile                      | activation                                   | purpose                                                                                                                                                        |
| ---------------------------- | -------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `modules-jdk17`              | JDK 17 up to 20                              | Build all modules and build releasable artifacts.                                                                                                              |
| `modules-jdk21`              | JDK 21 and up                                | Build all modules and build releasable artifacts.                                                                                                              |
| `static-analysis`            | `disableStaticAnalysis` property must be off | Run static analysis checks. This only happens on a recent JDK. Can be disabled by running `mvn verify -DdisableStaticAnalysis`.                                |
| `static-analysis-checkstyle` | -                                            | Run Checkstyle checks, for Checkstyle's regression CI.                                                                                                         |
| `argline-preview`            | `preview` property must be on                | Enable Java preview features. Can be activated by running `mvn verify -Dpreview`.                                                                              |
| `argline-experimental`       | `experimental` property must be on           | Enables ByteBuddy experimental features; useful for testing EqualsVerifier on Early Access JDK builds. Can be activated by running `mvn verify -Dexperimental` |
| `pitest`                     | `pitest` property must be on                 | Used by PITest integration on GitHub. Can be activated by running `mvn verify -Dpitest`.                                                                       |
| `release`                    | Must be actuated manually                    | Mixes in the modules that are needed to make a release. Running `mvn verify -Prelease` tests the release, but doesn't actually deploy it.                      |

## Formatting

EqualsVerifier uses [Spotless for Maven](https://github.com/diffplug/spotless/tree/main/plugin-maven) with a custom [Eclipse configuration](https://github.com/jqno/equalsverifier/blob/main/build/eclipse-formatter-config.xml) to format Java files. You can check formatting using `mvn clean verify`, or run `mvn spotless:apply` to fix any formatting issues automatically.

## Modules

This project is a multi-module project to make it easier to deal with shading and multi-release jar files. See [this question on StackOverflow](https://stackoverflow.com/q/70541340/127863) for the rationale behind it.

Here's a description of the modules:

| module                        | purpose                                                               |
| ----------------------------- | --------------------------------------------------------------------- |
| docs                          | project's Jekyll website                                              |
| equalsverifier-core           | the actual EqualsVerifier code                                        |
| equalsverifier-21             | tests for record pattern matching                                     |
| equalsverifier-testhelpers    | shared types and helpers for use in tests                             |
| equalsverifier-test           | integration tests (without access to Mockito)                         |
| equalsverifier-test-jpms      | tests for the Java module system (with access to Mockito)             |
| equalsverifier-test-kotlin    | tests for Kotlin classes                                              |
| equalsverifier-test-mockito   | tests for instantiation using Mockito                                 |
| equalsverifier-aggregator     | generic release assembly description, and shared jacoco configuration |
| equalsverifier-release-main   | release assembly for jar with dependencies                            |
| equalsverifier-release-nodep  | release assembly for fat jar (with dependencies shaded in)            |
| equalsverifier-release-verify | validation tests for the releases                                     |

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

## Website

To generate the website

- Using Docker: start the server by running `docker-compose up` or `docker compose run jekyll serve`.
- Using Jekyll: install the Ruby 3.x toolchain and run `bundle exec jekyll serve --watch`

Note that the page uses the [TilburgsAns](https://www.tilburgsans.nl/) font but references it from the main site at [jqno.nl](https://jqno.nl). In development, it will fall back to a `sans-serif` font. See the font license [here](assets/tilburgsans/Ans%20Font%20License-AFL.pdf).
