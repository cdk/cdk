/* $Revision: 8397 $ $Author: rajarshi $ $Date: 2007-06-24 05:24:27 +0200 (Sun, 24 Jun 2007) $
 *
 * Copyright (C) 1997 Guillaume Cottenceau <gcottenc@ens.insa-rennes.fr> 
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
package org.openscience.cdk.tools;

import java.io.IOException;
import java.util.ArrayList;


import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;

/**
 * Tool to determine molecular formula consistent with a given accurate mass.
 * 
 * @author     Miguel Rojas
 * 
 * @cdk.module experimental
 */
public class MassToFormulaTool {

	private LoggingTool logger = new LoggingTool(MassToFormulaTool.class);
	
	/** The mass which is calculate the molecular formula. */
	private double mass;
	
	/** The max number of solutions to be found. Default number fixed to 50*/
	private static int max_Solutions = 50;
	
	/** The molecular formulas obtained from the accurate mass.*/
	private ArrayList<String> molecularFormula;

	protected IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

	/** Elements that must be presents in the molecular formula.*/
	private IElement_Nr[] elemToCond;

	/** Mass Ratio to look for. As default 0.05*/
	private static double ratio = 0.05;
	

	/**
	 * Construct an instance of MassToFormulaTool.
	 */
	public MassToFormulaTool() {
	}
	/**
	 * Construct an instance of MassToFormulaTool, initialized with a mass.
	 * 
	 * @param  mass  Mass with which is determined the molecular formula
	 */
	public MassToFormulaTool(double mass) {
		this(mass, max_Solutions, null);
	}
	
	/**
	 * Construct an instance of MassToFormulaTool, initialized with a mass.
	 * This constructor restricts the number maxim of elemental elements to be
	 * found.
	 * 
	 * @param  mass  Mass with which is determined the molecular formula
	 * @param  max_Solut    Number max of solutions
	 */
	public MassToFormulaTool(double mass, int max_solut) {
		this(mass, max_solut, null);
	}
	
	/**
	 * Construct an instance of MassToFormulaTool, initialized with a mass.
	 * This constructor restricts the elements to be found.
	 * 
	 * @param  mass            Mass with which is determined the molecular formula
	 * @param  elemToCondione  Elements that must be presents
	 */
	public MassToFormulaTool(double mass,  IElement[] elemToCondione) {
		this(mass, max_Solutions, elemToCondione);
	}
	/**
	 * Construct an instance of MassToFormulaTool, initialized with a mass.
	 * This constructor restricts the elements to be found and the charge.
	 * 
	 * @param  mass            Mass with which is determined the molecular formula
//	 * @param  charge          Charge of the molecule
	 * @param  elemToCondione  Elements that must be presents
	 */
//	public MassToFormulaTool(double mass,  int charge, IElement[] elemToCondione) {
//		this(mass, max_Solutions, charge, elemToCondione);
//	}
	/**
	 * Construct an instance of MassToFormulaTool, initialized with a mass.
	 * This constructor restricts the elements to be found and the charge.
	 * This constructor restricts the number maxim of elemental elements to be
	 * found.
	 * 
	 * @param  mass            Mass with which is determined the molecular formula
	 * @param  max_Solut       Number max of solutions to be found
//	 * @param  charge          Charge of the molecule
	 * @param  elemToCondione  Elements that must be presents
	 */
	public MassToFormulaTool(double mass, int max_Solut, IElement[] elemToCondione) {
		IElement_Nr[] elemToCondione_re = null;
		/*convert the IElement array to IElement_Nr which contains as default(0,9) the number of maximum
		 * and minimum the repetitions for each IElement.*/
		if(elemToCondione != null){
			elemToCondione_re = new IElement_Nr[elemToCondione.length];
			for(int i = 1; i < elemToCondione_re.length; i++)
				elemToCondione_re[i] = new IElement_Nr(elemToCondione[i].getSymbol(),0,9);
				
		}
		
		this.molecularFormula = analyseMF(mass, max_Solut, 0, ratio, elemToCondione_re);
	}
	/**
	 * Construct an instance of MassToFormulaTool, initialized with a mass.
	 * This constructor restricts the elements to be found and the charge.
	 * This constructor restricts the number maxim of elemental elements to be
	 * found.
	 * 
	 * @param  mass            Mass with which is determined the molecular formula
	 * @param  max_Solut       Number max of solutions to be found
	 * @param  charge          Charge of the molecule
	 * @param  ratio           Ratio between the mass of the molecular formula and the mass to apply
	 * @param  elemToCondione  Elements that must be presents
	 */
	public MassToFormulaTool(double mass, int max_Solut, int charge, double ratio, IElement_Nr[] elemToCondione) {
		this.molecularFormula = analyseMF(mass, max_Solut, charge, ratio, elemToCondione);
	}
	
