## Process this file with automake to produce Makefile.in
## $Id$

PREFIX=@prefix@
JARLIBDIR=${PREFIX}/lib/cdk
JARLIBS=vecmath1.2-1.14.jar \
  postgresql7.0-1.2.jar \
  mysql-2.0.4.jar \
  log4j-core.jar \
  log4j.jar \
  printf.jar \
  gnujaxp.jar \
  baysmith-io.jar \
  dadml.jar \
  junit.jar \
  xerces-2.5.0.jar \
  batik-awt-util.jar \
  batik-dom.jar \
  batik-svggen.jar \
  batik-util.jar \
  batik-xml.jar \
  castor-0.9.4.3-xml.jar \
  stmml.jar \
  xindice.jar \
  xmldb.jar

EXTRA_DIST=${JARLIBS}

alljardir = ${JARLIBDIR}
alljar_DATA = ${JARLIBS}

SUBDIRS= libio 

${JARLIBS}:
	cd ../../../../jar; cp ${JARLIBS} ../packages/tar/cdk/jar/.
