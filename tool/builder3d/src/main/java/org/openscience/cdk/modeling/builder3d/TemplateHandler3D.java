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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import javax.vecmath.Point3d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Helper class for ModelBuilder3D. Handles templates. This is
 * our layout solution for 3D ring systems
 *
 * @author cho
 * @author steinbeck
 * @author John Mayfield
 * @cdk.created 2004-09-21
 * @cdk.module builder3d
 * @cdk.githash
 */
public class TemplateHandler3D {

    private static final IChemObjectBuilder builder       = SilentChemObjectBuilder.getInstance();
    public static final  String             TEMPLATE_PATH = "data/ringTemplateStructures.sdf.gz";

    private final List<IAtomContainer>      templates = new ArrayList<>();
    private final List<IQueryAtomContainer> queries   = new ArrayList<>();
    private final List<Pattern>             patterns  = new ArrayList<>();

    private static TemplateHandler3D self = null;

    private final ILoggingTool logger = LoggingToolFactory.createLoggingTool(TemplateHandler3D.class);

    private UniversalIsomorphismTester universalIsomorphismTester = new UniversalIsomorphismTester();

    private TemplateHandler3D() {
    }

    public static TemplateHandler3D getInstance() throws CDKException {
        if (self == null) {
            self = new TemplateHandler3D();
        }
        return self;
    }

    private void addTemplateMol(IAtomContainer mol) {
        templates.add(mol);
        QueryAtomContainer query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(mol, false);
        queries.add(query);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            query.getAtom(i).setPoint3d(new Point3d(mol.getAtom(i).getPoint3d()));
        }
        patterns.add(Pattern.findSubstructure(query));
    }

    /**
     * Load ring template
     *
     * @throws CDKException The template file cannot be loaded
     */
    private void loadTemplates() throws CDKException {
        try (InputStream gin = getClass().getResourceAsStream(TEMPLATE_PATH);
             InputStream in = new GZIPInputStream(gin);
             IteratingSDFReader sdfr = new IteratingSDFReader(in, builder)) {
            while (sdfr.hasNext()) {
                final IAtomContainer mol = sdfr.next();
                addTemplateMol(mol);
            }
        } catch (IOException e) {
            throw new CDKException("Could not load ring templates", e);
        }
    }


    public static BitSet getBitSetFromFile(StringTokenizer st) throws
                                                               Exception {
        BitSet bitSet = new BitSet(1024);
        while (st.hasMoreTokens()) {
            bitSet.set(Integer.parseInt(st.nextToken()));
        }
        return bitSet;
    }

    /**
     * Returns the largest (number of atoms) ring set in a molecule.
     *
     * @param ringSystems RingSystems of a molecule
     * @return The largestRingSet
     */
    public IRingSet getLargestRingSet(List<IRingSet> ringSystems) {
        IRingSet       largestRingSet = null;
        int            atomNumber     = 0;
        IAtomContainer container      = null;
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
        IAtomContainer           resultContainer = ringSet.getBuilder().newInstance(IAtomContainer.class);
        Iterator<IAtomContainer> containers      = RingSetManipulator.getAllAtomContainers(ringSet).iterator();
        while (containers.hasNext()) {
            resultContainer.add((IAtomContainer) containers.next());
        }
        return resultContainer;
    }

    /**
     * @deprecated Use {@link #mapTemplates(org.openscience.cdk.interfaces.IAtomContainer, int)}
     */
    @Deprecated
    public void mapTemplates(IAtomContainer ringSystems,
                             double numberOfRingAtoms) throws CDKException,
                                                              CloneNotSupportedException {
        mapTemplates(ringSystems, (int) numberOfRingAtoms);
    }

    private boolean isExactMatch(IAtomContainer query,
                                 Map<IChemObject, IChemObject> mapping) {
        for (IAtom src : query.atoms()) {
            IAtom dst = (IAtom) mapping.get(src);
            if (!Objects.equals(src.getSymbol(), dst.getSymbol()))
                return false;
        }
        for (IBond src : query.bonds()) {
            IBond dst = (IBond) mapping.get(src);
            if (!Objects.equals(src.getOrder(), dst.getOrder()))
                return false;
        }
        return true;
    }

    /**
     * Checks if one of the loaded templates is a substructure in the given
     * Molecule. If so, it assigns the coordinates from the template to the
     * respective atoms in the Molecule.
     *
     * @param mol               AtomContainer from the ring systems.
     * @param numberOfRingAtoms Number of atoms in the specified ring
     * @throws CloneNotSupportedException The atomcontainer cannot be cloned.
     */
    public void mapTemplates(IAtomContainer mol, int numberOfRingAtoms)
        throws CDKException, CloneNotSupportedException {
        if (templates.isEmpty())
            loadTemplates();

        IAtomContainer                best          = null;
        Map<IChemObject, IChemObject> bestMap       = null;
        IAtomContainer                secondBest    = null;
        Map<IChemObject, IChemObject> secondBestMap = null;

        for (int i = 0; i < templates.size(); i++) {

            IAtomContainer query = queries.get(i);

            //if the atom count is different, it can't be right anyway
            if (query.getAtomCount() != mol.getAtomCount()) {
                continue;
            }

            Mappings mappings = patterns.get(i).matchAll(mol);
            for (Map<IChemObject, IChemObject> map : mappings.toAtomBondMap()) {
                if (isExactMatch(query, map)) {
                    assignCoords(query, map);
                    return;
                } else if (query.getBondCount() == mol.getBondCount()) {
                    best = query;
                    bestMap = new HashMap<>(map);
                } else {
                    secondBest = query;
                    secondBestMap = new HashMap<>(map);
                }
            }
        }

        if (best != null) {
            assignCoords(best, bestMap);
        } else if (secondBest != null) {
            assignCoords(secondBest, secondBestMap);
        }

        logger.warn("Maybe RingTemplateError!");
    }

    private void assignCoords(IAtomContainer template,
                              Map<IChemObject, IChemObject> map) {
        for (IAtom src : template.atoms()) {
            IAtom dst = (IAtom) map.get(src);
            dst.setPoint3d(new Point3d(src.getPoint3d()));
        }
    }

    /**
     * Gets the templateCount attribute of the TemplateHandler object.
     *
     * @return The templateCount value
     */
    public int getTemplateCount() {
        return templates.size();
    }

    /**
     * Gets the templateAt attribute of the TemplateHandler object.
     *
     * @param position Description of the Parameter
     * @return The templateAt value
     */
    public IAtomContainer getTemplateAt(int position) {
        return templates.get(position);
    }
}
