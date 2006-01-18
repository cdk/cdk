/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.structgen.deterministic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.MFAnalyser;

/**
 * An adapted implementation of Molodtsov structure generator. 
 * The final part is not the original idea, such as
 * normalization, also the author omitted the strong canonicity as it needs other algorithms. 
 * Therefore, it is a mixture
 * of Molodtsov structure generator with other ideas.
 *
 * <p>Details are found in the following papers 
 * {@cdk.cite Molodtsov94, Molchanova96, Hu94, Hu94b, Hu99}.
 *
 * @author      Junfeng Hao
 * @cdk.created 2004-02-16
 */
public class GENMDeterministicGenerator
{
	private int numberOfSetFragment;
	private int numberOfStructure;
	private AtomContainer atomContainer;
	private int[] molecularFormula;
	private int[] numberOfBasicUnit;
	private int[] numberOfBasicFragment;
	private Vector basicFragment;
	private Vector structures;
	private Vector smiles;
	private org.openscience.cdk.tools.LoggingTool logger;
	private PrintWriter structureout;
	
	private static double LOST=0.000000000001;
	
	/**
	 *  Constructor for the GENMDeterministicGenerator object. This constructor is only 
	 *  for molecular formula
	 * @param	mf	molecular formula string
	 * @param   path        Path to the file used for writing structures. Leave blank if current directory should be used.
	 *
	 */
	 public GENMDeterministicGenerator(String mf, String path) throws java.lang.Exception
	{
		numberOfSetFragment=0;
		numberOfStructure=0;
		System.out.println(mf);
		MFAnalyser mfa = new MFAnalyser(mf, new AtomContainer());
		molecularFormula=new int[12];
		numberOfBasicUnit=new int[23];
		numberOfBasicFragment=new int[34];
		basicFragment=new Vector();
		structures=new Vector();
		smiles=new Vector();
		
		logger = new org.openscience.cdk.tools.LoggingTool(this);
		
		structureout=new PrintWriter(new FileWriter(path+"structuredata.txt"),true);
		
		initializeParameters();
		analyseMolecularFormula(mfa);
		generateBasicUnits();
		logger.debug("numberofstructure is="+numberOfStructure);
	}
	
	
	/**
	 * Constructor for GENMDeterministicGenerator object. This constructor could be 
	 * used for a set of basic units.
	 * @param	basicUnits	Vector contain a basic unit set
	 * @param   path        Path to the file used for writing structures. Leave blank if current directory should be used.
	 *
	 */
	public GENMDeterministicGenerator(Vector basicUnits, String path) throws IOException,Exception
	{
		numberOfSetFragment=0;
		numberOfStructure=0;
		numberOfBasicUnit=new int[23];
		numberOfBasicFragment=new int[34];
		basicFragment=new Vector();
		structures=new Vector();
		
		logger = new org.openscience.cdk.tools.LoggingTool(this);
		
		structureout=new PrintWriter(new FileWriter(path+"structuredata.txt"),true);
		
		initializeParameters();
		if(basicUnits!=null)getBasicUnit(basicUnits);
		else
			logger.error("input false");
		generateBasicFragments();
		//System.out.println("numberofstructure is="+numberOfStructure);
	}
	
	
	/**
	 * get basic units from input information.
	 * @param	basicUnits	vector contains basic units which stored as string
	 */
	 public void getBasicUnit(Vector basicUnits)
	 {
		 int i,j;
		 for(i=0;i<basicUnits.size();i++)
		 {
			 String s=(String)basicUnits.get(i);
			 if(s.equals("Si"))numberOfBasicUnit[1]+=1;
			 else if(s.equals("P"))numberOfBasicUnit[2]+=1;
			 else if(s.equals("S"))numberOfBasicUnit[3]+=1;
			 else if(s.equals("N"))numberOfBasicUnit[4]+=1;
			 else if(s.equals("O"))numberOfBasicUnit[5]+=1;
			 else if(s.equals("C"))numberOfBasicUnit[6]+=1;
			 else if(s.equals("SiH3"))numberOfBasicUnit[7]+=1;
			 else if(s.equals("SiH2"))numberOfBasicUnit[8]+=1;
			 else if(s.equals("SiH"))numberOfBasicUnit[9]+=1;
			 else if(s.equals("PH2"))numberOfBasicUnit[10]+=1;
			 else if(s.equals("PH"))numberOfBasicUnit[11]+=1;
			 else if(s.equals("SH"))numberOfBasicUnit[12]+=1;
			 else if(s.equals("NH2"))numberOfBasicUnit[13]+=1;
			 else if(s.equals("NH"))numberOfBasicUnit[14]+=1;
			 else if(s.equals("OH"))numberOfBasicUnit[15]+=1;
			 else if(s.equals("CH3"))numberOfBasicUnit[16]+=1;
			 else if(s.equals("CH2"))numberOfBasicUnit[17]+=1;
			 else if(s.equals("CH"))numberOfBasicUnit[18]+=1;
			 else
				 logger.error("input error");
		 }
	 }
	
	
	/**
	 *  Initialize the basic fragment. For the definition, please see the BasicFragment class.
	 *  
	 */
	public void initializeParameters() throws java.lang.Exception
	{
		
		basicFragment.addElement(new BasicFragment(1,4,1,0,1,">C<","C"));
		basicFragment.addElement(new BasicFragment(2,3,102,0,2,">C=","C"));
		basicFragment.addElement(new BasicFragment(3,2,2,0,3,"=C=","C"));
		basicFragment.addElement(new BasicFragment(4,2,103,0,4,"-C#","C"));
		basicFragment.addElement(new BasicFragment(5,3,1,1,5,">CH-","C"));
		basicFragment.addElement(new BasicFragment(6,2,102,1,6,"=CH-","C"));
		basicFragment.addElement(new BasicFragment(7,1,3,1,6,"CH#","C"));
		basicFragment.addElement(new BasicFragment(8,3,1,0,8,">N-","N"));
		basicFragment.addElement(new BasicFragment(9,2,102,0,9,"=N-","N"));
		basicFragment.addElement(new BasicFragment(10,1,3,0,10,"N#","N"));
		basicFragment.addElement(new BasicFragment(11,2,1,2,11,"-CH2-","C"));
		basicFragment.addElement(new BasicFragment(12,1,2,2,12,"CH2=","C"));
		
		basicFragment.addElement(new BasicFragment(13,2,1,1,13,"-NH-","N"));
		basicFragment.addElement(new BasicFragment(14,1,2,1,14,"NH=","N"));
		basicFragment.addElement(new BasicFragment(15,2,1,0,15,"-O-","O"));
		basicFragment.addElement(new BasicFragment(16,1,2,0,16,"O=","O"));
		basicFragment.addElement(new BasicFragment(17,2,1,0,17,"-S-","S"));
		basicFragment.addElement(new BasicFragment(18,1,2,0,18,"S=","S"));
		basicFragment.addElement(new BasicFragment(19,1,1,3,19,"CH3-","C"));
		basicFragment.addElement(new BasicFragment(20,1,1,2,20,"NH2-","N"));
		basicFragment.addElement(new BasicFragment(21,1,1,1,21,"OH-","O"));
		basicFragment.addElement(new BasicFragment(22,1,1,1,22,"-SH","S"));
		basicFragment.addElement(new BasicFragment(23,3,1,0,23,">P-","P"));
		basicFragment.addElement(new BasicFragment(24,2,1,1,24,"-PH-","P"));
		basicFragment.addElement(new BasicFragment(25,1,1,2,25,"PH2-","P"));
		basicFragment.addElement(new BasicFragment(26,4,1,0,26,">Si<","Si"));
		basicFragment.addElement(new BasicFragment(27,3,1,1,26,">SiH-","Si"));
		basicFragment.addElement(new BasicFragment(28,2,1,2,28,"-SiH2-","Si"));
		basicFragment.addElement(new BasicFragment(29,1,1,3,29,"SiH3-","Si"));
		basicFragment.addElement(new BasicFragment(30,1,1,0,30,"F-","F"));
		basicFragment.addElement(new BasicFragment(31,1,1,0,31,"Cl-","Cl"));
		basicFragment.addElement(new BasicFragment(32,1,1,0,32,"Br-","Br"));
		basicFragment.addElement(new BasicFragment(33,1,1,0,33,"I-","I"));
		
		/*for decompose the complex fragment*/
		
		basicFragment.addElement(new BasicFragment(42,2,1,0,2,">C","C"));
		basicFragment.addElement(new BasicFragment(43,1,2,0,2,"C=","C"));
		
		
		basicFragment.addElement(new BasicFragment(44,1,1,0,4,"C-","C"));
		basicFragment.addElement(new BasicFragment(45,1,3,0,4,"C#","C"));
		
		
		basicFragment.addElement(new BasicFragment(46,1,1,1,6,"CH-","C"));
		basicFragment.addElement(new BasicFragment(47,1,2,1,6,"CH=","C"));
		
		
		basicFragment.addElement(new BasicFragment(50,1,1,0,9,"N-","N"));
		basicFragment.addElement(new BasicFragment(51,1,2,0,9,"N=","N"));
		//Maybe add later
		return;
	}
	
	/**
	 *  Analyse molecular formula to verify it is valid.
	 *  @param mfa	MFAnalyser object to operate the molecular formula
	 *
	 */
	 public void analyseMolecularFormula(MFAnalyser mfa) throws java.lang.Exception
	 {
		 int i,j,k;
	 	 molecularFormula[1]=mfa.getAtomCount("C");
		 molecularFormula[2]=mfa.getAtomCount("H");
		 molecularFormula[3]=mfa.getAtomCount("O");
		 molecularFormula[4]=mfa.getAtomCount("N");
		 molecularFormula[5]=mfa.getAtomCount("S");
		 molecularFormula[6]=mfa.getAtomCount("P");
		 molecularFormula[7]=mfa.getAtomCount("Si");
		 molecularFormula[8]=mfa.getAtomCount("F");
		 molecularFormula[9]=mfa.getAtomCount("Cl");
		 molecularFormula[10]=mfa.getAtomCount("Br");
		 molecularFormula[11]=mfa.getAtomCount("I");
		 
		 molecularFormula[0]=2*molecularFormula[1]+molecularFormula[4]+molecularFormula[6]+
			2*molecularFormula[7]+2-molecularFormula[2]-molecularFormula[8]-molecularFormula[9]-
			molecularFormula[10]-molecularFormula[11];
		 if(molecularFormula[0]<0)
		 {
			logger.debug("Input molecular formula error!");
	 	 }
		 
		// for(i=1;i<=11;i++)
		//	logger.debug("molecularFormula["+i+"]="+molecularFormula[i]);
		 	
		 
		 return;
	 }
	
