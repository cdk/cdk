package org.openscience.cdk.modeling.builder3d;

import javax.vecmath.Point3d;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.smiles.SmilesParser;

import org.openscience.cdk.modeling.builder3d.AtomPlacer3D;

/**
 * This class tests the different functionalities
 * of the AtomPlacer3D class.
 * 
 * @category 3D model building
 * @cdk.module test-builder3d
 * @author danielszisz
 * @created 27/02/2012
 */


public class FurtherAtomPlacer3DTest extends CDKTestCase {
	
	
	@Test
	public void testAllHeavyAtomsPlaced_IAtomContainer() {
		AtomPlacer3D atmplacer=new AtomPlacer3D();
		List<IAtomContainer> molecules = new ArrayList<IAtomContainer>();
		molecules.add(MoleculeFactory.makeAlkane(5));
		molecules.add(MoleculeFactory.makeBenzene());
		molecules.add(MoleculeFactory.makePhenylAmine());
		molecules.add(MoleculeFactory.makeCyclobutadiene());
		for(IAtomContainer ac : molecules) {
			Assert.assertFalse(atmplacer.allHeavyAtomsPlaced(ac));
		}
		for(IAtomContainer ac : molecules) {
			for(IAtom atom : ac.atoms()) {
				atom.setFlag(CDKConstants.ISPLACED, true);
			}
			Assert.assertTrue(new AtomPlacer3D().allHeavyAtomsPlaced(ac));	
		}
	}
	
	@Test
	public void testNumberOfUnplacedHeavyAtoms_IAtomContainer() {
		IAtomContainer molecule = MoleculeFactory.makeAlkane(5);
		for(int i=0; i < 3; i++) {
			(molecule.getAtom(i)).setFlag(CDKConstants.ISPLACED, true);			
		}
		int placedAtoms = new AtomPlacer3D().numberOfUnplacedHeavyAtoms(molecule);
		Assert.assertEquals(2, placedAtoms);
	}
	
	@Test 
	public void testGetPlacedHeavyAtoms_IAtomContainer_IAtom() {
		AtomPlacer3D placer = new AtomPlacer3D();
		IAtomContainer molecule = MoleculeFactory.makeBenzene();
		for(int j=0; j < 3; j++) {
			(molecule.getAtom(j)).setFlag(CDKConstants.ISPLACED, true) ;		
	}
    IAtomContainer placed1 = placer.getPlacedHeavyAtoms(molecule, molecule.getAtom(1));
    IAtomContainer placed2 = placer.getPlacedHeavyAtoms(molecule, molecule.getAtom(2));
    IAtomContainer placed4 = placer.getPlacedHeavyAtoms(molecule, molecule.getAtom(4));

    IAtomContainer p1 = molecule.getBuilder().newInstance(IAtomContainer.class);
    p1.addAtom(molecule.getAtom(0));
    p1.addAtom(molecule.getAtom(2));
 
    IAtomContainer p2 = molecule.getBuilder().newInstance(IAtomContainer.class);
    p2.addAtom(molecule.getAtom(1));
    
    Assert.assertEquals(2, placed1.getAtomCount());
    Assert.assertEquals(1, placed2.getAtomCount());
    Assert.assertEquals(0, placed4.getAtomCount());
	
	}
	
