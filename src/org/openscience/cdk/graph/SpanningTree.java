/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 2001-2005  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.graph;


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.Ring;
import org.openscience.cdk.interfaces.RingSet;
import org.openscience.cdk.exception.NoSuchAtomException;


/**
 * Spanning tree of a molecule.
 * Used to discover the number of cyclic bonds in order to prevent the 
 * inefficient AllRingsFinder to run for too long.
 *
 * @author   Nina Jeliazkova
 * @cdk.todo junit test of this
 *
 * @cdk.module  standard
 * @cdk.dictref blue-obelisk:graphSpanningTree
 */
public class SpanningTree {
	private int[] parent = null;
	private int[][] cb = null;
	
	protected boolean[] bondsInTree;
	
	private int sptSize = 0;
	private int edrSize = 0;
	
	private int bondsAcyclicCount = 0,bondsCyclicCount = 0;
	
	private IAtomContainer molecule = null;
	private int E=0,V=0;
	private boolean disconnected;
	
	public boolean isDisconnected() {
		return disconnected;
	}
	/**
	 * 
	 */
	public SpanningTree(IAtomContainer atomContainer) {
		super();
		buildSpanningTree(atomContainer);
	}
	
	public SpanningTree() {
		super();
	}
	public void clear() {
		molecule = null;
		cb = null;
		parent = null;
		sptSize = 0;
		edrSize = 0;
		bondsAcyclicCount = 0;
		bondsCyclicCount = 0;
		bondsInTree = null;
		E=0;
		V=0;
		disconnected = false;
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
	
	
	/**
	 * Kruskal algorithm
	 * @param atomContainer
	 */
	public void buildSpanningTree(IAtomContainer atomContainer){
		disconnected = false;
		molecule = atomContainer;
		
		V = atomContainer.getAtomCount();
		E = atomContainer.getBondCount();
		
		sptSize = 0;edrSize = 0; 		
		fastFindInit(V);
		for (int i = 0; i < V; i++) {
			(atomContainer.getAtomAt(i)).setProperty("ST_ATOMNO", Integer.toString(i+1));
		}
		Bond bond;
		int v1,v2;
		bondsInTree = new boolean[E];
		
		for (int b=0; b < E; b++ ) {
			bondsInTree[b] = false;			
			bond = atomContainer.getBondAt(b);
			v1 = Integer.parseInt((bond.getAtomAt(0)).getProperty("ST_ATOMNO").toString());
			v2 = Integer.parseInt((bond.getAtomAt(1)).getProperty("ST_ATOMNO").toString());
			//this below is a little bit  slower
			//v1 = atomContainer.getAtomNumber(bond.getAtomAt(0))+1; 
			//v2 = atomContainer.getAtomNumber(bond.getAtomAt(1))+1;
			if (fastfind(v1,v2,true)) {
				bondsInTree[b] = true;
				sptSize++;
				//System.out.println("ST : includes bond between atoms "+v1+","+v2);
			}
			if (sptSize>=(V-1)) break;
			
		}
		// if atomcontainer is connected then the number of bonds in the spanning tree = (No atoms-1)
		//i.e.  edgesRings = new Bond[E-V+1];
		//but to hold all bonds if atomContainer was disconnected then  edgesRings = new Bond[E-sptSize]; 
		if (sptSize != (V-1)) disconnected = true;
		for (int b=0; b < E; b++ ) if (!bondsInTree[b]){
//			edgesRings[edrSize] = atomContainer.getBondAt(b);
			edrSize++;
		}
		cb = new int[edrSize][E];
		for (int i = 0; i < edrSize; i++) 
			for (int a = 0; a < E; a++) 
				cb[i][a] = 0;
		
	}
	public IAtomContainer getSpanningTree() {
		IAtomContainer ac = molecule.getBuilder().newAtomContainer();
		for (int a=0 ; a < V; a++) ac.addAtom(molecule.getAtomAt(a));
		for (int b=0; b < E; b++ ) if (bondsInTree[b])
			ac.addBond(molecule.getBondAt(b));
		return ac;
	}
	
	public static void resetFlags(IAtomContainer ac)
	{
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			ac.getAtomAt(f).setFlag(CDKConstants.VISITED, false);
		}
		for (int f = 0; f < ac.getElectronContainerCount(); f++)
		{
			ac.getElectronContainerAt(f).setFlag(CDKConstants.VISITED, false);
		}
		
	}	
	public IAtomContainer getPath(IAtomContainer spt,IAtom a1, IAtom a2) throws NoSuchAtomException {
		
		IAtomContainer path = spt.getBuilder().newAtomContainer();
		PathTools.resetFlags(spt);
		path.addAtom(a1);
		PathTools.depthFirstTargetSearch(spt,a1,a2,path);		
		return path;
	}
	private Ring getRing(IAtomContainer spt, Bond bond) throws NoSuchAtomException {
		Ring ring = spt.getBuilder().newRing();
		PathTools.resetFlags(spt);
		ring.addAtom(bond.getAtomAt(0));		
		PathTools.depthFirstTargetSearch(spt,bond.getAtomAt(0),bond.getAtomAt(1),ring);		
		ring.addBond(bond);
		return ring;
	}
	private void getBondsInRing(IAtomContainer mol, Ring ring, int[] bonds) {
		for (int i=0; i < ring.getBondCount(); i++ ) {
			int m = mol.getBondNumber(ring.getBondAt(i));
			bonds[m] = 1;
		}
	}
	