	/**
	 *  The first step: generate sets of basic units by backtracking algorithm
	 *
	 */
	 public void generateBasicUnits() throws java.lang.Exception
	 {
		 int[] maxNumberOfBasicUnit=new int[23];
		 int i,j,k;
		 int iter1,iter2,iter3,iter4;
		 int numberSi,numberSiH,numberP,numberPH,numberS,numberSH,numberN,numberNH;
		 int numberO,numberOH,numberCH,numberH;
		 int basicUnit;
		 
		 /* Generate the maximum number of basic units based on molecular formula. The corresponding 
		 basic units is Si,P,S,N,O,C,SiH3,SiH2,SiH,PH2,PH,SH,NH2,NH,OH,CH3,CH2,CH,F,Cl,Br,I. We could 
		 get the basic units directly for F,Cl,Br,I as they do not contain hydrogen atoms.*/
		 maxNumberOfBasicUnit[1]=molecularFormula[7];//Si
		 maxNumberOfBasicUnit[2]=molecularFormula[6];//P
		 maxNumberOfBasicUnit[3]=molecularFormula[5];//S
		 maxNumberOfBasicUnit[4]=molecularFormula[4];//N
		 maxNumberOfBasicUnit[5]=molecularFormula[3];//O
		 maxNumberOfBasicUnit[6]=molecularFormula[1];//C
		 maxNumberOfBasicUnit[7]=molecularFormula[1];//SiH3
		 maxNumberOfBasicUnit[8]=molecularFormula[1];//SiH2
		 maxNumberOfBasicUnit[9]=molecularFormula[1];//SiH
		 maxNumberOfBasicUnit[10]=molecularFormula[6];//PH2
		 maxNumberOfBasicUnit[11]=molecularFormula[6];//PH
		 maxNumberOfBasicUnit[12]=molecularFormula[5];//SH
		 maxNumberOfBasicUnit[13]=molecularFormula[4];//NH2
		 maxNumberOfBasicUnit[14]=molecularFormula[4];//NH
		 maxNumberOfBasicUnit[15]=molecularFormula[3];//OH
		 maxNumberOfBasicUnit[16]=molecularFormula[1];//CH3
		 maxNumberOfBasicUnit[17]=molecularFormula[1];//CH2
		 maxNumberOfBasicUnit[18]=molecularFormula[1];//CH
		 
		 /* for CH3, CH2 the number of H should be consider at the same time*/
		 i=molecularFormula[2];
		 j=i/3;
		 k=i/2;
		 if(maxNumberOfBasicUnit[16]>j)maxNumberOfBasicUnit[16]=j;
		 if(maxNumberOfBasicUnit[17]>k)maxNumberOfBasicUnit[17]=k;
		 
		 /* initialization */
		 for(i=1;i<=22;i++)numberOfBasicUnit[i]=0;
		 numberSi=0;
		 numberSiH=0;
		 numberP=0;
		 numberPH=0;
		 numberS=0;
		 numberSH=0;
		 numberN=0;
		 numberNH=0;
		 numberO=0;
		 numberOH=0;
		 numberCH=0;
		 numberH=0;
		 basicUnit=0;
		 
		 /* to distribute the hydrogen into heavy atoms. It is easily to see that only the basic
		  units which contain hydrogens need to be considered. Therefore */
		 iter1=6;
		 iter2=18;
		 do
		 {
			 iter1++;
			 while(iter1<iter2)
			 {
				 numberOfBasicUnit[iter1]=0;
				 switch(iter1-1)
				 {
					 case 9:
					 	numberSi=numberOfBasicUnit[7]+numberOfBasicUnit[8]+numberOfBasicUnit[9];
						numberSiH=numberSi+2*numberOfBasicUnit[7]+numberOfBasicUnit[8];
						break;
					case 11:
						numberP=numberOfBasicUnit[10]+numberOfBasicUnit[11];
						numberPH=numberSiH+numberP+numberOfBasicUnit[10];
						break;
					case 12:
						numberS=numberOfBasicUnit[12];
						numberSH=numberPH+numberS;
						break;
					case 14:
						numberN=numberOfBasicUnit[13]+numberOfBasicUnit[14];
						numberNH=numberS+numberN+numberOfBasicUnit[13];
						break;
					case 15:
						numberO=numberOfBasicUnit[15];
						numberOH=numberNH+numberO;
						break;
				 }
			 	 iter1++;
			 }
			 
			 do
			 {
				 /* begin from CH*/
				 numberCH=numberOH+3*numberOfBasicUnit[16]+2*numberOfBasicUnit[17];
				 numberH=molecularFormula[2]-numberCH;//left number of hydrogen atoms
				 if(numberH>maxNumberOfBasicUnit[18])break;
				 if(numberH<0)break;
				 numberOfBasicUnit[18]=numberH;
				 
				 /* for Si */
				 numberOfBasicUnit[1]=molecularFormula[7]-numberSi;
				 if(numberOfBasicUnit[1]>maxNumberOfBasicUnit[1])break;
				 if(numberOfBasicUnit[1]<0)break;
				 
				 /* for P */
				 numberOfBasicUnit[2]=molecularFormula[6]-numberP;
				 if(numberOfBasicUnit[2]>maxNumberOfBasicUnit[2])break;
				 if(numberOfBasicUnit[2]<0)break;
				 
				 /* for S */
				 numberOfBasicUnit[3]=molecularFormula[5]-numberS;
				 if(numberOfBasicUnit[3]>maxNumberOfBasicUnit[3])break;
				 if(numberOfBasicUnit[3]<0)break;
				 
				 /* for N */
				 numberOfBasicUnit[4]=molecularFormula[4]-numberN;
				 if(numberOfBasicUnit[4]>maxNumberOfBasicUnit[4])break;
				 if(numberOfBasicUnit[4]<0)break;
				 
				 /* for O */
				 numberOfBasicUnit[5]=molecularFormula[3]-numberO;
				 if(numberOfBasicUnit[5]>maxNumberOfBasicUnit[5])break;
				 if(numberOfBasicUnit[5]<0)break;
				 
				 /* for C */
				 numberOfBasicUnit[6]=molecularFormula[1]-numberOfBasicUnit[16]-numberOfBasicUnit[17]-numberOfBasicUnit[18];
				 if(numberOfBasicUnit[6]>maxNumberOfBasicUnit[6])break;
				 if(numberOfBasicUnit[6]<0)break;
				
				 
				 basicUnit+=1;
				 numberOfBasicUnit[0]=basicUnit;
				 /* for F,Cl,Br,I */
				 for(i=19;i<=22;i++)
					 if(molecularFormula[i-11]!=0)numberOfBasicUnit[i]=molecularFormula[i-11];
				 for(i=0;i<=22;i++)
				 {
					 if(numberOfBasicUnit[i]!=0)
					 {
						// logger.debug("numberOfBasicUnit["+i+"]="+numberOfBasicUnit[i]);
						//System.out.println("numberOfBasicUnit["+i+"]="+numberOfBasicUnit[i]);
					 }
				 }
				 
				 /* If all the following judgement are satisfactory, it should be one set of basic unit.Output them*/
				 generateBasicFragments();
			 }while(false);
			 
			 /* exit from the distribution hydrogen, backtracking*/
			 do
			 {
				 iter1-=1;
				 if(iter1==6)return;
				 if(numberOfBasicUnit[iter1]>=maxNumberOfBasicUnit[iter1])continue;
				 
				 do
				 {
					 numberOfBasicUnit[iter1]+=1;
					 if(iter1==7)//SiH3
					 {
						 iter3=numberOfBasicUnit[7];
						 if(iter3>molecularFormula[7])break;
					 }
					 else if(iter1==8)//SiH2
					 {
						 iter3=numberOfBasicUnit[7]+numberOfBasicUnit[8];
						 if(iter3>molecularFormula[7])break;
					 }
					 else if(iter1==9)//SiH2
					 {
						 iter3=numberOfBasicUnit[7]+numberOfBasicUnit[8]+numberOfBasicUnit[9];
						 if(iter3>molecularFormula[7])break;
					 }
					 else if(iter1==10)//PH2
					 {
						 iter3=numberOfBasicUnit[10];
						 if(iter3>molecularFormula[6])break;
					 }
					 else if(iter1==11)//PH
					 {
						 iter3=numberOfBasicUnit[10]+numberOfBasicUnit[11];
						 if(iter3>molecularFormula[6])break;
					 }
					 else if(iter1==13)//NH2
					 {
						 iter3=numberOfBasicUnit[13];
						 if(iter3>molecularFormula[4])break;
					 }
					 else if(iter1==14)//NH
					 {
						 iter3=numberOfBasicUnit[13]+numberOfBasicUnit[14];
						 if(iter3>molecularFormula[4])break;
					 }
					 else if(iter1==16)//CH3
					 {
						 iter3=numberOfBasicUnit[16];
						 if(iter3>molecularFormula[1])break;
						 iter4=numberOH+3*numberOfBasicUnit[16];
						 if(iter4>molecularFormula[2])break;
						 i=molecularFormula[2]-iter4;
						 j=molecularFormula[1]-iter3;
						 k=2*j;
						 if(i>k)continue;
						 else break;
					 }
					 else if(iter1==17)//CH2
					 {
						 iter3=numberOfBasicUnit[16]+numberOfBasicUnit[17];
						 if(iter3>molecularFormula[1])break;
						 iter4=numberOH+3*numberOfBasicUnit[16]+2*numberOfBasicUnit[17];
						 if(iter4>molecularFormula[2])break;
						 i=molecularFormula[2]-iter4;
						 j=molecularFormula[1]-iter3;
						 if(i>j)continue;
						 else break;
					 }
					 else
					 {
						 break;
					 }
					 break;
				 }while(true);
				 break;
			 }while(true);
			 
		 }while(iter1>6);
		 return;
	 }
	 
	 
	/**
	 *  Second step: generate basic fragments, the generating rules from basic units are the following:
	 *  for C: >C<, >C=,=C=,-C#(triple bond), 1~4
	 *  CH: >CH-,=CH-,CH#, 5~7
	 *  CH2: -CH2-,CH2=, 11~12
	 *  CH3: CH3-,19

	 *  for N: >N-,-N=,N#,8~10
	 *  NH: -NH-, NH= ,13~14
	 *  NH2: NH2-,20

	 *  for O: -O-,O=, 15~16
	 *  OH: -OH, 21

	 *  for S: -S-,S=, 17~18
	 *  SH, -SH,22

	 *  for P: >P-, 23
	 *  PH: -PH-,24
	 *  PH2: -PH2,25

	 *  for Si: >Si< 26
	 *  SiH: >SiH- 27
	 *  SiH2: -SiH2- 28
	 *  SiH3: SiH3- 29

	 *  for F:F- 30
	 *  for Cl: Cl- 31
	 *  for Br: Br- 32
	 *  for I: I- 33
	 *  It could be easily add the new fragments, such as valence, variable valence, such as N,P,also, fragments contains
	 *  charges.
	 * it is a combinatorial algorithm n1*n2*n3...
	 */
	 public void generateBasicFragments() throws java.lang.Exception
	 {
		 int i,j,k;
		 int iter1,iter2;
		 boolean flag;
		 int[] maxNumberOfBasicFragment=new int[34];
		 /*initialization for the variables*/
		 for(i=0;i<=22;i++)
			 if(numberOfBasicUnit[i]!=0)logger.debug("numberOfBasicUnit["+i+"]="+numberOfBasicUnit[i]);
		 for(i=1;i<34;i++)numberOfBasicFragment[i]=0;
		 
		 /* maximum number of basic fragments*/
		 maxNumberOfBasicFragment[1]=numberOfBasicUnit[6];//>C<
		 maxNumberOfBasicFragment[2]=numberOfBasicUnit[6];//>C=
		 maxNumberOfBasicFragment[3]=numberOfBasicUnit[6];//=C=
		 maxNumberOfBasicFragment[4]=numberOfBasicUnit[6];//-C#
		 
		 maxNumberOfBasicFragment[5]=numberOfBasicUnit[18];//>CH-
		 maxNumberOfBasicFragment[6]=numberOfBasicUnit[18];//=CH-
		 maxNumberOfBasicFragment[7]=numberOfBasicUnit[18];//CH#
		 
		 maxNumberOfBasicFragment[8]=numberOfBasicUnit[4];//>N-
		 maxNumberOfBasicFragment[9]=numberOfBasicUnit[4];//-N=
		 maxNumberOfBasicFragment[10]=numberOfBasicUnit[4];//N#
		 
		 maxNumberOfBasicFragment[11]=numberOfBasicUnit[17];//-CH2-
		 maxNumberOfBasicFragment[12]=numberOfBasicUnit[17];//CH2=
		 
		 maxNumberOfBasicFragment[13]=numberOfBasicUnit[14];//-NH-
		 maxNumberOfBasicFragment[14]=numberOfBasicUnit[14];//NH=
		 
		 maxNumberOfBasicFragment[15]=numberOfBasicUnit[5];//-O-
		 maxNumberOfBasicFragment[16]=numberOfBasicUnit[5];//O=
		 
		 maxNumberOfBasicFragment[17]=numberOfBasicUnit[3];//-S-
		 maxNumberOfBasicFragment[18]=numberOfBasicUnit[3];//S=
		 
		 numberOfBasicFragment[19]=numberOfBasicUnit[16];
		 numberOfBasicFragment[20]=numberOfBasicUnit[13];
		 numberOfBasicFragment[21]=numberOfBasicUnit[15];
		 numberOfBasicFragment[22]=numberOfBasicUnit[12];
		 numberOfBasicFragment[23]=numberOfBasicUnit[2];
		 numberOfBasicFragment[24]=numberOfBasicUnit[11];
		 numberOfBasicFragment[25]=numberOfBasicUnit[10];
		 numberOfBasicFragment[26]=numberOfBasicUnit[1];
		 numberOfBasicFragment[27]=numberOfBasicUnit[9];
		 numberOfBasicFragment[28]=numberOfBasicUnit[8];
		 numberOfBasicFragment[29]=numberOfBasicUnit[7];
		 numberOfBasicFragment[30]=numberOfBasicUnit[19];
		 numberOfBasicFragment[31]=numberOfBasicUnit[20];
		 numberOfBasicFragment[32]=numberOfBasicUnit[21];
		 numberOfBasicFragment[33]=numberOfBasicUnit[22];
		 
		 iter1=17;
		 iter2=0;
		 while(iter1>0)
		 {
			 iter1+=1;
			 
			 while(iter1<18)
			 {
				numberOfBasicFragment[iter1]=0;
				iter1+=1;
			 }
			
			 do
			 {
				 numberOfBasicFragment[18]=numberOfBasicUnit[3]-numberOfBasicFragment[17];
				 if(numberOfBasicFragment[18]<0)break;
				 numberOfBasicFragment[16]=numberOfBasicUnit[5]-numberOfBasicFragment[15];
				 if(numberOfBasicFragment[16]<0)break;
				 numberOfBasicFragment[14]=numberOfBasicUnit[14]-numberOfBasicFragment[13];
				 if(numberOfBasicFragment[14]<0)break;
				 numberOfBasicFragment[12]=numberOfBasicUnit[17]-numberOfBasicFragment[11];
				 if(numberOfBasicFragment[12]<0)break;
				 numberOfBasicFragment[10]=numberOfBasicUnit[4]-numberOfBasicFragment[8]-numberOfBasicFragment[9];
				 if(numberOfBasicFragment[10]<0)break;
				
				 numberOfBasicFragment[7]=numberOfBasicUnit[18]-numberOfBasicFragment[5]-numberOfBasicFragment[6];
				 if(numberOfBasicFragment[7]<0)break;
				 numberOfBasicFragment[4]=numberOfBasicUnit[6]-numberOfBasicFragment[1]-numberOfBasicFragment[2]-numberOfBasicFragment[3];
				 if(numberOfBasicFragment[4]<0)break;
				 
				 flag=testBasicFragment();
				 if(flag)
				 {
					 numberOfSetFragment+=1;
					 logger.debug("Fragment Set	"+numberOfSetFragment);
					 for(i=1;i<34;i++)
					 {
						 if(numberOfBasicFragment[i]!=0)logger.debug(((BasicFragment)(basicFragment.get(i-1))).getBasicFragment()+"		"+numberOfBasicFragment[i]);
					 }
					generateIsomers();
				}
			  
			 }while(false);
			 
			 do
			 {
				 iter1=iter1-1;
				 if(iter1==0)return;
				 
				 if(numberOfBasicFragment[iter1]>=maxNumberOfBasicFragment[iter1])continue;
				 numberOfBasicFragment[iter1]=numberOfBasicFragment[iter1]+1;
				
				 if(iter1==2)
				 {
					 k=numberOfBasicFragment[1]+numberOfBasicFragment[2];
					 if(k>numberOfBasicUnit[6])continue;
					 else break;
				 }
				 if(iter1==3)
				 {
					 k=numberOfBasicFragment[1]+numberOfBasicFragment[2]+numberOfBasicFragment[3];
					 if(k>numberOfBasicUnit[6])continue;
					 else break;
				 }
				 if(iter1==4)
				 {
					 k=numberOfBasicFragment[1]+numberOfBasicFragment[2]+numberOfBasicFragment[3]+numberOfBasicFragment[4];
					 if(k>numberOfBasicUnit[6])continue;
					 else break;
				 }
				 if(iter1==6)
				 {
					 k=numberOfBasicFragment[5]+numberOfBasicFragment[6];
					 if(k>numberOfBasicUnit[18])continue;
					 else break;
				 }
				 if(iter1==7)
				 {
					 k=numberOfBasicFragment[5]+numberOfBasicFragment[6]+numberOfBasicFragment[7];
					 if(k>numberOfBasicUnit[18])continue;
					 else break;
				 }
				 
				 if(iter1==9)
				 {
					 k=numberOfBasicFragment[8]+numberOfBasicFragment[9];
					 if(k>numberOfBasicUnit[4])continue;
					 else break;
				 }
				 if(iter1==10)
				 {
					 k=numberOfBasicFragment[8]+numberOfBasicFragment[9]+numberOfBasicFragment[10];
					 if(k>numberOfBasicUnit[4])continue;
					 else break;
				 }
				 if(iter1==12)
				 {
					 k=numberOfBasicFragment[11]+numberOfBasicFragment[12];
					 if(k>numberOfBasicUnit[17])continue;
					 else break;
				 }
				 if(iter1==14)
				 {
					 k=numberOfBasicFragment[13]+numberOfBasicFragment[14];
					 if(k>numberOfBasicUnit[14])continue;
					 else break;
				 }
				 if(iter1==16)
				 {
					 k=numberOfBasicFragment[15]+numberOfBasicFragment[16];
					 if(k>numberOfBasicUnit[5])continue;
					 else break;
				 }
				 if(iter1==18)
				 {
					 k=numberOfBasicFragment[17]+numberOfBasicFragment[18];
					 if(k>numberOfBasicUnit[3])continue;
					 else break;
				 }
				 break;
			 }while(true);
		 }
	 }
	 
	 
	 
