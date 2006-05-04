/*
 *  Copyright (C) 2002-2006  The Jmol Development Team
 *
 *  Contact: jmol-developers@lists.sf.net
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
 */

package org.openscience.cdk.applications.jchempaint.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.PropertyResourceBundle;
import java.util.StringTokenizer;

import javax.swing.JApplet;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.applications.jchempaint.JCPPropertyHandler;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintPanel;
import org.openscience.cdk.applications.swing.JExternalFrame;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.tools.manipulator.SetOfAtomContainersManipulator;
import org.openscience.cdk.tools.manipulator.SetOfMoleculesManipulator;

/**
 * The
 * 
 * @cdk.module jchempaint.applet
 * @author dirk49
 * @cdk.created 22. April 2005
 */
public abstract class JChemPaintAbstractApplet extends JApplet {
	private JChemPaintPanel theJcpp = null;
	private JChemPaintModel theModel = null;
	private String localeString = null;
	private PropertyResourceBundle appletProperties = null;
	JExternalFrame jexf = null;

	private static String appletInfo = "JChemPaint Applet. See http://cdk.sourceforge.net "
			+ "for more information";

	private static String[][] paramInfo = {
			{ "background", "color", 	"Background color as integer" },
      { "atomNumbersVisible", "true or false", "should atom numbers be shown"},
			{ "load", "url", "URL of the chemical data" },
			{ "detachable", "true or false", "should applet be detachable by double click"},};

	public String getAppletInfo() {
		return appletInfo;
	}

	public String[][] getParameterInfo() {
		return paramInfo;
	}

//	private String getValue(String propertyName, String defaultValue) {
//		String stringValue = getParameter(propertyName);
//		if (stringValue != null)
//			return stringValue;
//		if (appletProperties != null) {
//			try {
//				stringValue = appletProperties.getString(propertyName);
//				return stringValue;
//			} catch (MissingResourceException ex) {
//			}
//		}
//		return defaultValue;
//	}
//
//	private int getValue(String propertyName, int defaultValue) {
//		String stringValue = getValue(propertyName, null);
//		if (stringValue != null)
//			try {
//				return Integer.parseInt(stringValue);
//			} catch (NumberFormatException ex) {
//				System.out.println(propertyName + ":" + stringValue
//						+ " is not an integer");
//			}
//		return defaultValue;
//	}
//
//	private double getValue(String propertyName, double defaultValue) {
//		String stringValue = getValue(propertyName, null);
//		if (stringValue != null)
//			try {
//				return (new Double(stringValue)).doubleValue();
//			} catch (NumberFormatException ex) {
//				System.out.println(propertyName + ":" + stringValue
//						+ " is not a floating point number");
//			}
//		return defaultValue;
//	}

