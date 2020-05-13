/* Copyright (C) 2008  Miguel Rojas <miguelrojasch@users.sf.net>
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
package org.openscience.cdk.reaction;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.dict.Dictionary;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.dict.EntryReact;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for IReactionProcess implementations.
 *
 * @cdk.module test-reaction
 */
public abstract class ReactionProcessTest extends CDKTestCase {

    private IReactionProcess   reaction;
    private Dictionary         dictionary;
    private String             entryString = "";
    private IChemObjectBuilder builder     = SilentChemObjectBuilder.getInstance();

    private static final SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Canonical |  SmiFlavor.CxRadical);

    protected static void assertEquals(IAtomContainer expected, IAtomContainer act)
        throws CDKException {
        assertThat(cansmi(act), is(cansmi(expected)));
    }

    private static String cansmi(IAtomContainer mol) throws CDKException {
        return smigen.create(AtomContainerManipulator.copyAndSuppressedHydrogens(mol));
    }

    protected static String cansmi(IReaction rxn) throws CDKException {
        IReaction copy = rxn.getBuilder().newInstance(IReaction.class);
        for (IAtomContainer mol : rxn.getReactants().atomContainers())
            copy.addReactant(AtomContainerManipulator.copyAndSuppressedHydrogens(mol));
        for (IAtomContainer mol : rxn.getProducts().atomContainers())
            copy.addProduct(AtomContainerManipulator.copyAndSuppressedHydrogens(mol));
        for (IAtomContainer mol : rxn.getAgents().atomContainers())
            copy.addAgent(AtomContainerManipulator.copyAndSuppressedHydrogens(mol));
        return smigen.create(copy);
    }

    protected void assertReaction(String smiles)
        throws CDKException {
        final SmilesParser smipar = new SmilesParser(builder);
        final IReaction reaction = smipar.parseReactionSmiles(smiles);
        final IReactionSet reactions = this.reaction.initiate(reaction.getReactants(), null);
        final String expected = cansmi(reaction);
        assertThat(reactions.getReactionCount(), is(not(0)));
        for (IReaction actual : reactions.reactions()) {
            assertThat(cansmi(actual), is(expected));
        }
    }

    /**
     * Set the IReactionProcess to analyzed
     *
     * @param reactionClass   The IReactionProcess class
     * @throws Exception
     */
    public void setReaction(Class<?> reactionClass) throws Exception {
        if (dictionary == null) dictionary = openingDictionary();

        Object object = (Object) reactionClass.newInstance();
        if (!(object instanceof IReactionProcess)) {
            throw new CDKException("The passed reaction class must be a IReactionProcess");
        } else if (reaction == null) {
            reaction = (IReactionProcess) object;

            entryString = reaction.getSpecification().getSpecificationReference();
            entryString = entryString.substring(entryString.indexOf("#") + 1, entryString.length());
        }
    }

    /**
     * Open the Dictionary OWLReact.
     *
     * @return The dictionary reaction-processes
     */
    private Dictionary openingDictionary() {
        DictionaryDatabase db = new DictionaryDatabase();
        Dictionary dict = db.getDictionary("reaction-processes");
        return dict;
    }

    /**
     * Makes sure that the extending class has set the super.descriptor.
     * Each extending class should have this bit of code (JUnit3 formalism):
     * <pre>
     * public void setUp() {
     *   // Pass a Class, not an Object!
     *   setReaction(SomeReaction.class);
     * }
     *
     * <p>The unit tests in the extending class may use this instance, but
     * are not required.
     *
     * </pre>
     */
    @Test
    public void testHasSetSuperDotDescriptor() {
        Assert.assertNotNull("The extending class must set the super.descriptor in its setUp() method.", reaction);
    }

    /**
     * Test if the reaction process is contained in the Dictionary as a entry.
     *
     * @throws Exception
     */
    @Test
    public void testGetEntryFromReaction() throws Exception {

        entryString = reaction.getSpecification().getSpecificationReference();
        entryString = entryString.substring(entryString.indexOf("#") + 1, entryString.length());

        Assert.assertNotSame("The Entry ID from  [" + reaction.getClass() + "] doesn't exist.", "nothing", entryString);
    }

    /**
     * Test if the reaction process is contained in the Dictionary as a entry.
     *
     * @throws Exception
     */
    @Test
    public void testGetDictionaryEntry() throws Exception {

        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
        Assert.assertNotNull("The Entry [" + entryString + "] doesn't exist in OWL Dictionary.", entry);

    }

    /**
     * Test if this entry has a definition schema in the Dictionary.
     *
     * @throws Exception
     */
    @Test
    public void testGetEntryDefinition() throws Exception {

        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());

        Assert.assertNotNull("The definition entry for [" + entryString + "] must not be null.", entry.getDefinition());

    }

    /**
     * Checks if the parameterization key is consistent with those coming from the dictionary.
     *
     * @throws Exception
     */
    @Test
    public void testGetParameterList() throws Exception {
        List<IParameterReact> paramObj = reaction.getParameterList();

        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
        List<List<String>> paramDic = entry.getParameterClass();

        Assert.assertNotNull("The parameters entry for [" + entryString + "]  must contain at least one parameter.",
                paramObj);
        Assert.assertNotNull("The parameters entry for [" + entryString + "]  must contain at least one parameter.",
                paramDic);
        Assert.assertSame("The parameters entry for [" + entryString
                + "]  must contain the same lenght as the reaction object.", paramObj.size(), paramDic.size());
    }

    /**
     * Test the specification of the IReactionProcess.
     *
     */
    @Test
    public void testGetSpecification() {
        ReactionSpecification spec = reaction.getSpecification();
        Assert.assertNotNull("The descriptor specification returned must not be null.", spec);

        Assert.assertNotNull("The specification identifier must not be null.", spec.getImplementationIdentifier());
        Assert.assertNotSame("The specification identifier must not be empty.", 0, spec.getImplementationIdentifier()
                .length());

        Assert.assertNotNull("The specification title must not be null.", spec.getImplementationTitle());
        Assert.assertNotSame("The specification title must not be empty.", 0, spec.getImplementationTitle().length());

        Assert.assertNotNull("The specification vendor must not be null.", spec.getImplementationVendor());
        Assert.assertNotSame("The specification vendor must not be empty.", 0, spec.getImplementationVendor().length());

        Assert.assertNotNull("The specification reference must not be null.", spec.getSpecificationReference());
        Assert.assertNotSame("The specification reference must not be empty.", 0, spec.getSpecificationReference()
                .length());
    }

    /**
     * Test if this entry has a definition schema in the Dictionary.
     *
     * @throws Exception
     */
    @Test
    public void testGetEntryDescription() throws Exception {

        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());

        Assert.assertNotNull("The description entry for [" + entryString + "] must not be null.",
                entry.getDescription());
    }

    /**
     * Test if this entry has at least one representation schema in the Dictionary.
     *
     * @throws Exception
     */
    @Test
    public void testGetEntryRepresentation() throws Exception {

        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());

        Assert.assertNotSame("The representation entry for [" + entryString
                + "]  must contain at least one representation.", 0, entry.getRepresentations().size());
    }

    /**
     * Test reactive center parameter
     *
     * @return    The test suite
     */
    @Test
    public void testCentreActive() throws Exception {
        IReactionProcess type = reaction;

        IParameterReact ipr = type.getParameterClass(SetReactionCenter.class);
        Assert.assertNotNull(ipr);
        Assert.assertFalse(ipr.isSetParameter());

        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        IParameterReact ipr2 = type.getParameterClass(SetReactionCenter.class);
        Assert.assertTrue(ipr2.isSetParameter());
    }

    /**
     * Test extracting a reaction as example.
     *
     * TODO: REACT: One example for each reaction should be set in owl dictionary.
     * @return    The test suite
     */
    @Test
    public void testGetExampleReaction() throws Exception {
        //		EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
        //    	List<String> xmlList = entry.getExampleReactions();
        //    	Assert.assertTrue("The representation entry for ["+entryString+"]  must contain at least one example of reaction.",
        //    			xmlList.size() != 0);
        //    	Assert.assertTrue("The representation entry for ["+entryString+"]  must contain at least one example of reaction.",
        //    			xmlList.size() > 0);
        //    	for(Iterator<String> it = xmlList.iterator(); it.hasNext();){
        //			String xml = it.next();
        //			CMLReader reader = new CMLReader(new ByteArrayInputStream(xml.getBytes()));
        //	        IChemFile chemFile = (IChemFile)reader.read(builder.newInstance(IChemFile.class));
        //	        IReaction reactionDict = chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0);
        //	        for(Iterator<IAtomContainer> itM = reactionDict.getReactants().molecules().iterator(); itM.hasNext();){
        //	        	IAtomContainer molecule = (IAtomContainer) itM.next();
        //	        	Assert.assertNotNull("The representation entry for ["+entryString+"]  must contain the InChI id for each reactant.",
        //	        			molecule.getProperty(CDKConstants.INCHI));
        //	        	Assert.assertNotSame("The representation entry for ["+entryString+"]  must contain the InChI id for each reactant.",
        //	        			"",molecule.getProperty(CDKConstants.INCHI));
        //
        //	        }
        //    	}
    }

    /**
     * Test extracting a reaction as example and comparing with the initiated.
     *
     * TODO: REACT: How to comparing two reaction?
     *
     * @return    The test suite
     */
    @Test
    public void testInitiate_IAtomContainerSet_IAtomContainerSet() throws Exception {
        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
        List<String> xmlList = entry.getExampleReactions();
        Assert.assertTrue("The representation entry for [" + entryString
                + "]  must contain at least one example of reaction.", xmlList.size() != 0);
        Assert.assertTrue("The representation entry for [" + entryString
                + "]  must contain at least one example of reaction.", xmlList.size() > 0);
        for (Iterator<String> it = xmlList.iterator(); it.hasNext();) {
            String xml = it.next();
            CMLReader reader = new CMLReader(new ByteArrayInputStream(xml.getBytes()));
            IChemFile chemFile = (IChemFile) reader.read(builder.newInstance(IChemFile.class));
            IReaction reactionDict = chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0);

            IAtomContainerSet reactants = reactionDict.getReactants();
            IAtomContainerSet agents = reactionDict.getAgents();
            IAtomContainerSet products = reactionDict.getProducts();
            if (agents.getAtomContainerCount() == 0) agents = null;

            IReactionSet reactions = reaction.initiate(reactants, agents);

            Assert.assertTrue("The products for [" + entryString + "] reaction is at least one reaction expected.",
                    reactions.getReactionCount() > 0);

            Assert.assertSame("The products for [" + entryString + "] reaction is not the expected.", products
                    .getAtomContainer(0).getAtomCount(), reactions.getReaction(0).getProducts().getAtomContainer(0)
                    .getAtomCount());

        }
    }
    //	/**
    //	 * Test the reaction center
    //	 *
    //	 * @return    The test suite
    //	 */
    //	@Test public void testCDKConstants_REACTIVE_CENTER() throws Exception {
    //	}
    //
    //	/**
    //	 * Test mapping in reaction process.
    //	 *
    //	 * @return    The test suite
    //	 */
    //	@Test public void testMapping() throws Exception {
    //
    //
    //	}
    //
    //	/**
    //	 * Set reaction center and generates the product.
    //	 *
    //	 * @return    The test suite
    //	 */
    //	@Test public void testManuallyCentreActive() throws Exception {
    //
    //	}
    //
    //	/**
    //	 * Automatically looks for reaction center and generates the product.
    //	 *
    //	 * @return    The test suite
    //	 */
    //	@Test public void testAutomaticallyCentreActive() throws Exception {
    //
    //	}
    //
    //
    //	/**
    //	 * Control that the reactant is the not modified during the process.
    //	 *
    //	 * @return    The test suite
    //	 */
    //	@Test public void testCalculate_Results() throws Exception {
    //		EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
    //    	List<String> xmlList = entry.getExampleReactions();
    //    	for(Iterator<String> it = xmlList.iterator(); it.hasNext();){
    //			String xml = it.next();
    //			System.out.println(xml);
    //			CMLReader reader = new CMLReader(new ByteArrayInputStream(xml.getBytes()));
    //	        IChemFile chemFile = (IChemFile)reader.read(builder.newInstance(IChemFile.class));
    //	        IReaction reactionDict = chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0);
    //
    //	        IReaction reactionTest = builder.newInstance(IReaction.class);
    //	        for(Iterator<IAtomContainer> itM = reactionDict.getReactants().molecules(); itM.hasNext();){
    //	        	reactionTest.addReactant((IAtomContainer) itM.next());
    //	        }
    //	        for(Iterator<IAtomContainer> itM = reactionDict.getAgents().molecules(); itM.hasNext();){
    //	        	reactionTest.addAgent((IAtomContainer) itM.next());
    //	        }
    //	        IAtomContainerSet reactants = reactionDict.getReactants();
    //	        System.out.println(reactants);
    //	        if(reactants.getAtomContainerCount() == 0)
    //	        	reactants = null;
    //	        IAtomContainerSet agents = reactionDict.getAgents();
    //	        if(agents.getAtomContainerCount() == 0)
    //	        	agents = null;
    //	        System.out.println(agents);
    //	        IReactionSet setOfReactions = reaction.initiate(reactants, agents);
    //
    //
    //
    //		}
    //	}
    //
    //	/**
    //	 * Control that the reactant is the not modified during the process.
    //	 *
    //	 * @return    The test suite
    //	 */
    //	@Test public void testGetMechanism() throws Exception {
    //		EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
    //
    //		String mechanismName = "org.openscience.cdk.reaction.mechanism."+entry.getMechanism();
    //
    //		Assert.assertNotNull(
    //    			"The representation entry for ["+entryString+"]  must contain at least one mechanism coming from.",
    //    			this.getClass().getClassLoader().loadClass(mechanismName).newInstance());
    //
    //	}
}
