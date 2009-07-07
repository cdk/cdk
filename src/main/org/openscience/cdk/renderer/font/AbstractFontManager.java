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
 * @cdk.module renderbasic
 */
public abstract class AbstractFontManager implements IFontManager {
    
    private String fontName = "Arial";
    
    private IFontManager.FontStyle fontStyle;

	private Map<Double, Integer> zoomToFontSizeMap;

	// these two values track the font position if it falls
	// off the end of the array so that font and scale are always in synch
	private int lowerVirtualCount;

	private int upperVirtualCount;

	protected int currentFontIndex;

	public AbstractFontManager() {
		this.zoomToFontSizeMap = new TreeMap<Double, Integer>();
	}
	
	protected abstract void makeFonts();
	
	public String getFontName() {
	    return this.fontName;
	}
	
	public void setFontName(String fontName) {
	    if (this.fontName.equals(fontName)) {
	        return;
	    } else {
	        this.fontName = fontName;
	        makeFonts();
	    }
	}
	
	public void setFontStyle(IFontManager.FontStyle fontStyle) {
	    if (this.fontStyle == fontStyle) {
	        return;
	    } else {
	        this.fontStyle = fontStyle;
	        makeFonts();
	    }
	}
	
	public IFontManager.FontStyle getFontStyle() {
	    return this.fontStyle;
	}

	public void registerFontSizeMapping(double zoom, int size) {
		this.zoomToFontSizeMap.put(zoom, size);
	}

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

	public int getNumberOfFontSizes() {
		return this.zoomToFontSizeMap.size();
	}

	public void resetVirtualCounts() {
		this.lowerVirtualCount = 0;
		this.upperVirtualCount = this.getNumberOfFontSizes() - 1;
	}

	public void toMiddle() {
		this.currentFontIndex = this.getNumberOfFontSizes() / 2;
	}

	public void increaseFontSize() {
		// move INTO range if we have just moved OUT of lower virtual
		if (this.inRange() || (this.atMin() && this.atLowerBoundary())) {
			this.currentFontIndex++;
		} else if (this.atMax()){
			this.upperVirtualCount++;
		} else if (this.atMin() && this.inLower()){
			this.lowerVirtualCount++;
		}
	}

	public void decreaseFontSize() {
		// move INTO range if we have just moved OUT of upper virtual
		if (this.inRange() || (this.atMax() && this.atUpperBoundary())) {
			this.currentFontIndex--;
		} else if (this.atMin()) {
			this.lowerVirtualCount--;
		} else if (this.atMax() && this.inUpper()) {
			this.upperVirtualCount--;
		}
	}

	public boolean inRange() {
		return this.currentFontIndex > 0
				&& this.currentFontIndex < this.getNumberOfFontSizes() - 1;
	}

	public boolean atLowerBoundary() {
		return this.lowerVirtualCount == 0;
	}

	public boolean atUpperBoundary() {
		return this.upperVirtualCount == this.getNumberOfFontSizes() - 1;
	}

	public boolean inLower() {
		return this.lowerVirtualCount < 0;
	}

	public boolean inUpper() {
		return this.upperVirtualCount > this.getNumberOfFontSizes() - 1;
	}

	public boolean atMax() {
		return this.currentFontIndex == this.getNumberOfFontSizes() - 1;
	}

	public boolean atMin() {
		return this.currentFontIndex == 0;
	}
}
