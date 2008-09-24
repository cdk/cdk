/* $Revision: 11555 $ $Author: egonw $ $Date: 2008-07-12 20:31:17 +0200 (Sat, 12 Jul 2008) $
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.modeling.builder3d;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.nonotify.NNAtomContainer;

/**
 * @cdk.module test-builder3d
 *
 * @author      chhoppe
 * @author      Christoph Steinbeck
 * @cdk.created 2004-11-04
 */
public class TemplateHandler3DTest extends NewCDKTestCase {
		
	@Test
	public void testGetInstance() throws Exception {
		TemplateHandler3D th3d = TemplateHandler3D.getInstance();
		// need to trigger a load of the templates
		th3d.mapTemplates(new NNAtomContainer(), 0);
		Assert.assertEquals(10751, th3d.getTemplateCount());
	}
	
	@Test
	public void testFingerprints() throws Exception{
		BitSet[] bsmb=new BitSet[10];
		bsmb[0]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 142, 144, 184, 188, 204, 400, 478, 524, 534, 553, 562, 567, 597, 626, 644, 686, 696, 710, 741, 767, 775, 786, 823, 898, 925, 965, 987, 1000}", "\t ;{, }"));
		bsmb[1]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 142, 144, 184, 188, 204, 400, 478, 524, 534, 553, 562, 567, 597, 626, 644, 686, 696, 710, 741, 767, 775, 786, 823, 898, 925, 965, 987, 1000}", "\t ;{, }"));
		bsmb[2]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{27, 29, 134, 137, 148, 164, 180, 184, 212, 273, 278, 305, 330, 361, 379, 400, 409, 441, 486, 491, 504, 524, 547, 562, 567, 643, 648, 686, 688, 694, 696, 710, 741, 743, 775, 786, 815, 823, 840, 848, 852, 855, 857, 864, 866, 898, 916, 925}", "\t ;{, }"));
		bsmb[3]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 184, 553, 644, 741, 775, 823, 898, 1000}", "\t ;{, }"));
		bsmb[4]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{27, 29, 32, 48, 136, 142, 144, 148, 180, 184, 188, 204, 212, 273, 278, 305, 330, 361, 366, 379, 400, 430, 441, 478, 486, 524, 534, 547, 553, 562, 567, 597, 626, 643, 644, 645, 648, 686, 688, 694, 696, 710, 741, 743, 767, 775, 786, 815, 823, 840, 852, 855, 857, 866, 898, 906, 916, 925, 965, 974, 987, 1000, 1005}", "\t ;{, }"));
		bsmb[5]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 184, 553, 644, 741, 775, 823, 898, 1000}", "\t ;{, }"));
		bsmb[6]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 184, 553, 644, 741, 775, 823, 898, 1000}", "\t ;{, }"));
		bsmb[7]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 184, 644, 741, 775, 823, 898, 1000}", "\t ;{, }"));
		bsmb[8]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{27, 29, 134, 137, 148, 164, 180, 184, 212, 273, 278, 305, 330, 361, 379, 400, 409, 441, 486, 491, 504, 524, 547, 562, 567, 643, 648, 686, 688, 694, 696, 710, 741, 743, 775, 786, 815, 823, 840, 848, 852, 855, 857, 864, 866, 898, 916, 925}", "\t ;{, }"));
		bsmb[9]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 184, 553, 644, 741, 775, 823, 898, 1000}", "\t ;{, }"));		
        
		String filename = "data/mdl/fingerprints_from_modelbuilder3d.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        List data=new TemplateExtractor().makeFingerprintsFromSdf(true,false, new HashMap(), new BufferedReader ( new InputStreamReader ( ins ) ),10);
        for(int i=0;i<data.size();i++){
        	BitSet bs=(BitSet) data.get(i);
        	Assert.assertEquals(bs,bsmb[i]);
        }		
	}

}
