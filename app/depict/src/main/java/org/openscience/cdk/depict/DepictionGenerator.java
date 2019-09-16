/* Copyright (C) 2015  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.depict;

import com.google.common.collect.FluentIterable;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.SymbolVisibility;
import org.openscience.cdk.renderer.color.CDK2DAtomColors;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.MarkedElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.BackgroundColor;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.generators.standard.SelectionVisibility;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator.DelocalisedDonutsBondDisplay;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator.ForceDelocalisedBondDisplay;
import org.openscience.cdk.tools.LoggingToolFactory;

import javax.vecmath.Point2d;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A high-level API for depicting molecules and reactions.
 *
 * <br>
 * <b>General Usage</b>
 * Create a generator and reuse it for multiple depictions. Configure how
 * the depiction will look using {@code with...()} methods.
 * <pre>{@code
 * DepictionGenerator dg = new DepictionGenerator().withSize(512, 512)
 *                                                 .withAtomColors();
 * for (IAtomContainer mol : mols) {
 *   dg.depict(mol).writeTo("~/mol.png");
 * }
 * }</pre>
 *
 * <br>
 * <b>One Line Quick Use</b>
 * For simplified use we can create a generator and use it once for a single depiction.
 * <pre>{@code
 * new DepictionGenerator().depict(mol)
 *                         .writeTo("~/mol.png");
 * }</pre>
 * The intermediate {@link Depiction} object can write to many different formats
 * through a variety of API calls.
 * <pre>{@code
 * Depiction depiction = new DepictionGenerator().depict(mol);
 * 
 * // quick use, format determined by name by path
 * depiction.writeTo("~/mol.png");
 * depiction.writeTo("~/mol.svg");
 * depiction.writeTo("~/mol.pdf");
 * depiction.writeTo("~/mol.jpg");
 * 
 * // manually specify the format
 * depiction.writeTo(Depiction.SVG_FMT, "~/mol");
 * 
 * // convert to a Java buffered image
 * BufferedImage img = depiction.toImg();
 * 
 * // get the SVG XML string
 * String svg = depiction.toSvgStr();
 * }</pre>
 *
 * @author John may
 */
@SuppressWarnings("PMD.ShortVariable")
public final class DepictionGenerator {

    /**
     * Visually distinct colors for highlighting.
     * http://stackoverflow.com/a/4382138
     * Kenneth L. Kelly and Deanne B. Judd.
     * "Color: Universal Language and Dictionary of Names",
     * National Bureau of Standards,
     * Spec. Publ. 440, Dec. 1976, 189 pages.
     */
    private static final Color[] KELLY_MAX_CONTRAST = new Color[]{
            new Color(0x00538A), // Strong Blue (sub-optimal for defective color vision)
            new Color(0x93AA00), // Vivid Yellowish Green (sub-optimal for defective color vision)
            new Color(0xC10020), // Vivid Red
            new Color(0xFFB300), // Vivid Yellow
            new Color(0x007D34), // Vivid Green (sub-optimal for defective color vision)
            new Color(0xFF6800), // Vivid Orange
            new Color(0xCEA262), // Grayish Yellow
            new Color(0x817066), // Medium Gray
            new Color(0xA6BDD7), // Very Light Blue
            new Color(0x803E75), // Strong Purple

            new Color(0xF6768E), // Strong Purplish Pink (sub-optimal for defective color vision)

            new Color(0xFF7A5C), // Strong Yellowish Pink (sub-optimal for defective color vision)
            new Color(0x53377A), // Strong Violet (sub-optimal for defective color vision)
            new Color(0xFF8E00), // Vivid Orange Yellow (sub-optimal for defective color vision)
            new Color(0xB32851), // Strong Purplish Red (sub-optimal for defective color vision)
            new Color(0xF4C800), // Vivid Greenish Yellow (sub-optimal for defective color vision)
            new Color(0x7F180D), // Strong Reddish Brown (sub-optimal for defective color vision)

            new Color(0x593315), // Deep Yellowish Brown (sub-optimal for defective color vision)
            new Color(0xF13A13), // Vivid Reddish Orange (sub-optimal for defective color vision)
            new Color(0x232C16), // Dark Olive Green (sub-optimal for defective color vision)
    };

    /**
     * Magic value for indicating automatic parameters. These can
     * be overridden by a caller.
     */
    public static double AUTOMATIC = -1;

    /**
     * Default margin for vector graphics formats.
     */
    public static double DEFAULT_MM_MARGIN = 0.56;

    /**
     * Default margin for raster graphics formats.
     */
    public static double DEFAULT_PX_MARGIN = 4;

    /**
     * The dimensions (width x height) of the depiction.
     */
    private Dimensions dimensions = Dimensions.AUTOMATIC;

    /**
     * Storage of rendering parameters.
     */
    private final Map<Class<? extends IGeneratorParameter>, IGeneratorParameter<?>> params = new HashMap<>();

    /**
     * Font used for depictions.
     */
    private final Font font;

    /**
     * Diagram generators.
     */
    private final List<IGenerator<IAtomContainer>> gens = new ArrayList<>();

    /**
     * Flag to indicate atom numbers should be displayed.
     */
    private boolean annotateAtomNum = false;

