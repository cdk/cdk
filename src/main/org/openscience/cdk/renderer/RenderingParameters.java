/* Copyright (C) 2009  Gilleain Torrance <gilleain@users.sf.net>
 *               2009  Arvid Berg <goglepox@users.sf.net>
 *               2009  Egon Willighagen <egonw@users.sf.net>
 *               2009  Stefan Kuhn <shk3@users.sf.net>
 *
 * Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.renderer;

import java.awt.Color;

/**
 * @cdk.module render
 * @cdk.githash
 */
public class RenderingParameters {

    /**
     * When atoms are selected or in compact mode, they will
     * be covered by a shape determined by this enumeration
     */
//    public enum AtomShape { OVAL, SQUARE };
    
    /**
     * The width of an arrow head
     */
    private int arrowHeadWidth = 10;

    /**
     * The length on screen of a typical bond
     */
    private double bondLength = 40.0;

    /**
     * The width on screen of an atom-atom mapping line
     */
    private double mappingLineWidth = 1.0;
    
    /**
     * The color of the box drawn at the bounds of a
     * molecule, molecule set, or reaction
     */
    private Color boundsColor = Color.LIGHT_GRAY;

    /**
     * The shape of the compact mode atom.
     */
//    private AtomShape compactShape = AtomShape.SQUARE;

    private Color externalHighlightColor = Color.orange;

    private boolean fitToScreen = false;

    private Color foreColor = Color.black;

    private Color hoverOverColor = Color.lightGray;

    /**
     * The maximum distance on the screen the mouse pointer has to be to
     * highlight an element.
     */
    private double highlightDistance = 8;

    private boolean highlightShapeFilled = false;

    private Color mappingColor = Color.gray;

    /**
     * The factor to convert from model space to screen space.
     */
    private double scale = 1.0;

    private Color selectedPartColor = Color.lightGray;

    /**
     * The shape to display over selected atoms
     */
//    private AtomShape selectionShape = AtomShape.SQUARE;

    /**
     * The radius on screen of the selection shape
     */
    private double selectionRadius = 3;

    private boolean showAtomAtomMapping = true;

    private boolean showAtomTypeNames = false;

    /** Determines whether explicit hydrogens should be drawn. */
    private boolean showExplicitHydrogens = true;

    /** Determines whether implicit hydrogens should be drawn. */
    private boolean showImplicitHydrogens = true;

    private boolean showMoleculeTitle = false;

    private boolean showReactionBoxes = true;

    private boolean showTooltip = false;

    private boolean willDrawNumbers = false;

    /**
     * The width on screen of the fat end of a wedge bond.
     */
    private double wedgeWidth = 2.0;


    public int getArrowHeadWidth() {
        return arrowHeadWidth;
    }

    public void setArrowHeadWidth(int arrowHeadWidth) {
        this.arrowHeadWidth = arrowHeadWidth;
    }

    public boolean isHighlightShapeFilled() {
        return highlightShapeFilled;
    }

    public void setHighlightShapeFilled(boolean highlightShapeFilled) {
        this.highlightShapeFilled = highlightShapeFilled;
    }

    public double getWedgeWidth() {
        return wedgeWidth;
    }

    public void setWedgeWidth(double wedgeWidth) {
        this.wedgeWidth = wedgeWidth;
    }

    /**
     * The scale is the factor to multiply model coordinates by to convert to
     * coordinates in screen space.
     *
     * @return the scale
     */
    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getHighlightDistance() {
        return highlightDistance;
    }

    public void setHighlightDistance(double highlightDistance) {
        this.highlightDistance = highlightDistance;
    }

    public double getBondLength() {
        return bondLength;
    }

    public void setBondLength(double bondLength) {
        this.bondLength = bondLength;
    }

    public double getMappingLineWidth() {
        return mappingLineWidth;
    }
    
    public Color getExternalHighlightColor() {
        return externalHighlightColor;
    }

    public boolean isFitToScreen() {
        return fitToScreen;
    }

    public void setFitToScreen(boolean fitToScreen) {
        this.fitToScreen = fitToScreen;
    }

    public Color getForeColor() {
        return foreColor;
    }

    public Color getHoverOverColor() {
        return hoverOverColor;
    }

    public Color getMappingColor() {
        return mappingColor;
    }

    public Color getSelectedPartColor() {
        return selectedPartColor;
    }

    public boolean isShowAtomAtomMapping() {
        return showAtomAtomMapping;
    }

    public boolean isShowAtomTypeNames() {
        return showAtomTypeNames;
    }

    public boolean isShowExplicitHydrogens() {
        return showExplicitHydrogens;
    }

    public boolean isShowImplicitHydrogens() {
        return showImplicitHydrogens;
    }

    public boolean isShowMoleculeTitle() {
        return showMoleculeTitle;
    }

    public boolean isShowReactionBoxes() {
        return showReactionBoxes;
    }

    public boolean isShowTooltip() {
        return showTooltip;
    }

    public boolean isWillDrawNumbers() {
        return willDrawNumbers;
    }

    public void setMappingLineWidth(double mappingLineWidth) {
        this.mappingLineWidth = mappingLineWidth;
    }
    
    public void setExternalHighlightColor(Color externalHighlightColor) {
        this.externalHighlightColor = externalHighlightColor;
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
    }

    public void setHoverOverColor(Color hoverOverColor) {
        this.hoverOverColor = hoverOverColor;
    }

    public void setMappingColor(Color mappingColor) {
        this.mappingColor = mappingColor;
    }

    public void setSelectedPartColor(Color selectedPartColor) {
        this.selectedPartColor = selectedPartColor;
    }

    public void setShowAtomAtomMapping(boolean showAtomAtomMapping) {
        this.showAtomAtomMapping = showAtomAtomMapping;
    }

    public void setShowAtomTypeNames(boolean showAtomTypeNames) {
        this.showAtomTypeNames = showAtomTypeNames;
    }

    public void setShowExplicitHydrogens(boolean showExplicitHydrogens) {
        this.showExplicitHydrogens = showExplicitHydrogens;
    }

    public void setShowImplicitHydrogens(boolean showImplicitHydrogens) {
        this.showImplicitHydrogens = showImplicitHydrogens;
    }

    public void setShowMoleculeTitle(boolean showMoleculeTitle) {
        this.showMoleculeTitle = showMoleculeTitle;
    }

    public void setShowReactionBoxes(boolean showReactionBoxes) {
        this.showReactionBoxes = showReactionBoxes;
    }

    public void setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
    }

    public void setWillDrawNumbers(boolean willDrawNumbers) {
        this.willDrawNumbers = willDrawNumbers;
    }

    public Color getBoundsColor() {
        return this.boundsColor;
    }

    public void setBoundsColor(Color color) {
        this.boundsColor = color;
    }

	public double getSelectionRadius() {
		return this.selectionRadius;
	}

	public void setSelectionRadius(double selectionRadius) {
		this.selectionRadius = selectionRadius;
	}

}
