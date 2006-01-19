/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.iupac.generator;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.Fragment;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;

/**
 * This class implements a IUPAC name generator.
 * IMPORTANT: it is highly experimental, and NOT
 * usefull for use.
 *
 * @cdk.module experimental
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl> 
 * @cdk.created 2002-05-21
 *
 * @cdk.keyword IUPAC name
 */
public class IUPACNameGenerator {

    private Locale locale;
    private IUPACNameLocalizer localizer;
    private Vector rules;
    private IUPACName name;

    private HydrogenAdder hydrogenAdder;

    private org.openscience.cdk.tools.LoggingTool logger;

    /**
     * Constructor for a localized IUPAC name generator.
     */
    public IUPACNameGenerator(Locale l) {
        this.locale = l;
        this.localizer = IUPACNameLocalizer.getInstance(l);

        this.name = new IUPACName();

        // instantiate logger
        logger = new org.openscience.cdk.tools.LoggingTool(this);

        // instantiate the saturation checker
        try {
            hydrogenAdder = new HydrogenAdder();
        } catch (Exception e) {
            logger.error("Cannot instantiate hydrogen adder!");
        }

        // this is dirty! Rules should be automatically detected!
        rules = new Vector();
        rules.add(new org.openscience.cdk.iupac.generator.sectiona.Rule1dot1());
        rules.add(new org.openscience.cdk.iupac.generator.sectiona.Rule1dot2());
        rules.add(new org.openscience.cdk.iupac.generator.sectiona.Rule2dot1());
        rules.add(new org.openscience.cdk.iupac.generator.sectionc.Rule102dot1());
        rules.add(new org.openscience.cdk.iupac.generator.sectionc.Rule103dot1());
    }

    /**
     *  Constructor for a IUPAC name generator.
     */
    public IUPACNameGenerator() {
        this(new Locale("en", "US"));
    }

    public IUPACName getName() {
        return name;
    }
    /**
     *  Generates a IUPAC name for a molecule.
     *
     *  <p>Mechanism:
     *  <ol>
     *    <li>
     *      apply the first applicable rule
     *      <ul><li>this marks the named atoms</li></ul>
     *    <li>
     *      delete named atoms
     *      <ol>
     *        <li>
     *          for each a in atoms-to-delete do
     *          <ul>
     *            <li>check for bonded atoms that need not deletion
     *            <li>mark those atoms with a FLAG and with a ref to
     *                the deleted atom
     *          </ul>
     *        </li>
     *      <ol>
     *    <li>determine fragments that are left behind
     *    <li>for each f in fragments recurse to step 1.
     *  </ol>
     *
     * @param moleculeToName Must be a Molecule or a Fragment which needs to be
     *                       named, an attempt to name any other AtomContainer will fail.
     */
    public void generateName(IAtomContainer moleculeToName) {       
        //Must use a clone to avoid deleting the user's atoms.
        IAtomContainer m = (IAtomContainer) moleculeToName.clone();
        
        if (!(m instanceof Fragment || m instanceof IMolecule)) {
            return;
        }
        // set some initial values
        m.setProperty(Rule.COMPLETED_FLAG, "no");
        m.setProperty(Rule.NONE_APPLICABLE, "no");

        /** First calculate some general statistics that
         *  can speed up the application of rules.
         */
        IMolecule molecule = new org.openscience.cdk.Molecule(m);
        try {
            hydrogenAdder.addExplicitHydrogensToSatisfyValency(molecule);
        } catch (Exception exception) {
            logger.error("Error while saturating molecule");
        };
        MFAnalyser mfa = new MFAnalyser(molecule);
        logger.info("Naming struct with MF: " + mfa.getMolecularFormula());
        m.setProperty(Rule.ELEMENT_COUNT, new Integer(mfa.getElementCount()));
        m.setProperty(Rule.CARBON_COUNT, new Integer(mfa.getAtomCount("C")));
        m.setProperty(Rule.HYDROGEN_COUNT, new Integer(mfa.getAtomCount("H")));
        m.setProperty(Rule.CHLORO_COUNT, new Integer(mfa.getAtomCount("Cl")));
        m.setProperty(Rule.BROMO_COUNT, new Integer(mfa.getAtomCount("Br")));
        m.setProperty(Rule.FLUORO_COUNT, new Integer(mfa.getAtomCount("F")));

        // step 0
        logger.info("Step 0");
        markAtomsAsUnnamed(m);
        // step 1: apply rule with highest priority
        logger.info("Step 1");
        IUPACNamePart inp = applyFirstApplicableRule(m);
        if (inp != null) {
            logger.debug("Adding first name part");
            name.addFront(inp);
            logger.info("current name:\n" + name.toString());
            if (m.getProperty(Rule.COMPLETED_FLAG).equals("no") &&
                m.getProperty(Rule.NONE_APPLICABLE).equals("no")) {
                logger.debug("Molecule has not been named completely.");
                // step 2: delete all named atoms
                logger.info("Step 2");
                Enumeration fragments = deleteAtomsAndPartitionIntoFragments(m);
                // step 3
                while (fragments.hasMoreElements()) {
                    logger.info("naming fragment");
                    FragmentWithAtomicValencies f = (FragmentWithAtomicValencies)fragments.nextElement();
                    // merge name ? how ?
                    this.generateName(f);
                }
            }
        }
        logger.info("current name:\n" + name.toString());
        deleteNamedAtoms(m);
        return;
    }

