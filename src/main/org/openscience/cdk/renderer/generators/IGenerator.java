/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.renderer.generators;

import java.util.List;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.IRenderingElement;

/**
 * An {@link IGenerator} converts chemical entities into parts of the 
 * chemical drawing expressed as {@link IRenderingElement}s.
 * 
 * Note that some generators have explicit empty constructors (like:
 * "public MyGenerator() {}") which can be useful in some situations where
 * reflection is required. It is not, however, necessary for most normal
 * drawing situations.
 * 
 * @cdk.module  render
 * @cdk.githash
 */
public interface IGenerator<T extends IChemObject>  {

	/**
	 * Returns the list of {@link IGeneratorParameter} for this particular
	 * generator.
	 * 
	 * @return a {@link List} of {@link IGeneratorParameter}s
	 */
	public List<IGeneratorParameter<?>> getParameters();

	/**
	 * Converts a {@link IChemObject} from the chemical data model into
	 * something that can be drawn in the chemical drawing.
	 * 
	 * @param object the chemical entity to be depicted
	 * @param model  the rendering parameters
	 * @return       a drawable chemical depiction component
	 */
	public IRenderingElement generate(T object, RendererModel model); 

}