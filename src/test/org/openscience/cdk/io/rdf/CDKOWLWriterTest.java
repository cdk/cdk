/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@slists.sourceforge.net
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
package org.openscience.cdk.io.rdf;

import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.ChemObjectWriterTest;
import org.openscience.cdk.nonotify.NNMolecule;

/**
 * TestCase for the {@link CDKOWLWriter}.
 *
 * @cdk.module test-iordf
 */
public class CDKOWLWriterTest extends ChemObjectWriterTest {

    @BeforeClass public static void setup() {
        setChemObjectWriter(new CDKOWLWriter());
    }

    @Test public void testWriteMolecule() throws Exception {
        StringWriter output = new StringWriter();
        CDKOWLWriter writer = new CDKOWLWriter(output);

        IMolecule mol = new NNMolecule();
        writer.write(mol);
        String outputString = output.toString();
        Assert.assertTrue(outputString.contains(
            "http://cdk.sourceforge.net/model.owl#"
        ));
    }
}