	 /**
	 *  Test for the set of basic fragment.
	 *
	 */
	 public boolean testBasicFragment()
	 {
		 int i,j,k;
		 int numberOfFragment,numberOfOneFreeValence,numberOfTwoFreeValence;
		 int numberOfThreeFreeValence,numberOfFourFreeValence;
		 int numberOfSideDoubleBond,numberOfMiddleDoubleBond;
		 int totalFreeValence,totalDoubleBond,totalTripleBond;
		 int numberOfSideTripleBond,numberOfMiddleTripleBond;
		 boolean flag=true;
		 
		 /* initialization the variable*/
		 numberOfFragment=0;
		 numberOfOneFreeValence=0;
		 numberOfTwoFreeValence=0;
		 numberOfThreeFreeValence=0;
		 numberOfFourFreeValence=0;
		 numberOfSideDoubleBond=0;
		 numberOfMiddleDoubleBond=0;
		 numberOfSideTripleBond=0;
		 numberOfMiddleTripleBond=0;
		 totalTripleBond=0;
		 totalFreeValence=0;
		 
		 for(i=1;i<34;i++)
		 {
			 numberOfFragment+=numberOfBasicFragment[i];
			 j=((BasicFragment)(basicFragment.get(i-1))).getNumberOfFreeValence();
			 switch(j)
			 {
				 case 1: numberOfOneFreeValence+=numberOfBasicFragment[i];break;
				 case 2: numberOfTwoFreeValence+=numberOfBasicFragment[i];break;
				 case 3: numberOfThreeFreeValence+=numberOfBasicFragment[i];break;
				 case 4: numberOfFourFreeValence+=numberOfBasicFragment[i];break;
			 }
			 
		 }
		 /* 1. total free valence test*/
		 totalFreeValence=numberOfOneFreeValence+numberOfTwoFreeValence*2+numberOfThreeFreeValence*3+numberOfFourFreeValence*4;
		 if((totalFreeValence%2)!=0)return false;
		 
		 totalFreeValence/=2;
		 if(numberOfFragment>(totalFreeValence+1))return false;
		 
		 /*2. double bond test: */
		 numberOfSideDoubleBond=numberOfBasicFragment[12]+numberOfBasicFragment[14]+numberOfBasicFragment[16]+numberOfBasicFragment[18];
		 numberOfMiddleDoubleBond=numberOfBasicFragment[2]+numberOfBasicFragment[6]+numberOfBasicFragment[9];
		 if(numberOfBasicFragment[3]==0)
		 {
			 if(numberOfSideDoubleBond>numberOfMiddleDoubleBond && numberOfMiddleDoubleBond>0)return false;
			 totalDoubleBond=numberOfSideDoubleBond+numberOfMiddleDoubleBond;
			 if((totalDoubleBond%2)!=0)return false;
			 if((numberOfMiddleDoubleBond==0)&& (numberOfSideDoubleBond==2)&&(numberOfFragment>2))return false;
		 }
		 if(numberOfBasicFragment[3]>0)
		 {
			 if((numberOfMiddleDoubleBond==0)&&(numberOfBasicFragment[3]<(numberOfFragment-2)))return false;
			 if((numberOfMiddleDoubleBond+numberOfSideDoubleBond==0)&&(numberOfBasicFragment[3]<numberOfFragment))return false;
		 }
		 
		 /*triple bond test*/
		 
		 numberOfSideTripleBond=numberOfBasicFragment[7]+numberOfBasicFragment[10];
		 numberOfMiddleTripleBond=numberOfBasicFragment[4];
		 totalTripleBond=numberOfSideTripleBond+numberOfMiddleTripleBond;
		 
		 if((totalTripleBond%2)!=0)return false;
		 if(numberOfMiddleTripleBond==0 && numberOfSideTripleBond!=0)return false;
		 return true;
	 }
	 
	 
	/**
	 *  The thrid step: generate all the possible consititional isomers
	 *
	 */
	 public void generateIsomers()
	 {
		 int i,j,iter,rows;
		 int step,firstUnfilledRow,equivalentClass;
		 int[] totalNumberOfTheSet=new int[1];
		 int totalNumberOfAtomAndBond=0;
		 totalNumberOfTheSet[0]=0;
		 
		 boolean flag,nextWCF,isForced,isPossibleFilling;
		 int[][] adjacencyMatrix;
		 int[][] previousMatrix;
		 int[] rowMatrix;
		 int[] setOfStability;
		 int[] category;
		 int[] bondAttribute;
		 int[] parentID;
		 int[] storedSymbolOfStructure=new int[4000000];
		 Vector setOfBasicFragment=new Vector();
		 AtomContainer atomContainer=null;
		 
		 /*1. prepare the vector of basic fragment and atomContainer
		 * atomContainer put the bond formed, while set of basic fragment
		 * put the source basic fragment.
		 */
		 
		 
		 for(i=1;i<34;i++)
			 if(numberOfBasicFragment[i]!=0)
			 {
				 for(j=1;j<=numberOfBasicFragment[i];j++)
					 totalNumberOfAtomAndBond+=((BasicFragment)(basicFragment.get(i-1))).getNumberOfFreeValence();
				 
			 }
		 totalNumberOfAtomAndBond/=2;
		 
		 
		 for(i=1;i<34;i++)
			 if(numberOfBasicFragment[i]!=0)
			 {
				 for(j=1;j<=numberOfBasicFragment[i];j++)
				 {
					totalNumberOfAtomAndBond+=1;
				 }
				 
				 // consider the complex fragment
				 switch(i)
				 {
					 case 2:
					 	for(j=1;j<=numberOfBasicFragment[i];j++)
						{
							setOfBasicFragment.addElement((BasicFragment)(basicFragment.get(33)));
							setOfBasicFragment.addElement((BasicFragment)(basicFragment.get(34)));
						}
						
						break;
					 case 4:
					 	for(j=1;j<=numberOfBasicFragment[i];j++)
						{
							setOfBasicFragment.addElement((BasicFragment)(basicFragment.get(35)));
							setOfBasicFragment.addElement((BasicFragment)(basicFragment.get(36)));
						}
						break;
					case 6:
					 	for(j=1;j<=numberOfBasicFragment[i];j++)
						{
							setOfBasicFragment.addElement((BasicFragment)(basicFragment.get(37)));
							setOfBasicFragment.addElement((BasicFragment)(basicFragment.get(38)));
						}
						break;
					 case 9:
					 	for(j=1;j<=numberOfBasicFragment[i];j++)
						{
							setOfBasicFragment.addElement((BasicFragment)(basicFragment.get(39)));
							setOfBasicFragment.addElement((BasicFragment)(basicFragment.get(40)));
						}
						break;
					default:
						for(j=1;j<=numberOfBasicFragment[i];j++)
							setOfBasicFragment.addElement((BasicFragment)(basicFragment.get(i-1)));
						break;
				 }
				 
			 }
			 
		//order the fragments
		atomContainer=new org.openscience.cdk.AtomContainer();
		
		parentID=new int[setOfBasicFragment.size()];
		
		/* sort the fragment set according to the free valence*/
		setOfBasicFragment=getOrderOfBasicFragmentSet(setOfBasicFragment,parentID);
		
		rows=setOfBasicFragment.size();
		adjacencyMatrix=new int[rows][rows];
		previousMatrix=new int[rows][rows];
		rowMatrix=new int[rows];
		setOfStability=new int[rows];
		bondAttribute=new int[rows];
		
		for(i=0;i<rows;i++)
		{
			bondAttribute[i]=((BasicFragment)(setOfBasicFragment.get(i))).getAttribute();
		}
		
		for(i=0;i<setOfBasicFragment.size();i++)
		{
			atomContainer.addAtom(new Atom(((BasicFragment)(setOfBasicFragment.get(i))).getHeavyAtomSymbol()));
		}
		
		org.openscience.cdk.interfaces.IAtom[] atom=atomContainer.getAtoms();
		
		for(i=0;i<atom.length;i++)
		{
			atom[i].setHydrogenCount(((BasicFragment)(setOfBasicFragment.get(i))).getNumberOfHydrogen());
		}
		
		 //2. initialize the matrix
		initializeMatrix(setOfBasicFragment,bondAttribute,adjacencyMatrix,previousMatrix);
		// Initial equivalent
		equivalentClass=getEquivalentClass(setOfBasicFragment,setOfStability);
		 //3. begin, see the flowchart 
		step=0;
		iter=0;
		
		 //Maximum WCF(M)
		isPossibleFilling=getMaximumWCF(setOfBasicFragment,0,rowMatrix,adjacencyMatrix,previousMatrix,parentID);
		if(!isPossibleFilling)return;
		while(step<(rows-1))
		{
			// Force filling the left lines
			step+=1;
			firstUnfilledRow=forceFilling(step,setOfBasicFragment,adjacencyMatrix,previousMatrix);
			do
			{
				// Check admissibility
				flag=checkAdmissibility(step,setOfBasicFragment,adjacencyMatrix);
				if(!flag)break;
				
				// constraint checker
				flag=checkConstraint(firstUnfilledRow,setOfBasicFragment,adjacencyMatrix,atomContainer);
				if(!flag)break;
			}while(false);
			
			if(flag)
			{
				if(firstUnfilledRow<rows)
				{
					step=firstUnfilledRow;
					isPossibleFilling=getMaximumWCF(setOfBasicFragment,step,rowMatrix,adjacencyMatrix,previousMatrix,parentID);
					if(!isPossibleFilling)return;
					continue;
				}
				else
				{
					getFinalStructure(setOfBasicFragment,adjacencyMatrix,storedSymbolOfStructure,totalNumberOfTheSet,totalNumberOfAtomAndBond);
					step-=1;
				}
			}
			else step-=1;
			
			// judge whether the line is forcefilling or not
			isForced=isForceFilling(step,previousMatrix,adjacencyMatrix);
			while(isForced)
			{
				step-=1;
				isForced=isForceFilling(step,previousMatrix,adjacencyMatrix);
			}
			
			//get rowMatrix
			for(i=0;i<rowMatrix.length;i++)rowMatrix[i]=adjacencyMatrix[step][i];
			// backtracking: restore the matrix to previous step
			restoreMatrix(step,bondAttribute,previousMatrix,adjacencyMatrix);
			nextWCF=getNextWCF(setOfBasicFragment,step,rowMatrix,adjacencyMatrix,previousMatrix,parentID);
			
			while(!nextWCF)
			{
				step-=1;
				if(step<0)return;
				else	
				{
					isForced=isForceFilling(step,previousMatrix,adjacencyMatrix);
					while(isForced)
					{
						step-=1;
						isForced=isForceFilling(step,previousMatrix,adjacencyMatrix);
					}
					for(i=0;i<rowMatrix.length;i++)
					{
						rowMatrix[i]=adjacencyMatrix[step][i];
					}
					restoreMatrix(step,bondAttribute,previousMatrix,adjacencyMatrix);
					nextWCF=getNextWCF(setOfBasicFragment,step,rowMatrix,adjacencyMatrix,previousMatrix,parentID);
					if(nextWCF)break;
				}
			}
		 }
		 return;
	 }
	 
