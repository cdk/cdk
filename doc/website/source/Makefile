PROCOPT=--nonet
PROC=xsltproc ${PROCOPT}
STYLEDIR=xsl
TABSTYLE=${STYLEDIR}/tabular.xsl
STYLESHEET=${TABSTYLE}
STYLEOPT=


.PHONY : clean

all:
	make website
	cp *.html ../target/.
	mv ../target/index.html ../target/index_template.html

include depends.tabular

autolayout.xml: layout.xml
	$(PROC) ${STYLEDIR}/autolayout.xsl $< > $@
	make depends

%.html: autolayout.xml
	$(PROC) ${STYLEOPT} ${STYLESHEET} $(filter-out autolayout.xml,$^) > $@

depends: autolayout.xml
	$(PROC) ${STYLEDIR}/makefile-dep.xsl $< > depends.tabular
