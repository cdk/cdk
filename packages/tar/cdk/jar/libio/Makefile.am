## Process this file with automake to produce Makefile.in
## $Id$

PREFIX="@prefix@"
JARLIBDIR="${PREFIX}/lib/cdk"
JARLIBS=joelib.jar jmol.jar

EXTRA_DIST=${JARLIBS}

alljardir = "${JARLIBDIR}"

alljar_DATA = ${JARLIBS}

${JARLIBS}:
	cd ../../../../../jar/libio; cp ${JARLIBS} ../../packages/tar/cdk/jar/libio/.
