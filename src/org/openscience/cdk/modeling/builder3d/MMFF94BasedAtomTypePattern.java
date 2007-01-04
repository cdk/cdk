/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.modeling.builder3d;

import java.util.Vector;
import java.util.regex.Pattern;

/**
 *  Class stores hose code patterns to identify mm2 force field atom types
 *
 * @author     chhoppe
 * @cdk.created    2004-09-07
 * @cdk.module     forcefield
 */
public class MMFF94BasedAtomTypePattern {

	private Vector atomTypePatterns = new Vector();

	/**
	 *Constructor for the MM2BasedAtomTypePattern object
	 */
	MMFF94BasedAtomTypePattern() {
		createPattern();

	}

	/**
	 *  Gets the atomTypePatterns attribute of the MM2BasedAtomTypePattern object
	 *
	 * @return    The atomTypePatterns as a vector
	 */
	public Vector getAtomTypePatterns() {
		return atomTypePatterns;
	}

	/**
	 *  Creates the atom type pattern
	 */
	private void createPattern() {
		
		atomTypePatterns.addElement(Pattern.compile("C-[0-4][+]?+;[A-Za-z+-]{0,6}[(].*+"));
		//Csp3
		atomTypePatterns.addElement(Pattern.compile("[C]-[1-3];[H]{0,2}+[A-Za-z]*+=[C]{1}+.*+"));
		//Csp2
		atomTypePatterns.addElement(Pattern.compile("[CS]-[0-3];[H]{0,2}+[A-Za-z]*+=[NOPS]{1}+.*+"));
		//Csp2 C=
		atomTypePatterns.addElement(Pattern.compile("C-[0-2];[H]{0,1}+%.*+"));
		//csp
		atomTypePatterns.addElement(Pattern.compile("[CS]-[3][-]?+;[A-Za-z]{0,2}+=O=O[A-Za-z]{0,2}+[(].*+"));
		//C(S)O2-M
		atomTypePatterns.addElement(Pattern.compile("C-[2-3];[H]{0,1}+=N[+]?+N[+]?+C[(].*+"));
		//CNN+ N+=C-N
		atomTypePatterns.addElement(Pattern.compile("C-1[-]?+;%N[+]?+[(].*+"));
		//c in isonitrile C%
		atomTypePatterns.addElement(Pattern.compile("C-2;=N[+]?+N[(].*+"));
		//imidazolium IM+ 
		atomTypePatterns.addElement(Pattern.compile("C-[0-4];[A-Za-z+-]{1,6}[(].*+"));
		//CR4R Csp3 in 4 member rings -> in configure atom type (20)
		atomTypePatterns.addElement(Pattern.compile("C-[0-4];[A-Za-z+-]{1,6}[(].*+"));
		//CR3R Csp3 in 3 member rings -> in configure atom type (10)
		atomTypePatterns.addElement(Pattern.compile("[C]-[0-3];[H]{0,2}+[A-Za-z]*+=[A-Z]{1,2}+.*+"));
		//CE4R Csp2 ->configure atom 4mRing
		atomTypePatterns.addElement(Pattern.compile("[C]-[0-3];[H]{0,2}+[A-Za-z]*+=[A-Z]{1,2}+.*+"));
		//Car Csp2 aromatic 
		atomTypePatterns.addElement(Pattern.compile("C-[2-3];[H]?+[C]{1}+[A-Z&&[^C]]{1}+[(].*+"));
		//C5A atom configure alpha carbon 5 mem. hetero ring
		atomTypePatterns.addElement(Pattern.compile("C-[2-3];[H]?+[C]{2,3}+[(][HC]{0,2}+[,]?+[A-Z&&[^C]]{1}+.*+"));
		//C5B atom configure beta carbon 5 mem. hetero ring
		atomTypePatterns.addElement(Pattern.compile("NO PATTERN"));
		// C5 c or n in heteroaromtaic ring, not alpha or beta C5/N5 (15)
		atomTypePatterns.addElement(Pattern.compile("H-[1];[C,Si][+]?+[(].*+"));
		//HC
		atomTypePatterns.addElement(Pattern.compile("H-[1];O[(].{2,}+.*+"));
		//HO
		atomTypePatterns.addElement(Pattern.compile("H-[0-1];[N][(].*+"));
		//HN
		atomTypePatterns.addElement(Pattern.compile("H-1;O[(]C/[H]{0,1}+=O.*+"));
		//HO COOH-> configure Atom
		atomTypePatterns.addElement(Pattern.compile("H-[0-1];N[(][H]{0,2}+=C.*+"));
		//HN=C -> configure atom (20)
		atomTypePatterns.addElement(Pattern.compile("H-[0-1];N[(][H]{0,2}+=[A-Z[^C]].*+"));
		//HN2 HN=X -> configure atom
		atomTypePatterns.addElement(Pattern.compile("H-[0-1];O[(]C/[H]?+=C.*+"));
		//HOCC enol phenol
		atomTypePatterns.addElement(Pattern.compile("H-[0-1];O[(]H[)]"));
		//HOH
		atomTypePatterns.addElement(Pattern.compile("H-[0-1];O[(][H]{0,2}+=?+S.*+"));
		//HOS
		atomTypePatterns.addElement(Pattern.compile("H-[0-1];[N][+][(].*+"));
		//HN+
		atomTypePatterns.addElement(Pattern.compile("H-[1];O[+][(][A-Za-z[^=]]{1,4}+.*+"));
		//HO+ 
		atomTypePatterns.addElement(Pattern.compile("H-[1];O[+][(].*+"));
		//HO=+
		atomTypePatterns.addElement(Pattern.compile("H-[1];[SP].*+"));
		//H on S or P (28)
		
		atomTypePatterns.addElement(Pattern.compile("O-[2];[HCSN]{1,2}+[+]?+[(].*+"));
		//O Ether,Alcohol
		atomTypePatterns.addElement(Pattern.compile("O-[1];=.*+"));
		//0= (30)
		atomTypePatterns.addElement(Pattern.compile("O-[1];=[A-Za-z[^C]]{1,2}+.*+"));
		//O=X
		atomTypePatterns.addElement(Pattern.compile("O-[1][-]?+;.*+"));
		//OM O-
		atomTypePatterns.addElement(Pattern.compile("O-[3][+];.*+"));
		//O+
		atomTypePatterns.addElement(Pattern.compile("O-[1-2][+];[A-Za-z]{0,2}+=.*+"));
		//O=+
		atomTypePatterns.addElement(Pattern.compile("O-[1-2];[H]{0,2}+"));
		//O in water 
		atomTypePatterns.addElement(Pattern.compile("O-2;CC.=C.*+&.*+&.*+"));
		//osp2furan (36)
		
		atomTypePatterns.addElement(Pattern.compile("N-[0-3];[A-Za-z &&[^=%]]{1,3}+.*+"));
		//N nsp3
		atomTypePatterns.addElement(Pattern.compile("N-[1-3];[H]{0,2}+[A-Za-z]*+=[CN].*+"));
		//N=C n imides
		atomTypePatterns.addElement(Pattern.compile("N-[1-3];[H]{0,3}+[C]*+[(].*+=C.*+"));
		//NC=C
		atomTypePatterns.addElement(Pattern.compile("N-[1-2][+]?+;%.*+"));
		//nsp (40)
		atomTypePatterns.addElement(Pattern.compile("N-[2][+]?+;=[NC]=[NC][-]?+[(].*+"));
		//n =N= C=N=N N=N=N)
		atomTypePatterns.addElement(Pattern.compile("N-1[+-]?+;%?+=?+N[+]?+[(]=?+N[-]?+.*+"));
		//NAZT terminal n in azido);
		atomTypePatterns.addElement(Pattern.compile("N-4[+];.*+"));
		//N+ nsp3 ammonium
		atomTypePatterns.addElement(Pattern.compile("N-[2-3][+]?+;=[A-Z[^O]]{1,2}+O[-]?+[(].*+"));
		//N2OX n aromatic n oxide sp2
		atomTypePatterns.addElement(Pattern.compile("N-[1-3];[H]{0,2}+[O]{0,1}+[-]?+[A-Za-z[^O]]{0,2}+[O]{0,1}+[-]?+[(].*+"));
		//N3OX aromatic n oxide sp3
		atomTypePatterns.addElement(Pattern.compile("N-[1-3][+]?+;[H]{0,2}+[A-Za-z]{0,6}+[(].*+%C.*+%N.*+"));
		//NC#N N->CN
		atomTypePatterns.addElement(Pattern.compile("N-3[+];=OCO-.*+"));
		//n no2
		atomTypePatterns.addElement(Pattern.compile("N-2;[A-Z[^O]]{0,1}=O[A-Z[^O]]{0,1}[(].*+"));
		//n N=O
		atomTypePatterns.addElement(Pattern.compile("N-[1-3];[CH]{1,3}.{1}+[A-Z]{0,3}+[,]?+=OC.*+"));
		//NC=0 amid 
		atomTypePatterns.addElement(Pattern.compile("N-1-2];[CH]{1}+=S[(].*+"));
		//NSO (50)
		atomTypePatterns.addElement(Pattern.compile("N-[1-3][+];[H]{0,2}+=[A-Za-z]{1,3}+[(].*+"));
		//n N+=  
		atomTypePatterns.addElement(Pattern.compile("N-[0-3][+];[H]{0,2}+=C[(][A-Za-z[^=%N]]{0,7}[N]{1}+/.*+"));
		//n NCN+
		atomTypePatterns.addElement(Pattern.compile("N-[0-3][+];[H]{0,2}+=C[(][N]]{2}+/.*+"));
		//n NGD+
		atomTypePatterns.addElement(Pattern.compile("N-[1-2][+];[H]{0,1}+%[NC][-]?+[(].*+"));
		//NR% n in isonitrile, diazo
		atomTypePatterns.addElement(Pattern.compile("N-[1-2][-];[H]{0,1}+S[A-Z]{0,1}+[(][H]{0,4}+=?+O[-]?+.*+"));
		//NM n deproonated sulfonamid
		atomTypePatterns.addElement(Pattern.compile("N-[2][-];.*+"));
		//N5M neg charged n
		atomTypePatterns.addElement(Pattern.compile("N-[2-3];[H]{0,1}+[A-Za-z[^N]]{2,3}+[(].*+"));
		//NPYD n aromatic 6
		atomTypePatterns.addElement(Pattern.compile("N-[2-3];[H]{0,1}+[A-Za-z[^N]]{2,3}+[(].*+"));
		//NPYL n aromtiac 5
		atomTypePatterns.addElement(Pattern.compile("N-[2-3][+];[H]{0,1}+[A-Za-z[^NO]]{2,3}+[(].*+"));
		//n npyd+ NCN+ Pyrimidinium
		atomTypePatterns.addElement(Pattern.compile("N-[2-3][+]?+;[H]{0,1}+=?+[N,O,S]{0,1}+[+]?+=?+C[+]?+[N,O,S]{0,1}+[(].*+"));
		//N5A n aromatic 5 CN=N (60)
		atomTypePatterns.addElement(Pattern.compile("N-[2,3];[H]{0,1}+=CC[(][H]{0,3}+=?+[A-Z[^C]].*+"));
		//N5B n aromatic 5 N=CN
		atomTypePatterns.addElement(Pattern.compile("N-[3][+];[A-Z[^ON]]{2}+O[-]?+[(].*+"));
		//NPOX n aromatic n oxide aromatic 6 ring -> configure
		atomTypePatterns.addElement(Pattern.compile("N-[3][+];[A-Z[^ON]]{2}+O[-]?+[(].*+"));
		//N5Ox
		atomTypePatterns.addElement(Pattern.compile("NO PATTERN"));
		//N5+
		atomTypePatterns.addElement(Pattern.compile("N-[1-3];[H]{0,1}+[A-Za-z]++[(].*+"));
		//N5 (65)
		
		atomTypePatterns.addElement(Pattern.compile("S-[1-2];[HCNO]{1,2}+[+]?+[(].*+"));
		//S thioether, mercaptane
		atomTypePatterns.addElement(Pattern.compile("S-[1];[H]{0,2}+=C.*+"));
		//terminal S=C
		atomTypePatterns.addElement(Pattern.compile("S-[1-3];[H]{0,2}+=[ON].*+"));
		//>SN
		atomTypePatterns.addElement(Pattern.compile("S-[3-4];[H]{0,2}+=[OCN]=[OCN]=?+[OCN]{0,2}+[(].*+"));
		//SO2
		atomTypePatterns.addElement(Pattern.compile("S-[1-2][-]?+;[H]{0,1}+[A-Za-z]{0,2}+[(].*+"));
		//temrinal SX (70)
		atomTypePatterns.addElement(Pattern.compile("S-[3];=OO[-]?+[A-Za-z]{1,2}+[-]?+[(].*+"));
		//S SO2 in negativly charged SO2R group
		atomTypePatterns.addElement(Pattern.compile("S-[2];=[A-Za-z]{1,2}=O+[(].*+"));
		//=SO
		atomTypePatterns.addElement(Pattern.compile("S-[2];[H]{0,3}+=C.*+"));
		//Stringin thiophen (73)
		
		atomTypePatterns.addElement(Pattern.compile("P-[4];.*+"));
		//P tetra ->configure Atom for P
		atomTypePatterns.addElement(Pattern.compile("P-[0-3];.*+"));
		//P tri -> configure atom for P=C
		atomTypePatterns.addElement(Pattern.compile("P-[2];=C[A-Za-z]{1,2}+[(].*+"));
		//P C=P-		
		atomTypePatterns.addElement(Pattern.compile("F-[0-7][+]?+;.*+"));
		//F
		atomTypePatterns.addElement(Pattern.compile("Cl-[0-7][+]?+;.*+"));
		//Cl
		atomTypePatterns.addElement(Pattern.compile("Br-[0-7][+]?+;.*+"));
		//Br
		atomTypePatterns.addElement(Pattern.compile("I.*+"));
		//I		
		atomTypePatterns.addElement(Pattern.compile("Si.*+"));
		//Silane
		atomTypePatterns.addElement(Pattern.compile("Cl[4];.*+"));
		//cl in perchlorat anion
		atomTypePatterns.addElement(Pattern.compile("Fe2[+].*+"));
		//Fe 2
		atomTypePatterns.addElement(Pattern.compile("Fe3[+].*+"));
		//Fe 3
		atomTypePatterns.addElement(Pattern.compile("F-[0-2][-];.*+"));
		//F
		atomTypePatterns.addElement(Pattern.compile("Cl-[0-2][-];.*+"));
		//Cl
		atomTypePatterns.addElement(Pattern.compile("Br-[0-2][-];.*+"));
		//Br
		atomTypePatterns.addElement(Pattern.compile("Li-[0-2][+];.*+"));
		//Li+
		atomTypePatterns.addElement(Pattern.compile("Na[+];.*+"));
		//Na+
		atomTypePatterns.addElement(Pattern.compile("K[+];.*+"));
		//K+
		atomTypePatterns.addElement(Pattern.compile("Zn2[+];.*+"));
		//Zn2+
		atomTypePatterns.addElement(Pattern.compile("Ca2[+];.*+"));
		//Ca2+
		atomTypePatterns.addElement(Pattern.compile("Cu[+];.*+"));
		//Cu1+
		atomTypePatterns.addElement(Pattern.compile("Cu2[+];.*+"));
		//Cu2+
		atomTypePatterns.addElement(Pattern.compile("Mg2[+];.*+"));
		//Mg2+
	}
}

