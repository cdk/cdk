/* Copyright (C) 2006-2007,2014  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.io.cml;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IChemFile;

/**
 * TestCase for the {@link CMLModuleStack} class.
 *
 * @cdk.module test-io
 */
public class CMLModuleStackTest extends CDKTestCase {

    @Test
    public void testPush_String() {
        // the class has a hardcoded default length. Test going beyond this.
        CMLModuleStack stack = new CMLModuleStack();
        for (int i = 0; i < 100; i++) {
            stack.push(new CMLCoreModule((IChemFile) null));
        }
    }

    @Test
    public void testPop() {
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class,
                                () -> {
                                    CMLModuleStack stack = new CMLModuleStack();
                                    ICMLModule first = new CMLCoreModule((IChemFile) null);
                                    ICMLModule second = new CMLCoreModule((IChemFile) null);
                                    ICMLModule third = new CMLCoreModule((IChemFile) null);
                                    stack.push(first);
                                    stack.push(second);
                                    stack.push(third);
                                    Assertions.assertEquals(third, stack.pop());
                                    Assertions.assertEquals(second, stack.pop());
                                    Assertions.assertEquals(first, stack.pop());
                                    stack.pop();
                                });
    }

    @Test
    public void testCurrent() {
        CMLModuleStack stack = new CMLModuleStack();
        ICMLModule first = new CMLCoreModule((IChemFile) null);
        stack.push(first);
        Assertions.assertEquals(first, stack.current());
    }

    @Test
    public void testEndsWith_String() {
        CMLModuleStack stack = new CMLModuleStack();
        ICMLModule first = new CMLCoreModule((IChemFile) null);
        stack.push(first);
        Assertions.assertTrue(stack.endsWith(first));
        ICMLModule second = new CMLCoreModule((IChemFile) null);
        stack.push(second);
        Assertions.assertTrue(stack.endsWith(second));
    }

    @Test
    public void testEndsWith_String_String() {
        CMLModuleStack stack = new CMLModuleStack();
        ICMLModule first = new CMLCoreModule((IChemFile) null);
        stack.push(first);
        ICMLModule second = new CMLCoreModule((IChemFile) null);
        stack.push(second);
        Assertions.assertTrue(stack.endsWith(first, second));
        ICMLModule third = new CMLCoreModule((IChemFile) null);
        stack.push(third);
        Assertions.assertTrue(stack.endsWith(second, third));
    }

    @Test
    public void testEndsWith_String_String_String() {
        CMLModuleStack stack = new CMLModuleStack();
        ICMLModule first = new CMLCoreModule((IChemFile) null);
        stack.push(first);
        ICMLModule second = new CMLCoreModule((IChemFile) null);
        stack.push(second);
        ICMLModule third = new CMLCoreModule((IChemFile) null);
        stack.push(third);
        Assertions.assertTrue(stack.endsWith(first, second, third));
    }
}
