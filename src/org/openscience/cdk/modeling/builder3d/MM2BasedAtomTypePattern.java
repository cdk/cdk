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
 *  Class stores hose code patterns to identify mm2 force field atom types.
 *
 * @author      chhoppe
 * @cdk.created 2004-09-07
 * @cdk.module  forcefield
 */
public class MM2BasedAtomTypePattern {

	private Vector atomTypePatterns = new Vector();

	/**
	 *Constructor for the MM2BasedAtomTypePattern object
	 */
	MM2BasedAtomTypePattern() {
		this.createPattern();
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
		atomTypePatterns.addElement(Pattern.compile("[CSP]-[0-4][-]?+;[A-Za-z+-]{0,6}[(].*+"));
		//Csp3
		atomTypePatterns.addElement(Pattern.compile("[CS]-[0-3];[H]{0,2}+[A-Za-z]*+=[A-Z]{1,2}+.*+"));
		//Csp2
		atomTypePatterns.addElement(Pattern.compile("C-[0-3];=O.*+"));
		//C carbonyl
		atomTypePatterns.addElement(Pattern.compile("C-[1-2][-]?+;[H]{0,1}+%.*+"));
		//csp
		atomTypePatterns.addElement(Pattern.compile("H-[0-1];[C].*+"));
		//H
		atomTypePatterns.addElement(Pattern.compile("[OS]-[0-2][-]?+;[A-Za-z]{1,4}+[+]?+[(].*+"));
		//O Ether,Alcohol
		atomTypePatterns.addElement(Pattern.compile("O-[1-2][+]?+;[H]{0,1}+=[SPC].[^O]++.*+"));
		//=0 Carbonyl
		atomTypePatterns.addElement(Pattern.compile("N-[0-3][+-]?+;[A-Z &&[^=%]]{1,3}+.*+"));
		//nsp3
		atomTypePatterns.addElement(Pattern.compile("N-[1-3][-+]?+;=?+[ON]?+[+]?+[CH]*+.(=O)?+.*+"));
		//nsp2amide
		atomTypePatterns.addElement(Pattern.compile("N-[1-2][+]?+;%.*+"));
		//nsp (10)
		atomTypePatterns.addElement(Pattern.compile("F.*+"));
		//F
		atomTypePatterns.addElement(Pattern.compile("Cl.*+"));
		//Cl
		atomTypePatterns.addElement(Pattern.compile("Br.*+"));
		//Br
		atomTypePatterns.addElement(Pattern.compile("I.*+"));
		//I
		atomTypePatterns.addElement(Pattern.compile("S-[1-2][-]?+;[HCSON]{1,2}+[(].*+"));
		//S Sulfide
		atomTypePatterns.addElement(Pattern.compile("S-3+;.?+[A-Za-z]++.*+"));
		//S+Sulfonium
		atomTypePatterns.addElement(Pattern.compile("S-[1-2][+]?+;=[OCNP][A-Z]++.*+"));
		//S=0
		atomTypePatterns.addElement(Pattern.compile("S-4;=O=O[A-Za-z]++.*+"));
		//So2
		atomTypePatterns.addElement(Pattern.compile("Si.*+"));
		//Silane
		atomTypePatterns.addElement(Pattern.compile("LP.*+"));
		//Lonepair (20)
		atomTypePatterns.addElement(Pattern.compile("H-1;O[+-]?+.[PSCN]{0,2}+/.*+"));
		//H- OH
		atomTypePatterns.addElement(Pattern.compile("C-3;CCC..?+&?+[A-Za-z]?+,?+.?+&?+,?+.?+&?+.*+"));
		//C Cyclopropane
		atomTypePatterns.addElement(Pattern.compile("H-1;[NP][+]?+[(][H]{0,2}+=?+[A-Z]{0,2}+/.*+"));
		//H- NH amine
		atomTypePatterns.addElement(Pattern.compile("H-1;O[+]?+.=?+C/=?+[OCSP]{1,2}+/.*+"));
		//H- COOH
		atomTypePatterns.addElement(Pattern.compile("P-[0-3];[A-Za-z]{1,3}[(].*+"));
		//>P
		atomTypePatterns.addElement(Pattern.compile("B-[0-3];[A-Za-z]{1,2}.*+"));
		//>B
		atomTypePatterns.addElement(Pattern.compile("B-4;[A-Za-z]{1,4}.*+"));
		//>B<
		atomTypePatterns.addElement(Pattern.compile("SPECIAL DEFINITON "));
		//H- Amide/Enol
		atomTypePatterns.addElement(Pattern.compile("NOT Implemented"));
		//C* Carbonradical
		atomTypePatterns.addElement(Pattern.compile("C-[0-9][+];.*+"));
		//C+ (30)
		atomTypePatterns.addElement(Pattern.compile("Ge.*+"));
		//Ge
		atomTypePatterns.addElement(Pattern.compile("Sn.*+"));
		//Sn
		atomTypePatterns.addElement(Pattern.compile("Pb.*+"));
		//Pb
		atomTypePatterns.addElement(Pattern.compile("Se.*+"));
		//Se
		atomTypePatterns.addElement(Pattern.compile("Te.*+"));
		//Te
		atomTypePatterns.addElement(Pattern.compile("D-1;.*+"));
		//D
		atomTypePatterns.addElement(Pattern.compile("N-2;=CC..*+"));
		//-N= azo,Pyridin
		atomTypePatterns.addElement(Pattern.compile("C-2;=CC..?+[A-Za-z]?+,?+&?+,?+C?+&?+.*+"));
		//Csp2 Cyclopropene
		atomTypePatterns.addElement(Pattern.compile("N-4[+]?+;.*+"));
		//nsp3 ammonium
		atomTypePatterns.addElement(Pattern.compile("N-[2-3];H?+CC.[^(=O)].*+"));
		//nsp2pyrrole (40)
		atomTypePatterns.addElement(Pattern.compile("O-2;CC.=C.*+&.*+&.*+"));
		//osp2furan
		atomTypePatterns.addElement(Pattern.compile("S-2;CC.*+"));
		//s sp2 thiophene
		atomTypePatterns.addElement(Pattern.compile("N-[2-3][+]?+;=N.*+C?+O?+[-]?+.*+"));
		//-N=N-O
		atomTypePatterns.addElement(Pattern.compile("H-1;S.*+"));
		//H- S hiol
		atomTypePatterns.addElement(Pattern.compile("N-2[+];=?+%?+[NC][-=]{0,2}+[NC][-]?+.*+"));
		//Azide Center n
		atomTypePatterns.addElement(Pattern.compile("N-3[+];=O[A-Z]-?+[A-Z]-?+.*+"));
		//n no2
		atomTypePatterns.addElement(Pattern.compile("O-1-?+;=?+[CS][(][=0]?+[OCSNH]*+/.*+"));
		//0 carboxylate
		atomTypePatterns.addElement(Pattern.compile("H-1;N[+].[A-Z]{0,3}+/.*+"));
		//h ammonium
		atomTypePatterns.addElement(Pattern.compile("O-2;CC.H?+,?+H?+,?+&,&.*+"));
		//Epoxy
		atomTypePatterns.addElement(Pattern.compile("C-2;=CC.*+"));
		//C Benzene (50)
		atomTypePatterns.addElement(Pattern.compile("He.*+"));
		//He
		atomTypePatterns.addElement(Pattern.compile("Ne.*+"));
		//Ne
		atomTypePatterns.addElement(Pattern.compile("Ar.*+"));
		//Ar
		atomTypePatterns.addElement(Pattern.compile("Kr.*+"));
		//Kr
		atomTypePatterns.addElement(Pattern.compile("Xe.*+"));
		//Xe
		atomTypePatterns.addElement(Pattern.compile("NotImplemented"));
		atomTypePatterns.addElement(Pattern.compile("NotImplemented"));
		atomTypePatterns.addElement(Pattern.compile("NotImplemented"));
		atomTypePatterns.addElement(Pattern.compile("Mg.*+"));
		//Mg
		atomTypePatterns.addElement(Pattern.compile("P-[2-4];.*"));
		//P (60)
		atomTypePatterns.addElement(Pattern.compile("Fe.*+"));
		//Fe 2
		atomTypePatterns.addElement(Pattern.compile("Fe.*+"));
		//Fe 3
		atomTypePatterns.addElement(Pattern.compile("Ni.*+"));
		//Ni 2
		atomTypePatterns.addElement(Pattern.compile("Ni.*+"));
		//Ni 3
		atomTypePatterns.addElement(Pattern.compile("Co.*+"));
		//Co 2
		atomTypePatterns.addElement(Pattern.compile("Co.*+"));
		//Co 3
		atomTypePatterns.addElement(Pattern.compile("NotImplemented"));
		atomTypePatterns.addElement(Pattern.compile("NotImplemented"));
		atomTypePatterns.addElement(Pattern.compile("O-1[-]?+;=?+N.*+"));
		//Amineoxide
		atomTypePatterns.addElement(Pattern.compile("O-3[+];[H]{0,3}+[C]{0,3}+[(].*+"));
		//Ketoniumoxygen (70)
		atomTypePatterns.addElement(Pattern.compile("C-1NotImplemented"));
		//Ketoniumcarbon
		atomTypePatterns.addElement(Pattern.compile("N-2;=C[^CO].*+"));
		//N =N-Imine,Oxime
		atomTypePatterns.addElement(Pattern.compile("N-3[+];[H]{0,2}+=?+[C]{0,3}+[(].*+"));
		//N+ =N+Pyridinium
		atomTypePatterns.addElement(Pattern.compile("N-[2-3][+];=C[CO]{2}+.?+[(].*+"));
		//N+ =N+Imminium
		atomTypePatterns.addElement(Pattern.compile("N-[2-3][+]?+;=CO.*+"));
		//N-0H Oxime
		atomTypePatterns.addElement(Pattern.compile("H-1;N[(]{1}+[CH]{2,2}+/[H]{0,3}+[,]?+=OC.*+"));
		//H- Amide
		atomTypePatterns.addElement(Pattern.compile("H-1;O.C/=CC/.*+"));
		//H- AEnol (77)
		atomTypePatterns.addElement(Pattern.compile("N-[1-3];[CH]{1,3}.{1}+[A-Z]{0,3}+[,]?+=OC.*+"));
		//amid
	}
}

