all: cheminf.bib cheminf.dbx

cheminf.bib: cheminf.btx
	jref-convert --input:bibtexml --output:bibtex cheminf.btx > cheminf.bib

cheminf.dbx: cheminf.btx
	jref-convert --input:bibtexml --output:docbook cheminf.btx > cheminf.dbx
