/*
 MIT License

 Copyright (c) 2018 Mehmet Aziz Yirik

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction,
 including without limitation the rights to use, copy, modify, merge, publish, distribute,
 sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or
 substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

/*
 This class is for the distribution of hydrogens into chemical structures. For a given chemical
 formula, all the possible distributions of the hydrogen atoms to the hetero atoms are generated.

 @author Mehmet Aziz Yirik
*/
package org.openscience.cdk.structgen.maygen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

class HydrogenDistributor {
    private int[] capacity;
    private int[] valences;
    private int totalHydrogen; // Total number of hydrogens.
    private int[] totalAtom; // Total number of atoms.
    private int hydrogens2distribute;

    /**
     * The basic functions used in the hydrogen distributor.
     *
     * @param a the a
     * @param e the e
     * @return the new array
     */
    public int[] addElement(int[] a, int e) {
        a = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    public int sum(int[] list, int index) {
        int sum = 0;
        for (int i = 0; i <= index; i++) {
            sum = sum + list[i];
        }
        return sum;
    }

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

    public int sum(int[] array) {
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum = sum + array[i];
        }
        return sum;
    }

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

    public int[] arraySum(int[] a, int[] b) {
        List<int[]> arrays = new ArrayList<>();
        arrays.add(a);
        arrays.add(b);
        return mergeArrays(arrays);
    }

    public List<int[]> combineArrays(LinkedList<List<int[]>> lists) {
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
     * @param partition the partition
     * @param degrees the degrees
     * @return the list of int arrays
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
                LinkedList<List<int[]>> lists = new LinkedList<>();
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
     * @param n the n
     * @param d the d
     * @param depth the depth
     * @return the list of int arrays
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

    public int[] addZeros(int[] array, int zeros) {
        for (int i = 0; i < zeros; i++) {
            array = addElement(array, 0);
        }
        return array;
    }

    public int[] descendingOrderArray(int[] arr) {
        return Arrays.stream(arr).boxed().sorted().mapToInt(Integer::intValue).toArray();
    }

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
