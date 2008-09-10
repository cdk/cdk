/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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
package org.openscience.cdk.qsar.model.R2;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.model.QSARModelException;

/**
 * TestSuite that runs a test for the CNNRegressionModel
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module test-qsar
 */

public class CNNRegressionModelTest extends CDKTestCase {

    public CNNRegressionModelTest() {
    }

    public static Test suite() {
        return new TestSuite(org.openscience.cdk.qsar.model.R2.CNNRegressionModelTest.class);
    }

    public void testCNNRegressionModel() throws CDKException, Exception, QSARModelException {

        double[][] x = getXData();
        double[] y = getYData();

        CNNRegressionModel cnnrm = new CNNRegressionModel(x, y, 3);
        assertNotNull(RModel.getRengine());
        assertEquals("cdkCNNModel0", cnnrm.getModelName());

        Double[] Wts = getWeights();
        cnnrm.setParameters("Wts", Wts);
        cnnrm.build();
        assertNotNull(cnnrm.getModelObject());

        double value = cnnrm.getValue();
        assertEquals(value, 8.076735, .000001);

        double[] wts = cnnrm.getWts();
        assertTrue(wts != null);
        assertEquals(-1.635880, wts[0], .000001);
        assertEquals(-6.227619, wts[1], .000001);
        assertEquals(-4.639471, wts[2], .000001);
        assertEquals(-4.060546, wts[3], .000001);

        double[][] fitted = cnnrm.getFittedValues();
        assertTrue(fitted != null);
        assertEquals(0.527783, fitted[0][0], .000001);
        assertEquals(0.527783, fitted[1][0], .000001);
        assertEquals(0.527783, fitted[2][0], .000001);
        assertEquals(0.203857, fitted[6][0], .000001);

        /* Test predictions */
        Double[][] newx = {
                {new Double(9.81536768251), new Double(3.82849269659), new Double(7.22212024421)},
                {new Double(0.197449829806), new Double(0.324130354642), new Double(2.8329420321)},
                {new Double(0.548460836141), new Double(7.28037586863), new Double(8.13728493983)},
                {new Double(1.76049278788), new Double(6.41731766803), new Double(5.53986167864)},
                {new Double(3.4541825491), new Double(9.78038580407), new Double(3.58954097059)}
        };

        cnnrm.setParameters("newdata", newx);
        cnnrm.setParameters("type", "raw"); // this is set by default so no real need
        cnnrm.predict();

        double[][] preds = cnnrm.getPredictions();
        assertTrue(preds != null);
        assertEquals(0.527783, preds[0][0], 0.000001);
        assertEquals(0.401678, preds[1][0], 0.000001);
        assertEquals(0.390702, preds[2][0], 0.000001);
        assertEquals(0.527783, preds[3][0], 0.000001);
        assertEquals(0.527783, preds[4][0], 0.000001);

        assertTrue(cnnrm.summary() != null);
    }

    public void testModelLoadSave() throws QSARModelException {
        double[][] x = getXData();
        double[] y = getYData();

        CNNRegressionModel cnnrm = new CNNRegressionModel(x, y, 3);
        assertNotNull(RModel.getRengine());
        assertEquals("cdkCNNModel1", cnnrm.getModelName());

        Double[] Wts = getWeights();
        cnnrm.setParameters("Wts", Wts);
        cnnrm.build();
        cnnrm.saveModel(cnnrm.getModelName(), "cnntest.Rda");

        CNNRegressionModel loadedModel = new CNNRegressionModel();
        loadedModel.loadModel("cnntest.Rda");

        System.out.println("loadedModel.getModelName() = " + loadedModel.getModelName());

//        assertEquals("cdkCNNModel0", loadedModel.getModelName());
        assertNotNull(loadedModel.getModelObject());

        double[] wts = cnnrm.getWts();
        assertTrue(wts != null);
        assertEquals(-1.635880, wts[0], .000001);
        assertEquals(-6.227619, wts[1], .000001);
        assertEquals(-4.639471, wts[2], .000001);
        assertEquals(-4.060546, wts[3], .000001);

        double[][] fitted = cnnrm.getFittedValues();
        assertTrue(fitted != null);
        assertEquals(0.527783, fitted[0][0], .000001);
        assertEquals(0.527783, fitted[1][0], .000001);
        assertEquals(0.527783, fitted[2][0], .000001);
        assertEquals(0.203857, fitted[6][0], .000001);

        /* Test predictions */
        Double[][] newx = {
                {new Double(9.81536768251), new Double(3.82849269659), new Double(7.22212024421)},
                {new Double(0.197449829806), new Double(0.324130354642), new Double(2.8329420321)},
                {new Double(0.548460836141), new Double(7.28037586863), new Double(8.13728493983)},
                {new Double(1.76049278788), new Double(6.41731766803), new Double(5.53986167864)},
                {new Double(3.4541825491), new Double(9.78038580407), new Double(3.58954097059)}
        };

        loadedModel.setParameters("newdata", newx);
        loadedModel.setParameters("type", "raw"); // this is set by default so no real need
        loadedModel.predict();

        double[][] preds = loadedModel.getPredictions();
        assertTrue(preds != null);
        assertEquals(0.527783, preds[0][0], 0.000001);
        assertEquals(0.401678, preds[1][0], 0.000001);
        assertEquals(0.390702, preds[2][0], 0.000001);
        assertEquals(0.527783, preds[3][0], 0.000001);
        assertEquals(0.527783, preds[4][0], 0.000001);


    }