    /**
     * Flag to indicate atom values should be displayed.
     */
    private boolean annotateAtomVal = false;

    /**
     * Flag to indicate atom maps should be displayed.
     */
    private boolean annotateAtomMap = false;

    /**
     * Flag to indicate atom maps should be highlighted with colored.
     */
    private boolean highlightAtomMap = false;

    /**
     * Colors to use in atom-map highlighting.
     */
    private Color[] atomMapColors = null;

    /**
     * Reactions are aligned such that mapped atoms have the same coordinates on the left/right.
     */
    private boolean alignMappedReactions = true;

    /**
     * Object that should be highlighted
     */
    private Map<IChemObject, Color> highlight = new HashMap<>();

    /**
     * Create a depiction generator using the standard sans-serif
     * system font.
     */
    public DepictionGenerator() {
        this(new Font(getDefaultOsFont(), Font.PLAIN, 13));
        setParam(BasicSceneGenerator.BondLength.class, 26.1d);
        setParam(StandardGenerator.HashSpacing.class, 26 / 8d);
        setParam(StandardGenerator.WaveSpacing.class, 26 / 8d);
    }

    /**
     * Create a depiction generator that will render atom
     * labels using the specified AWT font.
     *
     * @param font the font to use to display
     */
    public DepictionGenerator(Font font) {
        gens.add(new BasicSceneGenerator());
        gens.add(new StandardGenerator(this.font = font));


        for (IGenerator<IAtomContainer> gen : gens) {
            for (IGeneratorParameter<?> param : gen.getParameters()) {
                params.put(param.getClass(), param);
            }
        }
        for (IGeneratorParameter<?> param : new RendererModel().getRenderingParameters()) {
            params.put(param.getClass(), param);
        }

        // default margin and separation is automatic
        // since it depends on raster (px) vs vector (mm)
        setParam(BasicSceneGenerator.Margin.class, AUTOMATIC);
        setParam(RendererModel.Padding.class, AUTOMATIC);
    }

    /**
     * Internal copy constructor.
     *
     * @param org original depiction
     */
    private DepictionGenerator(DepictionGenerator org) {
        this.annotateAtomMap = org.annotateAtomMap;
        this.annotateAtomVal = org.annotateAtomVal;
        this.annotateAtomNum = org.annotateAtomNum;
        this.highlightAtomMap = org.highlightAtomMap;
        this.atomMapColors = org.atomMapColors;
        this.dimensions = org.dimensions;
        this.font = org.font;
        this.highlight.putAll(org.highlight);
        this.gens.addAll(org.gens);
        this.params.putAll(org.params);
        this.alignMappedReactions = org.alignMappedReactions;
    }

    private <U, T extends IGeneratorParameter<U>> U getParameterValue(Class<T> key) {
        @SuppressWarnings("unchecked")
        final T param = (T) params.get(key);
        if (param == null)
            throw new IllegalArgumentException("No parameter registered: " + key + " " + params.keySet());
        return (U) param.getValue();
    }

    private <T extends IGeneratorParameter<S>, S, U extends S> void setParam(Class<T> key, U val) {
        T param = null;
        try {
            param = key.newInstance();
            param.setValue(val);
            params.put(key, param);
        } catch (InstantiationException | IllegalAccessException e) {
            LoggingToolFactory.createLoggingTool(getClass()).error("Could not copy rendering parameter: " + key);
        }
    }

    private RendererModel getModel() {
        RendererModel model = new RendererModel();
        for (IGenerator<IAtomContainer> gen : gens)
            model.registerParameters(gen);
        for (IGeneratorParameter<?> param : params.values())
            model.set(param.getClass(), param.getValue());
        return model;
    }

    /**
     * Depict a single molecule.
     *
     * @param mol molecule
     * @return depiction instance
     * @throws CDKException a depiction could not be generated
     */
    public Depiction depict(final IAtomContainer mol) throws CDKException {
        return depict(Collections.singleton(mol), 1, 1);
    }

    /**
     * Depict a set of molecules, they will be depicted in a grid. The grid
     * size (nrow x ncol) is determined automatically based on the number
     * molecules.
     *
     * @param mols molecules
     * @return depiction
     * @throws CDKException a depiction could not be generated
     * @see #depict(Iterable, int, int)
     */
    public Depiction depict(Iterable<IAtomContainer> mols) throws CDKException {
        int nMols = FluentIterable.from(mols).size();
        Dimension grid = Dimensions.determineGrid(nMols);
        return depict(mols, grid.height, grid.width);
    }

