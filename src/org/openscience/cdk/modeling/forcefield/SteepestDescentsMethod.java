package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  Steepest Descents Method for optimisation
 *
 *@author     LabartaV
 *@created    2004-12-03
 */
public class SteepestDescentsMethod {
	double stepSize =2;	//	arbitrary step size
	int stepK = 0;	//	Step number
	int dimension=3;	//	3 * Atoms number
	//GVector pointK = new GVector(3);	//	r coordinates at K step
	GVector pointK = null;
	//GVector sK = new GVector(3);
	GVector sK = null;
	//TestPotentialFunction functionToOptimise = new TestPotentialFunction();	//	To be changed
	
	public double getStepSize(){
		return stepSize;
	}
	
	public void setStepSize(double StepSize){
		stepSize=StepSize;
	}
/**
	 *  Constructor for the SteepestDescentsMethod object
	 */
	public SteepestDescentsMethod(GVector point) { 
	dimension = point.getSize();
	pointK.setSize(point.getSize());
	pointK.set(point);
	sK.setSize(point.getSize());
	
	//functionToOptimise=workFunction;
	}


	/**
	 *  	Method that take minimum point like new starting point
	 *
	 *@param  oldStartingPoint  Description of the Parameter
	 *@return                   Description of the Return Value
	 */
	public GVector newCoordinates(GVector oldPoint) {
		pointK.set(sK);
		//System.out.println("pointK = " + pointK);
		pointK.scale(stepSize);
		//System.out.println("pointK = " + pointK);
		pointK.add(oldPoint);
		System.out.println("pointK = " + pointK);
		stepK = stepK + 1;	//stepK =+ 1
		return pointK;	//	maybe is better return nothing
	}

	public void vectorSkCalculation(GVector gK){	//	sK=-gK/|gk|;
		System.out.println("Start sK calculation with gK = " + gK);
		sK=gK;
		sK.normalize();	//	Normalizes this vector in place.
		sK.scale(-1);
		System.out.println("vectorSk : " + sK);
		return;
	}	
	
		//double absoluteValueOfGk = 0;
		//for (int i=0;i<dimension;i++){
		//	absoluteValueOfGk = absoluteValueOfGk + Math.pow(gK.getElement(i), 2);	
			//	Math.pow(gK.getElement(i), 2)	Returns of value of the first argument raised to the power of the second argument
		//}
		
		//absoluteValueOfGk = Math.sqrt(absoluteValueOfGk);	//	Returns the correctly rounded positive square root of a double value.
		//System.out.println("My calculation of the absolute value : " + absoluteValueOfGk);
				
		//absoluteValueOfGk = sK.norm();	//	Returns the square root of the sum of the squares of this vector (its length in n-dimensional space).
		//System.out.println("Java calculation of the absolute value : " + absoluteValueOfGk);
		
		//sK.scale(-1/absoluteValueOfGk);
		//System.out.println("My calculation of sK : " + sK);

		//sK.set(gK);
		//	sK.scale(-1);

