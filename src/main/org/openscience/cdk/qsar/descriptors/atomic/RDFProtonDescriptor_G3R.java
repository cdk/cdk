/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2004-2007  Matteo Floris <mfe4@users.sf.net>
 * Copyright (C) 2006-2007  Federico
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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetector;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class calculates G3R proton descriptors used in neural networks for H1
 * NMR shift.
 * 
 * <p>
 * This descriptor uses these parameters: <table border="1">
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td>checkAromaticity</td>
 * <td>false</td>
 * <td>True is the aromaticity has to be checked</td>
 * </tr>
 * </table>
 * 
 * @author      Federico
 * @cdk.created 2006-12-11
 * @cdk.module  qsaratomic
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:rdfProtonCalculatedValues
 * @cdk.bug     1632419
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.RDFProtonDescriptor_G3RTest")
public class RDFProtonDescriptor_G3R implements IAtomicDescriptor {

    private static final int g3r_desc_length = 13;    

    private boolean checkAromaticity = false;

	private IAtomContainer acold = null;

	private IRingSet varRingSet = null;

	private IAtomContainerSet varAtomContainerSet = null;
	
    private final static LoggingTool logger = new LoggingTool(RDFProtonDescriptor_G3R.class);

    private static String[] descriptorNames;

    /**
	 * Constructor for the RDFProtonDescriptor object
	 */
	public RDFProtonDescriptor_G3R() {
        descriptorNames = new String[g3r_desc_length];
        for (int i = 0; i < g3r_desc_length; i++) {

            descriptorNames[i] = "g3r_" + (i + 1);
        }
    }

	/**
	 * Gets the specification attribute of the RDFProtonDescriptor_G3R object
	 * 
	 * @return The specification value
	 */
	@TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#rdfProtonCalculatedValues",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}

	/**
	 * Sets the parameters attribute of the RDFProtonDescriptor object
	 * 
	 * @param params
	 *            Parameters are the proton position and a boolean (true if you
	 *            need to detect aromaticity)
	 * @exception CDKException
	 *                Possible Exceptions
	 */
	@TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException(
					"RDFProtonDescriptor only expects one parameters");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException(
					"The second parameter must be of type Boolean");
		}
		checkAromaticity = (Boolean) params[0];
	}

	/**
	 * Gets the parameters attribute of the RDFProtonDescriptor object
	 * 
	 * @return The parameters value
	 */
	@TestMethod(value="testGetParameters")
    public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = checkAromaticity;
		return params;
	}

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return descriptorNames;
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        DoubleArrayResult result = new DoubleArrayResult(g3r_desc_length);
        for (int i = 0; i < g3r_desc_length; i++)
            result.add(Double.NaN);
        return new DescriptorValue(
                getSpecification(), getParameterNames(),
                getParameters(), result,
                getDescriptorNames(), e);
    }

    @TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom,
			IAtomContainer varAtomContainerSet) {
		return (calculate(atom, varAtomContainerSet, null));
	}

	@TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom,
			IAtomContainer atomContainer, IRingSet precalculatedringset) {

        IAtomContainer varAtomContainer;
        try {
            varAtomContainer = (IAtomContainer) atomContainer.clone();
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(e);
        }

        int atomPosition = atomContainer.getAtomNumber(atom);
        IAtom clonedAtom = varAtomContainer.getAtom(atomPosition);
		DoubleArrayResult rdfProtonCalculatedValues = new DoubleArrayResult(g3r_desc_length);
        if (!atom.getSymbol().equals("H")) {
            return getDummyDescriptorValue(new CDKException("Invalid atom specified"));
        }

        // ///////////////////////FIRST SECTION OF MAIN METHOD: DEFINITION OF
		// MAIN VARIABLES
		// ///////////////////////AND AROMATICITY AND PI-SYSTEM AND RINGS
		// DETECTION

		Molecule mol = new Molecule(varAtomContainer);
        if (varAtomContainer != acold) {
            acold = varAtomContainer;
            // DETECTION OF pi SYSTEMS
            varAtomContainerSet = ConjugatedPiSystemsDetector.detect(mol);
            if (precalculatedringset == null)
                try {
                    varRingSet = (new AllRingsFinder())
                            .findAllRings(varAtomContainer);
                } catch (CDKException e) {
                    return getDummyDescriptorValue(e);
                }
            else
                varRingSet = precalculatedringset;
            try {
                GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
                peoe.assignGasteigerMarsiliSigmaPartialCharges(mol, true);
            } catch (Exception ex1) {
                return getDummyDescriptorValue(ex1);
            }
        }
		if (checkAromaticity) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(varAtomContainer);
                CDKHueckelAromaticityDetector.detectAromaticity(varAtomContainer);
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }
        }
		IRingSet rsAtom;
		Ring ring;
		IRingSet ringsWithThisBond;
		// SET ISINRING FLAGS FOR BONDS