    private Double[] getWeights() {
        return new Double[]{
                new Double(0.51404345), new Double(0.50840113), new Double(0.74685975), new Double(0.13561035),
                new Double(0.29352420), new Double(0.82764496), new Double(0.48282801), new Double(0.36164355),
                new Double(0.33401877), new Double(0.58141624), new Double(0.08336752), new Double(0.72385802),
                new Double(0.94860029), new Double(0.32215781), new Double(0.32806066), new Double(0.66709418)
        };
    }

    private double[] getYData() {
        return new double[]{0.548279405588, 0.749557798438, 0.704786225556, 0.064272559019, 0.959196778261, 0.443650457811, 0.139588310157, 0.697614953528, 0.894633307417, 0.288986449536, 0.968020911596, 0.00941763156173, 0.803870693657, 0.457124742168, 0.728543899161, 0.88083354383, 0.624089352674, 0.470379461181, 0.86877991158, 0.622721685808, 0.0250057478044, 0.2376603194, 0.112920370051, 0.608780223601, 0.62741359624, 0.39753977229, 0.396823887458, 0.0259021311271, 0.433022176171, 0.94665816668, 0.788805032857, 0.831096752197, 0.981239642073, 0.72411413954, 0.585272152663, 0.694317542691, 0.890624533901, 0.244048473797, 0.422902339036, 0.597269134374, 0.911340032927, 0.00186723050398, 0.439586593554, 0.714613974993, 0.815341829936, 0.726336948414, 0.742772100572, 0.597295528478, 0.305955366581, 0.155579392014, 0.000873693540479, 0.339225424495, 0.433434106377, 0.109738110471, 0.0193980726758, 0.258795872246, 0.322462583569, 0.326807898424, 0.079866937163, 0.741776416238, 0.597174006951, 0.289816194377, 0.691182117374, 0.113315930392, 0.302120795811, 0.616653275971, 0.833480904688, 0.881803762099, 0.734675438389, 0.269429129873, 0.977225860294, 0.327410536298, 0.319292292397, 0.876227987007, 0.832930007711, 0.941552570764, 0.0433177729231, 0.333665283905, 0.889264621262, 0.367930824862, 0.143633644589, 0.0106269520474, 0.623817520313, 0.237853599409, 0.301794094647, 0.912166461213, 0.663976930266, 0.918081800984, 0.909573924607, 0.976541368479, 0.340915467396, 0.617160565805, 0.0315242385532, 0.869413665191, 0.695610662213, 0.144537534715, 0.619567870639, 0.159550199731, 0.536333432502, 0.837898880743};
    }

