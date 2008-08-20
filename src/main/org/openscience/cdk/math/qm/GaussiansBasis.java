/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2001-2007  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * 
 * Contact: cdk-devel@lists.sf.net
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
 *  
 */
package org.openscience.cdk.math.qm;
 
import org.openscience.cdk.math.Matrix;
import org.openscience.cdk.math.Vector;

/**
 * This class contains the information to use gauss function as a base
 * for calculation of quantum mechanics. The function is defined as:
 * <pre>
 * f(x,y,z) = (x-rx)^nx * (y-ry)^ny * (z-rz)^nz * exp(-alpha*(r-ri)^2)
 * </pre>
 *
 * <p>
 * S = &lt;phi_i|phi_j><br>
 * J = &lt;d/dr phi_i | d/dr phi_j><br>
 * V = &lt;phi_i | 1/r | phi_j><br>
 * 
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.svnrev  $Revision$
 * @cdk.created 2001-06-14
 * @cdk.module  qm
 *
 * @cdk.keyword Gaussian basis set
 */ 
public class GaussiansBasis implements IBasis
{
  private int count; // number of the basis functions
  private int[] nx; // [base]
  private int[] ny; // [base]
  private int[] nz; // [base]
  private double[] alpha; // [base]
  private double[] norm; // Normalize the bases
  private Vector[] r; // [basis] Positions of base functions

  private int count_atoms; // number of the atoms
  private Vector[] rN; // [atom] Positions of the atoms
  private int[] oz; // [atom] Atomic numbers of the atoms
  
  // For the volume
  private double minx = 0; private double maxx = 0;
  private double miny = 0; private double maxy = 0;
  private double minz = 0; private double maxz = 0;

  public GaussiansBasis()
  {
  }

  /**
   * Set up basis with gauss funktions
   * f(x,y,z) = (x-rx)^nx * (y-ry)^ny * (z-rz)^nz * exp(-alpha*(r-ri)^2).
   *
   * @param atoms The atom will need to calculate the core potential
   */
  public GaussiansBasis(int[] nx, int[] ny, int[] nz, double[] alpha, Vector[] r, org.openscience.cdk.interfaces.IAtom[] atoms)
  {
    setBasis(nx, ny, nz, alpha, r, atoms);
  }

  /**
   * Set up basis with gauss funktions
   * f(x,y,z) = (x-rx)^nx * (y-ry)^ny * (z-rz)^nz * exp(-alpha*(r-ri)^2).
   *
   * @param atoms The atom will need to calculate the core potential
   */
  protected void setBasis(int[] nx, int[] ny, int[] nz, double[] alpha, Vector[] r, org.openscience.cdk.interfaces.IAtom[] atoms)
  {
    int i;

    //count_atoms = molecule.getSize();
    count_atoms = atoms.length;
    
//    logger.debug("Count of atoms: "+count_atoms);
    
    this.rN = new Vector[count_atoms];
    this.oz = new int[count_atoms];
    for(i=0; i<count_atoms; i++)
    { 
      this.rN[i] = (new Vector(atoms[i].getPoint3d())).mul(1.8897);
      this.oz[i] = atoms[i].getAtomicNumber();
//      logger.debug((i+1)+".Atom Z="+this.oz[i]+" r="+(new Vector(atoms[i].getPoint3d()))+"[angstrom]");
    }
//    logger.debug();

    count = Math.min(nx.length,
            Math.min(ny.length,
            Math.min(nz.length,alpha.length)));

//    logger.debug("Count of bases: "+count);

    this.nx = new int[count];
    this.ny = new int[count];
    this.nz = new int[count];
    this.alpha = new double[count];
    //this.atoms = new int[count];
    this.norm = new double[count];
    this.r = new Vector[count];
    this.oz = new int[count];

    for(i=0; i<count; i++)
    {
      this.nx[i] = nx[i];
      this.ny[i] = ny[i];
      this.nz[i] = nz[i];
      this.alpha[i] = alpha[i];
      //this.atoms[i] = atoms[i];
      //this.r[i] = new Vector(atoms[i].getPoint3d()).mul(1.8897);
      this.r[i] = r[i].mul(1.8897);

      norm[i] = Math.sqrt(calcS(i,i));
      if (norm[i]==0d) 
        norm[i] = 1d;
      else
        norm[i] = 1/norm[i];

//      logger.debug((i+1)+".Base nx="+nx[i]+" ny="+ny[i]+" nz="+nz[i]+" alpha="+
//              alpha[i]+" r="+r[i]+" norm="+norm[i]);

      if (i>0)
      {
        minx = Math.min(minx,this.r[i].vector[0]); maxx = Math.max(maxx,this.r[i].vector[0]);
        miny = Math.min(miny,this.r[i].vector[1]); maxy = Math.max(maxy,this.r[i].vector[1]);
        minz = Math.min(minz,this.r[i].vector[2]); maxz = Math.max(maxz,this.r[i].vector[2]);
      }
      else
      {
        minx = r[0].vector[0]; maxx = r[0].vector[0];
        miny = r[0].vector[1]; maxy = r[0].vector[1];
        minz = r[0].vector[2]; maxz = r[0].vector[2];
      }
    }

    minx -= 2; maxx += 2;
    miny -= 2; maxy += 2;
    minz -= 2; maxz += 2;

//    logger.debug();
  }

