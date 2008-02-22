/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.tools;

import java.util.Hashtable;

import org.openscience.cdk.exception.CDKException;

/**
 * @cdk.keyword HOSE code
 * @cdk.svnrev  $Revision$
 */
public class BremserOneSphereHOSECodePredictor implements java.io.Serializable
{

    private static final long serialVersionUID = 4382025930031432321L;
    
    Hashtable ht;
	
	public BremserOneSphereHOSECodePredictor()
	{
		ht = new Hashtable(700);
		prepareHashTable();
	}
	
	public double predict(String hoseCode) throws CDKException
	{
		if (!(hoseCode == null) && ht.containsKey(hoseCode))
		{
			return ((HOSECodeShiftRange)ht.get(hoseCode)).shift;
		}
		throw new CDKException("No prediction available for HOSE code " +  hoseCode);
	}

	public String predictFull(String hoseCode) throws CDKException
	{
		if (!(hoseCode == null) && ht.containsKey(hoseCode))
		{
			return ((HOSECodeShiftRange)ht.get(hoseCode)).toString();
		}
		throw new CDKException("No prediction available for HOSE code " +  hoseCode);
	}


	public double getConfidenceLimit(String hoseCode) throws CDKException
	{
		if (!(hoseCode == null) && ht.containsKey(hoseCode))
		{
			return ((HOSECodeShiftRange)ht.get(hoseCode)).confidenceLimit;
		}
		throw new CDKException("No confidence limit available for HOSE code " +  hoseCode);
	}

	class HOSECodeShiftRange implements java.io.Serializable
	{

        private static final long serialVersionUID = 1649047314594679297L;
        
        String code;
		double shift;
		double confidenceLimit;
		String multiplicity;
		double maxShift;
		double minShift;
		int fourSphereCount;
		int lineCount;
		
		HOSECodeShiftRange(String c, double s, double conf, String m, double ma, double mi, int f, int l)
		{
			code = c;
			shift = s;
			confidenceLimit = conf;
			multiplicity = m;
			maxShift = ma;
			minShift = mi;
			fourSphereCount = f;
			lineCount = l;
		}
		
		public String toString()
		{
			String s = "delta_C: " + shift + " for code " + code + " with confidence limit " + confidenceLimit;
			s += " in shift range " + maxShift + "-" + minShift + ". ";
			return s; 
		}
		
	}
	
	public String getBibData()
	{
		String s = "The carbon NMR chemical shift prediction of this module \n";
		s += "is based on the 651 1-sphere HOSE-Code table published by W. Bremser in:\n";
		s += "W. Bremser, \"Expectation Ranges of 13C NMR Chemical Shifts\", \n";
		s += "Mag. Res. Chem., Vol. 23, No. 4, 1985, 271-275.\n";
		s += "It is important to understand, that these values are indeed not more than expectation ranges.\n";
		return s;
	}
	
