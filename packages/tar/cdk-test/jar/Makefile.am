## Process this file with automake to produce Makefile.in
## $Id$

PREFIX="@prefix@"
JARLIBDIR="${PREFIX}/lib/cdk"
JARLIBS=junit.jar

EXTRA_DIST=${JARLIBS}

CLEANFILES = $(JARLIBS)

alljardir = "${JARLIBDIR}"

alljar_DATA = ${JARLIBS}

junit.jar:
	cp ../../../../jar/junit.jar .
