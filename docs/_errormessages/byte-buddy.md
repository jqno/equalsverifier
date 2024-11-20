---
title: "Java _x_ is not supported by the current version of Byte Buddy which officially supports Java _y_"
---
For example:

    Java 23 (67) is not supported by the current version of
    Byte Buddy which officially supports Java 22 (66) - update
    Byte Buddy or set net.bytebuddy.experimental as a VM property

You have an older version of one of EqualsVerifier's dependencies on the class path, most likely as a transitive dependency for some other thing that you use in your project. You can check this by running:

    mvn dependency:tree

or

    gradle dependencies

Find [EqualsVerifier's pom file](https://search.maven.org/artifact/nl.jqno.equalsverifier/equalsverifier) and make a note of the dependencies and their versions. Then see if you can find these same dependencies, with older versions, elsewhere in the dependency tree. If that's the case, try to update the library that pulls in the older version of the dependency.

For example, `mvn dependency:tree` might produce this output:

    [INFO] +- org.springframework.boot:spring-boot-starter-test:jar:2.4.3:test
    [INFO] |  +- org.mockito:mockito-core:jar:3.6.28:test
    [INFO] |  |  \- net.bytebuddy:byte-buddy-agent:jar:1.10.20:test
    [...]
    [INFO] +- nl.jqno.equalsverifier:equalsverifier:jar:3.12:test
    [INFO] |  +- org.objenesis:objenesis:jar:3.3:test
    [INFO] |  \- net.bytebuddy:byte-buddy:jar:1.10.20:test

As you can see, the Spring Boot Starter Test pulls in Mockito, which pulls in Byte Buddy version 1.10.20, which is quite an old version. EqualsVerifier 3.12 specifies in its pom file that it wants Byte Buddy 1.12.19. In this case, the best solution would be to update the Spring Boot Starter Test dependency, which would automatically update both Mockito and Byte Buddy.

Alternatively, you could explicitly include Byte Buddy as a direct dependency at its most recent version in your project's build script. That way, this version is chosen before whatever version gets pulled in as a transitive dependency.

Finally, you could use EqualsVerifier's `equalsverifier-nodep` dependency. This is a so-called uberjar which contains all of EqualsVerifier's dependencies within it, so it will never cause a version conflict. The downside of using this is that you lose explicit control of which versions of dependencies you can use in your project, and that the jar file is _a lot_ bigger than the regular one.