    /**
     * Depict a set of molecules, they will be depicted in a grid with the
     * specified number of rows and columns. Rows are filled first and then
     * columns.
     *
     * @param mols molecules
     * @param nrow number of rows
     * @param ncol number of columns
     * @return depiction
     * @throws CDKException a depiction could not be generated
     */
    public Depiction depict(Iterable<IAtomContainer> mols, int nrow, int ncol) throws CDKException {

        List<LayoutBackup> layoutBackups = new ArrayList<>();
        int molId = 0;
        for (IAtomContainer mol : mols) {
            if (mol == null)
                throw new NullPointerException("Null molecule provided!");
            setIfMissing(mol, MarkedElement.ID_KEY, "mol" + ++molId);
            layoutBackups.add(new LayoutBackup(mol));
        }

        // ensure we have coordinates, generate them if not
        // we also rescale the molecules such that all bond
        // lengths are the same.
        prepareCoords(mols);

        // highlight parts
        for (Map.Entry<IChemObject, Color> e : highlight.entrySet())
            e.getKey().setProperty(StandardGenerator.HIGHLIGHT_COLOR, e.getValue());

        // setup the model scale
        List<IAtomContainer> molList = FluentIterable.from(mols).toList();
        DepictionGenerator copy = this.withParam(BasicSceneGenerator.Scale.class,
                                                 caclModelScale(molList));

        // generate bound rendering elements
        final RendererModel model = copy.getModel();
        final List<Bounds> molElems = copy.generate(molList, model, 1);

        // reset molecule coordinates
        for (LayoutBackup backup : layoutBackups)
            backup.reset();

        // generate titles (if enabled)
        final List<Bounds> titles = new ArrayList<>();
        if (copy.getParameterValue(BasicSceneGenerator.ShowMoleculeTitle.class)) {
            for (IAtomContainer mol : mols)
                titles.add(copy.generateTitle(mol, model.get(BasicSceneGenerator.Scale.class)));
        }

        // remove current highlight buffer
        for (IChemObject obj : this.highlight.keySet())
            obj.removeProperty(StandardGenerator.HIGHLIGHT_COLOR);
        this.highlight.clear();

        return new MolGridDepiction(model, molElems, titles, dimensions, nrow, ncol);
    }

    /**
     * Prepare a collection of molecules for rendering. If coordinates are not
     * present they are generated, if coordinates exists they are scaled to
     * be consistent (length=1.5).
     *
     * @param mols molecules
     * @return coordinates
     * @throws CDKException
     */
    private void prepareCoords(Iterable<IAtomContainer> mols) throws CDKException {
        for (IAtomContainer mol : mols) {
            if (!ensure2dLayout(mol) && mol.getBondCount() > 0) {
                final double factor = GeometryUtil.getScaleFactor(mol, 1.5);
                GeometryUtil.scaleMolecule(mol, factor);
            }
        }
    }

    private static void setIfMissing(IChemObject chemObject, String key, String val) {
        if (chemObject.getProperty(key) == null)
            chemObject.setProperty(key, val);
    }

    /**
     * Depict a reaction.
     *
     * @param rxn reaction instance
     * @return depiction
     * @throws CDKException a depiction could not be generated
     */
    public Depiction depict(IReaction rxn) throws CDKException {

        ensure2dLayout(rxn); // can reorder components if align is enabled!

        final Color fgcol = getParameterValue(StandardGenerator.AtomColor.class).getAtomColor(rxn.getBuilder()
                                                                                                 .newInstance(IAtom.class, "C"));

        final List<IAtomContainer> reactants = toList(rxn.getReactants());
        final List<IAtomContainer> products = toList(rxn.getProducts());
        final List<IAtomContainer> agents = toList(rxn.getAgents());
        List<LayoutBackup> layoutBackups = new ArrayList<>();

        // set ids for tagging elements
        int molId = 0;
        for (IAtomContainer mol : reactants) {
            setIfMissing(mol, MarkedElement.ID_KEY, "mol" + ++molId);
            setIfMissing(mol, MarkedElement.CLASS_KEY, "reactant");
            layoutBackups.add(new LayoutBackup(mol));
        }
        for (IAtomContainer mol : products) {
            setIfMissing(mol, MarkedElement.ID_KEY, "mol" + ++molId);
            setIfMissing(mol, MarkedElement.CLASS_KEY, "product");
            layoutBackups.add(new LayoutBackup(mol));
        }
        for (IAtomContainer mol : agents) {
            setIfMissing(mol, MarkedElement.ID_KEY, "mol" + ++molId);
            setIfMissing(mol, MarkedElement.CLASS_KEY, "agent");
            layoutBackups.add(new LayoutBackup(mol));
        }

        final Map<IChemObject, Color> myHighlight = new HashMap<>();
        if (highlightAtomMap) {
            myHighlight.putAll(makeHighlightAtomMap(reactants, products));
        }
        // user highlight buffer pushes out the atom-map highlight if provided
        myHighlight.putAll(highlight);
        highlight.clear();

        prepareCoords(reactants);
        prepareCoords(products);
        prepareCoords(agents);

        // highlight parts
        for (Map.Entry<IChemObject, Color> e : myHighlight.entrySet())
            e.getKey().setProperty(StandardGenerator.HIGHLIGHT_COLOR, e.getValue());

        // setup the model scale based on bond length
        final double scale = this.caclModelScale(rxn);
        final DepictionGenerator copy = this.withParam(BasicSceneGenerator.Scale.class, scale);
        final RendererModel model = copy.getModel();

        // reactant/product/agent element generation, we number the reactants, then products then agents
        List<Bounds> reactantBounds = copy.generate(reactants, model, 1);
        List<Bounds> productBounds = copy.generate(toList(rxn.getProducts()), model, rxn.getReactantCount());
        List<Bounds> agentBounds = copy.generate(toList(rxn.getAgents()), model, rxn.getReactantCount() + rxn.getProductCount());

        // remove current highlight buffer
        for (IChemObject obj : myHighlight.keySet())
            obj.removeProperty(StandardGenerator.HIGHLIGHT_COLOR);

        // generate a 'plus' element
        Bounds plus = copy.generatePlusSymbol(scale, fgcol);

        // reset the coordinates to how they were before we invoked depict
        for (LayoutBackup backup : layoutBackups)
            backup.reset();

        final Bounds emptyBounds = new Bounds();
        final Bounds title = copy.getParameterValue(BasicSceneGenerator.ShowReactionTitle.class) ? copy.generateTitle(rxn, scale) : emptyBounds;
        final List<Bounds> reactantTitles = new ArrayList<>();
        final List<Bounds> productTitles = new ArrayList<>();
        if (copy.getParameterValue(BasicSceneGenerator.ShowMoleculeTitle.class)) {
            for (IAtomContainer reactant : reactants)
                reactantTitles.add(copy.generateTitle(reactant, scale));
            for (IAtomContainer product : products)
                productTitles.add(copy.generateTitle(product, scale));
        }

        final Bounds conditions = generateReactionConditions(rxn, fgcol, model.get(BasicSceneGenerator.Scale.class));

        return new ReactionDepiction(model,
                                     reactantBounds, productBounds, agentBounds,
                                     plus, rxn.getDirection(), dimensions,
                                     reactantTitles,
                                     productTitles,
                                     title,
                                     conditions,
                                     fgcol);
    }

