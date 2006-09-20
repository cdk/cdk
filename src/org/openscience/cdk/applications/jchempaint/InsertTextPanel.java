/*
 *
* Copyright (C) 2001-2006  Rajarshi Guha <rajarshi@users.sourceforge.net>
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
package org.openscience.cdk.applications.jchempaint;

import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.layout.TemplateHandler;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.vecmath.Vector2d;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * A panel containing a text field and button to directly insert SMILES or InChI's
 *
 * @author Rajarshi Guha
 * @cdk.module jchempaint
 * @cdk.require swing
 */
public class InsertTextPanel extends JPanel implements ActionListener {

    private JChemPaintPanel jChemPaintPanel;
    private JComboBox textCombo;
    private JTextComponent editor;


    public InsertTextPanel(JChemPaintPanel jChemPaintPanel) {
        super();
        setLayout(new GridBagLayout());

        Vector oldText = new Vector();
        oldText.add("");

        textCombo = new JComboBox(oldText);
        textCombo.setEditable(true);
        textCombo.setToolTipText("Enter a SMILES or InChI string");

        textCombo.addActionListener(this);
        editor = (JTextComponent) textCombo.getEditor().getEditorComponent();


        JButton button = new JButton("Insert");
        button.addActionListener(this);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();


        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        add(textCombo, gridBagConstraints);

        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridx = 1;
        add(button, gridBagConstraints);


        this.jChemPaintPanel = jChemPaintPanel;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();
        if (actionCommand.equals("comboBoxEdited") || actionCommand.equals("Insert")) {
            IMolecule molecule = getMolecule();
            if (molecule == null) return;
            generateModel(molecule);
        }
    }

    private IMolecule getMolecule() {
        IMolecule molecule;
        String text = (String) textCombo.getSelectedItem();
        text = text.trim(); // clean up extra white space

        if (text.equals("")) return null;

        if (text.startsWith("InChI")) { // handle it as an InChI
            try {
                /*
                InChIGeneratorFactory inchiFactory = new InChIGeneratorFactory();
                InChIToStructure inchiToStructure = inchiFactory.getInChIToStructure(text);
                INCHI_RET status = inchiToStructure.getReturnStatus();
                if (status != INCHI_RET.OKAY) {
                  JOptionPane.showMessageDialog(jChemPaintPanel, "Could not parse string as SMILES or InChI");
                  return;
                }
                molecule = (IMolecule) inchiToStructure.getAtomContainer();
                */
                throw new CDKException("pending");
            } catch (CDKException e2) {
                JOptionPane.showMessageDialog(jChemPaintPanel, "Could not load InChI subsystem");
                return null;
            }
        } else { // we assume it's a SMILES
            SmilesParser smilesParser = new SmilesParser();
            try {
                molecule = smilesParser.parseSmiles(text);
            } catch (InvalidSmilesException e1) {
                JOptionPane.showMessageDialog(jChemPaintPanel, "Invalid SMILES specified");
                return null;
            }
        }

        // OK, we have a valid molecule, save it and show it
        String tmp = (String) textCombo.getItemAt(0);
        if (tmp.equals("")) textCombo.removeItemAt(0);
        textCombo.addItem(text);
        editor.setText("");

        return molecule;
    }

    private void generateModel(IMolecule molecule) {

        // ok, get relevent bits from active model
        JChemPaintModel jcpModel = jChemPaintPanel.getJChemPaintModel();
        Renderer2DModel renderModel = jcpModel.getRendererModel();
        org.openscience.cdk.interfaces.IChemModel chemModel = jcpModel.getChemModel();
        org.openscience.cdk.interfaces.IMoleculeSet moleculeSet = chemModel.getSetOfMolecules();
        if (moleculeSet == null) {
            moleculeSet = new SetOfMolecules();
        }

        // ok, now generate 2D coordinates
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setTemplateHandler(new TemplateHandler(moleculeSet.getBuilder()));
        try {
            sdg.setMolecule(molecule);
            sdg.generateCoordinates(new Vector2d(0, 1));
            molecule = sdg.getMolecule();
            double bondLength = renderModel.getBondLength();
            double scaleFactor = GeometryTools.getScaleFactor(molecule, bondLength);
            GeometryTools.scaleMolecule(molecule, scaleFactor, renderModel.getRenderingCoordinates());
            //if there are no atoms in the actual chemModel all 2D-coordinates would be set to NaN
            if (ChemModelManipulator.getAllInOneContainer(chemModel).getAtomCount() != 0) {
                GeometryTools.translate2DCenterTo(molecule,
                        GeometryTools.get2DCenter(
                                ChemModelManipulator.getAllInOneContainer(chemModel)
                        )
                );
            }
            GeometryTools.translate2D(molecule, 5 * bondLength, 0, renderModel.getRenderingCoordinates()); // in pixels
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        moleculeSet.addMolecule(molecule);
//        renderModel.setSelectedPart(m);
        jChemPaintPanel.getChemModel().setSetOfMolecules(moleculeSet);
        jChemPaintPanel.scaleAndCenterMolecule(jChemPaintPanel.getChemModel());
        jcpModel.fireChange(jChemPaintPanel.getChemModel());
    }
}
