/*  Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
 *                     2011  Egon Willighagen <egonw@users.sf.net>
 *                     2014  Mark B Vine (orcid:0000-0002-7794-0426)
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.FingerprinterTool;
import org.openscience.cdk.fingerprint.HybridizationFingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Helper class for ModelBuilder3D. Handles templates. This is
 * our layout solution for 3D ring systems
 *
 * @author      cho
 * @author      steinbeck
 * @cdk.created 2004-09-21
 * @cdk.module  builder3d
 * @cdk.githash
 */
public class TemplateHandler3D {

    private static final IChemObjectBuilder builder                    = SilentChemObjectBuilder.getInstance();
    private static final ILoggingTool       logger                     = LoggingToolFactory
                                                                               .createLoggingTool(TemplateHandler3D.class);

    IAtomContainer                          molecule;
    IRingSet                                sssr;
    IAtomContainerSet                       templates                  = null;
    List<BitSet>                            fingerprintData            = null;
    private boolean                         templatesLoaded            = false;

    private static TemplateHandler3D        self                       = null;

    private UniversalIsomorphismTester      universalIsomorphismTester = new UniversalIsomorphismTester();

    private TemplateHandler3D() {
        templates = builder.newInstance(IAtomContainerSet.class);
        fingerprintData = new ArrayList<BitSet>();
    }

    public static TemplateHandler3D getInstance() throws CDKException {
        if (self == null) {
            self = new TemplateHandler3D();
        }
        return self;
    }

    /**
     * Loads all existing templates into memory.
     * Template file is a mdl file. Creates a Object Set of Molecules
     * @throws CDKException The template file cannot be loaded
     */
    private void loadTemplates() throws CDKException {
        logger.debug("Loading templates...");
        IteratingSDFReader imdl;
        InputStream ins;
        BufferedReader fin;

        try {
            ins = this.getClass().getClassLoader()
                    .getResourceAsStream("org/openscience/cdk/modeling/builder3d/data/ringTemplateStructures.sdf.gz");
            fin = new BufferedReader(new InputStreamReader(new GZIPInputStream(ins)));
            imdl = new IteratingSDFReader(fin, builder);
        } catch (IOException exc1) {
            throw new CDKException("Problems loading file ringTemplateStructures.sdf.gz", exc1);
        }
        IAtomContainer molecule;
        while (imdl.hasNext()) {
            molecule = (IAtomContainer) imdl.next();
            templates.addAtomContainer(molecule);
        }
        molecule = null;
        try {
            imdl.close();
        } catch (Exception exc2) {
            System.out.println("Could not close Reader due to: " + exc2.getMessage());
        }
        //logger.debug("TEMPLATE Finger");
        try {

            ins = this.getClass().getClassLoader()
                    .getResourceAsStream("org/openscience/cdk/modeling/builder3d/data/ringTemplateFingerprints.txt.gz");
            fin = new BufferedReader(new InputStreamReader(new GZIPInputStream(ins)));
        } catch (Exception exc3) {
            throw new CDKException("Could not read Fingerprints from FingerprintFile due to: " + exc3.getMessage(),
                    exc3);
        }
        String s = null;
        while (true) {
            try {
                s = fin.readLine();
            } catch (Exception exc4) {
                throw new CDKException("Error while reading the fingerprints: " + exc4.getMessage(), exc4);
            }

            if (s == null) {
                break;
            }
            try {
                fingerprintData.add((BitSet) getBitSetFromFile(new StringTokenizer(s, "\t ;{, }")));
            } catch (Exception exception) {
                throw new CDKException("Error while reading the fingerprints: " + exception.getMessage(), exception);
            }
        }
        //logger.debug("Fingerprints are read in:"+fingerprintData.size());
        templatesLoaded = true;
    }

    public static BitSet getBitSetFromFile(StringTokenizer st) throws Exception {
        BitSet bitSet = new BitSet(1024);
        while (st.hasMoreTokens()) {
            bitSet.set(Integer.parseInt(st.nextToken()));
        }
        return bitSet;
    }

    /**
     * Returns the largest (number of atoms) ring set in a molecule.
     *
     *@param  ringSystems  RingSystems of a molecule
     *@return              The largestRingSet
     */
    public IRingSet getLargestRingSet(List<IRingSet> ringSystems) {
        IRingSet largestRingSet = null;
        int atomNumber = 0;
        IAtomContainer container = null;
        for (int i = 0; i < ringSystems.size(); i++) {
            container = getAllInOneContainer(ringSystems.get(i));
            if (atomNumber < container.getAtomCount()) {
                atomNumber = container.getAtomCount();
                largestRingSet = ringSystems.get(i);
            }
        }
        return largestRingSet;
    }