	/**
	 * Initialized the adjacency matrix.
	 * @param	setOfBasicFragment	set of basic fragment
	 * @param	bondAttribute		to distinguish the bond
	 * @param	adjacency		adjacency matrix
	 * @param	previousMatrix		tracing the change of adjacency matrix
	 */
	 public void initializeMatrix(Vector setOfBasicFragment, int[] bondAttribute,int[][] adjacency,int[][] previousMatrix)
	 {
		int i,j,row,sum;
		row=setOfBasicFragment.size();
		
		int[] valenceOfFragment=new int[row];
		
		
		for(i=0;i<adjacency.length;i++)
			for(j=0;j<adjacency.length;j++)
				adjacency[i][j]=-1;
		for(i=0;i<adjacency.length;i++)
		{
				adjacency[i][i]=0;
				valenceOfFragment[i]=0;
		}
		
		
		/* if there are two basic fragments which is 1 free valence, appraently it is impossible to 
		* be bonded between these two fragments.
		*/
		sum=0;
		if(row>2)
		{
			for(i=0;i<row;i++)
			{
				if(((BasicFragment)(setOfBasicFragment.get(i))).getNumberOfFreeValence()==1
					&& ((BasicFragment)(setOfBasicFragment.get(i))).getID()<=33)valenceOfFragment[i]=1;
				sum+=valenceOfFragment[i];
			}
			if(sum>1)
				for(i=0;i<row-1;i++)
					for(j=i+1;j<row;j++)
					{
						if(valenceOfFragment[i]==1 &&valenceOfFragment[j]==1)
						{
							adjacency[i][j]=0;
							adjacency[j][i]=0;
						}
					}
		}
		// The following loop is to give the zero element for bond unmatch. 28-11-2003
		for(i=0;i<row-1;i++)
			for(j=i;j<row;j++)
				if(bondAttribute[i]!=bondAttribute[j])
				{
					adjacency[i][j]=0;
					adjacency[j][i]=0;
				}
		for(i=0;i<adjacency.length;i++)
		{
			for(j=0;j<adjacency.length;j++)
			{
				previousMatrix[i][j]=adjacency[i][j];
			}
		}
		return;	 
	 }
	 
	 
	 /**
	  * restor the adjacency matrix to previous step
	  * @param	step		the line is filling
	  * @param	freeValence	array for free valence of each node left
	  * @param	sourceMatrix	the reference matrix 
	  * @param	destMatrix	the matrix need to backtrack
	  */
	  public void restoreMatrix(int step,int[] freeValence,int[][] sourceMatrix,int[][] destMatrix)
	  {
		  int i,j;
		  for(i=step;i<sourceMatrix.length;i++)
		  {
			  if(sourceMatrix[i][i]>step)
			  {
				  destMatrix[i][i]=0;sourceMatrix[i][i]=0;
			  }
			 
			  for(j=0;j<sourceMatrix.length;j++)
			  {
				  if(sourceMatrix[i][j]>step)
				  {
					  if(i!=j)
					 {
							 destMatrix[i][j]=-1;sourceMatrix[i][j]=-1;
					 }
				  }
			  }
		  }
		  return;
	  }
	  
	 /**
	 * Maximum weakly canonical filling of line M.
	 * @param	setOfBasicFragment	set of basic fragment
	 * @param	step 			the line is filling
	 * @param	rowMatrix		the row which contains the filling line
	 * @param	adjacency		adjacency matrix
	 * @param	previousMatrix		matrix is used for tracing the change of adjacency matrix.
	 * @param	parentID		mainly used for unsaturated part
	 */
	 public boolean getMaximumWCF(Vector setOfBasicFragment,int step,int[] rowMatrix, int[][] adjacency,int[][] previousMatrix,int[] parentID)
	 {
		 int i,j,iter,bondOrder,size;
		 int totalBond,existBond,leftBond;
		 int[] setOfStability;
		 setOfStability=new int[rowMatrix.length];
		 
		 /*Get the row matrix*/
		 size=setOfBasicFragment.size();
		 iter=0;
		 for(i=0;i<adjacency.length;i++)
		 {
			 rowMatrix[i]=adjacency[step][i];
			 if(rowMatrix[i]==-1)iter+=1;
		 }
		 getSetOfStability(setOfBasicFragment,step,rowMatrix,adjacency,setOfStability,parentID);
		 existBond=0;
		 for(i=0;i<adjacency.length;i++)if(rowMatrix[i]>0)existBond+=rowMatrix[i];
		 totalBond=((BasicFragment)(setOfBasicFragment.get(step))).getNumberOfFreeValence();
		 bondOrder=((BasicFragment)(setOfBasicFragment.get(step))).getAttribute();
		 leftBond=totalBond-existBond;
		 if(leftBond>iter)return false;
		
		 /* maximum this line*/
		 iter=leftBond;
		 for(i=1;i<rowMatrix.length;i++)
		 {
			 if(setOfStability[i]!=0)
			 {
				if(iter>0){rowMatrix[i]=bondOrder;iter-=1;}
				 else rowMatrix[i]=0;
			 }
		 }
		 /* A(i,j)=A(j,i)*/
		 for(i=0;i<adjacency.length;i++)
		 {
			 adjacency[step][i]=rowMatrix[i];
			 if(previousMatrix[step][i]==-1)previousMatrix[step][i]=step+1;
			 
			 adjacency[i][step]=rowMatrix[i];
			 if(previousMatrix[i][step]==-1)previousMatrix[i][step]=step+1;
		 }
		 
		 //for unsaturated part
		 int decomposedNumber=0;
		 int number1=0;
		 int number2=0;
		 if(parentID[step]==-1)return true;
		 else
		 {
			
			 for(i=0;i<size;i++)
				 if(parentID[i]!=-1)decomposedNumber+=1;
			 for(i=0;i<adjacency.length;i++)
				 if(rowMatrix[i]>0 && parentID[i]!=-1)
				 {
					 
					  number1=parentID[step];
					  number2=parentID[i];
					  if((adjacency[step][i]>0 || adjacency[i][step]>0) && adjacency[number1][number2]==-1)
					  {
						adjacency[number1][number2]=0;
						adjacency[number2][number1]=0;
						 if(previousMatrix[number1][number2]==-1)
							 previousMatrix[number1][number2]=step+1;
					 	if(previousMatrix[number2][number1]==-1)
							previousMatrix[number2][number1]=step+1;
					  }
				 }
		 }
		 return true;
	 }
	 
	 
	 /**
	 * get the partition for the left fragment at every step.
	 * @param	setOfBasicFragment	set of basic fragment
	 * @param	step			the line is filling
	 * @param	rowMatrix		the row which contains the filling line
	 * @param	adjacency		adjacency matrix
	 * @param	setOfStability		the previous equivalent partition
	 * @param	parentID		mainly used for unsaturated part
	 */
	 public  void getSetOfStability(Vector setOfBasicFragment,int step,int[] rowMatrix, int[][] adjacency,int[] setOfStability,int[] parentID)
	 {
		 int i,j,count,line;
		 int temp,size,number;
		 size=rowMatrix.length;
		 boolean[] isAdded=new boolean[size];
		 int[] category=new int[size+1];
		 int[] equivalentClass=new int[size];
		 
		 line=0;
		 for(i=0;i<adjacency.length;i++)setOfStability[i]=0;
		 if(step==0)
		 {
			for(i=1;i<setOfBasicFragment.size();i++)
			{
				if(rowMatrix[i]==-1)
				{
					setOfStability[i]=((BasicFragment)(setOfBasicFragment.get(i))).getID();
				}
			}
			
		 }
		 else
		 {
			for(i=step+1;i<setOfBasicFragment.size();i++)
			{
				if(rowMatrix[i]==-1)
				{
					setOfStability[i]=((BasicFragment)(setOfBasicFragment.get(i))).getID();
				}
			}
			
			/* judge temporatory equivalent at this line. */
			for(i=step+1;i<setOfStability.length-1;i++)
				for(j=i+1;j<setOfStability.length;j++)
				{
					if(rowMatrix[i]==-1 && rowMatrix[j]==-1 && setOfStability[i]==setOfStability[j])
					{
						for(line=0;line<step;line++)
						{
							if(adjacency[line][i]==adjacency[line][j])continue;
							else break;
						}
						if(line<step)setOfStability[j]+=100;//
					}
				}
		 }
		 
		 temp=0;
		 for(i=0;i<parentID.length;i++)
		 {
			 if(parentID[i]!=-1)temp+=1;
		 }
		 if(temp>4)
		 {
			 int[] fragmentID=new int[setOfBasicFragment.size()];
			 for(i=0;i<setOfBasicFragment.size();i++)
				 fragmentID[i]=((BasicFragment)(setOfBasicFragment.get(i))).getID();
			 for(i=step+1;i<setOfBasicFragment.size()-1;i++)
				 for(j=i+1;j<setOfBasicFragment.size();j++)
				 {
					 int temp1=parentID[i];
					 int temp2=parentID[j];
					 if(rowMatrix[i]==-1 && rowMatrix[j]==-1)
					 {
						 if(fragmentID[i]>33 && fragmentID[j]>33
							 && rowMatrix[temp1]!=-1 && rowMatrix[temp2]!=-1 
						 	 && setOfStability[i]==setOfStability[j])
						 	setOfStability[j]+=100;
					 }
				 }
		 }
		 /* get the category for this line*/
		 count=1;
		 for(i=step+1;i<setOfStability.length;i++)if(rowMatrix[i]==-1){line=i;break;}
		 category[1]=setOfStability[line];
		 for(i=step+1;i<setOfStability.length;i++)
		 {
			 if(setOfStability[i]!=0)
			 {
				 for(j=1;j<=count;j++)
				 {
					 if(rowMatrix[i]==-1)
					 {
						 temp=setOfStability[i]-category[j];
						 if(temp==0)break;
					 }
					 else continue;
				 }
				 if(j>count)
				 {
					 count+=1;
					 category[count]=setOfStability[i];
				 }
			 }
		 }
		 for(i=1;i<setOfStability.length;i++)
			for(j=1;j<=count;j++)
			{
				if(rowMatrix[i]==-1)
				{
					temp=setOfStability[i]-category[j];
					if(temp==0)equivalentClass[i]=j;
				}
			}
		 equivalentClass[0]=count;
		 
		 for(i=0;i<setOfStability.length;i++)
		 {
			 setOfStability[i]=equivalentClass[i];
		 }
	 }
	 
