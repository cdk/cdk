# Makefile for making Debian GNU/Linux packages
#
DESTDIR     =
CURRDIR     = .
JOELIBDIR   = ../libjoelib-java/ant
JOELIBLIB   = ../libjoelib-java/lib
JOELIBJAR   = ../libjoelib-java/dist/bin-dist/JOELib/lib
LIBS        = ${DESTDIR}/usr/share/java
EXTRA       = ${DESTDIR}/usr/share/libcdk-java/lib
DEBJAVA     = /usr/share/java
DOCBOOKDIR  = /usr/share/xml/docbook/stylesheet/nwalsh


all:
	@echo INFO: Build cdk-dist-all with joelib.jar
	@ant clean && ant clearstamp
	@ant -l builddeb.log -Ddocbook.dir=${DOCBOOKDIR} dist-all

joelib:
	@echo INFO: Build joelib.jar without cdk*.jar
	-cd ${JOELIBLIB} && test -e cdk-core.jar && rm cdk-*.jar
	-cd ${JOELIBLIB} && test -e vecmath1.2-1.14.jar && rm vecmath1.2-1.14.jar
	@cp jar/vecmath1.2-1.14.jar ${JOELIBLIB}
	@cd ${JOELIBDIR} && ant clean && ant -l dist1.log dist
	@echo INFO: Copy joelib.jar to jar/libio
	@cp ${JOELIBJAR}/joelib.jar ./jar/libio/
	@echo INFO: Build cdk-dist
	@ant clean && ant clearstamp
	@ant -l builddist2.log -Ddocbook.dir=${DOCBOOKDIR} dist
	@echo INFO: Copy cdk-dist to joelib-dir
	@cp dist/jar/* ${JOELIBLIB}
	@echo INFO: Build joelib.jar with cdk*.jar
	@cd ${JOELIBDIR} && ant clean && ant -l dist2.log dist
	@cp ${JOELIBJAR}/joelib.jar ./jar/libio/

clean:
	@ant clean
	@rm -f ./jar/libio/joelib.jar
	@rm -f build*.log

install:
	@echo INFO: Install cdk*.jar
	@install ./dist/jar/*.jar ${LIBS}

common:
	@echo INFO: Install common jars
	@install ./jar/baysmith-io.jar ${EXTRA}
	@install ./jar/dadml.jar ${EXTRA}
	@install ./jar/printf.jar ${EXTRA}
	@install ./jar/vecmath1.2-1.14.jar ${EXTRA}
	@install ./jar/xindice.jar ${EXTRA}
	@install ./jar/xmldb.jar ${EXTRA}
	@install ./jar/stmml.jar ${EXTRA}
	@install ./jar/castor-0.9.5-xml.jar ${EXTRA}
	@install ./jar/cmlAll.jar ${EXTRA}
	@install ./jar/base.jar ${EXTRA}
	@install ./jar/pmrlib.jar ${EXTRA}
	@install ./jar/jmolApis.jar ${EXTRA}
	@install ./jar/jmolIO.jar ${EXTRA}
	@install ./jar/jfontchooser.jar ${EXTRA}
	# @install ./jar/libio/joelib.jar ${EXTRA}

