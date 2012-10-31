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
package org.openscience.cdk;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.formula.AdductFormula;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.formula.MolecularFormulaSet;
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
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.protein.data.PDBMonomer;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.protein.data.PDBStructure;
import org.openscience.cdk.stereo.TetrahedralChirality;

/**
 * A helper class to instantiate a {@link ICDKObject} for the original CDK
 * implementation.
 *
 * @author        egonw
 * @cdk.module    data
 * @cdk.githash
 */
public class DefaultChemObjectBuilder implements IChemObjectBuilder {

	private static IChemObjectBuilder instance = null;
	
	private DefaultChemObjectBuilder() {}

	public static IChemObjectBuilder getInstance() {
		if (instance == null) {
			instance = new DefaultChemObjectBuilder();
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
    public <T extends ICDKObject>T newInstance(
	    Class<T> clazz, Object... params)
	{
        if (IElement.class.isAssignableFrom(clazz)) {
            return newElementInstance(clazz, params);
        } else if (IElectronContainer.class.isAssignableFrom(clazz)) {
            return newElectronContainerInstance(clazz, params);
        } else if (IAminoAcid.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new AminoAcid();
        } else if (IChemFile.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new ChemFile();
        } else if (IChemModel.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new ChemModel();
        } else if (IChemSequence.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new ChemSequence();
        } else if (IPDBMonomer.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new PDBMonomer();
        } else if (IMonomer.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new Monomer();
        } else if (IStrand.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new Strand();
        } else if (IPDBPolymer.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new PDBPolymer();
        } else if (IBioPolymer.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new BioPolymer();
        } else if (IReaction.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new Reaction();
        } else if (IReactionScheme.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new ReactionScheme();
        } else if (IReactionSet.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new ReactionSet();
        } else if (IPolymer.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new Polymer();
        } else if (IRingSet.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new RingSet();
        } else if (IMoleculeSet.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new MoleculeSet();
        } else if (IAtomContainerSet.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new AtomContainerSet();
        } else if (IAtomContainer.class.isAssignableFrom(clazz)) {
            return newAtomContainerInstance(clazz, params);
        } else if (IMapping.class.isAssignableFrom(clazz)) {
            if (params.length == 2 &&
                params[0] instanceof IChemObject &&
                params[1] instanceof IChemObject) {
                return (T)new Mapping((IChemObject)params[0], (IChemObject)params[1]);
            }
        } else if (IChemObject.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T)new ChemObject();
            if (params.length == 1 &&
                params[0] instanceof IChemObject)
                return (T)new ChemObject((IChemObject)params[0]);
        } else if (clazz.isAssignableFrom(IAtomParity.class)) {
            if (params.length == 6 &&
                params[0] instanceof IAtom &&
                params[1] instanceof IAtom &&
                params[2] instanceof IAtom &&
                params[3] instanceof IAtom &&
                params[4] instanceof IAtom &&
                params[5] instanceof Integer)
                return (T)new AtomParity(
                    (IAtom)params[0],
                    (IAtom)params[1],
                    (IAtom)params[2],
                    (IAtom)params[3],
                    (IAtom)params[4],
                    (Integer)params[5]
                );
        } else if (clazz.isAssignableFrom(IPDBStructure.class)) {
            if (params.length == 0) return (T)new PDBStructure();
        } else if (clazz.isAssignableFrom(IMolecularFormula.class)) {
            if (params.length == 0) return (T)new MolecularFormula();
        } else if (clazz.isAssignableFrom(ITetrahedralChirality.class)) {
            if (params.length == 3 &&
                params[0] instanceof IAtom &&
                params[1] instanceof IAtom[] &&
                params[2] instanceof Stereo) {
                TetrahedralChirality chirality = new TetrahedralChirality(
                    (IAtom)params[0], (IAtom[])params[1], (Stereo)params[2]
                );
                chirality.setBuilder(this);
                return (T)chirality;
            }
        } else if (clazz.isAssignableFrom(IMolecularFormulaSet.class)) {
            if (params.length == 0) return (T)new MolecularFormulaSet();
            if (params.length == 1 &&
                params[0] instanceof IMolecularFormula)
                return (T)new MolecularFormulaSet((IMolecularFormula)params[0]);
        } else if (clazz.isAssignableFrom(IAdductFormula.class)) {
            if (params.length == 0) return (T)new AdductFormula();
            if (params.length == 1 &&
                params[0] instanceof IMolecularFormula)
                return (T)new AdductFormula((IMolecularFormula)params[0]);
        }

	    throw new IllegalArgumentException(getNoConstructorFoundMessage(clazz));
	}

	private String getNoConstructorFoundMessage(Class clazz) {
	    StringBuffer buffer = new StringBuffer();
	    String className = clazz.getName().substring(32);
	    buffer.append("No constructor found for ");
	    buffer.append(className);
	    buffer.append(" with the given number of parameters.");

	    // try loading the implementation
	    try {
            Class impl = this.getClass().getClassLoader().loadClass(
                "org.openscience.cdk." + className
            );
            buffer.append(" Candidates are: ");
            Constructor[] constructors = impl.getConstructors();
            for (int i=0; i<constructors.length; i++) {
                buffer.append(className).append('(');
                Class[] params = constructors[i].getParameterTypes();
                for (int j=0; j<params.length; j++) {
                    buffer.append(params[j].getName().substring(
                        params[j].getName().lastIndexOf('.') + 1
                    ));
                    if ((j+1)<params.length) buffer.append(", ");
                }
                buffer.append(')');
                if ((i+1)<constructors.length) buffer.append(", ");
            }
        } catch (ClassNotFoundException e) {
            // ok, then we do without suggestions
        }
        return buffer.toString();
	}
	
    @SuppressWarnings("unchecked")
    private <T extends ICDKObject>T newAtomContainerInstance(
            Class<T> clazz, Object... params)
    {
        if (ICrystal.class.isAssignableFrom(clazz)) {
            if (params.length == 0) {
                return (T)new Crystal();
            } else if (params.length == 1 &&
                params[0] instanceof IAtomContainer) {
                return (T)new Crystal((IAtomContainer)params[0]);
            }
        } else if (IMolecule.class.isAssignableFrom(clazz)) {
            if (params.length == 0) {
                return (T)new Molecule();
            } else if (params.length == 1 &&
                params[0] instanceof IAtomContainer) {
                return (T)new Molecule((IAtomContainer)params[0]);
            } else if (params.length == 4 &&
                    params[0] instanceof Integer &&
                    params[1] instanceof Integer &&
                    params[2] instanceof Integer &&
                    params[3] instanceof Integer) {
                return (T)new Molecule(
                    (Integer)params[0], (Integer)params[1], (Integer)params[2], (Integer)params[3]
                );
            }
        } else if (IRing.class.isAssignableFrom(clazz)) {
            if (params.length == 0) {
                return (T)new Ring();
            } else if (params.length == 1) {
                if (params[0] instanceof IAtomContainer) {
                    return (T)new Ring((IAtomContainer)params[0]);
                } else if (params[0] instanceof Integer) {
                    return (T)new Ring((Integer)params[0]);
                } 
            } else if (params.length == 2 &&
                    params[0] instanceof Integer &&
                    params[1] instanceof String) {
                return (T)new Ring((Integer)params[0], (String)params[1]);
            }
        } else {
            if (params.length == 0) {
                return (T)new AtomContainer();
            } else if (params.length == 1 &&
                params[0] instanceof IAtomContainer) {
                return (T)new AtomContainer((IAtomContainer)params[0]);
            } else if (params.length == 4 &&
                    params[0] instanceof Integer &&
                    params[1] instanceof Integer &&
                    params[2] instanceof Integer &&
                    params[3] instanceof Integer) {
                return (T)new AtomContainer(
                    (Integer)params[0], (Integer)params[1], (Integer)params[2], (Integer)params[3]
                );
            }
        }

        throw new IllegalArgumentException(getNoConstructorFoundMessage(clazz));
    }

	@SuppressWarnings("unchecked")
    private <T extends ICDKObject>T newElementInstance(
	        Class<T> clazz, Object... params)
	{
	    if (IFragmentAtom.class.isAssignableFrom(clazz)) {
	        if (params.length == 0) return (T)new FragmentAtom();
	    } else if (IPDBAtom.class.isAssignableFrom(clazz)) {
	        if (params.length == 1) {
	            if (params[0] instanceof String)   return (T)new PDBAtom((String)params[0]);
	            if (params[0] instanceof IElement) return (T)new PDBAtom((IElement)params[0]);
	        } else  if (params.length == 2 &&
	                params[0] instanceof String &&
	                params[1] instanceof Point3d) {
	            return (T)new PDBAtom((String)params[0], (Point3d)params[1]);
	        }
	    } else if (IPseudoAtom.class.isAssignableFrom(clazz)) {
	        if (params.length == 0) return (T)new PseudoAtom();
	        if (params.length == 1) {
	            if (params[0] instanceof String)   return (T)new PseudoAtom((String)params[0]);
	            if (params[0] instanceof IElement) return (T)new PseudoAtom((IElement)params[0]);
	        } else  if (params.length == 2 && params[0] instanceof String) {
	            if (params[1] instanceof Point2d)
	                return (T)new PseudoAtom((String)params[0], (Point2d)params[1]);
	            if (params[1] instanceof Point3d)
	                return (T)new PseudoAtom((String)params[0], (Point3d)params[1]);
	        }
	    } else if (IAtom.class.isAssignableFrom(clazz)) {
	        if (params.length == 0) return (T)new Atom();
	        if (params.length == 1) {
	            if (params[0] instanceof String)   return (T)new Atom((String)params[0]);
	            if (params[0] instanceof IElement) return (T)new Atom((IElement)params[0]);
	        } else  if (params.length == 2 && params[0] instanceof String) {
	            if (params[1] instanceof Point2d)
	                return (T)new Atom((String)params[0], (Point2d)params[1]);
	            if (params[1] instanceof Point3d)
	                return (T)new Atom((String)params[0], (Point3d)params[1]);
	        }
        } else if (IAtomType.class.isAssignableFrom(clazz)) {
            if (params.length == 1) {
                if (params[0] instanceof String)
                    return (T)new AtomType((String)params[0]);
                if (params[0] instanceof IElement)
                    return (T)new AtomType((IElement)params[0]);
            } else if (params.length == 2 &&
                    params[0] instanceof String &&
                    params[1] instanceof String) {
                return (T)new AtomType(
                    (String)params[0], (String)params[1]
                );
            }
        } else if (IIsotope.class.isAssignableFrom(clazz)) {
            if (params.length == 1) {
                if (params[0] instanceof IElement) return (T)new Isotope((IElement)params[0]);
                if (params[0] instanceof String) return (T)new Isotope((String)params[0]);
            } else if (params.length == 2 &&
                    params[0] instanceof String &&
                    params[1] instanceof Integer) {
                return (T)new Isotope(
                    (String)params[0], (Integer)params[1]
                );
            } else if (params.length == 4 &&
                    params[0] instanceof Integer &&
                    params[1] instanceof String &&
                    params[2] instanceof Double &&
                    params[3] instanceof Double) {
                return (T)new Isotope(
                    (Integer)params[0], (String)params[1],
                    (Double)params[2], (Double)params[3]
                );
            } else if (params.length == 5 &&
                    params[0] instanceof Integer &&
                    params[1] instanceof String &&
                    params[2] instanceof Integer &&
                    params[3] instanceof Double &&
                    params[4] instanceof Double) {
                return (T)new Isotope(
                    (Integer)params[0], (String)params[1], (Integer)params[2],
                    (Double)params[3], (Double)params[4]
                );
            }
        } else {
            if (params.length == 0) {
                return (T)new Element();
            } else if (params.length == 1) {
                if (params[0] instanceof String)
                    return (T)new Element((String)params[0]);
                if (params[0] instanceof IElement)
                    return (T)new Element((IElement)params[0]);
            } else if (params.length == 2 &&
                    params[0] instanceof String &&
                    params[1] instanceof Integer) {
                return (T)new Element(
                    (String)params[0], (Integer)params[1]
                );
            }
	    }

	    throw new IllegalArgumentException(getNoConstructorFoundMessage(clazz));
	}
	
	@SuppressWarnings("unchecked")
    private <T extends ICDKObject>T newElectronContainerInstance(
            Class<T> clazz, Object... params)
    {
	    if (IBond.class.isAssignableFrom(clazz)) {
	        if (params.length == 0) {
	            return (T)new Bond();
	        } else if (params.length == 2 &&
	                params[0] instanceof IAtom &&
	                params[1] instanceof IAtom) {
	            return (T)new Bond((IAtom)params[0], (IAtom)params[1]);
	        } else if (params.length == 3 &&
	                params[0] instanceof IAtom &&
	                params[1] instanceof IAtom &&
	                params[2] instanceof IBond.Order) {
	            return (T)new Bond(
	                (IAtom)params[0], (IAtom)params[1], (IBond.Order)params[2]
	            );
	        } else if (params.length == 4 &&
	                params[0] instanceof IAtom &&
	                params[1] instanceof IAtom &&
	                params[2] instanceof IBond.Order &&
	                params[3] instanceof IBond.Stereo) {
	            return (T)new Bond(
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
	                return (T)new Bond(
	                    atoms.toArray(new IAtom[atoms.size()]),
	                    (IBond.Order)params[orderIndex]
	                );
	            }
	        } else {
	            // the IBond(IAtom[]) constructor
	            boolean allIAtom = true;
	            for (int i=0; i<params.length && allIAtom; i++) {
	            	System.out.println(params[i]);
	                if (!(params[i] instanceof IAtom)) allIAtom = false;
	            }
	            if (allIAtom) {
	                return (T)new Bond((IAtom[])params);
	            }
	        }
	    } else if (ILonePair.class.isAssignableFrom(clazz)) {
	        if (params.length == 0) {
	            return (T)new LonePair();
	        } else if (params.length == 1 &&
	                params[0] instanceof IAtom) {
	            return (T)new LonePair((IAtom)params[0]);
	        }
	    } else if (ISingleElectron.class.isAssignableFrom(clazz)) {
	        if (params.length == 0) {
	            return (T)new SingleElectron();
	        } else if (params.length == 1 &&
	                params[0] instanceof IAtom) {
	            return (T)new SingleElectron((IAtom)params[0]);
	        }
	    } else {
	        if (params.length == 0) return (T)new ElectronContainer();
	    }

	    throw new IllegalArgumentException(getNoConstructorFoundMessage(clazz));
    }
	
}


