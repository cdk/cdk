PROCOPT=--nonet
PROC=xsltproc ${PROCOPT}
STYLEDIR=xsl
TABSTYLE=${STYLEDIR}/tabular.xsl
STYLESHEET=${TABSTYLE}
STYLEOPT=

XML_FILES := $(wildcard *.xml)

.PHONY : clean

all:
	make website
	make install

install:
	cp *.html ../target/.
	mv ../target/index.html ../target/index_template.html

update: update-stats update-keyword

stats: javadoc-stats.html javancss-stats.html
update-stats: javancss-tmp.xml

keyword: update-keyword
update-keyword: ../../javadoc/MakeKeywordIndexDoclet.class ../../javadoc/keyword.index.xml

../../javadoc/MakeKeywordIndexDoclet.class:
	cd ../../javadoc; make

../../javadoc/keyword.index.xml:
	cd ../../javadoc; ant keyword.index

include depends.tabular

dist-clean: clean
	rm javancss-tmp.xml

autolayout.xml: layout.xml
	$(PROC) ${STYLEDIR}/autolayout.xsl layout.xml > $@
	make depends

javadoc-stats.html: autolayout.xml javadoc-data.xml javadoc-stats.xml
	$(PROC) ${STYLEOPT} ${STYLESHEET} javadoc-stats.xml > javadoc-stats.html

javancss-stats.html: autolayout.xml javancss-data.xml javancss-stats.xml
	$(PROC) ${STYLEOPT} ${STYLESHEET} javancss-stats.xml > javancss-stats.html

%.html:
	$(PROC) ${STYLEOPT} ${STYLESHEET} $^ > $@

depends: autolayout.xml
	$(PROC) ${STYLEDIR}/makefile-dep.xsl $^ > depends.tabular

javancss-tmp.xml:
	cd javancss/bin; ./javancss -package -xml -recursive ../../../../../src > ../../javancss-tmp.xml

javancss-data.xml: javancss-tmp.xml xsl/javancss2docbook.xsl
	xsltproc xsl/javancss2docbook.xsl javancss-tmp.xml > javancss-data.xml

javadoc-data.xml: javancss-tmp.xml xsl/javancss2docbook-javadoc.xsl
	xsltproc xsl/javancss2docbook-javadoc.xsl javancss-tmp.xml > javadoc-data.xml
	
check-xml: $(XML_FILES)
	@if test -z "$(XML_FILES)"; \
	then \
	    echo No XML files to validate; \
	else \
	    for f in $(XML_FILES); \
	    do \
	        /usr/ucb/echo -n Checking $$f...; \
	        xmllint --noout --valid $$f 2> xmllint.error; \
	        if test -s xmllint.error; \
	        then \
	            echo failed; \
	            cat xmllint.error; \
	            rm -f xmllint.error; \
	        else \
	            echo ok; \
	            rm -f xmllint.error; \
	        fi; \
	    done; \
	fi
