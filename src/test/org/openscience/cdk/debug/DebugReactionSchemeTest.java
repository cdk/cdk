/* $RCSfile$
 * $Author: egonw $    
 * $Date: 2006-04-12 09:16:35 +0000 (Wed, 12 Apr 2006) $    
 * $Revision: 5921 $
 * 
 * Copyright (C) 2008  Miguel Rojasch <miguelrojasch@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * 
 */
package org.openscience.cdk.debug;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IReactionScheme;
import org.openscience.cdk.interfaces.IReactionSchemeTest;


/**
 * Checks the functionality of the {@link DebugReactionScheme}.
 *
 * @cdk.module test-datadebug
 */
public class DebugReactionSchemeTest extends IReactionSchemeTest {

    @BeforeClass public static void setUp() {
        setBuilder(DebugChemObjectBuilder.getInstance());
    }

    @Test public void testReactionScheme() {
        IReactionScheme scheme = new DebugReactionScheme();
        Assert.assertNotNull(scheme);
    }
}
