/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
 * 
 * This code has been kindly provided by Stephane Werner
 * and Thierry Hanser from IXELIS mail@ixelis.net
 * 
 * IXELIS sarl - Semantic Information Systems
 * 17 rue des C???res 67200 Strasbourg, France
 * Tel/Fax : +33(0)3 88 27 81 39 Email: mail@ixelis.net
 * 
 * CDK Contact: cdk-devel@lists.sf.net
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.isomorphism.matchers;

import org.openscience.cdk.Atom;

/**
 * @cdk.module dontcompile
 */
public abstract class QueryAtom extends Atom{
	public boolean matches(Atom atom) {};
}

