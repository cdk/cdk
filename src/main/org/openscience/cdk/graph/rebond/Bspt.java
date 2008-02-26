/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2003-2007  Miguel Howard <miguel@jmol.org>
 *
 * Contact: cdk-devel@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.graph.rebond;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.util.Enumeration;

/**
 *  BSP-Tree stands for Binary Space Partitioning Tree.
 *  The tree partitions n-dimensional space (in our case 3) into little
 *  boxes, facilitating searches for things which are *nearby*.
 *  For some useful background info, search the web for "bsp tree faq".
 *  Our application is somewhat simpler because we are storing points instead
 *  of polygons.
 *
 * <p>We are working with three dimensions. For the purposes of the Bspt code
 *  these dimensions are stored as 0, 1, or 2. Each node of the tree splits
 *  along the next dimension, wrapping around to 0.
 * <pre>
 *    mySplitDimension = (parentSplitDimension + 1) % 3;
 * </pre>
 *  A split value is stored in the node. Values which are <= splitValue are
 *  stored down the left branch. Values which are >= splitValue are stored
 *  down the right branch. When this happens, the search must proceed down
 *  both branches.
 *  Planar and crystaline substructures can generate values which are == along
 *  one dimension.
 *
 * <p>To get a good picture in your head, first think about it in one dimension,
 *  points on a number line. The tree just partitions the points.
 *  Now think about 2 dimensions. The first node of the tree splits the plane
 *  into two rectangles along the x dimension. The second level of the tree
 *  splits the subplanes (independently) along the y dimension into smaller
 *  rectangles. The third level splits along the x dimension.
 *  In three dimensions, we are doing the same thing, only working with
 *  3-d boxes.
 *
 * <p>Three enumerators are provided
 * <ul>
 *    <li>enumNear(Bspt.Tuple center, double distance)<br>
 *      returns all the points contained in of all the boxes which are within
 *      distance from the center.
 *    <li>enumSphere(Bspt.Tuple center, double distance)<br>
 *      returns all the points which are contained within the sphere (inclusive)
 *      defined by center + distance
 *    <li>enumHemiSphere(Bspt.Tuple center, double distance)<br>
 *      same as sphere, but only the points which are greater along the
 *      x dimension
 * </ul>
 *
 * @author  Miguel Howard
 * @cdk.created 2003-05
 *
 * @cdk.module  standard
 * @cdk.svnrev  $Revision$
 * @cdk.keyword rebonding
 * @cdk.keyword Binary Space Partitioning Tree
 * @cdk.keyword join-the-dots
 */
@TestClass("org.openscience.cdk.graph.rebond.BsptTest")
public final class Bspt {

  private final static int leafCount = 4;
  private final static int stackDepth = 64; /* this corresponds to the max height of the tree */
  int dimMax;
  Element eleRoot;

  /*
  static double distance(int dim, Tuple t1, Tuple t2) {
    return Math.sqrt(distance2(dim, t1, t2));
  }

  static double distance2(int dim, Tuple t1, Tuple t2) {
    double distance2 = 0.0;
    while (--dim >= 0) {
      double distT = t1.getDimValue(dim) - t2.getDimValue(dim);
      distance2 += distT*distT;
    }
    return distance2;
  }
  */

  public Bspt(int dimMax) {
    this.dimMax = dimMax;
    this.eleRoot = new Leaf();
  }

  public  void addTuple(Tuple tuple) {
    if (! eleRoot.addTuple(tuple)) {
      eleRoot = new Node(0, dimMax, (Leaf) eleRoot);
      if (! eleRoot.addTuple(tuple))
        throw new Error("Bspt.addTuple() failed");
    }
  }

    @TestMethod("testToString")
  public String toString() {
    return eleRoot.toString();
  }

  protected  void dump() {
    eleRoot.dump(0);
  }

  protected  Enumeration enumeration() {
    return new EnumerateAll();
  }

  class EnumerateAll implements Enumeration {
    Node[] stack;
    int sp;
    int i;
    Leaf leaf;

    EnumerateAll() {
      stack = new Node[stackDepth];
      sp = 0;
      Element ele = eleRoot;
      while (ele instanceof Node) {
        Node node = (Node) ele;
        if (sp == stackDepth)
        	throw new Error("Bspt.EnumerateAll tree stack overflow");
        stack[sp++] = node;
        ele = node.eleLE;
      }
      leaf = (Leaf)ele;
      i = 0;
    }

    public boolean hasMoreElements() {
      return (i < leaf.count) || (sp > 0);
    }

