## Process this file with automake to produce Makefile.in
## $Id$

PREFIX="@prefix@"
JARLIBDIR="${PREFIX}/lib/cdk"
JARLIBS=xerces-1.3.0.jar vecmath1.2-1.14.jar postgresql7.0-1.2.jar mysql-2.0.4.jar log4j.jar log4j-core.jar

EXTRA_DIST=${JARLIBS}

alljardir = "${JARLIBDIR}"

alljar_DATA = ${JARLIBS}

${JARLIBS}:
	cd ../../../jar; cp ${JARLIBS} ../packages/tar/jar/.
