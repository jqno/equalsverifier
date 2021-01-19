[![Apache License 2.0](https://img.shields.io/:license-Apache%20License%202.0-blue.svg?style=shield)](https://github.com/jqno/equalsverifier/blob/master/LICENSE.md)
[![Maven Central](https://img.shields.io/maven-central/v/nl.jqno.equalsverifier/equalsverifier.svg?style=shield)](https://maven-badges.herokuapp.com/maven-central/nl.jqno.equalsverifier/equalsverifier/)
[![Javadoc](https://javadoc.io/badge/nl.jqno.equalsverifier/equalsverifier.svg?color=blue)](https://javadoc.io/doc/nl.jqno.equalsverifier/equalsverifier)

[![Build Status](https://github.com/jqno/equalsverifier/workflows/CI/badge.svg)](https://github.com/jqno/equalsverifier/actions)
[![SemVer stability](https://api.dependabot.com/badges/compatibility_score?dependency-name=nl.jqno.equalsverifier:equalsverifier&package-manager=maven&version-scheme=semver)](https://dependabot.com/compatibility-score/?dependency-name=nl.jqno.equalsverifier:equalsverifier&package-manager=maven&version-scheme=semver)
[![Issue resolution](https://isitmaintained.com/badge/resolution/jqno/equalsverifier.svg)](https://isitmaintained.com/project/jqno/equalsverifier "Average time to resolve an issue")

**EqualsVerifier** can be used in Java unit tests to verify whether the contract for the equals and hashCode methods in a class is met.
The Maven coordinates are:

```xml
<dependency>
    <groupId>nl.jqno.equalsverifier</groupId>
    <artifactId>equalsverifier</artifactId>
    <version>3.5.2</version>
    <scope>test</scope>
</dependency>
```


# Documentation

Please see the [project's website](https://www.jqno.nl/equalsverifier).


# Contribution

Pull requests are welcome! If you open one, please also [register an issue](https://code.google.com/p/equalsverifier/issues/list) or [send a message to the Google Group](https://groups.google.com/forum/?fromgroups#!forum/equalsverifier), so we can discuss it.


# Development

## Build

To build EqualsVerifier, you need [Maven](https://maven.apache.org/). Just call `mvn` from the command-line, without any parameters, and you're done. Alternatively, you can use any IDE with Maven support.


## Formatting

EqualsVerifier uses [Google Java Format](https://github.com/google/google-java-format) to format Java files. You can call it using `mvn` (without any parameters), which will also run the tests and all the other static analysis. Or run `mvn spotless:apply` to only run the formatter.


## Conditional tests

The `src/test` folder contains, apart from the regular `java` folder, some version-specific `javaXX` folders that target specific JDKs. For instance, the `src/test/java16` folder contains unit tests that test things related to records. A Maven profile automatically picks up these folders if the JDK that runs the tests has at least that version. `nl.jqno.equalsverifier.internal.architecture.TestPresenceTest` checks that the tests are indeed picked up when the JDK version matches.


## Signed Jar

The `lib/` folder contains a local Maven repository containing a signed jar, used to test potential ClassLoader issues
<br/>
Here's how to install a jar into it:<br>
<pre>
mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
                        -Dfile=&lt;path-to-file> -DgroupId=&lt;myGroup> \
                        -DartifactId=&lt;myArtifactId> -Dversion=&lt;myVersion> \
                        -Dpackaging=&lt;myPackaging> -DcreateChecksum=true \
                        -DlocalRepositoryPath=lib
</pre>
The signed jar itself can be found in [this repo](https://github.com/jqno/equalsverifier-signedjar-test).

# Disclaimer

Copyright 2009-2020 Jan Ouwens
