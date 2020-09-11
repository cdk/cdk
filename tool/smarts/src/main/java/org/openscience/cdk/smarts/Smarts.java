/*
 * Copyright (c) 2018 NextMove Software
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk.smarts;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ReactionRole;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openscience.cdk.isomorphism.matchers.Expr.Type.*;

/**
 * Parse and generate the SMARTS query language. Given an {@link IAtomContainer}
 * a SMARTS pattern is parsed and new
 * {@link org.openscience.cdk.isomorphism.matchers.IQueryAtom}s and
 * {@link org.openscience.cdk.isomorphism.matchers.IQueryBond}s are appended
 * to the connection table. Each query atom/bond contains an {@link Expr} that
 * describes the predicate to check when matching. This {@link Expr} is also
 * used for generating SMARTS.
 * <br>
 * <pre>
 * {@code
 * IAtomContainer mol = ...;
 * if (Smarts.parse(mol, "[aD3]a-a([aD3])[aD3]")) {
 *     String smarts = Smarts.generate(mol);
 * }
 * }
 * </pre>
 * When parsing SMARTS several flavors are available. The flavors affect how
 * queries are interpreted. The following flavors are available:
 * <ul>
 *     <li>{@link #FLAVOR_LOOSE} - allows all unambiguous extensions.</li>
 *     <li>{@link #FLAVOR_DAYLIGHT} - no extensions, as documented in
 *         <a href="http://www.daylight.com/dayhtml/doc/theory/theory.smarts.html">
 *         Daylight theory manual</a>.</li>
 *     <li>{@link #FLAVOR_CACTVS} - '[#X]' hetero atom, '[#G8]' periodic group 8,
 *         '[G]' or '[i]' means insaturation. '[Z2]' means 2 aliphatic hetero
 *         neighbors, '[z2]' means 2 aliphatic hetero </li>
 *     <li>{@link #FLAVOR_MOE} - '[#X]' hetero atom, '[#G8]' periodic group 8,
 *         '[i]' insaturation.</li>
 *     <li>{@link #FLAVOR_OECHEM} - '[R3]' means ring bond count 3 (e.g. [x3])
 *         instead of in 3 rings (problems with SSSR uniqueness). '[^2]' matches
 *         hybridisation (2=Sp2)</li>
 *     <li>{@link #FLAVOR_CDK} - Same as {@link #FLAVOR_LOOSE}</li>
 *     <li>{@link #FLAVOR_CDK_LEGACY} - '[D3]' means heavy degree 3 instead of
 *         explicit degree 3. '[^2]' means hybridisation (2=Sp2). '[G8]' periodic
 *         group 8</li>
 * </ul>
 * <br>
 * In addition to the flavors above CACTVS toolkit style ranges are supported.
 * For example <code>[D{2-4}]</code> means degree 2, 3, or 4. On writing such
 * ranges are converted to <code>[D2,D3,D4]</code>.
 */
public final class Smarts {

    public static final int FLAVOR_LOOSE      = 0x01;
    public static final int FLAVOR_DAYLIGHT   = 0x02;
    public static final int FLAVOR_CACTVS     = 0x04;
    public static final int FLAVOR_MOE        = 0x08;
    public static final int FLAVOR_OECHEM     = 0x10;
    public static final int FLAVOR_CDK        = FLAVOR_LOOSE;
    public static final int FLAVOR_CDK_LEGACY = 0x40;

    // input flags
    private static final int BOND_UNSPEC = '?';
    private static final int BOND_UP     = '/';
    private static final int BOND_DOWN   = '\\';

    // flags used for generating bond stereo
    private static final int BSTEREO_ANY             = 0b111;
    private static final int BSTEREO_INVALID         = 0b000;
    private static final int BSTEREO_CIS             = 0b100;
    private static final int BSTEREO_TRANS           = 0b010;
    private static final int BSTEREO_UNSPEC          = 0b001;
    private static final int BSTEREO_CIS_OR_TRANS    = 0b110;
    private static final int BSTEREO_CIS_OR_UNSPEC   = 0b101;
    private static final int BSTEREO_TRANS_OR_UNSPEC = 0b011;

    // symbols used for encoding bond stereo
    private static final String BSTEREO_UP      = "/";
    private static final String BSTEREO_DN      = "\\";
    private static final String BSTEREO_NEITHER = "!/!\\";
    private static final String BSTEREO_EITHER  = "/,\\";
    private static final String BSTEREO_UPU     = "/?";
    private static final String BSTEREO_DNU     = "\\?";


    private static final class SmartsError {
        private String str;
        private int    pos;
        private String mesg;

        public SmartsError(String str, int pos, String mesg) {
            this.str = str;
            this.pos = pos;
            this.mesg = mesg;
        }
    }

    public static ThreadLocal<SmartsError> lastError = new ThreadLocal<>();

    private static void setErrorMesg(String sma, int pos, String str) {
        lastError.set(new SmartsError(sma, pos, str));
    }

    /**
     * Access the error message from previously parsed SMARTS (when
     * {@link #parse}=false).
     *
     * @return the error message, or null if none
     */
    public static String getLastErrorMesg() {
        SmartsError error = lastError.get();
        if (error != null)
            return error.mesg;
        return null;
    }

    /**
     * Access a display of the error position from previously parsed SMARTS
     * (when {@link #parse}=false)
     *
     * @return the error message, or null if none
     */
    public static String getLastErrorLocation() {
        SmartsError error = lastError.get();
        if (error != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(error.str);
            sb.append('\n');
            char[] cs = new char[error.pos-1];
            Arrays.fill(cs, ' ');
            sb.append(cs);
            sb.append('^');
            sb.append('\n');
            return sb.toString();
        }
        return null;
    }

    private static final class LocalNbrs {
        List<IBond> bonds = new ArrayList<>(4);
        boolean     isFirst;

        LocalNbrs(boolean first) {
            this.isFirst = first;
        }
    }

    private static final class Parser {
        public  String         error;
        private String         str;
        private IAtomContainer mol;
        private int            flav;
        private int            pos;

        private IAtom     prev;
        private QueryBond bond;
        private Deque<IAtom>            stack   = new ArrayDeque<>();
        private IBond                   rings[] = new IBond[100];
        private Map<IAtom, LocalNbrs>   local   = new HashMap<>();
        private Set<IAtom>              astereo = new HashSet<>();
        private Set<IBond>              bstereo = new HashSet<>();
        private int numRingOpens;
        private ReactionRole role = ReactionRole.None;
        private int numComponents;
        private int curComponentId;

        public Parser(IAtomContainer mol, String str, int flav) {
            this.str = str;
            this.mol = mol;
            this.flav = flav;
            this.pos = 0;
        }

        IBond addBond(IAtom atom, IBond bond) {
            if (atom.equals(bond.getBegin())) {
                mol.addBond(bond);
                bond = mol.getBond(mol.getBondCount() - 1);
            }
            LocalNbrs nbrs = local.get(atom);
            if (nbrs == null)
                local.put(atom, nbrs = new LocalNbrs(false));
            nbrs.bonds.add(bond);
            return bond;
        }

        int nextUnsignedInt() {
            if (!isDigit(peek()))
                return -1;
            int res = next() - '0';
            while (isDigit(peek()))
                res = 10 * res + (next() - '0');
            return res;
        }

        boolean parseExplicitHydrogen(IAtom atom, Expr dest) {
            int mark    = pos;
            int isotope = nextUnsignedInt();
            if (str.charAt(pos++) != 'H') {
                pos = mark; // reset
                return false;
            }
            Expr hExpr = isotope < 0 ?
                         new Expr(Expr.Type.ELEMENT, 1) :
                         new Expr(Expr.Type.AND,
                                  new Expr(Expr.Type.ISOTOPE, isotope),
                                  new Expr(Expr.Type.ELEMENT, 1));
            if (peek() == '+') {
                pos++;
                int num = nextUnsignedInt();
                if (num < 0) {
                    num = 1;
                    while (peek() == '+') {
                        next();
                        num++;
                    }
                }
                hExpr.and(new Expr(Expr.Type.FORMAL_CHARGE, +num));
            } else if (peek() == '-') {
                pos++;
                int num = nextUnsignedInt();
                if (num < 0) {
                    num = 1;
                    while (peek() == '-') {
                        next();
                        num++;
                    }
                }
                hExpr.and(new Expr(FORMAL_CHARGE, -num));
            }

            // atom mapping
            if (peek() == ':') {
                next();
                int num = nextUnsignedInt();
                if (num < 0) {
                    pos = mark;
                    return false;
                }
                atom.setProperty(CDKConstants.ATOM_ATOM_MAPPING, num);
            }

            if (peek() == ']') {
                pos++;
                dest.set(hExpr);
                return true;
            } else {
                pos = mark;
                return false;
            }
        }

        private boolean parseRange(Expr expr) {
            if (next() != '{')
                return false;
            int lo = nextUnsignedInt();
            if (next() != '-')
                return false;
            int hi = nextUnsignedInt();
            Expr.Type type = expr.type();

            // adjusted types
            switch (type) {
                case HAS_IMPLICIT_HYDROGEN:
                    type = IMPL_H_COUNT;
                    break;
            }

            expr.setPrimitive(type, lo);
            for (int i = lo + 1; i <= hi; i++)
                expr.or(new Expr(type, i));
            return next() == '}';
        }

        private boolean parseGt(Expr expr) {
            if (next() != '>')
                return false;
            int lo = nextUnsignedInt();
            Expr.Type type = expr.type();

            // adjusted types
            switch (type) {
                case HAS_IMPLICIT_HYDROGEN:
                    type = IMPL_H_COUNT;
                    break;
            }

            expr.setPrimitive(type, 0);
            expr.negate();
            for (int i = 1; i <= lo; i++)
                expr.and(new Expr(type, i).negate());
            return true;
        }

        private boolean parseLt(Expr expr) {
            if (next() != '<')
                return false;
            int lo = nextUnsignedInt();
            Expr.Type type = expr.type();

            // adjusted types
            switch (type) {
                case HAS_IMPLICIT_HYDROGEN:
                    type = IMPL_H_COUNT;
                    break;
            }

            expr.setPrimitive(type, 0);
            for (int i = 1; i < lo; i++)
                expr.or(new Expr(type, i));
            return true;
        }