  /**
   * Gets the number of base vectors.
   */
  public int getSize()
  {
    return count;
  }

  /**
   * Gets the dimension of the volume, which describes the base.
   */
  public double getMinX()
  {
    return minx;
  }

  /**
   * Gets the dimension of the volume, which describes the base.
   */
  public double getMaxX()
  {
    return maxx;
  }

  /**
   * Gets the dimension of the volume, which describes the base.
   */
  public double getMinY()
  {
    return miny;
  }
  
  /**
   * Gets the dimension of the volume, which describes the base.
   */
  public double getMaxY()
  {
    return maxy;
  }

  /**
   * Gets the dimension of the volume, which describes the base.
   */
  public double getMinZ()
  {
    return minz;
  }
  
  /**
   * Gets the dimension of the volume, which describes the base.
   */
  public double getMaxZ()
  {
    return maxz;
  }

  /**
   * Calculates the function value an (x,y,z).
   * @param index The number of the base 
   */
  public double getValue(int index, double x, double y, double z)
  {
    double dx = (x*1.8897)-r[index].vector[0];
    double dy = (y*1.8897)-r[index].vector[1];
    double dz = (z*1.8897)-r[index].vector[2];
    double result = 1d;
    int i;
    for(i=0; i<nx[index]; i++)
      result *= dx;
    for(i=0; i<ny[index]; i++)
      result *= dy;
    for(i=0; i<nz[index]; i++)
      result *= dz;
    return result*Math.exp(-alpha[index]*(dx*dx+dy*dy+dz*dz));
  }

  /**
   * Calculates the function values.
   * @param index The number of the base 
   */
  public Vector getValues(int index, Matrix m)
  {
    if (m.rows!=3)
      return null;

    double x,y,z,dx,dy,dz,mx,my,mz;

    x = m.matrix[0][0]; y = m.matrix[1][0]; z = m.matrix[2][0];
            
    dx = (x*1.8897)-r[index].vector[0];
    dy = (y*1.8897)-r[index].vector[1];
    dz = (z*1.8897)-r[index].vector[2];
    
    Vector result = new Vector(m.columns);
    int i,j; 

    mx = 1d;
    for(i=0; i<nx[index]; i++)
      mx *= dx;

    my = 1d;
    for(i=0; i<ny[index]; i++)
      my *= dy;

    mz = 1d;
    for(i=0; i<nz[index]; i++)
      mz *= dz;

    dx *= dx; dy *= dy; dz *= dz;

    result.vector[0] = mx*my*mz*Math.exp(-alpha[index]*(dx+dy+dz));

    for(j=1; j<m.columns; j++)
    {
      if (x!=m.matrix[0][j])
      {
        x = m.matrix[0][j];
        dx = (x*1.8897)-r[index].vector[0];
        mx = 1d;
        for(i=0; i<nx[index]; i++)
          mx *= dx;
        dx *= dx;
      }

      if (y!=m.matrix[1][j])
      {
        y = m.matrix[1][j];
        dy = (y*1.8897)-r[index].vector[1];
        my = 1d; 
        for(i=0; i<ny[index]; i++)
          my *= dy;
        dy *= dy;
      }

      if (z!=m.matrix[2][j])
      {
        z = m.matrix[2][j];
        dz = (z*1.8897)-r[index].vector[2];
        mz = 1d; 
        for(i=0; i<nz[index]; i++)
          mz *= dz;
        dz *= dz;
      }

      result.vector[j] = mx*my*mz*Math.exp(-alpha[index]*(dx+dy+dz));
    }

    return result;
  }

