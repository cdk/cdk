/* $Revision: 7844 $ $Author: rajarshi $ $Date: 2007-04-08 14:46:29 -0500 (Thu, 01 Feb 2007) $
 *
 * Copyright (C) 2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.smiles.smarts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.LoggingTool;

/**
 * This class provides a easy to use wrapper around SMARTS matching
 * functionality. <p/> User code that wants to do SMARTS matching should use
 * this rather than using SMARTSParser (and UniversalIsomorphismTester)
 * directly. Example usage would be
 * 
 * <pre>
 * SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 * IAtomContainer atomContainer = sp.parseSmiles(&quot;CC(=O)OC(=O)C&quot;);
 * SMARTSQueryTool querytool = new SMARTSQueryTool(&quot;O=CO&quot;);
 * &lt;p/&gt;
 * boolean status = querytool.matches(atomContainer);
 * if (status) {
 *    int nmatch = querytool.countMatches();
 *    List mappings = querytool.getMatchingAtoms();
 *    for (int i = 0; i &lt; nmatch; i++) {
 *       List atomIndices = (List) mappings.get(i);
 *    }
 * }
 * </pre>
 * 
 * To use the JJTree based Smarts Parser, use:
 * 
 * <pre>
 * SMARTSQueryTool querytool = new SMARTSQueryTool(&quot;O=CO&quot;, true);
 * </pre>
 * 
 * @author Rajarshi Guha
 * @cdk.created 2007-04-08
 * @cdk.module smarts
 * @cdk.keyword SMARTS
 * @cdk.keyword substructure search
 */
public class SMARTSQueryTool {
    private LoggingTool logger;
    private String smarts;
    private IAtomContainer atomContainer = null;
    private QueryAtomContainer query = null;

    private List matchingAtoms = null;
    /**
	 * Whether to use JJTree based smarts parser
	 */
    private boolean useJJTree = false;

    public SMARTSQueryTool(String smarts) throws CDKException {
    	this(smarts, false);
    }
    
    public SMARTSQueryTool(String smarts, boolean useJJTree) throws CDKException {
        logger = new LoggingTool(this);
        this.smarts = smarts;
        this.useJJTree = useJJTree;
        initializeQuery();
    }

    /**
	 * Returns the current SMARTS pattern being used.
	 * 
	 * @return The SMARTS pattern
	 */
    public String getSmarts() {
        return smarts;
    }

    /**
	 * Set a new SMARTS pattern.
	 * 
	 * @param smarts
	 *            The new SMARTS pattern
	 * @throws CDKException
	 *             if there is an error in parsing the pattern
	 */
    public void setSmarts(String smarts) throws CDKException {
        this.smarts = smarts;
        initializeQuery();
    }

    /**
	 * Perform a SMARTS match and check whether the query is present in the
	 * target molecule. <p/> This function simply checks whether the query
	 * pattern matches the specified molecule. However the function will also,
	 * internally, save the mapping of query atoms to the target molecule
	 * 
	 * @param atomContainer
	 *            The target moleculoe
	 * @return true if the pattern is found in the target molecule, false
	 *         otherwise
	 * @throws CDKException
	 *             if there is an error in ring, aromaticity or isomorphism
	 *             perception
	 * @see #getMatchingAtoms()
	 * @see #countMatches()
	 */
    public boolean matches(IAtomContainer atomContainer) throws CDKException {
        // TODO: we should consider some sort of caching?
        this.atomContainer = atomContainer;
        initializeMolecule();

        // lets see if we have a single atom query
        if (query.getAtomCount() == 1) {
            // lets get the query atom
            IQueryAtom queryAtom = (IQueryAtom) query.getAtom(0);

            matchingAtoms = new ArrayList();
            Iterator atoms = this.atomContainer.atoms();
            while (atoms.hasNext()) {
                IAtom atom = (IAtom) atoms.next();
                if (queryAtom.matches(atom)) {
                    List tmp = new ArrayList();
                    tmp.add(atom);
                    matchingAtoms.add(tmp);
                }
            }
        } else {
            List bondMapping = UniversalIsomorphismTester.getSubgraphMaps(this.atomContainer, query);
            matchingAtoms = getAtomMappings(bondMapping, this.atomContainer);
        }

        return matchingAtoms.size() != 0;
    }

