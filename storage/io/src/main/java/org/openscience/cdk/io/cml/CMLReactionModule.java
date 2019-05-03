/* Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io.cml;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.xml.sax.Attributes;

/**
 * @author Egon Willighagen &lt;elw38@cam.ac.uk&gt;
 *
 * @cdk.module io
 * @cdk.githash
 */
public class CMLReactionModule extends CMLCoreModule {

    private String objectType;

    public CMLReactionModule(IChemFile chemFile) {
        super(chemFile);
    }

    public CMLReactionModule(ICMLModule conv) {
        super(conv);
        logger.debug("New CML-Reaction Module!");
    }

    @Override
    public void startElement(CMLStack xpath, String uri, String local, String raw, Attributes atts) {
        if ("reaction".equals(local)) {
            //            cdo.startObject("Reaction");
            if (currentReactionSet == null)
                currentReactionSet = currentChemFile.getBuilder().newInstance(IReactionSet.class);
            currentReaction = currentChemFile.getBuilder().newInstance(IReaction.class);
            String id = atts.getValue("id");
            if (id != null) currentReaction.setID(id);
            //            	cdo.setObjectProperty("Reaction", "id", id);
        } else if ("reactionList".equals(local)) {
            //            cdo.startObject("ReactionSet");
            currentReactionSet = currentChemFile.getBuilder().newInstance(IReactionSet.class);
            String id = atts.getValue("id");
            if (id != null) currentReactionSet.setID(id);
            //            	cdo.setObjectProperty("reactionList", "id", id);
        } else if ("reactant".equals(local)) {
            //            cdo.startObject("Reactant");
            if (currentReaction == null) {
                if (currentReactionSet == null)
                    currentReactionSet = currentChemFile.getBuilder().newInstance(IReactionSet.class);
                currentReaction = currentChemFile.getBuilder().newInstance(IReaction.class);
            }
            currentMolecule = currentChemFile.getBuilder().newInstance(IAtomContainer.class);
            objectType = "Reactant";
            String id = atts.getValue("id");
            if (id != null)
                currentMolecule.setID(id);
            else {
                String ref = atts.getValue("ref");
                if (ref != null) currentMolecule.setID(ref);
            }
            //            	cdo.setObjectProperty("Reactant", "id", id);
        } else if ("product".equals(local)) {
            //            cdo.startObject("Product");
            if (currentReaction == null) {
                if (currentReactionSet == null)
                    currentReactionSet = currentChemFile.getBuilder().newInstance(IReactionSet.class);
                currentReaction = currentChemFile.getBuilder().newInstance(IReaction.class);
            }
            currentMolecule = currentChemFile.getBuilder().newInstance(IAtomContainer.class);
            objectType = "Product";
            String id = atts.getValue("id");
            if (id != null)
                currentMolecule.setID(id);
            else {
                String ref = atts.getValue("ref");
                if (ref != null) currentMolecule.setID(ref);
            }
            //            	cdo.setObjectProperty("Product", "id", id);
        } else if ("substance".equals(local)) {
            //            cdo.startObject("Agent");
            if (currentReaction == null) {
                if (currentReactionSet == null)
                    currentReactionSet = currentChemFile.getBuilder().newInstance(IReactionSet.class);
                currentReaction = currentChemFile.getBuilder().newInstance(IReaction.class);
            }
            currentMolecule = currentChemFile.getBuilder().newInstance(IAtomContainer.class);
            objectType = "Agent";
            String id = atts.getValue("id");
            if (id != null)
                currentMolecule.setID(id);
            else {
                String ref = atts.getValue("ref");
                if (ref != null) currentMolecule.setID(ref);
            }
            //            	cdo.setObjectProperty("Agent", "id", id);
        } else if ("molecule".equals(local)) {
            // clear existing molecule data
            super.newMolecule();
            String id = atts.getValue("id");
            if (id != null) {
                // check for existing molecule of that id
                IAtomContainer existing = getMoleculeFromID(currentMoleculeSet, id);
                if (existing != null) {
                    currentMolecule = existing;
                } else {
                    currentMolecule.setID(id);
                }
            } else {
                String ref = atts.getValue("ref");
                if (ref != null) {
                    IAtomContainer atomC = getMoleculeFromID(currentMoleculeSet, ref);

                    // if there was no molecule create a new one for the reference. this
                    // happens when the reaction is defined before the molecule set
                    if (atomC == null) {
                        atomC = currentChemFile.getBuilder().newInstance(IAtomContainer.class);
                        atomC.setID(ref);
                        currentMoleculeSet.addAtomContainer(atomC);
                    }

                    super.currentMolecule = atomC;
                }
            }
        } else {
            super.startElement(xpath, uri, local, raw, atts);
        }
    }

    @Override
    public void endElement(CMLStack xpath, String uri, String local, String raw) {
        if ("reaction".equals(local)) {
            //            cdo.endObject("Reaction");
            currentReactionSet.addReaction(currentReaction);
            currentChemModel.setReactionSet(currentReactionSet);
        } else if ("reactionList".equals(local)) {
            //            cdo.endObject("ReactionSet");
            currentChemModel.setReactionSet(currentReactionSet);
            /* FIXME: this should be when document is closed! */
        } else if ("reactant".equals(local)) {
            //            cdo.endObject("Reactant");
            currentReaction.addReactant(currentMolecule);
        } else if ("product".equals(local)) {
            //            cdo.endObject("Product");
            currentReaction.addProduct(currentMolecule);
        } else if ("substance".equals(local)) {
            //            cdo.endObject("Agent");
            currentReaction.addAgent(currentMolecule);
        } else if ("molecule".equals(local)) {
            logger.debug("Storing Molecule");
            //if the current molecule exists in the currentMoleculeSet means that is a reference in these.
            if (currentMoleculeSet.getMultiplier(currentMolecule) == -1) super.storeData();
            // do nothing else but store atom/bond information
        } else {
            super.endElement(xpath, uri, local, raw);
        }
    }

    /**
     * Get the IAtomContainer contained in a IAtomContainerSet object with a ID.
     *
     * @param molSet   The IAtomContainerSet
     * @param id       The ID the look
     * @return         The IAtomContainer with the ID
     */
    private IAtomContainer getMoleculeFromID(IAtomContainerSet molSet, String id) {
        for (IAtomContainer mol : molSet.atomContainers()) {
            if (mol.getID().equals(id)) return mol;
        }
        return null;
    }
}
