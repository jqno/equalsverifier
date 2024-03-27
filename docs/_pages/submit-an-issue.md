---
title: Submitting an issue
permalink: /submit-an-issue/
---
Hi there!

First off, thank you for taking the time to contribute to EqualsVerifier! You've found a bug, or thought of a way to improve EqualsVerifier, and want to let me know about it. Awesome, I appreciate it! You can open a ticket [here](https://github.com/jqno/equalsverifier/issues/new/choose).

But please keep in mind, I'm just a single person, working on this project in my spare time. I know your time is precious, and so is mine. Let's try and find a way to minimize the time we need to spend on resolving your issue.

Before you open a ticket, please take a moment to browse [the documentation](/equalsverifier). Among other things, it contains a list of common error messages and their solutions, and a manual. They might already answer your question, which saves you the effort of submitting a ticket!

If you do decide to open a ticket, please fill out the template as completely as you can. If you include an error message, please include the full stack trace. You'd be surprised how many people strip that out "to save space".

Also, you would do me a huge favour if you could include a _minimal reproduction_ to demonstrate your problem. You'll be doing yourself a favour too, because that way I can get back to you much more quickly. This is what the rest of this document is about.

## What I need

Ideally, I would like to be able to copy/paste your reproducer directly from the GitHub issue into my editor. That includes:

- an EqualsVerifier invocation,
- the class EqualsVerifier is testing,
- all the import statements required to compile both,
- all the classes that the class under test depends on (i.e., superclasses, fields, inner classes, etc.),
- anything else needed to compile the code.

However! I also need the reproduction to be _minimal_. That means that it contains the least amount of code necessary to trigger the issue. The less code you give me, the quicker I'll be able to find your problem. That means:

- try to eliminate any third-party dependencies. If you can't, specify which one you need, including its version number;
- try to eliminate any annotations you might have on your class, like Lombok, Spring, JPA/Hibernate, etc.;
- try to eliminate classes that the class under test depends on (i.e., superclasses, fields, inner classes, etc.);
- try to eliminate getters, setters, and other methods that don't directly contribute to `equals`/`hashCode`;
- try to eliminate generics as much as possible. If you're testing a generic class, but the generics aren't needed to reproduce the issue, perhaps you can remove them.

If you use Lombok, please consider [de-lomboking](https://projectlombok.org/features/delombok) your code. Also, StackOverflow has [some tips](https://stackoverflow.com/help/minimal-reproducible-example) on how you can create a minimal reproducer.

## But…

I know this is a big ask, because it means more work for you. Perhaps you might react in one of these ways:

- "I already described what you need in the issue"

Thank you for taking the time to describe your issue! I really do appreciate it. Sometimes, it's all I need. But sometimes, I really do need to see the code to figure out what's going on.

- "it's too much work to create a minimal reproduction"

It's your code base; you know it much better than I do. It'll be much less work for you than it would be for me. The less work I need to do to reproduce your issue, the quicker I can help you find a solution.

- "why can't you use my production repository to debug?"

In order to do so, I have to figure out your build process in order to be able to run the code. This almost always requires fiddling with build scripts, environment variables, configuration files, dependencies, et cetera. I have to navigate the code base to find the files that are relevant. I have to read the code and determine which parts of it are relevant to the problem, and which aren't. If I want to comment things out to find out if they're relevant, I have to be careful that it doesn't break the build, or else I might not be able to run the test.

Of course I can do all that, but it takes _a lot_ of time.

## Thank you!

I really appreciate you taking the effort to submit an issue to EqualsVerifier. It means you care enough about the project that you want to help improve it, and that makes me really grateful. But as I mentioned, I'm just one person, and my time to work on EqualsVerifier is extremely limited. If you take the effort to make a truly minimal reproducer, I can respond to you much faster.

So please, help me help you! ❤️
