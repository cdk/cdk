#
# Rajarshi Guha <rxg218@psu.edu>
# 18/12/2004
#
# Provides the fingerprint() function described in
# CDK News 2.1, available from http://cdk.sf.net/
#
require(SJava)
if (!isJavaInitialized()) {
    print('Starting JVM')
    .JavaInit()
}
fingerprint <- function( files ) {
    fplist <- list()
    cnt <- 1
    for (f in files) {
        filereader <- .JavaConstructor('FileReader', .JavaConstructor("File",f) )
        chemobjectreader <- .Java(.JavaConstructor("ReaderFactory"), 'createReader', filereader)
        content <- .Java(chemobjectreader,'read', .JavaConstructor('ChemFile'))
        container <- .Java('ChemFileManipulator','getAllAtomContainers', content)

        # a container might contain more than 1 molecule
        nummol <- .JavaArrayLength(container)
        for (i in 1:nummol) {
            molecule <- .JavaGetArrayElement(container,i)
            fp <- .Java('Fingerprinter','getFingerprint', molecule)
            fplist[[cnt]] <- as.numeric(strsplit(sub('}','',(sub('{','',.Java(fp,'toString') ))), split=',')[[1]])                                                                                 
            cnt <- cnt + 1
        }
    }
    fplist
}

