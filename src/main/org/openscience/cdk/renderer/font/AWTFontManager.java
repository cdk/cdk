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

import java.awt.Font;
import java.util.HashMap;

/**
 * @cdk.module renderbasic
 */
public class AWTFontManager extends AbstractFontManager {
	
	private HashMap<Integer, Font> fontSizeToFontMap;
	
	private int minFontSize;
	
	private Font currentFont;
	
	public AWTFontManager() {
		// apparently 9 pixels per em is the minimum
		// but I don't know if (size 9 == 9 px.em-1)... 
		this.minFontSize = 9;
		this.makeFonts();
		this.toMiddle();
		this.resetVirtualCounts();
	}
	
	protected void makeFonts() {
		int size = this.minFontSize;
		double scale = 0.5;
		this.fontSizeToFontMap = new HashMap<Integer, Font>();
		
		for (int i = 0; i < 20; i++) {
		    if (super.getFontStyle() == IFontManager.FontStyle.NORMAL) {
    			this.fontSizeToFontMap.put(size,
    					new Font(super.getFontName(), Font.PLAIN, size));
		    } else {
		        this.fontSizeToFontMap.put(size,
                        new Font(super.getFontName(), Font.BOLD, size));
		    }
			this.registerFontSizeMapping(scale, size);
			size += 1;
			scale += 0.1;
		}
	}
	
	public void setFontForZoom(double zoom) {
		int size = this.getFontSizeForZoom(zoom);
		if (size != -1) {
			this.currentFont = this.fontSizeToFontMap.get(size); 
		}
	}
	
	public Font getFont() {
		return currentFont;
	}
}