	 /**
	 * force filling the left matrix at every step.
	 * @param	step			the line is filling
	 * @param 	setOfBasicFragment	vector contains the set of basic fragment.
	 * @param 	adjacencyMatrix		adjacency matrix of the given basic fragment.
	 * @param	previousMatrix		matrix is used for tracing the change of adjacency matrix.
	 * @return	the first line which is unfilled
	 */
	 public int forceFilling(int step,Vector setOfBasicFragment,int[][] adjacencyMatrix,int[][] previousMatrix)
	 {
		 /* 1.  Minimal forcing. The sum of filled elements of each unfilled row i->J(B)
		  * 	is equal to the valence of the corresponding vertex.
		  * 2.  Maximal forcing. The sum of all maximal multiplicity of edges of the Vi-th vertex, i->J(B)
		  *	is equal to the valence of the vertex.
		  */
		  int i,j,iter,sum,bondOrder;
		  int filledValence,unfilledValence,changedLine;
		
		  do
		  {
			  changedLine=0;
			  for(i=step;i<adjacencyMatrix.length;i++)
			  {
				  sum=0;
				  unfilledValence=0;
				  if(previousMatrix[i][i]>0)continue;
				  for(j=0;j<adjacencyMatrix.length;j++)
				  {
					  if(adjacencyMatrix[i][j]>0)sum+=1;
					  else if(adjacencyMatrix[i][j]==-1)unfilledValence+=1;
				  }
				
				  if(previousMatrix[i][i]==0 && unfilledValence==0)
				  {
					  previousMatrix[i][i]=step;
				  }
				  if(sum==((BasicFragment)(setOfBasicFragment.get(i))).getNumberOfFreeValence() && unfilledValence>0)
				  {
					  //Satisfy the minimal forcing, fill this line
					  for(iter=0;iter<adjacencyMatrix.length;iter++)
					  {
						  if(adjacencyMatrix[i][iter]==-1)
						  {
							  adjacencyMatrix[i][iter]=0;
							  previousMatrix[i][iter]=step;
							  adjacencyMatrix[iter][i]=0;
							  previousMatrix[iter][i]=step;
						  }
						  
					  }
					  previousMatrix[i][i]=step;
					  changedLine+=1;
				  }
			  }
		  
		  	/* maximal forcing. */
			for(i=step;i<adjacencyMatrix.length;i++)
			{
				sum=0;
				filledValence=0;
				if(previousMatrix[i][i]>0)continue;
				for(j=0;j<adjacencyMatrix.length;j++)
				{
				  
				  	if(adjacencyMatrix[i][j]==-1)sum+=1;
					else  if(adjacencyMatrix[i][j]>0)filledValence+=1;
				}
				if(previousMatrix[i][i]==0 && sum==0)
				  {
					  previousMatrix[i][i]=step;
				  }
				  if(sum==(((BasicFragment)(setOfBasicFragment.get(i))).getNumberOfFreeValence()-filledValence) && sum>0)
				  {
					  bondOrder=((BasicFragment)(setOfBasicFragment.get(i))).getAttribute();
					  //Satisfy the maximal forcing, fill this line
					  for(iter=0;iter<adjacencyMatrix.length;iter++)
					  {
						  if(adjacencyMatrix[i][iter]==-1)
						  {
							  adjacencyMatrix[i][iter]=bondOrder;
							  previousMatrix[i][iter]=step;
							  adjacencyMatrix[iter][i]=bondOrder; 
							  previousMatrix[iter][i]=step;
						  }
						  
					  }
					  previousMatrix[i][i]=step;
					  changedLine+=1;
				  }
			}
		  }while(changedLine>0);
		  
		  /* find the first unfilled line*/
		  for(i=step;i<adjacencyMatrix.length;i++)
			  for(j=0;j<adjacencyMatrix.length;j++)if(adjacencyMatrix[i][j]==-1)return i;
		  return adjacencyMatrix.length;
	 }
	 
	 
	 /**
	  * check admissibility of the matrix.
	  * Details see Sergey G. Molodtsov, Computer-Aided Generation of Molecular Graphs,
	  * Match, 30(213),1994
	  * @param	step			the line is filling.
	  * @param	setOfBasicFragment	set of basic fragment
	  * @param	adjacencyMatrix		adjacency matrix
	  * @return	a boolean value whether this line could pass adissibility or not
	  */
	 public boolean checkAdmissibility(int step,Vector setOfBasicFragment,int[][] adjacencyMatrix)
	 {
		 for(int i=step;i<adjacencyMatrix.length;i++)
		 {
			  int sum=0;
			  int unfilledValence=0;
			  {
				  for(int j=0;j<adjacencyMatrix.length;j++)
				  {
					  if(adjacencyMatrix[i][j]>0)sum+=1;
					  if(adjacencyMatrix[i][j]==-1)unfilledValence+=1;
				  }
				if(sum>((BasicFragment)(setOfBasicFragment.get(i))).getNumberOfFreeValence()){return false;}
				  if(unfilledValence<(((BasicFragment)(setOfBasicFragment.get(i))).getNumberOfFreeValence()-sum) && unfilledValence>=0)
				  {
					 return false;
				  }
			  }
			  
		  }
		  return true;
	 }
	 
	 
	 /**
	  * check constraint of the matrix. Currently, there is only connectivity test.
	  * Later, there might be other tests. 
	  * @param	step			the line is filling.
	  * @param	setOfBasicFragment	set of basic fragment
	  * @param	adjacencyMatrix		adjacency matrix
	  * @param	ac			atomContainer of the node set, not used now
	  * @return	a boolean value whether this adjacency matrix pass  the constraint check or not
	  */
	 public boolean checkConstraint(int step,Vector setOfBasicFragment,int[][] adjacencyMatrix,AtomContainer ac)
	 {
		 int i,j,partialSum,totalSum,numberOfBond,decomposedNumber;
		 boolean isConnectivity;
		 boolean[] isVisited=new boolean[adjacencyMatrix.length];
		 int[] isDecomposed=new int[adjacencyMatrix.length];
		 int[] parentID=new int[adjacencyMatrix.length];
		 for(i=0;i<adjacencyMatrix.length;i++)
		 {
			 isVisited[i]=false;
			 isDecomposed[i]=0;
			 parentID[i]=0;
		 }
		 /* check connectivity first. This time, it is substructure or structure*/
		 if(step==1)return true;
		 else if(step==adjacencyMatrix.length)
		 {
			 decomposedNumber=0;
			 for(i=0;i<adjacencyMatrix.length;i++)
			 {
				 if(((BasicFragment)(setOfBasicFragment.get(i))).getID()>33)
				 {
					 isDecomposed[i]=((BasicFragment)(setOfBasicFragment.get(i))).getID();
					 parentID[i]=((BasicFragment)(setOfBasicFragment.get(i))).getParentID();
					 decomposedNumber+=1;
				 }
			 }
			 if(decomposedNumber>0)
			 {
				 for(i=0;i<adjacencyMatrix.length-1;i++)
					 for(j=i+1;j<adjacencyMatrix.length;j++)
						 if(isDecomposed[i]!=isDecomposed[j] && parentID[i]==parentID[j] &&
							 i!=j && adjacencyMatrix[i][i]==0 && adjacencyMatrix[j][j]==0)
						 {
							 adjacencyMatrix[i][j]=10;
							 adjacencyMatrix[j][i]=10;
							 adjacencyMatrix[i][i]=10;
							 adjacencyMatrix[j][j]=10;
						 }
			 }
			 
			 DFSM(adjacencyMatrix,0,isVisited);
			 if(decomposedNumber>0)
			 {
				 for(i=0;i<adjacencyMatrix.length;i++)
					 for(j=0;j<adjacencyMatrix.length;j++)
						 if(adjacencyMatrix[i][j]==10)adjacencyMatrix[i][j]=0;
			 }
			 for(i=0;i<adjacencyMatrix.length;i++)if(!isVisited[i])return false;
			 return true;
			// }
			
		 }
		 else 
		 {
			 decomposedNumber=0;
			 for(i=0;i<adjacencyMatrix.length;i++)
			 {
				 if(((BasicFragment)(setOfBasicFragment.get(i))).getID()>33)
				 {
					 isDecomposed[i]=((BasicFragment)(setOfBasicFragment.get(i))).getID();
					 parentID[i]=((BasicFragment)(setOfBasicFragment.get(i))).getParentID();
					 decomposedNumber+=1;
				 }
			 }
			 if(decomposedNumber>0)
			 {
				 for(i=0;i<adjacencyMatrix.length-1;i++)
					 for(j=i;j<adjacencyMatrix.length;j++)
						  if(isDecomposed[i]!=isDecomposed[j] && parentID[i]==parentID[j] &&
							 i!=j && adjacencyMatrix[i][i]==0 && adjacencyMatrix[j][j]==0)
						{
							 adjacencyMatrix[i][j]=10;
							 adjacencyMatrix[j][i]=10;
							 adjacencyMatrix[i][i]=10;
							 adjacencyMatrix[j][j]=10;
						 }
			 }
			 DFSM(adjacencyMatrix,0,isVisited);
			 if(decomposedNumber>0)
			 {
				 for(i=0;i<adjacencyMatrix.length;i++)
					 for(j=0;j<adjacencyMatrix.length;j++)
					 	if(adjacencyMatrix[i][j]==10)adjacencyMatrix[i][j]=0;
			 }
			 
			 partialSum=0;
			 totalSum=0;
			 for(i=0;i<step;i++)
			 {
				 if(isVisited[i])partialSum+=1;
			 }
			 for(i=0;i<adjacencyMatrix.length;i++)
			 {
				 if(isVisited[i])totalSum+=1;
			 }
			 if(partialSum==step && partialSum==totalSum)return false;
			 else if(totalSum==adjacencyMatrix.length)return true;
			 else return true;
		 }
	 }
	 
