The cdk-SwingJS project is a work in progress (2025.02.22). It aims (minimially) optimize CDX Java code for the both Java and JavaScript using the [java2script Eclipse plug-in transpiler and the SwingJS JavaScript runtime library](https://github.com/BobHanson/java2script). CDK-SwingJS can be utilized by any Web-based JavaScript application, whether it be node-based or just simple JavaScript on a web page, similarly to what has been used for Jmol [Jmol-SwingJS](https://github.com/BobHanson/Jmol-SwingJS), JME [JME-SwingJS](https://github.com/BobHanson/JME-SwingJS), and OpenChemLib [OCL-SwingJS](https://github.com/BobHanson/OCL-SwingJS). The transpiler allows for a common codebase for Java and JavaScript a bit like GWT but far more standard and capable, particularly in that it allows for Swing, has better handling of long number types, includes more extensions to and beyond Java 8, and allows for fully Java-compliant dynamic class loading and packaging. Simultaneous transpiling to JavaScript using an Eclipse Java compiler "[CompilationParticipant](https://github.com/BobHanson/java2script/blob/master/sources/net.sf.j2s.core/src/j2s/core/Java2ScriptCompilationParticipant.java)" means active-page comparisons in Java and JavaScript functionality and performance. No functionality of the Java is lost, and all development still continues in Java. For more examples of the success of java2script/SwingJS, see the [Working Examples](https://github.com/BobHanson/java2script/tree/master?tab=readme-ov-file#working-examples) links on that project site.


Original README follows:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.openscience.cdk/cdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.openscience.cdk/cdk) [![build](https://github.com/cdk/cdk/actions/workflows/maven.yml/badge.svg)](https://github.com/cdk/cdk/actions/workflows/maven.yml) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=cdk&metric=bugs)](https://sonarcloud.io/summary/overall?id=cdk)


# The Chemistry Development Kit (CDK)
 
Copyright &copy; 1997-2024 The CDK Development Team

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
functionality as a consistent API. `ScyJava` can also be used, as explain in [ChemPyFormatics](https://egonw.github.io/chempyformatics/).

Further details on building the project in integrated development environments (IDEs) are available on the wiki:
 * [Building the CDK](https://github.com/cdk/cdk/wiki/Building-CDK)
 * [Maven Reporting Plugins](https://github.com/cdk/cdk/wiki/Maven-Reporting-Plugins)

## Getting Help

The [Toolkit-Rosetta Wiki Page](https://github.com/cdk/cdk/wiki/Toolkit-Rosetta) provides some examples for common tasks. If you need help using the CDK and have questions please use the user mailing list, [``cdk-user@lists.sf.net``](mailto:cdk-user@lists.sf.net) (**you must [subscribe here]( https://sourceforge.net/projects/cdk/lists/cdk-user) first to post**).
 
## Acknowledgments

![YourKit Logo](https://www.yourkit.com/images/yklogo.png)

The CDK developers use YourKit to profile and optimise code.

YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of <a href="https://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a>
and <a href="https://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>,
innovative and intelligent tools for profiling Java and .NET applications.
