/* $RCSfile$   
 * $Author$   
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.applications.swing.editor;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.openscience.cdk.applications.swing.FieldTablePanel;
import org.openscience.cdk.renderer.Renderer2DModel;

import com.ozten.font.JFontChooser;

/**
 * @cdk.module applications
 * @cdk.builddepends jfontchooser.jar
 * @cdk.require swing
 */
public class Renderer2DModelEditor extends FieldTablePanel implements ActionListener {
    
    private JCheckBox drawNumbers;
    private JCheckBox showAtomAtomMapping;
    private JCheckBox useKekuleStructure;
    private JCheckBox showEndCarbons;
    private JCheckBox showImplicitHydrogens;
    private JCheckBox showAromaticity;
    private JCheckBox showAromaticityInCDKStyle;
    private JCheckBox colorAtomsByType;
    private JCheckBox showToolTip;
    private JCheckBox showReactionBoxes;
    private JCheckBox useAA;
    private JLabel fontName;
    private JButton chooseFontButton;
    private Font currentFont;
    
    private JFrame frame;
    
    private Renderer2DModel model;
    
	public Renderer2DModelEditor(JFrame frame) {
        super();
        this.frame = frame;
        constructPanel();
	}
    
    private void constructPanel() {
        currentFont = null;
        drawNumbers = new JCheckBox();
        addField("Draw atom numbers", drawNumbers);
        showAtomAtomMapping = new JCheckBox();
        addField("Show atom-atom mappings", showAtomAtomMapping);
        useKekuleStructure = new JCheckBox();
        addField("Explicit carbons", useKekuleStructure);
        showEndCarbons = new JCheckBox();
        addField("Show explicit methyl groups", showEndCarbons);
        showImplicitHydrogens = new JCheckBox();
        addField("Show implicit hydrogens", showImplicitHydrogens);
        showAromaticity = new JCheckBox();
        addField("Use aromatic ring circles", showAromaticity);
        showAromaticityInCDKStyle = new JCheckBox();
        addField("Use CDK style aromaticity indicators", showAromaticityInCDKStyle);
        colorAtomsByType = new JCheckBox();
        addField("Color atoms by element", colorAtomsByType);
        useAA = new JCheckBox();
        addField("Use Anti-Aliasing", useAA);
        showToolTip = new JCheckBox();
        addField("Show tooltips", showToolTip);        
        showReactionBoxes = new JCheckBox();
        addField("Show boxes around reactions", showReactionBoxes);        
        fontName = new JLabel();
        addField("Font name", fontName);
        chooseFontButton = new JButton("Choose Font...");
        chooseFontButton.addActionListener(this);
        chooseFontButton.setActionCommand("chooseFont");
        addField("", chooseFontButton);
    }
    
    public void setModel(Renderer2DModel model) {
        this.model = model;
        drawNumbers.setSelected(model.getDrawNumbers());
        showAtomAtomMapping.setSelected(model.getShowAtomAtomMapping());
        useKekuleStructure.setSelected(model.getKekuleStructure());
        showEndCarbons.setSelected(model.getShowEndCarbons());
        showImplicitHydrogens.setSelected(model.getShowImplicitHydrogens());
        showAromaticity.setSelected(model.getShowAromaticity());
        showAromaticityInCDKStyle.setSelected(model.getShowAromaticityInCDKStyle());
        colorAtomsByType.setSelected(model.getColorAtomsByType());
        useAA.setSelected(model.getUseAntiAliasing());
        showToolTip.setSelected(model.getShowTooltip());
        showReactionBoxes.setSelected(model.getShowReactionBoxes());
        currentFont = model.getFont();
        if (currentFont != null) {
            fontName.setText(currentFont.getFontName());
        }
        validate();
    }
	
    public void applyChanges() {
        model.setDrawNumbers(drawNumbers.isSelected());
        model.setShowAtomAtomMapping(showAtomAtomMapping.isSelected());
        model.setKekuleStructure(useKekuleStructure.isSelected());
        model.setShowEndCarbons(showEndCarbons.isSelected());
        model.setShowImplicitHydrogens(showImplicitHydrogens.isSelected());
        model.setShowAromaticity(showAromaticity.isSelected());
        model.setShowAromaticityInCDKStyle(showAromaticityInCDKStyle.isSelected());
        model.setColorAtomsByType(colorAtomsByType.isSelected());
        model.setUseAntiAliasing(useAA.isSelected());
        model.setShowTooltip(showToolTip.isSelected());
        model.setShowReactionBoxes(showReactionBoxes.isSelected());
        model.setFont(currentFont);
    }
    
    /**
     * Required by the ActionListener interface.
     */
    public void actionPerformed(ActionEvent e) {
        if ("chooseFont".equals(e.getActionCommand())) {
            Font newFont = JFontChooser.showDialog(this.frame, "Choose a Font", "Carbon Dioxide", currentFont);
            if (newFont != null) {
                currentFont = newFont;
                fontName.setText(currentFont.getFontName());
            }
        }
    }     
}


