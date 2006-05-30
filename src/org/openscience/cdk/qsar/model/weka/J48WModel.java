package org.openscience.cdk.qsar.model.weka;

import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;

import weka.classifiers.trees.J48;
/** 
 * A modeling class that provides the Quinlan's model C4.5 known as J48 
 * using Weka library.
 * The use of this class is shown in the following code snippet
 * <pre>
 * try {
 *     J48WModel j48 = new J48WModel(typAttrib,classAttrib,x,y);
 *     j48.setOptions(options);
 *     j48.build();
 *     j48.setParameters(newX);
 *     j48.predict();
 * 		
 * 		double[] predictedvalues = j48.getPredictPredicted();
 * 
 * } catch (QSARModelException qme) {
 *     System.out.println(qme.toString());
 * }
 * </pre>
 *
 * @author Miguel Rojas
 * @cdk.require weka.jar
 * @cdk.module qsar
 * @see Weka
 */
public class J48WModel extends IWekaModel{
	/**Dependent variable */
	private Object[] y;
	/**Independent variable */
	private Object[][] x;
	private Weka weka = null;
	/**Array of strings containing the options*/
	private String[] options;
	/**A String specifying the path of the file, format arff,
	 * which contians the variables and attributes with whose to test.*/
	private String pathTest = null;
	/** results of the prediction*/
	private double[] results;
	/**A Array Object containing the independent variable*/
	private Object[][] newX = null;
	/**A String specifying the path of the file, format arff,
	 * which contians the independent values with whose to predict.*/
	private String pathNewX = null;
	/**Attribute type: NUMERICAL or NOMINAL*/
	private int[] typAttrib;
	/**String with the attribut class*/
	private String[] classAttrib;

	/**
	 * Constructor of the J48WModel object from varibles
	 * 
     * @param typAttrib   Attribute type: NUMERICAL or NOMINAL.
     * @param classAttrib String with the attribut class.
	 * @param y  An array containing the dependent variable.
	 * @param x  An double array containing the independent variable.
	 */
	public J48WModel(int[] typAttrib, String[] classAttrib, Object[] y, Object[][] x){
		this.typAttrib = typAttrib;
		this.classAttrib = classAttrib;
		this.y = y;
		this.x = x;
	}
	/**
	 * Constructor of the J48WModel object from varibles
	 * @param pathTest Path of the dataset file format arff to train
	 */
	public J48WModel(String pathTest){
		this.pathTest  = pathTest;
	}

	/**
     * Parses a given list of options. The parameters are determited from weka. And are specific for each
     * algorithm.
     *
     * @param options An Array of strings containing the options 
     * @throws QSARModelException if the options are of the wrong type for the given modeling function
     * 
     */
	public void setOptions(String[] options) throws QSARModelException {
		this.options = options;
	}
	/**
     * Get the current settings of the classifier. The parameters are determited from weka. And are specific for each
     * algorithm.
     *
     * @return An Array of strings containing the options 
     * @throws QSARModelException if the options are of the wrong type for the given modeling function
     * 
     */
    public String[] getOptions() throws QSARModelException {
		return options;
	}
	/**
     * Builds (trains) the model.
     *
     * @throws QSARModelException if errors occur in data types, calls to the R session. See
     * the corresponding method in subclasses of this class for further details.
     */
	public void build() throws QSARModelException {
		weka = new Weka();
		try {
			J48 j48 = new J48();
			if(options != null)
				j48.setOptions(options);
			
			if(pathTest != null){
				weka.setDataset(pathTest, j48);
			}else{
				String[] attrib = new String[x[0].length];
				for(int i = 0 ; i < x[0].length; i++){
					attrib[i] = "X"+i;
				}
				weka.setDataset(attrib,typAttrib,classAttrib,y,x,j48);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * Specifies the parameters to predict. In this case will be the dependent varibles.
     * 
     * @param  path  A String specifying the path of the file, format arff, which contians 
     * 				 the dependent values with whose to predict.
     * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
     * 
     */
    public void setParameters(String path) throws QSARModelException {
    	this.pathNewX = path;
	}
	/**
     * Specifies the parameters to predict. In this case will be the independent varibles.
     * 
     * @param  newX  A Array Object containing the independent variable.
     * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
     */
    public void setParameters(Object[][] newX) throws QSARModelException {
    	this.newX = newX;
    }
    
    /**
     * Makes predictions using a previously built model.
     *
     * @throws QSARModelException if errors occur in data types, calls to the R session. See
     * the corresponding method in subclasses of this class for further details.
     */
	public void predict() throws QSARModelException {
		try{
			if(pathNewX != null)
				results = weka.getPrediction(pathNewX);
			else if(newX != null)
				results = weka.getPrediction(newX);
			
		} catch ( Exception e){
			e.printStackTrace();
		}
	}
	/**
     * Returns the predicted values for the prediction set. 
     *
     * This function only returns meaningful results if the <code>predict</code>
     * method of this class has been called.
     *
     * @return A double[] containing the predicted values
     */
	public double[] getPredictPredicted() {
		return results;
	}


}
