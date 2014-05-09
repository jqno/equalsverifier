**EqualsVerifier** can be used in Java unit tests to verify whether the contract for the equals and hashCode methods in a class is met.

For documentation, please see the [project's website](http://www.jqno.nl/equalsverifier).

Pull requests are welcome! If you open one, please also [register an issue](https://code.google.com/p/equalsverifier/issues/list) or [send a message to the Google Group](https://groups.google.com/forum/?fromgroups#!forum/equalsverifier), so we can discuss it.

[![Build Status](https://travis-ci.org/jqno/equalsverifier.png)](https://travis-ci.org/jqno/equalsverifier)


Build
---

To build EqualsVerifier, you need [Ant](http://ant.apache.org/). Just call `ant` from the command-line, and you're done. Alternatively, you can use [Eclipse](https://www.eclipse.org/) with [IvyDE](http://ant.apache.org/ivy/ivyde/) installed.


Project structure
---

`src/`

* `nl.jqno.equalsverifier`  
  Main EqualsVerifier logic
* `nl.jqno.equalsverifier.util`  
  Reflection helpers
* `nl.jqno.equalsverifier.util.exceptions`  
  Internally used exceptions

`test/`

* `javax.persistence`  
  Annotations used by integration tests
* `nl.jqno.equalsverifier`  
  Unit tests for specific subcomponents of EqualsVerifier
* `nl.jqno.equalsverifier.coverage`  
  Code coverage tests, which fail if coverage is less than 100%
* `nl.jqno.equalsverifier.integration.basic_contract`  
  Integration tests that cover the contract as stated in `java.lang.Object`'s javadoc
* `nl.jqno.equalsverifier.integration.extended_contract`  
  Integration tests that cover specific corner cases in the Java language, and other essential points that are discussed in other sources, such as Effective Java, but not in the javadoc
* `nl.jqno.equalsverifier.integration.extra_features`  
  Integration tests that cover non-standard situations that EqualsVerifier supports
* `nl.jqno.equalsverifier.integration.inheritance`  
  Integration tests that cover inheritance in equality relations
* `nl.jqno.equalsverifier.integration.operational`  
  Integration tests that cover issues that don't pertain to equals or hashCode themselves, but to EqualsVerifier's operation
* `nl.jqno.equalsverifier.testhelpers`  
  Utility classes for use in tests
* `nl.jqno.equalsverifier.testhelpers.annotations`  
  Annotations used by unit tests and integration tests
* `nl.jqno.equalsverifier.testhelpers.annotations.casefolding`  
  More annotations which would clash with other annotations because of casing
* `nl.jqno.equalsverifier.points`  
   Various data classes for use in unit tests and integration tests
* `nl.jqno.equalsverifier.util`  
  Unit tests for the reflection helpers


Disclaimer
---

For license information, see `LICENSE.TXT`.

Copyright 2009-2014 Jan Ouwens