	/**
	 *  	 Obtain line of search (Interception xk and slope like Sk)
	 *
	 *@param  interception          xk
	 *@param  interceptionGradient  Sk
	 *@return                       Description of the Return Value
	 */
	public String searchLine(Vector interception, Vector interceptionGradient) {
		String lineEquation = "";
		return lineEquation;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  line  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	public String linePointsSeeking(String line) {
		// Look for 3 points along the line where the energy of the middle point
		String lineSegment = "";
		// is lower than the energy of the two outer points
		return lineSegment;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  function  Description of the Parameter
	 *@param  segment   Description of the Parameter
	 *@return           Description of the Return Value
	 */
	public Vector minimizeFitFunction(String function, String segment) {
		// Minimize the fitted function
		Vector fitFunctionMinimum = new Vector();
		return fitFunctionMinimum;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  point1  Description of the Parameter
	 *@param  point2  Description of the Parameter
	 *@param  point3  Description of the Parameter
	 *@param  order   Description of the Parameter
	 *@return         Description of the Return Value
	 */
	public String fitFunction(Vector point1, Vector point2, Vector point3, byte order) {
		// Fit a function of order p to three point
		String fitFunctionShape = "";
		return fitFunctionShape;
	}


	// Methods usefuls for Arbitrary Step method

	/**
	 *  xk+1= Xk + Lambdak Sk
	 *
	 *@param  oldStartingPoint  Description of the Parameter
	 *@param  stepSize          Description of the Parameter
	 *@return                   Description of the Return Value
	 */
	public Vector arbitraryStep(Vector oldStartingPoint, byte stepSize) {
		Vector arbitraryStep = new Vector();
		return arbitraryStep;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  kPoint       Description of the Parameter
	 *@param  kplus1point  Description of the Parameter
	 *@return              Description of the Return Value
	 */
	public boolean compareSteps(Vector kPoint, Vector kplus1point) {
		// Compare f(Xk) with f(Xk+1)
		boolean decreasingEnergyOr = false;
		return decreasingEnergyOr;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  oldStepSize       Description of the Parameter
	 *@param  decreasingEnergy  Description of the Parameter
	 *@return                   Description of the Return Value
	 */
	public byte newStepSize(byte oldStepSize, boolean decreasingEnergy) {
		// Analize how will be the shape of the next step size
		byte stepSize = oldStepSize;
		return stepSize;
	}
}

/*
 *  public void steepestDescentsMinimizer ()  {
 *  System.out.println("In steepestDescentMinimizer");
 *  int atomsNumber=0;
 *  int CoordinatesNumber=0;
 *  /System.out.println("Number of coordinates (2 or 3): ");
 *  /System.in.readln(CoordinatesNumber);
 *  if (atomsNumber == 0) {
 *  /try {
 *  /	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
 *  / char[] atomsNumberRead=new char[100];
 *  String atomsNumberString="";
 *  while (atomsNumber == 0) {
 *  System.out.print("> Number of atoms: ");
 *  /in.read(atomsNumberRead,0,atomsNumberRead.length);
 *  /		atomsNumberString=in.readLine();
 *  System.out.println("I read:" +atomsNumberString);
 *  /atomsNumber=Integer.valueOf(atomsNumberString);
 *  /atomsNumber=Integer.valueOf(atomsNumberRead);
 *  /System.out.println("atomsNumberRead[0]=" + atomsNumberRead[0] + ", atomsNumberRead[1]=" + atomsNumberRead[1] + ", atomsNumberRead[2]=" + atomsNumberRead[2]);
 *  /System.out.println(">Ok, " + atomsNumber + " atoms");
 *  /System.out.println(atomsNumber);
 *  atomsNumber=1;
 *  }
 *  /} catch (IOException e) {
 *  /}
 *  }
 *  /String piStr = "3.14159";
 *  /Float pi = Float.valueOf(piStr);
 *  System.out.println("> OK, " + atomsNumber + " atoms");
 *  System.out.println("> OK, " + CoordinatesNumber + " coordinates");
 *  /int dimension=atomsNumber*CoordinatesNumber;
 *  /float[] startingPoint=new float[(dimension)];
 *  /System.out.println("> The dimension is : " + dimension);
 *  System.out.println("> Let see you");
 *  /AtomsNumber = DataInputStream.readByte();
 *  System.out.println("Please write the starting point: "); // Read starting point
 *  /for (int i=0;i<dimension;i++);{
 *  /	for (int j=1;j<=atomsNumber;j++);{
 *  /		System.out.println("x1 : ");
 *  /startingPoint[i]=System.in.readln();
 *  /	}
 *  /}
 *  / Read file of molecule parameters
 *  boolean convergence=false;
 *  do {
 *  / Evaluate gradient in starting point
 *  / Build line
 *  / Minimize surface in the line
 *  / Starpoint= Minimum
 *  / Checking convergence
 *  } while (convergence!=true); //Checking of convergence
 *  return;
 *  }
 */