    /**
     * Internal - makes a map of the highlights for reaction mapping.
     *
     * @param reactants reaction reactants
     * @param products  reaction products
     * @return the highlight map
     */
    private Map<IChemObject, Color> makeHighlightAtomMap(List<IAtomContainer> reactants,
                                                         List<IAtomContainer> products) {

        Map<IChemObject, Color> colorMap = new HashMap<>();
        Map<Integer, Color> mapToColor = new HashMap<>();
        Map<Integer, IAtom> amap = new TreeMap<>();
        int colorIdx = -1;
        for (IAtomContainer mol : reactants) {
            int prevPalletIdx = colorIdx;
            for (IAtom atom : mol.atoms()) {
                int mapidx = accessAtomMap(atom);
                if (mapidx > 0) {
                    if (prevPalletIdx == colorIdx) {
                        colorIdx++; // select next color
                        if (colorIdx >= atomMapColors.length)
                            throw new IllegalArgumentException("Not enough colors to highlight atom mapping, please provide mode");
                    }
                    Color color = atomMapColors[colorIdx];
                    colorMap.put(atom, color);
                    mapToColor.put(mapidx, color);
                    amap.put(mapidx, atom);
                }
            }
            if (colorIdx > prevPalletIdx) {
                for (IBond bond : mol.bonds()) {
                    IAtom a1 = bond.getBegin();
                    IAtom a2 = bond.getEnd();
                    Color c1 = colorMap.get(a1);
                    Color c2 = colorMap.get(a2);
                    if (c1 != null && c1 == c2)
                        colorMap.put(bond, c1);
                }
            }
        }

        for (IAtomContainer mol : products) {
            for (IAtom atom : mol.atoms()) {
                int mapidx = accessAtomMap(atom);
                if (mapidx > 0) {
                    colorMap.put(atom, mapToColor.get(mapidx));
                }
            }
            for (IBond pBnd : mol.bonds()) {
                IAtom pBeg = pBnd.getBegin();
                IAtom pEnd = pBnd.getEnd();
                Color c1 = colorMap.get(pBeg);
                Color c2 = colorMap.get(pEnd);
                if (c1 != null && c1 == c2) {
                    IAtom rBeg = amap.get(accessAtomMap(pBeg));
                    IAtom rEnd = amap.get(accessAtomMap(pEnd));
                    if (rBeg != null && rEnd != null) {
                        IBond rBnd = rBeg.getBond(rEnd);
                        if (rBnd != null &&
                            ((pBnd.isAromatic() && rBnd.isAromatic()) ||
                              rBnd.getOrder() == pBnd.getOrder())) {
                            colorMap.put(pBnd, c1);
                        } else {
                            colorMap.remove(rBnd);
                        }
                    }
                }
            }
        }

        return colorMap;
    }

    private Integer accessAtomMap(IAtom atom) {
        Integer mapidx = atom.getProperty(CDKConstants.ATOM_ATOM_MAPPING, Integer.class);
        if (mapidx == null)
            return 0;
        return mapidx;
    }

    private Bounds generatePlusSymbol(double scale, Color fgcol) {
        return new Bounds(StandardGenerator.embedText(font, "+", fgcol, 1 / scale));
    }

    private List<IAtomContainer> toList(IAtomContainerSet set) {
        return FluentIterable.from(set.atomContainers()).toList();
    }

