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

DEBIANFILES=changelog control copyright postinst Makefile prerm rules cdk.install libcdk-java.install cdk.dirs libcdk-java.dirs

JAVA_HOME=/usr/lib/j2se/1.4

READONLYFILES=AUTHORS COPYING ChangeLog INSTALL LICENSE NEWS README VERSION

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
	find /tmp/cdk-${VERSION} -name "CVS" | xargs rm -R
	@echo " ... removing files provided by other Debian packages"
	rm /tmp/cdk-${VERSION}/jar/xerces-2.6.2.jar
	rm /tmp/cdk-${VERSION}/jar/xmlApis-2.6.2.jar
	rm /tmp/cdk-${VERSION}/jar/junit.jar
	@echo " ... removing unneeded files"
	@echo " ... adapt Makefiles for debian needs"
	cd /tmp/cdk-${VERSION}/jar; cat Makefile.am | grep -v xerces-2.6.2.jar | grep -v junit.jar > Makefile.am2 && mv Makefile.am2 Makefile.am
	cd /tmp/cdk-${VERSION}/jar; cat Makefile.am | grep -v xmlApis-2.6.2.jar | grep -v junit.jar > Makefile.am2 && mv Makefile.am2 Makefile.am
	cd /tmp/cdk-${VERSION}/jar; cat Makefile.am | perl -n -e "s/xerces-2.6.2.jar//; print;" > Makefile.am2 && mv Makefile.am2 Makefile.am
	cd /tmp/cdk-${VERSION}/jar; cat Makefile.am | perl -n -e "s/xmlApis-2.6.2.jar//; print;" > Makefile.am2 && mv Makefile.am2 Makefile.am
	cd /tmp/cdk-${VERSION}/jar; cat Makefile.am | perl -n -e "s/junit.jar//; print;" > Makefile.am2 && mv Makefile.am2 Makefile.am
	cd /tmp/cdk-${VERSION}; cat Makefile.am | perl -n -e "s#ant#ant -Dbuild.compiler=gcj#; print;" > Makefile.am2 && mv Makefile.am2 Makefile.am
	@echo " ... adapt build.xml for debian needs"
	cd /tmp/cdk-${VERSION}; patch < debian/patches/patch.build.xml
	@echo " ... fixing the CDKLIBDIR install paths"
	cd /tmp/cdk-${VERSION}; cat configure | perl -n -e "s#prefix/lib/cdk#prefix/share/cdk/lib#; print;" > configure2 && mv configure2 configure && chmod 755 ./configure
	cd /tmp/cdk-${VERSION}; cat configure.in | perl -n -e "s#prefix/lib/cdk#prefix/share/cdk/lib#; print;" > configure2 && mv configure2 configure.in && chmod 755 ./configure.in
	cd /tmp/cdk-${VERSION}/jar; cat Makefile.am | perl -n -e "s/^JARLIBDIR=.*/JARLIBDIR=\@CDKLIBDIR\@/; print;" > Makefile.am2 && mv Makefile.am2 Makefile.am
	@echo " ... fixing some file permissions and other stuff"
	cd /tmp/cdk-${VERSION}; chmod 644 ${READONLYFILES}
	cd /tmp/cdk-${VERSION}; rm -R -f sourcedist reports
	@echo " ... renaming ChangeLog to changelog"
	cd /tmp/cdk-${VERSION}; cp ChangeLog changelog
	cd /tmp/cdk-${VERSION}; cat Makefile.am | perl -n -e "s/ChangeLog/changelog/; print;" > Makefile.am2 && mv Makefile.am2 Makefile.am
    
deb: setup
	@echo "Building package for version ${VERSION}"
	cd /tmp/cdk-${VERSION}; dpkg-buildpackage -rfakeroot
