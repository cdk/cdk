/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.debug;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAdductFormula;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ICDKObject;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IFragmentAtom;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.interfaces.IPDBMonomer;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.interfaces.IPDBStructure;
import org.openscience.cdk.interfaces.IPolymer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionScheme;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStrand;

/**
 * A helper class to instantiate a {@link IChemObject} for the original CDK
 * implementation.
 *
 * @author        egonw
 * @cdk.module    datadebug
 * @cdk.githash
 */
public class DebugChemObjectBuilder implements IChemObjectBuilder {

	private static IChemObjectBuilder instance = null;
	
	private DebugChemObjectBuilder() {}

	public static IChemObjectBuilder getInstance() {
		if (instance == null) {
			instance = new DebugChemObjectBuilder();
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
    public <T extends ICDKObject>T newInstance(
	    Class<T> clazz, Object... params)
	{
        if (IFragmentAtom.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugFragmentAtom();
        } else if (IPDBAtom.class.isAssignableFrom(clazz)) {
            if (params.length == 1) {
                if (params[0] instanceof String)   return (T)new DebugPDBAtom((String)params[0]);
                if (params[0] instanceof IElement) return (T)new DebugPDBAtom((IElement)params[0]);
            } else  if (params.length == 2 && params[0] instanceof String) {
                if (params[1] instanceof Point3d)
                    return (T)new DebugPDBAtom((String)params[0], (Point3d)params[1]);
            }
        } else if (IPseudoAtom.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugPseudoAtom();
            if (params.length == 1) {
                if (params[0] instanceof String)   return (T)new DebugPseudoAtom((String)params[0]);
                if (params[0] instanceof IElement) return (T)new DebugPseudoAtom((IElement)params[0]);
            } else  if (params.length == 2 && params[0] instanceof String) {
                if (params[1] instanceof Point2d)
                    return (T)new DebugPseudoAtom((String)params[0], (Point2d)params[1]);
                if (params[1] instanceof Point3d)
                    return (T)new DebugPseudoAtom((String)params[0], (Point3d)params[1]);
            }
        } else if (IAtom.class.isAssignableFrom(clazz)) {
	        if (params.length == 0) return (T)new DebugAtom();
	        if (params.length == 1) {
	            if (params[0] instanceof String)   return (T)new DebugAtom((String)params[0]);
	            if (params[0] instanceof IElement) return (T)new DebugAtom((IElement)params[0]);
	        } else  if (params.length == 2 && params[0] instanceof String) {
	            if (params[1] instanceof Point2d)
	                return (T)new DebugAtom((String)params[0], (Point2d)params[1]);
                if (params[1] instanceof Point3d)
                    return (T)new DebugAtom((String)params[0], (Point3d)params[1]);
            }
	    } else if (IAminoAcid.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugAminoAcid();
        } else if (IChemFile.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugChemFile();
        } else if (IChemModel.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugChemModel();
        } else if (IChemSequence.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugChemSequence();
        } else if (IPDBMonomer.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugPDBMonomer();
        } else if (IMonomer.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugMonomer();
        } else if (IStrand.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugStrand();
        } else if (IPDBPolymer.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugPDBPolymer();
        } else if (IBioPolymer.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugBioPolymer();
        } else if (IReaction.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugReaction();
        } else if (IReactionScheme.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugReactionScheme();
        } else if (IReactionSet.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugReactionSet();
        } else if (IPolymer.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugPolymer();
        } else if (IRingSet.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugRingSet();
        } else if (IMoleculeSet.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugMoleculeSet();
        } else if (IAtomContainerSet.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugAtomContainerSet();
        } else if (ICrystal.class.isAssignableFrom(clazz)) {
            if (params.length == 0) {
                return (T)new DebugCrystal();
            } else if (params.length == 1 &&
                params[0] instanceof IAtomContainer) {
                return (T)new DebugCrystal((IAtomContainer)params[0]);
            }
        } else if (IMolecule.class.isAssignableFrom(clazz)) {
            if (params.length == 0) {
                return (T)new DebugMolecule();
            } else if (params.length == 1 &&
                params[0] instanceof IAtomContainer) {
                return (T)new DebugMolecule((IAtomContainer)params[0]);
            } else if (params.length == 4 &&
                    params[0] instanceof Integer &&
                    params[1] instanceof Integer &&
                    params[2] instanceof Integer &&
                    params[3] instanceof Integer) {
                return (T)new DebugMolecule(
                    (Integer)params[0], (Integer)params[1], (Integer)params[2], (Integer)params[3]
                );
            }
        } else if (IRing.class.isAssignableFrom(clazz)) {
            if (params.length == 0) {
                return (T)new DebugRing();
            } else if (params.length == 1) {
                if (params[0] instanceof IAtomContainer) {
                    return (T)new DebugRing((IAtomContainer)params[0]);
                } else if (params[0] instanceof Integer) {
                    return (T)new DebugRing((Integer)params[0]);
                } 
            } else if (params.length == 2 &&
                    params[0] instanceof Integer &&
                    params[1] instanceof String) {
                return (T)new DebugRing((Integer)params[0], (String)params[1]);
            }
        } else if (IAtomContainer.class.isAssignableFrom(clazz)) {
            if (params.length == 0) {
                return (T)new DebugAtomContainer();
            } else if (params.length == 1 &&
                params[0] instanceof IAtomContainer) {
                return (T)new DebugAtomContainer((IAtomContainer)params[0]);
            } else if (params.length == 4 &&
                    params[0] instanceof Integer &&
                    params[1] instanceof Integer &&
                    params[2] instanceof Integer &&
                    params[3] instanceof Integer) {
                return (T)new DebugAtomContainer(
                    (Integer)params[0], (Integer)params[1], (Integer)params[2], (Integer)params[3]
                );
            }
        } else if (IAtomType.class.isAssignableFrom(clazz)) {
            if (params.length == 1) {
                if (params[0] instanceof String)
                    return (T)new DebugAtomType((String)params[0]);
                if (params[0] instanceof IElement)
                    return (T)new DebugAtomType((IElement)params[0]);
            } else if (params.length == 2 &&
                    params[0] instanceof String &&
                    params[1] instanceof String) {
                return (T)new DebugAtomType(
                    (String)params[0], (String)params[1]
                );
            }
        } else if (IIsotope.class.isAssignableFrom(clazz)) {
            if (params.length == 1) {
                if (params[0] instanceof IElement) return (T)new DebugIsotope((IElement)params[0]);
                if (params[0] instanceof String) return (T)new DebugIsotope((String)params[0]);
            } else if (params.length == 2 &&
                    params[0] instanceof String &&
                    params[1] instanceof Integer) {
                return (T)new DebugIsotope(
                    (String)params[0], (Integer)params[1]
                );
            } else if (params.length == 4 &&
                    params[0] instanceof Integer &&
                    params[1] instanceof String &&
                    params[2] instanceof Double &&
                    params[3] instanceof Double) {
                return (T)new DebugIsotope(
                    (Integer)params[0], (String)params[1],
                    (Double)params[2], (Double)params[3]
                );
            } else if (params.length == 5 &&
                    params[0] instanceof Integer &&
                    params[1] instanceof String &&
                    params[2] instanceof Integer &&
                    params[3] instanceof Double &&
                    params[4] instanceof Double) {
                return (T)new DebugIsotope(
                    (Integer)params[0], (String)params[1], (Integer)params[2],
                    (Double)params[3], (Double)params[4]
                );
            }
        } else if (IElement.class.isAssignableFrom(clazz)) {
            if (params.length == 0) {
                return (T)new DebugElement();
            } else if (params.length == 1) {
                if (params[0] instanceof String)
                    return (T)new DebugElement((String)params[0]);
                if (params[0] instanceof IElement)
                    return (T)new DebugElement((IElement)params[0]);
            } else if (params.length == 2 &&
                    params[0] instanceof String &&
                    params[1] instanceof Integer) {
                return (T)new DebugElement(
                    (String)params[0], (Integer)params[1]
                );
            }
        } else if (IBond.class.isAssignableFrom(clazz)) {
            if (params.length == 0) {
                return (T)new DebugBond();
            } else if (params.length == 2 &&
                params[0] instanceof IAtom &&
                params[0] instanceof IAtom) {
                return (T)new DebugBond((IAtom)params[0], (IAtom)params[1]);
            } else if (params.length == 3 &&
                    params[0] instanceof IAtom &&
                    params[1] instanceof IAtom &&
                    params[2] instanceof IBond.Order) {
                return (T)new DebugBond(
                    (IAtom)params[0], (IAtom)params[1], (IBond.Order)params[2]
                );
            } else if (params.length == 4 &&
                    params[0] instanceof IAtom &&
                    params[1] instanceof IAtom &&
                    params[2] instanceof IBond.Order &&
                    params[3] instanceof IBond.Stereo) {
                return (T)new DebugBond(
                    (IAtom)params[0], (IAtom)params[1],
                    (IBond.Order)params[2], (IBond.Stereo)params[3]
                );
            } else if (params[params.length-1] instanceof IBond.Order) {
                // the IBond(IAtom[], IBond.Order) constructor
                boolean allIAtom = true;
                int orderIndex = params.length-1;
                List<IAtom> atoms = new ArrayList<IAtom>();
                for (int i=0; i<(orderIndex-1) && allIAtom; i++) {
                    if (!(params[i] instanceof IAtom)) {
                        allIAtom = false;
                        atoms.add((IAtom)params[i]);
                    }
                }
                if (allIAtom) {
                    return (T)new DebugBond(
                        atoms.toArray(new IAtom[atoms.size()]),
                        (IBond.Order)params[orderIndex]
                    );
                }
            } else {
                // the IBond(IAtom[]) constructor
                boolean allIAtom = true;
                for (int i=0; i<(params.length-1) && allIAtom; i++) {
                    if (!(params[i] instanceof IAtom)) allIAtom = false;
                }
                if (allIAtom) {
                    return (T)new DebugBond((IAtom[])params);
                }
            }
        } else if (ILonePair.class.isAssignableFrom(clazz)) {
            if (params.length == 0) {
                return (T)new DebugLonePair();
            } else if (params.length == 1 &&
                params[0] instanceof IAtom) {
                return (T)new DebugLonePair((IAtom)params[0]);
            }
        } else if (ISingleElectron.class.isAssignableFrom(clazz)) {
            if (params.length == 0) {
                return (T)new DebugSingleElectron();
            } else if (params.length == 1 &&
                params[0] instanceof IAtom) {
                return (T)new DebugSingleElectron((IAtom)params[0]);
            }
        } else if (IElectronContainer.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugElectronContainer();
        } else if (IMapping.class.isAssignableFrom(clazz)) {
            if (params.length == 2 &&
                params[0] instanceof IChemObject &&
                params[1] instanceof IChemObject) {
                return (T)new DebugMapping((IChemObject)params[0], (IChemObject)params[1]);
            }
        } else if (IChemObject.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new DebugChemObject();
            if (params.length == 1 &&
                params[0] instanceof IChemObject)
                return (T)new DebugChemObject((IChemObject)params[0]);
        } else if (clazz.isAssignableFrom(IAtomParity.class)) {
            if (params.length == 6 &&
                params[0] instanceof IAtom &&
                params[1] instanceof IAtom &&
                params[2] instanceof IAtom &&
                params[3] instanceof IAtom &&
                params[4] instanceof IAtom &&
                params[5] instanceof Integer)
                return (T)new DebugAtomParity(
                    (IAtom)params[0],
                    (IAtom)params[1],
                    (IAtom)params[2],
                    (IAtom)params[3],
                    (IAtom)params[4],
                    (Integer)params[5]
                );
        } else if (clazz.isAssignableFrom(IPDBStructure.class)) {
            if (params.length == 0) return (T)new DebugPDBStructure();
        } else if (clazz.isAssignableFrom(IMolecularFormula.class)) {
            if (params.length == 0) return (T)new DebugMolecularFormula();
        } else if (clazz.isAssignableFrom(IMolecularFormulaSet.class)) {
            if (params.length == 0) return (T)new DebugMolecularFormulaSet();
            if (params.length == 1 &&
                    params[0] instanceof IMolecularFormula)
                return (T)new DebugMolecularFormulaSet((IMolecularFormula)params[0]);
        } else if (clazz.isAssignableFrom(IAdductFormula.class)) {
            if (params.length == 0) return (T)new DebugAdductFormula();
            if (params.length == 1 &&
                    params[0] instanceof IMolecularFormula)
                return (T)new DebugAdductFormula((IMolecularFormula)params[0]);
        }

	    throw new IllegalArgumentException(
	        "No constructor found with the given number of parameters."
	    );
	}
}


