@echo off

set CLASSPATH=%CLASSPATH%;dist\jar\cdk-core.jar
set CLASSPATH=%CLASSPATH%;dist\jar\cdk-standard.jar
set CLASSPATH=%CLASSPATH%;dist\jar\cdk-io.jar
set CLASSPATH=%CLASSPATH%;dist\jar\cdk-render.jar
set CLASSPATH=%CLASSPATH%;dist\jar\cdk-extra.jar
set CLASSPATH=%CLASSPATH%;dist\jar\cdk-test.jar
set CLASSPATH=%CLASSPATH%;jar\JSX1.0.6.0.jar
set CLASSPATH=%CLASSPATH%;jar\JSX1.0.6.0.jar
set CLASSPATH=%CLASSPATH%;jar\log4j-core.jar
set CLASSPATH=%CLASSPATH%;jar\log4j.jar
set CLASSPATH=%CLASSPATH%;jar\printf.jar
set CLASSPATH=%CLASSPATH%;jar\vecmath1.2-1.14.jar
set CLASSPATH=%CLASSPATH%;jar\xerces-1.3.0.jar

java -classpath %CLASSPATH%;jar\junit.jar junit.swingui.TestRunner org.openscience.cdk.test.CDKTests
