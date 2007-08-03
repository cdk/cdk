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

/**
 * Tool to determine molecular formula consistent with a given accurate mass.
 * Originally developed by Guillaume Cottenceau and Henry Rzepa.
 * 
 * @author     Guillaume Cottenceau
 * @author     Henry Rzepa
 * @cdk.module experimental
 */
public class MassToFormulaTool {

// IMPORTANT: below is the original code, which needs to be ported. It actually
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
