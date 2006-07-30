/*
 * $RCSfile$   
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.database.DBReader;
import org.openscience.cdk.database.DBWriter;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 * @cdk.module orphaned
 */
public class DatabaseTest {
  private Connection db;	

  public String driver = "postgres";
  public String hostname = "localhost";
  public String port = "";
  public String database = "";
	public String user = "";
	public String pwd = "";
	
  public boolean connected = false;
  
	public DatabaseTest() {
  }
  
  public void connect() {
    connected = true;
		try	{
        if (driver.equals("postgres")) {
          Class.forName("postgres.Driver");
        } else if (driver.equals("mysql")) {
          Class.forName("org.gjt.mm.mysql.Driver").newInstance();
        }
        StringBuffer url = new StringBuffer();
        url.append("jdbc:");
        url.append(driver);
        url.append("://");
        url.append(hostname);
        if (!port.equals("")) {
          url.append(":");
          url.append(port);
        }
        url.append("/");
        url.append(database);
        System.out.println("Using url: " + url.toString());
	    	db = DriverManager.getConnection(url.toString(),user,pwd);
    } catch (ClassNotFoundException exc) {
		    System.out.println("Error while trying to load JDBC driver.");
		    System.out.println("Is JDBC driver in classpath?");
        connected = false;
		} catch (SQLException exc) {
		    System.out.println(exc.toString());      
        connected = false;
		} catch (Exception exc) {
		    System.out.println(exc.toString());      
        connected = false;
		}
  }

  public void read() {
    Molecule mol = null;
    try {
        DBReader dbr = new DBReader(db);
        dbr.setQuery("SELECT * FROM molecules WHERE C > 0");

  			mol = (Molecule)dbr.read(new Molecule());

        System.out.println(" molecule read from database \n" + mol);
        Renderer2DModel r2dm = new Renderer2DModel();
        MoleculeViewer2D mv = new MoleculeViewer2D(mol, r2dm);
        mv.display();        
		} catch (Exception exc) {
		    System.out.println("Error while doing test.");
			  exc.printStackTrace();
		}
	}
  
  public void write(org.openscience.cdk.interfaces.IMoleculeSet som) {
    try {
      DBWriter dbw = new DBWriter(db);
      dbw.write(som);
		} catch (Exception exc) {
		    System.out.println("Error while doing test.");
			  exc.printStackTrace();
		}
  }

  public static void printSyntax() {
    System.out.println("Syntax: DatabaseTest [options] <--read|--write [molfiles]>");
    System.out.println("  -d      driver (mysql or postgres)");
    System.out.println("  -h      hostname");
    System.out.println("  -P      port");
    System.out.println("  -n      database name");
    System.out.println("  -u      user");
    System.out.println("  -p      password");    
  }
	
    public static void main(String[] args) {
      int i = 0;
      String arg;
      boolean readmode = false;
      boolean writemode = false;
	    DatabaseTest drt = new DatabaseTest();
      Vector files = new Vector();

      if (args.length < 1) {
        DatabaseTest.printSyntax();
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
        }
      }
      while (i < args.length) {
        arg = args[i++];
        files.add(arg);
      }      
      
      drt.connect();
      if (drt.connected) {
        if (readmode) {
          System.out.println("Viewing molecules in database...");
          drt.read();
        } else if (writemode) {
          System.out.println("Storing molecules in database...");
          IChemObjectReader reader;
          Enumeration filestoread = files.elements();
          while (filestoread.hasMoreElements()) {
            String inFile = (String)filestoread.nextElement();
            try {
              System.out.println("Loading: " + inFile);
              if (inFile.endsWith(".cml")) {
                reader = new CMLReader(new FileReader(inFile));
                System.out.println("Expecting CML format...");
              } else {
                reader = new MDLReader(new FileInputStream(inFile));
                System.out.println("Expecting MDL MolFile format...");
              }
              ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
              
              org.openscience.cdk.interfaces.IChemSequence chemSequence;
              org.openscience.cdk.interfaces.IChemModel chemModel;
              org.openscience.cdk.interfaces.IMoleculeSet setOfMolecules;
              System.out.println("  number of sequences: " + chemFile.getChemSequenceCount());
              for (int sequence = 0; sequence < chemFile.getChemSequenceCount(); sequence++) {
                chemSequence = chemFile.getChemSequence(sequence);
                System.out.println("  number of models in sequence " + sequence + ": " + 
                                   chemSequence.getChemModelCount());
                for (int model = 0; model < chemSequence.getChemModelCount(); model++) {
                  chemModel = chemSequence.getChemModel(model);
                  setOfMolecules = chemModel.getSetOfMolecules();
                  System.out.println("  number of molecules in model " + model + ": " + 
                                       setOfMolecules.getMoleculeCount());
                  // do nothing yet
                  drt.write(setOfMolecules);
                }
              }
            } catch (FileNotFoundException e) {
              System.out.println("Skipping " + inFile + ". It does not exist.");
            } catch (Exception e) {
              System.out.println("Error occured. Skipping " + inFile);
            }
          }
        } else {
          // connection failed
        }        
      } else {
        DatabaseTest.printSyntax();
      }
    }
}



		

