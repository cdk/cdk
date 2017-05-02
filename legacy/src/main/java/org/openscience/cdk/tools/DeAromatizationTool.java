/* Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.tools;

import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Element;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * Methods that takes a ring of which all bonds are aromatic, and assigns single
 * and double bonds. It does this in a non-general way by looking at the ring
 * size and take everything as a special case.
 *
 *
 * @author         seb
 * @cdk.created    13. April 2005
 * @cdk.module     extra
 * @cdk.githash
 * @cdk.keyword    aromatic ring, bond order adjustment
 * @deprecated the newer {@link org.openscience.cdk.aromaticity.Kekulization} provides a faster, more generic and
 *             comprehensive algorithm.
 */
@Deprecated
public class DeAromatizationTool {

    /**
     * Methods that takes a ring of which all bonds are aromatic, and assigns single
     * and double bonds. It does this in a non-general way by looking at the ring
     * size and take everything as a special case.
     *
     * @param ring Ring to dearomatize
     * @return  False if it could not convert the aromatic ring bond into single and double bonds
     */
    public static boolean deAromatize(IRing ring) {
        boolean allaromatic = true;
        for (int i = 0; i < ring.getBondCount(); i++) {
            if (!ring.getBond(i).getFlag(CDKConstants.ISAROMATIC)) allaromatic = false;
        }
        if (!allaromatic) return false;
        for (int i = 0; i < ring.getBondCount(); i++) {
            if (ring.getBond(i).getFlag(CDKConstants.ISAROMATIC)) ring.getBond(i).setOrder(IBond.Order.SINGLE);
        }
        boolean result = false;
        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(ring);
        //		Map elementCounts = new MFAnalyser(ring).getFormulaHashtable();
        if (ring.getRingSize() == 6) {
            if (MolecularFormulaManipulator.getElementCount(formula, new Element("C")) == 6) {
                result = DeAromatizationTool.deAromatizeBenzene(ring);
            } else if (MolecularFormulaManipulator.getElementCount(formula, new Element("C")) == 5
                    && MolecularFormulaManipulator.getElementCount(formula, new Element("N")) == 1) {
                result = DeAromatizationTool.deAromatizePyridine(ring);
            }
        }
        if (ring.getRingSize() == 5) {
            if (MolecularFormulaManipulator.getElementCount(formula, new Element("C")) == 4
                    && MolecularFormulaManipulator.getElementCount(formula, new Element("N")) == 1) {
                result = deAromatizePyrolle(ring);
            }
        }
        return result;
    }

    private static boolean deAromatizePyridine(IRing ring) {
        return deAromatizeBenzene(ring); // same task to do
    }

    private static boolean deAromatizePyrolle(IRing ring) {
        if (ring.getBondCount() != 5) return false;
        for (int i = 0; i < ring.getAtomCount(); i++) {
            IAtom atom = ring.getAtom(i);
            if (atom.getSymbol().equals("N")) {
                int done = 0;
                IBond bond = null;
                int count = 0;
                while (done != 2) {
                    bond = getNextBond(atom, bond, ring);
                    if (bond.getBegin() == atom)
                        atom = bond.getEnd();
                    else
                        atom = bond.getBegin();
                    count++;
                    if (count % 2 == 0) {
                        bond.setOrder(IBond.Order.DOUBLE);
                        done++;
                    }
                }
                break;
            }
        }
        return true;
    }

    private static IBond getNextBond(IAtom atom, IBond bond, IRing ring) {
        List<IBond> bonds = ring.getConnectedBondsList(atom);
        for (int i = 0; i < bonds.size(); i++)
            if (bonds.get(i) != bond) return (IBond) bonds.get(i);
        return null;
    }

    private static boolean deAromatizeBenzene(IRing ring) {
        if (ring.getBondCount() != 6) return false;
        int counter = 0;
        for (IBond bond : ring.bonds()) {
            if (counter % 2 == 0) {
                bond.setOrder(Order.SINGLE);
            } else {
                bond.setOrder(Order.DOUBLE);
            }
            counter++;
        }
        return true;
    }

