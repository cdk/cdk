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

all: deb

clean:

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
	@echo " ... removing files provided by other Debian packages"
	@echo " ... removing unneeded files"
	#rm /tmp/cdk-${VERSION}/jar/xerces-1.3.0.jar
	#rm /tmp/cdk-${VERSION}/jar/junit.jar
	@echo " ... adapt files for debian needs"
	cd /tmp/cdk-${VERSION}/jar; cat Makefile.am | perl -n -e "s/JSX1.0.1.1.jar.*/JSX1.0.1.1.jar/; print;" | grep -v junit.jar > Makefile.am

deb: setup
	@echo "Building package for version ${VERSION}"
	cd /tmp/cdk-${VERSION}; dpkg-buildpackage -rfakeroot
