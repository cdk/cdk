/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2003  The Chemistry Development Kit (CDK) project
 *
 *  Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.renderer;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.tools.LoggingTool;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.lang.reflect.*;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Displays a ChemObject as a tree. An example tree looks like this:
 * <pre>
 *   ChemModel
 *    +- SetOfMolecules
 *        +- Molecule
 *        |   +- Atom
 *        |   +- Atom
 *        |   +- Bond
 *        +- Molecule
 * </pre>
 *
 * <p>For each ChemObject it will add child nodes for each variable
 * it has that is either a ChemObject or a ChemObject[].
 *
 * @author     egonw
 * @created    December 29, 2002
 * @keyword    tree, ChemObject
 */
public class ChemObjectTree extends JPanel {

    private LoggingTool logger;

    private JTree tree;

	/**
	 * Constructs a JPanel showing a ChemObject in a tree.
	 */
	public ChemObjectTree() {
        logger = new LoggingTool(this.getClass().getName());
        this.tree = new JTree(new DefaultMutableTreeNode("empty"));
        this.add(tree);
	}

    /**
     * Displays the ChemObject in tree format.
     */
    public void paintChemObject(ChemObject object) {
        // make a new tree
        this.removeAll();
        if (object == null) {
            // logger.info("Making empty ChemObjectTree");
            this.tree = new JTree(new DefaultMutableTreeNode("empty"));
        } else {
            // logger.info("Making ChemObjectTree for " + object.getClass().getName());
            DefaultMutableTreeNode topNode = getTree(object);
            this.tree = new JTree(topNode);
        }
        this.add(this.tree);
    }
    
    /**
     * Creates a node with the object name.
     */
    private DefaultMutableTreeNode getObjectName(Object object) {
        return new DefaultMutableTreeNode(object.getClass()
            .getName().substring("org.openscience.cdk.".length()));
    }
    
    /**
     * Generate a tree of ChemObject's.
     */
    private DefaultMutableTreeNode getTree(ChemObject object) {
        DefaultMutableTreeNode node = getObjectName(object);
        Class reflectedClass = object.getClass();
        // get all fields in this ChemObject
        Field[] fields = getFields(reflectedClass);
        // logger.debug(reflectedClass.getName() + " #fields: " + fields.length); 
        for (int i=0; i<fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);
            // logger.debug("Field name: " + f.getName());
            // logger.debug("Field type: " + f.getType().getName());
            try {
                // get an instance of the object in the field
                Object fieldObject = f.get(object);
                if (fieldObject != null) {
                    // logger.debug("Field value: " + fieldObject.getClass().getName());
                    if (fieldObject instanceof ChemObject) {
                        // yes, found a ChemObject!
                        // logger.debug("Recursing into this object");
                        node.add(getTree((ChemObject)fieldObject));
                    } else if (fieldObject instanceof ChemObject[]) {
                        // yes, found a Array!
                        // logger.debug("Recursing into this Array");
                        // determine what kind of Array
                        ChemObject[] objects = (ChemObject[])fieldObject;
                        int count = objects.length;
                        // Because the count above gives the array length and not the number
                        // of not null objects the array, some intelligence must be added
                        if (object instanceof AtomContainer) {
                            if (objects[0] instanceof Atom) {
                                count = ((AtomContainer)object).getAtomCount();
                            } else if (objects[0] instanceof Bond) {
                                count = ((AtomContainer)object).getBondCount();
                            }
                        }
                        // now start actual looping over child objects
                        for (int j=0; j<objects.length; j++) {
                            if (objects[j] != null) {
                                node.add(getTree(objects[j]));
                            }
                        }
                    }
                } else {
                    //logger.debug("Field value: null");
                }
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
        return node;
    }
    
    private Field[] getFields(Class reflectedClass) {
        Field[] fields = reflectedClass.getDeclaredFields();
        if (fields.length == 0) {
            // try its super class (as long as it is still a ChemObject
            Class superClass = reflectedClass.getSuperclass();
            try {
                if (superClass.newInstance() instanceof ChemObject) {
                    fields = getFields(superClass);
                }
            } catch (IllegalAccessException event) {
            } catch (InstantiationException event) {
            };
        }
        return fields;
    }
}

