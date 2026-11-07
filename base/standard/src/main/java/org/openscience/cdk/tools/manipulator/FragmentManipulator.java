package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IFragmentAtom;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FragmentManipulator {

  private static void copy(IAtom dst, IAtom src) {
    dst.setAtomicNumber(src.getAtomicNumber());
    dst.setMassNumber(src.getMassNumber());
    dst.setImplicitHydrogenCount(src.getImplicitHydrogenCount());
    dst.setFormalCharge(src.getFormalCharge());
    dst.setHybridization(src.getHybridization());
    dst.setFormalNeighbourCount(src.getFormalNeighbourCount());
    dst.setValency(src.getValency());
    dst.setAtomTypeName(src.getAtomTypeName());
  }

  public static IFragmentAtom fromSgroup(IAtomContainer container,
                                         Sgroup sgroup) {
    if (sgroup.getType() != SgroupType.CtabAbbreviation)
      throw new IllegalArgumentException("Sgroup is not a super atom");
    if (sgroup.getBonds().size() > 1)
      throw new IllegalArgumentException("Currently only abbreviations <= 1 bond can be converted.");
    if (sgroup.getAtoms().isEmpty())
      throw new IllegalArgumentException("No atoms in Sgroup");

    IChemObjectBuilder builder = container.getBuilder();
    IAtomContainer frag = builder.newAtomContainer();
    Set<IAtom> atomset = sgroup.getAtoms();

    IFragmentAtom fragAtom = builder.newInstance(IFragmentAtom.class);
    fragAtom.setFragment(frag);
    fragAtom.setLabel(sgroup.getSubscript());

    IAtom anchor = null;
    // note replaceAtomByAtom will also remove the sgroup for us which we don't
    for (IBond bond : sgroup.getBonds()) {
      if (atomset.contains(bond.getBegin()))
        anchor = bond.getBegin();
      else if (atomset.contains(bond.getEnd()))
        anchor = bond.getEnd();
      else
        anchor = atomset.iterator().next();
    }

    if (anchor == null)
      throw new IllegalArgumentException();
    if (anchor instanceof IPseudoAtom)
      throw new IllegalArgumentException();

    copy(fragAtom, anchor);

    // also updates the sgroup
    container.setAtom(container.indexOf(anchor), fragAtom);

    for (IAtom atom : atomset) {
      frag.addAtom(atom);
    }
    for (IBond bond : container.bonds()) {
      if (atomset.contains(bond.getBegin()) && atomset.contains(bond.getEnd()))
        frag.addBond(bond);
    }

    // remove from the parent
    fragAtom.setExpanded(true);
    contract(container, fragAtom);

    // remove the sgroup
    List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
    if (sgroups != null) {
      sgroups = new ArrayList<>(sgroups);
      sgroups.remove(sgroup);
      container.setProperty(CDKConstants.CTAB_SGROUPS, sgroups);
    }

    return fragAtom;
  }

  public static boolean fromSgroups(IAtomContainer container) {
    boolean changed = false;
    List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
    if (sgroups == null)
      return false;
    for (Sgroup sgroup : new ArrayList<>(sgroups)) {
      if (sgroup.getType() == SgroupType.CtabAbbreviation &&
          sgroup.getBonds().size() <= 1) {
        fromSgroup(container, sgroup);
        changed = true;
      }
    }
    return changed;
  }


  public static Sgroup toSgroup(IAtomContainer container,
                                IFragmentAtom fragAtom) {
    expand(container, fragAtom);
    IAtom atom = container.getBuilder().newAtom();
    copy(atom, fragAtom);

    IAtomContainer fragment = fragAtom.getFragment();

    container.setAtom(container.indexOf(fragAtom), atom);
    fragment.setAtom(fragment.indexOf(fragAtom), atom);

    Sgroup sgroup = new Sgroup();
    sgroup.setType(SgroupType.CtabAbbreviation);
    sgroup.setSubscript(fragAtom.getLabel());
    for (IAtom a : fragment.atoms()) {
      sgroup.addAtom(a);
    }
    for (IBond b : container.getConnectedBondsList(atom)) {
      if (!fragment.contains(b))
        sgroup.addBond(b);
    }

    List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
    if (sgroups == null)
      sgroups = Collections.emptyList();
    sgroups = new ArrayList<>(sgroups);
    sgroups.add(sgroup);
    container.setProperty(CDKConstants.CTAB_SGROUPS, sgroups);

    return sgroup;
  }

  public static boolean toSgroups(IAtomContainer container) {
    List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
    if (sgroups == null)
      return false;
    // we need gather them all up first
    List<IFragmentAtom> fragAtoms = new ArrayList<>();
    for (IAtom atom : container.atoms()) {
      if (atom instanceof IFragmentAtom)
        fragAtoms.add((IFragmentAtom) atom);
    }
    for (IFragmentAtom fragAtom : fragAtoms)
      toSgroup(container, fragAtom);
    return !fragAtoms.isEmpty();
  }

  public static boolean expand(IAtomContainer container,
                               IFragmentAtom fragAtom) {
    if (fragAtom.isExpanded())
      return false;
    fragAtom.setExpanded(true);
    IAtomContainer frag = fragAtom.getFragment();
    for (IAtom a : frag.atoms()) {
      if (!a.equals(fragAtom))
        container.addAtom(a);
    }
    for (IBond b : frag.bonds()) {
      container.addBond(b);
    }
    if (frag.stereoElements().iterator().hasNext()) {
      for (IStereoElement<?, ?> se : frag.stereoElements()) {
        frag.addStereoElement(se);
      }
    }
    return true;
  }

  public static boolean contract(IAtomContainer container,
                                IFragmentAtom fragAtom) {
    if (!fragAtom.isExpanded())
      return false;
    fragAtom.setExpanded(false);
    IAtomContainer frag = fragAtom.getFragment();
    for (IBond b : frag.bonds()) {
      container.removeBond(b);
    }
    for (IAtom a : frag.atoms()) {
      if (!a.equals(fragAtom))
        container.removeAtom(a);
    }
    if (frag.stereoElements().iterator().hasNext()) {
      Set<IStereoElement> toRemove = new HashSet<>();
      for (IStereoElement<?, ?> se : frag.stereoElements()) {
        toRemove.add(se);
      }
      List<IStereoElement> updated = new ArrayList<>();
      for (IStereoElement<?, ?> se : container.stereoElements()) {
        if (!toRemove.contains(se))
          updated.add(se);
      }
      container.setStereoElements(updated);
    }
    return true;
  }

  public static void contractAll(IAtomContainer container) {
    for (IAtom atom : container.atoms()) {
      if (atom instanceof IFragmentAtom)
        contract(container, (IFragmentAtom)atom);
    }
  }

  public static void expandAll(IAtomContainer container) {
    for (IAtom atom : container.atoms()) {
      if (atom instanceof IFragmentAtom)
        expand(container, (IFragmentAtom)atom);
    }
  }
}
