/* Copyright (C) 2003-2008  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.config.atomtypes;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.BondManipulator;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX Handler for the {@link OWLAtomTypeReader}.
 *
 * @cdk.module  core
 * @cdk.githash
 */
public class OWLAtomTypeHandler extends DefaultHandler {

    private final String              NS_ATOMTYPE = "http://cdk.sf.net/ontologies/atomtypes#";

    private static ILoggingTool       logger      = LoggingToolFactory.createLoggingTool(OWLAtomTypeHandler.class);
    private String                    currentChars;
    private List<IAtomType>           atomTypes;
    private IAtomType                 currentAtomType;
    private int                       piBondCount;
    private int                       neighborCount;
    private Order                     maxBondOrder;
    private double                    bondOrderSum;

    private static IChemObjectBuilder builder;

    /**
     * Constructs a new AtomTypeHandler and will create IAtomType
     * implementations using the given IChemObjectBuilder.
     *
     * @param build The IChemObjectBuilder used to create the IAtomType's.
     */
    public OWLAtomTypeHandler(IChemObjectBuilder build) {
        builder = build;
    }

    /**
     * Returns a List with read IAtomType's.
     *
     * @return The read IAtomType's.
     */
    public List<IAtomType> getAtomTypes() {
        return atomTypes;
    }

    // SAX Parser methods

    /** {@inheritDoc} */
    @Override
    public void startDocument() {
        atomTypes = new ArrayList<IAtomType>();
        currentAtomType = null;
    }

    /** {@inheritDoc} */
    @Override
    public void endElement(String uri, String local, String raw) {
        if (NS_ATOMTYPE.equals(uri)) {
            endAtomTypeElement(local);
        } // ignore other namespaces
        currentChars = "";
    }

    private void endAtomTypeElement(String local) {
        if ("AtomType".equals(local)) {
            atomTypes.add(currentAtomType);
            currentAtomType.setProperty(CDKConstants.PI_BOND_COUNT, piBondCount);
            currentAtomType.setFormalNeighbourCount(neighborCount);
            if (maxBondOrder != Order.UNSET) currentAtomType.setMaxBondOrder(maxBondOrder);
            if (bondOrderSum > 0.1) currentAtomType.setBondOrderSum(bondOrderSum);
        } else if ("formalCharge".equals(local)) {
            if (currentChars.charAt(0) == '+') {
                currentChars = currentChars.substring(1);
            }
            currentAtomType.setFormalCharge(Integer.parseInt(currentChars));
        } else if ("formalNeighbourCount".equals(local)) {
            neighborCount = Integer.parseInt(currentChars);
        } else if ("lonePairCount".equals(local)) {
            currentAtomType.setProperty(CDKConstants.LONE_PAIR_COUNT, Integer.parseInt(currentChars));
        } else if ("singleElectronCount".equals(local)) {
            currentAtomType.setProperty(CDKConstants.SINGLE_ELECTRON_COUNT, Integer.parseInt(currentChars));
        } else if ("piBondCount".equals(local)) {
            piBondCount = Integer.parseInt(currentChars);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void startElement(String uri, String local, String raw, Attributes atts) {
        currentChars = "";
        if (NS_ATOMTYPE.equals(uri)) {
            startAtomTypeElement(local, atts);
        } // ignore other namespaces
    }

    private void startAtomTypeElement(String local, Attributes atts) {
        if ("AtomType".equals(local)) {
            currentAtomType = builder.newInstance(IAtomType.class, "H");
            currentAtomType.setAtomicNumber(null);
            currentAtomType.setAtomTypeName(atts.getValue("rdf:ID"));
            piBondCount = 0;
            neighborCount = 0;
            maxBondOrder = Order.UNSET;
            bondOrderSum = 0.0;
        } else if ("hasElement".equals(local)) {
            String attrValue = atts.getValue("rdf:resource");
            currentAtomType.setSymbol(attrValue.substring(attrValue.indexOf('#') + 1));
        } else if ("formalBondType".equals(local)) {
            neighborCount++;
            String attrValue = atts.getValue("rdf:resource");
            String bondType = attrValue.substring(attrValue.indexOf('#') + 1);
            if ("single".equals(bondType)) {
                maxBondOrder = BondManipulator.getMaximumBondOrder(maxBondOrder, Order.SINGLE);
                bondOrderSum += 1.0;
            } else if ("double".equals(bondType)) {
                maxBondOrder = BondManipulator.getMaximumBondOrder(maxBondOrder, Order.DOUBLE);
                piBondCount++;
                bondOrderSum += 2.0;
            } else if ("triple".equals(bondType)) {
                maxBondOrder = BondManipulator.getMaximumBondOrder(maxBondOrder, Order.TRIPLE);
                piBondCount = piBondCount + 2;
                bondOrderSum += 3.0;
            } else if ("quadruple".equals(bondType)) {
                maxBondOrder = BondManipulator.getMaximumBondOrder(maxBondOrder, Order.QUADRUPLE);
                piBondCount = piBondCount + 3;
                bondOrderSum += 4.0;
            } // else: should throw an exception
        } else if ("hybridization".equals(local)) {
            String attrValue = atts.getValue("rdf:resource");
            String hybridization = attrValue.substring(attrValue.indexOf('#') + 1);
            if ("sp3".equals(hybridization)) {
                currentAtomType.setHybridization(IAtomType.Hybridization.SP3);
            } else if ("sp2".equals(hybridization)) {
                currentAtomType.setHybridization(IAtomType.Hybridization.SP2);
            } else if ("sp1".equals(hybridization)) {
                currentAtomType.setHybridization(IAtomType.Hybridization.SP1);
            } else if ("s".equals(hybridization)) {
                currentAtomType.setHybridization(IAtomType.Hybridization.S);
            } else if ("planar".equals(hybridization)) {
                currentAtomType.setHybridization(IAtomType.Hybridization.PLANAR3);
            } else if ("sp3d1".equals(hybridization)) {
                currentAtomType.setHybridization(IAtomType.Hybridization.SP3D1);
            } else if ("sp3d2".equals(hybridization)) {
                currentAtomType.setHybridization(IAtomType.Hybridization.SP3D2);
            } else if ("sp3d3".equals(hybridization)) {
                currentAtomType.setHybridization(IAtomType.Hybridization.SP3D3);
            } else if ("sp3d4".equals(hybridization)) {
                currentAtomType.setHybridization(IAtomType.Hybridization.SP3D4);
            } else if ("sp3d5".equals(hybridization)) {
                currentAtomType.setHybridization(IAtomType.Hybridization.SP3D5);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void characters(char chars[], int start, int length) {
        logger.debug("character data");
        currentChars += new String(chars, start, length);
    }

}
