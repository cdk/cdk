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
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.database;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.io.CMLReader;

/**
 * A command line application for administering the database
 *
 * @author egonw
 *
 * @deprecated
 * @cdk.keyword MySQL
 * @cdk.depends postgresql7.0-1.2.jar
 * @cdk.depends mysql-2.0.4.jar
 *
 * @cdk.module orphaned
 */
public class DBAdmin {

    private org.openscience.cdk.tools.LoggingTool logger;


    /**
     *  The default URL for the DB to administer
     *   overwritten by command line option --useURL.
     **/
    public String url = "jdbc:postgresql://lemon.ice.mpg.de:5432/martinstestdb";
    /**
     *  The default user that has command over the database
     *  overwritten by command line option --username
     **/
    public String user = "postgres";

    /**
     *  A password to be supplied by the command line option --passwd
     **/
    public String pwd = "";


    /**
     *  A hostname to be used in a instead of localhost in the standard URL shown above.
     *  Overwritten by command line option --useHost
     **/
    public String host = "lemon.ice.mpg.de";


    /**
     *  Some booleans for bookkeeping while parsing command line options
     **/
    public boolean createDefaultTables = false, listTables = false, deleteTables = false, loadReferences = false;
    public boolean useURL = false, useHost = false, addContributor = false, insertMolecule = false, useDriver = false;

    /**
     *  Some strings for bookkeeping of the arguments of the command line options
     **/
    public String thisMol = "", thisEmail = "", thisRefFile = "";


    /**
     *  String for SQL query
     **/
    public String query = null;

    /**
     *  Driver for database
     **/
    public String driver = "postgresql";

    /**
     *  An object representing a connection to a database
     **/
    public Connection db = null;

    /**
     *
     **/
    public String databaseName = null;

    /**
     *  A statement send to the database
     **/
    public Statement st = null;

    /**
     *  The String containing the message describing the usage of this tool
     **/
    public static String usage = null;

    /**
     *  Main method, just instantiates a DBAdmin object.
     **/
    public static void main(String args[]) {
        try {
           new DBAdmin(args);
        } catch(Exception exc) {
		/* This is a command line application            *
		 * Do not convert these System.out/err.println() *
		 * to logger statements                          */
            System.out.println(usage);
            exc.printStackTrace();
        }
    }

