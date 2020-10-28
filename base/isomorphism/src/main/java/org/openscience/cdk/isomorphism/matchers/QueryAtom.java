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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

/**
 * @cdk.module isomorphism
 * @cdk.githash
 */
public class QueryAtom extends QueryChemObject implements IQueryAtom {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(QueryAtom.class);

    /**
     * The partial charge of the atom.
     * <p>
     * The default value is {@link CDKConstants#UNSET} and serves to provide a check whether the charge has been
     * set or not
     */
    protected Double charge = (Double) CDKConstants.UNSET;

    /**
     * A 2D point specifying the location of this atom in a 2D coordinate
     * space.
     */
    protected Point2d point2d = (Point2d) CDKConstants.UNSET;

    /**
     * A 3 point specifying the location of this atom in a 3D coordinate
     * space.
     */
    protected Point3d point3d = (Point3d) CDKConstants.UNSET;

    /**
     * A 3 point specifying the location of this atom in a crystal unit cell.
     */
    protected Point3d fractionalPoint3d = (Point3d) CDKConstants.UNSET;

    /**
     * The number of implicitly bound hydrogen atoms for this atom.
     */
    protected Integer hydrogenCount = (Integer) CDKConstants.UNSET;

    /**
     * A stereo parity descriptor for the stereochemistry of this atom.
     */
    protected Integer stereoParity = (Integer) CDKConstants.UNSET;

    /**
     * The maximum bond order allowed for this atom type.
     */
    IBond.Order maxBondOrder = null;
    /**
     * The maximum sum of all bond orders allowed for this atom type.
     */
    Double bondOrderSum = (Double) CDKConstants.UNSET;

    /**
     * The covalent radius of this atom type.
     */
    Double covalentRadius = (Double) CDKConstants.UNSET;

    /**
     * The formal charge of the atom with CDKConstants.UNSET as default. Implements RFC #6.
     * <p>
     * Note that some constructors e.g. ({@link org.openscience.cdk.silent.AtomType#AtomType(String)} and
     * {@link org.openscience.cdk.silent.AtomType#AtomType(String, String)} ) will explicitly set this field to 0
     */
    protected Integer formalCharge = (Integer) CDKConstants.UNSET;

    /**
     * The hybridization state of this atom with CDKConstants.HYBRIDIZATION_UNSET
     * as default.
     */
    protected IAtomType.Hybridization hybridization = (Hybridization) CDKConstants.UNSET;

    /**
     * The electron Valency of this atom with CDKConstants.UNSET as default.
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

    /**
     * Exact mass of this isotope.
     */
    public Double exactMass;

    /**
     * Natural abundance of this isotope.
     */
    public Double naturalAbundance;

    /**
     * The mass number for this isotope.
     */
    private Integer massNumber;

    /**
     * The element symbol for this element as listed in the periodic table.
     */
    protected String symbol;

    /**
     * The atomic number for this element giving their position in the periodic table.
     */
    protected Integer atomicNumber = (Integer) CDKConstants.UNSET;

    /**
     * Atom Expression
     */
    private Expr expr = new Expr(Expr.Type.TRUE);

    public QueryAtom(String symbol, IChemObjectBuilder builder) {
        this(builder);
        this.symbol = symbol;
        this.atomicNumber = PeriodicTable.getAtomicNumber(symbol);
    }

