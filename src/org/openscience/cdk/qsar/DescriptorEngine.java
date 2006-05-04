/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.qsar;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.dict.Entry;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * A class that provides access to automatic descriptor calculation and more.
 * <p/>
 * The aim of this class is to provide an easy to use interface to automatically evaluate
 * all the CDK descriptors for a given molecule. Note that at a given time this class
 * will evaluate all <i>atomic</i> or <i>molecular</i> descriptors but not both.
 * <p/>
 * The available descriptors are determined by scanning all the jar files in the users CLASSPATH
 * and selecting classes that belong to the CDK QSAR atomic or molecular descriptors package.
 * <p/>
 * An example of its usage would be
 * <pre>
 * Molecule someMolecule;
 * ...
 * DescriptorEngine descriptoEngine = new DescriptorEngine(DescriptorEngine.MOLECULAR, null);
 * descriptorEngine.process(someMolecule);
 * </pre>
 * The class allows the user to obtain a List of all the available descriptors in terms of their
 * Java class names as well as instances of each descriptor class.   For each descriptor, it is possible to
 * obtain its classification as described in the CDK descriptor-algorithms OWL dictionary.
 *
 * @cdk.created 2004-12-02
 * @cdk.module  qsar
 * @cdk.depends xom-1.0.jar
 * 
 * @see DescriptorSpecification
 * @see Dictionary
 * @see org.openscience.cdk.dict.OWLFile
 */
public class DescriptorEngine {
    private static String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    public static final int ATOMIC = 1;
    public static final int MOLECULAR = 2;

    private org.openscience.cdk.dict.Dictionary dict = null;
    private List classNames = null;
    private List descriptors = null;
    private List speclist = null;
    private LoggingTool logger;

   /**
     * Constructor that generates a list of descriptors to calculate.
     * <p/>
     * All available descriptors are included in the list of descriptors to
     * calculate This constructor assumes that system classpath is the one to look at
     * to find valid jar files.
     *
     * @param type Indicates whether molecular or atomic descriptors should be calculated. Possible values
     * are DescriptorEngine.ATOMIC or DescriptorEngine.MOLECULAR
     */
    public DescriptorEngine(int type) {
	this(type, null);
    }

    /**
     * Constructor that generates a list of descriptors to calculate.
     * <p/>
     * All available descriptors are included in the list of descriptors to
     * calculate
     *
     * @param type Indicates whether molecular or atomic descriptors should be calculated. Possible values
     * are DescriptorEngine.ATOMIC or DescriptorEngine.MOLECULAR
     * @pararm jarFileNames A String[] containing the fully qualified names of the jar files
     * to examine for descriptor classes. In general, this can be set to NULL, in which case
     * the system classpath is examined for available jar files. This parameter can be set for
     * situations where the system classpath is not available or is modified such as in an application
     * container.
     */
    public DescriptorEngine(int type, String[] jarFileNames) {
        logger = new LoggingTool(this);
        switch (type) {
            case ATOMIC:
                classNames = getDescriptorClassNameByPackage("org.openscience.cdk.qsar.descriptors.atomic", jarFileNames);
                break;
            case MOLECULAR:
                classNames = getDescriptorClassNameByPackage("org.openscience.cdk.qsar.descriptors.molecular", jarFileNames);
                break;
        }
        instantiateDescriptors(classNames);
        initializeSpecifications(descriptors);

        // get the dictionary for the descriptors
        DictionaryDatabase dictDB = new DictionaryDatabase();
        dict = dictDB.getDictionary("descriptor-algorithms");
    }


 
    /**
     * Constructor that generates a list of descriptors to calculate.
     *
     * @param descriptorClasses A String array containing one or more of the above elements
     * @deprecated
     */
    public DescriptorEngine(String[] descriptorClasses) {
        this(DescriptorEngine.MOLECULAR, null);
    }

    /**
     * Calculates all available (or only those specified) descriptors for a molecule.
     * <p/>
     * The results for a given descriptor as well as associated parameters and
     * specifications are used to create a <code>DescriptorValue</code>
     * object which is then added to the molecule as a property keyed
     * on the <code>DescriptorSpecification</code> object for that descriptor
     *
     * @param molecule The molecule for which we want to calculate descriptors
     * @throws CDKException if an error occured during descriptor calculation
     */
    public void process(IAtomContainer molecule) throws CDKException {

        for (int i = 0; i < descriptors.size(); i++) {
            IMolecularDescriptor descriptor = (IMolecularDescriptor) descriptors.get(i);
            try {
                DescriptorValue value = descriptor.calculate(molecule);
                molecule.setProperty(speclist.get(i), value);
            } catch (CDKException exception) {
                logger.error("Could not calculate descriptor value for: ", descriptor.getClass().getName());
                logger.debug(exception);
                throw new CDKException("Could not calculate descriptor value for: " + descriptor.getClass().getName(), exception);
            }
        }
    }

