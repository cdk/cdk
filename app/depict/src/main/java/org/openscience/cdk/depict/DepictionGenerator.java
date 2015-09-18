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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.SymbolVisibility;
import org.openscience.cdk.renderer.color.CDK2DAtomColors;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.generators.standard.SelectionVisibility;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator;

import javax.vecmath.Point2d;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A high-level API for depicting molecules and reactions.
 * <p/>
 * <h4>General Usage</h4>
 * Create a generator and reuse it for multiple depictions. Configure how
 * the depiction will look using {@code with...()} methods.
 * <pre>{@code
 * DepictionGenerator dg = new DepictionGenerator().withSize(512, 512)
 *                                                 .withAtomColors();
 * for (IAtomContainer mol : mols) {
 *   dg.depict(mol).writeTo("~/mol.png");
 * }
 * }</pre>
 * <p/>
 * <h4>One Line Quick Use</h4>
 * For simplifed use we can create a generator and use it once for a single depiction.
 * <pre>{@code
 * new DepictionGenerator().depict(mol)
 *                         .writeTo("~/mol.png");
 * }</pre>
 * The intermediate {@link Depiction} object can write to many different formats
 * through a variety of API calls.
 * <pre>{@code
 * Depiction depiction = new DepictionGenerator().depict(mol);
 * <p/>
 * // quick use, format determined by name by path
 * depiction.writeTo("~/mol.png");
 * depiction.writeTo("~/mol.svg");
 * depiction.writeTo("~/mol.pdf");
 * depiction.writeTo("~/mol.jpg");
 * <p/>
 * // manually specify the format
 * depiction.writeTo(Depiction.SVG_FMT, "~/mol");
 * <p/>
 * // convert to a Java buffered image
 * BufferedImage img = depiction.toImg();
 * <p/>
 * // get the SVG XML string
 * String svg = depiction.toSvgStr();
 * }</pre>
 *
 * @author John may
 */
public final class DepictionGenerator {

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
    private final RendererModel model = new RendererModel();

    /**
     * Font used for depictions.
     */
    private final Font font;

    /**
     * Diagram generators.
     */
    private final List<IGenerator<IAtomContainer>> gens = new ArrayList<>();

    /**
     * Structure diagram generator instance.
     */
    private final StructureDiagramGenerator sdg = new StructureDiagramGenerator();

    /**
     * Flag to indicate atom numbers should be displayed.
     */
    private boolean annotateAtomNum = false;

    /**
     * Object that should be highlighted
     */
    private List<IChemObject> highlight;

    /**
     * Create a depiction generator using the standard sans-serif
     * system font.
     */
    public DepictionGenerator() {
        this(new Font(getDefaultOsFont(), Font.PLAIN, 22));
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
        for (IGenerator<IAtomContainer> gen : gens)
            model.registerParameters(gen);

        // default margin and separation is automatic
        // since it depends on raster (px) vs vector (mm)
        withParam(BasicSceneGenerator.Margin.class,
                  AUTOMATIC);
        withParam(RendererModel.Padding.class,
                  AUTOMATIC);

        sdg.setUseTemplates(false);
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

        // ensure we have coordinates, generate if not
        for (IAtomContainer mol : mols)
            ensure2dLayout(mol);

        // setup the scale
        List<IAtomContainer> molList = FluentIterable.from(mols).toList();
        withParam(BasicSceneGenerator.Scale.class,
                  caclModelScale(molList));

        // generate bound rendering elements
        final List<Bounds> molElems = generate(molList, 1, false);

        // generate titles (if enabled)
        final List<Bounds> titles = new ArrayList<>();
        if (model.get(BasicSceneGenerator.ShowMoleculeTitle.class)) {
            for (IAtomContainer mol : mols)
                titles.add(generateTitle(mol));
        }

        return new MolGridDepiction(model, molElems, titles, dimensions, nrow, ncol);
    }

