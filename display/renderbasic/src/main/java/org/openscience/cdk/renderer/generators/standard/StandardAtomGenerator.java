/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.renderer.generators.standard;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.generators.standard.AbbreviationLabel.FormattedText;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.openscience.cdk.renderer.generators.standard.HydrogenPosition.Above;
import static org.openscience.cdk.renderer.generators.standard.HydrogenPosition.Below;
import static org.openscience.cdk.renderer.generators.standard.HydrogenPosition.Left;
import static org.openscience.cdk.renderer.generators.standard.HydrogenPosition.Right;

/**
 * Generates {@link AtomSymbol} instances with positioned adjuncts.
 * 
 *  Note the generator is purposefully not an {@link org.openscience.cdk.renderer.generators.IGenerator}
 * and is intended as part be called from the StandardGenerator.
 *
 * @author John May
 */
final class StandardAtomGenerator {

    /**
     * Default options for spacing and sizing adjuncts, could be configruable parameters.
     */
    private final static double DEFAULT_ADJUNCT_SPACING_RATIO = 0.15d;
    private final static double DEFAULT_SUBSCRIPT_SIZE        = 0.6d;

    /**
     * The font used in the symbol.
     */
    private final Font font;

    /**
     * Relative size of the adjunct sub/super script labels (0-1).
     */
    private final double scriptSize;

    /**
     * The absolute distance to 'pad' adjunct positioning with.
     */
    private final double padding;

    /**
     * Text outline is immutable so we can create a hydrogen are reuse it.
     */
    private final TextOutline defaultHydrogenLabel;

    /**
     * Create a standard atom generator using the specified font.
     *
     * @param font the symbol font
     */
    StandardAtomGenerator(Font font) {
        this(font, DEFAULT_ADJUNCT_SPACING_RATIO, DEFAULT_SUBSCRIPT_SIZE);
    }

    /**
     * Internal constructor with required attributes.
     *
     * @param font           the font to depict symbols with
     * @param adjunctSpacing the spacing between adjuncts and the element symbol as fraction of 'H'
     *                       width
     * @param scriptSize     the size of
     */
    private StandardAtomGenerator(Font font, double adjunctSpacing, double scriptSize) {
        this.font = font;
        this.scriptSize = scriptSize;
        this.defaultHydrogenLabel = new TextOutline("H", font);
        this.padding = adjunctSpacing * defaultHydrogenLabel.getBounds().getWidth();
    }

    /**
     * Generate the displayed atom symbol for an atom in given structure with the specified hydrogen
     * position.
     *
     * @param container structure to which the atom belongs
     * @param atom      the atom to generate the symbol for
     * @param position  the hydrogen position
     * @param model     additional rendering options
     * @return atom symbol
     */
    AtomSymbol generateSymbol(IAtomContainer container, IAtom atom, HydrogenPosition position, RendererModel model) {
        if (atom instanceof IPseudoAtom) {
            IPseudoAtom pAtom = (IPseudoAtom) atom;
            if (pAtom.getAttachPointNum() <= 0) {
                if (pAtom.getLabel().equals("*")) {
                    int mass = unboxSafely(pAtom.getMassNumber(), 0);
                    int charge  = unboxSafely(pAtom.getFormalCharge(), 0);
                    int hcnt    = unboxSafely(pAtom.getImplicitHydrogenCount(), 0);
                    int nrad = container.getConnectedSingleElectronsCount(atom);
                    if (mass != 0 || charge != 0 || hcnt != 0) {
                        return generatePeriodicSymbol(0, hcnt,
                                                      mass, charge,
                                                      nrad, position);
                    }
                }
                return generatePseudoSymbol(accessPseudoLabel(pAtom, "?"), position);
            }
            else
                return null; // attach point drawn in bond generator
        } else {
            int number = unboxSafely(atom.getAtomicNumber(), Elements.ofString(atom.getSymbol()).number());

            // unset the mass if it's the major isotope (could be an option)
            Integer mass = atom.getMassNumber();
            if (number != 0 &&
                mass != null &&
                model != null &&
                model.get(StandardGenerator.OmitMajorIsotopes.class) &&
                isMajorIsotope(number, mass)) {
                mass = null;
            }

            return generatePeriodicSymbol(number, unboxSafely(atom.getImplicitHydrogenCount(), 0),
                                          unboxSafely(mass, -1), unboxSafely(atom.getFormalCharge(), 0),
                                          container.getConnectedSingleElectronsCount(atom), position);
        }
    }

