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

import org.openscience.cdk.CDK;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.forcefield.mmff.Mmff;
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
 * properties ("MMFF94charge") for an AtomContainer ac, values are calculated with:
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
 * <b>Note:</b> This class delegates to {@link Mmff} and charges are also assigned
 * directly to the atom attribute {@link IAtom#getCharge()}. 
 *
 * @author mfe4
 * @author chhoppe
 * @cdk.created 2004-11-03
 * @cdk.module forcefield
 * @cdk.githash
 * @see Mmff#partialCharges(IAtomContainer) 
 */
public class MMFF94PartialCharges implements IChargeCalculator {

    public static final String MMFF_94_CHARGE = "MMFF94charge";
    private final ILoggingTool LOG = LoggingToolFactory.createLoggingTool(MMFF94BasedParameterSetReader.class);
    private final Mmff mmff = new Mmff();

    /**
     * Constructor for the MMFF94PartialCharges object
     */
    public MMFF94PartialCharges() {
    }

    /**
     * Main method which assigns MMFF94 partial charges
     *
     * @param ac AtomContainer
     * @return AtomContainer with MMFF94 partial charges as atom properties
     * @throws Exception Possible Exceptions
     */
    public IAtomContainer assignMMFF94PartialCharges(IAtomContainer ac) throws CDKException {
        if (!mmff.assignAtomTypes(ac))
            throw new CDKException("Molecule had an atom of unknown MMFF type");
        mmff.partialCharges(ac);
        mmff.clearProps(ac);
        for (IAtom atom : ac.atoms())
            atom.setProperty(MMFF_94_CHARGE, atom.getCharge());
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