	/**
	 * Method that actually does the work of extracting the molecular formula.
	 *
	 * @param  mass            molecular formula to create an AtomContainer from
	 * @param  max_Solut       Number max of solutions
	 * @param  charge          Charge of the molecule
	 * @param  elemToCondione  Elements that must be presents
	 * @param  ratio           Ratio between the mass of the molecular formula and the mass to apply
	 * @return                 the filled molecular formula as ArrayList
	 */
	private ArrayList<String> analyseMF(double m, int max_Solut, int charge, double rat, IElement_Nr[] elemToCondione) {

		ArrayList<String> solutions_found = new ArrayList<String>();
		
		if(m == 0.0){
			logger.error("Proposed mass is not a valid: ",mass);
			return null;
		}else
			mass = m;
		
		max_Solutions = max_Solut;
		ratio = rat;
		
		if(elemToCondione == null)
			elemToCond = generateElemDefault();
		else
			elemToCond = elemToCondione;
		
		/*put in order descendent the Elements according their mass*/
		IElement_Nr[] elemToCond_pro = elemToCond;
		IElement_Nr[] elemToCond_new = new IElement_Nr[elemToCond.length];
		int pos = 0;
		for (int i = 0; i < elemToCond.length ; i++){
			
			double valueMax = 0;
			int j_final = 0;
			for (int j = 0 ; j < elemToCond_pro.length; j++){
				if (elemToCond_pro[j] == null)
					continue;
				
				double atomicN = elemToCond_pro[j].getMajorIsotope().getExactMass();
				if (valueMax < atomicN){
					valueMax = atomicN;
					j_final = j;
				}
			}
			elemToCond_new[pos] = (IElement_Nr)elemToCond_pro[j_final];
			elemToCond_pro[j_final] = null;
			pos++;
			
		}
		
		for(int i = 0; i < elemToCond_new.length ; i++){
			
			/*if H is the last break*/
			if(elemToCond_new[i].getMyElement().getSymbol().equals("H"))
				break;
			
			int occurence1 = getMaxOccurence(mass, elemToCond_new[i].getMajorIsotope().getExactMass());

			int[] value_In = new int[elemToCond_new.length];
			for(int j= 1; j < elemToCond_new.length ; j++)
				value_In[j] = 0;
			
			value_In[i] = occurence1;
			for(int j = occurence1; j > 0; j--){
				
				int maxx = elemToCond_new[i].getOccurrenceMax();
				int minn = elemToCond_new[i].getOccurrenceMin();
				if(j < minn | maxx < j){
					value_In[i]--;
					continue;
				}
				
				for(int k = i+1; k < elemToCond_new.length ; k++)
					value_In[k] = 0;
				
				for(int k = i+1; k < elemToCond_new.length ; k++){
					value_In[k] = 0;
					double massT = calculateMassT(elemToCond_new,value_In);
					double diff = (mass - massT);
					int occurence2 = getMaxOccurence(diff,elemToCond_new[k].getMajorIsotope().getExactMass());
					if(occurence2 == 0){
						continue;
					}
					
					for(int s = occurence2; s > 0; s--){
						
						maxx = elemToCond_new[k].getOccurrenceMax();
						minn = elemToCond_new[k].getOccurrenceMin();
						if(s < minn | maxx < s){
							value_In[k]--;
							continue;
						}
						
						value_In[k] = s;
						double massTT = calculateMassT(elemToCond_new,value_In);
						diff = Math.abs(mass - Math.abs(massTT));
						if(diff < ratio){
							 if(isValiedMF(elemToCond_new,value_In)){ /*TODO*/
								 String myString = getFormulaString(elemToCond_new,value_In);
								 solutions_found.add(myString);
							 }
						
						}else{
							if(k == elemToCond_new.length-1)
								break;
							
							for(int l = k+1; l < elemToCond_new.length ; l++)
								value_In[l] = 0;
							
							for(int l = k+1; l < elemToCond_new.length ; l++){
								value_In[l] = 0;
								double massT3 = calculateMassT(elemToCond_new,value_In);
								double diff3 = (mass - massT3);
								int occurence3 = getMaxOccurence(diff3,elemToCond_new[l].getMajorIsotope().getExactMass());
								if(occurence3 == 0){
									continue;
								}
								
								for(int t = occurence3; t > 0; t--){
									maxx = elemToCond_new[l].getOccurrenceMax();
									minn = elemToCond_new[l].getOccurrenceMin();
									if(t < minn | maxx < t){
										value_In[l]--;
										continue;
									}
									
									value_In[l] = t;
									double massTT3 = calculateMassT(elemToCond_new,value_In);
									double diff4 = Math.abs(mass - Math.abs(massTT3));
									if(diff4 < ratio){
										 if(isValiedMF(elemToCond_new,value_In)){ 
											 /*******TODO***************************/
											 /*check if they have the correct valency
										      * http://www.sccj.net/publications/JCCJ/v3n3/a01/text.html*/
											 String myString = getFormulaString(elemToCond_new,value_In);
											 solutions_found.add(myString);
										 }
									
										value_In[l]--;	
									}else{
										if(l == elemToCond_new.length-1)
											break;
									}
								}
							}
						}
					}
					value_In[k]--;
				}
		
				value_In[i]--;
				
				
			}
		}
		
		return solutions_found;
	}
	/**
	 * Validation of the molecular formula.
	 * 
	 * @param elemToCond_new
	 * @param value_In
	 * @return
	 */
	private boolean isValiedMF(IElement_Nr[] elemToCond_new, int[] value_In) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Get the formula molecular as String from the the sum of the Elements
	 *   
	 * @param elemToCond_new
	 * @param value_In
	 * @return
	 */
	private String getFormulaString(IElement_Nr[] elemToCond_new, int[] value_In) {
		String result = "";
		for(int i = 0; i < elemToCond_new.length ; i++){
			if(value_In[i] != 0){
				result += elemToCond_new[i].getMyElement().getSymbol()+value_In[i];
			}
		}
		return result;
	}

	/**
	 * Calculate the mass total given the elements and their respective occurrences
	 * 
	 * @param elemToCond_new  The IElements to calculate
	 * @param value_In        The occurrences
	 * @return                The sum total
	 */
	private double calculateMassT(IElement_Nr[] elemToCond_new, int[] value_In) {
		double result = 0;
		for(int i = 0; i < elemToCond_new.length ; i++){
			if(value_In[i] != 0){
				result += elemToCond_new[i].getMajorIsotope().getExactMass()*value_In[i];
			}
		}
		return result;
	}

