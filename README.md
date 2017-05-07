# Intellatrade Storm Topologies

SJSU 295 Project
----------------

Table of Contents

* <a href="#project-summary">Project Summary</a>
* <a href="#build-install">Build & Install</a>
* <a href="#package-run">Package & Run</a>

---


<a name="project-summary"></a>

# Project Summary

## Prerequisites

First, you need `java` and `git` installed and in your user's `PATH`.

Next step:
    $ git clone https://github.com/juechemparathy/intellatrade.git


## IntellaTrade overview


Intellatrade project contains storm topologies, spouts and bolts that handle auto trading functionality using recommendation engine
and account management system.


<a name="build-install"></a>

## Build and install Storm jars locally

    # Must be run from the top-level directory of the Storm code repository
    $ mvn clean install -DskipTests=true

This command will build Storm locally and install its jar files to your user's `$HOME/.m2/repository/`.  When you run
the Maven command to build and run intellatrade (see below), Maven will then be able to find the corresponding version
of Storm in this local Maven repository at `$HOME/.m2/repository`.

## Running topologies with Maven

> Note: All following examples require that you run `cd examples/intellatrade` beforehand.

intellatrade topologies can be run with the maven-exec-plugin. For example, to
compile and run `AutotradeTopology` in local mode, use the command:

    $ mvn compile exec:java -Dstorm.topology=storm.sjsu.project.AutoTradeTopology


<a name="package-run"></a>
## Packaging intellatrade project for use on a Storm cluster

You can package a jar suitable for submitting to a Storm cluster with the command:

    $ mvn package

This will package your code and all the non-Storm dependencies into a single "uberjar" (or "fat jar") at the path
`target/intellatrade-{version}-jar-with-dependencies.jar`.

You can submit (run) a topology contained in this uberjar to Storm via the `storm` CLI tool:

    # Example 1: Run the RollingTopWords in local mode (LocalCluster)
    $ storm jar intellatrade-*-jar-with-dependencies.jar storm.starter.RollingTopWords

    # Example 2: Run the RollingTopWords in remote/cluster mode,
    #            under the name "production-topology"
    $ storm jar intellatrade-*-jar-with-dependencies.jar storm.sjsu.project.AutoTradeTopology production-topology remote


## Running unit tests

Use the following Maven command to run the unit tests that ship with intellatrade project.

    $ mvn test
