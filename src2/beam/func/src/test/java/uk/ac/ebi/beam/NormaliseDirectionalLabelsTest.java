package uk.ac.ebi.beam;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** @author John May */
public class NormaliseDirectionalLabelsTest {

    @Test public void simple() throws InvalidSmilesException {
        transform("F\\C=C\\F",
                  "F/C=C/F");
    }

    @Test public void ordering2() throws InvalidSmilesException {
        transform("C(\\F)(/C)=C\\F",
                  "C(/F)(\\C)=C/F");
    }

    @Test public void simple2() throws InvalidSmilesException {
        transform("C(\\F)=C\\F",
                  "C(/F)=C/F");
    }

    @Test public void partial() throws InvalidSmilesException {
        transform("FC=C(\\F)/C=C/F",
                  "FC=C(\\F)/C=C/F");
    }

    @Test public void partial2() throws InvalidSmilesException {
        transform("FC=C(F)C=C(F)\\C=C\\F",
                  "FC=C(F)C=C(F)/C=C/F");
    }

    @Test public void conjugated() throws InvalidSmilesException {
        transform("F\\C=C(\\F)/C(/F)=C\\F",
                  "F/C=C(/F)\\C(\\F)=C/F");
    }
    
    @Test public void cyclic() throws InvalidSmilesException {
        transform("C/C=C\\1/C\\C(=C\\C)\\C1",
                  "C/C=C\\1/C/C(=C/C)/C1");
        transform("C/C=C\\1/C/C(=C/C)/C1",
                  "C/C=C\\1/C/C(=C/C)/C1");
    }
    
    @Ignore("invalid structure")
    public void chebi15617() throws InvalidSmilesException {
        transform("C/C=C\\1/[C@@H](C)C(=O)N/C1=C\\C/2=N/C(=C\\C3=C(CCC(=O)O)C(=C(\\C=C/4\\C(=C(CC)C(=O)N4)C)N3)C)/C(=C2C)CCC(=O)O",
                  "C/C=C\\1/[C@@H](C)C(=O)N/C1=C\\C/2=N/C(=C\\C3=C(CCC(=O)O)C(=C(/C=C\\4/C(=C(CC)C(=O)N4)C)N3)C)/C(=C2C)CCC(=O)O");
        transform("C/C=C\\1/[C@@H](C)C(=O)N/C1=C\\C/2=N/C(=C\\C3=C(CCC(=O)O)C(=C(/C=C\\4/C(=C(CC)C(=O)N4)C)N3)C)/C(=C2C)CCC(=O)O",
                  "C/C=C\\1/[C@@H](C)C(=O)N/C1=C\\C/2=N/C(=C\\C3=C(CCC(=O)O)C(=C(/C=C\\4/C(=C(CC)C(=O)N4)C)N3)C)/C(=C2C)CCC(=O)O");
    }

    @Test public void chembl2064754() throws InvalidSmilesException {
        transform("CC(=O)OCC/C=1/SS/C(/CCOC(C)=O)=C(/C)\\N(C=O)CCCCCCCCCCCCN(C=O)\\C1\\C",
                  "CC(=O)OCC/C=1/SS/C(/CCOC(C)=O)=C(/C)\\N(C=O)CCCCCCCCCCCCN(C=O)\\C1\\C");
        transform("CC(=O)OCC\\C=1\\SS/C(/CCOC(C)=O)=C(/C)\\N(C=O)CCCCCCCCCCCCN(C=O)/C1/C",
                  "CC(=O)OCC/C=1/SS/C(/CCOC(C)=O)=C(/C)\\N(C=O)CCCCCCCCCCCCN(C=O)\\C1\\C");
    }
    
