/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.protein;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.Point3d;

import org.openscience.cdk.tools.GridGenerator;
import org.openscience.cdk.PDBAtom;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;

/**
 * The detection of pocket and cavities in a bioPolymer is done similar to the program 
 * LIGSITE {@cdk.cite MH1997}.
 * 
 * <p>TODO: Optimisation of the cubic grid placement 
 *
 * @author      cho
 * @cdk.created 2005-09-30
 * @cdk.module     experimental
 * @cdk.keyword    protein
 * @cdk.keyword    pocket
 */
public class ProteinPocketFinder {

	int solvantValue = 0;
	int proteinInterior = -1;
	int pocketSize = 100;// # datapoints needed to form a pocket
	double rAtom = 1.5;// default atom radius
	double rSolvent = 1.4;// default solvant radius
	double latticeConstant = 0.5;
	int minPSPocket = 2;
	int minPSCluster = 2;
	double linkageRadius = 1;
	double atomCheckRadius = 0;// variable to reduce the atom radius search
	// points
	IBioPolymer protein = null;
	String vanDerWaalsFile="org/openscience/cdk/config/data/pdb_atomtypes.xml";
	double[][][] grid = null;
	GridGenerator gridGenerator = new GridGenerator();
	Hashtable visited = new Hashtable();
	Vector pockets = new Vector();

	
	/**
	 * 
	 */
	public ProteinPocketFinder() {
	}

	/**
	 * @param String	biopolymerFile
	 * @param boolean	cubicGrid	
	 */
	public ProteinPocketFinder(String biopolymerFile, boolean cubicGrid) {
		readBioPolymer(biopolymerFile);
		if (cubicGrid) {
			createCubicGrid();
		} else {
			
		}
	}

	/**
	 * 
	 * @param String	biopolymerFile
	 * @param double	latticeConstant
	 * @param boolean	cubicGrid
	 */
	public ProteinPocketFinder(String biopolymerFile, double latticeConstant,
			boolean cubicGrid) {
		readBioPolymer(biopolymerFile);
		this.latticeConstant = latticeConstant;
		gridGenerator.setLatticeConstant(this.latticeConstant);
		if (cubicGrid) {
			createCubicGrid();
		} else {
			
		}
	}

	/**
	 * @param String	biopolymerFile
	 * @param double[][][]	grid
	 */
	public ProteinPocketFinder(String biopolymerFile, double[][][] grid) {
		this.grid = grid;
		gridGenerator.setGrid(grid);
		readBioPolymer(biopolymerFile);
	}

	/**
	 * @param IBioPolymer	protein
	 * @param double[][][]	grid
	 */
	public ProteinPocketFinder(IBioPolymer protein, double[][][] grid) {
		this.protein = protein;
		this.grid = grid;
		gridGenerator.setGrid(grid);
	}

