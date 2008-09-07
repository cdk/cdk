/* $Revision$ $Author$ $Date$    
 *
 * Copyright (C) 2001-2007  Nina Jeliazkova
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.graph;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;

/**
 * Spanning tree of a molecule.
 * Used to discover the number of cyclic bonds in order to prevent the 
 * inefficient AllRingsFinder to run for too long.
 *
 * @author      Nina Jeliazkova
 * @cdk.module  core
 * @cdk.svnrev  $Revision$
 * @cdk.dictref blue-obelisk:graphSpanningTree
 * @cdk.keyword spanning tree
 * @cdk.keyword ring finding
 * @cdk.bug     1817487
 */
@TestClass("org.openscience.cdk.graph.SpanningTreeTest")
public class SpanningTree {
	
	private final static String ATOM_NUMBER = "ST_ATOMNO";
	
	private int[] parent = null;
	private int[][] cb = null; // what is cb??? cyclic bonds?
	
	protected boolean[] bondsInTree;
	
	private int sptSize = 0;
	private int edrSize = 0;

	private int bondsAcyclicCount = 0,bondsCyclicCount = 0;
	
	private IAtomContainer molecule = null;
	private int totalEdgeCount=0, totalVertexCount=0;
	private boolean disconnected;
	private boolean identifiedBonds;

    @TestMethod("testIsDisconnected")
    public boolean isDisconnected() {
		return disconnected;
	}

    @TestMethod("testSpanningTree_IAtomContainer")
    public SpanningTree(IAtomContainer atomContainer) {
		identifiedBonds = false;
		buildSpanningTree(atomContainer);
	}

    private boolean fastfind(int v1,int v2, boolean union) {
		int i = v1;	while (parent[i] > 0) i = parent[i];
		int j = v2;	while (parent[j] > 0) j = parent[j];
		int t ;
		while (parent[v1] > 0) {
			t = v1; v1 = parent[v1]; parent[t] = i;
		}
		while (parent[v2] > 0) {
			t = v2; v2 = parent[v2]; parent[t] = j;
		}		
		if (union && (i!=j)) {
			if (parent[j] < parent[i]) {
				parent[j] = parent[j] + parent[i]-1;
				parent[i] = j; 
			} else {
				parent[i] = parent[i] + parent[j]-1;
				parent[j] = i;
			}
		}
		return (i != j);
	}
	
	private void fastFindInit(int V) {
		parent = new int[V+1];
		for (int i = 1; i <= V; i++) {
			parent[i] = 0;
		}
	}
	
	/*
	 * Kruskal algorithm
	 */
	private void buildSpanningTree(IAtomContainer atomContainer){
		disconnected = false;
		molecule = atomContainer;
		
		totalVertexCount = atomContainer.getAtomCount();
		totalEdgeCount = atomContainer.getBondCount();
		
		sptSize = 0;edrSize = 0;
		fastFindInit(totalVertexCount);
		for (int i = 0; i < totalVertexCount; i++) {
			(atomContainer.getAtom(i)).setProperty(ATOM_NUMBER, Integer.toString(i+1));
		}
		IBond bond;
		int v1,v2;
		bondsInTree = new boolean[totalEdgeCount];
		
		for (int b=0; b < totalEdgeCount; b++ ) {
			bondsInTree[b] = false;			
			bond = atomContainer.getBond(b);
			v1 = Integer.parseInt((bond.getAtom(0)).getProperty(ATOM_NUMBER).toString());
			v2 = Integer.parseInt((bond.getAtom(1)).getProperty(ATOM_NUMBER).toString());
			//this below is a little bit  slower
			//v1 = atomContainer.getAtomNumber(bond.getAtomAt(0))+1; 
			//v2 = atomContainer.getAtomNumber(bond.getAtomAt(1))+1;
			if (fastfind(v1,v2,true)) {
				bondsInTree[b] = true;
				sptSize++;
				//logger.debug("ST : includes bond between atoms "+v1+","+v2);
			}
			if (sptSize>=(totalVertexCount-1)) break;
			
		}
		// if atomcontainer is connected then the number of bonds in the spanning tree = (No atoms-1)
		//i.e.  edgesRings = new Bond[E-V+1];
		//but to hold all bonds if atomContainer was disconnected then  edgesRings = new Bond[E-sptSize]; 
		if (sptSize != (totalVertexCount-1)) disconnected = true;
		for (int b=0; b < totalEdgeCount; b++ ) if (!bondsInTree[b]){
//			edgesRings[edrSize] = atomContainer.getBondAt(b);
			edrSize++;
		}
		cb = new int[edrSize][totalEdgeCount];
		for (int i = 0; i < edrSize; i++) 
			for (int a = 0; a < totalEdgeCount; a++) 
				cb[i][a] = 0;
		
		// remove ATOM_NUMBER props again
		for (IAtom atom : atomContainer.atoms()) atom.removeProperty(ATOM_NUMBER);
	}

