/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2002-2007  Stephane Werner <mail@ixelis.net>
 *  
 * This code has been kindly provided by Stephane Werner 
 * and Thierry Hanser from IXELIS mail@ixelis.net.
 *  
 * IXELIS sarl - Semantic Information Systems
 *               17 rue des C?dres 67200 Strasbourg, France
 *               Tel/Fax : +33(0)3 88 27 81 39 Email: mail@ixelis.net
 *
 * CDK Contact: cdk-devel@lists.sf.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.isomorphism.mcss;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

/**
  * This class implements the Resolution Graph (RGraph).
  * The RGraph is a graph based representation of the search problem.
  * An RGraph is constructed from the two compared graphs (G1 and G2).
  * Each vertex (node) in the RGraph represents a possible association
  * from an edge in G1 with an edge in G2. Thus two compatible bonds
  * in two molecular graphs are represented by a vertex in the RGraph.
  * Each edge in the RGraph corresponds to a common adjacency relationship
  * between the 2 couple of compatible edges associated to the 2 RGraph nodes
  * forming this edge.
  * 
  * <p>Example:
  * <pre>
  *    G1 : C-C=O  and G2 : C-C-C=0
  *         1 2 3           1 2 3 4
  * </pre>
  *
  *  <p>The resulting RGraph(G1,G2) will contain 3 nodes:
  *  <ul>
  *    <li>Node A : association between bond C-C :  1-2 in G1 and 1-2 in G2
  *    <li>Node B : association between bond C-C :  1-2 in G1 and 2-3 in G2
  *    <li>Node C : association between bond C=0 :  2-3 in G1 and 3-4 in G2
  *  </ul>
  *  The RGraph will also contain one edge representing the 
  *  adjacency between node B and C  that is : bonds 1-2 and 2-3 in G1 
  *  and bonds 2-3 and 3-4 in G2.
  *
  *  <p>Once the RGraph has been built from the two compared graphs
  *  it becomes a very interesting tool to perform all kinds of 
  *  structural search (isomorphism, substructure search, maximal common
  *  substructure,....).
  *
  *  <p>The  search may be constrained by mandatory elements (e.g. bonds that
  *  have to be present in the mapped common substructures).
  *
  *  <p>Performing a query on an RGraph requires simply to set the constrains
  *  (if any) and to invoke the parsing method (parse())
  * 
  *  <p>The RGraph has been designed to be a generic tool. It may be constructed
  *  from any kind of source graphs, thus it is not restricted to a chemical
  *  context.
  *
  *  <p>The RGraph model is indendant from the CDK model and the link between
  *  both model is performed by the RTools class. In this way the RGraph 
  *  class may be reused in other graph context (conceptual graphs,....)
  *
  *  <p><b>Important note</b>: This implementation of the algorithm has not been
  *                      optimized for speed at this stage. It has been
  *                      written with the goal to clearly retrace the 
  *                      principle of the underlined search method. There is
  *                      room for optimization in many ways including the
  *                      the algorithm itself. 
  *
  *  <p>This algorithm derives from the algorithm described in
  *  {@cdk.cite HAN90} and modified in the thesis of T. Hanser {@cdk.cite HAN93}.
  *
  * @author      Stephane Werner from IXELIS mail@ixelis.net
  * @cdk.created 2002-07-17
  * @cdk.require java1.4+
  * @cdk.module  standard
 * @cdk.svnrev  $Revision$
  */
public class RGraph
{
    // an RGraph is a list of RGraph nodes
    // each node keeping track of its
    // neighbors.
    List<RNode> graph = null;

    // maximal number of iterations before
    // search break
    int maxIteration = -1;
    
    // dimensions of the compared graphs
    int firstGraphSize = 0;
    int secondGraphSize = 0;

    // constrains 
    BitSet c1 = null;
    BitSet c2 = null;
    
    // current solution list
    List<BitSet> solutionList = null;

    // flag to define if we want to get all possible 'mappings'    
    boolean findAllMap = false;
    
    // flag to define if we want to get all possible 'structures'
    boolean findAllStructure = true;
    
    // working variables
    boolean stop = false;
    int nbIteration = 0;
    BitSet graphBitSet = null;
    
    /**
     * Constructor for the RGraph object and creates an empty RGraph.
     */
    public RGraph()
    {
        graph = new ArrayList<RNode>();
        solutionList = new ArrayList<BitSet>();
        graphBitSet = new BitSet();
    }

