/* Copyright (C) 2004-2008  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.charges;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.modeling.builder3d.ForceFieldConfigurator;
import org.openscience.cdk.modeling.builder3d.MMFF94BasedParameterSetReader;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The calculation of the MMFF94 partial charges. Charges are stored as atom
 * properties: for an AtomContainer ac, values are calculated with:
 * <pre>
 *  HydrogenAdder hAdder = new HydrogenAdder();
 *  SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 *  IAtomContainer ac = sp.parseSmiles("CC");
 *  hAdder.addExplicitHydrogensToSatisfyValency((Molecule)ac);
 *  MMFF94PartialCharges mmff = new MMFF94PartialCharges();
 *  mmff.assignMMFF94PartialCharges(ac);
 *  </pre>
 * and for each atom, the value is given by:
 * <pre>
 *  ( (Double)atom.getProperty("MMFF94charge") ).doubleValue().
 *  </pre>
 *
 * @author mfe4
 * @author chhoppe
 * @cdk.created 2004-11-03
 * @cdk.module forcefield
 * @cdk.githash
 */
public class MMFF94PartialCharges implements IChargeCalculator {

    private final ILoggingTool LOG = LoggingToolFactory.createLoggingTool(MMFF94BasedParameterSetReader.class);

    /**
     * Constructor for the MMFF94PartialCharges object
     */
    public MMFF94PartialCharges() {}

    /**
     * Main method which assigns MMFF94 partial charges
     *
     * @param ac AtomContainer
     * @return AtomContainer with MMFF94 partial charges as atom properties
     * @throws Exception Possible Exceptions
     */
    public IAtomContainer assignMMFF94PartialCharges(IAtomContainer ac) throws Exception {
        ForceFieldConfigurator ffc = new ForceFieldConfigurator();
        ffc.setForceFieldConfigurator("mmff94", ac.getBuilder());
        ffc.assignAtomTyps(ac);
        Map<String, Object> parameterSet = ffc.getParameterSet();
        // for this calculation,
        // we need some values stored in the vector "data" in the
        // hashtable of these atomTypes:
        double charge = 0;
        double formalCharge = 0;
        double formalChargeNeigh = 0;
        double theta = 0;
        double sumOfFormalCharges = 0;
        double sumOfBondIncrements = 0;
        IAtom thisAtom = null;
        List<IAtom> neighboors;
        Object data = null;
        Object bondData = null;
        Object dataNeigh = null;
        Iterator<IAtom> atoms = ac.atoms().iterator();

        while (atoms.hasNext()) {
            thisAtom = atoms.next();
            LOG.debug("Assigning MMFF94 Charge for atom " + thisAtom.getAtomTypeName());
            data = parameterSet.get("data" + thisAtom.getAtomTypeName());
            LOG.debug("Atom data:");
            LOG.debug("WellD, Apol, Neff, DA, q, pbci, A_i, G_i");
            LOG.debug(data);
            neighboors = ac.getConnectedAtomsList(thisAtom);
            LOG.debug("Atom has  " + neighboors.size() + " neighbour(s)");
            formalCharge = thisAtom.getCharge();
            LOG.debug("Atom's formal charge is  " + formalCharge);
            theta = (Double) ((List) data).get(5);
            charge = formalCharge * (1 - (neighboors.size() * theta));
            sumOfFormalCharges = 0;
            sumOfBondIncrements = 0;
            for (IAtom neighboor : neighboors) {
                IAtom neighbour = (IAtom) neighboor;
                LOG.debug("  neighbour of " + thisAtom.getAtomTypeName() + " is " + neighbour.getAtomTypeName());
                dataNeigh = parameterSet.get("data" + neighbour.getAtomTypeName());
                LOG.debug("     dataNeigh is " + dataNeigh);
                if (parameterSet.containsKey("bond" + thisAtom.getAtomTypeName() + ";" + neighbour.getAtomTypeName())) {
                    bondData = parameterSet
                            .get("bond" + thisAtom.getAtomTypeName() + ";" + neighbour.getAtomTypeName());
                    sumOfBondIncrements -= (Double) ((List) bondData).get(4);
                } else if (parameterSet.containsKey("bond" + neighbour.getAtomTypeName() + ";"
                        + thisAtom.getAtomTypeName())) {
                    bondData = parameterSet
                            .get("bond" + neighbour.getAtomTypeName() + ";" + thisAtom.getAtomTypeName());
                    sumOfBondIncrements += (Double) ((List) bondData).get(4);
                } else {
                    // Maybe not all bonds have pbci in mmff94.prm, i.e. C-N
                    sumOfBondIncrements += (theta - (Double) ((List) dataNeigh).get(5));
                }

                dataNeigh = parameterSet.get("data" + neighbour.getID());
                formalChargeNeigh = neighbour.getCharge();
                sumOfFormalCharges += formalChargeNeigh;
            }
            charge += sumOfFormalCharges * theta;
            charge += sumOfBondIncrements;
            thisAtom.setProperty("MMFF94charge", charge);
            LOG.debug("Final MMFF94charge on : " + thisAtom.getAtomTypeName() + " is "
                    + thisAtom.getProperty("MMFF94charge") + "\n");
        }
        return ac;
    }

    @Override
    public void calculateCharges(IAtomContainer container) throws CDKException {
        try {
            assignMMFF94PartialCharges(container);
        } catch (Exception exception) {
            throw new CDKException("Could not calculate MMFF94 partial charges: " + exception.getMessage(), exception);
        }
    }
}
