This project was started Feb 22, 2025.

## Demo Pages

[CDK with no depict, headless](https://chemapps.stolaf.edu/inchi/site/test_CDK_SwingJSTest_core-nodepict.html) First demonstration of CDK in JavaScript. Carries out InChI-to-structure-to-inchi and inchi-to-struture-to-SMILES. 2025.02.24

[CDK with depict, headless](https://chemapps.stolaf.edu/inchi/site/test_CDK_SwingJSTest_core.html) First demonstration of CDK rendering classes in JavaScript, all of the above, plus creating a PNG image from an InChI. 2025.02.23

## Process

The cdk-SwingJS project allows minimially optimized CDK Java code to be used for both Java and JavaScript. JavaScript "class" files are created along with the standard Java class files using the java2script Eclipse plug-in transpiler; the JavaScript is then run in a browser using the SwingJS runtime library developed at St Olaf College.[ref](https://github.com/BobHanson/java2script). 

CDK-SwingJS can be utilized by any Web-based JavaScript application, whether it be node-based or just simple JavaScript on a web page -- similarly to what has been used for Jmol [Jmol-SwingJS](https://github.com/BobHanson/Jmol-SwingJS), JME [JME-SwingJS](https://github.com/BobHanson/JME-SwingJS), and OpenChemLib [OCL-SwingJS](https://github.com/BobHanson/OCL-SwingJS). 

The transpiler allows for a common codebase for Java and JavaScript a bit like GWT but far more standard and capable, particularly in that it allows for a full range of Java Swing GUI classes, has better handling of long number types, includes more extensions to and beyond Java 8, and allows for fully Java-compliant dynamic class loading and packaging. Simultaneous transpiling to JavaScript using an Eclipse Java compiler "[CompilationParticipant](https://github.com/BobHanson/java2script/blob/master/sources/net.sf.j2s.core/src/j2s/core/Java2ScriptCompilationParticipant.java)" means active-page comparisons in Java and JavaScript functionality and performance. 

No functionality of the Java is lost, and all development still continues in Java. For more examples of the success of java2script/SwingJS, see the [Working Examples](https://github.com/BobHanson/java2script/tree/master?tab=readme-ov-file#working-examples) links on that project site.

Currently, cdk-SwingJS is not a Maven project. (Wouldn't it be interesting, though, to also have Maven initiate java2script transpiling and JavaScipt packaging? But that's not the case at this point in time.) For now I'm leaving the pom.xml files here knowing that this creates a problem. The java2script transpiler needs all source code present, and there are various additional tweaks that the source code needs for JavaScript compatibility. Primarily this involves adjusting modal dialogs in Java to also allow asynchronous "pseudomodal" action in JavaScript. 

The java2script transpiler needs all source code utilized during runtime in a browser. These have been downloaded with Maven and loaded into the project in this first round. Fortunately, CDK does not have very many dependencies. org.uk.javax.vecmath is easy to hard-wire into the project without Maven. 

Bob Hanson, 2025.02.22

## Update 2025.02.24: 

Now have depict working to produce PNG files created from InChI-to-structure-to-InChI-to-structure-to-PNG

![testcdk(26)](https://github.com/user-attachments/assets/62b1a19a-9f4d-4fe5-a5b1-729ceef061ea)

	public static void getImageFromInChI(String inchi) {
		try {
			IAtomContainer mol = InChIGeneratorFactory.getInstance()
   					.getInChIToStructure(inchi, getBuilder(), "")
					.withCoordinates("2D")
					.getAtomContainer();
			DepictionGenerator dg = new DepictionGenerator().withSize(600,600);
			BufferedImage image = dg.depict(mol).toImg();
			FileOutputStream bos = new FileOutputStream("c:/temp/testcdk.png");
			ImageIO.write(image, "PNG", bos);
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

The FileOutputStream works just fine in JavaScript. It just sends the file to the downloads directory.

This required only a few very minor tweaks in TextOutput, GeneralPath, and AWTDrawVisitor

Noting that this added 120 classes!

## Update 2025.02.23

Well, that was relatively easy! 

Just a quick test: Java and JavaScript created identical results for the following short code run:

		// N variant
		long t0 = System.currentTimeMillis();
		
		IAtomContainer mol = TestMoleculeFactory.makeTetrahydropyran();
		mol.getAtom(0).setImplicitHydrogenCount(0);
		for (int i = 1; i < 6; i++)
			mol.getAtom(i).setImplicitHydrogenCount(2);
		String inchi = "InChI=1S/C41H45NO21/c43-13-27-32(51)34(53)37(56)40(61-27)59-25-11-19(45)10-21-20(25)12-26(30(42-21)17-4-7-22(46)23(47)9-17)60-41-38(63-39-36(55)31(50)24(48)14-58-39)35(54)33(52)28(62-41)15-57-29(49)8-3-16-1-5-18(44)6-2-16/h1-12,24,27-28,31-41,43-48,50-56H,13-15H2"
				+ "/b8-3+/t24-,27-,28-,31+,32-,33-,34+,35+,36-,37-,38-,39+,40-,41-/m1/s1";
		try {
			mol = InChIGeneratorFactory
					.getInstance()
					.getInChIToStructure(inchi, getBuilder(), "")
					.withCoordinates("2D")
					.getAtomContainer();
			String inchi2 = InChIGeneratorFactory.getInstance()
					.getInChIGenerator(mol)
					.getInchi();
			System.out.println(inchi);
			System.out.println(inchi2);
			System.out.println(inchi.equals(inchi2));
			String smi = new SmilesGenerator(SmiFlavor.Isomeric)
						.create(mol);
			System.out.println(smi);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println((System.currentTimeMillis() - t0) + " ms");

Output from JavaScript:

	InChI=1S/C41H45NO21/c43-13-27-32(51)34(53)37(56)40(61-27)59-25-11-19(45)10-21-20(25)12-26(30(42-21)17-4-7-22(46)23(47)9-17)60-41-38(63-39-36(55)31(50)24(48)14-58-39)35(54)33(52)28(62-41)15-57-29(49)8-3-16-1-5-18(44)6-2-16/h1-12,24,27-28,31-41,43-48,50-56H,13-15H2/b8-3+/t24-,27-,28-,31+,32-,33-,34+,35+,36-,37-,38-,39+,40-,41-/m1/s1
	InChI=1S/C41H45NO21/c43-13-27-32(51)34(53)37(56)40(61-27)59-25-11-19(45)10-21-20(25)12-26(30(42-21)17-4-7-22(46)23(47)9-17)60-41-38(63-39-36(55)31(50)24(48)14-58-39)35(54)33(52)28(62-41)15-57-29(49)8-3-16-1-5-18(44)6-2-16/h1-12,24,27-28,31-41,43-48,50-56H,13-15H2/b8-3+/t24-,27-,28-,31+,32-,33-,34+,35+,36-,37-,38-,39+,40-,41-/m1/s1
	true
	C=1C=C(C=CC1\C(=C(\C(=O)OC[C@@]2([C@]([C@@]([C@]([C@](OC=3C=C4C(C=C(C=C4O[C@]5([C@@]([C@]([C@@]([C@@](CO)(O5)[H])(O)[H])(O)[H])(O)[H])[H])O)=NC3C=6C=CC(=C(C6)O)O)(O2)[H])(O[C@]7([C@@]([C@]([C@@](CO7)(O)[H])(O)[H])(O)[H])[H])[H])(O)[H])(O)[H])[H])/[H])\[H])O
	1092 ms

Both Java and JavaScript took just over 1000 ms for this test. JavaScript loaded 434 transpiled class files. 

For this test, I did not include in the classpath all of the CDK. I just used enough to get this job done. The following CDK packages were reported loaded by JavaScript:


	org/openscience/cdk/config
	org/openscience/cdk/event
	org/openscience/cdk/formula
	org/openscience/cdk/geometry
	org/openscience/cdk/graph
	org/openscience/cdk/inchi
	org/openscience/cdk/interfaces
	org/openscience/cdk/io
	org/openscience/cdk/layout
	org/openscience/cdk/ringsearch
	org/openscience/cdk/smiles
	org/openscience/cdk/stereo
	org/openscience/cdk/templates
	org/openscience/cdk/tools

In addition, just a few source files from dependencies were needed. Namely:

	io/github/dan2097/jnainchi (2 classes)
	javax/vecmath (5 classes)
	uk/ac/ebi/beam (15 classes)
 

Modifications required in the CDK were mostly isolated to the inchi package. This just required a few modifications of [InChIGeneratorFactory](https://github.com/BobHanson/cdk-SwingJS/blob/main/src2/cdk/src/main/java/org/openscience/cdk/inchi/InChIGeneratorFactory.java) in order to allow for JNA and JS subclasses for both [InChIGenerator](https://github.com/BobHanson/cdk-SwingJS/blob/main/src2/cdk/src/main/java/org/openscience/cdk/inchi/InChIGenerator.java) and [InChIToStructure](https://github.com/BobHanson/cdk-SwingJS/blob/main/src2/cdk/src/main/java/org/openscience/cdk/inchi/InChIToStructure.java). The original classes are still there; it's just that now they are abstract classes, and any operation that is platform-specific, such as referencing JNA-InChI objects, are made abstract.

I took the liberty of a couple of additional tweaks as well:

- adding InChIToStructure.withCoordinates("2D"|"3D") (because this seemed like a likely thing a user would do anyway, and I needed that for the WASM side, since it accepts only MOL file data for input)
- adding MDLV2000Writer.setDate(dateString)  (because just adding the date for this quick use of the MOL file format required the loading of 49 class files in JavaScript just to print "022325141312". This seemed unnecessary)

Neither of these was critical.

Finally, there were two minor fixes I needed to in java2script/SwingJS. There was a mistake in the transpling of the Java 8 

  AtomIterator::new

syntax that was easily fixed, and there was also an omission of .hashCode() for number types such as Integer.TYPE. But, again, that was a quick fix. 

In all, it took about 14 hours to get this job done. About 3 hours to get the forked project going and gather the various code dependencies just so it would run in Java with all the code being transpiled and get the JavaScript running. Then about 8 hours to do the refactoring, 2 hr to test and clean up the code, and another hour or so to write this all up.


##Original README follows:

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
