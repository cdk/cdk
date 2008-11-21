/*  $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2004-2007  Christian Hoppe <chhoppe@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.modeling.builder3d;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomTypeException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.HOSECodeGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 *  Reads in a force field configuration file, set the atom types into a vector, and the data into a hashtable
 *  Therefore, it uses the class {@link MM2BasedParameterSetReader}.
 *  private Hashtable parameterSet;
 *  key=nameofdatafield+atomid1+;atomid2;atomxid
 *  
 *  <p>MM2 and MMFF94 force field are implemented 
 *  With force field data it configures the cdk atom (assign atomtype, van der Waals radius, charge...)
 *
 * @author     chhoppe
 * @cdk.created    2004-09-07
 * @cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 */
public class ForceFieldConfigurator {

	private String ffName = "mmff94";
	private List<IAtomType> atomTypes;
	private Map<String, Object> parameterSet=null;
	private MM2BasedParameterSetReader mm2 = null;
	private MMFF94BasedParameterSetReader mmff94= null;
	private InputStream ins = null;
	private String[] fftypes = {"mm2","mmff94"};
	
	/**
	 *Constructor for the ForceFieldConfigurator object
	 */
	public ForceFieldConfigurator() { }

	/**
	 *  Sets the inputStream attribute of the ForceFieldConfigurator object
	 *
	 * @param  ins  The new inputStream value
	 */
	public void setInputStream(InputStream ins) {
		this.ins = ins;
	}
  
  
	/**
	 *  gives a list of possible force field types
	 *
	 *@return                the list
	 */
  public String[] getFfTypes(){
    return fftypes;
  }

	/**
	 *  Sets the forceFieldType attribute of the ForceFieldConfigurator object
	 *
	 * @param  ffname  The new forceFieldType name
	 */
	public boolean checkForceFieldType(String ffname) {
		boolean check=false;
		for (int i = 0; i <= fftypes.length; i++) {
			if (fftypes[i].equals(ffname)) {
				check=true;
				break;
			} 
		}
		if (!check) {
//			logger.debug("FFError:checkForceFieldType> Unknown forcefield:" + ffname + "Take default:"+ffName);
			return false;
		}
		return true;
	}

	/**
	 *Constructor for the ForceFieldConfigurator object
	 *
	 * @param  ffname  name of the force field data file
	 */
	public void setForceFieldConfigurator(String ffname) throws CDKException {
		ffname=ffname.toLowerCase();
		boolean check=false;
		
		if (ffname==ffName && parameterSet!=null){
		}else{
			check=this.checkForceFieldType(ffname);
			ffName=ffname;
			if (ffName.equals("mm2")) {
				//logger.debug("ForceFieldConfigurator: open Force Field mm2");
				//f = new File(mm2File);
				//readFile(f);
				ins = this.getClass().getClassLoader().getResourceAsStream("org/openscience/cdk/modeling/forcefield/data/mm2.prm");
				//logger.debug("ForceFieldConfigurator: open Force Field mm2 ... READY");
				mm2 = new MM2BasedParameterSetReader();
				mm2.setInputStream(ins);
				//logger.debug("ForceFieldConfigurator: mm2 set input stream ... READY");
				try{
					this.setMM2Parameters();
				}catch (Exception ex1){
					throw new CDKException("Problems with set MM2Parameters due to "+ex1.toString(), ex1);	
				}
			}else if (ffName.equals("mmff94") || !check) {
				//logger.debug("ForceFieldConfigurator: open Force Field mmff94");
				//f = new File(mmff94File);
				//readFile(f);
				ins = this.getClass().getClassLoader().getResourceAsStream("org/openscience/cdk/modeling/forcefield/data/mmff94.prm");
				mmff94= new MMFF94BasedParameterSetReader();
				
				mmff94.setInputStream(ins);
				try{
					this.setMMFF94Parameters();
				}catch (Exception ex2){
					throw new CDKException("Problems with set MM2Parameters due to"+ex2.toString(), ex2);	
				}
			}
		}
		//throw new CDKException("Data file for "+ffName+" force field could not be found");
	}


	/**
	 *  Sets the atomTypes attribute of the ForceFieldConfigurator object
	 *
	 * @param  atomtypes  The new atomTypes 
	 */
	public void setAtomTypes(List<IAtomType> atomtypes) {
		atomTypes = atomtypes;
	}

