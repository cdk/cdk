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
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.applications.swing;

import java.awt.Button;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.EventObject;

import org.openscience.cdk.Element;
import org.openscience.cdk.event.CDKChangeListener;

/**
 *  JPanel version of the periodic system.
 *
 *@author        Egon Willighagen
 *@author        Geert Josten
 *@created       February 10, 2004
 *@cdkPackage    applications
 */
public class PeriodicTablePanel extends JPanel
{
	Vector listeners = null;
	Element selectedElement = null;


	/**
	 *  Constructor for the PeriodicTablePanel object
	 */
	public PeriodicTablePanel()
	{
		super();
		listeners = new Vector();
		setLayout(new GridLayout(0, 32));

		add(new ElementButton(new Element("H", 1), new ElementButtonAction()));
		for (int i = 0; i < 30; i++)
		{
			add(new Button());
		}
		add(new ElementButton(new Element("He", 2), new ElementButtonAction()));

		add(new ElementButton(new Element("Li", 3), new ElementButtonAction()));
		add(new ElementButton(new Element("Be", 4), new ElementButtonAction()));
		for (int i = 0; i < 24; i++)
		{
			add(new Button());
		}
		add(new ElementButton(new Element("B", 5), new ElementButtonAction()));
		add(new ElementButton(new Element("C", 6), new ElementButtonAction()));
		add(new ElementButton(new Element("N", 7), new ElementButtonAction()));
		add(new ElementButton(new Element("O", 8), new ElementButtonAction()));
		add(new ElementButton(new Element("F", 9), new ElementButtonAction()));
		add(new ElementButton(new Element("Ne", 0), new ElementButtonAction()));

		add(new ElementButton(new Element("Na", 1), new ElementButtonAction()));
		add(new ElementButton(new Element("Mg", 2), new ElementButtonAction()));
		for (int i = 0; i < 24; i++)
		{
			add(new Button());
		}
		add(new ElementButton(new Element("Al", 3), new ElementButtonAction()));
		add(new ElementButton(new Element("Si", 4), new ElementButtonAction()));
		add(new ElementButton(new Element("P", 5), new ElementButtonAction()));
		add(new ElementButton(new Element("S", 6), new ElementButtonAction()));
		add(new ElementButton(new Element("Cl", 7), new ElementButtonAction()));
		add(new ElementButton(new Element("Ar", 8), new ElementButtonAction()));

		add(new ElementButton(new Element("K", 9), new ElementButtonAction()));
		add(new ElementButton(new Element("Ca", 0), new ElementButtonAction()));
		add(new ElementButton(new Element("Sc", 1), new ElementButtonAction()));
		for (int i = 0; i < 14; i++)
		{
			add(new Button());
		}
		add(new ElementButton(new Element("Ti", 2), new ElementButtonAction()));
		add(new ElementButton(new Element("V", 3), new ElementButtonAction()));
		add(new ElementButton(new Element("Cr", 4), new ElementButtonAction()));
		add(new ElementButton(new Element("Mn", 5), new ElementButtonAction()));
		add(new ElementButton(new Element("Fe", 6), new ElementButtonAction()));
		add(new ElementButton(new Element("Co", 7), new ElementButtonAction()));
		add(new ElementButton(new Element("Ni", 8), new ElementButtonAction()));
		add(new ElementButton(new Element("Cu", 9), new ElementButtonAction()));
		add(new ElementButton(new Element("Zn", 0), new ElementButtonAction()));
		add(new ElementButton(new Element("Ga", 1), new ElementButtonAction()));
		add(new ElementButton(new Element("Ge", 2), new ElementButtonAction()));
		add(new ElementButton(new Element("As", 3), new ElementButtonAction()));
		add(new ElementButton(new Element("Se", 4), new ElementButtonAction()));
		add(new ElementButton(new Element("Br", 5), new ElementButtonAction()));
		add(new ElementButton(new Element("Kr", 6), new ElementButtonAction()));

		add(new ElementButton(new Element("Rb", 7), new ElementButtonAction()));
		add(new ElementButton(new Element("Sr", 8), new ElementButtonAction()));
		add(new ElementButton(new Element("Y", 9), new ElementButtonAction()));
		for (int i = 0; i < 14; i++)
		{
			add(new Button());
		}
		add(new ElementButton(new Element("Zr", 0), new ElementButtonAction()));
		add(new ElementButton(new Element("Nb", 1), new ElementButtonAction()));
		add(new ElementButton(new Element("Mo", 2), new ElementButtonAction()));
		add(new ElementButton(new Element("Tc", 3), new ElementButtonAction()));
		add(new ElementButton(new Element("Ru", 4), new ElementButtonAction()));
		add(new ElementButton(new Element("Rh", 5), new ElementButtonAction()));
		add(new ElementButton(new Element("Pd", 6), new ElementButtonAction()));
		add(new ElementButton(new Element("Ag", 7), new ElementButtonAction()));
		add(new ElementButton(new Element("Cd", 8), new ElementButtonAction()));
		add(new ElementButton(new Element("In", 9), new ElementButtonAction()));
		add(new ElementButton(new Element("Sn", 0), new ElementButtonAction()));
		add(new ElementButton(new Element("Sb", 1), new ElementButtonAction()));
		add(new ElementButton(new Element("Te", 2), new ElementButtonAction()));
		add(new ElementButton(new Element("I", 3), new ElementButtonAction()));
		add(new ElementButton(new Element("Xe", 4), new ElementButtonAction()));

		add(new ElementButton(new Element("Cs", 5), new ElementButtonAction()));
		add(new ElementButton(new Element("Ba", 6), new ElementButtonAction()));
		add(new ElementButton(new Element("La", 7), new ElementButtonAction()));
		add(new ElementButton(new Element("Ce", 8), new ElementButtonAction()));
		add(new ElementButton(new Element("Pr", 9), new ElementButtonAction()));
		add(new ElementButton(new Element("Nd", 0), new ElementButtonAction()));
		add(new ElementButton(new Element("Pm", 1), new ElementButtonAction()));
		add(new ElementButton(new Element("Sm", 2), new ElementButtonAction()));
		add(new ElementButton(new Element("Eu", 3), new ElementButtonAction()));
		add(new ElementButton(new Element("Gd", 4), new ElementButtonAction()));
		add(new ElementButton(new Element("Tb", 5), new ElementButtonAction()));
		add(new ElementButton(new Element("Dy", 6), new ElementButtonAction()));
		add(new ElementButton(new Element("Ho", 7), new ElementButtonAction()));
		add(new ElementButton(new Element("Er", 8), new ElementButtonAction()));
		add(new ElementButton(new Element("Tm", 9), new ElementButtonAction()));
		add(new ElementButton(new Element("Yb", 0), new ElementButtonAction()));
		add(new ElementButton(new Element("Lu", 1), new ElementButtonAction()));
		add(new ElementButton(new Element("Hf", 2), new ElementButtonAction()));
		add(new ElementButton(new Element("Ta", 3), new ElementButtonAction()));
		add(new ElementButton(new Element("W", 4), new ElementButtonAction()));
		add(new ElementButton(new Element("Re", 5), new ElementButtonAction()));
		add(new ElementButton(new Element("Os", 6), new ElementButtonAction()));
		add(new ElementButton(new Element("Ir", 7), new ElementButtonAction()));
		add(new ElementButton(new Element("Pt", 8), new ElementButtonAction()));
		add(new ElementButton(new Element("Au", 9), new ElementButtonAction()));
		add(new ElementButton(new Element("Hg", 0), new ElementButtonAction()));
		add(new ElementButton(new Element("Tl", 1), new ElementButtonAction()));
		add(new ElementButton(new Element("Pb", 2), new ElementButtonAction()));
		add(new ElementButton(new Element("Bi", 3), new ElementButtonAction()));
		add(new ElementButton(new Element("Po", 4), new ElementButtonAction()));
		add(new ElementButton(new Element("At", 5), new ElementButtonAction()));
		add(new ElementButton(new Element("Rn", 6), new ElementButtonAction()));

		add(new ElementButton(new Element("Fr", 7), new ElementButtonAction()));
		add(new ElementButton(new Element("Ra", 8), new ElementButtonAction()));
		add(new ElementButton(new Element("Ac", 9), new ElementButtonAction()));
		add(new ElementButton(new Element("Th", 0), new ElementButtonAction()));
		add(new ElementButton(new Element("Pa", 1), new ElementButtonAction()));
		add(new ElementButton(new Element("U", 2), new ElementButtonAction()));
		add(new ElementButton(new Element("Np", 3), new ElementButtonAction()));
		add(new ElementButton(new Element("Pu", 4), new ElementButtonAction()));
		add(new ElementButton(new Element("Am", 5), new ElementButtonAction()));
		add(new ElementButton(new Element("Cm", 6), new ElementButtonAction()));
		add(new ElementButton(new Element("Bk", 7), new ElementButtonAction()));
		add(new ElementButton(new Element("Cf", 8), new ElementButtonAction()));
		add(new ElementButton(new Element("Es", 9), new ElementButtonAction()));
		add(new ElementButton(new Element("Fm", 0), new ElementButtonAction()));
		add(new ElementButton(new Element("Md", 1), new ElementButtonAction()));
		add(new ElementButton(new Element("No", 2), new ElementButtonAction()));
		add(new ElementButton(new Element("Lr", 3), new ElementButtonAction()));
		add(new ElementButton(new Element("Unq", 4), new ElementButtonAction()));
		add(new ElementButton(new Element("Unp", 5), new ElementButtonAction()));
		add(new ElementButton(new Element("Unh", 6), new ElementButtonAction()));
	}


