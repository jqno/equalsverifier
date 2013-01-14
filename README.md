**EqualsVerifier** can be used in Java unit tests to verify whether the contract for the equals and hashCode methods in a class is met.

For full documentation, please see the [Project Page](https://code.google.com/p/equalsverifier/) on Google Code.
Pull requests are welcome! If you open one, please also [register an issue](https://code.google.com/p/equalsverifier/issues/list) or [send a message to the Google Group](https://groups.google.com/forum/?fromgroups#!forum/equalsverifier), so we can discuss it.

Quick Start
-----------

Add EqualsVerifier to your `pom`. The `groupId` is `nl.jqno.equalsverifier` and the `artifactId` is `equalsverifier`. Make sure to use the latest version, and put it in the test scope. 

Use, as follows, in your unit test:

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(My.class).verify();
    }

Or, if you prefer to use a getClass() check instead of an instanceof check in the body of your equals method:

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(My.class)
                .usingGetClass()
                .verify();
    }

With some warnings suppressed:

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(My.class)
                .suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS)
                .verify();
    }

Disclaimer
----------

For license information, see LICENSE.TXT.

Copyright 2009-2013 Jan Ouwens