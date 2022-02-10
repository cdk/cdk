/*
 * Copyright (c) 2021 Mehmet Aziz Yirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>
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

package org.openscience.cdk.structgen.maygen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.IntStream;

/**
 * The hydrogen distribution process is a preliminary step in the generation. The pre-distribution
 * of hydrogens accelerates the generation. For a given chemical formula, all the possible
 * distributions of the hydrogen atoms to the hetero atoms are generated.For example, C6H6 has 6
 * hydrogens and 6 carbons. There are 7 unique possible distribution of these hydrogens to these
 * carbons.
 *
 * @author MehmetAzizYirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>
 * @cdk.module structgen
 */
class HydrogenDistributor {
    private int[] capacity;
    private int[] valences;
    private int totalHydrogen; // Total number of hydrogens.
    private int[] totalAtom; // Total number of atoms.
    private int hydrogens2distribute;

    /**
     * Adding new element to an int array.
     *
     * @param a int[] a
     * @param e int[] e
     * @return int[]
     */
    public int[] addElement(int[] a, int e) {
        a = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    /**
     * Summing the entries of an array.
     *
     * @param list int[] list
     * @param index int entry index
     * @return int
     */
    public int sum(int[] list, int index) {
        int sum = 0;
        for (int i = 0; i <= index; i++) {
            sum = sum + list[i];
        }
        return sum;
    }

    /**
     * Setting the global variables based on the initial partition and degrees.
     *
     * @param partition int[] atom partition
     * @param degrees int[] atom valences
     * @return int[]
     */
    public int[] setValues(int[] partition, int[] degrees) {
        int partitionSize = partition.length;
        int[] localCapacity = new int[partitionSize];
        int[] localValences = new int[partitionSize];
        int[] localTotalAtom = new int[partitionSize];
        int i = 0;
        int sum;
        for (int j = 0; j < partitionSize; j++) {
            localTotalAtom[i] = partition[i];
            sum = sum(partition, i);
            localValences[i] = degrees[sum - 1] - 1;
            localCapacity[i] = (degrees[sum - 1] - 1) * partition[i];
            i++;
        }

        this.capacity = localCapacity;
        this.valences = localValences;
        this.totalAtom = localTotalAtom;
        return localCapacity;
    }

    /**
     * Summing entries of an int array
     *
     * @param array int[] array
     * @return int
     */
    public int sum(int[] array) {
        int sum = 0;
        for (int j : array) {
            sum = sum + j;
        }
        return sum;
    }

    /**
     * Combining list of int arrays.
     *
     * @param arrays List<int[]> list of int arrays
     * @return int[]
     */
    public int[] mergeArrays(List<int[]> arrays) {
        int size = 0;
        for (int[] array : arrays) {
            size += array.length;
        }
        int[] mergedArray = new int[size];
        int index = 0;
        for (int[] array : arrays) {
            for (int i : array) {
                mergedArray[index++] = i;
            }
        }
        return mergedArray;
    }

    /**
     * Combining 2 int arrays.
     *
     * @param a int[] int array
     * @param b int[] int array
     * @return int[]
     */
    public int[] arraySum(int[] a, int[] b) {
        List<int[]> arrays = new ArrayList<>();
        arrays.add(a);
        arrays.add(b);
        return mergeArrays(arrays);
    }

    /**
     * Combining list of int arrays.
     *
     * @param lists Deque<List<int[]>> lists
     * @return List<int[]>
     */
    public List<int[]> combineArrays(Deque<List<int[]>> lists) {
        List<int[]> comb = new ArrayList<>(lists.removeFirst());
        while (!lists.isEmpty()) {
            List<int[]> list = lists.removeFirst();
            List<int[]> newComb = new ArrayList<>();
            for (int[] arr1 : comb) {
                for (int[] arr2 : list) {
                    newComb.add(arraySum(arr1, arr2));
                }
            }
            comb = newComb;
        }
        return comb;
    }

    /**
     * To initialise the inputs and run the functions while recording the duration time.
     *
     * @param partition int[] partition
     * @param degrees int[] degrees
     * @return List<int[]>
     */
    public List<int[]> run(int[] partition, int[] degrees) {
        int partitionSize = partition.length;
        int hydrogen = partition[partitionSize - 1];
        int isotopes = partitionSize - 1;
        setValues(partition, degrees);
        totalHydrogen = hydrogen;
        List<int[]> result;
        if (isotopes == 1) {
            List<int[]> iarrays = new ArrayList<>();
            int[] array = new int[0];
            hydrogens2distribute = totalHydrogen;
            distribute(iarrays, totalHydrogen, array, valences[0], totalAtom[0]);
            result = iarrays;
        } else {
            List<int[]> distributions = new ArrayList<>();
            for (int[] dene : partition(totalHydrogen, isotopes, 0)) {
                Deque<List<int[]>> lists = new ArrayDeque<>();
                for (int i = 0; i < dene.length; i++) {
                    hydrogens2distribute = dene[i];
                    List<int[]> iarrays = new ArrayList<>();
                    int[] array = new int[0];
                    distribute(iarrays, dene[i], array, valences[i], totalAtom[i]);
                    lists.add(iarrays);
                }
                List<int[]> combined = combineArrays(lists);
                distributions.addAll(combined);
            }
            result = distributions;
        }
        return result;
    }

    /**
     * These functions are built for the integer partitioning problem.
     *
     * @param n int total number of hydrogens
     * @param d int total number of isotopes to distribute hydrogens
     * @param depth int starting from zero until the number of isotopes recursively filling.
     * @return List<int[]>
     */
    public List<int[]> partition(int n, int d, int depth) {
        if (d == depth) {
            List<int[]> array = new ArrayList<>();
            int[] take = new int[0];
            array.add(take);
            return array;
        }
        return buildArray(n, d, depth);
    }

    /**
     * Subfunction of partition function.
     *
     * @param n int total number of hydrogens
     * @param d int total number of isotopes to distribute hydrogens
     * @param depth int starting from zero until the number of isotopes recursively filling.
     * @return List<int[]>
     */
    public List<int[]> buildArray(int n, int d, int depth) {
        List<int[]> array = new ArrayList<>();
        IntStream range = IntStream.rangeClosed(0, n);
        for (int i : range.toArray()) {
            for (int[] item : partition(n - i, d, depth + 1)) {
                buildArrayItem(d, array, i, item);
            }
        }
        return array;
    }

    /**
     * These functions are built for the integer partitioning problem.
     *
     * @param d int total number of isotopes to distribute hydrogens
     * @param List<int[]> List<int[]> list of partition
     * @param i int entry
     * @param item int[] new partition
     * @return List<int[]>
     */
    public void buildArrayItem(int d, List<int[]> array, int i, int[] item) {
        if (i <= capacity[item.length]) {
            item = addElement(item, i);
            if (item.length == d) {
                if (sum(item) == totalHydrogen) {
                    array.add(item);
                }
            } else {
                array.add(item);
            }
        }
    }

    /**
     * Adding zeros to the end of an array.
     *
     * @param array int[] partition
     * @param zeros int number of zeros
     * @return int[]
     */
    public int[] addZeros(int[] array, int zeros) {
        for (int i = 0; i < zeros; i++) {
            array = addElement(array, 0);
        }
        return array;
    }

    /**
     * Ordering the int array in descending order.
     *
     * @param array int[] array
     * @return int[]
     */
    public int[] descendingOrderArray(int[] arr) {
        return Arrays.stream(arr).boxed().sorted().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Distributing number of hydrogens in an unique way to a number of isotopes.
     *
     * @param arrays List<int[]> list of output arrays
     * @param hydrogen int number of hydrogens to add
     * @param array int[] array
     * @param valence int atom valence
     * @param numAtom int number of atoms in a given index
     */
    public void distribute(List<int[]> arrays, int hydrogen, int[] arr, int valence, int numAtom) {
        if (hydrogen == 0 && sum(arr) == hydrogens2distribute) {
            if (arr.length != numAtom) {
                arr = addZeros(arr, (numAtom - arr.length));
            }
            arr = descendingOrderArray(arr);
            arrays.add(arr);
        } else if (numAtom - arr.length == 1) {
            numAtomMinusArrLengthEqualsOne(arrays, hydrogen, arr, valence, numAtom);
        } else {
            for (int i = Math.min(valence, hydrogen); i > 0; i--) {
                if (arr.length == 0) {
                    distribute(arrays, (hydrogen - i), addElement(arr, i), valence, numAtom);
                }
                if ((arr.length) > 0 && arr[arr.length - 1] <= i) {
                    distribute(arrays, (hydrogen - i), addElement(arr, i), valence, numAtom);
                }
            }
        }
    }

    /**
     * Subfunction of the distribute functon for the case when (numAtom - arr.length == 1)
     *
     * @param arrays List<int[]> list of output arrays
     * @param hydrogen int number of hydrogens to add
     * @param array int[] array
     * @param valence int atom valence
     * @param numAtom int number of atoms in a given index
     */
    public void numAtomMinusArrLengthEqualsOne(
            List<int[]> arrays, int hydrogen, int[] arr, int valence, int numAtom) {
        int add = Math.min(hydrogen, valence);
        if (arr.length == 0) {
            distribute(arrays, 0, addElement(arr, add), valence, numAtom);
        }
        if ((arr.length) > 0 && arr[arr.length - 1] <= add) {
            distribute(arrays, 0, addElement(arr, add), valence, numAtom);
        }
    }
}
