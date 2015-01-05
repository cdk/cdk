/* Copyright (C) 2001-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
package org.openscience.cdk.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.openscience.cdk.config.isotopes.IsotopeReader;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;

/**
 * Used to store and return data of a particular isotope. As this class is a
 * singleton class, one gets an instance with:
 * <pre>
 * IsotopeFactory ifac = IsotopFactory.getInstance(new IChemObject().getNewBuilder());
 * </pre>
 *
 * <p>Data about the isotopes are read from the file
 * org.openscience.cdk.config.isotopes.xml in the cdk-standard
 * module. Part of the data in this file was collected from
 * the website <a href="http://www.webelements.org">webelements.org</a>.
 *
 * <p>The use of this class is exemplified as follows. To get information
 * about the major isotope of hydrogen, one can use this code:
 * <pre>
 *   IsotopeFactory factory = XMLIsotopeFactory.getInstance(DefaultChemObjectBuilder.getInstance());
 *   Isotope major = factory.getMajorIsotope("H");
 * </pre>
 *
 * @cdk.module     extra
 * @cdk.githash
 *
 * @author     steinbeck
 * @cdk.created    2001-08-29
 * @cdk.keyword    isotope
 * @cdk.keyword    element
 */
public class XMLIsotopeFactory extends IsotopeFactory {

    private static XMLIsotopeFactory ifac  = null;
    private boolean                  debug = false;

    /**
     * Private constructor for the IsotopeFactory object.
     *
     *@exception IOException             A problem with reading the isotopes.xml
     *      file
     * @param builder The builder from which we the factory will be generated
     */
    private XMLIsotopeFactory(IChemObjectBuilder builder) throws IOException {
        logger.info("Creating new IsotopeFactory");

        InputStream ins;
        // ObjIn in = null;
        String errorMessage = "There was a problem getting org.openscience.cdk." + "config.isotopes.xml as a stream";
        try {
            String configFile = "org/openscience/cdk/config/data/isotopes.xml";
            if (debug) logger.debug("Getting stream for ", configFile);
            ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        } catch (Exception exception) {
            logger.error(errorMessage);
            logger.debug(exception);
            throw new IOException(errorMessage);
        }
        if (ins == null) {
            logger.error(errorMessage);
            throw new IOException(errorMessage);
        }
        IsotopeReader reader = new IsotopeReader(ins, builder);
        //in = new ObjIn(ins, new Config().aliasID(false));
        this.isotopes = new HashMap<String, List<IIsotope>>();
        List<IIsotope> isotopes = reader.readIsotopes();
        for (IIsotope isotope : isotopes)
            add(isotope);
        if (debug) logger.debug("Found #isotopes in file: ", isotopes.size());
        /*
         * for (int f = 0; f < isotopes.size(); f++) { Isotope isotope =
         * (Isotope)isotopes.elementAt(f); } What's this loop for??
         */

        majorIsotopes = new HashMap<String, IIsotope>();
    }

    /**
     * Returns an IsotopeFactory instance.
     *
         * @param      builder                 ChemObjectBuilder used to construct the Isotope's
     * @return                             The instance value
     * @exception  IOException  if isotopic data files could not be read.
     */
    public static XMLIsotopeFactory getInstance(IChemObjectBuilder builder) throws IOException {
        if (ifac == null) {
            ifac = new XMLIsotopeFactory(builder);
        }
        return ifac;
    }

}