    public static void Dearomatize(IAtomContainer molecule)
    {
        SSSRFinder finder = new SSSRFinder(molecule);
        IRingSet sssr = finder.findEssentialRings();
        List<IRingSet> rings = RingPartitioner.partitionRings(sssr);
        for (IRingSet ring : rings)
        {
            if (Dearomatize666Rings(ring)) continue;
            if (Dearomatize566Rings(ring)) continue;
            if (Dearomatize66Rings(ring)) continue;
            if (Dearomatize56Rings(ring)) continue;
            if (Dearomatize6Ring(ring)) continue;   
            if (Dearomatize5Ring(ring)) continue;
        }
    }

    public static boolean Dearomatize666Rings(IRingSet rings)
    {
        if (rings.getAtomContainerCount()!=3) 
        {
            // Stop if not three rings fused together.
            return false;
        }
        IAtomContainer ringA = rings.getAtomContainer(0);
        IAtomContainer ringB = rings.getAtomContainer(1);
        IAtomContainer ringC = rings.getAtomContainer(2);
        
        // Make ringB the middle ring.
        if (isMiddleRing(ringA, ringB, ringC))
        {
            IAtomContainer temp = ringB;
            ringB = ringA;
            ringA = temp;
        }
        if (isMiddleRing(ringC, ringA, ringB))
        {
            IAtomContainer temp = ringB;
            ringB = ringC;
            ringC = temp;
        }
        
        int maxAtomsRingA = ringA.getAtomCount();
        int maxAtomsRingB = ringB.getAtomCount();
        int maxAtomsRingC = ringC.getAtomCount();
        if (maxAtomsRingA!=6 || maxAtomsRingB!=6 || maxAtomsRingC!=6)
        {
            // Stop if not a 6-membered ring fused to 6-membered ring fused to another 6-membered ring.
            return false;
        }
                
        if (!isAllAtomsAromatic(ringA) || !isAllAtomsAromatic(ringB) || !isAllAtomsAromatic(ringC))
        {
            // Not all atoms of the three rings are aromatic.
            return false;
        }
        
        InvalidateBonds(ringA);
        InvalidateBonds(ringB);
        InvalidateBonds(ringC);
        
        // Change first 6-membered ring.
        DearomatizeRing(ringA, null, null);
        
        // Change second 6-membered ring.
        DearomatizeRing(ringB, null, null);
        
        // Change third 6-membered ring.
        DearomatizeRing(ringC, null, null);

        return true;
    }
    
    public static boolean Dearomatize566Rings(IRingSet rings)
    {
        if (rings.getAtomContainerCount()!=3) 
        {
            // Stop if not three rings fused together.
            return false;
        }
        IAtomContainer ringA = rings.getAtomContainer(0);
        IAtomContainer ringB = rings.getAtomContainer(1);
        IAtomContainer ringC = rings.getAtomContainer(2);
        
        if (ringA.getAtomCount() > ringB.getAtomCount())
        {
            // Ensure ringA is smaller than ringB and ringC
            ringA = rings.getAtomContainer(1);
            ringB = rings.getAtomContainer(0);
        }
        else if (ringA.getAtomCount() > ringC.getAtomCount())
        {
            // Ensure ringA is smaller than ringB and ringC
            ringA = rings.getAtomContainer(2);
            ringC = rings.getAtomContainer(0);
        }
        
        // Make ringB the middle ring if ringA is not the middle ring.
        if (isMiddleRing(ringC, ringA, ringB))
        {
            IAtomContainer temp = ringB;
            ringB = ringC;
            ringC = temp;
        }
        
        int maxAtomsRingA = ringA.getAtomCount();
        int maxAtomsRingB = ringB.getAtomCount();
        int maxAtomsRingC = ringC.getAtomCount();
        if (maxAtomsRingA!=5 || maxAtomsRingB!=6 || maxAtomsRingC!=6)
        {
            // Stop if not a 5-membered ring fused to 6-membered ring fused to another 6-membered ring.
            return false;
        }
                
        if (!isAllAtomsAromatic(ringA) || !isAllAtomsAromatic(ringB) || !isAllAtomsAromatic(ringC))
        {
            // Not all atoms of the three rings are aromatic.
            return false;
        }    
       
        InvalidateBonds(ringA);
        InvalidateBonds(ringB);
        InvalidateBonds(ringC);
        
        // Find nH, o or s in 5-membered ring if any.
        IAtom startAtom = ringA.getAtom(0);
        for (int a=0; a<maxAtomsRingA; ++a)
        {
            IAtom atom = ringA.getAtom(a);
            if ((atom.getAtomicNumber()==7 && atom.getImplicitHydrogenCount()>=1) ||    // nH
                atom.getAtomicNumber()==8 ||    // o
                atom.getAtomicNumber()==16)     // s
            {
                startAtom = atom;
                break;
            }
        }
        
        // Change 5-membered ring.
        IBond startBond = ringA.getConnectedBondsList(startAtom).get(0);
        DearomatizeRing(ringA, startAtom, startBond);
        
        // Change second 6-membered ring.
        DearomatizeRing(ringB, null, null);
        
        // Change third 6-membered ring.
        DearomatizeRing(ringC, null, null);

        return true;
    }