    public Object nextElement() {
      if (i == leaf.count) {
        //        logger.debug("-->" + stack[sp-1].splitValue);
        Element ele = stack[--sp].eleGE;
        while (ele instanceof Node) {
          Node node = (Node) ele;
          stack[sp++] = node;
          ele = node.eleLE;
        }
        leaf = (Leaf)ele;
        i = 0;
      }
      return leaf.tuples[i++];
    }
  }

  protected  Enumeration enumNear(Tuple center, double distance) {
    return new EnumerateNear(center, distance);
  }

  class EnumerateNear implements Enumeration {
    Node[] stack;
    int sp;
    int i;
    Leaf leaf;
    double distance;
    Tuple center;

    EnumerateNear(Tuple center, double distance) {
      this.distance = distance;
      this.center = center;

      stack = new Node[stackDepth];
      sp = 0;
      Element ele = eleRoot;
      while (ele instanceof Node) {
        Node node = (Node) ele;
        if (center.getDimValue(node.dim) - distance <= node.splitValue) {
          if (sp == stackDepth)
        	  throw new Error("Bspt.EnumerateNear tree stack overflow");
          stack[sp++] = node;
          ele = node.eleLE;
        } else {
          ele = node.eleGE;
        }
      }
      leaf = (Leaf)ele;
      i = 0;
    }

    public boolean hasMoreElements() {
      if (i < leaf.count)
        return true;
      if (sp == 0)
        return false;
      Element ele = stack[--sp];
      while (ele instanceof Node) {
        Node node = (Node) ele;
        if (center.getDimValue(node.dim) + distance < node.splitValue) {
          if (sp == 0)
            return false;
          ele = stack[--sp];
        } else {
          ele = node.eleGE;
          while (ele instanceof Node) {
            Node nodeLeft = (Node) ele;
            stack[sp++] = nodeLeft;
            ele = nodeLeft.eleLE;
          }
        }
      }
      leaf = (Leaf)ele;
      i = 0;
      return true;
    }

    public Object nextElement() {
      return leaf.tuples[i++];
    }
  }

  protected  EnumerateSphere enumSphere(Tuple center, double distance) {
    return new EnumerateSphere(center, distance, false);
  }

  protected  EnumerateSphere enumHemiSphere(Tuple center, double distance) {
    return new EnumerateSphere(center, distance, true);
  }

  class EnumerateSphere implements Enumeration {
    Node[] stack;
    int sp;
    int i;
    Leaf leaf;
    double distance;
    double distance2;
    Tuple center;
    double centerValues[];
    double foundDistance2; // the dist squared of a found Element;

    // when set, only the hemisphere sphere .GT. or .EQ. the point
    // (on the first dim) is returned
    boolean tHemisphere;

    EnumerateSphere(Tuple center, double distance, boolean tHemisphere) {
      this.distance = distance;
      this.distance2 = distance*distance;
      this.center = center;
      this.tHemisphere = tHemisphere;
      centerValues = new double[dimMax];
      for (int dim = dimMax; --dim >= 0; )
        centerValues[dim] = center.getDimValue(dim);
      stack = new Node[stackDepth];
      sp = 0;
      Element ele = eleRoot;
      while (ele instanceof Node) {
        Node node = (Node) ele;
        if (center.getDimValue(node.dim) - distance <= node.splitValue) {
          if (sp == stackDepth)
        	  throw new Error("Bspt.EnumerateSphere tree stack overflow");
          stack[sp++] = node;
          ele = node.eleLE;
        } else {
          ele = node.eleGE;
        }
      }
      leaf = (Leaf)ele;
      i = 0;
    }

    private boolean isWithin(Tuple t) {
      double dist2;
      double distT;
      distT = t.getDimValue(0) - centerValues[0];
      if (tHemisphere && distT < 0) {
        return false;
      }
      dist2 = distT * distT;
      if (dist2 > distance2) {
        return false;
      }
      for (int dim = dimMax; --dim > 0; ) {
        distT = t.getDimValue(dim) - centerValues[dim];
        dist2 += distT*distT;
        if (dist2 > distance2) {
          return false;
        }
      }
      this.foundDistance2 = dist2;
      return true;
    }

