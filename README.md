[![Build Status](https://travis-ci.org/cdk/cdk.svg?branch=master)](https://travis-ci.org/cdk/cdk)

# The Chemistry Development Kit (CDK)
 
Copyright 1997-2017 The CDK Development Team

License: LGPL v2, see doc/lgpl.license

## Introduction

You are currently reading the README file for the Chemistry Development Project (CDK).
This project is hosted under http://cdk.github.io/
Please refer to these pages for updated information and the latest version of the CDK. CDK's API documentation is available though our [Github site](http://cdk.github.io/cdk/).

The CDK is an open-source library of algorithms for structural chemo- and bioinformatics, implemented in 
the programming language Java(tm). The library is published under terms of the the 
GNU Lesser General Public License v2. This has implications on what you can do with sources and
binaries of the CDK library. For details, please refer to the file LICENSE, which should have been
provided with this distribution.

PLEASE NOTE: This is a library of useful data structures and algorithms to manipulate them 
from the area of structural chemo- and bioinformatics. As such, it is intended for the use by
programmers, who wish to save some effort by reusing code. It is not intended for the enduser. 
If you consider yourself to be more like user, you might not find what you wanted. 
Please refer to other projects like the JChemPaint project (http://jchempaint.github.com/)
or the Jmol project (http://www.jmol.org/) for programs that actually take advantage of the 
CDK library.

## Compiling

Compiling the library is performed with Apache Maven and requires Java 1.7 or later:

```bash
cdk/$ ls pom.xml
pom.xml
cdk/$ mvn compile
```

This will produce a 'jar' file for each module located in each modules 'target/' directory.

## Creating the JavaDoc

The JavaDoc documentation for the API describes all of the CDK classes in detail. It functions as
the user manual for the CDK, although you should also look at the list of examples and tutorials
below. 

Before creating the JavaDoc you will need to install the CDK build util project in your local maven repo.

```bash
git clone https://github.com/cdk/cdk-build-util
cd cdk-build-util
mvn install
```

The documentation on the main project can then be created:

```bash
cdk/$ ls pom.xml
pom.xml
cdk/$ mvn javadoc:aggregate
```

The documentation is created as a series of .html pages in target/site/apidocs. If you use firefox, you can read
the documentation using the following command:

```bash
cdk/$ firefox target/site/apidocs/index.html
```

## Creating a Jar of all sources

To create a Jar containing all source files use the following command on the main pom. 

```bash
cdk/$ mvn source:aggregate
```

The `cdk-{version}-sources.jar` will be generated in the `target/directory`.

## Running tests

IMPORTANT: this requires the Git version of the sources, because the test files are not included in
the source code distribution.

After you compiled the code, you can do "mvn test" to run the test suite of non-interactive, automated
tests.
Upon "mvn test", you should see something like:

    Tests run: 199, Failures: 0, Errors: 0, Skipped: 0

As you can see, the vast majority of tests ran successfully, but that there
are failures and errors. 

You can run the tests for an individual module by changing to the module directory:

```bash
cdk/$ cd descriptor/fingerprint
cdk/descriptor/fingerprint/$ mvn test
```

Tutorials on building the project in integrated development environments (IDEs) are available on the wiki:
https://github.com/cdk/cdk/wiki/Building-CDK

## Using CDK

CDK is a class library intended to be used by other programs. It will not run 
as a stand-alone program, although it contains some GUI- and command
line applications. If your project is also using maven you can install the 
library in your local repository (~/.m2/repository) as follows:

```bash
cdk/$ mvn install -Dmaven.test.failure.ignore=true
```

A large bundled jar with all dependencies can also be built. If you have locally
made modifications to the source code you will need to install these to your
local repository. The jar will in the target directory of the 'bundle' module.

```bash
cdk/$ mvn install -DskipTests=true
cdk/$ ls bundle/target/cdk-{version}.jar
```

If you have not made any changes you need only package the bundle module. The other
modules will be automatically downloaded.

```bash
cdk/$ cd bundle
cdk/$ mvn package
cdk/$ ls target/cdk-{version}.jar
```

## Maven Artefacts

Maven artefacts of each module are deployed to the Maven Central Repository. To use a CDK module 
just specify the dependency in your `pom.xml`. Any additional requirements of the module will
also be downloaded and included.

```xml
<dependency>
  <groupId>org.openscience.cdk</groupId>
  <artifactId>cdk-fingerprint</artifactId>
  <version>1.5.10</version>
</dependency>
```

To include everything in the library use the `cdk-bundle` artefact.

### Maven reporting plugins 

This section details how to run the plugins and access the reports.

#### PMD

[PMD](http://en.wikipedia.org/wiki/PMD_(software)) analyses code style (e.g. variable naming, complexity) and reports potential bugs. Currently only production (non-test) code is inspected. The following snippet shows how to run PMD on the 'cdk-silent' module.

```
cdk/: cd base/silent
cdk/base/silent: ls
cdk/base/silent: mvn pmd:pmd
cdk/base/silent: open target/site/pmd.html 
```

#### java-formatter

As a relatively mature project with many different developers there are many different formatting styles used in the CDK source code. Following patches from different IDEs with different settings some files have gotten pretty messy. The java-formatter tidies up the code using consistent settings. 

The formatting settings are in the cdk-build-util project [cdk-build-util/.../cdk-formatting-conventions.xml](https://github.com/cdk/cdk-build-util/blob/master/src/main/resources/cdk-formatting-conventions.xml).

To run the formatter on the silent module

```
cdk/: cd base/silent
cdk/base/silent: ls
cdk/base/silent: mvn java-formatter:format
[INFO] --- maven-java-formatter-plugin:0.4:format (default-cli) @ cdk-silent ---
[INFO] Using 'UTF-8' encoding to format source files.
[INFO] Number of files to be formatted: 76
[INFO] Successfully formatted: 76 file(s)
[INFO] Fail to format        : 0 file(s)
[INFO] Skipped               : 0 file(s)
[INFO] Approximate time taken: 3s
```

#### JaCoCo

[JaCoCo](http://en.wikipedia.org/wiki/Java_Code_Coverage_Tools#JaCoCo) is a tool for analysing test coverage. JaCoCo can install [agent](http://www.javabeat.net/introduction-to-java-agents/) instrumentation and check exactly which lines are called and missed by tests. This not only serves as a quality measure but also can guide optimisation, "why isn't that conditional ever hit by my tests, is it even possible?". 

I'll use the new MMFF atom typing to demonstrate:

```
cdk/: cd tool/forcefield
cdk/tool/forcefield: ls
cdk/tool/forcefield: mvn jacoco:prepare-agent test
cdk/tool/forcefield: mvn jacoco:report
cdk/tool/forcefield: open target/site/jacoco/index.html
```

The contribute method determines the number of pi electrons for an element with specified valence (v) and connectivity (x). We can see that two lines are flagged as yellow. On inspection we can see that 1 of 4 branches was missed. There are four branches because of two conditionals (2^2=4) and one of them is missed.

![JaCoCo Report Example](http://i56.photobucket.com/albums/g187/johnymay/cdk-wiki/jacoco-mmff-example_zps529c0073.png)

IDEs and CI servers (Jenkins) can also integrate the reports directly.

Reporting coverage when the tests are separate to the production code is a little more tricky but possible. Here is an example for the 'cdk-standard' module.

```
cdk/: cd base/standard
cdk/base/standard: mvn install
cdk/base/standard: cd ../test-standard
cdk/base/test-standard: mvn jacoco:prepare-agent test
cdk/base/standard: cd ../standard
cdk/base/standard: mvn jacoco:report
cdk/base/standard: open target/site/jacoco/index.html
```

#### Dependency Tree

Maven can create a dependency tree with the following command:

```
cdk/: mvn dependency:tree
```

## Examples and tutorials

To get started using the CDK, you may be interested in the following websites which contain
examples and tutorials:

* http://pele.farmbio.uu.se/planetcdk/
* http://rguha.net/code/java/
* http://www.redbrick.dcu.ie/~noel/CDKJython.html

To keep up with the latest news on CDK development and usage

* Google Plus - https://plus.google.com/103703750118158205464/posts
* Mailing Lists - https://sourceforge.net/p/cdk/mailman/
 
## Acknowledgments

![YourKit Logo](https://www.yourkit.com/images/yklogo.png)

The CDK developers use YourKit to profile and optimise code.

YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of <a href="https://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a>
and <a href="https://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>,
innovative and intelligent tools for profiling Java and .NET applications.
