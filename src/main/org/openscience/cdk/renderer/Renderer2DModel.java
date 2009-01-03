/* $Revision$ $Author$ $Date$
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
 *  */
package org.openscience.cdk.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;

import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.color.CDK2DAtomColors;
import org.openscience.cdk.renderer.color.IAtomColorer;

/**
 * Model for {@link Renderer2DModel} that contains settings for drawing objects.
 *
 * @cdk.module render
 * @cdk.svnrev  $Revision$
 */
public class Renderer2DModel implements java.io.Serializable, Cloneable {
    
    private static final long serialVersionUID = -4420308906715213445L;

    private double scaleFactor = 60.0;
    
    /** Determines how much the image is zoomed into on. */
    private double zoomFactor = 1.0;
	
	private double bondWidth = 2.0;

	private double bondDistance = 6.0;

	private double bondLength = 36.0;

	private Color backColor = Color.white;
	private Color foreColor = Color.black;
	private Color mappingColor = Color.gray;
    
	private Color hoverOverColor = Color.lightGray;
	private Color selectedPartColor = Color.lightGray;
	private Color externalHighlightColor = Color.orange;	
	
	/**
	 * @deprecated old way of storing highlightRadius based on screensize, 
	 *   new one: {@link #highlightRadiusModel}
	 */
	@Deprecated private double highlightRadius = 10.0;
	private double highlightRadiusModel = 0.7;
	
	private boolean willDrawNumbers = false;
	
	private boolean showAtomTypeNames = false;

    private boolean showAtomAtomMapping = true;

    private boolean useAntiAliasing = true;
    
    private boolean showReactionBoxes = true;

    private boolean showMoleculeTitle = false;

	private int atomRadius = 8;
	
	private IAtom highlightedAtom = null;
	
	private IBond highlightedBond = null;
	
    /** 
     * The color hash is used to color substructures.
     * @see #getColorHash()
     */
	private Map<IChemObject,Color> colorHash = new Hashtable<IChemObject,Color>();
    private IAtomColorer colorer = new CDK2DAtomColors();
	
	private transient List<ICDKChangeListener> listeners = new ArrayList<ICDKChangeListener>();
	
	private Point pointerVectorStart = null;
	
	private Point pointerVectorEnd = null;
	
	private Polygon selectRect = null;
	
	private IAtomContainer selectedPart = null;
	private IAtomContainer externalSelectedPart = null;
	private IAtomContainer clipboardContent = null;
	
	private List<Point> lassoPoints = new ArrayList<Point>();
    
    /** Determines whether structures should be drawn as Kekule structures,
     *  thus giving each carbon element explicitly, instead of not displaying
     *  the element symbol. Example C-C-C instead of /\.
     */
    private boolean kekuleStructure = false;

    /** Determines whether methyl carbons' symbols should be drawn explicit
     *  for methyl carbons. Example C/\C instead of /\. 
     */
    private boolean showEndCarbons = false;

    /** Determines whether implicit hydrogens should be drawn. */
    private boolean showImplicitHydrogens = true;

    /** Determines whether implicit hydrogens should be drawn. */
    private boolean showExplicitHydrogens = true;

    /** Determines whether rings should be drawn with a circle if they are aromatic. */
    private boolean showAromaticity = false;
    private boolean showAromaticityInCDKStyle = false;

    /** Determines whether atoms are colored by type. */
    private boolean colorAtomsByType = true;

    private Dimension backgroundDimension = new Dimension(794,1123);
    
    private boolean showTooltip = false;
    
    private Map<IAtom,String> toolTipTextMap = new HashMap<IAtom,String>();
    
    private Font customFont = null;
    
    private boolean compact=false;
    
	private HashMap merge=new HashMap();
	
	private double[] rotateCenter=null;
	private double rotateRadius=0;
	
	private Map<IAtom,Point2d> renderingCoordinates = new HashMap<IAtom,Point2d>();
	
