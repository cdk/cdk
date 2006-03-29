/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.applications;

/**
 * Program that gives some information when running the large CDK jar.
 *
 * @cdk.module applications
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
public class DistLargeInfo {

    public DistLargeInfo() {}

    /**
     * actual program
     */
    public static void main(String[] args) {
        String version = "unknown";
        Package self = Package.getPackage("org.openscience.cdk");
        if (self != null) {
            version = self.getImplementationVersion();
        }
        
        System.out.println("CDK Utilities v. " + version);
        System.out.println();
        System.out.println("  Syntax: java -cp cdk-all.jar <program>");
        System.out.println();
        System.out.println("  Available programs:");
        System.out.println();
        System.out.println("    org.openscience.cdk.applications.FileConvertor");
        System.out.println("    org.openscience.cdk.applications.FileFormatGuesser");
        System.out.println("    org.openscience.cdk.applications.FingerPrinter");
        System.out.println("    org.openscience.cdk.applications.IUPACNameGenerator");
        System.out.println("    org.openscience.cdk.applications.SubstructureFinder");
        System.out.println("    org.openscience.cdk.applications.Validator");
    }

}