  /**
   * Gets the position of a base.
   */
  public Vector getPosition(int index)
  {
    return r[index].duplicate().mul(0.52918);
  }

  public double calcD(double normi, double normj, double alphai, double alphaj, Vector ri, Vector rj)
  {
    double dx = ri.vector[0]-rj.vector[0];
    double dy = ri.vector[1]-rj.vector[1];
    double dz = ri.vector[2]-rj.vector[2];
    return Math.exp(-((alphai*alphaj)/(alphai+alphaj))*(dx*dx+dy*dy+dz*dz))*normi*normj;
  }

  /**
   * Transfer equation for the calculation of the overlap integral..
   */
  private double calcI(int ni, int nj, double alphai, double alphaj, double xi, double xj)
  {
    if ((ni<0) || (nj<0))
    {
      System.err.println("Error [Basis.calcI()]: nj="+nj);
      return Double.NaN; // Falls fehlerhafte Parameter
    } 
    double[][] I = new double[nj+1][];
    
    double alphaij = alphai+alphaj;
    double xij = (alphai*xi+alphaj*xj)/alphaij;
    
    I[0] = new double[(nj+ni+1)];
    I[0][0] = Math.sqrt(Math.PI)/Math.sqrt(alphaij); // I(0,0)=G(0)
    
    if ((nj+ni+1)>1)
    {
      I[0][1] = -(xi-xij)*I[0][0]; // I(0,1)=G(1)
      
      // I(0,n)=G(n)
      for(int i=2; i<=(nj+ni); i++)
        I[0][i] = ((i-1)/(2*alphaij))*I[0][i-2]-(xi-xij)*I[0][i-1];
        
      for(int j=1; j<=nj; j++)
      {
        I[j] = new double[(nj+ni+1)-j];
        
        for(int i=0; i<=(nj+ni-j); i++)
          I[j][i] = I[nj-1][ni+1]+(xi-xj)*I[nj-1][ni];
      }   
    }

    return I[nj][ni];
  }

  public double calcS(int i, int j)
  {
    //logger.debug("S: i="+i+" j="+j+" r[i]="+r[i]+" r[j]="+r[j]);
    return calcD(norm[i], norm[j], alpha[i],alpha[j],r[i],r[j]) * 
           calcI(nx[i],nx[j],alpha[i],alpha[j],r[i].vector[0],r[j].vector[0]) *
           calcI(ny[i],ny[j],alpha[i],alpha[j],r[i].vector[1],r[j].vector[1]) * 
           calcI(nz[i],nz[j],alpha[i],alpha[j],r[i].vector[2],r[j].vector[2]);
  }

  /**
   * Transfer equation the the calculation of the impulse
   */
  public double calcJ(int ni, int nj, double alphai, double alphaj, double xi, double xj)
  {
    if (ni>1)
      return -4d*alphai*alphai*   calcI(ni+2,nj,alphai,alphaj,xi,xj)
             +2d*alphai*(2*ni+1)* calcI(ni  ,nj,alphai,alphaj,xi,xj)
             -ni*(ni-1)*          calcI(ni-2,nj,alphai,alphaj,xi,xj);
    else if (ni==1)
      return -4d*alphai*alphai*   calcI(3   ,nj,alphai,alphaj,xi,xj)
             +6d*alphai*          calcI(1   ,nj,alphai,alphaj,xi,xj);
    else if (ni==0)
      return -4d*alphai*alphai*   calcI(2   ,nj,alphai,alphaj,xi,xj)
             +2d*alphai*          calcI(0   ,nj,alphai,alphaj,xi,xj);
             
    System.err.println("Error [Basis.calcJ]: ni="+ni);
    return Double.NaN; // Falls fehlerhafte Parameter
  }

