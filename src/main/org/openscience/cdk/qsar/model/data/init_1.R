require(SJava)
if (!isJavaInitialized()) {
    .JavaInit()
}
library(nnet)
library(pls.pcr)
options(show.error.messages=FALSE)

saveModel <- function(modelname, filename) {
    resp <- try( do.call('save',list(modelname,file=filename)), silent=TRUE )
}

loadModel <- function(filename) {
    modelname <- load(filename, .GlobalEnv)
    get(modelname)
}
loadModel.getName <- function(filename) {
   modelname <- load(filename)
   modelname
}
unserializeModel <- function(modelstr, modelname) {
    zzz <- paste(paste(modelstr, sep='', collapse='\n'), '\n', sep='', collapse='')
    assign(modelname, unserialize(zzz), pos=1)
    get(modelname)
}
summaryModel <- function(modelname) {
    summary(get(modelname))
}


hashmap.to.list <- function(params) {
    keys <- unlist(params$keySet()$toArray())
    paramlist <- list()
    cnt <- 1
    for (key in keys) {
        paramlist[[cnt]] <- params$get(key)
        cnt <- cnt+1
    }
    names(paramlist) <- keys
    paramlist
}

