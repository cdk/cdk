package test;

import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.templates.TestMoleculeFactory;

public class CDKSwingJSTest {
	
	public static void main(String[] args) {
	  test0();	
	}
	
	private static IChemObjectBuilder getBuilder() {
		return DefaultChemObjectBuilder.getInstance();
	}
	
	private static void test0() {
		String inchi = "InChI=1S/C41H44O22/c42-13-27-31(50)33(52)36(55)40(61-27)59-25-11-19(44)10-24-20(25)12-26(37(58-24)17-4-7-21(45)22(46)9-17)60-41-38(63-39-35(54)30(49)23(47)14-57-39)34(53)32(51)28(62-41)15-56-29(48)8-3-16-1-5-18(43)6-2-16/h1-12,23,27-28,30-36,38-42,47,49-55H,13-15H2,(H3-,43,44,45,46,48)/p+1/t23-,27-,28-,30+,31-,32-,33+,34+,35-,36-,38-,39+,40-,41-/m1/s1";
		IAtomContainer mol = TestMoleculeFactory.makeTetrahydropyran();
		System.out.println(mol.toString());
		try {
			InChIToStructure i2s = InChIGeneratorFactory.getInstance().getInChIToStructure(inchi, getBuilder(), "");
			mol = i2s.getAtomContainer();
			String inchi2 = InChIGeneratorFactory.getInstance().getInChIGenerator(mol).getInchi();
			System.out.println(inchi);
			System.out.println(inchi2);
			System.out.println(inchi.equals(inchi2));
		} catch (CDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}