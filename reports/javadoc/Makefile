JAVAC=${JAVA_HOME}/bin/javac
TOOLSJAR=${JAVA_HOME}/lib/tools.jar

all: JavaDocStats.class MakeKeywordIndexDoclet.class

JavaDocStats.class: source/JavaDocStats.java
	${JAVAC} -classpath ${TOOLSJAR} -d . -O source/JavaDocStats.java

MakeKeywordIndexDoclet.class: source/MakeKeywordIndexDoclet.java
	${JAVAC} -classpath ${TOOLSJAR} -d . -O source/MakeKeywordIndexDoclet.java
