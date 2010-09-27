/* $Revision$ $Author$ $Date$
*
*  Copyright (C) 2008 Gilleain Torrance <gilleain.torrance@gmail.com>
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

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;

/**
 * Combination generator for basic drawing of molecules. It only creates drawing
 * elements for atoms and bonds, using the {@link BasicAtomGenerator} and
 * {@link BasicBondGenerator}.
 * 
 * @cdk.module renderbasic
 * @author maclean
 */
public class BasicGenerator implements IGenerator<IAtomContainer> {
	
    /** Holder for various parameters, such as background color */
    private BasicSceneGenerator sceneGenerator;
    
    /** Generates elements for each atom in a container */
	private BasicAtomGenerator atomGenerator;
	
	/** Generates elements for each bond in a container */
	private BasicBondGenerator bondGenerator;
	
	/**
	 * Make a basic generator that creates elements for atoms and bonds.
	 */
	public BasicGenerator() {
		this.atomGenerator = new BasicAtomGenerator();
		this.bondGenerator = new BasicBondGenerator();
        this.sceneGenerator = new BasicSceneGenerator();
	}
	
	/** {@inheritDoc} */
	public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
		ElementGroup diagram = new ElementGroup();
        diagram.add(this.sceneGenerator.generate(ac, model));
		diagram.add(this.bondGenerator.generate(ac, model));
		diagram.add(this.atomGenerator.generate(ac, model));
		return diagram;
	}

    @Override
    /** {@inheritDoc} */
    public List<IGeneratorParameter<?>> getParameters() {
        return new ArrayList<IGeneratorParameter<?>>();
    }

}
