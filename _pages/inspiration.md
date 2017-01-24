---
title: Inspiration
permalink: /inspiration/
---
The verifications are inspired by:

* _Effective Java, Second Edition_ by Joshua Bloch, Addison-Wesley, 2008: Items 8 (_Obey the general contract when overriding `equals`_) and 9 (_Always override `hashCode` when you override `equals`_).
* _Programming in Scala_ by Martin Odersky, Lex Spoon and Bill Venners, Artima Press, 2008: Chapter 28 (_Object equality_) / Second Edition, 2011: Chapter 30 (_Object equality_). A Java-centric version of this chapter can be found online [here](http://www.artima.com/lejava/articles/equality.html).
* _JUnit Recipes_ by [J.B. Rainsberger](https://twitter.com/jbrains/status/661556171166392320), Manning, 2005: Appendix B.2 (_Strangeness and transitivity_).
* _How Do I Correctly Implement the equals() Method?_ by Tal Cohen, Dr. Dobb's Journal, May 2002. Read the [article](http://www.ddj.com/java/184405053) and Cohen's [follow-up](http://tal.forum2.org/equals).

And of course GSBase's `EqualsTester` by Mike Bowler, Gargoyle Software, which I think is pretty good, but lacking with respect to inheritance. Also, it requires that examples be given, while EqualsVerifier can auto-generate them in most cases (which, of course, is way cool). You can find it on [SourceForge](http://gsbase.sourceforge.net/index.html).