	/**
	 * Creates from a pdb File a BioPolymer
	 * 
	 * @param String	biopolymerFile
	 * @return void
	 */
	private void readBioPolymer(String biopolymerFile) {
		try {
			// Read PDB file
			FileReader fileReader = new FileReader(biopolymerFile);
			IChemObjectReader reader = new ReaderFactory()
					.createReader(fileReader);
			IChemFile chemFile = (IChemFile) reader
					.read((IChemObject) new org.openscience.cdk.ChemFile());
			// Get molecule from ChemFile
			IChemSequence chemSequence = chemFile.getChemSequence(0);
			IChemModel chemModel = chemSequence.getChemModel(0);
			ISetOfMolecules setOfMolecules = chemModel.getSetOfMolecules();
			protein = (IBioPolymer) setOfMolecules.getMolecule(0);
		} catch (Exception exc) {
			System.out.println("Could not read BioPolymer from file>"
					+ biopolymerFile + " due to: " + exc.getMessage());
		}
	}

	
	/**
	 * Method determines the minimum and maximum values of a coordinate space 
	 * up to 3D space
	 * 
	 * @return double[] stores min,max,min,max,min,max
	 */
	public double[] findGridBoundaries() {
		IAtom[] atoms = protein.getAtoms();
		double[] minMax = new double[6];
		minMax[0] = atoms[0].getPoint3d().x;
		minMax[1] = atoms[0].getPoint3d().x;
		minMax[2] = atoms[0].getPoint3d().y;
		minMax[3] = atoms[0].getPoint3d().y;
		minMax[4] = atoms[0].getPoint3d().z;
		minMax[5] = atoms[0].getPoint3d().z;
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint3d().x > minMax[1]) {
				minMax[1] = atoms[i].getPoint3d().x;
			} else if (atoms[i].getPoint3d().y > minMax[3]) {
				minMax[3] = atoms[i].getPoint3d().y;
			} else if (atoms[i].getPoint3d().z > minMax[5]) {
				minMax[5] = atoms[i].getPoint3d().z;
			} else if (atoms[i].getPoint3d().x < minMax[0]) {
				minMax[0] = atoms[i].getPoint3d().x;
			} else if (atoms[i].getPoint3d().y < minMax[2]) {
				minMax[2] = atoms[i].getPoint3d().y;
			} else if (atoms[i].getPoint3d().z < minMax[4]) {
				minMax[4] = atoms[i].getPoint3d().z;
			}
		}
		return minMax;
	}


	/**
	 * Method creates a cubic grid with the grid generator class
	 *
	 * @return void
	 */
	public void createCubicGrid() {
//		System.out.println("	CREATE CUBIC GRID");
		gridGenerator.setDimension(findGridBoundaries(), true);
		gridGenerator.generateGrid();
		this.grid = gridGenerator.getGrid();
	}

	/**
	 * Method assigns the atoms of a biopolymer to the grid. For every atom
	 * the corresponding grid point is identified and set to the value
	 * of the proteinInterior variable.
	 * The atom radius and solvent radius is accounted for with the variables:
	 *   double rAtom 
	 *   dpuble rSolvent
	 *
	 * @throws Exception
	 * @return void
	 */
	public void assignProteinToGrid() throws Exception {
//		System.out.print("	ASSIGN PROTEIN TO GRID");
		// 1. Step: Set all grid points to solvent accessible
		this.grid = gridGenerator.initializeGrid(this.grid, 0);
		// 2. Step Grid points inaccessible to solvent are assigend a value of -1
		// set grid points around (r_atom+r_solv) to -1
		IAtom[] atoms = protein.getAtoms();
		Point3d gridPoint = null;
		int checkGridPoints = 0;
		double vdWRadius = 0;
		int[] dim = gridGenerator.getDim();
		int proteinAtomCount = 0;
		int[] minMax = { 0, 0, 0, 0, 0, 0 };

		for (int i = 0; i < atoms.length; i++) {
			if (((PDBAtom) atoms[i]).getHetAtom()) {
				continue;
			}
			gridPoint = gridGenerator.getGridPointFrom3dCoordinates(atoms[i]
					.getPoint3d());
			this.grid[(int) gridPoint.x][(int) gridPoint.y][(int) gridPoint.z] = -1;
			vdWRadius = atoms[i].getVanderwaalsRadius();
			if (vdWRadius == 0) {
				vdWRadius = rAtom;
			}
			checkGridPoints = (int) (((vdWRadius + rSolvent) / gridGenerator
					.getLatticeConstant()) - atomCheckRadius);
			if (checkGridPoints < 0) {
				checkGridPoints = 0;
			}
			minMax[0] = (int) gridPoint.x - checkGridPoints;
			minMax[1] = (int) gridPoint.x + checkGridPoints;
			minMax[2] = (int) gridPoint.y - checkGridPoints;
			minMax[3] = (int) gridPoint.y + checkGridPoints;
			minMax[4] = (int) gridPoint.z - checkGridPoints;
			minMax[5] = (int) gridPoint.z + checkGridPoints;
			minMax = checkBoundaries(minMax, dim);
			for (int x = minMax[0]; x <= minMax[1]; x++) {
				for (int y = minMax[2]; y <= minMax[3]; y++) {
					for (int z = minMax[4]; z <= minMax[5]; z++) {
						this.grid[x][y][z] = this.grid[x][y][z] - 1;
						proteinAtomCount++;
					}
				}

			}
		}// for atoms.length

//		System.out.println("- checkGridPoints>" + checkGridPoints
//				+ " ProteinGridPoints>" + proteinAtomCount);
	}

	public void debugg_checkPSPEvent() {
		System.out.print("	debugg_checkPSPEvent");
		int[] dim = gridGenerator.getDim();
		// int pspMin=0;
		int[] pspEvents = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int proteinGrid = 0;
		for (int x = 0; x <= dim[0]; x++) {
			for (int y = 0; y <= dim[1]; y++) {
				for (int z = 0; z <= dim[2]; z++) {

					if (this.grid[x][y][z] == 0) {
						pspEvents[0]++;
					} else if (this.grid[x][y][z] == 1) {
						pspEvents[1]++;
					} else if (this.grid[x][y][z] == 2) {
						pspEvents[2]++;
					} else if (this.grid[x][y][z] == 3) {
						pspEvents[3]++;
					} else if (this.grid[x][y][z] == 4) {
						pspEvents[4]++;
					} else if (this.grid[x][y][z] == 5) {
						pspEvents[5]++;
					} else if (this.grid[x][y][z] == 6) {
						pspEvents[6]++;
					} else if (this.grid[x][y][z] == 7) {
						pspEvents[7]++;
					} else if (this.grid[x][y][z] >= 7) {
						pspEvents[8]++;
					}

					if (this.grid[x][y][z] < 0) {
						proteinGrid++;
					}
				}
			}
		}
		System.out.print("  minPSPocket:" + minPSPocket + " proteinGridPoints:"
				+ proteinGrid);
		int sum = 0;
		for (int i = 0; i < pspEvents.length; i++) {
			if (i >= minPSPocket) {
				sum = sum + pspEvents[i];
			}
			System.out.print(" " + i + ":" + pspEvents[i]);
		}
		System.out.println(" pspAll>" + sum);
		// System.out.println(" PSPAll:"+pspAll+" minPSP:"+minPSP+"
		// #pspMin:"+pspMin+" psp7:"+psp7+" proteinGridPoints:"+proteinGrid
		// +" solventGridPoints:"+solventGrid);
	}

	/**
	 * Main method which calls the methods:
	 * 	assignProteinToGrid
	 * 	GridScan
	 *  FindPockets
	 *
	 * @return void
	 */
	public void sitefinder() {
		//System.out.println("SITEFINDER");
		try {
			assignProteinToGrid();
		} catch (Exception ex1) {
			System.out.println("Problems with assignProteinToGrid due to:"
					+ ex1.toString());
		}
		// 3. Step scan allong x,y,z axis and the diagonals, if PSP event add +1
		// to grid cell
		int[] dim = gridGenerator.getDim();
//		System.out.println("	SITEFINDER-SCAN - dim:" + dim[0] + " grid:"
//				+ this.grid[0].length + " grid point sum:" + this.grid.length
//				* this.grid[0].length * this.grid[0][0].length);
		axisScanX(dim[2], dim[1], dim[0]);// x-Axis
		axisScanY(dim[2], dim[0], dim[1]);// y-Axis
		axisScanZ(dim[0], dim[1], dim[2]);// z-Axis

		diagonalAxisScanXZY(dim[0], dim[2], dim[1]);// diagonal1-Axis
		diagonalAxisScanYZX(dim[1], dim[2], dim[0]);// diagonal2-Axis
		diagonalAxisScanYXZ(dim[1], dim[0], dim[2]);// diagonal3-Axis
		diagonalAxisScanXYZ(dim[0], dim[1], dim[2]);// diagonal4-Axis

		//debugg_checkPSPEvent();

		findPockets();

		sortPockets();
	}

	/**
	 * Method sorts the pockets due to its size. the biggest pocket is the first
	 *
	 * @return void
	 */
	private void sortPockets() {
//		System.out.println("	SORT POCKETS Start#:" + pockets.size());
		Hashtable hashPockets = new Hashtable();
		Vector pocket = new Vector();
		Vector sortPockets = new Vector(pockets.size());
		for (int i = 0; i < pockets.size(); i++) {
			pocket = (Vector) pockets.get(i);
			if (hashPockets.containsKey(new Integer(pocket.size()))) {
				Vector tmp = (Vector) hashPockets
						.get(new Integer(pocket.size()));
				tmp.add(new Integer(i));
				hashPockets.put(new Integer(pocket.size()), tmp);
			} else {
				Vector value = new Vector();
				value.add(new Integer(i));
				hashPockets.put(new Integer(pocket.size()), value);
			}
		}

		ArrayList keys = new ArrayList(hashPockets.keySet());
		Collections.sort(keys);
		for (int i = keys.size() - 1; i >= 0; i--) {
			Vector value = (Vector) hashPockets.get(keys.get(i));
//			System.out.println("key:" + i + " Value" + keys.get(i)
//					+ " #Pockets:" + value.size());
			for (int j = 0; j < value.size(); j++) {
				sortPockets.add(pockets
						.get(((Integer) value.get(j)).intValue()));
			}
		}
//		System.out.println("	SORT POCKETS End#:" + sortPockets.size());
		pockets = sortPockets;
	}

	/**
	 * Method which finds the pocket, with a simple nearest neighbour clustering. The points
	 * which should be clustered or form a pocket can be determined with:
	 * 	minPSPocket
	 * 	minPSCluster
	 *  linkageRadius
	 *  pocketSize
	 *  
	 * @return void
	 */
	private void findPockets() {
		int[] dim = gridGenerator.getDim();
//		System.out.println("	FIND POCKETS>dimx:" + dim[0] + " dimy:" + dim[1]
//				+ " dimz:" + dim[2] + " linkageRadius>" + linkageRadius
//				+ " latticeConstant>" + latticeConstant + " pocketSize:"
//				+ pocketSize + " minPSPocket:" + minPSPocket + " minPSCluster:"
//				+ minPSCluster);
		int pointsVisited = 0;
		int significantPointsVisited = 0;
		for (int x = 0; x < dim[0]; x++) {
			for (int y = 0; y < dim[1]; y++) {
				for (int z = 0; z < dim[2]; z++) {
					// System.out.print(" x:"+x+" y:"+y+" z:"+z);
					Point3d start = new Point3d(x, y, z);
					pointsVisited++;
					if (this.grid[x][y][z] >= minPSPocket
							& !visited.containsKey(new String(x + "." + y + "."
									+ z))) {
						Vector subPocket = new Vector();
						// System.out.print("new Point: "+grid[x][y][z]);
						significantPointsVisited++;
						// System.out.println("visited:"+pointsVisited);
						subPocket = this
								.clusterPSPPocket(start, subPocket, dim);
						if (subPocket.size() >= pocketSize && subPocket != null) {
							pockets.add((Vector) subPocket);
						}
						// System.out.println(" Points visited:"+pointsVisited+"
						// subPocketSize:"+subPocket.size()+"
						// pocketsSize:"+pockets.size()
						// +" hashtable:"+visited.size());

					}
				}
			}

		}
//		try {
//			System.out.println("	->>>> #pockets:" + pockets.size()
//					+ " significantPointsVisited:" + significantPointsVisited
//					+ " keys:" + visited.size() + " PointsVisited:"
//					+ pointsVisited);
//		} catch (Exception ex1) {
//			System.out
//					.println("Problem in System.out due to " + ex1.toString());
//		}

	}

	/**
	 * Method performs the clustering, is called by findPockets()
	 *
	 * @param Point3d	root
	 * @param Vector	sub_Pocket
	 * @param int[]		dim
	 * @return Vector
	 */
	public Vector clusterPSPPocket(Point3d root, Vector sub_Pocket, int[] dim) {
		// System.out.println(" ****** New Root ******:"+root.x+" "+root.y+"
		// "+root.z);
		visited.put(new String((int) root.x + "." + (int) root.y + "."
				+ (int) root.z), new Integer(1));
		int[] minMax = { 0, 0, 0, 0, 0, 0 };
		minMax[0] = (int) (root.x - linkageRadius);
		minMax[1] = (int) (root.x + linkageRadius);
		minMax[2] = (int) (root.y - linkageRadius);
		minMax[3] = (int) (root.y + linkageRadius);
		minMax[4] = (int) (root.z - linkageRadius);
		minMax[5] = (int) (root.z + linkageRadius);
		minMax = checkBoundaries(minMax, dim);
		// System.out.println("cluster:"+minMax[0]+" "+minMax[1]+" "+minMax[2]+"
		// "+minMax[3]+" "+minMax[4]+" "+minMax[5]+" ");
		for (int k = minMax[0]; k <= minMax[1]; k++) {
			for (int m = minMax[2]; m <= minMax[3]; m++) {
				for (int l = minMax[4]; l <= minMax[5]; l++) {
					Point3d node = new Point3d(k, m, l);
					// System.out.println(" clusterPSPPocket:"+root.x+"
					// "+root.y+" "+root.z+" ->"+k+" "+m+" "+l+"
					// #>"+this.grid[k][m][l]+" key:"+visited.containsKey(new
					// String(k+"."+m+"."+l)));
					if (this.grid[k][m][l] >= minPSCluster
							&& !visited.containsKey(new String(k + "." + m
									+ "." + l))) {
						// System.out.println(" ---->FOUND");
						sub_Pocket.add(node);
						this.clusterPSPPocket(node, sub_Pocket, dim);
					}
				}
			}
		}
		sub_Pocket.add(root);
		return sub_Pocket;
	}

	/**
	 * Method checks boundaries
	 *
	 * @param int[]	with minMax values
	 * @param int[]	dimension
	 * @return int[]	new minMax values between 0 and dim	
	 */
	private int[] checkBoundaries(int[] minMax, int[] dim) {
		if (minMax[0] < 0) {
			minMax[0] = 0;
		}
		if (minMax[1] > dim[0]) {
			minMax[1] = dim[0];
		}
		if (minMax[2] < 0) {
			minMax[2] = 0;
		}
		if (minMax[3] > dim[1]) {
			minMax[3] = dim[1];
		}
		if (minMax[4] < 0) {
			minMax[4] = 0;
		}
		if (minMax[5] > dim[2]) {
			minMax[5] = dim[2];
		}
		return minMax;
	}

	/**
	 * Method which assigns upon a PSP event +1 to these grid points
	 *
	 * @param Vector	line
	 * @return void
	 */
	private void firePSPEvent(Vector line) {
		for (int i = 0; i < line.size(); i++) {
			this.grid[(int) ((Point3d) line.get(i)).x][(int) ((Point3d) line
					.get(i)).y][(int) ((Point3d) line.get(i)).z] = this.grid[(int) ((Point3d) line
					.get(i)).x][(int) ((Point3d) line.get(i)).y][(int) ((Point3d) line
					.get(i)).z] + 1;
		}

	}

	/**
	 * Method performs a scan
	 * works only for cubic grids!
	 *
	 * @param int	dimK -> first dimension 
	 * @param int	dimL -> second dimension 
	 * @param int	dimM -> third dimension 
	 * @return void
	 */
	public void diagonalAxisScanXZY(int dimK, int dimL, int dimM) {
		// x min ->x max;left upper corner z+y max->min//1
		//System.out.print("	diagonalAxisScanXZY");
		if (dimM < dimL) {
			dimL = dimM;
		}
		int gridPoints = 0;
		Vector line = new Vector();
		int pspEvent = 0;
		int m = 0;
		for (int j = dimM; j >= 1; j--) {// z
			line.removeAllElements();
			pspEvent = 0;
			for (int k = 0; k <= dimK; k++) {// min -> max; x
				m = dimM;// m==y
				line.removeAllElements();
				pspEvent = 0;
				for (int l = dimL; l >= 0; l--) {// z
					gridPoints++;
					if (grid[k][m][l] < 0) {
						if (pspEvent < 2) {
							line.removeAllElements();
							pspEvent = 1;
						} else if (pspEvent == 2) {
							firePSPEvent(line);
							line.removeAllElements();
							pspEvent = 1;
						}
					} else {
						if (pspEvent == 1 | pspEvent == 2) {
							line.add(new Point3d(k, m, l));
							pspEvent = 2;
						}
					}
					m--;
				}// for l
			}
			dimL = j;
		}
		//System.out.println(" #gridPoints>" + gridPoints);
	}

	/**
	 * Method performs a scan
	 * works only for cubic grids!
	 *
	 * @param int	dimK -> first dimension 
	 * @param int	dimL -> second dimension 
	 * @param int	dimM -> third dimension 
	 * @return void
	 */
	public void diagonalAxisScanYZX(int dimK, int dimL, int dimM) {
		// y min -> y max; right lower corner zmax->zmin, xmax ->min//4
		// System.out.print(" diagonalAxisScanYZX");
		int gridPoints = 0;
		if (dimM < dimL) {
			dimL = dimM;
		}
		Vector line = new Vector();
		int pspEvent = 0;
		int m = 0;
		for (int j = dimM; j >= 1; j--) {// z
			line.removeAllElements();
			pspEvent = 0;
			for (int k = 0; k <= dimK; k++) {// min -> max; y
				m = dimM;// m==x
				line.removeAllElements();
				pspEvent = 0;
				for (int l = dimL; l >= 0; l--) {// z
					gridPoints++;
					if (grid[m][k][l] < 0) {
						if (pspEvent < 2) {
							line.removeAllElements();
							pspEvent = 1;
						} else if (pspEvent == 2) {
							firePSPEvent(line);
							line.removeAllElements();
							pspEvent = 1;
						}
					} else {
						if (pspEvent == 1 | pspEvent == 2) {
							line.add(new Point3d(m, k, l));
							pspEvent = 2;
						}
					}
					m--;
				}// for l
			}
			dimL = j;
		}
		// System.out.println(" #gridPoints>"+gridPoints);
	}

	/**
	 * Method performs a scan
	 * works only for cubic grids!
	 *
	 * @param int	dimK -> first dimension 
	 * @param int	dimL -> second dimension 
	 * @param int	dimM -> third dimension 
	 * @return void
	 */
	public void diagonalAxisScanYXZ(int dimK, int dimL, int dimM) {
		// y min -> y max; left lower corner z max->min, x min->max//2
		// System.out.print(" diagonalAxisScanYXZ");
		int gridPoints = 0;
		if (dimM < dimL) {
			dimL = dimM;
		} else {
			dimM = dimL;
		}
		Vector line = new Vector();
		int pspEvent = 0;
		int l = 0;
		for (int j = dimL; j >= 1; j--) {// z
			line.removeAllElements();
			pspEvent = 0;
			for (int k = 0; k <= dimK; k++) {// min -> max; y
				line.removeAllElements();
				pspEvent = 0;
				l = 0;// x
				for (int m = dimM; m >= 0; m--) {// z
					gridPoints++;
					if (grid[l][k][m] < 0) {
						if (pspEvent < 2) {
							line.removeAllElements();
							pspEvent = 1;
						} else if (pspEvent == 2) {
							firePSPEvent(line);
							line.removeAllElements();
							pspEvent = 1;
						}
					} else {
						if (pspEvent == 1 | pspEvent == 2) {
							line.add(new Point3d(l, k, m));
							pspEvent = 2;
						}
					}
					l++;
				}// for m;z
			}// for k;y
			dimM = j;
		}
		// System.out.println(" #gridPoints>"+gridPoints);
	}

	/**
	 * Method performs a scan
	 * works only for cubic grids!
	 *
	 * @param int	dimK -> first dimension 
	 * @param int	dimL -> second dimension 
	 * @param int	dimM -> third dimension 
	 * @return void
	 */
	public void diagonalAxisScanXYZ(int dimK, int dimL, int dimM) {
		// x min -> xmax;left lower corner z max->min, y min->max//3
		// System.out.print(" diagonalAxisScanXYZ");
		int gridPoints = 0;
		if (dimM < dimL) {
			dimL = dimM;
		} else {
			dimM = dimL;
		}
		Vector line = new Vector();
		int pspEvent = 0;
		int l = 0;
		for (int j = dimL; j >= 1; j--) {// z
			line.removeAllElements();
			pspEvent = 0;
			for (int k = 0; k <= dimK; k++) {// min -> max;x
				line.removeAllElements();
				pspEvent = 0;
				l = 0;// y
				for (int m = dimM; m >= 0; m--) {// z
					gridPoints++;
					if (grid[k][l][m] < 0) {
						if (pspEvent < 2) {
							line.removeAllElements();
							pspEvent = 1;
						} else if (pspEvent == 2) {
							firePSPEvent(line);
							line.removeAllElements();
							pspEvent = 1;
						}
					} else {
						if (pspEvent == 1 | pspEvent == 2) {
							line.add(new Point3d(k, l, m));
							pspEvent = 2;
						}
					}
					l++;
				}// for m;z
			}// for k;x
			dimM = j;
		}
		// System.out.println(" #gridPoints>"+gridPoints);
	}

	/**
	 * Method performs a scan
	 * works only for cubic grids!
	 *
	 * @param int	dimK -> first dimension 
	 * @param int	dimL -> second dimension 
	 * @param int	dimM -> third dimension 
	 * @return void
	 */
	public void axisScanX(int dimK, int dimL, int dimM) {
		// z,y,x
//		System.out.print("	diagonalAxisScanX");
		int gridPoints = 0;
		Vector line = new Vector();
		int pspEvent = 0;
		for (int k = 0; k <= dimK; k++) {
			line.removeAllElements();
			pspEvent = 0;
			for (int l = 0; l <= dimL; l++) {
				line.removeAllElements();
				pspEvent = 0;
				for (int m = 0; m <= dimM; m++) {
					gridPoints++;
					if (grid[m][l][k] < 0) {
						if (pspEvent < 2) {
							pspEvent = 1;
							line.removeAllElements();
						} else if (pspEvent == 2) {
							firePSPEvent(line);
							line.removeAllElements();
							pspEvent = 1;
						}
					} else {
						if (pspEvent == 1 | pspEvent == 2) {
							line.add(new Point3d(m, l, k));
							pspEvent = 2;
						}
					}
				}
			}
		}
//		System.out.println(" #gridPoints>" + gridPoints);
	}

	/**
	 * Method performs a scan
	 * works only for cubic grids!
	 *
	 * @param int	dimK -> first dimension 
	 * @param int	dimL -> second dimension 
	 * @param int	dimM -> third dimension 
	 * @return void
	 */
	public void axisScanY(int dimK, int dimL, int dimM) {
		// z,x,y
		Vector line = new Vector();
		int pspEvent = 0;
		for (int k = 0; k <= dimK; k++) {
			line.removeAllElements();
			pspEvent = 0;
			for (int l = 0; l <= dimL; l++) {
				line.removeAllElements();
				pspEvent = 0;
				for (int m = 0; m <= dimM; m++) {
					if (grid[l][m][k] < 0) {
						if (pspEvent < 2) {
							pspEvent = 1;
							line.removeAllElements();
						} else if (pspEvent == 2) {
							// if (line.size()>2){
							firePSPEvent(line);
							// }
							line.removeAllElements();
							pspEvent = 1;
						}
					} else {
						if (pspEvent > 0) {
							line.add(new Point3d(l, m, k));
							pspEvent = 2;
						}
					}
				}
			}
		}
	}

	/**
	 * Method performs a scan
	 * works only for cubic grids!
	 *
	 * @param int	dimK -> first dimension 
	 * @param int	dimL -> second dimension 
	 * @param int	dimM -> third dimension 
	 * @return void
	 */
	public void axisScanZ(int dimK, int dimL, int dimM) {
		// x,y,z
		Vector line = new Vector();
		int pspEvent = 0;
		for (int k = 0; k <= dimK; k++) {
			line.removeAllElements();
			pspEvent = 0;
			for (int l = 0; l <= dimL; l++) {
				line.removeAllElements();
				pspEvent = 0;
				for (int m = 0; m <= dimM; m++) {
					if (grid[k][l][m] < 0) {
						if (pspEvent < 2) {
							pspEvent = 1;
							line.removeAllElements();
						} else if (pspEvent == 2) {
							firePSPEvent(line);
							line.removeAllElements();
							pspEvent = 1;
						}
					} else {
						if (pspEvent > 0) {
							line.add(new Point3d(k, l, m));
							pspEvent = 2;
						}
					}
				}
			}
		}
	}

	
	/**
	 * Method which assigns van der Waals radii to the biopolymer
	 * default org/openscience/cdk/config/data/pdb_atomtypes.xml
	 * stored in the variable String vanDerWaalsFile
	 *
	 * @return void
	 */
	public void assignVdWRadiiToProtein() {
		AtomTypeFactory atf = null;
		IAtom[] atoms = protein.getAtoms();
		try {
			atf = AtomTypeFactory.getInstance(
                vanDerWaalsFile, atoms[0].getBuilder()
            );
		} catch (Exception ex1) {
			System.out.println("Problem with AtomTypeFactory due to:"
					+ ex1.toString());
		}
		for (int i = 0; i < atoms.length; i++) {
			try {
				atf.configure(atoms[i]);
			} catch (Exception ex2) {
				System.out.println("Problem with atf.configure due to:"
						+ ex2.toString());
			}
		}

	}

	/**
	 * Method writes the grid to pmesh format
	 *
	 * @param String	outPutFileName
	 * @return void
	 */
	public void gridToPmesh(String outPutFileName) {
		try {
			gridGenerator.writeGridInPmeshFormat(outPutFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method writes the PSP points (>=minPSPocket) to pmesh format
	 *
	 * @param String	outPutFileName
	 * @return void
	 */
	public void pspGridToPmesh(String outPutFileName) {
		try {
			gridGenerator.writeGridInPmeshFormat(outPutFileName, minPSPocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method writes the protein grid points to pmesh format
	 *
	 * @param String	outPutFileName
	 * @return void
	 */
	public void proteinGridToPmesh(String outPutFileName) {
		try {
			gridGenerator.writeGridInPmeshFormat(outPutFileName, -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method writes the pockets to pmesh format
	 *
	 * @param String	outPutFileName
	 * @return void
	 */
	public void writePocketsToPMesh(String outPutFileName) {

		try {
			for (int i = 0; i < pockets.size(); i++) {// go through every
				// pocket
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						outPutFileName + "-" + i + ".pmesh"));
				Vector pocket = (Vector) pockets.get(i);
				writer.write(pocket.size() + "\n");
				for (int j = 0; j < pocket.size(); j++) {// go through every
					// grid point of the
					// actual pocket
					Point3d actualGridPoint = (Point3d) pocket.get(j);
					Point3d coords = gridGenerator
							.getCoordinatesFromGridPoint(actualGridPoint);
					writer.write(coords.x + "\t" + coords.y + "\t" + coords.z
							+ "\n");
				}
				writer.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * @return double[][][]	Returns the grid.
	 */
	public double[][][] getGrid() {
		return grid;
	}

	
	/**
	 * @param double[][][] grid The grid to set.
	 */
	public void setGrid(double[][][] grid) {
		this.grid = grid;
	}

	
	/**
	 * @return double	Returns the latticeConstant.
	 */
	public double getLatticeConstant() {
		return latticeConstant;
	}

	
	/**
	 * @param double latticeConstant The latticeConstant to set.
	 */
	public void setLatticeConstant(double latticeConstant) {
		this.latticeConstant = latticeConstant;
	}

	
	/**
	 * @return double	Returns the linkageRadius.
	 */
	public double getLinkageRadius() {
		return linkageRadius;
	}

	
	/**
	 * @param double	linkageRadius The linkageRadius to set.
	 */
	public void setLinkageRadius(double linkageRadius) {
		this.linkageRadius = linkageRadius;
	}

	
	/**
	 * @return int	Returns the minPSCluster.
	 */
	public int getMinPSCluster() {
		return minPSCluster;
	}

	
	/**
	 * @param int minPSCluster The minPSCluster to set.
	 */
	public void setMinPSCluster(int minPSCluster) {
		this.minPSCluster = minPSCluster;
	}

	
	/**
	 * @return int	Returns the minPSPocket.
	 */
	public int getMinPSPocket() {
		return minPSPocket;
	}

	
	/**
	 * @param int minPSPocket The minPSPocket to set.
	 */
	public void setMinPSPocket(int minPSPocket) {
		this.minPSPocket = minPSPocket;
	}

	
	/**
	 * @return int	Returns the pocketSize.
	 */
	public int getPocketSize() {
		return pocketSize;
	}

	
	/**
	 * @param int pocketSize The pocketSize to set.
	 */
	public void setPocketSize(int pocketSize) {
		this.pocketSize = pocketSize;
	}

	
	/**
	 * @return BioPolymer	Returns the protein.
	 */
	public IBioPolymer getProtein() {
		return protein;
	}

	
	/**
	 * @param IBioPolymer protein The protein to set.
	 */
	public void setProtein(IBioPolymer protein) {
		this.protein = protein;
	}

	
	/**
	 * @return int	Returns the proteinInterior.
	 */
	public int getProteinInterior() {
		return proteinInterior;
	}

	
	/**
	 * @param int proteinInterior The proteinInterior to set.
	 */
	public void setProteinInterior(int proteinInterior) {
		this.proteinInterior = proteinInterior;
	}

	
	/**
	 * @return double	Returns the rAtom.
	 */
	public double getRAtom() {
		return rAtom;
	}

	
	/**
	 * @param double atom The rAtom to set.
	 */
	public void setRAtom(double atom) {
		rAtom = atom;
	}

	
	/**
	 * @return double	Returns the rSolvent.
	 */
	public double getRSolvent() {
		return rSolvent;
	}

	
	/**
	 * @param double solvent The rSolvent to set.
	 */
	public void setRSolvent(double solvent) {
		rSolvent = solvent;
	}

	
	/**
	 * @return int	Returns the solvantValue.
	 */
	public int getSolvantValue() {
		return solvantValue;
	}

	
	/**
	 * @param int solvantValue The solvantValue to set.
	 */
	public void setSolvantValue(int solvantValue) {
		this.solvantValue = solvantValue;
	}

	
	/**
	 * @return String	Returns the vanDerWaalsFile.
	 */
	public String getVanDerWaalsFile() {
		return vanDerWaalsFile;
	}

	
	/**
	 * @param String vanDerWaalsFile The vanDerWaalsFile to set.
	 */
	public void setVanDerWaalsFile(String vanDerWaalsFile) {
		this.vanDerWaalsFile = vanDerWaalsFile;
	}

	
	/**
	 * @return Vector	Returns the pockets.
	 */
	public Vector getPockets() {
		return pockets;
	}

	
	/**
	 * @param Vector atomCheckRadius The atomCheckRadius to set.
	 */
	public void setAtomCheckRadius(double atomCheckRadius) {
		this.atomCheckRadius = atomCheckRadius;
	}
	
}
