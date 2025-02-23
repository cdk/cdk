/**
JnaInchi.java * JNA-InChI - Library for calling InChI from Java
 * Copyright © 2018 Daniel Lowe
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dan2097.jnainchi;

public enum InchiFlag {

  /** Both ends of wedge point to stereocenters [compatible with standard InChI]*/
  NEWPSOFF,
  
  /** All hydrogens in input structure are explicit [compatible with standard InChI]*/
  DoNotAddH,
  
  /** Ignore stereo [compatible with standard InChI]*/
  SNon,
  
  /** Use relative stereo*/
  SRel,
  
  /** Use racemic stereo*/
  SRac,
  
  /** Use Chiral Flag in MOL/SD file record: if On – use Absolute stereo, Off – use Relative stereo*/
  SUCF,
  
  /** Set chiral flag ON*/
  ChiralFlagON,
  
  /** Set chiral flag OFF*/
  ChiralFlagOFF,
  
  /** Allows input of molecules up to 32767 atoms [Produces 'InChI=1B' indicating beta status of resulting identifiers]*/
  LargeMolecules,
  
  /** Always indicate unknown/undefined stereo*/
  SUU,
  
  /** Stereo labels for "unknown" and "undefined" are different, 'u' and '?', resp.*/
  SLUUD,
  
  /** Include Fixed H layer*/
  FixedH,
  
  /** Include reconnected metals results*/
  RecMet,
  
  /** Account for keto-enol tautomerism (experimental)*/
  KET,
  
  /** Account for 1,5-tautomerism (experimental)*/
  OneFiveT,
  
  /** Omit auxiliary information*/
  AuxNone,
  
  /** Warn and produce empty InChI for empty structure
   * NOTE: This option doesn't currently work due to an InChI library bug*/
  WarnOnEmptyStructure,
  
  /** Save custom InChI creation options (non-standard InChI)*/
  SaveOpt,
  
  /** Suppress all warning messages*/
  NoWarnings,
  
  /** Relax criteria of ambiguous drawing for in-ring tetrahedral stereo*/
  LooseTSACheck,
  
  /** Allow processing of polymers (experimental)*/
  Polymers,
  
  /** Allow processing of polymers (experimental, legacy mode of v. 1.05)*/
  Polymers105,
  
  /** Remove repeats within constitutional repeating units (CRU/SRU)*/
  FoldCRU,
  
  /** Disable polymer CRU frame shift*/
  NoFrameShift,
  
  /** Disable polymer CRU frame shift and folding*/
  NoEdits,
  
  /** Allow non-polymer-related Zz atoms (pseudo element placeholders)*/
  NPZz,
  
  /** Allow stereo at atoms connected to Zz*/
  SAtZz,
  
  /** Use absolute stereo (this is the default, so this flag is typically redundant) */
  SAbs,
  
  /** Output an empty InChI ("InChI=1//" or "InChI=1S//") on error */
  OutErrInChI;
  
  @Override
  public String toString() {
    if (this == OneFiveT) {
      //Java doesn't allow enums to start with digits
      return "15T";
    }
    return super.toString();
  }
  
  public static InchiFlag getFlagFromName(String name) {
    for (InchiFlag item : values())
      if (item.toString().equalsIgnoreCase(name))
        return item;
    return null;
  }

}