    public static boolean Dearomatize66Rings(IRingSet rings)
    {
        if (rings.getAtomContainerCount()!=2) 
        {
            // Stop if not two rings fused together.
            return false;
        }
        IAtomContainer ringA = rings.getAtomContainer(0);
        IAtomContainer ringB = rings.getAtomContainer(1);        
        int maxAtomsRingA = ringA.getAtomCount();
        int maxAtomsRingB = ringB.getAtomCount();
        if (maxAtomsRingA!=6 || maxAtomsRingB!=6)
        {
            // Stop if not a 6-membered ring fused to 6-membered ring.
            return false;
        }
                
        if (!isAllAtomsAromatic(ringA) || !isAllAtomsAromatic(ringB))
        {
            // Not all atoms of the two rings are aromatic.
            return false;
        }
        
        InvalidateBonds(ringA);
        InvalidateBonds(ringB);
        
        // Change first 6-membered ring.
        DearomatizeRing(ringA, null, null);
        
        // Change second 6-membered ring.
        DearomatizeRing(ringB, null, null);

        return true;
    }
        
    public static boolean Dearomatize56Rings(IRingSet rings)
    {
        if (rings.getAtomContainerCount()!=2) 
        {
            // Stop if not two rings fused together.
            return false;
        }
        IAtomContainer ringA = rings.getAtomContainer(0);
        IAtomContainer ringB = rings.getAtomContainer(1);
        if (ringA.getAtomCount() > ringB.getAtomCount())
        {
            // Ensure ringA is smaller than ringB
            ringA = rings.getAtomContainer(1);
            ringB = rings.getAtomContainer(0);
        }
        int maxAtomsRingA = ringA.getAtomCount();
        int maxAtomsRingB = ringB.getAtomCount();
        if (maxAtomsRingA!=5 || maxAtomsRingB!=6)
        {
            // Stop if not a 5-membered ring fused to 6-membered ring.
            return false;
        }
        
        if (!isAllAtomsAromatic(ringA) || !isAllAtomsAromatic(ringB))
        {
            // Not all atoms of the two rings are aromatic.
            return false;
        }
        
        InvalidateBonds(ringA);
        InvalidateBonds(ringB);
        
        // Find nH, o or s in 5-membered ring if any.
        IAtom startAtom = ringA.getAtom(0);
        for (int a=0; a<maxAtomsRingA; ++a)
        {
            IAtom atom = ringA.getAtom(a);
            if ((atom.getAtomicNumber()==7 && atom.getImplicitHydrogenCount()>=1) ||    // nH
                atom.getAtomicNumber()==8 ||    // o
                atom.getAtomicNumber()==16)     // s
            {
                startAtom = atom;
                break;
            }
        }
        
        // Change 5-membered ring.
        IBond startBond = ringA.getConnectedBondsList(startAtom).get(0);
        DearomatizeRing(ringA, startAtom, startBond);
        
        // Change 6-membered ring.
        DearomatizeRing(ringB, null, null);

        return true;
    }
    
    public static boolean Dearomatize6Ring(IRingSet rings)
    {
        if (rings.getAtomContainerCount()!=1) 
        {
            // Stop if not one ring.
            return false;
        }
        IAtomContainer ringA = rings.getAtomContainer(0);       
        int maxAtomsRingA = ringA.getAtomCount();
        if (maxAtomsRingA!=6)
        {
            // Stop if not a 6-membered ring.
            return false;
        }
        
        if (!isAllAtomsAromatic(ringA))
        {
            // Not all atoms of the ring are aromatic.
            return false;
        }
        
        InvalidateBonds(ringA);
               
        // Change 6-membered ring.
        IAtom startAtom = ringA.getAtom(0);       
        IBond startBond = ringA.getConnectedBondsList(startAtom).get(0);
        DearomatizeRing(ringA, startAtom, startBond);

        return true;
    }
    