    /**
     *  Returns the size of the first of the two
     *  compared graphs.
     * @return The size of the first of the two compared graphs         
     */
    public int getFirstGraphSize()
    {
	    return firstGraphSize;
    }
    
    /**
     *  Returns the size of the second of the two
     *  compared graphs.
     * @return The size of the second of the two compared graphs         
     */
    public int getSecondGraphSize()
    {
	    return secondGraphSize;
    }

    
    /**
     *  Sets the size of the first of the two
     *  compared graphs.
     * @param n1 The size of the second of the two compared graphs         
     */
    public void setFirstGraphSize(int n1)
    {
	    firstGraphSize = n1;
    }
    
    /**
     *  Returns the size of the second of the two
     *  compared graphs.
     * @param n2 The size of the second of the two compared graphs         
     */
    public void setSecondGraphSize(int n2)
    {
	    secondGraphSize = n2;
    }

    /**
     *  Reinitialisation of the TGraph.
     */
    public void clear()
    {
        graph.clear();
        graphBitSet.clear();
    }

    /**
     *  Returns the graph object of this RGraph.
     * @return      The graph object, a list         
     */
    public List<RNode> getGraph()
    {
	    return this.graph;
    }
    
    /**
     *  Adds a new node to the RGraph.
     * @param  newNode  The node to add to the graph
     */
    public void addNode(RNode newNode)
    {
        graph.add(newNode);
        graphBitSet.set(graph.size() - 1);
    }

    /**
     *  Parsing of the RGraph. This is the main method
     *  to perform a query. Given the constrains c1 and c2
     *  defining mandatory elements in G1 and G2 and given
     *  the search options, this method builds an initial set
     *  of starting nodes (B) and parses recursively the
     *  RGraph to find a list of solution according to 
     *  these parameters.
     *
     * @param  c1  constrain on the graph G1
     * @param  c2  constrain on the graph G2
     * @param  findAllStructure true if we want all results to be generated   
     * @param  findAllMap true is we want all possible 'mappings'
     */
    public void parse(BitSet c1, BitSet c2, boolean findAllStructure, boolean findAllMap)
    {
        // initialize the list of solution
        solutionList.clear();
        
        // builds the set of starting nodes
        // according to the constrains
        BitSet b = buildB(c1, c2);
        
        // setup options
        setAllStructure(findAllStructure);
        setAllMap(findAllMap);
        
        // parse recursively the RGraph
        parseRec(new BitSet(b.size()), b, new BitSet(b.size()));
    }
    
    /**
     *  Parsing of the RGraph. This is the recursive method
     *  to perform a query. The method will recursively
     *  parse the RGraph thru connected nodes and visiting the
     *  RGraph using allowed adjacency relationship.
     *
     * @param  traversed  node already parsed
     * @param  extension  possible extension node (allowed neighbors)
     * @param  forbiden   node forbidden (set of node incompatible with the current solution)
     */
    private void parseRec(BitSet traversed, BitSet extension, BitSet forbidden)
    {
        BitSet newTraversed = null;
        BitSet newExtension = null;
        BitSet newForbidden = null;
        BitSet potentialNode = null;

        // if there is no more extension possible we
        // have reached a potential new solution
        if(extension.isEmpty())
        {
            solution(traversed);
        }
        // carry on with each possible extension
        else
        {
            // calculates the set of nodes that may still
            // be reached at this stage (not forbidden)
            potentialNode = ((BitSet) graphBitSet.clone());
            potentialNode.andNot(forbidden);
            potentialNode.or(traversed);

            // checks if we must continue the search
            // according to the potential node set
            if(mustContinue(potentialNode))
            {
                // carry on research and update iteration count
                nbIteration++;

                // for each node in the set of possible extension (neighbors of 
                // the current partial solution, include the node to the solution
                // and parse recursively the RGraph with the new context.
                for(int x = extension.nextSetBit(0); x >= 0 && !stop; x = extension.nextSetBit(x + 1))
                {
                    // evaluates the new set of forbidden nodes
                    // by including the nodes not compatible with the
                    // newly accepted node.
                    newForbidden = (BitSet) forbidden.clone();
                    newForbidden.or(((RNode) graph.get(x)).forbidden);

                    // if it is the first time we are here then
                    // traversed is empty and we initialize the set of
                    // possible extensions to the extension of the first
                    // accepted node in the solution.
                    if(traversed.isEmpty())
                    {
                        newExtension = (BitSet) (((RNode) graph.get(x)).extension.clone());
                    }
                    // else we simply update the set of solution by
                    // including the neighbors of the newly accepted node
                    else
                    {
                        newExtension = (BitSet) extension.clone();
                        newExtension.or(((RNode) graph.get(x)).extension);
                    }
                    
                    // extension my not contain forbidden nodes
                    newExtension.andNot(newForbidden);
                    
                    // create the new set of traversed node
                    // (update current partial solution)
                    // and add x to the set of forbidden node
                    // (a node may only appear once in a solution)
                    newTraversed = (BitSet) traversed.clone();
                    newTraversed.set(x);
                    forbidden.set(x);
                    
                    // parse recursively the RGraph
                    parseRec(newTraversed, newExtension, newForbidden);
                }
            }
        }
    }

