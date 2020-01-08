/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.sgroup;

/**
 * Keys for indexing common Sgroup attributes.
 */
public enum SgroupKey {
    CtabType,
    CtabSubType,
    /**
     * Not to be confused with the subscript key
     * this is Sgroup label not the bracket label (e.g. 'n').
     */
    CtabLabel,
    CtabExpansion,
    CtabCorrespondence,
    CtabAbbreviationVector,
    CtabAbbreviationAttachPoint,
    CtabDisplayInfo,
    CtabSubScript,
    CtabConnectivity,
    CtabBracket,
    CtabBracketStyle,
    CtabClass,
    CtabParentAtomList,
    CtabComponentNumber,
    // Data Sgroups
    DataFieldName,
    DataFieldFormat,
    DataFieldUnits,
    DataDisplayInformation,
    Data;
}
