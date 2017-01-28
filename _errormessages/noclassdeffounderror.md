---
title: "NoClassDefFoundError"
---
    java.lang.AssertionError: java.lang.NoClassDefFoundError: net/sf/cglib/asm/FieldVisitor

_Note_: This applies to versions of EqualsVerifier prior to version 1.5.1. As of version 1.5, EqualsVerifier is shipped as an "uber jar", which means it does not pull in any of its own dependencies anymore, and therefore won't conflict with different versions of these dependencies that may be on your classpath.

If you get this error message, then you probably have a transitive dependency problem with cglib. EqualsVerifier requires version 2.2, but other libraries may depend on earlier versions. If their dependency happens to be resolved before EqualsVerifier's, the wrong version of cglib will be loaded on your classpath.

To fix this, make sure you have cglib 2.2. If you use Maven, add the following lines to your pom:

{% highlight xml %}
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>cglib</groupId>
      <artifactId>cglib-nodep</artifactId>
      <version>2.2</version>
    </dependency>
  </dependencies>
</dependencyManagement>
{% endhighlight %}

Also, make sure that you use cglib-nodep, instead of 'vanilla' cglib!
