## Process this file with automake to produce Makefile.in
## $Id$

PREFIX=@prefix@
JARLIBDIR=${PREFIX}/lib/cdk
JARLIBS=vecmath1.2-1.14.jar postgresql7.0-1.2.jar mysql-2.0.4.jar \
        log4j-core.jar log4j.jar printf.jar \
        gnujaxp.jar baysmith-io.jar dadml.jar JSX1.0.7.4.jar \
        junit.jar xerces-2.3.0.jar batik-awt-util.jar \
        batik-dom.jar batik-util.jar castor-0.9.4.3-xml.jar \
        xindice.jar stmml.jar xmldb.jar

EXTRA_DIST=${JARLIBS}

alljardir = ${JARLIBDIR}
alljar_DATA = ${JARLIBS}

SUBDIRS= libio 

${JARLIBS}:
	cd ../../../../jar; cp ${JARLIBS} ../packages/tar/cdk/jar/.
