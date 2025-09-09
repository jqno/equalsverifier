default:
    @just --list

clean:
    mvn clean -Prelease

compile:
    mvn test-compile

test:
    mvn test

verify:
    mvn verify -Prelease

format:
    mvn spotless:apply

pitest:
    mvn clean test org.pitest:pitest-maven:mutationCoverage
