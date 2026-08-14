package org.openscience.cdk.stereo;

import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.StringReader;

public class InorganicTest {

  @Test
  public void test() throws CDKException {
    String molfile = "\n" +
                     "  CDK     06272621542D\n" +
                     "\n" +
                     " 15 18  0  0  0  0  0  0  0  0999 V2000\n" +
                     "    3.3392   -3.4593    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    5.4962   -1.4746    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    6.8367   -2.1985    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    5.9373   -4.9593    0.0000 S   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    3.8883   -7.0083    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    5.9373   -3.4593    0.0000 As  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    4.6383   -4.2093    0.0000 Mo  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    2.6006   -6.1397    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    4.6383   -2.7093    0.0000 P   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    3.3392   -4.9593    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    1.8392   -3.4593    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    7.4373   -4.9593    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    1.8392   -4.9593    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    7.4373   -3.4593    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    4.6383   -5.7093    0.0000 Se  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "  6 14  1  0  0  0  0\n" +
                     "  7  1  1  6  0  0  0\n" +
                     " 13 10  1  0  0  0  0\n" +
                     "  5  8  1  0  0  0  0\n" +
                     " 15  5  1  0  0  0  0\n" +
                     "  6  3  1  0  0  0  0\n" +
                     " 12  4  1  0  0  0  0\n" +
                     " 14 12  1  0  0  0  0\n" +
                     "  1 11  1  0  0  0  0\n" +
                     " 11 13  1  0  0  0  0\n" +
                     "  7  4  1  1  0  0  0\n" +
                     "  7  9  1  0  0  0  0\n" +
                     " 10  8  1  0  0  0  0\n" +
                     "  7 15  1  0  0  0  0\n" +
                     "  7  6  1  6  0  0  0\n" +
                     "  3  2  1  0  0  0  0\n" +
                     "  7 10  1  1  0  0  0\n" +
                     "  9  2  1  0  0  0  0\n" +
                     "M  END\n" +
                     "$$$$\n";
    IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
    MDLV2000Reader mdlr = new MDLV2000Reader(new StringReader(molfile));
    IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
    InorganicConfig2d.create(mol);
  }

  @Test
  public void test2() throws CDKException {
    String molfile = "\n" +
                     "  CDK     06272621592D\n" +
                     "\n" +
                     "  7  6  0  0  0  0  0  0  0  0999 V2000\n" +
                     "    4.0175  -14.3132    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    3.5781  -13.2526    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    5.0782  -14.7525    0.0000 Se  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    6.1388  -12.1919    0.0000 As  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    5.0782  -13.2525    0.0000 Mo  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    6.5782  -13.2525    0.0000 S   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    5.0782  -11.7525    0.0000 P   0  0  0  0  0  1  0  0  0  0  0  0\n" +
                     "  5  4  1  6  0  0  0\n" +
                     "  5  7  1  0  0  0  0\n" +
                     "  5  2  1  0  0  0  0\n" +
                     "  5  6  1  0  0  0  0\n" +
                     "  5  1  1  1  0  0  0\n" +
                     "  5  3  1  0  0  0  0\n" +
                     "M  END\n" +
                     "$$$$\n";
    IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
    MDLV2000Reader mdlr = new MDLV2000Reader(new StringReader(molfile));
    IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
    InorganicConfig2d.create(mol);
  }

  @Test
  public void test3_distorted() throws CDKException {
    String molfile = "\n" +
                     "  CDK     06282607252D\n" +
                     "\n" +
                     "  7  6  0  0  0  0  0  0  0  0999 V2000\n" +
                     "   10.6817  -15.9350    0.0000 P   0  0  0  0  0  1  0  0  0  0  0  0\n" +
                     "   12.1305  -17.8232    0.0000 S   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "   10.6817  -18.9350    0.0000 Se  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "   12.1305  -17.0468    0.0000 As  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "   10.6817  -17.4350    0.0000 Mo  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    9.2327  -17.0468    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "    9.2327  -17.8232    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                     "  5  2  1  1  0  0  0\n" +
                     "  5  7  1  1  0  0  0\n" +
                     "  5  3  1  0  0  0  0\n" +
                     "  5  6  1  6  0  0  0\n" +
                     "  5  4  1  6  0  0  0\n" +
                     "  5  1  1  0  0  0  0\n" +
                     "M  END\n" +
                     "$$$$\n";
    IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
    MDLV2000Reader mdlr = new MDLV2000Reader(new StringReader(molfile));
    IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
    InorganicConfig2d.create(mol);
  }

}