    /**
     * Checks if a potential solution is a real one 
     * (not included in a previous solution)
     *  and add this solution to the solution list
     * in case of success.
     *
     * @param  traversed  new potential solution
     */
    private void solution(BitSet traversed)
    {
        boolean included = false;
        BitSet projG1 = projectG1(traversed);
        BitSet projG2 = projectG2(traversed);

        // the solution must follows the search constrains
        // (must contain the mandatory elements in G1 an G2)
        if(isContainedIn(c1, projG1) && isContainedIn(c2, projG2))
        {
            // the solution should not be included in a previous solution
            // at the RGraph level. So we check against all previous solution
            // On the other hand if a previous solution is included in the
            // new one, the previous solution is removed.
            for(Iterator<BitSet> i = solutionList.listIterator(); i.hasNext() && !included; )
            {
                BitSet sol = i.next();

                if(!sol.equals(traversed))
                {
                    // if we asked to save all 'mappings' then keep this mapping
                    if(findAllMap && (projG1.equals(projectG1(sol)) || projG2.equals(projectG2(sol))))
                    {
                        // do nothing
                    }
                    // if the new solution is included mark it as included
                    else if(isContainedIn(projG1, projectG1(sol)) || isContainedIn(projG2, projectG2(sol)))
                    {
                        included = true;
                    }
                    // if the previous solution is contained in the new one, remove the previous solution
                    else if(isContainedIn(projectG1(sol), projG1) || isContainedIn(projectG2(sol), projG2))
                    {
                        i.remove();
                    }
                }
                else
                {
                    // solution already exists
                    included = true;
                }
            }

            if(included == false)
            {
                // if it is really a new solution add it to the 
                // list of current solution
                solutionList.add(traversed);
            }

            if(!findAllStructure)
            {
                // if we need only one solution
                // stop the search process
                // (e.g. substructure search)
                stop = true;
            }
        }
    }

    /**
     *  Determine if there are potential solution remaining.
     * @param       potentialNode  set of remaining potential nodes
     * @return      true if it is worse to continue the search         
     */
    private boolean mustContinue(BitSet potentialNode)
    {
        boolean result = true;
        boolean cancel = false;
        BitSet projG1 = projectG1(potentialNode);
        BitSet projG2 = projectG2(potentialNode);

        // if we reached the maximum number of
        // search iterations than do not continue
        if(maxIteration != -1 && nbIteration >= maxIteration)
        {
            return false;
        }
        
        // if constrains may no more be fulfilled then stop.
        if(!isContainedIn(c1, projG1) || !isContainedIn(c2, projG2))
        {
            return false;
        }
        
        // check if the solution potential is not included in an already
        // existing solution
        for(Iterator<BitSet> i = solutionList.iterator(); i.hasNext() && !cancel; )
        {
            BitSet sol = i.next();

            // if we want every 'mappings' do not stop
            if(findAllMap && (projG1.equals(projectG1(sol)) || projG2.equals(projectG2(sol))))
            {
                // do nothing
            }
            // if it is not possible to do better than an already existing solution than stop.
            else if(isContainedIn(projG1, projectG1(sol)) || isContainedIn(projG2, projectG2(sol)))
            {
                result = false;
                cancel = true;
            }
        }
        
        return result;
    }

