/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) Project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.applications.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.openscience.cdk.Element;
import org.openscience.cdk.PeriodicTableElement;
import org.openscience.cdk.config.ElementPTFactory;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.tools.LoggingTool;
/**
 * JPanel version of the periodic system.
 *
 * @author        Egon Willighagen
 * @author        Geert Josten
 * @author        Miguel Rojas
 * @cdk.created   May 9, 2005
 * @cdk.module    applications
 * @cdk.require   swing
 */
public class PeriodicTablePanel extends JPanel
{
	Vector listeners = null;
	PeriodicTableElement selectedElement = null;
	
	private JPanel panel;
	private JLabel label;
	private JLayeredPane layeredPane;
	
	private ElementPTFactory factory;
	private LoggingTool logger;
	
	public static int APPLICATION = 0;
	/*default*/
	public static int JCP = 1;
	/* 
	 * set if the button should be written with html - which takes 
	 * too long time for loading
	 * APPLICATION = with html
	 * JCP = default
	 */ 	
	private int controlViewerButton;
	
	/**
	*  Constructor of the PeriodicTablePanel object
	*/
	public PeriodicTablePanel()
	{
		super();
		setLayout( new BorderLayout());
		try {
			factory = ElementPTFactory.getInstance();
		} catch (Exception ex1) 
		{
			logger.error(ex1.getMessage());
			logger.debug(ex1);
		}
		layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(611, 575));
		layeredPane.setBorder(BorderFactory.createTitledBorder(
                                    "Periodic Table for CDK"));
		JPanel tp = PTPanel();
		tp.setBounds(8,85,600, 480);
		
		JButton button = new JButton("Reload");
		button.setVerticalTextPosition(AbstractButton.BOTTOM);
		button.setHorizontalTextPosition(AbstractButton.CENTER);
		button.setMnemonic(KeyEvent.VK_R);
		button.setToolTipText("Click this button to back to PeriodicTable.");
		button.setFont(new Font("Times-Roman",Font.BOLD, 10));
		button.setBounds(510, 20, 90, 20);
		button.addActionListener( new BackAction() );
		panel = CreateLabelProperties(null);
		