	 /**
	  * get the next WCF--weakly canonical complement. 
	  * details see Sergey G. Molodtsov, Computer-Aided Generation of Molecular Graphs,
	  * Match, 30(213),1994
	  * @param	setOfBasicFragment	set of basic fragment
	  * @param	step			the step of the generation
	  * @param	rowMatrix		the row which contains the filling line
	  * @param	adjacencyMatrix		adjacency matrix
	  * @param	previousMatrix		matrix to trace the change of adjacency matrix
	  * @param	parentID		array mainly used for complex bond
	  * @return	a boolean value whether there is next WCF or not
	  *
	  */
	 public boolean getNextWCF(Vector setOfBasicFragment,int step,int[] rowMatrix,int[][] adjacencyMatrix,int[][] previousMatrix,int[] parentID)
	 {
		 int i,j,iter,existBond,leftBond,totalBond,changedCategory,sum,changedFilledValue,nextNonZeroElement;
		 int bondOrder;
		 int[] setOfStability;
		 int[] previousLine;
		 int[] category;
		 int[] previousFilledValue;
		 int[] currentFilledValue;
		 
		 setOfStability=new int[rowMatrix.length];
		 previousLine=new int[rowMatrix.length];
		 category=new int[rowMatrix.length];
		 previousFilledValue=new int[rowMatrix.length];
		 currentFilledValue=new int[rowMatrix.length];
		 for(i=step;i<rowMatrix.length;i++)
		 {
			 previousLine[i]=rowMatrix[i];
		 }
		 
		 iter=0;
		 for(i=0;i<rowMatrix.length;i++)
		 {
			 rowMatrix[i]=adjacencyMatrix[step][i];
			 if(rowMatrix[i]==-1)iter+=1;
		 }
		 getSetOfStability(setOfBasicFragment,step,rowMatrix,adjacencyMatrix,setOfStability,parentID);
		 
		 existBond=0;
		 for(i=0;i<adjacencyMatrix.length;i++)if(rowMatrix[i]>0)existBond+=1;
		 totalBond=((BasicFragment)(setOfBasicFragment.get(step))).getNumberOfFreeValence();
		 bondOrder=((BasicFragment)(setOfBasicFragment.get(step))).getAttribute();
		 leftBond=totalBond-existBond;
		 iter=1;
		 while(iter<=setOfStability[0])
		 {
			 for(i=1;i<rowMatrix.length;i++)
			 {
				 if(setOfStability[i]==iter)category[iter]+=1;
				 if(setOfStability[i]==iter)
					 if(previousLine[i]>0)previousFilledValue[iter]+=1;
			 }
			 iter+=1;
		 }
		 category[0]=setOfStability[0];
		 changedCategory=0;
		 for(i=category[0];i>=1;i--)if(previousFilledValue[i]!=0){changedCategory=i;break;}
		 
		 /* judge mininal WCF*/
		 if(changedCategory==category[0])
		 {
			 if(previousFilledValue[category[0]]< category[changedCategory] && previousFilledValue[category[0]]==leftBond)
				 return false;
			 else if(previousFilledValue[category[0]]==category[changedCategory])
			 {
				 sum=0;
				 for(i=category[0];i>=1;i--)
				 {
					 sum+=previousFilledValue[i];
					 if(previousFilledValue[i]==category[i] && sum<leftBond)continue;
					 if(sum==leftBond)return false;
					 break;//non-termination
				 }
			 }
		 }
		 
		 /*get next WCF*/
		 if(changedCategory!=category[0])
		 {
			 for(i=1;i<=category[0];i++)
			 {
				 if(i<changedCategory)currentFilledValue[i]=previousFilledValue[i];
				 else if(i==changedCategory)currentFilledValue[i]=previousFilledValue[i]-1;
				 else if(i==(changedCategory+1))currentFilledValue[i]=1;
			 }
		 }
		else if(changedCategory==category[0] && (previousFilledValue[changedCategory]<category[changedCategory]||previousFilledValue[changedCategory-1]==0))
		{
			while(previousFilledValue[changedCategory-1]==0)changedCategory-=1;
			sum=0;
			for(i=1;i<(changedCategory-1);i++)
			{
				currentFilledValue[i]=previousFilledValue[i];
				sum+=currentFilledValue[i];
			}
			
			currentFilledValue[changedCategory-1]=previousFilledValue[changedCategory-1]-1;
			sum+=currentFilledValue[changedCategory-1];
			iter=changedCategory;
			 while(leftBond>sum)
			  {
				 currentFilledValue[iter]=leftBond-sum;
				 if(currentFilledValue[iter]>category[iter] && iter<category[0])
				 {
					 currentFilledValue[iter]=category[iter];
					 sum+=currentFilledValue[iter];
					 iter+=1;
					 continue;
				 }
				 else break;
			  }
		}
		else if(changedCategory==category[0]&& previousFilledValue[changedCategory]==category[changedCategory] && 
			previousFilledValue[changedCategory-1]!=0 && previousFilledValue[changedCategory-1]<category[changedCategory-1])
		{
			while(previousFilledValue[changedCategory-2]==0)changedCategory-=1;
			sum=0;
			for(i=1;i<(changedCategory-2);i++)
			{
				currentFilledValue[i]=previousFilledValue[i];
				sum+=currentFilledValue[i];
			}
			
			currentFilledValue[changedCategory-2]=previousFilledValue[changedCategory-2]-1;
			sum+=currentFilledValue[changedCategory-2];
			iter=changedCategory-1;
			 while(leftBond>sum)
			  {
				 currentFilledValue[iter]=leftBond-sum;
				 if(currentFilledValue[iter]>category[iter] && iter<category[0])
				 {
					 currentFilledValue[iter]=category[iter];
					 sum+=currentFilledValue[iter];
					 iter+=1;
					 continue;
				 }
				
				 else break;
			  }
		}
		else if(changedCategory==category[0]&& previousFilledValue[changedCategory]==category[changedCategory] && 
			previousFilledValue[changedCategory-1]!=0 && previousFilledValue[changedCategory-1]==category[changedCategory-1])
		{
			while(previousFilledValue[changedCategory-2]==category[changedCategory-2])changedCategory-=1;
			changedCategory-=1;
			while(previousFilledValue[changedCategory-2]==0)changedCategory-=1;
			sum=0;
			for(i=1;i<(changedCategory-2);i++)
			{
				currentFilledValue[i]=previousFilledValue[i];
				sum+=currentFilledValue[i];
			}
			
			currentFilledValue[changedCategory-2]=previousFilledValue[changedCategory-2]-1;
			sum+=currentFilledValue[changedCategory-2];
			iter=changedCategory-1;
			 while(leftBond>sum)
			  {
				 currentFilledValue[iter]=leftBond-sum;
				 if(currentFilledValue[iter]>category[iter] && iter<category[0])
				 {
					 currentFilledValue[iter]=category[iter];
					 sum+=currentFilledValue[iter];
					 iter+=1;
					 continue;
				 }
				
				 else break;
			  }
		}
		 for(i=1;i<=category[0];i++)
		 {
			 iter=currentFilledValue[i];
			 for(j=step+1;j<rowMatrix.length;j++)
			 {
				 if(setOfStability[j]==i && iter>0)
				 {
					 rowMatrix[j]=bondOrder;
					 iter-=1;
				 }
				 else if(iter==0)break;
			}
		 }
		 for(i=step+1;i<rowMatrix.length;i++)if(rowMatrix[i]==-1)rowMatrix[i]=0;
		 for(i=0;i<adjacencyMatrix.length;i++)
		 {
			 adjacencyMatrix[step][i]=rowMatrix[i];
			 if(previousMatrix[step][i]==-1)previousMatrix[step][i]=step+1;
			 
			 adjacencyMatrix[i][step]=rowMatrix[i];
			 if(previousMatrix[i][step]==-1)previousMatrix[i][step]=step+1;
		 }
		
		 return true;
	 }
	 
	 /**
	  * Judge whether this line of the adjacency matrix is forced or not.
	  * @param	step            the step of the generation
	  * @param	previousMatrix  matrix to trace the change of adjacency matrix
	  * @param	adjacency       adjacency matrix 
	  * @return	a boolean value whether the line is force-filling or not
	  *
	  */
	 public boolean isForceFilling(int step,int[][] previousMatrix,int[][] adjacency)
	 {
		 if(previousMatrix[step][step]>0)return true;
		 return false;
		 
	 }
	 
	 /**
	  * get the initial equivalent partition.
	  * @param	setOfBasicFragment	set of basic fragment
	  * @param	setOfStability		array contains the initial classing of the nodes
	  * @return	the number of equivalent class
	  */
	 public int getEquivalentClass(Vector setOfBasicFragment,int[] setOfStability)
	 {
		 int i,j,count,line;
		 int temp,size;
		 
		 size=setOfBasicFragment.size();
		 int[] category=new int[size+1];
		 int[] equivalentClass=new int[size];
		 
		 line=0;
		 for(i=0;i<size;i++)setOfStability[i]=0;
		 for(i=0;i<size;i++)
			setOfStability[i]=((BasicFragment)(setOfBasicFragment.get(i))).getID();
		
		 /* get the category for this line*/
		 count=1;
		 
		 category[1]=setOfStability[0];
		 for(i=1;i<size;i++)
		 {
			 for(j=1;j<=count;j++)
			 {
				temp=setOfStability[i]-category[j];
				 if(temp==0)break;
			 }
			 if(j>count)
			 {
				 count+=1;
				 category[count]=setOfStability[i];
			 }
		 }
		 for(i=0;i<size;i++)
			for(j=1;j<=count;j++)
			{
				temp=setOfStability[i]-category[j];
				if(temp==0)equivalentClass[i]=j;
			}
		 for(i=0;i<setOfStability.length;i++)
		 {
			 setOfStability[i]=equivalentClass[i];
		 }
		 return count;
	 }
	 
	 
	 /**
	  * write adjacencyMatrix to debug. The method is only for debugging.
	  * @param	setOfBasicFragment	set of basic fragment
	  * @param	number			the sequence number of the structure
	  * @param	adjacency		adjacency matrix of the structure
	  */
	  public void writeToFile(Vector setOfBasicFragment,int number,int[][] adjacency)
	  {
		  int i,j,size;
		  
		  size=setOfBasicFragment.size();
			  String s="Number Of Structure is	";
			  structureout.print(s);
			  structureout.print(number);
			  structureout.println();
			  for(i=0;i<setOfBasicFragment.size();i++)
			  {
				  structureout.print(((BasicFragment)(setOfBasicFragment.get(i))).getBasicFragment());
				  structureout.print("	");
			  }
			  structureout.println();
			  for(i=0;i<size;i++)
			  {
				  for(j=0;j<size;j++)
				  {
					  structureout.print(adjacency[i][j]);
					  structureout.print("	");
				  }
				  structureout.println();
			  }
			structureout.flush();
	  }
	  
	  
	  
	 /**
	  * recursive graph traversal with the adjacency matrix.
	  * @param	adjacency	Adjacency matrix
	  * @param	number		node which would be visited
	  * @param	isVisited	boolean array which stored the visiting state for nodes
	  */
	 public void DFSM(int[][] adjacency,int number,boolean[] isVisited)
	 {
		  int j;
		  isVisited[number]=true;
		  for(j=0;j<adjacency.length;j++)
			  if((adjacency[number][j]>0) && !isVisited[j])DFSM(adjacency,j,isVisited);
	 }
	  
