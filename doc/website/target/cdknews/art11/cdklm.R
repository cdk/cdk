#
# Rajarshi Guha <rxg218@psu.edu>
# 21/12/2004
#
# Provides the fingerprint() function described in
# CDK News 2.1, available from http://cdk.sf.net/
#
require(SJava)
if (!isJavaInitialized()) {
    print('Starting JVM')
    .JavaInit()
}

descriptors <- function(files) {

    descnames <- c(
    'GravitationalIndexDescriptor',
    'HBondAcceptorCountDescriptor',
    'BCUTDescriptor',
    'KappaShapeIndicesDescriptor',
    'XLogPDescriptor'    
    )

    descframe <- matrix()
    nm <- c(); x <- ''
    for (f in 1:length(files)) {

        dvec <- numeric()
        cnt <- 1

        filereader <- .JavaConstructor('FileReader', .JavaConstructor("File",files[f]) )
        chemobjectreader <- .Java(.JavaConstructor("ReaderFactory"), 'createReader', filereader)
        content <- .Java(chemobjectreader,'read', .JavaConstructor('ChemFile'))
        container <- .Java('ChemFileManipulator','getAllAtomContainers', content)

        # assume that each container contains one molecule
        molecule <- .JavaGetArrayElement(container,1)
        

        for (dn in descnames) {

            # evaluate the descriptor
            desc <- .JavaConstructor(dn)
            
            if (dn == 'BCUTDescriptor') {
                params <- .JavaArrayConstructor("Integer",dim=c(2))
                .JavaSetArrayElement(params,.JNew('Integer',as.integer(2)),1)
                .JavaSetArrayElement(params,.JNew('Integer',as.integer(2)),2)
                desc$setParameters(params)
            }
            dval <- .Java(desc, 'calculate',molecule)


            if (f == 1) {
                x <- desc$getSpecification()$getImplementationTitle()
                print(x)
                x <- strsplit(x,'\\.')[[1]]
                x <- substr(x[length(x)], 1,3)
            }
            
            # extract based on class type of return object
            if (javaIs(dval,'IntegerResult')) {
                dvec[cnt] <- dval$intValue()
                cnt <- cnt + 1
                if (f == 1) {nm <- c(nm, x)}
            } else if (javaIs(dval, 'DoubleResult')) {
                dvec[cnt] <- dval$doubleValue()
                cnt <- cnt + 1
                if (f == 1) {nm <- c(nm, x)}
            } else if (javaIs(dval,'DoubleArrayResult')) {
                len <- dval$size()
                for (i in 1:dval$size()) {
                    dvec[cnt] <- dval$get(as.integer(i-1))
                    cnt <- cnt + 1
                }
                if (f == 1) {nm <- c(nm, paste(x,1:len,sep=''))}
            }
        }
        if (f == 1) {
            descframe <- matrix(0, ncol=length(dvec))
            descframe <- rbind(descframe,dvec)
        } else {
            descframe <- rbind(descframe, dvec)
        }
    }
    x <- data.frame(descframe[-1,], row.names=NULL)
    names(x) <- nm
    x
}

