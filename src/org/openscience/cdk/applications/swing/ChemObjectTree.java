/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.applications.swing;

import java.awt.FlowLayout;
import java.lang.reflect.Field;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Displays a IChemObject as a tree. An example tree looks like this:
 * <pre>
 *   ChemModel
 *    +- MoleculeSet
 *        +- Molecule
 *        |   +- Atom
 *        |   +- Atom
 *        |   +- Bond
 *        +- Molecule
 * </pre>
 *
 * <p>For each IChemObject it will add child nodes for each variable
 * it has that is either a IChemObject or a IChemObject[].
 *
 * @cdk.module applications
 *
 * @author     egonw
 * @cdk.created    2002-12-29
 * @cdk.keyword    tree, IChemObject
 * @cdk.require swing
 */
public class ChemObjectTree extends JPanel {

	private static final long serialVersionUID = -3219029673378945019L;

	private LoggingTool logger;

    private JTree tree;
    private TreeSelectionListener treeListener;

	/**
	 * Constructs a JPanel showing a IChemObject in a tree.
	 */
	public ChemObjectTree() {
        logger = new LoggingTool(this);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.tree = new JTree(new DefaultMutableTreeNode("No Object"));
        this.add(tree);
        treeListener = null;
        this.setBackground(tree.getBackground());
	}

    /**
     * Displays the IChemObject in tree format.
     */
    public void paintChemObject(IChemObject object) {
        // make a new tree
        this.removeAll();
        if (object == null) {
            // logger.info("Making empty ChemObjectTree");
            this.tree = new JTree(new DefaultMutableTreeNode("No Object"));
        } else {
            logger.info("Making ChemObjectTree for " + object.getClass().getName());
            logger.debug(object);
            DefaultMutableTreeNode topNode = getTree(object);
            this.tree = new JTree(topNode);
            int rowcount = 0;
            do {
                rowcount = this.tree.getRowCount();
                for (int row=rowcount; row>=0; row--){
                    this.tree.expandRow(row);
                }
            }
            while (rowcount != this.tree.getRowCount());
        }
        if (treeListener != null) {
            this.tree.addTreeSelectionListener(treeListener);
        }
        this.add(this.tree);
    }
    
    public void addTreeSelectionListener(TreeSelectionListener tsl) {
        this.treeListener = tsl;
        if (this.tree != null) {
            this.tree.addTreeSelectionListener(tsl);
        }
    }
    
    /**
     * Generate a tree of IChemObject's.
     */
    private DefaultMutableTreeNode getTree(IChemObject object) {
        DefaultMutableTreeNode node = new ChemObjectTreeNode(object);
        Class reflectedClass = object.getClass();
        logger.debug("getTree for class: ", reflectedClass);
        // get all fields in this IChemObject
        Field[] fields = getFields(reflectedClass);
        logger.debug(reflectedClass.getName(), " #fields: " + fields.length); 
        for (int i=0; i<fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);
            logger.debug("Field name: ", f.getName());
            logger.debug("Field type: ", f.getType().getName());
            try {
                // get an instance of the object in the field
                Object fieldObject = f.get(object);
                if (fieldObject != null) {
                    logger.debug("Field value: ", fieldObject.getClass().getName());
                    if (fieldObject instanceof IChemObject) {
                        // yes, found a IChemObject!
                        logger.debug("Recursing into this object");
                        node.add(getTree((IChemObject)fieldObject));
                    } else if (fieldObject instanceof IChemObject[]) {
                        // yes, found a Array!
                        logger.debug("Recursing into this Array");
                        // determine what kind of Array
                        IChemObject[] objects = (IChemObject[])fieldObject;
                        int count = objects.length;
                        // Because the count above gives the array length and not the number
                        // of not null objects the array, some intelligence must be added
                        if (object instanceof AtomContainer && objects[0] != null) {
                            logger.debug("field class: ", objects[0].getClass().getName());
                            if (objects[0] instanceof Atom) {
                                count = ((AtomContainer)object).getAtomCount();
                            } else if (objects[0] instanceof ElectronContainer) {
                                count = ((AtomContainer)object).getElectronContainerCount();
                            } else {
                                logger.warn("Object not counted!");
                            }
                        } else {
                            logger.debug("Not going to recurse into arrays that are not field of AtomContainer");
                        }
                        logger.debug("Found #entries in array: ", count);
                        // now start actual looping over child objects
                        for (int j=0; j<objects.length; j++) {
                            if (objects[j] != null) {
                                node.add(getTree(objects[j]));
                            }
                        }
                    }
                } else {
                    logger.debug("Field value: null");
                }
            } catch (Exception e) {
                logger.error("Error while constructing COT: ", e.getMessage());
                logger.debug(e);
            }
        }
        logger.debug("Returning tree node: ", node);
        return node;
    }
    
    private Field[] getFields(Class reflectedClass) {
        Field[] fields = reflectedClass.getDeclaredFields();
        if (fields.length == 0) {
            // try its super class (as long as it is still a IChemObject
            Class superClass = reflectedClass.getSuperclass();
            try {
                if (superClass.newInstance() instanceof IChemObject) {
                    fields = getFields(superClass);
                }
            } catch (IllegalAccessException event) {
            } catch (InstantiationException event) {
            };
        }
        return fields;
    }
}