    private IRenderingElement generate(IAtomContainer molecule, RendererModel model, int atomNum) throws CDKException {

        // tag the atom and bond ids
        String molId = molecule.getProperty(MarkedElement.ID_KEY);
        if (molId != null) {
            int atomId = 0, bondid = 0;
            for (IAtom atom : molecule.atoms())
                setIfMissing(atom, MarkedElement.ID_KEY, molId + "atm" + ++atomId);
            for (IBond bond : molecule.bonds())
                setIfMissing(bond, MarkedElement.ID_KEY, molId + "bnd" + ++bondid);
        }

        if (annotateAtomNum) {
            for (IAtom atom : molecule.atoms()) {
                if (atom.getProperty(StandardGenerator.ANNOTATION_LABEL) != null)
                    throw new UnsupportedOperationException("Multiple annotation labels are not supported.");
                atom.setProperty(StandardGenerator.ANNOTATION_LABEL,
                                 Integer.toString(atomNum++));
            }
        } else if (annotateAtomVal) {
            for (IAtom atom : molecule.atoms()) {
                if (atom.getProperty(StandardGenerator.ANNOTATION_LABEL) != null)
                    throw new UnsupportedOperationException("Multiple annotation labels are not supported.");
                atom.setProperty(StandardGenerator.ANNOTATION_LABEL,
                                 atom.getProperty(CDKConstants.COMMENT));
            }
        } else if (annotateAtomMap) {
            for (IAtom atom : molecule.atoms()) {
                if (atom.getProperty(StandardGenerator.ANNOTATION_LABEL) != null)
                    throw new UnsupportedOperationException("Multiple annotation labels are not supported.");
                int mapidx = accessAtomMap(atom);
                if (mapidx > 0) {
                    atom.setProperty(StandardGenerator.ANNOTATION_LABEL, Integer.toString(mapidx));
                }
            }
        }

        ElementGroup grp = new ElementGroup();
        for (IGenerator<IAtomContainer> gen : gens)
            grp.add(gen.generate(molecule, model));

        // cleanup
        if (annotateAtomNum || annotateAtomMap) {
            for (IAtom atom : molecule.atoms()) {
                atom.removeProperty(StandardGenerator.ANNOTATION_LABEL);
            }
        }

        return grp;
    }

    private List<Bounds> generate(List<IAtomContainer> mols, RendererModel model, int atomNum) throws CDKException {
        List<Bounds> elems = new ArrayList<>();
        int num = 0;
        for (IAtomContainer mol : mols) {
            elems.add(new Bounds(generate(mol, model, atomNum)));
            atomNum += mol.getAtomCount();
        }
        return elems;
    }

    /**
     * Generate a bound element that is the title of the provided molecule. If title
     * is not specified an empty bounds is returned.
     *
     * @param chemObj molecule or reaction
     * @return bound element
     */
    private Bounds generateTitle(IChemObject chemObj, double scale) {
        String title = chemObj.getProperty(CDKConstants.TITLE);
        if (title == null || title.isEmpty())
            return new Bounds();
        scale = 1 / scale * getParameterValue(RendererModel.TitleFontScale.class);
        return new Bounds(MarkedElement.markup(StandardGenerator.embedText(font, title, getParameterValue(RendererModel.TitleColor.class), scale),
                                               "title"));
    }

    private Bounds generateReactionConditions(IReaction chemObj, Color fg, double scale) {
        String title = chemObj.getProperty(CDKConstants.REACTION_CONDITIONS);
        if (title == null || title.isEmpty())
            return new Bounds();
        return new Bounds(MarkedElement.markup(StandardGenerator.embedText(font, title, fg, 1/scale),
                                               "conditions"));
    }


    /**
     * Automatically generate coordinates if a user has provided a molecule without them.
     *
     * @param container a molecule
     * @return if coordinates needed to be generated
     * @throws CDKException coordinates could not be generated
     */
    private boolean ensure2dLayout(IAtomContainer container) throws CDKException {
        if (!GeometryUtil.has2DCoordinates(container)) {
            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            sdg.generateCoordinates(container);
            return true;
        }
        return false;
    }

    /**
     * Automatically generate coordinates if a user has provided reaction without them.
     *
     * @param rxn reaction
     * @throws CDKException coordinates could not be generated
     */
    private void ensure2dLayout(IReaction rxn) throws CDKException {
        if (!GeometryUtil.has2DCoordinates(rxn)) {
            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            sdg.setAlignMappedReaction(alignMappedReactions);
            sdg.generateCoordinates(rxn);
        }
    }

    /**
     * Color atom symbols using typical colors, oxygens are red, nitrogens are
     * blue, etc.
     *
     * @return new generator for method chaining
     * @see StandardGenerator.AtomColor
     * @see StandardGenerator.Highlighting
     * @see StandardGenerator.HighlightStyle
     * @see CDK2DAtomColors
     */
    public DepictionGenerator withAtomColors() {
        return withAtomColors(new CDK2DAtomColors());
    }

    /**
     * Color atom symbols using provided colorer.
     *
     * @return new generator for method chaining
     * @see StandardGenerator.AtomColor
     * @see StandardGenerator.Highlighting
     * @see StandardGenerator.HighlightStyle
     * @see CDK2DAtomColors
     * @see org.openscience.cdk.renderer.color.UniColor
     */
    public DepictionGenerator withAtomColors(IAtomColorer colorer) {
        return withParam(StandardGenerator.AtomColor.class, colorer);
    }

    /**
     * Change the background color.
     *
     * @param color background color
     * @return new generator for method chaining
     * @see BackgroundColor
     */
    public DepictionGenerator withBackgroundColor(Color color) {
        return withParam(BackgroundColor.class, color);
    }

