[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.openscience.cdk/cdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.openscience.cdk/cdk) [![build](https://github.com/cdk/cdk/actions/workflows/maven.yml/badge.svg)](https://github.com/cdk/cdk/actions/workflows/maven.yml) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=cdk&metric=bugs)](https://sonarcloud.io/summary/overall?id=cdk)


# The Chemistry Development Kit (CDK)
 
Copyright &copy; 1997-2025 The CDK Development Team

License: LGPL v2, see LICENSE.txt

[Home Page](https://cdk.github.io/) | [JavaDoc](http://cdk.github.io/cdk/latest/docs/api/index.html?overview-summary.html) | [Wiki](https://github.com/cdk/cdk/wiki) | [Issues](https://github.com/cdk/cdk/issues) | [Mailing List](https://sourceforge.net/projects/cdk/lists/cdk-user)

## Introduction

The CDK is an open-source Java library for cheminformatics and bioinformatics.

Key Features:
  * Molecule and reaction valence bond representation.
  * Read and write file formats: SMILES, SDF, InChI, Mol2, CML, and others.
  * Efficient molecule processing algorithms: Ring Finding, Kekulisation, Aromaticity.
  * Coordinate generation and rendering.
  * Canonical identifiers for fast exact searching.
  * Substructure and SMARTS pattern searching.
  * ECFP, Daylight, MACCS, and other fingerprint methods for similarity searching.
  * QSAR descriptor calculations

## Install

The CDK is a class library intended to be used by other programs, it will not run as a stand-alone program. 

The library is built with Apache Maven and currently requires Java 1.7 or later. From the root of the project run to build the JAR files for each module. The ``bundle/target/`` directory contains the main JAR with all dependencies included:

```bash
$ mvn install
```

You can also download a pre-built library JAR from [releases](https://github.com/cdk/cdk/releases). 

Include the main JAR on the Java classpath when compiling and running your code:

```bash
$ javac -cp cdk-2.9.jar MyClass.java
$ java -cp cdk-2.9.jar:. MyClass
```

If you are using Maven, you can use the **uber** ``cdk-bundle`` to grab 
everything, note it is much more efficient to use include the modules you need:

```xml
<dependency>
  <artifactId>cdk-bundle</artifactId>
  <groupId>org.openscience.cdk</groupId>
  <version>2.9</version>
</dependency>
```

If you are a Python user, the Cinfony project provides access via [Jython](http://www.redbrick.dcu.ie/~noel/CDKJython.html).
Noel O'Boyle's [Cinfony](http://cinfony.github.io/) provides a wrapper around the CDK and over toolkits exposing core
functionality as a consistent API. `ScyJava` can also be used, as explain in [ChemPyFormatics](https://cdk.github.io/chempyformatics/).

Further details on building the project in integrated development environments (IDEs) are available on the wiki:
 * [Building the CDK](https://github.com/cdk/cdk/wiki/Building-CDK)
 * [Maven Reporting Plugins](https://github.com/cdk/cdk/wiki/Maven-Reporting-Plugins)

## Getting Help

The [Toolkit-Rosetta Wiki Page](https://github.com/cdk/cdk/wiki/Toolkit-Rosetta) provides some examples for common tasks. If you need help using the CDK and have questions please use the user mailing list, [``cdk-user@lists.sf.net``](mailto:cdk-user@lists.sf.net) (**you must [subscribe here]( https://sourceforge.net/projects/cdk/lists/cdk-user) first to post**).
