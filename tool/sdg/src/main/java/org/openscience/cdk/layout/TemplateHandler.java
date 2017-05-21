/*  Copyright (C) 2003-2005  Christoph Steinbeck
 *                2003-2008  Egon Willighagen
 *                           Stefan Kuhn
 *                           Rajarshi Guha
 *                2015       John May
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
package org.openscience.cdk.layout;

import com.google.common.collect.FluentIterable;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.isomorphism.AtomMatcher;
import org.openscience.cdk.isomorphism.BondMatcher;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.VentoFoggia;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import javax.vecmath.Point2d;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for Structure Diagram Generation. Handles templates. This is
 * our layout solution for ring systems which are notoriously difficult to
 * layout, like cubane, adamantane, porphyrin, etc.
 *
 * @author steinbeck
 * @cdk.created 2003-09-04
 * @cdk.keyword layout
 * @cdk.keyword 2D-coordinates
 * @cdk.keyword structure diagram generation
 * @cdk.require java1.4+
 * @cdk.module sdg
 * @cdk.githash
 */
public final class TemplateHandler {

    private final static ILoggingTool         LOGGER       = LoggingToolFactory.createLoggingTool(TemplateHandler.class);
    private final        List<IAtomContainer> templates    = new ArrayList<>();
    private final        List<Pattern>        anonPatterns = new ArrayList<>();
    private final        List<Pattern>        elemPatterns = new ArrayList<>();


    private final AtomMatcher elemAtomMatcher = new AtomMatcher() {
        @Override
        public boolean matches(IAtom a, IAtom b) {
            return a.getAtomicNumber().equals(b.getAtomicNumber());
        }
    };
    private final AtomMatcher anonAtomMatcher = new AtomMatcher() {
        @Override
        public boolean matches(IAtom a, IAtom b) {
            return true;
        }
    };
    private final BondMatcher anonBondMatcher = new BondMatcher() {
        @Override
        public boolean matches(IBond a, IBond b) {
            return true;
        }
    };

    /**
     * Creates a new TemplateHandler with default templates loaded.
     */
    public TemplateHandler(IChemObjectBuilder builder) {
        loadTemplates(builder);
    }

    /**
     * Creates a new TemplateHandler without any default templates.
     */
    public TemplateHandler() {
    }

    /**
     * Loads all existing templates into memory. To add templates to be used in
     * SDG, place a drawing with the new template in org/openscience/cdk/layout/templates and add the
     * template filename to org/openscience/cdk/layout/templates/template.list
     */
    public void loadTemplates(IChemObjectBuilder builder) {
        String line = null;
        try {
            InputStream ins = this.getClass().getClassLoader()
                                  .getResourceAsStream("org/openscience/cdk/layout/templates/templates.list");
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            while (reader.ready()) {
                line = reader.readLine();
                line = "org/openscience/cdk/layout/templates/" + line;
                LOGGER.debug("Attempting to read template ", line);
                try {
                    CMLReader structureReader = new CMLReader(this.getClass().getClassLoader()
                                                                  .getResourceAsStream(line));
                    IChemFile file = (IChemFile) structureReader.read(builder.newInstance(IChemFile.class));
                    List<IAtomContainer> files = ChemFileManipulator.getAllAtomContainers(file);
                    for (int i = 0; i < files.size(); i++)
                        addMolecule(files.get(i));
                    LOGGER.debug("Successfully read template ", line);
                } catch (CDKException | IllegalArgumentException e) {
                    LOGGER.warn("Could not read template ", line, ", reason: ", e.getMessage());
                    LOGGER.debug(e);
                }

            }
        } catch (IOException e) {
            LOGGER.warn("Could not read (all of the) templates, reason: ", e.getMessage());
            LOGGER.debug(e);
        }
    }


