## Process this file with automake to produce Makefile.in
## $Id$

PREFIX="@prefix@"
JARLIBDIR="${PREFIX}/lib/cdk"
JARLIBS=cdk-core.jar cdk-extra.jar

EXTRA_DIST=${JARLIBS}

CLEANFILES = $(JARLIBS)

alljardir = "${JARLIBDIR}"

alljar_DATA = ${JARLIBS}

cdk-core.jar:
	cp ../../../../dist/jar/cdk-core.jar cdk-core.jar

cdk-extra.jar:
	cp ../../../../dist/jar/cdk-extra-with-java3d.jar cdk-extra.jar
