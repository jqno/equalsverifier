# Default action, performed by running `just` without parameters: shows the actions that are available
default:
    @just --list

# Cleans all build artifacts
clean:
    mvn clean -Prelease

# Compiles all code and all tests
compile:
    mvn test-compile

# Runs all tests, but no static analysis and no release verification tests
test:
    mvn test

# Runs all tests, including release verification tests, and also static analysis
verify:
    mvn verify -Prelease

# Ensures all code complies with formatting rules
format:
    mvn spotless:apply

# Installs EqualsVerifier in local Maven cache, after running all tests and static analysis
local-install:
    mvn install -Prelease

# Runs mutation tests
pitest:
    mvn clean test org.pitest:pitest-maven:mutationCoverage
