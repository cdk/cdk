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
	
	private Molecule molecule;
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
	

	public void setMolecule(Molecule mol, boolean clone) {

		if (clone) {
			this.molecule = (Molecule) mol.clone();
		} else {
			this.molecule = mol;
		}
	}

		
	public void minimize( ) throws Exception{
		ConnectivityChecker cc = new ConnectivityChecker();
		if (!cc.isConnected(molecule)) {
			throw new Exception("CDKError: Molecule is NOT connected,could not layout.");
		}
		GVector moleculeCoords = new GVector(3);
		
		if (potentialFunction=="mmff94"){
			setMMFF94Tables(molecule);
		}
		moleculeCoords.setSize(molecule.getAtomCount() * 3);
		moleculeCoords.set(ffTools.getCoordinates3xNVector((AtomContainer)molecule));
		
	
		
		//steepestDescentsMinimization(molecule3Coordinates,);
		//conjugateGradientMinimization(molecule3Coordinates, tpf);
		//conjugateGradientMinimization(molecule3Coordinates, tpf);
		ffTools.assignCoordinatesToMolecule(moleculeCoords, (AtomContainer) molecule);
	}
			

	public Molecule getMolecule() {
		return this.molecule;
	}

}


