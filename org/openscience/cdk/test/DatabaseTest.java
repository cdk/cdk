/* 
 * $RCSfile$   
 * $Author$  
 * $Date$ 
 * $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk.test;

import org.openscience.cdk.renderer.*;
import org.openscience.cdk.*;
import org.openscience.cdk.database.*;
import java.sql.*;


public class DatabaseTest
{
	public String url = "jdbc:postgresql://lemon.ice.mpg.de:5432/martinstestdb";
  public String driver = "postgresql";
  public String hostname = "lemon.ice.mpg.de";
  public String port = "5432";
  public String database = "martinstestdb";
	public String user = "postgres";
	public String pwd = "";
	
	public DatabaseTest() {
  }
  
  public void read() {
    DBReader dbr;
    Connection db;	
    Molecule mol = null;
		try	{
        if (driver.equals("postgres")) {
          Class.forName("postgres.Driver");
        } else if (driver.equals("mysql")) {
          Class.forName("org.gjt.mm.mysql.Driver");
        }
        String url = "jdbc:" + driver + "://" + hostname + ":" + port + "/" +
                     database;
	    	db = DriverManager.getConnection(url,user,pwd);

        dbr = new DBReader(db);
        dbr.setQuery("SELECT * FROM molecules WHERE C > 0");

  			mol = (Molecule)dbr.read(new Molecule());

        System.out.println(" molecule read from database \n" + mol);
        Renderer2DModel r2dm = new Renderer2DModel();
        MoleculeViewer2D mv = new MoleculeViewer2D(mol, r2dm);
        mv.display();        
		} catch (ClassNotFoundException exc) {
		    System.out.println("Error while trying to load JDBC driver.");
		    System.out.println("Is JDBC driver in classpath?");
		} catch (Exception exc) {
		    System.out.println("Error while doing test.");
			  exc.printStackTrace();
		}
	}
	
    public static void main(String[] args) {
      int i = 0, j;
      String arg;
      char flag;
      boolean readmode = false;
      boolean writemode = false;
	    DatabaseReaderTest drt = new DatabaseReaderTest();

      if (args.length < 1) {
        System.out.println("Syntax: DatabaseTest [options] <--read|--write [molfiles]>");
        System.out.println("  -d      driver (mysql or postgres)");
        System.out.println("  -h      hostname");
        System.out.println("  -P      port");
        System.out.println("  -u      user");
        System.out.println("  -p      password");
        System.exit(0);
      }
      
      while (i < args.length && args[i].startsWith("-")) {
        arg = args[i++];

        // use this type of check for arguments that require arguments
        if (arg.equals("-d")) {
          if (i < args.length)
            drt.driver = args[i++];
          else
            System.err.println("-d requires a driver name");
        } else if (arg.equals("-h")) {
          if (i < args.length)
            drt.hostname = args[i++];
          else
            System.err.println("-h requires a hostname");
        } else if (arg.equals("-p")) {
          if (i < args.length)
            drt.pwd = args[i++];
          else
            System.err.println("-p requires a password");
        } else if (arg.equals("-u")) {
          if (i < args.length)
            drt.user = args[i++];
          else
            System.err.println("-u requires a username");
        } else if (arg.equals("-n")) {
          if (i < args.length)
            drt.database = args[i++];
          else
            System.err.println("-n requires a database name");
        } else if (arg.equals("-P")) {
          if (i < args.length)
            drt.port = args[i++];
          else
            System.err.println("-P requires a port number");
        } else if (arg.equals("--read")) {
          readmode = true;
        } else if (arg.equals("--write")) {
          writemode = true;
        } else if (writemode) {
          // take this argument as filename of molecule to insert in database
        }
      }
      
      if (readmode) {
        drt.read();
      } else if (writemode) {
        // not implemented yet
      }
    }
}



		