    /**
     *
     **/
    DBAdmin(String args[]) {
        logger = new org.openscience.cdk.tools.LoggingTool(this);

	String option = null;
        usage = "Usage: java DBAdmin options\n";
        usage += "Options:\n";
        usage += "--useURL 'thisURL' -r            Administer DB specified by 'thisURL'\n";
        usage += "                                 (example: \"jdbc:postgresql://www.nmrshiftdb.org/nmrshiftdb\")\n";
        usage += "--useHost 'thisHost' -h          Administer DB specified by 'thisHost'\n";
        usage += "                                 (URL is formed as follows: \"jdbc:postgresql://thisHost/nmrshiftdb\")\n";
        usage += "--useDatabase 'thisDB' -b         Database name\n";
        usage += "--useDriver 'thisDriver' -D      Driver for DB specified by 'thisDriver'\n";
        usage += "                                 (mysql | postgresql)\n";
        usage += "--username 'thisUser' -u         Log into database using 'thisUser' as the username\n";
        usage += "--passwd 'thisPasswd' -p         Log into database using 'thisPasswd' as the passwd\n";
        usage += "--createDefaultTables -c         Initialize empty database with default tables\n";
        usage += "--insertMolecule 'xxx.mol' -i    Loads some test data into table\n";
        usage += "--listTables -l                  List tables, their structure and some info on their content\n";
        usage += "--deleteTables -d                Delete all the tables in the database\n";

        if (args.length == 0) {
            	System.out.println(usage);
             	System.exit(0);
        }
        for (int f = 0;f < args.length;f++) {
            option = args[f];

            if (option.equals("--useURL") || option.equals("-r")) {
                useURL = true;
                url = args[++f];
                logger.info("Using URL: " + url);
            } else if(option.equals("--useHost") || option.equals("-h")) {
                useHost = true;
                host = args[++f];
                logger.info("Using Host: " + host);
            } else if(option.equals("--username") || option.equals("-u")) {
                user = args[++f];
                logger.info("Using user name: " + user);
            } else if(option.equals("--useDriver") || option.equals("-D")) {
	        useDriver = true;
                driver = args[++f];
                logger.info("Using Driver: " + driver);
            } else if(option.equals("--useDatabase") || option.equals("-b")) {
                databaseName = args[++f];
                logger.info("Using Database: " + databaseName);
            } else if(option.equals("--passwd") || option.equals("-p")) {
                pwd = args[++f];
            } else if(option.equals("--createDefaultTables") || option.equals("-c")) {
                createDefaultTables = true;
            } else if(option.equals("--listTables") || option.equals("-l")) {
                listTables = true;
            } else if(option.equals("--insertMolecule") || option.equals("-i")) {
            	insertMolecule = true;
	        thisMol = args[++f];
            } else if(option.equals("--deleteTables") || option.equals("-d")) {
            	deleteTables = true;
            } else {
                System.err.println("Unknown option: " + args[f]);
            	System.out.println(usage);
            	System.exit(0);
            }
       }

	/*
	 *  Parsing of command line options done, do it now
	 */

	 // try to load database driver
        try {
            if (useHost || useDriver) {
                if (driver.equals("postgresql")) {
                    logger.info("Loading PostGres driver... ");
                    Class.forName("org.postgresql.Driver");
                } else if (driver.equals("mysql")) {
                    logger.info("Loading MySQL driver... ");
                    Class.forName("org.gjt.mm.mysql.Driver").newInstance();
                }
                logger.info("done.");
	    }
            if (useHost) {
            	url = "jdbc:" + driver + "://" + host + "/" + databaseName;
            }
        }catch(Exception exc) {
            criticalExit("Error while trying to load JDBC driver",exc);
        }

        // detect yet unsupported PostgreSQL
		if (driver.equals("postgresql") || (url.indexOf("postgresql") != -1)) {
			/* This is a command line application            *
			 * Do not convert these System.out/err.println() *
			 * to logger statements                          */			
		    System.out.println("PostGresQL is not yet supported.");
		}

	// try to make connection to database
        try {
	    logger.info("Trying to connect to dabase...");
            db = DriverManager.getConnection(url,user,pwd);
	    logger.info("done");
        }catch(Exception exc) {
	    criticalExit("Error while trying to connect to database",exc);
        }

	// execute command
        if(createDefaultTables)
        {
       	    createDefaultTables();
        }
        if (listTables)
        {
        	listTables();
        }
        if(insertMolecule)
        {
        	insertMolecule(thisMol);
        }
        if(deleteTables)
        {
        	deleteTables();
        }
        try
        {
            db.close();
        }
        catch(Exception exc)
        {
            criticalExit("Error while trying to close connection",exc);
        }
    }

    /**
     *
     **/
    protected void createDefaultTables()
    {
        try
        {
            /* table for molecules.
             * Molecules are characterized by an implicit OID.
             * This OID is assigned by the PostgreSQL database once a 
             * row (i.e. a molecule) in the database is created.
             * There further are the unique autonom name, some other unique or non-unique
             * identifiers like a CAS registry number and the Beilstein RN
             * as well as a timestamp of the moment of their creation in the database.
             */
            st = db.createStatement();
            query = "CREATE TABLE molecules (";
            query += "molid INT, CMLcode TEXT";
            query += ");";
            logger.debug(query);
            st.executeUpdate(query);
            System.out.println("Default tables 'molecules' created.");
            logger.info("Default tables 'molecules' created.");

            /* table for names of structures.
             * 'names' consists of a name and a link to a 'molecule' (by using it's OID)
             * for which this name
             * is valid. This allows for having as many different names for a molecules
             * as one likes.
             */
            st = db.createStatement();
            query = "CREATE TABLE chemnames (";
            query += "molid INT, autonom TEXT, name TEXT";
            query += ");";
            logger.debug(query);
            st.executeUpdate(query);
            System.out.println("Default tables 'chemnames' created.");
            logger.info("Default tables 'chemnames' created.");

	   /* table for names of structures.
             * 'names' consists of a name and a link to a 'molecule' (by using it's OID)
             * for which this name
             * is valid. This allows for having as many different names for a molecules
             * as one likes.
             */
            st = db.createStatement();
            query = "CREATE TABLE indices (";
            query += "molid INT, cas TEXT, beilstein TEXT";
            query += ");";
            logger.debug(query);
            st.executeUpdate(query);
            System.out.println("Default tables 'indices' created.");
            logger.info("Default tables 'indices' created.");
        } catch(Exception exc) {
            criticalExit("Error while trying to create tables in database",exc);
        }
    }


