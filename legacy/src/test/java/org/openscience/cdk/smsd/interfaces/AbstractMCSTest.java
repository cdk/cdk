/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

/**
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 *
 * @cdk.module test-smsd
 */
public abstract class AbstractMCSTest {

    public class AbstractMCSImpl extends AbstractMCS {

        @Override
        public void init(IAtomContainer source, IAtomContainer target, boolean removeHydrogen, boolean cleanMol)
                throws CDKException {}

        @Override
        public void init(IQueryAtomContainer source, IAtomContainer target) throws CDKException {}

        @Override
        public void setChemFilters(boolean stereoFilter, boolean fragmentFilter, boolean energyFilter) {}

        @Override
        public Double getEnergyScore(int Key) {
            return null;
        }

        @Override
        public Integer getFragmentSize(int Key) {
            return null;
        }

        @Override
        public IAtomContainer getProductMolecule() {
            return null;
        }

        @Override
        public IAtomContainer getReactantMolecule() {
            return null;
        }

        @Override
        public Integer getStereoScore(int Key) {
            return null;
        }

        @Override
        public boolean isStereoMisMatch() {
            return false;
        }

        @Override
        public boolean isSubgraph() {
            return false;
        }

        @Override
        public double getTanimotoSimilarity() throws IOException {
            return 0.0;
        }

        @Override
        public double getEuclideanDistance() throws IOException {
            return 0.0;
        }

        @Override
        public List<Map<IAtom, IAtom>> getAllAtomMapping() {
            return null;
        }

        @Override
        public List<Map<Integer, Integer>> getAllMapping() {
            return null;
        }

        @Override
        public Map<IAtom, IAtom> getFirstAtomMapping() {
            return null;
        }

        @Override
        public Map<Integer, Integer> getFirstMapping() {
            return null;
        }

        @Override
        public double getBondSensitiveTimeOut() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setBondSensitiveTimeOut(double bondSensitiveTimeOut) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public double getBondInSensitiveTimeOut() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setBondInSensitiveTimeOut(double bondInSensitiveTimeOut) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
