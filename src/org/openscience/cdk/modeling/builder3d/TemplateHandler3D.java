/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2006  Christian Hoppe <chhoppe@users.sf.net>
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
 */
package org.openscience.cdk.modeling.builder3d;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.isomorphism.mcss.RMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.BitSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * Helper class for ModelBuilder3D. Handles templates. This is
 * our layout solution for 3D ring systems
 *
 * @author      cho,steinbeck
 * @cdk.created 2004-09-21
 * @cdk.module  builder3d
 * @cdk.bug     1300920
 */
public class TemplateHandler3D {
    Molecule molecule;
    RingSet sssr;
    MoleculeSet templates = null;
    Vector fingerprintData = null;
    Vector ringTemplates = null;

    /**
     * The empty constructor.
     */
    public TemplateHandler3D() {
        templates = new MoleculeSet();
        fingerprintData = new Vector();
        ringTemplates = new Vector(75);
    }


    /**
     * Loads all existing templates into memory To add templates to be used in
     * Template file is a mdl file. Creates a Object Set of Molecules
     */
    public void loadTemplates() throws CDKException {
        //System.out.println("TEMPLATE START");
        IteratingMDLReader imdl;
        InputStream ins;
        BufferedReader fin;

        try {
            ins = this.getClass().getClassLoader().getResourceAsStream("org/openscience/cdk/modeling/builder3d/data/ringTemplateStructures.sdf.gz");
            fin = new BufferedReader(new InputStreamReader(new GZIPInputStream(ins)));
            imdl = new IteratingMDLReader(fin, DefaultChemObjectBuilder.getInstance());
        } catch (Exception exc1) {
            throw new CDKException("Problems loading file ringTemplateStructures.sdf.gz", exc1);
        }
        Molecule molecule;
        while (imdl.hasNext()) {
            molecule = (Molecule) imdl.next();
            templates.addMolecule(molecule);
        }
        molecule = null;
        try {
            imdl.close();
        } catch (Exception exc2) {
            System.out.println("Could not close Reader due to: " + exc2.getMessage());
        }
        //System.out.println("TEMPLATE Finger");
        try {

            ins = this.getClass().getClassLoader().getResourceAsStream("org/openscience/cdk/modeling/builder3d/data/ringTemplateFingerprints.txt.gz");
            fin = new BufferedReader(new InputStreamReader(new GZIPInputStream(ins)));
        } catch (Exception exc3) {
            System.out.println("Could not read Fingerprints from FingerprintFile due to: " + exc3.getMessage());
        }
        String s = null;
        while (true) {
            try {
                s = fin.readLine();
            } catch (Exception exc4) {
                exc4.printStackTrace();
            }

            if (s == null) {
                break;
            }
            fingerprintData.add((BitSet) getBitSetFromFile(new StringTokenizer(s, "\t ;{, }")));
        }
        //System.out.println("Fingerprints are read in:"+fingerprintData.size());
    }

    private BitSet getBitSetFromFile(StringTokenizer st) {
        BitSet bitSet = new BitSet(1024);
        for (int i = 0; i < st.countTokens(); i++) {

            try {
                bitSet.set(Integer.parseInt(st.nextToken()));
            } catch (NumberFormatException nfe) {
                // do nothing
            }
        }
        return bitSet;
    }

    /**
     * Checks if one of the loaded templates is a substructure in the given
     * Molecule. If so, it assigns the coordinates from the template to the
     * respective atoms in the Molecule.
     *
     * @param ringSystems       AtomContainer from the ring systems
     * @param NumberOfRingAtoms double
     */
    public void mapTemplates(IAtomContainer ringSystems, double NumberOfRingAtoms) throws Exception {
        //System.out.println("Map Template...START---Number of Ring Atoms:"+NumberOfRingAtoms);
        IAtomContainer template;
        QueryAtomContainer queryRingSystem = QueryAtomContainerCreator.createAnyAtomContainer(ringSystems, false);
        QueryAtomContainer query;
        BitSet ringSystemFingerprint = new Fingerprinter().getFingerprint(queryRingSystem);
        RMap map;
        org.openscience.cdk.interfaces.IAtom atom1;
        org.openscience.cdk.interfaces.IAtom atom2;
        boolean flagMaxSubstructure = false;
        for (int i = 0; i < fingerprintData.size(); i++) {
            template = templates.getMolecule(i);
            if (template.getAtomCount() != ringSystems.getAtomCount()) {
                continue;
            }
            if (Fingerprinter.isSubset(ringSystemFingerprint, (BitSet) fingerprintData.get(i))) {
                query = QueryAtomContainerCreator.createAnyAtomContainer(template, true);
                if (UniversalIsomorphismTester.isSubgraph(ringSystems, query)) {
                    List list = UniversalIsomorphismTester.getSubgraphAtomsMap(ringSystems, query);
                    if ((NumberOfRingAtoms) / list.size() == 1) {
                        flagMaxSubstructure = true;
                    }

                    for (int j = 0; j < list.size(); j++) {
                        map = (RMap) list.get(j);
                        atom1 = ringSystems.getAtom(map.getId1());
                        atom2 = template.getAtom(map.getId2());
                        if (atom1.getFlag(CDKConstants.ISINRING)) {
                            atom1.setX3d(atom2.getPoint3d().x);
                            atom1.setY3d(atom2.getPoint3d().y);
                            atom1.setZ3d(atom2.getPoint3d().z);
                        }
                    }//for j

                    if (flagMaxSubstructure) {
                        break;
                    }

                }//if subgraph
            }//if fingerprint
        }//for i
        if (!flagMaxSubstructure) {
            System.out.println("WARNING:Maybe RingTemplateError");
        }
    }

    /**
     * Gets the templateCount attribute of the TemplateHandler object
     *
     * @return The templateCount value
     */
    public int getTemplateCount() {
        return templates.getMoleculeCount();
    }


    /**
     *  Gets the templateAt attribute of the TemplateHandler object
     *
     *@param  position  Description of the Parameter
     *@return The templateAt value
     */
    public IAtomContainer getTemplateAt(int position) {
        return templates.getMolecule(position);
	}
}

