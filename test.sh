#! /bin/sh

CLASSPATH=${CLASSPATH}:dist/jar/cdk-core.jar:dist/jar/cdk-standard.jar:dist/jar/cdk-io.jar:dist/jar/cdk-render.jar:dist/jar/cdk-extra.jar:dist/jar/cdk-test.jar:jar/JSX1.0.6.0.jar:jar/JSX1.0.6.0.jar:jar/log4j-core.jar:jar/log4j.jar:jar/printf.jar:jar/vecmath1.2-1.14.jar:jar/xerces-1.3.0.jar:jar/baysmith-io.jar:jar/gnujaxp.jar:jar/junit.jar

java -Dcdk.debugging=true -classpath ${CLASSPATH} junit.swingui.TestRunner $*
