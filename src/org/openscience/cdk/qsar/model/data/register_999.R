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

