# Makefile for making Debian GNU/Linux packages
#
DESTDIR  =
PACKAGE  = cdk
BIN      = $(DESTDIR)/usr/bin
LIBS     = ${DESTDIR}/usr/share/java
EXTRA    = ${DESTDIR}/usr/share/${PACKAGE}/lib
HTMLDOC  = ${DESTDIR}/usr/share/doc/${PACKAGE}/html

VERSION=`cat ../../tar/cdk-libs/VERSION`

CLASSPATH=`find /tmp/${PACKAGE}-${VERSION}/jars -name "*.jar" | tr '\n' ':'`

DEBIANFILES=changelog control copyright dirs postinst Makefile prerm rules

all: deb

archive:

clean:
	@rm -f -r cdk-*
	@rm -f -r cdk_*

install:
	@install ./jar/*jar ${EXTRA}

setup: clean
	@echo "Cleaning old stuff..."
	@rm -f -r /tmp/libcdkextra*
	@rm -f -r /tmp/cdk-libs*
	@echo "Setting up build dir for version ${VERSION}"
	cp ../../tar/cdk-libs/cdk-libs-${VERSION}.tar.gz /tmp/libcdkextra-${VERSION}.tar.gz
	echo ${VERSION} > /tmp/LIBCDKEXTRAVERSION
	cd /tmp; tar zxf libcdkextra-`cat LIBCDKEXTRAVERSION`.tar.gz
	cd /tmp; mv cdk-libs-`cat LIBCDKEXTRAVERSION` libcdkextra-`cat LIBCDKEXTRAVERSION`
	rm /tmp/LIBCDKEXTRAVERSION
	@echo " ... copying debian specific files"
	mkdir -p /tmp/libcdkextra-${VERSION}/debian
	cp ${DEBIANFILES} /tmp/libcdkextra-${VERSION}/debian
	cp Makefile /tmp/libcdkextra-${VERSION}/.
	@echo " ... removing files provided by other Debian packages"
	@echo " ... removing unneeded files"

deb: setup
	@echo "Building package for version ${VERSION}"
	cd /tmp/libcdkextra-${VERSION}; dpkg-buildpackage -rfakeroot
