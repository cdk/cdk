## Process this file with automake to produce Makefile.in
## $Id$

PREFIX="@prefix@"
JARLIBDIR="${PREFIX}/lib/cdk"
JARLIBS=

EXTRA_DIST=${JARLIBS}

alljardir = "${JARLIBDIR}"

alljar_DATA = ${JARLIBS}

${JARLIBS}:
	cd ../../../../../jar/libio; \
	    if (test -e $@) ; \
	    then \
	        cp --target-directory=../../packages/tar/cdk/jar/libio/. $$file; \
	    fi;
	
