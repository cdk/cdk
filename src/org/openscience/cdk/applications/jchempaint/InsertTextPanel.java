/*
 *
* Copyright (C) 2001-2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import javax.vecmath.Vector2d;

import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.layout.TemplateHandler;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

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
    private JFrame closeafter=null;


    public InsertTextPanel(JChemPaintPanel jChemPaintPanel, JFrame closeafter) {
        super();
        this.closeafter=closeafter;
        setLayout(new GridBagLayout());

        List oldText = new ArrayList();
        oldText.add("");

        textCombo = new JComboBox(oldText.toArray());
        textCombo.setEditable(true);
        textCombo.setToolTipText("Enter a CAS, SMILES or InChI string");

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
            if(closeafter!=null)
            	closeafter.setVisible(false);
        }
    }

    private IMolecule getMolecule() {

        IMolecule molecule;
        String text = (String) textCombo.getSelectedItem();
        text = text.trim(); // clean up extra white space

        if (text.equals("")) return null;

        if (text.startsWith("InChI")) { // handle it as an InChI
            try {
                InChIGeneratorFactory inchiFactory = new InChIGeneratorFactory();
                InChIToStructure inchiToStructure = inchiFactory.getInChIToStructure(text);
                INCHI_RET status = inchiToStructure.getReturnStatus();
                if (status != INCHI_RET.OKAY) {
                  JOptionPane.showMessageDialog(jChemPaintPanel, "Could not process InChI");
                  return null;
                }
                IAtomContainer atomContainer = inchiToStructure.getAtomContainer();
                molecule = atomContainer.getBuilder().newMolecule(atomContainer);
            } catch (CDKException e2) {
                JOptionPane.showMessageDialog(jChemPaintPanel, "Could not load InChI subsystem");
                return null;
            }
        } else if (isCASNumber(text)) { // is it a CAS number?
            try {
                molecule = getMoleculeFromCAS(text);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(jChemPaintPanel, "Error in reading data from PubChem");
                return null;
            }
        } else { // OK, it must be a SMILES
            SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
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

    private boolean isCASNumber(String text) {
        String[] chars = text.split("-");
        if (chars.length != 3) return false;
        for (int i = 0; i < 3; i++) {
            if (i == 2 && chars[i].length() != 1) return false;
            if (i == 1 && chars[i].length() != 2) return false;
            if (i == 0 && chars[i].length() > 6) return false;
            try {
                Integer.parseInt(chars[i]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private IMolecule getMoleculeFromCAS(String cas) throws IOException {
        String data;

        String firstURL = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=pccompound&term=" + cas;

        data = getDataFromURL(firstURL);

        Pattern pattern = Pattern.compile("http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi\\?cid=(\\d*)");
        Matcher matcher = pattern.matcher(data);

        String cid = null;
        boolean found = false;
        while (matcher.find()) {
            cid = matcher.group(1);
            try { // should be an integer
                Integer.parseInt(cid);
                found = true;
                break;
            } catch (NumberFormatException e) {
                continue;
            }
        }
        if (!found) return null;

        String secondURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?tool=jcppubchem&db=pccompound&id=" + cid;
        data = getDataFromURL(secondURL);

        pattern = Pattern.compile("<Item Name=\"CanonicalSmile\" Type=\"String\">([^\\s]*?)</Item>");
        matcher = pattern.matcher(data);
        String smiles = null;
        found = false;
        while (matcher.find()) {
            smiles = matcher.group(1);
            if (smiles != null || !smiles.equals("")) {
                found = true;
                break;
            }
        }
        if (!found) return null;

        // got the canonical SMILES, lets get the molecule
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule molecule;
        try {
            molecule = smilesParser.parseSmiles(smiles);
        } catch (InvalidSmilesException e1) {
            JOptionPane.showMessageDialog(jChemPaintPanel, "Couldn't process data from PubChem");
            return null;
        }
        return molecule;
    }

    private String getDataFromURL(String url) throws IOException {
        URL theURL = new URL(url);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(theURL.openStream()));
        String data = "";
        String line;
        while ((line = bufferedReader.readLine()) != null) data += line;
        bufferedReader.close();
        return data;
    }

    public void generateModel(IMolecule molecule) {
        if (molecule == null) return;

        // ok, get relevent bits from active model
        JChemPaintModel jcpModel = jChemPaintPanel.getJChemPaintModel();
        Renderer2DModel renderModel = jcpModel.getRendererModel();
        org.openscience.cdk.interfaces.IChemModel chemModel = jcpModel.getChemModel();
        org.openscience.cdk.interfaces.IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
        if (moleculeSet == null) {
            moleculeSet = new MoleculeSet();
        }

        // ok, now generate 2D coordinates
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setTemplateHandler(new TemplateHandler(moleculeSet.getBuilder()));
        try {
            sdg.setMolecule(molecule);
            sdg.generateCoordinates(new Vector2d(0, 1));
            molecule = sdg.getMolecule();
            double bondLength = renderModel.getBondLength();
            double scaleFactor = GeometryTools.getScaleFactor(molecule, bondLength,jChemPaintPanel.getJChemPaintModel().getRendererModel().getRenderingCoordinates());
            GeometryTools.scaleMolecule(molecule, scaleFactor, renderModel.getRenderingCoordinates());
            //if there are no atoms in the actual chemModel all 2D-coordinates would be set to NaN
            if (ChemModelManipulator.getAtomCount(chemModel) != 0) {
            	IAtomContainer container = chemModel.getBuilder().newAtomContainer();
            	Iterator containers = ChemModelManipulator.getAllAtomContainers(chemModel).iterator();
            	while (containers.hasNext()) {
            		container.add((IAtomContainer)containers.next());
            	}
                GeometryTools.translate2DCenterTo((IAtomContainer)molecule,
                		GeometryTools.get2DCenter(container,
                                jChemPaintPanel.getJChemPaintModel().getRendererModel().getRenderingCoordinates()
                        ),
                        jChemPaintPanel.getJChemPaintModel().getRendererModel().getRenderingCoordinates()
                );
            }
            GeometryTools.translate2D(molecule, 5 * bondLength, 0, renderModel.getRenderingCoordinates()); // in pixels
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        moleculeSet.addMolecule(molecule);
//        renderModel.setSelectedPart(m);
        jChemPaintPanel.getChemModel().setMoleculeSet(moleculeSet);
        jChemPaintPanel.scaleAndCenterMolecule(jChemPaintPanel.getChemModel());
        jcpModel.fireChange(jChemPaintPanel.getChemModel());
    }
}