	private void prepareHashTable()
	{
		ht.put("%CC$(//)", new HOSECodeShiftRange("%CC$(//)", 79.3, 16.4, "S", 112.7, 54.4, 36, 54));
		ht.put("%CC(//)", new HOSECodeShiftRange("%CC(//)", 81.8, 21.2, "S", 144.7, 32.9, 547, 1402));
		ht.put("%CO(//)", new HOSECodeShiftRange("%CO(//)", 88.2, 5.0, "S", 88.2, 88.2, 1, 1));
		ht.put("%CN(//)", new HOSECodeShiftRange("%CN(//)", 81.4, 5.0, "S", 83.6, 79.3, 2, 2));
		ht.put("%CS(//)", new HOSECodeShiftRange("%CS(//)", 80.2, 28.0, "S", 99.8, 68.1, 6, 7));
		ht.put("%CP$(//)", new HOSECodeShiftRange("%CP$(//)", 74.8, 5.0, "S", 77.9, 71.2, 8, 10));
		ht.put("%CP(//)", new HOSECodeShiftRange("%CP(//)", 77.7, 21.3, "S", 106.9, 60.4, 15, 25));
		ht.put("%CQ(//)", new HOSECodeShiftRange("%CQ(//)", 110.2, 30.4, "S", 132.3, 82.6, 15, 29));
		ht.put("%CF(//)", new HOSECodeShiftRange("%CF(//)", 90.1, 5.0, "S", 90.1, 90.1, 1, 1));
		ht.put("%CX(//)", new HOSECodeShiftRange("%CX(//)", 65.2, 35.3, "S", 79.8, 56.7, 3, 4));
		ht.put("%CY(//)", new HOSECodeShiftRange("%CY(//)", 41.7, 13.8, "S", 50.1, 37.0, 6, 9));
		ht.put("%CI(//)", new HOSECodeShiftRange("%CI(//)", 0.0, 17.6, "S", 9.0, -6.3, 3, 5));
		ht.put("%C(//)", new HOSECodeShiftRange("%C(//)", 72.6, 13.3, "D", 96.3, 22.0, 103, 265));
		ht.put("%NC$(//)", new HOSECodeShiftRange("%NC$(//)", 113.0, 5.0, "S", 113.1, 112.9, 2, 2));
		ht.put("%NC(//)", new HOSECodeShiftRange("%NC(//)", 116.7, 8.4, "S", 133.2, 37.7, 806, 2046));
		ht.put("%NO(//)", new HOSECodeShiftRange("%NO(//)", 109.1, 5.0, "S", 109.7, 108.5, 1, 7));
		ht.put("%NN(//)", new HOSECodeShiftRange("%NN(//)", 116.8, 6.6, "S", 124.1, 105.7, 34, 57));
		ht.put("%NS(//)", new HOSECodeShiftRange("%NS(//)", 116.9, 16.7, "S", 134.1, 107.0, 27, 73));
		ht.put("%NP(//)", new HOSECodeShiftRange("%NP(//)", 119.1, 5.0, "S", 120.3, 118.0, 2, 2));
		ht.put("%NB(//)", new HOSECodeShiftRange("%NB(//)", 165.4, 5.0, "S", 165.4, 165.4, 1, 1));
		ht.put("%N(//)", new HOSECodeShiftRange("%N(//)", 112.7, 5.0, "D", 112.8, 112.6, 1, 2));
		ht.put("%PC(//)", new HOSECodeShiftRange("%PC(//)", 164.9, 5.0, "S", 164.9, 164.9, 1, 1));
		ht.put("=C=C(//)", new HOSECodeShiftRange("=C=C(//)", 189.9, 51.3, "S", 235.4, 97.4, 108, 156));
		ht.put("=C=O(//)", new HOSECodeShiftRange("=C=O(//)", 172.0, 68.1, "S", 206.2, 18.5, 26, 37));
		ht.put("=C=N(//)", new HOSECodeShiftRange("=C=N(//)", 197.4, 28.2, "S", 227.6, 186.7, 15, 15));
		ht.put("=C=S(//)", new HOSECodeShiftRange("=C=S(//)", 269.1, 5.0, "S", 269.1, 269.1, 1, 1));
		ht.put("=CC$C$(//)", new HOSECodeShiftRange("=CC$C$(//)", 111.1, 28.4, "S", 153.7, 80.5, 130, 289));
		ht.put("=CC$C(//)", new HOSECodeShiftRange("=CC$C(//)", 126.9, 32.3, "S", 187.4, 63.1, 1243, 3017));
		ht.put("=CC$O(//)", new HOSECodeShiftRange("=CC$O(//)", 148.5, 25.4, "S", 201.0, 118.0, 207, 409));
		ht.put("=CC$N$$(//)", new HOSECodeShiftRange("=CC$N$$(//)", 130.5, 36.0, "S", 143.3, 120.9, 4, 4));
		ht.put("=CC$N(//)", new HOSECodeShiftRange("=CC$N(//)", 129.8, 29.0, "S", 161.4, 90.2, 257, 546));
		ht.put("=CC$S$$(//)", new HOSECodeShiftRange("=CC$S$$(//)", 113.8, 5.0, "S", 115.3, 112.4, 3, 4));
		ht.put("=CC$S$(//)", new HOSECodeShiftRange("=CC$S$(//)", 115.0, 12.1, "S", 121.8, 110.3, 4, 5));
		ht.put("=CC$S(//)", new HOSECodeShiftRange("=CC$S(//)", 131.3, 39.0, "S", 185.8, 57.9, 86, 133));
		ht.put("=CC$P$(//)", new HOSECodeShiftRange("=CC$P$(//)", 125.1, 5.0, "S", 130.5, 119.7, 1, 2));
		ht.put("=CC$F(//)", new HOSECodeShiftRange("=CC$F(//)", 142.7, 9.4, "S", 152.1, 135.5, 5, 12));
		ht.put("=CC$X(//)", new HOSECodeShiftRange("=CC$X(//)", 131.3, 28.0, "S", 188.0, 87.4, 73, 164));
		ht.put("=CC$Y(//)", new HOSECodeShiftRange("=CC$Y(//)", 116.1, 23.9, "S", 134.8, 89.1, 30, 43));
		ht.put("=CC$I(//)", new HOSECodeShiftRange("=CC$I(//)", 88.6, 32.8, "S", 105.7, 72.7, 5, 5));
		ht.put("=CC$(//)", new HOSECodeShiftRange("=CC$(//)", 119.6, 25.2, "D", 166.9, 63.5, 1159, 3433));
		ht.put("=CCC(//)", new HOSECodeShiftRange("=CCC(//)", 136.2, 34.9, "S", 238.3, 39.4, 3854, 10753));
		ht.put("=CC$CC(//)", new HOSECodeShiftRange("=CC$CC(//)", 124.6, 17.4, "*", 135.8, 112.4, 5, 15));
		ht.put("=CC$C(//)", new HOSECodeShiftRange("=CC$C(//)", 113.5, 21.5, "S", 121.1, 107.3, 2, 3));
		ht.put("=CC$(//)", new HOSECodeShiftRange("=CC$(//)", 109.8, 5.0, "D", 109.8, 109.8, 1, 1));
		ht.put("=CCO(//)", new HOSECodeShiftRange("=CCO(//)", 161.6, 28.5, "S", 208.4, 110.7, 883, 1892));
		ht.put("=CCN$$(//)", new HOSECodeShiftRange("=CCN$$(//)", 137.8, 21.4, "S", 164.4, 120.5, 62, 97));
		ht.put("=CCN(//)", new HOSECodeShiftRange("=CCN(//)", 141.8, 27.7, "S", 181.7, 76.9, 892, 2035));
		ht.put("=CCS$$(//)", new HOSECodeShiftRange("=CCS$$(//)", 131.8, 60.2, "S", 152.8, 69.0, 11, 15));
		ht.put("=CCS$(//)", new HOSECodeShiftRange("=CCS$(//)", 144.5, 30.8, "S", 153.0, 109.2, 7, 10));
		ht.put("=CCS(//)", new HOSECodeShiftRange("=CCS(//)", 139.7, 34.2, "S", 181.5, 78.1, 265, 519));
		ht.put("=CCP$(//)", new HOSECodeShiftRange("=CCP$(//)", 124.1, 41.3, "S", 146.5, 95.3, 8, 11));
		ht.put("=CCP(//)", new HOSECodeShiftRange("=CCP(//)", 126.5, 73.3, "S", 161.3, 30.8, 21, 31));
		ht.put("=CCQ(//)", new HOSECodeShiftRange("=CCQ(//)", 141.3, 58.1, "S", 183.7, 73.7, 26, 41));
		ht.put("=CCB(//)", new HOSECodeShiftRange("=CCB(//)", 151.2, 115.0, "S", 180.1, 127.3, 3, 3));
		ht.put("=CCF(//)", new HOSECodeShiftRange("=CCF(//)", 149.7, 26.5, "S", 180.0, 121.8, 17, 22));
		ht.put("=CCX(//)", new HOSECodeShiftRange("=CCX(//)", 132.0, 17.3, "S", 166.8, 87.9, 267, 746));
		ht.put("=CCY(//)", new HOSECodeShiftRange("=CCY(//)", 110.7, 29.8, "S", 161.5, 80.6, 82, 99));
		ht.put("=CCI(//)", new HOSECodeShiftRange("=CCI(//)", 91.8, 48.7, "S", 130.6, 64.2, 7, 8));
		ht.put("=CC(//)", new HOSECodeShiftRange("=CC(//)", 127.8, 25.7, "D", 223.5, 12.1, 7600, 21001));
		ht.put("=C$=C$(//)", new HOSECodeShiftRange("=C$=C$(//)", -14.6, 5.0, "S", -14.6, -14.6, 1, 1));
		ht.put("=C$C$C$(//)", new HOSECodeShiftRange("=C$C$C$(//)", 48.3, 5.0, "S", 48.3, 48.3, 1, 1));
		ht.put("=C$CC(//)", new HOSECodeShiftRange("=C$CC(//)", 25.9, 47.7, "S", 51.7, -5.3, 7, 9));
		ht.put("=C$CP(//)", new HOSECodeShiftRange("=C$CP(//)", 39.2, 5.0, "S", 39.2, 39.2, 1, 1));
		ht.put("=C$CX(//)", new HOSECodeShiftRange("=C$CX(//)", 85.0, 5.0, "S", 85.0, 85.0, 1, 1));
		ht.put("=C$C(//)", new HOSECodeShiftRange("=C$C(//)", 19.3, 38.2, "D", 28.6, 10.9, 3, 3));
		ht.put("=C$X(//)", new HOSECodeShiftRange("=C$X(//)", 70.1, 5.0, "D", 70.1, 70.1, 1, 1));
		ht.put("=C$(//)", new HOSECodeShiftRange("=C$(//)", 2.7, 5.0, "I", 2.9, 2.5, 1, 3));
		ht.put("=COO(//)", new HOSECodeShiftRange("=COO(//)", 168.6, 18.7, "S", 184.2, 141.9, 43, 81));
		ht.put("=CON$$(//)", new HOSECodeShiftRange("=CON$$(//)", 152.7, 5.0, "S", 153.7, 151.5, 4, 11));
		ht.put("=CON(//)", new HOSECodeShiftRange("=CON(//)", 158.1, 16.2, "S", 173.8, 129.8, 69, 122));
		ht.put("=COS$$(//)", new HOSECodeShiftRange("=COS$$(//)", 161.1, 5.0, "S", 161.1, 161.1, 1, 1));
		ht.put("=COS(//)", new HOSECodeShiftRange("=COS(//)", 154.8, 26.4, "S", 172.8, 148.2, 6, 7));
		ht.put("=COQ(//)", new HOSECodeShiftRange("=COQ(//)", 158.5, 16.4, "S", 173.0, 151.3, 9, 17));
		ht.put("=COX(//)", new HOSECodeShiftRange("=COX(//)", 141.5, 27.2, "S", 148.7, 126.7, 3, 3));
		ht.put("=COY(//)", new HOSECodeShiftRange("=COY(//)", 122.4, 5.0, "S", 125.1, 120.8, 5, 8));
		ht.put("=COI(//)", new HOSECodeShiftRange("=COI(//)", 88.5, 5.0, "S", 88.5, 88.5, 1, 1));
		ht.put("=CO(//)", new HOSECodeShiftRange("=CO(//)", 144.8, 16.4, "D", 193.6, 109.3, 399, 1555));
		ht.put("=CN$$N(//)", new HOSECodeShiftRange("=CN$$N(//)", 137.9, 11.0, "S", 151.2, 128.4, 12, 17));
		ht.put("=CN$$S(//)", new HOSECodeShiftRange("=CN$$S(//)", 147.2, 10.1, "S", 154.3, 129.7, 11, 16));
		ht.put("=CN$$(//)", new HOSECodeShiftRange("=CN$$(//)", 120.0, 38.1, "D", 142.9, 96.8, 10, 13));
		ht.put("=CN$S(//)", new HOSECodeShiftRange("=CN$S(//)", 152.8, 5.0, "S", 153.4, 152.2, 2, 2));
		ht.put("=CNN(//)", new HOSECodeShiftRange("=CNN(//)", 149.0, 16.4, "S", 169.1, 121.3, 117, 185));
		ht.put("=CNS(//)", new HOSECodeShiftRange("=CNS(//)", 154.3, 24.4, "S", 181.5, 125.8, 52, 77));
		ht.put("=CNP$(//)", new HOSECodeShiftRange("=CNP$(//)", 142.1, 5.0, "S", 146.6, 137.6, 1, 2));
		ht.put("=CNQ(//)", new HOSECodeShiftRange("=CNQ(//)", 140.7, 35.2, "S", 150.0, 134.5, 3, 3));
		ht.put("=CNF(//)", new HOSECodeShiftRange("=CNF(//)", 151.8, 5.0, "S", 152.3, 151.3, 2, 2));
		ht.put("=CNX(//)", new HOSECodeShiftRange("=CNX(//)", 129.3, 28.6, "S", 162.4, 106.1, 25, 33));
		ht.put("=CNY(//)", new HOSECodeShiftRange("=CNY(//)", 111.2, 23.6, "S", 136.5, 94.4, 23, 30));
		ht.put("=CNI(//)", new HOSECodeShiftRange("=CNI(//)", 81.8, 5.0, "S", 81.8, 81.8, 1, 1));
		ht.put("=CN(//)", new HOSECodeShiftRange("=CN(//)", 131.5, 27.1, "D", 167.5, 86.1, 925, 1980));
		ht.put("=CS$$X(//)", new HOSECodeShiftRange("=CS$$X(//)", 129.7, 5.0, "S", 129.7, 129.7, 1, 1));
		ht.put("=CS$$Y(//)", new HOSECodeShiftRange("=CS$$Y(//)", 122.7, 5.0, "S", 122.7, 122.7, 1, 1));
		ht.put("=CS$$(//)", new HOSECodeShiftRange("=CS$$(//)", 121.6, 40.9, "D", 145.1, 62.4, 20, 37));
		ht.put("=CS$X(//)", new HOSECodeShiftRange("=CS$X(//)", 137.3, 5.0, "S", 143.5, 131.1, 2, 2));
		ht.put("=CS$Y(//)", new HOSECodeShiftRange("=CS$Y(//)", 131.5, 5.0, "S", 131.5, 131.5, 1, 1));
		ht.put("=CS$(//)", new HOSECodeShiftRange("=CS$(//)", 116.9, 60.2, "D", 148.4, 59.3, 13, 15));
		ht.put("=CSS(//)", new HOSECodeShiftRange("=CSS(//)", 145.4, 37.6, "S", 195.6, 106.4, 47, 74));
		ht.put("=CSB(//)", new HOSECodeShiftRange("=CSB(//)", 143.1, 5.0, "S", 143.1, 143.1, 1, 1));
		ht.put("=CSF(//)", new HOSECodeShiftRange("=CSF(//)", 166.5, 5.0, "S", 166.5, 166.5, 1, 1));
		ht.put("=CSX(//)", new HOSECodeShiftRange("=CSX(//)", 129.4, 16.0, "S", 150.3, 114.7, 17, 26));
		ht.put("=CSY(//)", new HOSECodeShiftRange("=CSY(//)", 114.3, 10.1, "S", 125.5, 92.1, 24, 46));
		ht.put("=CSI(//)", new HOSECodeShiftRange("=CSI(//)", 77.3, 14.5, "S", 82.2, 73.1, 2, 4));
		ht.put("=CS(//)", new HOSECodeShiftRange("=CS(//)", 125.7, 24.0, "D", 190.2, 33.2, 258, 562));
		ht.put("=CP$(//)", new HOSECodeShiftRange("=CP$(//)", 109.4, 42.2, "D", 131.3, 73.7, 15, 21));
		ht.put("=CP(//)", new HOSECodeShiftRange("=CP(//)", 101.9, 68.3, "D", 148.2, 29.6, 64, 116));
		ht.put("=CQQ(//)", new HOSECodeShiftRange("=CQQ(//)", 128.6, 127.6, "S", 189.6, 59.0, 7, 14));
		ht.put("=CQX(//)", new HOSECodeShiftRange("=CQX(//)", 143.0, 5.0, "S", 143.0, 143.0, 1, 2));
		ht.put("=CQ(//)", new HOSECodeShiftRange("=CQ(//)", 132.4, 31.8, "D", 152.2, 81.8, 21, 37));
		ht.put("=CB(//)", new HOSECodeShiftRange("=CB(//)", 143.4, 33.0, "D", 177.9, 114.3, 12, 20));
		ht.put("=CFF(//)", new HOSECodeShiftRange("=CFF(//)", 156.8, 5.0, "S", 157.4, 156.0, 2, 3));
		ht.put("=CFY(//)", new HOSECodeShiftRange("=CFY(//)", 138.3, 5.0, "S", 138.3, 138.3, 1, 1));
		ht.put("=CF(//)", new HOSECodeShiftRange("=CF(//)", 149.5, 5.0, "D", 151.3, 147.7, 2, 2));
		ht.put("=CXX(//)", new HOSECodeShiftRange("=CXX(//)", 122.8, 13.9, "S", 144.5, 106.8, 67, 123));
		ht.put("=CX(//)", new HOSECodeShiftRange("=CX(//)", 121.5, 20.1, "D", 156.2, 87.9, 49, 120));
		ht.put("=CYY(//)", new HOSECodeShiftRange("=CYY(//)", 92.2, 13.3, "S", 103.9, 89.1, 7, 8));
		ht.put("=CYI(//)", new HOSECodeShiftRange("=CYI(//)", 70.3, 5.0, "S", 70.3, 70.3, 1, 1));
		ht.put("=CY(//)", new HOSECodeShiftRange("=CY(//)", 112.4, 19.0, "D", 139.4, 74.0, 25, 62));
		ht.put("=CII(//)", new HOSECodeShiftRange("=CII(//)", 30.5, 5.0, "S", 32.4, 28.7, 2, 2));
		ht.put("=CI(//)", new HOSECodeShiftRange("=CI(//)", 90.2, 21.0, "D", 103.9, 82.3, 5, 8));
		ht.put("=C(//)", new HOSECodeShiftRange("=C(//)", 112.6, 23.0, "T", 155.1, 12.5, 907, 3109));
		ht.put("=O=N(//)", new HOSECodeShiftRange("=O=N(//)", 124.8, 5.0, "S", 132.1, 115.9, 30, 72));
		ht.put("=OC$C$(//)", new HOSECodeShiftRange("=OC$C$(//)", 168.8, 5.0, "S", 168.8, 168.8, 1, 1));
		ht.put("=OC$C(//)", new HOSECodeShiftRange("=OC$C(//)", 188.2, 22.6, "S", 235.5, 103.6, 116, 216));
		ht.put("=OC$O(//)", new HOSECodeShiftRange("=OC$O(//)", 161.3, 9.3, "S", 182.1, 155.9, 36, 85));
		ht.put("=OC$N(//)", new HOSECodeShiftRange("=OC$N(//)", 158.4, 6.1, "S", 167.6, 148.7, 47, 93));
		ht.put("=OC$S(//)", new HOSECodeShiftRange("=OC$S(//)", 187.5, 5.0, "S", 189.0, 186.1, 2, 2));
		ht.put("=OC$Q(//)", new HOSECodeShiftRange("=OC$Q(//)", 199.2, 5.0, "S", 199.2, 199.2, 1, 1));
		ht.put("=OCC(//)", new HOSECodeShiftRange("=OCC(//)", 198.6, 25.4, "S", 244.3, 87.5, 3091, 7782));
		ht.put("=OCO(//)", new HOSECodeShiftRange("=OCO(//)", 170.6, 16.6, "S", 1000.0, 128.0, 4638, 16486));
		ht.put("=OCN(//)", new HOSECodeShiftRange("=OCN(//)", 168.7, 12.1, "S", 197.8, 34.3, 2161, 5520));
		ht.put("=OCS(//)", new HOSECodeShiftRange("=OCS(//)", 192.7, 24.2, "S", 213.6, 106.9, 78, 115));
		ht.put("=OCP(//)", new HOSECodeShiftRange("=OCP(//)", 229.0, 5.0, "S", 229.0, 229.0, 1, 1));
		ht.put("=OCQ(//)", new HOSECodeShiftRange("=OCQ(//)", 242.1, 68.1, "S", 291.7, 216.8, 6, 6));
		ht.put("=OCF(//)", new HOSECodeShiftRange("=OCF(//)", 157.4, 5.0, "S", 161.6, 155.9, 6, 31));
		ht.put("=OCX(//)", new HOSECodeShiftRange("=OCX(//)", 169.5, 8.8, "S", 180.0, 151.0, 77, 144));
		ht.put("=OCY(//)", new HOSECodeShiftRange("=OCY(//)", 173.7, 18.4, "S", 178.0, 165.3, 4, 4));
		ht.put("=OC(//)", new HOSECodeShiftRange("=OC(//)", 193.7, 14.5, "D", 212.2, 156.8, 414, 954));
		ht.put("=OOO(//)", new HOSECodeShiftRange("=OOO(//)", 153.9, 11.6, "S", 173.8, 148.1, 73, 127));
		ht.put("=OON(//)", new HOSECodeShiftRange("=OON(//)", 155.3, 6.9, "S", 186.4, 144.7, 309, 792));
		ht.put("=OOS(//)", new HOSECodeShiftRange("=OOS(//)", 163.1, 21.9, "S", 173.8, 147.1, 7, 9));
		ht.put("=OOP$(//)", new HOSECodeShiftRange("=OOP$(//)", 167.0, 5.0, "S", 167.0, 167.0, 1, 1));
		ht.put("=OOX(//)", new HOSECodeShiftRange("=OOX(//)", 151.0, 5.0, "S", 157.6, 147.2, 9, 22));
		ht.put("=OO(//)", new HOSECodeShiftRange("=OO(//)", 161.7, 6.1, "D", 172.8, 158.5, 22, 73));
		ht.put("=ONN(//)", new HOSECodeShiftRange("=ONN(//)", 154.8, 10.0, "S", 185.1, 112.2, 422, 1159));
		ht.put("=ONS$$(//)", new HOSECodeShiftRange("=ONS$$(//)", 161.2, 5.0, "S", 161.8, 160.8, 2, 4));
		ht.put("=ONS$(//)", new HOSECodeShiftRange("=ONS$(//)", 168.4, 5.0, "S", 169.0, 167.9, 2, 4));
		ht.put("=ONS(//)", new HOSECodeShiftRange("=ONS(//)", 167.7, 6.3, "S", 173.4, 157.1, 32, 45));
		ht.put("=ONP$(//)", new HOSECodeShiftRange("=ONP$(//)", 169.4, 5.0, "S", 170.1, 168.8, 2, 2));
		ht.put("=ONP(//)", new HOSECodeShiftRange("=ONP(//)", 166.0, 5.0, "S", 167.6, 164.4, 1, 2));
		ht.put("=ONB(//)", new HOSECodeShiftRange("=ONB(//)", 182.8, 5.0, "S", 182.8, 182.8, 1, 1));
		ht.put("=ONX(//)", new HOSECodeShiftRange("=ONX(//)", 147.5, 5.0, "S", 149.2, 144.6, 7, 9));
		ht.put("=ONY(//)", new HOSECodeShiftRange("=ONY(//)", 146.5, 5.0, "S", 146.5, 146.5, 1, 1));
		ht.put("=ON(//)", new HOSECodeShiftRange("=ON(//)", 162.5, 5.0, "D", 168.4, 155.8, 62, 140));
		ht.put("=OSS(//)", new HOSECodeShiftRange("=OSS(//)", 190.2, 6.1, "S", 192.6, 188.3, 2, 4));
		ht.put("=OSX(//)", new HOSECodeShiftRange("=OSX(//)", 164.2, 5.0, "S", 165.0, 163.4, 1, 2));
		ht.put("=OS(//)", new HOSECodeShiftRange("=OS(//)", 187.7, 5.0, "D", 187.7, 187.7, 1, 1));
		ht.put("=O(//)", new HOSECodeShiftRange("=O(//)", 60.6, 5.0, "T", 60.6, 60.6, 1, 1));
		ht.put("=N=N(//)", new HOSECodeShiftRange("=N=N(//)", 136.3, 10.2, "S", 143.6, 125.6, 19, 26));
		ht.put("=N=S(//)", new HOSECodeShiftRange("=N=S(//)", 134.7, 11.6, "S", 150.1, 121.0, 29, 70));
		ht.put("=NC$C$(//)", new HOSECodeShiftRange("=NC$C$(//)", 115.9, 55.2, "S", 145.6, 65.4, 16, 24));
		ht.put("=NC$C(//)", new HOSECodeShiftRange("=NC$C(//)", 144.9, 41.4, "S", 176.4, 60.0, 101, 183));
		ht.put("=NC$O(//)", new HOSECodeShiftRange("=NC$O(//)", 153.1, 14.6, "S", 162.5, 146.1, 5, 6));
		ht.put("=NC$N(//)", new HOSECodeShiftRange("=NC$N(//)", 144.8, 14.1, "S", 159.7, 136.8, 12, 21));
		ht.put("=NC$S(//)", new HOSECodeShiftRange("=NC$S(//)", 159.7, 7.4, "S", 165.9, 153.6, 8, 10));
		ht.put("=NC$P$(//)", new HOSECodeShiftRange("=NC$P$(//)", 52.4, 5.0, "S", 56.1, 48.7, 1, 2));
		ht.put("=CN$X(//)", new HOSECodeShiftRange("=CN$X(//)", 142.8, 30.6, "S", 160.9, 136.3, 2, 4));
		ht.put("=NC$(//)", new HOSECodeShiftRange("=NC$(//)", 121.5, 72.4, "D", 150.8, 46.0, 32, 46));
		ht.put("=NCC(//)", new HOSECodeShiftRange("=NCC(//)", 156.3, 27.8, "S", 207.3, 51.2, 604, 1575));
		ht.put("=NCO(//)", new HOSECodeShiftRange("=NCO(//)", 163.6, 18.6, "S", 195.1, 143.3, 140, 255));
		ht.put("=NCN(//)", new HOSECodeShiftRange("=NCN(//)", 155.3, 16.1, "S", 185.6, 129.9, 330, 658));
		ht.put("=NCS$$(//)", new HOSECodeShiftRange("=NCS$$(//)", 162.9, 5.0, "S", 162.9, 162.9, 1, 1));
		ht.put("=NCS(//)", new HOSECodeShiftRange("=NCS(//)", 165.2, 17.6, "S", 193.3, 134.9, 113, 238));
		ht.put("=NCF(//)", new HOSECodeShiftRange("=NCF(//)", 150.4, 23.6, "S", 156.8, 147.3, 2, 3));
		ht.put("=NCX(//)", new HOSECodeShiftRange("=NCX(//)", 154.3, 28.7, "S", 184.5, 131.0, 25, 31));
		ht.put("=NCY(//)", new HOSECodeShiftRange("=NCY(//)", 128.5, 16.5, "S", 143.8, 117.1, 9, 15));
		ht.put("=NCI(//)", new HOSECodeShiftRange("=NCI(//)", 93.3, 5.0, "S", 93.3, 93.3, 1, 1));
		ht.put("=NC(//)", new HOSECodeShiftRange("=NC(//)", 151.0, 27.9, "D", 187.0, 47.2, 519, 1125));
		ht.put("=NOO(//)", new HOSECodeShiftRange("=NOO(//)", 163.9, 9.0, "S", 171.1, 159.8, 4, 11));
		ht.put("=NON(//)", new HOSECodeShiftRange("=NON(//)", 155.5, 13.7, "S", 174.6, 139.2, 38, 62));
		ht.put("=NOS(//)", new HOSECodeShiftRange("=NOS(//)", 164.9, 14.7, "S", 175.8, 157.9, 9, 12));
		ht.put("=NO(//)", new HOSECodeShiftRange("=NO(//)", 155.5, 11.6, "D", 171.6, 143.6, 19, 33));
		ht.put("=NN$$S(//)", new HOSECodeShiftRange("=NN$$S(//)", 165.7, 5.0, "S", 166.0, 165.4, 1, 2));
		ht.put("=NNN(//)", new HOSECodeShiftRange("=NNN(//)", 155.8, 10.7, "S", 180.0, 135.0, 263, 479));
		ht.put("=NNS$(//)", new HOSECodeShiftRange("=NNS$(//)", 162.4, 5.0, "S", 162.6, 162.2, 1, 2));
		ht.put("=NNS(//)", new HOSECodeShiftRange("=NNS(//)", 162.4, 16.7, "S", 187.1, 140.2, 245, 459));
		ht.put("=NNP(//)", new HOSECodeShiftRange("=NNP(//)", 186.5, 31.0, "S", 194.8, 182.1, 3, 3));
		ht.put("=NNX(//)", new HOSECodeShiftRange("=NNX(//)", 144.7, 17.2, "S", 152.7, 132.5, 8, 10));
		ht.put("=NNY(//)", new HOSECodeShiftRange("=NNY(//)", 127.6, 14.5, "S", 139.3, 121.5, 6, 8));
		ht.put("=NN(//)", new HOSECodeShiftRange("=NN(//)", 145.0, 13.6, "D", 175.1, 121.3, 310, 918));
		ht.put("=NS$$S(//)", new HOSECodeShiftRange("=NS$$S(//)", 155.9, 5.0, "S", 155.9, 155.9, 1, 1));
		ht.put("=NS$$P$(//)", new HOSECodeShiftRange("=NS$$P$(//)", 60.2, 5.0, "S", 61.3, 59.1, 1, 2));
		ht.put("=NS$S(//)", new HOSECodeShiftRange("=NS$S(//)", 177.6, 5.0, "S", 177.6, 177.6, 1, 1));
		ht.put("=NSS(//)", new HOSECodeShiftRange("=NSS(//)", 166.1, 34.3, "S", 197.2, 138.6, 35, 68));
		ht.put("=NSF(//)", new HOSECodeShiftRange("=NSF(//)", 168.6, 5.0, "S", 168.6, 168.6, 1, 1));
		ht.put("=NSX(//)", new HOSECodeShiftRange("=NSX(//)", 153.6, 24.5, "S", 178.1, 137.9, 9, 14));
		ht.put("=NSY(//)", new HOSECodeShiftRange("=NSY(//)", 133.2, 24.1, "S", 138.8, 124.2, 3, 3));
		ht.put("=NS(//)", new HOSECodeShiftRange("=NS(//)", 155.6, 20.0, "D", 183.6, 141.4, 22, 54));
		ht.put("=NP$P$(//)", new HOSECodeShiftRange("=NP$P$(//)", 37.9, 5.0, "S", 37.9, 37.9, 1, 1));
		ht.put("=NXX(//)", new HOSECodeShiftRange("=NXX(//)", 143.8, 5.0, "S", 160.5, 127.1, 2, 2));
		ht.put("=NX(//)", new HOSECodeShiftRange("=NX(//)", 166.2, 5.0, "D", 166.2, 166.2, 1, 2));
		ht.put("=N(//)", new HOSECodeShiftRange("=N(//)", 118.5, 93.6, "T", 170.5, 22.9, 11, 24));
		ht.put("=S=S(//)", new HOSECodeShiftRange("=S=S(//)", 192.7, 5.0, "S", 192.8, 192.6, 1, 4));
		ht.put("=SC$C(//)", new HOSECodeShiftRange("=SC$C(//)", 152.4, 5.0, "S", 152.4, 152.4, 1, 1));
		ht.put("=SC$O(//)", new HOSECodeShiftRange("=SC$O(//)", 207.5, 5.0, "S", 214.3, 200.7, 2, 2));
		ht.put("=SC$N(//)", new HOSECodeShiftRange("=SC$N(//)", 191.1, 24.5, "S", 198.1, 182.0, 4, 4));
		ht.put("=SCC(//)", new HOSECodeShiftRange("=SCC(//)", 214.7, 70.9, "S", 278.4, 166.7, 41, 70));
		ht.put("=SCO(//)", new HOSECodeShiftRange("=SCO(//)", 206.0, 20.7, "S", 221.2, 183.7, 16, 21));
		ht.put("=SCN(//)", new HOSECodeShiftRange("=SCN(//)", 194.8, 20.4, "S", 212.5, 165.2, 88, 155));
		ht.put("=SCS(//)", new HOSECodeShiftRange("=SCS(//)", 209.8, 63.8, "S", 251.6, 127.9, 24, 29));
		ht.put("=SCX(//)", new HOSECodeShiftRange("=SCX(//)", 202.3, 5.0, "S", 202.3, 202.3, 1, 1));
		ht.put("=SC(//)", new HOSECodeShiftRange("=SC(//)", 197.9, 64.8, "D", 206.6, 180.5, 2, 3));
		ht.put("=S$C$C$(//)", new HOSECodeShiftRange("=S$C$C$(//)", 66.3, 5.0, "S", 66.3, 66.3, 1, 1));
		ht.put("=S$(//)", new HOSECodeShiftRange("=S$(//)", 32.8, 5.0, "T", 32.8, 32.8, 1, 1));
		ht.put("=SOO(//)", new HOSECodeShiftRange("=SOO(//)", 191.0, 5.0, "S", 193.3, 185.4, 6, 18));
		ht.put("=SON(//)", new HOSECodeShiftRange("=SON(//)", 184.1, 9.2, "S", 192.9, 174.0, 18, 38));
		ht.put("=SOS(//)", new HOSECodeShiftRange("=SOS(//)", 211.4, 15.0, "S", 233.7, 205.6, 9, 24));
		ht.put("=SOX(//)", new HOSECodeShiftRange("=SOX(//)", 184.4, 5.0, "S", 184.4, 184.4, 1, 1));
		ht.put("=SNN(//)", new HOSECodeShiftRange("=SNN(//)", 179.3, 9.5, "S", 194.6, 157.5, 272, 693));
		ht.put("=SNS(//)", new HOSECodeShiftRange("=SNS(//)", 193.4, 17.3, "S", 215.8, 172.0, 68, 148));
		ht.put("=SNP$(//)", new HOSECodeShiftRange("=SNP$(//)", 196.1, 5.0, "S", 196.9, 194.7, 4, 11));
		ht.put("=SNX(//)", new HOSECodeShiftRange("=SNX(//)", 174.1, 5.0, "S", 175.5, 172.7, 1, 2));
		ht.put("=SN(//)", new HOSECodeShiftRange("=SN(//)", 187.8, 12.0, "D", 205.9, 183.7, 10, 14));
		ht.put("=SSS(//)", new HOSECodeShiftRange("=SSS(//)", 219.7, 15.8, "S", 231.7, 207.0, 14, 22));
		ht.put("=SSX(//)", new HOSECodeShiftRange("=SSX(//)", 192.4, 5.0, "S", 195.1, 189.8, 2, 2));
		ht.put("=SXX(//)", new HOSECodeShiftRange("=SXX(//)", 193.7, 5.0, "S", 217.4, 170.1, 2, 2));
		ht.put("=S(//)", new HOSECodeShiftRange("=S(//)", 30.5, 5.0, "T", 30.5, 30.5, 1, 1));
		ht.put("=P=P(//)", new HOSECodeShiftRange("=P=P(//)", 8.3, 5.0, "S", 10.8, 5.8, 2, 2));
		ht.put("=PC$(//)", new HOSECodeShiftRange("=PC$(//)", 52.4, 5.0, "D", 53.8, 51.4, 2, 3));
		ht.put("=PCC(//)", new HOSECodeShiftRange("=PCC(//)", 106.5, 136.5, "S", 193.4, 77.4, 5, 5));
		ht.put("=PCO(//)", new HOSECodeShiftRange("=PCO(//)", 215.7, 5.0, "S", 216.0, 215.5, 2, 2));
		ht.put("=PC(//)", new HOSECodeShiftRange("=PC(//)", 51.7, 93.4, "D", 128.5, 3.2, 8, 11));
		ht.put("=POP(//)", new HOSECodeShiftRange("=POP(//)", 205.3, 5.0, "S", 205.3, 205.3, 1, 1));
		ht.put("=PNN(//)", new HOSECodeShiftRange("=PNN(//)", 186.5, 31.0, "S", 194.8, 182.1, 3, 3));
		ht.put("=PSS(//)", new HOSECodeShiftRange("=PSS(//)", 27.6, 5.0, "S", 28.4, 26.8, 2, 2));
		ht.put("=PP(//)", new HOSECodeShiftRange("=PP(//)", 7.4, 5.0, "D", 8.2, 6.8, 2, 5));
		ht.put("=PQQ(//)", new HOSECodeShiftRange("=PQQ(//)", 12.0, 33.4, "S", 30.6, 0.3, 4, 5));
		ht.put("=PQ(//)", new HOSECodeShiftRange("=PQ(//)", 90.2, 5.0, "D", 147.9, 32.6, 2, 2));
		ht.put("=P(//)", new HOSECodeShiftRange("=P(//)", -4.7, 13.0, "T", 10.0, -14.2, 11, 13));
		ht.put("=BCB(//)", new HOSECodeShiftRange("=BCB(//)", 115.2, 5.0, "S", 115.2, 115.2, 1, 1));
		ht.put("*C*C*C(//)", new HOSECodeShiftRange("*C*C*C(//)", 130.5, 11.0, "S", 162.1, 99.1, 896, 3851));
		ht.put("*C*C*O(//)", new HOSECodeShiftRange("*C*C*O(//)", 159.5, 9.6, "S", 168.7, 154.7, 4, 10));
		ht.put("*C*C*N(//)", new HOSECodeShiftRange("*C*C*N(//)", 142.4, 11.9, "S", 159.7, 120.4, 214, 766));
		ht.put("*C*C*S(//)", new HOSECodeShiftRange("*C*C*S(//)", 167.5, 5.0, "S", 167.5, 167.5, 1, 2));
		ht.put("*C*CC$(//)", new HOSECodeShiftRange("*C*CC$(//)", 126.9, 20.4, "S", 173.2, 77.1, 1774, 6780));
		ht.put("*C*CC(//)", new HOSECodeShiftRange("*C*CC(//)", 134.5, 19.8, "S", 186.2, 74.6, 7374, 26549));
		ht.put("*C*CO(//)", new HOSECodeShiftRange("*C*CO(//)", 153.6, 14.1, "S", 184.7, 115.2, 2544, 13422));
		ht.put("*C*CN$$(//)", new HOSECodeShiftRange("*C*CN$$(//)", 143.8, 11.5, "S", 159.1, 119.2, 473, 1514));
		ht.put("*C*CN$(//)", new HOSECodeShiftRange("*C*CN$(//)", 160.6, 18.6, "S", 166.8, 141.4, 9, 12));
		ht.put("*C*CN(//)", new HOSECodeShiftRange("*C*CN(//)", 140.4, 17.1, "S", 168.2, 77.3, 2283, 7826));
		ht.put("*C*CS$$(//)", new HOSECodeShiftRange("*C*CS$$(//)", 136.7, 12.4, "S", 162.5, 105.5, 280, 993));
		ht.put("*C*CS$(//)", new HOSECodeShiftRange("*C*CS$(//)", 143.4, 13.0, "S", 164.8, 122.9, 40, 89));
		ht.put("*C*CS(//)", new HOSECodeShiftRange("*C*CS(//)", 132.8, 16.4, "S", 162.1, 104.7, 346, 860));
		ht.put("*C*CP$(//)", new HOSECodeShiftRange("*C*CP$(//)", 132.5, 11.3, "S", 165.9, 107.6, 36, 141));
		ht.put("*C*CP(//)", new HOSECodeShiftRange("*C*CP(//)", 129.4, 16.5, "S", 159.6, 113.1, 127, 1065));
		ht.put("*C*CQ(//)", new HOSECodeShiftRange("*C*CQ(//)", 136.9, 10.7, "S", 166.1, 92.6, 73, 259));
		ht.put("*C*CB(//)", new HOSECodeShiftRange("*C*CB(//)", 146.2, 23.4, "S", 163.9, 127.3, 12, 31));
		ht.put("*C*CF(//)", new HOSECodeShiftRange("*C*CF(//)", 155.2, 18.9, "S", 175.3, 121.7, 290, 1101));
		ht.put("*C*CX(//)", new HOSECodeShiftRange("*C*CX(//)", 129.8, 14.4, "S", 151.8, 95.6, 808, 3670));
		ht.put("*C*CY(//)", new HOSECodeShiftRange("*C*CY(//)", 118.7, 15.2, "S", 166.3, 93.5, 279, 758));
		ht.put("*C*CI(//)", new HOSECodeShiftRange("*C*CI(//)", 94.3, 20.4, "S", 122.2, 65.6, 120, 248));
		ht.put("*C*C(//)", new HOSECodeShiftRange("*C*C(//)", 125.3, 14.3, "D", 180.9, 52.9, 16673, 124916));
		ht.put("H*C*C(//)", new HOSECodeShiftRange("H*C*C(//)", 125.3, 14.3, "D", 180.9, 52.9, 16673, 124916));
		ht.put("*C*O*N(//)", new HOSECodeShiftRange("*C*O*N(//)", 179.8, 5.0, "S", 179.8, 179.8, 1, 1));
		ht.put("*C*OC$(//)", new HOSECodeShiftRange("*C*OC$(//)", 203.6, 5.0, "S", 203.6, 203.6, 1, 1));
		ht.put("*C*OC(//)", new HOSECodeShiftRange("*C*OC(//)", 179.1, 22.6, "S", 216.0, 146.8, 46, 150));
		ht.put("*C*OO(//)", new HOSECodeShiftRange("*C*OO(//)", 171.4, 5.0, "S", 171.4, 171.4, 1, 2));
		ht.put("*C*ON(//)", new HOSECodeShiftRange("*C*ON(//)", 160.6, 10.4, "S", 167.1, 153.8, 10, 16));
		ht.put("*C*O(//)", new HOSECodeShiftRange("*C*O(//)", 176.7, 31.8, "D", 191.9, 147.0, 8, 19));
		ht.put("*C*N*N(//)", new HOSECodeShiftRange("*C*N*N(//)", 154.0, 5.5, "S", 159.4, 145.4, 14, 38));
		ht.put("*C*NC$(//)", new HOSECodeShiftRange("*C*NC$(//)", 139.6, 28.9, "S", 157.2, 108.1, 76, 123));
		ht.put("*C*NC(//)", new HOSECodeShiftRange("*C*NC(//)", 154.6, 19.3, "S", 177.7, 99.7, 475, 1032));
		ht.put("*C*NO(//)", new HOSECodeShiftRange("*C*NO(//)", 161.1, 11.4, "S", 177.9, 136.5, 159, 280));
		ht.put("*C*NN$$(//)", new HOSECodeShiftRange("*C*NN$$(//)", 152.3, 37.2, "S", 162.0, 145.4, 3, 3));
		ht.put("*C*NN(//)", new HOSECodeShiftRange("*C*NN(//)", 154.2, 11.1, "S", 178.1, 125.8, 259, 682));
		ht.put("*C*NS$$(//)", new HOSECodeShiftRange("*C*NS$$(//)", 153.4, 5.0, "S", 166.5, 140.3, 2, 2));
		ht.put("*C*NS(//)", new HOSECodeShiftRange("*C*NS(//)", 162.6, 18.4, "S", 183.8, 140.3, 28, 45));
		ht.put("*C*NQ(//)", new HOSECodeShiftRange("*C*NQ(//)", 168.2, 5.0, "S", 168.2, 168.2, 1, 1));
		ht.put("*C*NF(//)", new HOSECodeShiftRange("*C*NF(//)", 157.8, 16.7, "S", 174.2, 143.9, 23, 47));
		ht.put("*C*NX(//)", new HOSECodeShiftRange("*C*NX(//)", 148.8, 24.1, "S", 165.9, 117.8, 99, 201));
		ht.put("*C*NY(//)", new HOSECodeShiftRange("*C*NY(//)", 135.4, 21.9, "S", 147.2, 108.6, 17, 24));
		ht.put("*C*NT(//)", new HOSECodeShiftRange("*C*NT(//)", 108.2, 36.8, "S", 127.8, 82.4, 7, 8));
		ht.put("*C*N(//)", new HOSECodeShiftRange("*C*N(//)", 146.9, 14.7, "D", 168.9, 104.7, 978, 3025));
		ht.put("*C*SN(//)", new HOSECodeShiftRange("*C*SN(//)", 214.6, 5.0, "S", 214.7, 214.5, 1, 2));
		ht.put("*C*SS(//)", new HOSECodeShiftRange("*C*SS(//)", 199.8, 20.8, "S", 203.7, 190.1, 3, 4));
		ht.put("*C*S(//)", new HOSECodeShiftRange("*C*S(//)", 158.8, 5.0, "D", 158.8, 158.8, 1, 2));
		ht.put("*C*PC(//)", new HOSECodeShiftRange("*C*PC(//)", 124.4, 72.7, "S", 166.9, 95.7, 5, 13));
		ht.put("*C*P(//)", new HOSECodeShiftRange("*C*P(//)", 135.1, 82.8, "D", 154.5, 71.8, 4, 9));
		ht.put("*C*Q*Q*C(//)", new HOSECodeShiftRange("*C*Q*Q*C(//)", 79.5, 46.2, "P", 85.7, 67.1, 2, 3));
		ht.put("*C*Q(//)", new HOSECodeShiftRange("*C*Q(//)", 129.0, 5.0, "D", 129.0, 129.0, 1, 2));
		ht.put("*Q*NC(//)", new HOSECodeShiftRange("*Q*NC(//)", 173.7, 5.0, "S", 173.9, 173.6, 2, 2));
		ht.put("*N*N*N(//)", new HOSECodeShiftRange("*N*N*N(//)", 157.8, 15.6, "S", 190.9, 147.5, 12, 26));
		ht.put("*N*NC$(//)", new HOSECodeShiftRange("*N*NC$(//)", 149.7, 27.5, "S", 157.7, 140.2, 3, 4));
		ht.put("*N*NC(//)", new HOSECodeShiftRange("*N*NC(//)", 164.3, 16.7, "S", 183.1, 145.5, 72, 134));
		ht.put("*N*NO(//)", new HOSECodeShiftRange("*N*NO(//)", 166.7, 14.3, "S", 179.1, 149.1, 34, 58));
		ht.put("*N*NN(//)", new HOSECodeShiftRange("*N*NN(//)", 160.5, 10.9, "S", 178.7, 148.9, 85, 156));
		ht.put("*N*NS$$(//)", new HOSECodeShiftRange("*N*NS$$(//)", 162.7, 15.7, "S", 166.9, 160.6, 2, 3));
		ht.put("*N*NS(//)", new HOSECodeShiftRange("*N*NS(//)", 168.0, 14.4, "S", 186.7, 148.1, 30, 61));
		ht.put("*N*NP$(//)", new HOSECodeShiftRange("*N*NP$(//)", 170.7, 5.0, "S", 172.7, 168.8, 2, 2));
		ht.put("*N*NF(//)", new HOSECodeShiftRange("*N*NF(//)", 168.7, 11.9, "S", 174.0, 156.9, 6, 12));
		ht.put("*N*NX(//)", new HOSECodeShiftRange("*N*NX(//)", 164.2, 12.8, "S", 177.0, 142.1, 22, 54));
		ht.put("*N*NY(//)", new HOSECodeShiftRange("*N*NY(//)", 153.5, 5.0, "S", 153.6, 153.4, 1, 2));
		ht.put("*N*NI(//)", new HOSECodeShiftRange("*N*NI(//)", 129.8, 5.0, "S", 130.2, 129.4, 1, 2));
		ht.put("*N*N(//)", new HOSECodeShiftRange("*N*N(//)", 152.2, 13.8, "D", 167.8, 127.9, 124, 372));
		ht.put("*P*P(//)", new HOSECodeShiftRange("*P*P(//)", 26.2, 5.0, "D", 26.2, 26.2, 1, 1));
		ht.put("*Q(//)", new HOSECodeShiftRange("*Q(//)", 84.0, 5.0, "T", 84.0, 84.0, 1, 1));
		ht.put("C$C$C$C$(//)", new HOSECodeShiftRange("C$C$C$C$(//)", 71.3, 5.0, "S", 71.3, 71.3, 1, 1));
		ht.put("C$C$C$(//)", new HOSECodeShiftRange("C$C$C$(//)", 58.0, 5.0, "D", 59.6, 56.4, 1, 2));
		ht.put("C$C$CC(//)", new HOSECodeShiftRange("C$C$CC(//)", 61.0, 17.4, "S", 85.1, 28.5, 125, 228));
		ht.put("C$C$CO(//)", new HOSECodeShiftRange("C$C$CO(//)", 81.0, 22.4, "S", 95.3, 61.3, 8, 10));
		ht.put("C$C$CY(//)", new HOSECodeShiftRange("C$C$CY(//)", 59.4, 19.3, "S", 64.6, 56.7, 3, 3));
		ht.put("C$C$C(//)", new HOSECodeShiftRange("C$C$C(//)", 57.1, 23.6, "D", 123.3, 41.8, 104, 162));
		ht.put("C$C$OO(//)", new HOSECodeShiftRange("C$C$OO(//)", 90.5, 5.0, "S", 90.5, 90.5, 1, 1));
		ht.put("C$C$DC(//)", new HOSECodeShiftRange("C$C$DC(//)", 94.3, 5.0, "S", 94.3, 94.3, 1, 1));
		ht.put("C$C$O(//)", new HOSECodeShiftRange("C$C$O(//)", 78.5, 5.0, "D", 85.1, 71.9, 2, 2));
		ht.put("C$C$NS(//)", new HOSECodeShiftRange("C$C$NS(//)", 64.7, 5.0, "S", 64.7, 64.7, 1, 1));
		ht.put("C$C$N(//)", new HOSECodeShiftRange("C$C$N(//)", 63.9, 21.6, "D", 92.9, 54.6, 10, 16));
		ht.put("C$C$XX(//)", new HOSECodeShiftRange("C$C$XX(//)", 72.2, 12.3, "S", 76.9, 67.1, 4, 5));
		ht.put("C$C$X(//)", new HOSECodeShiftRange("C$C$X(//)", 54.8, 5.0, "D", 54.8, 54.8, 1, 1));
		ht.put("C$C$YY(//)", new HOSECodeShiftRange("C$C$YY(//)", 50.2, 5.0, "S", 50.2, 50.2, 1, 1));
		ht.put("C$C$Y(//)", new HOSECodeShiftRange("C$C$Y(//)", 62.8, 153.7, "D", 104.1, 41.6, 2, 3));
		ht.put("C$C$(//)", new HOSECodeShiftRange("C$C$(//)", 48.2, 10.7, "T", 61.5, 36.7, 51, 127));
		ht.put("C$CCC(//)", new HOSECodeShiftRange("C$CCC(//)", 50.5, 18.1, "S", 104.1, 21.8, 838, 1886));
		ht.put("C$CCO(//)", new HOSECodeShiftRange("C$CCO(//)", 79.3, 15.9, "S", 103.4, 55.1, 283, 540));
		ht.put("C$CCN$$(//)", new HOSECodeShiftRange("C$CCN$$(//)", 97.8, 5.0, "S", 97.8, 97.8, 1, 1));
		ht.put("C$CCN(//)", new HOSECodeShiftRange("C$CCN(//)", 70.2, 27.3, "S", 115.5, 36.3, 89, 159));
		ht.put("C$CCS$$(//)", new HOSECodeShiftRange("C$CCS$$(//)", 67.4, 10.6, "S", 75.8, 62.8, 5, 8));
		ht.put("C$CCS(//)", new HOSECodeShiftRange("C$CCS(//)", 51.9, 9.3, "S", 62.6, 49.2, 9, 24));
		ht.put("C$CCQ(//)", new HOSECodeShiftRange("C$CCQ(//)", 46.7, 5.0, "S", 58.8, 37.7, 2, 2));
		ht.put("C$CCF(//)", new HOSECodeShiftRange("C$CCF(//)", 94.0, 5.0, "S", 58.8, 34.7, 2, 2));
		ht.put("C$CCX(//)", new HOSECodeShiftRange("C$CCX(//)", 71.5, 15.5, "S", 84.6, 51.8, 32, 51));
		ht.put("C$CCY(//)", new HOSECodeShiftRange("C$CCY(//)", 63.3, 24.3, "S", 80.5, 32.2, 24, 34));
		ht.put("C$CC(//)", new HOSECodeShiftRange("C$CC(//)", 46.6, 18.6, "D", 92.5, 10.9, 1527, 3361));
		ht.put("C$COO(//)", new HOSECodeShiftRange("C$COO(//)", 100.3, 11.5, "S", 111.2, 86.0, 36, 111));
		ht.put("C$CON(//)", new HOSECodeShiftRange("C$CON(//)", 87.7, 15.8, "S", 97.5, 71.0, 9, 12));
		ht.put("C$COS(//)", new HOSECodeShiftRange("C$COS(//)", 93.0, 31.6, "S", 102.7, 71.3, 6, 6));
		ht.put("C$COP$(//)", new HOSECodeShiftRange("C$COP$(//)", 86.0, 5.0, "S", 86.0, 86.0, 1, 1));
		ht.put("C$COX(//)", new HOSECodeShiftRange("C$COX(//)", 95.7, 5.0, "S", 97.3, 94.1, 2, 2));
		ht.put("C$COY(//)", new HOSECodeShiftRange("C$COY(//)", 92.4, 5.0, "S", 92.4, 92.4, 1, 1));
		ht.put("C$CO(//)", new HOSECodeShiftRange("C$CO(//)", 73.0, 16.9, "D", 96.3, 46.3, 383, 850));
		ht.put("C$CN$$Y(//)", new HOSECodeShiftRange("C$CN$$Y(//)", 91.7, 5.0, "S", 91.7, 91.7, 1, 1));
		ht.put("C$CN$$(//)", new HOSECodeShiftRange("C$CN$$(//)", 83.6, 5.0, "D", 83.6, 83.6, 1, 1));
		ht.put("C$CNN(//)", new HOSECodeShiftRange("C$CNN(//)", 65.9, 66.1, "S", 87.5, 32.5, 5, 6));
		ht.put("C$CNS$(//)", new HOSECodeShiftRange("C$CNS$(//)", 101.8, 5.0, "S", 101.8, 101.8, 1, 1));
		ht.put("C$CNS(//)", new HOSECodeShiftRange("C$CNS(//)", 76.1, 5.0, "S", 77.0, 74.8, 2, 4));
		ht.put("C$CN(//)", new HOSECodeShiftRange("C$CN(//)", 57.3, 11.6, "D", 83.2, 32.6, 669, 1898));
		ht.put("C$CS$$(//)", new HOSECodeShiftRange("C$CS$$(//)", 67.4, 5.3, "D", 71.1, 64.1, 10, 15));
		ht.put("C$CSS(//)", new HOSECodeShiftRange("C$CSS(//)", 65.7, 23.5, "S", 74.9, 57.9, 3, 4));
		ht.put("C$CSX(//)", new HOSECodeShiftRange("C$CSX(//)", 98.0, 8.6, "S", 99.0, 90.0, 3, 6));
		ht.put("C$CS(//)", new HOSECodeShiftRange("C$CS(//)", 47.6, 21.2, "D", 88.7, 32.2, 23, 46));
		ht.put("C$CP$(//)", new HOSECodeShiftRange("C$CP$(//)", 39.8, 5.0, "D", 39.8, 39.8, 1, 1));
		ht.put("C$CP(//)", new HOSECodeShiftRange("C$CP(//)", 38.6, 5.0, "D", 38.6, 38.6, 1, 1));
		ht.put("C$CQ(//)", new HOSECodeShiftRange("C$CQ(//)", 25.0, 16.2, "D", 28.8, 21.3, 3, 3));
		ht.put("C$CB(//)", new HOSECodeShiftRange("C$CB(//)", 41.9, 5.0, "D", 43.0, 40.8, 1, 5));
		ht.put("C$CFF(//)", new HOSECodeShiftRange("C$CFF(//)", 108.7, 7.0, "S", 109.8, 106.9, 3, 3));
		ht.put("C$CFX(//)", new HOSECodeShiftRange("C$CFX(//)", 107.7, 5.0, "S", 107.7, 107.7, 1, 1));
		ht.put("C$CFY(//)", new HOSECodeShiftRange("C$CFY(//)", 102.4, 5.0, "S", 103.1, 102.0, 3, 3));
		ht.put("C$CF(//)", new HOSECodeShiftRange("C$CF(//)", 90.4, 6.9, "D", 95.1, 85.8, 6, 8));
		ht.put("C$CXX(//)", new HOSECodeShiftRange("C$CXX(//)", 85.1, 11.7, "S", 101.4, 76.4, 34, 49));
		ht.put("C$CXY(//)", new HOSECodeShiftRange("C$CXY(//)", 75.8, 22.6, "S", 81.5, 71.2, 3, 3));
		ht.put("C$CX(//)", new HOSECodeShiftRange("C$CX(//)", 59.5, 10.1, "D", 80.5, 50.1, 65, 107));
		ht.put("C$CYY(//)", new HOSECodeShiftRange("C$CYY(//)", 66.8, 24.1, "S", 86.7, 54.3, 7, 8));
		ht.put("C$CY(//)", new HOSECodeShiftRange("C$CY(//)", 47.3, 9.8, "D", 63.6, 38.0, 78, 161));
		ht.put("C$CI(//)", new HOSECodeShiftRange("C$CI(//)", 26.8, 29.3, "D", 34.3, 21.0, 3, 3));
		ht.put("C$C(//)", new HOSECodeShiftRange("C$C(//)", 38.2, 13.0, "T", 86.9, 19.4, 2580, 6952));
		ht.put("C$OOO(//)", new HOSECodeShiftRange("C$OOO(//)", 111.6, 5.0, "S", 111.6, 116.6, 1, 1));
		ht.put("C$OO(//)", new HOSECodeShiftRange("C$OO(//)", 94.8, 11.9, "D", 105.8, 86.6, 18, 25));
		ht.put("C$ON(//)", new HOSECodeShiftRange("C$ON(//)", 82.0, 6.4, "D", 85.4, 71.7, 10, 22));
		ht.put("C$OS$(//)", new HOSECodeShiftRange("C$OS$(//)", 97.4, 5.0, "D", 98.6, 96.2, 1, 2));
		ht.put("C$OS(//)", new HOSECodeShiftRange("C$OS(//)", 74.1, 5.0, "D", 75.4, 72.9, 2, 2));
		ht.put("C$OXX(//)", new HOSECodeShiftRange("C$OXX(//)", 98.7, 5.0, "S", 98.7, 98.7, 1, 1));
		ht.put("C$O(//)", new HOSECodeShiftRange("C$O(//)", 67.0, 5.4, "T", 77.7, 60.4, 108, 263));
		ht.put("C$NN(//)", new HOSECodeShiftRange("C$NN(//)", 62.3, 5.0, "D", 62.3, 62.3, 1, 1));
		ht.put("C$NS(//)", new HOSECodeShiftRange("C$NS(//)", 71.9, 12.7, "D", 77.0, 64.8, 3, 7));
		ht.put("C$NX(//)", new HOSECodeShiftRange("C$NX(//)", 79.9, 5.0, "D", 79.9, 79.9, 1, 1));
		ht.put("C$$NY(//)", new HOSECodeShiftRange("C$$NY(//)", 71.8, 5.0, "D", 71.8, 71.8, 1, 1));
		ht.put("C$N(//)", new HOSECodeShiftRange("C$N(//)", 50.1, 14.0, "T", 74.9, 38.3, 248, 615));
		ht.put("C$S$$(//)", new HOSECodeShiftRange("C$S$$(//)", 62.4, 5.7, "T", 67.7, 56.6, 12, 20));
		ht.put("C$S$XX(//)", new HOSECodeShiftRange("C$S$XX(//)", 93.9, 5.0, "S", 93.9, 93.9, 1, 1));
		ht.put("C$S$(//)", new HOSECodeShiftRange("C$S$(//)", 59.1, 14.8, "T", 62.7, 52.8, 4, 4));
		ht.put("C$SS(//)", new HOSECodeShiftRange("C$SS(//)", 67.5, 25.1, "D", 71.6, 44.8, 2, 7));
		ht.put("C$SXX(//)", new HOSECodeShiftRange("C$SXX(//)", 94.1, 5.0, "S", 94.1, 94.1, 1, 2));
		ht.put("C$SX(//)", new HOSECodeShiftRange("C$SX(//)", 65.5, 5.0, "D", 65.5, 65.5, 1, 1));
		ht.put("C$S(//)", new HOSECodeShiftRange("C$S(//)", 34.7, 9.7, "T", 48.5, 26.2, 92, 143));
		ht.put("C$P$(//)", new HOSECodeShiftRange("C$P$(//)", 45.1, 10.4, "T", 55.5, 32.9, 15, 30));
		ht.put("C$P(//)", new HOSECodeShiftRange("C$P(//)", 34.2, 13.7, "T", 40.1, 23.2, 6, 8));
		ht.put("C$Q(//)", new HOSECodeShiftRange("C$Q(//)", 25.3, 5.0, "T", 25.9, 24.8, 2, 2));
		ht.put("C$B(//)", new HOSECodeShiftRange("C$B(//)", 28.1, 11.7, "T", 29.8, 25.0, 3, 3));
		ht.put("C$FFF(//)", new HOSECodeShiftRange("C$FFF(//)", 117.3, 8.9, "S", 151.0, 108.6, 21, 50));
		ht.put("C$FFX(//)", new HOSECodeShiftRange("C$FFX(//)", 118.0, 5.0, "S", 118.4, 117.9, 2, 3));
		ht.put("C$FF(//)", new HOSECodeShiftRange("C$FF(//)", 108.5, 6.1, "D", 110.1, 107.4, 3, 3));
		ht.put("C$FXX(//)", new HOSECodeShiftRange("C$FXX(//)", 112.3, 5.0, "S", 113.9, 110.8, 2, 2));
		ht.put("C$FXY(//)", new HOSECodeShiftRange("C$FXY(//)", 104.2, 5.0, "S", 104.2, 104.2, 1, 1));
		ht.put("C$F(//)", new HOSECodeShiftRange("C$F(//)", 81.3, 8.2, "T", 84.9, 78.9, 3, 4));
		ht.put("C$XXX(//)", new HOSECodeShiftRange("C$XXX(//)", 92.0, 5.0, "S", 96.5, 88.5, 29, 63));
		ht.put("C$XXY(//)", new HOSECodeShiftRange("C$XXY(//)", 81.2, 5.0, "S", 81.2, 81.2, 1, 1));
		ht.put("C$XX(//)", new HOSECodeShiftRange("C$XX(//)", 66.0, 5.1, "D", 70.9, 61.6, 20, 31));
		ht.put("C$XYY(//)", new HOSECodeShiftRange("C$XYY(//)", 64.5, 5.0, "S", 64.5, 64.5, 1, 1));
		ht.put("C$XY(//)", new HOSECodeShiftRange("C$XY(//)", 57.5, 5.0, "D", 57.5, 57.5, 1, 1));
		ht.put("C$X(//)", new HOSECodeShiftRange("C$X(//)", 43.1, 5.0, "T", 49.1, 40.5, 27, 72));
		ht.put("C$YYY(//)", new HOSECodeShiftRange("C$YYY(//)", 36.5, 5.0, "S", 44.6, 28.4, 2, 2));
		ht.put("C$YY(//)", new HOSECodeShiftRange("C$YY(//)", 34.4, 7.6, "D", 39.4, 31.3, 6, 7));
		ht.put("C$Y(//)", new HOSECodeShiftRange("C$Y(//)", 30.1, 6.8, "T", 35.5, 25.0, 10, 21));
		ht.put("C$I(//)", new HOSECodeShiftRange("C$I(//)", -4.0, 5.0, "T", -3.5, -4.6, 2, 2));
		ht.put("C$(//)", new HOSECodeShiftRange("C$(//)", 23.1, 7.3, "Q", 56.0, 13.8, 464, 4656));
		ht.put("CCCC(//)", new HOSECodeShiftRange("CCCC(//)", 39.6, 17.2, "S", 252.9, -8.9, 3912, 12026));
		ht.put("CCCO(//)", new HOSECodeShiftRange("CCCO(//)", 76.6, 17.3, "S", 112.7, 49.8, 1876, 4419));
		ht.put("CCCN$$(//)", new HOSECodeShiftRange("CCCN$$(//)", 87.3, 15.5, "S", 96.2, 71.4, 19, 30));
		ht.put("CCCN$(//)", new HOSECodeShiftRange("CCCN$(//)", 91.1, 5.0, "S", 91.4, 91.0, 3, 3));
		ht.put("CCCN(//)", new HOSECodeShiftRange("CCCN(//)", 59.9, 22.4, "S", 110.4, 29.9, 575, 973));
		ht.put("CCCS$$(//)", new HOSECodeShiftRange("CCCS$$(//)", 65.3, 9.6, "S", 78.2, 57.6, 25, 34));
		ht.put("CCCS$(//)", new HOSECodeShiftRange("CCCS$(//)", 53.3, 42.8, "S", 87.4, 21.4, 12, 18));
		ht.put("CCCS(//)", new HOSECodeShiftRange("CCCS(//)", 51.1, 17.9, "S", 71.0, 31.3, 108, 171));
		ht.put("CCCP$(//)", new HOSECodeShiftRange("CCCP$(//)", 39.4, 17.1, "S", 50.8, 32.7, 7, 8));
		ht.put("CCCP(//)", new HOSECodeShiftRange("CCCP(//)", 38.1, 22.3, "S", 122.1, 23.4, 54, 110));
		ht.put("CCCQ(//)", new HOSECodeShiftRange("CCCQ(//)", 21.8, 17.9, "S", 71.0, 0.6, 24, 118));
		ht.put("CCCB(//)", new HOSECodeShiftRange("CCCB(//)", 26.7, 19.0, "S", 41.7, 17.6, 8, 9));
		ht.put("CCCF(//)", new HOSECodeShiftRange("CCCF(//)", 94.4, 11.9, "S", 112.0, 74.9, 22, 35));
		ht.put("CCCX(//)", new HOSECodeShiftRange("CCCX(//)", 76.0, 14.6, "S", 91.6, 45.8, 206, 495));
		ht.put("CCCY(//)", new HOSECodeShiftRange("CCCY(//)", 63.5, 28.5, "S", 96.6, 1.0, 53, 80));
		ht.put("CCCI(//)", new HOSECodeShiftRange("CCCI(//)", 50.2, 22.3, "S", 71.6, 31.1, 16, 43));
		ht.put("CCC(//)", new HOSECodeShiftRange("CCC(//)", 40.1, 23.0, "D", 355.5, -6.8, 9398, 30350));
		ht.put("CCOO(//)", new HOSECodeShiftRange("CCOO(//)", 106.7, 14.3, "S", 132.9, 57.8, 402, 981));
		ht.put("CCON(//)", new HOSECodeShiftRange("CCON(//)", 90.7, 24.0, "S", 119.9, 58.0, 71, 130));
		ht.put("CCOS$$(//)", new HOSECodeShiftRange("CCOS$$(//)", 86.7, 5.0, "S", 86.7, 86.7, 1, 1));
		ht.put("CCOS(//)", new HOSECodeShiftRange("CCOS(//)", 86.8, 20.5, "S", 102.7, 72.3, 12, 16));
		ht.put("CCOP$(//)", new HOSECodeShiftRange("CCOP$(//)", 94.9, 5.0, "S", 95.0, 94.8, 1, 2));
		ht.put("CCOP(//)", new HOSECodeShiftRange("CCOP(//)", 74.2, 5.0, "S", 77.5, 70.9, 2, 2));
		ht.put("CCOQ(//)", new HOSECodeShiftRange("CCOQ(//)", 78.1, 5.0, "S", 80.8, 75.5, 2, 2));
		ht.put("CCOX(//)", new HOSECodeShiftRange("CCOX(//)", 97.0, 22.4, "S", 106.7, 72.8, 16, 28));
		ht.put("CCOY(//)", new HOSECodeShiftRange("CCOY(//)", 80.1, 5.0, "S", 80.1, 80.1, 1, 1));
		ht.put("CCO(//)", new HOSECodeShiftRange("CCO(//)", 73.1, 14.4, "D", 104.3, 34.2, 5218, 20980));
		ht.put("CCN$$N$$(//)", new HOSECodeShiftRange("CCN$$N$$(//)", 116.2, 5.0, "S", 116.2, 116.2, 1, 1));
		ht.put("CCN$$X(//)", new HOSECodeShiftRange("CCN$$X(//)", 100.4, 5.0, "S", 100.4, 100.4, 1, 1));
		ht.put("CCN$$Y(//)", new HOSECodeShiftRange("CCN$$Y(//)", 89.1, 5.0, "S", 89.1, 89.1, 1, 1));
		ht.put("CCN$$(//)", new HOSECodeShiftRange("CCN$$(//)", 86.1, 11.6, "D", 96.5, 66.7, 28, 57));
		ht.put("CCN$X(//)", new HOSECodeShiftRange("CCN$X(//)", 59.8, 5.0, "S", 59.8, 59.8, 1, 1));
		ht.put("CCNN(//)", new HOSECodeShiftRange("CCNN(//)", 79.1, 18.3, "S", 106.5, 56.0, 48, 65));
		ht.put("CCNS$$(//)", new HOSECodeShiftRange("CCNS$$(//)", 93.4, 5.0, "S", 93.4, 93.4, 1, 1));
		ht.put("CCNS$(//)", new HOSECodeShiftRange("CCNS$(//)", 93.5, 5.0, "S", 94.1, 93.0, 2, 2));
		ht.put("CCNS(//)", new HOSECodeShiftRange("CCNS(//)", 75.4, 17.9, "S", 101.9, 64.0, 14, 18));
		ht.put("CCNP$(//)", new HOSECodeShiftRange("CCNP$(//)", 49.3, 5.0, "S", 49.3, 49.3, 1, 1));
		ht.put("CCNQ(//)", new HOSECodeShiftRange("CCNQ(//)", 90.4, 5.0, "S", 90.6, 90.2, 1, 2));
		ht.put("CCNX(//)", new HOSECodeShiftRange("CCNX(//)", 87.4, 16.4, "S", 96.1, 73.5, 8, 11));
		ht.put("CCNY(//)", new HOSECodeShiftRange("CCNY(//)", 73.3, 5.0, "S", 77.1, 69.6, 2, 2));
		ht.put("CCN(//)", new HOSECodeShiftRange("CCN(//)", 57.6, 18.7, "D", 163.8, 12.7, 1988, 5153));
		ht.put("CCS$$S$$(//)", new HOSECodeShiftRange("CCS$$S$$(//)", 88.4, 11.5, "S", 92.0, 80.7, 4, 8));
		ht.put("CCS$$(//)", new HOSECodeShiftRange("CCS$$(//)", 60.1, 17.0, "D", 82.0, 28.3, 91, 125));
		ht.put("CCS$S(//)", new HOSECodeShiftRange("CCS$S(//)", 62.2, 19.8, "S", 65.7, 57.0, 2, 3));
		ht.put("CCS$(//)", new HOSECodeShiftRange("CCS$(//)", 60.8, 11.9, "D", 73.3, 45.9, 21, 54));
		ht.put("CCSS(//)", new HOSECodeShiftRange("CCSS(//)", 57.6, 20.4, "S", 115.8, 29.9, 56, 97));
		ht.put("CCSP$(//)", new HOSECodeShiftRange("CCSP$(//)", 55.5, 5.0, "S", 55.8, 55.2, 1, 2));
		ht.put("CCSX(//)", new HOSECodeShiftRange("CCSX(//)", 80.8, 5.0, "S", 80.8, 80.8, 1, 1));
		ht.put("CCS(//)", new HOSECodeShiftRange("CCS(//)", 45.6, 18.4, "D", 82.2, 22.7, 299, 643));
		ht.put("CCP$(//)", new HOSECodeShiftRange("CCP$(//)", 30.6, 9.9, "D", 47.0, 26.9, 32, 81));
		ht.put("CCPP(//)", new HOSECodeShiftRange("CCPP(//)", 71.2, 5.0, "S", 71.2, 71.2, 1, 2));
		ht.put("CCP(//)", new HOSECodeShiftRange("CCP(//)", 40.3, 26.4, "D", 77.9, 0.4, 78, 170));
		ht.put("CCQQ(//)", new HOSECodeShiftRange("CCQQ(//)", 39.4, 5.0, "S", 39.4, 39.4, 1, 1));
		ht.put("CCQ(//)", new HOSECodeShiftRange("CCQ(//)", 36.3, 39.1, "D", 84.1, -7.1, 35, 54));
		ht.put("CCBB(//)", new HOSECodeShiftRange("CCBB(//)", 48.2, 5.0, "S", 56.2, 40.3, 2, 2));
		ht.put("CCB(//)", new HOSECodeShiftRange("CCB(//)", 31.0, 18.4, "D", 56.5, 18.5, 12, 38));
		ht.put("CCFF(//)", new HOSECodeShiftRange("CCFF(//)", 114.9, 18.2, "S", 134.1, 88.8, 40, 82));
		ht.put("CCFX(//)", new HOSECodeShiftRange("CCFX(//)", 104.0, 13.9, "S", 110.5, 94.5, 5, 7));
		ht.put("CCFY(//)", new HOSECodeShiftRange("CCFY(//)", 90.8, 22.1, "S", 99.5, 82.0, 2, 4));
		ht.put("CCF(//)", new HOSECodeShiftRange("CCF(//)", 90.3, 19.3, "D", 101.6, 48.9, 29, 53));
		ht.put("CCXX(//)", new HOSECodeShiftRange("CCXX(//)", 88.3, 27.7, "S", 113.9, 55.5, 137, 321));
		ht.put("CCXY(//)", new HOSECodeShiftRange("CCXY(//)", 68.0, 30.2, "S", 92.7, 50.2, 6, 11));
		ht.put("CCX(//)", new HOSECodeShiftRange("CCX(//)", 60.8, 14.2, "D", 82.4, 27.3, 295, 565));
		ht.put("CCYY(//)", new HOSECodeShiftRange("CCYY(//)", 51.0, 33.3, "S", 85.6, 27.4, 21, 28));
		ht.put("CCY(//)", new HOSECodeShiftRange("CCY(//)", 50.9, 16.1, "D", 92.7, 13.9, 183, 537));
		ht.put("CCI(//)", new HOSECodeShiftRange("CCI(//)", 30.4, 18.2, "D", 64.4, 15.8, 36, 65));
		ht.put("CC(//)", new HOSECodeShiftRange("CC(//)", 29.6, 14.8, "T", 255.1, -8.0, 24063, 96834));
		ht.put("COOO(//)", new HOSECodeShiftRange("COOO(//)", 115.2, 20.4, "S", 125.0, 77.1, 21, 35));
		ht.put("COON(//)", new HOSECodeShiftRange("COON(//)", 113.2, 19.8, "S", 133.7, 102.7, 9, 14));
		ht.put("COOS(//)", new HOSECodeShiftRange("COOS(//)", 118.1, 5.0, "S", 118.3, 117.9, 1, 2));
		ht.put("COOP$(//)", new HOSECodeShiftRange("COOP$(//)", 106.8, 5.0, "S", 106.9, 106.7, 1, 2));
		ht.put("COOX(//)", new HOSECodeShiftRange("COOX(//)", 120.5, 5.0, "S", 121.7, 118.0, 3, 4));
		ht.put("COO(//)", new HOSECodeShiftRange("COO(//)", 99.2, 8.9, "D", 122.1, 76.9, 636, 3201));
		ht.put("CON$$(//)", new HOSECodeShiftRange("CON$$(//)", 106.6, 5.0, "D", 110.8, 102.5, 2, 2));
		ht.put("CONN(//)", new HOSECodeShiftRange("CONN(//)", 107.6, 90.8, "S", 120.6, 83.3, 3, 3));
		ht.put("CONS(//)", new HOSECodeShiftRange("CONS(//)", 112.5, 5.0, "S", 115.1, 110.2, 2, 11));
		ht.put("CON(//)", new HOSECodeShiftRange("CON(//)", 88.2, 12.5, "D", 108.0, 58.2, 134, 503));
		ht.put("COS$$(//)", new HOSECodeShiftRange("COS$$(//)", 84.3, 8.2, "D", 92.1, 81.1, 4, 8));
		ht.put("COSS(//)", new HOSECodeShiftRange("COSS(//)", 94.5, 39.6, "S", 110.7, 83.3, 4, 4));
		ht.put("COS(//)", new HOSECodeShiftRange("COS(//)", 82.4, 12.3, "D", 103.4, 67.9, 19, 31));
		ht.put("COP$P$(//)", new HOSECodeShiftRange("COP$P$(//)", 72.4, 5.0, "S", 72.4, 72.4, 1, 1));
		ht.put("COP$(//)", new HOSECodeShiftRange("COP$(//)", 71.0, 5.9, "D", 73.8, 67.6, 4, 10));
		ht.put("COP(//)", new HOSECodeShiftRange("COP(//)", 72.2, 9.1, "D", 74.7, 71.0, 3, 3));
		ht.put("COFF(//)", new HOSECodeShiftRange("COFF(//)", 118.0, 5.4, "S", 119.9, 114.6, 6, 6));
		ht.put("COF(//)", new HOSECodeShiftRange("COF(//)", 106.7, 6.1, "D", 114.5, 103.3, 7, 35));
		ht.put("COXX(//)", new HOSECodeShiftRange("COXX(//)", 102.0, 6.9, "S", 104.6, 99.6, 5, 5));
		ht.put("COX(//)", new HOSECodeShiftRange("COX(//)", 88.3, 10.7, "D", 96.4, 74.9, 14, 22));
		ht.put("COY(//)", new HOSECodeShiftRange("COY(//)", 76.7, 32.6, "D", 92.1, 55.1, 7, 9));
		ht.put("COI(//)", new HOSECodeShiftRange("COI(//)", 47.4, 5.0, "D", 47.4, 47.4, 1, 1));
		ht.put("CO(//)", new HOSECodeShiftRange("CO(//)", 65.0, 10.8, "T", 173.7, 19.3, 3649, 15321));
		ht.put("CN$$XX(//)", new HOSECodeShiftRange("CN$$XX(//)", 112.3, 5.0, "S", 115.2, 109.5, 2, 2));
		ht.put("CN$$X(//)", new HOSECodeShiftRange("CN$$X(//)", 90.7, 5.0, "D", 93.3, 88.1, 2, 2));
		ht.put("CN$$(//)", new HOSECodeShiftRange("CN$$(//)", 76.1, 8.0, "T", 85.0, 68.9, 20, 37));
		ht.put("CNN(//)", new HOSECodeShiftRange("CNN(//)", 76.3, 22.3, "D", 153.0, 40.7, 73, 178));
		ht.put("CNS$$(//)", new HOSECodeShiftRange("CNS$$(//)", 70.0, 18.0, "D", 73.3, 61.6, 2, 4));
		ht.put("CNS$(//)", new HOSECodeShiftRange("CNS$(//)", 62.9, 5.0, "D", 62.9, 62.9, 1, 1));
		ht.put("CNSS(//)", new HOSECodeShiftRange("CNSS(//)", 72.4, 5.0, "S", 72.4, 72.4, 1, 1));
		ht.put("CNS(//)", new HOSECodeShiftRange("CNS(//)", 62.8, 10.9, "D", 74.5, 49.4, 27, 102));
		ht.put("CNP$(//)", new HOSECodeShiftRange("CNP$(//)", 53.7, 13.2, "D", 57.1, 51.1, 3, 3));
		ht.put("CNP(//)", new HOSECodeShiftRange("CNP(//)", 83.7, 133.7, "D", 110.0, 49.4, 3, 3));
		ht.put("CNXX(//)", new HOSECodeShiftRange("CNXX(//)", 97.1, 25.8, "S", 101.5, 90.3, 3, 3));
		ht.put("CNX(//)", new HOSECodeShiftRange("CNX(//)", 73.2, 36.4, "D", 92.8, 62.5, 5, 5));
		ht.put("CNYY(//)", new HOSECodeShiftRange("CNYY(//)", 77.6, 5.0, "S", 77.6, 77.6, 1, 1));
		ht.put("CN(//)", new HOSECodeShiftRange("CN(//)", 50.1, 15.2, "T", 84.4, -84.4, 4697, 13562));
		ht.put("CS$$S$$(//)", new HOSECodeShiftRange("CS$$S$$(//)", 83.6, 5.0, "D", 84.0, 83.2, 1, 2));
		ht.put("CS$$FF(//)", new HOSECodeShiftRange("CS$$FF(//)", 115.9, 5.0, "S", 115.9, 115.9, 1, 1));
		ht.put("CS$$X(//)", new HOSECodeShiftRange("CS$$X(//)", 73.9, 5.0, "D", 76.2, 65.7, 6, 42));
		ht.put("CS$$Y(//)", new HOSECodeShiftRange("CS$$Y(//)", 65.0, 5.0, "D", 65.0, 65.0, 1, 1));
		ht.put("CS$$(//)", new HOSECodeShiftRange("CS$$(//)", 54.1, 13.9, "T", 76.9, 31.6, 229, 446));
		ht.put("CS$S$(//)", new HOSECodeShiftRange("CS$S$(//)", 74.0, 9.9, "D", 76.3, 71.7, 1, 3));
		ht.put("CS$S(//)", new HOSECodeShiftRange("CS$S(//)", 68.3, 15.5, "D", 79.4, 50.8, 5, 12));
		ht.put("CS$XX(//)", new HOSECodeShiftRange("CS$XX(//)", 102.0, 5.0, "S", 102.0, 102.0, 1, 1));
		ht.put("CS$X(//)", new HOSECodeShiftRange("CS$X(//)", 80.3, 11.2, "D", 82.9, 77.7, 1, 3));
		ht.put("CS$(//)", new HOSECodeShiftRange("CS$(//)", 52.5, 13.8, "T", 72.0, 33.8, 88, 169));
		ht.put("CSSS(//)", new HOSECodeShiftRange("CSSS(//)", 64.6, 89.2, "S", 88.4, 50.0, 3, 3));
		ht.put("CSS(//)", new HOSECodeShiftRange("CSS(//)", 48.8, 13.9, "D", 64.5, 35.1, 53, 99));
		ht.put("CS(//)", new HOSECodeShiftRange("CS(//)", 34.1, 14.8, "T", 64.7, 15.5, 942, 2502));
		ht.put("CP$X(//)", new HOSECodeShiftRange("CP$X(//)", 62.4, 5.0, "D", 62.4, 62.4, 1, 1));
		ht.put("CP$(//)", new HOSECodeShiftRange("CP$(//)", 29.0, 10.1, "T", 41.5, 16.2, 76, 148));
		ht.put("CPQ(//)", new HOSECodeShiftRange("CPQ(//)", 21.8, 5.0, "D", 21.8, 21.8, 1, 1));
		ht.put("CP(//)", new HOSECodeShiftRange("CP(//)", 24.0, 19.2, "T", 58.0, -63.2, 360, 970));
		ht.put("CQQQ(//)", new HOSECodeShiftRange("CQQQ(//)", 24.7, 5.0, "S", 29.3, 20.1, 2, 2));
		ht.put("CQQB(//)", new HOSECodeShiftRange("CQQB(//)", 26.0, 5.0, "S", 27.2, 25.0, 2, 3));
		ht.put("CQQ(//)", new HOSECodeShiftRange("CQQ(//)", 12.5, 26.6, "D", 19.7, 8.7, 2, 3));
		ht.put("CQY(//)", new HOSECodeShiftRange("CQY(//)", 48.7, 5.0, "D", 48.7, 48.7, 1, 2));
		ht.put("CQ(//)", new HOSECodeShiftRange("CQ(//)", 16.7, 20.7, "T", 34.9, -65.1, 81, 140));
		ht.put("CBBB(//)", new HOSECodeShiftRange("CBBB(//)", 90.6, 30.1, "S", 106.3, 75.9, 4, 12));
		ht.put("CBB(//)", new HOSECodeShiftRange("CBB(//)", 76.7, 29.6, "D", 86.5, 54.3, 3, 10));
		ht.put("CB(//)", new HOSECodeShiftRange("CB(//)", 20.0, 21.2, "T", 37.5, -4.6, 36, 95));
		ht.put("CFFF(//)", new HOSECodeShiftRange("CFFF(//)", 122.7, 5.0, "S", 128.4, 104.3, 138, 363));
		ht.put("CFFX(//)", new HOSECodeShiftRange("CFFX(//)", 124.9, 5.2, "S", 128.1, 122.4, 6, 7));
		ht.put("CFFY(//)", new HOSECodeShiftRange("CFFY(//)", 119.3, 5.0, "S", 119.3, 119.3, 1, 1));
		ht.put("CFF(//)", new HOSECodeShiftRange("CFF(//)", 110.6, 8.1, "D", 115.0, 107.8, 5, 5));
		ht.put("CFXX(//)", new HOSECodeShiftRange("CFXX(//)", 118.4, 6.7, "S", 120.2, 116.1, 3, 4));
		ht.put("CFX(//)", new HOSECodeShiftRange("CFX(//)", 95.2, 5.0, "D", 95.7, 94.5, 4, 4));
		ht.put("CF(//)", new HOSECodeShiftRange("CF(//)", 83.0, 8.4, "T", 92.4, 78.1, 8, 14));
		ht.put("CXXX(//)", new HOSECodeShiftRange("CXXX(//)", 97.6, 10.6, "S", 110.5, 69.2, 125, 244));
		ht.put("CXXY(//)", new HOSECodeShiftRange("CXXY(//)", 78.5, 8.8, "S", 82.4, 76.4, 4, 4));
		ht.put("CXX(//)", new HOSECodeShiftRange("CXX(//)", 71.8, 8.9, "D", 84.0, 63.1, 68, 132));
		ht.put("CXYY(//)", new HOSECodeShiftRange("CXYY(//)", 63.3, 5.0, "S", 63.3, 63.3, 1, 1));
		ht.put("CXY(//)", new HOSECodeShiftRange("CXY(//)", 57.4, 5.0, "D", 61.3, 55.2, 6, 22));
		ht.put("CX(//)", new HOSECodeShiftRange("CX(//)", 43.9, 8.9, "T", 59.5, 25.3, 254, 564));
		ht.put("CYYY(//)", new HOSECodeShiftRange("CYYY(//)", 45.7, 19.7, "S", 63.2, 31.5, 9, 11));
		ht.put("CYY(//)", new HOSECodeShiftRange("CYY(//)", 44.7, 14.1, "D", 63.2, 32.4, 30, 41));
		ht.put("CY(//)", new HOSECodeShiftRange("CY(//)", 32.7, 8.9, "T", 48.5, 13.5, 167, 380));
		ht.put("CI(//)", new HOSECodeShiftRange("CI(//)", 4.9, 11.1, "T", 26.3, -16.5, 43, 98));
		ht.put("C(//)", new HOSECodeShiftRange("C(//)", 20.3, 13.3, "Q", 68.7, -9.9, 13215, 72490));
		ht.put("OOOO(//)", new HOSECodeShiftRange("OOOO(//)", 128.9, 50.1, "S", 135.8, 115.5, 3, 3));
		ht.put("OOO(//)", new HOSECodeShiftRange("OOO(//)", 113.0, 5.7, "D", 117.1, 107.1, 10, 12));
		ht.put("OON(//)", new HOSECodeShiftRange("OON(//)", 114.1, 37.8, "D", 130.1, 101.6, 4, 4));
		ht.put("OOP$(//)", new HOSECodeShiftRange("OOP$(//)", 95.8, 5.0, "D", 95.8, 95.8, 1, 1));
		ht.put("OO(//)", new HOSECodeShiftRange("OO(//)", 96.9, 10.1, "T", 109.6, 80.8, 106, 472));
		ht.put("ONN(//)", new HOSECodeShiftRange("ONN(//)", 105.9, 5.0, "D", 105.9, 105.9, 1, 1));
		ht.put("ON(//)", new HOSECodeShiftRange("ON(//)", 76.4, 18.7, "T", 92.5, 60.2, 60, 87));
		ht.put("OS$$(//)", new HOSECodeShiftRange("OS$$(//)", 77.4, 5.0, "T", 79.9, 75.0, 2, 2));
		ht.put("OS(//)", new HOSECodeShiftRange("OS(//)", 78.1, 5.0, "T", 78.1, 78.1, 1, 1));
		ht.put("OP$(//)", new HOSECodeShiftRange("OP$(//)", 60.0, 8.9, "T", 66.1, 55.3, 4, 9));
		ht.put("OP(//)", new HOSECodeShiftRange("OP(//)", 65.9, 5.0, "T", 66.2, 65.6, 1, 2));
		ht.put("OQ(//)", new HOSECodeShiftRange("OQ(//)", 56.9, 5.0, "T", 58.0, 56.0, 3, 3));
		ht.put("OFFF(//)", new HOSECodeShiftRange("OFFF(//)", 120.4, 5.0, "S", 120.9, 118.0, 3, 10));
		ht.put("OFFX(//)", new HOSECodeShiftRange("OFFX(//)", 125.2, 5.0, "S", 125.4, 125.0, 1, 5));
		ht.put("OFF(//)", new HOSECodeShiftRange("OFF(//)", 116.0, 5.0, "D", 117.1, 114.3, 4, 4));
		ht.put("OXXX(//)", new HOSECodeShiftRange("OXXX(//)", 119.8, 5.0, "S", 119.8, 119.8, 1, 1));
		ht.put("OX(//)", new HOSECodeShiftRange("OX(//)", 76.9, 16.0, "T", 85.0, 68.9, 4, 5));
		ht.put("O(//)", new HOSECodeShiftRange("O(//)", 54.7, 6.2, "Q", 87.1, 27.3, 739, 11054));
		ht.put("N$$(//)", new HOSECodeShiftRange("N$$(//)", 62.4, 5.0, "Q", 63.5, 61.3, 1, 3));
		ht.put("NNNS(//)", new HOSECodeShiftRange("NNNS(//)", 104.2, 5.0, "S", 104.2, 104.2, 1, 1));
		ht.put("NNN(//)", new HOSECodeShiftRange("NNN(//)", 101.8, 42.4, "D", 150.4, 87.3, 7, 10));
		ht.put("NNS(//)", new HOSECodeShiftRange("NNS(//)", 78.2, 8.0, "D", 80.3, 76.7, 3, 3));
		ht.put("NNXX(//)", new HOSECodeShiftRange("NNXX(//)", 103.5, 5.0, "S", 106.6, 100.5, 2, 2));
		ht.put("NN(//)", new HOSECodeShiftRange("NN(//)", 66.9, 18.3, "T", 83.7, 42.8, 81, 298));
		ht.put("NS$$(//)", new HOSECodeShiftRange("NS$$(//)", 62.0, 5.0, "T", 62.0, 62.0, 1, 1));
		ht.put("NSXX(//)", new HOSECodeShiftRange("NSXX(//)", 50.1, 5.0, "S", 50.1, 50.1, 1, 1));
		ht.put("NS(//)", new HOSECodeShiftRange("NS(//)", 51.2, 14.7, "T", 60.4, 33.3, 20, 30));
		ht.put("NP$P$(//)", new HOSECodeShiftRange("NP$P$(//)", 50.7, 5.0, "D", 56.0, 45.4, 1, 2));
		ht.put("NP$(//)", new HOSECodeShiftRange("NP$(//)", 52.0, 16.8, "T", 59.5, 37.9, 9, 15));
		ht.put("NPQ(//)", new HOSECodeShiftRange("NPQ(//)", 90.4, 5.0, "D", 90.4, 90.4, 1, 1));
		ht.put("NQ(//)", new HOSECodeShiftRange("NQ(//)", 32.4, 20.2, "T", 44.7, 26.0, 5, 5));
		ht.put("NFFF(/))", new HOSECodeShiftRange("NFFF(/))", 119.3, 5.0, "S", 121.3, 116.1, 3, 6));
		ht.put("NXX(//)", new HOSECodeShiftRange("NXX(//)", 80.3, 11.8, "D", 88.0, 76.4, 6, 8));
		ht.put("NX(//)", new HOSECodeShiftRange("NX(//)", 58.4, 11.6, "T", 72.9, 49.4, 13, 19));
		ht.put("NY(//)", new HOSECodeShiftRange("NY(//)", 46.1, 5.0, "T", 46.1, 46.1, 1, 2));
		ht.put("N(//)", new HOSECodeShiftRange("N(//)", 40.1, 14.3, "Q", 65.6, 19.8, 1842, 7007));
		ht.put("S$$S$$(//)", new HOSECodeShiftRange("S$$S$$(//)", 69.3, 6.5, "T", 70.8, 67.8, 1, 3));
		ht.put("S$$S$(//)", new HOSECodeShiftRange("S$$S$(//)", 66.4, 5.0, "T", 66.8, 66.0, 1, 2));
		ht.put("S$$S(//)", new HOSECodeShiftRange("S$$S(//)", 68.4, 5.0, "T", 68.4, 68.4, 1, 2));
		ht.put("S$$FFF(//)", new HOSECodeShiftRange("S$$FFF(//)", 118.8, 5.0, "S", 120.3, 118.0, 5, 8));
		ht.put("S$$FF(//)", new HOSECodeShiftRange("S$$FF(//)", 114.7, 5.0, "D", 114.7, 114.7, 1, 1));
		ht.put("S$$XXX(//)", new HOSECodeShiftRange("S$$XXX(//)", 107.3, 5.0, "S", 107.3, 107.3, 1, 1));
		ht.put("S$$XX(//)", new HOSECodeShiftRange("S$$XX(//)", 78.8, 5.0, "D", 78.8, 78.8, 1, 1));
		ht.put("S$$X(//)", new HOSECodeShiftRange("S$$X(//)", 74.5, 5.0, "T", 74.5, 74.5, 1, 1));
		ht.put("S$$(//)", new HOSECodeShiftRange("S$$(//)", 40.5, 5.8, "Q", 52.6, 30.9, 56, 169));
		ht.put("S$S$(//)", new HOSECodeShiftRange("S$S$(//)", 68.6, 5.0, "T", 69.7, 65.9, 2, 4));
		ht.put("S$S(//)", new HOSECodeShiftRange("S$S(//)", 51.6, 5.6, "T", 53.1, 49.8, 2, 4));
		ht.put("S$(//)", new HOSECodeShiftRange("S$(//)", 40.6, 7.0, "Q", 57.0, 34.8, 34, 94));
		ht.put("SSS(//)", new HOSECodeShiftRange("SSS(//)", 56.7, 29.9, "D", 69.3, 41.7, 3, 5));
		ht.put("SSP$(//)", new HOSECodeShiftRange("SSP$(//)", 44.1, 5.0, "D", 44.1, 44.1, 1, 1));
		ht.put("SSX(//)", new HOSECodeShiftRange("SSX(//)", 74.9, 5.0, "D", 74.9, 74.9, 1, 1));
		ht.put("SS(//)", new HOSECodeShiftRange("SS(//)", 39.2, 24.4, "T", 97.7, 18.6, 35, 57));
		ht.put("SQ(//)", new HOSECodeShiftRange("SQ(//)", 23.2, 5.0, "T", 23.2, 23.2, 1, 2));
		ht.put("SFFF(//)", new HOSECodeShiftRange("SFFF(//)", 129.2, 5.0, "S", 129.7, 128.2, 4, 13));
		ht.put("SFFX(//)", new HOSECodeShiftRange("SFFX(//)", 130.0, 5.0, "S", 130.1, 129.9, 1, 6));
		ht.put("SFXX(//)", new HOSECodeShiftRange("SFXX(//)", 121.3, 5.0, "S", 121.6, 119.4, 3, 13));
		ht.put("SXXX(//)", new HOSECodeShiftRange("SXXX(//)", 99.3, 5.0, "S", 102.9, 97.6, 6, 44));
		ht.put("SXX(//)", new HOSECodeShiftRange("SXX(//)", 77.0, 5.0, "D", 78.2, 75.9, 2, 2));
		ht.put("SX(//)", new HOSECodeShiftRange("SX(//)", 46.3, 10.9, "T", 50.8, 42.5, 3, 4));
		ht.put("S(//)", new HOSECodeShiftRange("S(//)", 19.4, 15.0, "Q", 49.0, 6.5, 217, 732));
		ht.put("P$XXX(//)", new HOSECodeShiftRange("P$XXX(//)", 89.0, 5.0, "S", 89.0, 89.0, 1, 1));
		ht.put("P$X(//)", new HOSECodeShiftRange("P$X(//)", 35.3, 13.1, "T", 45.6, 33.0, 3, 6));
		ht.put("P$(//)", new HOSECodeShiftRange("P$(//)", 14.2, 6.5, "Q", 20.7, 4.3, 29, 65));
		ht.put("PP(//)", new HOSECodeShiftRange("PP(//)", 33.3, 5.0, "D", 33.3, 33.3, 1, 1));
		ht.put("PPQ(//)", new HOSECodeShiftRange("PPQ(//)", 17.6, 5.0, "D", 17.6, 17.6, 1, 1));
		ht.put("PP(//)", new HOSECodeShiftRange("PP(//)", 35.9, 32.9, "T", 53.9, 13.8, 6, 8));
		ht.put("PQ(//)", new HOSECodeShiftRange("PQ(//)", 16.0, 18.6, "T", 23.4, 7.8, 4, 6));
		ht.put("PFFF(//)", new HOSECodeShiftRange("PFFF(//)", 124.5, 5.0, "S", 126.4, 118.7, 9, 15));
		ht.put("PX(//)", new HOSECodeShiftRange("PX(//)", 43.4, 5.0, "T", 53.1, 33.8, 2, 2));
		ht.put("P(//)", new HOSECodeShiftRange("P(//)", 16.6, 13.0, "Q", 34.9, -38.8, 308, 1520));
		ht.put("QQQQ(//)", new HOSECodeShiftRange("QQQQ(//)", 7.7, 28.1, "S", 13.1, 0.3, 2, 3));
		ht.put("QQ(//)", new HOSECodeShiftRange("QQ(//)", 2.5, 5.0, "T", 6.1, -2.2, 8, 13));
		ht.put("QXX(//)", new HOSECodeShiftRange("QXX(//)", 62.6, 5.0, "D", 64.6, 61.9, 2, 4));
		ht.put("QX(//)", new HOSECodeShiftRange("QX(//)", 29.9, 10.5, "T", 32.7, 20.7, 5, 7));
		ht.put("QY(//)", new HOSECodeShiftRange("QY(//)", 16.6, 5.0, "T", 17.6, 14.8, 3, 4));
		ht.put("QI(//)", new HOSECodeShiftRange("QI(//)", -13.1, 5.0, "T", -13.1, -13.1, 1, 2));
		ht.put("Q(//)", new HOSECodeShiftRange("Q(//)", 1.3, 9.8, "Q", 54.6, -30.3, 420, 2793));
		ht.put("BBBB(//)", new HOSECodeShiftRange("BBBB(//)", 62.4, 5.0, "S", 63.4, 61.4, 1, 4));
		ht.put("BBB(//)", new HOSECodeShiftRange("BBB(//)", 102.4, 5.0, "D", 102.4, 102.4, 1, 2));
		ht.put("B(//)", new HOSECodeShiftRange("B(//)", 7.5, 16.2, "Q", 19.2, -15.7, 36, 62));
		ht.put("FF(//)", new HOSECodeShiftRange("FF(//)", 108.5, 5.0, "T", 108.5, 108.5, 1, 1));
		ht.put("F(//)", new HOSECodeShiftRange("F(//)", 73.5, 7.4, "Q", 75.2, 71.8, 1, 3));
		ht.put("XXXX(//)", new HOSECodeShiftRange("XXXX(//)", 96.1, 5.0, "S", 96.3, 95.9, 1, 5));
		ht.put("XXXY(//)", new HOSECodeShiftRange("XXXY(//)", 67.4, 5.0, "S", 67.5, 67.3, 1, 2));
		ht.put("XXX(//)", new HOSECodeShiftRange("XXX(//)", 77.2, 5.0, "D", 77.3, 77.1, 1, 7));
		ht.put("XXYY(//)", new HOSECodeShiftRange("XXYY(//)", 35.4, 5.0, "S", 35.4, 35.4, 1, 1));
		ht.put("XXY(//)", new HOSECodeShiftRange("XXY(//)", 56.9, 5.0, "D", 57.2, 56.6, 1, 2));
		ht.put("XX(//)", new HOSECodeShiftRange("XX(//)", 53.8, 5.0, "T", 53.9, 53.7, 1, 4));
		ht.put("XYYY(//)", new HOSECodeShiftRange("XYYY(//)", 4.8, 5.0, "S", 4.8, 4.8, 1, 1));
		ht.put("XYY(//)", new HOSECodeShiftRange("XYY(//)", 34.2, 5.0, "D", 34.2, 34.2, 1, 2));
		ht.put("XY(//)", new HOSECodeShiftRange("XY(//)", 38.6, 5.0, "T", 40.3, 36.9, 1, 2));
		ht.put("X(//)", new HOSECodeShiftRange("X(//)", 25.2, 5.0, "Q", 25.7, 24.7, 1, 2));
		ht.put("YYYY(//)", new HOSECodeShiftRange("YYYY(//)", -30.0, 5.0, "S", -29.5, -30.5, 1, 4));
		ht.put("YYY(//)", new HOSECodeShiftRange("YYY(//)", 11.5, 5.6, "D", 12.8, 10.2, 1, 3));
		ht.put("YY(//)", new HOSECodeShiftRange("YY(//)", 20.3, 5.0, "T", 21.6, 19.0, 1, 2));
		ht.put("Y(//)", new HOSECodeShiftRange("Y(//)", 9.6, 5.0, "Q", 9.9, 9.3, 1, 3));
		ht.put("III(//)", new HOSECodeShiftRange("III(//)", -97.4, 2.4, "D", -99.9, -99.9, 1, 2));
		ht.put("II(//)", new HOSECodeShiftRange("II(//)", -58.1, 5.0, "T", -52.5, -63.7, 1, 2));
		ht.put("I(//)", new HOSECodeShiftRange("I(//)", -21.0, 5.0, "Q", -20.0, -22.0, 1, 5));
	}
}
