#!/bin/sh
XMLSOURCE=cdkdocs.xml
STYLESHEET=~/lib/docbook/xhtml/docbook.xsl
OUTPUTFILE=../htdocs/index.html
CLASSPATH=~/lib/java/xerces.jar:~/lib/java/xalan.jar
java org.apache.xalan.xslt.Process -in ${XMLSOURCE} -xsl ${STYLESHEET} -out ${OUTPUTFILE}