	/**
	 *  Sets the parameters attribute of the ForceFieldConfigurator object
	 *
	 * @param  parameterset  The new parameter values
	 */
	public void setParameters(Map<String, Object> parameterset) {
		parameterSet = parameterset;
	}

	/**
	 *  Sets the parameters attribute of the ForceFieldConfigurator object, default is mm2 force field
	 */
	public void setMM2Parameters() throws CDKException{
		try{
			mm2.readParameterSets();
		}catch(Exception ex1){
			throw new CDKException("Problem within readParameterSets due to:"+ex1.toString(), ex1);
		}
		parameterSet = mm2.getParamterSet();
		atomTypes = mm2.getAtomTypes();
  }
	
	public void setMMFF94Parameters() throws Exception{
		mmff94.readParameterSets();
		parameterSet = mmff94.getParamterSet();
		atomTypes = mmff94.getAtomTypes();
	}
	
	/**
	 *  Gets the atomTypes attribute of the ForceFieldConfigurator object
	 *
	 * @return    The atomTypes vector
	 */
	public List<IAtomType> getAtomTypes() {
		return atomTypes;
	}

	/**
	 *  Gets the parameterSet attribute of the ForceFieldConfigurator object
	 *
	 * @return    The parameterSet hashtable
	 */
	public Map<String,Object> getParameterSet() {
		return this.parameterSet;
	}

	/**
	 *  Find the atomType for a id
	 *
	 * @param  ID                           Atomtype id of the forcefield
	 * @return                              The atomType 
	 * @exception  NoSuchAtomTypeException  atomType is not known
	 */
	private IAtomType getAtomType(String ID) throws NoSuchAtomTypeException {
		IAtomType at = null;
    		for (int i = 0; i < atomTypes.size(); i++) {
			at = (IAtomType) atomTypes.get(i);
			if (at.getAtomTypeName().equals(ID)) {
				return at;
			}
		}
		throw new NoSuchAtomTypeException("AtomType " + ID + " could not be found");
	}

	
	/**
	 *  Method assigns atom types to atoms (calculates sssr and aromaticity)
	 *
	 *@return                sssrf set
	 *@exception  Exception  Description of the Exception
	 */
	public IRingSet assignAtomTyps(IMolecule molecule) throws Exception {
		org.openscience.cdk.interfaces.IAtom atom = null;
		String hoseCode = "";
		HOSECodeGenerator hcg = new HOSECodeGenerator();
		int NumberOfRingAtoms = 0;
		IRingSet ringSetA = null;
		IRingSet ringSetMolecule = new SSSRFinder(molecule).findSSSR();
		boolean isInHeteroRing = false;
		try {
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			CDKHueckelAromaticityDetector.detectAromaticity(molecule);
		} catch (Exception cdk1) {
			throw new CDKException(
                "AROMATICITYError: Cannot determine aromaticity due to: " +
                cdk1.getMessage(), cdk1
            );
		}

		for (int i = 0; i < molecule.getAtomCount(); i++) {
			atom = molecule.getAtom(i);
			if (ringSetMolecule.contains(atom)) {
				NumberOfRingAtoms = NumberOfRingAtoms + 1;
				atom.setFlag(CDKConstants.ISINRING, true);
				atom.setFlag(CDKConstants.ISALIPHATIC, false);
				ringSetA = ringSetMolecule.getRings(atom);
				RingSetManipulator.sort(ringSetA);
				IRing sring = (IRing) ringSetA.getAtomContainer(ringSetA.getAtomContainerCount()-1);
				atom.setProperty("RING_SIZE", Integer.valueOf(sring.getRingSize()));
				isInHeteroRing = false;
				Iterator<IAtomContainer> containers = RingSetManipulator.getAllAtomContainers(ringSetA).iterator();
				while (!isInHeteroRing && containers.hasNext()) {
					isInHeteroRing = isHeteroRingSystem(containers.next());
				}
			} else {
				atom.setFlag(CDKConstants.ISALIPHATIC, true);
				atom.setFlag(CDKConstants.ISINRING, false);
				isInHeteroRing = false;
			}
			atom.setProperty("MAX_BOND_ORDER", new Double(molecule.getMaximumBondOrder(atom).ordinal()+1));

			try {
				hoseCode = hcg.getHOSECode(molecule, atom, 3);
				//logger.debug("HOSECODE GENERATION: ATOM "+i+" HoseCode: "+hoseCode+" ");
			} catch (CDKException ex1) {
				System.out.println("Could not build HOSECode from atom " + i + " due to " + ex1.toString());
				throw new CDKException("Could not build HOSECode from atom "+ i + " due to " + ex1.toString(), ex1);
			}
			try {
				configureAtom(atom, hoseCode, isInHeteroRing);
			} catch (CDKException ex2) {
				System.out.println("Could not final configure atom " + i + " due to " + ex2.toString());
				throw new Exception("Could not final configure atom due to problems with force field", ex2);
			}
		}
		
//		IBond[] bond = molecule.getBonds();
		String bondType;
        for (IBond bond : molecule.bonds()) {

			//logger.debug("bond[" + i + "] properties : " + molecule.getBond(i).getProperties());
			bondType = "0";
			if (bond.getOrder() == IBond.Order.SINGLE) {
				if ((bond.getAtom(0).getAtomTypeName().equals("Csp2")) &
					((bond.getAtom(1).getAtomTypeName().equals("Csp2")) | (bond.getAtom(1).getAtomTypeName().equals("C=")))) {
					bondType = "1";
				}
					
				if ((bond.getAtom(0).getAtomTypeName().equals("C=")) &
					((bond.getAtom(1).getAtomTypeName().equals("Csp2")) | (bond.getAtom(1).getAtomTypeName().equals("C=")))) {
					bondType = "1";}
					
				if ((bond.getAtom(0).getAtomTypeName().equals("Csp")) &
					(bond.getAtom(1).getAtomTypeName().equals("Csp"))) {
					bondType = "1";}
			}
//			molecule.getBond(i).setProperty("MMFF94 bond type", bondType);
            bond.setProperty("MMFF94 bond type", bondType);
            //logger.debug("bond[" + i + "] properties : " + molecule.getBond(i).getProperties());
		}

		return ringSetMolecule;
	}
	