        boolean parseAtomExpr(IAtom atom, Expr dest, char lastOp) {
            Expr expr = null;
            int  num;
            char currOp;
            while (true) {
                currOp = '&'; // implicit and
                switch (next()) {
                    case '*':
                        expr = new Expr(TRUE);
                        break;
                    case 'A':
                        switch (next()) {
                            case 'c': // Ac=Actinium
                                expr = new Expr(ELEMENT, 89);
                                break;
                            case 'g': // Ag=Silver
                                expr = new Expr(ELEMENT, 47);
                                break;
                            case 'l': // Al=Aluminum
                                expr = new Expr(ALIPHATIC_ELEMENT, 13);
                                break;
                            case 'm': // Am=Americium
                                expr = new Expr(ELEMENT, 95);
                                break;
                            case 'r': // Ar=Argon
                                expr = new Expr(ELEMENT, 18);
                                break;
                            case 's': // As=Arsenic
                                expr = new Expr(ALIPHATIC_ELEMENT, 33);
                                break;
                            case 't': // At=Astatine
                                expr = new Expr(ELEMENT, 85);
                                break;
                            case 'u': // Au=Gold
                                expr = new Expr(ELEMENT, 79);
                                break;
                            default:  // A=None
                                unget();
                                expr = new Expr(IS_ALIPHATIC);
                                break;
                        }
                        break;
                    case 'B':
                        switch (next()) {
                            case 'a': // Ba=Barium
                                expr = new Expr(ELEMENT, 56);
                                break;
                            case 'e': // Be=Beryllium
                                expr = new Expr(ELEMENT, 4);
                                break;
                            case 'h': // Bh=Bohrium
                                expr = new Expr(ELEMENT, 107);
                                break;
                            case 'i': // Bi=Bismuth
                                expr = new Expr(ELEMENT, 83);
                                break;
                            case 'k': // Bk=Berkelium
                                expr = new Expr(ELEMENT, 97);
                                break;
                            case 'r': // Br=Bromine
                                expr = new Expr(ELEMENT, 35);
                                break;
                            default:  // B=Boron
                                unget();
                                expr = new Expr(ALIPHATIC_ELEMENT, 5);
                                break;
                        }
                        break;
                    case 'C':
                        switch (next()) {
                            case 'a': // Ca=Calcium
                                expr = new Expr(ELEMENT, 20);
                                break;
                            case 'd': // Cd=Cadmium
                                expr = new Expr(ELEMENT, 48);
                                break;
                            case 'e': // Ce=Cerium
                                expr = new Expr(ELEMENT, 58);
                                break;
                            case 'f': // Cf=Californium
                                expr = new Expr(ELEMENT, 98);
                                break;
                            case 'l': // Cl=Chlorine
                                expr = new Expr(ELEMENT, 17);
                                break;
                            case 'm': // Cm=Curium
                                expr = new Expr(ELEMENT, 96);
                                break;
                            case 'n': // Cn=Copernicium
                                expr = new Expr(ELEMENT, 112);
                                break;
                            case 'o': // Co=Cobalt
                                expr = new Expr(ELEMENT, 27);
                                break;
                            case 'r': // Cr=Chromium
                                expr = new Expr(ELEMENT, 24);
                                break;
                            case 's': // Cs=Cesium
                                expr = new Expr(ELEMENT, 55);
                                break;
                            case 'u': // Cu=Copper
                                expr = new Expr(ELEMENT, 29);
                                break;
                            default:  // C=Carbon
                                unget();
                                expr = new Expr(ALIPHATIC_ELEMENT, 6);
                                break;
                        }
                        break;
                    case 'D':
                        switch (next()) {
                            case 'b': // Db=Dubnium
                                expr = new Expr(ELEMENT, 105);
                                break;
                            case 's': // Ds=Darmstadtium
                                expr = new Expr(ELEMENT, 110);
                                break;
                            case 'y': // Dy=Dysprosium
                                expr = new Expr(ELEMENT, 66);
                                break;
                            default:  // D=Degree
                                unget();
                                num = nextUnsignedInt();
                                if (num < 0) {
                                    if (isFlavor(FLAVOR_CDK_LEGACY))
                                        expr = new Expr(HEAVY_DEGREE, 1);
                                    else
                                        expr = new Expr(DEGREE, 1);
                                    switch (peek()) {
                                        case '{':
                                            // CACTVS style ranges D{0-2}
                                            if (!parseRange(expr))
                                                return false;
                                            break;
                                        case '>':
                                            // Lilly/CACTVS/NextMove inequalities
                                            if (!parseGt(expr))
                                                return false;
                                            break;
                                        case '<':
                                            // Lilly/CACTVS/NextMove inequalities
                                            if (!parseLt(expr))
                                                return false;
                                            break;
                                    }
                                } else {
                                    if (isFlavor(FLAVOR_CDK_LEGACY))
                                        expr = new Expr(HEAVY_DEGREE, num);
                                    else
                                        expr = new Expr(DEGREE, num);
                                }
                                break;
                        }
                        break;
                    case 'E':
                        switch (next()) {
                            case 'r': // Er=Erbium
                                expr = new Expr(ELEMENT, 68);
                                break;
                            case 's': // Es=Einsteinium
                                expr = new Expr(ELEMENT, 99);
                                break;
                            case 'u': // Eu=Europium
                                expr = new Expr(ELEMENT, 63);
                                break;
                            default:  // E=None
                                return false;
                        }
                        break;
                    case 'F':
                        switch (next()) {
                            case 'e': // Fe=Iron
                                expr = new Expr(ELEMENT, 26);
                                break;
                            case 'l': // Fl=Flerovium
                                expr = new Expr(ELEMENT, 114);
                                break;
                            case 'm': // Fm=Fermium
                                expr = new Expr(ELEMENT, 100);
                                break;
                            case 'r': // Fr=Francium
                                expr = new Expr(ELEMENT, 87);
                                break;
                            default:  // F=Fluorine
                                unget();
                                expr = new Expr(ELEMENT, 9);
                                break;
                        }
                        break;
                    case 'G':
                        switch (next()) {
                            case 'a': // Ga=Gallium
                                expr = new Expr(ELEMENT, 31);
                                break;
                            case 'd': // Gd=Gadolinium
                                expr = new Expr(ELEMENT, 64);
                                break;
                            case 'e': // Ge=Germanium
                                expr = new Expr(ALIPHATIC_ELEMENT, 32);
                                break;
                            default:  // G=None or Periodic Group or Insaturation
                                unget();
                                num = nextUnsignedInt();
                                if (num <= 0 || num > 18)
                                    return false;
                                if (isFlavor(FLAVOR_CDK_LEGACY))
                                    expr = new Expr(PERIODIC_GROUP, num);
                                else if (isFlavor(FLAVOR_CACTVS))
                                    expr = new Expr(INSATURATION, num);
                                else
                                    return false;
                                break;
                        }
                        break;
                    case 'H':
                        switch (next()) {
                            case 'e': // He=Helium
                                expr = new Expr(ELEMENT, 2);
                                break;
                            case 'f': // Hf=Hafnium
                                expr = new Expr(ELEMENT, 72);
                                break;
                            case 'g': // Hg=Mercury
                                expr = new Expr(ELEMENT, 80);
                                break;
                            case 'o': // Ho=Holmium
                                expr = new Expr(ELEMENT, 67);
                                break;
                            case 's': // Hs=Hassium
                                expr = new Expr(ELEMENT, 108);
                                break;
                            default:  // H=Hydrogen
                                unget();
                                num = nextUnsignedInt();
                                if (num < 0) {
                                    expr = new Expr(TOTAL_H_COUNT, 1);
                                    switch (peek()) {
                                        case '{':
                                            // CACTVS style ranges H{0-2}
                                            if (!parseRange(expr))
                                                return false;
                                            break;
                                        case '>':
                                            // Lilly/CACTVS/NextMove inequalities
                                            if (!parseGt(expr))
                                                return false;
                                            break;
                                        case '<':
                                            // Lilly/CACTVS/NextMove inequalities
                                            if (!parseLt(expr))
                                                return false;
                                            break;
                                    }
                                } else
                                    expr = new Expr(TOTAL_H_COUNT, num);
                                break;
                        }
                        break;
                    case 'I':
                        switch (next()) {
                            case 'n': // In=Indium
                                expr = new Expr(ELEMENT, 49);
                                break;
                            case 'r': // Ir=Iridium
                                expr = new Expr(ELEMENT, 77);
                                break;
                            default:  // I=Iodine
                                unget();
                                expr = new Expr(ELEMENT, 53);
                                break;
                        }
                        break;
                    case 'K':
                        switch (next()) {
                            case 'r': // Kr=Krypton
                                expr = new Expr(ELEMENT, 36);
                                break;
                            default:  // K=Potassium
                                unget();
                                expr = new Expr(ELEMENT, 19);
                                break;
                        }
                        break;
                    case 'L':
                        switch (next()) {
                            case 'a': // La=Lanthanum
                                expr = new Expr(ELEMENT, 57);
                                break;
                            case 'i': // Li=Lithium
                                expr = new Expr(ELEMENT, 3);
                                break;
                            case 'r': // Lr=Lawrencium
                                expr = new Expr(ELEMENT, 103);
                                break;
                            case 'u': // Lu=Lutetium
                                expr = new Expr(ELEMENT, 71);
                                break;
                            case 'v': // Lv=Livermorium
                                expr = new Expr(ELEMENT, 116);
                                break;
                            default:  // L=None
                                return false;
                        }
                        break;
                    case 'M':
                        switch (next()) {
                            case 'c': // Mc=Moscovium
                                expr = new Expr(ELEMENT, 115);
                                break;
                            case 'd': // Md=Mendelevium
                                expr = new Expr(ELEMENT, 101);
                                break;
                            case 'g': // Mg=Magnesium
                                expr = new Expr(ELEMENT, 12);
                                break;
                            case 'n': // Mn=Manganese
                                expr = new Expr(ELEMENT, 25);
                                break;
                            case 'o': // Mo=Molybdenum
                                expr = new Expr(ELEMENT, 42);
                                break;
                            case 't': // Mt=Meitnerium
                                expr = new Expr(ELEMENT, 109);
                                break;
                            default:  // M=None
                                return false;
                        }
                        break;
                    case 'N':
                        switch (next()) {
                            case 'a': // Na=Sodium
                                expr = new Expr(ELEMENT, 11);
                                break;
                            case 'b': // Nb=Niobium
                                expr = new Expr(ELEMENT, 41);
                                break;
                            case 'd': // Nd=Neodymium
                                expr = new Expr(ELEMENT, 60);
                                break;
                            case 'e': // Ne=Neon
                                expr = new Expr(ELEMENT, 10);
                                break;
                            case 'h': // Nh=Nihonium
                                expr = new Expr(ELEMENT, 113);
                                break;
                            case 'i': // Ni=Nickel
                                expr = new Expr(ELEMENT, 28);
                                break;
                            case 'o': // No=Nobelium
                                expr = new Expr(ELEMENT, 102);
                                break;
                            case 'p': // Np=Neptunium
                                expr = new Expr(ELEMENT, 93);
                                break;
                            default:  // N=Nitrogen
                                unget();
                                expr = new Expr(ALIPHATIC_ELEMENT, 7);
                                break;
                        }
                        break;
                    case 'O':
                        switch (next()) {
                            case 'g': // Og=Oganesson
                                expr = new Expr(ELEMENT, 118);
                                break;
                            case 's': // Os=Osmium
                                expr = new Expr(ELEMENT, 76);
                                break;
                            default:  // O=Oxygen
                                unget();
                                expr = new Expr(ALIPHATIC_ELEMENT, 8);
                                break;
                        }
                        break;
                    case 'P':
                        switch (next()) {
                            case 'a': // Pa=Protactinium
                                expr = new Expr(ELEMENT, 91);
                                break;
                            case 'b': // Pb=Lead
                                expr = new Expr(ELEMENT, 82);
                                break;
                            case 'd': // Pd=Palladium
                                expr = new Expr(ELEMENT, 46);
                                break;
                            case 'm': // Pm=Promethium
                                expr = new Expr(ELEMENT, 61);
                                break;
                            case 'o': // Po=Polonium
                                expr = new Expr(ELEMENT, 84);
                                break;
                            case 'r': // Pr=Praseodymium
                                expr = new Expr(ELEMENT, 59);
                                break;
                            case 't': // Pt=Platinum
                                expr = new Expr(ELEMENT, 78);
                                break;
                            case 'u': // Pu=Plutonium
                                expr = new Expr(ELEMENT, 94);
                                break;
                            default:  // P=Phosphorus
                                unget();
                                expr = new Expr(ALIPHATIC_ELEMENT, 15);
                                break;
                        }
                        break;
                    case 'Q':
                        return false;
                    case 'R':
                        switch (next()) {
                            case 'a': // Ra=Radium
                                expr = new Expr(ELEMENT, 88);
                                break;
                            case 'b': // Rb=Rubidium
                                expr = new Expr(ELEMENT, 37);
                                break;
                            case 'e': // Re=Rhenium
                                expr = new Expr(ELEMENT, 75);
                                break;
                            case 'f': // Rf=Rutherfordium
                                expr = new Expr(ELEMENT, 104);
                                break;
                            case 'g': // Rg=Roentgenium
                                expr = new Expr(ELEMENT, 111);
                                break;
                            case 'h': // Rh=Rhodium
                                expr = new Expr(ELEMENT, 45);
                                break;
                            case 'n': // Rn=Radon
                                expr = new Expr(ELEMENT, 86);
                                break;
                            case 'u': // Ru=Ruthenium
                                expr = new Expr(ELEMENT, 44);
                                break;
                            default:  // R=Ring Count
                                unget();
                                num = nextUnsignedInt();
                                if (num < 0) {
                                    expr = new Expr(Expr.Type.IS_IN_RING);
                                    switch (peek()) {
                                        case '{':
                                            // CACTVS style ranges H{0-2}
                                            expr.setPrimitive(RING_COUNT, 0);
                                            if (!parseRange(expr))
                                                return false;
                                            break;
                                        case '>':
                                            // Lilly/CACTVS/NextMove inequalities
                                            expr.setPrimitive(RING_COUNT, 0);
                                            if (!parseGt(expr))
                                                return false;
                                            break;
                                        case '<':
                                            // Lilly/CACTVS/NextMove inequalities
                                            expr.setPrimitive(RING_COUNT, 0);
                                            if (!parseLt(expr))
                                                return false;
                                            break;
                                    }
                                }
                                else if (num == 0)
                                    expr = new Expr(Expr.Type.IS_IN_CHAIN);
                                else if (isFlavor(FLAVOR_OECHEM))
                                    expr = new Expr(Expr.Type.RING_BOND_COUNT, num);
                                else
                                    expr = new Expr(Expr.Type.RING_COUNT, num);
                                break;
                        }
                        break;
                    case 'S':
                        switch (next()) {
                            case 'b': // Sb=Antimony
                                expr = new Expr(ALIPHATIC_ELEMENT, 51);
                                break;
                            case 'c': // Sc=Scandium
                                expr = new Expr(ELEMENT, 21);
                                break;
                            case 'e': // Se=Selenium
                                expr = new Expr(ALIPHATIC_ELEMENT, 34);
                                break;
                            case 'g': // Sg=Seaborgium
                                expr = new Expr(ELEMENT, 106);
                                break;
                            case 'i': // Si=Silicon
                                expr = new Expr(ALIPHATIC_ELEMENT, 14);
                                break;
                            case 'm': // Sm=Samarium
                                expr = new Expr(ELEMENT, 62);
                                break;
                            case 'n': // Sn=Tin
                                expr = new Expr(ELEMENT, 50);
                                break;
                            case 'r': // Sr=Strontium
                                expr = new Expr(ELEMENT, 38);
                                break;
                            default:  // S=Sulfur
                                unget();
                                expr = new Expr(ALIPHATIC_ELEMENT, 16);
                                break;
                        }
                        break;
                    case 'T':
                        switch (next()) {
                            case 'a': // Ta=Tantalum
                                expr = new Expr(ELEMENT, 73);
                                break;
                            case 'b': // Tb=Terbium
                                expr = new Expr(ELEMENT, 65);
                                break;
                            case 'c': // Tc=Technetium
                                expr = new Expr(ELEMENT, 43);
                                break;
                            case 'e': // Te=Tellurium
                                expr = new Expr(ALIPHATIC_ELEMENT, 52);
                                break;
                            case 'h': // Th=Thorium
                                expr = new Expr(ELEMENT, 90);
                                break;
                            case 'i': // Ti=Titanium
                                expr = new Expr(ELEMENT, 22);
                                break;
                            case 'l': // Tl=Thallium
                                expr = new Expr(ELEMENT, 81);
                                break;
                            case 'm': // Tm=Thulium
                                expr = new Expr(ELEMENT, 69);
                                break;
                            case 's': // Ts=Tennessine
                                expr = new Expr(ELEMENT, 117);
                                break;
                            default:  // T=None
                                return false;
                        }
                        break;
                    case 'U':
                        switch (next()) {
                            default:  // U=Uranium
                                unget();
                                expr = new Expr(ELEMENT, 92);
                                break;
                        }
                        break;
                    case 'V':
                        switch (next()) {
                            default:  // V=Vanadium
                                unget();
                                expr = new Expr(ELEMENT, 23);
                                break;
                        }
                        break;
                    case 'W':
                        switch (next()) {
                            default:  // W=Tungsten
                                unget();
                                expr = new Expr(ELEMENT, 74);
                                break;
                        }
                        break;
                    case 'X':
                        switch (next()) {
                            case 'e': // Xe=Xenon
                                expr = new Expr(ELEMENT, 54);
                                break;
                            default:  // X=Connectivity
                                unget();
                                num = nextUnsignedInt();
                                if (num < 0) {
                                    expr = new Expr(TOTAL_DEGREE, 1);
                                    switch (peek()) {
                                        case '{':
                                            // CACTVS style ranges X{0-2}
                                            if (!parseRange(expr))
                                                return false;
                                            break;
                                        case '>':
                                            // Lilly/CACTVS/NextMove inequalities
                                            if (!parseGt(expr))
                                                return false;
                                            break;
                                        case '<':
                                            // Lilly/CACTVS/NextMove inequalities
                                            if (!parseLt(expr))
                                                return false;
                                            break;
                                    }
                                } else
                                    expr = new Expr(TOTAL_DEGREE, num);
                                break;
                        }
                        break;
                    case 'Y':
                        switch (next()) {
                            case 'b': // Yb=Ytterbium
                                expr = new Expr(ELEMENT, 70);
                                break;
                            default:  // Y=Yttrium
                                unget();
                                expr = new Expr(ELEMENT, 39);
                                break;
                        }
                        break;
                    case 'Z':
                        switch (next()) {
                            case 'n': // Zn=Zinc
                                expr = new Expr(ELEMENT, 30);
                                break;
                            case 'r': // Zr=Zirconium
                                expr = new Expr(ELEMENT, 40);
                                break;
                            default:  // Z=None
                                unget();
                                num = nextUnsignedInt();
                                if (isFlavor(FLAVOR_DAYLIGHT)) {
                                    if (num < 0)
                                        expr = new Expr(IS_IN_RING);
                                    else if (num == 0)
                                        expr = new Expr(IS_IN_CHAIN);
                                    else
                                        expr = new Expr(RING_SIZE, num);
                                } else if (isFlavor(FLAVOR_CACTVS)) {
                                    if (num < 0)
                                        expr = new Expr(HAS_ALIPHATIC_HETERO_SUBSTITUENT);
                                    else if (num == 0)
                                        expr = new Expr(HAS_ALIPHATIC_HETERO_SUBSTITUENT).negate();
                                    else
                                        expr = new Expr(ALIPHATIC_HETERO_SUBSTITUENT_COUNT, num);
                                } else {
                                    return false;
                                }
                                break;
                        }
                        break;
                    case 'a':
                        switch (next()) {
                            case 'l': // al=Aluminum (aromatic)
                                expr = new Expr(AROMATIC_ELEMENT, 13);
                                break;
                            case 's': // as=Arsenic (aromatic)
                                expr = new Expr(AROMATIC_ELEMENT, 33);
                                break;
                            default:
                                unget();
                                expr = new Expr(IS_AROMATIC);
                                break;
                        }
                        break;
                    case 'b':
                        switch (next()) {
                            default:  // b=Boron (aromatic)
                                unget();
                                expr = new Expr(AROMATIC_ELEMENT, 5);
                                break;
                        }
                        break;
                    case 'c':
                        expr = new Expr(AROMATIC_ELEMENT, 6);
                        break;
                    case 'n':
                        expr = new Expr(AROMATIC_ELEMENT, 7);
                        break;
                    case 'o':
                        expr = new Expr(AROMATIC_ELEMENT, 8);
                        break;
                    case 'p':
                        expr = new Expr(AROMATIC_ELEMENT, 15);
                        break;
                    case 's':
                        switch (next()) {
                            case 'b': // sb=Antimony (aromatic)
                                expr = new Expr(AROMATIC_ELEMENT, 51);
                                break;
                            case 'e': // se=Selenium (aromatic)
                                expr = new Expr(AROMATIC_ELEMENT, 34);
                                break;
                            case 'i': // si=Silicon (aromatic)
                                expr = new Expr(AROMATIC_ELEMENT, 14);
                                break;
                            default:  // s=Sulfur (aromatic)
                                unget();
                                expr = new Expr(AROMATIC_ELEMENT, 16);
                                break;
                        }
                        break;
                    case 't':
                        switch (next()) {
                            case 'e': // te=Tellurium (aromatic)
                                expr = new Expr(AROMATIC_ELEMENT, 52);
                                break;
                            default:
                                unget();
                                return false;
                        }
                        break;


                    case 'r':
                        num = nextUnsignedInt();
                        if (num < 0) {
                            expr = new Expr(Expr.Type.IS_IN_RING);
                            // CACTVS style ranges r{0-2}
                            if (peek() == '{') {
                                expr.setPrimitive(RING_SMALLEST, 0);
                                if (!parseRange(expr))
                                    return false;
                            }
                        }
                        else if (num == 0)
                            expr = new Expr(Expr.Type.IS_IN_CHAIN);
                        else if (num > 2)
                            expr = new Expr(Expr.Type.RING_SMALLEST, num);
                        else
                            return false;
                        break;
                    case 'v':
                        num = nextUnsignedInt();
                        if (num < 0) {
                            expr = new Expr(Expr.Type.VALENCE, 1);
                            // CACTVS style ranges v{0-2}
                            switch (peek()) {
                                case '{':
                                    // CACTVS style ranges v{0-2}
                                    if (!parseRange(expr))
                                        return false;
                                    break;
                                case '>':
                                    // Lilly/CACTVS/NextMove inequalities
                                    if (!parseGt(expr))
                                        return false;
                                    break;
                                case '<':
                                    // Lilly/CACTVS/NextMove inequalities
                                    if (!parseLt(expr))
                                        return false;
                                    break;
                            }
                        } else
                            expr = new Expr(Expr.Type.VALENCE, num);
                        break;
                    case 'h':
                        num = nextUnsignedInt();
                        if (num < 0) {
                            expr = new Expr(Expr.Type.HAS_IMPLICIT_HYDROGEN);
                            switch (peek()) {
                                case '{':
                                    // CACTVS style ranges h{0-2}
                                    if (!parseRange(expr))
                                        return false;
                                    break;
                                case '>':
                                    // Lilly/CACTVS/NextMove inequalities
                                    if (!parseGt(expr))
                                        return false;
                                    break;
                                case '<':
                                    // Lilly/CACTVS/NextMove inequalities
                                    if (!parseLt(expr))
                                        return false;
                                    break;
                            }
                        }
                        else
                            expr = new Expr(Expr.Type.IMPL_H_COUNT, num);
                        break;
                    case 'x':
                        num = nextUnsignedInt();
                        if (num < 0) {
                            expr = new Expr(Expr.Type.IS_IN_RING);
                            switch (peek()) {
                                case '{':
                                    // CACTVS style ranges x{0-2}
                                    expr.setPrimitive(RING_BOND_COUNT, 0);
                                    if (!parseRange(expr))
                                        return false;
                                    break;
                                case '>':
                                    // Lilly/CACTVS/NextMove inequalities
                                    expr.setPrimitive(RING_BOND_COUNT, 0);
                                    if (!parseGt(expr))
                                        return false;
                                    break;
                                case '<':
                                    // Lilly/CACTVS/NextMove inequalities
                                    expr.setPrimitive(RING_BOND_COUNT, 0);
                                    if (!parseLt(expr))
                                        return false;
                                    break;
                            }
                        }
                        else if (num == 0)
                            expr = new Expr(Expr.Type.IS_IN_CHAIN);
                        else if (num > 1)
                            expr = new Expr(Expr.Type.RING_BOND_COUNT, num);
                        else
                            return false;
                        break;
                    case '#':
                        num = nextUnsignedInt();
                        if (num < 0) {
                            if (isFlavor(FLAVOR_LOOSE | FLAVOR_CACTVS | FLAVOR_MOE)) {
                                switch (next()) {
                                    case 'X':
                                        expr = new Expr(Expr.Type.IS_HETERO);
                                        break;
                                    case 'G':
                                        num = nextUnsignedInt();
                                        if (num <= 0 || num > 18)
                                            return false;
                                        expr = new Expr(Expr.Type.PERIODIC_GROUP, num);
                                        break;
                                    default:
                                        return false;
                                }

                            } else {
                                return false;
                            }
                        } else {
                            expr = new Expr(Expr.Type.ELEMENT, num);
                        }
                        break;
                    case '^':
                        if (!isFlavor(FLAVOR_LOOSE | FLAVOR_OECHEM | FLAVOR_CDK_LEGACY))
                            return false;
                        num = nextUnsignedInt();
                        if (num <= 0 || num > 8)
                            return false;
                        expr = new Expr(Expr.Type.HYBRIDISATION_NUMBER, num);
                        break;
                    case 'i':
                        if (!isFlavor(FLAVOR_MOE | FLAVOR_CACTVS | FLAVOR_LOOSE))
                            return false;
                        num = nextUnsignedInt();
                        if (num <= 0 || num > 8)
                            expr = new Expr(UNSATURATED);
                        else
                            expr = new Expr(Expr.Type.INSATURATION, num);
                        break;
                    case 'z':
                        if (!isFlavor(FLAVOR_CACTVS))
                            return false;
                        num = nextUnsignedInt();
                        if (num < 0)
                            expr = new Expr(HAS_HETERO_SUBSTITUENT);
                        else if (num == 0)
                            expr = new Expr(HAS_HETERO_SUBSTITUENT).negate();
                        else
                            expr = new Expr(HETERO_SUBSTITUENT_COUNT, num);
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        unget();
                        num = nextUnsignedInt();
                        if (num == 0)
                            expr = new Expr(Expr.Type.HAS_UNSPEC_ISOTOPE);
                        else
                            expr = new Expr(Expr.Type.ISOTOPE, num);
                        break;
                    case '-':
                        num = nextUnsignedInt();
                        if (num < 0) {
                            num = 1;
                            while (peek() == '-') {
                                num++;
                                pos++;
                            }
                        }
                        expr = new Expr(Expr.Type.FORMAL_CHARGE, -num);
                        break;
                    case '+':
                        num = nextUnsignedInt();
                        if (num < 0) {
                            num = 1;
                            while (peek() == '+') {
                                num++;
                                pos++;
                            }
                        }
                        expr = new Expr(Expr.Type.FORMAL_CHARGE, +num);
                        break;
                    case '@':
                        num = IStereoElement.LEFT;
                        if (peek() == '@') {
                            next();
                            num = IStereoElement.RIGHT;
                        }
                        expr = new Expr(Expr.Type.STEREOCHEMISTRY, num);
                        // "or unspecified"
                        if (peek() == '?') {
                            next();
                            expr.or(new Expr(Expr.Type.STEREOCHEMISTRY, 0));
                        }

                        // neigbours will be index on 'finish()'
                        astereo.add(atom);
                        break;

                    case '&':
                        if (dest.type() == NONE)
                            return false;
                        expr = new Expr(Expr.Type.NONE);
                        if (!parseAtomExpr(atom, expr, '&'))
                            return false;
                        break;
                    case ';':
                        if (dest.type() == NONE)
                            return false;
                        if (hasPrecedence(lastOp, ';'))
                            return true;
                        expr = new Expr(Expr.Type.NONE);
                        if (!parseAtomExpr(atom, expr, ';'))
                            return false;
                        break;
                    case ',':
                        if (dest.type() == NONE)
                            return false;
                        if (hasPrecedence(lastOp, ','))
                            return true;
                        expr = new Expr(Expr.Type.NONE);
                        if (!parseAtomExpr(atom, expr, ','))
                            return false;
                        currOp = ',';
                        break;
                    case '!':
                        expr = new Expr(Expr.Type.NONE);
                        if (!parseAtomExpr(atom, expr, '!'))
                            return false;
                        expr.negate();
                        break;
                    case '$':
                        if (next() != '(')
                            return false;
                        int beg = pos;
                        int end = beg;
                        int depth = 1;
                        while (end < str.length()) {
                            switch (str.charAt(end++)) {
                                case '(':
                                    depth++;
                                    break;
                                case ')':
                                    depth--;
                                    break;
                            }
                            if (depth == 0)
                                break;
                        }
                        if (end == str.length())
                            return false;
                        IAtomContainer submol = new QueryAtomContainer(mol.getBuilder());
                        if (!new Parser(submol, str.substring(beg, end - 1), flav).parse())
                            return false;
                        if (submol.getAtomCount() == 1) {
                            expr = ((QueryAtom) AtomRef.deref(submol.getAtom(0)))
                                    .getExpression();
                        } else {
                            expr = new Expr(Expr.Type.RECURSIVE, submol);
                        }
                        pos = end;
                        break;
                    case ':':
                        if (expr == null)
                            return false;
                        num = nextUnsignedInt();
                        if (num < 0)
                            return false;
                        if (num != 0)
                            atom.setProperty(CDKConstants.ATOM_ATOM_MAPPING, num);
                        // should be add end of expr
                        if (lastOp != 0)
                            return peek() == ']';
                        else
                            return next() == ']';
                    case ']':
                        if (dest == null || dest.type() == Expr.Type.NONE)
                            return false;
                        if (lastOp != 0)
                            unget();
                        return true;
                    default:
                        return false;
                }

                if (dest.type() == Expr.Type.NONE) {
                    dest.set(expr);
                    // negation is tightest binding
                    if (lastOp == '!')
                        return true;
                } else {
                    switch (currOp) {
                        case '&':
                            dest.and(expr);
                            break;
                        case ',':
                            dest.or(expr);
                            break;
                    }
                }
            }
        }