    /**
     *  Builds the initial extension set. This is the
     *  set of node that may be used as seed for the
     *  RGraph parsing. This set depends on the constrains
     *  defined by the user.
     * @param  c1  constraint in the graph G1
     * @param  c2  constraint in the graph G2
     * @return     
     */
    private BitSet buildB(BitSet c1, BitSet c2)
    {
        this.c1 = c1;
        this.c2 = c2;

        BitSet bs = new BitSet();

        // only nodes that fulfill the initial constrains
        // are allowed in the initial extension set : B
        for(Iterator<RNode> i = graph.iterator(); i.hasNext(); )
        {
            RNode rn = i.next();

            if((c1.get(rn.rMap.id1) || c1.isEmpty()) && (c2.get(rn.rMap.id2) || c2.isEmpty()))
            {
                bs.set(graph.indexOf(rn));
            }
        }
        return bs;
    }
    
    /**
     *  Returns the list of solutions.
     *
     * @return    The solution list 
     */
    public List<BitSet> getSolutions()
    {
        return solutionList;
    }

    /**
     *  Converts a RGraph bitset (set of RNode)
     * to a list of RMap that represents the 
     * mapping between to substructures in G1 and G2
     * (the projection of the RGraph bitset on G1
     * and G2).
     *
     * @param  set  the BitSet
     * @return      the RMap list
     */
    public List<RMap> bitSetToRMap(BitSet set)
    {
        List<RMap> rMapList = new ArrayList<RMap>();

        for(int x = set.nextSetBit(0); x >= 0; x = set.nextSetBit(x + 1))
        {
            RNode xNode = graph.get(x);
            rMapList.add(xNode.rMap);
        }
        return rMapList;
    }
    
    /**
     *  Sets the 'AllStructres' option. If true
     * all possible solutions will be generated. If false
     * the search will stop as soon as a solution is found.
     * (e.g. when we just want to know if a G2 is
     *  a substructure of G1 or not).
     *
     * @param  findAllStructure  
     */
    public void setAllStructure(boolean findAllStructure)
    {
        this.findAllStructure = findAllStructure;
    }

    /**
     *  Sets the 'finAllMap' option. If true
     * all possible 'mappings' will be generated. If false
     * the search will keep only one 'mapping' per structure
     * association.
     *
     * @param  findAllMap  
     */
    public void setAllMap(boolean findAllMap)
    {
        this.findAllMap = findAllMap;
    }

    /**
     * Sets the maxIteration for the RGraph parsing. If set to -1,
     * then no iteration maximum is taken into account.
     *
     * @param  it  The new maxIteration value
     */
    public void setMaxIteration(int it)
    {
        this.maxIteration = it;
    }
    
   /**
    *  Returns a string representation of the RGraph.
    * @return the string representation of the RGraph
    */
    public String toString()
    {
        String message = "";
        int j = 0;

        for(Iterator<RNode> i = graph.iterator(); i.hasNext(); )
        {
            RNode rn = i.next();
            message += "-------------\n" + "RNode " + j + "\n" + rn.toString() + "\n";
            j++;
        }
        return message;
    } 

    
    /////////////////////////////////
    // BitSet tools
    /**
     *  Projects a RGraph bitset on the source graph G1.
     * @param  set  RGraph BitSet to project
     * @return      The associate BitSet in G1 
     */
    public BitSet projectG1(BitSet set)
    {
        BitSet projection = new BitSet(firstGraphSize);
        RNode xNode = null;

        for(int x = set.nextSetBit(0); x >= 0; x = set.nextSetBit(x + 1))
        {
            xNode = (RNode) graph.get(x);
            projection.set(xNode.rMap.id1);
        }
        return projection;
    }

    /**
     *  Projects a RGraph bitset on the source graph G2.
     * @param  set  RGraph BitSet to project
     * @return      The associate BitSet in G2 
     */
    public BitSet projectG2(BitSet set)
    {
        BitSet projection = new BitSet(secondGraphSize);
        RNode xNode = null;

        for(int x = set.nextSetBit(0); x >= 0; x = set.nextSetBit(x + 1))
        {
            xNode = (RNode) graph.get(x);
            projection.set(xNode.rMap.id2);
        }
        return projection;
    }

    /**
     *  Test if set A is contained in  set B.
     * @param  A  a bitSet 
     * @param  B  a bitSet 
     * @return    true if  A is contained in  B 
     */
    private boolean isContainedIn(BitSet A, BitSet B)
    {
        boolean result = false;

        if(A.isEmpty())
        {
            return true;
        }

        BitSet setA = (BitSet) A.clone();
        setA.and(B);

        if(setA.equals(A))
        {
            result = true;
        }

        return result;
    }   
}