    @TestMethod("testGetSpanningTree")
    public IAtomContainer getSpanningTree() {
		IAtomContainer ac = molecule.getBuilder().newAtomContainer();
		for (int a=0 ; a < totalVertexCount; a++) ac.addAtom(molecule.getAtom(a));
		for (int b=0; b < totalEdgeCount; b++ ) if (bondsInTree[b])
			ac.addBond(molecule.getBond(b));
		return ac;
	}

    @TestMethod("testGetPath_IAtomContainer_IAtom_IAtom")
    public IAtomContainer getPath(IAtomContainer spt,IAtom a1, IAtom a2) throws NoSuchAtomException {
		IAtomContainer path = spt.getBuilder().newAtomContainer();
		PathTools.resetFlags(spt);
		path.addAtom(a1);
		PathTools.depthFirstTargetSearch(spt,a1,a2,path);
		if (path.getAtomCount() == 1) path.removeAtom(a1); // no path found: remove initial atom
		return path;
	}

	private IRing getRing(IAtomContainer spt, IBond bond) {
		IRing ring = spt.getBuilder().newRing();
		PathTools.resetFlags(spt);
		ring.addAtom(bond.getAtom(0));		
		PathTools.depthFirstTargetSearch(spt,bond.getAtom(0),bond.getAtom(1),ring);		
		ring.addBond(bond);
		return ring;
	}
	
	private void getBondsInRing(IAtomContainer mol, IRing ring, int[] bonds) {
		for (int i=0; i < ring.getBondCount(); i++ ) {
			int m = mol.getBondNumber(ring.getBond(i));
			bonds[m] = 1;
		}
	}

    @TestMethod("testGetBasicRings")
    public IRingSet getBasicRings() throws NoSuchAtomException {
		IRingSet ringset = molecule.getBuilder().newRingSet();
		IAtomContainer spt = getSpanningTree();
		for (int i = 0; i < totalEdgeCount; i++) if (!bondsInTree[i])  
			ringset.addAtomContainer(getRing(spt,molecule.getBond(i)));
		return ringset;
	}

	/**
	 * Returns an IAtomContainer which contains all the atoms and bonds which
	 * are involved in ring systems.
	 * 
	 * @see #getAllRings()
	 * @see #getBasicRings()
     * @return the IAtomContainer as described above
	 */
    @TestMethod("testGetCyclicFragmentsContainer")
    public IAtomContainer getCyclicFragmentsContainer()  {
        IAtomContainer fragContainer = this.molecule.getBuilder().newAtomContainer();
        IAtomContainer spt = getSpanningTree();

        for (int i = 0; i < totalEdgeCount; i++)
            if (!bondsInTree[i]) {
                IRing ring = getRing(spt, molecule.getBond(i));
                for (int b = 0; b < ring.getBondCount(); b++) {
                    IBond ringBond = ring.getBond(b);
                    if (!fragContainer.contains(ringBond)) {
                        fragContainer.addBond(ringBond);
                        for (int atomCount = 0; atomCount < ringBond.getAtomCount(); atomCount++) {
                            IAtom atom = ringBond.getAtom(atomCount);
                            if (!fragContainer.contains(atom)) {
                                atom.setFlag(CDKConstants.ISINRING, true);
                                fragContainer.addAtom(atom);
                            }
                        }
                    }
                }
            }
        return fragContainer;
    }