    /**
     * Highlights are shown as an outer glow around the atom symbols and bonds
     * rather than recoloring. The width of the glow can be set but defaults to
     * 4x the stroke width.
     *
     * @return new generator for method chaining
     * @see StandardGenerator.Highlighting
     * @see StandardGenerator.HighlightStyle
     */
    public DepictionGenerator withOuterGlowHighlight() {
        return withOuterGlowHighlight(4);
    }

    /**
     * Highlights are shown as an outer glow around the atom symbols and bonds
     * rather than recoloring.
     *
     * @param width width of the outer glow relative to the bond stroke
     * @return new generator for method chaining
     * @see StandardGenerator.Highlighting
     * @see StandardGenerator.HighlightStyle
     */
    public DepictionGenerator withOuterGlowHighlight(double width) {
        return withParam(StandardGenerator.Highlighting.class,
                         StandardGenerator.HighlightStyle.OuterGlow)
                .withParam(StandardGenerator.OuterGlowWidth.class,
                           width);
    }

    /**
     * Display atom numbers on the molecule or reaction. The numbers are based on the
     * ordering of atoms in the molecule data structure and not a systematic system
     * such as IUPAC numbering.
     * 
     * Note: A depiction can not have both atom numbers and atom maps visible
     * (but this can be achieved by manually setting the annotation).
     *
     * @return new generator for method chaining
     * @see #withAtomMapNumbers()
     * @see StandardGenerator#ANNOTATION_LABEL
     */
    public DepictionGenerator withAtomNumbers() {
        if (annotateAtomMap || annotateAtomVal)
            throw new IllegalArgumentException("Can not annotated atom numbers, atom values or maps are already annotated");
        DepictionGenerator copy = new DepictionGenerator(this);
        copy.annotateAtomNum = true;
        return copy;
    }

    /**
     * Display atom values on the molecule or reaction. The values need to be assigned by 
     * 
     * <pre>{@code
     * atom.setProperty(CDKConstants.COMMENT, myValueToBeDisplayedNextToAtom);
     * }</pre>
     *
     * Note: A depiction can not have both atom numbers and atom maps visible
     * (but this can be achieved by manually setting the annotation).
     *
     * @return new generator for method chaining
     * @see #withAtomMapNumbers()
     * @see StandardGenerator#ANNOTATION_LABEL
     */
    public DepictionGenerator withAtomValues() {
        if (annotateAtomNum || annotateAtomMap)
            throw new IllegalArgumentException("Can not annotated atom values, atom numbers or maps are already annotated");
        DepictionGenerator copy = new DepictionGenerator(this);
        copy.annotateAtomVal = true;
        return copy;
    }

    /**
     * Display atom-atom mapping numbers on a reaction. Each atom map index
     * is loaded from the property {@link CDKConstants#ATOM_ATOM_MAPPING}.
     * 
     * Note: A depiction can not have both atom numbers and atom
     * maps visible (but this can be achieved by manually setting
     * the annotation).
     *
     * @return new generator for method chaining
     * @see #withAtomNumbers()
     * @see CDKConstants#ATOM_ATOM_MAPPING
     * @see StandardGenerator#ANNOTATION_LABEL
     */
    public DepictionGenerator withAtomMapNumbers() {
        if (annotateAtomNum)
            throw new IllegalArgumentException("Can not annotated atom maps, atom numbers or values are already annotated");
        DepictionGenerator copy = new DepictionGenerator(this);
        copy.annotateAtomMap = true;
        return copy;
    }

    /**
     * Adds to the highlight the coloring of reaction atom-maps. The
     * optional color array is used as the pallet with which to
     * highlight. If none is provided a set of high-contrast colors
     * will be used.
     *
     * @return new generator for method chaining
     * @see #withAtomMapNumbers()
     * @see #withAtomMapHighlight()
     */
    public DepictionGenerator withAtomMapHighlight() {
        return withAtomMapHighlight(KELLY_MAX_CONTRAST);
    }

    /**
     * Adds to the highlight the coloring of reaction atom-maps. The
     * optional color array is used as the pallet with which to
     * highlight. If none is provided a set of high-contrast colors
     * will be used.
     *
     * @param colors array of colors
     * @return new generator for method chaining
     * @see #withAtomMapNumbers()
     * @see #withAtomMapHighlight()
     */
    public DepictionGenerator withAtomMapHighlight(Color[] colors) {
        DepictionGenerator copy = new DepictionGenerator(this);
        copy.highlightAtomMap = true;
        copy.atomMapColors = Arrays.copyOf(colors, colors.length);
        return copy;

    }

    /**
     * Display a molecule title with each depiction. The title
     * is specified by setting the {@link org.openscience.cdk.CDKConstants#TITLE}
     * property. For reactions only the main components have their
     * title displayed.
     *
     * @return new generator for method chaining
     * @see BasicSceneGenerator.ShowMoleculeTitle
     */
    public DepictionGenerator withMolTitle() {
        return withParam(BasicSceneGenerator.ShowMoleculeTitle.class,
                         true);
    }

    /**
     * Display a reaction title with the depiction. The title
     * is specified by setting the {@link org.openscience.cdk.CDKConstants#TITLE}
     * property on the {@link IReaction} instance.
     *
     * @return new generator for method chaining
     * @see BasicSceneGenerator.ShowReactionTitle
     */
    public DepictionGenerator withRxnTitle() {
        return withParam(BasicSceneGenerator.ShowReactionTitle.class,
                         true);
    }

