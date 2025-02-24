package test;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.renderer.generators.standard.TextOutline;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.LoggingToolFactory;

import swingjs.SwingJSLogger;

public class CDKSwingJSTest {
	private static boolean isJS = /** @j2sNative true || */false;
	public static void main(String[] args) {
		if (isJS) {
			LoggingToolFactory.setLoggingToolClass(SwingJSLogger.class);
		}
		InChIGeneratorFactory.getInstance(() -> {
			//testFonts();
			test0();
		});
	
	}
	
	private static IChemObjectBuilder getBuilder() {
		return DefaultChemObjectBuilder.getInstance();
	}
	
	private static void test0() {
		// N variant
		long t0 = System.currentTimeMillis();

		String inchi = "InChI=1S/C41H45NO21/c43-13-27-32(51)34(53)37(56)40(61-27)59-25-11-19(45)10-21-20(25)12-26(30(42-21)17-4-7-22(46)23(47)9-17)60-41-38(63-39-36(55)31(50)24(48)14-58-39)35(54)33(52)28(62-41)15-57-29(49)8-3-16-1-5-18(44)6-2-16/h1-12,24,27-28,31-41,43-48,50-56H,13-15H2"
				+ "/b8-3+/t24-,27-,28-,31+,32-,33-,34+,35+,36-,37-,38-,39+,40-,41-/m1/s1";
		IAtomContainer mol = TestMoleculeFactory.makeTetrahydropyran();
		mol.getAtom(0).setImplicitHydrogenCount(0);
		for (int i = 1; i < 6; i++)
			mol.getAtom(i).setImplicitHydrogenCount(2);
		try {
			mol = InChIGeneratorFactory.getInstance().getInChIToStructure(inchi, getBuilder(), "").withCoordinates("2D")
					.getAtomContainer();
			String inchi2 = InChIGeneratorFactory.getInstance().getInChIGenerator(mol).getInchi();
			System.out.println(inchi);
			System.out.println(inchi2);
			System.out.println(inchi.equals(inchi2));

			// inchi to mol

			String smi = new SmilesGenerator(SmiFlavor.Isomeric).create(mol);
			System.out.println(smi);
			// C=1C=C(C=CC1\C(\[H])=C(/[H])\C(=O)OC[C@]2([H])[C@]([H])([C@@]([H])([C@]([H])([C@]([H])(OC=3C=C4C(C=C(C=C4O[C@]5([C@]([C@@]([C@]([C@](CO)([H])O5)([H])O)([H])O)([H])O)[H])O)=NC3C=6C=CC(=C(C6)O)O)O2)O[C@]7([C@]([C@@]([C@](CO7)([H])O)([H])O)([H])O)[H])O)O)O

			
			getDataURLForInChI(inchi2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println((System.currentTimeMillis() - t0) + " ms");
	}


	public static String getDataURLForInChI(String inchi) {
		//inchi = "InChI=1S/C2H6O/c1-3-2/h1-2H3";//"InChI=1S/H2O/h1H2";
		try {
			IAtomContainer mol = InChIGeneratorFactory.getInstance().getInChIToStructure(inchi, getBuilder(), "").withCoordinates("2D")
					.getAtomContainer();
			DepictionGenerator dg = new DepictionGenerator().withSize(600,600);
			BufferedImage image = dg.depict(mol).toImg();
			
			//ByteArrayOutputStream bos = new ByteArrayOutputStream();
			FileOutputStream bos = new FileOutputStream("c:/temp/testcdk.png");
			ImageIO.write(image, "PNG", bos);
			bos.close();
//			String s = new BASE64Encoder().encode(bos.toByteArray());
			return "";//"data:image/png;base64," + s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	public static void testFonts() {
		// used for testing font business
		String[] s = new String[] { "H", "j", "Hj", "HjHjHj" };
		Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
		for (int i = 0; i < s.length; i++) {
			TextOutline t = new TextOutline(s[i], f);
		}
	}
}