    /**
     * Generates an atom symbol for a pseudo atom.
     *
     * @return the atom symbol
     */
    AtomSymbol generatePseudoSymbol(String label, HydrogenPosition position) {

        final Font italicFont = font.deriveFont(Font.BOLD)
                                    .deriveFont(Font.ITALIC);
        List<TextOutline> outlines = new ArrayList<>(3);

        int beg = 0;
        int pos = 0;
        int len = label.length();

        // upper case followed by lower case
        while (pos < len && isUpperCase(label.charAt(pos)))
            pos++;
        if (label.charAt(0) != 'R') // Ar is not A^r but 'Ra' is R^a etc
            while (pos < len && isLowerCase(label.charAt(pos)))
                pos++;

        if (pos > beg) {
            outlines.add(new TextOutline(label.substring(beg, pos), italicFont));
            beg = pos;
            // 2a etc.
            while (pos < len && isDigit(label.charAt(pos)))
                pos++;
            while (pos < len && isLowerCase(label.charAt(pos)))
                pos++;

            if (pos > beg) {
                TextOutline outline = new TextOutline(label.substring(beg, pos), italicFont);
                outline = outline.resize(scriptSize, scriptSize);
                outline = positionSuperscript(outlines.get(0), outline);
                outlines.add(outline);
            }

            int numPrimes = 0;
            PRIMES:
            while (pos < len) {
                switch (label.charAt(pos)) {
                    case '\'': numPrimes++; break;
                    case '`': numPrimes++;  break;
                    case '‘': numPrimes++;  break;
                    case '’': numPrimes++;  break;
                    case '‛': numPrimes++;  break;
                    case '“': numPrimes+=2; break;
                    case '”': numPrimes+=2; break;
                    case '′': numPrimes++;  break;
                    case '″': numPrimes+=2; break;
                    case '‴': numPrimes+=3; break;
                    case '⁗': numPrimes+=4; break;
                    case '‵': numPrimes++;  break;
                    case '‶': numPrimes+=2; break;
                    case '‷': numPrimes+=3; break;
                    case '´': numPrimes++;  break;
                    case 'ˊ': numPrimes++;  break;
                    case '́': numPrimes++;  break;
                    case '˝': numPrimes+=2; break;
                    case '̋': numPrimes+=2; break;
                    default: break PRIMES;
                }
                pos++;
            }

            if (pos < len) {
                return new AtomSymbol(new TextOutline(label, italicFont), Collections.<TextOutline>emptyList());
            } else {
                TextOutline outline = null;
                TextOutline ref = outlines.get(outlines.size()-1);
                switch (numPrimes) {
                    case 0: break;
                    case 1: outline = new TextOutline("′", font); break;
                    case 2: outline = new TextOutline("″", font); break;
                    case 3: outline = new TextOutline("‴", font); break;
                    default:
                        String lab = "";
                        while (numPrimes-->0)
                            lab += "′";
                        outline = new TextOutline(lab, font);
                        break;
                }
                if (outline != null) {
                    if (outlines.size() > 1)
                        outline = outline.resize(scriptSize, scriptSize);
                    outline = positionSuperscript(ref, outline);
                    outlines.add(outline);
                }
            }

            // line up text
            for (int i = 1; i < outlines.size(); i++) {
                TextOutline ref = outlines.get(i - 1);
                TextOutline curr = outlines.get(i);
                outlines.set(i, positionAfter(ref, curr));
            }

            return new AtomSymbol(outlines.get(0),
                                  outlines.subList(1, outlines.size()));
        } else {
            return new AtomSymbol(new TextOutline(label, italicFont), Collections.<TextOutline>emptyList());
        }
    }

