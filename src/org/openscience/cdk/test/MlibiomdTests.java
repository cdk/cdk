/* $Revision: 7636 $ $Author: ospjuth $ $Date: 2007-01-04 17:46:10 +0000 (Thu, 04 Jan 2007) $
 * 
 * Copyright (C) 2006-2007  Sam Adams <sea36@users.sf.net>
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
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.libio.md.MDMoleculeTest;

/**
 * TestSuite that runs all the sample tests for the CDK module libiomd.
 *
 * @cdk.module test-libiomd
 */
public class MlibiomdTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("libiomd inchi module Tests");
        
        suite.addTest(MDMoleculeTest.suite());
        
        return suite;
    }

}
