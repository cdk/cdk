JAVAC=/home/analchem/egonw/acf7/j2sdk1.4.0/bin/javac
TOOLSJAR=/home/analchem/egonw/acf7/j2sdk1.4.0/lib/tools.jar

all: JavaDocStats.class MakeKeywordIndexDoclet.class

JavaDocStats.class: source/JavaDocStats.java
	${JAVAC} -classpath ${TOOLSJAR} -d . -O source/JavaDocStats.java

MakeKeywordIndexDoclet.class: source/MakeKeywordIndexDoclet.java
	${JAVAC} -classpath ${TOOLSJAR} -d . -O source/MakeKeywordIndexDoclet.java
