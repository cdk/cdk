
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
  xmllint --valid --noout $$f.dbx && echo ok; \
  rm -f $$f.dbx; \
 done

docbooks = cdk-fileconvert.xml cdk-dbadmin.xml cdk-view.xml

man_MANS = cdk-fileconvert.1 cdk-dbadmin.1 cdk-view.1

CLEANFILES = 

EXTRA_DIST = $(docbooks) ${man_MANS}

cdk-fileconvert.1: cdk-fileconvert.xml
	$(XML2MAN_RULE)

cdk-dbadmin.1: cdk-dbadmin.xml
	$(XML2MAN_RULE)

cdk-view.1: cdk-view.xml
	$(XML2MAN_RULE)

check-xml: $(docbooks)
	$(CHECK_XML_RULE)