    private boolean isUpperCase(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private boolean isLowerCase(char c) {
        return c >= 'a' && c <= 'z';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    AtomSymbol generateAbbreviatedSymbol(String label, HydrogenPosition position) {
        List<String> tokens = new ArrayList<>();
        if (AbbreviationLabel.parse(label, tokens)) {
            return generateAbbreviationSymbol(tokens, position);
        } else {
            return new AtomSymbol(new TextOutline(label, font), Collections.<TextOutline>emptyList());
        }
    }

    /**
     * Generate a formatted abbreviation AtomSymbol for the given Hydrogen position.
     *
     * @param tokens   the parsed tokens
     * @param position hydrogen position - determines if we reverse the label
     * @return the generated symbol
     */
    AtomSymbol generateAbbreviationSymbol(List<String> tokens, HydrogenPosition position) {

        if (position == Left)
            AbbreviationLabel.reverse(tokens);

        final TextOutline tmpRefPoint = new TextOutline("H", font);
        final List<FormattedText> fTexts = AbbreviationLabel.format(tokens);

        final Font italicFont = font.deriveFont(Font.ITALIC);

        if (position == Below || position == Above)
            AbbreviationLabel.reduce(fTexts, 1, fTexts.size());
        else
            AbbreviationLabel.reduce(fTexts, 0, fTexts.size());

        // convert to outlines
        final List<TextOutline> outlines = new ArrayList<>(fTexts.size());
        for (FormattedText fText : fTexts) {

            TextOutline outline = fText.style == AbbreviationLabel.STYLE_ITALIC
                                  ? new TextOutline(fText.text, italicFont)
                                  : new TextOutline(fText.text, font);

            // resize and position scripts
            if (fText.style == AbbreviationLabel.STYLE_SUBSCRIPT) {
                outline = outline.resize(scriptSize, scriptSize);
                outline = positionSubscript(tmpRefPoint, outline);
            } else if (fText.style == AbbreviationLabel.STYLE_SUPSCRIPT) {
                outline = outline.resize(scriptSize, scriptSize);
                outline = positionSuperscript(tmpRefPoint, outline);
            }

            outlines.add(outline);
        }

        // position the outlines relative to each other
        for (int i = 1; i < outlines.size(); i++) {
            TextOutline ref = outlines.get(i - 1);
            TextOutline curr = outlines.get(i);
            // charge aligns to symbol not a subscript part
            if (fTexts.get(i).style == AbbreviationLabel.STYLE_SUPSCRIPT &&
                fTexts.get(i - 1).style == AbbreviationLabel.STYLE_SUBSCRIPT && i > 1) {
                ref = outlines.get(i - 2);
            }
            outlines.set(i, positionAfter(ref, curr));
        }

        // find symbol where we want to attach the bond
        // this becomes the primary outline
        int index;
        if (position == Left) {
            for (index = outlines.size() - 1; index >= 0; index--)
                if ((fTexts.get(index).style & 0x1) == 0) break;
        } else {
            for (index = 0; index < outlines.size(); index++)
                if ((fTexts.get(index).style & 0x1) == 0) break;
        }

        TextOutline primary = outlines.remove(index);

        if (position == Below || position == Above) {
            double offsetX = primary.getBounds().getX() - outlines.get(0).getBounds().getX();
            double offsetY = position == Below
                             ? padding + primary.getBounds().getHeight()
                             : -primary.getBounds().getHeight() - padding;
            for (int i = 0; i < outlines.size(); i++) {
                outlines.set(i, outlines.get(i).translate(offsetX, offsetY));
            }
        }

        return new AtomSymbol(primary, outlines);
    }

    /**
     * Generate an atom symbol for a periodic element with the specified number of hydrogens, ionic
     * charge, mass,
     *
     * @param number    atomic number
     * @param hydrogens labelled hydrogen count
     * @param mass      atomic mass
     * @param charge    ionic formal charge
     * @param unpaired  number of unpaired electrons
     * @param position  placement of hydrogen
     * @return laid out atom symbol
     */
    AtomSymbol generatePeriodicSymbol(final int number, final int hydrogens, final int mass, final int charge,
                                      final int unpaired, HydrogenPosition position) {

        TextOutline element = number == 0 ? new TextOutline("*", font)
                                          : new TextOutline(Elements.ofNumber(number).symbol(), font);
        TextOutline hydrogenAdjunct = defaultHydrogenLabel;

        // the hydrogen count, charge, and mass adjuncts are script size
        TextOutline hydrogenCount = new TextOutline(Integer.toString(hydrogens), font).resize(scriptSize, scriptSize);
        TextOutline chargeAdjunct = new TextOutline(chargeAdjunctText(charge, unpaired), font).resize(scriptSize,
                                                                                                      scriptSize);
        TextOutline massAdjunct = new TextOutline(Integer.toString(mass), font).resize(scriptSize, scriptSize);

        // position each adjunct relative to the element label and each other
        hydrogenAdjunct = positionHydrogenLabel(position, element, hydrogenAdjunct);
        hydrogenCount = positionSubscript(hydrogenAdjunct, hydrogenCount);
        chargeAdjunct = positionChargeLabel(hydrogens, position, chargeAdjunct, element, hydrogenAdjunct);
        massAdjunct = positionMassLabel(massAdjunct, element);

        // when the hydrogen label is positioned to the left we may need to nudge it
        // over to account for the hydrogen count and/or the mass adjunct colliding
        // with the element label
        if (position == Left) {
            final double nudgeX = hydrogenXDodge(hydrogens, mass, element, hydrogenAdjunct, hydrogenCount, massAdjunct);
            hydrogenAdjunct = hydrogenAdjunct.translate(nudgeX, 0);
            hydrogenCount = hydrogenCount.translate(nudgeX, 0);
        }

        final List<TextOutline> adjuncts = new ArrayList<TextOutline>(4);

        if (hydrogens > 0) adjuncts.add(hydrogenAdjunct);
        if (hydrogens > 1) adjuncts.add(hydrogenCount);
        if (charge != 0 || unpaired > 0) adjuncts.add(chargeAdjunct);
        if (mass > 0) adjuncts.add(massAdjunct);

        return new AtomSymbol(element, adjuncts);
    }

    /**
     * Position the hydrogen label relative to the element label.
     *
     * @param position relative position where the hydrogen is placed
     * @param element  the outline of the element label
     * @param hydrogen the outline of the hydrogen
     * @return positioned hydrogen label
     */
    TextOutline positionHydrogenLabel(HydrogenPosition position, TextOutline element, TextOutline hydrogen) {
        final Rectangle2D elementBounds = element.getBounds();
        final Rectangle2D hydrogenBounds = hydrogen.getBounds();
        switch (position) {
            case Above:
                return hydrogen.translate(0, (elementBounds.getMinY() - padding) - hydrogenBounds.getMaxY());
            case Right:
                return hydrogen.translate((elementBounds.getMaxX() + padding) - hydrogenBounds.getMinX(), 0);
            case Below:
                return hydrogen.translate(0, (elementBounds.getMaxY() + padding) - hydrogenBounds.getMinY());
            case Left:
                return hydrogen.translate((elementBounds.getMinX() - padding) - hydrogenBounds.getMaxX(), 0);
        }
        return hydrogen; // never reached
    }

    /**
     * Positions an outline in the subscript position relative to another 'primary' label.
     *
     * @param label     a label outline
     * @param subscript the label outline to position as subscript
     * @return positioned subscript outline
     */
    TextOutline positionSubscript(TextOutline label, TextOutline subscript) {
        final Rectangle2D hydrogenBounds = label.getBounds();
        final Rectangle2D hydrogenCountBounds = subscript.getBounds();
        subscript = subscript.translate((hydrogenBounds.getMaxX() + padding) - hydrogenCountBounds.getMinX(),
                                        (hydrogenBounds.getMaxY() + (hydrogenCountBounds.getHeight() / 2)) - hydrogenCountBounds.getMaxY());
        return subscript;
    }

    TextOutline positionSuperscript(TextOutline label, TextOutline superscript) {
        final Rectangle2D labelBounds = label.getBounds();
        final Rectangle2D superscriptBounds = superscript.getBounds();
        superscript = superscript.translate((labelBounds.getMaxX() + padding) - superscriptBounds.getMinX(),
                                            (labelBounds.getMinY() - (superscriptBounds.getHeight() / 2)) - superscriptBounds.getMinY());
        return superscript;
    }

    TextOutline positionAfter(TextOutline before, TextOutline after) {
        final Rectangle2D fixedBounds = before.getBounds();
        final Rectangle2D movableBounds = after.getBounds();
        after = after.translate((fixedBounds.getMaxX() + padding) - movableBounds.getMinX(),
                                0);
        return after;
    }

    /**
     * Position the charge label on the top right of either the element or hydrogen label. Where the
     * charge is placed depends on the number of hydrogens and their position relative to the
     * element symbol.
     *
     * @param hydrogens number of hydrogen
     * @param position  position of hydrogen
     * @param charge    the charge label outline (to be positioned)
     * @param element   the element label outline
     * @param hydrogen  the hydrogen label outline
     * @return positioned charge label
     */
    TextOutline positionChargeLabel(int hydrogens,
                                    HydrogenPosition position,
                                    TextOutline charge,
                                    TextOutline element,
                                    TextOutline hydrogen) {

        final Rectangle2D chargeBounds = charge.getBounds();

        // the charge is placed to the top right of the element symbol
        // unless either the hydrogen label or the hydrogen count label
        // are in the way - in which case we place it relative to the
        // hydrogen
        Rectangle2D referenceBounds = element.getBounds();
        if (hydrogens > 0 && (position == Left || position == Right))
            referenceBounds = hydrogen.getBounds();
        if (position == Left)
            return charge.translate((referenceBounds.getMinX() - padding) - chargeBounds.getMaxX(),
                                    (referenceBounds.getMinY() - (chargeBounds.getHeight() / 2)) - chargeBounds.getMinY());
        else
            return charge.translate((referenceBounds.getMaxX() + padding) - chargeBounds.getMinX(),
                                    (referenceBounds.getMinY() - (chargeBounds.getHeight() / 2)) - chargeBounds.getMinY());
    }

    /**
     * Position the mass label relative to the element label. The mass adjunct is position to the
     * top left of the element label.
     *
     * @param massLabel    mass label outline
     * @param elementLabel element label outline
     * @return positioned mass label
     */
    TextOutline positionMassLabel(TextOutline massLabel, TextOutline elementLabel) {
        final Rectangle2D elementBounds = elementLabel.getBounds();
        final Rectangle2D massBounds = massLabel.getBounds();
        return massLabel.translate((elementBounds.getMinX() - padding) - massBounds.getMaxX(),
                                   (elementBounds.getMinY() - (massBounds.getHeight() / 2)) - massBounds.getMinY());
    }

    /**
     * If the hydrogens are position in from of the element we may need to move the hydrogen and
     * hydrogen count labels. This code assesses the positions of the mass, hydrogen, and hydrogen
     * count labels and determines the x-axis adjustment needed for the hydrogen label to dodge a
     * collision.
     *
     * @param hydrogens     number of hydrogens
     * @param mass          atomic mass
     * @param elementLabel  element label outline
     * @param hydrogenLabel hydrogen label outline
     * @param hydrogenCount hydrogen count label outline
     * @param massLabel     the mass label outline
     * @return required adjustment to x-axis
     */
    private double hydrogenXDodge(int hydrogens, int mass, TextOutline elementLabel, TextOutline hydrogenLabel,
                                  TextOutline hydrogenCount, TextOutline massLabel) {
        if (mass < 0 && hydrogens > 1) {
            return (elementLabel.getBounds().getMinX() - padding) - hydrogenCount.getBounds().getMaxX();
        } else if (mass >= 0) {
            if (hydrogens > 1) {
                return (massLabel.getBounds().getMinX() + padding) - hydrogenCount.getBounds().getMaxX();
            } else if (hydrogens > 0) {
                return (massLabel.getBounds().getMinX() - padding) - hydrogenLabel.getBounds().getMaxX();
            }
        }
        return 0;
    }

    /**
     * Utility to determine if the specified mass is the major isotope for the given atomic number.
     *
     * @param number atomic number
     * @param mass   atomic mass
     * @return the mass is the major mass for the atomic number
     */
    private boolean isMajorIsotope(int number, int mass) {
        try {
            IIsotope isotope = Isotopes.getInstance().getMajorIsotope(number);
            return isotope != null && isotope.getMassNumber().equals(mass);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Utility to safely unbox an object instance number. If the value is null, the default value is
     * returned.
     *
     * @param value        a value
     * @param defaultValue value to return if null
     * @return unbox number or the default value
     */
    private static int unboxSafely(Integer value, int defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * Characters used in the charge label.
     */
    private static final char BULLET = '•', // '\u2022'
            PLUS                     = '+', MINUS = '−'; // '\u2212' and not a hyphen

    /**
     * Create the charge adjunct text for the specified charge and number of unpaired electrons.
     *
     * @param charge   formal charge
     * @param unpaired number of unpaired electrons
     * @return adjunct text
     */
    static String chargeAdjunctText(final int charge, final int unpaired) {
        StringBuilder sb = new StringBuilder();

        if (unpaired == 1) {
            if (charge != 0) {
                sb.append('(').append(BULLET).append(')');
            } else {
                sb.append(BULLET);
            }
        } else if (unpaired > 1) {
            if (charge != 0) {
                sb.append('(').append(unpaired).append(BULLET).append(')');
            } else {
                sb.append(unpaired).append(BULLET);
            }
        }

        final char sign = charge < 0 ? MINUS : PLUS;
        final int coefficient = Math.abs(charge);

        if (coefficient > 1) sb.append(coefficient);
        if (coefficient > 0) sb.append(sign);

        return sb.toString();
    }

    /**
     * Safely access the label of a pseudo atom. If the label is null or empty, the default label is
     * returned.
     *
     * @param atom the pseudo
     * @return pseudo label
     */
    static String accessPseudoLabel(IPseudoAtom atom, String defaultLabel) {
        String label = atom.getLabel();
        if (label != null && !label.isEmpty()) return label;
        return defaultLabel;
    }
}
