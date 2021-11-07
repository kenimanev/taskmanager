# Introduction

Dear Reader,

Welcome to my submission. Nice to meet you! 

I thoroughly enjoyed solving the [iptiQ](http://iptiq.com) coding challenge, and I hope you will enjoy reading through the files.

I tried to be helpful by producing clean code and by leaving comments where applicable. However, there are 
aspects that are better explained separately from code.

# Documented decisions

## Performance
The submission does a lot of looping (O(n) operations) for cases where it is possible to optimize for speed (O(1)). 
For example
- Killing a process by pid 
- Picking the oldest process to replace when capacity has been reached

In both cases I have opted for the higher algorithmic complexity in favour of code readability. I am more than 
happy to make the necessary adjustments, and use an optimal data structure/algorithm for specific cases at a later
interview stage.

## Enum vs. Interface
You may ask why I chose to use Enum for the `ListOrder` and a full-blown interface for `ReplacementStrategy`. After all,
these appear to be similar: in terms of complexity both boil down to a single function. 

Well, I felt the ways to sort are well-defined and can be selected from a set of predefined options. Even if we decide 
to add new sorting orders, these will follow the same pattern of code, and thus can be coupled in a single enum.

On the other hand, the logic to replace/kill processes in TaskManager (when its capacity is full) 
is likely to evolve in unpredictable ways if new clients keep demanding customizations at this rate. 
This is why I decided an interface makes sense. Isolating the different options in their own implementation classes 
allows me to properly test their corresponding behaviour, without cluttering the code and the tests for TaskManager.

## Pattern Matching
I wanted to impress you by using Java17 Pattern Matching for either `ListOrder` or `ReplacementStrategy`
but after short consideration I dropped it.

The resulting code would have included concerns into TaskManager that did not belong there.

So it looks like we have to believe the omnipotent Brian Goetz, when he claims that Pattern Matching is mostly 
for handling plain data objects (see https://youtu.be/krmW1wcFvcE?t=250)

## Why no interface for TaskManager?
I don't yet see how it will be used in a larger context. Thus creating a premature interface to 
"prepare for the future" felt superfluous.

## Why do you use a Map to store processes?
I agree, that taking `Set<Process>` would have been the more natural choice. However, I really 
wanted an easy way to verify that only a single process with given `pid` can be active at any point in time, and 
I decided to use `Map<Integer, Process>` to allow the check.

## Do you always write so many comments?
No, not really. I demand from myself to produce simple, understandable code. 
Comments may become obsolete, so I tend to leave as little as I can possibly get away with. 

## Lombok usage
I suppose I am too lazy to write `toString()` and a builder for the `Process` class. I agree
that adding Lombok to the project is heavyweight, and not justified. It is my understanding 
that Kotlin has better ways to deal with similar issues.

# Open points

## Processes die of natural causes
In the real world processes can die on their own i.e. not necessarily when the TaskManager
invokes their `kill()` method. A possible extension is to pass a callback to their `start()`
method, which will be invoked when the process dies of "natural" causes.

This will then need to modify the internal state of the TaskManager instance.

The current submission does not address this important point, and is thus very incomplete. 
However, if we decide to implement support for real OS processes, this may be too much. I'd 
need to learn more from the interviewers (or the Product Owners) before proceeding with
tricky implementation.

For now, the submission demonstrates my ability to code.

# Package and execution
The delivery's build system is Maven, which most Java engineers are fairly familiar with.
You can directly import the project in your IDE and play with it.

Alternatively, you can compile and run the tool from command line.

## Maven wrapper
In case you have no Maven installed on your system, you can use the 
`mvnw` scripts in the root folder of the project to run Maven command.

These will automatically download and setup the correct Maven version on 
your local machine. Further, `mvnw` is very useful when setting up CI/CD 
pipelines on machine that don't necessarily have Maven installed.

## Build
Execute the following command to compile the project

``
mvn clean package
``

## Coverage reports
You can produce test coverage reports based on JaCoCo by executing
``
mvn test site
``

