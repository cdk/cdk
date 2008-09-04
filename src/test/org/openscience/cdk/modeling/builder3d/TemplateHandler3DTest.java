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
		bsmb[0]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 40, 110, 138, 144, 184, 202, 204, 248, 510, 553, 571, 644, 710, 727, 741, 746, 775, 780, 792, 823, 863, 898, 917, 925, 957, 978, 1000, 1005};", "\t ;{, }"));
		bsmb[1]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 40, 110, 138, 144, 184, 202, 204, 248, 510, 553, 571, 644, 710, 727, 741, 746, 775, 780, 792, 823, 863, 898, 917, 925, 957, 978, 1000, 1005}", "\t ;{, }"));
		bsmb[2]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 40, 70, 137, 156, 184, 203, 220, 222, 248, 361, 406, 458, 461, 491, 518, 520, 542, 545, 547, 563, 571, 643, 648, 688, 694, 698, 710, 727, 741, 743, 757, 775, 792, 797, 864, 866, 898, 916, 925, 952, 954, 963, 978, 990, 1005, 1006}", "\t ;{, }"));
		bsmb[3]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 184, 553, 644, 741, 775, 823, 898, 1000}", "\t ;{, }"));
		bsmb[4]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 40, 53, 70, 109, 110, 138, 140, 144, 156, 184, 202, 203, 204, 220, 222, 248, 361, 429, 458, 472, 486, 510, 518, 520, 542, 547, 553, 571, 579, 643, 644, 645, 648, 688, 694, 698, 710, 727, 741, 743, 746, 757, 775, 780, 792, 823, 863, 866, 898, 916, 917, 924, 925, 952, 954, 957, 963, 978, 982, 990, 1000, 1005, 1006}", "\t ;{, }"));
		bsmb[5]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 184, 553, 644, 741, 775, 823, 898, 1000}", "\t ;{, }"));
		bsmb[6]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 184, 553, 644, 741, 775, 823, 898, 1000}", "\t ;{, }"));
		bsmb[7]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 184, 644, 741, 775, 823, 898, 1000}", "\t ;{, }"));
		bsmb[8]=(BitSet) TemplateHandler3D.getBitSetFromFile(new StringTokenizer("{29, 40, 70, 137, 156, 184, 203, 220, 222, 248, 361, 406, 458, 461, 491, 518, 520, 542, 545, 547, 563, 571, 643, 648, 688, 694, 698, 710, 727, 741, 743, 757, 775, 792, 797, 864, 866, 898, 916, 925, 952, 954, 963, 978, 990, 1005, 1006}", "\t ;{, }"));
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
