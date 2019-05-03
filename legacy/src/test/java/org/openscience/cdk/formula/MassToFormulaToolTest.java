/* Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
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
package org.openscience.cdk.formula;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.formula.rules.ChargeRule;
import org.openscience.cdk.formula.rules.ElementRule;
import org.openscience.cdk.formula.rules.IRule;
import org.openscience.cdk.formula.rules.IsotopePatternRule;
import org.openscience.cdk.formula.rules.ToleranceRangeRule;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * Checks the functionality of the MassToFormulaTool.
 *
 * @cdk.module test-formula
 */
@Deprecated
public class MassToFormulaToolTest extends CDKTestCase {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    private IsotopeFactory                  ifac;

    /**
     *  Constructor for the MassToFormulaToolTest object.
     */
    public MassToFormulaToolTest() {

        super();
        try {
            ifac = Isotopes.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testMassToFormulaTool_IChemObjectBuilder() {

        Assert.assertNotNull(new MassToFormulaTool(builder));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testMass_0Null() {

        Assert.assertNull(new MassToFormulaTool(builder).generate(0.0));
    }

    /**
     * A unit test suite for JUnit
     *
     * @return    The test suite
     */
    @Test
    public void testMass_NegativeNULL() {

        Assert.assertNull(new MassToFormulaTool(builder).generate(-10.0));
    }

    /**
     * A unit test suite for JUnit
     *
     * @return    The test suite
     */
    @Test
    public void testGetRestrictions() {

        Assert.assertNotNull(new MassToFormulaTool(builder).getRestrictions());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetDefaultRestrictions() {
        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> rules = mfTool.getRestrictions();
        Assert.assertNotNull(rules);

        Assert.assertEquals(3, rules.size());

        ElementRule elemR = (ElementRule) rules.get(0);
        Assert.assertEquals(1, elemR.getParameters().length);
        Object[] objects = elemR.getParameters();
        MolecularFormulaRange mfRange = (MolecularFormulaRange) objects[0];
        Assert.assertEquals(4, mfRange.getIsotopeCount());
        Assert.assertEquals(15, mfRange.getIsotopeCountMax(ifac.getMajorIsotope("C")));
        Assert.assertEquals(0, mfRange.getIsotopeCountMin(ifac.getMajorIsotope("C")));
        Assert.assertEquals(15, mfRange.getIsotopeCountMax(ifac.getMajorIsotope("H")));
        Assert.assertEquals(0, mfRange.getIsotopeCountMin(ifac.getMajorIsotope("H")));
        Assert.assertEquals(15, mfRange.getIsotopeCountMax(ifac.getMajorIsotope("N")));
        Assert.assertEquals(0, mfRange.getIsotopeCountMin(ifac.getMajorIsotope("N")));
        Assert.assertEquals(15, mfRange.getIsotopeCountMax(ifac.getMajorIsotope("O")));
        Assert.assertEquals(0, mfRange.getIsotopeCountMin(ifac.getMajorIsotope("O")));

        ChargeRule chargeR = (ChargeRule) rules.get(1);
        Assert.assertEquals(1, chargeR.getParameters().length);
        objects = chargeR.getParameters();
        double charge = (Double) objects[0];
        Assert.assertEquals(0.0, charge, 0.001);

        ToleranceRangeRule toleranceR = (ToleranceRangeRule) rules.get(2);
        Assert.assertEquals(2, toleranceR.getParameters().length);
        objects = toleranceR.getParameters();
        double mass = (Double) objects[0];
        Assert.assertEquals(0.0, mass, 0.001);
        double tolerance = (Double) objects[1];
        Assert.assertEquals(0.05, tolerance, 0.001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetRestrictions_List() throws Exception {

        MassToFormulaTool mfTool = new MassToFormulaTool(builder);
        List<IRule> rules = mfTool.getRestrictions();
        Assert.assertNotNull(rules);

        Assert.assertEquals(3, rules.size());

        // put one rule more.
        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule4 = new IsotopePatternRule();
        myRules.add(rule4);

        mfTool.setRestrictions(myRules);

        Assert.assertEquals(4, rules.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetRestrictionsElements() throws Exception {

        MassToFormulaTool mfTool = new MassToFormulaTool(builder);
        List<IRule> rules = mfTool.getRestrictions();
        Assert.assertNotNull(rules);

        Assert.assertEquals(3, rules.size());

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(builder.newInstance(IIsotope.class, "C"), 1, 10);
        mfRange.addIsotope(builder.newInstance(IIsotope.class, "H"), 1, 10);
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);
        mfTool.setRestrictions(myRules);

        //-----------------------------------------------------------------

        rules = mfTool.getRestrictions();
        Assert.assertNotNull(rules);

        Assert.assertEquals(3, rules.size());

        Iterator<IRule> ruleIt = rules.iterator();
        while (ruleIt.hasNext()) {
            IRule ruleA = ruleIt.next();
            if (ruleA instanceof ElementRule) {
                Assert.assertEquals(1, ruleA.getParameters().length);
                Object[] objects = ruleA.getParameters();
                MolecularFormulaRange mfRange2 = (MolecularFormulaRange) objects[0];
                Assert.assertEquals(2, mfRange2.getIsotopeCount());
            }
        }

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetRestrictionsCharge() throws Exception {

        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule2 = new ChargeRule();
        Object[] params = new Object[1];
        params[0] = -1.0;
        rule2.setParameters(params);
        myRules.add(rule2);

        mfTool.setRestrictions(myRules);

        //-----------------------------------------------------------------

        List<IRule> rules = mfTool.getRestrictions();
        Assert.assertNotNull(rules);

        Iterator<IRule> ruleIt = rules.iterator();
        while (ruleIt.hasNext()) {
            IRule ruleA = ruleIt.next();
            if (ruleA instanceof ChargeRule) {
                Assert.assertEquals(1, ruleA.getParameters().length);
                Object[] objects = ruleA.getParameters();
                double charge2 = (Double) objects[0];
                Assert.assertEquals(-1.0, charge2, 0.001);
            }
        }

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetRestrictionsTolerance() throws Exception {

        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule3 = new ToleranceRangeRule();
        Object[] params = new Object[2];
        params[0] = 133.0;
        params[1] = 0.00001;
        rule3.setParameters(params);
        myRules.add(rule3);

        mfTool.setRestrictions(myRules);

        //-----------------------------------------------------------------

        List<IRule> rules = mfTool.getRestrictions();
        Assert.assertNotNull(rules);

        Iterator<IRule> ruleIt = rules.iterator();
        while (ruleIt.hasNext()) {
            IRule ruleA = ruleIt.next();
            if (ruleA instanceof ToleranceRangeRule) {
                Assert.assertEquals(2, ruleA.getParameters().length);
                Object[] objects = ruleA.getParameters();
                double mass2 = (Double) objects[0];
                Assert.assertEquals(133.0, mass2, 0.001);
                double tolerance2 = (Double) objects[1];
                Assert.assertEquals(0.00001, tolerance2, 0.001);
            }
        }

    }

    /**
    * A unit test suite for JUnit
    *
    * @return    The test suite
    */
    @Test
    public void testGenerate_double() {

        Assert.assertNotNull(new MassToFormulaTool(builder).generate(44.0032));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     * @cdk.inchi InChI=1/C5H12N2O2/c6-3-1-2-4(7)5(8)9/h4H,1-3,6-7H2,(H,8,9)
     */
    @Test
    public void testValidation_Orthinine() {

        IIsotope carb = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope oxy = ifac.getMajorIsotope("O");
        IIsotope nit = ifac.getMajorIsotope("N");

        // FIXME: MF: this looks like two tests... is that really so?
        // FIXME: MF: M: It is only one. But it generates 8 MF and I put two to check
        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(carb, 3);
        mf1.addIsotope(h, 11);
        mf1.addIsotope(oxy, 1);
        mf1.addIsotope(nit, 5);

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(carb, 5);
        mf2.addIsotope(h, 13);
        mf2.addIsotope(oxy, 2);
        mf2.addIsotope(nit, 2);

        IMolecularFormulaSet mfSet = new MassToFormulaTool(builder).generate(133.0968);

        Assert.assertEquals(37, mfSet.size());

        Assert.assertEquals(MolecularFormulaManipulator.getAtomCount(mf1),
                MolecularFormulaManipulator.getAtomCount(mfSet.getMolecularFormula(0)));
        Assert.assertEquals(mf1.getIsotopeCount(), mfSet.getMolecularFormula(0).getIsotopeCount());
        Assert.assertEquals(mf1.getIsotopeCount(carb), mfSet.getMolecularFormula(0).getIsotopeCount(carb));
        Assert.assertEquals(mf1.getIsotopeCount(ifac.getMajorIsotope("N")), mfSet.getMolecularFormula(0)
                .getIsotopeCount(ifac.getMajorIsotope("N")));

        Assert.assertEquals(MolecularFormulaManipulator.getAtomCount(mf2),
                MolecularFormulaManipulator.getAtomCount(mfSet.getMolecularFormula(1)));
        Assert.assertEquals(mf2.getIsotopeCount(), mfSet.getMolecularFormula(1).getIsotopeCount());
        Assert.assertEquals(mf2.getIsotopeCount(ifac.getMajorIsotope("C")), mfSet.getMolecularFormula(1)
                .getIsotopeCount(ifac.getMajorIsotope("C")));
        Assert.assertEquals(mf2.getIsotopeCount(ifac.getMajorIsotope("N")), mfSet.getMolecularFormula(1)
                .getIsotopeCount(ifac.getMajorIsotope("N")));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     * @cdk.inchi InChI=1/C5H12N2O2/c6-3-1-2-4(7)5(8)9/h4H,1-3,6-7H2,(H,8,9)
     */
    @Test
    public void testValidation_Orthinine_Restrictions() throws Exception {

        IIsotope carb = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");

        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(carb, 10);
        mf1.addIsotope(h, 13);

        MassToFormulaTool mfTool = new MassToFormulaTool(builder);
        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(ifac.getMajorIsotope("C"), 10, 20);
        mfRange.addIsotope(ifac.getMajorIsotope("H"), 0, 20);
        mfRange.addIsotope(ifac.getMajorIsotope("O"), 0, 1);
        mfRange.addIsotope(ifac.getMajorIsotope("N"), 0, 1);
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);
        mfTool.setRestrictions(myRules);

        IMolecularFormulaSet mfSet = mfTool.generate(133.0968);

        Assert.assertEquals(1, mfSet.size());

        Assert.assertEquals(MolecularFormulaManipulator.getAtomCount(mf1),
                MolecularFormulaManipulator.getAtomCount(mfSet.getMolecularFormula(0)));
        Assert.assertEquals(mf1.getIsotopeCount(), mfSet.getMolecularFormula(0).getIsotopeCount());
        Assert.assertEquals(mf1.getIsotopeCount(ifac.getMajorIsotope("C")), mfSet.getMolecularFormula(0)
                .getIsotopeCount(ifac.getMajorIsotope("C")));
        Assert.assertEquals(mf1.getIsotopeCount(ifac.getMajorIsotope("N")), mfSet.getMolecularFormula(0)
                .getIsotopeCount(ifac.getMajorIsotope("N")));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     * @cdk.inchi InChI=1/C6H14N4O2/c7-4(5(11)12)2-1-3-10-6(8)9/h4H,1-3,7H2,(H,11,12)(H4,8,9,10)/t4-/m0/s1/f/h11H,8-9H2
     */
    @Test
    public void testValidation_Arginine_Restrictions() throws Exception {

        IIsotope carb = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope nitro = ifac.getMajorIsotope("N");
        IIsotope oxy = ifac.getMajorIsotope("O");

        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(carb, 6);
        mf1.addIsotope(h, 15);
        mf1.addIsotope(nitro, 1);
        mf1.addIsotope(oxy, 2);

        MassToFormulaTool mfTool = new MassToFormulaTool(builder);
        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(carb, 1, 50);
        mfRange.addIsotope(h, 0, 50);
        mfRange.addIsotope(nitro, 0, 50);
        mfRange.addIsotope(oxy, 0, 50);
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);
        mfTool.setRestrictions(myRules);

        IMolecularFormulaSet mfSet = mfTool.generate(133.0968);

        Assert.assertEquals(42, mfSet.size());

        Assert.assertEquals(MolecularFormulaManipulator.getAtomCount(mf1),
                MolecularFormulaManipulator.getAtomCount(mfSet.getMolecularFormula(13)));
        Assert.assertEquals(mf1.getIsotopeCount(), mfSet.getMolecularFormula(13).getIsotopeCount());
        Assert.assertEquals(mf1.getIsotopeCount(ifac.getMajorIsotope("C")), mfSet.getMolecularFormula(13)
                .getIsotopeCount(ifac.getMajorIsotope("C")));
        Assert.assertEquals(mf1.getIsotopeCount(ifac.getMajorIsotope("N")), mfSet.getMolecularFormula(13)
                .getIsotopeCount(ifac.getMajorIsotope("N")));

    }

    /**
     * A unit test suite for JUnit. Pentacarboxyporphyrin, Mass=698.2588. MF=C37H38N4O10
     *
     * @return    The test suite
     * @cdk.inchi InChI=
     */
    @Test
    public void testMiddleMass() throws Exception {
        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(ifac.getMajorIsotope("C"), 0, 40);
        mfRange.addIsotope(ifac.getMajorIsotope("H"), 0, 40);
        mfRange.addIsotope(ifac.getMajorIsotope("O"), 0, 10);
        mfRange.addIsotope(ifac.getMajorIsotope("N"), 0, 20);
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);

        IRule rule3 = new ToleranceRangeRule();
        params = new Object[2];
        params[0] = 0.0;
        params[1] = 0.0001;
        rule3.setParameters(params);
        myRules.add(rule3);

        mfTool.setRestrictions(myRules);

        IMolecularFormulaSet mfSet = mfTool.generate(698.2588); //C37H38N4O10

        Assert.assertNotNull(mfSet);
        Assert.assertSame(3, mfSet.size());

        IMolecularFormula mf1 = new MolecularFormula();
        IIsotope carb = ifac.getMajorIsotope("C");
        mf1.addIsotope(carb, 37);
        IIsotope h = ifac.getMajorIsotope("H");
        mf1.addIsotope(h, 38);
        IIsotope oxy = ifac.getMajorIsotope("O");
        mf1.addIsotope(oxy, 10);
        IIsotope nit = ifac.getMajorIsotope("N");
        mf1.addIsotope(nit, 4);

        Assert.assertEquals(MolecularFormulaManipulator.getString(mf1),
                MolecularFormulaManipulator.getString(mfSet.getMolecularFormula(0)));

    }

    /**
     * A unit test suite for JUnit. Test takes approximately 2 min.
     *
     * @return    The test suite
     * @cdk.inchi InChI=1/C111H166N8O184S32/c1-18(121)113-35-52(43(281-313(172,173)174)27(249-96(35)144)9-241-304(145,146)147)258-105-81(297-329(220,221)222)67(290-322(199,200)201)60(74(273-105)89(130)131)266-97-36(112-17-120)53(44(282-314(175,176)177)28(251-97)10-242-305(148,149)150)259-106-82(298-330(223,224)225)68(291-323(202,203)204)61(75(274-106)90(132)133)268-99-38(115-20(3)123)55(46(284-316(181,182)183)30(253-99)12-244-307(154,155)156)261-108-84(300-332(229,230)231)70(293-325(208,209)210)63(77(276-108)92(136)137)270-101-40(117-22(5)125)57(48(286-318(187,188)189)32(255-101)14-246-309(160,161)162)263-110-86(302-334(235,236)237)72(295-327(214,215)216)65(79(278-110)94(140)141)272-103-42(119-24(7)127)59(50(288-320(193,194)195)34(257-103)16-248-311(166,167)168)265-111-87(303-335(238,239)240)73(296-328(217,218)219)66(80(279-111)95(142)143)271-102-41(118-23(6)126)58(49(287-319(190,191)192)33(256-102)15-247-310(163,164)165)264-109-85(301-333(232,233)234)71(294-326(211,212)213)64(78(277-109)93(138)139)269-100-39(116-21(4)124)56(47(285-317(184,185)186)31(254-100)13-245-308(157,158)159)262-107-83(299-331(226,227)228)69(292-324(205,206)207)62(76(275-107)91(134)135)267-98-37(114-19(2)122)54(45(283-315(178,179)180)29(252-98)11-243-306(151,152)153)260-104-51(289-321(196,197)198)25(280-312(169,170)171)8-26(250-104)88(128)129/h8,17,25,27-87,96-111,144H,9-16H2,1-7H3,(H,112,120)(H,113,121)(H,114,122)(H,115,123)(H,116,124)(H,117,125)(H,118,126)(H,119,127)(H,128,129)(H,130,131)(H,132,133)(H,134,135)(H,136,137)(H,138,139)(H,140,141)(H,142,143)(H,145,146,147)(H,148,149,150)(H,151,152,153)(H,154,155,156)(H,157,158,159)(H,160,161,162)(H,163,164,165)(H,166,167,168)(H,169,170,171)(H,172,173,174)(H,175,176,177)(H,178,179,180)(H,181,182,183)(H,184,185,186)(H,187,188,189)(H,190,191,192)(H,193,194,195)(H,196,197,198)(H,199,200,201)(H,202,203,204)(H,205,206,207)(H,208,209,210)(H,211,212,213)(H,214,215,216)(H,217,218,219)(H,220,221,222)(H,223,224,225)(H,226,227,228)(H,229,230,231)(H,232,233,234)(H,235,236,237)(H,238,239,240)/t25-,27+,28+,29+,30+,31+,32+,33+,34+,35+,36+,37+,38+,39+,40+,41+,42+,43+,44+,45+,46+,47+,48+,49+,50+,51+,52+,53+,54+,55+,56+,57+,58+,59+,60-,61-,62-,63-,64-,65-,66-,67-,68-,69-,70-,71-,72-,73-,74-,75-,76-,77-,78-,79-,80-,81+,82+,83+,84+,85+,86+,87+,96?,97-,98-,99-,100-,101-,102-,103-,104-,105+,106+,107+,108+,109+,110+,111+/m0/s1/f/h112-119,128,130,132,134,136,138,140,142,145,148,151,154,157,160,163,166,169,172,175,178,181,184,187,190,193,196,199,202,205,208,211,214,217,220,223,226,229,232,235,238H
     */
    @Test
    public void testHighMass() throws Exception {
        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(ifac.getMajorIsotope("C"), 0, 200);
        mfRange.addIsotope(ifac.getMajorIsotope("H"), 0, 200);
        mfRange.addIsotope(ifac.getMajorIsotope("O"), 0, 200);
        mfRange.addIsotope(ifac.getMajorIsotope("N"), 0, 50);
        mfRange.addIsotope(ifac.getMajorIsotope("S"), 0, 50);
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);
        mfTool.setRestrictions(myRules);

        //		IMolecularFormulaSet mfSet = mfTool.generate(5577.48486328125);
        //		// FIXME: MF: no hits found. InChI of at least one hit is given in the JavaDoc
        //		// FIXME: MF: M: Now it is found but is taking to much time for the calculation
        //		for(int i=0; i < mfSet.size();i++)
        //			System.out.println(MolecularFormulaManipulator.getString(mfSet.getMolecularFormula(i)));
        //		Assert.assertNotNull(mfSet);
        //		Assert.assertNotSame(0, mfSet.size());
    }

    /**
     * A unit test suite for JUnit. Test takes approximately 2 min.
     *
     * @return    The test suite
     *
     */
    @Test
    public void testFoundMF() throws Exception {
        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(ifac.getMajorIsotope("C"), 1, 50);
        mfRange.addIsotope(ifac.getMajorIsotope("H"), 1, 100);
        mfRange.addIsotope(ifac.getMajorIsotope("O"), 1, 50);
        mfRange.addIsotope(ifac.getMajorIsotope("N"), 1, 50);
        //    	mfRange.addIsotope( ifac.getMajorIsotope("S"), 0, 50);
        //    	mfRange.addIsotope( ifac.getMajorIsotope("P"), 0, 50);
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);

        ToleranceRangeRule ruleToleran = new ToleranceRangeRule();
        Object[] paramsT = new Object[2];
        paramsT[0] = 133.0;
        paramsT[1] = 0.2;
        ruleToleran.setParameters(paramsT);

        mfTool.setRestrictions(myRules);

        IMolecularFormulaSet mfSet = mfTool.generate(137.03807);
        Assert.assertEquals(24, mfSet.size());
        boolean found = false;
        for (IMolecularFormula formula : mfSet.molecularFormulas()) {
            String mf = MolecularFormulaManipulator.getString(formula);
            if (mf.equals("C4H11NO4")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue("The molecular formula C4H11NO4 should be found", found);
    }

    /**
     * A unit test suite for JUnit. Test takes approximately 2 min.
     *
     * @return    The test suite
     *
     */
    @Test
    public void testFoundMF2() throws Exception {
        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(ifac.getMajorIsotope("C"), 1, 50);
        mfRange.addIsotope(ifac.getMajorIsotope("H"), 1, 100);
        mfRange.addIsotope(ifac.getMajorIsotope("O"), 1, 50);
        mfRange.addIsotope(ifac.getMajorIsotope("N"), 1, 50);
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);

        ToleranceRangeRule ruleToleran = new ToleranceRangeRule();
        Object[] paramsT = new Object[2];
        paramsT[0] = 133.0;
        paramsT[1] = 0.001;
        ruleToleran.setParameters(paramsT);

        mfTool.setRestrictions(myRules);

        IMolecularFormulaSet mfSet = mfTool.generate(188.0711);
        Assert.assertEquals(56, mfSet.size());
        boolean found = false;
        for (IMolecularFormula formula : mfSet.molecularFormulas()) {
            String mf = MolecularFormulaManipulator.getString(formula);
            if (mf.equals("C11H10NO2")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue("The molecular formula C4H11NO4 should be found", found);
    }

    /**
     * A unit test suite for JUnit. C5H11N2O
     *
     * @return    The test suite
     */
    @Test
    public void testFragment() throws Exception {
        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(ifac.getMajorIsotope("C"), 0, 10);
        mfRange.addIsotope(ifac.getMajorIsotope("H"), 7, 15);
        mfRange.addIsotope(ifac.getMajorIsotope("O"), 0, 5);
        mfRange.addIsotope(ifac.getMajorIsotope("N"), 0, 9);
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);

        ToleranceRangeRule rule2 = new ToleranceRangeRule();
        Object[] params2 = new Object[2];
        params2[0] = 115.086589;
        params2[1] = 0.5;
        rule2.setParameters(params2);
        myRules.add(rule2);

        mfTool.setRestrictions(myRules);

        IMolecularFormulaSet mfSet = mfTool.generate(115.086589);

        Assert.assertNotNull(mfSet);
        Assert.assertNotSame(0, mfSet.size());

        IMolecularFormula mf1 = new MolecularFormula();
        IIsotope carb = ifac.getMajorIsotope("C");
        mf1.addIsotope(carb, 5);
        IIsotope h = ifac.getMajorIsotope("H");
        mf1.addIsotope(h, 11);
        IIsotope oxy = ifac.getMajorIsotope("O");
        mf1.addIsotope(oxy, 1);
        IIsotope nit = ifac.getMajorIsotope("N");
        mf1.addIsotope(nit, 2);

        Assert.assertEquals(MolecularFormulaManipulator.getString(mf1),
                MolecularFormulaManipulator.getString(mfSet.getMolecularFormula(0)));

    }

    /**
     * A unit test suite for JUnit. C8H9Cl3NO2PS
     *
     * @return    The test suite
     * @cdk.inchi InChI=1/C8H9Cl3NO2PS/c1-12-15(16,13-2)14-8-4-6(10)5(9)3-7(8)11/h3-4H,1-2H3,(H,12,16)
     */
    @Test
    public void testCompoundWith7Elements() throws Exception {
        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(ifac.getMajorIsotope("C"), 7, 9);
        mfRange.addIsotope(ifac.getMajorIsotope("H"), 8, 10);
        mfRange.addIsotope(ifac.getMajorIsotope("O"), 1, 3);
        mfRange.addIsotope(ifac.getMajorIsotope("N"), 1, 2);
        mfRange.addIsotope(ifac.getMajorIsotope("S"), 1, 2);
        mfRange.addIsotope(ifac.getMajorIsotope("P"), 1, 2);
        mfRange.addIsotope(ifac.getMajorIsotope("Cl"), 2, 4);
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);

        ToleranceRangeRule rule2 = new ToleranceRangeRule();
        Object[] params2 = new Object[2];
        params2[0] = 318.915722;
        params2[1] = 0.05;
        rule2.setParameters(params2);
        myRules.add(rule2);

        mfTool.setRestrictions(myRules);

        IMolecularFormulaSet mfSet = mfTool.generate(318.915722);

        Assert.assertNotNull(mfSet);
        Assert.assertNotSame(0, mfSet.size());

        IMolecularFormula mf1 = new MolecularFormula();
        IIsotope carb = ifac.getMajorIsotope("C");
        mf1.addIsotope(carb, 8);
        IIsotope h = ifac.getMajorIsotope("H");
        mf1.addIsotope(h, 9);
        IIsotope oxy = ifac.getMajorIsotope("O");
        mf1.addIsotope(oxy, 2);
        IIsotope nit = ifac.getMajorIsotope("N");
        mf1.addIsotope(nit, 1);
        IIsotope cl = ifac.getMajorIsotope("Cl");
        mf1.addIsotope(cl, 3);
        IIsotope pho = ifac.getMajorIsotope("P");
        mf1.addIsotope(pho);
        IIsotope sol = ifac.getMajorIsotope("S");
        mf1.addIsotope(sol);

        Assert.assertEquals(MolecularFormulaManipulator.getString(mf1),
                MolecularFormulaManipulator.getString(mfSet.getMolecularFormula(0)));

    }

    /**
     * A unit test suite for JUnit. C(^12)3C(^13)H5
     *
     * @return    The test suite
     */
    @Test
    public void testDifferentIsotopes() throws Exception {
        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        IIsotope carbon12 = ifac.getMajorIsotope("C");
        mfRange.addIsotope(carbon12, 0, 11);
        IIsotope[] carbons = ifac.getIsotopes("C");
        IIsotope carbon13 = carbons[5]; // 13
        mfRange.addIsotope(carbon13, 0, 10);
        mfRange.addIsotope(ifac.getMajorIsotope("H"), 0, 10);
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);

        ToleranceRangeRule rule2 = new ToleranceRangeRule();
        Object[] params2 = new Object[2];
        params2[0] = 54.04193;
        params2[1] = 0.001;
        rule2.setParameters(params2);
        myRules.add(rule2);

        mfTool.setRestrictions(myRules);

        IMolecularFormulaSet mfSet = mfTool.generate(54.04193);

        Assert.assertNotNull(mfSet);
        Assert.assertNotSame(0, mfSet.size());

        IMolecularFormula mf1 = new MolecularFormula(); //C3CH5
        mf1.addIsotope(carbon12, 3);
        mf1.addIsotope(carbon13, 1);
        IIsotope h = ifac.getMajorIsotope("H");
        mf1.addIsotope(h, 5);

        Assert.assertEquals(mf1.getIsotopeCount(), mfSet.getMolecularFormula(0).getIsotopeCount());
        Assert.assertEquals(mf1.getIsotopeCount(carbon12), mfSet.getMolecularFormula(0).getIsotopeCount(carbon12));
        Assert.assertEquals(mf1.getIsotopeCount(carbon13), mfSet.getMolecularFormula(0).getIsotopeCount(carbon13));

    }

    @Test
    public void testFixedFormulaRange() throws Exception {
        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(ifac.getMajorIsotope("C"), 12, 12);
        mfRange.addIsotope(ifac.getMajorIsotope("H"), 25, 25);
        mfRange.addIsotope(ifac.getMajorIsotope("O"), 2, 2);
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);

        ToleranceRangeRule rule2 = new ToleranceRangeRule();
        Object[] params2 = new Object[2];
        params2[0] = 318.915722;
        params2[1] = 0.5;
        rule2.setParameters(params2);
        myRules.add(rule2);

        mfTool.setRestrictions(myRules);

        IMolecularFormulaSet mfSet = mfTool.generate(199.16973802990927);

        Assert.assertNull(mfSet);

    }

    @Test
    public void testFixedFormulaRange2() throws Exception {
        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(ifac.getMajorIsotope("C"), 7, 7);
        mfRange.addIsotope(ifac.getMajorIsotope("H"), 15, 15);
        mfRange.addIsotope(ifac.getMajorIsotope("O"), 4, 4);
        mfRange.addIsotope(ifac.getMajorIsotope("N"), 2, 2);
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);

        ToleranceRangeRule rule2 = new ToleranceRangeRule();
        Object[] params2 = new Object[2];
        params2[0] = 318.915722;
        params2[1] = 0.05;
        rule2.setParameters(params2);
        myRules.add(rule2);

        mfTool.setRestrictions(myRules);

        IMolecularFormulaSet mfSet = mfTool.generate(191.10318196);

        Assert.assertNotNull(mfSet);

        IMolecularFormula mf1 = new MolecularFormula();
        IIsotope carb = ifac.getMajorIsotope("C");
        mf1.addIsotope(carb, 7);
        IIsotope h = ifac.getMajorIsotope("H");
        mf1.addIsotope(h, 15);
        IIsotope oxy = ifac.getMajorIsotope("O");
        mf1.addIsotope(oxy, 4);
        IIsotope nit = ifac.getMajorIsotope("N");
        mf1.addIsotope(nit, 2);

        Assert.assertEquals(MolecularFormulaManipulator.getString(mf1),
                MolecularFormulaManipulator.getString(mfSet.getMolecularFormula(0)));

    }

    /**
     * Test to find a single carbon.
     */
    @Ignore("Demonstrates a deficiency in the implementation - use MolecularFormulaGenerator")
    public void testSingleCarbon() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 100);

        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);

        ToleranceRangeRule rule2 = new ToleranceRangeRule();
        Object[] params2 = new Object[2];
        params2[0] = 10.0;
        params2[1] = 5.0;
        rule2.setParameters(params2);
        myRules.add(rule2);

        mfTool.setRestrictions(myRules);

        IMolecularFormulaSet mfSet = mfTool.generate(10.0);

        Assert.assertNotNull(mfSet);
        Assert.assertEquals(1, mfSet.size());
        Assert.assertEquals("C", MolecularFormulaManipulator
                .getString(mfSet.getMolecularFormula(0)));
    }
    
    /**
     * Test to find H2O in a range of 1-20.
     */
    @Ignore("Demonstrates a deficiency in the implementation - use MolecularFormulaGenerator")
    public void testWater() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");
        IIsotope p = ifac.getMajorIsotope("P");
        IIsotope s = ifac.getMajorIsotope("S");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 10);
        mfRange.addIsotope(h, 0, 10);
        mfRange.addIsotope(o, 0, 10);
        mfRange.addIsotope(n, 0, 10);
        mfRange.addIsotope(p, 0, 10);
        mfRange.addIsotope(s, 0, 10);

        MassToFormulaTool mfTool = new MassToFormulaTool(builder);

        List<IRule> myRules = new ArrayList<IRule>();

        IRule rule1 = new ElementRule();
        Object[] params = new Object[1];
        params[0] = mfRange;
        rule1.setParameters(params);
        myRules.add(rule1);

        ToleranceRangeRule rule2 = new ToleranceRangeRule();
        Object[] params2 = new Object[2];
        params2[0] = 10.0;
        params2[1] = 9.0;
        rule2.setParameters(params2);
        myRules.add(rule2);

        mfTool.setRestrictions(myRules);

        IMolecularFormulaSet mfSet = mfTool.generate(10.0);

        Assert.assertNotNull(mfSet);

        boolean found = false;
        for (IMolecularFormula formula : mfSet.molecularFormulas()) {
            String mf = MolecularFormulaManipulator.getString(formula);
            if (mf.equals("H2O")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue("The molecular formula H2O should be found", found);
    }
}