	@Test
	public void testGetPlacedHeavyAtom_IAtomContainer_IAtom_IAtom() {
		AtomPlacer3D ap = new AtomPlacer3D();
		IAtomContainer m = MoleculeFactory.makeAlkane(7);
		for(int j =0; j< 5; j++) {
			m.getAtom(j).setFlag(CDKConstants.ISPLACED, true);
		}
		IAtom c = ap.getPlacedHeavyAtom(m, m.getAtom(1), m.getAtom(0));
		IAtom d = ap.getPlacedHeavyAtom(m, m.getAtom(2), m.getAtom(1));
		IAtom e = ap.getPlacedHeavyAtom(m, m.getAtom(0), m.getAtom(1));
		
		Assert.assertEquals(c, m.getAtom(2));
		Assert.assertEquals(d, m.getAtom(3));
		Assert.assertNull(e);
	}
	
	
	@Test
	public void testGetPlacedHeavyAtom_IAtomContainer_IAtom() {
		AtomPlacer3D ap = new AtomPlacer3D();
		IAtomContainer m = MoleculeFactory.makeCyclohexane();
//		for(IAtom a : m.atoms()) a.setFlag(CDKConstants.ISPLACED, true);
	    for(int i=0; i< 3; i++) m.getAtom(i).setFlag(CDKConstants.ISPLACED, true);
		
	    IAtom a = ap.getPlacedHeavyAtom(m, m.getAtom(0));
	    Assert.assertEquals(a, m.getAtom(1));
	    IAtom b = ap.getPlacedHeavyAtom(m, m.getAtom(2));
	    Assert.assertEquals(b, m.getAtom(1));
	    IAtom c = ap.getPlacedHeavyAtom(m, m.getAtom(4));
	    Assert.assertNull(c);
	    
	}
	
	@Test
	public void testGeometricCenterAllPlacedAtoms_IAtomContainer() {
		AtomPlacer3D ap = new AtomPlacer3D();
		IAtomContainer m = MoleculeFactory.makeAlkane(2);
		for(IAtom a : m.atoms()) {
		 a.setFlag(CDKConstants.ISPLACED, true); 	
		}
		m.getAtom(0).setPoint3d(new Point3d(-1.0, 0.0, 0.0));
		m.getAtom(1).setPoint3d(new Point3d(1.0, 0.0, 0.0));
		
		Point3d c = ap.geometricCenterAllPlacedAtoms(m);
		Assert.assertEquals(0.0, c.x, 0.01);
		Assert.assertEquals(0.0, c.y, 0.01);
		Assert.assertEquals(0.0, c.z, 0.01);
		
	}
	
	@Test
	public void testGetUnplacedRingHeavyAtom_IAtomContainer_IAtom() {
		AtomPlacer3D ap = new AtomPlacer3D();
		IAtomContainer m = MoleculeFactory.makeCyclopentane();
		
		for(IAtom a : m.atoms()) a.setFlag(CDKConstants.ISINRING, true);
		for(int j=0; j < 2; j++) {
			m.getAtom(j).setFlag(CDKConstants.ISPLACED, true);
		}
		IAtom a1 = m.getAtom(0);
		IAtom a2 = m.getAtom(1);
		IAtom n = m.getAtom(4);
		
		IAtom b1 = ap.getUnplacedRingHeavyAtom(m, a1);
		IAtom b2 = ap.getUnplacedRingHeavyAtom(m, a2);
		IAtom nn = ap.getUnplacedRingHeavyAtom(m, n);
		
		Assert.assertEquals(b1, m.getAtom(4));
		Assert.assertEquals(b2, m.getAtom(2));
		
		Assert.assertEquals(m.getAtom(0).getFlag(CDKConstants.ISPLACED), true);
		for(IBond b : m.bonds()) {
			if(b.getConnectedAtom(m.getAtom(4)) != null && 
					!b.getConnectedAtom(m.getAtom(4)).getFlag(CDKConstants.ISPLACED)){
				
		  nn = b.getConnectedAtom(m.getAtom(4));		
				}
		}
		Assert.assertEquals(nn, m.getAtom(3));
	}
	
	@Test
	public void testGetFarthestAtom_Point3d_IAtomContainer() {
		AtomPlacer3D ap = new AtomPlacer3D();
		IAtomContainer m = MoleculeFactory.makeBenzene();
		
		m.getAtom(0).setPoint3d(new Point3d(0.0, 0.0, 0.0));
		m.getAtom(1).setPoint3d(new Point3d(1.0, 1.0, 1.0));
		m.getAtom(4).setPoint3d(new Point3d(3.0, 2.0, 1.0));
		m.getAtom(5).setPoint3d(new Point3d(4.0, 4.0, 4.0));
		
		IAtom a = ap.getFarthestAtom(m.getAtom(0).getPoint3d(), m);
		IAtom b = ap.getFarthestAtom(m.getAtom(4).getPoint3d(), m);
		
		Assert.assertEquals(m.getAtom(5), a);
		Assert.assertEquals(m.getAtom(0), b);
		
	}
	
