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

/**
 * An interface for managing the drawing of fonts at different zoom levels.
 *
 * @author maclean
 * @cdk.module render
 * @cdk.githash
 */
public interface IFontManager {

    /**
     * Style of the font to use to draw text.
     */
    public enum FontStyle {
        /** Regular font style. */
        NORMAL,
        /** Bold font style. */
        BOLD
    }

    /**
     * For a particular zoom level, set the appropriate font size to use.
     *
     * @param zoom a real number in the range (0.0, INF)
     */
    public void setFontForZoom(double zoom);

    /**
     * Set the font style.
     *
     * @param fontStyle an {@link FontStyle} type
     */
    public void setFontStyle(IFontManager.FontStyle fontStyle);

    /**
     * Set the font name ('Arial', 'Times New Roman') and so on.
     *
     * @param fontName name of the font to use
     */
    public void setFontName(String fontName);

}