	/**
	 *  Returns true if atom is in hetero ring system
	 *
	 *@param  ac  AtomContainer
	 *@return     true/false
	 */
	private boolean isHeteroRingSystem(IAtomContainer ac) {
		if (ac != null) {
			for (int i = 0; i < ac.getAtomCount(); i++) {
				if (!(ac.getAtom(i).getSymbol()).equals("H") && !(ac.getAtom(i).getSymbol()).equals("C")) {
					return true;
				}
			}
		}
		return false;
	}

	
	/**
	 *  Assigns an atom type to an atom
	 *
	 * @param  atom  The atom to be aasigned
	 * @param  ID    the atom type id
	 * @return       the assigned atom
	 */
	private org.openscience.cdk.interfaces.IAtom setAtom(org.openscience.cdk.interfaces.IAtom atom, String ID) throws Exception {
		IAtomType at = null;
		String key = "";
		List<?> data = null;
		Double value = null;
		
		at = getAtomType(ID);
		if (atom.getSymbol()==null){
			atom.setSymbol(at.getSymbol());
		}
		atom.setAtomTypeName(at.getAtomTypeName());
		atom.setFormalNeighbourCount(at.getFormalNeighbourCount());
		key = "vdw" + ID;
		data = (List) parameterSet.get(key);
		value = (Double) data.get(0);
		key = "charge" + ID;
		if (parameterSet.containsKey(key)) {
			data = (List) parameterSet.get(key);
			value = (Double) data.get(0);
			atom.setCharge(value.doubleValue());
		}
		Object color = at.getProperty("org.openscience.cdk.renderer.color");
		if (color != null) {
			atom.setProperty("org.openscience.cdk.renderer.color", color);
		}
		if (at.getAtomicNumber() != 0) {
			atom.setAtomicNumber(at.getAtomicNumber());
		} 
		if (at.getExactMass() > 0.0) {
			atom.setExactMass(at.getExactMass());
		} 
		return atom;
	}
	
