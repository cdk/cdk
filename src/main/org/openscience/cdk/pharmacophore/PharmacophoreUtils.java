/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.pharmacophore;

import nu.xom.*;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Provides some utility methods for pharmacophore handling.
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.svnrev $Revision$
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @cdk.builddepends xom-1.1.jar
 * @cdk.depends xom-1.1.jar
 */
@TestClass("org.openscience.cdk.pharmacophore.PharmacophoreUtilityTest")
public class PharmacophoreUtils {

    /**
     * Read in a set of pharmacophore definitions to create pharmacophore queries.
     * <p/>
     * Pharmacophore queries can be saved in an XML format which is described XXX. The
     * file can contain multiple definitions. This method will process all the definitions
     * and return a list fo {@link IQueryAtomContainer} objects which can be used with
     * the {@link PharmacophoreMatcher} class.
     * <p/>
     * The current schema for the document allows one to specify angle and distance
     * constraints. Currently the CDK does not support angle constraints, so they are
     * ignored.
     * <p/>
     * The schema also specifies a <i>units</i> attribute for a given constraint. The
     * current reader ignores this and assumes that all distances are in Angstroms.
     * <p/>
     * Finally, if there is a description associated with a pharmacophore definition, it is
     * available as the <i>"description"</i> property of the {@link IQueryAtomContainer} object.
     * <p/>
     * Example usage is
     * <pre>
     * List<IQueryAtomContainer> defs = readPharmacophoreDefinitions"mydefs.xml");
     * System.out.println("Number of definitions = "+defs.size());
     * for (int i = 0; i < defs.size(); i++) {
     *     System.out.println("Desc: "+defs.get(i).getProperty("description");
     * }
     * </pre>
     *
     * @param filename The file to read the definitions from
     * @return A list of {@link IQueryAtomContainer} objects
     * @throws CDKException if there is an error in the format
     * @throws IOException  if there is an error in opening the file
     * @see PharmacophoreQueryAtom
     * @see PharmacophoreQueryBond
     * @see PharmacophoreMatcher
     */
    @TestMethod("testReadPcoreDef, testInvalidPcoreXML")
    public static List<IQueryAtomContainer> readPharmacophoreDefinitions(String filename) throws CDKException, IOException {
        Builder parser = new Builder();
        Document doc;
        try {
            doc = parser.build(filename);
        } catch (ParsingException e) {
            throw new CDKException("Invalid pharmacophore definition file");
        }
        return getdefs(doc);
    }

    /**
     * Read in a set of pharmacophore definitions to create pharmacophore queries.
     * <p/>
     * Pharmacophore queries can be saved in an XML format which is described XXX. The
     * file can contain multiple definitions. This method will process all the definitions
     * and return a list of {@link IQueryAtomContainer} objects which can be used with
     * the {@link PharmacophoreMatcher} class.
     * <p/>
     * The current schema for the document allows one to specify angle and distance
     * constraints.
     * <p/>
     * The schema also specifies a <i>units</i> attribute for a given constraint. The
     * current reader ignores this and assumes that all distances are in Angstroms and
     * angles are in degrees.
     * <p/>
     * Finally, if there is a description associated with a pharmacophore definition, it is
     * available as the <i>"description"</i> property of the {@link IQueryAtomContainer} object.
     * <p/>
     * Example usage is
     * <pre>
     * List<IQueryAtomContainer> defs = readPharmacophoreDefinitions"mydefs.xml");
     * System.out.println("Number of definitions = "+defs.size());
     * for (int i = 0; i < defs.size(); i++) {
     *     System.out.println("Desc: "+defs.get(i).getProperty("description");
     * }
     * </pre>
     *
     * @param ins The stream to read the definitions from
     * @return A list of {@link IQueryAtomContainer} objects
     * @throws CDKException if there is an error in the format
     * @throws IOException  if there is an error in opening the file
     * @see PharmacophoreQueryAtom
     * @see PharmacophoreQueryBond
     * @see PharmacophoreMatcher
     */
    @TestMethod("testReadPcoreDef, testReadPcoreAngleDef, testInvalidPcoreXML")
    public static List<IQueryAtomContainer> readPharmacophoreDefinitions(InputStream ins) throws IOException, CDKException {
        Builder parser = new Builder();
        Document doc;
        try {
            doc = parser.build(ins);
        } catch (ParsingException e) {
            throw new CDKException("Invalid pharmacophore definition file");
        }
        return getdefs(doc);
    }