    private Enumeration deleteAtomsAndPartitionIntoFragments(IAtomContainer ac) {
        Vector frags = new Vector();

        for (int i = ac.getAtomCount()-1; i >= 0; i--) {
        	org.openscience.cdk.interfaces.IAtom a = ac.getAtomAt(i);
            if (a.getProperty(Rule.ATOM_NAMED_FLAG).equals("yes")) {
                a.setProperty(Rule.ATOM_HAS_VALENCY, "no");
                // loop over connected atoms
                org.openscience.cdk.interfaces.IAtom[] connectedAtoms = ac.getConnectedAtoms(a);
                for (int j = 0; j < connectedAtoms.length; j++) {
                	org.openscience.cdk.interfaces.IAtom b = connectedAtoms[j];
                    if (b.getProperty(Rule.ATOM_NAMED_FLAG).equals("yes")) {
                        b.setProperty(Rule.ATOM_HAS_VALENCY, "no");
                    } else {
                        b.setProperty(Rule.ATOM_HAS_VALENCY, "yes");
                        a.setProperty(Rule.ATOM_HAS_VALENCY, "yes");
                    }
                }
            }
        }

        deleteNamedAtoms(ac);
        // step 3
        logger.info("Step 3");
        try {
            ISetOfMolecules moleculeSet = ConnectivityChecker.partitionIntoMolecules(ac);
            IMolecule[] molecules = moleculeSet.getMolecules();
            for (int j=0; j<molecules.length; j++) {
                FragmentWithAtomicValencies fwav = new FragmentWithAtomicValencies(molecules[j]);
                for (int i=0; i < fwav.getAtomCount(); i++) {
                    try {
                    	org.openscience.cdk.interfaces.IAtom a = fwav.getAtomAt(i);
                        String prop = (String)a.getProperty(Rule.ATOM_HAS_VALENCY);
                        if (prop != null && prop.equals("yes")) {
                            fwav.addValencyAtAtom(a);
                        }
                    } catch (Exception e) {
                        logger.error("Error in program!");
                        logger.error(e.toString());
                    }
                }
                frags.add(fwav);
            }
        } catch (Exception e) {
            logger.error("Cannot partition remainder of molecule into fragments!");
            logger.error(e.toString());
        }
        return frags.elements();
    }

    private IUPACNamePart applyFirstApplicableRule(IAtomContainer m) {
        IUPACNamePart name = null;

        // Try all rules
        Enumeration rulenum = rules.elements();
        m.setProperty(Rule.COMPLETED_FLAG, "no");
        boolean done = false;
        while (rulenum.hasMoreElements() && !done) {
            Object o = (Object)rulenum.nextElement();
            // make sure Rule is really a Rule
            if (o instanceof NamingRule) {
                NamingRule rule = (NamingRule)o;
                // use localization
                rule.setIUPACNameLocalizer(localizer);
                logger.info("Testing rule: " + rule.getName());
                name = rule.apply(m);
                if (name != null) {
                    logger.debug("current name:");
                    logger.debug(name.toString());
                    // done = m.getProperty(Rule.COMPLETED_FLAG).equals("yes");
                    done = true; // i.e. start again with first rule
                }
            } else if (o instanceof NumberingRule) {
                logger.info("Skipping NumberingRule class: " + o.getClass().getName());
            } else {
                logger.warn("Skipping non-Rule class: " + o.getClass().getName());
            }
        }
        return name;
    }

    private void deleteNamedAtoms(IAtomContainer ac) {
        for (int i = ac.getAtomCount()-1; i >= 0; i--) {
        	org.openscience.cdk.interfaces.IAtom a = ac.getAtomAt(i);
            if (a.getProperty(Rule.ATOM_NAMED_FLAG).equals("yes")) {
                logger.info("Deleting atom: " + a.getSymbol());
                ac.removeAtomAndConnectedElectronContainers(ac.getAtomAt(i));
            }
        }
    }

    private void markAtomsAsUnnamed(IAtomContainer ac) {
        for (int i = ac.getAtomCount()-1; i >= 0; i--) {
            ac.getAtomAt(i).setProperty(Rule.ATOM_NAMED_FLAG, "no");
        }
    }

	public HydrogenAdder getHydrogenAdder() {
		return hydrogenAdder;
	}

	public void setHydrogenAdder(HydrogenAdder hydrogenAdder) {
		this.hydrogenAdder = hydrogenAdder;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public IUPACNameLocalizer getLocalizer() {
		return localizer;
	}

	public void setLocalizer(IUPACNameLocalizer localizer) {
		this.localizer = localizer;
	}

	public org.openscience.cdk.tools.LoggingTool getLogger() {
		return logger;
	}

	public void setLogger(org.openscience.cdk.tools.LoggingTool logger) {
		this.logger = logger;
	}

	public Vector getRules() {
		return rules;
	}

	public void setRules(Vector rules) {
		this.rules = rules;
	}

	public void setName(IUPACName name) {
		this.name = name;
	}

}