        private boolean isFlavor(int flav) {
            return (this.flav & flav) != 0;
        }

        private boolean parseBondExpr(Expr dest, IBond bond, char lastOp) {
            Expr expr;
            char currOp;
            while (true) {
                currOp = '&';
                switch (next()) {
                    case '-':
                        expr = new Expr(ALIPHATIC_ORDER, 1);
                        break;
                    case '=':
                        expr = new Expr(ALIPHATIC_ORDER, 2);
                        break;
                    case '#':
                        expr = new Expr(ALIPHATIC_ORDER, 3);
                        break;
                    case '$':
                        expr = new Expr(ALIPHATIC_ORDER, 4);
                        break;
                    case ':':
                        expr = new Expr(Expr.Type.IS_AROMATIC);
                        break;
                    case '~':
                        expr = new Expr(Expr.Type.TRUE);
                        break;
                    case '@':
                        expr = new Expr(Expr.Type.IS_IN_RING);
                        break;
                    case '&':
                        if (dest.type() == Expr.Type.NONE)
                            return false;
                        expr = new Expr(Expr.Type.NONE);
                        if (!parseBondExpr(expr, bond, '&'))
                            return false;
                        break;
                    case ';':
                        if (dest.type() == Expr.Type.NONE)
                            return false;
                        if (hasPrecedence(lastOp, ';'))
                            return true;
                        expr = new Expr(Expr.Type.NONE);
                        if (!parseBondExpr(expr, bond, ';'))
                            return false;
                        break;
                    case ',':
                        if (dest.type() == Expr.Type.NONE)
                            return false;
                        if (hasPrecedence(lastOp, ','))
                            return true;
                        expr = new Expr(Expr.Type.NONE);
                        if (!parseBondExpr(expr, bond, ','))
                            return false;
                        currOp = ',';
                        break;
                    case '!':
                        expr = new Expr(Expr.Type.NONE);
                        if (!parseBondExpr(expr, bond, '!'))
                            return false;
                        expr.negate();
                        break;
                    case '/':
                        expr = new Expr(Expr.Type.STEREOCHEMISTRY, BOND_UP);
                        if (peek() == '?') {
                            next();
                            expr.or(new Expr(Expr.Type.STEREOCHEMISTRY, BOND_UNSPEC));
                        }
                        bstereo.add(bond);
                        break;
                    case '\\':
                        expr = new Expr(Expr.Type.STEREOCHEMISTRY, BOND_DOWN);
                        if (peek() == '?') {
                            next();
                            expr.or(new Expr(Expr.Type.STEREOCHEMISTRY, BOND_UNSPEC));
                        }
                        bstereo.add(bond);
                        break;
                    default:
                        pos--;
                        return dest.type() != Expr.Type.NONE;
                }

                if (dest.type() == Expr.Type.NONE) {
                    dest.set(expr);
                    // negation is tightest binding
                    if (lastOp == '!')
                        return true;
                } else {
                    switch (currOp) {
                        case '&':
                            dest.and(expr);
                            break;
                        case ',':
                            dest.or(expr);
                            break;
                    }
                }
            }
        }