	/**
	 * calculate the occurrence of this Element
	 * @param element    The element to analyze
	 * @return           The occurrence
	 */
	private int getMaxOccurence(double massTo, double massIn) {
		int value = (int)((massTo+1)/massIn);
		return value;
	}
	/**
	 * generate all elements that will be present as default. They 
	 * are C, H, O and N.
	 * 
	 * @return The group of IElement_Nr as default
	 */
	private IElement_Nr[] generateElemDefault(){
		IElement_Nr[] elemDefault = new IElement_Nr[4];
		
		elemDefault[0] = new IElement_Nr("C",0,9);
		elemDefault[1] = new IElement_Nr("H",0,9);
		elemDefault[2] = new IElement_Nr("O",0,9);
		elemDefault[3] = new IElement_Nr("N",0,9);
		return elemDefault;
	}

	/**
	 * returns the exact mass used to calculate the molecular formula.
	 * 
	 * @return    The mass value
	 */
	public double getMass() {
		return mass;
	}
	
	/**
	 * Returns the complete set of Nodes, as implied by the molecular
	 * formula, including all the hydrogens.
	 *
	 * @return    The molecularFormula ArrayList
	 * @see       #getHTMLMolecularFormula()
	 */
	public ArrayList<String> getMolecularFormula() {
		return molecularFormula;
	}
	
	/**
	 * subclass of IElement which informs about the number 
	 * of maximum and minimum of repetitive elements that will be contained in the 
	 * molecular formula.
	 * 
	 * @author Miguel Rojas
	 *
	 */
	public class IElement_Nr{
		
		IElement myElement;
		int maxi = 0;
		int mini = 0;
		private IIsotope maxIsotop;
		/**
		 * Constructor of the ,0,9.
		 * 
		 * @param element The IElement object
		 * @param nim     The number of minimum occurrences of this IElement to look for
		 * @param max     The number of maximum occurrences of this IElement to look for
		 */
		public IElement_Nr(String symbol, int min, int max){

			
			try {
				IsotopeFactory ifac = IsotopeFactory.getInstance(builder);
				myElement = ifac.getElement(symbol);
				maxIsotop = ifac.getMajorIsotope(symbol);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			maxi = max;
			mini = min;
		}
		
		/**
			
		 * return the IElement
		 * 
		 * @return The IElement
		 */
		public IElement getMyElement(){
			return myElement;
		}
		/**
		 * return the number of maximum number of this Element to look for
		 * 
		 * @return The maximum value
		 */
		public int getOccurrenceMax(){
			return maxi;
		}
		/**
		 * return the number of minimum number of this Element to look for
		 * 
		 * @return The minimum value
		 */
		public int getOccurrenceMin(){
			return mini;
		}
		/**IElement_Nr element = new IElement_Nr("C",0,9);
		
		 * Returns the most abundant (major) isotope whose Element
		 * .
		 * @return The IIsotope value
		 */
		public IIsotope getMajorIsotope(){
			return maxIsotop;
		}
	}
	
