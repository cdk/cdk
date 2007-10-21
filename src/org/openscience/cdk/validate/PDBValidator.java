/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 * 
 */
package org.openscience.cdk.validate;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Vector;

import org.openscience.cdk.EnzymeResidueLocator;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.MACiEReader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Validates the existence of references to dictionaries.
 *
 * @cdk.module experimental
 * @cdk.svnrev  $Revision$
 *
 * @author   Egon Willighagen
 * @cdk.created  2003-08-27
 * @cdk.require java1.4+
 */ 
public class PDBValidator extends AbstractValidator {

    private static LoggingTool logger;
    
    static {
        logger = new LoggingTool(PDBValidator.class);
    }

    private static String prefix = "http://www.rcsb.org/pdb/cgi/export.cgi/?format=PDB&pdbId=";
    private static String postfix = "&compression=None";

    public PDBValidator() {}

    public ValidationReport validateChemModel(IChemModel subject) {
        ValidationReport report = new ValidationReport();
        logger.debug("Starting to validate against PDB entry...");
        Object PDBcodeObject = subject.getProperty(MACiEReader.PDBCode);
        if (PDBcodeObject != null) {
            String PDB = PDBcodeObject.toString();
            logger.info("Validating against PDB code: " + PDB);
            IChemFile file = null;
            try {
                URL pdbQuery = new URL(prefix + PDB + postfix);
                logger.info("Downloading PDB file from: " + pdbQuery.toString());
                URLConnection connection = pdbQuery.openConnection();
                PDBReader reader = new PDBReader(new InputStreamReader(connection.getInputStream()));
                file = (IChemFile)reader.read(subject.getBuilder().newChemFile());
            } catch (Exception exception) {
                logger.error("Could not download or parse PDB entry");
                logger.debug(exception);
                return report;
            }
            logger.info("Successvully download PDB entry");
            
            // ok, now make a hash with all residueLocator in the PDB file
            Vector residues = new Vector();
            Iterator containers = ChemFileManipulator.getAllAtomContainers(file).iterator();
            while (containers.hasNext()) {
            	IAtomContainer allPDBAtoms = (IAtomContainer) containers.next();
            	Iterator atoms = allPDBAtoms.atoms();
            	logger.info("Found in PDB file, #atoms: " + allPDBAtoms.getAtomCount());
            	while (atoms.hasNext()) {
            		IAtom atom = (IAtom)atoms.next();
            		String resName = (String)atom.getProperty("pdb.resName");
            		String resSeq = (String)atom.getProperty("pdb.resSeq");
            		String resLocator = resName + resSeq;
            		if (!residues.contains(resLocator.toLowerCase())) {
            			logger.debug("Found new residueLocator: " + resLocator);
            			residues.add(resLocator.toLowerCase());
            		}
            	}
            }
            
            // now see if the model undergoing validation has bad locators
            containers = ChemModelManipulator.getAllAtomContainers(subject).iterator();
            while (containers.hasNext()) {
            	IAtomContainer allAtoms = (IAtomContainer) containers.next();
            	Iterator validateAtoms = allAtoms.atoms();
            	while (validateAtoms.hasNext()) {
            		// only testing PseudoAtom's
            		IAtom validateAtom = (IAtom)validateAtoms.next();
            		if (validateAtom instanceof EnzymeResidueLocator) {
            			ValidationTest badResidueLocator = new ValidationTest(validateAtom,
            					"ResidueLocator does not exist in PDB entry."
            			);
            			String label = ((IPseudoAtom)validateAtom).getLabel();
            			if (residues.contains(label.toLowerCase())) {
            				// yes, not problem
            				report.addOK(badResidueLocator);
            			} else {
            				badResidueLocator.setDetails(
            						"Could not find " + label + " in PDB entry for " + PDB
            				);
            				report.addError(badResidueLocator);
            			}
            		} else {
            			// ok, then don't test
            		}
            	}
            }
        }
        return report;
    }
    
}
