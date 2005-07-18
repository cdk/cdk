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


