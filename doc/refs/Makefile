JAVA=${JAVA_HOME}/bin/java

JREFLIBS=../../jar/gnujaxp.jar
CLASSPATH=${JREFLIBS}:../jar/jreferences.jar

CONVERT=${JAVA} -cp ${CLASSPATH} org.openscience.references.applications.ConvertReference

GENERATED=cheminf.bib cheminf.dbx

all: ${GENERATED}

clean:
	rm ${GENERATED}

cheminf.bib: cheminf.btx
	${CONVERT} --input:bibtexml --output:bibtex cheminf.btx > cheminf.bib 2> /dev/null

cheminf.dbx: cheminf.btx
	${CONVERT} --input:bibtexml --output:docbook cheminf.btx > cheminf.dbx 2> /dev/null