    /**
	 * Returns the number of times the pattern was found in the target molecule.
	 * <p/> This function should be called after
	 * {@link #matches(org.openscience.cdk.interfaces.IAtomContainer)}. If not,
	 * the results may be undefined.
	 * 
	 * @return The number of times the pattern was found in the target molecule
	 */
    public int countMatches() {
        return matchingAtoms.size();
    }

    /**
	 * Get the atoms in the target molecule that match the query pattern. <p/>
	 * Since there may be multiple matches, the return value is a List of List
	 * objects. Each List object contains the indices of the atoms in the target
	 * molecule, that match the query pattern
	 * 
	 * @return A List of List of atom indices in the target molecule
	 */
    public List getMatchingAtoms() {
        return matchingAtoms;
    }

    /**
	 * Prepare the target molecule for analysis. <p/> We perform ring perception
	 * and aromaticity detection and set up the appropriate properties. Right
	 * now, this function is called each time we need to do a query and this is
	 * inefficient.
	 * 
	 * @throws CDKException
	 *             if there is a problem in ring perception or aromaticity
	 *             detection, which is usually related to a timeout in the ring
	 *             finding code.
	 */
    private void initializeMolecule() throws CDKException {    	
        // do all ring perception
        AllRingsFinder arf = new AllRingsFinder();
        IRingSet allRings = null;
        try {
            allRings = arf.findAllRings(atomContainer);
        } catch (CDKException e) {
            logger.debug(e.toString());
            throw new CDKException(e.toString());
        }

        // sets SSSR information
        SSSRFinder finder = new SSSRFinder(atomContainer);
        IRingSet sssr = finder.findEssentialRings();
        
        Iterator atoms = atomContainer.atoms();
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            
            // add a property to each ring atom that will be an array of
            // Integers, indicating what size ring the given atom belongs to
            // Add SSSR ring counts
            if (allRings.contains(atom)) { // it's in a ring
            	atom.setFlag(CDKConstants.ISINRING, true);
                // lets find which ring sets it is a part of
                List ringsizes = new ArrayList();
                IRingSet currentRings = allRings.getRings(atom);
                int min = 0;
                for (int i = 0; i < currentRings.getAtomContainerCount(); i++) {
                    int size = currentRings.getAtomContainer(i).getAtomCount();
                    if (min > size) min = size;
                    ringsizes.add(new Integer(size));
                }
                atom.setProperty(CDKConstants.RING_SIZES, ringsizes);
                atom.setProperty(CDKConstants.SMALLEST_RINGS, sssr.getRings(atom));
            }
            
            // determine how many rings bonds each atom is a part of
            int hCount = atom.getHydrogenCount();
            List connectedAtoms = atomContainer.getConnectedAtomsList(atom);
            int total = hCount + connectedAtoms.size(); 
            for (int i = 0; i < connectedAtoms.size(); i++) {
            	if(((IAtom)connectedAtoms.get(i)).getSymbol().equals("H")) {
            		total--;
            		hCount++;
                }
             }
            atom.setProperty(CDKConstants.TOTAL_CONNECTIONS,new Integer(total));
            atom.setProperty(CDKConstants.TOTAL_H_COUNT, new Integer(hCount)); 
            
            // TODO: Sets atom valency.
            // Code copied from org.openscience.cdk.qsar.ChiIndexUtils
            Map valencesTable = new HashMap();
            valencesTable.put("H", new Integer(1));
            valencesTable.put("Li", new Integer(1));
            valencesTable.put("Be", new Integer(2));
            valencesTable.put("B", new Integer(3));
            valencesTable.put("C", new Integer(4));
            valencesTable.put("N", new Integer(5));
            valencesTable.put("O", new Integer(6));
            valencesTable.put("F", new Integer(7));
            valencesTable.put("Na", new Integer(1));
            valencesTable.put("Mg", new Integer(2));
            valencesTable.put("Al", new Integer(3));
            valencesTable.put("Si", new Integer(4));
            valencesTable.put("P", new Integer(5));
            valencesTable.put("S", new Integer(6));
            valencesTable.put("Cl", new Integer(7));
            valencesTable.put("K", new Integer(1));
            valencesTable.put("Ca", new Integer(2));
            valencesTable.put("Ga", new Integer(3));
            valencesTable.put("Ge", new Integer(4));
            valencesTable.put("As", new Integer(5));
            valencesTable.put("Se", new Integer(6));
            valencesTable.put("Br", new Integer(7));
            valencesTable.put("Rb", new Integer(1));
            valencesTable.put("Sr", new Integer(2));
            valencesTable.put("In", new Integer(3));
            valencesTable.put("Sn", new Integer(4));
            valencesTable.put("Sb", new Integer(5));
            valencesTable.put("Te", new Integer(6));
            valencesTable.put("I", new Integer(7));
            valencesTable.put("Cs", new Integer(1));
            valencesTable.put("Ba", new Integer(2));
            valencesTable.put("Tl", new Integer(3));
            valencesTable.put("Pb", new Integer(4));
            valencesTable.put("Bi", new Integer(5));
            valencesTable.put("Po", new Integer(6));
            valencesTable.put("At", new Integer(7));
            valencesTable.put("Fr", new Integer(1));
            valencesTable.put("Ra", new Integer(2));
            valencesTable.put("Cu", new Integer(2));
            valencesTable.put("Mn", new Integer(2));
            valencesTable.put("Co", new Integer(2));

            if (valencesTable.get(atom.getSymbol()) != null) {
                atom.setValency(((Integer)valencesTable.get(atom.getSymbol())).intValue() -
                		atom.getFormalCharge());
            }
        }
        
