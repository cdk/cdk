## Process this file with automake to produce Makefile.in
## $Id$

PREFIX=@prefix@
PACKAGE=@PACKAGE@
DOCTAR=javadoc.tar.gz
ANT=@PATHTOANT@
TAR=@PATHTOTAR@
GZIP=@PATHTOGZIP@

EXTRA_DIST=${DOCTAR} lgpl.license

CLEANFILES=$(DOCTAR)

alldocdir = "${PREFIX}/share/doc/${PACKAGE}"
alldoc_DATA = ${DOCTAR}

lgpl.license: ../../../../doc/lgpl.license
	cp ../../../../doc/lgpl.license .

javadoc.tar.gz:
	cd ..; ${ANT} -buildfile javadoc.xml html
	${TAR} cf javadoc.tar api
	${GZIP} -9 javadoc.tar

