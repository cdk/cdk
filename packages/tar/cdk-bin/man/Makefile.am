
XML2MAN_RULE = \
        docbook2man $< > $@

CHECK_XML_RULE = for f in $^; \
 do \
  echo -n Checking $$f...; \
  rm -f $$f.dbx; \
  echo "<?xml version=\"\"?>" > $$f.dbx; \
  echo "<!DOCTYPE part PUBLIC \"-//OASIS//DTD DocBook XML V4.1.2//EN\"" >> $$f.dbx; \
  echo "        \"http://www.oasis-open.org/docbook/xml/4.1.2/docbookx.dtd\">" >> $$f.dbx; \
  echo "<part>" >> $$f.dbx; \
  echo "  <title>Man page for $$f</title>" >> $$f.dbx; \
  cat $$f >> $$f.dbx; \
  echo "</part>" >> $$f.dbx; \
  $(PATHTONSGMLS) -s xml.dcl $$f.dbx && echo ok; \
  rm -f $$f.dbx; \
 done

docbooks = cdk-fileconvert.xml cdk-dbadmin.xml

man_MANS = cdk-fileconvert.1 cdk-dbadmin.1

CLEANFILES = $(man_MANS)

EXTRA_DIST = $(docbooks)

cdk-fileconvert.1: cdk-fileconvert.xml
	$(XML2MAN_RULE)

cdk-dbadmin.1: cdk-dbadmin.xml
	$(XML2MAN_RULE)

check-xml: $(docbooks)
	$(CHECK_XML_RULE)


