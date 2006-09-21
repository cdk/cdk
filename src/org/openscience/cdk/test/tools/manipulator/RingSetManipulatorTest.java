package org.openscience.cdk.test.tools.manipulator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * @cdk.module test-standard
 */
public class RingSetManipulatorTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
	public RingSetManipulatorTest(String name) {
        super(name);
    }
	
	public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }
	
	public static Test suite() {
		return new TestSuite(RingSetManipulatorTest.class);
	}
	
    public void testIsSameRing_IAtom_IAtom() {
    	DefaultChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRingSet ringset = builder.newRingSet();

        IAtom ring1Atom1 = builder.newAtom("C"); // rather artificial molecule
        IAtom ring1Atom2 = builder.newAtom("C");
        IAtom ring1Atom3 = builder.newAtom("C");
        IAtom ring2Atom1 = builder.newAtom("C");
        IAtom ring2Atom2 = builder.newAtom("C");
        IAtom ring2Atom3 = builder.newAtom("C");
        IBond ring1Bond1 = builder.newBond(ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = builder.newBond(ring1Atom2, ring1Atom3);
        IBond ring1Bond3 = builder.newBond(ring1Atom3, ring1Atom1);
        
        IBond ring2Bond1 = builder.newBond(ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = builder.newBond(ring2Atom2, ring2Atom3);
        IBond ring2Bond3 = builder.newBond(ring2Atom3, ring2Atom1);

        IRing ring1 = builder.newRing();
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(ring1Atom3);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);

        IRing ring2 = builder.newRing();
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(ring2Atom3);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        
        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);
        
        assertTrue(RingSetManipulator.isSameRing(ringset, ring1Atom1, ring1Atom3));
        assertFalse(RingSetManipulator.isSameRing(ringset, ring1Atom1, ring2Atom3));
    }

    public void testRingAlreadyInSet_IRing() {
        IRing r1 = builder.newRing(5, "C");
        IRing r2 = builder.newRing(3, "C");
        
        IRingSet rs = builder.newRingSet();
        assertFalse(RingSetManipulator.ringAlreadyInSet(r1, rs));
        assertFalse(RingSetManipulator.ringAlreadyInSet(r2, rs));
        
        rs.addAtomContainer(r1);
        assertTrue(RingSetManipulator.ringAlreadyInSet(r1, rs));
        assertFalse(RingSetManipulator.ringAlreadyInSet(r2, rs));
        
        rs.addAtomContainer(r2);
        assertTrue(RingSetManipulator.ringAlreadyInSet(r1, rs));
        assertTrue(RingSetManipulator.ringAlreadyInSet(r2, rs));
    }
    
	
	
}
