set JAVA_HOME=c:\programme\jdk1.3\bin
set XMLSOURCE=..\jcpdocs.xml
REM set STYLESHEET=z:\documents\config\xml\xsl\docbook\html\chunk.xsl
REM set STYLESHEET=z:\documents\config\xml\xsl\docbookx\docbook\html\docbook.xsl
set STYLESHEET=..\lib\jcpdocs.xsl
set OUTPUTFILE=..\..\htdocs\index.html
set OUTPUTDIR="..\..\htdocs\"
set CLASSPATH=z:\develop\classes\jar\xerces.jar;z:\develop\classes\jar\xalan.jar;z:\develop\classes\jar\saxon.jar;z:\develop\classes\jar\bsf.jar

REM %JAVA_HOME%\java org.apache.xalan.xslt.Process -in %XMLSOURCE% -xsl %STYLESHEET% -out %OUTPUTDIR%

%JAVA_HOME%\java org.apache.xalan.xslt.Process -in %XMLSOURCE% -xsl %STYLESHEET% 

REM %JAVA_HOME%\java com.icl.saxon.StyleSheet %XMLSOURCE% %STYLESHEET% dir=%OUTPUTDIR%

pause
