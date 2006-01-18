/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.controller;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.Reaction;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 *  Class that acts on MouseEvents and KeyEvents.
 *
 *@author         steinbeck
 *@author         egonw
 *@cdk.created    2. Mai 2005
 *@cdk.keyword    mouse events
 *@cdk.require    java1.4+
 */
public class SimpleController2D extends AbstractController2D
{
		
	SimpleController2D()
	{
		super();

	}

	SimpleController2D(Controller2DModel c2dm)
	{
		super(c2dm);
	}

	SimpleController2D(Renderer2DModel r2dm, Controller2DModel c2dm)
	{
		super(r2dm, c2dm);
	}
	
	Reaction getReactionInRange(int X, int Y) {
		return null;
	}
	
	Reaction getRelevantReaction(IChemModel chemModel, IAtom atom)
	{
		return null;
	}


}

