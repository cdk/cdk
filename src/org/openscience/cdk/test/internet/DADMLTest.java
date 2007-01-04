/* $RCSfile$
 * $Author$   
 * $Date$   
 * $Revision$
 * 
 * Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.test.internet;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.AtomicTable;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.internet.DADMLReader;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module  test-extra
 * @cdk.require dadml
 */
public class DADMLTest {

    private LoggingTool logger;

    public DADMLTest(String superdb, String type, String index) {
      logger = new LoggingTool(this);
      LoggingTool.configureLog4j();

      try {
        logger.info("SuperDB: ", superdb);
        logger.info("index type: ", type);
        logger.info("index: ", index);
        DADMLReader reader = new DADMLReader(superdb);
        reader.setQuery(type, index);
        Molecule m = (Molecule)reader.read(new Molecule());

        MoleculeViewer2D mv = new MoleculeViewer2D(m);
        mv.display();
        AtomicTable at = new AtomicTable(m);
        at.display();
      } catch(Exception exc) {
	      logger.error(exc.toString());
      }
    }
    
    public static void main(String[] args) {
      if (args.length == 3) {
        String superdb = args[0];
        String type = args[1];
        String index = args[2];
        new DADMLTest(superdb, type, index);
      } else {
        System.out.println("Syntax: DADMLTest <superdb> <index_type> <index>");
      }
    }
}

