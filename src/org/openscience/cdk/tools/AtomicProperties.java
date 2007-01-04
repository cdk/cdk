/* $RCSfile: $
 * $Author: egonw $
 * $Date: 2006-05-04 19:29:58 +0000 (Thu, 04 May 2006) $
 * $Revision: 6171 $
 *
 * Copyright (C) 2006  Todd Martin (Environmental Protection Agency)
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
import java.util.LinkedList;

public class AtomicProperties {

	private LoggingTool logger;
	
	private static AtomicProperties ap=null;
	
	private Hashtable htMass=new Hashtable();
	private Hashtable htVdWVolume=new Hashtable();
	private Hashtable htElectronegativity=new Hashtable();
	private Hashtable htPolarizability=new Hashtable();
	
	
	private AtomicProperties() throws IOException {
		
		logger = new LoggingTool(this);
		
	    String configFile = "src/org/openscience/cdk/config/data/whim_weights.txt";
	    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(ins));
		
		String Header= br.readLine(); // header
		
		String Line="";
		while (true) {
			Line=br.readLine();
			if (!(Line instanceof String)) {
				break;
			}
			
			LinkedList l = parseStringIntoList(Line,"\t");
			
			String symbol=(String)l.get(0);
			htMass.put(symbol,l.get(1));
			htVdWVolume.put(symbol,l.get(2));
			htElectronegativity.put(symbol,l.get(3));
			htPolarizability.put(symbol,l.get(4));
			
		}
						
		br.close();
	}

	public static LinkedList parseStringIntoList(String Line, String Delimiter) {
		// parses a delimited string into a list
		
		LinkedList myList = new LinkedList();
		
		int tabpos = 1;
		
		while (tabpos > -1) {
			tabpos = Line.indexOf(Delimiter);
			
			if (tabpos > 0) {
				myList.add(Line.substring(0, tabpos));
				Line = Line.substring(tabpos + 1, Line.length());
			} else if (tabpos == 0) {
				myList.add("");
				Line = Line.substring(tabpos + 1, Line.length());
			} else {
				myList.add(Line.trim());
			}
		}
		
		return myList;
		
	}

	public double getVdWVolume(String symbol) {
		double VdWVolume=-99;
		
		String strVdWVolume=(String)htVdWVolume.get(symbol);
		
		try {
			VdWVolume=Double.parseDouble(strVdWVolume);
		} catch (Exception e) {
			logger.error("Error while parsing the Vanderwaals volume: " + e.getMessage());
			logger.debug(e);
		}
		
		
		return VdWVolume;
		
	}
	
	public double getNormalizedVdWVolume(String symbol) {
		double VdWVolume=-99;
		
		VdWVolume=this.getVdWVolume(symbol)/this.getVdWVolume("C");
				
		return VdWVolume;
		
	}
	
	public double getElectronegativity(String symbol) {
		double Electronegativity=-99;
		
		String strElectronegativity=(String)htElectronegativity.get(symbol);
		
		try {
		Electronegativity=Double.parseDouble(strElectronegativity);
		} catch (Exception e) {
			logger.error("Error while parsing the electronegativity: " + e.getMessage());
			logger.debug(e);
		}
		
		
		return Electronegativity;
		
	}
	
	public double getNormalizedElectronegativity(String symbol) {
		double Electronegativity=-99;
		
		Electronegativity=this.getElectronegativity(symbol)/this.getElectronegativity("C");
				
		return Electronegativity;
		
	}
	public double getPolarizability(String symbol) {
		double Polarizability=-99;
		
		String strPolarizability=(String)htPolarizability.get(symbol);
		
		try {
		Polarizability=Double.parseDouble(strPolarizability);
		} catch (Exception e) {
			logger.error("Error while parsing the polarizability: " + e.getMessage());
			logger.debug(e);
		}
		
		
		return Polarizability;
		
	}
	
	public double getNormalizedPolarizability(String symbol) {
		double Polarizability=-99;
		
		Polarizability=this.getPolarizability(symbol)/this.getPolarizability("C");
				
		return Polarizability;
		
	}
	public double getMass(String symbol) {
		double mass=-99;
		
		String strMass=(String)htMass.get(symbol);
		
		try {
		mass=Double.parseDouble(strMass);
		
		} catch (Exception e) {
			logger.error("Error while parsing the mass: " + e.getMessage());
			logger.debug(e);
		}
		
		
		return mass;
		
	}
	
	public double getNormalizedMass(String symbol) {
		double mass=-99;
		
		mass=this.getMass(symbol)/this.getMass("C");
				
		return mass;
		
	}
	
	
	
	public static AtomicProperties getInstance() throws IOException
	{
		if (ap == null) {
			ap = new AtomicProperties();
		}
		return ap;
	}
}
