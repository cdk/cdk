## Process this file with automake to produce Makefile.in
## $Id$

PREFIX=@prefix@
JARLIBDIR=@CDKLIBDIR@
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
  castor-0.9.5-xml.jar \
  stmml.jar \
  xindice.jar \
  xmldb.jar \
  javacc.jar \
  base.jar \
  cmlAll.jar \
  pmrlib.jar

EXTRA_DIST=${JARLIBS}

alljardir = ${JARLIBDIR}
alljar_DATA = ${JARLIBS}

SUBDIRS= libio 

${JARLIBS}:
	cd ../../../../jar; cp ${JARLIBS} ../packages/tar/cdk/jar/.