        private void unget() {
            if (pos <= str.length())
                pos--;
        }

        private boolean hasPrecedence(char lastOp, char currOp) {
            if (lastOp > 0 && currOp > lastOp) {
                unget();
                return true;
            }
            return false;
        }

        private boolean parseAtomExpr() {
            QueryAtom atom = new QueryAtom(mol.getBuilder());
            Expr      expr = new Expr(Expr.Type.NONE);
            atom.setExpression(expr);
            if (!parseExplicitHydrogen(atom, expr) &&
                !parseAtomExpr(atom, expr, '\0')) {
                error = "Invalid atom expression";
                return false;
            }
            append(atom);
            return true;
        }

        boolean parseBondExpr() {
            bond = new QueryBond(mol.getBuilder());
            bond.setExpression(new Expr(Expr.Type.NONE));
            if (!parseBondExpr(bond.getExpression(), bond, '\0')) {
                error = "Invalid bond expression";
                return false;
            }
            return true;
        }

        void newFragment() {
            prev = null;
        }

        boolean begComponentGroup() {
            curComponentId = ++numComponents;
            return true;
        }

        boolean endComponentGroup() {
            // closing an unopen component group
            if (curComponentId == 0) {
                error = "Closing unopened component grouping";
                return false;
            }
            curComponentId = 0;
            return true;
        }

        boolean openBranch() {
            if (prev == null || bond != null) {
                error = "No previous atom to open branch";
                return false;
            }
            stack.push(prev);
            return true;
        }

        boolean closeBranch() {
            if (stack.isEmpty() || bond != null) {
                error = "Closing unopened branch";
                return false;
            }
            prev = stack.pop();
            return true;
        }

        boolean openRing(int rnum) {
            if (prev == null) {
                error = "Cannot open ring, no previous atom";
                return false;
            }
            if (bond == null) {
                bond = new QueryBond(null);
                bond.setExpression(null);
            }
            bond.setAtom(prev, 0);
            rings[rnum] = addBond(prev, bond);
            numRingOpens++;
            bond = null;
            return true;
        }

        boolean closeRing(int rnum) {
            IBond bond = rings[rnum];
            rings[rnum] = null;
            numRingOpens--;
            Expr openExpr = ((QueryBond) BondRef.deref(bond)).getExpression();
            if (this.bond != null) {
                Expr closeExpr = ((QueryBond) BondRef.deref(this.bond)).getExpression();
                if (openExpr == null)
                    ((QueryBond) BondRef.deref(bond)).setExpression(closeExpr);
                else if (!openExpr.equals(closeExpr)) {
                    error = "Open/close expressions are not equivalent";
                    return false;
                }
                this.bond = null;
            } else if (openExpr == null) {
                ((QueryBond) BondRef.deref(bond)).setExpression(new Expr(SINGLE_OR_AROMATIC));
            }
            bond.setAtom(prev, 1);
            addBond(prev, bond);
            return true;
        }

        boolean ringClosure(int rnum) {
            if (rings[rnum] == null)
                return openRing(rnum);
            else
                return closeRing(rnum);
        }

        void swap(Object[] obj, int i, int j) {
            Object tmp = obj[i];
            obj[i] = obj[j];
            obj[j] = tmp;
        }

        boolean hasAliphaticDoubleBond(Expr expr) {
            for (; ; ) {
                switch (expr.type()) {
                    case NOT:
                        expr = expr.left();
                        break;
                    case AND:
                    case OR:
                        if (hasAliphaticDoubleBond(expr.left()))
                            return true;
                        expr = expr.right();
                        break;
                    case ALIPHATIC_ORDER:
                        return expr.value() == 2;
                    default:
                        return false;
                }
            }
        }

        /**
         * Traverse an expression tree and flip all the stereo expressions.
         */
        void flip(Expr expr) {
            for (; ; ) {
                switch (expr.type()) {
                    case STEREOCHEMISTRY:
                        if (expr.value() != 0)
                            expr.setPrimitive(expr.type(),
                                              expr.value() ^ 0x3);
                        return;
                    case AND:
                    case OR:
                        flip(expr.left());
                        expr = expr.right();
                        break;
                    case NOT:
                        expr = expr.left();
                        break;
                }
            }
        }

        /**
         * Determines the bond stereo (cis/trans) of a double bond
         * given the left and right bonds connected to the central bond. For
         * example:
         * <pre>{@code
         *  C/C=C/C    => trans
         *  C/C=C\C    => cis
         *  C/C=C\,/C  => cis or trans
         *  C/C=C/?C   => trans or unspec
         *  C/C=C!/!\C => unspecified
         *  C/C=C!/C   => cis or unspec (not trans)
         *  C/C=C\/C   => cis and trans (always false)
         * }</prev>
         *
         * @param left left directional bond
         * @param right right directional bond
         * @return the bond stereo or null if could not be determined
         */
        Expr determineBondStereo(Expr left, Expr right) {
            switch (left.type()) {
                case AND:
                case OR:
                    Expr sub1 = determineBondStereo(left.left(), right);
                    Expr sub2 = determineBondStereo(left.right(), right);
                    if (sub1 != null && sub2 != null)
                        return new Expr(left.type(), sub1, sub2);
                    else if (sub1 != null)
                        return sub1;
                    else if (sub2 != null)
                        return sub2;
                    else
                        return null;
                case NOT:
                    sub1 = determineBondStereo(left.left(), right);
                    if (sub1 != null)
                        return sub1.negate();
                    break;
                case STEREOCHEMISTRY:
                    switch (right.type()) {
                        case AND:
                        case OR:
                            sub1 = determineBondStereo(left, right.left());
                            sub2 = determineBondStereo(left, right.right());
                            if (sub1 != null && sub2 != null)
                                return new Expr(right.type(), sub1, sub2);
                            else if (sub1 != null)
                                return sub1;
                            else if (sub2 != null)
                                return sub2;
                            else
                                return null;
                        case NOT:
                            sub1 = determineBondStereo(left, right.left());
                            if (sub1 != null)
                                return sub1.negate();
                            return null;
                        case STEREOCHEMISTRY:
                            if (left.value() == BOND_UNSPEC || right.value() == BOND_UNSPEC)
                                return new Expr(STEREOCHEMISTRY, 0);
                            if (left.value() == right.value())
                                return new Expr(STEREOCHEMISTRY, IStereoElement.TOGETHER);
                            else
                                return new Expr(STEREOCHEMISTRY, IStereoElement.OPPOSITE);
                        default:
                            return null;
                    }
                default:
                    return null;
            }
            return null;
        }

