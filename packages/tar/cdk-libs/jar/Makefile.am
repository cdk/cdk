## Process this file with automake to produce Makefile.in
## $Id$

PREFIX="@prefix@"
JARLIBDIR="${PREFIX}/lib/cdk"
JARLIBS=vecmath1.2-1.14.jar postgresql7.0-1.2.jar mysql-2.0.4.jar \
        gnujaxp.jar baysmith-io.jar dadml.jar JSX0.8.20.1.jar

EXTRA_DIST=${JARLIBS}

alljardir = "${JARLIBDIR}"

alljar_DATA = ${JARLIBS}

${JARLIBS}:
	cd ../../../../jar; cp ${JARLIBS} ../packages/tar/cdk-libs/jar/.
