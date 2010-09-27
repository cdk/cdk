/* Copyright (C) 2009  Gilleain Torrance <gilleain@users.sf.net>
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
package org.openscience.cdk.renderer.font;

import java.util.Map;
import java.util.TreeMap;

/**
 * Implements the common parts of the {@link IFontManager} interface.
 * 
 * @cdk.module renderbasic
 * @cdk.githash
 */
public abstract class AbstractFontManager implements IFontManager {

    /** The default font family */
    private String fontName = "Arial";

    /** The font style - normal or bold */
    private IFontManager.FontStyle fontStyle;

    /** The mapping between zoom levels and font sizes */
    private Map<Double, Integer> zoomToFontSizeMap;

    // these two values track the font position if it falls
    // off the end of the array so that font and scale are always in synch
    private int lowerVirtualCount;

    private int upperVirtualCount;

    protected int currentFontIndex;

    /**
     * Call this in subclasses with the super() constructor.
     */
    public AbstractFontManager() {
        this.zoomToFontSizeMap = new TreeMap<Double, Integer>();
    }

    /**
     * Make widget-specific fonts.
     */
    protected abstract void makeFonts();

    /**
     * Get the font family name used in this font manager.
     * 
     * @return the font name
     */
    public String getFontName() {
        return this.fontName;
    }

    /** {@inheritDoc} */
    public void setFontName(String fontName) {
        if (this.fontName.equals(fontName)) {
            return;
        } else {
            this.fontName = fontName;
            makeFonts();
        }
    }

    /** {@inheritDoc} */
    public void setFontStyle(IFontManager.FontStyle fontStyle) {
        if (this.fontStyle == fontStyle) {
            return;
        } else {
            this.fontStyle = fontStyle;
            makeFonts();
        }
    }

    /**
     * Get the font style, defined in the {@link IFontManager} interface.
     *  
     * @return the font style
     */
    public IFontManager.FontStyle getFontStyle() {
        return this.fontStyle;
    }

    /**
     * For a particular zoom level, register a font point-size so that this
     * size of font will be used when the zoom is at this level. 
     * 
     * @param zoom the zoom level
     * @param size the font size
     */
    public void registerFontSizeMapping(double zoom, int size) {
        this.zoomToFontSizeMap.put(zoom, size);
    }

    /**
     * For a particular zoom, get the appropriate font size.
     *  
     * @param zoom the zoom level
     * @return an integer font size
     */
    protected Integer getFontSizeForZoom(double zoom) {
        double lower = -1;
        for (double upper : this.zoomToFontSizeMap.keySet()) {
            if (lower == -1) {
                lower = upper;
                if(zoom<=lower)
                    return this.zoomToFontSizeMap.get(upper);
                continue;
            }
            if (zoom > lower && zoom <= upper) {
                return this.zoomToFontSizeMap.get(upper);
            }
            lower = upper;
        }

        return this.zoomToFontSizeMap.get(lower);
    }

    /**
     * Get the number of font sizes used.
     * 
     * @return the size of the zoom to font map
     */
    public int getNumberOfFontSizes() {
        return this.zoomToFontSizeMap.size();
    }

    /**
     * Reset the virtual counts.
     */
    public void resetVirtualCounts() {
        this.lowerVirtualCount = 0;
        this.upperVirtualCount = this.getNumberOfFontSizes() - 1;
    }

    /**
     * Set the font size pointer to the middle of the range. 
     */
    public void toMiddle() {
        this.currentFontIndex = this.getNumberOfFontSizes() / 2;
    }

    /**
     * Move the font size pointer up. If this would move the pointer past
     * the maximum font size, track this increase with a virtual size.
     */
    public void increaseFontSize() {
        // move INTO range if we have just moved OUT of lower virtual
        if (inRange() || (atMin() && atLowerBoundary())) {
            currentFontIndex++;
        } else if (atMax()){
            upperVirtualCount++;
        } else if (atMin() && inLower()){
            lowerVirtualCount++;
        }
    }

    /**
     * Move the font size pointer down. If this would move the pointer past
     * the minimum font size, track this increase with a virtual size.
     */
    public void decreaseFontSize() {
        // move INTO range if we have just moved OUT of upper virtual
        if (inRange() || (atMax() && atUpperBoundary())) {
            currentFontIndex--;
        } else if (atMin()) {
            lowerVirtualCount--;
        } else if (atMax() && inUpper()) {
            upperVirtualCount--;
        }
    }

    /**
     * Check that the font pointer is in the range (0, numberOfFonts - 1). 
     * 
     * @return true if the current font index is between 0 and |fonts| - 1
     */
    public boolean inRange() {
        return currentFontIndex > 0 
            && currentFontIndex < getNumberOfFontSizes() - 1;
    }

    /**
     * Test the virtual font pointer to see if it is at the lower boundary of
     * the font size range (0).
     *  
     * @return true if the lower virtual count is zero
     */
    public boolean atLowerBoundary() {
        return this.lowerVirtualCount == 0;
    }

    /**
     * Test the virtual font pointer to see if it is at the upper boundary of
     * the font size range (|fonts| - 1).
     * 
     * @return true if the upper virtual count is |fonts| - 1
     */
    public boolean atUpperBoundary() {
        return this.upperVirtualCount == this.getNumberOfFontSizes() - 1;
    }

    /**
     * Test to see if the lower virtual pointer is in use.
     * 
     * @return true if the lower virtual count is less than zero
     */
    public boolean inLower() {
        return this.lowerVirtualCount < 0;
    }

    /**
     * Test to see if the upper virtual pointer is in use.
     * 
     * @return true if the upper virtual count is greater than |fonts| - 1
     */
    public boolean inUpper() {
        return this.upperVirtualCount > this.getNumberOfFontSizes() - 1;
    }

    /**
     * Check if the font pointer is as the maximum value.
     * 
     * @return true if the current font index is equal to |fonts| - 1
     */
    public boolean atMax() {
        return this.currentFontIndex == this.getNumberOfFontSizes() - 1;
    }

    /**
     * Check if the font pointer is as the minimum value.
     * 
     * @return true if the current font index is equal to zero
     */
    public boolean atMin() {
        return this.currentFontIndex == 0;
    }
}