  public double calcJ(int i, int j)
  {
    return calcD(norm[i], norm[j], alpha[i],alpha[j],r[i],r[j])*
           (calcJ(nx[i],nx[j],alpha[i],alpha[j],r[i].vector[0],r[j].vector[0])*
            calcI(ny[i],ny[j],alpha[i],alpha[j],r[i].vector[1],r[j].vector[1])*
            calcI(nz[i],nz[j],alpha[i],alpha[j],r[i].vector[2],r[j].vector[2])+

            calcI(nx[i],nx[j],alpha[i],alpha[j],r[i].vector[0],r[j].vector[0])*
            calcJ(ny[i],ny[j],alpha[i],alpha[j],r[i].vector[1],r[j].vector[1])*
            calcI(nz[i],nz[j],alpha[i],alpha[j],r[i].vector[2],r[j].vector[2])+

            calcI(nx[i],nx[j],alpha[i],alpha[j],r[i].vector[0],r[j].vector[0])*
            calcI(ny[i],ny[j],alpha[i],alpha[j],r[i].vector[1],r[j].vector[1])*
            calcJ(nz[i],nz[j],alpha[i],alpha[j],r[i].vector[2],r[j].vector[2]));
  }

  /**
   * Transfer equation for the calculation of core potentials
   */
  public double calcG(int n, double t, double alphai, double alphaj, double xi, double xj, double xN)
  {
    if (n>1)
      return ((n-1)/(2*(alphai+alphaj)))*                       calcG(n-2, t, alphai, alphaj, xi, xj, xN)
             -(n-1)*t*t*                                        calcG(n-2, t, alphai, alphaj, xi, xj, xN)
             +(((alphai*xi+alphaj*xj)/(alphai+alphaj))-xi)*     calcG(n-1, t, alphai, alphaj, xi, xj, xN)
             +(((alphai*xi+alphaj*xj)/(alphai+alphaj))-xN)*t*t* calcG(n-1, t, alphai, alphaj, xi, xj, xN);
             
    else if (n==1)
      return (((alphai*xi+alphaj*xj)/(alphai+alphaj))-xi)*      calcG(0, t, alphai, alphaj, xi, xj, xN)
             +(((alphai*xi+alphaj*xj)/(alphai+alphaj))-xN)*t*t* calcG(0, t, alphai, alphaj, xi, xj, xN);
             
    else if (n==0)
      return Math.sqrt(Math.PI)/Math.sqrt(alphai+alphaj);
      
    System.err.println("Error [Basis.calcG]: n="+n);
    return Double.NaN; // Falls fehlerhafte Parameter
  }

  /**
   * Transfer equation for the calculation of core potentials.
   */
  private double calcI(int ni, int nj, double t, double alphai, double alphaj, 
                        double xi, double xj, double xN)
  {
    if (nj>0)
      return calcI(ni+1, nj-1, t, alphai, alphaj, xi, xj, xN)+
             (xi-xj)*calcI(ni, nj-1, t, alphai, alphaj, xi, xj, xN);
             
    else if (nj==0)
      return calcG(ni, t, alphai, alphaj, xi, xj, xN);
      
    System.err.println("Error [Basis.calcI()]: nj="+nj);
    return Double.NaN; // Falls fehlerhafte Parameter
  }