    /**
     * Specifies that reactions with atom-atom mappings should have their reactants/product
     * coordinates aligned. Default: true.
     *
     * @param val setting value
     * @return new generator for method chaining
     */
    public DepictionGenerator withMappedRxnAlign(boolean val) {
        DepictionGenerator copy = new DepictionGenerator(this);
        copy.alignMappedReactions = val;
        return copy;
    }

    /**
     * Set the color annotations (e.g. atom-numbers) will appear in.
     *
     * @param color the color of annotations
     * @return new generator for method chaining
     * @see StandardGenerator.AnnotationColor
     */
    public DepictionGenerator withAnnotationColor(Color color) {
        return withParam(StandardGenerator.AnnotationColor.class,
                         color);
    }

    /**
     * Set the size of annotations relative to atom symbols.
     *
     * @param scale the scale of annotations
     * @return new generator for method chaining
     * @see StandardGenerator.AnnotationFontScale
     */
    public DepictionGenerator withAnnotationScale(double scale) {
        return withParam(StandardGenerator.AnnotationFontScale.class,
                         scale);
    }

    /**
     * Set the color titles will appear in.
     *
     * @param color the color of titles
     * @return new generator for method chaining
     * @see RendererModel.TitleColor
     */
    public DepictionGenerator withTitleColor(Color color) {
        return withParam(RendererModel.TitleColor.class,
                         color);
    }

    /**
     * Set the size of titles compared to atom symbols.
     *
     * @param scale the scale of titles
     * @return new generator for method chaining
     * @see RendererModel.TitleFontScale
     */
    public DepictionGenerator withTitleScale(double scale) {
        return withParam(RendererModel.TitleFontScale.class,
                         scale);
    }

    /**
     * Display atom symbols for terminal carbons (i.e. Methyl)
     * groups.
     *
     * @return new generator for method chaining
     * @see StandardGenerator.Visibility
     */
    public DepictionGenerator withTerminalCarbons() {
        return withParam(StandardGenerator.Visibility.class,
                         SelectionVisibility.disconnected(SymbolVisibility.iupacRecommendations()));
    }

    /**
     * Display atom symbols for all atoms in the molecule.
     *
     * @return new generator for method chaining
     * @see StandardGenerator.Visibility
     */
    public DepictionGenerator withCarbonSymbols() {
        return withParam(StandardGenerator.Visibility.class,
                         SymbolVisibility.all());
    }

    /**
     * Highlight the provided set of atoms and bonds in the depiction in the
     * specified color.
     * 
     * Calling this methods appends to the current highlight buffer. The buffer
     * is cleared after each depiction is generated (e.g. {@link #depict(IAtomContainer)}).
     *
     * @param chemObjs set of atoms and bonds
     * @param color    the color to highlight
     * @return new generator for method chaining
     * @see StandardGenerator#HIGHLIGHT_COLOR
     */
    public DepictionGenerator withHighlight(Iterable<? extends IChemObject> chemObjs, Color color) {
        DepictionGenerator copy = new DepictionGenerator(this);
        for (IChemObject chemObj : chemObjs) {
            if (chemObj instanceof IAtomContainer) {
                for (IAtom atom : ((IAtomContainer) chemObj).atoms())
                    copy.highlight.put(atom, color);
                for (IBond bond : ((IAtomContainer) chemObj).bonds())
                    copy.highlight.put(bond, color);
            }
            else copy.highlight.put(chemObj, color);
        }
        return copy;
    }

    /**
     * Specify a desired size of depiction. The units depend on the output format with
     * raster images using pixels and vector graphics using millimeters. By default depictions
     * are only ever made smaller if you would also like to make depictions fill all available
     * space use the {@link #withFillToFit()} option. 
     * 
     * Currently the size must either both be precisely specified (e.g. 256x256) or
     * automatic (e.g. {@link #AUTOMATIC}x{@link #AUTOMATIC}) you cannot for example
     * specify a fixed height and automatic width.
     *
     * @param w max width
     * @param h max height
     * @return new generator for method chaining
     * @see #withFillToFit()
     */
    public DepictionGenerator withSize(double w, double h) {
        if (w < 0 && h >= 0 || h < 0 && w >= 0)
            throw new IllegalArgumentException("Width and height must either both be automatic or both specified");
        DepictionGenerator copy = new DepictionGenerator(this);
        copy.dimensions = w == AUTOMATIC ? Dimensions.AUTOMATIC : new Dimensions(w, h);
        return copy;
    }

    /**
     * Specify a desired size of margin. The units depend on the output format with
     * raster images using pixels and vector graphics using millimeters.
     *
     * @param m margin
     * @return new generator for method chaining
     * @see BasicSceneGenerator.Margin
     */
    public DepictionGenerator withMargin(double m) {
        return withParam(BasicSceneGenerator.Margin.class,
                         m);
    }

    /**
     * Specify a desired size of padding for molecule sets and reactions. The units
     * depend on the output format with raster images using pixels and vector graphics
     * using millimeters.
     *
     * @param p padding
     * @return new generator for method chaining
     * @see RendererModel.Padding
     */
    public DepictionGenerator withPadding(double p) {
        return withParam(RendererModel.Padding.class,
                         p);
    }

