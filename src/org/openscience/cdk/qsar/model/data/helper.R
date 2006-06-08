#
#  Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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


# load some common packages that will always be installed

library(MASS)
library(nnet)

# some helper functions
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
