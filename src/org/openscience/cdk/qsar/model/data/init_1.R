require(SJava)
if (!isJavaInitialized()) {
    .JavaInit()
}
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

