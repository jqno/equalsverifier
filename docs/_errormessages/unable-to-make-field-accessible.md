---
title: 'Unable to make field foo accessible: module bar does not "opens bar" to baz'
---
This error message occurs in older versions of EqualsVerifier. If you upgrade, the error message wil probably be replaced with "The class is not accessible via the Java Module system" or "Field foo of type Bar is not accessible via the Java Module system". If so, please check [that error message](/equalsverifier/errormessages/class-or-field-is-not-accessible-jpms)
