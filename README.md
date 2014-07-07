# The Chemical Development Kit (CDK)
 
Copyright 1997-2014 The CDK Development Team

License: LGPL v2, see doc/lgpl.license

## Introduction

You are currently reading the README file for the Chemistry Development Project (CDK).
This project is hosted under http://cdk.sourceforge.net/
Please refer to these pages for updated information and the latest version of the CDK.

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

Compiling the library is performed with Apache Maven and requires Java 1.6.0 or later:

```bash
cdk/$ ls pom.xml
cdk/$ pom.xml
cdk/$ mvn compile
```

This will produce a 'jar' file for each module located in each modules 'target/' directory.

## Creating the JavaDoc documentation for the API

The JavaDoc documentation for the API describes all of the CDK classes in detail. It functions as
the user manual for the CDK, although you should also look at the list of examples and tutorials
below. 
This documentation is created by 'mvn' from the Java source code for the CDK as follows:

```bash
cdk/$ ls pom.xml
pom.xml
cdk/$ mvn javadoc:aggregate
```

The documenation is created as a series of .html pages in target/site/apidocs. If you use firefox, you can read
the documentation using the following command:

```bash
cdk/$ firefox target/site/apidocs/index.html
```

## Running tests

IMPORTANT: this requires the Git version of the sources, because the test files are not included in
the source code distribution.

After you compiled the code, you can do "mvn test" to run the test suite of non-interactive, automated
tests.
Upon "mvn test", you should see something like:

test:
Running org.openscience.cdk.test.CDKTests
Tests run: 1065, Failures: 7, Errors: 1, Time elapsed: 27,55 sec

As you can see, the vast majority of tests ran successfully, but that there
are failures and errors. 

You can run the tests for an individual module by changing to the module directory:

```bash
cdk/$ cd descriptor/fingerprint
cdk/descriptor/fingerprint/$ mvn test
```

Tutorials on building the project in integrated development enviroments (IDEs) are avaialble on the wiki:
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

cdk/$ mvn install -DskipTests=true
cdk/$ ls bundle/target/cdk-{version}.jar

If you have not made any changes you need only package the bundle module. The other
modules will be automatically downloaded.

```bash
cdk/$ cd bundle
cdk/$ mvn package
cdk/$ ls target/cdk-{version}.jar
```

## Maven Artefacts

The Maven artefacts are currently deployed to the European Bioinformatics Institute (EMBL-EBI) remote repositories. To use the repositories from a maven project add the following configuration to you `pom.xml`.

```xml
<repositories>
  <repository>
    <id>ebi-repo</id>
    <url>http://www.ebi.ac.uk/intact/maven/nexus/content/repositories/ebi-repo/</url>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
  </repository>
  <repository>
    <id>ebi-repo-snapshots</id>
    <url>http://www.ebi.ac.uk/intact/maven/nexus/content/repositories/ebi-repo-snapshots/</url>
    <releases>
      <enabled>false</enabled>
    </releases>
  </repository>
</repositories>
```

You can then use a cdk module by specifying a dependency in the `pom.xml`. Any additional requirements of the module will also be included. To include a dependency you specify a version.

Using `RELEASE` specifies the latest released version. The CDK uses even minor versions for stable releases (1.0, 1.2, 1.4, ..) and odd minor numbers for developer releases (1.1, 1.3, 1.5, ..). The `RELEASE` version here refers to most recently deploy version which will normally be the developer. Snapshot builds are daily previews of the next release, for instance, `1.5.7-SNAPSHOT` is the preview of the `1.5.7` release. Generally, it is preferable to indicate a specific version requirement and only rely on snapshots when developing and a new feature is urgently required. [Ranges](http://stackoverflow.com/questions/30571/how-do-i-tell-maven-to-use-the-latest-version-of-a-dependency) can also be used.

```xml
<dependencies>
  <dependency>
    <groupId>org.openscience.cdk</groupId>
    <artifactId>cdk-fingerprint</artifactId>
    <version>1.5.6</version>
  </dependency>
</dependencies>
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
