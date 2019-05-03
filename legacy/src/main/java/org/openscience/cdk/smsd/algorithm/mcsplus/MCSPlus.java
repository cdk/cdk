/* Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smsd.algorithm.mcgregor.McGregor;
import org.openscience.cdk.smsd.global.TimeOut;
import org.openscience.cdk.smsd.tools.TimeManager;

/**
 * This class handles MCS plus algorithm which is a combination of
 * c-clique algorithm and McGregor algorithm.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class MCSPlus {

    /**
    * Default constructor added
    */
    public MCSPlus() {

    }

    private static TimeManager timeManager = null;

    /**
     * @return the timeout
     */
    protected synchronized static double getTimeout() {
        return TimeOut.getInstance().getTimeOut();
    }

    /**
     * @return the timeManager
     */
    protected synchronized static TimeManager getTimeManager() {
        return timeManager;
    }

    /**
     * @param aTimeManager the timeManager to set
     */
    protected synchronized static void setTimeManager(TimeManager aTimeManager) {
        TimeOut.getInstance().setTimeOutFlag(false);
        timeManager = aTimeManager;
    }

    /**
     *
     * @param ac1
     * @param ac2
     * @param shouldMatchBonds
     * @return
     * @throws CDKException
     */
    protected List<List<Integer>> getOverlaps(IAtomContainer ac1, IAtomContainer ac2, boolean shouldMatchBonds)
            throws CDKException {
        Stack<List<Integer>> maxCliqueSet = null;
        List<List<Integer>> mappings = new ArrayList<List<Integer>>();
        try {
            GenerateCompatibilityGraph gcg = new GenerateCompatibilityGraph(ac1, ac2, shouldMatchBonds);
            List<Integer> compGraphNodes = gcg.getCompGraphNodes();

            List<Integer> cEdges = gcg.getCEgdes();
            List<Integer> dEdges = gcg.getDEgdes();

            //            System.err.println("**************************************************");
            //            System.err.println("CEdges: " + CEdges.size());
            //            System.out.println("DEdges: " + DEdges.size());

            BKKCKCF init = new BKKCKCF(compGraphNodes, cEdges, dEdges);
            maxCliqueSet = init.getMaxCliqueSet();

            //            System.err.println("**************************************************");
            //            System.err.println("Max_Cliques_Set: " + maxCliqueSet.size());
            //            System.out.println("Best Clique Size: " + init.getBestCliqueSize());

            //clear all the compatibility graph content
            gcg.clear();
            while (!maxCliqueSet.empty()) {
                List<Integer> cliqueList = maxCliqueSet.peek();
                int cliqueSize = cliqueList.size();
                if (cliqueSize < ac1.getAtomCount() && cliqueSize < ac2.getAtomCount()) {
                    McGregor mgit = new McGregor(ac1, ac2, mappings, shouldMatchBonds);
                    mgit.startMcGregorIteration(mgit.getMCSSize(), cliqueList, compGraphNodes);
                    mappings = mgit.getMappings();
                    mgit = null;
                } else {
                    mappings = ExactMapping.extractMapping(mappings, compGraphNodes, cliqueList);
                }
                maxCliqueSet.pop();
                if (isTimeOut()) {
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MCSPlus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mappings;
    }

    public synchronized static boolean isTimeOut() {
        if (getTimeout() > -1 && getTimeManager().getElapsedTimeInMinutes() > getTimeout()) {
            TimeOut.getInstance().setTimeOutFlag(true);
            return true;
        }
        return false;
    }
}
