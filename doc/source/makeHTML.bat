set JAVA_HOME=c:\programme\jdk1.3\bin
set XMLSOURCE=cdkdocs.xml
set STYLESHEET=z:\documents\config\xml\xsl\docbookx\docbook\html\docbook.xsl
set OUTPUTFILE=..\htdocs\index.html
set CLASSPATH=z:\develop\classes\jar\xerces.jar;z:\develop\classes\jar\xalan.jar
%JAVA_HOME%\java org.apache.xalan.xslt.Process -in %XMLSOURCE% -xsl %STYLESHEET% -out %OUTPUTFILE%
REM %JAVA_HOME%\java org.apache.xalan.xslt.Process -in %XMLSOURCE% -xsl %STYLESHEET% 
pause
