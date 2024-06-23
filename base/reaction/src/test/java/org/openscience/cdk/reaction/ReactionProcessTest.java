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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
import org.openscience.cdk.test.valency.ValencyCheckTestCase;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Tests for IReactionProcess implementations.
 *
 * @cdk.module test-reaction
 */
public abstract class ReactionProcessTest extends ValencyCheckTestCase {

    private IReactionProcess   reaction;
    private Dictionary         dictionary;
    private String             entryString = "";
    private final IChemObjectBuilder builder     = SilentChemObjectBuilder.getInstance();

    private static final SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Canonical |  SmiFlavor.CxRadical);

    protected static void assertEquals(IAtomContainer expected, IAtomContainer act)
        throws CDKException {
        assertThat(cansmi(act), is(cansmi(expected)));
    }

    private static String cansmi(IAtomContainer mol) throws CDKException {
        return smigen.create(AtomContainerManipulator.copyAndSuppressedHydrogens(mol));
    }

    static String cansmi(IReaction rxn) throws CDKException {
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
    protected void setReaction(Class<?> reactionClass) throws Exception {
        if (dictionary == null) dictionary = openingDictionary();

        Object object = reactionClass.newInstance();
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
    void testHasSetSuperDotDescriptor() {
        Assertions.assertNotNull(reaction, "The extending class must set the super.descriptor in its setUp() method.");
    }

    /**
     * Test if the reaction process is contained in the Dictionary as a entry.
     *
     * @throws Exception
     */
    @Test
    void testGetEntryFromReaction() throws Exception {

        entryString = reaction.getSpecification().getSpecificationReference();
        entryString = entryString.substring(entryString.indexOf("#") + 1, entryString.length());

        Assertions.assertNotSame("nothing", entryString, "The Entry ID from  [" + reaction.getClass() + "] doesn't exist.");
    }

    /**
     * Test if the reaction process is contained in the Dictionary as a entry.
     *
     * @throws Exception
     */
    @Test
    void testGetDictionaryEntry() throws Exception {

        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
        Assertions.assertNotNull(entry, "The Entry [" + entryString + "] doesn't exist in OWL Dictionary.");

    }

    /**
     * Test if this entry has a definition schema in the Dictionary.
     *
     * @throws Exception
     */
    @Test
    void testGetEntryDefinition() throws Exception {

        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());

        Assertions.assertNotNull(entry.getDefinition(), "The definition entry for [" + entryString + "] must not be null.");

    }

    /**
     * Checks if the parameterization key is consistent with those coming from the dictionary.
     *
     * @throws Exception
     */
    @Test
    void testGetParameterList() throws Exception {
        List<IParameterReact> paramObj = reaction.getParameterList();

        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
        List<List<String>> paramDic = entry.getParameterClass();

        Assertions.assertNotNull(paramObj, "The parameters entry for [" + entryString + "]  must contain at least one parameter.");
        Assertions.assertNotNull(paramDic, "The parameters entry for [" + entryString + "]  must contain at least one parameter.");
        Assertions.assertSame(paramObj.size(), paramDic.size(), "The parameters entry for [" + entryString
                + "]  must contain the same lenght as the reaction object.");
    }

    /**
     * Test the specification of the IReactionProcess.
     *
     */
    @Test
    void testGetSpecification() {
        ReactionSpecification spec = reaction.getSpecification();
        Assertions.assertNotNull(spec, "The descriptor specification returned must not be null.");

        Assertions.assertNotNull(spec.getImplementationIdentifier(), "The specification identifier must not be null.");
        Assertions.assertNotSame(0, spec.getImplementationIdentifier()
                                        .length(), "The specification identifier must not be empty.");

        Assertions.assertNotNull(spec.getImplementationTitle(), "The specification title must not be null.");
        Assertions.assertNotSame(0, spec.getImplementationTitle().length(), "The specification title must not be empty.");

        Assertions.assertNotNull(spec.getImplementationVendor(), "The specification vendor must not be null.");
        Assertions.assertNotSame(0, spec.getImplementationVendor().length(), "The specification vendor must not be empty.");

        Assertions.assertNotNull(spec.getSpecificationReference(), "The specification reference must not be null.");
        Assertions.assertNotSame(0, spec.getSpecificationReference()
                                        .length(), "The specification reference must not be empty.");
    }

    /**
     * Test if this entry has a definition schema in the Dictionary.
     *
     * @throws Exception
     */
    @Test
    void testGetEntryDescription() throws Exception {

        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());

        Assertions.assertNotNull(entry.getDescription(), "The description entry for [" + entryString + "] must not be null.");
    }

    /**
     * Test if this entry has at least one representation schema in the Dictionary.
     *
     * @throws Exception
     */
    @Test
    void testGetEntryRepresentation() throws Exception {

        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());

        Assertions.assertNotSame(0, entry.getRepresentations().size(), "The representation entry for [" + entryString
                + "]  must contain at least one representation.");
    }

    /**
     * Test reactive center parameter
     *
     *
     */
    @Test
    void testCentreActive() throws Exception {
        IReactionProcess type = reaction;

        IParameterReact ipr = type.getParameterClass(SetReactionCenter.class);
        Assertions.assertNotNull(ipr);
        Assertions.assertFalse(ipr.isSetParameter());

        List<IParameterReact> paramList = new ArrayList<>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        IParameterReact ipr2 = type.getParameterClass(SetReactionCenter.class);
        Assertions.assertTrue(ipr2.isSetParameter());
    }

    /**
     * Test extracting a reaction as example.
     *
     * TODO: REACT: One example for each reaction should be set in owl dictionary.
     *
     */
    @Test
    void testGetExampleReaction() throws Exception {
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
     *
     */
    @Test
    public void testInitiate_IAtomContainerSet_IAtomContainerSet() throws Exception {
        EntryReact entry = (EntryReact) dictionary.getEntry(entryString.toLowerCase());
        List<String> xmlList = entry.getExampleReactions();
        Assertions.assertTrue(xmlList.size() != 0, "The representation entry for [" + entryString
                + "]  must contain at least one example of reaction.");
        Assertions.assertTrue(xmlList.size() > 0, "The representation entry for [" + entryString
                + "]  must contain at least one example of reaction.");
        for (String xml : xmlList) {
            CMLReader reader = new CMLReader(new ByteArrayInputStream(xml.getBytes()));
            IChemFile chemFile = reader.read(builder.newInstance(IChemFile.class));
            IReaction reactionDict = chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0);

            IAtomContainerSet reactants = reactionDict.getReactants();
            IAtomContainerSet agents = reactionDict.getAgents();
            IAtomContainerSet products = reactionDict.getProducts();
            if (agents.getAtomContainerCount() == 0) agents = null;

            IReactionSet reactions = reaction.initiate(reactants, agents);

            Assertions.assertTrue(reactions.getReactionCount() > 0, "The products for [" + entryString + "] reaction is at least one reaction expected.");

            Assertions.assertSame(products
                    .getAtomContainer(0).getAtomCount(), reactions.getReaction(0).getProducts().getAtomContainer(0)
                                                                  .getAtomCount(), "The products for [" + entryString + "] reaction is not the expected.");

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
