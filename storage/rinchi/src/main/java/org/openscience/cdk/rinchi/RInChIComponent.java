/* Copyright (C) 2024 Beilstein-Institute
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
package org.openscience.cdk.rinchi;

import io.github.dan2097.jnainchi.InchiStatus;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;

/**
 * Class to represent a component of a RInChI (Reaction International Chemical Identifier).
 * It encapsulates information related to the InChI string, auxiliary information, and the InChI key.
 *
 * <p>This class is designed to handle the potential absence of structural data,
 * providing methods to retrieve InChI-related information while reporting status messages.</p>
 *
 * @author Felix Bänsch
 */
class RInChIComponent extends StatusMessagesOutput {

    private boolean isNoStructure;
    private String inchi;
    private String auxInfo;
    private String inchiKey;

    /**
     * Constructs an {@code RInChIComponent} using the specified {@link InChIGenerator}.
     *
     * <p>This constructor initializes the component based on the generator's status and output.
     * If the generator is null, has an error status, or produces an empty InChI,
     * the component is marked as having no structure.</p>
     *
     * @param gen the {@link InChIGenerator} to generate InChI information from
     */
    protected RInChIComponent(InChIGenerator gen){
        if (gen == null || gen.getStatus() == InchiStatus.ERROR || gen.getInchi() == null || gen.getInchi().isEmpty()) {
            this.isNoStructure = true;
            return;
        }
        this.inchi = gen.getInchi();
        this.auxInfo = gen.getAuxInfo();
        try {
            this.inchiKey = gen.getInchiKey();
        } catch (CDKException e) {
            addMessage(e.getMessage(), Status.ERROR);
        }
    }

    /**
     * Checks if the component has no structure.
     *
     * @return {@code true} if this component represents no structure; {@code false} otherwise
     */
    protected boolean isNoStructure() {
        return this.isNoStructure;
    }

    /**
     * Retrieves the InChI string associated with this component.
     *
     * @return the InChI string, or {@code null} if there is no structure
     */
    protected String getInchi() {
        return this.inchi;
    }

    /**
     * Retrieves auxiliary information associated with the InChI component.
     *
     * @return the auxiliary information, or {@code null} if there is no structure
     */
    protected String getAuxInfo() {
        return this.auxInfo;
    }

    /**
     * Retrieves the InChI key associated with this component.
     *
     * @return the InChI key, or {@code null} if there is no structure
     */
    protected String getInchiKey() {
        return this.inchiKey;
    }
}