    @Test public void pubchem16088588() throws InvalidSmilesException {
        transform("CC(C)N1CC(=O)NC=2C=CC(=CC2)OC3=CC=C(C=C3)NC(=O)CN(C(=O)CO/N=C\\4/C[C@H]5C[C@@H](O)[C@H]6[C@@H]7CC[C@H]([C@H](C)CCC(=O)N(CC(=O)NC8=CC=C(C=C8)OC=9C=CC(=CC9)NC(=O)CN(C(=O)CC[C@@H](C)[C@H]%10CC[C@H]%11[C@@H]%12[C@H](O)C[C@@H]%13C/C(/CC[C@]%13(C)[C@H]%12C[C@H](O)[C@]%10%11C)=N/OCC1=O)C(C)C)C(C)C)[C@@]7(C)[C@@H](O)C[C@@H]6[C@@]5(C)CC4)C(C)C",
                  "CC(C)N1CC(=O)NC=2C=CC(=CC2)OC3=CC=C(C=C3)NC(=O)CN(C(=O)CO/N=C\\4/C[C@H]5C[C@@H](O)[C@H]6[C@@H]7CC[C@H]([C@H](C)CCC(=O)N(CC(=O)NC8=CC=C(C=C8)OC=9C=CC(=CC9)NC(=O)CN(C(=O)CC[C@@H](C)[C@H]%10CC[C@H]%11[C@@H]%12[C@H](O)C[C@@H]%13C/C(/CC[C@]%13(C)[C@H]%12C[C@H](O)[C@]%10%11C)=N/OCC1=O)C(C)C)C(C)C)[C@@]7(C)[C@@H](O)C[C@@H]6[C@@]5(C)CC4)C(C)C");
        transform("CC(C)N1CC(=O)NC=2C=CC(=CC2)OC3=CC=C(C=C3)NC(=O)CN(C(=O)CO\\N=C/4\\C[C@H]5C[C@@H](O)[C@H]6[C@@H]7CC[C@H]([C@H](C)CCC(=O)N(CC(=O)NC8=CC=C(C=C8)OC=9C=CC(=CC9)NC(=O)CN(C(=O)CC[C@@H](C)[C@H]%10CC[C@H]%11[C@@H]%12[C@H](O)C[C@@H]%13C\\C(\\CC[C@]%13(C)[C@H]%12C[C@H](O)[C@]%10%11C)=N\\OCC1=O)C(C)C)C(C)C)[C@@]7(C)[C@@H](O)C[C@@H]6[C@@]5(C)CC4)C(C)C",
                  "CC(C)N1CC(=O)NC=2C=CC(=CC2)OC3=CC=C(C=C3)NC(=O)CN(C(=O)CO/N=C\\4/C[C@H]5C[C@@H](O)[C@H]6[C@@H]7CC[C@H]([C@H](C)CCC(=O)N(CC(=O)NC8=CC=C(C=C8)OC=9C=CC(=CC9)NC(=O)CN(C(=O)CC[C@@H](C)[C@H]%10CC[C@H]%11[C@@H]%12[C@H](O)C[C@@H]%13C/C(/CC[C@]%13(C)[C@H]%12C[C@H](O)[C@]%10%11C)=N/OCC1=O)C(C)C)C(C)C)[C@@]7(C)[C@@H](O)C[C@@H]6[C@@]5(C)CC4)C(C)C");
    }


    @Test public void chembl294514_1() throws InvalidSmilesException {
        transform("Cc1c(CCC(=O)NCCOCCOCCN)c2/C=C/3\\N\\C(=C/c4[nH]c(/C=C/5\\N=C(CC6=N/C(=C\\c1[nH]2)/C(=C6C)CC)C(=C5CC)C)c(C)c4CCC(=O)NCCOCCOCCN)\\C(=C3CC)CC",
                  "Cc1c(CCC(=O)NCCOCCOCCN)c2/C=C/3\\N\\C(=C/c4[nH]c(/C=C/5\\N=C(CC6=N/C(=C\\c1[nH]2)/C(=C6C)CC)C(=C5CC)C)c(C)c4CCC(=O)NCCOCCOCCN)\\C(=C3CC)CC");
    }

    @Test public void chembl294514_2() throws InvalidSmilesException {
        transform("Cc1c(CCC(=O)NCCOCCOCCN)c2\\C=C\\3/N/C(=C\\c4[nH]c(/C=C/5\\N=C(CC6=N/C(=C\\c1[nH]2)/C(=C6C)CC)C(=C5CC)C)c(C)c4CCC(=O)NCCOCCOCCN)/C(=C3CC)CC",
                  "Cc1c(CCC(=O)NCCOCCOCCN)c2/C=C/3\\N\\C(=C/c4[nH]c(/C=C/5\\N=C(CC6=N/C(=C\\c1[nH]2)/C(=C6C)CC)C(=C5CC)C)c(C)c4CCC(=O)NCCOCCOCCN)\\C(=C3CC)CC");
    }
    
    @Test public void chembl147624() throws InvalidSmilesException {
        transform("COc1cc2nc(nc(N)c2cc1OC)N3CCN(CC3)S(=O)(=O)c4no[n+]([O-])c4C=5\\C=C/C=C\\C=C/C5",
                  "COc1cc2nc(nc(N)c2cc1OC)N3CCN(CC3)S(=O)(=O)c4no[n+]([O-])c4C=5/C=C\\C=C/C=C\\C5");
    }
    

    static void transform(String smi, String exp) throws
                                                  InvalidSmilesException {
        Assert.assertThat(Generator.generate(new NormaliseDirectionalLabels()
                                                     .apply(Parser.parse(smi))), CoreMatchers
                                  .is(exp));
    }

}