  /**
   * Calculates the core potential.
   * It use a 10 point Simpson formula.
   *
   * @param i Index of the first base
   * @param j Index of the second base
   * @param rN Position the core potential
   * @param Z Atomic number of the nucleous
   */
  public double calcV(int i, int j, Vector rN, double Z)
  {
    double f,t;

    double sum1,sum2;
    double f1,f2;

    int steps = 10;
    double h = 1d/steps;

    double alphaij = alpha[i]+alpha[j];

    double rxij = (alpha[i]*r[i].vector[0]+alpha[j]*r[j].vector[0])/alphaij;
    double ryij = (alpha[i]*r[i].vector[1]+alpha[j]*r[j].vector[1])/alphaij;
    double rzij = (alpha[i]*r[i].vector[2]+alpha[j]*r[j].vector[2])/alphaij;

    double X = alphaij*((rxij-rN.vector[0])*(rxij-rN.vector[0]) +
                        (ryij-rN.vector[1])*(ryij-rN.vector[1]) +
                        (rzij-rN.vector[2])*(rzij-rN.vector[2]));

    double C = 2*calcD(norm[i], norm[j], 
              alpha[i], alpha[j], r[i], r[j])*Math.sqrt(alphaij)/Math.sqrt(Math.PI);

    sum1 = 0;
    for(f = 1; f<steps; f=f+2)
    {
      t = f*h;
      sum1 += Math.exp(-X*t*t)*calcI(nx[i], nx[j], t, alpha[i], alpha[j], 
                                     r[i].vector[0], r[j].vector[0], rN.vector[0]) *
                               calcI(ny[i], ny[j], t, alpha[i], alpha[j], 
                                     r[i].vector[1], r[j].vector[1], rN.vector[1]) *
                               calcI(nz[i], nz[j], t, alpha[i], alpha[j], 
                                     r[i].vector[2], r[j].vector[2], rN.vector[2]);
    }

    sum2 = 0;
    for(f = 2; f<steps; f=f+2)
    {
      t = f*h;
      sum2 += Math.exp(-X*t*t)*calcI(nx[i], nx[j], t, alpha[i], alpha[j],
                                     r[i].vector[0], r[j].vector[0], rN.vector[0]) *
                               calcI(ny[i], ny[j], t, alpha[i], alpha[j], 
                                     r[i].vector[1], r[j].vector[1], rN.vector[1]) *
                               calcI(nz[i], nz[j], t, alpha[i], alpha[j], 
                                     r[i].vector[2], r[j].vector[2], rN.vector[2]);
    }

    t = 0d;
    f1 = Math.exp(-X*t*t)*calcI(nx[i], nx[j], t, alpha[i], alpha[j],
                                     r[i].vector[0], r[j].vector[0], rN.vector[0]) *
                               calcI(ny[i], ny[j], t, alpha[i], alpha[j], 
                                     r[i].vector[1], r[j].vector[1], rN.vector[1]) *
                               calcI(nz[i], nz[j], t, alpha[i], alpha[j], 
                                     r[i].vector[2], r[j].vector[2], rN.vector[2]);

    t = 1d;
    f2 = Math.exp(-X*t*t)*calcI(nx[i], nx[j], t, alpha[i], alpha[j],
                                     r[i].vector[0], r[j].vector[0], rN.vector[0]) *
                               calcI(ny[i], ny[j], t, alpha[i], alpha[j], 
                                     r[i].vector[1], r[j].vector[1], rN.vector[1]) *
                               calcI(nz[i], nz[j], t, alpha[i], alpha[j], 
                                     r[i].vector[2], r[j].vector[2], rN.vector[2]);

    return (h/3)*(f1 + 4*sum1 + 2*sum2 + f2)*Z*C;
  }

  /**
   * Calculates the core potential.
   * It use a 10 point Simpson formula.
   *
   * @param i Index of the first base
   * @param j Index of the second base
   */
  public double calcV(int i, int j)
  {
    double result = 0d;
    for(int k=0; k<count_atoms; k++)
    { 
      //logger.debug("k="+k+" r="+r[k]);
      result += calcV(i,j, rN[k], oz[k]);
    }
    return -result; // Vorsicht negatives Vorzeichen
  }

