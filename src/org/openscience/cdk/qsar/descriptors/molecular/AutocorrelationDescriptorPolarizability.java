package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.graph.matrix.TopologicalMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.exception.CDKException;
import java.util.*;
import java.lang.Object;

public class AutocorrelationDescriptorPolarizability implements IMolecularDescriptor{
	
	private static List listpolarizability (IAtomContainer container)throws CDKException{
		int natom = container.getAtomCount();
		List polars = new ArrayList();
		
		for(int i = 0; i < natom; i++){
			IAtom atom = container.getAtom(i);
			try{
				Polarizability polar = new Polarizability();
				polars.add(new Double(polar.calculateGHEffectiveAtomPolarizability(container, atom, 100)));
			}catch(Exception ex1) {
				throw new CDKException("Problems with assign Polarizability due to " + ex1.toString(), ex1);
			}
		}
		
		return polars;
	}
	
	public DescriptorValue calculate(IAtomContainer container) throws CDKException{
		try{		
			List list = listpolarizability(container);
			List list1 = listpolarizability(container);
			
			int natom = container.getAtomCount();
	
			int[][] distancematrix = TopologicalMatrix.getMatrix(container);
	
			double[] PolarizabilitySum = new double[5];
			
	
			for (int k = 0; k < 5; k++) {
				for (int i = 0; i < natom; i++) {
					for (int j = 0; j < natom; j++) {
						
						if (distancematrix[i][j] == k){
							double num = ((Double)list.get(i)).doubleValue();
							double num2 = ((Double)list1.get(j)).doubleValue();
							PolarizabilitySum[k] += 1 * (num * num2);
						}
					}
				}
				if (k > 0)
					PolarizabilitySum[k] = PolarizabilitySum[k] / 2;
				
			}
			DoubleArrayResult result=new DoubleArrayResult(5);
			for(int i=0;i<PolarizabilitySum.length;i++){
				result.add(PolarizabilitySum[i]);
				
			}

            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
	                result, new String[] {"ATSp1", "ATSp2", "ATSp3", "ATSp4" ,"ATSp5"});
			
		}catch(Exception ex){
			throw new CDKException("Error while calculating the ATS_polarizability descriptor: " + ex.getMessage(), ex);
		}
}

	public String[] getParameterNames() {
		return null;
	}

	public Object getParameterType(String name) {
		return null;
	}

	public Object[] getParameters() {
		return null;
	}

	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#atomCount",
                this.getClass().getName(),
                "$Id: AtomCountDescriptor.java $",
                "The Chemistry Development Kit");
	}
	
	public IDescriptorResult getDescriptorResultType() {
        return new DoubleResult(0.0);
    }

	public void setParameters(Object[] params) throws CDKException {
		
		}
	
}