//		org.openscience.cdk.interfaces.IBond[] bondsInContainer = varAtomContainer.getBonds();

        Iterator<IBond> bondsInContainer = varAtomContainer.bonds().iterator();
        while (bondsInContainer.hasNext()) {
            IBond bond = bondsInContainer.next();
			ringsWithThisBond = varRingSet.getRings(bond);
			if (ringsWithThisBond.getAtomContainerCount() > 0) {
				bond.setFlag(CDKConstants.ISINRING, true);
			}
		}
		// SET ISINRING FLAGS FOR ATOMS
		org.openscience.cdk.interfaces.IRingSet ringsWithThisAtom;

		for (int w = 0; w < varAtomContainer.getAtomCount(); w++) {
			ringsWithThisAtom = varRingSet
					.getRings(varAtomContainer.getAtom(w));
			if (ringsWithThisAtom.getAtomContainerCount() > 0) {
				varAtomContainer.getAtom(w)
						.setFlag(CDKConstants.ISINRING, true);
			}
		}

		IAtomContainer detected = varAtomContainerSet.getAtomContainer(0);

		// neighboors[0] is the atom joined to the target proton:
		List<IAtom> neighboors = mol.getConnectedAtomsList(clonedAtom);
		IAtom neighbour0 = neighboors.get(0);

		// 2', 3', 4', 5', 6', and 7' atoms up to the target are detected:
		List<IAtom> atomsInSecondSphere = mol.getConnectedAtomsList(neighbour0);
		List<IAtom> atomsInThirdSphere;
		List<IAtom> atomsInFourthSphere;
		List<IAtom> atomsInFifthSphere;
		List<IAtom> atomsInSixthSphere;
		List<IAtom> atomsInSeventhSphere;

		// SOME LISTS ARE CREATED FOR STORING OF INTERESTING ATOMS AND BONDS
		// DURING DETECTION
		ArrayList<Integer> singles = new ArrayList<Integer>(); // list of any bond not
												// rotatable
		ArrayList<Integer> doubles = new ArrayList<Integer>(); // list with only double bonds
		ArrayList<Integer> atoms = new ArrayList<Integer>(); // list with all the atoms in
											// spheres
		// atoms.add( Integer.valueOf( mol.getAtomNumber(neighboors[0]) ) );
		ArrayList<Integer> bondsInCycloex = new ArrayList<Integer>(); // list for bonds in
													// cycloexane-like rings

		// 2', 3', 4', 5', 6', and 7' bonds up to the target are detected:
		IBond secondBond; // (remember that first bond is proton bond)
		IBond thirdBond; //
		IBond fourthBond; //
		IBond fifthBond; //
		IBond sixthBond; //
		IBond seventhBond; //

		// definition of some variables used in the main FOR loop for detection
		// of interesting atoms and bonds:
		boolean theBondIsInA6MemberedRing; // this is like a flag for bonds
											// which are in cycloexane-like
											// rings (rings with more than 4
											// at.)
		IBond.Order bondOrder;
		int bondNumber;
		int sphere;

		// THIS MAIN FOR LOOP DETECT RIGID BONDS IN 7 SPHERES:
        for (IAtom curAtomSecond : atomsInSecondSphere) {
            secondBond = mol.getBond(neighbour0, curAtomSecond);
            if (mol.getAtomNumber(curAtomSecond) != atomPosition
                    && getIfBondIsNotRotatable(mol, secondBond, detected)) {
                sphere = 2;
                bondOrder = secondBond.getOrder();
                bondNumber = mol.getBondNumber(secondBond);
                theBondIsInA6MemberedRing = false;
                checkAndStore(bondNumber, bondOrder, singles, doubles,
                        bondsInCycloex, mol.getAtomNumber(curAtomSecond),
                        atoms, sphere, theBondIsInA6MemberedRing);
                atomsInThirdSphere = mol.getConnectedAtomsList(curAtomSecond);
                if (atomsInThirdSphere.size() > 0) {
                    for (IAtom curAtomThird : atomsInThirdSphere) {
                        thirdBond = mol.getBond(curAtomThird, curAtomSecond);
                        // IF THE ATOMS IS IN THE THIRD SPHERE AND IN A
                        // CYCLOEXANE-LIKE RING, IT IS STORED IN THE PROPER
                        // LIST:
                        if (mol.getAtomNumber(curAtomThird) != atomPosition
                                && getIfBondIsNotRotatable(mol, thirdBond,
                                detected)) {
                            sphere = 3;
                            bondOrder = thirdBond.getOrder();
                            bondNumber = mol.getBondNumber(thirdBond);
                            theBondIsInA6MemberedRing = false;

                            // if the bond is in a cyclohexane-like ring (a ring
                            // with 5 or more atoms, not aromatic)
                            // the boolean "theBondIsInA6MemberedRing" is set to
                            // true
                            if (!thirdBond.getFlag(CDKConstants.ISAROMATIC)) {
                                if (!curAtomThird.equals(neighbour0)) {
                                    rsAtom = varRingSet.getRings(thirdBond);
                                    for (IAtomContainer aRsAtom : rsAtom.atomContainers()) {
                                        ring = (Ring) aRsAtom;
                                        if (ring.getRingSize() > 4
                                                && ring.contains(thirdBond)) {
                                            theBondIsInA6MemberedRing = true;
                                        }
                                    }
                                }
                            }
                            checkAndStore(bondNumber, bondOrder, singles,
                                    doubles, bondsInCycloex, mol
                                    .getAtomNumber(curAtomThird),
                                    atoms, sphere, theBondIsInA6MemberedRing);
                            theBondIsInA6MemberedRing = false;
                            atomsInFourthSphere = mol
                                    .getConnectedAtomsList(curAtomThird);
                            if (atomsInFourthSphere.size() > 0) {
                                for (IAtom curAtomFourth : atomsInFourthSphere) {
                                    fourthBond = mol.getBond(curAtomThird,
                                            curAtomFourth);
                                    if (mol.getAtomNumber(curAtomFourth) != atomPosition
                                            && getIfBondIsNotRotatable(mol,
                                            fourthBond, detected)) {
                                        sphere = 4;
                                        bondOrder = fourthBond.getOrder();
                                        bondNumber = mol
                                                .getBondNumber(fourthBond);
                                        theBondIsInA6MemberedRing = false;
                                        checkAndStore(
                                                bondNumber,
                                                bondOrder,
                                                singles,
                                                doubles,
                                                bondsInCycloex,
                                                mol
                                                        .getAtomNumber(curAtomFourth),
                                                atoms, sphere,
                                                theBondIsInA6MemberedRing);
                                        atomsInFifthSphere = mol
                                                .getConnectedAtomsList(curAtomFourth);
                                        if (atomsInFifthSphere.size() > 0) {
                                            for (IAtom curAtomFifth : atomsInFifthSphere) {
                                                fifthBond = mol.getBond(
                                                        curAtomFifth,
                                                        curAtomFourth);
                                                if (mol
                                                        .getAtomNumber(curAtomFifth) != atomPosition
                                                        && getIfBondIsNotRotatable(
                                                        mol, fifthBond,
                                                        detected)) {
                                                    sphere = 5;
                                                    bondOrder = fifthBond
                                                            .getOrder();
                                                    bondNumber = mol
                                                            .getBondNumber(fifthBond);
                                                    theBondIsInA6MemberedRing = false;
                                                    checkAndStore(
                                                            bondNumber,
                                                            bondOrder,
                                                            singles,
                                                            doubles,
                                                            bondsInCycloex,
                                                            mol
                                                                    .getAtomNumber(curAtomFifth),
                                                            atoms, sphere,
                                                            theBondIsInA6MemberedRing);
                                                    atomsInSixthSphere = mol
                                                            .getConnectedAtomsList(curAtomFifth);
                                                    if (atomsInSixthSphere
                                                            .size() > 0) {
                                                        for (IAtom curAtomSixth : atomsInSixthSphere) {
                                                            sixthBond = mol
                                                                    .getBond(
                                                                            curAtomFifth,
                                                                            curAtomSixth);
                                                            if (mol
                                                                    .getAtomNumber(curAtomSixth) != atomPosition
                                                                    && getIfBondIsNotRotatable(
                                                                    mol,
                                                                    sixthBond,
                                                                    detected)) {
                                                                sphere = 6;
                                                                bondOrder = sixthBond
                                                                        .getOrder();
                                                                bondNumber = mol
                                                                        .getBondNumber(sixthBond);
                                                                theBondIsInA6MemberedRing = false;
                                                                checkAndStore(
                                                                        bondNumber,
                                                                        bondOrder,
                                                                        singles,
                                                                        doubles,
                                                                        bondsInCycloex,
                                                                        mol
                                                                                .getAtomNumber(curAtomSixth),
                                                                        atoms,
                                                                        sphere,
                                                                        theBondIsInA6MemberedRing);
                                                                atomsInSeventhSphere = mol
                                                                        .getConnectedAtomsList(curAtomSixth);
                                                                if (atomsInSeventhSphere
                                                                        .size() > 0) {
                                                                    for (IAtom curAtomSeventh : atomsInSeventhSphere) {
                                                                        seventhBond = mol
                                                                                .getBond(
                                                                                        curAtomSeventh,
                                                                                        curAtomSixth);
                                                                        if (mol
                                                                                .getAtomNumber(curAtomSeventh) != atomPosition
                                                                                && getIfBondIsNotRotatable(
                                                                                mol,
                                                                                seventhBond,
                                                                                detected)) {
                                                                            sphere = 7;
                                                                            bondOrder = seventhBond
                                                                                    .getOrder();
                                                                            bondNumber = mol
                                                                                    .getBondNumber(seventhBond);
                                                                            theBondIsInA6MemberedRing = false;
                                                                            checkAndStore(
                                                                                    bondNumber,
                                                                                    bondOrder,
                                                                                    singles,
                                                                                    doubles,
                                                                                    bondsInCycloex,
                                                                                    mol
                                                                                            .getAtomNumber(curAtomSeventh),
                                                                                    atoms,
                                                                                    sphere,
                                                                                    theBondIsInA6MemberedRing);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Variables
		double distance;
		double sum;
		double smooth = -20;
		double partial;
		int position;
		double limitInf;
		double limitSup;
		double step;

		// ////////////////////////LAST DESCRIPTOR IS g3(r), FOR PROTONS BONDED
		// TO LIKE-CYCLOEXANE RINGS:

		Vector3d a_a = new Vector3d();
		Vector3d a_b = new Vector3d();
		Vector3d b_a = new Vector3d();
		Vector3d b_b = new Vector3d();
		double angle = 0;

		if (bondsInCycloex.size() > 0) {
			IAtom cycloexBondAtom0;
			IAtom cycloexBondAtom1;
			org.openscience.cdk.interfaces.IBond theInCycloexBond;
			distance = 0;
			limitInf = 0;
			limitSup = Math.PI;
			step = (limitSup - limitInf) / 13;
			position = 0;
			smooth = -2.86;
			angle = 0;
			int ya_counter = 0;
			List<IAtom> connAtoms;
			int counter = 0;
			for (double g3r = 0; g3r < limitSup; g3r = g3r + step) {
				sum = 0;
                for (Integer aBondsInCycloex : bondsInCycloex) {
                    ya_counter = 0;
                    angle = 0;
                    partial = 0;
                    position = aBondsInCycloex;
                    theInCycloexBond = mol.getBond(position);
                    cycloexBondAtom0 = theInCycloexBond.getAtom(0);
                    cycloexBondAtom1 = theInCycloexBond.getAtom(1);

                    connAtoms = mol.getConnectedAtomsList(cycloexBondAtom0);
                    for (IAtom connAtom : connAtoms) {
                        if (connAtom.equals(neighbour0))
                            ya_counter += 1;
                    }

                    if (ya_counter > 0) {
                        a_a.set(cycloexBondAtom1.getPoint3d().x,
                                cycloexBondAtom1.getPoint3d().y,
                                cycloexBondAtom1.getPoint3d().z);
                        a_b.set(cycloexBondAtom0.getPoint3d().x,
                                cycloexBondAtom0.getPoint3d().y,
                                cycloexBondAtom0.getPoint3d().z);
                    } else {
                        a_a.set(cycloexBondAtom0.getPoint3d().x,
                                cycloexBondAtom0.getPoint3d().y,
                                cycloexBondAtom0.getPoint3d().z);
                        a_b.set(cycloexBondAtom1.getPoint3d().x,
                                cycloexBondAtom1.getPoint3d().y,
                                cycloexBondAtom1.getPoint3d().z);
                    }
                    b_a.set(neighbour0.getPoint3d().x,
                            neighbour0.getPoint3d().y,
                            neighbour0.getPoint3d().z);
                    b_b.set(atom.getPoint3d().x, atom.getPoint3d().y, atom
                            .getPoint3d().z);

                    angle = calculateAngleBetweenTwoLines(a_a, a_b, b_a, b_b);

                    // logger.debug("gcycr ANGLE: " + angle + " "
                    // +mol.getAtomNumber(cycloexBondAtom0) + "
                    // "+mol.getAtomNumber(cycloexBondAtom1));

                    partial = Math.exp(smooth * (Math.pow((g3r - angle), 2)));
                    sum += partial;
                }
                // g3r_function.add(new Double(sum));
				rdfProtonCalculatedValues.add(sum);
				logger.debug("RDF g3r prob.: "+sum+ " at distance "+g3r);
				counter++;
			}
		}
		else {
			return getDummyDescriptorValue(new CDKException("Some error occurred. Please report"));
		}
		return new DescriptorValue(
			getSpecification(), getParameterNames(), 
			getParameters(), rdfProtonCalculatedValues,
			getDescriptorNames());
	}

	// Others definitions

	private boolean getIfBondIsNotRotatable(Molecule mol,
			org.openscience.cdk.interfaces.IBond bond, IAtomContainer detected) {
		boolean isBondNotRotatable = false;
		int counter = 0;
		IAtom atom0 = bond.getAtom(0);
		IAtom atom1 = bond.getAtom(1);
		if (detected != null) {
			if (detected.contains(bond))
				counter += 1;
		}
		if (atom0.getFlag(CDKConstants.ISINRING)) {
			if (atom1.getFlag(CDKConstants.ISINRING)) {
				counter += 1;
			} else {
				if (atom1.getSymbol().equals("H"))
					counter += 1;
				else
					counter += 0;
			}
		}
		if (atom0.getSymbol().equals("N") && atom1.getSymbol().equals("C")) {
			if (getIfACarbonIsDoubleBondedToAnOxygen(mol, atom1))
				counter += 1;
		}
		if (atom0.getSymbol().equals("C") && atom1.getSymbol().equals("N")) {
			if (getIfACarbonIsDoubleBondedToAnOxygen(mol, atom0))
				counter += 1;
		}
		if (counter > 0)
			isBondNotRotatable = true;
		return isBondNotRotatable;
	}

	private boolean getIfACarbonIsDoubleBondedToAnOxygen(Molecule mol,
			IAtom carbonAtom) {
		boolean isDoubleBondedToOxygen = false;
		List<IAtom> neighToCarbon = mol.getConnectedAtomsList(carbonAtom);
		org.openscience.cdk.interfaces.IBond tmpBond;
		int counter = 0;
        for (IAtom neighbour : neighToCarbon) {
            if (neighbour.getSymbol().equals("O")) {
                tmpBond = mol.getBond(neighbour, carbonAtom);
                if (tmpBond.getOrder() == IBond.Order.DOUBLE)
                    counter += 1;
            }
        }
        if (counter > 0)
			isDoubleBondedToOxygen = true;
		return isDoubleBondedToOxygen;
	}

	// this method calculates the angle between two bonds given coordinates of
	// their atoms

	private double calculateAngleBetweenTwoLines(Vector3d a, Vector3d b,
			Vector3d c, Vector3d d) {
		Vector3d firstLine = new Vector3d();
		firstLine.sub(a, b);
		Vector3d secondLine = new Vector3d();
		secondLine.sub(c, d);
		Vector3d firstVec = new Vector3d(firstLine);
		Vector3d secondVec = new Vector3d(secondLine);
		return firstVec.angle(secondVec);
	}

	// this method store atoms and bonds in proper lists:
	private void checkAndStore(int bondToStore, IBond.Order bondOrder,
			ArrayList<Integer> singleVec,
            ArrayList<Integer> doubleVec,
            ArrayList<Integer> cycloexVec,
			int a1,
            ArrayList<Integer> atomVec,
            int sphere, boolean isBondInCycloex) {
		if (!atomVec.contains(Integer.valueOf(a1))) {
			if (sphere < 6)
				atomVec.add(a1);
		}
		if (!cycloexVec.contains(Integer.valueOf(bondToStore))) {
			if (isBondInCycloex) {
				cycloexVec.add(bondToStore);
			}
		}
		if (bondOrder == IBond.Order.DOUBLE) {
			if (!doubleVec.contains(Integer.valueOf(bondToStore)))
				doubleVec.add(bondToStore);
		}
		if (bondOrder == IBond.Order.SINGLE) {
			if (!singleVec.contains(Integer.valueOf(bondToStore)))
				singleVec.add(bondToStore);
		}
	}

	// generic method for calculation of distance btw 2 atoms
	private double calculateDistanceBetweenTwoAtoms(IAtom atom1, IAtom atom2) {
		double distance;
		Point3d firstPoint = atom1.getPoint3d();
		Point3d secondPoint = atom2.getPoint3d();
		distance = firstPoint.distance(secondPoint);
		return distance;
	}

	// given a double bond
	// this method returns a bond bonded to this double bond
	private int getNearestBondtoAGivenAtom(Molecule mol, IAtom atom,
			org.openscience.cdk.interfaces.IBond bond) {
		int nearestBond = 0;
		double[] values;
		double distance = 0;
		IAtom atom0 = bond.getAtom(0);
        List<IBond> bondsAtLeft = mol.getConnectedBondsList(atom0);
		int partial;
		for (int i = 0; i < bondsAtLeft.size(); i++) {
			IBond curBond = bondsAtLeft.get(i);
			values = calculateDistanceBetweenAtomAndBond(atom, curBond);
			partial = mol.getBondNumber(curBond);
			if (i == 0) {
				nearestBond = mol.getBondNumber(curBond);
				distance = values[0];
			} else {
				if (values[0] < distance) {
					nearestBond = partial;
				}
				/*
				 * XXX commented this out, because is has no effect
				 * 
				 * else { nearestBond = nearestBond; }
				 */
			}
		}
		return nearestBond;

	}

	// method which calculated distance btw an atom and the middle point of a
	// bond
	// and returns distance and coordinates of middle point
	private double[] calculateDistanceBetweenAtomAndBond(IAtom proton,
			org.openscience.cdk.interfaces.IBond theBond) {
		Point3d middlePoint = theBond.get3DCenter();
		Point3d protonPoint = proton.getPoint3d();
		double[] values = new double[4];
		values[0] = middlePoint.distance(protonPoint);
		values[1] = middlePoint.x;
		values[2] = middlePoint.y;
		values[3] = middlePoint.z;
		return values;
	}

	/**
	 * Gets the parameterNames attribute of the RDFProtonDescriptor object
	 * 
	 * @return The parameterNames value
	 */
	@TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "checkAromaticity";
		return params;
	}

	/**
	 * Gets the parameterType attribute of the RDFProtonDescriptor object
	 * 
	 * @param name
	 *            Description of the Parameter
	 * @return The parameterType value
	 */
	@TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        if (name.equals("checkAromaticity")) return Boolean.TRUE;
        return null;
	}
}