  /**
   * Transfer equation for a four center integral.
   */
  public double calcG(int n, int m, double u, 
     double alphai, double alphaj, double alphak, double alphal, double xi, double xj, double xk, double xl)
  {
    if ((n<0) || (m<0))
    {
//      logger.debug("Error(CalcG):Bad parameter n="+n+" m="+m);
      return Double.NaN;
    }

    double alphaij = alphai+alphaj;
    double alphakl = alphak+alphal;

    double xij = (alphai*xi+alphaj*xj)/alphaij;
    double xkl = (alphak*xk+alphal*xl)/alphakl;

    double C00 = (xij-xi)-((u*u*alphakl*(xij-xkl))/(u*u*(alphaij+alphakl)+alphaij*alphakl));
    double Cs00 = (xkl-xk)+((u*u*alphaij*(xij-xkl))/(u*u*(alphaij+alphakl)+alphaij*alphakl));
    double B00 = u*u/(2*(u*u*(alphaij+alphakl)+alphaij*alphakl));
    double B10 = (u*u+alphakl)/(2*(u*u*(alphaij+alphakl)+alphaij*alphakl));
    double Bs01 = (u*u+alphaij)/(2*(u*u*(alphaij+alphakl)+alphaij*alphakl));

    double[][] G = new double[n+1][m+1];

    int i,j;

    G[0][0] = 1d;
    
    // Nach 1)
    if (n>0)
      G[1][0] = C00;

    // Nach 1)
    for(i=2; i<=n; i++)
      G[i][0] = (i-1)*B10   *G[i-2][0]+
                C00         *G[i-1][0];

    // Nach 2)
    if (m>0)
      G[0][1] = Cs00;

    // Nach 2)
    for(i=2; i<=m; i++)
      G[0][i] = (i-1)*Bs01 *G[0][i-2]+
                Cs00       *G[0][i-1];

    // Nach 1)
    if (n>0)
      for(i=1; i<=m; i++)
        G[1][i] = i*B00       *G[0][i-1]+
                  C00         *G[0][i];

    // Nach 1)
    for(i=2; i<=n; i++)
      for(j=1; j<=m; j++)
        G[i][j] = (i-1)*B10   *G[i-2][j]+
                  j*B00       *G[i-1][j-1]+
                  C00         *G[i-1][j];

    return G[n][m];
  }  

  /**
   * Transfer equation for a four center integral.
   */
  public double calcI(int ni, int nj, int nk, int nl, double u, 
                      double alphai, double alphaj, double alphak, double alphal,
                      double xi, double xj, double xk, double xl)
  {
    if (nj>0)
      return          calcI(ni+1,nj-1,nk  ,nl  ,u,alphai,alphaj,alphak,alphal,xi,xj,xk,xl)+
             (xj-xi)*calcI(ni  ,nj-1,nk  ,nl  ,u,alphai,alphaj,alphak,alphal,xi,xj,xk,xl);

    if (nl>0)
      return          calcI(ni  ,nj  ,nk+1,nl-1,u,alphai,alphaj,alphak,alphal,xi,xj,xk,xl)+
             (xl-xk)*calcI(ni  ,nj  ,nk  ,nl-1,u,alphai,alphaj,alphak,alphal,xi,xj,xk,xl);

    if ((ni==0) && (nj==0) && (nk==0) && (nl==0))
      return 1d;

    if ((nj==0) && (nl==0))
      return calcG(ni,nk,u,alphai,alphaj,alphak,alphal,xi,xj,xk,xl);

//    logger.debug("Error(CalcI):Bad parameter ni="+ni+" nj="+nj+" nk="+nk+" nl="+nl);
    return Double.NaN;
  }

