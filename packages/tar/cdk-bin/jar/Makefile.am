## Process this file with automake to produce Makefile.in
## $Id$

PREFIX="@prefix@"
JARLIBDIR="@CDKLIBDIR@"
JARLIBS=cdk-core.jar cdk-extra.jar cdk-libio.jar cdk-apps.jar

EXTRA_DIST= $(JARLIBS)

# Don't add the CDK jars! They are not compiled from source!
# This is an issue when building the Debian package.
CLEANFILES=

alljardir = "${JARLIBDIR}"

alljar_DATA = ${JARLIBS}

cdk-core.jar:
	cp ../../../../dist/jar/cdk-core.jar cdk-core.jar

cdk-extra.jar:
	cp ../../../../dist/jar/cdk-extra-with-java3d.jar cdk-extra.jar

cdk-libio.jar:
	cp ../../../../dist/jar/cdk-libio.jar cdk-libio.jar

cdk-apps.jar:
	cp ../../../../dist/jar/cdk-apps.jar cdk-apps.jar
