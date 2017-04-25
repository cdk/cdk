/*  
 * Copyright (C) 2012  Klas Jönsson <klas.joensson@gmail.com>
 *               2014  Mark B Vine (orcid:0000-0002-7794-0426)
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * This class tries to figure out the bond order of the bonds that has the flag
 * <code>SINGLE_OR_DOUBLE</code> raised (i.e. set to <code>true</code>).<br>
 * The code is written with the assumption that the properties of the atoms in
 * the molecule has configured with the help of {@link AtomContainerManipulator}.
 * This class uses the {@link SaturationChecker} internally.<br>
 * If it can't find a solution where all atoms in the molecule are saturated,
 * it gives a "best guess", i.e. the solution with most saturated atoms. If not
 * all atoms are saturated then it will be noticed as a warning in the log.
 *
 * @author Klas J&ouml;nsson
 * @author Egon Willighagen
 * @cdk.created 2012-04-13
 * @cdk.githash
 *
 * @cdk.keyword bond order
 * @cdk.module  valencycheck
 */
public class AtomTypeAwareSaturationChecker implements IValencyChecker, IDeduceBondOrderTool {

    SaturationChecker           staturationChecker;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(SaturationChecker.class);
    private IBond.Order         oldBondOrder;
    private int                 startBond;

    /**
     * Constructs an {@link AtomTypeAwareSaturationChecker} checker.
     */
    public AtomTypeAwareSaturationChecker() {
        staturationChecker = new SaturationChecker();
    }

    /**
     * This method decides the bond order on bonds that has the
     * <code>SINGLE_OR_DOUBLE</code>-flag raised.
     *
     * @param atomContainer The molecule to investigate
     * @param atomsSaturated Set to true if you want to make sure that all
     * 		atoms are saturated.
     * @throws CDKException
     */
    public void decideBondOrder(IAtomContainer atomContainer, boolean atomsSaturated) throws CDKException {
        if (atomContainer.getBondCount() == 0)
        // In this case the atom only has implicit bonds, and then it wan't be aromatic
            return;
        startBond = 0;
        int saturnatedAtoms = 0;
        int[] bestGuess = {startBond, saturnatedAtoms};
        if (atomsSaturated) {
            do {
                if (startBond == atomContainer.getBondCount()) {
                    if (bestGuess[1] == 0)
                        throw new CDKException("Can't find any solution");
                    else {
                        decideBondOrder(atomContainer, bestGuess[0]);
                        double satAtoms = ((bestGuess[1] * 1.0) / atomContainer.getAtomCount()) * 10000;
                        satAtoms = Math.round(satAtoms) / 100;
                        //						System.out.println("Staring on bond "+bestGuess[0]+
                        //								" gives "+satAtoms+"% saturated atoms.");
                        logger.warn("Can't find any solution where all atoms " + "are saturated. A best guess gives "
                                + satAtoms + "% Saturated atoms.");
                        return;
                    }
                }

                decideBondOrder(atomContainer, startBond);

                saturnatedAtoms = 0;
                for (IAtom atom : atomContainer.atoms()) {
                    if (isSaturated(atom, atomContainer)) saturnatedAtoms++;
                }

                if (bestGuess[1] < saturnatedAtoms) {
                    bestGuess[0] = startBond;
                    bestGuess[1] = saturnatedAtoms;
                }

                startBond++;
            } while (!isSaturated(atomContainer));
        } else
            decideBondOrder(atomContainer, startBond);
    }

    /**
     * This method decides the bond order on bonds that has the
     * <code>SINGLE_OR_DOUBLE</code>-flag raised.
     *
     * @param atomContainer The molecule to investigate.
     * @throws CDKException
     */
    public void decideBondOrder(IAtomContainer atomContainer) throws CDKException {
        this.decideBondOrder(atomContainer, true);
    }

    /**
     * This method decides the bond order on bonds that has the
     * <code>SINGLE_OR_DOUBLE</code>-flag raised.
     *
     * @param atomContainer The molecule to investigate
     * @param start The bond to start with
     * @throws CDKException
     */
    private void decideBondOrder(IAtomContainer atomContainer, int start) throws CDKException {
        for (int i = 0; i < atomContainer.getBondCount(); i++)
            if (atomContainer.getBond(i).getFlag(CDKConstants.SINGLE_OR_DOUBLE))
                atomContainer.getBond(i).setOrder(IBond.Order.SINGLE);

        for (int i = start; i < atomContainer.getBondCount(); i++) {
            checkBond(atomContainer, i);
        }
        /*
         * If we don't start with first bond, then we have to check the bonds
         * before the bond we started with.
         */
        if (start > 0) {
            for (int i = start - 1; i >= 0; i--) {
                checkBond(atomContainer, i);
            }
        }
    }