    /**
     * Adds a Molecule to the list of templates use by this TemplateHandler.
     *
     * @param molecule The molecule to be added to the TemplateHandler
     */
    public void addMolecule(IAtomContainer molecule) {
        if (!GeometryUtil.has2DCoordinates(molecule))
            throw new IllegalArgumentException("Template did not have 2D coordinates");

        // we want a consistent scale!
        GeometryUtil.scaleMolecule(molecule, GeometryUtil.getScaleFactor(molecule,
                                                                         StructureDiagramGenerator.DEFAULT_BOND_LENGTH));

        templates.add(molecule);
        anonPatterns.add(VentoFoggia.findSubstructure(molecule,
                                                      anonAtomMatcher,
                                                      anonBondMatcher));
        elemPatterns.add(VentoFoggia.findSubstructure(molecule,
                                                      elemAtomMatcher,
                                                      anonBondMatcher));
    }

    public IAtomContainer removeMolecule(IAtomContainer molecule) throws CDKException {
        for (int i = 0; i < templates.size(); i++) {
            if (VentoFoggia.findIdentical(templates.get(i), anonAtomMatcher, anonBondMatcher)
                           .matches(molecule)) {
                elemPatterns.remove(i);
                anonPatterns.remove(i);
                return templates.remove(i);
            }
        }
        return null;
    }

