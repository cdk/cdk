/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.test.database;

import java.sql.Connection;
import java.sql.DriverManager;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.database.DBReader;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 * @cdk.module orphaned
 */
public class DatabaseReaderTest
{
	DBReader dbr;
	Connection db;	
	public String url = "jdbc:postgresql://lemon.ice.mpg.de:5432/martinstestdb";
	public String user = "postgres";
	public String pwd = "";



	
	public DatabaseReaderTest()
	{
		DBReader dbr;
        Molecule mol = null;

		try
		{
		    Class.forName("postgresql.Driver");
	    	db = DriverManager.getConnection(url,user,pwd);
		}
		catch(Exception exc)
		{
		    System.out.println("Error while trying to load JDBC driver");
			exc.printStackTrace();
		}
		dbr = new DBReader(db);
		dbr.setQuery("SELECT * FROM molecules WHERE C > 0");
		try
		{
			mol = (Molecule)dbr.read(new Molecule());
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
		}
		System.out.println(" molecule read from database \n" + mol);
		Renderer2DModel r2dm = new Renderer2DModel();
		MoleculeViewer2D mv = new MoleculeViewer2D(mol, r2dm);
		mv.display();
	}
	


	
    public static void main(String[] args) 
	{
	    new DatabaseReaderTest();
    }
}



		
