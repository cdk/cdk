set JAVA_HOME=c:\programme\jdk1.3\bin
set XMLSOURCE=z:\documents\config\xml\xsl\docbook\html\titlepage.templates.xml
set STYLESHEET=z:\documents\config\xml\xsl\docbook\template\titlepage.xsl
REM set STYLESHEET=z:\documents\config\xml\xsl\docbookx\docbook\html\docbook.xsl
REM set STYLESHEET=jcpdocs.xsl
set OUTPUTFILE=z:\documents\config\xml\xsl\docbook\html\titlepage.templates.xsl
set OUTPUTDIR="..\htdocs\"
set CLASSPATH=z:\develop\classes\jar\xerces.jar;z:\develop\classes\jar\xalan.jar;z:\develop\classes\jar\saxon.jar;z:\develop\classes\jar\bsf.jar

%JAVA_HOME%\java org.apache.xalan.xslt.Process -in %XMLSOURCE% -xsl %STYLESHEET% -out %OUTPUTFILE%

pause 