	private boolean notification = true;
	
	/**
	 * Area on each of the four margins to keep white.
	 */
	private double margin = 0.05;
	
	public void setRenderingCoordinate(IAtom atom, Point2d point){
		this.renderingCoordinates.put(atom,point);
	}
	
	public Point2d getRenderingCoordinate(IAtom atom){
		return (Point2d)this.renderingCoordinates.get(atom);
	}
	
	public Map<IAtom,Point2d> getRenderingCoordinates(){
		return this.renderingCoordinates;
	}
    
    /**
     * @return null if no custom font set
     */
    public Font getFont() {
        return customFont;
    }
    
    public void setFont(Font font) {
        this.customFont = font;
        fireChange();
    }
    
    public boolean getIsCompact() {
        return compact;
    }
    
    public void setIsCompact(boolean compact) {
        this.compact=compact;
    }

    public boolean getUseAntiAliasing() {
        return useAntiAliasing;
    }
    
    public void setUseAntiAliasing(boolean bool) {
        this.useAntiAliasing = bool;
        fireChange();
    }
    
    public boolean getShowReactionBoxes() {
        return showReactionBoxes;
    }
    
    public void setShowReactionBoxes(boolean bool) {
        this.showReactionBoxes = bool;
        fireChange();
    }
    
    public boolean getShowMoleculeTitle() {
        return showMoleculeTitle;
    }
    
    public void setShowMoleculeTitle(boolean bool) {
        this.showMoleculeTitle = bool;
        fireChange();
    }
    
    /**
     * Returns the active background dimensions, thus applying the zoom
     * factor.
     *
     * @see #getUnzoomedBackgroundDimension
     */
    public Dimension getBackgroundDimension() {
        return new Dimension((int)((double)backgroundDimension.getWidth() * zoomFactor),
                             (int)((double)backgroundDimension.getHeight() * zoomFactor));
    }
    
    /**
     * Returns the unzoomed background dimensions.
     *
     * @see #getBackgroundDimension
     */
    public Dimension getUnzoomedBackgroundDimension() {
        return backgroundDimension;
    }
    
    /**
     * Sets the background dimensions in an unzoomed state.
     */
    public void setBackgroundDimension(Dimension dim) {
        this.backgroundDimension = dim;
        fireChange();
    }
    
	/**
	 * Returns the distance between two lines in a double or triple bond
	 *
	 * @return     the distance between two lines in a double or triple bond
	 */
	public double getBondDistance() {
		return this.bondDistance;
	}


	/**
	 * Sets the distance between two lines in a double or triple bond
	 *
	 * @param   bondDistance  the distance between two lines in a double or triple bond
	 */
	public void setBondDistance(double bondDistance) {
		this.bondDistance = bondDistance;
        fireChange();
	}

	

	/**
	 * Returns the thickness of a bond line.
	 *
	 * @return     the thickness of a bond line
	 */
	public double getBondWidth()
	{
		return this.bondWidth;
	}


	/**
	 * Sets the thickness of a bond line.
	 *
	 * @param   bondWidth  the thickness of a bond line
	 */
	public void setBondWidth(double bondWidth)
	{
		this.bondWidth = bondWidth;
        fireChange();
	}


	/**
	 * Returns the length of a bond line.
	 *
	 * @return     the length of a bond line
	 */
	public double getBondLength()
	{
		return this.bondLength;
	}


	/**
	 * Sets the length of a bond line.
	 *
	 * @param   bondLength  the length of a bond line
	 */
	public void setBondLength(double bondLength)
	{
		this.bondLength = bondLength;
        fireChange();
	}
	

	/**
	 * A scale factor for the drawing.
	 *
	 * @return a scale factor for the drawing
	 */
	public double getScaleFactor()
	{
		return this.scaleFactor;
	}