    /**
     * Returns the type of the descriptor as defined in the descriptor dictionary.
     * <p/>
     * The method will look for the identifier specified by the user in the QSAR descriptor
     * dictionary. If a corresponding entry is found, first child element that is called
     * "isClassifiedAs" is returned. Note that the OWL descriptor spec allows both the class of
     * descriptor (electronic, topological etc) as well as the type of descriptor (molecular, atomic)
     * to be specified in an "isClassifiedAs" element. Thus we ignore any such element that
     * indicates the descriptors class.
     * <p/>
     * The method assumes that any descriptor entry will have only one "isClassifiedAs" entry describing
     * the descriptors type.
     * <p/>
     * The descriptor can be identified either by the name of the class implementing the descriptor
     * or else the specification reference value of the descriptor which can be obtained from an instance
     * of the descriptor class.
     *
     * @param identifier A String containing either the descriptors fully qualified class name or else the descriptors
     *                   specification reference
     * @return The type of the descriptor as stored in the dictionary, null if no entry is found matching
     *         the supplied identifier
     */
    public String getDictionaryType(String identifier) {

        Entry[] dictEntries = dict.getEntries();
        String specRef = getSpecRef(identifier);

        logger.debug("Got identifier: "+identifier);
        logger.debug("Final spec ref: "+specRef);

        for (int j = 0; j < dictEntries.length; j++) {
            if (!dictEntries[j].getClassName().equals("Descriptor")) continue;
            if (dictEntries[j].getID().equals(specRef.toLowerCase())) {
                Element rawElement = (Element) dictEntries[j].getRawContent();
                // assert(rawElement != null);
                // We're not fully Java 1.5 yet, so commented it out now. If it is
                // really important to have it, then add @cdk.require java1.5 in the
                // Class javadoc (and all classes that use this class)
                Elements classifications = rawElement.getChildElements("isClassifiedAs", dict.getNS());

                for (int i = 0; i < classifications.size(); i++) {
                    Element e = classifications.get(i);
                    Attribute attr = e.getAttribute("resource", rdfNS);
                    if ((attr.getValue().indexOf("molecularDescriptor") != -1) || 
                        (attr.getValue().indexOf("atomicDescriptor") != -1)) {
                        String[] tmp = attr.getValue().split("#");
                        return tmp[1];
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the type of the descriptor as defined in the descriptor dictionary.
     * <p/>
     * The method will look for the identifier specified by the user in the QSAR descriptor
     * dictionary. If a corresponding entry is found, first child element that is called
     * "isClassifiedAs" is returned. Note that the OWL descriptor spec allows both the class of
     * descriptor (electronic, topological etc) as well as the type of descriptor (molecular, atomic)
     * to be specified in an "isClassifiedAs" element. Thus we ignore any such element that
     * indicates the descriptors class.
     * <p/>
     * The method assumes that any descriptor entry will have only one "isClassifiedAs" entry describing
     * the descriptors type.
     * <p/>
     * The descriptor can be identified it DescriptorSpecification object
     *
     * @param descriptorSpecification A DescriptorSpecification object
     * @return he type of the descriptor as stored in the dictionary, null if no entry is found matching
     *         the supplied identifier
     */
    public String getDictionaryType(DescriptorSpecification descriptorSpecification) {
        return getDictionaryType(descriptorSpecification.getSpecificationReference());
    }

    /**
     * Returns the class(es) of the decsriptor as defined in the descriptor dictionary.
     * <p/>
     * The method will look for the identifier specified by the user in the QSAR descriptor
     * dictionary. If a corresponding entry is found, the meta-data list is examined to
     * look for a dictRef attribute that contains a descriptorClass value. if such an attribute is
     * found, the value of the contents attribute  add to a list. Since a descriptor may be classed in
     * multiple ways (geometric and electronic for example), in general, a given descriptor will
     * have multiple classes associated with it.
     * <p/>
     * The descriptor can be identified either by the name of the class implementing the descriptor
     * or else the specification reference value of the descriptor which can be obtained from an instance
     * of the descriptor class.
     *
     * @param identifier A String containing either the descriptors fully qualified class name or else the descriptors
     *                   specification reference
     * @return A List containing the names of the QSAR descriptor classes that this  descriptor was declared
     *         to belong to. If an entry for the specified identifier was not found, null is returned.
     */
    public String[] getDictionaryClass(String identifier) {

        Entry[] dictEntries = dict.getEntries();

        String specRef = getSpecRef(identifier);
        if (specRef == null) {
        	logger.error("Cannot determine specification for id: ", identifier);
        	return new String[0];
        }
        List dictClasses = new ArrayList();

        for (int j = 0; j < dictEntries.length; j++) {
            if (!dictEntries[j].getClassName().equals("Descriptor")) continue;
            if (dictEntries[j].getID().equals(specRef.toLowerCase())) {
                Element rawElement = (Element) dictEntries[j].getRawContent();
                Elements classifications = rawElement.getChildElements("isClassifiedAs", dict.getNS());
                for (int i = 0; i < classifications.size(); i++) {
                    Element e = classifications.get(i);
                    Attribute attr = e.getAttribute("resource", rdfNS);
                    if ((attr.getValue().indexOf("molecularDescriptor") >= 0) || 
                        (attr.getValue().indexOf("atomicDescriptor") >= 0)) {
                        continue;
                    }
                    String[] tmp = attr.getValue().split("#");
                    dictClasses.add(tmp[1]);
                }
            }
        }


        if (dictClasses.size() == 0) return null;
        else
            return (String[]) dictClasses.toArray(new String[]{});
    }

    /**
     * Returns the class(es) of the descriptor as defined in the descriptor dictionary.
     * <p/>
     * The method will look for the identifier specified by the user in the QSAR descriptor
     * dictionary. If a corresponding entry is found, the meta-data list is examined to
     * look for a dictRef attribute that contains a descriptorClass value. if such an attribute is
     * found, the value of the contents attribute  add to a list. Since a descriptor may be classed in
     * multiple ways (geometric and electronic for example), in general, a given descriptor will
     * have multiple classes associated with it.
     * <p/>
     * The descriptor can be identified by its DescriptorSpecification object.
     *
     * @param descriptorSpecification A DescriptorSpecification object
     * @return A List containing the names of the QSAR descriptor classes that this  descriptor was declared
     *         to belong to. If an entry for the specified identifier was not found, null is returned.
     */

    public String[] getDictionaryClass(DescriptorSpecification descriptorSpecification) {
        return getDictionaryClass(descriptorSpecification.getSpecificationReference());
    }


    /**
     * Gets the definition of the descriptor.
     * <p/>
     * All descriptors in the descriptor dictioanry will have a definition element. This function
     * returns the value of that element. Many descriptors also have a description element which is
     * more detailed. However the value of these elements can contain arbitrary mark up (such as MathML)
     * and I'm not sure what I should return it as
     *
     * @param identifier A String containing either the descriptors fully qualified class name or else the descriptors
     *                   specification reference
     * @return The definition
     */
    public String getDictionaryDefinition(String identifier) {
        Entry[] dictEntries = dict.getEntries();

        String specRef = getSpecRef(identifier);
        String definition = null;

        for (int j = 0; j < dictEntries.length; j++) {
            if (!dictEntries[j].getClassName().equals("Descriptor")) continue;
            if (dictEntries[j].getID().equals(specRef.toLowerCase())) {
                definition = dictEntries[j].getDefinition();
                break;
            }
        }
        return definition;
    }

    /**
     * Gets the definition of the descriptor.
     * <p/>
     * All descriptors in the descriptor dictioanry will have a definition element. This function
     * returns the value of that element. Many descriptors also have a description element which is
     * more detailed. However the value of these elements can contain arbitrary mark up (such as MathML)
     * and I'm not sure what I should return it as
     *
     * @param descriptorSpecification A DescriptorSpecification object
     * @return The definition
     */
    public String getDictionaryDefinition(DescriptorSpecification descriptorSpecification) {
        return getDictionaryDefinition(descriptorSpecification.getSpecificationReference());
    }

    /**
     * Returns the DescriptorSpecification objects for all available descriptors.
     *
     * @return An array of <code>DescriptorSpecification</code> objects. These are the keys
     *         with which the <code>DescriptorValue</code> objects can be obtained from a
     *         molecules property list
     */
    public List getDescriptorSpecifications() {
        return (speclist);
    }

    /**
     * Returns a list containing the names of the classes implementing the descriptors.
     *
     * @return A list of class names.
     */
    public List getDescriptorClassNames() {
        return classNames;
    }

    /**
     * Returns a List containing the instantiated descriptor classes.
     *
     * @return A List containing descriptor classes
     */
    public List getDescriptorInstances() {
        return descriptors;
    }

    /**
     * Get the all the unique dictionary classes that the descriptors belong to.
     *
     * @return An array containing the unique dictionary classes.
     */
    public String[] getAvailableDictionaryClasses() {
        List classList = new ArrayList();
        for (Iterator iter = speclist.iterator(); iter.hasNext();) {
            DescriptorSpecification spec = (DescriptorSpecification) iter.next();
            String[] tmp = getDictionaryClass(spec);
            if (tmp != null) classList.addAll(Arrays.asList(tmp));
        }
        Set uniqueClasses = new HashSet(classList);
        return (String[]) uniqueClasses.toArray(new String[]{});
    }

    /**
     * Returns a list containing the classes found in the specified descriptor package.
     * <p/>
     * The package name specified can be null or an empty string. In this case the package name
     * is automatcally set to "org.openscience.cdk.qsar.descriptors" and as a result will return
     * classes corresponding to both atomic and molecular descriptors.
     *
     * @param packageName The name of the package containing the required descriptor
     * @param jarFileNames A String[] containing the fully qualified names of the jar files
     * to examine for descriptor classes. In general this can be set to NULL, in which case
     * the system classpath is examined for available jar files. This parameter can be set for
     * situations where the system classpath is not available or is modified such as in an application
     * container.
     * @return A list containing the classes in the specified package
     */
    public List    getDescriptorClassNameByPackage(String packageName, String[] jarFileNames) {

        if (packageName == null || packageName.equals("")) {
            packageName = "org.openscience.cdk.qsar.descriptors";
        }

	String[] jars = null;
	if (jarFileNames == null) {
	    String classPath = System.getProperty("java.class.path");
	    jars = classPath.split(File.pathSeparator);
	} else {
	    jars = jarFileNames;
	}

        ArrayList classlist = new ArrayList();

        for (int i = 0; i < jars.length; i++) {
            logger.debug("Looking in "+jars[i]);
            JarFile j;
            try {
                j = new JarFile(jars[i]);
                Enumeration e = j.entries();
                while (e.hasMoreElements()) {
                    JarEntry je = (JarEntry) e.nextElement();
                    if (je.toString().indexOf(".class") != -1) {
                        String tmp = je.toString().replace('/', '.').replaceAll(".class", "");
                        if (!(tmp.indexOf(packageName) != -1)) continue;
                        if (tmp.indexOf("$") != -1) continue;
                        classlist.add(tmp);
                    }
                }
            } catch (IOException e) {
                logger.error("Error opening the jar file: " + jars[i]);
                logger.debug(e);
            }
        }
        return classlist;
    }

    private void instantiateDescriptors(List descriptorClassNames) {
        if (descriptors != null) return;

        descriptors = new ArrayList();
        for (Iterator iter = descriptorClassNames.iterator(); iter.hasNext();) {
            String descriptorName = (String) iter.next();
            try {
                IDescriptor descriptor = (IDescriptor) this.getClass().getClassLoader().loadClass(descriptorName).newInstance();
                descriptors.add(descriptor);
                logger.info("Loaded descriptor: ", descriptorName);
            } catch (ClassNotFoundException exception) {
                logger.error("Could not find this Descriptor: ", descriptorName);
                logger.debug(exception);
            } catch (Exception exception) {
                logger.error("Could not load this Descriptor: ", descriptorName);
                logger.debug(exception);
            }
        }
    }

    private void initializeSpecifications(List descriptors) {
        speclist = new Vector();
        for (int i = 0; i < descriptors.size(); i++) {
            IDescriptor descriptor = (IDescriptor) descriptors.get(i);
            speclist.add(descriptor.getSpecification());
        }
    }

    private String getSpecRef(String identifier) {
        String specRef = null;
        // see if we got a descriptors java class name
        for (int i = 0; i < classNames.size(); i++) {
            String className = (String) classNames.get(i);
            if (className.equals(identifier)) {
                IDescriptor descriptor = (IDescriptor) descriptors.get(i);
                DescriptorSpecification descSpecification = descriptor.getSpecification();
                String[] tmp = descSpecification.getSpecificationReference().split("#");
                if (tmp.length != 2) {
                	logger.debug("Something fishy with the spec ref: ", descSpecification.getSpecificationReference());
                } else {
                	specRef = tmp[1];
                }
            }
        }
        // if we are here and specRef==null we have a SpecificationReference
        if (specRef == null) {
            String[] tmp = identifier.split("#");
            if (tmp.length != 2) {
            	logger.debug("Something fishy with the identifier: ", identifier);
            } else {
            	specRef = tmp[1];
            }
        }
        return specRef;
    }
}

