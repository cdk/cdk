@echo off
echo Set CDK_HOME to the Jmol installation directory.
if "%CDK_HOME%"=="" set CDK_HOME=.

@set libDir=%CDK_HOME%\jar
@java -cp "%libDir%\cdk-apps.jar;%libDir%\jmol.jar" org.openscience.cdk.applications.Viewer %1 %2 %3 %4 %5 %6 %7 %8 %9