    /**
     * Depict a reaction.
     *
     * @param rxn reaction instance
     * @return depiction
     * @throws CDKException a depiction could not be generated
     */
    public Depiction depict(IReaction rxn) throws CDKException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private IRenderingElement generate(IAtomContainer molecule, int atomNum) throws CDKException {

        if (annotateAtomNum) {
            for (IAtom atom : molecule.atoms()) {
                if (atom.getProperty(StandardGenerator.ANNOTATION_LABEL) != null)
                    throw new UnsupportedOperationException("Multiple annotation labels are not supported.");
                atom.setProperty(StandardGenerator.ANNOTATION_LABEL,
                                 Integer.toString(atomNum++));
            }
        }

        // highlight
        if (highlight != null) {
            for (IChemObject obj : highlight)
                obj.setProperty(StandardGenerator.HIGHLIGHT_COLOR, Color.RED);
        }

        ElementGroup grp = new ElementGroup();
        for (IGenerator<IAtomContainer> gen : gens)
            grp.add(gen.generate(molecule, model));

        // cleanup
        if (annotateAtomNum) {
            for (IAtom atom : molecule.atoms()) {
                atom.removeProperty(StandardGenerator.ANNOTATION_LABEL);
            }
        }
        if (highlight != null) {
            for (IChemObject obj : highlight)
                obj.removeProperty(StandardGenerator.HIGHLIGHT_COLOR);
        }

        return grp;
    }

    private List<Bounds> generate(List<IAtomContainer> mols, int atomNum,
                                             boolean pluses) throws CDKException {
        List<Bounds> elems = new ArrayList<>();
        int num = 0;
        for (IAtomContainer mol : mols) {
            if (pluses && elems.size() > 0)
                elems.add(null); // ToDo need 'plus icons'
            elems.add(new Bounds(generate(mol, atomNum)));
            atomNum += mol.getAtomCount();
        }
        return elems;
    }

    /**
     * Generate a bound element that is the title of the provided molecule. If title
     * is not specified an empty bounds is returned.
     *
     * @param mol molecule
     * @return bound element
     */
    private Bounds generateTitle(IAtomContainer mol) {
        String title = mol.getProperty(CDKConstants.TITLE);
        if (title == null || title.isEmpty())
            return new Bounds();
        final double scale = 1/model.get(BasicSceneGenerator.Scale.class) * model.get(RendererModel.TitleFontScale.class);
        return new Bounds(StandardGenerator.embedText(font, title, model.get(RendererModel.TitleColor.class), scale));
    }



    /**
     * Automatically generate coordinates if a user has provided a molecule without them.
     *
     * @param container a molecule
     * @throws CDKException coordinates could not be generated
     */
    private void ensure2dLayout(IAtomContainer container) throws CDKException {
        if (!GeometryUtil.has2DCoordinates(container)) {
            sdg.setMolecule(container, false);
            sdg.generateCoordinates();
        }
    }

    /**
     * Automatically generate coordinates if a user has provided reaction without them.
     *
     * @param rxn reaction
     * @throws CDKException coordinates could not be generated
     */
    private void ensure2dLayout(IReaction rxn) throws CDKException {
        for (IAtomContainer mol : rxn.getReactants().atomContainers())
            ensure2dLayout(mol);
        for (IAtomContainer mol : rxn.getProducts().atomContainers())
            ensure2dLayout(mol);
        for (IAtomContainer mol : rxn.getAgents().atomContainers())
            ensure2dLayout(mol);
    }

    /**
     * Color atom symbols using typical colors, oxygens are red, nitrogens are
     * blue, etc.
     *
     * @return this generator for method chaining
     * @see StandardGenerator.AtomColor
     * @see StandardGenerator.Highlighting
     * @see StandardGenerator.HighlightStyle
     */
    public DepictionGenerator withAtomColors() {
        withParam(StandardGenerator.AtomColor.class,
                  new CDK2DAtomColors());
        withParam(StandardGenerator.Highlighting.class,
                  StandardGenerator.HighlightStyle.OuterGlow);
        return this;
    }

    /**
     * Display atom numbers on the molecule. The numbers are based on the
     * ordering of atoms in the molecule data structure and not a
     * systematic system such as IUPAC numbering.
     *
     * @return this generator for method chaining
     */
    public DepictionGenerator withAtomNumbers() {
        annotateAtomNum = true;
        return this;
    }

    /**
     * Display a molecule title with each depiction. The title
     * is specified by setting the {@link org.openscience.cdk.CDKConstants#TITLE}
     * property.
     *
     * @return this generator for method chaining
     * @see BasicSceneGenerator.ShowMoleculeTitle
     */
    public DepictionGenerator withMolTitle() {
        withParam(BasicSceneGenerator.ShowMoleculeTitle.class,
                  true);
        return this;
    }