	public void initPanelAndModel(JChemPaintPanel jcpp) {
		getContentPane().removeAll();
		getContentPane().setLayout(new BorderLayout());
		theModel.setTitle("JCP Applet" /* getNewFrameName() */);
    	try{
    		theModel.setAuthor(JCPPropertyHandler.getInstance().getJCPProperties().getProperty("General.UserName"));
    	}catch(NullPointerException ex){
    		//It seems we get an npe here sometimes. the line is not necessary
    	}
		// Package self = Package.getPackage("org.openscience.cdk.applications.jchempaint");
		// String version = self.getImplementationVersion();
		// model.setSoftware("JChemPaint " + version);
		theModel.setSoftware("JChemPaint " /*+  version */);
		theModel.setGendate((Calendar.getInstance()).getTime().toString());
		jcpp.setJChemPaintModel(theModel);
		jcpp.registerModel(theModel);
		if(getParameter("detachable")!=null && getParameter("detachable").equals("true"))
			jcpp.addFilePopUpMenu();		
		if(getParameter("compact")!=null && getParameter("compact").equals("true")){
			theModel.getRendererModel().setIsCompact(true);
		}
		if(getParameter("tooltips")!=null){
			StringTokenizer st=new StringTokenizer(getParameter("tooltips"),"|");
			while(st.hasMoreTokens()){
				StringTokenizer st2=new StringTokenizer(st.nextToken(),"\\");
				IAtom atom=SetOfAtomContainersManipulator.getAllInOneContainer(theModel.getChemModel().getSetOfMolecules()).getAtomAt(Integer.parseInt(st2.nextToken())-1);
				theModel.getRendererModel().getToolTipTextMap().put(atom,st2.nextToken());
			}
			theModel.getRendererModel().setShowTooltip(true);
		}
		if(theJcpp.getJChemPaintModel()!=null){
			jcpp.scaleAndCenterMolecule(theModel.getChemModel(),new Dimension((int)this.getSize().getWidth()-100,(int)this.getSize().getHeight()-100));
		if(theModel.getChemModel().getSetOfMolecules()!=null){
	        int smallestX=Integer.MAX_VALUE;
	        int largestX=Integer.MIN_VALUE;
	        int smallestY=Integer.MAX_VALUE;
	        int largestY=Integer.MIN_VALUE;
	        for(int i=0;i<theModel.getChemModel().getSetOfMolecules().getMolecules().length;i++){
	          for(int k=0;k<theModel.getChemModel().getSetOfMolecules().getMolecule(i).getAtomCount();k++){
	            if(theModel.getChemModel().getSetOfMolecules().getMolecule(0).getAtomAt(k).getPoint2d().x<smallestX)
	              smallestX=(int)theModel.getChemModel().getSetOfMolecules().getMolecule(0).getAtomAt(k).getPoint2d().x;
	            if(theModel.getChemModel().getSetOfMolecules().getMolecule(0).getAtomAt(k).getPoint2d().x>largestX)
	              largestX=(int)theModel.getChemModel().getSetOfMolecules().getMolecule(0).getAtomAt(k).getPoint2d().x;
	            if(theModel.getChemModel().getSetOfMolecules().getMolecule(0).getAtomAt(k).getPoint2d().y<smallestY)
	              smallestY=(int)theModel.getChemModel().getSetOfMolecules().getMolecule(0).getAtomAt(k).getPoint2d().y;
	            if(theModel.getChemModel().getSetOfMolecules().getMolecule(0).getAtomAt(k).getPoint2d().y>largestY)
	              largestY=(int)theModel.getChemModel().getSetOfMolecules().getMolecule(0).getAtomAt(k).getPoint2d().y;
	          }
	        }
	        if(!theModel.getRendererModel().getIsCompact()){
		        int x=largestX-smallestX+30;
		        int y=largestY - smallestY+30;
		        if(x<300)
		          x=300;
		        if(y<300)
		          y=300;
		        theModel.getRendererModel().setBackgroundDimension(new Dimension(x,y));
		        jcpp.scaleAndCenterMolecule(theModel.getChemModel(),theModel.getRendererModel().getBackgroundDimension());
	        }else{
	        	theModel.getRendererModel().setBackgroundDimension(new Dimension((int)this.getSize().getWidth()-100,(int)this.getSize().getHeight()-100));
	        	IAtomContainer atomContainer=SetOfAtomContainersManipulator.getAllInOneContainer(theModel.getChemModel().getSetOfMolecules());
	    		GeometryTools.translateAllPositive(atomContainer,theModel.getRendererModel().getRenderingCoordinates());
	    		GeometryTools.scaleMolecule(atomContainer, theModel.getRendererModel().getBackgroundDimension(), 0.8,theModel.getRendererModel().getRenderingCoordinates());			
	    		GeometryTools.center(atomContainer, theModel.getRendererModel().getBackgroundDimension(),theModel.getRendererModel().getRenderingCoordinates());

	        }
	      }
	    }
		//embedded means that additional instances can't be created, which is
		// needed for applet as well
		jcpp.setEmbedded();
		getContentPane().add(jcpp, BorderLayout.CENTER);
	}

	// Code for both loadModel methods taken from JCPCDK applet
	
	/**
	 * @param theModel
	 */
	protected void loadModelFromParam() {
		URL fileURL = null;
		try {
			URL documentBase = getDocumentBase();
			String load = getParameter("load");
      if (load != null)
				fileURL = new URL(documentBase, load);
    } catch (Exception exception) {
			System.out.println("Cannot load model: " + exception.toString());
			exception.printStackTrace();
		}
		loadModelFromUrl(fileURL);
	}