    /**
     * This method tries to set the bond order on the current bond.
     *
     * @param atomContainer The molecule
     * @param index The index of the current bond
     * @throws CDKException when no suitable solution can be found
     */
    private void checkBond(IAtomContainer atomContainer, int index) throws CDKException {
        IBond bond = atomContainer.getBond(index);

        if (bond != null && bond.getFlag(CDKConstants.SINGLE_OR_DOUBLE)) {
            try {
                oldBondOrder = bond.getOrder();
                bond.setOrder(IBond.Order.SINGLE);
                setMaxBondOrder(bond, atomContainer);
            } catch (CDKException e) {
                bond.setOrder(oldBondOrder);
                logger.debug(e);
            }
        }
    }

    /**
     * This method decides the highest bond order that the bond can have and set
     * it to that.
     *
     * @param bond The bond to be investigated
     * @param atomContainer The {@link IAtomContainer} that contains the bond
     * @throws CDKEXception when the bond cannot be further increased
     */
    private void setMaxBondOrder(IBond bond, IAtomContainer atomContainer) throws CDKException {
        if (bondOrderCanBeIncreased(bond, atomContainer)) {
            if (bond.getOrder() != IBond.Order.QUADRUPLE)
                bond.setOrder(BondManipulator.increaseBondOrder(bond.getOrder()));
            else
                throw new CDKException("Can't increase a quadruple bond!");
        }
    }

    /**
     * Check if the bond order can be increased. This method assumes that the
     * bond is between only two atoms.
     *
     * @param bond The bond to check
     * @param atomContainer The {@link IAtomContainer} that the bond belongs to
     * @return True if it is possibly to increase the bond order
     * @throws CDKException
     */
    public boolean bondOrderCanBeIncreased(IBond bond, IAtomContainer atomContainer) throws CDKException {
        boolean atom0isUnsaturated = false, atom1isUnsaturated = false;
        double sum;
        if (bond.getAtom(0).getBondOrderSum() == null) {
            sum = getAtomBondordersum(bond.getAtom(1), atomContainer);
        } else
            sum = bond.getAtom(0).getBondOrderSum();
        if (bondsUsed(bond.getAtom(0), atomContainer) < sum) atom0isUnsaturated = true;

        if (bond.getAtom(1).getBondOrderSum() == null) {
            sum = getAtomBondordersum(bond.getAtom(1), atomContainer);
        } else
            sum = bond.getAtom(1).getBondOrderSum();
        if (bondsUsed(bond.getAtom(1), atomContainer) < sum) atom1isUnsaturated = true;

        if (atom0isUnsaturated == atom1isUnsaturated)
            return atom0isUnsaturated;
        else {
            /*
             * If one of the atoms is saturated and the other isn't, what do we
             * do then? Look at the bonds on each side and decide from that...
             */
            int myIndex = atomContainer.indexOf(bond);
            // If it's the first bond, then just move on.
            if (myIndex == 0) return false;
            /*
             * If the previous bond is the reason it's no problem, so just move
             * on...
             */
            /*
             * TODO instead check if the atom that are in both bonds are
             * saturated...?
             */
            if (atomContainer.getBond(myIndex - 1).getOrder() == IBond.Order.DOUBLE) return false;
            /*
             * The only reason for trouble should now be that the next bond make
             * one of the atoms saturated, so lets throw an exception and
             * reveres until we can place a double bond and set it as single and
             * continue
             */
            if (isConnected(atomContainer.getBond(myIndex), atomContainer.getBond(0)))
                throw new CantDecideBondOrderException("Can't decide bond order of this bond");
            else {
                return false;
            }
        }
    }

    /**
     * This method is used if, by some reason, the bond order sum is not set
     * for an atom.
     *
     * @param atom The atom in question
     * @param mol The molecule that the atom belongs to
     * @return The bond order sum
     * @throws CDKException
     */
    private double getAtomBondordersum(IAtom atom, IAtomContainer mol) throws CDKException {
        double sum = 0;

        for (IBond bond : mol.bonds())
            if (bond.contains(atom)) sum += BondManipulator.destroyBondOrder(bond.getOrder());

        return sum;
    }

