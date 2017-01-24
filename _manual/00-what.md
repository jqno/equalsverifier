---
title: What does EqualsVerifier do?
permalink: /manual/what/
---
EqualsVerifier tests you `equals` and `hashCode` methods by repeatedly calling them with different values.

It checks the following properties:

* Preconditions for EqualsVerifier itself (like: did the fields specified in `withIgnoredFields` actually exist?)
* The five properties of the [`equals` contract](https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#equals(java.lang.Object)):
  * Reflexivity
  * Symmetry
  * Transitivity
  * Consistency
  * "Non-nullity"
* The same five properties within an inheritance hierarchy (if applicable)
* The [`hashCode` contract](https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#hashCode())
* That `equals` and `hashCode` are defined in terms of the same fields
* That `equals` has the correct signature, so it actually overrides `equals` instead of overload it
* That the fields of the class under test are final ([this is important for consistency](/equalsverifier/manual/immutability))
* That the class under test, or its `equals` and `hashCode` methods, are final ([this is important for symmetry and transitivity in inheritance hierarchies](/equalsverifier/manual/final))
* That `Arrays.equals` or `Arrays.deepEquals` are used for array fields
* That `Float.compare` or `Double.compare` are used for `float` and `double` fields

It also gives you 100% coverage on sensible implementation of `equals` and `hashCode`. I say 'sensible' because it's always possible to fool EqualsVerifier if you really want to 😉.

### Also, how?
The way EqualsVerifier achieves this, is through a lot of reflection and a little bit of bytecode manipulation.

First, it creates an instance of your class, without calling the constructor, in the same way that mocking frameworks do. This gives an object where all the fields are `0` or `null`. If the class isn't final, it also generates a subclass for the class to test with. Then, EqualsVerifier invents values for all the fields, and assigns these using reflection.

EqualsVerifier then calls `equals` and `hashCode` repeatedly on various permutations of these objects to see if they return the values it expects.

Finally, it also uses reflection to look at the signature of `equals`, to see if it actually overrides `equals` instead of overload it.