	/**
	 * @param fileURL
	 */
	public void loadModelFromUrl(URL fileURL) {
		if (fileURL != null) {
			try {
				InputStreamReader isReader = new InputStreamReader(fileURL.openStream());
				IChemObjectReader reader = new ReaderFactory().createReader(isReader);
				ChemModel chemModel = (ChemModel) reader.read(new ChemModel());
        
        int count=0;
        for(int i=0;i<chemModel.getSetOfMolecules().getMolecules().length;i++){
          for(int k=0;k<chemModel.getSetOfMolecules().getMolecules()[i].getAtomCount();k++){
            chemModel.getSetOfMolecules().getMolecules()[i].getAtomAt(k).setProperty("OriginalNumber", new Integer(count));
            count++;
          }
        }
        theModel = new JChemPaintModel(chemModel);
			} catch (Exception exception) {
				System.out.println("Cannot parse model: " + exception.toString());
				exception.printStackTrace();
			}
		}else{
      theModel=new JChemPaintModel();
    }
		initPanelAndModel(theJcpp);
	}
	
	public void start() {
		//Parameter parsing goes here
    loadModelFromParam();
		String atomNumbers=getParameter("atomNumbersVisible");
    if(atomNumbers!=null){
      if(atomNumbers.equals("true"))
        theJcpp.getJChemPaintModel().getRendererModel().setDrawNumbers(true);
    }
    String background = getParameter("background");
    if(background!=null){
      theJcpp.getJChemPaintModel().getRendererModel().setBackColor(new Color(Integer.parseInt(background)));
    }
	}
	
	public void init(){
		prepareExternalFrame();
	}

	public void stop() {
	}
	/**
	 * @return Returns the theJcpp.
	 */
	public JChemPaintPanel getTheJcpp() {
		return theJcpp;
	}
	/**
	 * @param theJcpp The theJcpp to set.
	 */
	public void setTheJcpp(JChemPaintPanel theJcpp) {
		this.theJcpp = theJcpp;
	}
  
  public void setTheModel(JChemPaintModel theModel){
    this.theModel=theModel;
  }
  
  public String getMolFile() throws Exception{
    StringWriter sw = new StringWriter();
    MDLWriter mdlwriter = new MDLWriter(sw);
    mdlwriter.dontWriteAromatic();
    Molecule all=new Molecule((AtomContainer)SetOfMoleculesManipulator.getAllInOneContainer(theJcpp.getJChemPaintModel().getChemModel().getSetOfMolecules()));
    mdlwriter.write(all);
    return(sw.toString());
  }

  /**
   * This method replaces all \n characters with the system line separator. This can be used when setting a mol file in an applet
   * without knowing which platform the applet is running on.
   * 
   * @param mol The mol file to set
   * @throws Exception
   */
  public void setMolFileWithReplace(String mol) throws Exception{
	StringBuffer newmol=new StringBuffer();
    int s = 0;
    int e = 0;
    while ((e = mol.indexOf("\\n", s)) >= 0) {
      newmol.append(mol.substring(s, e));
      newmol.append(System.getProperty("file.separator"));
      s = e + 1;
    }
    newmol.append(mol.substring(s));
    theJcpp.showChemFile(new StringReader(newmol.toString()));
    repaint();
  }
  
  public void setMolFile(String mol) throws Exception{
    theJcpp.showChemFile(new StringReader(mol));
    repaint();
  }

  
  public void clear() throws Exception{
	  theModel.getChemModel().setSetOfMolecules(new SetOfMolecules());
	  repaint();
  }

  public void selectAtom(int atom){
    theJcpp.getJChemPaintModel().getRendererModel().setExternalHighlightColor(Color.RED);
    IAtomContainer ac=theJcpp.getJChemPaintModel().getChemModel().getSetOfMolecules().getBuilder().newAtomContainer();
    ac.addAtom(theJcpp.getJChemPaintModel().getChemModel().getSetOfMolecules().getMolecules()[0].getAtomAt(atom));
    theJcpp.getJChemPaintModel().getRendererModel().setExternalSelectedPart(ac);
    getTheJcpp().repaint();
  }

	/**
	 * @return Returns the jexf.
	 */
	private JExternalFrame getJexf() {
		if (jexf == null)
			jexf = new JExternalFrame();
		return jexf;
	}

	/**
	 * sets title for external frame
	 * adds listener for double clicks in order to open external frame
	 */
	private void prepareExternalFrame() { 
		if (this.getParameter("name") != null)
			getJexf().setTitle(this.getParameter("name"));
		getTheJcpp().getDrawingPanel().addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 1 && e.getClickCount() == 2)
					if (!getJexf().isShowing()) {
						getJexf().show(getTheJcpp());
				}	
			}
		});
	}
}