  public double calcI(int i, int j, int k, int l)
  {
    double f,t;
    
    double sum1,sum2;
    double f1,f2;
    
    // Berechnen der Integration nach Simson
    int steps = 10;
    double h = 1d/steps;
    //double h2 = 2d*h;

    double alphaij = alpha[i]+alpha[j];
    double alphakl = alpha[k]+alpha[l];

    double rxij = (alpha[i]*r[i].vector[0]+alpha[j]*r[j].vector[0])/alphaij;
    double ryij = (alpha[i]*r[i].vector[1]+alpha[j]*r[j].vector[1])/alphaij;
    double rzij = (alpha[i]*r[i].vector[2]+alpha[j]*r[j].vector[2])/alphaij;

    double rxkl = (alpha[k]*r[k].vector[0]+alpha[l]*r[l].vector[0])/alphakl;
    double rykl = (alpha[k]*r[k].vector[1]+alpha[l]*r[l].vector[1])/alphakl;
    double rzkl = (alpha[k]*r[k].vector[2]+alpha[l]*r[l].vector[2])/alphakl;

    double alpha0 = alphaij*alphakl/(alphaij+alphakl);

    double X = alpha0*((rxij-rxkl)*(rxij-rxkl) +
                       (ryij-rykl)*(ryij-rzkl) +
                       (rzij-rzkl)*(rzij-rzkl));
    
    double C = (Math.PI*Math.PI*Math.PI/Math.pow((alpha[i]+alpha[j])*(alpha[k]+alpha[l]),1.5))*
               Math.sqrt(alpha0)*
               calcD(norm[i], norm[j], alpha[i], alpha[j], r[i], r[j])*
               calcD(norm[k], norm[l], alpha[k], alpha[l], r[k], r[l])*
               (2d/Math.sqrt(Math.PI));

    sum1 = 0;
    for(f = 1; f<steps; f=f+2)
    {
      t = f*h;
      sum1 += Math.exp(-X*t*t)*calcI(nx[i], nx[j], nx[k], nx[l], t, alpha[i], alpha[j], alpha[k], alpha[l],
                 r[i].vector[0], r[j].vector[0], r[k].vector[0], r[l].vector[0]) *
                               calcI(ny[i], ny[j], ny[k], ny[l], t, alpha[i], alpha[j], alpha[k], alpha[l],
                 r[i].vector[1], r[j].vector[1], r[k].vector[1], r[l].vector[1]) *
                               calcI(nz[i], nz[j], nz[k], nz[l], t, alpha[i], alpha[j], alpha[k], alpha[l],
                 r[i].vector[2], r[j].vector[2], r[k].vector[2], r[l].vector[2]);

    }

    sum2 = 0;
    for(f = 2; f<steps; f=f+2)
    {
      t = f*h;
      sum2 += Math.exp(-X*t*t)*calcI(nx[i], nx[j], nx[k], nx[l], t, alpha[i], alpha[j], alpha[k], alpha[l],
                 r[i].vector[0], r[j].vector[0], r[k].vector[0], r[l].vector[0]) *
                               calcI(ny[i], ny[j], ny[k], ny[l], t, alpha[i], alpha[j], alpha[k], alpha[l],
                 r[i].vector[1], r[j].vector[1], r[k].vector[1], r[l].vector[1]) *
                               calcI(nz[i], nz[j], nz[k], nz[l], t, alpha[i], alpha[j], alpha[k], alpha[l],
                 r[i].vector[2], r[j].vector[2], r[k].vector[2], r[l].vector[2]);
    }                                
    
    t = 0d;
    f1 = Math.exp(-X*t*t)*calcI(nx[i], nx[j], nx[k], nx[l], t, alpha[i], alpha[j], alpha[k], alpha[l],
                 r[i].vector[0], r[j].vector[0], r[k].vector[0], r[l].vector[0]) *
                               calcI(ny[i], ny[j], ny[k], ny[l], t, alpha[i], alpha[j], alpha[k], alpha[l],
                 r[i].vector[1], r[j].vector[1], r[k].vector[1], r[l].vector[1]) *
                               calcI(nz[i], nz[j], nz[k], nz[l], t, alpha[i], alpha[j], alpha[k], alpha[l],
                 r[i].vector[2], r[j].vector[2], r[k].vector[2], r[l].vector[2]);
                                     
    t = 1d;                          
    f2 = Math.exp(-X*t*t)*calcI(nx[i], nx[j], nx[k], nx[l], t, alpha[i], alpha[j], alpha[k], alpha[l],
                 r[i].vector[0], r[j].vector[0], r[k].vector[0], r[l].vector[0]) *
                               calcI(ny[i], ny[j], ny[k], ny[l], t, alpha[i], alpha[j], alpha[k], alpha[l],
                 r[i].vector[1], r[j].vector[1], r[k].vector[1], r[l].vector[1]) *
                               calcI(nz[i], nz[j], nz[k], nz[l], t, alpha[i], alpha[j], alpha[k], alpha[l],
                 r[i].vector[2], r[j].vector[2], r[k].vector[2], r[l].vector[2]);

    return C * (h/3)*(f1 + 4*sum1 + 2*sum2 + f2);
  }
}
