#############################################
# CNN classification fit/predict converters
#############################################
cnnClassFitConverter <-
function(obj,...) 
{
    noutput <- ncol(obj$fitted)
    nobs <- nrow(obj$fitted)
    if ('Hessian' %in% names(obj)) {
        .JNew('org.openscience.cdk.qsar.model.R.CNNClassificationModelFit',
        noutput,nobs, obj$wts, obj$fitted, obj$residuals, obj$value, obj$Hessian)
    } else {
        .JNew('org.openscience.cdk.qsar.model.R.CNNClassificationModelFit',
        noutput, nobs,obj$wts, obj$fitted, obj$residuals, obj$value)
    }
}
cnnClassPredictConverter <-
function(obj,...) {
    # The obj we get is actually a 'matrix' but we set its class
    # to cnnclsprediction so that SJava would send it specifically
    # to us. So we should convert obj back to class 'matrix' so 
    # that SJava can send it correctly to the Java side
    if (class(obj[1]) == 'numeric') {
        class(obj) <- 'matrix'
        .JNew('org.openscience.cdk.qsar.model.R.CNNClassificationModelPredict',
        ncol(obj), obj)
    } else if (class(obj[1]) == 'character') {
        class(obj) <- 'character'
        .JNew('org.openscience.cdk.qsar.model.R.CNNClassificationModelPredict', obj)
    }
}
buildCNNClass <- function(modelname, params) {
    library(nnet)
    paramlist <- hashmap.to.list(params)
    attach(paramlist)

    x <- matrix(unlist(x), nrow=length(x), byrow=TRUE)
    y <- factor(unlist(y)) # y will come in as a single vector
    if (nrow(x) != length(y)) { stop('The number of observations in x & y dont match') }

    ninput <- ncol(x)
    nhidden <- size
    if (length(levels(y)) == 2) noutput <- 1
    else noutput = length(levels(y))

    nwt <- (ninput*nhidden) + (nhidden*noutput) + nhidden + noutput
    if (class(weights) == 'logical' && !weights) weights <- rep(1, length(y))
    if (class(subset) == 'logical' && !subset) subset <- 1:length(y)
    if (class(Wts) == 'logical' && !Wts) { Wts <- runif(nwt) }
    if (class(mask) == 'logical' && !mask) { mask <- rep(TRUE, nwt) }
    

    assign(modelname, 
    nnet(y~., data=data.frame(y=y,x=x),weights=weights,size=size,Wts=Wts,mask=mask,linout=linout,
    softmax=softmax,censored=censored,skip=skip,rang=rang,
    decay=decay,maxit=maxit,Hess=Hess,trace=trace,MaxNWts=MaxNWts,
    abstol=abstol,reltol=reltol), pos=1)

    detach(paramlist)
    get(modelname)
}

predictCNNClass <- function(modelname, params) {
    # Since buildCNNClass should have been called before this
    # we dont bother loading the nnet library
    paramlist <- hashmap.to.list(params)
    attach(paramlist)

    newx <- data.frame( y=1, x=matrix(unlist(newdata), nrow=length(newdata), byrow=TRUE) )
    names(newx) <- get(modelname)$coefnames
    if (type == '' || !(type %in% c('raw','class')) ) { 
        type = 'raw'
    } 

    preds <- predict( get(modelname), newdata=newx, type=type);
    class(preds) <- 'cnnclsprediction'
    detach(paramlist)
    preds
}

