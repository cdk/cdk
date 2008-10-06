/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2006-2007  Todd Martin (Environmental Protection Agency)
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
package org.openscience.cdk.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;

/**
 * Provides atomic property values for descriptor calculations.
 *
 * This class currently provides values for mass, Vanderwaals volume, electronegativity and
 * polarizability.
 *
 * @author     Todd Martin
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 */
public class AtomicProperties {

    private static AtomicProperties ap=null;

	private Map<String, Double> htMass=new Hashtable<String,Double>();
	private Map<String, Double> htVdWVolume=new Hashtable<String, Double>();
	private Map<String, Double> htElectronegativity=new Hashtable<String, Double>();
	private Map<String, Double> htPolarizability=new Hashtable<String, Double>();


	private AtomicProperties() throws IOException {

        String configFile = "org/openscience/cdk/config/data/whim_weights.txt";
	    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ins));
        bufferedReader.readLine(); // header

		String Line;
		while (true) {
			Line=bufferedReader.readLine();
			if (Line == null) {
				break;
			}
			String[] components = Line.split("\t");

			String symbol=components[0];
			htMass.put(symbol,Double.parseDouble(components[1]));
			htVdWVolume.put(symbol, Double.parseDouble(components[2]));
			htElectronegativity.put(symbol, Double.parseDouble(components[3]));
			htPolarizability.put(symbol, Double.parseDouble(components[4]));
		}

		bufferedReader.close();
	}



	public double getVdWVolume(String symbol) {
		return htVdWVolume.get(symbol);
	}

	public double getNormalizedVdWVolume(String symbol) {
		return this.getVdWVolume(symbol)/this.getVdWVolume("C");
	}

	public double getElectronegativity(String symbol) {
		return htElectronegativity.get(symbol);
	}

	public double getNormalizedElectronegativity(String symbol) {
		return this.getElectronegativity(symbol)/this.getElectronegativity("C");
	}
	public double getPolarizability(String symbol) {
		return htPolarizability.get(symbol);
	}

	public double getNormalizedPolarizability(String symbol) {
		return this.getPolarizability(symbol)/this.getPolarizability("C");
	}

    public double getMass(String symbol) {
		return htMass.get(symbol);
	}

	public double getNormalizedMass(String symbol) {
		return this.getMass(symbol)/this.getMass("C");
	}



	public static AtomicProperties getInstance() throws IOException
	{
		if (ap == null) {
			ap = new AtomicProperties();
		}
		return ap;
	}


}