	/**
	 * Returns the scale factor for the drawing
	 *
	 * @param   scaleFactor  the scale factor for the drawing
	 */
	public void setScaleFactor(double scaleFactor)
	{
		this.scaleFactor = scaleFactor;
        fireChange();
	}

	/**
	 * A zoom factor for the drawing.
	 *
	 * @return a zoom factor for the drawing
	 */
	public double getZoomFactor() {
		return this.zoomFactor;
	}


	/**
	 * Returns the zoom factor for the drawing.
	 *
	 * @param   zoomFactor  the zoom factor for the drawing
	 */
	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
        fireChange();
	}	

	/**
	 * Returns the foreground color for the drawing.
	 *
	 * @return the foreground color for the drawing    
	 */
	public Color getForeColor()
	{
		return this.foreColor;
	}


	/**
	 * Sets the foreground color with which bonds and atoms are drawn
	 *
	 * @param   foreColor  the foreground color with which bonds and atoms are drawn
	 */
	public void setForeColor(Color foreColor)
	{
		this.foreColor = foreColor;
        fireChange();
	}

	

	/**
	 * Returns the background color 
	 *
	 * @return the background color     
	 */
	public Color getBackColor()
	{
		return this.backColor;
	}


	/**
	 * Sets the background color 
	 *
	 * @param   backColor the background color  
	 */
	public void setBackColor(Color backColor)
	{
		this.backColor = backColor;
        fireChange();
	}

    /**
     * Returns the atom-atom mapping line color 
     *
     * @return the atom-atom mapping line color     
     */
    public Color getAtomAtomMappingLineColor() {
        return this.mappingColor;
    }

    /**
     * Sets the atom-atom mapping line color 
     *
     * @param   mappingColor the atom-atom mapping line color  
     */
    public void setAtomAtomMappingLineColor(Color mappingColor) {
        this.mappingColor = mappingColor;
        fireChange();
    }

	
	/**
	 * Returns if the drawing of atom numbers is switched on for this model
	 *
	 * @return  true if the drawing of atom numbers is switched on for this model   
	 */
	public boolean drawNumbers()
	{
		return this.willDrawNumbers;
	}

	public boolean getKekuleStructure() {
		return this.kekuleStructure;
	}

	public void setKekuleStructure(boolean kekule) {
		this.kekuleStructure = kekule;
        fireChange();
	}

    public boolean getColorAtomsByType() {
        return this.colorAtomsByType;
    }
    
    public void setColorAtomsByType(boolean bool) {
        this.colorAtomsByType = bool;
        fireChange();
    }

    public boolean getShowEndCarbons() {
        return this.showEndCarbons;
    }
    
    public void setShowEndCarbons(boolean showThem) {
        this.showEndCarbons = showThem;
        fireChange();
    }
    
    public boolean getShowImplicitHydrogens() {
        return this.showImplicitHydrogens;
    }
    
    public void setShowImplicitHydrogens(boolean showThem) {
        this.showImplicitHydrogens = showThem;
        fireChange();
    }
    
    public boolean getShowExplicitHydrogens() {
        return this.showExplicitHydrogens;
    }
    
    public void setShowExplicitHydrogens(boolean showThem) {
        this.showExplicitHydrogens = showThem;
        fireChange();
    }
    
    public boolean getShowAromaticity() {
        return this.showAromaticity;
    }
    
    public void setShowAromaticity(boolean showIt) {
        this.showAromaticity = showIt;
        fireChange();
    }
    
    public boolean getShowAromaticityInCDKStyle() {
        return this.showAromaticityInCDKStyle;
    }
    
    public void setShowAromaticityInCDKStyle(boolean showIt) {
        this.showAromaticityInCDKStyle = showIt;
        fireChange();
    }
    
	/**
	 * Sets if the drawing of atom numbers is switched on for this model.
	 *
	 * @param   drawNumbers  true if the drawing of atom numbers is to be switched on for this model
	 */
	public void setDrawNumbers(boolean drawNumbers)
	{
		this.willDrawNumbers = drawNumbers;
        fireChange();
	}

	/**
	 * Returns true if atom numbers are drawn.
	 */
	public boolean getDrawNumbers() {
		return willDrawNumbers;
	}


	/**
	 * Returns the radius around an atoms, for which the atom is 
	 * marked highlighted if a pointer device is placed within this radius.
	 * 
	 * @return The highlight radius for all atoms (in screensize)
	 * @deprecated old way of getting highlightRadius, new one: {@link #getHighlightRadiusModel()}
	 */
	@Deprecated public double getHighlightRadius()
	{
		return this.highlightRadius;
	}
	

	/**
	 * Sets the radius around an atoms, for which the atom is 
	 * marked highlighted if a pointer device is placed within this radius.
	 *
	 * @param   highlightRadius  the highlight radius of all atoms (in screensize)
	 * @deprecated old way of getting highlightRadius, 
	 *             new one: {@link #setHighlightRadiusModel(double)}
	 */
	@Deprecated public void setHighlightRadius(double highlightRadius)
	{
		this.highlightRadius = highlightRadius;
        fireChange();
	}
	/**
	 * Returns the radius around an atoms, for which the atom is 
	 * marked highlighted if a pointer device is placed within this radius.
	 * 
	 * @return The highlight radius for all atoms (in model based size)
	 */
	public double getHighlightRadiusModel()
	{
		return this.highlightRadiusModel;
	}
	/**
	 * Sets the radius around an atoms, for which the atom is 
	 * marked highlighted if a pointer device is placed within this radius.
	 *
	 * @param   highlightRadius  the highlight radius of all atoms (in model based size)
	 */
	public void setHighlightRadiusModel(double highlightRadius)
	{
		this.highlightRadiusModel = highlightRadius;
        fireChange();
	}
	/**
     * Returns whether Atom-Atom mapping must be shown.
     */
     public boolean getShowAtomAtomMapping() {
         return this.showAtomAtomMapping;
     }


    /**
     * Sets whether Atom-Atom mapping must be shown.
     */
    public void setShowAtomAtomMapping(boolean value) {
        this.showAtomAtomMapping = value;
        fireChange();
    }

    /**
     * XXX No idea what this is about.
     */
    public int getAtomRadius() {
        return this.atomRadius;
    }


	/**
	 * XXX No idea what this is about.
	 *
	 * @param   atomRadius   XXX No idea what this is about
	 */
	public void setAtomRadius(int atomRadius)
	{
		this.atomRadius = atomRadius;
        fireChange();
	}

	

	/**
	 * Returns the atom currently highlighted.
	 *
	 * @return the atom currently highlighted    
	 */
	public IAtom getHighlightedAtom()
	{
		return this.highlightedAtom;
	}


	/**
	 * Sets the atom currently highlighted.
	 *
	 * @param   highlightedAtom The atom to be highlighted  
	 */
	public void setHighlightedAtom(IAtom highlightedAtom)
	{
        if ((this.highlightedAtom != null) || (highlightedAtom != null)) {
            this.highlightedAtom = highlightedAtom;
            fireChange();
        }
    }

	

	/**
	 * Returns the Bond currently highlighted.
	 *
	 * @return the Bond currently highlighted    
	 */
	public IBond getHighlightedBond()
	{
		return this.highlightedBond;
	}


	/**
	 * Sets the Bond currently highlighted.
	 *
	 * @param   highlightedBond  The Bond to be currently highlighted
	 */
	public void setHighlightedBond(IBond highlightedBond)
	{
        if ((this.highlightedBond != null) || (highlightedBond != null)) {
            this.highlightedBond = highlightedBond;
            fireChange();
        }
    }

	

	/**
	 * Returns the {@link Map} used for coloring substructures.
	 *
	 * @return the {@link Map} used for coloring substructures     
	 */
	public Map<IChemObject,Color> getColorHash() {
		return this.colorHash;
	}
    
    /**
     * Returns the drawing color of the given atom.
     * An atom is colored as highlighted if hightlighted.
     * The atom is color marked if in a substructure.
     * If not, the color from the CDK2DAtomColor is used
     * (if selected). Otherwise, the atom is colored black.
     */
    public Color getAtomColor(IAtom atom, Color defaultColor) {
        // logger.debug("Getting atom front color for " + atom.toString());
        Color atomColor = defaultColor;
        if (colorAtomsByType) {
            // logger.debug("Coloring atom by type");
            atomColor = colorer.getAtomColor(atom);
        }
        // logger.debug("Color: " + atomColor.toString());
        return atomColor;
    }

    /**
     * Returns the background color of the given atom.
     */
    public Color getAtomBackgroundColor(IAtom atom) {
        // logger.debug("Getting atom back color for " + atom.toString());
        Color atomColor = getBackColor();
        // logger.debug("  BackColor: " + atomColor.toString());
        Color hashColor = (Color) this.getColorHash().get(atom);
        if (hashColor != null) {
            // logger.debug("Background color atom according to hashing (substructure)");
            atomColor = hashColor;
        }
        if (atom == this.getHighlightedAtom()) {
            // logger.debug("Background color atom according to highlighting");
            atomColor = this.getHoverOverColor();
        }
        // logger.debug("Color: " + atomColor.toString());
        return atomColor;
    }

    /**
     * Returns the current atom colorer.
     *
     * @return  The AtomColorer.
     */
    public IAtomColorer getAtomColorer()
    {
        return colorer;
    }

    /**
     * Sets the atom colorer.
     *
     * @param atomColorer  the new colorer.
     */
    public void setAtomColorer(final IAtomColorer atomColorer)
    {
        colorer = atomColorer;
    }

	/**
	 * Sets the {@link Map} used for coloring substructures 
	 *
	 * @param   colorHash  the {@link Map} used for coloring substructures 
	 */
	public void setColorHash(Map<IChemObject,Color> colorHash)
	{
		this.colorHash = colorHash;
        fireChange();
	}
	

	/**
	 * Returns the end of the pointer vector
	 *
	 * @return the end point
	 */
	public Point getPointerVectorEnd()
	{
		return this.pointerVectorEnd;
	}


	/**
	 * Sets the end of a pointer vector
	 *
	 * @param   pointerVectorEnd  
	 */
	public void setPointerVectorEnd(Point pointerVectorEnd)
	{
		this.pointerVectorEnd = pointerVectorEnd;
		fireChange();
	}

	

	/**
	 * Returns the start of a pointer vector
	 *
	 * @return the start point
	 */
	public Point getPointerVectorStart()
	{
		return this.pointerVectorStart;
	}


	/**
	 * Sets the start point of a pointer vector
	 *
	 * @param   pointerVectorStart  
	 */
	public void setPointerVectorStart(Point pointerVectorStart)
	{
		this.pointerVectorStart = pointerVectorStart;
		fireChange();
	}
	
	

	/**
	 * Returns selected rectangular
	 *
	 * @return the selection
	 */
	public Polygon getSelectRect()
	{
		return this.selectRect;
	}


	/**
	 * Sets a selected region
	 *
	 * @param   selectRect  
	 */
	public void setSelectRect(Polygon selectRect)
	{
		this.selectRect = selectRect;
		fireChange();		
	}

	

    /**
     * Returns the atoms and bonds on the Renderer2D clipboard. If the clipboard
     * is empty it returns null.
     * Primarily used for copy/paste.
     *
     * @return an atomcontainer with the atoms and bonds on the clipboard.
     */
    public IAtomContainer getClipboardContent() {
        return clipboardContent;
    }


    /**
     * Sets the atoms and bonds on the Renderer2D clipboard.
     * Primarily used for copy/paste.
     *
     * @param content the new content of the clipboard.
     */
    public void setClipboardContent(IAtomContainer content) {
        this.clipboardContent = content;
    }

	/**
	 * Get selected atoms. These are atoms selected internally in e. g. JCP with the lasso, painted in selectedPartColor
	 *
	 * @return an atomcontainer with the selected atoms
	 */
	public IAtomContainer getSelectedPart()
	{
		return this.selectedPart;
	}

	/**
	 * Sets the selected atoms. These are atoms selected internally in e. g. JCP with the lasso, painted in selectedPartColor
	 *
	 * @param   selectedPart  
	 */
	public void setSelectedPart(IAtomContainer selectedPart)
	{
		this.selectedPart = selectedPart;
		getColorHash().clear();
		for (int i = 0; i < selectedPart.getAtomCount(); i++)
		{
			getColorHash().put(selectedPart.getAtom(i), this.getSelectedPartColor());
		}
        Iterator<IBond> bonds = selectedPart.bonds().iterator();
		while (bonds.hasNext()) {
			getColorHash().put(bonds.next(), getSelectedPartColor());
		}		
        fireChange();
	}


	/**
	 * Returns a set of points constituating a selected region
	 *
	 * @return a vector with points
	 */
	public List<Point> getLassoPoints()
	{
		return this.lassoPoints;
	}


	/**
	 * Adds a point to the list of lasso points
	 *
	 * @param   point  Point to add to list
	 */
	public void addLassoPoint(Point point)
	{
		this.lassoPoints.add(point);
		fireChange();
	}


	/**
	 * Adds a change listener to the list of listeners
	 *
	 * @param   listener  The listener added to the list 
	 */

	public void addCDKChangeListener(ICDKChangeListener listener)
	{
		if (listeners == null) {
			listeners = new ArrayList<ICDKChangeListener>();	
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	

	/**
	 * Removes a change listener from the list of listeners
	 *
	 * @param   listener  The listener removed from the list 
	 */
	public void removeCDKChangeListener(ICDKChangeListener listener) {
		listeners.remove(listener);
	}


	/**
	 * Notifies registered listeners of certain changes
	 * that have occurred in this model.
	 */
	public void fireChange() {
		if (getNotification() && listeners != null) {
			EventObject event = new EventObject(this);
			for (int i = 0; i < listeners.size(); i++) {
				listeners.get(i).stateChanged(event);
			}
		}
	}
  
  
  /**
   *  Gets the toolTipText for atom certain atom.
   *
   * @param  atom  The atom.
   * @return    The toolTipText value.
   */
  public String getToolTipText(IAtom atom) {
      if (toolTipTextMap.get(atom) != null) {
          return toolTipTextMap.get(atom);
      } else {
          return null;
      }
  }


    /**
   *  Sets the showTooltip attribute.
   *
   * @param  showToolTip  The new value.
   */
    public void setShowTooltip(boolean showToolTip) {
        this.showTooltip = showToolTip;
        fireChange();
    }
  
  
  /**
   *  Gets showTooltip attribute.
   *
   * @return    The showTooltip value.
   */
  public boolean getShowTooltip(){
    return(showTooltip);
  }
  
  
  /**
   *  Sets the toolTipTextMap.
   *
   * @param  map  A map containing Atoms of the current molecule as keys and Strings to display as values. A line break will be inserted where a \n is in the string.  
   */
  public void setToolTipTextMap(Map<IAtom,String> map){
	  toolTipTextMap=map;
	  fireChange();
  }


  /**
   *  Gets the toolTipTextMap.
   *
   * @return  The toolTipTextValue.  
   */
  public Map<IAtom,String> getToolTipTextMap(){
    return toolTipTextMap;
  }
	
	/**
	 * This is the central facility for handling "merges" of atoms. A merge occures if during moving atoms an atom is in Range of another atom.
	 * These atoms are then put into the merge map as a key-value pair. During the move, the atoms are then marked by a circle and on releasing the mouse
	 * they get actually merged, meaning one atom is removed and bonds pointing to this atom are made to point to the atom it has been marged with.
	 * 
	 * @return Returns the merge.map
	 * 
	 * FIXME: this belongs in the controller model... this is not about rendering, it's about editing (aka controlling)
	 */
	public HashMap getMerge() {
		return merge;
	}
	
	
	public double[] getRotateCenter() {
		return rotateCenter;
	}
	
	public void setRotateCenter(double x, double y) {
		double[] rotateCenter={x,y};
		this.rotateCenter = rotateCenter;
	}
	
	public double getRotateRadius() {
		return rotateRadius;
	}
	
	public void setRotateRadius(double rotateRadius) {
		this.rotateRadius = rotateRadius;
	}
	
	/**
	 * Gets the color used for drawing the part which was selected externally
	 */
	public Color getExternalHighlightColor() {
		return externalHighlightColor;
	}
	
	/**
	 * Sets the color used for drawing the part which was selected externally
	 * 
	 * @param externalHighlightColor The color
	 */
	public void setExternalHighlightColor(Color externalHighlightColor) {
		this.externalHighlightColor = externalHighlightColor;
	}
	
	/**
	 * Gets the color used for drawing the part we are hovering over.
	 */
	public Color getHoverOverColor() {
		return hoverOverColor;
	}
	
	/**
	 * Sets the color used for drawing the part we are hovering over.
	 * 
	 * @param hoverOverColor The color
	 */
	public void setHoverOverColor(Color hoverOverColor) {
		this.hoverOverColor = hoverOverColor;
	}
	
	/**
	 * Gets the color used for drawing the internally selected part.
	 */
	public Color getSelectedPartColor() {
		return selectedPartColor;
	}
	
	/**
	 * Sets the color used for drawing the internally selected part.
	 * 
	 * @param selectedPartColor The color
	 */
	public void setSelectedPartColor(Color selectedPartColor) {
		this.selectedPartColor = selectedPartColor;
	}
	
	/**
	 * Get externally selected atoms. These are atoms selected externally in e. g. Bioclipse via the ChemObjectTree, painted in externalSelectedPartColor
	 *
	 * @return the selected part
	 */
	public IAtomContainer getExternalSelectedPart() {
		return externalSelectedPart;
	}
	
	/**
	 * Set externally selected atoms. These are atoms selected externally in e. g. Bioclipse via the ChemObjectTree, painted in externalSelectedPartColor
	 * 
	 * @param externalSelectedPart the selected part
	 */
	public void setExternalSelectedPart(IAtomContainer externalSelectedPart) {
		this.externalSelectedPart = externalSelectedPart;
		getColorHash().clear();
		for (int i = 0; i < externalSelectedPart.getAtomCount(); i++)
		{
			getColorHash().put(externalSelectedPart.getAtom(i), this.getExternalHighlightColor());
		}
        Iterator<IBond> bonds = externalSelectedPart.bonds().iterator();
		while (bonds.hasNext()) {
			getColorHash().put(bonds.next(), getExternalHighlightColor());
		}		
        fireChange();
    }

	public boolean getNotification() {
		return notification;
	}

	public void setNotification(boolean notification) {
		this.notification = notification;
	}

	public boolean showAtomTypeNames() {
		return showAtomTypeNames;
	}

	public void setShowAtomTypeNames(boolean showAtomTypeNames) {
		this.showAtomTypeNames = showAtomTypeNames;
	}

	public void setRenderingCoordinates(Map<IAtom,Point2d> renderingCoordinates) {
		this.renderingCoordinates = renderingCoordinates;
	}

	public double getMargin() {
		return margin;
	}

	public void setMargin(double margin) {
		this.margin = margin;
	}
}
