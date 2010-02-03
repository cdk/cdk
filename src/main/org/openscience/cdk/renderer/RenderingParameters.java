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

import org.openscience.cdk.renderer.font.IFontManager;

/**
 * @cdk.module render
 */
public class RenderingParameters {

    /**
     * The size on screen of a compact mode shape
     */
    private double atomRadius = 8;

    /**
     * When atoms are selected or in compact mode, they will
     * be covered by a shape determined by this enumeration
     */
    public enum AtomShape { OVAL, SQUARE };
    
    /**
     * The width of an arrow head
     */
    private int arrowHeadWidth = 10;

    /**
     * The background color of the rendered image
     */
    private Color backColor = Color.white;

    /**
     * The gap between double and triple bond lines on the screen
     */
    private double bondDistance = 2;

    /**
     * The length on screen of a typical bond
     */
    private double bondLength = 40.0;

    /**
     * The width on screen of a bond
     */
    private double bondWidth = 1.0;

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
     * Determines whether atoms are colored by type
     */
    private boolean colorAtomsByType = true;

    /**
     * If true, atoms are displayed in a compact notation,
     * as a colored square or circle, rather than as text
     */
    private boolean compact = false;

    /**
     * The shape of the compact mode atom.
     */
    private AtomShape compactShape = AtomShape.SQUARE;

    /**
     * The color to draw bonds if not other color is given.
     */
    private Color defaultBondColor = Color.BLACK;

    private String fontName = "Arial";

    private IFontManager.FontStyle fontStyle = IFontManager.FontStyle.NORMAL;

    private Color externalHighlightColor = Color.orange;

    private boolean fitToScreen = false;

    private Color foreColor = Color.black;

    private Color hoverOverColor = Color.lightGray;

    /**
     * Determines whether structures should be drawn as Kekule structures, thus
     * giving each carbon element explicitly, instead of not displaying the
     * element symbol. Example C-C-C instead of /\.
     */
    private boolean kekuleStructure = false;


    /**
     * The maximum distance on the screen the mouse pointer has to be to
     * highlight an element.
     */
    private double highlightDistance = 8;

    private boolean highlightShapeFilled = false;

    private Color mappingColor = Color.gray;

    /**
     * Area on each of the four margins to keep white.
     */
    private double margin = 10;

    /**
     * The proportion of a ring bounds to use to draw the ring.
     */
    private double ringProportion = 0.35;

    /**
     * The factor to convert from model space to screen space.
     */
    private double scale = 1.0;

    private Color selectedPartColor = Color.lightGray;

    /**
     * The shape to display over selected atoms
     */
    private AtomShape selectionShape = AtomShape.SQUARE;

    /**
     * The radius on screen of the selection shape
     */
    private double selectionRadius = 3;

    /**
     * Determines whether rings should be drawn with a circle if they are
     * aromatic.
     */
    private boolean showAromaticity = true;

    private boolean showAromaticityInCDKStyle = false;

    private boolean showAtomAtomMapping = true;

    private boolean showAtomTypeNames = false;

    /**
     * Determines whether methyl carbons' symbols should be drawn explicit for
     * methyl carbons. Example C/\C instead of /\.
     */
    private boolean showEndCarbons = false;

    /** Determines whether explicit hydrogens should be drawn. */
    private boolean showExplicitHydrogens = true;

    /** Determines whether implicit hydrogens should be drawn. */
    private boolean showImplicitHydrogens = true;

    private boolean showMoleculeTitle = false;

    private boolean showReactionBoxes = true;

    private boolean showTooltip = false;

    private boolean useAntiAliasing = true;

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

    public boolean isShowAromaticityInCDKStyle() {
        return this.showAromaticityInCDKStyle;
    }

    public void setShowAromaticityInCDKStyle(boolean shouldShow) {
        this.showAromaticityInCDKStyle = shouldShow;
    }

    public double getWedgeWidth() {
        return wedgeWidth;
    }

    public void setWedgeWidth(double wedgeWidth) {
        this.wedgeWidth = wedgeWidth;
    }

