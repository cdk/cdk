#
#  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
#
#  Contact: cdk-devel@lists.sourceforge.net
#
#  This program is free software; you can redistribute it and/or
#  modify it under the terms of the GNU Lesser General Public License
#  as published by the Free Software Foundation; either version 2.1
#  of the License, or (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU Lesser General Public License for more details.
#
#  You should have received a copy of the GNU Lesser General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.


# Basically the idea is to be able to pass an arbitrary Java object
# to an R session. For this to work, the object should be converted to
# a valid R object within the R session.
#
# How does R know how to convert a Java object it recieves? This is done
# by a matcher function. This looks at the class name of the object and if
# it matches the class name in the matcher function, the converter is called
#
# The converter then accesses any methods for the Java object or uses the methods
# provided by SJava to extract information from the Java object to create an R 
# object.
# 
# After implementing matcher and converter functions they should be registered
# with SJava using setJavaFunctionConverter()
#
# So the flow when calling an R function *from* a Java program and passing
# an arbitrary Java object is:
#
# 1. The R function recieves the Java object
# 2. Runs it through the matcher functions SJava knows about
# 3. If a matcher function returns TRUE the corresponding converter function
#    is called. The return value if an R object (vector, data.frame, list etc)
# 4. The function then works with the object as usual 
# 5. If no matcher was found in (2) then the R function will see the object
#    as an AnonymousOmegahatReference
#
# If the R function that was called from the Java session returns the recieved
# object then Java will see it as a R object. So if the converter for a Java
# vector turns it into a numeric() and returns it Java will get the object back
# as a double[] which can be printed by ROmegahatInterpreter.show()
#
#
#
# Passing an arbitrary R object back to Java is done similarly. In this case
# the converter function will call some Java function that creaates a 
# AnonymousOmegahatReference (or named) from the R object (possibly by
# calling methods of the class). The matcher function uses the inherits function 
# in R to determine whether the R object is of the proper class. So in this case
# the flow is :
#
# 1. Java calls a R function which does some calculation and returns an R object
# 2. SJava looks for a matcher that matches the R class of the return value
#    and calls the corresponding converter function with the R object
# 3. The converter will generally return a Java object containing the information
#    from the R object. 
#
# For primitives such as vectors, this process is not required. But if we want
# to return say a lm or nnet object we would create a Java class that contains
# setter and getter methods. The R converter would create a new instance of this
# wrapping class and set the fields with the values from the R object and return this
# Java object which will then be passed back to the Java calling program


require(SJava)
if (!isJavaInitialized()) {
    .JavaInit()
}
library(nnet)
#library(pls.pcr)

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

#############################################
# Linear regression fit/predict converters
#############################################
lmFitConverter <-
function(obj,...)
{
    .JNew('org.openscience.cdk.qsar.model.R.LinearRegressionModelFit',
    obj$coefficients, obj$residuals,
    obj$fitted, obj$rank, obj$df.residual)
}
lmPredictConverter <- function(preds,...) {
    .JNew('org.openscience.cdk.qsar.model.R.LinearRegressionModelPredict',
    preds$fit[,1], preds$se.fit, preds$fit[,2], preds$fit[,3],
    preds$df, preds$residual.scale)
}
lmSummaryConverter <- function(sumry,...) {
    .JNew('org.openscience.cdk.qsar.model.R.LinearRegressionModelSummary',
    sumry$residuals, sumry$coeff,
    sumry$sigma, sumry$r.squared, sumry$adj.r.squared,
    sumry$df[2], sumry$fstatistic,
    attr(sumry$coeff, 'dimnames')[[1]],
    attr(sumry$coeff, 'dimnames')[[2]])
}

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

#############################################
# Register the fit/predict converter funcs
#############################################
setJavaFunctionConverter(lmFitConverter, function(x,...){inherits(x,'lm')},
                          description='lm fit object to Java',
                          fromJava=F)
setJavaFunctionConverter(lmPredictConverter, function(x,...){inherits(x,'lmregprediction')},
                          description='lm predict object to Java',
                          fromJava=F)
setJavaFunctionConverter(lmSummaryConverter, function(x,...){inherits(x,'summary.lm')},
                          description='lm summary object to Java',
                          fromJava=F)
setJavaFunctionConverter(cnnClassFitConverter, function(x,...){inherits(x,'nnet.formula')},
                          description='cnn (nnet) classification fit object to Java',
                          fromJava=F)
setJavaFunctionConverter(cnnSummaryConverter, function(x,...){inherits(x,'summary.nnet')},
                          description='cnn (nnet) summary object to Java',
                          fromJava=F)
setJavaFunctionConverter(cnnFitConverter, function(x,...){inherits(x,'nnet')},
                          description='cnn (nnet) fit object to Java',
                          fromJava=F)
setJavaFunctionConverter(cnnClassPredictConverter, function(x,...){inherits(x,'cnnclsprediction')},
                          description='cnn (nnet) classification predict object to Java',
                          fromJava=F)
setJavaFunctionConverter(cnnPredictConverter, function(x,...){inherits(x,'cnnregprediction')},
                          description='cnn (nnet) predict object to Java',
                          fromJava=F)
setJavaFunctionConverter(plsFitConverter, function(x,...){inherits(x,'mvr')},
                          description='pls/pcr fit object to Java',
                          fromJava=F)
setJavaFunctionConverter(plsPredictConverter, function(x,...){inherits(x,'plsregressionprediction')},
                          description='pls/pcr predict object to Java',
                          fromJava=F)
                          
buildLM <- function(modelname, params) {
    # params is a java.util.HashMap containing the parameters
    # we need to extract them and add them to this environment
    paramlist <- hashmap.to.list(params)
    attach(paramlist)

    # x will come in as a double[][]
    x <- matrix(unlist(x), nrow=length(x), byrow=TRUE)

    # assumes y ~ all columns of x
    d <- data.frame(y=y,x)
    assign(modelname, lm(y~., d, weights=weights), pos=1)
    detach(paramlist)
    get(modelname)
}

predictLM <- function( modelname, params) {
    # params is a java.util.HashMap containing the parameters
    # we need to extract them and add them to this environment
    paramlist <- hashmap.to.list(params)
    attach(paramlist)

    newx <- data.frame( matrix(unlist(newdata), nrow=length(newdata), byrow=TRUE) )
    names(newx) <- names(get(modelname)$coef)[-1]
    if (interval == '' || !(interval %in% c('confidence','prediction')) ) { 
        interval = 'confidence'
    } 
    preds <- predict( get(modelname), newx, se.fit = TRUE, interval=interval);
    class(preds) <- 'lmregprediction'

    detach(paramlist)
    preds
}

buildCNN <-  function(modelname, params) {
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


buildCNNClass <- function(modelname, params) {
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
predictCNNClass <- function(modelname, params) {
    # Since buildCNNClass should have been called before this
    # we dont bother loading the nnet library
    paramlist <- hashmap.to.list(params)
    attach(paramlist)

    newx <- data.frame( matrix(unlist(newdata), nrow=length(newdata), byrow=TRUE) )
    names(newx) <- get(modelname)$coefnames
    if (type == '' || !(type %in% c('raw','class')) ) { 
        type = 'raw'
    } 

    preds <- predict( get(modelname), newdata=newx, type=type);
    class(preds) <- 'cnnclsprediction'
    detach(paramlist)
    preds
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