        // final check
        boolean finish() {
            // check for unclosed rings, components, and branches
            if (numRingOpens != 0 || curComponentId != 0 ||
                !stack.isEmpty() || bond != null) {
                error = "Unclosed ring, component group, or branch";
                return false;
            }
            if (role != ReactionRole.None) {
                if (role != ReactionRole.Agent) {
                    error = "Missing '>' to complete reaction";
                    return false;
                }
                markReactionRoles();
                for (IAtom atom : mol.atoms()) {
                    ReactionRole role = atom.getProperty(CDKConstants.REACTION_ROLE);
                    ((QueryAtom) AtomRef.deref(atom)).getExpression().and(
                        new Expr(Expr.Type.REACTION_ROLE,
                                 role.ordinal())
                    );
                }
            }
            // setup data structures for stereo chemistry
            for (IAtom atom : astereo) {
                LocalNbrs nbrinfo = local.get(atom);
                if (nbrinfo == null)
                    continue;
                IAtom[] ligands = new IAtom[4];
                int     degree  = 0;
                for (IBond bond : nbrinfo.bonds)
                    ligands[degree++] = bond.getOther(atom);
                // add implicit neighbor, and move to correct position
                if (degree == 3) {
                    ligands[degree++] = atom;
                    if (nbrinfo.isFirst)
                        swap(ligands, 2, 3);
                }
                if (degree == 4) {
                    // Note the left and right is stored in the atom expression, we
                    // only need the IStereoElement for the local ordering of neighbors
                    mol.addStereoElement(new TetrahedralChirality(atom, ligands, 0));
                }
            }
            // convert SMARTS up/down bond stereo to something we use to match
            if (!bstereo.isEmpty()) {
                for (IBond bond : mol.bonds()) {
                    Expr expr = ((QueryBond) BondRef.deref(bond)).getExpression();
                    if (hasAliphaticDoubleBond(expr)) {
                        IBond left = null, right = null;
                        LocalNbrs bBonds = local.get(bond.getBegin());
                        LocalNbrs eBonds = local.get(bond.getEnd());

                        // not part of this parse
                        if (bBonds == null || eBonds == null)
                            continue;

                        for (IBond b : bBonds.bonds)
                            if (bstereo.contains(b))
                                left = b;
                        for (IBond b : eBonds.bonds)
                            if (bstereo.contains(b))
                                right = b;
                        if (left == null || right == null)
                            continue;
                        Expr leftExpr  = ((QueryBond) BondRef.deref(left)).getExpression();
                        Expr rightExpr = ((QueryBond) BondRef.deref(right)).getExpression();
                        Expr bexpr     = determineBondStereo(leftExpr, rightExpr);
                        if (bexpr != null) {
                            expr.and(bexpr);
                            // '/' and '\' are directional, correct for this
                            // relative labelling
                            // C(/C)=C/C and C\C=C/C are both cis
                            if (left.getBegin().equals(bond.getBegin()) !=
                                right.getBegin().equals(bond.getEnd()))
                                flip(bexpr);
                            mol.addStereoElement(new DoubleBondStereochemistry(bond,
                                                                               new IBond[]{
                                                                                   left,
                                                                                   right},
                                                                               0));
                        }
                    }
                }
                // now strip all '/' and '\' from adjacent double bonds
                for (IBond bond : bstereo) {
                    Expr expr = ((QueryBond) BondRef.deref(bond)).getExpression();
                    expr = strip(expr, Expr.Type.STEREOCHEMISTRY);
                    if (expr == null)
                        expr = new Expr(SINGLE_OR_AROMATIC);
                    else
                        expr.and(new Expr(SINGLE_OR_AROMATIC));
                    ((QueryBond) bond).setExpression(expr);
                }
            }
            return true;
        }

        void append(IAtom atom) {
            if (curComponentId != 0)
                atom.setProperty(CDKConstants.REACTION_GROUP, curComponentId);
            mol.addAtom(atom);
            if (prev != null) {
                if (bond == null) {
                    bond = new QueryBond(mol.getBuilder());
                    bond.setExpression(new Expr(SINGLE_OR_AROMATIC));
                }
                bond.setAtom(prev, 0);
                bond.setAtom(atom, 1);
                addBond(prev, bond);
                addBond(atom, bond);
            } else
                local.put(atom, new LocalNbrs(true));
            prev = atom;
            bond = null;
        }

        void append(Expr expr) {
            QueryAtom atom = new QueryAtom(mol.getBuilder());
            atom.setExpression(expr);
            append(atom);
        }

        private char peek() {
            return pos < str.length() ? str.charAt(pos) : '\0';
        }

        private char next() {
            if (pos < str.length())
                return str.charAt(pos++);
            pos++;
            return '\0';
        }

        private static boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }

        public boolean parse() {
            while (pos < str.length()) {
                switch (str.charAt(pos++)) {
                    case '*':
                        append(new Expr(Expr.Type.TRUE));
                        break;
                    case 'A':
                        append(new Expr(Expr.Type.IS_ALIPHATIC));
                        break;
                    case 'B':
                        if (peek() == 'r') {
                            next();
                            append(new Expr(ELEMENT,
                                            Elements.BROMINE.getAtomicNumber()));
                        } else {
                            append(new Expr(ALIPHATIC_ELEMENT,
                                            Elements.BORON.getAtomicNumber()));
                        }
                        break;
                    case 'C':
                        if (peek() == 'l') {
                            next();
                            append(new Expr(ELEMENT,
                                            Elements.CHLORINE.getAtomicNumber()));
                        } else {
                            append(new Expr(ALIPHATIC_ELEMENT,
                                            Elements.CARBON.getAtomicNumber()));
                        }
                        break;
                    case 'N':
                        append(new Expr(ALIPHATIC_ELEMENT,
                                        Elements.NITROGEN.getAtomicNumber()));
                        break;
                    case 'O':
                        append(new Expr(ALIPHATIC_ELEMENT,
                                        Elements.OXYGEN.getAtomicNumber()));
                        break;
                    case 'P':
                        append(new Expr(ALIPHATIC_ELEMENT,
                                        Elements.PHOSPHORUS.getAtomicNumber()));
                        break;
                    case 'S':
                        append(new Expr(ALIPHATIC_ELEMENT,
                                        Elements.SULFUR.getAtomicNumber()));
                        break;
                    case 'F':
                        append(new Expr(ELEMENT,
                                        Elements.FLUORINE.getAtomicNumber()));
                        break;
                    case 'I':
                        append(new Expr(ELEMENT,
                                        Elements.IODINE.getAtomicNumber()));
                        break;

                    case 'a':
                        append(new Expr(Expr.Type.IS_AROMATIC));
                        break;
                    case 'b':
                        append(new Expr(AROMATIC_ELEMENT,
                                        Elements.BORON.getAtomicNumber()));
                        break;
                    case 'c':
                        append(new Expr(AROMATIC_ELEMENT,
                                        Elements.CARBON.getAtomicNumber()));
                        break;
                    case 'n':
                        append(new Expr(AROMATIC_ELEMENT,
                                        Elements.NITROGEN.getAtomicNumber()));
                        break;
                    case 'o':
                        append(new Expr(AROMATIC_ELEMENT,
                                        Elements.OXYGEN.getAtomicNumber()));
                        break;
                    case 'p':
                        append(new Expr(AROMATIC_ELEMENT,
                                        Elements.PHOSPHORUS.getAtomicNumber()));
                        break;
                    case 's':
                        append(new Expr(AROMATIC_ELEMENT,
                                        Elements.SULFUR.getAtomicNumber()));
                        break;
                    case '[':
                        if (!parseAtomExpr())
                            return false;
                        break;

                    case '.':
                        newFragment();
                        break;
                    case '-':
                    case '=':
                    case '#':
                    case '$':
                    case ':':
                    case '@':
                    case '~':
                    case '!':
                    case '/':
                    case '\\':
                        if (prev == null)
                            return false;
                        unget();
                        if (!parseBondExpr())
                            return false;
                        break;

                    case '(':
                        if (prev == null) {
                            if (!begComponentGroup())
                                return false;
                        } else {
                            if (!openBranch())
                                return false;
                        }
                        break;
                    case ')':
                        if (stack.isEmpty()) {
                            if (!endComponentGroup())
                                return false;
                        } else {
                            if (!closeBranch())
                                return false;
                        }
                        break;

                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        if (!ringClosure(str.charAt(pos - 1) - '0'))
                            return false;
                        break;
                    case '%':
                        if (isDigit(peek())) {
                            int rnum = str.charAt(pos++) - '0';
                            if (isDigit(peek()))
                                ringClosure(10 * rnum + (str.charAt(pos++) - '0'));
                            else
                                return false;
                        } else {
                            return false;
                        }
                        break;

                    case '>':
                        if (!stack.isEmpty())
                            return false;
                        if (!markReactionRoles())
                            return false;
                        prev = null;
                        break;

                    case ' ':
                    case '\t':
                        while (true) {
                            if (isTerminalChar(next()))
                                break;
                        }
                        mol.setTitle(str.substring(pos - 1));
                        break;
                    case '\r':
                    case '\n':
                    case '\0':
                        return finish();

                    default:
                        error = "Unexpected character";
                        return false;
                }
            }
            return finish();
        }

        private boolean markReactionRoles() {
            if (role == ReactionRole.None)
                role = ReactionRole.Reactant;
            else if (role == ReactionRole.Reactant)
                role = ReactionRole.Agent;
            else if (role == ReactionRole.Agent)
                role = ReactionRole.Product;
            else
                error = "To many '>' in reaction";
            int idx = mol.getAtomCount() - 1;
            while (idx >= 0) {
                IAtom atom = mol.getAtom(idx--);
                if (atom.getProperty(CDKConstants.REACTION_ROLE) != null)
                    break;
                atom.setProperty(CDKConstants.REACTION_ROLE, role);
            }
            return true;
        }

