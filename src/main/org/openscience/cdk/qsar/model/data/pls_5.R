#############################################
# PLS fit/predict converter
#############################################
plsFitConverter <-
function(obj,...) {
    tmp <- .JNew('org.openscience.cdk.qsar.model.R.PLSRegressionModelFit',
     obj$nobj, obj$nvar, obj$npred, obj$ncomp, obj$method)
    tmp$setTrainingData(
     obj$training$B, obj$training$Ypred, obj$training$RMS,
     obj$training$Xscores, obj$training$Xload,
     obj$training$Yscores, obj$training$Yload)
    tmp$PLSRegressionModelSetTrain()
    if ('validat' %in% names(obj)) {
        # Add validation fields
        tmp$setValidationData(
         obj$valid$niter, obj$valid$nLV,
         obj$valid$Ypred, obj$valid$RMS, obj$valid$RMS.sd, obj$valid$R2)
    }
    tmp
}
plsPredictConverter <- 
function(obj,...) {
    class(obj) <- 'matrix'
    .JNew('org.openscience.cdk.qsar.model.R.PLSRegressionModelPredict',ncol(obj),obj)
}
buildPLS <- function(modelname, params) {
    library(pls.pcr)
    paramlist <- hasmap.to.list(params)
    attach(paramlist)
    
    x <- matrix(unlist(x), nrow=length(x), byrow=TRUE)
    y <- matrix(unlist(y), nrow=length(y), byrow=TRUE)
    if (nrow(x) != nrow(y)) { stop('The number of observations in x & y dont match') }

    if (!ncomp) {
        ncomp <- 1:ncol(x)
    } else {
        ncomp <- unlist(ncomp)
    }

    if (!(method %in% c('PCR','SIMPLS','kernelPLS'))) {
        stop('Invalid methopd specification')
    }
    if (!(validation %in% c('none','CV'))) {
        stop('Invalid validation sepcification')
    }
    
    if (niter == 0 && validation == 'CV') {
        niter = nrow(y)
    }
    

    # We should do this since when both grpsize and niter are specified niter
    # is used. So if grpsize comes in as 0 (which will be the default setting)
    # we specify only niter and if not zero we use grpsize and ignore niter
    if (grpsize != 0) {
        assign(modelname,
        pls(x=x,y=y,ncomp=ncomp,method=method,validation=validation,grpsize=grpsize),
        pos=1)
    } else {
        assign(modelname,
        pls(x=x,y=y,ncomp=ncomp,method=method,validation=validation,niter=niter),
        pos=1)
    }
    detach(paramlist)
    get(modelname)
}
predictPLS <- function(modelname, params) {
    paramlist <- hashmap.to.list(params)
    attach(paramlist)
    
    newX <- matrix(unlist(newX), nrow=length(x), byrow=TRUE)
    model <- get(modelname)
    if (ncol(newX) != model$nvar) {
        stop('The number of independent variables in the new data does not match that specified during building')
    }
    if (nlv == FALSE) {
        preds <- predict(model, newX)
    } else {
        preds <- predict(model, newX, nlv)
    }
    class(preds) <- 'plsregressionprediction'
    preds
}
