/* $RCSfile$ 
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2001-2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.io;
 
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @cdkPackage io
 *
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @created 2001-07-14
 */
public class LogWriter
{
	private StringBuffer buffer = new StringBuffer();

	private PrintStream output;

	public LogWriter()
  {
  }
  
  public LogWriter(OutputStream output)
	{
		setOutputStream(output);
	}

	public void append(String string)
	{
		buffer.append(string);
		if (output!=null)
			output.print(string);
	}

	public void print(String string)
  { 
    buffer.append(string);
    if (output!=null)
      output.print(string);
  }

	public void println(String string)
  {
    buffer.append(string);
		buffer.append("\n");
    if (output!=null)
      output.println(string);
  }

	public void println()
  {
    buffer.append("\n");
    if (output!=null)
      output.println();
  }

	public void setOutputStream(OutputStream output)
	{
		if (output==null)
			this.output = null;
		else
			this.output = new PrintStream(output);
	}

	public void clear()
	{
		buffer = new StringBuffer();
	}

	public String toString()
	{
		return buffer.toString();
	} 
}
