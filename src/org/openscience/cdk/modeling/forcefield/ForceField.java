package org.openscience.cdk.modeling.forcefield;

import java.io.*;

import java.util.*;
import javax.vecmath.*;

//import org.openscience.cdk.modeling.forcefield.CdkJmol3DPanel;

import org.openscience.cdk.modeling.builder3d.ModelBuilder3D;
import org.openscience.cdk.modeling.forcefield.*;
import org.openscience.cdk.*;

import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.graph.ConnectivityChecker;

/**
 *  To work with the coordinates of the molecule, like get the 3d coordinates of the atoms or 
 *  calculate the distance between two atoms.
 *@author     vlabarta
 *
 */
public class ForceField extends GeometricMinimizer{
	

	private String potentialFunction="mmff94";
	ForceFieldTools ffTools = new ForceFieldTools();
	
	/**
	 *  Constructor for the ForceField object
	 */
	public ForceField() {
	}
	
	public ForceField(Molecule molecule) {
		setMolecule(molecule, false);
		
	}
	
	public void setPotentialFunction(String potentialName){
		potentialFunction=potentialName;
	}
	
		
	public void minimize( ) throws Exception{
		ConnectivityChecker cc = new ConnectivityChecker();
		if (!cc.isConnected(molecule)) {
			throw new Exception("CDKError: Molecule is NOT connected,could not layout.");
		}
		GVector moleculeCoords = new GVector(3);
		MMFF94EnergyFunction mmff94PF=null;
		if (potentialFunction=="mmff94"){
		    System.out.println("SET POTENTIAL FUNCTION TO MMFF94");
		    setMMFF94Tables(molecule);
		    mmff94PF=new MMFF94EnergyFunction((AtomContainer)molecule,getPotentialParameterSet());
		}
		moleculeCoords.setSize(molecule.getAtomCount() * 3);
		moleculeCoords.set(ffTools.getCoordinates3xNVector((AtomContainer)molecule));
		
		System.out.println("PotentialFunction set:"+potentialFunction+"MoleculeCoords set:"+moleculeCoords.getSize()+" Hashtable:"+getPotentialParameterSet().size());
		
		
		//steepestDescentsMinimization(moleculeCoords,mmff94PF);

		conjugateGradientMinimization(moleculeCoords, mmff94PF);
		//conjugateGradientMinimization(moleculeCoords, tpf);
	

		System.out.println("Minimization READY");
		//ffTools.assignCoordinatesToMolecule(moleculeCoords, (AtomContainer) molecule); 
	}
			



}


