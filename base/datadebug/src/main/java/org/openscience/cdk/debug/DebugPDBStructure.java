/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.debug;

import org.openscience.cdk.protein.data.PDBStructure;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Debugging data class.
 *
 * @author     Miguel Rojas
 * @cdk.module datadebug
 * @cdk.githash
 */
public class DebugPDBStructure extends PDBStructure {

    private static final long serialVersionUID = 1934748703085969097L;

    ILoggingTool              logger           = LoggingToolFactory.createLoggingTool(DebugAtomContainer.class);

    /** {@inheritDoc} */
    @Override
    public Character getEndChainID() {
        logger.debug("Getting End Chain ID: ", super.getEndChainID());
        return super.getEndChainID();
    }

    /** {@inheritDoc} */
    @Override
    public void setEndChainID(Character endChainID) {
        logger.debug("Setting End Chain ID: ", endChainID);
        super.setEndChainID(endChainID);
    }

    /** {@inheritDoc} */
    @Override
    public Character getEndInsertionCode() {
        logger.debug("Getting End Insertion Code: ", super.getEndInsertionCode());
        return super.getEndInsertionCode();
    }

    /** {@inheritDoc} */
    @Override
    public void setEndInsertionCode(Character endInsertionCode) {
        logger.debug("Setting End Insertion Code: ", endInsertionCode);
        super.setEndInsertionCode(endInsertionCode);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getEndSequenceNumber() {
        logger.debug("Getting End Sequence Number: ", super.getEndSequenceNumber());
        return super.getEndSequenceNumber();
    }

    /** {@inheritDoc} */
    @Override
    public void setEndSequenceNumber(Integer endSequenceNumber) {
        logger.debug("Setting End Sequence Number: ", endSequenceNumber);
        super.setEndSequenceNumber(endSequenceNumber);
    }

    /** {@inheritDoc} */
    @Override
    public Character getStartChainID() {
        logger.debug("Getting Start Chain ID: ", super.getStartChainID());
        return super.getStartChainID();
    }

    /** {@inheritDoc} */
    @Override
    public void setStartChainID(Character startChainID) {
        logger.debug("Setting Start Chain ID: ", startChainID);
        super.setStartChainID(startChainID);
    }

    /** {@inheritDoc} */
    @Override
    public Character getStartInsertionCode() {
        logger.debug("Getting Start Insertion Code: ", super.getStartInsertionCode());
        return super.getStartInsertionCode();
    }

    /** {@inheritDoc} */
    @Override
    public void setStartInsertionCode(Character startInsertionCode) {
        logger.debug("Setting Star tInsertion Code: ", startInsertionCode);
        super.setStartInsertionCode(startInsertionCode);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getStartSequenceNumber() {
        logger.debug("Getting Start Sequence Number: ", super.getStartSequenceNumber());
        return super.getStartSequenceNumber();
    }

    /** {@inheritDoc} */
    @Override
    public void setStartSequenceNumber(Integer startSequenceNumber) {
        logger.debug("Setting Start Sequence Number: ", startSequenceNumber);
        super.setStartSequenceNumber(startSequenceNumber);
    }

    /** {@inheritDoc} */
    @Override
    public String getStructureType() {
        logger.debug("Getting Structure Type: ", super.getStructureType());
        return super.getStructureType();
    }

    /** {@inheritDoc} */
    @Override
    public void setStructureType(String structureType) {
        logger.debug("Setting Structure Type: ", structureType);
        super.setStructureType(structureType);
    }

}
