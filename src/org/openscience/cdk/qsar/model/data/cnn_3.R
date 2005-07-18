#############################################
# CNN regression fit/predict converters
#############################################
cnnSummaryConverter <- 
function(obj,...)
{
    .JNew('org.openscience.cdk.qsar.model.R.CNNRegressionModelSummary',
    obj$n, obj$entropy, obj$softmax, obj$censored, obj$value, obj$residuals)
}

cnnFitConverter <-
function(obj,...) 
{
    noutput <- ncol(obj$fitted)
    nobs <- nrow(obj$fitted)
    if ('Hessian' %in% names(obj)) {
        .JNew('org.openscience.cdk.qsar.model.R.CNNRegressionModelFit',
        noutput,nobs, obj$wts, obj$fitted, obj$residuals, obj$value, obj$Hessian)
    } else {
        .JNew('org.openscience.cdk.qsar.model.R.CNNRegressionModelFit',
        noutput, nobs,obj$wts, obj$fitted, obj$residuals, obj$value)
    }
}
cnnPredictConverter <-
function(obj,...) {
    # The obj we get is actually a 'matrix' but we set its class
    # to cnnregprediction so that SJava would send it specifically
    # to us. So we should convert obj back to class 'matrix' so 
    # that SJava can send it correctly to the Java side
    class(obj) <- 'matrix'
    .JNew('org.openscience.cdk.qsar.model.R.CNNRegressionModelPredict',
    ncol(obj), obj)
}
buildCNN <-  function(modelname, params) {
    library(nnet)
    paramlist <- hashmap.to.list(params)
    attach(paramlist)

    x <- matrix(unlist(x), nrow=length(x), byrow=TRUE)
    y <- matrix(unlist(y), nrow=length(y), byrow=TRUE)
    if (nrow(x) != nrow(y)) { 
        stop('The number of observations in x & y dont match') 
    }

    ninput <- ncol(x)
    nhidden <- size
    noutput <- ncol(y)
    nwt <- (ninput*nhidden) + (nhidden*noutput) + nhidden + noutput
    
    if (class(weights) == 'logical' && !weights) weights <- rep(1, nrow(y))
    if (class(subset) == 'logical' && !subset) subset <- 1:nrow(y)
    if (class(Wts) == 'logical' && !Wts) { Wts <- runif(nwt) }
    if (class(mask) == 'logical' && !mask) { mask <- rep(TRUE, nwt) }

    assign(modelname, 
    nnet(x,y,weights=weights,size=size,Wts=Wts,mask=mask,linout=linout,
    entropy=entropy,softmax=softmax,censored=censored,skip=skip,rang=rang,
    decay=decay,maxit=maxit,Hess=Hess,trace=trace,MaxNWts=MaxNWts,
    abstol=abstol,reltol=reltol), pos=1)

    detach(paramlist)
    get(modelname)
}
predictCNN <- function(modelname, params) {
    # Since buildCNN should have been called before this
    # we dont bother loading the nnet library
    paramlist <- hashmap.to.list(params)
    attach(paramlist)

    newx <- data.frame( matrix(unlist(newdata), nrow=length(newdata), byrow=TRUE) )
    names(newx) <- get(modelname)$coefnames
    if (type == '' || !(type %in% c('raw','class')) ) { 
        type = 'raw'
    } 


    preds <- predict( get(modelname), newdata=newx, type=type);
    class(preds) <- 'cnnregprediction'

    detach(paramlist)
    preds
}

