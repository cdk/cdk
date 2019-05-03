/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
 *               2010  Brian Gilman <gilmanb@gmail.com>
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
package org.openscience.cdk.io.pubchemxml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPseudoAtom;

/**
 * Helper class to parse PubChem XML documents.
 *
 * @cdk.module io
 * @cdk.githash
 *
 * @author Egon Willighagen &lt;egonw@users.sf.net&gt;
 * @cdk.created  2008-05-05
 */
public class PubChemXMLHelper {

    private IChemObjectBuilder builder;
    private IsotopeFactory     factory;

    /**
     * @throws java.io.IOException if there is error in getting the {@link IsotopeFactory}
     */
    public PubChemXMLHelper(IChemObjectBuilder builder) throws IOException {
        this.builder = builder;
        factory = Isotopes.getInstance();
    }

    // general elements
    public final static String EL_PCCOMPOUND        = "PC-Compound";
    public final static String EL_PCCOMPOUNDS       = "PC-Compounds";
    public final static String EL_PCSUBSTANCE       = "PC-Substance";
    public final static String EL_PCSUBSTANCE_SID   = "PC-Substance_sid";
    public final static String EL_PCCOMPOUND_ID     = "PC-Compound_id";
    public final static String EL_PCCOMPOUND_CID    = "PC-CompoundType_id_cid";
    public final static String EL_PCID_ID           = "PC-ID_id";

    // atom block elements
    public final static String EL_ATOMBLOCK         = "PC-Atoms";
    public final static String EL_ATOMSELEMENT      = "PC-Atoms_element";
    public final static String EL_ATOMSCHARGE       = "PC-Atoms_charge";
    public final static String EL_ATOMINT           = "PC-AtomInt";
    public final static String EL_ATOMINT_AID       = "PC-AtomInt_aid";
    public final static String EL_ATOMINT_VALUE     = "PC-AtomInt_value";
    public final static String EL_ELEMENT           = "PC-Element";

    // coordinate block elements
    public final static String EL_COORDINATESBLOCK  = "PC-Compound_coords";
    public final static String EL_COORDINATES_AID   = "PC-Coordinates_aid";
    public final static String EL_COORDINATES_AIDE  = "PC-Coordinates_aid_E";
    public final static String EL_ATOM_CONFORMER    = "PC-Conformer";
    public final static String EL_ATOM_CONFORMER_X  = "PC-Conformer_x";
    public final static String EL_ATOM_CONFORMER_XE = "PC-Conformer_x_E";
    public final static String EL_ATOM_CONFORMER_Y  = "PC-Conformer_y";
    public final static String EL_ATOM_CONFORMER_YE = "PC-Conformer_y_E";
    public final static String EL_ATOM_CONFORMER_Z  = "PC-Conformer_z";
    public final static String EL_ATOM_CONFORMER_ZE = "PC-Conformer_z_E";

    // bond block elements
    public final static String EL_BONDBLOCK         = "PC-Bonds";
    public final static String EL_BONDID1           = "PC-Bonds_aid1";
    public final static String EL_BONDID2           = "PC-Bonds_aid2";
    public final static String EL_BONDORDER         = "PC-Bonds_order";

    // property block elements
    public final static String EL_PROPSBLOCK        = "PC-Compound_props";
    public final static String EL_PROPS_INFODATA    = "PC-InfoData";
    public final static String EL_PROPS_URNLABEL    = "PC-Urn_label";
    public final static String EL_PROPS_URNNAME     = "PC-Urn_name";
    public final static String EL_PROPS_SVAL        = "PC-InfoData_value_sval";
    public final static String EL_PROPS_FVAL        = "PC-InfoData_value_fval";
    public final static String EL_PROPS_BVAL        = "PC-InfoData_value_binary";

