
#
#  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
#  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.


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
    print('Starting JVM')
    .JavaInit()
}

lmFitConverter <-
function(obj,...)
{
    .JNew("org.openscience.cdk.qsar.model.R.LinearRegressionModelFit",
    obj$coefficients, obj$residuals,
    obj$fitted, obj$rank, obj$df.residual)
}
lmPredictConverter <- function(preds,...) {
    .JNew("org.openscience.cdk.qsar.model.R.LinearRegressionModelPredict",
    preds$fit[,1], preds$se.fit, preds$fit[,2], preds$fit[,3],
    preds$df, preds$residual.scale)
}
setJavaFunctionConverter(lmFitConverter, function(x,...){inherits(x,"lm")},
                          description="lm fit object to Java",
                          fromJava=F)
setJavaFunctionConverter(lmPredictConverter, function(x,...){inherits(x,"lmprediction")},
                          description="lm predict object to Java",
                          fromJava=F)
                          
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
    if (interval == '' || !(interval %in% c('confidence','prediction')) ) { 
        interval = 'confidence'
    } 
    preds <- predict( get(modelname), newx, se.fit = TRUE, interval=interval);
    class(preds) <- 'lmprediction'

    detach(paramlist)
    preds
}

