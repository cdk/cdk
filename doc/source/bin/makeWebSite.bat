set JAVA_HOME=c:\programme\jdk1.3\bin
set MY_HOME=z:\
REM set MY_HOME=c:\home
set APP_HOME=%MY_HOME%\develop\classes\jar
set XML_HOME=%MY_HOME%\documents\config\xml
set XMLSOURCE=..\website.xml
set STYLESHEET=..\lib\website.local.xsl
set CLASSPATH=%APP_HOME%\xerces.jar;%APP_HOME%\xalan.jar;%APP_HOME%\bsf.jar

%JAVA_HOME%\java org.apache.xalan.xslt.Process -in %XMLSOURCE% -xsl %STYLESHEET%
del ..\..\htdocs\*.html
move *.html ..\..\htdocs

pause
