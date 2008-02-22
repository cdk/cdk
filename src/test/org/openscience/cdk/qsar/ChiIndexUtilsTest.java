/* $Revision: 8431 $ $Author: rajarshi $ $Date: 2007-06-27 00:49:22 +0200 (Wed, 27 Jun 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.test.qsar;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-qsarmolecular
 */
public class ChiIndexUtilsTest extends CDKTestCase {

    public ChiIndexUtilsTest() {
    }

    public static Test suite() {
        return new TestSuite(ChiIndexUtilsTest.class);
    }

}