	/**
	 *  Sets the selectedElement attribute of the PeriodicTablePanel object
	 *
	 *@param  selectedElement  The new selectedElement value
	 */
	public void setSelectedElement(Element selectedElement)
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
		return selectedElement;
	}


	/**
	 *  Adds a change listener to the list of listeners
	 *
	 *@param  listener  The listener added to the list
	 */

	public void addCDKChangeListener(CDKChangeListener listener)
	{
		listeners.add(listener);
	}


	/**
	 *  Removes a change listener from the list of listeners
	 *
	 *@param  listener  The listener removed from the list
	 */
	public void removeCDKChangeListener(CDKChangeListener listener)
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
			((CDKChangeListener) listeners.get(i)).stateChanged(event);
		}
	}



	/**
	 *  Description of the Class
	 *
	 *@author     steinbeck
	 *@created    February 10, 2004
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
			System.out.println(getSelectedElement());
		}
	}


	/**
	 *  Description of the Class
	 *
	 *@author     steinbeck
	 *@created    February 10, 2004
	 */
	class ElementButton extends Button
	{

		private Element element;


		/**
		 *  Constructor for the ElementButton object
		 *
		 *@param  element  Description of the Parameter
		 */
		public ElementButton(Element element)
		{
			super(element.getSymbol());
			this.element = element;
		}


		/**
		 *  Constructor for the ElementButton object
		 *
		 *@param  element  Description of the Parameter
		 *@param  e        Description of the Parameter
		 */
		public ElementButton(Element element, ElementButtonAction e)
		{
			super(element.getSymbol());
			this.element = element;
			addActionListener(e);
		}


		/**
		 *  Gets the element attribute of the ElementButton object
		 *
		 *@return    The element value
		 */
		public Element getElement()
		{
			return this.element;
		}

	}

}

