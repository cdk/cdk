/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CKD) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.Isotope;

/**
 * Used to store data of a particular isotope.
 *
 * @cdkPackage standard
 *
 * @cdk.keyword isotope
 */
public class StandardIsotopes {

    public StandardIsotopes()
    {
    }

    public int getSize()
    {
        return isotopes.length;
    }

    public Isotope elementAt(int pos)
    {
        if (pos >= 0 && pos < getSize())
            return (Isotope) isotopes[pos].clone();
        return null;
    }

    /** 
     *  Returns the the most abundant isotope (major isotope) given
     *  an element symbol.
     *
     * @param  element      element symbol of the requested isotope
     * @return The major isotope with element symbol element
     */
    public Isotope getMajorIsotope(String element)
    {
        Isotope isotope = null;
        for (int f = 0; f < getSize(); f++)
        {
            if (isotopes[f].getSymbol().equals(element))
            {
                if ((int) isotopes[f].naturalAbundance == 100)
                {
                    isotope = (Isotope) isotopes[f].clone();
                }
            }
            if (isotope != null)
                return isotope;
        }
        return null;
    }

    private void enlargeIsotopeArray()
    {
        int newSize = (int)(isotopes.length * 1.1);
        Isotope[] newIsotopes = new Isotope[newSize];
        System.arraycopy(isotopes, 0, newIsotopes, 0, isotopes.length);
        isotopes = newIsotopes;
    }