	@Test
	public void testGetNextPlacedHeavyAtomWithUnplacedRingNeighbour_IAtomContainer() {
		AtomPlacer3D ap = new AtomPlacer3D();
		IAtomContainer m1 = MoleculeFactory.makeAlkane(3);
		IAtomContainer m2 = MoleculeFactory.makeCyclopentane();
		
		//MoleculeFactory does not set ISINRING flags for cyclic molecules 
		Assert.assertEquals(false, m2.getAtom(0).getFlag(CDKConstants.ISINRING));
		for(IAtom a : m2.atoms()) a.setFlag(CDKConstants.ISINRING, true);
		
		//acyclic molecule so null is expected		
		for(IAtom a : m1.atoms()) a.setFlag(CDKConstants.ISPLACED, true);
		Assert.assertNull(ap.getNextPlacedHeavyAtomWithUnplacedRingNeighbour(m1));
	
		//this method loops on the edges this is why it does not find 0 as the
		//next atom which is placed and has an unplaced ring atom neighbour, but
		//atom number 2
		for(int j=0; j < 3; j++) m2.getAtom(j).setFlag(CDKConstants.ISPLACED, true);
		Assert.assertEquals(m2.getAtom(2), ap.getNextPlacedHeavyAtomWithUnplacedRingNeighbour(m2));
				
		
	}
	
	@Test
	public void testGetNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour_IAtomContainer() {
		AtomPlacer3D ap = new AtomPlacer3D();
		IAtomContainer m1 = MoleculeFactory.makeBenzene();
		IAtomContainer m2 = MoleculeFactory.makeAlkane(5);
		
		for(IAtom a : m1.atoms()) a.setFlag(CDKConstants.ISINRING, true);
		for(IAtom a : m2.atoms() ) a.setFlag(CDKConstants.ISALIPHATIC, true);		
		
		for(int j=0; j< 3; j++) m1.getAtom(j).setFlag(CDKConstants.ISPLACED, true);
		IAtom searchedatom1 = ap.getNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour(m1);
		Assert.assertNull(searchedatom1);
		
		for(IAtom a : m1.atoms()) {
			if(!a.getFlag(CDKConstants.ISPLACED)) a.setFlag(CDKConstants.ISPLACED, true);
		}
		IAtom searchedatom2 = ap.getNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour(m1);
		Assert.assertNull(searchedatom2);
		
		for(int k=0; k < 3; k++) m2.getAtom(k).setFlag(CDKConstants.ISPLACED, true);
		IAtom a = ap.getNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour(m2);
		Assert.assertEquals(m2.getAtom(2), a);
		
		
	}
	
	@Test
	public void testGetNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour_IAtomContainer() {
		AtomPlacer3D ap = new AtomPlacer3D();
		IAtomContainer m1 = MoleculeFactory.makeCyclobutane();
		IAtomContainer m2 = MoleculeFactory.makeAlkane(6);
		
		for(IAtom a : m1.atoms()) a.setFlag(CDKConstants.ISINRING, true);
		for(IAtom a : m2.atoms()) a.setFlag(CDKConstants.ISALIPHATIC, true);
				
	    for(int j=0; j < 3; j++) m1.getAtom(j).setFlag(CDKConstants.ISPLACED, true);
	    IAtom s1 = ap.getNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour(m1);
	    Assert.assertNull(s1);
	    
	    for(IAtom a : m1.atoms()) {
			if(!a.getFlag(CDKConstants.ISPLACED)) a.setFlag(CDKConstants.ISPLACED, true);
		}
		IAtom s2 = ap.getNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour(m1);
		Assert.assertNull(s2);
		
		for(int k=0; k < 3; k++) m2.getAtom(k).setFlag(CDKConstants.ISPLACED, true);
		IAtom a = ap.getNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour(m2);
		Assert.assertEquals(m2.getAtom(3), a);
	    
		for(IAtom atom : m2.atoms()) atom.setFlag(CDKConstants.ISPLACED, true);
	    IAtom s3 = ap.getNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour(m2);
		Assert.assertNull(s3);
	}
	
