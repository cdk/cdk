/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemSequence;

/**
 * This Validator tests the internal datastructures, and
 * tries to detect inconsistencies in it.
 *
 * @author   Egon Willighagen
 * @cdk.created  2003-08-22
 */ 
public class CDKValidator extends AbstractValidator {

    public CDKValidator() {
    }
    
    public ValidationReport validateChemFile(ChemFile subject) {
        return validateChemFileNulls(subject);
    }
    public ValidationReport validateChemSequence(ChemSequence subject) {
        return validateChemSequenceNulls(subject);
    }

    private ValidationReport validateChemFileNulls(ChemFile chemFile) {
        ValidationReport report = new ValidationReport();
        org.openscience.cdk.interfaces.ChemSequence[] sequences = chemFile.getChemSequences();
        ValidationTest hasNulls = new ValidationTest(chemFile,
            "ChemFile contains a null ChemSequence."
        );
        for (int i=0; i < chemFile.getChemSequenceCount(); i++) { // DIRTY !!!! FIXME !!!!!
            // but it does not seem to work on 1.4.2 otherwise....
            if (sequences[i] == null) {
                report.addError(hasNulls);
            } else {
                report.addOK(hasNulls);
            }
        }
        return report;
    }
        
    private ValidationReport validateChemSequenceNulls(ChemSequence sequence) {
        ValidationReport report = new ValidationReport();
        org.openscience.cdk.interfaces.IChemModel[] models = sequence.getChemModels();
        ValidationTest hasNulls = new ValidationTest(sequence,
            "ChemSequence contains a null ChemModel."
        );
        for (int i=0; i < sequence.getChemModelCount(); i++) { // DIRTY !!!! FIXME !!!!!
            // but it does not seem to work on 1.4.2 otherwise....
            if (models[i] == null) {
                report.addError(hasNulls);
            } else {
                report.addOK(hasNulls);
            }
        }
        return report;
    }

}
