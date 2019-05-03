/* Copyright (C) 1997-2010  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.normalize;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class NormalizerTest extends CDKTestCase {

    @Test
    public void testNormalize() throws Exception {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("N"));
        ac.addAtom(new Atom("O"));
        ac.addAtom(new Atom("O"));
        ac.addBond(new Bond(ac.getAtom(0), ac.getAtom(1)));
        ac.addBond(new Bond(ac.getAtom(1), ac.getAtom(2), IBond.Order.DOUBLE));
        ac.addBond(new Bond(ac.getAtom(1), ac.getAtom(3), IBond.Order.DOUBLE));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element set = doc.createElement("replace-set");
        doc.appendChild(set);
        Element replace = doc.createElement("replace");
        set.appendChild(replace);
        replace.appendChild(doc.createTextNode("O=N=O"));
        Element replacement = doc.createElement("replacement");
        set.appendChild(replacement);
        replacement.appendChild(doc.createTextNode("[O-][N+]=O"));
        Normalizer.normalize(ac, doc);
        Assert.assertTrue(ac.getBond(1).getOrder() == IBond.Order.SINGLE
                ^ ac.getBond(2).getOrder() == IBond.Order.SINGLE);
    }
}
