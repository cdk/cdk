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

####################################################
#
# Jama Matrix
#
####################################################
jamaMatrixConverter <- function(x, klassName) {
    vals <- unlist(x$getArray())
    matrix(vals, x$getRowDimension(), x$getColumnDimension());
}
jamaMatrixMatch <- function(x, klassName) {
    klassName == 'Jama.Matrix';
}

####################################################
#
# Jama Eigenvalue decomposition
#
####################################################
jamaEDConverter <- function(x, klassName) {
    re <- x$getRealEigenvalues()
    re
}
jamaEDMatch <- function(x, klassName) {
    klassName == 'Jama.EigenvalueDecomposition'
}


#####################################
# User accessible functions
#####################################
printMatrix <- function(m) {
    print(eigen(m)$values)
}

printEigenvalues <- function(ed) {
    print(ed)
}

printJavadoubleArray <- function(doubleArray) {
    print(doubleArray)
}
printJavadoubleArray2D <- function(doubleArray) {
    m <- matrix(unlist(doubleArray), ncol=length(doubleArray))
    print(m)
}

setJavaFunctionConverter(jamaMatrixConverter, jamaMatrixMatch, description="Jama.Matrix to R matrix", fromJava=T);
setJavaFunctionConverter(jamaEDConverter, jamaEDMatch, description="Jama.EigenvalueDecompositin EV's to R vector", fromJava=T);
#setJavaFunctionConverter(javaArrayRefConverter,javaArrayRefMatch , description="Jama.EigenvalueDecompositin EV's to R vector", fromJava=T);

