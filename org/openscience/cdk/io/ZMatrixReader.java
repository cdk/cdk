/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.exception.*;
import java.util.*;
import java.io.*;
import javax.vecmath.*;

/* This class is based on Dan Gezelter's XYZReader from Jmol */
/**
 * This class is expermiental
 * It reads Z matrices like in gaussians input file
 */
public class ZMatrixReader implements ChemObjectReader 
{
  private BufferedReader input;
  
  public ZMatrixReader(Reader input) 
  {
    this.input = new BufferedReader(input);
  }
    
  public ChemObject read(ChemObject object) throws UnsupportedChemObjectException 
  {
    if (object instanceof ChemFile) 
      return (ChemObject)readChemFile();
    else 
      throw new UnsupportedChemObjectException("Only supported is ChemFile.");
  }

  private ChemFile readChemFile() 
  {
    ChemFile file = new ChemFile();
    ChemSequence chemSequence = new ChemSequence();
        
    int number_of_atoms = 0;
    StringTokenizer tokenizer;
        
    try 
    {
      String line = input.readLine();
      while (input.ready() && line != null) 
      {
        // parse frame by frame
        tokenizer = new StringTokenizer(line, "\t ,;");
                
        String token = tokenizer.nextToken();
        number_of_atoms = Integer.parseInt(token);
        String info = input.readLine();
                
        ChemModel chemModel = new ChemModel();
        SetOfMolecules setOfMolecules = new SetOfMolecules();
                
        Molecule m = new Molecule();
        m.setTitle(info);

        String[] types = new String[number_of_atoms];
        double[] d = new double[number_of_atoms]; int[] d_atom = new int[number_of_atoms]; // Distances
        double[] a = new double[number_of_atoms]; int[] a_atom = new int[number_of_atoms]; // Angles
        double[] da = new double[number_of_atoms]; int[] da_atom = new int[number_of_atoms]; // Diederangles
                
        for (int i = 0; i < number_of_atoms; i++) 
        {
          line = input.readLine();
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
            }
            else if (i==1)
            {
              types[i] = tokenizer.nextToken();
              d[i] = (new Double(tokenizer.nextToken())).doubleValue();
              d_atom[i] = (new Integer(tokenizer.nextToken())).intValue();
            }
            else if (i==2)
            {
              types[i] = tokenizer.nextToken();
              d[i] = (new Double(tokenizer.nextToken())).doubleValue();
              d_atom[i] = (new Integer(tokenizer.nextToken())).intValue();
              a[i] = (new Double(tokenizer.nextToken())).doubleValue();
              a_atom[i] = (new Integer(tokenizer.nextToken())).intValue();
            }
            else
            {
              types[i] = tokenizer.nextToken();
              d[i] = (new Double(tokenizer.nextToken())).doubleValue();
              d_atom[i] = (new Integer(tokenizer.nextToken())).intValue();
              a[i] = (new Double(tokenizer.nextToken())).doubleValue();
              a_atom[i] = (new Integer(tokenizer.nextToken())).intValue();
              da[i] = (new Double(tokenizer.nextToken())).doubleValue();
              da_atom[i] = (new Integer(tokenizer.nextToken())).intValue();
            }
          }
        }

        setOfMolecules.addMolecule(m);
        chemModel.setSetOfMolecules(setOfMolecules);
        chemSequence.addChemModel(chemModel);
        line = input.readLine();
      }
      file.addChemSequence(chemSequence);
    } catch (IOException e) 
    {
      // should make some noise now
      file = null;
    }
    return file;
  }

  private Vector3d calcPos(int index, double[] d, int[] d_atom,
                                      double[] a, int[] a_atom,
                                      double[] da, int[] da_atom)
  { 
    if (index==0)
      return new Vector3d(0d,0d,0d);
    else if (index==1)
      return new Vector3d(d[1],0d,0d);
    else if (index==2)
    { 
      return new Vector3d(-Math.cos(a[2])*d[2]+d[1], 
                           Math.sin(a[2])*d[2],
                           0d);
    }
    else 
    { 
      Vector3d cd = new Vector3d();
      cd.sub(calcPos(da_atom[index], d, d_atom, a, a_atom, da, da_atom),
             calcPos(a_atom[index], d, d_atom, a, a_atom, da, da_atom));

      Vector3d bc = new Vector3d();
      bc.sub(calcPos(a_atom[index], d, d_atom, a, a_atom, da, da_atom),
             calcPos(d_atom[index], d, d_atom, a, a_atom, da, da_atom));

      Vector3d n1 = new Vector3d();
      n1.cross(cd, bc);

      Vector3d n2 = rotate(n1,bc,da[index]);
      Vector3d ba = rotate(bc,n2,-a[index]);

      ba.normalize();

      Vector3d ban = new Vector3d(ba);
      ban.scale(d[index]);

      Vector3d result = new Vector3d();
      result.add(calcPos(d_atom[index], d, d_atom, a, a_atom, da, da_atom), ba);
      return result;
    }
  }

  private Vector3d rotate(Vector3d vector, Vector3d axis, double angle)
  {
    Matrix3d rotate = new Matrix3d();
    rotate.set(new AxisAngle4d(axis.x, axis.y, axis.z, angle));
    Vector3d result = new Vector3d();
    rotate.transform(vector, result);
    return result;
  }
}
