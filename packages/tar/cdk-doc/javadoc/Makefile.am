## Process this file with automake to produce Makefile.in
## $Id$

PREFIX=@prefix@
DOCTAR=javadoc.tar.gz
ANT=@PATHTOANT@
TAR=@PATHTOTAR@
GZIP=@PATHTOGZIP@

EXTRA_DIST=${DOCTAR}

CLEANFILES=$(DOCTAR)

alldocdir = "${PREFIX}/share/doc"

alldoc_DATA = ${DOCTAR}

javadoc.tar.gz:
	cd ../../../..; ${ANT} -buildfile javadoc.xml html
	cd ../../../..; ${TAR} cf javadoc.tar javadoc
	cp ../../../../javadoc.tar .
	${GZIP} -9 javadoc.tar

