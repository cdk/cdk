# Makefile for making Debian GNU/Linux packages
#
DESTDIR  =
PACKAGE  = cdk
BIN      = $(DESTDIR)/usr/bin
LIBS     = ${DESTDIR}/usr/share/java
EXTRA    = ${DESTDIR}/usr/share/${PACKAGE}/lib
HTMLDOC  = ${DESTDIR}/usr/share/doc/${PACKAGE}/html

VERSION=`cat ../../tar/cdk-bin/VERSION`

CLASSPATH=`find /tmp/${PACKAGE}-${VERSION}/jars -name "*.jar" | tr '\n' ':'`

DEBIANFILES=changelog control copyright dirs postinst Makefile prerm rules

all: deb

archive:

clean:
	@rm -f -r cdk-*
	@rm -f -r cdk_*

install:
	@install ./jar/*jar ${LIBS}

setup: clean
	@echo "Cleaning old stuff..."
	@rm -f -r /tmp/cdk*
	@rm -f -r /tmp/cdk-bin*
	@echo "Setting up build dir for version ${VERSION}"
	cp ../../tar/cdk-bin/cdk-bin-${VERSION}.tar.gz /tmp/cdk-${VERSION}.tar.gz
	echo ${VERSION} > /tmp/CDKVERSION
	cd /tmp; tar zxf cdk-`cat CDKVERSION`.tar.gz
	cd /tmp; mv cdk-bin-`cat CDKVERSION` cdk-`cat CDKVERSION`
	rm /tmp/CDKVERSION
	@echo " ... copying debian specific files"
	mkdir -p /tmp/cdk-${VERSION}/debian
	cp ${DEBIANFILES} /tmp/cdk-${VERSION}/debian
	@echo " ... removing files provided by other Debian packages"
	@echo " ... removing unneeded files"
	@echo " ... adapt files for debian needs"
	cd /tmp/cdk-${VERSION}; cat Makefile.am | perl -n -e "s/SUBDIRS=\ bin\ jar\ man/SUBDIRS=\ bin\ man/; print;" > Makefile.am

deb: setup
	@echo "Building package for version ${VERSION}"
	cd /tmp/cdk-${VERSION}; dpkg-buildpackage -rfakeroot