    /**
	 * Identifies whether bonds are cyclic or not. It is used by several other methods.
	 */
	private void identifyBonds() {
		IAtomContainer spt = getSpanningTree();
		IRing ring;
		int nBasicRings = 0;
		for (int i = 0; i < totalEdgeCount; i++) {
			if (!bondsInTree[i]) {  
				ring = getRing(spt,molecule.getBond(i));
				for (int b=0; b < ring.getBondCount(); b++ ) {
					int m = molecule.getBondNumber(ring.getBond(b));
					cb[nBasicRings][m] = 1;
				}
				nBasicRings++;
			}
		}
		bondsAcyclicCount = 0; bondsCyclicCount = 0;
		for (int i = 0; i < totalEdgeCount; i++) {
			int s = 0;
			for (int j = 0; j < nBasicRings; j++) {
				s+= cb[j][i];
			}
			switch(s) {
				case(0): { bondsAcyclicCount++; break; }
				case(1): { bondsCyclicCount ++; break; }
				default: { bondsCyclicCount ++; }
			}				
		}
		identifiedBonds = true;
	}


    @TestMethod("testGetAllRings")
    public IRingSet getAllRings() throws NoSuchAtomException {
		IRingSet ringset = getBasicRings();
		IRing newring;
		
		int nBasicRings = ringset.getAtomContainerCount();
		for (int i = 0; i < nBasicRings; i++) 
			getBondsInRing(molecule,(IRing) ringset.getAtomContainer(i), cb[i]);
		
		for (int i= 0; i < nBasicRings; i++) {
			for (int j= i+1; j < nBasicRings; j++) {
				//logger.debug("combining rings "+(i+1)+","+(j+1));
				newring = combineRings(ringset, i, j);				
				//newring = combineRings((Ring)ringset.get(i),(Ring)ringset.get(j));
				if (newring != null) ringset.addAtomContainer(newring);
			}
		}
			
		return ringset;
	}	

    @TestMethod("testGetSpanningTreeSize")
    public int getSpanningTreeSize() {
		return sptSize;
	}

	private IRing combineRings(IRingSet ringset, int i, int j) {
		int c = 0;
		for (int b= 0; b < cb[i].length; b++) {
			c = cb[i][b] + cb[j][b];
			if (c > 1) break;  //at least one common bond
		}
		if (c < 2) return null;
		IRing ring = molecule.getBuilder().newRing();
		IRing ring1 = (IRing) ringset.getAtomContainer(i);
		IRing ring2 = (IRing) ringset.getAtomContainer(j);
		for (int b= 0; b < cb[i].length; b++) {
			c = cb[i][b] + cb[j][b];
			if ((c == 1) && (cb[i][b] == 1)) ring.addBond(molecule.getBond(b));
			else
			if ((c == 1) && (cb[j][b] == 1)) ring.addBond(molecule.getBond(b));			
		}
		for (int a = 0; a < ring1.getAtomCount(); a++) 
			ring.addAtom(ring1.getAtom(a));
		for (int a = 0; a < ring2.getAtomCount(); a++) 
			ring.addAtom(ring2.getAtom(a));
		
		return ring;
	}

	/**
	 * @return Returns the bondsAcyclicCount.
	 */
    @TestMethod("testGetBondsAcyclicCount")
    public int getBondsAcyclicCount() {
		if (!identifiedBonds) identifyBonds();
		return bondsAcyclicCount;
	}

	/**
	 * @return Returns the bondsCyclicCount.
	 */
    @TestMethod("testGetBondsCyclicCount")
    public int getBondsCyclicCount()  {
		if (!identifiedBonds) identifyBonds();
		return bondsCyclicCount;
	}
}