    /**
     * Write out one or more pharmacophore queries in the CDK XML format.
     *
     * @param query The pharmacophore queries
     * @param out   The OutputStream to write to
     * @throws IOException if there is a problem writing the XML document
     */
    @TestMethod("testPCoreWrite")
    public static void writePharmacophoreDefinition(IQueryAtomContainer query, OutputStream out) throws IOException {
        writePharmacophoreDefinition(new IQueryAtomContainer[]{query}, out);
    }

    /**
     * Write out one or more pharmacophore queries in the CDK XML format.
     *
     * @param queries The pharmacophore queries
     * @param out     The OutputStream to write to
     * @throws IOException if there is a problem writing the XML document
     */
    @TestMethod("testPCoreWrite")
    public static void writePharmacophoreDefinition(List<IQueryAtomContainer> queries, OutputStream out) throws IOException {
        writePharmacophoreDefinition(queries.toArray(new IQueryAtomContainer[]{}), out);
    }

    /**
     * Write out one or more pharmacophore queries in the CDK XML format.
     *
     * @param queries The pharmacophore queries
     * @param out     The OutputStream to write to
     * @throws IOException if there is a problem writing the XML document
     */
    @TestMethod("testPCoreWrite")
    public static void writePharmacophoreDefinition(IQueryAtomContainer[] queries, OutputStream out) throws IOException {
        Element root = new Element("pharmacophoreContainer");
        root.addAttribute(new Attribute("version", "1.0"));
        for (IQueryAtomContainer query : queries) {
            Element pcore = new Element("pharmacophore");

            Object description = query.getProperty("description");
            if (description != null)
                pcore.addAttribute(new Attribute("description", (String) description));

            Object name = query.getProperty(CDKConstants.TITLE);
            if (name != null)
                pcore.addAttribute(new Attribute("name", (String) name));

            // we add the pcore groups for this query as local to the group
            Iterator<IAtom> atoms = query.atoms().iterator();
            while (atoms.hasNext()) {
                IAtom atom = atoms.next();
                Element group = new Element("group");
                group.addAttribute(new Attribute("id", atom.getSymbol()));
                group.appendChild(((PharmacophoreQueryAtom) atom).getSmarts());
                pcore.appendChild(group);
            }

            // now add the constraints
            Iterator<IBond> bonds = query.bonds().iterator();
            while (bonds.hasNext()) {
                IBond bond = bonds.next();
                Element elem = null;
                if (bond instanceof PharmacophoreQueryBond) {
                    PharmacophoreQueryBond dbond = (PharmacophoreQueryBond) bond;
                    elem = new Element("distanceConstraint");
                    elem.addAttribute(new Attribute("lower", String.valueOf(dbond.getLower())));
                    elem.addAttribute(new Attribute("upper", String.valueOf(dbond.getUpper())));
                    elem.addAttribute(new Attribute("units", "A"));
                } else if (bond instanceof PharmacophoreQueryAngleBond) {
                    PharmacophoreQueryAngleBond dbond = (PharmacophoreQueryAngleBond) bond;
                    elem = new Element("angleConstraint");
                    elem.addAttribute(new Attribute("lower", String.valueOf(dbond.getLower())));
                    elem.addAttribute(new Attribute("upper", String.valueOf(dbond.getUpper())));
                    elem.addAttribute(new Attribute("units", "degrees"));
                }

                // now add the group associated with this constraint
                Iterator<IAtom> constraintGroups = bond.atoms().iterator();
                while (constraintGroups.hasNext()) {
                    PharmacophoreQueryAtom atom = (PharmacophoreQueryAtom) constraintGroups.next();
                    Element gelem = new Element("groupRef");
                    gelem.addAttribute(new Attribute("id", atom.getSymbol()));
                    if (elem != null) {
                        elem.appendChild(gelem);
                    }
                }
                pcore.appendChild(elem);
            }
            root.appendChild(pcore);
        }
        Document doc = new Document(root);
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setIndent(4);
        serializer.setMaxLength(128);
        serializer.write(doc);
    }