    public static boolean Dearomatize5Ring(IRingSet rings)
    {
        if (rings.getAtomContainerCount()!=1) 
        {
            // Stop if not one ring.
            return false;
        }
        IAtomContainer ringA = rings.getAtomContainer(0);       
        int maxAtomsRingA = ringA.getAtomCount();
        if (maxAtomsRingA!=5)
        {
            // Stop if not a 5-membered ring.
            return false;
        }
        
        if (!isAllAtomsAromatic(ringA))
        {
            // Not all atoms of the ring are aromatic.
            return false;
        }
        
        InvalidateBonds(ringA);
        
        // Find nH, o or s in 5-membered ring if any.
        IAtom startAtom = ringA.getAtom(0);
        for (int a=0; a<maxAtomsRingA; ++a)
        {
            IAtom atom = ringA.getAtom(a);
            if ((atom.getAtomicNumber()==7 && atom.getImplicitHydrogenCount()>=1) ||    // nH
                atom.getAtomicNumber()==8 ||    // o
                atom.getAtomicNumber()==16)     // s
            {
                startAtom = atom;
                break;
            }
        }
        
        // Change 5-membered ring.
        IBond startBond = ringA.getConnectedBondsList(startAtom).get(0);
        DearomatizeRing(ringA, startAtom, startBond);

        return true;
    }
    
    private static boolean isAllAtomsAromatic(IAtomContainer ring)
    {
        for (IAtom atom : ring.atoms())
        {
            if (!atom.getFlag(CDKConstants.ISAROMATIC))
            {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isMiddleRing(IAtomContainer ring, IAtomContainer ringA, IAtomContainer ringB)
    {
        boolean hasAtomInRingA = false;
        boolean hasAtomInRingB = false;
        for (IAtom atom : ring.atoms())
        {
            if (!hasAtomInRingA && ringA.contains(atom)) hasAtomInRingA = true;
            else if (!hasAtomInRingB && ringB.contains(atom)) hasAtomInRingB = true;
        }
        return hasAtomInRingA && hasAtomInRingB;
    }
        
    private static void RemoveAromaticFlag(IAtomContainer ring)
    {
        for (IAtom atom : ring.atoms())
        {
            atom.setFlag(CDKConstants.ISAROMATIC, false);
        }
        for (IBond bond : ring.bonds())
        {
            bond.setFlag(CDKConstants.ISAROMATIC, false);
        }
    }
    
    private static void InvalidateBonds(IAtomContainer ring)
    {
        for (IBond bond : ring.bonds())
        {
            bond.setOrder(IBond.Order.QUADRUPLE);
        }
    }
    
    private static void DearomatizeRing(IAtomContainer ring, IAtom startAtom, IBond startBond)
    {
        IAtom curAtom = startAtom;
        IBond curBond = startBond;
        int maxBonds = ring.getBondCount();
        
        if (curAtom==null)
        {
            curAtom = ring.getAtom(0);        
        }
        if (curBond==null)
        {
            curBond = ring.getConnectedBondsList(curAtom).get(0);
        }
        
        boolean nextIsSingle = true;
        boolean nextNextIsSingle = false;
        for (int i=0; i<maxBonds; ++i)
        {
            IBond bond = ring.getBond(i);
            if (bond.getOrder()!=IBond.Order.QUADRUPLE)
            {
                curBond = bond;
                curAtom = bond.getBegin();
                if (bond.getOrder()==IBond.Order.SINGLE)
                {
                    nextIsSingle = true;
                    nextNextIsSingle = true;
                }
                else
                {
                    nextIsSingle = false;
                    nextNextIsSingle = true;
                }
            }
        }
        
        for (int i=0; i<maxBonds; ++i)
        {
            if (nextIsSingle || (i==1 && nextNextIsSingle))
            {
                curBond.setOrder(IBond.Order.SINGLE);
                nextIsSingle = false;
            }
            else
            {
                curBond.setOrder(IBond.Order.DOUBLE);
                nextIsSingle = true;
            }
            curAtom = curBond.getOther(curAtom);
            List<IBond> bonds = ring.getConnectedBondsList(curAtom);
            for (IBond bond : bonds)
            {
                if (bond!=curBond)
                {
                    curBond = bond;
                    break;
                }
            }
        }
        
        RemoveAromaticFlag(ring);
    }

}
