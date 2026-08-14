package org.openscience.cdk.stereo;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.List;

public class InorganicConfig2d {

  public static final double AXIS_THRESHOLD_DEG = 7.5;
  public static final double AXIS_THRESHOLD_DOT = 1-Math.cos(Math.toRadians(AXIS_THRESHOLD_DEG/2));

  private static final class Axis {
    IAtom beg, end;
    IBond.Display begDisp, endDisp;

    public Axis(IAtom beg, IAtom end, IBond.Display begDisp, IBond.Display endDisp) {
      this.beg = beg;
      this.end = end;
      this.begDisp = begDisp;
      this.endDisp = endDisp;
    }
  }

  enum Dir {N,S,E,W,NE,NW,SE,SW}

  private static Dir[] map = new Dir[]{Dir.N,
                                       Dir.NW, Dir.NW,
                                       Dir.W,
                                       Dir.SW, Dir.SW,
                                       Dir.S,
                                       Dir.SE, Dir.SE,
                                       Dir.E,
                                       Dir.NE,
                                       Dir.NE};

  static void swap(IBond[] bonds, int i, int j) {
    IBond tmp = bonds[i];
    bonds[i] = bonds[j];
    bonds[j] = tmp;
  }

  static public List<IStereoElement<?,?>> create(IAtomContainer mol) {
    List<IStereoElement<?,?>> elements = new ArrayList<>();
    for (IAtom atom : mol.atoms()) {
      if (isCandidate(atom)) {
        List<IBond> bonds = new ArrayList<>();
        for (IBond bond : atom.bonds()) {
          bonds.add(bond);
        }

        List<Vector2d> vectors = new ArrayList<>();
        for (IBond b : bonds) {
          IAtom nbor = b.getOther(atom);
          Vector2d v = new Vector2d(nbor.getPoint2d().x - atom.getPoint2d().x,
                                    nbor.getPoint2d().y - atom.getPoint2d().y);
          v.normalize();
          vectors.add(v);
        }

        List<Axis> axis = new ArrayList<>();
        int bsize = bonds.size();
        for (int i = 0; i < bsize; i++) {
          IBond b = bonds.get(i);
          Vector2d v = vectors.get(i);
          int best = -1;
          double deltaBest = 1;
          for (int j = i+1; j < bsize; j++) {
            double delta = Math.abs(-1 - v.dot(vectors.get(j)));
            if (delta < AXIS_THRESHOLD_DOT && delta < deltaBest) {
              best = j;
              deltaBest = delta;
            }
          }
          if (best >= 0) {
            axis.add(new Axis(bonds.get(i).getOther(atom),
                              bonds.get(best).getOther(atom),
                              IBond.Display.Solid,
                              IBond.Display.Solid));
          }
        }
      }
    }
    return elements;
  }

  private static Vector2d rotate(Tuple2d t, double theta) {
    double x = t.x * Math.cos(theta) - t.y * Math.sin(theta);
    double y = t.x * Math.sin(theta) + t.y * Math.cos(theta);
    return new Vector2d(x, y);
  }


  /**
   * An atom is a candidate for inorganic configuration if:
   * - Has 2D coordinate
   * - Is a metal
   * - The number of connected atoms/bond is >= 4
   * @param atom the central atom
   * @return the atom is a candidate
   */
  private static boolean isCandidate(IAtom atom) {
    return atom.getPoint2d() != null &&
           Elements.isMetal(atom) &&
           (atom.getImplicitHydrogenCount() + atom.getBondCount()) >= 4;
  }
}
