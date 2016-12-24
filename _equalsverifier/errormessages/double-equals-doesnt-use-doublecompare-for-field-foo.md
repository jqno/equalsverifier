---
title: "Double: equals doesn't use Double.compare for field foo"
---
You should never use `==` to compare doubles or floats in an `equals` method. Always use `Double.compare` or [`Double.compareTo`](http://download.oracle.com/javase/6/docs/api/java/lang/Double.html#compareTo%28java.lang.Double%29) instead.

Josh Bloch explains this in his book Effective Java. The short summary is that this method will do the right thing when confronted with `NaN` and positive and negative infinities. For example, `Float.NaN` is not equal to itself, but it has to be for `equals`, or you would never be able to retrieve it from a `HashMap`.