		layeredPane.add(button, new Integer(1));
		layeredPane.add(tp, new Integer(0));
		layeredPane.add(panel, new Integer(1));
		add(layeredPane);
	}
	
	private JPanel PTPanel()
	{

		controlViewerButton = PeriodicTablePanel.JCP;
		JPanel panel = new JPanel();
		listeners = new Vector();
		panel.setLayout(new GridLayout(0, 18));
		
		//--------------------------------
		JButton butt = new JButton("IA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		//--------------------------------
		for (int i = 0; i < 16; i++)
		{
			Box.createHorizontalGlue();
			panel.add(Box.createHorizontalGlue());
		}
		butt = new JButton("VIIIA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		
		panel.add(createButton("H"));
		
		butt = new JButton("IIA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		for (int i = 0; i < 10; i++)
		{
			panel.add(Box.createHorizontalGlue());
		}
		butt = new JButton("IIIA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		
		butt = new JButton("VIA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		
		butt = new JButton("VA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		
		butt = new JButton("VIA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		
		butt = new JButton("VIIA");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		//
		
		panel.add(createButton("He"));
		
		panel.add(createButton("Li"));
		
		panel.add(createButton("Be"));
		for (int i = 0; i < 10; i++)
		{
			panel.add(Box.createHorizontalGlue());
		}
		//no metall
		panel.add(createButton("B"));
		panel.add(createButton("C"));
		panel.add(createButton("N"));
		panel.add(createButton("O"));
		panel.add(createButton("F"));
		//
		panel.add(createButton("Ne"));
		
		panel.add(createButton("Na"));
		panel.add(createButton("Mg"));
		
		butt = new JButton("IIIB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("IVB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("VB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("VIB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("VIIB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("--");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("VIIIB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("--");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("IB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		butt = new JButton("IIB");
		butt.setBorder(new EmptyBorder(2,2,2,2));
		panel.add(butt);
		//no metall
		panel.add(createButton("Al"));
		panel.add(createButton("Si"));
		panel.add(createButton("P"));
		panel.add(createButton("S"));
		panel.add(createButton("Cl"));
		//
		panel.add(createButton("Ar"));
		
		panel.add(createButton("K"));
		panel.add(createButton("Ca"));
		//transition
		panel.add(createButton("Sc"));
		panel.add(createButton("Ti"));
		panel.add(createButton("V"));
		panel.add(createButton("Cr"));
		panel.add(createButton("Mn"));
		panel.add(createButton("Fe"));
		panel.add(createButton("Co"));
		panel.add(createButton("Ni"));
		panel.add(createButton("Cu"));
		panel.add(createButton("Zn"));
		//no metall
		panel.add(createButton("Ga"));
		panel.add(createButton("Ge"));
		panel.add(createButton("As"));
		panel.add(createButton("Se"));
		panel.add(createButton("Br"));
		//
		panel.add(createButton("Kr"));
		
		panel.add(createButton("Rb"));
		panel.add(createButton("Sr"));
		//transition
		panel.add(createButton("Y"));
		panel.add(createButton("Zr"));
		panel.add(createButton("Nb"));
		panel.add(createButton("Mo"));
		panel.add(createButton("Tc"));
		panel.add(createButton("Ru"));
		panel.add( createButton("Rh"));
		panel.add(createButton("Pd"));
		panel.add(createButton("Ag"));
		panel.add(createButton("Cd"));
		//no metall
		panel.add(createButton("In"));
		panel.add(createButton("Sn"));
		panel.add(createButton("Sb"));
		panel.add(createButton("Te"));
		panel.add(createButton("I"));
		//
		panel.add(createButton("Xe"));
		
		panel.add(createButton("Cs"));
		panel.add(createButton("Ba"));
		//transition
		panel.add(createButton("La"));
		panel.add(createButton("Hf"));
		panel.add(createButton("Ta"));
		panel.add(createButton("W"));
		panel.add(createButton("Re"));
		panel.add(createButton("Os"));
		panel.add(createButton("Ir"));
		panel.add(createButton("Pt"));
		panel.add(createButton("Au"));
		panel.add(createButton("Hg"));
		//no metall
		panel.add(createButton("Tl"));
		panel.add(createButton("Pb"));
		panel.add(createButton("Bi"));
		panel.add(createButton("Po"));
		panel.add(createButton("At"));
		//
		panel.add(createButton("Rn"));
		
		panel.add(createButton("Fr"));
		panel.add(createButton("Ra"));
		//transition
		panel.add(createButton("Ac"));
		panel.add(createButton("Rf"));
		panel.add(createButton("Db"));
		panel.add(createButton("Sg"));
		panel.add(createButton("Bh"));
		panel.add(createButton("Hs"));
		panel.add(createButton("Mt"));
		panel.add(createButton("Ds"));
		panel.add(createButton("Rg"));
		for (int i = 0; i < 9; i++)
		{
			panel.add(Box.createHorizontalGlue());
		}
		//Acti
		panel.add(createButton("Ce"));
		panel.add(createButton("Pr"));
		panel.add(createButton("Nd"));
		panel.add(createButton("Pm"));
		panel.add(createButton("Sm"));
		panel.add(createButton("Eu"));
		panel.add(createButton("Gd"));
		panel.add(createButton("Tb"));
		panel.add(createButton("Dy"));
		panel.add(createButton("Ho"));
		panel.add(createButton("Er"));
		panel.add(createButton("Tm"));
		panel.add(createButton("Yb"));
		panel.add(createButton("Lu"));
		for (int i = 0; i < 4; i++)
		{
			panel.add(Box.createHorizontalGlue());
		}
		//Lacti
		panel.add( createButton("Th"));
		panel.add(createButton("Pa"));
		panel.add(createButton("U"));
		panel.add(createButton("Np"));
		panel.add(createButton("Pu"));
		panel.add(createButton("Am"));
		panel.add(createButton("Cm"));
		panel.add(createButton("Bk"));
		panel.add(createButton("Cf"));
		panel.add(createButton("Es"));
		panel.add(createButton("Fm"));
		panel.add(createButton("Md"));
		panel.add(createButton("No"));
		panel.add(createButton("Lr"));
		//End
		panel.setVisible(true);
		return panel;
	    }
	    
	/**
	* create button. Difine the color of the font and background
	*
	*@param elementS  String of the element
	*@return button   JButton
	*/
	private JButton createButton(String elementS)
	{
		PeriodicTableElement element = factory.configure(new PeriodicTableElement(elementS));
		String colorFS = "000000";
		Color colorF = new Color(0,0,0);
		String colorPh = element.getPhase();
		if(colorPh.equals("Solid")){
			colorFS = "000000"; 
			colorF = new Color(0,0,0);
		}
		else if(colorPh.equals("Gas")){
			colorFS = "CC0033"; 
			colorF = new Color(200,0,0);
		}
		else if(colorPh.equals("Liquid")){
			colorFS = "3300CC"; 
			colorF = new Color(0,0,200);
		}
		else if(colorPh.equals("Synthetic")){
			colorFS = "FFCC00";
			colorF = new Color(235,208,6);
		}
		
		Color colorB = null;
		String serie = element.getChemicalSerie();
		if(serie.equals("Noble Gasses"))
			colorB = new Color(255,153,255);
		else if(serie.equals("Halogens"))
			colorB = new Color(255,153,153); 
		else if(serie.equals("Nonmetals"))
			colorB = new Color(255,152,90);
		else if(serie.equals("Metalloids"))
			colorB = new Color(255,80,80);
		else if(serie.equals("Metals"))
			colorB = new Color(255,50,0);
		else if(serie.equals("Alkali Earth Metals"))
			colorB = new Color(102,150,255);
		else if(serie.equals("Alkali Metals"))
			colorB = new Color(130,130,255);
		else if(serie.equals("Transition metals"))
			colorB = new Color(255,255,110);
		else if(serie.equals("Lanthanides"))
			colorB = new Color(255,255,150);
		else if(serie.equals("Actinides"))
			colorB = new Color(255,255,200);
		
		JButton button = new ElementButton(element, new ElementButtonAction(), getTextButton(element,colorFS), colorF);
		button.setBackground(colorB);
		
		return button;
	}
	/**
	 *  Sets the selectedElement attribute of the PeriodicTablePanel object
	 *
	 *@param  selectedElement  The new selectedElement value
	 */
	public void setSelectedElement(PeriodicTableElement selectedElement)
	{
		this.selectedElement = selectedElement;
	}


	/**
	 *  Gets the selectedElement attribute of the PeriodicTablePanel object
	 *
	 *@return    The selectedElement value
	 */
	public Element getSelectedElement()
	{
		return PeriodicTableElement.configure(selectedElement);
	}


	/**
	 *  Adds a change listener to the list of listeners
	 *
	 *@param  listener  The listener added to the list
	 */

	public void addCDKChangeListener(ICDKChangeListener listener)
	{
		listeners.add(listener);
	}


	/**
	 *  Removes a change listener from the list of listeners
	 *
	 *@param  listener  The listener removed from the list
	 */
	public void removeCDKChangeListener(ICDKChangeListener listener)
	{
		listeners.remove(listener);
	}


	/**
	 *  Notifies registered listeners of certain changes that have occurred in this
	 *  model.
	 */
	public void fireChange()
	{
		EventObject event = new EventObject(this);
		for (int i = 0; i < listeners.size(); i++)
		{
			((ICDKChangeListener) listeners.get(i)).stateChanged(event);
		}
	}

	/**
	 * get the format which the text will be introduce into the button
	 * 
	 * @param element The PeriodicTableElement
	 * @return the String to show
	 */
	public String getTextButton(PeriodicTableElement element, String color){
		String buttonString = null;
		switch (controlViewerButton) {
			case 0: buttonString ="<html><p><u><FONT SIZE=-2>"+element.getAtomicNumber()+"</FONT></u></p><p><font COLOR="+color+">"
			+element.getSymbol()+"<font></p></html>";break;
			case 1: buttonString = element.getSymbol();break;
			default: buttonString = element.getSymbol();break;
		}
		return buttonString;
	}


	/**
	 *  Description of the Class
	 *
	 *@author     steinbeck
	 *@cdk.created    February 10, 2004
	 */
	public class ElementButtonAction extends AbstractAction
	{
		/**
		 *  Description of the Method
		 *
		 *@param  e  Description of the Parameter
		 */
		public void actionPerformed(ActionEvent e)
		{
			ElementButton button = (ElementButton) e.getSource();
			setSelectedElement(button.getElement());
			
			layeredPane.remove(panel);
			panel = CreateLabelProperties(button.getElement());
			layeredPane.add(panel, new Integer(1));
			layeredPane.repaint();
			
			fireChange();
		}
	}
	/**
	 * This action fragment a molecule which is on the frame JChemPaint
	 *
	 */
	 class BackAction extends AbstractAction 
	 {
		 /**
		 *  Description of the Method
		 *
		 * @param  e  Description of the Parameter
		 *
		 */
		 public void actionPerformed(ActionEvent e)
		 {
			 layeredPane.remove(panel);
			 panel = CreateLabelProperties(null);
			 layeredPane.add(panel, new Integer(1));
			 layeredPane.repaint();
		 }
	 }
	 
	 /**
	 *  Description of the Class
	 *
	 *@author     steinbeck
	 *@cdk.created    February 10, 2004
	 */
	 class ElementButton extends JButton
	 {

		private PeriodicTableElement element;


		/**
		 *  Constructor for the ElementButton object
		 *
		 *@param  element  Description of the Parameter
		 */
		public ElementButton(PeriodicTableElement element)
		{
			super("H");
			this.element = factory.configure(element);
		}
		/**
		 *  Constructor for the ElementButton object
		 * 
		 * @param element Description of the Parameter
		 * @param e       Description of the Parameter
		 * @param color   Description of the Parameter
		 * @param controlViewer Description of the Parameter
		 */
		public ElementButton(
				PeriodicTableElement element, ElementButtonAction e,String buttonString, Color color)
		{
			super(buttonString);
			if(controlViewerButton == JCP){
				setForeground(color);
			}
			
			this.element = element;
			setFont(new Font("Times-Roman",Font.BOLD, 15));
			setBorder( new BevelBorder(BevelBorder.RAISED) );
			setToolTipText(element.getName());
			addActionListener(e);
		}
		/**
		 *  Gets the element attribute of the ElementButton object
		 *
		 *@return    The element value
		 */
		public PeriodicTableElement getElement()
		{
			return this.element;
		}
	}
	/**
	*  create the Label
	*
	*@param element   PeriodicTableElement
	*@return pan      JPanel
	*/
	private JPanel CreateLabelProperties(PeriodicTableElement element) 
	{
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		Color color = new Color(255,255,255);
		Point origin = new Point(90, 20);   	
		JLabel label;
		if(element != null){
			if(controlViewerButton == PeriodicTablePanel.APPLICATION)
			{
				label = new JLabel("<html><PRE>   <FONT SIZE=+2>"
					+element.getSymbol()+"</FONT>"
					+":   At.No "+element.getAtomicNumber()
					+", Group "+element.getGroup()+", Period "
					+ element.getPeriod()+"</PRE></html>");
				pan.add(label,BorderLayout.NORTH);
				
				label = new JLabel("<html><PRE><FONT SIZE=-2>"
					+" CAS id: "+element.getCASid()+"<br>"
					+" Name: "+element.getName()+"<br>"
					+" Serie: "+element.getChemicalSerie()+"<br>"
					+" State: "+element.getPhase()+"<br>"
					+" Appar: XXXX<br>"
					+" Mp: 0.0000<br>"
					+" Bp: 0.0000<br>"
					+" Conduc: 0.0000<br>"
					+" Densit: 0.0000<br>"
					+" VaporH: 0.0000<br>"
					+" XXXX: 0.0000<br>"
					+" XXXX: 0.0000<br>"
					+"</FONT></PRE></html>");
				label.setMinimumSize(new Dimension(145,150));
				pan.add(label,BorderLayout.WEST);
				
				label = new JLabel("<html><PRE><FONT SIZE=-2>"
					+" At. Weight: 0.000000<br>"
					+" At. Radius: 0.0000<br>"
					+" Cov Radius: 0.0000<br>"
					+" VW Radius: 0.0000<br>"
					+" Io Radius: 0.0000<br>"
					+" e config: 1s1<br>"
					+" Valency e: 1s1<br>"
					+" Electro: 0.0<br>"
					+" Oxid: 1<br>"
					+" IP: 0.0000<br>"
					+" Crist: XXXXXX<br>"
					+" XXXX: 0.0000<br>"
					+"</FONT></PRE></html>");
				label.setMinimumSize(new Dimension(145,150));
				pan.add(label,BorderLayout.EAST);
			}
		}
		else
		{
			label = new JLabel("<html></head><br><br>"
				+"<p><FONT><pre>   PERIODIC TABLE<pre></FONT></p>"
				+"<p><PRE>    of elements</PRE></p><br><br><br><br>"
				+"<FONT SIZE=-2>D.I. Mendeleev(1834-1907)</FONT></html>");
			
			label.setOpaque(true);
			label.setBackground(color);
			pan.add(label,BorderLayout.EAST);
			URL url = this.getClass().getResource(
				"/org/openscience/cdk/applications/swing/periodicTable_Mendeleev.jpg");
			if(url!=null)
			{
				ImageIcon image = new ImageIcon(url);
				
				label = new JLabel(image,JLabel.CENTER);
				
				pan.add(label,BorderLayout.WEST);
			}
		}
		
		pan.setBackground(color);
		pan.setForeground(Color.black);
		pan.setBorder(BorderFactory.createLineBorder(Color.black));
		pan.setBounds(origin.x, origin.y, 295, 210);
		return pan;
	}
	/**
	 * set the form to do a button {html or normal)
	 * 
	 * @param controlViewer
	 */
	public void setControlViewer(int controlViewer){
		this.controlViewerButton = controlViewer;
	}
}