	  /**
	   * Sort the basic fragment set. The purpose is to improve the generation speed by some specific 
	   * ordering. In fact, it is little usage in the whole step.
	   * @param	setOfBasicFragment	set of basic fragment
	   * @param	parentID		Array for storing the previous ID, only used for special fragment.
	   * @return	vector contains the sorting result.
	   */
	  public Vector getOrderOfBasicFragmentSet(Vector setOfBasicFragment,int[] parentID)
	  {
		  int i,j,sum;
		  int size=setOfBasicFragment.size();
		  int classID1,classID2;
		  int[] originalNumbering=new int[size];
		  int[] revisedNumbering=new int[size];
		  
		  Vector orderSet=new Vector();
		  int number=1;
		  
		  for(i=0;i<size;i++)
		  {
			  parentID[i]=-1;
			
		  }
		 
		  
		  for(j=0;j<setOfBasicFragment.size();j++)
		  {
			  if(((BasicFragment)(setOfBasicFragment.get(j))).getID()==1)
				  orderSet.addElement((BasicFragment)(setOfBasicFragment.get(j)));
		  }
		  for(j=0;j<setOfBasicFragment.size();j++)
		  {
			  if(((BasicFragment)(setOfBasicFragment.get(j))).getID()==5)
				  orderSet.addElement((BasicFragment)(setOfBasicFragment.get(j)));
		  }
		  for(j=setOfBasicFragment.size()-1;j>=0;j--)
		   {
			if(((BasicFragment)(setOfBasicFragment.get(j))).getID()<=33)
				continue;
			orderSet.addElement((BasicFragment)(setOfBasicFragment.get(j)));
		  }
		  
		  for(j=0;j<setOfBasicFragment.size();j++)
		  {
			if(((BasicFragment)(setOfBasicFragment.get(j))).getID()>33)
				continue;
			if(((BasicFragment)(setOfBasicFragment.get(j))).getID()==1 
				|| ((BasicFragment)(setOfBasicFragment.get(j))).getID()==3
				||((BasicFragment)(setOfBasicFragment.get(j))).getID()==5)
				continue;
			orderSet.addElement((BasicFragment)(setOfBasicFragment.get(j)));
		  }
		  
		  for(j=0;j<setOfBasicFragment.size();j++)
		  {
			  if(((BasicFragment)(setOfBasicFragment.get(j))).getID()==3)
				  orderSet.addElement((BasicFragment)(setOfBasicFragment.get(j)));
		  }
		 
		  for(i=0;i<size;i++)
			  revisedNumbering[i]=((BasicFragment)(orderSet.get(i))).getParentID();
		  for(i=0;i<size;i++)
			  originalNumbering[i]=((BasicFragment)(orderSet.get(i))).getID();
		  for(i=0;i<(size-1);i++)
		  {
			  if(originalNumbering[i]<=33)continue;
			
			  for(j=i+1;j<size;j++)
			  {
				  if(originalNumbering[j]<=33)continue;
				  
				  if(revisedNumbering[i]==revisedNumbering[j] && originalNumbering[i]!=originalNumbering[j] && j!=i && parentID[i]==-1 && parentID[j]==-1)
				  {
					  parentID[i]=j;
					  parentID[j]=i;
					  break;
				  }
			  }
		  }
		  setOfBasicFragment.clear();
		  return orderSet;
	  }
	  
	  
	  /**
	   * Judge the possible structure. The idea is the following:
	   * 1. get the canonical representation for each candidate.
	   * 2. For each fragment set, compare with the previous structures to remove redundancy.
	   *
	   * @param	setOfBasicFragment	set of basic fragment
	   * @param	adjacencyMatrix		adjacency Matrix of candidate
	   * @param	storedSymbolOfStructure	structures are converted into symbol stored in one array
	   * @param	totalNumberOfThisSet	Number of the structures for this fragment set
	   * @param	totalNumberOfAtomAndBond	for one fragment, total number of atom and bond
	   */
	  public void getFinalStructure(Vector setOfBasicFragment,int[][] adjacencyMatrix,int[] storedSymbolOfStructure,int[] totalNumberOfThisSet,int totalNumberOfAtomAndBond)
	  {
		  int i,j,decomposedNumber,row,column,size;
		  int m;
		  int kk,k1;
		  
		  Vector originalSet=new Vector();
		  int[] isDecomposed=new int[adjacencyMatrix.length];
		  int[] parentID=new int[adjacencyMatrix.length];
		  int[] decomposedLine=new int[adjacencyMatrix.length];
		  int[] connectedFragment=new int[adjacencyMatrix.length];
		  int[][] originMatrix=new int[adjacencyMatrix.length][adjacencyMatrix.length];
		  int[] b=new int[400];
		  int[][] connectivity;
		  
		  decomposedNumber=0;
		  for(i=0;i<adjacencyMatrix.length;i++)
		  {
			 isDecomposed[i]=-1;
			 parentID[i]=-1;
			 decomposedLine[i]=-1;
			 connectedFragment[i]=-1;
		  }
		  
		  for(i=0;i<adjacencyMatrix.length;i++)
		  {
			  if(((BasicFragment)(setOfBasicFragment.get(i))).getID()>33)
			  {
				 isDecomposed[i]=((BasicFragment)(setOfBasicFragment.get(i))).getID();
				 parentID[i]=((BasicFragment)(setOfBasicFragment.get(i))).getParentID();
				 decomposedNumber+=1;
			  }
		   }
		   
		   decomposedNumber/=2;
		   if(decomposedNumber>0)
		   {
			for(i=0;i<adjacencyMatrix.length;i++)
			{
				if(isDecomposed[i]==-1)originalSet.addElement(((BasicFragment)(setOfBasicFragment.get(i))));
				else
				{
					if(decomposedLine[i]==1)continue;
					for(j=i+1;j<adjacencyMatrix.length;j++)
					{
						if(isDecomposed[i]!=isDecomposed[j] && parentID[i]==parentID[j] && j>i && decomposedLine[j]==-1)
						{
							originalSet.addElement((BasicFragment)(basicFragment.get(parentID[i]-1)));
							decomposedLine[j]=1;
							connectedFragment[j]=i;
							connectedFragment[i]=j;
							break;
						}
					}
				}
			}
			row=0;
			for(i=0;i<adjacencyMatrix.length;i++)
			{
				int number=connectedFragment[i];
				
				if(decomposedLine[i]==1)continue;
				else if(number>0)
				{
					if(decomposedLine[number]==1)
					{
						originMatrix[row][row]=0;
						column=0;
						for(j=0;j<adjacencyMatrix.length;j++)
						{
							if(decomposedLine[j]==-1)
							{
								int correspondingLine=connectedFragment[j];
								if(correspondingLine!=-1)
								{
									if(adjacencyMatrix[i][j]>0)
									{
										originMatrix[row][column]=adjacencyMatrix[i][j];
										column+=1;
									}
									else if(adjacencyMatrix[i][correspondingLine]>0)
									{
										originMatrix[row][column]=adjacencyMatrix[i][correspondingLine];
										column+=1;
									}
									else if(adjacencyMatrix[number][correspondingLine]>0)
									{
										originMatrix[row][column]=adjacencyMatrix[number][correspondingLine];
										column+=1;
									}
									else if(adjacencyMatrix[number][j]>0)
									{
										originMatrix[row][column]=adjacencyMatrix[number][j];
										column+=1;
									}
									else
									{
										originMatrix[i][j]=0;
										column+=1;
									}
									
								}
								else
								{
									
									originMatrix[row][column]=adjacencyMatrix[i][j]+adjacencyMatrix[connectedFragment[i]][j];
									column+=1;
								}
							}
						}
						row+=1;
					}
				}
				else if(number<0)
				{
					originMatrix[row][row]=0;
					column=0;
					for(j=0;j<adjacencyMatrix.length;j++)
					{
					
						if(decomposedLine[j]==-1)
						{
							if(connectedFragment[j]==-1)
							{
								originMatrix[row][column]=adjacencyMatrix[i][j];
								column+=1;
							}
							else if(decomposedLine[connectedFragment[j]]==1)
							{
								originMatrix[row][column]=adjacencyMatrix[i][j]+adjacencyMatrix[i][connectedFragment[j]];
								column+=1;
							}
						}
					}
					row+=1;
				}
			}
			
			int bondNumber=0;
			for(i=0;i<row-1;i++)
				for(j=i;j<row;j++)
					if(originMatrix[i][j]!=0)bondNumber+=1;
			if(bondNumber<(totalNumberOfAtomAndBond-originalSet.size()))return;
			/*canonical*/
			connectivity=normalization(originalSet,originMatrix);
			size=originalSet.size();
			
			m=0;
			for(i=0;i<size;i++)
				for(j=0;j<connectivity[i][8];j++)
					if(connectivity[i][j]>i)
					{
						m+=1;
						b[m]=i*1000+connectivity[i][j]*10+connectivity[i][j+4];
					}
					b[0]=m;
		  }
		  
		  else
		  {
			  
			  /*canonical*/
			  connectivity=normalization(setOfBasicFragment,adjacencyMatrix);
			  size=setOfBasicFragment.size();
		  
		  }
		  m=0;
		  for(i=0;i<size;i++)
			for(j=0;j<connectivity[i][8];j++)
				if(connectivity[i][j]>i)
				{
					m+=1;
					b[m]=i*1000+connectivity[i][j]*10+connectivity[i][j+4];
				}
				b[0]=m;
		  if(totalNumberOfThisSet[0]>0)
		  {
			  for(i=0;i<totalNumberOfThisSet[0];i++)
			  {
				  kk=i*totalNumberOfAtomAndBond;
				  k1=kk+size;
				  for(j=0;j<size;j++)
				  {
					  if(storedSymbolOfStructure[kk+j]!=connectivity[j][10])break;
				  }
				  
				  if(j<size)continue;
				  for(j=1;j<=b[0];j++)
				  {
					  if(storedSymbolOfStructure[k1+j-1]!=b[j])break;
				  }
				  if(j<=b[0])continue;
				  if(j>b[0])return;
			  }
		  }
		  
		  kk=totalNumberOfThisSet[0]*totalNumberOfAtomAndBond;
		  k1=kk+size;
		  for(j=0;j<size;j++)
			  storedSymbolOfStructure[kk+j]=connectivity[j][10];
		  for(j=1;j<=b[0];j++)
			  storedSymbolOfStructure[k1+j-1]=b[j];
		  totalNumberOfThisSet[0]+=1;
		  numberOfStructure+=1;
		  if(decomposedNumber>0)
		  {
			 writeToFile(originalSet,numberOfStructure, originMatrix);
			 if(numberOfStructure<500)
				 convertToMol(originalSet,originMatrix,structures);
			 convertToSMILES(originalSet,originMatrix,smiles);
		  //	 writeToFile(setOfBasicFragment,numberOfStructure, adjacencyMatrix);
		  }
		  else
		  {
			writeToFile(setOfBasicFragment,numberOfStructure, adjacencyMatrix);
			if(numberOfStructure<500)
				convertToMol(setOfBasicFragment,adjacencyMatrix,structures);
			convertToSMILES(setOfBasicFragment,adjacencyMatrix,smiles);
		  }
	  }
	  
	  
	  /**
	   * Canonicalize the structure based on All-Path algorithm.
	   * @param	setOfBasicFragment	basic fragment set
	   * @param	adjacencyMatrix		adjacency matrix for a candidate
	   * @return	the canonical representation of the given candidate
	   */
	  public int[][] normalization(Vector setOfBasicFragment,int[][] adjacencyMatrix) 
	  {
		  
		  int i,ii,j,number,number1,number2,size,startClass,ki;
		  int kk,kk1,kk2,mm,m1,m2,m,k;
		  int[] classID;
		  int[][] connectivity;
		  int[][] changedConnectivity;
		  int[][] replacedConnectivity;
		  
		  int[] connectedAtom,connectedBond;
		  
		  int[] sameClassNumber;
		  int[] changedClassID;
		  int[] originalClass;
		  
		  size=setOfBasicFragment.size();
		  classID=new int[size];
		  connectivity=new int[size][11];
		  changedConnectivity=new int[size][11];
		  replacedConnectivity=new int[size][11];
		  
		  connectedAtom=new int[6];
		  connectedBond=new int[6];
		  
		  sameClassNumber=new int[size];
		  originalClass=new int[size];
		  changedClassID=new int[size];
		  j=0;
		  for(i=0;i<size;i++)
		  {
			  classID[i]=((BasicFragment)(setOfBasicFragment.get(i))).getID();
			  originalClass[i]=classID[i];
		  }
		  
		  for(i=0;i<size;i++)
		  {
			 number=0;
			 for(j=0;j<size;j++)
			 {
				 if(adjacencyMatrix[i][j]!=0)
				 {
					 connectivity[i][number]=j;
					 connectivity[i][number+4]=adjacencyMatrix[i][j];
					 number+=1;
				 }
			 }
			 connectivity[i][8]=number;
			 connectivity[i][9]=-1;
			 connectivity[i][10]=originalClass[i];
		  }
		  for(i=0;i<size;i++)
			  for(j=0;j<11;j++)
				  replacedConnectivity[i][j]=connectivity[i][j];
		  
		  
		  number1=getSymmetryFromAllPath(size,replacedConnectivity,classID);
		  number2=getSymmetryFromNeighbour(size,number1,replacedConnectivity,classID);
		  kk1=0;
		  kk2=classID[0];
		  for(i=0;i<size;i++)
			  if(classID[i]>kk2)
			  {
				  kk2=classID[i];
				  kk1=i;
			  }
		  startClass=0;
		  for(i=0;i<size;i++)
			  if(classID[i]==kk2)
			  {
				  sameClassNumber[startClass]=i;
				  startClass+=1;
			  }
		  
		  
		  for(i=0;i<size;i++)changedClassID[i]=classID[i];
		  
		  number1=number2;
		  for(ki=0;ki<startClass;ki++)
		  {
			  kk1=sameClassNumber[ki];
			  for(i=0;i<size;i++)
			  {
				  replacedConnectivity[i][9]=-1;
				  number2=number1;
				  classID[i]=changedClassID[i];
				  for(j=0;j<11;j++)changedConnectivity[i][j]=-1;
			  }
			  changedConnectivity[0][8]=replacedConnectivity[kk1][8];
			  changedConnectivity[0][10]=replacedConnectivity[kk1][10];
			  changedConnectivity[0][9]=kk1;
			  replacedConnectivity[kk1][9]=0;
			  kk2=0;
			  ii=0;
			  m2=0;
			  while(ii<size)
			  {
				  mm=replacedConnectivity[kk1][8];
				  for(i=0;i<mm;i++)
				  {
					  connectedAtom[i]=replacedConnectivity[kk1][i];
					  connectedBond[i]=replacedConnectivity[kk1][i+4];
				  }
				  while(true)
				  {
					  do
					  {
					  for(i=0;i<mm;i++)
						  if(replacedConnectivity[connectedAtom[i]][9]!=-1)
						  {
							  m2=i;
							  break;
						  }
					    if(i<mm)break;
					    if(mm==1)
					   {
						   m2=0;
						   
						    kk2+=1;
						    changedConnectivity[kk2][8]=replacedConnectivity[connectedAtom[m2]][8];
						    changedConnectivity[kk2][10]=replacedConnectivity[connectedAtom[m2]][10];
						    replacedConnectivity[connectedAtom[m2]][9]=kk2;
						    changedConnectivity[kk2][9]=connectedAtom[m2];
					   }
					   else
					   {
					   
					 	  m1=classID[connectedAtom[0]];
						  m2=0;
						  for(i=1;i<mm;i++)
							  if(classID[connectedAtom[i]]>m1)
							  {
								  m2=i;
								  m1=classID[connectedAtom[i]];
							  }
					   	for(i=0;i<mm;i++)
						   if((i!=m2)&& classID[connectedAtom[i]]==classID[connectedAtom[m2]])
						   {
							   for(j=0;j<size;j++)
								   if(replacedConnectivity[j][9]!=-1)classID[j]=classID[j]+size-replacedConnectivity[j][9];
							   number2=getSymmetryFromNeighbour(size,number2,replacedConnectivity,classID);
							   m1=classID[connectedAtom[0]];
							   m2=0;
							   for(j=1;j<mm;j++)
								   if(classID[connectedAtom[j]]>m1)
								   {
									   m2=j;
									   m1=classID[connectedAtom[j]];
								   }
								   break;
						   }
						    kk2+=1;
						    changedConnectivity[kk2][8]=replacedConnectivity[connectedAtom[m2]][8];
						    changedConnectivity[kk2][10]=replacedConnectivity[connectedAtom[m2]][10];
						    replacedConnectivity[connectedAtom[m2]][9]=kk2;
						    changedConnectivity[kk2][9]=connectedAtom[m2];
					   }
					  }while(false);
					  
					    kk=replacedConnectivity[connectedAtom[m2]][9];
					    for(i=0;i<4;i++)
					    {
						    if(changedConnectivity[ii][i]==kk)break;
						    if(changedConnectivity[ii][i]!=-1)continue;
						    changedConnectivity[ii][i]=kk;
						    changedConnectivity[ii][i+4]=connectedBond[m2];
						    break;
					    }
					    
					    for(i=0;i<4;i++)
					    {
						    if(changedConnectivity[kk][i]==ii)break;
						    if(changedConnectivity[kk][i]!=-1)continue;
						    changedConnectivity[kk][i]=ii;
						    changedConnectivity[kk][i+4]=connectedBond[m2];
						    break;
					    }
					    mm-=1;
					    if(mm==0)
					    {
						    ii+=1;
						    if(ii<size)kk1=changedConnectivity[ii][9];
						    break;
					    }
					    else
					    {
						    for(i=m2;i<mm;i++)
						    {
							    connectedAtom[i]=connectedAtom[i+1];
							    connectedBond[i]=connectedBond[i+1];
						    }
						    connectedAtom[mm+1]=0;
						    connectedBond[mm+1]=0;
					    }
				  }
			  }
			  
			  for(i=0;i<size;i++)
				  for(j=0;j<changedConnectivity[i][8]-1;j++)
					  for(kk=j+1;kk<changedConnectivity[i][8];kk++)
						  if(changedConnectivity[i][j]>changedConnectivity[i][kk])
						  {
							  mm=changedConnectivity[i][j];
							  changedConnectivity[i][j]=changedConnectivity[i][kk];
							  changedConnectivity[i][kk]=mm;
							  mm=changedConnectivity[i][j+4];
							  changedConnectivity[i][j+4]=changedConnectivity[i][kk+4];
							  changedConnectivity[i][kk+4]=mm;
						  }
			  if(ki==0)
			  {
				  for(i=0;i<size;i++)
					  for(j=0;j<11;j++)
					  {
						  connectivity[i][j]=changedConnectivity[i][j];
					  }
				  for(i=0;i<size;i++)
					  replacedConnectivity[i][9]=-1;
				  continue;
			  }
			  
			  for(i=0;i<size;i++)
			  {
				  for(j=0;j<9;j++)
				  {
					  if(connectivity[i][j]>changedConnectivity[i][j])
					  {
						  for(k=0;k<size;k++)
							  for(m=0;m<10;m++)
							  {
								  connectivity[k][m]=changedConnectivity[k][m];
							  }
						  break;
					  }
					  else if(connectivity[i][j]<changedConnectivity[i][j])break;
				  }
				  if(j<9)break;
			  }
			  
			  if(i==size && j==9)
			  {
				  for(i=0;i<size;i++)
					  for(j=0;j<11;j++)
					  {
						  connectivity[i][j]=changedConnectivity[i][j];
					  }
			  }
			  for(i=0;i<size;i++)
					  replacedConnectivity[i][9]=-1;

		  }
		  for(i=0;i<size;i++)
			  for(j=0;j<connectivity[i][8]-1;j++)
				  for(k=j+1;k<connectivity[i][8];k++)
					  if(connectivity[i][j]>connectivity[i][k])
					  {
						  m=connectivity[i][j];
						  connectivity[i][j]=connectivity[i][k];
						  connectivity[i][k]=m;
						  m=connectivity[i][j+4];
						  connectivity[i][j+4]=connectivity[i][k+4];
						  connectivity[i][k+4]=m;
					  }
		  return connectivity;
	 }
	 
	 
	 /**
	  * Method to get the equivalent class. It is a method based on All-Path algorithm.
	  * Detail please see article  Hu CY, Chemom. Intell. Lab. Syst. 45(318),1999
	  * @param	size		the number of nonhydrogen atom.
	  * @param	connectivity	adjacency matrix
	  * @param	classID		initial class
	  * @return	number of class
	  */
	 public int getSymmetryFromAllPath(int size,int[][] connectivity,int[] classID)
	 {
		 int i,j,k,no,k1,k2,kk,number,count;
		 int[] ipath;
		 int[] con;
		 double a,b,c,t;
		 double[] w=new double[size+1];
		 double[] s=new double[size+1];
		 double[] pw=new double[size+1];
		 
		 
		 ipath=new int[size+1];
		 con=new int[size+1];
		 
		 ipath[0]=0;
		 con[0]=0;
		 no=0;
		 
		 while(no<size)
		 {
			 for(i=0;i<size;i++)
			 {
				 w[i]=(double)0.0;
				 pw[i]=(double)0.0;
			 }
			 
			 kk=1;
			 ipath[kk]=no;
			 w[kk-1]=(double)classID[no];
			 pw[kk-1]=pw[kk-1]+w[kk-1]*w[kk-1];
			 
			 kk+=1;
			 
			 
			 con[kk-1]=0;
			 while(true)
			 {
				 
				 con[kk-1]+=1;
				 k1=ipath[kk-1];
				 if(con[kk-1]>connectivity[k1][8])
				 {
					 kk-=1;
					 if(kk==1)break;
					 continue;
				 }
				 k2=con[kk-1];
				 k=connectivity[k1][k2-1];
				 if((kk>3)&&(k==no))
				 {
					 w[0]=(double)classID[no];
					 continue;
				 }
				 for(i=1;i<=kk-1;i++)
					 if(k==ipath[i])break;
				 if(i<=(kk-1))continue;
				 ipath[kk]=k;
				 
				 a=(double)classID[k];
				 b=(double)connectivity[k1][k2+4-1];
				 t=(double)(1-kk);
				 w[kk-1]=w[kk-2]+a*b*(double)Math.pow(10.0,t);
				 t=w[kk-1]/(double)(kk*kk*kk);
				 pw[kk-1]+=t*t;
	 		 	 kk+=1;
				 con[kk-1]=0;
			 }
			 
			 s[no]=(double)0.0;
			 for(i=0;i<size-1;i++)s[no]+=pw[i];
			 no+=1;
		 }
		 count=0;
		 w[0]=s[0];
		 for(i=1;i<size;i++)
		 {
			 for(j=0;j<=count;j++)
			 {
				 t=s[i]-w[j];
				 if(t<0.0)t=-t;
				 if(t<LOST)break;
			 }
			 if(j>count)
			 {
				 count+=1;
				 w[count]=s[i];
			 }
		 }
		 
		 for(i=0;i<=count-1;i++)
			 for(j=i+1;j<=count;j++)
				 if(w[i]>w[j])
				 {
					 t=w[i];
					 w[i]=w[j];
					 w[j]=t;
				 }
		 for(i=0;i<size;i++)
			 for(j=0;j<=count;j++)
			 {
				 t=s[i]-w[j];
				 if(t<0.0)t=-t;
				 if(t<LOST)
				 {
					 classID[i]=j+1;
				 }
				 
			 }
		 return count+1;
	 }


