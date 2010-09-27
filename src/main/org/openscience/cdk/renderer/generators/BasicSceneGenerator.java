/* Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
 *               2010  Egon Willighagen <egonw@users.sf.net>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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
package org.openscience.cdk.renderer.generators;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.font.IFontManager.FontStyle;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;

/**
 * This generator does not create any elements, but acts as a holding place
 * for various generator parameters used by most drawings, such as the zoom,
 * background color, margin, etc.
 * 
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class BasicSceneGenerator implements IGenerator<IAtomContainer> {

    public static class ShowTooltip extends 
    AbstractGeneratorParameter<Boolean> {
        public Boolean getDefault() {
            return Boolean.FALSE;
        }
    }
    private ShowTooltip showTooltip = new ShowTooltip();

    public static class ShowMoleculeTitle extends 
    AbstractGeneratorParameter<Boolean> {
        public Boolean getDefault() {
            return Boolean.FALSE;
        }
    }
    private ShowMoleculeTitle showMoleculeTitle = new ShowMoleculeTitle();

    public static class FitToScreen extends 
    AbstractGeneratorParameter<Boolean> {
        public Boolean getDefault() {
            return Boolean.FALSE;
        }
    }
    /** If true, the scale is set such that the diagram fills the whole screen*/
    private FitToScreen fitToScreen = new FitToScreen();

    /**
     * The scale is the factor to multiply model coordinates by to convert the
     * coordinates to screen space coordinate, such that the entire structure
     * fits the visible screen dimension.
     */
    public static class Scale extends
    AbstractGeneratorParameter<Double> {
        public Double getDefault() {
            return 1.0;
        }
    }
    private IGeneratorParameter<Double> scale = new Scale();	

    public static class BackgroundColor extends
    AbstractGeneratorParameter<Color> {
        public Color getDefault() {
            return Color.WHITE;
        }
    }
    private IGeneratorParameter<Color> backgroundColor = new BackgroundColor();

    public static class ForegroundColor extends
    AbstractGeneratorParameter<Color> {
        public Color getDefault() {
            return Color.BLACK;
        }
    }
    private IGeneratorParameter<Color> foregroundColor = new ForegroundColor();

    public static class UseAntiAliasing extends
    AbstractGeneratorParameter<Boolean> {
        public Boolean getDefault() {
            return Boolean.TRUE;
        }
    }
    private IGeneratorParameter<Boolean> useAntiAliasing = new UseAntiAliasing();

    /**
     * Area on each of the four margins to keep white.
     */
    public static class Margin extends
    AbstractGeneratorParameter<Double> {
        public Double getDefault() {
            return 10.0;
        }
    }
    private IGeneratorParameter<Double> margin = new Margin();

    public static class UsedFontStyle extends
    AbstractGeneratorParameter<FontStyle> {
        public FontStyle getDefault() {
            return FontStyle.NORMAL;
        }
    }
    private IGeneratorParameter<FontStyle> fontStyle = new UsedFontStyle();

    public static class FontName extends
    AbstractGeneratorParameter<String> {
        public String getDefault() {
            return "Arial";
        }
    }
    private IGeneratorParameter<String> fontName = new FontName();

    public static class ZoomFactor extends
    AbstractGeneratorParameter<Double> {
        public Double getDefault() {
            return 1.0;
        }
    }
    /** A zoom of 100% is defined to be a value of 1.0 */
    private IGeneratorParameter<Double> zoomFactor = new ZoomFactor();

    /**
     * An empty constructor necessary for reflection.
     */
    public BasicSceneGenerator() {}

    /** {@inheritDoc} */
    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        return new ElementGroup();
    }

    /** {@inheritDoc} */
    public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(
                new IGeneratorParameter<?>[] {
                        backgroundColor,
                        foregroundColor,
                        margin,
                        useAntiAliasing,
                        fontStyle,
                        fontName,
                        zoomFactor,
                        scale,
                        fitToScreen,
                        showMoleculeTitle,
                        showTooltip
                }
        );
    }
}
