/* Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.smsd.global.TimeOut;
import org.openscience.cdk.smsd.tools.TimeManager;

/**
 * @author Asad
 */
class BKKCKCFTest {

    @Test
    void newCliqueSearchDoesNotInheritAnExpiredClockOrFlag() {
        TimeOut timeout = TimeOut.getInstance();
        double previous = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        TimeManager previousClock = MCSPlus.getTimeManager();
        try {
            TimeManager expired = new TimeManager() {
                @Override
                public double getElapsedTimeInMinutes() { return 10; }
            };
            timeout.setTimeOut(1);
            MCSPlus.setTimeManager(expired);
            timeout.setTimeOutFlag(true);
            BKKCKCF search = new BKKCKCF(Arrays.asList(0, 0, 1, 1, 1, 2),
                    Arrays.asList(1, 2), new ArrayList<>());
            Assertions.assertEquals(2, search.getBestCliqueSize());
            Assertions.assertFalse(timeout.isTimeOutFlag());
            Assertions.assertSame(expired, MCSPlus.getTimeManager());
        } finally {
            MCSPlus.setTimeManager(previousClock);
            timeout.setTimeOut(previous);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

    @Test
    void rejectsMalformedCompatibilityGraphBeforeSearching() {
        List<Integer> nodes = Arrays.asList(0, 0, 1, 1, 1, 2);
        List<Integer> empty = java.util.Collections.emptyList();
        Assertions.assertThrows(NullPointerException.class, () -> new BKKCKCF(null, empty, empty));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new BKKCKCF(Arrays.asList(0, 0), empty, empty));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new BKKCKCF(Arrays.asList(0, 0, 1, 1, 1, 1), empty, empty));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new BKKCKCF(nodes, Arrays.asList(1), empty));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new BKKCKCF(nodes, Arrays.asList(1, 99), empty));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new BKKCKCF(nodes, Arrays.asList(1, 1), empty));
    }

    public BKKCKCFTest() {}

    @BeforeAll
    static void setUpClass() throws Exception {}

    @AfterAll
    static void tearDownClass() throws Exception {}

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    @Test
    void testSomeMethod() {
        // TODO review the generated test code and remove the default call to fail.
        Assertions.assertNotNull(new BKKCKCF(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    void cliqueEnumerationKeepsTheSearchDeadline() {
        TimeOut timeout = TimeOut.getInstance();
        double previousTimeout = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        TimeManager previousClock = MCSPlus.getTimeManager();
        try {
            timeout.setTimeOut(0);
            TimeManager clock = new TimeManager();
            MCSPlus.setTimeManager(clock);
            BKKCKCF search = new BKKCKCF(Arrays.asList(0, 0, 1, 1, 1, 2),
                    Arrays.asList(1, 2), new ArrayList<>());
            Assertions.assertSame(clock, MCSPlus.getTimeManager());
            Assertions.assertTrue(timeout.isTimeOutFlag());
            Assertions.assertEquals(0, search.getBestCliqueSize());
        } finally {
            MCSPlus.setTimeManager(previousClock);
            timeout.setTimeOut(previousTimeout);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

    @Test
    void connectedCliquesAgreeWithExhaustiveEnumeration() {
        Random random = new Random(90131L);
        // All 3^6 edge colorings on four vertices, plus larger random graphs.
        for (int sample = 0; sample < 729 + 48; sample++) {
            int size = sample < 729 ? 4 : 2 + random.nextInt(5);
            int coloring = sample;
            int[][] edges = new int[size][size];
            List<Integer> nodes = new ArrayList<>();
            List<Integer> cEdges = new ArrayList<>();
            List<Integer> dEdges = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                nodes.add(i);
                nodes.add(i);
                nodes.add(130 + i);
                for (int j = 0; j < i; j++) {
                    edges[i][j] = edges[j][i] = sample < 729 ? coloring % 3 : random.nextInt(3);
                    coloring /= 3;
                    if (edges[i][j] > 0) {
                        List<Integer> list = edges[i][j] == 1 ? cEdges : dEdges;
                        list.add(130 + i);
                        list.add(130 + j);
                    }
                }
            }
            Set<Set<Integer>> expected = new HashSet<>();
            int maximum = 0;
            for (int mask = 1; mask < (1 << size); mask++) {
                if (!isConnectedClique(mask, edges)) continue;
                int count = Integer.bitCount(mask);
                if (count > maximum) {
                    expected.clear();
                    maximum = count;
                }
                if (count == maximum) {
                    Set<Integer> clique = new TreeSet<>();
                    for (int i = 0; i < size; i++) {
                        if ((mask & (1 << i)) != 0) clique.add(130 + i);
                    }
                    expected.add(clique);
                }
            }
            BKKCKCF search = new BKKCKCF(nodes, cEdges, dEdges);
            Set<Set<Integer>> actual = new HashSet<>();
            for (List<Integer> clique : search.getMaxCliqueSet()) actual.add(new TreeSet<>(clique));
            Assertions.assertEquals(maximum, search.getBestCliqueSize(), "sample " + sample);
            Assertions.assertEquals(expected, actual, "sample " + sample);
        }
    }

    private boolean isConnectedClique(int mask, int[][] edges) {
        for (int i = 0; i < edges.length; i++) {
            if ((mask & (1 << i)) == 0) continue;
            for (int j = 0; j < i; j++) {
                if ((mask & (1 << j)) != 0 && edges[i][j] == 0) return false;
            }
        }
        int reached = Integer.lowestOneBit(mask);
        int previous;
        do {
            previous = reached;
            for (int i = 0; i < edges.length; i++) {
                if ((reached & (1 << i)) == 0) continue;
                for (int j = 0; j < edges.length; j++) {
                    if (edges[i][j] == 1 && (mask & (1 << j)) != 0) reached |= 1 << j;
                }
            }
        } while (previous != reached);
        return reached == mask;
    }


    @Test
    void cliqueResultsDoNotAliasStoredCliquesOrEarlierSnapshots() {
        BKKCKCF finder = new BKKCKCF(java.util.Arrays.asList(0, 0, 1, 1, 1, 2),
                java.util.Arrays.asList(1, 2), java.util.Collections.emptyList());
        java.util.Stack<java.util.List<Integer>> retained = finder.getMaxCliqueSet();
        java.util.Stack<java.util.List<Integer>> changed = finder.getMaxCliqueSet();
        changed.get(0).clear();
        changed.clear();
        Assertions.assertEquals(2, finder.getBestCliqueSize());
        Assertions.assertEquals(java.util.Arrays.asList(1, 2), retained.get(0));
        Assertions.assertEquals(retained, finder.getMaxCliqueSet());
    }

}
