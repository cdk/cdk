/*
 * Copyright (C) 2024 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.aromaticity;

/**
 * Aromatic atom types organized by element and degree, for example
 * B2 => "Boron Degree 2" <code>SMILES: *B=*</code>.
 */
enum AromaticType {
    UNKNOWN,
    /** {@code *B=* } */
    B2,
    /** {@code *B(*)* } */
    B3,
    /** {@code *[C-]=* } */
    C2_MINUS,
    /** {@code *[C+]=* } */
    C2_PLUS,
    /** {@code *C(*)@=* } */
    C3,
    /** {@code *C(*)!@=* } */
    C3_EXO,
    /** {@code *C(*)!@=[!#6] } */
    C3_ENEG_EXO,
    /** {@code *[C-](*)* } */
    C3_MINUS,
    /** {@code *[C+](*)* } */
    C3_PLUS,
    /** {@code *N=* } */
    N2,
    /** {@code *[N-]* } */
    N2_MINUS,
    /** {@code *N(*)* } */
    N3,
    /** {@code *N(=O)=* } */
    N3_OXIDE,
    /** {@code *[N+]([O-])=* } */
    N3_OXIDE_PLUS,
    /** {@code *[N+](*)=* } */
    N3_PLUS,
    /** {@code *O* } */
    O2,
    /** {@code *[O+]=* } */
    O2_PLUS,
    /** {@code *P=* } */
    P2,
    /** {@code *[P-]* } */
    P2_MINUS,
    /** {@code *P(*)* } */
    P3,
    /** {@code *P(=@*)(*)* } */
    P4,
    /** {@code *P(=O)=* } */
    P3_OXIDE,
    /** {@code *[P+]([O-])=* } */
    P3_OXIDE_PLUS,
    /** {@code *[P+](*)=* } */
    P3_PLUS,
    /** {@code *S* } */
    S2,
    /** {@code *@=S=@* } */
    S2_CUML,
    /** {@code *S(*)@=* } */
    S3,
    /** {@code *[S+]@=* } */
    S2_PLUS,
    /** {@code *[S+](*)* } */
    S3_PLUS,
    /** {@code *S(=O)* } */
    S3_OXIDE,
    /** {@code *[S+]([O-])* } */
    S3_OXIDE_PLUS,
    /** {@code *[Si-]=* } */
    Si2_MINUS,
    /** {@code *[Si+]=* } */
    Si2_PLUS,
    /** {@code *[Si](*)@=* } */
    Si3,
    /** {@code *[Si](*)!@=* } */
    Si3_EXO,
    /** {@code *[Si-](*)* } */
    Si3_MINUS,
    /** {@code *[Si+](*)* } */
    Si3_PLUS,
    /** {@code *[Se]* } */
    Se2,
    /** {@code *[Se+]=* } */
    Se2_PLUS,
    /** {@code *[Se](=@*)* } */
    Se3,
    /** {@code *[Se](=O)* } */
    Se3_OXIDE,
    /** {@code *[Se+]([O-])* } */
    Se3_OXIDE_PLUS,
    /** {@code *[As]=* } */
    As2,
    /** {@code *[As-]* } */
    As2_MINUS,
    /** {@code *[As](*)* } */
    As3,
    /** {@code *[As+](=*)* } */
    As3_PLUS,
    /** {@code *[Te]* } */
    Te2,
    /** {@code *[Te+]=* } */
    Te2_PLUS
}