    /**
     *
     **/
    protected void insertMolecule(String s) {
	DBWriter dbw = new DBWriter(db);
        File file = new File(s);
        System.out.println(file.getAbsolutePath());
		try {
			CMLReader mdlr = new CMLReader(new FileReader(file));
			ChemFile cf = (ChemFile)mdlr.read(new ChemFile());
	                for (int sequence = 0; sequence < cf.getChemSequenceCount(); sequence++) {
	                	org.openscience.cdk.interfaces.IChemSequence chemSequence = cf.getChemSequence(sequence);
 			     logger.info("number of models in sequence " + sequence + ": " +
                                 chemSequence.getChemModelCount());
			     for (int model = 0; model < chemSequence.getChemModelCount(); model++) {
			    	 org.openscience.cdk.interfaces.IChemModel chemModel = chemSequence.getChemModel(model);
				  org.openscience.cdk.interfaces.IMoleculeSet setOfMolecules = chemModel.getSetOfMolecules();
				  logger.info("number of molecules in model " + model + ": " +
                                      setOfMolecules.getMoleculeCount());
	                          for (int i = 0; i < setOfMolecules.getMoleculeCount(); i++) {
	                        	  org.openscience.cdk.interfaces.IMolecule molecule = setOfMolecules.getMolecule(i);
			              dbw.write(molecule);
                                  }
                             }
	                }
		} catch (Exception exc) {
			exc.printStackTrace();
		}
    }


    /**
     *
     **/
    protected void listTables()
    {
        Vector tableNames = new Vector();
        try
        {
            db.createStatement();
        }
        catch(Exception exc)
        {
        }
        String query = null;
        try
        {
            DatabaseMetaData dmd = db.getMetaData();
            ResultSet rs = dmd.getTables(null,null,null,null);
            while(rs.next())
            {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
            System.out.println("Found " + tableNames.size() + " table(s), now listing: ");
            for(int f = 0;f < tableNames.size();f++)
            {
                rs = dmd.getColumns(null,null,(String)tableNames.elementAt(f),null);
                query = "Table " + (String)tableNames.elementAt(f) + " has columns:\n";
                while(rs.next())
                {
                    query += rs.getString(4) + " | ";
                }
                System.out.println(query + "\n\n");
            }
        }
        catch(Exception exc)
        {
            System.out.println("Error while trying to get metadata.");
            System.out.println(exc);
        }
    }

    /**
     *
     **/
    protected boolean deleteTables()
    {
        Vector tableNames = new Vector();
        int done = 0;
        Statement st = null;
        String query = null;
        DatabaseMetaData dmd = null;
        ResultSet rs = null;
        try
        {
            st = db.createStatement();
            query = null;
            dmd = db.getMetaData();
            rs = dmd.getTables(null,null,null,null);
        }
        catch(Exception exc)
        {
            System.out.println(exc);
            return false;
        }
        try
        {
            while(rs.next())
            {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
        }
        catch(Exception exc)
        {
            System.out.println(exc);
            return false;
        }
        do
        {
            done = 0;
            for(int f = 0;f < tableNames.size();f++)
            {
                if(((String)tableNames.elementAt(f)).indexOf("_number_seq") < 1)
                {
                    try
                    {
                        query = "drop table " + (String)tableNames.elementAt(f) + ";";
                        st.executeUpdate(query);
                    }
                    catch(Exception exc)
                    {
                        System.out.println(exc);
                        done ++;
                    }
                }
                else
                {
                    try
                    {
                        query = "drop sequence " + (String)tableNames.elementAt(f) + ";";
                        st.executeUpdate(query);
                    }
                    catch(Exception exc)
                    {
                        System.out.println(exc);
                        done ++;
                    }
                }
            }
            System.out.println(done + " - " +tableNames.size());
        }
        while(done < tableNames.size());
        return true;
    }

    /**
     *
     **/
    protected void criticalExit(String s, Exception exc) {
        System.out.println(s);
        logger.error(s);
        logger.error(exc.toString());
        System.out.println("Existing program.... Done!");
        logger.error("Existing program.... Done!");
        System.exit(1);
    }

}