	 /**
	  * This method is used in the middle of partitioning nodes if there are two or more nodes which are equivalent. By
	  * using this method, equivalent nodes could be divided into different class by adding some other method.
	  * @param	size		the number of nonhydrogen atom.
	  * @param	number		the existing class
	  * @param	connectivity	adjacency matrix
	  * @param	classID		initial class
	  * @return	number of class
	  */
	 public int getSymmetryFromNeighbour(int size,int number,int[][] connectivity,int[] classID)
	 {
		 int i,j,k,kk,numberOfClass,count;
		 int[] denovoClass;
		 int[] neighbour=new int[4];
		 double temp1,temp2,temp3,temp4,temp5,t;
		
		 double[] w=new double[size];
		 double[] s=new double[size];
		 
		 numberOfClass=number;
		 while(numberOfClass<size)
		 {
			 for(kk=0;kk<size;kk++)
			 {
				 for(i=0;i<neighbour.length;i++)
					 neighbour[i]=0;
				 for(i=0;i<connectivity[kk][8];i++)
				 {
					 neighbour[i]=classID[connectivity[kk][i]]*10+connectivity[kk][i+4];
				 }
				 for(i=0;i<neighbour.length-1;i++)
					 for(j=i+1;j<neighbour.length;j++)
						 if(neighbour[i]<neighbour[j])
						 {
							 k=neighbour[i];
							 neighbour[i]=neighbour[j];
							 neighbour[j]=k;
						 }
				temp1=classID[kk];
				temp2=neighbour[0];
				temp3=neighbour[1];
				temp4=neighbour[2];
				temp5=neighbour[3];
				s[kk]=temp1*10E12+temp2*10E9+temp3*10E6+temp4*10E3+temp5;
			 }
			 count=0;
			 w[0]=s[0];
			 for(i=1;i<size;i++)
			 {
				 for(j=0;j<=count;j++)
				 {
					 t=s[i]-w[j];
					 if(t<0.0)t=-t;
					 if(t<LOST)break;
				 }
				 if(j>count)
				 {
					 count+=1;
					 w[count]=s[i];
				 }
			 }
			 if((count+1)<=numberOfClass)return count+1;
			 
			 for(i=0;i<=count-1;i++)
				 for(j=i+1;j<=count;j++)
					 if(w[i]>w[j])
					 {
						 t=w[i];
						 w[i]=w[j];
						 w[j]=t;
					 }
			 for(i=0;i<size;i++)
				 for(j=0;j<=count;j++)
				 {
					 t=s[i]-w[j];
					 if(t<0.0)t=-t;
					 if(t<LOST)
					 {
					 classID[i]=j+1;
					 }
				 
				 }
			 numberOfClass=count+1;
			 if((count+1)==size)return count+1;
		 }
		 return numberOfClass;
	 }
	 
	/**
	 * A bridge between CDK molecule and adjacency matrix. It might be a temporary thing, later,
	 * all should be done according to CDK.
	 * @param	set			basic fragment set
	 * @param	matrix			adjacency matrix of the corresponding structure
	 * @param	structures		vector contains all generated structures
	 */
	 public void convertToMol(Vector set,int[][] matrix,Vector structures)
	 {
		 int i,j;
		 Molecule mol=new Molecule();
		 int size=set.size();
		 for(i=0;i<size;i++)
			 mol.addAtom(new Atom(((BasicFragment)(set.get(i))).getHeavyAtomSymbol()));
		 for(i=0;i<size-1;i++)
			 for(j=i+1;j<size;j++)
				 if(matrix[i][j]!=0)mol.addBond(i,j,matrix[i][j]);
		 
		 structures.addElement(mol);
	 }
	 
	 
	 /**
	  * A bridge between CDK SMILES format and adjacency matrix. It might be a temporary thing, later,
	  * all should be done according to CDK.
	  * @param	set      basic fragment set
	  * @param	matrix   adjacency matrix of the corresponding structure
	  * @param	smiles   vector contains all generated structures
	  */
	 public void convertToSMILES(Vector set,int[][] matrix,Vector smiles)
	 {
		 int i,j;
		 Molecule mol=new Molecule();
		 int size=set.size();
		 for(i=0;i<size;i++)
		 {
			 Atom atom=new Atom(((BasicFragment)(set.get(i))).getHeavyAtomSymbol());
			 
			 atom.setHydrogenCount(((BasicFragment)(set.get(i))).getNumberOfHydrogen());
			 
			 mol.addAtom(atom);
		 }
		 for(i=0;i<size-1;i++)
			 for(j=i+1;j<size;j++)
				 if(matrix[i][j]!=0)mol.addBond(i,j,matrix[i][j]);
		 
		 SmilesGenerator sg = new SmilesGenerator(mol.getBuilder());
		 String smilesString = sg.createSMILES(mol);
		 smiles.addElement(smilesString);
	 }
	 
	 
	 
	  /**
	   * Get the suitable structures
	   * @return	vector contains suitable structures
	   */
	  public Vector getStructures()
	  {
		 return this.structures;
	  }
	 
	  /**
	   * Get the number of isomers
	   * @return	the number of isomers
	   */
	 public int getNumberOfStructure()
	 {
		return this.numberOfStructure;
	 }
	 
	 
	  /**
	   * Get the vector of SMILES
	   * @return	vector contains suitable SMILES format for suitable structures
	   */
	  public Vector getSMILES()
	  {
		 return this.smiles;
	  }
	 
	 /**
	  * As only used in this class might now, define it as an inner class. It just works as fragment class
	  */
	 private class BasicFragment
	 {
		 private int ID;
		 private int numberOfFreeValence;
		 private int bondAttribute;
		 private String basicFragment;
		 private String heavyAtomSymbol;
		 private int numberOfHydrogen;
		 private int parentID;
		 
		 public BasicFragment(int ID,int numberOfFreeValence,int attribute,int numberOfHydrogen, int parentID,String basicFragment,String heavyAtomSymbol)
		 {
			 this.ID=ID;
			 this.numberOfFreeValence=numberOfFreeValence;
			 this.bondAttribute=attribute;
			 this.numberOfHydrogen=numberOfHydrogen;
			 this.basicFragment=basicFragment;
			 this.heavyAtomSymbol=heavyAtomSymbol;
			 this.parentID=parentID;
		 }
		 
		 public int getID()
		 {
			 return this.ID;
		 }
		 
		 public int getNumberOfFreeValence()
		 {
			 return this.numberOfFreeValence;
		 }
		 
		 public int getAttribute()
		 {
			 return this.bondAttribute;
		 }
		 
		 public String getBasicFragment()
		 {
			 return this.basicFragment;
		 }
		 
		 public String getHeavyAtomSymbol()
		 {
			 return this.heavyAtomSymbol;
		 }
		 
		 public int getNumberOfHydrogen()
		 {
			 return this.numberOfHydrogen;
		 }
		 
		 public int getParentID()
		 {
			 return this.parentID;
		 }
	 }

	public AtomContainer getAtomContainer() {
		return atomContainer;
	}


	public void setAtomContainer(AtomContainer atomContainer) {
		this.atomContainer = atomContainer;
	}
}


