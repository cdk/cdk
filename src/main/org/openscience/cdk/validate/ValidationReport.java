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

import java.util.Vector;

/**
 * A report on validation of chemical semantics.
 */
public class ValidationReport {

    private Vector errors;
    private Vector warnings;
    private Vector oks;
    private Vector cdkErrors;

    /**
     * Constructs a new empty ValidationReport.
     */
    public ValidationReport() {
        errors = new Vector();
        warnings = new Vector();
        oks = new Vector();
        cdkErrors = new Vector();
    }

    /**
     * Merges the tests with the tests in this ValidationReport.
     */
    public void addReport(ValidationReport report) {
        errors.addAll(report.errors);
        warnings.addAll(report.warnings);
        oks.addAll(report.oks);
        cdkErrors.addAll(report.cdkErrors);
    }
    
    /**
     * Adds a validation test which gives serious errors.
     */
    public void addError(ValidationTest test) {
        errors.add(test);
    }
    
    /**
     * Adds a validation test which indicate a possible problem.
     */
    public void addWarning(ValidationTest test) {
        warnings.add(test);
    }
    
    /**
     * Adds a validation test which did not find a problem.
     */
    public void addOK(ValidationTest test) {
        oks.add(test);
    }
    
    /**
     * Adds a CDK problem.
     */
    public void addCDKError(ValidationTest test) {
        cdkErrors.add(test);
    }
    
    /**
     * Returns the number of failed tests.
     */
    public int getErrorCount() {
        return errors.size();
    }
    
    /**
     * Returns the number of tests which gave warnings.
     */
    public int getWarningCount() {
        return warnings.size();
    }
    
    /**
     * Returns the number of tests without errors.
     */
    public int getOKCount() {
        return oks.size();
    }
    
    /**
     * Returns the number of CDK errors.
     */
    public int getCDKErrorCount() {
        return cdkErrors.size();
    }
    
    /**
     * Returns the number of CDK errors.
     */
    public int getCount() {
        return cdkErrors.size() + errors.size() + warnings.size() + oks.size();
    }
    
    /**
     * Returns an array of ValidationTest errors.
     */
    public Vector getErrors() {
        return errors;
    }

    /**
     * Returns an array of ValidationTest warnings.
     */
    public Vector getWarnings() {
        return warnings;
    }

    /**
     * Returns an array of ValidationTest which did not find problems.
     */
    public Vector getOKs() {
        return oks;
    }

    /**
     * Returns an array of ValidationTest indicating CDK problems.
     */
    public Vector getCDKErrors() {
        return cdkErrors;
    }

}