    /**
     * Specify a desired zoom factor - this changes the base size of a
     * depiction and is used for uniformly making depictions bigger. If
     * you would like to simply fill all available space (not recommended)
     * use {@link #withFillToFit()}.
     * 
     * The zoom is a scaling factor, specifying a zoom of 2 is double size,
     * 0.5 half size, etc.
     *
     * @param zoom zoom factor
     * @return new generator for method chaining
     * @see BasicSceneGenerator.ZoomFactor
     */
    public DepictionGenerator withZoom(double zoom) {
        return withParam(BasicSceneGenerator.ZoomFactor.class,
                         zoom);
    }

    /**
     * Resize depictions to fill all available space (only if a size is specified).
     * This generally isn't wanted as very small molecules (e.g. acetaldehyde) may
     * become huge.
     *
     * @return new generator for method chaining
     * @see BasicSceneGenerator.FitToScreen
     */
    public DepictionGenerator withFillToFit() {
        return withParam(BasicSceneGenerator.FitToScreen.class,
                         true);
    }

    /**
     * When aromaticity is set on bonds, display this in the diagram. IUPAC
     * recommends depicting kekulé structures to avoid ambiguity but it's common
     * practice to render delocalised rings "donuts" or "life buoys". With fused
     * rings this can be somewhat confusing as you end up with three lines at
     * the fusion point. <br>
     * By default small rings are renders as donuts with dashed bonds used
     * otherwise. You can use dashed bonds always by turning off the
     * {@link DelocalisedDonutsBondDisplay}.
     *
     * @return new generator for method chaining
     * @see ForceDelocalisedBondDisplay
     * @see DelocalisedDonutsBondDisplay
     */
    public DepictionGenerator withAromaticDisplay() {
        return withParam(ForceDelocalisedBondDisplay.class,
                         true);
    }

    /**
     * Low-level option method to set a rendering model parameter.
     *
     * @param key   option key
     * @param value option value
     * @param <T>   option key type
     * @param <U>   option value type
     * @return new generator for method chaining
     */
    public <T extends IGeneratorParameter<S>, S, U extends S> DepictionGenerator withParam(Class<T> key, U value) {
        DepictionGenerator copy = new DepictionGenerator(this);
        copy.setParam(key, value);
        return copy;
    }

    private double caclModelScale(Collection<IAtomContainer> mols) {
        List<IBond> bonds = new ArrayList<>();
        for (IAtomContainer mol : mols) {
            for (IBond bond : mol.bonds()) {
                bonds.add(bond);
            }
        }
        return calcModelScaleForBondLength(medianBondLength(bonds));
    }

    private double caclModelScale(IReaction rxn) {
        List<IAtomContainer> mols = new ArrayList<>();
        for (IAtomContainer mol : rxn.getReactants().atomContainers())
            mols.add(mol);
        for (IAtomContainer mol : rxn.getProducts().atomContainers())
            mols.add(mol);
        for (IAtomContainer mol : rxn.getAgents().atomContainers())
            mols.add(mol);
        return caclModelScale(mols);
    }

    private double medianBondLength(Collection<IBond> bonds) {
        if (bonds.isEmpty())
            return 1.5;
        int nBonds = 0;
        double[] lengths = new double[bonds.size()];
        for (IBond bond : bonds) {
            Point2d p1 = bond.getBegin().getPoint2d();
            Point2d p2 = bond.getEnd().getPoint2d();
            // watch out for overlaid atoms (occur in multiple group Sgroups)
            if (!p1.equals(p2))
                lengths[nBonds++] = p1.distance(p2);
        }
        Arrays.sort(lengths, 0, nBonds);
        return lengths[nBonds / 2];
    }

    private double calcModelScaleForBondLength(double bondLength) {
        return getParameterValue(BasicSceneGenerator.BondLength.class) / bondLength;
    }

    private static String getDefaultOsFont() {
        // TODO: Native Font Support - choose best for Win/Linux/OS X etc
        return Font.SANS_SERIF;
    }

  /**
   * Utility class for storing coordinates and bond types and resetting them after use.
   */
  private static final class LayoutBackup {
        private final Point2d[]      coords;
        private final IBond.Stereo[] btypes;
        private final IAtomContainer mol;

        public LayoutBackup(IAtomContainer mol) {
            final int numAtoms = mol.getAtomCount();
            final int numBonds = mol.getBondCount();
            this.coords = new Point2d[numAtoms];
            this.btypes = new IBond.Stereo[numBonds];
            this.mol = mol;
            for (int i = 0; i < numAtoms; i++) {
                IAtom atom = mol.getAtom(i);
                coords[i] = atom.getPoint2d();
                if (coords[i] != null)
                    atom.setPoint2d(new Point2d(coords[i])); // copy
            }
            for (int i = 0; i < numBonds; i++) {
                IBond bond = mol.getBond(i);
                btypes[i] = bond.getStereo();
            }
        }

        void reset() {
            final int numAtoms = mol.getAtomCount();
            final int numBonds = mol.getBondCount();
            for (int i = 0; i < numAtoms; i++)
                mol.getAtom(i).setPoint2d(coords[i]);
            for (int i = 0; i < numBonds; i++)
                mol.getBond(i).setStereo(btypes[i]);
        }
    }
}