    public QueryAtom(IChemObjectBuilder builder) {
        super(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public IAtomContainer getContainer() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IBond> bonds() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBondCount() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond getBond(IAtom atom) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the partial charge of this atom.
     *
     * @param charge The partial charge
     * @see #getCharge
     */
    @Override
    public void setCharge(Double charge) {
        this.charge = charge;
        notifyChanged();
    }

    /**
     * Returns the partial charge of this atom.
     * <p>
     * If the charge has not been set the return value is Double.NaN
     *
     * @return the charge of this atom
     * @see #setCharge
     */
    @Override
    public Double getCharge() {
        return this.charge;
    }

    /**
     * Sets the number of implicit hydrogen count of this atom.
     *
     * @param hydrogenCount The number of hydrogen atoms bonded to this atom.
     * @see #getImplicitHydrogenCount
     */
    @Override
    public void setImplicitHydrogenCount(Integer hydrogenCount) {
        this.hydrogenCount = hydrogenCount;
        notifyChanged();
    }

    /**
     * Returns the hydrogen count of this atom.
     *
     * @return The hydrogen count of this atom.
     * @see #setImplicitHydrogenCount
     */
    @Override
    public Integer getImplicitHydrogenCount() {
        return this.hydrogenCount;
    }

    /**
     * Sets a point specifying the location of this
     * atom in a 2D space.
     *
     * @param point2d A point in a 2D plane
     * @see #getPoint2d
     */
    @Override
    public void setPoint2d(Point2d point2d) {
        this.point2d = point2d;
        notifyChanged();
    }

    /**
     * Sets a point specifying the location of this
     * atom in 3D space.
     *
     * @param point3d A point in a 3-dimensional space
     * @see #getPoint3d
     */
    @Override
    public void setPoint3d(Point3d point3d) {
        this.point3d = point3d;
        notifyChanged();
    }

    /**
     * Sets a point specifying the location of this
     * atom in a Crystal unit cell.
     *
     * @param point3d A point in a 3d fractional unit cell space
     * @see #getFractionalPoint3d
     * @see org.openscience.cdk.Crystal
     */
    @Override
    public void setFractionalPoint3d(Point3d point3d) {
        this.fractionalPoint3d = point3d;
        notifyChanged();
    }

    /**
     * Sets the stereo parity for this atom.
     *
     * @param stereoParity The stereo parity for this atom
     * @see org.openscience.cdk.CDKConstants for predefined values.
     * @see #getStereoParity
     */
    @Override
    public void setStereoParity(Integer stereoParity) {
        this.stereoParity = stereoParity;
        notifyChanged();
    }

    /**
     * Returns a point specifying the location of this
     * atom in a 2D space.
     *
     * @return A point in a 2D plane. Null if unset.
     * @see #setPoint2d
     */
    @Override
    public Point2d getPoint2d() {
        return this.point2d;
    }

    /**
     * Returns a point specifying the location of this
     * atom in a 3D space.
     *
     * @return A point in 3-dimensional space. Null if unset.
     * @see #setPoint3d
     */
    @Override
    public Point3d getPoint3d() {
        return this.point3d;
    }

    /**
     * Returns a point specifying the location of this
     * atom in a Crystal unit cell.
     *
     * @return A point in 3d fractional unit cell space. Null if unset.
     * @see #setFractionalPoint3d
     * @see org.openscience.cdk.CDKConstants for predefined values.
     */
    @Override
    public Point3d getFractionalPoint3d() {
        return this.fractionalPoint3d;
    }

    /**
     * Returns the stereo parity of this atom. It uses the predefined values
     * found in CDKConstants.
     *
     * @return The stereo parity for this atom
     * @see org.openscience.cdk.CDKConstants
     * @see #setStereoParity
     */
    @Override
    public Integer getStereoParity() {
        return this.stereoParity;
    }

    /**
     * Sets the if attribute of the AtomType object.
     *
     * @param identifier The new AtomTypeID value. Null if unset.
     * @see #getAtomTypeName
     */
    @Override
    public void setAtomTypeName(String identifier) {
        this.identifier = identifier;
        notifyChanged();
    }

    /**
     * Sets the MaxBondOrder attribute of the AtomType object.
     *
     * @param maxBondOrder The new MaxBondOrder value
     * @see #getMaxBondOrder
     */
    @Override
    public void setMaxBondOrder(IBond.Order maxBondOrder) {
        this.maxBondOrder = maxBondOrder;
        notifyChanged();
    }

    /**
     * Sets the the exact bond order sum attribute of the AtomType object.
     *
     * @param bondOrderSum The new bondOrderSum value
     * @see #getBondOrderSum
     */
    @Override
    public void setBondOrderSum(Double bondOrderSum) {
        this.bondOrderSum = bondOrderSum;
        notifyChanged();
    }

    /**
     * Gets the id attribute of the AtomType object.
     *
     * @return The id value
     * @see #setAtomTypeName
     */
    @Override
    public String getAtomTypeName() {
        return this.identifier;
    }

    /**
     * Gets the MaxBondOrder attribute of the AtomType object.
     *
     * @return The MaxBondOrder value
     * @see #setMaxBondOrder
     */
    @Override
    public IBond.Order getMaxBondOrder() {
        return maxBondOrder;
    }

    /**
     * Gets the bondOrderSum attribute of the AtomType object.
     *
     * @return The bondOrderSum value
     * @see #setBondOrderSum
     */
    @Override
    public Double getBondOrderSum() {
        return bondOrderSum;
    }

    /**
     * Sets the formal charge of this atom.
     *
     * @param charge The formal charge
     * @see #getFormalCharge
     */
    @Override
    public void setFormalCharge(Integer charge) {
        this.formalCharge = charge;
        notifyChanged();
    }

    /**
     * Returns the formal charge of this atom.
     *
     * @return the formal charge of this atom
     * @see #setFormalCharge
     */
    @Override
    public Integer getFormalCharge() {
        return this.formalCharge;
    }

    /**
     * Sets the formal neighbour count of this atom.
     *
     * @param count The neighbour count
     * @see #getFormalNeighbourCount
     */
    @Override
    public void setFormalNeighbourCount(Integer count) {
        this.formalNeighbourCount = count;
        notifyChanged();
    }

    /**
     * Returns the formal neighbour count of this atom.
     *
     * @return the formal neighbour count of this atom
     * @see #setFormalNeighbourCount
     */
    @Override
    public Integer getFormalNeighbourCount() {
        return this.formalNeighbourCount;
    }

    /**
     * Sets the hybridization of this atom.
     *
     * @param hybridization The hybridization
     * @see #getHybridization
     */
    @Override
    public void setHybridization(IAtomType.Hybridization hybridization) {
        this.hybridization = hybridization;
        notifyChanged();
    }

    /**
     * Returns the hybridization of this atom.
     *
     * @return the hybridization of this atom
     * @see #setHybridization
     */
    @Override
    public IAtomType.Hybridization getHybridization() {
        return this.hybridization;
    }

    /**
     * Sets the NaturalAbundance attribute of the Isotope object.
     *
     * @param naturalAbundance The new NaturalAbundance value
     * @see #getNaturalAbundance
     */
    @Override
    public void setNaturalAbundance(Double naturalAbundance) {
        this.naturalAbundance = naturalAbundance;
        notifyChanged();
    }

    /**
     * Sets the ExactMass attribute of the Isotope object.
     *
     * @param exactMass The new ExactMass value
     * @see #getExactMass
     */
    @Override
    public void setExactMass(Double exactMass) {
        this.exactMass = exactMass;
        notifyChanged();
    }

    /**
     * Gets the NaturalAbundance attribute of the Isotope object.
     *
     * <p>Once instantiated all field not filled by passing parameters
     * to the constructor are null. Isotopes can be configured by using
     * the IsotopeFactory.configure() method:
     * </p>
     * <pre>
     *   Isotope isotope = new Isotope("C", 13);
     *   IsotopeFactory if = IsotopeFactory.getInstance(isotope.getNewBuilder());
     *   if.configure(isotope);
     * </pre>
     *
     * @return The NaturalAbundance value
     * @see #setNaturalAbundance
     */
    @Override
    public Double getNaturalAbundance() {
        return this.naturalAbundance;
    }

    /**
     * Gets the ExactMass attribute of the Isotope object.
     * <p>Once instantiated all field not filled by passing parameters
     * to the constructor are null. Isotopes can be configured by using
     * the IsotopeFactory.configure() method:
     * </p>
     * <pre>
     *   Isotope isotope = new Isotope("C", 13);
     *   IsotopeFactory if = IsotopeFactory.getInstance(isotope.getNewBuilder());
     *   if.configure(isotope);
     * </pre>
     *
     * @return The ExactMass value
     * @see #setExactMass
     */
    @Override
    public Double getExactMass() {
        return this.exactMass;
    }

    /**
     * Returns the atomic mass of this element.
     *
     * <p>Once instantiated all field not filled by passing parameters
     * to the constructor are null. Isotopes can be configured by using
     * the IsotopeFactory.configure() method:
     * </p>
     * <pre>
     *   Isotope isotope = new Isotope("C", 13);
     *   IsotopeFactory if = IsotopeFactory.getInstance(isotope.getNewBuilder());
     *   if.configure(isotope);
     * </pre>
     *
     * @return The atomic mass of this element
     * @see #setMassNumber(Integer)
     */
    @Override
    public Integer getMassNumber() {
        return this.massNumber;
    }

    /**
     * Sets the atomic mass of this element.
     *
     * @param massNumber The atomic mass to be assigned to this element
     * @see #getMassNumber
     */
    @Override
    public void setMassNumber(Integer massNumber) {
        this.massNumber = massNumber;
        notifyChanged();
    }

    /**
     * Returns the atomic number of this element.
     *
     * <p>Once instantiated all field not filled by passing parameters
     * to the constructor are null. Elements can be configured by using
     * the IsotopeFactory.configure() method:</p>
     * <pre>
     *   Element element = new Element("C");
     *   IsotopeFactory if = IsotopeFactory.getInstance(element.getNewBuilder());
     *   if.configure(element);
     * </pre>
     *
     * @return The atomic number of this element
     * @see #setAtomicNumber
     */
    @Override
    public Integer getAtomicNumber() {
        return this.atomicNumber;
    }

    /**
     * Sets the atomic number of this element.
     *
     * @param atomicNumber The atomic mass to be assigned to this element
     * @see #getAtomicNumber
     */
    @Override
    public void setAtomicNumber(Integer atomicNumber) {
        this.atomicNumber = atomicNumber;
        notifyChanged();
    }

    /**
     * Returns the element symbol of this element.
     *
     * @return The element symbol of this element. Null if unset.
     * @see #setSymbol
     */
    @Override
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * Sets the element symbol of this element.
     *
     * @param symbol The element symbol to be assigned to this atom
     * @see #getSymbol
     */
    @Override
    public void setSymbol(String symbol) {
        this.symbol = symbol;
        notifyChanged();
    }

    /**
     * Sets the covalent radius for this AtomType.
     *
     * @param radius The covalent radius for this AtomType
     * @see #getCovalentRadius
     */
    @Override
    public void setCovalentRadius(Double radius) {
        this.covalentRadius = radius;
        notifyChanged();
    }

    /**
     * Returns the covalent radius for this AtomType.
     *
     * @return The covalent radius for this AtomType
     * @see #setCovalentRadius
     */
    @Override
    public Double getCovalentRadius() {
        return this.covalentRadius;
    }

    /**
     * Sets the the exact electron valency of the AtomType object.
     *
     * @param valency The new valency value
     * @see #getValency
     */
    @Override
    public void setValency(Integer valency) {
        this.electronValency = valency;
        notifyChanged();
    }

    /**
     * Gets the the exact electron valency of the AtomType object.
     *
     * @return The valency value
     * @see #setValency
     */
    @Override
    public Integer getValency() {
        return this.electronValency;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAromatic() {
        return getFlag(CDKConstants.ISAROMATIC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIsAromatic(boolean arom) {
        setFlag(CDKConstants.ISAROMATIC, arom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInRing() {
        return getFlag(CDKConstants.ISINRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIsInRing(boolean ring) {
        setFlag(CDKConstants.ISINRING, ring);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMapIdx() {
        Integer mapidx = getProperty(CDKConstants.ATOM_ATOM_MAPPING);
        if (mapidx == null)
            return 0;
        return mapidx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMapIdx(int mapidx) {
        if (mapidx < 0)
            throw new IllegalArgumentException("setMapIdx(val) value must be >= 0");
        setProperty(CDKConstants.ATOM_ATOM_MAPPING, mapidx);
    }

    /**
     * Set the atom-expression predicate for this query atom.
     *
     * @param expr the expression
     */
    public void setExpression(Expr expr) {
        this.expr = expr;
    }

    /**
     * Get the atom-expression predicate for this query atom.
     *
     * @return the expression
     */
    public Expr getExpression() {
        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(IAtom atom) {
        return expr.matches(atom);
    }

    @Override
    public IAtom clone() throws CloneNotSupportedException {
        // XXX: clone always dodgy
        return (IAtom) super.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AtomRef)
            return super.equals(((AtomRef) obj).deref());
        return super.equals(obj);
    }

    /**
     * Create a new query atom with the given an expression.
     *
     * <pre>{@code
     * // oxygen in a ring
     * Expr expr = new Expr(IS_IN_RING);
     * expr.and(new Expr(ELEMENT, 8));
     * new QueryAtom(expr);
     * }</pre>
     *
     * @param expr the expr
     */
    public QueryAtom(Expr expr) {
        this((IChemObjectBuilder) null);
        this.expr.set(expr);
    }

    /**
     * Create a new query atom with the given an predicate expression type.
     *
     * <pre>{@code
     * new QueryAtom(IS_IN_RING);
     * }</pre>
     *
     * @param type the expr type
     */
    public QueryAtom(Expr.Type type) {
        this(new Expr(type));
    }

    /**
     * Create a new query atom with the given an value expression type.
     *
     * <pre>{@code
     * // oxygen
     * new QueryAtom(ELEMENT, 8);
     * }</pre>
     *
     * @param type the expr type
     * @param val  the expr value
     */
    public QueryAtom(Expr.Type type, int val) {
        this(new Expr(type, val));
    }
}
