set JAVA_HOME=c:\programme\jdk1.3\bin
set XMLSOURCE=cdkdocs.xml
set JAR=z:\develop\classes\jar
set STYLESHEET=z:\documents\config\xml\xsl\docbookx\docbook\fo\docbook.xsl
REM set STYLESHEET=z:\documents\config\xml\xsl\docbookx\docbook\html\docbook.xsl
set HTMLOUTPUTFILE=..\htdocs\index.html
set FOPOUTPUTFILE=cdkdocs.fo
set PDFOUTPUTFILE=..\cdkdocs.pdf
set OUTPUTDIR="..\htdocs\"
set CLASSPATH=%JAR%\xerces.jar;%JAR%\w3c.jar;%JAR%\fop.jar;%JAR%\xalan.jar

%JAVA_HOME%\java org.apache.xalan.xslt.Process -in %XMLSOURCE% -xsl %STYLESHEET% -out %FOPOUTPUTFILE%

%JAVA_HOME%\java org.apache.fop.apps.CommandLine %FOPOUTPUTFILE% %PDFOUTPUTFILE%

pause
