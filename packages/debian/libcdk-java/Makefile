# Makefile for making Debian GNU/Linux packages
#
DESTDIR     =
JOELIBDIR   = ../libjoelib-java/ant
JOELIBLIB   = ../libjoelib-java/lib
JOELIBJAR   = ../libjoelib-java/dist/bin-dist/JOELib/lib
APIDOC      = ${DESTDIR}/usr/share/doc/libcdk-java/apidoc
DOCBOOKDIR  = /usr/share/xml/docbook/stylesheet/nwalsh


all:
	@echo INFO: Build cdk-dist-all with joelib.jar
	@ant clean && ant clearstamp
	@ant -l builddeb.log -Ddocbook.dir=${DOCBOOKDIR} dist-all javadoc
	# Make manpages.
	@cd debian/cdk-manpages && for mxml in "*.xml"; \
	do \
		xsltproc /usr/share/xml/docbook/stylesheet/nwalsh/manpages/docbook.xsl $$mxml; \
	done

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
	@ant -l builddist.log -Ddocbook.dir=${DOCBOOKDIR} dist
	@echo INFO: Copy cdk-dist to joelib-dir
	@cp dist/jar/* ${JOELIBLIB}
	@echo INFO: Build joelib.jar with cdk*.jar
	@cd ${JOELIBDIR} && ant clean && ant -l dist2.log dist
	@cp ${JOELIBJAR}/joelib.jar ./jar/libio/

clean:
	@ant clean
	@rm -f ./jar/libio/joelib.jar
	@rm -f build*.log
	@rm debian/cdk-manpages/cdk-*.1

javadocs:
	@echo INFO: Install API-Doc
	@cp -r ./javadoc/* ${APIDOC}

