/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
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
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.vecmath.Point2d;

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;


/**
 * Creates a reaction object
 *
 * @cdk.module jchempaint
 *@author     steinbeck
 */
public class CreateReactionAction extends JCPAction
{

	private static final long serialVersionUID = -7625810885316702776L;


	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent event)
	{
		IChemObject object = getSource(event);

		logger.debug("CreateReaction action");
		JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
		IChemModel model = jcpmodel.getChemModel();
		IReactionSet reactionSet = model.getReactionSet();
		if (reactionSet == null)
		{
			reactionSet = model.getBuilder().newReactionSet();
		}
		IAtomContainer container = null;
		if (object instanceof IAtom)
		{
			container = ChemModelManipulator.getRelevantAtomContainer(model, (IAtom) object);
		} else
		{
			logger.error("Cannot add to reaction object of type: " + object.getClass().getName());
		}
		if (container == null)
		{
			logger.error("Cannot find container to add object to!");
		} else
		{
			IAtomContainer newContainer;
			try {
				newContainer = (IAtomContainer) container.clone();
			} catch (CloneNotSupportedException e) {
				logger.error("Could not clone IAtomContainer: ", e.getMessage());
				logger.debug(e);
				return;
			}
			// delete atoms in current model
			IAtom[] atoms = container.getAtoms();
			for (int i = 0; i < atoms.length; i++)
			{
				ChemModelManipulator.removeAtomAndConnectedElectronContainers(model, atoms[i]);
			}
			logger.debug("Deleted atom from old container...");

			// add reaction
			IReaction reaction = model.getBuilder().newReaction();
			reaction.setID("reaction-" + System.currentTimeMillis());
			logger.debug("type: ", type);
			if ("addReactantToNew".equals(type))
			{
				reaction.addReactant(model.getBuilder().newMolecule(newContainer));
				reactionSet.addReaction(reaction);
			} else if ("addReactantToExisting".equals(type))
			{
				if (reactionSet.getReactionCount() == 0)
				{
					logger.warn("Cannot add to reaction if no one exists");
					// FIXME: give feedback to user
					return;
				} else
				{
//					XXX needs fixing
					Object[] ids = getReactionIDs(reactionSet);
					
					String s = (String) JOptionPane.showInputDialog(
							//jcpPanel.getFrame(),
                            null,
							"Reaction Chooser",
							"Choose reaction to add reaction to",
							JOptionPane.PLAIN_MESSAGE,
							null,
							ids,
							ids[0]
							);
					//String s2 = "";

					if ((s != null) && (s.length() > 0))
					{
						String selectedReactionID = s;
						reaction = getReaction(reactionSet, selectedReactionID);
						reaction.addReactant(model.getBuilder().newMolecule(newContainer));
					} else
					{
						logger.error("No reaction selected");
					}
				}
			} else if ("addProductToNew".equals(type))
			{
				reaction.addProduct(model.getBuilder().newMolecule(newContainer));
				reactionSet.addReaction(reaction);
			} else if ("addProductToExisting".equals(type))
			{
				if (reactionSet.getReactionCount() == 0)
				{
					logger.warn("Cannot add to reaction if no one exists");
					// FIXME: give feedback to user
					return;
				} else
				{
					//XXX needs fixing
					
					Object[] ids = getReactionIDs(reactionSet);
					String s = (String) JOptionPane.showInputDialog(
                            //jcpPanel.getFrame(),
							null,
                            "Reaction Chooser",
							"Choose reaction to add reaction to",
							JOptionPane.PLAIN_MESSAGE,
							null,
							ids,
							ids[0]
							);
					//String s2 = "";

					if ((s != null) && (s.length() > 0))
					{
						String selectedReactionID = s;
						reaction = getReaction(reactionSet, selectedReactionID);
						reaction.addProduct(model.getBuilder().newMolecule(newContainer));
					} else
					{
						logger.error("No reaction selected");
					}
				}
			} else
			{
				logger.warn("Don't know about this action type: " + type);
				return;
			}
		}
		model.setReactionSet(reactionSet);
		
		for(int i=0;i<reactionSet.getReactionCount();i++){
			for(int k=0;k<reactionSet.getReaction(i).getProductCount();k++){
				for(int l=0;l<reactionSet.getReaction(i).getProducts().getAtomContainer(k).getAtomCount();l++){
					if(jcpmodel.getRendererModel().getRenderingCoordinates().get(reactionSet.getReaction(i).getProducts().getAtomContainer(k).getAtom(l))==null)
						jcpmodel.getRendererModel().getRenderingCoordinates().put(reactionSet.getReaction(i).getProducts().getAtomContainer(k).getAtom(l),new Point2d(reactionSet.getReaction(i).getProducts().getAtomContainer(k).getAtom(l).getPoint2d()));
				}
			}
			for(int k=0;k<reactionSet.getReaction(i).getReactantCount();k++){
				for(int l=0;l<reactionSet.getReaction(i).getReactants().getAtomContainer(k).getAtomCount();l++){
					if(jcpmodel.getRendererModel().getRenderingCoordinates().get(reactionSet.getReaction(i).getReactants().getAtomContainer(k).getAtom(l))==null)
						jcpmodel.getRendererModel().getRenderingCoordinates().put(reactionSet.getReaction(i).getReactants().getAtomContainer(k).getAtom(l),new Point2d(reactionSet.getReaction(i).getReactants().getAtomContainer(k).getAtom(l).getPoint2d()));
				}
			}
		}
	}


	/**
	 *  Gets the reactionIDs attribute of the CreateReactionAction object
	 *
	 *@param  reactionSet  Description of the Parameter
	 *@return              The reactionIDs value
	 */
	private Object[] getReactionIDs(IReactionSet reactionSet)
	{
		if (reactionSet != null)
		{
			
			String[] ids = new String[reactionSet.getReactionCount()];
			for (int i = 0; i < reactionSet.getReactionCount(); i++)
			{
				ids[i] = reactionSet.getReaction(i).getID();
			}
			return ids;
		} else
		{
			return new String[0];
		}
	}


	/**
	 *  Gets the reaction attribute of the CreateReactionAction object
	 *
	 *@param  reactionSet  Description of the Parameter
	 *@param  id           Description of the Parameter
	 *@return              The reaction value
	 */
	private org.openscience.cdk.interfaces.IReaction getReaction(org.openscience.cdk.interfaces.IReactionSet reactionSet, String id)
	{
		java.util.Iterator reactionIter = reactionSet.reactions();
		while (reactionIter.hasNext())
		{
			IReaction reaction = (IReaction)reactionIter.next();
			if (reaction.getID().equals(id))
			{
				return reaction;
			}
		}
		return null;
	}
}