    private IAtomContainer getAllInOneContainer(IRingSet ringSet) {
        IAtomContainer resultContainer = ringSet.getBuilder().newInstance(IAtomContainer.class);
        Iterator<IAtomContainer> containers = RingSetManipulator.getAllAtomContainers(ringSet).iterator();
        while (containers.hasNext()) {
            resultContainer.add((IAtomContainer) containers.next());
        }
        return resultContainer;
    }

    /**
     * @deprecated Use {@link #mapTemplates(org.openscience.cdk.interfaces.IAtomContainer, int)}
     */
    @Deprecated
    public void mapTemplates(IAtomContainer ringSystems, double numberOfRingAtoms) throws CDKException,
            CloneNotSupportedException {
        mapTemplates(ringSystems, (int) numberOfRingAtoms);
    }

    /**
     * Checks if one of the loaded templates is a substructure in the given
     * Molecule. If so, it assigns the coordinates from the template to the
     * respective atoms in the Molecule.
     *
     * @param ringSystems       AtomContainer from the ring systems.
     * @param numberOfRingAtoms Number of atoms in the specified ring
     * @throws CloneNotSupportedException The atomcontainer cannot be cloned.
     */
    public void mapTemplates(IAtomContainer ringSystems, int numberOfRingAtoms) throws CDKException,
            CloneNotSupportedException {
        if (!templatesLoaded) self.loadTemplates();

        //logger.debug("Map Template...START---Number of Ring Atoms:"+numberOfRingAtoms);
        IAtomContainer ringSystemAnyBondAnyAtom = AtomContainerManipulator.anonymise(ringSystems);
        BitSet ringSystemFingerprint = new HybridizationFingerprinter().getBitFingerprint(ringSystemAnyBondAnyAtom)
                .asBitSet();
        boolean flagMaxSubstructure = false;
        boolean flagSecondbest = false;
        for (int i = 0; i < fingerprintData.size(); i++) {
            IAtomContainer template = templates.getAtomContainer(i);
            //if the atom count is different, it can't be right anyway
            if (template.getAtomCount() != ringSystems.getAtomCount()) {
                continue;
            }
            //we compare the fingerprint with any atom and any bond
            if (FingerprinterTool.isSubset(fingerprintData.get(i), ringSystemFingerprint)) {
                IAtomContainer templateAnyBondAnyAtom = AtomContainerManipulator.anonymise(template);
                //we do the exact match with any atom and any bond
                if (universalIsomorphismTester.isSubgraph(ringSystemAnyBondAnyAtom, templateAnyBondAnyAtom)) {
                    //if this is the case, we keep it as a guess, but look if we can do better
                    List<RMap> list = universalIsomorphismTester.getSubgraphAtomsMap(ringSystemAnyBondAnyAtom,
                            templateAnyBondAnyAtom);
                    boolean flagwritefromsecondbest = false;
                    if ((numberOfRingAtoms == list.size())
                            && templateAnyBondAnyAtom.getBondCount() == ringSystems.getBondCount()) {
                        //so atom and bond count match, could be it's even an exact match,
                        //we check this with the original ring system
                        if (universalIsomorphismTester.isSubgraph(ringSystems, template)) {
                            flagMaxSubstructure = true;
                            list = universalIsomorphismTester.getSubgraphAtomsMap(ringSystems, template);
                        } else {
                            //if it isn't we still now it's better than just the isomorphism
                            flagSecondbest = true;
                            flagwritefromsecondbest = true;
                        }
                    }

                    if (!flagSecondbest || flagMaxSubstructure || flagwritefromsecondbest) {
                        for (int j = 0; j < list.size(); j++) {
                            RMap map = (RMap) list.get(j);
                            IAtom atom1 = ringSystems.getAtom(map.getId1());
                            IAtom atom2 = template.getAtom(map.getId2());
                            if (atom1.getFlag(CDKConstants.ISINRING)) {
                                atom1.setPoint3d(new Point3d(atom2.getPoint3d()));
                            }
                        }//for j
                    }

                    if (flagMaxSubstructure) {
                        break;
                    }

                }//if subgraph
            }//if fingerprint
        }//for i
        if (!flagMaxSubstructure) {
            System.out.println("WARNING: Maybe RingTemplateError!");
        }
    }

    /**
     * Gets the templateCount attribute of the TemplateHandler object.
     *
     * @return The templateCount value
     */
    public int getTemplateCount() {
        return templates.getAtomContainerCount();
    }

    /**
     *  Gets the templateAt attribute of the TemplateHandler object.
     *
     *@param  position  Description of the Parameter
     *@return The templateAt value
     */
    public IAtomContainer getTemplateAt(int position) {
        return templates.getAtomContainer(position);
    }
}