	public org.openscience.cdk.interfaces.IAtom configureAtom(org.openscience.cdk.interfaces.IAtom atom, String hoseCode, boolean _boolean) throws Exception {
		if (ffName.equals("mm2")){
			return configureMM2BasedAtom(atom, hoseCode,_boolean);
		}else if (ffName.equals("mmff94")){
			return configureMMFF94BasedAtom(atom, hoseCode,_boolean);
		}
		return atom;
	}
	
	/**
	 *  Configures an atom to a mm2 based atom type
	 *
	 * @param  atom              atom to be configured
	 * @param  hoseCode          the 4 sphere hose code of the atom
	 * @return                   atom
	 * @exception  CDKException  Description of the Exception
	 */
	public IAtom configureMM2BasedAtom(IAtom atom, String hoseCode,boolean hetRing) throws Exception {
		//logger.debug("CONFIGURE MM2 ATOM");
		List<Pattern> atomTypePattern = null;
		MM2BasedAtomTypePattern atp = new MM2BasedAtomTypePattern();
		atomTypePattern = atp.getAtomTypePatterns();
		Double d_tmp = null;
		Pattern p = null;
		String ID = "";
		boolean atomTypeFlag = false;
		
		if (atom instanceof IPseudoAtom) {
			return atom;
		}
		
		hoseCode=removeAromaticityFlagsFromHoseCode(hoseCode);
		
		String [] ids={"C","Csp2","C=","Csp","HC","O","O=","N","Nsp2","Nsp",
				"F","CL","BR","I","S","S+",">SN","SO2","SI","LP","HO",
				"CR3R","HN","HOCO","P","B","BTET","HN2","C.","C+","GE",
				"SN","PB","SE","TE","D","NPYD","CE3R","N+","NPYL","Oar",
				"Sthi","N2OX","HS","=N=","NO3","OM","HN+","OR","Car","HE",
				"NE","AR","KR","XE","","","","MG","PTET","FE","FE","NI","NI","CO","CO",
				"","","OX","OK","C++","N=C","NPD+","N+=","N2OX"
		};
		
		for (int j = 0; j < atomTypePattern.size(); j++) {
			p = (Pattern) atomTypePattern.get(j);
			Matcher mat = p.matcher(hoseCode);
			if (mat.matches()) {
				ID = ids[j];
				//CHECK Rings 1,2,8,9? Thiole 44? AZO 9? Radical - ? Amid 23/enol 21?
				if (j == 0) {
					//csp3
					if (atom.getFlag(CDKConstants.ISINRING)) {
						if (atom.getProperty("RING_SIZE").equals(Integer.valueOf(3))) {
							ID=ids[21];
						}else if (atom.getProperty("RING_SIZE").equals(Integer.valueOf(6)) & atom.getFlag(CDKConstants.ISAROMATIC)) {
							ID=ids[1];
						}else if (atom.getFlag(CDKConstants.ISAROMATIC)){
							ID=ids[1];
						}
					}
				} else if (j == 1) {
					//csp2
					if (atom.getFlag(CDKConstants.ISINRING)) {
						if (atom.getProperty("RING_SIZE").equals(Integer.valueOf(6)) & atom.getFlag(CDKConstants.ISAROMATIC)) {
						}else if (atom.getProperty("RING_SIZE").equals(Integer.valueOf(3))) {
							ID=ids[37];
						}else{
							ID=ids[1];
							}
					}
					p = (Pattern) atomTypePattern.get(2);
					//COOH
					mat = p.matcher(hoseCode);
					if (mat.matches() & !atom.getFlag(CDKConstants.ISINRING)) {
						ID=ids[2];
					}
						
				} else if (j == 5) {
					//OH/Ether
					if (atom.getFlag(CDKConstants.ISINRING)) {
						if (atom.getProperty("RING_SIZE").equals(Integer.valueOf(3))) {
							ID=ids[48];
							//EPOXY
						}else if (atom.getProperty("RING_SIZE").equals(Integer.valueOf(5)) & atom.getFlag(CDKConstants.ISAROMATIC)){
							ID=ids[40];
						}else{
							ID=ids[5];
						}
					}
				} else if (j == 7) {
					//n sp3
					if (atom.getFlag(CDKConstants.ISINRING) & atom.getFlag(CDKConstants.ISAROMATIC)) {
						if (atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))) {
							ID=ids[39];
						}
					}
					//Amid
					p = (Pattern) atomTypePattern.get(77);
					mat = p.matcher(hoseCode);
					if (mat.matches() & !atom.getFlag(CDKConstants.ISINRING)) {
						ID=ids[8];
					}
				} else if (j == 8) {
					//nsp2
					if (atom.getFlag(CDKConstants.ISINRING)) {
						if (atom.getProperty("RING_SIZE").equals(Integer.valueOf(6))) {
							ID=ids[36];
						}
					}
					p = (Pattern) atomTypePattern.get(36);
					//AZO
					mat = p.matcher(hoseCode);
					if (mat.matches() & !atom.getFlag(CDKConstants.ISINRING)) {
						ID=ids[36];
					}
						
				} else if (j == 43) {
					//h thiol
					d_tmp = (Double) atom.getProperty("MAX_BOND_ORDER");
					if (d_tmp.doubleValue() > 1) {
						ID=ids[4];
					}
				} else if (j == 20) {
					//h alcohol,ether
					p = (Pattern) atomTypePattern.get(76);
					//Enol
					mat = p.matcher(hoseCode);
					if (mat.matches() & !atom.getFlag(CDKConstants.ISINRING)) {
						ID=ids[27];
					}
					p = (Pattern) atomTypePattern.get(23);
					//COOH
					mat = p.matcher(hoseCode);
					if (mat.matches() & !atom.getFlag(CDKConstants.ISINRING)) {
						ID=ids[23];
					}
				} else if (j == 22) {
					p = (Pattern) atomTypePattern.get(75);
					//Amid
					mat = p.matcher(hoseCode);
					if (mat.matches()) {
						ID=ids[27];
					}
				} 

				atomTypeFlag = true;
				//logger.debug("Atom Symbol:" + atom.getSymbol() + " MATCH AtomType> " + ID + " HoseCode>" + hoseCode + " ");
				break;
			}//IF
		}//for end
		if (atomTypeFlag) {
			atomTypeFlag = false;
			return setAtom(atom, ID);
		} else {
			throw new NoSuchAtomTypeException("Atom is unkown: Symbol:" + atom.getSymbol() + " does not MATCH AtomType. HoseCode:" + hoseCode);
		}
	}
	
	public String removeAromaticityFlagsFromHoseCode(String hoseCode){
		String hosecode="";
		for (int i=0;i<hoseCode.length();i++){
			if (hoseCode.charAt(i)!= '*'){
				hosecode=hosecode+hoseCode.charAt(i);
			}
		}
		return hosecode;
	}
	
	/**
	 *  Configures an atom to a mmff94 based atom type
	 *
	 * @param  atom              atom to be configured
	 * @param  hoseCode          the 4 sphere hose code of the atom
	 * @return                   atom
	 * @exception  CDKException  Description of the Exception
	 */
	public IAtom configureMMFF94BasedAtom(IAtom atom, String hoseCode, boolean isInHetRing) throws Exception {
		//logger.debug("****** Configure MMFF94 AtomType ******");
		List<Pattern> atomTypePattern = null;
		MMFF94BasedAtomTypePattern atp = new MMFF94BasedAtomTypePattern();
		atomTypePattern = atp.getAtomTypePatterns();
		Pattern p = null;
		Pattern p2 = null;
		String ID = "";
		boolean atomTypeFlag = false;
		Matcher mat=null;
		Matcher mat2=null;
		hoseCode=removeAromaticityFlagsFromHoseCode(hoseCode);
		
		String [] ids={"C","Csp2","C=","Csp","CO2M","CNN+","C%","CIM+","CR4R",
				"CR3R","CE4R","Car","C5A","C5B","C5","HC","HO","HN","HOCO",
				"HN=C","HN2","HOCC","HOH","HOS","HN+","HO+","HO=+","HP","O","O=",
				"OX","OM","O+","O=+","OH2","Oar","N","N=C","NC=C","NSP","=N=","NAZT",
				"N+","N2OX","N3OX","NC#N","NO3","N=O","NC=O","NSO","N+=","NCN+","NGD+","NR%",
				"NM","N5M","NPYD","NPYL","NPD+","N5A","N5B","NPOX","N5OX","N5+","N5","S",
				"S=C",">SN","SO2","SX","SO2M","=SO","Sthi","PTET","P","-P=C","F","CL","BR",
				"I","SI","CL04","FE+2","FE+3","F-","CL-","BR-","LI+","NA+","K+","ZN+2","CA+2","CU+1",
				"CU+2","MG+2","Du"
		};
		
		if (atom instanceof IPseudoAtom) {
			return atom;
		}
		
		for (int j = 0; j < atomTypePattern.size(); j++) {
			p = (Pattern) atomTypePattern.get(j);
			mat = p.matcher(hoseCode);
			if (mat.matches()) {
				ID = ids[j];
				if (j == 0) {//csp3
					if (atom.getFlag(CDKConstants.ISINRING)) {
						p = (Pattern) atomTypePattern.get(13);//c beta heteroaromatic ring
						mat = p.matcher(hoseCode);
						p2 = (Pattern) atomTypePattern.get(12);//c alpha heteroaromatic ring
						mat2 = p2.matcher(hoseCode);
						if (mat.matches() && isInHetRing && atom.getFlag(CDKConstants.ISAROMATIC) && atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))){
							ID = ids[13];
						}else if (mat2.matches() && isInHetRing && atom.getFlag(CDKConstants.ISAROMATIC) && atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))){
							ID = ids[12];
						}else if (atom.getProperty("RING_SIZE").equals(Integer.valueOf(3))& !atom.getFlag(CDKConstants.ISAROMATIC)) {
							ID = ids[9];//sp3 3mem rings
						}else if (atom.getProperty("RING_SIZE").equals(Integer.valueOf(4)) & !atom.getFlag(CDKConstants.ISAROMATIC)) {
							ID = ids[8];//sp3 4mem rings
						}else if (atom.getFlag(CDKConstants.ISAROMATIC) && isInHetRing && atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))) {
							ID = ids[14];//C in het 5 ring
						}else if (atom.getFlag(CDKConstants.ISAROMATIC)) {
							ID = ids[11];//C in benzene, pyroll
						}
					}else{
						p = (Pattern) atomTypePattern.get(66);//S=C
						mat = p.matcher(hoseCode);
						if (mat.matches()){
							ID = ids[66];
						}
					}
				} else if (j == 1) {//csp2
					if (atom.getFlag(CDKConstants.ISINRING)) {
						if (atom.getProperty("RING_SIZE").equals(Integer.valueOf(4)) & !atom.getFlag(CDKConstants.ISAROMATIC) && !isInHetRing) {
							ID = ids[29];//C= in 4 ring
						}
					}
					
				} else if (j == 2) {//csp2 C=Hetatom
					if (atom.getFlag(CDKConstants.ISINRING) && isInHetRing && atom.getFlag(CDKConstants.ISAROMATIC)) {
						ID = ids[12];
					}
				
				} else if (j == 36) {//n sp3
					//Amid
					p = (Pattern) atomTypePattern.get(48);
					mat = p.matcher(hoseCode);
					if (mat.matches() & !atom.getFlag(CDKConstants.ISINRING)) {
						ID = ids[48];
					}
					
					p = (Pattern) atomTypePattern.get(44);//sp3 n-oxide
					mat = p.matcher(hoseCode);
					if (mat.matches()){
						ID = ids[44];
					}
					
					p = (Pattern) atomTypePattern.get(56);//npyd
					mat = p.matcher(hoseCode);
					
					if (atom.getFlag(CDKConstants.ISAROMATIC)){//id in pyridin, pyrol etc...						if (mat.matches() && atom.getFlag(CDKConstants.ISAROMATIC) && atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))){
						if(atom.getProperty("RING_SIZE").equals(Integer.valueOf(6)) && mat.matches()){
							ID = ids[56];
						}else if(atom.getProperty("RING_SIZE").equals(Integer.valueOf(5)) && mat.matches()){
							ID = ids[57];
						}else{
							ID=ids[64];
						}
					}
					
					p = (Pattern) atomTypePattern.get(61);//npyd
					mat = p.matcher(hoseCode);
					if (atom.getFlag(CDKConstants.ISAROMATIC)){//id in pyridin, pyrol etc...						if (mat.matches() && atom.getFlag(CDKConstants.ISAROMATIC) && atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))){
						if(atom.getProperty("RING_SIZE").equals(Integer.valueOf(6)) && mat.matches()){
							ID = ids[61];
						}else if(atom.getProperty("RING_SIZE").equals(Integer.valueOf(5)) && mat.matches()){
							ID = ids[62];
						}else{
							ID=ids[43];
						}
					}
					
					p = (Pattern) atomTypePattern.get(45);//NC#N
					mat = p.matcher(hoseCode);
					if (mat.matches()){
						ID = ids[45];
					}
				
				}else if (j == 37) {//N=C n in imine
					p = (Pattern) atomTypePattern.get(59);//n beta heteroaromatic ring
					mat = p.matcher(hoseCode);
					if (atom.getFlag(CDKConstants.ISINRING)) {
						if (mat.matches() && isInHetRing && atom.getFlag(CDKConstants.ISAROMATIC) && atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))){
							ID = ids[59];
						}else if( atom.getFlag(CDKConstants.ISAROMATIC) && atom.getProperty("RING_SIZE").equals(Integer.valueOf(6))){
							ID = ids[56];
						}else if( atom.getFlag(CDKConstants.ISAROMATIC) && atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))){
							ID = ids[57];
						}
					}
					
					p = (Pattern) atomTypePattern.get(43);//N2OX
					mat = p.matcher(hoseCode);
					if (mat.matches()){
						if (atom.getFlag(CDKConstants.ISAROMATIC)&& atom.getProperty("RING_SIZE").equals(Integer.valueOf(6))){
							ID = ids[61];//npox
						}else if (mat.matches() && atom.getFlag(CDKConstants.ISAROMATIC) && atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))){
							ID = ids[62];//n5ox
						}else{
							ID = ids[43];
						}
					}
				
				}else if (j==43){//sp2 n oxide
					if (atom.getFlag(CDKConstants.ISINRING) && atom.getFlag(CDKConstants.ISAROMATIC)
						&& atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))){
						ID = ids[62];
					}else if (atom.getFlag(CDKConstants.ISINRING) && atom.getFlag(CDKConstants.ISAROMATIC)
						&& atom.getProperty("RING_SIZE").equals(Integer.valueOf(6))){
						ID = ids[61];
					}
				}else if (j==40 || j==41){//n in c=n=n or terminal n in azido
					if (atom.getFlag(CDKConstants.ISINRING) && atom.getFlag(CDKConstants.ISAROMATIC)
						&& atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))){
						ID = ids[59];//aromatic N 5R alpha
					}
				}else if (j==50){//n+= 
					if (atom.getFlag(CDKConstants.ISINRING) && atom.getFlag(CDKConstants.ISAROMATIC)
						&& atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))){
						ID = ids[63];//n5+
					}else if (atom.getFlag(CDKConstants.ISINRING) && atom.getFlag(CDKConstants.ISAROMATIC)
						&& atom.getProperty("RING_SIZE").equals(Integer.valueOf(6))){
						ID = ids[58];//npd+
					}
				}else if (j==28){//O ->furan
					if (atom.getFlag(CDKConstants.ISINRING) && atom.getFlag(CDKConstants.ISAROMATIC)
						&& atom.getProperty("RING_SIZE").equals(Integer.valueOf(5))){
							ID = ids[35];
					}
				}else if (j==16){//H-Object-> enol
					p = (Pattern) atomTypePattern.get(21);//enol
					mat = p.matcher(hoseCode);
					if (mat.matches()){
						ID = ids[21];
					}
					p = (Pattern) atomTypePattern.get(18);//enol
					mat = p.matcher(hoseCode);
					if (mat.matches()){
						ID = ids[18];
					}
					
				}else if (j==74){//P
					p = (Pattern) atomTypePattern.get(75);//-P=C
					mat = p.matcher(hoseCode);
					if (mat.matches()){
						ID = ids[75];
					}
				}
				
				atomTypeFlag = true;
				//logger.debug("Atom Symbol:" + atom.getSymbol() + " MATCH AtomType> " + ID + " HoseCode>" + hoseCode + " ");
				break;
			}//IF
		}//for end
		if (atomTypeFlag) {
			atomTypeFlag = false;
			return setAtom(atom, ID);
		} else {
			throw new NoSuchAtomTypeException("Atom is unkown: Symbol:" + atom.getSymbol() + " does not MATCH AtomType. HoseCode:" + hoseCode);
		}
	}
}


