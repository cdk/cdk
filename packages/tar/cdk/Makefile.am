
PREFIX=@prefix@

SUBDIRS=jar doc bin man

JAVA3DDIR=@JAVA3DDIR@

JARLIBDIR=@CDKJARDIR@
JARLIBS=cdk-core.jar cdk-extra.jar cdk-libio.jar cdk-apps.jar cdk-test.jar

# set the Java compiler
if WITHJIKES
JAVAC=-Dbuild.compiler=jikes
endif
if WITHGCJ
JAVAC=-Dbuild.compiler=gcj
else
JAVAC=
endif

alljardir = "${JARLIBDIR}"

alljar_DATA = ${JARLIBS}

EXTRA_DIST=VERSION ChangeLog build.xml javadoc.xml data src

CLEANFILES=cdk-core.jar cdk-extra.jar \
        cdk-libio.jar cdk-apps.jar cdk-test.jar \
        dist/jar/cdk-core.jar dist/jar/cdk-apps.jar \
        dist/jar/cdk-libio.jar dist/jar/cdk-extra-with-java3d.jar \
        dist/jar/cdk-test.jar

dist/jar/cdk-core.jar:
	ant ${JAVAC} dist-core

cdk-test.jar: dist/jar/cdk-test.jar
	cp dist/jar/cdk-test.jar .

dist/jar/cdk-test.jar:
	ant ${JAVAC} dist-test

cdk-core.jar: dist/jar/cdk-core.jar
	cp dist/jar/cdk-core.jar .

dist/jar/cdk-extra-with-java3d.jar:
	ant ${JAVAC} -Dpathtojava3d=${JAVA3DDIR} dist-with-java3d

cdk-extra.jar: dist/jar/cdk-extra-with-java3d.jar
	cp dist/jar/cdk-extra-with-java3d.jar cdk-extra.jar

dist/jar/cdk-libio.jar:
	ant ${JAVAC} dist-libio

cdk-libio.jar: dist/jar/cdk-libio.jar
	cp dist/jar/cdk-libio.jar .

dist/jar/cdk-apps.jar:
	ant ${JAVAC} dist-apps

cdk-apps.jar: dist/jar/cdk-apps.jar
	cp dist/jar/cdk-apps.jar .

