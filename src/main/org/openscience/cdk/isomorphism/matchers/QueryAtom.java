/* Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                    2010  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.isomorphism.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.event.ChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

public abstract class QueryAtom implements IQueryAtom {

    /**
     *  The partial charge of the atom.
     *
     * The default value is {@link CDKConstants#UNSET} and serves to provide a check whether the charge has been
     * set or not
     */
    protected Double charge = (Double) CDKConstants.UNSET;

    /**
     *  A 2D point specifying the location of this atom in a 2D coordinate 
     *  space.
     */
    protected javax.vecmath.Point2d point2d = (Point2d) CDKConstants.UNSET;

    /**
     *  A 3 point specifying the location of this atom in a 3D coordinate 
     *  space.
     */
    protected javax.vecmath.Point3d point3d = (Point3d) CDKConstants.UNSET;

    /**
     *  A 3 point specifying the location of this atom in a crystal unit cell.
     */
    protected javax.vecmath.Point3d fractionalPoint3d = (Point3d) CDKConstants.UNSET;

    /**
     *  The number of implicitly bound hydrogen atoms for this atom.
     */
    protected Integer hydrogenCount = (Integer) CDKConstants.UNSET;

    /**
     *  A stereo parity descriptor for the stereochemistry of this atom.
     */
    protected Integer stereoParity = (Integer) CDKConstants.UNSET;

    /**
     *  The maximum bond order allowed for this atom type.
     */
    IBond.Order maxBondOrder = null;
    /**
     *  The maximum sum of all bond orders allowed for this atom type.
     */
    Double bondOrderSum = (Double) CDKConstants.UNSET;

    /**
     * The covalent radius of this atom type.
     */
    Double covalentRadius = (Double) CDKConstants.UNSET;
    
    /**
     *  The formal charge of the atom with CDKConstants.UNSET as default. Implements RFC #6.
     * 
     *  Note that some constructors ({@link #AtomType(String)} and
     * {@link #AtomType(String, String)} ) will explicitly set this field to 0
     */
    protected Integer formalCharge = (Integer) CDKConstants.UNSET;

    /**
     * The hybridization state of this atom with CDKConstants.HYBRIDIZATION_UNSET
     * as default.
     */
    protected IAtomType.Hybridization hybridization = (Hybridization) CDKConstants.UNSET;

    /**
     *  The electron Valency of this atom with CDKConstants.UNSET as default.
     */
    protected Integer electronValency = (Integer) CDKConstants.UNSET;

    /**
     * The formal number of neighbours this atom type can have with CDKConstants_UNSET
     * as default. This includes explicitely and implicitely connected atoms, including
     * implicit hydrogens.
     */
    protected Integer formalNeighbourCount = (Integer) CDKConstants.UNSET;

    /**
     * String representing the identifier for this atom type with null as default.
     */
    private String identifier = (String) CDKConstants.UNSET;

    /** Exact mass of this isotope. */
    public Double exactMass;

    /** Natural abundance of this isotope. */
    public Double naturalAbundance;

    /** The mass number for this isotope. */
    private Integer massNumber;

    /** The element symbol for this element as listed in the periodic table. */
    protected String symbol;

    /** The atomic number for this element giving their position in the periodic table. */
    protected Integer atomicNumber = (Integer) CDKConstants.UNSET;

    /**
     * List for listener administration.
     */
    private List<IChemObjectListener> chemObjectListeners;

    /**
     *  A hashtable for the storage of any kind of properties of this IChemObject.
     */
    private Map<Object, Object> properties;

    /**
     *  You will frequently have to use some flags on a IChemObject. For example, if
     *  you want to draw a molecule and see if you've already drawn an atom, or in
     *  a ring search to check whether a vertex has been visited in a graph
     *  traversal. Use these flags while addressing particular positions in the
     *  flag array with self-defined constants (flags[VISITED] = true). 100 flags
     *  per object should be more than enough.
     */
    private boolean[] flags;

    /**
     *  Sets the partial charge of this atom.
     *
     * @param  charge  The partial charge
     *
     * @see    #getCharge
     */
    public void setCharge(Double charge) {
        this.charge = charge;
        notifyChanged();
    }

    /**
     *  Returns the partial charge of this atom.
     *
     * If the charge has not been set the return value is Double.NaN
     *
     * @return the charge of this atom
     *
     * @see    #setCharge
     */
    public Double getCharge() {
           return this.charge;
    }

    /**
     *  Sets the number of implicit hydrogen count of this atom.
     *
     * @param  hydrogenCount  The number of hydrogen atoms bonded to this atom.
     *
     * @see    #getImplicitHydrogenCount
     */
    public void setImplicitHydrogenCount(Integer hydrogenCount) {
        this.hydrogenCount = hydrogenCount;
        notifyChanged();
    }

    /**
     *  Returns the hydrogen count of this atom.
     *
     * @return    The hydrogen count of this atom.
     *
     * @see       #setImplicitHydrogenCount
     */
    public Integer getImplicitHydrogenCount() {
        return this.hydrogenCount;
    }

    /**
    *
    * Sets a point specifying the location of this
    * atom in a 2D space.
    *
    * @param  point2d  A point in a 2D plane
    *
    * @see    #getPoint2d
    */
   public void setPoint2d(javax.vecmath.Point2d point2d) {
           this.point2d = point2d;
   notifyChanged();
   }
   /**
    *
    * Sets a point specifying the location of this
    * atom in 3D space.
    *
    * @param  point3d  A point in a 3-dimensional space
    *
    * @see    #getPoint3d
    */
   public void setPoint3d(javax.vecmath.Point3d point3d) {
           this.point3d = point3d;
   notifyChanged();
   }
   /**
    * Sets a point specifying the location of this
    * atom in a Crystal unit cell.
    *
    * @param  point3d  A point in a 3d fractional unit cell space
    *
    * @see    #getFractionalPoint3d
    * @see    org.openscience.cdk.Crystal
    */
   public void setFractionalPoint3d(javax.vecmath.Point3d point3d) {
           this.fractionalPoint3d = point3d;
   notifyChanged();
   }
   /**
    * Sets the stereo parity for this atom.
    *
    * @param  stereoParity  The stereo parity for this atom
    *
    * @see    org.openscience.cdk.CDKConstants for predefined values.
    * @see    #getStereoParity
    */
   public void setStereoParity(Integer stereoParity) {
       this.stereoParity = stereoParity;
       notifyChanged();
   }

   /**
    * Returns a point specifying the location of this
    * atom in a 2D space.
    *
    * @return    A point in a 2D plane. Null if unset.
    *
    * @see       #setPoint2d
    */
   public javax.vecmath.Point2d getPoint2d() {
           return this.point2d;
   }
   /**
    * Returns a point specifying the location of this
    * atom in a 3D space.
    *
    * @return    A point in 3-dimensional space. Null if unset.
    *
    * @see       #setPoint3d
    */
   public javax.vecmath.Point3d getPoint3d() {
           return this.point3d;
   }

   /**
    * Returns a point specifying the location of this
    * atom in a Crystal unit cell.
    *
    * @return    A point in 3d fractional unit cell space. Null if unset.
    *
    * @see       #setFractionalPoint3d
    * @see       org.openscience.cdk.CDKConstants for predefined values.
    */
   public javax.vecmath.Point3d getFractionalPoint3d() {
           return this.fractionalPoint3d;
   }

   /**
    *  Returns the stereo parity of this atom. It uses the predefined values
    *  found in CDKConstants.
    *
    * @return    The stereo parity for this atom
    *
    * @see       org.openscience.cdk.CDKConstants
    * @see       #setStereoParity
    */
   public Integer getStereoParity() {
       return this.stereoParity;
   }

   /**
    *  Sets the if attribute of the AtomType object.
    *
    * @param  identifier  The new AtomTypeID value. Null if unset.
    *
    * @see    #getAtomTypeName
    */
   public void setAtomTypeName(String identifier)
   {
       this.identifier = identifier;
       notifyChanged();
   }


   /**
    *  Sets the MaxBondOrder attribute of the AtomType object.
    *
    * @param  maxBondOrder  The new MaxBondOrder value
    *
    * @see       #getMaxBondOrder
    */
   public void setMaxBondOrder(IBond.Order maxBondOrder)
   {
       this.maxBondOrder = maxBondOrder;
       notifyChanged();
   }


   /**
    *  Sets the the exact bond order sum attribute of the AtomType object.
    *
    * @param  bondOrderSum  The new bondOrderSum value
    *
    * @see       #getBondOrderSum
    */
   public void setBondOrderSum(Double bondOrderSum)
   {
       this.bondOrderSum = bondOrderSum;
       notifyChanged();
   }


   /**
    *  Gets the id attribute of the AtomType object.
    *
    * @return    The id value
    *
    * @see       #setAtomTypeName
    */
   public String getAtomTypeName()
   {
       return this.identifier;
   }


   /**
    *  Gets the MaxBondOrder attribute of the AtomType object.
    *
    * @return    The MaxBondOrder value
    *
    * @see       #setMaxBondOrder
    */
   public IBond.Order getMaxBondOrder()
   {
       return maxBondOrder;
   }


   /**
    *  Gets the bondOrderSum attribute of the AtomType object.
    *
    * @return    The bondOrderSum value
    *
    * @see       #setBondOrderSum
    */
   public Double getBondOrderSum()
   {
       return bondOrderSum;
   }

   /**
    *  Sets the formal charge of this atom.
    *
    * @param  charge  The formal charge
    *
    * @see    #getFormalCharge
    */
   public void setFormalCharge(Integer charge) {
       this.formalCharge = charge;
       notifyChanged();
   }
   
   /**
    *  Returns the formal charge of this atom.
    *
    * @return the formal charge of this atom
    *
    * @see    #setFormalCharge
    */
   public Integer getFormalCharge() {
       return this.formalCharge;
   }
   
   /**
    * Sets the formal neighbour count of this atom.
    *
    * @param  count  The neighbour count
    *
    * @see    #getFormalNeighbourCount
    */
   public void setFormalNeighbourCount(Integer count) {
       this.formalNeighbourCount = count;
   notifyChanged();
   }
   
   /**
    * Returns the formal neighbour count of this atom.
    *
    * @return the formal neighbour count of this atom
    *
    * @see    #setFormalNeighbourCount
    */
   public Integer getFormalNeighbourCount() {
       return this.formalNeighbourCount;
   }
   
   /**
    *  Sets the hybridization of this atom.
    *
    * @param  hybridization  The hybridization
    *
    * @see    #getHybridization
    */
   public void setHybridization(IAtomType.Hybridization hybridization) {
       this.hybridization = hybridization;
       notifyChanged();
   }
   
   /**
    *  Returns the hybridization of this atom.
    *
    * @return the hybridization of this atom
    *
    * @see    #setHybridization
    */
   public IAtomType.Hybridization getHybridization() {
       return this.hybridization;
   }

   /**
    *  Sets the NaturalAbundance attribute of the Isotope object.
    *
    * @param  naturalAbundance  The new NaturalAbundance value
    *
    * @see       #getNaturalAbundance
    */
   public void setNaturalAbundance(Double naturalAbundance) {
       this.naturalAbundance = naturalAbundance;
       notifyChanged();
   }


   /**
    *  Sets the ExactMass attribute of the Isotope object.
    *
    * @param  exactMass  The new ExactMass value
    *
    * @see       #getExactMass
    */
   public void setExactMass(Double exactMass) {
       this.exactMass = exactMass;
       notifyChanged();
   }


   /**
    *  Gets the NaturalAbundance attribute of the Isotope object.
    *  
    *  <p>Once instantiated all field not filled by passing parameters
    * to the constructor are null. Isotopes can be configured by using
    * the IsotopeFactory.configure() method:
    * <pre>
    *   Isotope isotope = new Isotope("C", 13);
    *   IsotopeFactory if = IsotopeFactory.getInstance(isotope.getNewBuilder());
    *   if.configure(isotope);
    * </pre>
    * </p>
    *
    * @return    The NaturalAbundance value
    *
    * @see       #setNaturalAbundance
    */
   public Double getNaturalAbundance() {
       return this.naturalAbundance;
   }


   /**
    *  Gets the ExactMass attribute of the Isotope object.
    *  <p>Once instantiated all field not filled by passing parameters
    * to the constructor are null. Isotopes can be configured by using
    * the IsotopeFactory.configure() method:
    * <pre>
    *   Isotope isotope = new Isotope("C", 13);
    *   IsotopeFactory if = IsotopeFactory.getInstance(isotope.getNewBuilder());
    *   if.configure(isotope);
    * </pre>
    * </p>
    *
    * @return    The ExactMass value
    *
    * @see       #setExactMass
    */
   public Double getExactMass() {
       return this.exactMass;
   }

   /**
    * Returns the atomic mass of this element.
    * 
    * <p>Once instantiated all field not filled by passing parameters
    * to the constructor are null. Isotopes can be configured by using
    * the IsotopeFactory.configure() method:
    * <pre>
    *   Isotope isotope = new Isotope("C", 13);
    *   IsotopeFactory if = IsotopeFactory.getInstance(isotope.getNewBuilder());
    *   if.configure(isotope);
    * </pre>
    * </p>
    *
    * @return The atomic mass of this element
    *
    * @see    #setMassNumber(Integer)
    */
   public Integer getMassNumber() {
       return this.massNumber;
   }

   /**
    * Sets the atomic mass of this element.
    *
    * @param   massNumber The atomic mass to be assigned to this element
    *
    * @see    #getMassNumber
    */
   public void setMassNumber(Integer massNumber) {
       this.massNumber = massNumber;
       notifyChanged();
   }

   /**
    * Returns the atomic number of this element.
    * 
    *  <p>Once instantiated all field not filled by passing parameters
    * to the constructor are null. Elements can be configured by using
    * the IsotopeFactory.configure() method:
    * <pre>
    *   Element element = new Element("C");
    *   IsotopeFactory if = IsotopeFactory.getInstance(element.getNewBuilder());
    *   if.configure(element);
    * </pre>
    * </p>      
    *
    * @return The atomic number of this element    
    *
    * @see    #setAtomicNumber
    */
   public Integer getAtomicNumber() {
       return this.atomicNumber;
   }

   /**
    * Sets the atomic number of this element.
    *
    * @param   atomicNumber The atomic mass to be assigned to this element
    *
    * @see    #getAtomicNumber
    */
   public void setAtomicNumber(Integer atomicNumber) {
       this.atomicNumber = atomicNumber;
       notifyChanged();
   }

   /**
    * Returns the element symbol of this element.
    *
    * @return The element symbol of this element. Null if unset.
    *
    * @see    #setSymbol
    */
   public String getSymbol() {
       return this.symbol;
   }

   /**
    * Sets the element symbol of this element.
    *
    * @param symbol The element symbol to be assigned to this atom
    *
    * @see    #getSymbol
    */
   public void setSymbol(String symbol) {
       this.symbol = symbol;
   notifyChanged();
   }

   /**
    * Sets the covalent radius for this AtomType.
    *
    * @param radius The covalent radius for this AtomType
    * @see    #getCovalentRadius
    */
   public void setCovalentRadius(Double radius) {
       this.covalentRadius = radius;
   notifyChanged();
   }

   /**
    * Returns the covalent radius for this AtomType.
    *
    * @return The covalent radius for this AtomType
    * @see    #setCovalentRadius
    */
   public Double getCovalentRadius() {
       return this.covalentRadius;
   }

   /**
    *  Sets the the exact electron valency of the AtomType object.
    *
    * @param  valency  The new valency value
    * @see #getValency
    *
    */
   public void setValency(Integer valency)
   {
       this.electronValency = valency;
       notifyChanged();
   }

   /**
    *  Gets the the exact electron valency of the AtomType object.
    *
    * @return The valency value
    * @see #setValency
    *
    */
   public Integer getValency()
   {
       return this.electronValency;
   }

   /**
    *  Lazy creation of chemObjectListeners List.
    *
    *@return    List with the ChemObjects associated.
    */
   private List<IChemObjectListener> lazyChemObjectListeners()
   {
       if (chemObjectListeners == null) {
           chemObjectListeners = new ArrayList<IChemObjectListener>();
       }
       return chemObjectListeners;
   }


   /**
    *  Use this to add yourself to this IChemObject as a listener. In order to do
    *  so, you must implement the ChemObjectListener Interface.
    *
    *@param  col  the ChemObjectListener
    *@see         #removeListener
    */
   public void addListener(IChemObjectListener col)
   {
       List<IChemObjectListener> listeners = lazyChemObjectListeners();

       if (!listeners.contains(col))
       {
           listeners.add(col);
       }
       // Should we throw an exception if col is already in here or
       // just silently ignore it?
   }


   /**
    *  Returns the number of ChemObjectListeners registered with this object.
    *
    *@return    the number of registered listeners.
    */
   public int getListenerCount() {
       if (chemObjectListeners == null) {
           return 0;
       }
       return lazyChemObjectListeners().size();
   }


   /**
    *  Use this to remove a ChemObjectListener from the ListenerList of this
    *  IChemObject. It will then not be notified of change in this object anymore.
    *
    *@param  col  The ChemObjectListener to be removed
    *@see         #addListener
    */
   public void removeListener(IChemObjectListener col) {
       if (chemObjectListeners == null) {
           return;
       }
       
       List<IChemObjectListener> listeners = lazyChemObjectListeners();
       if (listeners.contains(col)) {
           listeners.remove(col);
       }
   }


   /**
    *  This should be triggered by an method that changes the content of an object
    *  to that the registered listeners can react to it.
    */
   public void notifyChanged() {
       if (getNotification() && getListenerCount() > 0) {
           List<IChemObjectListener> listeners = lazyChemObjectListeners();
           for (Object listener : listeners) {
               ((IChemObjectListener) listener).stateChanged(
                       new ChemObjectChangeEvent(this)
               );
           }
       }
   }


   /**
    *  This should be triggered by an method that changes the content of an object
    *  to that the registered listeners can react to it. This is a version of
    *  notifyChanged() which allows to propagate a change event while preserving
    *  the original origin.
    *
    *@param  evt  A ChemObjectChangeEvent pointing to the source of where
    *      the change happend
    */
   public void notifyChanged(IChemObjectChangeEvent evt) {
       if (getNotification() && getListenerCount() > 0) {
           List<IChemObjectListener> listeners = lazyChemObjectListeners();
           for (Object listener : listeners) {
               ((IChemObjectListener) listener).stateChanged(evt);
           }
       }
   }


   /**
    * Lazy creation of properties hash.
    *
    * @return    Returns in instance of the properties
    */
   private Map<Object, Object> lazyProperties()
   {
       if (properties == null)
       {
           properties = new HashMap<Object, Object>();
       }
       return properties;
   }


   /**
    *  Sets a property for a IChemObject.
    *
    *@param  description  An object description of the property (most likely a
    *      unique string)
    *@param  property     An object with the property itself
    *@see                 #getProperty
    *@see                 #removeProperty
    */
   public void setProperty(Object description, Object property)
   {
       lazyProperties().put(description, property);
       notifyChanged();
   }


   /**
    *  Removes a property for a IChemObject.
    *
    *@param  description  The object description of the property (most likely a
    *      unique string)
    *@see                 #setProperty
    *@see                 #getProperty
    */
   public void removeProperty(Object description)
   {
       if (properties == null) {
           return;
       }
       if (lazyProperties().remove(description) != null)
           notifyChanged();
   }

   /**
    *  Returns a property for the IChemObject.
    *
    *@param  description  An object description of the property (most likely a
    *      unique string)
    *@return              The object containing the property. Returns null if
    *      propert is not set.
    *@see                 #setProperty
    *@see                 #removeProperty
    */
   public Object getProperty(Object description)
   {
       if (properties != null) {
           return lazyProperties().get(description);
       }
       return null;
   }

   /**
    *  Returns a Map with the IChemObject's properties.
    *
    *@return    The object's properties as an Hashtable
    *@see       #setProperties
    */
   public Map<Object,Object> getProperties()
   {
       return lazyProperties();
   }

   /**
    *  Returns the identifier (ID) of this object.
    *
    *@return    a String representing the ID value
    *@see       #setID
    */
   public String getID()
   {
       return this.identifier;
   }

   /**
    *  Sets the identifier (ID) of this object.
    *
    *@param  identifier  a String representing the ID value
    *@see                #getID
    */
   public void setID(String identifier)
   {
       this.identifier = identifier;
       notifyChanged();
   }

   /**
    *  Sets the value of some flag.
    *
    *@param  flag_type   Flag to set
    *@param  flag_value  Value to assign to flag
    *@see                #getFlag
    */
   public void setFlag(int flag_type, boolean flag_value)
   {
       flags[flag_type] = flag_value;
       notifyChanged();
   }

   /**
    *  Returns the value of some flag.
    *
    *@param  flag_type  Flag to retrieve the value of
    *@return            true if the flag <code>flag_type</code> is set
    *@see               #setFlag
    */
   public boolean getFlag(int flag_type)
   {
       return flags[flag_type];
   }

   /**
    *  Sets the properties of this object.
    *
    *@param  properties  a Hashtable specifying the property values
    *@see                #getProperties
    */
   public void setProperties(Map<Object,Object> properties)
   {
       Iterator<Object> keys = properties.keySet().iterator();
       while (keys.hasNext())
       {
           Object key = keys.next();
           lazyProperties().put(key, properties.get(key));
       }
       notifyChanged();
   }

   private boolean doNotification = true;

   /**
    * Sets the whole set of flags.
    *
    * @param  flagsNew    the new flags.
    * @see                #getFlags
    */
   public void setFlags(boolean[] flagsNew){
       flags=flagsNew;
   }

   /**
    * Returns the whole set of flags.
    *
    *@return    the flags.
    *@see       #setFlags
    */
   public boolean[] getFlags(){
       return(flags);
   }

   public void setNotification(boolean bool) {
       this.doNotification = bool;
   }

   public boolean getNotification() {
       return this.doNotification;
   }

   @Override
    public IChemObjectBuilder getBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean matches(IAtom atom) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }
}