	/**
	 * return all molecular formula but ordered from difference of the ratio between masses.
	 * 
	 * @return A ArrayList
	 */
	public ArrayList<String> getMoleculesFormulaOrned(){
		ArrayList<String> solutions_new = null;
		if(molecularFormula.size() != 0){
			
			ArrayList<String> solutions_pro = molecularFormula;
			solutions_new = new ArrayList<String>();
			for (int i = 0; i < molecularFormula.size() ; i++){
				
				double valueMax = 10;
				int j_final = 0;
				for (int j = 0 ; j < solutions_pro.size() ; j++){
					
					MFAnalyser mfa = new MFAnalyser((String)solutions_pro.get(j), new Molecule());
			    	try {
						double value = mfa.getNaturalMass();

						double diff = Math.abs(mass - Math.abs(value));
						if (valueMax > diff){
							valueMax = diff;
							j_final = j;
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
			    	
				}
				solutions_new.add(solutions_pro.get(j_final));
				solutions_pro.remove(j_final);
				
			}
		}
		return solutions_new;
	}
	
	
// IMPORTANT: below was the original code, which needs to be ported. It actually
// is code for an applet, and much can be deleted.
	
//	//
////  FormulaToMass  (c) G. Cottenceau 1997 
////
//
//import java.applet.*;
//import java.awt.*;
//import java.awt.*;
//import java.net.*;
//import java.io.*;
//import java.util.*;
//
//public class f2m2f extends Applet implements Runnable {
// 
//    SettingsFrame My_SettFrame;
//    About My_About;
//    Help My_Help;
//    AboutBenchmark My_About_Benchmark;
//
//  TextArea Saisie_Formula;
//  TextArea Saisie_Mass;
//  TextArea Results;
// 
//  Button My_Buttonf2m;
//  Button My_Buttonm2f;
//  Button My_Stop;
//  Button My_Help_Butt;
//  Button My_About_Butt;
//  Button My_Clear_FField;
//  Button My_Clear_MField;
//  Button My_Settings;
//  Button My_Benchmark;
//  Button My_Heavy_Benchmark;
//  Button My_About_Benchmark_Button;
// 
//  Panel About;
// 
//  Thread My_Thread;
//  boolean Flag_Do_m2f=false;
//  boolean Flag_Do_Benchmark=false;
//  boolean Flag_Do_Heavy_Benchmark=false;
// 
//  boolean gotFocus_Flag=false;
// 
//
//
//  double Masse_Molaire;             // 4 variables que renvoie Calculate_Masse
//  String element;
//  long qte_element;
//  boolean unknown_element;
// 
//  double Proposed_Mass;
//  String chaineaff;
// 
//  Vector source_vector;
// 
//  int Precision_Starter=1;
//  long Best_Results[][];
//  long Work_Result[];
//  long Mass_BResults[];
//  double Distance_Masse[];
//  int Solutions_Displayed=6;     // pair!
// 
//   private int Nb_Elements;
// 
//  boolean Stop_Flag=false;
// 
//  boolean Flag_Formula_Focus=false;
//  boolean Flag_Mass_Focus=false;
//  boolean Problem_Custom_File=false;
// 
//  boolean Flag_Deja_Benchmark=false;
// 
// 
//  public void init() {
// 
//    showStatus("Initializing Formula To Mass To Formula, please wait...");
//    System.out.println("Formula To Mass  (c) G. Cottenceau 1997");
// 
//   My_SettFrame=new SettingsFrame();
//   Nb_Elements=My_SettFrame.Nb_Elements;
// 
//    Best_Results=new long[Nb_Elements][Solutions_Displayed];
//    Mass_BResults=new long[Solutions_Displayed];
//    Work_Result=new long[Nb_Elements];
//    Distance_Masse=new double[Solutions_Displayed];
//   
//    My_Stop=new Button("Stop");
//    My_Help_Butt=new Button("Help");
//    My_About_Butt=new Button("About");
//    My_Settings=new Button("Settings applying to mass to formula calculation");
//    My_Clear_FField=new Button("Clear Formula");
//    My_Clear_MField=new Button("Clear Mass");
//    My_Benchmark=new Button("Benchmark");
//    My_Heavy_Benchmark=new Button("Heavy Bench");
//    My_About_Benchmark_Button=new Button("About Benchmark");
//    Saisie_Formula=new TextArea(1,40);
//    Saisie_Mass=new TextArea(1,30);
//    Results=new TextArea(3,100);
// 
//    setBackground(new Color(215,215,215));
//
//    Place_Objets();
// 
//    Image Mon_Image=getImage(getDocumentBase(),"me.jpg");
// 
//    MediaTracker tracker=new MediaTracker(this);
//    tracker.addImage(Mon_Image,0);
//
//    try {
//      tracker.waitForID(0);
//    } catch (InterruptedException e) { }
//
//    My_About=new About(Mon_Image);
//    My_Help=new Help();
//    My_About_Benchmark=new AboutBenchmark();
// 
//    String Name_Custom_File=getParameter("ADD_ELEMENTS");
//    try {
//      URL url=new URL(getDocumentBase(),Name_Custom_File);
//      InputStream stream=url.openStream();
//      DataInputStream fichier=new DataInputStream(stream);
//      source_vector=new Vector();
//      String s;
//      while ((s=fichier.readLine())!=null) {
//        source_vector.addElement(s);
//       }
//     } catch (Exception e) { Problem_Custom_File=true; }
// 
//    showStatus("Dealing with custom file...");
// 
//    if (!Problem_Custom_File)
//           Results.replaceText(My_SettFrame.firstinit(source_vector),0,Results.getText().length());
//      else Results.replaceText("Problem with customize file",0,Results.getText().length());
// 
//    Calculate_Masses_Custom();
// 
//  }
// 
// 
// public void Calculate_Masses_Custom() {
// 
//         // calcule les masses des elements custom definis par FORMULA
// 
//   int indice=0;
//   int indice2=108;
// 
//   while (indice<My_SettFrame.Nom_Elements_Cust.size()) {
// 
//     if (((String)My_SettFrame.Type_Codage_Masse.elementAt(indice)).compareTo("FORMULA")==0) {
//
//      Calculate_Mass((String) My_SettFrame.Masse_Elements_Cust.elementAt(indice));
// 
//      if (unknown_element) Results.replaceText("Unknown element in customize file : \""+element+"\"",0,Results.getText().length());
//           else My_SettFrame.Masse_Elements[indice2]=Masse_Molaire;
//         }
// 
//       else My_SettFrame.Masse_Elements[indice2]=Double.valueOf((String)My_SettFrame.Masse_Elements_Cust.elementAt(indice)).doubleValue();
//  
//     My_SettFrame.Nom_Elements[indice2]=(String) My_SettFrame.Nom_Elements_Cust.elementAt(indice);
//     My_SettFrame.Exist_Elements[indice2]=true;
// 
//     indice2++; 
//     indice++;
//    }
// }
// 
// 
// public void Place_Objets() {
// 
// 
//    setLayout(new GridLayout(5,1));
// 
//    My_Buttonf2m=new Button("Formula => Mass");
//    My_Buttonm2f=new Button("Mass => Formula");
// 
//
//      Panel Formula=new Panel();
//      Formula.add(new Label("Formula :"));
//      Formula.add(Saisie_Formula);
//    
//    add(Formula);
// 
//      Panel Tous_Boutons=new Panel();
//      Tous_Boutons.setLayout(new GridLayout(2,1));
// 
//        Panel LigneBout1=new Panel();
//
//        LigneBout1.add(My_Buttonf2m); 
//        LigneBout1.add(My_Buttonm2f);
//        LigneBout1.add(My_Stop);
//        LigneBout1.add(My_Clear_FField);
//        LigneBout1.add(My_Clear_MField);
// 
//      Tous_Boutons.add(LigneBout1);
// 
//        Panel LigneBout2=new Panel();
//        LigneBout2.add(My_Help_Butt);
//        LigneBout2.add(My_About_Butt);
//        LigneBout2.add(My_Benchmark);
//        LigneBout2.add(My_Heavy_Benchmark);
//        LigneBout2.add(My_About_Benchmark_Button);
//  
//      Tous_Boutons.add(LigneBout2);
// 
//    add(Tous_Boutons);
// 
//      Panel Mass=new Panel();
//      Mass.add(new Label("Mass :"));
//      Mass.add(Saisie_Mass);
//
//    add(Mass);
//   
//      Panel ButtonSettingsResTexte=new Panel();
//      ButtonSettingsResTexte.setLayout(new GridLayout(2,1));
// 
//        Panel NotToTakeAllWidth=new Panel();
// 
//        NotToTakeAllWidth.add(My_Settings);
// 
//      ButtonSettingsResTexte.add(NotToTakeAllWidth);     
//      ButtonSettingsResTexte.add(new Label("Results field :",Label.CENTER));
// 
//   add(ButtonSettingsResTexte);
// 
//   add(Results);
// 
// }
// 
// 
//  public void Do_Settings() {
//   
//     My_SettFrame.init(); 
//     My_SettFrame.show();
// 
//  }
// 
//  
//  public void start() {
//    if (My_Thread==null) {
//      My_Thread=new Thread(this);
//      My_Thread.start();
//     }
// 
//    showStatus("Ready");
// 
//   }
// 
// 
//  public void stop() {
//    if (My_Thread!=null) {
//      My_Thread.stop();
//      My_Thread=null;
//     }
//   }
// 
// 
//  public void run() {
// 
//    while (true) {
//      try {
//        My_Thread.sleep(100);
//         } catch (Exception e) {}
//      if (Flag_Do_m2f) { Flag_Do_m2f=false; Try_Do_m2f(); }
//      if (Flag_Do_Benchmark) { Flag_Do_Benchmark=false; Do_Benchmark(); }
//      if (Flag_Do_Heavy_Benchmark) { Flag_Do_Heavy_Benchmark=false; Do_Heavy_Benchmark(); }
//           }
// 
//   }
// 
// 
//  public void Do_Help() {
//
//     My_Help.init(); 
//     My_Help.show();
//
//   }
//
//
//  public void Do_About_Benchmark() {
//
//     My_About_Benchmark.init(); 
//     My_About_Benchmark.show();
//
//   }
//
//  public void Do_About() {
//
//     My_About.init(); 
//     My_About.show();
//
//   }
// 
// 
//  public boolean handleEvent(Event evt) {
// 
// 
//    if (evt.target==My_Buttonf2m && evt.id==Event.ACTION_EVENT) { 
//         Stop_Flag=false;
//         Do_f2m();
//         return true;
//       }
//          
//    if (evt.target==My_Buttonm2f && evt.id==Event.ACTION_EVENT) { 
//         Stop_Flag=false;
//         Flag_Do_m2f=true;
//         return true;
//       }
// 
//    if (evt.target==My_Clear_FField && evt.id==Event.ACTION_EVENT) {
//         Saisie_Formula.replaceText("",0,Saisie_Formula.getText().length());
//         return true;
//       }
// 
// 
//    if (evt.target==My_Clear_MField && evt.id==Event.ACTION_EVENT) {
//         Saisie_Mass.replaceText("",0,Saisie_Mass.getText().length());
//         return true;
//       }
// 
//    if (evt.target==My_Stop && evt.id==Event.ACTION_EVENT) {
//         Stop_Flag=true;
//         return true;
//       }
// 
//    if (evt.target==My_Help_Butt && evt.id==Event.ACTION_EVENT) {
//         Do_Help();
//         return true;
//       }
// 
//    if (evt.target==My_About_Benchmark_Button && evt.id==Event.ACTION_EVENT) {
//         Do_About_Benchmark(); 
//         return true;
//       }
// 
//    if (evt.target==My_Settings && evt.id==Event.ACTION_EVENT) {
//         Do_Settings();
//         return true;
//       }
// 
//    if (evt.target==My_Benchmark && evt.id==Event.ACTION_EVENT) {
//         Stop_Flag=false;
//         Flag_Do_Benchmark=true;
//         return true;
//       }
// 
//    if (evt.target==My_Heavy_Benchmark && evt.id==Event.ACTION_EVENT) {
//         Stop_Flag=false;
//         Flag_Do_Heavy_Benchmark=true;
//         return true;
//       }
// 
//    if (evt.target==My_About_Butt && evt.id==Event.ACTION_EVENT) {
//         Do_About();  
//         return true;
//       }
// 
//      return super.handleEvent(evt);
//    
//    }
// 
// 
//  public boolean gotFocus(Event evt,Object what) {
// 
// 
//      if (evt.target==Saisie_Formula) {
//           Flag_Formula_Focus=true; 
//           gotFocus_Flag=true;
//           return true;
//         }
//      if (evt.target==Saisie_Mass) {
//           Flag_Mass_Focus=true;
//           gotFocus_Flag=true;
//           return true;
//         }
// 
//      return super.gotFocus(evt,what);
// 
//    }
// 
//
//   public boolean lostFocus(Event evt,Object what) {
// 
//      if (evt.target==Saisie_Formula) {
//           Flag_Formula_Focus=false; 
//           return true;
//         }
//      if (evt.target==Saisie_Mass) {
//           Flag_Mass_Focus=false;
//           return true;
//         }
// 
//      return super.lostFocus(evt,what);
// 
//    }
// 
// 
//  public boolean keyDown(Event evt,int key) {
// 
//       if ((char) key=='\n') {
//                
//       if (Flag_Formula_Focus) {
//         Stop_Flag=false;
//         Do_f2m();
//         return true;
//           }
// 
//       if (Flag_Mass_Focus) {
//         Stop_Flag=false;
//         Flag_Do_m2f=true;
//         return true;
//           }
// 
//         return super.keyDown(evt,key);
//
//        }
// 
//      return super.keyDown(evt,key);
// 
//   }
// 
//
//  public void Calculate_Mass(String Texte_Formula) {
// 
//   long lis_nombre;
//   int indice=0;
//   boolean sort_det_nombre;
//   Masse_Molaire=0;
//   unknown_element=false;
// 
//  while (Texte_Formula.charAt(Texte_Formula.length()-1)==' ' ||
//         !Character.isLetterOrDigit(Texte_Formula.charAt(Texte_Formula.length()-1))
//        )
//          Texte_Formula=Texte_Formula.substring(0,Texte_Formula.length()-1);
//
//               // vazy!
//   while (indice<Texte_Formula.length() && !unknown_element) {
// 
//       element=Texte_Formula.substring(indice,indice+1);     // premiere lettre de l'element
//       indice++;
// 
//       if (indice!=Texte_Formula.length()) {
// 
//         if (Character.isLetter(Texte_Formula.charAt(indice))) { 
//                  if (Character.isLowerCase(Texte_Formula.charAt(indice))) {
//                     element+=Texte_Formula.substring(indice,indice+1);
//                     indice++;
//                                } else {
//                     qte_element=1;
//                                } 
//                 }
// 
//           } else qte_element=1;
// 
//       if (indice!=Texte_Formula.length()) {
// 
//         if (Character.isLetter(Texte_Formula.charAt(indice))) qte_element=1; else { 
//
//             qte_element=0;
//             while (indice!=Texte_Formula.length() && Character.isDigit(Texte_Formula.charAt(indice))) {
//                  qte_element=qte_element*10+Integer.valueOf(Texte_Formula.substring(indice,indice+1)).intValue();
//                  indice++;
//                }
// 
//           }
// 
//       } else qte_element=1;
// 
//       unknown_element=true;
//       for (int i=0;i<Nb_Elements;i++) {
//              if (element.compareTo(My_SettFrame.Nom_Elements[i])==0) 
//                        { Masse_Molaire+=My_SettFrame.Masse_Elements[i]*qte_element;
//                          unknown_element=false;
//                         }
//                                     }
//       if (unknown_element)
//         for (int i=0;i<My_SettFrame.Nom_Elements_Cust.size();i++) {
//              if (element.compareTo((String)My_SettFrame.Nom_Elements_Cust.elementAt(i))==0) 
//                        { Masse_Molaire+=(Double.valueOf((String)My_SettFrame.Masse_Elements_Cust.elementAt(i)).doubleValue())*qte_element;
//                          unknown_element=false;
//                         }
//                                     }
// 
//              }
// 
//   }
//
// 
//  public void Do_f2m() {
// 
//   Calculate_Mass(Saisie_Formula.getText());
//   
//   if (unknown_element) 
//	 { Results.replaceText("Unknown element : \""+element+"\"",0,Results.getText().length()); }
//    else { Results.replaceText("Mass : "+String.valueOf(Masse_Molaire),0,Results.getText().length()); 
//      	   Saisie_Mass.replaceText(String.valueOf(Masse_Molaire),0,Saisie_Mass.getText().length()); }
// } 
// 
// 
//  public int Get_Max_Occurence(int offsetelement) {
// 
//     String Elem=My_SettFrame.Nom_Elements[offsetelement];
// 
//     if (Elem.compareTo("H")==0) return My_SettFrame.Nb_H;
//      else
//      if (Elem.compareTo("C")==0) return My_SettFrame.Nb_C;
//      else
//      if (Elem.compareTo("O")==0) return My_SettFrame.Nb_O;
//      else return My_SettFrame.Nb_Autres;
//  
//    }
// 
// 
//  public void calculate_MaxForThisMass() {
// 
//          // calcule le tableau des maximum possible en se basant sur le fait que le nombre dans la molecule ne peut
//          // pas etre superieur au nombre max d'une molecule homogene
//
//     for (int i=0;i<Nb_Elements;i++)
//       if (My_SettFrame.Use_Elements[i]) {
//          int ValueElem=(int) Math.min(Math.floor(2*Proposed_Mass/My_SettFrame.Masse_Elements[i]),Get_Max_Occurence(i));
//          if (ValueElem==0) My_SettFrame.Use_Elements[i]=false; else My_SettFrame.MaxForThisMass[i]=ValueElem;
//         }
// 
//   }
// 
// 
//  public void Calculate_m2f() {
// 
//     long Variable_Progression=0;
//     long Max_Progression=1;
//     long Pourcentage_Progression=-1;
//     long New_Pourcentage_Progression;
//
//     
//     for (int i=0;i<Nb_Elements;i++)
//       if (My_SettFrame.Use_Elements[i]) Max_Progression*=My_SettFrame.MaxForThisMass[i]+1;
// 
//     for (int i=0;i<Nb_Elements;i++) 
//        for (int j=0;j<Solutions_Displayed;j++) 
//          Best_Results[i][j]=0;
//
//      
//              // calcul des meilleures approx    
//     double masse_un_elmt;
//     int indiceperso=0;
//  
//     for (int i=0;i<Nb_Elements;i++) Work_Result[i]=0;
//     for (int i=0;i<Solutions_Displayed;i++) Distance_Masse[i]=666667e66; 
//  
//     while (!My_SettFrame.Use_Elements[indiceperso] && indiceperso<Nb_Elements) indiceperso++;  // se place sur le premier
// 
//     while (indiceperso<Nb_Elements && !Stop_Flag) {
// 
//         masse_un_elmt=0;
//         for (int i=0;i<Nb_Elements;i++) { masse_un_elmt+=My_SettFrame.Masse_Elements[i]*Work_Result[i]; }
// 
//         indiceperso=0;     // mise en place des solutions
//         double distmass=Math.abs(masse_un_elmt-Proposed_Mass);
//         boolean sol_deja_mise=false;
//         while (indiceperso<Solutions_Displayed && !sol_deja_mise) {
//           if (distmass<Distance_Masse[indiceperso]) {
//                             // on translate les autres solutions
//                sol_deja_mise=true;
//                if (indiceperso<Solutions_Displayed-1) 
//                     for (int j=Solutions_Displayed-2;j>=indiceperso;j--) {
//                       Distance_Masse[j+1]=Distance_Masse[j];
//                       for (int i=0;i<Nb_Elements;i++) Best_Results[i][j+1]=Best_Results[i][j];
//                                                                         }
//                Distance_Masse[indiceperso]=distmass; 
//                for (int i=0;i<Nb_Elements;i++) Best_Results[i][indiceperso]=Work_Result[i];
//                 
//                                                     }
//           indiceperso++;
//          }
//
//         Variable_Progression++;
//         New_Pourcentage_Progression=(100*Variable_Progression)/Max_Progression;
//         if (New_Pourcentage_Progression!=Pourcentage_Progression) {
//              Pourcentage_Progression=New_Pourcentage_Progression;
//              Results.replaceText(
//                "Processing... "+String.valueOf(Pourcentage_Progression)+"%",0,Results.getText().length());
//                 }
//
//         indiceperso=0;
//         if (My_SettFrame.Use_Elements[0]) Work_Result[0]++; else Work_Result[0]=My_SettFrame.MaxForThisMass[0]+1;
// 
//         while (indiceperso<Nb_Elements && Work_Result[indiceperso]==My_SettFrame.MaxForThisMass[indiceperso]+1) {
//             Work_Result[indiceperso]=0; 
//             indiceperso++;
//             while (indiceperso<Nb_Elements && !My_SettFrame.Use_Elements[indiceperso]) indiceperso++;
//             if (indiceperso<Nb_Elements) Work_Result[indiceperso]++; 
//                                             }
//       }
// 
// 
//               // affichage
//    chaineaff="";
//      
//     for (int j=0;j<Solutions_Displayed;j++) {
//        double Mass=0;
//              // C en premier
//        if (Best_Results[31][j]!=0) {
//               chaineaff+=My_SettFrame.Nom_Elements[31];
//               if (Best_Results[31][j]!=1) chaineaff+=Best_Results[31][j];
//               Mass+=My_SettFrame.Masse_Elements[31]*Best_Results[31][j];
//                                    }
//       for (int i=0;i<Nb_Elements;i++) {
//        if (i!=31)  // ne pas repasser sur C
//         if (Best_Results[i][j]!=0) {
//               chaineaff+=My_SettFrame.Nom_Elements[i];
//               if (Best_Results[i][j]!=1) chaineaff+=Best_Results[i][j];
//               Mass+=My_SettFrame.Masse_Elements[i]*Best_Results[i][j];
//                                    }
//           }
//       chaineaff+=" (";
//       if (Mass-Proposed_Mass>0)  chaineaff+="+";         // mettre un + si c positif
//       String Une_Solution=String.valueOf(Mass-Proposed_Mass);
//               // trop de precision sur Communicator!
//       if (Une_Solution.length()>10) {
//               if (Une_Solution.indexOf('E')!=-1) 
//                  Une_Solution=Une_Solution.substring(0,Une_Solution.indexOf('E')-1)+"e"+Une_Solution.substring(Une_Solution.indexOf('E')+1);
//               if (Une_Solution.indexOf('e')==-1) Une_Solution=Une_Solution.substring(0,9);
//                    else Une_Solution=String.valueOf(
//                                      Math.pow(10,Double.valueOf(Une_Solution.substring(Une_Solution.indexOf('e')+1)).doubleValue())*
//                                      Double.valueOf(Une_Solution.substring(0,Math.min(9,Une_Solution.indexOf('e')-1))).doubleValue()
//                                                      );
//                                      }
//       chaineaff+=Une_Solution+")";
//       if (j!=Solutions_Displayed-1)  chaineaff+=" ; ";
//       if (j==Solutions_Displayed/2-1)  chaineaff+="\n";
//      }
// 
// 
//   } 
// 
// 
//
//  public void Do_m2f() {
// 
//    Calculate_m2f();
// 
//     if (Stop_Flag) {
//          Results.replaceText("Interrupted",0,Results.getText().length());
//                    } else {
//          Results.replaceText(chaineaff,0,Results.getText().length());
//          Results.replaceText(chaineaff.substring(0,1),0,1);       // ramene le debut visible sur PC!!
//                           }
//  
//  }
// 
// 
//  public String reduce_string(String tam) {
// 
//    if (tam.length()>8) tam=tam.substring(0,8);
// 
//    double sav=Double.valueOf(tam).doubleValue();
// 
//    while (tam.length()>0 && tam.charAt(tam.length()-1)=='0') tam=tam.substring(0,tam.length()-1);
//    if (tam.length()==0) tam="0";
//    if (tam.charAt(tam.length()-1)=='.') tam=tam.substring(0,tam.length()-1);
// 
//    if (sav!=Double.valueOf(tam).doubleValue()) return String.valueOf(sav); else return tam;
// 
// }
//
// 
//  public void Do_Benchmark() {
// 
//    int SavNb_H=My_SettFrame.Nb_H;
//    int SavNb_C=My_SettFrame.Nb_C;
//    int SavNb_O=My_SettFrame.Nb_O;
//    int SavNb_Autres=My_SettFrame.Nb_Autres;
// 
//    boolean SavUse_Elements[]=new boolean[116];
//    for (int i=0;i<116;i++) SavUse_Elements[i]=My_SettFrame.Use_Elements[i];
// 
//    My_SettFrame.Nb_H=16;
//    My_SettFrame.Nb_C=10;
//    My_SettFrame.Nb_O=8;
//    My_SettFrame.Nb_Autres=4;
// 
//    for (int i=0;i<116;i++) My_SettFrame.Use_Elements[i]=false;
// 
//    My_SettFrame.Use_Elements[0]=true;   // H
//    My_SettFrame.Use_Elements[31]=true;   // C
//    My_SettFrame.Use_Elements[32]=true;   // N
//    My_SettFrame.Use_Elements[33]=true;   // O
//    My_SettFrame.Use_Elements[36]=true;   // Na
// 
//    Proposed_Mass=667;
//    calculate_MaxForThisMass();
// 
//    long SaveMillis=System.currentTimeMillis();
// 
//    Calculate_m2f();
// 
//     double temps=((double)(System.currentTimeMillis()-SaveMillis))/1000;
//     chaineaff="Time for a basic calculation : "+reduce_string(String.valueOf(temps))+" secs";
// 
//     if (Stop_Flag) {
//          Results.replaceText("Interrupted",0,Results.getText().length());
//                    } else {
//          Results.replaceText(chaineaff,0,Results.getText().length());
//          Results.replaceText(chaineaff.substring(0,1),0,1);       // ramene le debut visible sur PC!!
//                           }
//  
//    My_SettFrame.Nb_H=SavNb_H;
//    My_SettFrame.Nb_C=SavNb_C;
//    My_SettFrame.Nb_O=SavNb_O;
//    My_SettFrame.Nb_Autres=SavNb_Autres;
//
//    for (int i=0;i<116;i++) My_SettFrame.Use_Elements[i]=SavUse_Elements[i];
// 
//  }
//
// 
//  public void Do_Heavy_Benchmark() {
// 
//    int SavNb_H=My_SettFrame.Nb_H;
//    int SavNb_C=My_SettFrame.Nb_C;
//    int SavNb_O=My_SettFrame.Nb_O;
//    int SavNb_Autres=My_SettFrame.Nb_Autres;
// 
//    boolean SavUse_Elements[]=new boolean[116];
//    for (int i=0;i<116;i++) SavUse_Elements[i]=My_SettFrame.Use_Elements[i];
// 
//    My_SettFrame.Nb_H=16;
//    My_SettFrame.Nb_C=10;
//    My_SettFrame.Nb_O=8;
//    My_SettFrame.Nb_Autres=7;
// 
//    for (int i=0;i<116;i++) My_SettFrame.Use_Elements[i]=false;
// 
//    My_SettFrame.Use_Elements[0]=true;   // H
//    My_SettFrame.Use_Elements[31]=true;   // C
//    My_SettFrame.Use_Elements[32]=true;   // N
//    My_SettFrame.Use_Elements[33]=true;   // O
//    My_SettFrame.Use_Elements[36]=true;   // Na
//    My_SettFrame.Use_Elements[37]=true;   // Mg
// 
//    Proposed_Mass=667;
//    calculate_MaxForThisMass();
// 
//    long SaveMillis=System.currentTimeMillis();
// 
//    Calculate_m2f();
// 
//     double temps=((double)(System.currentTimeMillis()-SaveMillis))/1000;
//     chaineaff="Time for a basic calculation : "+reduce_string(String.valueOf(temps))+" secs";
// 
//     if (Stop_Flag) {
//          Results.replaceText("Interrupted",0,Results.getText().length());
//                    } else {
//          Results.replaceText(chaineaff,0,Results.getText().length());
//          Results.replaceText(chaineaff.substring(0,1),0,1);       // ramene le debut visible sur PC!!
//                           }
//  
//    My_SettFrame.Nb_H=SavNb_H;
//    My_SettFrame.Nb_C=SavNb_C;
//    My_SettFrame.Nb_O=SavNb_O;
//    My_SettFrame.Nb_Autres=SavNb_Autres;
//
//    for (int i=0;i<116;i++) My_SettFrame.Use_Elements[i]=SavUse_Elements[i];
// 
//  }
//
//  
//  public void Try_Do_m2f() {
// 
//     String Errorstring="No error";
//     boolean Error_m2f=false;
//   
//     try {
//       Proposed_Mass=Double.valueOf(Saisie_Mass.getText()).doubleValue();
//           }
//         catch (Exception e) 
//            { Errorstring="Proposed mass is not a valid floating point number";
//              Error_m2f=true;
//              }
// 
//     if (!Error_m2f) {
// 
//       boolean au_moins_un=false;
//       for (int i=0;i<Nb_Elements;i++) {
//             My_SettFrame.Use_Elements[i]=My_SettFrame.element_boxes[i].getState();
//             if (My_SettFrame.Use_Elements[i]) au_moins_un=true;
//                                  }
// 
//       if (!au_moins_un) {
//         Errorstring="Enable at least one element!";
//         Error_m2f=true;
//            }
// 
//       calculate_MaxForThisMass();
// 
//       if (!Error_m2f) {
//
//         au_moins_un=false;
//         for (int i=0;i<Nb_Elements;i++) if (My_SettFrame.Use_Elements[i]) au_moins_un=true;
//   
//         if (!au_moins_un) {
//           Errorstring="Mass is not valid";
//           Error_m2f=true;
//              }
//        }
// 
//      }
//
//     if (Error_m2f) Results.replaceText(Errorstring,0,Results.getText().length());
//        else {
//           Do_m2f();
//          }
//    }
// 
// }  

	
}
