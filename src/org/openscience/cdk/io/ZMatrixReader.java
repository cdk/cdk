/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2003  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.io;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.CDKException;
import java.util.*;
import java.io.*;
import javax.vecmath.*;

/* This class is based on Dan Gezelter's XYZReader from Jmol */
/**
 * This class is expermiental
 * It reads Z matrices like in gaussians input file
 *
 * @keyword file format, Z-matrix
 */
public class ZMatrixReader implements ChemObjectReader {

  private BufferedReader input;
  
  /**
   * Constructs a ZMatrixReader from a Reader that contains the
   * data to be parsed.
   *
   * @param     input   Reader containing the data to read
   */
  public ZMatrixReader(Reader input) 
  {
    this.input = new BufferedReader(input);
  }
  
  /**
   *  Returns a ChemObject of type object bye reading from
   *  the input. 
   *
   *  The function supports only reading of ChemFile's.
   *
   * @param     object  ChemObject that types the class to return.
   * @throws    Exception when a ChemObject is requested that cannot be read.
   */
  public ChemObject read(ChemObject object) throws CDKException 
  {
    if (object instanceof ChemFile) 
      return (ChemObject)readChemFile();
    else 
      throw new CDKException("Only ChemFile objects can be read.");
  }

  /**
   *  Private method that actually parses the input to read a ChemFile
   *  object.
   *
   * @return A ChemFile containing the data parsed from input.
   */
  private ChemFile readChemFile()
  {
    ChemFile file = new ChemFile();
    ChemSequence chemSequence = new ChemSequence();
        
    int number_of_atoms = 0;
    StringTokenizer tokenizer;
        
    try 
    {
      String line = input.readLine();
      while (line.startsWith("#"))
        line = input.readLine();
      /*while (input.ready() && line != null) 
      {*/
        System.out.println("lauf");
        // parse frame by frame
        tokenizer = new StringTokenizer(line, "\t ,;");
                
        String token = tokenizer.nextToken();
        number_of_atoms = Integer.parseInt(token);
        String info = input.readLine();
                
        ChemModel chemModel = new ChemModel();
        SetOfMolecules setOfMolecules = new SetOfMolecules();
                
        Molecule m = new Molecule();
        m.setProperty(CDKConstants.TITLE ,info);

        String[] types = new String[number_of_atoms];
        double[] d = new double[number_of_atoms]; int[] d_atom = new int[number_of_atoms]; // Distances
        double[] a = new double[number_of_atoms]; int[] a_atom = new int[number_of_atoms]; // Angles
        double[] da = new double[number_of_atoms]; int[] da_atom = new int[number_of_atoms]; // Diederangles
        Point3d[] pos = new Point3d[number_of_atoms]; // calculated positions
                
        int i = 0;
        while(i < number_of_atoms)
        {
          line = input.readLine();
          System.out.println("line:\""+line+"\"");
          if (line == null) break;
          if (line.startsWith("#")) 
          {
            // skip comment in file
          } else 
          {
            d[i] = 0d; d_atom[i] = -1;
            a[i] = 0d; a_atom[i] = -1;
            da[i] = 0d; da_atom[i] = -1;

            tokenizer = new StringTokenizer(line, "\t ,;");
            int fields = tokenizer.countTokens();
                        
            if (fields < Math.min(i*2+1,7)) 
            {
              // this is an error but cannot throw exception
            }
            else if (i==0)
            {
              types[i] = tokenizer.nextToken();
              m.addAtom(new Atom(types[i], calcPos(i, pos, d, d_atom, a, a_atom, da, da_atom)));
              i++;
            }
            else if (i==1)
            {
              types[i] = tokenizer.nextToken();
              d_atom[i] = (new Integer(tokenizer.nextToken())).intValue()-1;
              d[i] = (new Double(tokenizer.nextToken())).doubleValue();
              m.addAtom(new Atom(types[i], calcPos(i, pos, d, d_atom, a, a_atom, da, da_atom)));
              i++;
            }
            else if (i==2)
            {
              types[i] = tokenizer.nextToken();
              d_atom[i] = (new Integer(tokenizer.nextToken())).intValue()-1;
              d[i] = (new Double(tokenizer.nextToken())).doubleValue();
              a_atom[i] = (new Integer(tokenizer.nextToken())).intValue()-1;
              a[i] = (new Double(tokenizer.nextToken())).doubleValue();
              m.addAtom(new Atom(types[i], calcPos(i, pos, d, d_atom, a, a_atom, da, da_atom)));
              i++;
            }
            else
            {
              types[i] = tokenizer.nextToken();
              d_atom[i] = (new Integer(tokenizer.nextToken())).intValue()-1;
              d[i] = (new Double(tokenizer.nextToken())).doubleValue();
              a_atom[i] = (new Integer(tokenizer.nextToken())).intValue()-1;
              a[i] = (new Double(tokenizer.nextToken())).doubleValue();
              da_atom[i] = (new Integer(tokenizer.nextToken())).intValue()-1;
              da[i] = (new Double(tokenizer.nextToken())).doubleValue();
              m.addAtom(new Atom(types[i], calcPos(i, pos, d, d_atom, a, a_atom, da, da_atom)));
              i++;
            }
          }
        }

        System.out.println("molecule:\n"+m);

        setOfMolecules.addMolecule(m);
        chemModel.setSetOfMolecules(setOfMolecules);
        chemSequence.addChemModel(chemModel);
        line = input.readLine();
      /*}*/
      file.addChemSequence(chemSequence);
    } catch (IOException e) 
    {
      // should make some noise now
      file = null;
    }
    return file;
  }

  private Point3d calcPos(int index, Point3d[] pos, double[] d, int[] d_atom,
                                                    double[] a, int[] a_atom,
                                                    double[] da, int[] da_atom)
  { 
    if (index==0)
    {
      pos[index] = new Point3d(0d,0d,0d);
      return pos[index];
    }
    else if (index==1)
    {
      pos[index] = new Point3d(d[1],0d,0d);
      return pos[index];
    }
    else if (index==2)
    { 
      pos[index] = new Point3d(-Math.cos((a[2]/180)*Math.PI)*d[2]+d[1],
                                Math.sin((a[2]/180)*Math.PI)*d[2],
                                0d);
      return pos[index];
    }
    else 
    { 
      Vector3d cd = new Vector3d();
      cd.sub(pos[da_atom[index]],
             pos[a_atom[index]]);

      Vector3d bc = new Vector3d();
      bc.sub(pos[a_atom[index]],
             pos[d_atom[index]]);

      Vector3d n1 = new Vector3d();
      n1.cross(cd, bc);
      n1.normalize();

      Vector3d n2 = rotate(n1,bc,da[index]);
      n2.normalize();
      Vector3d ba = rotate(bc,n2,-a[index]);

      ba.normalize();

      Vector3d ban = new Vector3d(ba);
      ban.scale(d[index]);

      Point3d result = new Point3d();
      result.add(pos[d_atom[index]], ba);
      pos[index] = result;
      return result;
    }
  }

  private Vector3d rotate(Vector3d vector, Vector3d axis, double angle)
  {
    Matrix3d rotate = new Matrix3d();
    rotate.set(new AxisAngle4d(axis.x, axis.y, axis.z, (angle/180)*Math.PI));
    Vector3d result = new Vector3d();
    rotate.transform(vector, result);
    return result;
  }
}
