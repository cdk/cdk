/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.openscience.cdk.tools;

import org.openscience.cdk.*;

import java.io.*;
import org.xml.sax.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import JSX.*;


/**
 * A collection of tools for handling an xml document as a DOM tree
 *
 * @author     steinbeck
 * @created    2001-08-09
 */
public class XMLTools
{

	Document document = null;

	// An array of names for DOM node-types

	public final static String[] typeName = {
			"none",
			"Element",
			"Attr",
			"Text",
			"CDATA",
			"EntityRef",
			"Entity",
			"ProcInstr",
			"Comment",
			"Document",
			"DocType",
			"DocFragment",
			"Notation",
			};

	static boolean docStart = false;
	static boolean parsingDone = true;
	static org.w3c.dom.Node startNode = null;
	static boolean debug = false;

	/**
	 *  Searches the DOM tree for a certain
	 *  start node and returns it.
	 *
	 *
	 *@param  node      The node to start the search at
	 *@param  startTag  The node name to find.
	 *@return           The node that has the name specified by startTag
	 */
	public static org.w3c.dom.Node getDocStart(org.w3c.dom.Node node, String startTag) {
		org.w3c.dom.Node newNode = null;

		for (int f = 0; f < node.getChildNodes().getLength(); f++)
		{
			newNode = node.getChildNodes().item(f);
			if (debug)
			{
				System.out.println(node.getNodeName().toLowerCase());
			}
			if (node.getNodeName().trim().toLowerCase().equals(startTag.trim().toLowerCase()))
			{
				docStart = true;
				startNode = node;
				return startNode;
			}
		}

		for (int f = 0; f < node.getChildNodes().getLength(); f++)
		{
			newNode = node.getChildNodes().item(f);
			getDocStart(newNode, startTag);
			if (docStart)
			{
				return startNode;
			}
		}

		if (debug)
		{
			System.out.println("Nothing found");
		}
		return null;
	}

	/**
	 *  Returns a DOM tree for an XML document stored in a given file
	 *
	 *@param  file  The file with the xml document
	 *@return       A document with an XML tree
	 */
	public static org.w3c.dom.Document openDocument(File file) throws Exception
	{
		org.w3c.dom.Document document = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(file);
		return document;
	}
	
	/**
	 *  Returns a DOM tree for an XML document stored in a given InputStream
	 *
	 *@param  file  The file with the xml document
	 *@return       A document with an XML tree
	 */
	public static org.w3c.dom.Document openDocument(InputStream in) throws Exception
	{
		org.w3c.dom.Document document = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(in);
		return document;
	}

	public static String getAsXMLString(Object obj) throws java.io.IOException
	{
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ObjOut out = new ObjOut(printWriter); 
    		out.writeObject(obj); 
		printWriter.flush();
		writer.flush();
		return writer.toString();
	}
	
	public static Object getFromXMLString(String xmlString) throws java.io.IOException, java.lang.ClassNotFoundException
	{
		StringReader reader = new StringReader(xmlString);
		ObjIn in = new ObjIn(reader); 
    		Object obj = in.readObject(); 
		return obj;
	}

}
