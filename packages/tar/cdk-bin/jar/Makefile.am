## Process this file with automake to produce Makefile.in
## $Id$

PREFIX="@prefix@"
JARLIBDIR="${PREFIX}/lib/cdk"
JARLIBS=cdk.jar

EXTRA_DIST=${JARLIBS}

CLEANFILES = $(JARLIBS)

alljardir = "${JARLIBDIR}"

alljar_DATA = ${JARLIBS}

cdk.jar:
	cp ../../../../dist/jar/cdk.jar .