    private double[][] getXData() {
        return new double[][]{{5.33029143313, 8.13257437501, 2.66720308462},
                {3.29906147519, 5.06835102093, 6.47319431067},
                {5.69553153292, 5.88043843898, 9.73312992111},
                {5.29194559083, 6.78243188133, 3.2602449344},
                {6.18105762768, 3.36588488672, 3.94539328809},
                {1.32223357975, 8.78797039033, 7.77485740688},
                {0.391740629966, 5.08060997023, 8.28722389016},
                {4.27475126706, 8.52015977633, 7.21468545649},
                {7.14131409262, 8.67086866827, 7.64228671009},
                {8.55502719447, 5.25013245421, 5.73240025988},
                {5.31791067667, 7.99313789208, 1.64835209014},
                {9.03149835466, 1.94042287241, 9.28020345543},
                {0.925468187342, 4.97155215507, 7.69457858258},
                {9.16182426614, 4.74534182996, 6.58111071706},
                {1.15220637861, 1.78078924823, 2.24407287943},
                {9.24209878847, 7.87658524713, 2.38732162601},
                {8.50715035908, 9.16453417058, 0.618727514944},
                {5.84019865932, 5.20208546615, 6.61838858253},
                {3.76256505014, 0.329738943471, 0.874419640166},
                {9.96004184517, 9.14019090437, 4.90929645109},
                {4.44743194213, 3.95642974577, 7.62629150218},
                {1.24177865105, 1.48660423923, 1.20830798956},
                {8.35590316383, 1.14743031542, 6.29868134513},
                {6.12876561357, 4.63929392357, 5.87722199543},
                {8.11829752127, 0.13950274139, 2.54723293455},
                {4.40852772122, 5.07291389291, 0.100128243526},
                {2.58403059855, 1.78831569742, 5.19817475725},
                {8.04282601008, 3.76076347262, 1.43904088129},
                {3.43713025153, 4.35105074191, 0.0189145485124},
                {5.0236445539, 1.06317719489, 5.10306592945},
                {9.77434875025, 9.0666617274, 6.99448050277},
                {4.06797047248, 7.62659701718, 9.83152424086},
                {6.48920287132, 0.156594507329, 5.46872113685},
                {6.42883928789, 2.01940454563, 6.46523071259},
                {1.16293901493, 5.15391581673, 3.56182526491},
                {7.38000931385, 0.453325117578, 6.61031329357},
                {9.32963370626, 2.12590745134, 0.405388324151},
                {0.737255223472, 7.39059871721, 2.86079226118},
                {6.85301380605, 2.1615949728, 8.87574040247},
                {3.74156226774, 4.24620341057, 4.35371571862},
                {2.18208535888, 3.53972126321, 3.59052000965},
                {4.72006492073, 3.3574566235, 9.62444364758},
                {2.84331278854, 1.74554945195, 4.51285607572},
                {3.86999763691, 9.49323614413, 5.08797427552},
                {7.43099014174, 6.2755590307, 4.57542355747},
                {6.01320531795, 8.25706473123, 7.40439342966},
                {6.46384266575, 3.51112862363, 9.47435948698},
                {2.29011620065, 0.401145254435, 7.28671287627},
                {5.7219136188, 4.43209346253, 0.0622901932013},
                {4.30214056802, 1.68925570283, 7.89926376252},
                {0.64305256706, 8.22063584536, 4.33019352991},
                {6.44843380824, 9.10336359279, 6.8777037869},
                {2.45354486215, 5.34166315571, 8.04822795875},
                {9.13675572384, 8.19635101591, 6.85475060116},
                {8.0492824201, 7.55216736195, 3.73472402973},
                {4.40590062277, 5.27106603309, 2.59962025805},
                {0.313960278741, 0.11866096726, 4.07985095305},
                {0.462136466507, 0.415202739102, 1.03258083165},
                {6.74723654049, 7.7080622951, 7.22322407979},
                {1.97571555403, 3.18544339131, 5.56211977273},
                {3.14021838165, 0.81551917817, 3.95156287418},
                {4.05709817216, 2.98004731237, 5.8975379443},
                {4.25420450429, 7.78663760941, 5.98061090504},
                {6.2650372416, 7.96507652177, 6.43631309268},
                {0.248308143147, 5.07557198176, 7.06413762375},
                {2.83741089895, 0.652445391344, 3.32535947415},
                {5.98115064142, 9.88913498552, 9.3923706794},
                {3.45667026676, 1.37451287268, 2.35331272082},
                {7.83964781871, 2.22111016571, 9.10723793073},
                {0.509210152705, 7.97088780188, 6.17963669424},
                {5.50910552235, 6.92372624674, 8.43151367671},
                {9.94686419266, 5.16899669191, 1.77353096261},
                {1.46501561342, 4.39317416608, 4.66752677391},
                {7.34126711314, 1.50352255841, 7.42777093653},
                {6.80122177161, 2.48753341584, 4.30535748793},
                {3.43057685209, 9.11458889251, 8.1389601215},
                {7.82076320157, 4.99727977399, 8.31875065375},
                {8.62799832715, 5.67304190345, 1.40517550057},
                {2.20910090066, 5.45236965227, 0.190013284925},
                {8.27876352499, 3.23706166886, 6.23912802837},
                {8.69440791615, 0.729194277167, 3.45645694332},
                {8.30552885891, 2.53977734839, 0.498635632483},
                {6.35009207052, 5.87727519703, 4.92604761655},
                {2.21876644613, 3.85669457256, 9.44139826683},
                {5.49181700898, 1.69048597254, 2.29475976286},
                {3.79777411904, 0.437885574937, 8.10175192316},
                {8.11720195104, 8.84115458961, 6.25490466144},
                {4.58878775312, 5.51332276174, 3.85400216514},
                {6.01729101329, 9.69817519935, 7.63607038602},
                {4.14247512757, 9.633551519, 0.543555309265},
                {1.69925453337, 4.77655288911, 0.950497583032},
                {3.84897216241, 3.27769006984, 9.17922626403},
                {2.79348258306, 4.38230737375, 7.26219595942},
                {4.88988551153, 2.95206506434, 3.65797143803},
                {1.91134803528, 0.829719567085, 1.73891604909},
                {5.5514711696, 8.80684284298, 2.66911304157},
                {2.95100011358, 0.832983961872, 4.19266815334},
                {4.19942346415, 5.92478285192, 8.33053966924},
                {3.11127058351, 3.25340097022, 7.07258377268},
                {7.61105416732, 8.46642439572, 5.61730141222}};
    }

}
