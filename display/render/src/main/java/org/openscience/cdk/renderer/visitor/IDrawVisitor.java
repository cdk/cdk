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
package org.openscience.cdk.renderer.visitor;

import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.IRenderingVisitor;
import org.openscience.cdk.renderer.font.IFontManager;

/**
 * An {@link IDrawVisitor} is an {@link IRenderingVisitor} that can be
 * customized and knows about fonts and other rendering parameters.
 *
 * @cdk.module render
 * @cdk.githash
 */
public interface IDrawVisitor extends IRenderingVisitor {

    /**
     * Sets the {@link IFontManager} this {@link IDrawVisitor} should use.
     *
     * @param fontManager the {@link IFontManager} to be used
     */
    public void setFontManager(IFontManager fontManager);

    /**
     * Sets the {@link RendererModel} this {@link IDrawVisitor} should use.
     *
     * @param rendererModel the {@link RendererModel} to be used
     */
    public void setRendererModel(RendererModel rendererModel);

}