    /**
     * Set the color annotations (e.g. atom-numbers) will appear in.
     *
     * @param color the color of annotations
     * @return this generator for method chaining
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
     * @return this generator for method chaining
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
     * @return this generator for method chaining
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
     * @return this generator for method chaining
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
     * @return this generator for method chaining
     * @see StandardGenerator.Visibility
     */
    public DepictionGenerator withTerminalCarbons() {
        withParam(StandardGenerator.Visibility.class,
                  SelectionVisibility.disconnected(SymbolVisibility.iupacRecommendations()));
        return this;
    }

    /**
     * Display atom symbols for all atoms in the molecule.
     *
     * @return this generator for method chaining
     * @see StandardGenerator.Visibility
     */
    public DepictionGenerator withCarbonSymbols() {
        withParam(StandardGenerator.Visibility.class,
                  SymbolVisibility.all());
        return this;
    }

    /**
     * Highlight the provided set of atoms and bonds in the depiction in the
     * specified color.
     * <p/>
     * Calling this methods replaces any previous highlight. To highlight multiple
     * parts in different colours manually set the atom/bond property {@link
     * StandardGenerator#HIGHLIGHT_COLOR}.
     *
     * @param chemObjs set of atoms and bonds
     * @param color    the color to highlight
     * @return this generator for method chaining
     * @see StandardGenerator#HIGHLIGHT_COLOR
     */
    public DepictionGenerator withHighlight(Iterable<IChemObject> chemObjs, Color color) {
        this.highlight = FluentIterable.from(chemObjs).toList();
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Specify a desired size of depiction. The units depend on the output format with
     * raster images using pixels and vector graphics using millimeters. By default depictions
     * are only ever made smaller if you would also like to make depictions fill all available
     * space use the {@link #withFillToFit()} option.
     *
     * @param w max width
     * @param h max height
     * @return this generator for method chaining
     * @see #withFillToFit()
     */
    public DepictionGenerator withSize(double w, double h) {
        if (w < 0 && h >= 0 || h < 0 && w >= 0)
            throw new UnsupportedOperationException("Both width and height must be automatic");
        dimensions = w == AUTOMATIC ? Dimensions.AUTOMATIC : new Dimensions(w, h);
        return this;
    }

    /**
     * Specify a desired size of margin. The units depend on the output format with
     * raster images using pixels and vector graphics using millimeters.
     *
     * @param m margin
     * @return this generator for method chaining
     * @see BasicSceneGenerator.Margin
     */
    public DepictionGenerator withMargin(double m) {
        withParam(BasicSceneGenerator.Margin.class,
                  m);
        return this;
    }

    /**
     * Specify a desired size of padding for molecule sets and reactions. The units
     * depend on the output format with raster images using pixels and vector graphics
     * using millimeters.
     *
     * @param p padding
     * @return this generator for method chaining
     * @see RendererModel.Padding
     */
    public DepictionGenerator withPadding(double p) {
        withParam(RendererModel.Padding.class,
                  p);
        return this;
    }

    /**
     * Resize depictions to fill all available space (only if a size is specified).
     * This generally isn't wanted as very small molecules (e.g. acetaldehyde) may
     * become huge.
     *
     * @return this generator for method chaining
     * @see BasicSceneGenerator.FitToScreen
     */
    public DepictionGenerator withFillToFit() {
        withParam(BasicSceneGenerator.FitToScreen.class,
                  true);
        return this;
    }

    /**
     * Low-level option method to set a rendering model parameter.
     *
     * @param key   option key
     * @param value option value
     * @param <T>   option key type
     * @param <U>   option value type
     * @return this generator for method chaining
     */
    public <T extends IGeneratorParameter<S>, S, U extends S> DepictionGenerator withParam(Class<T> key, U value) {
        model.set(key, value);
        return this;
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
            Point2d p1 = bond.getAtom(0).getPoint2d();
            Point2d p2 = bond.getAtom(1).getPoint2d();
            // watch out for overlaid atoms (occur in multiple group Sgroups)
            if (!p1.equals(p2))
                lengths[nBonds++] = p1.distance(p2);
        }
        Arrays.sort(lengths, 0, nBonds);
        return lengths[nBonds / 2];
    }

    private double calcModelScaleForBondLength(double bondLength) {
        return model.get(BasicSceneGenerator.BondLength.class) / bondLength;
    }

    private static String getDefaultOsFont() {
        // TODO: Native Font Support - choose best for Win/Linux/OS X etc
        return Font.SANS_SERIF;
    }
}
