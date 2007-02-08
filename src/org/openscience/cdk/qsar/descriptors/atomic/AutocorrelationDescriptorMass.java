package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.graph.matrix.TopologicalMatrix;
import java.util.ArrayList;

/**
 * This class calculates ATS autocorrelation descriptor, where the weight equal
 * to the scaled atomic mass. <p/>
 * <p>
 * This descriptor uses these parameters: <table border="1">
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * </table>
 * 
 * @author Federico
 * @cdk.created 2007-02-08
 * @cdk.module qsar
 * @cdk.set qsar-descriptors
 */

public class AutocorrelationDescriptorMass {

	/**
	 * This method gets the values of scaled atomic masses.
	 * 
	 * @param element
	 * @return scaled atomic masses in double format.
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	public static double ScaledAtomicMasses(IElement element)
			throws java.io.IOException, ClassNotFoundException {

		double realmasses = MFAnalyser.getNaturalMass(element);
		double scaled = (realmasses / 12.010735896788);

		return scaled;
	}

	/**
	 * This method gets an array with the values of scaled atomic masses.
	 * 
	 * @param container
	 * @return scaled atomic masses in array format.
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	public static ArrayList ListFormat(IAtomContainer container)
			throws java.io.IOException, ClassNotFoundException {
		int natom = container.getAtomCount();
		int i = 0;

		ArrayList<Double> scalated = new ArrayList<Double>();

		for (i = 0; i < natom; i++) {
			scalated.add(ScaledAtomicMasses(container.getAtom(i)));
		}
		return scalated;
	}

	/**
	 * This method gets the values of the autocorrelation descriptor in array
	 * format.
	 * The calculation is for k values between 0 and 4.
	 * 
	 * @param container
	 * @return values of the autocorrelation descriptor in array format.
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	public static double[] AutocorrelationMass(IAtomContainer container)
			throws java.io.IOException, ClassNotFoundException {
		int natom = container.getAtomCount();

		int[][] distancematrix = TopologicalMatrix.getMatrix(container);

		int k = 0;
		int i = 0;
		int j = 0;
		double[] massSum = new double[5];

		ArrayList<Double> list1 = ListFormat(container);
		ArrayList<Double> list2 = ListFormat(container);

		for (k = 0; k < 5; k++) {
			for (i = 0; i < natom; i++) {
				for (j = 0; j < natom; j++) {

					if (distancematrix[i][j] == k)
						massSum[k] += 1 * (list1.get(i) * list2.get(j));

				}
			}
			if (k > 0)
				massSum[k] = massSum[k] / 2;
		}
		return massSum;
	}

}