        Iterator bonds = atomContainer.bonds();
        while (bonds.hasNext()) {
        	IBond bond = (IBond)bonds.next();
        	if (allRings.getRings(bond).size() > 0) {
        		bond.setFlag(CDKConstants.ISINRING, true);
        	}
        }
        
        atoms = atomContainer.atoms();
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            List connectedAtoms = atomContainer.getConnectedAtomsList(atom);

            int counter = 0;
            IAtom any;
            for (int i = 0; i < connectedAtoms.size(); i++) {
                any = (IAtom) connectedAtoms.get(i);
                if (any.getFlag(CDKConstants.ISINRING)) {
                    counter++;
                }
            }
            atom.setProperty(CDKConstants.RING_CONNECTIONS, new Integer(counter));
        }

        // check for atomaticity
        try {
            HueckelAromaticityDetector.detectAromaticity(atomContainer, true, arf);
        } catch (CDKException e) {
            logger.debug(e.toString());
            throw new CDKException(e.toString());
        }

    }

    private void initializeQuery() throws CDKException {
        matchingAtoms = null;
        if (useJJTree) {
        	query = org.openscience.cdk.smiles.smarts.parser.SMARTSParser.parse(smarts);
        } else {
        	query = SMARTSParser.parse(smarts);
        }
    }


    private List getAtomMappings(List bondMapping, IAtomContainer atomContainer) {
        List atomMapping = new ArrayList();

        // loop over each mapping
        for (int i = 0; i < bondMapping.size(); i++) {
            List list = (List) bondMapping.get(i);

            List tmp = new ArrayList();
            // loop over this mapping
            for (int j = 0; j < list.size(); j++) {
                RMap map = (RMap) list.get(j);
                int bondID = map.getId1();

                // get the atoms in this bond
                IBond bond = atomContainer.getBond(bondID);
                IAtom atom1 = bond.getAtom(0);
                IAtom atom2 = bond.getAtom(1);

                Integer idx1 = new Integer(atomContainer.getAtomNumber(atom1));
                Integer idx2 = new Integer(atomContainer.getAtomNumber(atom2));

                if (!tmp.contains(idx1)) tmp.add(idx1);
                if (!tmp.contains(idx2)) tmp.add(idx2);
            }
            if (tmp.size() > 0) atomMapping.add(tmp);
        }
        return atomMapping;
    }
}
