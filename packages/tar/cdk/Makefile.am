
PREFIX="@prefix@"

SUBDIRS=jar doc bin man

JAVA3DDIR=@JAVA3DDIR@

JARLIBDIR=@CDKJARDIR@
JARLIBS=cdk-core.jar cdk-extra.jar \
        cdk-libio.jar cdk-apps.jar

alljardir = "${JARLIBDIR}"

alljar_DATA = ${JARLIBS}

EXTRA_DIST=VERSION ChangeLog build.xml javadoc.xml src

CLEANFILES=doc dist sourcedist reports \
        cdk-core.jar cdk-extra.jar \
        cdk-libio.jar cdk-apps.jar

cdk-core.jar:
	ant dist-core
	cp dist/jar/cdk-core.jar .

cdk-extra.jar:
	ant -Dpathtojava3d=${JAVA3DDIR} dist-with-java3d
	cp dist/jar/cdk-extra-with-java3d.jar cdk-extra.jar

cdk-libio.jar:
	ant dist-libio
	cp dist/jar/cdk-libio.jar .

cdk-apps.jar:
	ant dist-apps
	cp dist/jar/cdk-apps.jar .