    /**
     * Checks if one of the loaded templates is isomorph to the given
     * Molecule. If so, it assigns the coordinates from the template to the
     * respective atoms in the Molecule, and marks the atoms as ISPLACED.
     *
     * @param molecule The molecule to be check for potential templates
     * @return True if there was a possible mapping
     */
    public boolean mapTemplateExact(IAtomContainer molecule) throws CDKException {
        for (IAtomContainer template : templates) {
            Mappings mappings = VentoFoggia.findIdentical(template, anonAtomMatcher, anonBondMatcher)
                                           .matchAll(molecule);
            for (Map<IAtom, IAtom> atoms : mappings.toAtomMap()) {
                for (Map.Entry<IAtom, IAtom> e : atoms.entrySet()) {
                    e.getValue().setPoint2d(new Point2d(e.getKey().getPoint2d()));
                    e.getValue().setFlag(CDKConstants.ISPLACED, true);
                }
                if (!atoms.isEmpty())
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks if one of the loaded templates is a substructure in the given
     * Molecule. If so, it assigns the coordinates from the template to the
     * respective atoms in the Molecule, and marks the atoms as ISPLACED.
     *
     * @param molecule The molecule to be check for potential templates
     * @return True if there was a possible mapping
     */
    public boolean mapTemplates(IAtomContainer molecule) throws CDKException {
        // match element patterns first so hetero atoms are oriented correctly
        for (Pattern anonPattern : elemPatterns) {
            for (Map<IAtom, IAtom> atoms : anonPattern.matchAll(molecule).toAtomMap()) {
                for (Map.Entry<IAtom, IAtom> e : atoms.entrySet()) {
                    e.getValue().setPoint2d(new Point2d(e.getKey().getPoint2d()));
                    e.getValue().setFlag(CDKConstants.ISPLACED, true);
                }
                if (!atoms.isEmpty())
                    return true;
            }
        }
        // no element pattern matched, try anonymous patterns
        for (Pattern anonPattern : anonPatterns) {
            for (Map<IAtom, IAtom> atoms : anonPattern.matchAll(molecule).toAtomMap()) {
                for (Map.Entry<IAtom, IAtom> e : atoms.entrySet()) {
                    e.getValue().setPoint2d(new Point2d(e.getKey().getPoint2d()));
                    e.getValue().setFlag(CDKConstants.ISPLACED, true);
                }
                if (!atoms.isEmpty())
                    return true;
            }
        }
        return false;
    }

    /**
     * Gets the templateCount attribute of the TemplateHandler object
     *
     * @return The templateCount value
     */
    public int getTemplateCount() {
        return templates.size();
    }

    /**
     * Gets the templateAt attribute of the TemplateHandler object
     *
     * @param position Description of the Parameter
     * @return The templateAt value
     */
    public IAtomContainer getTemplateAt(int position) {
        return templates.get(position);
    }

    /**
     * Checks if one of the loaded templates is a substructure in the given
     * Molecule and returns all matched substructures in a IAtomContainerSet.
     * This method does not assign any coordinates.
     *
     * @param molecule The molecule to be check for potential templates
     * @return an IAtomContainerSet of all matched substructures of
     * the molecule
     * @throws CDKException if an error occurs
     */
    public IAtomContainerSet getMappedSubstructures(IAtomContainer molecule) throws CDKException {

        final IAtomContainerSet matchedSubstructures = molecule.getBuilder().newInstance(IAtomContainerSet.class);
        final Set<IChemObject>  matchedChemObjs      = new HashSet<>();

        for (Pattern anonPattern : anonPatterns) {
            for (Map<IChemObject, IChemObject> map : anonPattern.matchAll(molecule)
                                                                .uniqueAtoms()
                                                                .toAtomBondMap()) {
                boolean overlaps = false;
                IAtomContainer matched = molecule.getBuilder()
                                                 .newInstance(IAtomContainer.class);
                for (Map.Entry<IChemObject, IChemObject> e : map.entrySet()) {
                    if (matchedChemObjs.contains(e.getValue()))
                        overlaps = true;
                    if (e.getValue() instanceof IAtom)
                        matched.addAtom((IAtom) e.getValue());
                    else if (e.getValue() instanceof IBond)
                        matched.addBond((IBond) e.getValue());
                }

                // only add if the atoms/bonds of this match don't overlap existing
                if (!overlaps) {
                    matchedChemObjs.addAll(FluentIterable.from(matched.atoms()).toList());
                    matchedChemObjs.addAll(FluentIterable.from(matched.bonds()).toList());
                    matchedSubstructures.addAtomContainer(matched);
                }
            }
        }
        return matchedSubstructures;
    }

    /**
     * Singleton template instance, mainly useful for aligning molecules. If the template
     * does not have coordinates an error is thrown.
     *
     * For safety we clone the molecule.
     *
     * @param template the molecule
     * @return new template handler
     */
    public static TemplateHandler createSingleton(IAtomContainer template) {
        try {
            TemplateHandler handler = new TemplateHandler();
            IAtomContainer copy = template.clone();
            handler.addMolecule(copy);
            return handler;
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("Could not clone molecule.");
        }
    }

    /**
     * Create a template from a substructure pattern. Using this template handler in the diagram
     * generator then allows us to align to common reference.
     *
     * @param ptrn the structure pattern to match
     * @param mols list of molecules
     * @return new template handler
     */
    public static TemplateHandler createFromSubstructure(Pattern ptrn, Iterable<IAtomContainer> mols) {
        for (IAtomContainer mol : mols) {
            for (IAtomContainer template : ptrn.matchAll(mol).toSubstructures())
                return createSingleton(template);
        }
        throw new IllegalArgumentException("Pattern does not match any provided molecules");
    }

    /**
     * Create a template from a substructure pattern. Using this template handler in the diagram
     * generator then allows us to align to common reference.
     *
     * @param ptrn the structure pattern to match
     * @param mol molecule
     * @return new template handler
     */
    public static TemplateHandler createFromSubstructure(Pattern ptrn, IAtomContainer mol) {
        for (IAtomContainer template : ptrn.matchAll(mol).toSubstructures())
            return createSingleton(template);
        throw new IllegalArgumentException("Pattern does not match any provided molecules");
    }

    /**
     * Convert to an identity template library.
     *
     * @return identity template library
     */
    IdentityTemplateLibrary toIdentityTemplateLibrary() {
        IdentityTemplateLibrary lib = IdentityTemplateLibrary.empty();
        for (IAtomContainer mol : templates) {
            lib.add(mol);
            lib.add(AtomContainerManipulator.anonymise(mol));
        }
        return lib;
    }
}