	public RingSet getBasicRings() throws NoSuchAtomException {
		RingSet ringset = molecule.getBuilder().newRingSet();
		IAtomContainer spt = getSpanningTree();
		for (int i = 0; i < E; i++) if (!bondsInTree[i])  
			ringset.add(getRing(spt,molecule.getBondAt(i)));
		spt = null;	
		return ringset;
	}
	public void identifyBonds()  throws NoSuchAtomException {
		IAtomContainer spt = getSpanningTree();
		Ring ring;
		int nBasicRings = 0;
		for (int i = 0; i < E; i++) if (!bondsInTree[i]) {  
			ring = getRing(spt,molecule.getBondAt(i));
			for (int b=0; b < ring.getBondCount(); b++ ) {
				int m = molecule.getBondNumber(ring.getBondAt(b));
				cb[nBasicRings][m] = 1;
			}
			nBasicRings++;
	    }
		spt = null; ring = null;
		bondsAcyclicCount = 0; bondsCyclicCount = 0;
		for (int i = 0; i < E; i++) {
			int s = 0;
			for (int j = 0; j < nBasicRings; j++) s+= cb[j][i];
			switch(s) {
			//acyclic bond
			case(0): { bondsAcyclicCount++; break;}
			case(1): {bondsCyclicCount ++; break;
			}
			default: {
				bondsCyclicCount ++;
				
			}
			}
				
		}
	
	}
	public RingSet getAllRings() throws NoSuchAtomException {
		RingSet ringset = getBasicRings();
		Ring newring = null;
		//System.out.println("Rings "+ringset.size());
		
		int nBasicRings = ringset.size();
		for (int i = 0; i < nBasicRings; i++) 
			getBondsInRing(molecule,(Ring) ringset.get(i), cb[i]);
		
		
		
		for (int i= 0; i < nBasicRings; i++) {
			for (int j= i+1; j < nBasicRings; j++) {
				//System.out.println("combining rings "+(i+1)+","+(j+1));
				newring = combineRings(ringset, i, j);				
				//newring = combineRings((Ring)ringset.get(i),(Ring)ringset.get(j));
				if (newring != null) ringset.add(newring);
			}
		}
			
		return ringset;
	}	
	public int getSpanningTreeSize() {
		return sptSize;
	}
	public void printAtoms(IAtomContainer ac) {
		for (int i = 0; i < ac.getAtomCount(); i++)
			System.out.print(ac.getAtomAt(i).getProperty("ST_ATOMNO").toString() + ",");
	}
	private Ring combineRings(RingSet ringset, int i, int j) {
		int c = 0;
		for (int b= 0; b < cb[i].length; b++) {
			c = cb[i][b] + cb[j][b];
			if (c > 1) break;  //at least one common bond
		}
		if (c < 2) return null;
		Ring ring = molecule.getBuilder().newRing();
		Ring ring1 = (Ring) ringset.get(i);
		Ring ring2 = (Ring) ringset.get(j);
		for (int b= 0; b < cb[i].length; b++) {
			c = cb[i][b] + cb[j][b];
			if ((c == 1) && (cb[i][b] == 1)) ring.addBond(molecule.getBondAt(b));
			else
			if ((c == 1) && (cb[j][b] == 1)) ring.addBond(molecule.getBondAt(b));			
		}
		for (int a = 0; a < ring1.getAtomCount(); a++) 
			ring.addAtom(ring1.getAtomAt(a));
		for (int a = 0; a < ring2.getAtomCount(); a++) 
			ring.addAtom(ring2.getAtomAt(a));
		
		return ring;
	}

	/**
	 * @return Returns the bondsAcyclicCount.
	 */
	public int getBondsAcyclicCount() {
		return bondsAcyclicCount;
	}
	/**
	 * @return Returns the bondsCyclicCount.
	 */
	public int getBondsCyclicCount() {
		return bondsCyclicCount;
	}
}
