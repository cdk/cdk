PROCOPT=--nonet
PROC=xsltproc ${PROCOPT}
STYLEDIR=xsl
TABSTYLE=${STYLEDIR}/tabular.xsl
STYLESHEET=${TABSTYLE}
STYLEOPT=

.PHONY : clean

all: stats
	make website
	cp *.html ../target/.
	mv ../target/index.html ../target/index_template.html

stats: javadoc-stats.html javancss-stats.html
update-stats: javancss-tmp.xml

include depends.tabular

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
	cd javancss/bin; ./javancss -package -xml -recursive ../../../../../org > ../../javancss-tmp.xml

javancss-data.xml: javancss-tmp.xml xsl/javancss2docbook.xsl
	xsltproc xsl/javancss2docbook.xsl javancss-tmp.xml > javancss-data.xml

javadoc-data.xml: javancss-tmp.xml xsl/javancss2docbook-javadoc.xsl
	xsltproc xsl/javancss2docbook-javadoc.xsl javancss-tmp.xml > javadoc-data.xml
	