    public boolean hasMoreElements() {
      while (true) {
        for ( ; i < leaf.count; ++i)
          if (isWithin(leaf.tuples[i]))
            return true;
        if (sp == 0)
          return false;
        Element ele = stack[--sp];
        while (ele instanceof Node) {
          Node node = (Node) ele;
          if (center.getDimValue(node.dim) + distance < node.splitValue) {
            if (sp == 0)
              return false;
            ele = stack[--sp];
          } else {
            ele = node.eleGE;
            while (ele instanceof Node) {
              Node nodeLeft = (Node) ele;
              stack[sp++] = nodeLeft;
              ele = nodeLeft.eleLE;
            }
          }
        }
        leaf = (Leaf)ele;
        i = 0;
      }
    }

    public Object nextElement() {
      return leaf.tuples[i++];
    }

    public double foundDistance2() {
      return foundDistance2;
    }
  }

  public interface Tuple {
	 public double getDimValue(int dim);
  }

  interface Element {
    boolean addTuple(Tuple tuple);
    void dump(int level);
    boolean isLeafWithSpace();
  }

  class Node implements Element {
    Element eleLE;
    int dim;
    int dimMax;
    double splitValue;
    Element eleGE;

    Node(int dim, int dimMax, Leaf leafLE) {
      this.eleLE = leafLE;
      this.dim = dim;
      this.dimMax = dimMax;
      this.splitValue = leafLE.getSplitValue(dim);
      this.eleGE = new Leaf(leafLE, dim, splitValue);
    }

    public boolean addTuple(Tuple tuple) {
      if (tuple.getDimValue(dim) < splitValue) {
        if (eleLE.addTuple(tuple))
          return true;
        eleLE = new Node((dim + 1) % dimMax, dimMax, (Leaf)eleLE);
        return eleLE.addTuple(tuple);
      }
      if (tuple.getDimValue(dim) > splitValue) {
        if (eleGE.addTuple(tuple))
          return true;
        eleGE = new Node((dim + 1) % dimMax, dimMax, (Leaf)eleGE);
        return eleGE.addTuple(tuple);
      }
      if (eleLE.isLeafWithSpace())
        eleLE.addTuple(tuple);
      else if (eleGE.isLeafWithSpace())
        eleGE.addTuple(tuple);
      else if (eleLE instanceof Node)
        eleLE.addTuple(tuple);
      else if (eleGE instanceof Node)
        eleGE.addTuple(tuple);
      else {
        eleLE = new Node((dim + 1) % dimMax, dimMax, (Leaf)eleLE);
        return eleLE.addTuple(tuple);
      }
      return true;
    }

    public String toString() {
      return eleLE.toString() + dim + ":" + splitValue + "\n" + eleGE.toString();
    }

    public void dump(int level) {
      System.out.println("");
      eleLE.dump(level + 1);
      for (int i = 0; i < level; ++i)
        System.out.print("-");
      System.out.println(">" + splitValue);
      eleGE.dump(level + 1);
    }

    public boolean isLeafWithSpace() {
      return false;
    }
  }

  class Leaf implements Element {
    int count;
    Tuple[] tuples;

    Leaf() {
      count = 0;
      tuples = new Tuple[leafCount];
    }

    Leaf(Leaf leaf, int dim, double splitValue) {
      this();
      for (int i = leafCount; --i >= 0; ) {
        Tuple tuple = leaf.tuples[i];
        double value = tuple.getDimValue(dim);
        if (value > splitValue ||
            (value == splitValue && ((i & 1) == 1))) {
          leaf.tuples[i] = null;
          tuples[count++] = tuple;
        }
      }
      int dest = 0;
      for (int src = 0; src < leafCount; ++src)
        if (leaf.tuples[src] != null)
          leaf.tuples[dest++] = leaf.tuples[src];
      leaf.count = dest;
      if (count == 0)
        tuples[leafCount] = null; // explode
    }

    public double getSplitValue(int dim) {
      if (count != leafCount)
        tuples[leafCount] = null;
      return (tuples[0].getDimValue(dim) + tuples[leafCount - 1].getDimValue(dim)) / 2;
    }

    public String toString() {
      return "leaf:" + count + "\n";
    }

    public boolean addTuple(Tuple tuple) {
      if (count == leafCount)
        return false;
      tuples[count++] = tuple;
      return true;
    }

    public void dump(int level) {
      for (int i = 0; i < count; ++i) {
        Tuple t = tuples[i];
        for (int j = 0; j < level; ++j)
          System.out.print(".");
        for (int dim = 0; dim < dimMax-1; ++dim)
          System.out.print("" + t.getDimValue(dim) + ",");
        System.out.println("" + t.getDimValue(dimMax - 1));
      }
    }

    public boolean isLeafWithSpace() {
      return count < leafCount;
    }
  }
}

