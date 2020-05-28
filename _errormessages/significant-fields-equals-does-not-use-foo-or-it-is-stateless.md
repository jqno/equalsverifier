---
title: "Significant fields: equals does not use foo, or it is stateless"
---
The cause for this error message is usually that the `foo` field is not used in the class's `equals` contract: hence, "equals does not use foo".

In this case, the error can be fixed by simply including `foo` in your `equals` and `hashCode` methods; or if this is intentional, you can suppress `Warning.ALL_FIELDS_SHOULD_BE_USED` or call `.withIgnoredFields`.

This error message can also occur when `foo` is included, but it is of a type with no state. A stateless type is a type that has no fields (or derives its `equals` method directly from `Object` without overriding it.) In this case, the only way to make the error go away is to suppress `Warning.ALL_FIELDS_SHOULD_BE_USED` or to call `.withIgnoredFields`.

See also the chapter on [ignoring fields](/equalsverifier/manual/ignoring-fields) in the manual.
