
PREFIX=@prefix@

SUBDIRS=jar doc bin man

JAVA3DDIR=@JAVA3DDIR@

JARLIBDIR=@CDKJARDIR@
JARLIBS=cdk-core.jar cdk-standard.jar cdk-io.jar cdk-extra.jar cdk-render.jar cdk-test.jar cdk-apps.jar 

# set the Java compiler
if FREECOMPILER
if WITHJIKES
JAVAC=-Dbuild.compiler=jikes
endif
if WITHGCJ
JAVAC=-Dbuild.compiler=gcj
endif
else
JAVAC=
endif

alljardir = "${JARLIBDIR}"

alljar_DATA = ${JARLIBS}

EXTRA_DIST=VERSION ChangeLog build.xml javadoc.xml data src

CLEANFILES= cdk-core.jar cdk-extra.jar \
        cdk-libio.jar cdk-apps.jar cdk-test.jar \
        dist/jar/cdk-core.jar dist/jar/cdk-standard.jar \
        dist/jar/cdk-libio.jar dist/jar/cdk-render.jar \
        dist/jar/cdk-test.jar dist/jar/cdk-apps.jar

dist/jar/cdk-core.jar:
	ant ${JAVAC} dist-core

cdk-test.jar: dist/jar/cdk-test.jar
	cp dist/jar/cdk-test.jar .

dist/jar/cdk-test.jar:
	ant ${JAVAC} dist-test

dist/jar/cdk-standard.jar:
	ant ${JAVAC} dist-standard

cdk-standard.jar: dist/jar/cdk-standard.jar
	cp dist/jar/cdk-standard.jar .

cdk-core.jar: dist/jar/cdk-core.jar
	cp dist/jar/cdk-core.jar .

dist/jar/cdk-extra.jar:
	ant ${JAVAC} -Dpathtojava3d=${JAVA3DDIR} dist-extra

cdk-extra.jar: dist/jar/cdk-extra.jar 
	cp dist/jar/cdk-extra.jar .

dist/jar/cdk-render.jar:
	ant ${JAVAC} -Dpathtojava3d=${JAVA3DDIR} dist-render

cdk-render.jar: dist/jar/cdk-render.jar 
	cp dist/jar/cdk-render.jar .

dist/jar/cdk-libio.jar:
	ant ${JAVAC} dist-libio

cdk-libio.jar: dist/jar/cdk-libio.jar
	cp dist/jar/cdk-libio.jar .

dist/jar/cdk-apps.jar:
	ant ${JAVAC} dist-apps

cdk-apps.jar: dist/jar/cdk-apps.jar
	cp dist/jar/cdk-apps.jar .

dist/jar/cdk-io.jar:
	ant ${JAVAC} dist-io

cdk-io.jar: dist/jar/cdk-io.jar
	cp dist/jar/cdk-io.jar .

