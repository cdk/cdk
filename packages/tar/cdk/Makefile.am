
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

dist/jar/cdk-core.jar:
	ant dist-core

cdk-core.jar: dist/jar/cdk-core.jar
	cp dist/jar/cdk-core.jar .

dist/jar/cdk-extra-with-java3d.jar:
	ant -Dpathtojava3d=${JAVA3DDIR} dist-with-java3d

cdk-extra.jar: dist/jar/cdk-extra-with-java3d.jar
	cp dist/jar/cdk-extra-with-java3d.jar cdk-extra.jar

dist/jar/cdk-libio.jar:
	ant dist-libio

cdk-libio.jar: dist/jar/cdk-libio.jar
	cp dist/jar/cdk-libio.jar .

dist/jar/cdk-apps.jar:
	ant dist-apps

cdk-apps.jar: dist/jar/cdk-apps.jar
	cp dist/jar/cdk-apps.jar .