    public IAtomContainerSet parseCompoundsBlock(XMLStreamReader parser) throws Exception {
        IAtomContainerSet set = builder.newInstance(IAtomContainerSet.class);

        // assume the current element is PC-Compounds
        if (!parser.getLocalName().equals(EL_PCCOMPOUNDS)) {
            return null;
        }

        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                if (EL_PCCOMPOUNDS.equals(parser.getLocalName())) {
                    break; // done parsing compounds block
                }
            } else if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                if (EL_PCCOMPOUND.equals(parser.getLocalName())) {
                    IAtomContainer molecule = parseMolecule(parser, builder);
                    if (molecule.getAtomCount() > 0) {
                        // skip empty PC-Compound's
                        set.addAtomContainer(molecule);
                    }
                }
            }
        }
        return set;
    }

    public IChemModel parseSubstance(XMLStreamReader parser) throws Exception {
        IChemModel model = builder.newInstance(IChemModel.class);
        // assume the current element is PC-Compound
        if (!parser.getLocalName().equals("PC-Substance")) {
            return null;
        }

        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                if (EL_PCSUBSTANCE.equals(parser.getLocalName())) {
                    break; // done parsing the molecule
                }
            } else if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                if (EL_PCCOMPOUNDS.equals(parser.getLocalName())) {
                    IAtomContainerSet set = parseCompoundsBlock(parser);
                    model.setMoleculeSet(set);
                } else if (EL_PCSUBSTANCE_SID.equals(parser.getLocalName())) {
                    String sid = getSID(parser);
                    model.setProperty(CDKConstants.TITLE, sid);
                }
            }
        }
        return model;
    }

    public String getSID(XMLStreamReader parser) throws Exception {
        String sid = "unknown";
        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                if (EL_PCSUBSTANCE_SID.equals(parser.getLocalName())) {
                    break; // done parsing the atom block
                }
            } else if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                if (EL_PCID_ID.equals(parser.getLocalName())) {
                    sid = parser.getElementText();
                }
            }
        }
        return sid;
    }

    public String getCID(XMLStreamReader parser) throws Exception {
        String cid = "unknown";
        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                if (EL_PCCOMPOUND_ID.equals(parser.getLocalName())) {
                    break; // done parsing the atom block
                }
            } else if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                if (EL_PCCOMPOUND_CID.equals(parser.getLocalName())) {
                    cid = parser.getElementText();
                }
            }
        }
        return cid;
    }

    public void parseAtomElements(XMLStreamReader parser, IAtomContainer molecule) throws Exception {
        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                if (EL_ATOMSELEMENT.equals(parser.getLocalName())) {
                    break; // done parsing the atom elements
                }
            } else if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                if (EL_ELEMENT.equals(parser.getLocalName())) {
                    int atomicNumber = Integer.parseInt(parser.getElementText());
                    IElement element = factory.getElement(atomicNumber);
                    if (element == null) {
                        IAtom atom = molecule.getBuilder().newInstance(IPseudoAtom.class);
                        molecule.addAtom(atom);
                    } else {
                        IAtom atom = molecule.getBuilder().newInstance(IAtom.class, element.getSymbol());
                        atom.setAtomicNumber(element.getAtomicNumber());
                        molecule.addAtom(atom);
                    }
                }
            }
        }
    }

    public void parserAtomBlock(XMLStreamReader parser, IAtomContainer molecule) throws Exception {
        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                if (EL_ATOMBLOCK.equals(parser.getLocalName())) {
                    break; // done parsing the atom block
                }
            } else if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                if (EL_ATOMSELEMENT.equals(parser.getLocalName())) {
                    parseAtomElements(parser, molecule);
                } else if (EL_ATOMSCHARGE.equals(parser.getLocalName())) {
                    parseAtomCharges(parser, molecule);
                }
            }
        }
    }

    public void parserCompoundInfoData(XMLStreamReader parser, IAtomContainer molecule) throws Exception {
        String urnLabel = null;
        String urnName = null;
        String sval = null;
        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                if (EL_PROPS_INFODATA.equals(parser.getLocalName())) {
                    break; // done parsing the atom block
                }
            } else if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                if (EL_PROPS_URNNAME.equals(parser.getLocalName())) {
                    urnName = parser.getElementText();
                } else if (EL_PROPS_URNLABEL.equals(parser.getLocalName())) {
                    urnLabel = parser.getElementText();
                } else if (EL_PROPS_SVAL.equals(parser.getLocalName())) {
                    sval = parser.getElementText();
                } else if (EL_PROPS_FVAL.equals(parser.getLocalName())) {
                    sval = parser.getElementText();
                } else if (EL_PROPS_BVAL.equals(parser.getLocalName())) {
                    sval = parser.getElementText();
                }
            }
        }
        if (urnLabel != null & sval != null) {
            String property = urnLabel + (urnName == null ? "" : " (" + urnName + ")");
            molecule.setProperty(property, sval);
        }
    }

    public void parseAtomCharges(XMLStreamReader parser, IAtomContainer molecule) throws Exception {
        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                if (EL_ATOMSCHARGE.equals(parser.getLocalName())) {
                    break; // done parsing the molecule
                }
            } else if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                if (EL_ATOMINT.equals(parser.getLocalName())) {
                    int aid = 0;
                    int charge = 0;
                    while (parser.next() != XMLEvent.END_DOCUMENT) {
                        if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                            if (EL_ATOMINT.equals(parser.getLocalName())) {
                                molecule.getAtom(aid - 1).setFormalCharge(charge);
                                break; // done parsing an atoms charge
                            }
                        } else if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                            if (EL_ATOMINT_AID.equals(parser.getLocalName())) {
                                aid = Integer.parseInt(parser.getElementText());
                            } else if (EL_ATOMINT_VALUE.equals(parser.getLocalName())) {
                                charge = Integer.parseInt(parser.getElementText());
                            }
                        }
                    }
                }
            }
        }
    }

    public IAtomContainer parseMolecule(XMLStreamReader parser, IChemObjectBuilder builder) throws Exception {
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        // assume the current element is PC-Compound
        if (!parser.getLocalName().equals("PC-Compound")) {
            return null;
        }

        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                if (EL_PCCOMPOUND.equals(parser.getLocalName())) {
                    break; // done parsing the molecule
                }
            } else if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                if (EL_ATOMBLOCK.equals(parser.getLocalName())) {
                    parserAtomBlock(parser, molecule);
                } else if (EL_BONDBLOCK.equals(parser.getLocalName())) {
                    parserBondBlock(parser, molecule);
                } else if (EL_COORDINATESBLOCK.equals(parser.getLocalName())) {
                    parserCoordBlock(parser, molecule);
                } else if (EL_PROPS_INFODATA.equals(parser.getLocalName())) {
                    parserCompoundInfoData(parser, molecule);
                } else if (EL_PCCOMPOUND_ID.equals(parser.getLocalName())) {
                    String cid = getCID(parser);
                    molecule.setProperty("PubChem CID", cid);
                }
            }
        }
        return molecule;
    }

    public void parserBondBlock(XMLStreamReader parser, IAtomContainer molecule) throws Exception {
        List<String> id1s = new ArrayList<String>();
        List<String> id2s = new ArrayList<String>();
        List<String> orders = new ArrayList<String>();
        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                if (EL_BONDBLOCK.equals(parser.getLocalName())) {
                    break; // done parsing the atom block
                }
            } else if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                if (EL_BONDID1.equals(parser.getLocalName())) {
                    id1s = parseValues(parser, EL_BONDID1, "PC-Bonds_aid1_E");
                } else if (EL_BONDID2.equals(parser.getLocalName())) {
                    id2s = parseValues(parser, EL_BONDID2, "PC-Bonds_aid2_E");
                } else if (EL_BONDORDER.equals(parser.getLocalName())) {
                    orders = parseValues(parser, EL_BONDORDER, "PC-BondType");
                }
            }
        }
        // aggregate information
        if (id1s.size() != id2s.size()) {
            throw new CDKException("Inequal number of atom identifier in bond block.");
        }
        if (id1s.size() != orders.size()) {
            throw new CDKException("Number of bond orders does not match number of bonds in bond block.");
        }
        for (int i = 0; i < id1s.size(); i++) {
            IAtom atom1 = molecule.getAtom(Integer.parseInt(id1s.get(i)) - 1);
            IAtom atom2 = molecule.getAtom(Integer.parseInt(id2s.get(i)) - 1);
            IBond bond = molecule.getBuilder().newInstance(IBond.class, atom1, atom2);
            int order = Integer.parseInt(orders.get(i));
            if (order == 1) {
                bond.setOrder(IBond.Order.SINGLE);
                molecule.addBond(bond);
            } else if (order == 2) {
                bond.setOrder(IBond.Order.DOUBLE);
                molecule.addBond(bond);
            }
            if (order == 3) {
                bond.setOrder(IBond.Order.TRIPLE);
                molecule.addBond(bond);
            } else {
                // unknown bond order, skip
            }
        }
    }

    public void parserCoordBlock(XMLStreamReader parser, IAtomContainer molecule) throws Exception {
        List<String> ids = new ArrayList<String>();
        List<String> xs = new ArrayList<String>();
        List<String> ys = new ArrayList<String>();
        List<String> zs = new ArrayList<String>();
        boolean parsedFirstConformer = false;
        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                if (EL_COORDINATESBLOCK.equals(parser.getLocalName())) {
                    break; // done parsing the atom block
                } else if (EL_ATOM_CONFORMER.equals(parser.getLocalName())) {
                    parsedFirstConformer = true;
                }
            } else if (parser.getEventType() == XMLEvent.START_ELEMENT && !parsedFirstConformer) {
                if (EL_COORDINATES_AID.equals(parser.getLocalName())) {
                    ids = parseValues(parser, EL_COORDINATES_AID, EL_COORDINATES_AIDE);
                } else if (EL_ATOM_CONFORMER_X.equals(parser.getLocalName())) {
                    xs = parseValues(parser, EL_ATOM_CONFORMER_X, EL_ATOM_CONFORMER_XE);
                } else if (EL_ATOM_CONFORMER_Y.equals(parser.getLocalName())) {
                    ys = parseValues(parser, EL_ATOM_CONFORMER_Y, EL_ATOM_CONFORMER_YE);
                } else if (EL_ATOM_CONFORMER_Z.equals(parser.getLocalName())) {
                    zs = parseValues(parser, EL_ATOM_CONFORMER_Z, EL_ATOM_CONFORMER_ZE);
                }
            }
        }
        // aggregate information
        boolean has2dCoords = ids.size() == xs.size() && ids.size() == ys.size();
        boolean has3dCoords = has2dCoords && ids.size() == zs.size();

        for (int i = 0; i < ids.size(); i++) {
            IAtom atom = molecule.getAtom(Integer.parseInt(ids.get(i)) - 1);
            if (has3dCoords) {
                Point3d coord = new Point3d(Double.parseDouble(xs.get(i)), Double.parseDouble(ys.get(i)),
                        Double.parseDouble(zs.get(i)));
                atom.setPoint3d(coord);
            } else if (has2dCoords) {
                Point2d coord = new Point2d(Double.parseDouble(xs.get(i)), Double.parseDouble(ys.get(i)));
                atom.setPoint2d(coord);
            }
        }
    }

    private List<String> parseValues(XMLStreamReader parser, String endTag, String fieldTag) throws Exception {
        List<String> values = new ArrayList<String>();
        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.END_ELEMENT) {
                if (endTag.equals(parser.getLocalName())) {
                    // done parsing the values
                    break;
                }
            } else if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                if (fieldTag.equals(parser.getLocalName())) {
                    String value = parser.getElementText();
                    values.add(value);
                }
            }
        }
        return values;
    }

}