	/**
	 * @cdk.bug ???? : bad angle value
	 */
	@Test
	public void testGetAngleValue_String_String_String() throws Exception {
	MMFF94BasedParameterSetReader mmff94bpsr = new MMFF94BasedParameterSetReader();
	Map<String, Object> ffmap = new HashMap<String, Object>();
	AtomPlacer3D ap = new AtomPlacer3D();
	mmff94bpsr.readParameterSets();
	ffmap = mmff94bpsr.getParamterSet();
	ap.initilize(ffmap);
	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
	String smiles = "CCCCCC";
	IAtomContainer molecule = sp.parseSmiles(smiles);
	Assert.assertNotNull(molecule);
	String id1 = molecule.getAtom(1).getAtomTypeName();
	String id2 = molecule.getAtom(2).getAtomTypeName();
	String id3 = molecule.getAtom(3).getAtomTypeName();
	
	double anglev = ap.getAngleValue(id1, id2, id3);
	Assert.assertNotNull(anglev);
	System.err.println(anglev);
	
	}
	
	/**
	 * @cdk.bug ????? bad bond length value
	 */
	@Test
	public void testGetBondLengthValue_String_String() throws Exception {
		MMFF94BasedParameterSetReader mmff94bpsr = new MMFF94BasedParameterSetReader();
		Map<String, Object> ffmap = new HashMap<String, Object>();
		AtomPlacer3D ap = new AtomPlacer3D();
		mmff94bpsr.readParameterSets();
		ffmap = mmff94bpsr.getParamterSet();
		ap.initilize(ffmap);
	    SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		String smiles = "CCCCCC";
		IAtomContainer molecule = sp.parseSmiles(smiles);
		Assert.assertNotNull(molecule);
		
		String id1 = molecule.getAtom(1).getAtomTypeName();
		String id2 = molecule.getAtom(2).getAtomTypeName();
		String mmff94id1 = "C";
		String mmff94id2 = "C";
		Assert.assertNotSame(mmff94id1, id1);
		Assert.assertNotSame(mmff94id2, id2);
		
		double bondlength = ap.getBondLengthValue(id1, id2);
		System.err.println(bondlength);
		Assert.assertNotNull(bondlength);
	}
	
	@Test 
	public void testMarkPlaced_IAtomContainer()  {
		AtomPlacer3D ap = new AtomPlacer3D();
		IAtomContainer m = MoleculeFactory.makeAlkane(5);
		IAtomContainer pm = ap.markPlaced(m);
		for(IAtom a : pm.atoms()) Assert.assertTrue(a.getFlag(CDKConstants.ISPLACED));		
	}
	
	@Test
	public void testZmatrixChainToCartesian_IAtomContainer_boolean() throws CDKException {
		AtomPlacer3D ap = new AtomPlacer3D();
	    ForceFieldConfigurator ffc= new ForceFieldConfigurator();
	    ffc.setForceFieldConfigurator("mmff92");
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		String s1 = "CCCCCCCCCCCCCCCCCC";
		String s2 = "CCCCCC(CCCC)CCCC";
		IAtomContainer m1 = sp.parseSmiles(s1);
		IAtomContainer m2 = sp.parseSmiles(s2);
		ffc.assignAtomTyps(m1);
		ffc.assignAtomTyps(m2);
		ap.placeAliphaticHeavyChain(m1, m1);
		ap.placeAliphaticHeavyChain(m2, m2);
		ap.zmatrixChainToCartesian(m1, false);
		ap.zmatrixChainToCartesian(m2, true);
	    ModelBuilder3DTest.checkAverageBondLength(m1);
	    ModelBuilder3DTest.checkAverageBondLength(m2);
	}
	
	
	}
