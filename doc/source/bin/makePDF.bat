set JAVA_HOME=c:\programme\jdk1.3\bin
set XMLSOURCE=jcpdocs.xml
set JAR=z:\develop\classes\jar
set STYLESHEET=z:\documents\config\xml\xsl\docbook\fo\docbook.xsl
set FOPOUTPUTFILE=cdkdocs.fo
set PDFOUTPUTFILE=..\cdkdocs.pdf
set OUTPUTDIR="..\htdocs\"
set CLASSPATH=%JAR%\xerces.jar;%JAR%\w3c.jar;%JAR%\fop.jar;%JAR%\xalan.jar;%JAR%\bsf.jar

%JAVA_HOME%\java org.apache.xalan.xslt.Process -in %XMLSOURCE% -xsl %STYLESHEET% -out %FOPOUTPUTFILE%

%JAVA_HOME%\java org.apache.fop.apps.CommandLine %FOPOUTPUTFILE% %PDFOUTPUTFILE%

pause