    /**
     * Look if any atoms in <code>bond1</code> also are in <code>bond2</code>
     * and if so it conceder the bonds connected.
     * @param bond1 The first bond
     * @param bond2 The other bond
     * @return True if any of  the atoms in <code>bond1</code> also are in
     * 		<code>bond2</code>
     */
    private boolean isConnected(IBond bond1, IBond bond2) {
        for (IAtom atom : bond1.atoms())
            if (bond2.contains(atom)) return true;
        return false;
    }

    /**
     * This method calculates the number of bonds that an <code>IAtom</code>
     * can have.
     *
     * @param atom The <code>IAtom</code> to be investigated
     * @return The max number of bonds the <code>IAtom</code> can have
     * @throws CDKException when the atom's valency is not set
     */
    public double getMaxNoOfBonds(IAtom atom) throws CDKException {
        double noValenceElectrons = atom.getValency() == CDKConstants.UNSET ? -1 : atom.getValency();
        if (noValenceElectrons == -1) {
            throw new CDKException("Atom property not set: Valency");
        }
        // This will probably only work for group 13-18, and not for helium...
        return 8 - noValenceElectrons;
    }

    /**
     * A small help method that count how many bonds an atom has, regarding
     * bonds due to its charge and to implicit hydrogens.
     *
     * @param atom The atom to check
     * @param atomContainer The atomContainer containing the atom
     * @return The number of bonds that the atom has
     * @throws CDKException
     */
    private double bondsUsed(IAtom atom, IAtomContainer atomContainer) throws CDKException {
        int bondsToAtom = 0;
        for (IBond bond : atomContainer.bonds())
            if (bond.contains(atom)) bondsToAtom += BondManipulator.destroyBondOrder(bond.getOrder());
        int implicitHydrogens;
        if (atom.getImplicitHydrogenCount() == CDKConstants.UNSET || atom.getImplicitHydrogenCount() == null) {
            // Will probably only work with group 13-18, and not for helium...
            if (atom.getValency() == CDKConstants.UNSET || atom.getValency() == null)
                throw new CDKException("Atom " + atom.getAtomTypeName() + " has not got the valency set.");
            if (atom.getFormalNeighbourCount() == CDKConstants.UNSET || atom.getFormalNeighbourCount() == null)
                throw new CDKException("Atom " + atom.getAtomTypeName()
                        + " has not got the formal neighbour count set.");
            implicitHydrogens = (8 - atom.getValency()) - atom.getFormalNeighbourCount();
            String warningMessage = "Number of implicite hydrogens not set for atom " + atom.getAtomTypeName()
                    + ". Estimated it to: " + implicitHydrogens;
            logger.warn(warningMessage);
        } else
            implicitHydrogens = atom.getImplicitHydrogenCount();

        double charge;
        if (atom.getCharge() == CDKConstants.UNSET)
            if (atom.getFormalCharge() == CDKConstants.UNSET) {
                charge = 0;
                String warningMessage = "Neither charge nor formal charge is set for atom " + atom.getAtomTypeName()
                        + ". Estimate it to: 0";
                logger.warn(warningMessage);
            } else
                charge = atom.getFormalCharge();
        else
            charge = atom.getCharge();
        return bondsToAtom - charge + implicitHydrogens;
    }

    /** {@inheritDoc} */
    @Override
    public void saturate(IAtomContainer container) throws CDKException {
        staturationChecker.saturate(container);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSaturated(IAtomContainer container) throws CDKException {
        return staturationChecker.isSaturated(container);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSaturated(IAtom atom, IAtomContainer container) throws CDKException {
        return staturationChecker.isSaturated(atom, container);
    }

    /**
     * This is a private exception thrown when it detects an error and needs to
     * start to back-trace.
     *
     * @author Klas J&ouml;nsson
     *
     */
    @SuppressWarnings("serial")
    private class CantDecideBondOrderException extends CDKException {

        /**
         * Creates a new {@link CantDecideBondOrderException} with a given message.
         *
         * @param message Explanation about why the decision could not be made.
         */
        public CantDecideBondOrderException(String message) {
            super(message);
        }

    }

}
