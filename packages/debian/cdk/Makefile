# Makefile for making Debian GNU/Linux packages
#
DESTDIR  =
PACKGDIR = ../../tar/cdk
PACKAGE  = cdk
BIN      = $(DESTDIR)/usr/bin
LIBS     = ${DESTDIR}/usr/share/java
EXTRA    = ${DESTDIR}/usr/share/${PACKAGE}/lib
HTMLDOC  = ${DESTDIR}/usr/share/doc/${PACKAGE}/html

VERSION=`cat ${PACKGDIR}/VERSION`

DEBIANFILES=changelog control copyright dirs postinst Makefile prerm rules

JAVA_HOME=/usr/lib/j2se/1.4

all: deb

clean:

install:
	cd /tmp/cdk-${VERSION}; make install

setup: clean
	@echo "Cleaning old stuff..."
	@echo "Setting up build dir for version ${VERSION}"
	cp ${PACKGDIR}/cdk-${VERSION}.tar.gz /tmp/.
	echo ${VERSION} > /tmp/CDKVERSION
	cd /tmp; tar zxf cdk-`cat CDKVERSION`.tar.gz
	rm /tmp/CDKVERSION
	@echo " ... copying debian specific files"
	mkdir -p /tmp/cdk-${VERSION}/debian
	cp ${DEBIANFILES} /tmp/cdk-${VERSION}/debian
	cp -R patches /tmp/cdk-${VERSION}/debian/.
	@echo " ... removing files provided by other Debian packages"
	rm /tmp/cdk-${VERSION}/jar/xerces-2.3.0.jar
	rm /tmp/cdk-${VERSION}/jar/junit.jar
	@echo " ... removing unneeded files"
	@echo " ... adapt Makefiles for debian needs"
	cd /tmp/cdk-${VERSION}/jar; cat Makefile.am | grep -v xerces-2.3.0.jar | grep -v junit.jar > Makefile.am2 && mv Makefile.am2 Makefile.am
	cd /tmp/cdk-${VERSION}/jar; cat Makefile.am | perl -n -e "s/xerces-2.3.0.jar//; print;" > Makefile.am2 && mv Makefile.am2 Makefile.am
	cd /tmp/cdk-${VERSION}/jar; cat Makefile.am | perl -n -e "s/junit.jar//; print;" > Makefile.am2 && mv Makefile.am2 Makefile.am
	cd /tmp/cdk-${VERSION}; cat Makefile.am | perl -n -e "s#ant#JAVA_HOME=/usr/lib/j2se/1.4 ant#; print;" > Makefile.am2 && mv Makefile.am2 Makefile.am
	@echo " ... adapt build.xml for debian needs"
	cd /tmp/cdk-${VERSION}; patch < debian/patches/patch.build.xml

deb: setup
	@echo "Building package for version ${VERSION}"
	cd /tmp/cdk-${VERSION}; dpkg-buildpackage -rfakeroot