    public double getRingProportion() {
        return ringProportion;
    }

    public void setRingProportion(double ringProportion) {
        this.ringProportion = ringProportion;
    }

    /**
     * @return the shape to draw the atoms when in compact mode
     */
    public AtomShape getCompactShape() {
        return compactShape;
    }

    /**
     * @param compactShape the shape to draw the atoms when in compact mode
     */
    public void setCompactShape(AtomShape compactShape) {
        this.compactShape = compactShape;
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

    public Color getDefaultBondColor() {
        return defaultBondColor;
    }

    public void setDefaultBondColor(Color defaultBondColor) {
        this.defaultBondColor = defaultBondColor;
    }

    public AtomShape getSelectionShape() {
        return this.selectionShape;
    }

    public void setSelectionShape(AtomShape selectionShape) {
        this.selectionShape = selectionShape;
    }

	public String getFontName() {
        return this.fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public IFontManager.FontStyle getFontStyle() {
        return this.fontStyle;
    }

    public void setFontStyle(IFontManager.FontStyle fontStyle) {
        this.fontStyle = fontStyle;
    }

    public double getAtomRadius() {
        return atomRadius;
    }

    public Color getBackColor() {
        return backColor;
    }

    public double getBondDistance() {
        return bondDistance;
    }

    public double getBondLength() {
        return bondLength;
    }

    public void setBondLength(double bondLength) {
        this.bondLength = bondLength;
    }

    public double getBondWidth() {
        return bondWidth;
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

    public double getMargin() {
        return margin;
    }

    public Color getSelectedPartColor() {
        return selectedPartColor;
    }

    public boolean isColorAtomsByType() {
        return colorAtomsByType;
    }

    public boolean isCompact() {
        return compact;
    }

    public boolean isKekuleStructure() {
        return kekuleStructure;
    }

    public boolean isShowAromaticity() {
        return showAromaticity;
    }

    public boolean isShowAtomAtomMapping() {
        return showAtomAtomMapping;
    }

    public boolean isShowAtomTypeNames() {
        return showAtomTypeNames;
    }

    public boolean isShowEndCarbons() {
        return showEndCarbons;
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

    public boolean isUseAntiAliasing() {
        return useAntiAliasing;
    }

    public boolean isWillDrawNumbers() {
        return willDrawNumbers;
    }

    public void setAtomRadius(double atomRadius) {
        this.atomRadius = atomRadius;
    }

    public void setBackColor(Color backColor) {
        this.backColor = backColor;
    }

    public void setBondDistance(double bondDistance) {
        this.bondDistance = bondDistance;
    }

    public void setBondWidth(double bondWidth) {
        this.bondWidth = bondWidth;
    }

    public void setMappingLineWidth(double mappingLineWidth) {
        this.mappingLineWidth = mappingLineWidth;
    }
    
    public void setColorAtomsByType(boolean colorAtomsByType) {
        this.colorAtomsByType = colorAtomsByType;
    }

    public void setCompact(boolean compact) {
        this.compact = compact;
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

    public void setKekuleStructure(boolean kekuleStructure) {
        this.kekuleStructure = kekuleStructure;
    }

    public void setMappingColor(Color mappingColor) {
        this.mappingColor = mappingColor;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

    public void setSelectedPartColor(Color selectedPartColor) {
        this.selectedPartColor = selectedPartColor;
    }

    public void setShowAromaticity(boolean showAromaticity) {
        this.showAromaticity = showAromaticity;
    }

    public void setShowAtomAtomMapping(boolean showAtomAtomMapping) {
        this.showAtomAtomMapping = showAtomAtomMapping;
    }

    public void setShowAtomTypeNames(boolean showAtomTypeNames) {
        this.showAtomTypeNames = showAtomTypeNames;
    }

    public void setShowEndCarbons(boolean showEndCarbons) {
        this.showEndCarbons = showEndCarbons;
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

    public void setUseAntiAliasing(boolean useAntiAliasing) {
        this.useAntiAliasing = useAntiAliasing;
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