	    Isotope[] isotopes = {
	 new Isotope(1,"H",1.00782504, 100.0000),
	 new Isotope(2,"H",2.01410179, 0.0150),
	 new Isotope(2,"D",2.01410179, 100.0000),
	 new Isotope(3,"He",3.01602929, 0.0001),
	 new Isotope(4,"He",4.00260325, 100.0000),
	 new Isotope(6,"Li",6.01512320, 8.1081),
	 new Isotope(7,"Li",7.01600450, 100.0000),
	 new Isotope(9,"Be",9.01218250, 100.0000),
	 new Isotope(10,"B",10.0129380, 24.8439),
	 new Isotope(11,"B",11.0093053, 100.0000),
	 new Isotope(12,"C",12.0000000, 100.0000),
	 new Isotope(13,"C",13.0033548, 1.1122),
	 new Isotope(14,"N",14.0030740, 100.0000),
	 new Isotope(15,"N",15.0001090, 0.3673),
	 new Isotope(16,"O",15.9949146, 100.0000),
	 new Isotope(17,"O",16.9991306, 0.0381),
	 new Isotope(18,"O",17.9991594, 0.2005),
	 new Isotope(19,"F",18.9984033, 100.0000),
	 new Isotope(20,"Ne",19.9924391, 100.0000),
	 new Isotope(21,"Ne",20.9938453, 0.2983),
	 new Isotope(22,"Ne",21.9913837, 10.1867),
	 new Isotope(23,"Na",22.9897697, 100.0000),
	 new Isotope(24,"Mg",23.9850450, 100.0000),
	 new Isotope(25,"Mg",24.9858392, 12.6598),
	 new Isotope(26,"Mg",25.9825954, 13.9380),
	 new Isotope(27,"Al",26.9815413, 100.0000),
	 new Isotope(28,"Si",27.9769284, 100.0000),
	 new Isotope(29,"Si",28.9764964, 5.0634),
	 new Isotope(30,"Si",29.9737717, 3.3612),
	 new Isotope(31,"P",30.9737634, 100.0000),
	 new Isotope(32,"S",31.9720718, 100.0000),
	 new Isotope(33,"S",32.9714591, 0.7893),
	 new Isotope(34,"S",33.9678677, 4.4306),
	 new Isotope(36,"S",35.9670790, 0.0220),
	 new Isotope(35,"Cl",34.9688527, 100.0000),
	 new Isotope(37,"Cl",36.9659026, 31.9780),
	 new Isotope(39,"K",38.9637079, 100.0000),
	 new Isotope(40,"K",39.9639988, 0.0130),
	 new Isotope(42,"K",40.9618254, 7.2170),
	 new Isotope(36,"Ar",35.9675456, 0.3380),
	 new Isotope(38,"Ar",37.9627322, 0.0630),
	 new Isotope(40,"Ar",39.9623831, 100.0000),
	 new Isotope(40,"Ca",39.9625907, 100.0000),
	 new Isotope(42,"Ca",41.9586218, 0.6674),
	 new Isotope(43,"Ca",42.9587704, 0.1393),
	 new Isotope(44,"Ca",43.9554848, 2.1518),
	 new Isotope(46,"Ca",45.9536890, 0.0041),
	 new Isotope(48,"Ca",47.9525320, 0.1929),
	 new Isotope(45,"Sc",44.9559136, 100.0000),
	 new Isotope(46,"Ti",45.9526330, 10.8401),
	 new Isotope(47,"Ti",46.9517650, 9.8916),
	 new Isotope(48,"Ti",47.9479470, 100.0000),
	 new Isotope(49,"Ti",48.9478710, 7.4526),
	 new Isotope(50,"Ti",49.9447860, 7.3171),
	 new Isotope(50,"V",49.9471610, 0.2510),
	 new Isotope(51,"V",50.9439630, 100.0000),
	 new Isotope(50,"Cr",49.9460460, 5.1915),
	 new Isotope(52,"Cr",51.9405100, 100.0000),
	 new Isotope(53,"Cr",52.9406510, 11.3379),
	 new Isotope(54,"Cr",53.9388820, 2.8166),
	 new Isotope(55,"Mn",54.9380460, 100.0000),
	 new Isotope(54,"Fe",53.9396120, 6.3236),
	 new Isotope(56,"Fe",55.9349390, 100.0000),
	 new Isotope(57,"Fe",56.9353960, 2.3986),
	 new Isotope(58,"Fe",57.9332780, 0.3053),
	 new Isotope(58,"Ni",57.9353470, 100.0000),
	 new Isotope(60,"Ni",59.9307890, 38.2305),
	 new Isotope(61,"Ni",60.9310590, 1.6552),
	 new Isotope(62,"Ni",61.9283460, 5.2585),
	 new Isotope(64,"Ni",63.9279680, 1.3329),
	 new Isotope(59,"Co",58.9331980, 100.0000),
	 new Isotope(63,"Cu",62.9295991, 100.0000),
	 new Isotope(65,"Cu",64.9277921, 44.5710),
	 new Isotope(64,"Zn",63.9291450, 100.0000),
	 new Isotope(66,"Zn",65.9260350, 57.4074),
	 new Isotope(67,"Zn",66.9271290, 8.4362),
	 new Isotope(68,"Zn",67.9248460, 38.6831),
	 new Isotope(70,"Zn",69.9253250, 1.2346),
	 new Isotope(69,"Ga",68.9255810, 100.0000),
	 new Isotope(71,"Ga",70.9247010, 66.3890),
	 new Isotope(70,"Ge",69.9242500, 56.1644),
	 new Isotope(72,"Ge",71.9220800, 75.0685),
	 new Isotope(73,"Ge",72.9234640, 21.3698),
	 new Isotope(74,"Ge",73.9211790, 100.0000),
	 new Isotope(76,"Ge",75.9214030, 21.3698),
	 new Isotope(75,"As",74.9215960, 100.0000),
	 new Isotope(79,"Br",78.9183360, 100.0000),
	 new Isotope(81,"Br",80.9162900, 97.2776),
	 new Isotope(74,"Se",73.9224770, 1.8145),
	 new Isotope(76,"Se",75.9192070, 18.1451),
	 new Isotope(77,"Se",76.9199080, 15.3226),
	 new Isotope(78,"Se",77.9173040, 47.3790),
	 new Isotope(80,"Se",79.9165210, 100.0000),
	 new Isotope(82,"Se",81.9167090, 18.9516),
	 new Isotope(78,"Kr",77.9203970, 0.6140),
	 new Isotope(80,"Kr",79.9163750, 3.9474),
	 new Isotope(82,"Kr",81.9134830, 20.3509),
	 new Isotope(83,"Kr",82.9141340, 20.1754),
	 new Isotope(84,"Kr",83.9115060, 100.0000),
	 new Isotope(86,"Kr",85.9106140, 30.3509),
	 new Isotope(85,"Rb",84.9117996, 100.0000),
	 new Isotope(87,"Rb",86.9091840, 38.5710),
	 new Isotope(84,"Sr",83.9134280, 0.6781),
	 new Isotope(86,"Sr",85.9092730, 11.9399),
	 new Isotope(87,"Sr",86.9088900, 8.4766),
	 new Isotope(88,"Sr",87.9056250, 100.0000),
	 new Isotope(89,"Y",88.9058560, 100.0000),
	 new Isotope(90,"Zr",89.9047080, 100.0000),
	 new Isotope(91,"Zr",90.9056440, 21.9048),
	 new Isotope(92,"Zr",91.9050390, 33.3722),
	 new Isotope(94,"Zr",93.9063190, 33.6832),
	 new Isotope(96,"Zr",95.9082720, 5.4033),
	 new Isotope(93,"Nb",92.9063780, 100.0000),
	 new Isotope(92,"Mo",91.9068090, 61.5002),
	 new Isotope(94,"Mo",93.9050860, 38.3340),
	 new Isotope(95,"Mo",94.9058380, 65.9760),
	 new Isotope(96,"Mo",95.9046760, 69.1256),
	 new Isotope(97,"Mo",96.9060180, 39.5773),
	 new Isotope(98,"Mo",97.9054050, 100.0000),
	 new Isotope(100,"Mo",99.9074730, 39.9088),
	 new Isotope(96,"Ru",95.9075960, 17.4684),
	 new Isotope(98,"Ru",97.9052870, 5.9494),
	 new Isotope(99,"Ru",98.9059370, 40.1899),
	 new Isotope(100,"Ru",99.9042180, 39.8734),
	 new Isotope(101,"Ru",100.905581, 53.7975),
	 new Isotope(102,"Ru",101.904348, 100.0000),
	 new Isotope(104,"Ru",103.905422, 59.1772),
	 new Isotope(103,"Rh",102.905503, 100.0000),
	 new Isotope(102,"Pd",101.905609, 3.7322),
	 new Isotope(104,"Pd",103.904026, 40.7611),
	 new Isotope(105,"Pd",104.905075, 81.7051),
	 new Isotope(106,"Pd",105.903475, 100.0000),
	 new Isotope(108,"Pd",107.903894, 96.8167),
	 new Isotope(110,"Pd",109.905169, 42.883),
	 new Isotope(107,"Ag",106.905095, 100.0000),
	 new Isotope(109,"Ag",108.904754, 92.9050),
	 new Isotope(106,"Cd",105.906461, 4.3508),
	 new Isotope(108,"Cd",107.904186, 3.0978),
	 new Isotope(110,"Cd",109.903007, 43.4737),
	 new Isotope(111,"Cd",110.904182, 44.5527),
	 new Isotope(112,"Cd",111.902761, 83.9888),
	 new Isotope(113,"Cd",112.904401, 42.5339),
	 new Isotope(114,"Cd",113.903361, 100.0000),
	 new Isotope(116,"Cd",115.904758, 26.0703),
	 new Isotope(113,"In",112.904056, 4.4932),
	 new Isotope(115,"In",114.903875, 100.0000),
	 new Isotope(112,"Sn",111.904823, 3.0864),
	 new Isotope(114,"Sn",113.902781, 2.1605),
	 new Isotope(115,"Sn",114.903344, 1.2346),
	 new Isotope(116,"Sn",115.901744, 45.3704),
	 new Isotope(117,"Sn",116.902954, 23.7654),
	 new Isotope(118,"Sn",117.901607, 75.0000),
	 new Isotope(119,"Sn",118.903310, 26.5432),
	 new Isotope(120,"Sn",119.902199, 100.0000),
	 new Isotope(122,"Sn",121.903440, 14.1975),
	 new Isotope(124,"Sn",123.905271, 17.2840),
	 new Isotope(121,"Sb",120.903824, 100.0000),
	 new Isotope(123,"Sb",122.904222, 74.5201),
	 new Isotope(127,"I",126.904477, 100.0000),
	 new Isotope(120,"Te",119.904021, 0.2840),
	 new Isotope(122,"Te",121.903055, 7.6923),
	 new Isotope(123,"Te",122.904278, 2.6864),
	 new Isotope(124,"Te",123.902825, 14.2485),
	 new Isotope(125,"Te",124.904435, 21.1243),
	 new Isotope(126,"Te",125.903310, 56.0651),
	 new Isotope(128,"Te",127.904464, 93.7574),
	 new Isotope(130,"Te",129.906229, 100.0000),
	 new Isotope(124,"Xe",123.906120, 0.3717),
	 new Isotope(126,"Xe",125.904281, 0.3346),
	 new Isotope(128,"Xe",127.903531, 7.1004),
	 new Isotope(129,"Xe",128.904780, 98.1413),
	 new Isotope(130,"Xe",129.903510, 15.2416),
	 new Isotope(131,"Xe",130.905080, 78.8104),
	 new Isotope(132,"Xe",131.904148, 100.0000),
	 new Isotope(134,"Xe",133.905395, 38.6617),
	 new Isotope(136,"Xe",135.907219, 33.0855),
	 new Isotope(133,"Cs",132.905433, 100.0000),
	 new Isotope(130,"Ba",129.906277, 0.1478),
	 new Isotope(132,"Ba",131.905042, 0.1409),
	 new Isotope(134,"Ba",133.904490, 3.3710),
	 new Isotope(135,"Ba",134.905668, 9.1939),
	 new Isotope(136,"Ba",135.904556, 10.9540),
	 new Isotope(137,"Ba",136.905816, 15.6625),
	 new Isotope(138,"Ba",137.905236, 100.0000),
	 new Isotope(138,"La",137.907114, 0.0901),
	 new Isotope(139,"La",138.906355, 100.0000),
	 new Isotope(136,"Ce",135.907140, 0.2147),
	 new Isotope(138,"Ce",137.905996, 0.2825),
	 new Isotope(140,"Ce",139.905442, 100.0000),
	 new Isotope(142,"Ce",141.909249, 12.5226),
	 new Isotope(141,"Pr",140.907657, 100.0000),
	 new Isotope(142,"Nd",141.907731, 100.0000),
	 new Isotope(143,"Nd",142.909823, 44.8949),
	 new Isotope(144,"Nd",143.910096, 87.7258),
	 new Isotope(145,"Nd",144.912582, 30.5934),
	 new Isotope(146,"Nd",145.913126, 63.3616),
	 new Isotope(148,"Nd",147.916901, 21.2311),
	 new Isotope(150,"Nd",149.920900, 20.7888)
	};

}
