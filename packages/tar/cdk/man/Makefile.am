PREFIX=@prefix@

SUBDIRS= xsl

XML2MAN_RULE = \
        xsltproc xsl/db2man.xsl $< 

CHECK_XML_RULE = for f in $^; \
 do \
  echo -n "Checking $$f..."; \
  rm -f $$f.dbx; \
  echo "<?xml version=\"1.0\"?>" > $$f.dbx; \
  echo "<!DOCTYPE part PUBLIC \"-//OASIS//DTD DocBook XML V4.1.2//EN\"" >> $$f.dbx; \
  echo "        \"http://www.oasis-open.org/docbook/xml/4.1.2/docbookx.dtd\">" >> $$f.dbx; \
  echo "<part>" >> $$f.dbx; \
  echo "  <title>Man page for $$f</title>" >> $$f.dbx; \
  cat $$f >> $$f.dbx; \
  echo "</part>" >> $$f.dbx; \
  xmllint --noent --valid --noout $$f.dbx && echo ok; \
  rm -f $$f.dbx; \
 done

docbooks = cdk-fileconvert.xml cdk-dbadmin.xml cdk-view.xml cdk-download.xml \
  cdk-guessformat.xml cdk-test.xml cdk-iupacname.1 cdk-substructure.xml \
  cdk-fingerprinter.xml cdk-validate.xml cdk-iupacname.xml

mandir=${PREFIX}/share/man
man_MANS = cdk-fileconvert.1 cdk-dbadmin.1 cdk-view.1 cdk-download.1 \
  cdk-guessformat.1 cdk-test.1 cdk-validate.1 cdk-iupacname.1 \
  cdk-substructure.1 cdk-fingerprinter.1 cdk-validate.1 cdk-iupacname.1

CLEANFILES = ${man_MANS}

EXTRA_DIST = $(docbooks) ${man_MANS}

cdk-fileconvert.1: cdk-fileconvert.xml
	$(XML2MAN_RULE)

cdk-dbadmin.1: cdk-dbadmin.xml
	$(XML2MAN_RULE)

cdk-validate.1: cdk-validate.xml
	$(XML2MAN_RULE)

cdk-view.1: cdk-view.xml
	$(XML2MAN_RULE)

cdk-download.1: cdk-download.xml
	$(XML2MAN_RULE)

cdk-guessformat.1: cdk-guessformat.xml
	$(XML2MAN_RULE)

cdk-test.1: cdk-test.xml
	$(XML2MAN_RULE)

cdk-iupacname.1: cdk-iupacname.xml
	$(XML2MAN_RULE)

cdk-substructure.1: cdk-substructure.xml
	$(XML2MAN_RULE)

cdk-fingerprinter.1: cdk-fingerprinter.xml
	$(XML2MAN_RULE)

check-xml: $(docbooks)
	$(CHECK_XML_RULE)