        private boolean isTerminalChar(char c) {
            switch (c) {
                case '\0':
                case '\n':
                case '\r':
                    return true;
                default:
                    return false;
            }
        }
    }

    private static boolean hasOr(Expr expr) {
        for (; ; ) {
            switch (expr.type()) {
                case AND:
                    if (hasOr(expr.left()))
                        return true;
                    expr = expr.right();
                    break;
                case OR:
                    return expr.left().type() != STEREOCHEMISTRY ||
                           expr.right().type() != STEREOCHEMISTRY ||
                           expr.right().value() != 0;
                case SINGLE_OR_AROMATIC:
                case SINGLE_OR_DOUBLE:
                case DOUBLE_OR_AROMATIC:
                    return true;
                default:
                    return false;
            }
        }
    }

    private static boolean isUpper(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private static boolean isLower(char c) {
        return c >= 'a' && c <= 'z';
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean generateBond(StringBuilder sb, Expr expr) {
        switch (expr.type()) {
            case TRUE:
                sb.append('~');
                break;
            case FALSE:
                sb.append("!~");
                break;
            case IS_AROMATIC:
                sb.append(":");
                break;
            case IS_ALIPHATIC:
                sb.append("!:");
                break;
            case IS_IN_RING:
                sb.append("@");
                break;
            case IS_IN_CHAIN:
                sb.append("!@");
                break;
            case SINGLE_OR_AROMATIC:
                sb.append("-,:");
                break;
            case DOUBLE_OR_AROMATIC:
                sb.append("=,:");
                break;
            case SINGLE_OR_DOUBLE:
                sb.append("-,=");
                break;
            case ORDER:
                LoggingToolFactory.createLoggingTool(Smarts.class)
                                  .warn("Expr.Type.ORDER cannot be round-tripped via SMARTS!");
            case ALIPHATIC_ORDER:
                switch (expr.value()) {
                    case 1:
                        sb.append('-');
                        break;
                    case 2:
                        sb.append('=');
                        break;
                    case 3:
                        sb.append('#');
                        break;
                    case 4:
                        sb.append('$');
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                break;
            case NOT:
                sb.append('!');
                if (!generateBond(sb, expr.left())) {
                    sb.setLength(sb.length() - 1);
                    return false;
                }
                break;
            case OR:
                if (generateBond(sb, expr.left())) {
                    sb.append(',');
                    if (!generateBond(sb, expr.right()))
                        sb.setLength(sb.length() - 1);
                    return true;
                } else if (generateBond(sb, expr.right()))
                    return true;
                else
                    return false;
            case AND:
                boolean lowPrec = hasOr(expr.left()) || hasOr(expr.right());
                if (generateBond(sb, expr.left())) {
                    if (lowPrec)
                        sb.append(';');
                    if (!generateBond(sb, expr.right()) && lowPrec)
                        sb.setLength(sb.length() - 1);
                    return true;
                } else if (generateBond(sb, expr.right()))
                    return true;
                else
                    return false;
            case STEREOCHEMISTRY:
                // bond stereo is encoded with directional / \ bonds we determine
                // what these are separately and store them in 'bdirs'
                return false;
            default:
                throw new IllegalArgumentException("Can not generate SMARTS for bond "
                                                   + "expression: " + expr.type());
        }
        return true;
    }

    /**
     * Parse the provided SMARTS string appending query atom/bonds to the
     * provided molecule. This method allows the flavor of SMARTS to specified
     * that changes the meaning of queries.
     *
     * @param mol the molecule to store the query in
     * @param smarts the SMARTS string
     * @param flavor the SMARTS flavor (e.g. {@link Smarts#FLAVOR_LOOSE}.
     * @see Expr
     * @see org.openscience.cdk.isomorphism.matchers.IQueryAtom
     * @see org.openscience.cdk.isomorphism.matchers.IQueryBond
     * @return whether the SMARTS was valid
     */
    public static boolean parse(IAtomContainer mol, String smarts, int flavor) {
        Parser state = new Parser(mol, smarts, flavor);
        if (!state.parse()) {
            setErrorMesg(smarts, state.pos, state.error);
            return false;
        }
        return true;
    }

    /**
     * Parse the provided SMARTS string appending query atom/bonds to the
     * provided molecule. This method uses {@link Smarts#FLAVOR_LOOSE}.
     *
     * @param mol the molecule to store the query in
     * @param smarts the SMARTS string
     * @see Expr
     * @see org.openscience.cdk.isomorphism.matchers.IQueryAtom
     * @see org.openscience.cdk.isomorphism.matchers.IQueryBond
     * @return whether the SMARTS was valid
     */
    public static boolean parse(IAtomContainer mol, String smarts) {
        return parse(mol, smarts, FLAVOR_LOOSE);
    }

    /**
     * Utility to generate an atom expression.
     * <pre>{@code
     * Expr   expr  = new Expr(Expr.Type.DEGREE, 4).and(
     *                  new Expr(Expr.Type.IS_AROMATIC));
     * String aExpr = Smarts.generateAtom(expr);
     * // aExpr='[D4a]'
     * }</pre>
     * @see Expr
     * @param expr the expression
     * @return the SMARTS atom expression
     */
    public static String generateAtom(Expr expr) {
        return new Generator(null).generateAtom(null, expr);
    }

    /**
     * Utility to generate a bond expression.
     * <pre>{@code
     * Expr   expr  = new Expr(Expr.Type.TRUE);
     * String bExpr = Smarts.generateBond(expr);
     * // bExpr='~'
     * }</pre>
     * @see Expr
     * @param expr the expression
     * @return the SMARTS atom expression
     */
    public static String generateBond(Expr expr) {
        // default bond type
        if (expr.type() == SINGLE_OR_AROMATIC)
            return "";
        StringBuilder sb = new StringBuilder();
        generateBond(sb, expr);
        return sb.toString();
    }

    /**
     * Generate a SMARTS string from the provided molecule. The generator uses
     * {@link Expr}s stored on the {@link QueryAtom} and {@link QueryBond}
     * instances.
     * <pre>
     * {@code
     * IAtomContainer mol = ...;
     * QueryAtom qatom1 = new QueryAtom(mol.getBuilder());
     * QueryAtom qatom2 = new QueryAtom(mol.getBuilder());
     * QueryBond qbond  = new QueryBond(mol.getBuilder());
     * qatom1.setExpression(new Expr(Expr.Type.IS_AROMATIC));
     * qatom2.setExpression(new Expr(Expr.Type.IS_AROMATIC));
     * qbond.setExpression(new Expr(Expr.Type.IS_ALIPHATIC));
     * qbond.setAtoms(new IAtom[]{qatom1, qatom2});
     * mol.addAtom(qatom1);
     * mol.addAtom(qatom2);
     * mol.addBond(qbond);
     * String smartsStr = Smarts.generate(mol);
     * // smartsStr = 'a!:a'
     * }
     * </pre>
     * @param mol the query molecule
     * @return the SMARTS
     * @see Expr
     * @see org.openscience.cdk.isomorphism.matchers.IQueryAtom
     * @see org.openscience.cdk.isomorphism.matchers.IQueryBond
     */
    public static String generate(IAtomContainer mol) {
        return new Generator(mol).generate();
    }

    private static final class Generator {

        private final IAtomContainer          mol;
        private final Map<IAtom, List<IBond>> nbrs;
        // visit array
        private final Set<IAtom>          avisit = new HashSet<>();
        // ring bonds
        private final Set<IBond>          rbonds = new HashSet<>();
        // used ring numbers
        private final boolean[]           rvisit = new boolean[100];
        // open ring bonds and their number
        private final Map<IBond, Integer> rnums  = new HashMap<>();
        // bond direction (up/down) for stereo
        private final Map<IBond, String>  bdirs  = new HashMap<>();
        // logger
        private final ILoggingTool logger = LoggingToolFactory.createLoggingTool(Generator.class);

        /* fields only used for assign double bond stereo */
        // stereo element cache
        private Map<IChemObject,IStereoElement> ses;
        // marks atoms that are in double bonds with stereo
        private Set<IAtom>                      adjToDb;
        // marks stereo bonds we've visited
        private Set<IBond>                      bvisit;

        public Generator(IAtomContainer mol) {
            this.mol = mol;
            this.nbrs = new HashMap<>();
        }

        private int nextRingNum() {
            int rnum = 1;
            while (rnum < rvisit.length && rvisit[rnum]) {
                rnum++;
            }
            if (rnum < rvisit.length) {
                rvisit[rnum] = true;
                return rnum;
            }
            throw new IllegalStateException("Not enough ring numbers!");
        }

        private void markRings(IAtom atom, IBond prev) {
            avisit.add(atom);
            List<IBond> bonds = mol.getConnectedBondsList(atom);
            nbrs.put(atom, bonds);
            for (IBond bond : bonds) {
                if (bond == prev) continue;
                IAtom other = bond.getOther(atom);
                if (avisit.contains(other))
                    rbonds.add(bond);
                else
                    markRings(other, bond);
            }
        }

        // select a bond we will put the directional '/' '\' labels  on
        private IBond chooseBondToDir(IAtom atom, IBond db, Set<IAtom> adjToDb) {
            IBond choice = null;
            for (IBond bond : nbrs.get(atom)) {
                if (bond == db)
                    continue;
                if (adjToDb.contains(bond.getOther(atom)))
                    return bond;
                else
                    choice = bond;
            }
            return choice;
        }

        private void setBondDir(IAtom beg, IBond bond, String dir) {
            if (bond.getEnd().equals(beg)) {
                bdirs.put(bond, dir);
            } else if (bond.getBegin().equals(beg)) {
                if (dir.equals(BSTEREO_UP))
                    dir = BSTEREO_DN;
                else if (dir.equals(BSTEREO_DN))
                    dir = BSTEREO_UP;
                else if (dir.equals(BSTEREO_UPU))
                    dir = BSTEREO_DNU;
                else if (dir.equals(BSTEREO_DNU))
                    dir = BSTEREO_UPU;
                bdirs.put(bond, dir);
            } else
                throw new IllegalArgumentException();
        }

        private void setBondDirs(IAtomContainer mol) {

            adjToDb = new HashSet<>();
            ses = new HashMap<>();
            bvisit = new HashSet<>();

            for (IStereoElement se : mol.stereoElements()) {
                if (se.getConfigClass() == IStereoElement.CisTrans) {
                    ses.put(se.getFocus(), se);
                }
            }

            for (IBond bond : mol.bonds()) {
                Expr expr = ((QueryBond) BondRef.deref(bond)).getExpression();
                int  flags = getBondStereoFlag(expr);
                if (flags != BSTEREO_ANY) {
                    adjToDb.add(bond.getBegin());
                    adjToDb.add(bond.getEnd());
                }
            }

            // first we set and propagate
            for (IBond bond : mol.bonds()) {
                if (!bvisit.contains(bond))
                    propagateBondStereo(bond, false);
            }
            // now set the complex ones
            for (IBond bond : mol.bonds()) {
                if (!bvisit.contains(bond))
                    propagateBondStereo(bond, true);
            }
        }

        private void propagateBondStereo(IBond bond, boolean all) {
            Expr expr  = ((QueryBond) BondRef.deref(bond)).getExpression();
            int  flags = getBondStereoFlag(expr);
            if (flags != BSTEREO_ANY) {

                // first pass only handle CIS and TRANS bond stereo, ignoring
                // cis/upspec, trans/unspec, cis/trans, etc.
                if (!all && flags != BSTEREO_CIS && flags != BSTEREO_TRANS)
                    return;

                bvisit.add(bond);
                final IAtom beg = bond.getBegin();
                final IAtom end = bond.getEnd();

                final IBond bBond = chooseBondToDir(beg, bond, adjToDb);
                final IBond eBond = chooseBondToDir(end, bond, adjToDb);

                if (bBond == null || eBond == null) {
                    logger.warn("Too few bonds to encode bond stereochemistry in SMARTS");
                    return;
                }

                // if a stereo element is specified it may have extra
                // information about which bonds are the 'reference', only
                // matters if either neighbor is deg > 2
                IStereoElement se = ses.get(bond);
                if (se != null) {
                    if (se.getCarriers().contains(bBond) != se.getCarriers().contains(eBond)) {
                        switch (flags) {
                            case BSTEREO_CIS:
                                flags = BSTEREO_TRANS;
                                break;
                            case BSTEREO_TRANS:
                                flags = BSTEREO_CIS;
                                break;
                            case BSTEREO_CIS_OR_UNSPEC:
                                flags = BSTEREO_TRANS_OR_UNSPEC;
                                break;
                            case BSTEREO_TRANS_OR_UNSPEC:
                                flags = BSTEREO_CIS_OR_UNSPEC;
                                break;
                        }
                    }
                }

                // current begin and end directions
                String bDir = bdirs.get(bBond);
                String eDir = bdirs.get(eBond);

                // trivial case no conflict possible
                if (bDir == null && eDir == null) {
                    switch (flags) {
                        case BSTEREO_CIS:
                            setBondDir(beg, bBond, BSTEREO_UP);
                            setBondDir(end, eBond, BSTEREO_UP);
                            break;
                        case BSTEREO_TRANS:
                            setBondDir(beg, bBond, BSTEREO_UP);
                            setBondDir(end, eBond, BSTEREO_DN);
                            break;
                        case BSTEREO_CIS_OR_TRANS:
                            setBondDir(beg, bBond, BSTEREO_UP);
                            setBondDir(end, eBond, BSTEREO_EITHER);
                            break;
                        case BSTEREO_CIS_OR_UNSPEC:
                            setBondDir(beg, bBond, BSTEREO_UP);
                            setBondDir(end, eBond, BSTEREO_UPU);
                            break;
                        case BSTEREO_TRANS_OR_UNSPEC:
                            setBondDir(beg, bBond, BSTEREO_UP);
                            setBondDir(end, eBond, BSTEREO_DNU);
                            break;
                        case BSTEREO_UNSPEC:
                            setBondDir(beg, bBond, BSTEREO_UP);
                            setBondDir(end, eBond, BSTEREO_NEITHER);
                            break;
                    }
                }
                // set relative to the beg direction
                else if (eDir == null) {
                    switch (flags) {
                        case BSTEREO_CIS:
                            if (bDir.equals(BSTEREO_UP))
                                setBondDir(end, eBond, BSTEREO_UP);
                            else if (bDir.equals(BSTEREO_DN))
                                setBondDir(end, eBond, BSTEREO_DN);
                            else
                                logger.warn("Could not encode bond stereochemistry");
                            break;
                        case BSTEREO_TRANS:
                            if (bDir.equals(BSTEREO_UP))
                                setBondDir(end, eBond, BSTEREO_DN);
                            else if (bDir.equals(BSTEREO_DN))
                                setBondDir(end, eBond, BSTEREO_UP);
                            else
                                logger.warn("Could not encode bond stereochemistry");
                            break;
                        case BSTEREO_CIS_OR_TRANS:
                            if (bDir.equals(BSTEREO_UP) || bDir.equals(BSTEREO_DN))
                                setBondDir(end, eBond, BSTEREO_EITHER);
                            else if (!bDir.equals(BSTEREO_NEITHER))
                                setBondDir(end, bBond, BSTEREO_UP);
                            else
                                logger.warn("Could not encode bond stereochemistry");
                            break;
                        case BSTEREO_CIS_OR_UNSPEC:
                            if (bDir.equals(BSTEREO_UP))
                                setBondDir(end, eBond, BSTEREO_UPU);
                            else if (bDir.equals(BSTEREO_DN))
                                setBondDir(end, eBond, BSTEREO_DNU);
                            else
                                logger.warn("Could not encode bond stereochemistry");
                            break;
                        case BSTEREO_TRANS_OR_UNSPEC:
                            if (bDir.equals(BSTEREO_UP))
                                setBondDir(end, eBond, BSTEREO_DNU);
                            else if (bDir.equals(BSTEREO_DN))
                                setBondDir(end, eBond, BSTEREO_UPU);
                            else
                                logger.warn("Could not encode bond stereochemistry");
                            break;
                        case BSTEREO_UNSPEC:
                            if (bDir.equals(BSTEREO_NEITHER))
                                setBondDir(end, eBond, BSTEREO_UP);
                            else
                                setBondDir(end, eBond, BSTEREO_NEITHER);
                            break;
                    }
                }
                // set relative to the end direction
                else if (bDir == null) {
                    switch (flags) {
                        case BSTEREO_CIS:
                            if (eDir.equals(BSTEREO_UP))
                                setBondDir(beg, bBond, BSTEREO_UP);
                            else if (eDir.equals(BSTEREO_DN))
                                setBondDir(beg, bBond, BSTEREO_DN);
                            else
                                logger.warn("Could not encode bond stereochemistry");
                            break;
                        case BSTEREO_TRANS:
                            if (eDir.equals(BSTEREO_UP))
                                setBondDir(beg, bBond, BSTEREO_DN);
                            else if (eDir.equals(BSTEREO_DN))
                                setBondDir(beg, bBond, BSTEREO_UP);
                            else
                                logger.warn("Could not encode bond stereochemistry");
                            break;
                        case BSTEREO_CIS_OR_TRANS:
                            if (eDir.equals(BSTEREO_UP) || eDir.equals(BSTEREO_DN))
                                setBondDir(beg, bBond, BSTEREO_EITHER);
                            else if (!eDir.equals(BSTEREO_NEITHER))
                                setBondDir(beg, bBond, BSTEREO_UP);
                            else
                                logger.warn("Could not encode bond stereochemistry");
                            break;
                        case BSTEREO_CIS_OR_UNSPEC:
                            if (eDir.equals(BSTEREO_UP))
                                setBondDir(beg, bBond, BSTEREO_UPU);
                            else if (eDir.equals(BSTEREO_DN))
                                setBondDir(beg, bBond, BSTEREO_DNU);
                            else
                                logger.warn("Could not encode bond stereochemistry");
                            break;
                        case BSTEREO_TRANS_OR_UNSPEC:
                            if (eDir.equals(BSTEREO_UP))
                                setBondDir(beg, bBond, BSTEREO_DNU);
                            else if (eDir.equals(BSTEREO_DN))
                                setBondDir(beg, bBond, BSTEREO_UPU);
                            else
                                logger.warn("Could not encode bond stereochemistry");
                            break;
                        case BSTEREO_UNSPEC:
                            if (eDir.equals(BSTEREO_NEITHER))
                                setBondDir(beg, bBond, BSTEREO_UP);
                            else
                                setBondDir(beg, bBond, BSTEREO_NEITHER);
                            break;
                    }
                } else {
                    logger.warn("Bond stereochemistry may be incorrect");
                }

                // propagate bond decision
                for (IBond bOther : nbrs.get(bBond.getOther(beg)))
                    if (!bvisit.contains(bOther))
                        propagateBondStereo(bOther, all);
                for (IBond bOther : nbrs.get(eBond.getOther(end)))
                    if (!bvisit.contains(bOther))
                        propagateBondStereo(bOther, all);
            }
        }

        private boolean isRingOpen(IBond bond) {
            return rbonds.contains(bond);
        }

        private boolean isRingClose(IBond bond) {
            return rnums.containsKey(bond);
        }

        private void sort(List<IBond> bonds, final IBond prev) {
            Collections.sort(bonds,
                             new Comparator<IBond>() {
                                 @Override
                                 public int compare(IBond a, IBond b) {
                                     if (a == prev)
                                         return -1;
                                     if (b == prev)
                                         return +1;
                                     if (isRingClose(a) && !isRingClose(b))
                                         return -1;
                                     if (!isRingClose(a) && isRingClose(b))
                                         return +1;
                                     if (isRingOpen(a) && !isRingOpen(b))
                                         return -1;
                                     if (!isRingOpen(a) && isRingOpen(b))
                                         return +1;
                                     return 0;
                                 }
                             });
        }

        private void generateRecurAtom(StringBuilder sb,
                                       IAtom atom,
                                       Expr expr) {
            sb.append("$([");
            generateAtom(sb, atom, expr, false);
            sb.append("])");
        }

        private void generateAtom(StringBuilder sb,
                                  IAtom atom,
                                  Expr expr,
                                  boolean withDisjunction) {
            switch (expr.type()) {
                case TRUE:
                    sb.append('*');
                    break;
                case FALSE:
                    sb.append("!*");
                    break;
                case IS_AROMATIC:
                    sb.append('a');
                    break;
                case IS_ALIPHATIC:
                    sb.append('A');
                    break;
                case IS_IN_RING:
                    sb.append('R');
                    break;
                case IS_IN_CHAIN:
                    sb.append("!R");
                    break;
                case DEGREE:
                    sb.append('D');
                    if (expr.value() != 1)
                        sb.append(expr.value());
                    break;
                case TOTAL_H_COUNT:
                    sb.append('H');
                    sb.append(expr.value());
                    break;
                case HAS_IMPLICIT_HYDROGEN:
                    sb.append('h');
                    break;
                case IMPL_H_COUNT:
                    sb.append('h').append(expr.value());
                    break;
                case VALENCE:
                    sb.append('v');
                    if (expr.value() != 1)
                        sb.append(expr.value());
                    break;
                case TOTAL_DEGREE:
                    sb.append('X');
                    if (expr.value() != 1)
                        sb.append(expr.value());
                    break;
                case FORMAL_CHARGE:
                    if (expr.value() == -1)
                        sb.append('-');
                    else if (expr.value() == +1)
                        sb.append('+');
                    else if (expr.value() == 0)
                        sb.append('+').append('0');
                    else if (expr.value() < 0)
                        sb.append(expr.value());
                    else
                        sb.append('+').append(expr.value());
                    break;
                case RING_BOND_COUNT:
                    sb.append('x').append(expr.value());
                    break;
                case RING_COUNT:
                    sb.append('R').append(expr.value());
                    break;
                case RING_SMALLEST:
                    sb.append('r').append(expr.value());
                    break;
                case HAS_ISOTOPE:
                    sb.append("!0");
                    break;
                case HAS_UNSPEC_ISOTOPE:
                    sb.append("0");
                    break;
                case ISOTOPE:
                    sb.append(expr.value());
                    break;
                case ELEMENT:
                    switch (expr.value()) {
                        case 0:
                            sb.append("#0");
                            break;
                        case 1:
                            sb.append("#1");
                            break;
                        // may be aromatic? write as '#<num>'
                        case 5:  // B
                        case 6:  // C
                        case 7:  // N
                        case 8:  // O
                        case 13: // Al
                        case 14: // Si
                        case 15: // P
                        case 16: // S
                        case 33: // As
                        case 34: // Se
                        case 51: // Sb
                        case 52: // Te
                            sb.append('#').append(expr.value());
                            break;
                        default:
                            // can't be aromatic, just emit the upper case symbol
                            Elements elem = Elements.ofNumber(expr.value());
                            if (elem == Elements.Unknown)
                                throw new IllegalArgumentException("No element with atomic number: " + expr.value());
                            // portability for older matchers, write very high atomic
                            // num elements as #<num>
                            if (expr.value() > Elements.RADON.getAtomicNumber())
                                sb.append('#').append(expr.value());
                            else
                                sb.append(elem.symbol());
                            break;
                    }
                    break;
                case ALIPHATIC_ELEMENT:
                    switch (expr.value()) {
                        case 0:
                            sb.append("#0");
                            break;
                        case 1:
                            sb.append("#1");
                            break;
                        default:
                            // can't be aromatic, just emit the symbol
                            Elements elem = Elements.ofNumber(expr.value());
                            if (elem == Elements.Unknown)
                                throw new IllegalArgumentException("No element with atomic number: " + expr.value());
                            // portability for older matchers, write very high atomic
                            // num elements as #<num>
                            if (expr.value() > Elements.RADON.getAtomicNumber())
                                sb.append('#').append(expr.value());
                            else
                                sb.append(elem.symbol());
                            break;
                    }
                    break;
                case AROMATIC_ELEMENT:
                    // could restrict
                    switch (expr.value()) {
                        case 0:
                            sb.append("#0");
                            break;
                        case 1:
                            sb.append("#1");
                            break;
                        case 5:  // B
                        case 6:  // C
                        case 7:  // N
                        case 8:  // O
                        case 13: // Al
                        case 14: // Si
                        case 15: // P
                        case 16: // S
                        case 33: // As
                        case 34: // Se
                        case 51: // Sb
                        case 52: // Te
                            Elements elem = Elements.ofNumber(expr.value());
                            if (elem == Elements.Unknown)
                                throw new IllegalArgumentException("No element with atomic number: " + expr.value());
                            sb.append(elem.symbol().toLowerCase());
                            break;
                        default:
                            elem = Elements.ofNumber(expr.value());
                            if (elem == Elements.Unknown)
                                throw new IllegalArgumentException("No element with atomic number: " + expr.value());
                            // portability for older matchers, write very high atomic
                            // num elements as #<num>
                            if (expr.value() > Elements.RADON.getAtomicNumber())
                                sb.append('#').append(expr.value());
                            else
                                sb.append(elem.symbol()); // Must be aliphatic
                            break;
                    }
                    break;
                case AND:

                    if (expr.left().type() == REACTION_ROLE) {
                        generateAtom(sb, atom, expr.right(), withDisjunction);
                        return;
                    } else if (expr.right().type() == REACTION_ROLE) {
                        generateAtom(sb, atom, expr.left(), withDisjunction);
                        return;
                    }

                    boolean disjuncBelow = hasOr(expr.left()) || hasOr(expr.right());
                    if (disjuncBelow) {
                        // if we're below and above a disjunction we must use
                        // recursive SMARTS to group the terms correctly
                        if (withDisjunction) {
                            if (hasOr(expr.left()))
                                generateRecurAtom(sb, atom, expr.left());
                            else
                                generateAtom(sb, atom, expr.left(), true);
                            int mark = sb.length();
                            if (hasOr(expr.right()))
                                generateRecurAtom(sb, atom, expr.right());
                            else
                                generateAtom(sb, atom, expr.right(), true);
                            maybeExplAnd(sb, mark);
                        } else {
                            generateAtom(sb, atom, expr.left(), false);
                            sb.append(';');
                            generateAtom(sb, atom, expr.right(), false);
                        }
                    } else {
                        generateAtom(sb, atom, expr.left(), withDisjunction);
                        int mark = sb.length();
                        generateAtom(sb, atom, expr.right(), withDisjunction);
                        maybeExplAnd(sb, mark);
                    }
                    break;
                case OR:
                    if (expr.left().type() == STEREOCHEMISTRY &&
                        expr.right().type() == STEREOCHEMISTRY &&
                        expr.right().value() == 0) {
                        generateAtom(sb, atom, expr.left(), true);
                        sb.append('?');
                    } else {
                        generateAtom(sb, atom, expr.left(), true);
                        sb.append(',');
                        generateAtom(sb, atom, expr.right(), true);
                    }
                    break;
                case NOT:
                    sb.append('!');
                    switch (expr.left().type()) {
                        case AND:
                        case OR:
                            generateRecurAtom(sb, atom, expr.left());
                            break;
                        default:
                            generateAtom(sb, atom, expr.left(), withDisjunction);
                            break;
                    }
                    break;
                case RECURSIVE:
                    sb.append("$(").append(Smarts.generate(expr.subquery())).append(")");
                    break;
                case STEREOCHEMISTRY:
                    int order = expr.value();
                    // stereo depends on output order, if within writePart
                    // we have this stored in 'nbrs'
                    if (atom != null && flipStereo(atom))
                        order ^= (IStereoElement.LEFT | IStereoElement.RIGHT);
                    if (order == IStereoElement.LEFT)
                        sb.append('@');
                    else if (order == IStereoElement.RIGHT)
                        sb.append("@@");
                    else
                        throw new IllegalArgumentException();
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        static int parity4(IAtom[] b1, IAtom[] b2) {
            // auto generated
            if (b1[0] == b2[0]) {
                if (b1[1] == b2[1]) {
                    // a,b,c,d -> a,b,c,d
                    if (b1[2] == b2[2] && b1[3] == b2[3]) return 2;
                    // a,b,c,d -> a,b,d,c
                    if (b1[2] == b2[3] && b1[3] == b2[2]) return 1;
                } else if (b1[1] == b2[2]) {
                    // a,b,c,d -> a,c,b,d
                    if (b1[2] == b2[1] && b1[3] == b2[3]) return 1;
                    // a,b,c,d -> a,c,d,b
                    if (b1[2] == b2[3] && b1[3] == b2[1]) return 2;
                } else if (b1[1] == b2[3]) {
                    // a,b,c,d -> a,d,c,b
                    if (b1[2] == b2[2] && b1[3] == b2[1]) return 1;
                    // a,b,c,d -> a,d,b,c
                    if (b1[2] == b2[1] && b1[3] == b2[2]) return 2;
                }
            } else if (b1[0] == b2[1]) {
                if (b1[1] == b2[0]) {
                    // a,b,c,d -> b,a,c,d
                    if (b1[2] == b2[2] && b1[3] == b2[3]) return 1;
                    // a,b,c,d -> b,a,d,c
                    if (b1[2] == b2[3] && b1[3] == b2[2]) return 2;
                } else if (b1[1] == b2[2]) {
                    // a,b,c,d -> b,c,a,d
                    if (b1[2] == b2[0] && b1[3] == b2[3]) return 2;
                    // a,b,c,d -> b,c,d,a
                    if (b1[2] == b2[3] && b1[3] == b2[0]) return 1;
                } else if (b1[1] == b2[3]) {
                    // a,b,c,d -> b,d,c,a
                    if (b1[2] == b2[2] && b1[3] == b2[0]) return 2;
                    // a,b,c,d -> b,d,a,c
                    if (b1[2] == b2[0] && b1[3] == b2[2]) return 1;
                }
            } else if (b1[0] == b2[2]) {
                if (b1[1] == b2[1]) {
                    // a,b,c,d -> c,b,a,d
                    if (b1[2] == b2[0] && b1[3] == b2[3]) return 1;
                    // a,b,c,d -> c,b,d,a
                    if (b1[2] == b2[3] && b1[3] == b2[0]) return 2;
                } else if (b1[1] == b2[0]) {
                    // a,b,c,d -> c,a,b,d
                    if (b1[2] == b2[1] && b1[3] == b2[3]) return 2;
                    // a,b,c,d -> c,a,d,b
                    if (b1[2] == b2[3] && b1[3] == b2[1]) return 1;
                } else if (b1[1] == b2[3]) {
                    // a,b,c,d -> c,d,a,b
                    if (b1[2] == b2[0] && b1[3] == b2[1]) return 2;
                    // a,b,c,d -> c,d,b,a
                    if (b1[2] == b2[1] && b1[3] == b2[0]) return 1;
                }
            } else if (b1[0] == b2[3]) {
                if (b1[1] == b2[1]) {
                    // a,b,c,d -> d,b,c,a
                    if (b1[2] == b2[2] && b1[3] == b2[0]) return 1;
                    // a,b,c,d -> d,b,a,c
                    if (b1[2] == b2[0] && b1[3] == b2[2]) return 2;
                } else if (b1[1] == b2[2]) {
                    // a,b,c,d -> d,c,b,a
                    if (b1[2] == b2[1] && b1[3] == b2[0]) return 2;
                    // a,b,c,d -> d,c,a,b
                    if (b1[2] == b2[0] && b1[3] == b2[1]) return 1;
                } else if (b1[1] == b2[0]) {
                    // a,b,c,d -> d,a,c,b
                    if (b1[2] == b2[2] && b1[3] == b2[1]) return 2;
                    // a,b,c,d -> d,a,b,c
                    if (b1[2] == b2[1] && b1[3] == b2[2]) return 1;
                }
            }
            return 0;
        }

        public String generate(IAtom end, QueryBond bond) {
            String bexpr = generateBond(bond.getExpression());
            if (bdirs.containsKey(bond)) {
                String bdir = bdirs.get(bond);
                if (bond.getBegin().equals(end)) {
                    switch (bdir) {
                        case BSTEREO_DN:  bdir = BSTEREO_UP; break;
                        case BSTEREO_UP:  bdir = BSTEREO_DN; break;
                        case BSTEREO_DNU: bdir = BSTEREO_UPU; break;
                        case BSTEREO_UPU: bdir = BSTEREO_DNU; break;
                    }
                }
                if (bexpr.isEmpty())
                    bexpr = bdir;
                else
                    bexpr += ';' + bdir;
            }
            return bexpr;
        }

        private boolean flipStereo(IAtom atom) {
            List<IBond> bonds = nbrs.get(atom);
            for (IStereoElement se : mol.stereoElements()) {
                if (se.getConfigClass() == IStereoElement.TH &&
                    se.getFocus().equals(atom)) {
                    @SuppressWarnings("unchecked")
                    List<IAtom> src = (List<IAtom>) se.getCarriers();
                    List<IAtom> dst = new ArrayList<>();
                    for (IBond bond : bonds)
                        dst.add(bond.getOther(atom));
                    if (dst.size() == 3) {
                        if (avisit.contains(dst.get(0)))
                            dst.add(1, atom);
                        else
                            dst.add(0, atom);
                    }
                    return parity4(src.toArray(new IAtom[4]),
                                   dst.toArray(new IAtom[4])) == 1;
                }
            }
            // no enough info
            return false;
        }

        private static void maybeExplAnd(StringBuilder sb, int mark) {
            if (isDigit(sb.charAt(mark)) ||
                isUpper(sb.charAt(mark - 1)) && isLower(sb.charAt(mark)))
                sb.insert(mark, '&');
        }

        String generateAtom(IAtom atom, Expr expr) {

            if (expr.type() == AND) {
                if (expr.left().type() == Expr.Type.REACTION_ROLE)
                    return generateAtom(atom, expr.right());
                if (expr.right().type() == Expr.Type.REACTION_ROLE)
                    return generateAtom(atom, expr.left());
            }

            int mapidx = atom != null ? mapidx(atom) : 0;
            if (mapidx == 0) {
                switch (expr.type()) {
                    case TRUE:
                        return "*";
                    case IS_AROMATIC:
                        return "a";
                    case IS_ALIPHATIC:
                        return "A";
                    case ELEMENT:
                        switch (expr.value()) {
                            case 9:
                                return "F";
                            case 17:
                                return "Cl";
                            case 35:
                                return "Br";
                            case 53:
                                return "I";
                        }
                        break;
                    case AROMATIC_ELEMENT:
                        switch (expr.value()) {
                            case 5:
                                return "b";
                            case 6:
                                return "c";
                            case 7:
                                return "n";
                            case 8:
                                return "o";
                            case 15:
                                return "p";
                            case 16:
                                return "s";
                        }
                        break;
                    case ALIPHATIC_ELEMENT:
                        switch (expr.value()) {
                            case 5:
                                return "B";
                            case 6:
                                return "C";
                            case 7:
                                return "N";
                            case 8:
                                return "O";
                            case 9:
                                return "F";
                            case 15:
                                return "P";
                            case 16:
                                return "S";
                            case 17:
                                return "Cl";
                            case 35:
                                return "Br";
                            case 53:
                                return "I";
                        }
                        break;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            generateAtom(sb, atom, expr, false);
            if (mapidx != 0)
                sb.append(':').append(mapidx);
            sb.append(']');
            return sb.toString();
        }

        private void writePart(StringBuilder sb, IAtom atom, IBond prev) {
            final List<IBond> bonds  = nbrs.get(atom);
            int               remain = bonds.size();
            sort(bonds, prev);
            if (prev != null) {
                remain--;
                sb.append(generate(atom, ((QueryBond) BondRef.deref(prev))));
            }
            sb.append(generateAtom(atom, ((QueryAtom) AtomRef.deref(atom)).getExpression()));
            avisit.add(atom);

            for (IBond bond : bonds) {
                if (bond == prev)
                    continue;
                // ring close
                if (isRingClose(bond)) {
                    Integer rnum = rnums.get(bond);
                    sb.append(generate(bond.getOther(atom), ((QueryBond) BondRef.deref(bond))));
                    sb.append(rnum);
                    rvisit[rnum] = false;
                    rnums.remove(bond);
                    remain--;
                }
                // ring open
                else if (isRingOpen(bond)) {
                    int rnum = nextRingNum();
                    sb.append(rnum);
                    rnums.put(bond, rnum);
                    rbonds.remove(bond);
                    remain--;
                }
                // branch
                else {
                    IAtom other = bond.getOther(atom);
                    remain--;
                    if (remain != 0)
                        sb.append('(');
                    writePart(sb, other, bond);
                    if (remain != 0)
                        sb.append(')');
                }
            }
        }

        private void writeParts(IAtom[] atoms, StringBuilder sb,
                                ReactionRole role) {
            boolean first    = true;
            int     prevComp = 0;
            for (IAtom atom : atoms) {
                if (role != null && role(atom) != role)
                    continue;
                if (avisit.contains(atom))
                    continue;
                int currComp = compGroup(atom);
                if (prevComp != currComp && prevComp != 0)
                    sb.append(')');
                if (!first)
                    sb.append('.');
                if (currComp != prevComp && currComp != 0)
                    sb.append('(');
                writePart(sb, atom, null);
                first = false;
                prevComp = currComp;
            }
            if (prevComp != 0)
                sb.append(')');
        }

        public String generate() {

            IAtom[] atoms = AtomContainerManipulator.getAtomArray(mol);
            sortAtoms(atoms);

            // mark ring closures
            for (IAtom atom : atoms)
                if (!avisit.contains(atom))
                    markRings(atom, null);
            avisit.clear();

            setBondDirs(mol);

            boolean       isRxn = role(atoms[atoms.length - 1]) != ReactionRole.None;
            StringBuilder sb    = new StringBuilder();
            if (isRxn) {
                writeParts(atoms, sb, ReactionRole.Reactant);
                sb.append('>');
                writeParts(atoms, sb, ReactionRole.Agent);
                sb.append('>');
                writeParts(atoms, sb, ReactionRole.Product);
            } else {
                writeParts(atoms, sb, null);
            }
            return sb.toString();
        }


        private void sortAtoms(IAtom[] atoms) {
            Arrays.sort(atoms,
                        new Comparator<IAtom>() {
                            @Override
                            public int compare(IAtom a, IAtom b) {
                                int cmp = role(a).compareTo(role(b));
                                if (cmp != 0)
                                    return cmp;
                                return Integer.compare(compGroup(a), compGroup(b));
                            }
                        });
        }
    }

    private static int compGroup(IAtom atom) {
        Integer id = atom.getProperty(CDKConstants.REACTION_GROUP);
        return id != null ? id : 0;
    }

    private static ReactionRole role(IAtom atom) {
        ReactionRole role = atom.getProperty(CDKConstants.REACTION_ROLE);
        return role != null ? role : ReactionRole.None;
    }

    private static int mapidx(IAtom atom) {
        Integer mapidx = atom.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
        return mapidx != null ? mapidx : 0;
    }

    private static int getBondStereoFlag(Expr expr) {
        switch (expr.type()) {
            case STEREOCHEMISTRY:
                switch (expr.value()) {
                    case 0:
                        return BSTEREO_UNSPEC;
                    case IStereoElement.TOGETHER:
                        return BSTEREO_CIS;
                    case IStereoElement.OPPOSITE:
                        return BSTEREO_TRANS;
                    default:
                        throw new IllegalArgumentException();
                }
            case OR:
                return getBondStereoFlag(expr.left()) |
                       getBondStereoFlag(expr.right());
            case AND:
                return getBondStereoFlag(expr.left()) &
                       getBondStereoFlag(expr.right());
            case NOT:
                return ~getBondStereoFlag(expr.left());
            default:
                return BSTEREO_ANY;
        }
    }

    /**
     * Traverse and expression and remove all expressions of a given type.
     *
     * @param expr the expression tree
     * @param type remove expressions of this type
     * @return the stripped expression, possibly null
     */
    private static Expr strip(Expr expr, Expr.Type type) {
        switch (expr.type()) {
            case AND:
            case OR:
                Expr left = strip(expr.left(), type);
                Expr right = strip(expr.right(), type);
                if (left != null && right != null)
                    expr.setLogical(expr.type(), left, right);
                else if (left != null)
                    return left;
                else if (right != null)
                    return right;
                else
                    return null;
            case NOT:
                Expr sub = strip(expr.left(), type);
                if (sub != null) {
                    expr.setLogical(expr.type(), sub, null);
                    return expr;
                } else {
                    return null;
                }
            default:
                return expr.type() == type ? null : expr;
        }
    }
}
