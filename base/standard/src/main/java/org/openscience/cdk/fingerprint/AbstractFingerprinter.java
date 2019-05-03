/*
 * Copyright (c) 2017 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk.fingerprint;

import org.openscience.cdk.CDK;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractFingerprinter implements IFingerprinter {

    /**
     * Base classes should override this method to report the parameters they
     * are configured with.
     *
     * @return The key=value pairs of configured parameters
     */
    protected List<Map.Entry<String,String>> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public final String getVersionDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("CDK-")
          .append(getClass().getSimpleName())
          .append("/")
          .append(CDK.getVersion()); // could version fingerprints separetely
        for (Map.Entry<String,String> param : getParameters()) {
            sb.append(' ').append(param.getKey()).append('=').append(param.getValue());
        }
        return sb.toString();
    }

    /** {@inheritDoc} */
    @Override
    public BitSet getFingerprint(IAtomContainer mol) throws CDKException {
        return getBitFingerprint(mol).asBitSet();
    }
}
