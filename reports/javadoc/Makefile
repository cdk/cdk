all: JavaDocStats.class MakeKeywordIndexDoclet.class

JavaDocStats.class: source/JavaDocStats.java
	javac -classpath /usr/local/j2sdk1.4.0/lib/tools.jar -d . -O source/JavaDocStats.java

MakeKeywordIndexDoclet.class: source/MakeKeywordIndexDoclet.java
	javac -classpath /usr/local/j2sdk1.4.0/lib/tools.jar -d . -O source/MakeKeywordIndexDoclet.java