    private static List<IQueryAtomContainer> getdefs(Document doc) throws CDKException {
        Element root = doc.getRootElement();

        // ltes get the children of the container
        // these will be either group or pharmacophore elems
        List<IQueryAtomContainer> ret = new ArrayList<IQueryAtomContainer>();

        // get global group defs
        HashMap<String, String> groups = getGroupDefinitions(root);

        //now get the pcore defs
        Elements children = root.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            Element e = children.get(i);
            if (e.getQualifiedName().equals("pharmacophore"))
                ret.add(processPharmacophoreElement(e, groups));
        }
        return ret;
    }

    /* find all <group> elements that are directly under the supplied element
    so this wont recurse through sub elements that may contain group elements */
    private static HashMap<String, String> getGroupDefinitions(Element e) {
        HashMap<String, String> groups = new HashMap<String, String>();
        Elements children = e.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            Element child = children.get(i);
            if (child.getQualifiedName().equals("group")) {
                String id = child.getAttributeValue("id").trim();
                String smarts = child.getValue().trim();
                groups.put(id, smarts);
            }
        }
        return groups;
    }

    /* process a single pcore definition */
    private static IQueryAtomContainer processPharmacophoreElement(Element e,
                                                                   HashMap<String, String> global) throws CDKException {
        QueryAtomContainer ret = new QueryAtomContainer();
        ret.setProperty("description", e.getAttributeValue("description"));
        ret.setProperty(CDKConstants.TITLE, e.getAttributeValue("name"));

        // first get any local group definitions
        HashMap<String, String> local = getGroupDefinitions(e);

        // now lets look at the constraints
        Elements children = e.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            Element child = children.get(i);
            if (child.getQualifiedName().equals("distanceConstraint")) {
                processDistanceConstraint(child, local, global, ret);
            } else if (child.getQualifiedName().equals("angleConstraint")) {
                processAngleConstraint(child, local, global, ret);
            }
        }
        return ret;
    }

    private static void processDistanceConstraint(Element child,
                                                  HashMap<String, String> local,
                                                  HashMap<String, String> global,
                                                  QueryAtomContainer ret) throws CDKException {
        double lower;
        String tmp = child.getAttributeValue("lower");
        if (tmp == null) throw new CDKException("Must have a 'lower' attribute");
        else lower = Double.parseDouble(tmp);

        // we may not have an upper bound specified
        double upper;
        tmp = child.getAttributeValue("upper");
        if (tmp != null) upper = Double.parseDouble(tmp);
        else upper = lower;

        // now get the two groups for this distance
        Elements grouprefs = child.getChildElements();
        if (grouprefs.size() != 2) throw new CDKException("A distance constraint can only refer to 2 groups.");
        String id1 = grouprefs.get(0).getAttributeValue("id");
        String id2 = grouprefs.get(1).getAttributeValue("id");

        // see if it's a local def, else get it from the global list
        String smarts1, smarts2;
        if (local.containsKey(id1)) smarts1 = local.get(id1);
        else if (global.containsKey(id1)) smarts1 = global.get(id1);
        else throw new CDKException("Referring to a non-existant group definition");

        if (local.containsKey(id2)) smarts2 = local.get(id2);
        else if (global.containsKey(id2)) smarts2 = global.get(id2);
        else throw new CDKException("Referring to a non-existant group definition");

        // now see if we already have a correpsondiong pcore atom
        // else create a new atom
        if (!containsPatom(ret, id1)) {
            PharmacophoreQueryAtom pqa = new PharmacophoreQueryAtom(id1, smarts1);
            ret.addAtom(pqa);
        }
        if (!containsPatom(ret, id2)) {
            PharmacophoreQueryAtom pqa = new PharmacophoreQueryAtom(id2, smarts2);
            ret.addAtom(pqa);
        }

        // now add the constraint as a bond
        IAtom a1 = null, a2 = null;
        Iterator<IAtom> atoms = ret.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom queryAtom = atoms.next();
            if (queryAtom.getSymbol().equals(id1)) a1 = queryAtom;
            if (queryAtom.getSymbol().equals(id2)) a2 = queryAtom;
        }
        ret.addBond(new PharmacophoreQueryBond((PharmacophoreQueryAtom) a1, (PharmacophoreQueryAtom) a2, lower, upper));
    }

    private static void processAngleConstraint(Element child,
                                               HashMap<String, String> local,
                                               HashMap<String, String> global,
                                               QueryAtomContainer ret) throws CDKException {
        double lower;
        String tmp = child.getAttributeValue("lower");
        if (tmp == null) throw new CDKException("Must have a 'lower' attribute");
        else lower = Double.parseDouble(tmp);

        // we may not have an upper bound specified
        double upper;
        tmp = child.getAttributeValue("upper");
        if (tmp != null) upper = Double.parseDouble(tmp);
        else upper = lower;

        // now get the three groups for this distance
        Elements grouprefs = child.getChildElements();
        if (grouprefs.size() != 3) throw new CDKException("An angle constraint can only refer to 3 groups.");
        String id1 = grouprefs.get(0).getAttributeValue("id");
        String id2 = grouprefs.get(1).getAttributeValue("id");
        String id3 = grouprefs.get(2).getAttributeValue("id");

        // see if it's a local def, else get it from the global list
        String smarts1, smarts2, smarts3;
        if (local.containsKey(id1)) smarts1 = local.get(id1);
        else if (global.containsKey(id1)) smarts1 = global.get(id1);
        else throw new CDKException("Referring to a non-existant group definition");

        if (local.containsKey(id2)) smarts2 = local.get(id2);
        else if (global.containsKey(id2)) smarts2 = global.get(id2);
        else throw new CDKException("Referring to a non-existant group definition");

        if (local.containsKey(id3)) smarts3 = local.get(id3);
        else if (global.containsKey(id3)) smarts3 = global.get(id3);
        else throw new CDKException("Referring to a non-existant group definition");

        // now see if we already have a correpsondiong pcore atom
        // else create a new atom
        if (!containsPatom(ret, id1)) {
            PharmacophoreQueryAtom pqa = new PharmacophoreQueryAtom(id1, smarts1);
            ret.addAtom(pqa);
        }
        if (!containsPatom(ret, id2)) {
            PharmacophoreQueryAtom pqa = new PharmacophoreQueryAtom(id2, smarts2);
            ret.addAtom(pqa);
        }
        if (!containsPatom(ret, id3)) {
            PharmacophoreQueryAtom pqa = new PharmacophoreQueryAtom(id3, smarts3);
            ret.addAtom(pqa);
        }

        // now add the constraint as a bond
        IAtom a1 = null, a2 = null;
        IAtom a3 = null;
        Iterator<IAtom> atoms = ret.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom queryAtom = atoms.next();
            if (queryAtom.getSymbol().equals(id1)) a1 = queryAtom;
            if (queryAtom.getSymbol().equals(id2)) a2 = queryAtom;
            if (queryAtom.getSymbol().equals(id3)) a3 = queryAtom;
        }
        ret.addBond(new PharmacophoreQueryAngleBond(
                (PharmacophoreQueryAtom) a1,
                (PharmacophoreQueryAtom) a2,
                (PharmacophoreQueryAtom) a3, lower, upper));
    }

    private static boolean containsPatom(IQueryAtomContainer q, String id) {
        Iterator<IAtom> atoms = q.atoms().iterator();
        while (atoms.hasNext()) {
            IQueryAtom queryAtom = (IQueryAtom) atoms.next();
            if (queryAtom.getSymbol().equals(id)) return true;
        }
        return false;
    }

}
