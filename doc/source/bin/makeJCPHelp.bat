set JAVA_HOME=c:\programme\jdk1.3\bin
set MY_HOME=c:\home
set XMLSOURCE=..\jcphelp.xml
set STYLESHEET=%MY_HOME%\documents\config\xml\xsl\docbook\html\chunk.xsl
REM set STYLESHEET=%MYHOME%\documents\config\xml\xsl\docbookx\docbook\html\docbook.xsl
REM set STYLESHEET=..\lib\jcpdocs.xsl
set OUTPUTFILE=..\..\jcphelp\index.html
set OUTPUTDIR="..\..\jcphelp\"
set CLASSPATH=%MY_HOME%\develop\classes\jar\xerces.jar;%MY_HOME%\develop\classes\jar\xalan.jar;%MY_HOME%\develop\classes\jar\saxon.jar;%MY_HOME%\develop\classes\jar\bsf.jar

REM %JAVA_HOME%\java org.apache.xalan.xslt.Process -in %XMLSOURCE% -xsl %STYLESHEET% -out %OUTPUTDIR%

%JAVA_HOME%\java org.apache.xalan.xslt.Process -in %XMLSOURCE% -xsl %STYLESHEET% 
move 
REM %JAVA_HOME%\java com.icl.saxon.StyleSheet %XMLSOURCE% %STYLESHEET% dir=%OUTPUTDIR%
move *.html %OUTPUTDIR%
pause
