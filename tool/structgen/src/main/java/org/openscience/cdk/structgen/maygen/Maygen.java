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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.group.Permutation;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.Closeable;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The main class of the MAYGEN package. The basic input is the molecular
 * formula. For a molecular  * formula, MAYGEN first distributes hydrogens,
 * then for each distribution starting the generation process. The algorithm
 * can be run in sequential or parallel mode.
 *
 * To collect the structures you provide a {@link Maygen.Consumer} instance,
 * here is basic usage:
 * <pre>{@code
 * IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
 * SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default);
 *
 * Maygen maygen = new Maygen(builder);
 * maygen.setFormula("C3Cl2H4");
 * maygen.setConsumer(mol -> {
 *             try {
 *                 System.out.println(smigen.create(mol));
 *             } catch (CDKException ignore) { }
 *         });
 * maygen.run();
 * int count = maygen.getCount(); // number of structures generated
 * }</pre>
 *
 *
 *
 * @author MehmetAzizYirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>
 * @cdk.module structgen
 */
public class Maygen {

    private final ILoggingTool logger = LoggingToolFactory.createLoggingTool(Maygen.class);

    public interface Consumer extends Closeable {

        void consume(IAtomContainer mol);

        default void configure(String name) {

        }

        @Override
        default void close() throws IOException {

        }
    }

    public static final Consumer NOOP_CONSUMER = mol -> {

    };

    private static final String NUMBERS_FROM_0_TO_9 = "(?=[0-9])";
    private static final String LETTERS_FROM_A_TO_Z = "(?=[A-Z])";

    private static final String THE_INPUT_FORMULA = "The input formula, ";
    private static final String DOES_NOT_REPRESENT_ANY_MOLECULE =
            ", does not represent any molecule.";
    private final Map<String, Integer> valences;
    private int size = 0;
    private int total = 0;

    private Consumer consumer = NOOP_CONSUMER;
    private boolean tsvoutput = false;
    private boolean setElement = false;
    private boolean boundary = false;
    private boolean multiThread = false;
    private boolean verbose = false;
    private String formula;
    private String fuzzyFormula;

    private int hIndex = 0;
    private final AtomicInteger count = new AtomicInteger();
    private int fuzzyCount = 0;
    private int matrixSize = 0;
    private List<String> symbols = new ArrayList<>();
    private int[] occurrences;
    private int[] nodeLabels;
    private int graphSize;
    private List<int[]> oxygenSulfur = new ArrayList<>();
    private int[] firstDegrees;
    private int totalHydrogen = 0;
    private List<String> firstSymbols = new ArrayList<>();
    private int[] firstOccurrences;
    private boolean callHydrogenDistributor = false;
    private boolean justH = false;
    private boolean noHydrogen = false;
    private int sizePart = 0;
    private boolean singleAtom = true;
    private boolean onlyDegree2 = true;
    private boolean onSm = true;
    private int oxygen = 0;
    private int sulfur = 0;
    private String[] symbolArray;
    private final IChemObjectBuilder builder;
    private IAtomContainer atomContainer;

    public Maygen(IChemObjectBuilder builder) {
        this.builder = builder;
        this.atomContainer = builder.newAtomContainer();

        // The atom valences from CDK.
        valences = new HashMap<>();

        valences.put("C", 4);
        valences.put("N", 3);
        valences.put("O", 2);
        valences.put("S", 2);
        valences.put("P", 3);
        valences.put("F", 1);
        valences.put("I", 1);
        valences.put("Cl", 1);
        valences.put("Br", 1);
        valences.put("H", 1);
    }

    public int getSize() {
        return size;
    }

    public boolean isBoundary() {
        return boundary;
    }

    public void setBoundary(boolean boundary) {
        this.boundary = boundary;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public boolean isSetElement() {
        return setElement;
    }

    public void setSetElement(boolean setElement) {
        this.setElement = setElement;
    }

    public boolean isTsvoutput() {
        return tsvoutput;
    }

    public void setTsvoutput(boolean tsvoutput) {
        this.tsvoutput = tsvoutput;
    }
    public String[] getSymbolArray() {
        return symbolArray;
    }

    public IChemObjectBuilder getBuilder() {
        return builder;
    }

    public boolean isMultiThread() {
        return multiThread;
    }

    public void setMultiThread(boolean multiThread) {
        this.multiThread = multiThread;
    }

    public int getCount() {
        return count.get();
    }

    public int getFuzzyCount() {
        return fuzzyCount;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getFuzzyFormula() {
        return fuzzyFormula;
    }

    public void setFuzzyFormula(String fuzzyFormula) {
        this.fuzzyFormula = fuzzyFormula;
    }

    public int getTotal() {
        return total;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public int[] getOccurrences() {
        return occurrences;
    }

    public List<int[]> getOxygenSulfur() {
        return oxygenSulfur;
    }

    public int getTotalHydrogen() {
        return totalHydrogen;
    }

    public boolean isOnSm() {
        return onSm;
    }

    public boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /* Basic functions */

    /**
     * Permuting two entries of an Integer array.
     *
     * @param array Integer[] array
     * @param i int first index
     * @param j int second index
     * @return int[]
     */
    public int[] permuteArray(int[] array, int i, int j) {
        int temp;
        temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        return array;
    }

    /**
     * Summing entries of an array.
     *
     * @param array int[]
     * @return int sum
     */
    public int sum(int[] array) {
        int sum = 0;
        for (int j : array) {
            sum = sum + j;
        }
        return sum;
    }

    /**
     * Summing entries of a list until a given index.
     *
     * @param list the int array
     * @param index the index
     * @return int sum
     */
    public int sum(int[] list, int index) {
        int sum = 0;
        for (int i = 0; i <= index; i++) {
            sum += list[i];
        }
        return sum;
    }

    /**
     * Getting the number of atoms' occurrences.
     *
     * @param info String[] atom info
     * @return int
     */
    public int atomOccurrence(String[] info) {
        if (info.length == 1) {
            if (info[0].contains("(")) {
                return 1;
            } else {
                // Here we need 2 otherwise it will split also integers in case
                // of C10 -> C,1,0
                String[] info2 = info[0].split(NUMBERS_FROM_0_TO_9, 2);
                if (info2.length == 1) return 1;
                else {
                    return Integer.parseInt(info2[1]);
                }
            }
        } else {
            if (info[1].contains(")")) {
                String[] info2 = info[1].split("\\)");
                return info2.length > 1 ? Integer.parseInt(info2[1]) : 1;
            } else {
                return Integer.parseInt(info[1]);
            }
        }
    }

    /**
     * Performing the permutation action on an int array.
     *
     * @param array int[] array
     * @param permutation Permutation permutation
     * @return int[]
     */
    public int[] actArray(int[] array, Permutation permutation) {
        int permLength = permutation.size();
        int newIndex;
        int arrayLength = array.length;
        int[] modified = new int[arrayLength];
        for (int i = 0; i < permLength; i++) {
            newIndex = permutation.get(i);
            modified[newIndex] = array[i];
        }
        return modified;
    }

    /**
     * Values for an id permutation for a given localSize
     *
     * @param localSize int permutation localSize
     * @return int[]
     */
    public int[] idValues(int localSize) {
        int[] id = new int[localSize];
        for (int i = 0; i < localSize; i++) {
            id[i] = i;
        }
        return id;
    }

    /**
     * Builds id permutation.
     *
     * @param localSize int Permutation localSize
     * @return Permutation
     */
    public Permutation idPermutation(int localSize) {
        return new Permutation(localSize);
    }

    /**
     * The initializer function, reading the formula to set the degrees, partition and file
     * directory variables.
     *
     * @param symbols the symbols
     */
    public void sortAscending(List<String> symbols) {
        HashMap<String, Integer> inputs = new HashMap<>();
        for (String symbol : symbols) {
            if (inputs.containsKey(symbol)) {
                Integer localCount = inputs.get(symbol) + 1;
                inputs.put(symbol, localCount);
            } else {
                inputs.put(symbol, 1);
            }
        }

        Set<Entry<String, Integer>> set = inputs.entrySet();
        sort(symbols, set);
    }

    public void sort(List<String> symbols, Set<Entry<String, Integer>> set) {
        int index = 0;
        int value;
        ArrayList<Entry<String, Integer>> list = new ArrayList<>(set);
        list.sort(
                (value1, value2) -> {
                    int comparison = (value2.getValue()).compareTo(value1.getValue());
                    if (comparison == 0) {
                        return valences.get(value1.getKey())
                                .compareTo(valences.get(value2.getKey()));
                    } else {
                        return value1.getValue().compareTo(value2.getValue());
                    }
                });

        for (Entry<String, Integer> entry : list) {
            value = entry.getValue();
            for (int i = 0; i < value; i++) {
                symbols.set(index + i, entry.getKey());
            }
            index += value;
        }
    }

    public void singleAtomCheck(String[] atoms) {
        String[] info = atoms[0].split(NUMBERS_FROM_0_TO_9, 2);
        String symbol = info[0].split("\\(")[0];
        if (atoms.length == 1) {
            if (symbol.equals("H")) {
                singleAtom = false;
            } else {
                if (atomOccurrence(info) > 1) {
                    singleAtom = false;
                }
            }
        } else if (atoms.length == 2) {
            singleAtomCheckLengthIsBiggerThanOne(atoms);
        } else {
            singleAtom = false;
        }
    }

    public void singleAtomCheckLengthIsBiggerThanOne(String[] atoms) {
        String symbol;
        String[] info;
        int localCount = 0;
        for (String atom : atoms) {
            info = atom.split(NUMBERS_FROM_0_TO_9, 2);
            symbol = info[0];
            if (!symbol.equals("H")) {
                localCount++;
                if (atomOccurrence(info) > 1 || localCount > 1) {
                    singleAtom = false;
                    break;
                }
            }
        }
    }

    public void checkOxygenSulfur(String[] atoms) {
        String[] info;
        for (String atom : atoms) {
            info = atom.split("\\("); // to get the higher valence value
            final String symbol;
            if (info.length != 1) {
                symbol = info[0] + info[1].split("\\)")[0]; // to get the valence and frequency from x)y
            } else {
                symbol = info[0].split(NUMBERS_FROM_0_TO_9)[0];
            }
            if (valences.get(symbol) != 2) {
                onlyDegree2 = false;
                onSm = false;
                break;
            } else {
                if (symbol.equals("S")) {
                    sulfur = atomOccurrence(info);
                } else if (symbol.equals("O")) {
                    oxygen = atomOccurrence(info);
                }
            }
        }
        if (onlyDegree2) {
            matrixSize = sulfur + oxygen;
            hIndex = matrixSize;
        }
    }

    public void getSingleAtomVariables(String localFormula) {
        String[] atoms = localFormula.split(LETTERS_FROM_A_TO_Z);
        List<String> symbolList = new ArrayList<>();
        String[] info;
        int hydrogens = 0;
        String symbol;
        hIndex = 1;
        for (String atom : atoms) {
            info = atom.split(NUMBERS_FROM_0_TO_9, 2);
            symbol = info[0].split("\\(")[0];
            if (symbol.equals("H")) {
                hydrogens = atomOccurrence(info);
            } else {
                symbolList.add(symbol);
            }
        }
        matrixSize = hydrogens + 1;
        for (int i = 0; i < hydrogens; i++) {
            symbolList.add("H");
        }
        setSymbols(symbolList);
    }

    /**
     * Getting the symbol occurrences from the input local formula.
     *
     * @param localFormula String molecular formula
     */
    public void getSymbolOccurrences(String localFormula) {
        String[] atoms = localFormula.split(LETTERS_FROM_A_TO_Z);
        List<String> symbolList = new ArrayList<>();
        String[] info;
        int hydrogens = 0;
        for (String atom : atoms) {
            info = atom.split("\\(");
            if (info.length == 1) {
                hydrogens = getHydrogensInfoLengthIsOne(symbolList, info, hydrogens);
            } else {
                hydrogens = getHydrogens(symbolList, info, hydrogens);
            }
        }
        sortAscending(symbolList);
        for (int i = 0; i < hydrogens; i++) {
            symbolList.add("H");
        }
        firstOccurrences = getPartition(symbolList);
        matrixSize = sum(firstOccurrences);
        setSymbols(symbolList);
        occurrences = getPartition(symbolList);
        if (hydrogens != 0) {
            totalHydrogen += hydrogens;
            if (hIndex == 1) {
                callHydrogenDistributor = false;
            } else if (hIndex == 0) {
                justH = true;
                callHydrogenDistributor = false;
                hIndex = hydrogens;
                matrixSize = hIndex;
            } else {
                callHydrogenDistributor = true;
            }
        } else {
            callHydrogenDistributor = false;
            noHydrogen = true;
        }
    }

    public int getHydrogensInfoLengthIsOne(List<String> symbolList, String[] info, int hydrogens) {
        String symbol;
        int occur;
        symbol = info[0].split(NUMBERS_FROM_0_TO_9)[0];
        if (!symbol.equals("H")) {
            occur = atomOccurrence(info);
            sizePart++;
            for (int i = 0; i < occur; i++) {
                symbolList.add(symbol);
                hIndex++;
            }
        } else {
            hydrogens = atomOccurrence(info);
        }
        return hydrogens;
    }

    /**
     * Get the hydrogen information from the input molecular formula information.
     *
     * @param symbolList List<String> list of symbols
     * @param info String[] the molecular formula information
     * @param hydrogens int the number of hydrogens
     * @return int
     */
    private int getHydrogens(List<String> symbolList, String[] info, int hydrogens) {
        String symbol;
        int occur;
        symbol = info[0];
        symbol += info[1].split("\\)")[0];
        if (!symbol.equals("H")) {
            occur = atomOccurrence(info);
            sizePart++;
            for (int i = 0; i < occur; i++) {
                symbolList.add(symbol);
                hIndex++;
            }
        } else {
            hydrogens = atomOccurrence(info);
        }
        return hydrogens;
    }

    public int[] nextCount(int index, int i, int localSize, List<String> symbols, int[] partition) {
        int localCount = 1;
        if (i == (localSize - 1)) {
            partition[index] = 1;
            index++;
        } else {
            for (int j = i + 1; j < localSize; j++) {
                if (symbols.get(i).equals(symbols.get(j))) {
                    localCount++;
                    if (j == (localSize - 1)) {
                        partition[index] = localCount;
                        index++;
                        break;
                    }
                } else {
                    partition[index] = localCount;
                    index++;
                    break;
                }
            }
        }
        return new int[] {localCount, index};
    }

    /**
     * Getting the partition of symbols
     *
     * @param symbols the list of symbols
     * @return int[]
     */
    public int[] getPartition(List<String> symbols) {
        int i = 0;
        int[] partition = new int[sizePart + 1];
        int localSize = symbols.size();
        int next;
        int index = 0;
        int[] result;
        while (i < localSize) {
            result = nextCount(index, i, localSize, symbols, partition);
            next = (i + result[0]);
            index = result[1];
            if (next == localSize) {
                break;
            } else {
                i = next;
            }
        }
        return partition;
    }

    /**
     * Setting the firstSymbols and symbols global variables for the initial sorted list of symbols.
     *
     * @param symbolList the sorted list of atom symbols
     */
    public void setSymbols(List<String> symbolList) {
        symbolArray = new String[matrixSize];
        int index = 0;
        for (String symbol : symbolList) {
            symbolArray[index] = symbol;
            index++;
            if (!firstSymbols.contains(symbol)) {
                firstSymbols.add(symbol);
            }
        }
    }

    /** Replace each sub-string in from[] with it's normalisation in to[]. Since
     *  this is only used for formula validating and normalizing a parser would
     *  be better. */
    private static String replaceEach(String str, String[] from, String[] to) {
        if (from.length != to.length)
            throw new IllegalArgumentException();
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        while (pos < str.length()) {
            int best = -1;
            int bestLen = -1;
            for (int j = 0; j < from.length; j++) {
                int len = from[j].length();
                if (len > bestLen && str.regionMatches(pos, from[j], 0, len)) {
                    best = j;
                    bestLen = len;
                }
            }
            if (best < 0) {
                sb.append(str.charAt(pos));
                pos++;
            } else {
                sb.append(to[best]);
                pos += bestLen;
            }
        }
        return sb.toString();
    }

    public String normalizeFormula(String formula) {
        String[] from = {"cl", "CL", "c", "n", "o", "s", "p", "f", "i", "br", "BR", "h"};
        String[] to = {"Cl", "Cl", "C", "N", "O", "S", "P", "F", "I", "Br", "Br", "H"};
        return replaceEach(formula, from, to);
    }

    public String[] validateFormula(String formula) {
        String[] from = {"Cl", "C", "N", "O", "S", "P", "F", "I", "Br", "H"};
        String[] to = {"", "", "", "", "", "", "", "", "", ""};
        String result = replaceEach(formula.replaceAll("[0-9]", ""), from, to);
        return result.isEmpty() ? new String[0] : result.split("");
    }

    public String[] validateFuzzyFormula(String formula) {
        String[] from = {"Cl", "C", "N", "O", "S", "P", "F", "I", "Br", "H", "[", "]", "-"};
        String[] to = {"", "", "", "", "", "", "", "", "", "", "", "", ""};
        String result = replaceEach(formula.replaceAll("[0-9]", ""), from, to);
        return result.isEmpty() ? new String[0] : result.split("");
    }

    /**
     * Checking whether a molecular formula can represent a graph or not.
     *
     * <p>For a graph with n nodes, the sum of all its node degrees should be equal or bigger than
     * 2*(n-1). Thus, the minimum number of nodes.
     *
     * @param formula String molecular formula
     * @return boolean
     */
    public boolean canBuildIsomer(String formula) {
        String[] atoms = normalizeFormula(formula).split(LETTERS_FROM_A_TO_Z);
        String[] info;
        String symbol;
        int occur;
        int valence;
        int localSize = 0;
        int sum = 0;
        for (String atom : atoms) {
            if (atom.contains(")")) info = atom.split("\\)");
            else info = atom.split(NUMBERS_FROM_0_TO_9, 2);
            symbol = info[0].split("\\(")[0];
            if (setElement && info[0].contains("(")) {
                valence = Integer.parseInt(info[0].split("\\(")[1]);
            } else {
                valence = valences.get(symbol);
            }
            occur = atomOccurrence(info);
            localSize += occur;
            sum += (valence * occur);
        }
        total = localSize;
        return sum % 2 == 0 && sum >= 2 * (localSize - 1);
    }

    /**
     * Checking whether a molecular formula can represent a graph or not. This is just for the case
     * of molecular formulae with single heteroatoms.
     *
     * <p>For a graph with n nodes, the sum of all its node degrees should be equal or bigger than
     * 2*(n-1). Thus, the minimum number of nodes.
     *
     * @param formula String molecular formula
     * @return boolean
     */
    public boolean canBuildIsomerSingle(String formula) {
        String[] atoms = normalizeFormula(formula).split(LETTERS_FROM_A_TO_Z);
        String[] info;
        String symbol;
        boolean check = false;
        int nonHydrogen = 0;
        int hydrogens = 0;
        for (String atom : atoms) {
            info = atom.split(NUMBERS_FROM_0_TO_9, 2);
            symbol = info[0].split("\\(")[0];
            if (symbol.equals("H")) {
                hydrogens = atomOccurrence(info);
            } else {
                if (setElement) {
                    nonHydrogen = Integer.parseInt(info[1].split("\\)")[0]);
                } else {
                    nonHydrogen = valences.get(symbol);
                }
            }
        }
        if (nonHydrogen == hydrogens) check = true;
        return check;
    }

    /** Initial degree arrays are set based on the molecular formula. */
    public void initialDegrees() {
        firstDegrees = new int[matrixSize];
        int index = 0;
        String symbol;
        int length = firstSymbols.size();
        for (int i = 0; i < length; i++) {
            symbol = firstSymbols.get(i);
            for (int j = 0; j < firstOccurrences[i]; j++) {
                firstDegrees[index] = valences.get(symbol);
                index++;
            }
        }
    }

    /**
     * Checks two int[] arrays are equal with respect to an atom partition.
     *
     * @param array1 int[] first array
     * @param array2 int[] second array
     * @param partition int[] atom partition
     * @return boolean
     */
    public boolean equalSetCheck(int[] array1, int[] array2, int[] partition) {
        int[] temp = cloneArray(array2);
        temp = descendingSortWithPartition(temp, partition);
        return equalSetCheck2(partition, array1, temp);
    }

    /**
     * Getting a part of a int array specified by two entry indices
     *
     * @param array int[] array
     * @param begin int beginning index
     * @param end int ending index
     * @return Integer[]
     */
    public int[] getBlocks(int[] array, int begin, int end) {
        return Arrays.copyOfRange(array, begin, end);
    }

    /**
     * Checks two int[] arrays are equal with respect to an atom partition.
     *
     * @param partition int[] atom partition
     * @param array1 int[] array
     * @param array2 int[] array
     * @return boolean
     */
    public boolean equalSetCheck2(int[] partition, int[] array1, int[] array2) {
        boolean check = true;
        int i = 0;
        int limit = findZeros(partition);
        if (partition[size - 1] != 0) {
            for (int d = 0; d < size; d++) {
                if (array1[d] != array2[d]) {
                    check = false;
                    break;
                }
            }
        } else {
            int value;
            for (int s = 0; s < limit; s++) {
                value = partition[s];
                if (compareIndexwise(array1, array2, i, (value + i))) {
                    i = i + value;
                } else {
                    check = false;
                    break;
                }
            }
        }
        return check;
    }

    /**
     * Comparing two int arrays are equal or not for given range of entries.
     *
     * @param array int[] array
     * @param array2 int[] array
     * @param index1 int beginning index
     * @param index2 int last index
     * @return boolean
     */
    public boolean compareIndexwise(int[] array, int[] array2, int index1, int index2) {
        boolean check = true;
        for (int i = index1; i < index2; i++) {
            if (array[i] != array2[i]) {
                check = false;
                break;
            }
        }
        return check;
    }

    /**
     * Comparing two arrays are equal. The second row's index and entries are permuted based on
     * cycle transposition and given permutation.
     *
     * @param index int row index
     * @param a int[][] adjacency matrix
     * @param cycleTransposition Permutation cycle transposition
     * @param permutation Permutation permutation
     * @return boolean
     */
    public boolean equalRowsCheck(
            int index, int[][] a, Permutation cycleTransposition, Permutation permutation) {
        int[] canonical = a[index];
        int[] original;
        int newIndex = findIndex(index, cycleTransposition);
        Permutation pm = permutation.multiply(cycleTransposition);
        original = cloneArray(a[newIndex]);
        original = actArray(original, pm);
        return Arrays.equals(canonical, original);
    }

    /**
     * Sorting entries of a subarray specified by indices.
     *
     * @param array int[] array
     * @param index0 int beginning index
     * @param index1 int last index
     * @return int[]
     */
    public int[] descendingSort(int[] array, int index0, int index1) {
        int temp;
        for (int i = index0; i < index1; i++) {
            for (int j = i + 1; j < index1; j++) {
                if (array[i] < array[j]) {
                    temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }
        return array;
    }

    /**
     * Sorting entries of a int array for a given atom partition.
     *
     * @param array int[] array
     * @param partition int[] atom partition
     * @return int[]
     */
    public int[] descendingSortWithPartition(int[] array, int[] partition) {
        int i = 0;
        int p;
        int limit = findZeros(partition);
        for (int i1 = 0; i1 < limit; i1++) {
            p = partition[i1];
            array = descendingSort(array, i, i + p);
            i = i + p;
        }
        return array;
    }

    /**
     * Checks two arrays in descending order. The second row is sorted in descending order just to
     * check whether there is a possible permutations making the first row non-maximal.
     *
     * @param index int row index
     * @param firstRow int[] array
     * @param check int[] array
     * @param partition int[] atom partition
     * @return boolean
     */
    public boolean biggerCheck(int index, int[] firstRow, int[] check, int[] partition) {
        int[] sorted = cloneArray(check);
        sorted = descendingSortWithPartition(sorted, partition);
        return descendingOrderUpperMatrixCheck(index, partition, firstRow, sorted);
    }

    /**
     * Checks whether there is a permutation making the row bigger in descending order.
     *
     * @param index int row index
     * @param a int[][] adjacency matrix
     * @param permutation Permutation permutation
     * @param partition int[] atom partition
     * @return boolean
     */
    public boolean setBiggest(int index, int[][] a, Permutation permutation, int[] partition) {
        int[] check = row2compare(index, a, permutation);
        return biggerCheck(index, a[index], check, partition);
    }

    /**
     * Get indices from "learning from canonical test" method. Here, the entry makes the row
     * non-canonical is detected. Its indices are set to nonCanonicalIndices global variables.
     *
     * @param index int row index
     * @param a the adjacency matrix
     * @param cycles the list of cycle transpositions
     * @param partition the atom partition
     * @param nonCanonicalIndices the nonCanonicalIndices
     * @param learningFromCanonicalTest the learningFromCanonicalTest
     */
    public void getLernenIndices(
            int index,
            int[][] a,
            List<Permutation> cycles,
            int[] partition,
            int[] nonCanonicalIndices,
            boolean[] learningFromCanonicalTest) {
        int[] check;
        for (Permutation cycle : cycles) {
            check = row2compare(index, a, cycle);
            if (!biggerCheck(index, a[index], check, partition)) {
                setLernenIndices(
                        index,
                        cycle,
                        a,
                        check,
                        partition,
                        nonCanonicalIndices,
                        learningFromCanonicalTest);
                break;
            }
        }
    }

    /**
     * Setting the nonCanonicalIndices global variable.
     *
     * @param rowIndex1 int first row index
     * @param cycle Permutation cycle transposition
     * @param a int[][] adjacency matrix
     * @param secondRow int[] second row
     * @param partition int[] atom partition
     * @param nonCanonicalIndices the nonCanonicalIndices
     * @param learningFromCanonicalTest the learningFromCanonicalTest
     */
    public void setLernenIndices(
            int rowIndex1,
            Permutation cycle,
            int[][] a,
            int[] secondRow,
            int[] partition,
            int[] nonCanonicalIndices,
            boolean[] learningFromCanonicalTest) {
        System.arraycopy(new int[2], 0, nonCanonicalIndices, 0, 2);
        learningFromCanonicalTest[0] = false;
        int rowIndex2 = cycle.get(rowIndex1);
        Permutation permutation = getNonCanonicalMakerPermutation(secondRow, cycle, partition);
        learningFromCanonicalTest[0] = true;
        System.arraycopy(
                upperIndex(rowIndex1, rowIndex2, a, permutation), 0, nonCanonicalIndices, 0, 2);
    }

    /**
     * Calculating the permutation, permuting the second row and making first row non maximal.
     *
     * @param array the array
     * @param cycle the cycle
     * @param partition the partition
     * @return the Permutation
     */
    public Permutation getNonCanonicalMakerPermutation(
            int[] array, Permutation cycle, int[] partition) {
        int[] sorted = cloneArray(array);
        sorted = descendingSortWithPartition(sorted, partition);
        Permutation permutation = getCanonicalPermutation(sorted, array, partition);
        return permutation.multiply(cycle);
    }

    /**
     * For a row given by index, checking whether it is in maximal form or not. If not, the
     * nonCanonicalIndices is set.
     *
     * @param array int[] array
     * @return boolean
     */
    public boolean zero(int[] array) {
        boolean check = false;
        for (int i = 0; i < size; i++) {
            if (array[i] == 0) {
                check = true;
                break;
            }
        }
        return check;
    }

    /**
     * The row descending test is the part of canonical test function.
     *
     * @param index int index
     * @param a int[][] adjacency matrix
     * @param partition int[] atom partition
     * @param nonCanonicalIndices int[] the indices of the non canonical entry
     * @param learningFromCanonicalTest boolean[] the boolean setting
     * @return boolean
     */
    public boolean rowDescendingTest(
            int index,
            int[][] a,
            int[] partition,
            int[] nonCanonicalIndices,
            boolean[] learningFromCanonicalTest) {
        boolean check = true;
        if (zero(partition) && !descendingOrderCheck(partition, a[index])) {
            check = false;
            int[] array = cloneArray(a[index]);
            array = descendingSortWithPartition(array, partition);
            Permutation canonicalPermutation = getCanonicalPermutation(array, a[index], partition);
            learningFromCanonicalTest[0] = true;
            System.arraycopy(
                    upperIndex(index, index, a, canonicalPermutation),
                    0,
                    nonCanonicalIndices,
                    0,
                    2);
        }
        return check;
    }

    /**
     * By a given permutation, checking which entry is mapped to the index.
     *
     * @param permutation Permutation permutation
     * @param index int entry index in the row
     * @return int
     */
    public int getPermutedIndex(Permutation permutation, int index) {
        int out = 0;
        for (int i = 0; i < permutation.size(); i++) {
            if (permutation.get(i) == index) {
                out += i;
                break;
            }
        }
        return out;
    }

    /**
     * Looking for the upper limit where the original entry is smaller.
     *
     * @param index int row index
     * @param nextRowIndex int index of the row to compare
     * @param a int[][] adjacency matrix
     * @param permutation Permutation permutation from canonical test
     * @return int[]
     */
    public int[] limit(int index, int nextRowIndex, int[][] a, Permutation permutation) {
        int[] original = a[index];
        int[] permuted = a[nextRowIndex];
        int[] limit = new int[2];
        limit[0] = index;
        int newIndex;
        int value;
        int newValue;
        for (int i = index + 1; i < size; i++) {
            newIndex = getPermutedIndex(permutation, i);
            value = original[i];
            newValue = permuted[newIndex];
            if (value != newValue) {
                if (value < newValue) {
                    limit[1] = i;
                }
                break;
            }
        }
        return limit;
    }

    /**
     * Looking for the maximum index where the entry is not zero.
     *
     * @param index int row index
     * @param nextRowIndex int index of the row to compare
     * @param a int[][] adjacency matrix
     * @param permutation Permutation permutation from canonical test
     * @return int[]
     */
    public int[] lowerIndex(int index, int nextRowIndex, int[][] a, Permutation permutation) {
        int max = 0;
        int upperLimit = limit(index, nextRowIndex, a, permutation)[1];
        int[] permuted = a[nextRowIndex];
        int newIndex;
        int newValue;
        for (int i = index + 1; i < upperLimit; i++) {
            newIndex = getPermutedIndex(permutation, i);
            newValue = permuted[newIndex];
            if (newValue > 0 && max < newIndex) {
                max = newIndex;
            }
        }
        return new int[] {nextRowIndex, max};
    }

    /**
     * We need to calculate upperIndex. First, we need our j index where the original row become
     * smaller, then calculating the lower index. Based on these two values and the value of j in
     * the permutation, we calculate our upper index.
     *
     * <p>This upper index is used for the 'learning from canonical test' method.
     *
     * @param index int row index
     * @param nextRowIndex int index of the row to compare
     * @param a int[][] adjacency matrix
     * @param permutation Permutation permutation from canonical test
     * @return int[]
     */
    public int[] upperIndex(int index, int nextRowIndex, int[][] a, Permutation permutation) {
        int[] limit = limit(index, nextRowIndex, a, permutation);
        int[] lowerLimit = lowerIndex(index, nextRowIndex, a, permutation);
        int[] upperLimit = new int[2];
        upperLimit[0] = nextRowIndex;
        upperLimit[1] = getPermutedIndex(permutation, limit[1]);
        int[] maximalIndices = getMaximumPair(upperLimit, getMaximumPair(limit, lowerLimit));
        maximalIndices = maximalIndexWithNonZeroEntry(a, maximalIndices);
        return getTranspose(maximalIndices);
    }

    /**
     * In case if the index' entry is zero, updating the index with next index with non-zero entry.
     *
     * @param a int[][] adjacency matrix
     * @param maximalIndices int[] maximal indices for canonical test
     * @return int[]
     */
    public int[] maximalIndexWithNonZeroEntry(int[][] a, int[] maximalIndices) {
        int rowIndex = maximalIndices[0];
        int columnIndex = maximalIndices[1];
        if ((columnIndex > rowIndex) && a[rowIndex][columnIndex] != 0) {
            return maximalIndices;
        } else {
            int[] output = new int[2];
            for (int i = columnIndex; i < size; i++) {
                if (a[rowIndex][i] > 0) {
                    output[0] = rowIndex;
                    output[1] = i;
                    break;
                }
            }
            return output;
        }
    }

    /**
     * For an index pair, getting its transpose.
     *
     * @param indices int[] indices
     * @return int[]
     */
    public int[] getTranspose(int[] indices) {
        int[] out = new int[2];
        if (indices[0] > indices[1]) {
            out[0] = indices[1];
            out[1] = indices[0];
            return out;
        } else {
            return indices;
        }
    }

    /**
     * Between two index pairs, getting the bigger indices.
     *
     * @param a int[] indices
     * @param b int[] indices
     * @return int[]
     */
    public int[] getMaximumPair(int[] a, int[] b) {
        if (a[0] > b[0]) {
            return a;
        } else if (b[0] > a[0]) {
            return b;
        } else {
            if (a[1] > b[1]) {
                return a;
            } else if (b[1] > a[1]) {
                return b;
            } else {
                return a;
            }
        }
    }

    /**
     * Comparing two arrays for specific range of entries, whether the first array is bigger than
     * the second one or not.
     *
     * @param array1 int[] first array
     * @param array2 int[] second array
     * @param index1 int beginning index
     * @param index2 int last index
     * @return boolean
     */
    public boolean compare(int[] array1, int[] array2, int index1, int index2) {
        boolean check = true;
        for (int i = index1; i < index2; i++) {
            if (array1[i] != array2[i]) {
                if (array1[i] < array2[i]) {
                    check = false;
                    break;
                } else if (array1[i] > array2[i]) {
                    break;
                }
            }
        }
        return check;
    }

    /**
     * Checks the first row is bigger than the second row just in the upper matrix.
     *
     * @param index int row index
     * @param partition int[] atom partition
     * @param firstRow int[] first row
     * @param secondRow int[] second row
     * @return boolean
     */
    public boolean descendingOrderUpperMatrixCheck(
            int index, int[] partition, int[] firstRow, int[] secondRow) {
        boolean check = true;
        int i = index + 1;
        int p;
        int limit = findZeros(partition);
        for (int k = index + 1; k < limit; k++) {
            p = partition[k];
            if (descendingOrderCheck(firstRow, i, (i + p))) {
                if (!compareIndexwise(firstRow, secondRow, i, (i + p))) {
                    check = compare(firstRow, secondRow, i, (i + p));
                    break;
                }
                i = i + p;
            } else {
                check = false;
                break;
            }
        }
        return check;
    }

    /**
     * Checks subarray of specified range of entries, the array is descending order or not.
     *
     * @param array int[] array
     * @param f int first index
     * @param l int last index
     * @return boolean
     */
    public boolean descendingOrderCheck(int[] array, int f, int l) {
        boolean check = true;
        for (int i = f; i < l - 1; i++) {
            if (array[i] < array[i + 1]) {
                check = false;
                break;
            }
        }
        return check;
    }

    /**
     * Checks a int array is in descending order or not with respect to a given atom partition.
     *
     * @param partition int[] atom partition
     * @param array the int array
     * @return boolean
     */
    public boolean descendingOrderCheck(int[] partition, int[] array) {
        boolean check = true;
        int i = 0;
        int value;
        int limit = findZeros(partition);
        for (int s = 0; s < limit; s++) {
            value = partition[s];
            if (!descendingOrderCheck(array, i, (value + i))) {
                check = false;
                break;
            } else {
                i = i + value;
            }
        }
        return check;
    }

    /* ******************************************************************** */

    /* Candidate Matrix Generation Functions */

    /**
     * l; upper triangular matrix like given in 3.2.1. For (i,j), after the index, giving the
     * maximum line capacity.
     *
     * @param degrees the degrees
     * @param max the max
     * @param l the l
     */
    public void upperTriangularL(int[] degrees, int[][][] max, int[][][] l) {
        l[0] = new int[hIndex][hIndex];
        if (hIndex == 2) {
            for (int i = 0; i < hIndex; i++) {
                for (int j = i + 1; j < hIndex; j++) {
                    l[0][i][j] = Math.min(degrees[i], lsum(i, j, max));
                }
            }
        } else {
            for (int i = 0; i < hIndex; i++) {
                for (int j = i + 1; j < hIndex; j++) {
                    l[0][i][j] = Math.min(degrees[i], lsum(i, j + 1, max));
                }
            }
        }
    }

    /**
     * c; upper triangular matrix like given in 3.2.1. For (i,j), after the index, giving the
     * maximum column capacity.
     *
     * @param degrees int[] valences
     * @param max the max
     * @param c the c
     */
    public void upperTriangularC(int[] degrees, int[][][] max, int[][][] c) {
        c[0] = new int[hIndex][hIndex];
        if (hIndex == 2) {
            for (int i = 0; i < hIndex; i++) {
                for (int j = i + 1; j < hIndex; j++) {
                    c[0][i][j] = Math.min(degrees[j], csum(i, j, max));
                }
            }
        } else {
            for (int i = 0; i < hIndex; i++) {
                for (int j = i + 1; j < hIndex; j++) {
                    c[0][i][j] = Math.min(degrees[j], csum(i + 1, j, max));
                }
            }
        }
    }

    /**
     * Summing ith rows entries starting from the jth column.
     *
     * @param i int row index
     * @param j int column index
     * @param max the max
     * @return the lsum
     */
    public int lsum(int i, int j, int[][][] max) {
        int sum = 0;
        for (int k = j; k < hIndex; k++) {
            sum = sum + max[0][i][k];
        }
        return sum;
    }

    /**
     * Summing ith column entries starting from the jth row.
     *
     * @param i int column index
     * @param j int row index
     * @param max the max
     * @return the csum
     */
    public int csum(int i, int j, int[][][] max) {
        int sum = 0;
        for (int k = i; k < hIndex; k++) {
            sum = sum + max[0][k][j];
        }
        return sum;
    }

    /**
     * Possible maximal edge multiplicity for the atom pair (i,j).
     *
     * @param degrees the degrees
     * @param max the max
     */
    public void maximalMatrix(int[] degrees, int[][][] max) {
        max[0] = new int[hIndex][hIndex];
        for (int i = 0; i < hIndex; i++) {
            for (int j = 0; j < hIndex; j++) {
                int di = degrees[i];
                int dj = degrees[j];
                if (i == j) {
                    max[0][i][j] = 0;
                } else {
                    if (di != dj) {
                        max[0][i][j] = Math.min(di, dj);
                    } else {
                        checkJustH(max, i, j, di);
                    }
                }
            }
        }
    }

    public void checkJustH(int[][][] max, int i, int j, int di) {
        if (justH) {
            max[0][i][j] = di;
        } else {
            if (hIndex == 2) {
                max[0][i][j] = di;
            } else {
                if (di != 1) {
                    max[0][i][j] = (di - 1);
                } else {
                    max[0][i][j] = di;
                }
            }
        }
    }

    /**
     * Initialization of global variables for the generate of structures for given degree list.
     *
     * @param ac the IAtomContainer
     * @param symbolArrayCopy the symbolArrayCopy
     * @param degreeList int[] valences
     * @param initialPartition the initial partition
     * @param partitionList the partitionList
     * @param connectivityIndices the connectivityIndices
     * @param learningFromConnectivity the learningFromConnectivity
     * @param nonCanonicalIndices the nonCanonicalIndices
     * @param formerPermutations the formerPermutations
     * @param hydrogens the hydrogens
     * @param partSize the partSize
     * @param r the r
     * @param y the y
     * @param z the z
     * @param ys the ys
     * @param zs the zs
     * @param learningFromCanonicalTest the learningFromCanonicalTest
     * @throws IOException in case of IOException
     * @throws CDKException in case of CDKException
     * @throws CloneNotSupportedException in case of CloneNotSupportedException
     */
    public void generate(
            IAtomContainer ac,
            String[] symbolArrayCopy,
            int[] degreeList,
            int[] initialPartition,
            int[][] partitionList,
            int[] connectivityIndices,
            boolean[] learningFromConnectivity,
            int[] nonCanonicalIndices,
            List<ArrayList<Permutation>> formerPermutations,
            int[] hydrogens,
            int[] partSize,
            int[] r,
            int[] y,
            int[] z,
            int[][] ys,
            int[][] zs,
            boolean[] learningFromCanonicalTest)
            throws IOException, CloneNotSupportedException, CDKException {
        int[][] a = new int[matrixSize][matrixSize];
        int[] degrees = degreeList;
        boolean[] flag = new boolean[] {true};
        int[][][] max = new int[][][] {new int[0][0]};
        int[][][] l = new int[][][] {new int[0][0]};
        int[][][] c = new int[][][] {new int[0][0]};
        maximalMatrix(degrees, max);
        upperTriangularL(degrees, max, l);
        upperTriangularC(degrees, max, c);
        int[] indices = new int[2];
        indices[0] = 0;
        indices[1] = 1;
        boolean[] callForward = {true};
        r[0] = 0;
        y[0] = ys[0][r[0]];
        z[0] = zs[0][r[0]];
        while (flag[0]) {
            nextStep(
                    ac,
                    symbolArrayCopy,
                    a,
                    indices,
                    degrees,
                    initialPartition,
                    partitionList,
                    callForward,
                    connectivityIndices,
                    learningFromConnectivity,
                    nonCanonicalIndices,
                    formerPermutations,
                    hydrogens,
                    partSize,
                    r,
                    y,
                    z,
                    max,
                    l,
                    c,
                    ys,
                    zs,
                    learningFromCanonicalTest,
                    flag);
            if (!flag[0]) {
                break;
            }
            if (learningFromConnectivity[0]) {
                indices = connectivityIndices;
                findR(indices, initialPartition, r);
                int value = indexYZ(initialPartition, r);
                y[0] = ys[0][value];
                clearFormers(false, y[0], partitionList, formerPermutations);
                learningFromConnectivity[0] = false;
                callForward[0] = false;
            } else {
                if (learningFromCanonicalTest[0]) {
                    indices = successor(nonCanonicalIndices, max[0].length);
                    findR(indices, initialPartition, r);
                    learningFromCanonicalTest[0] = false;
                    callForward[0] = false;
                }
            }
        }
    }

    /**
     * Calculation of the next index pair in a matrix.
     *
     * @param indices int[] index pair.
     * @param localSize int row length.
     * @return int[]
     */
    public int[] successor(int[] indices, int localSize) {
        int i0 = indices[0];
        int i1 = indices[1];
        if (i1 < (localSize - 1)) {
            indices[0] = i0;
            indices[1] = (i1 + 1);
        } else if (i0 < (localSize - 2) && i1 == (localSize - 1)) {
            indices[0] = (i0 + 1);
            indices[1] = (i0 + 2);
        }
        return indices;
    }

    /**
     * Calculation of the former index pair in a matrix.
     *
     * @param indices int[] index pair.
     * @param localSize int row length.
     * @return int[]
     */
    public int[] predecessor(int[] indices, int localSize) {
        int i0 = indices[0];
        int i1 = indices[1];
        if (i0 == i1 - 1) {
            indices[0] = i0 - 1;
            indices[1] = localSize - 1;
        } else {
            indices[0] = i0;
            indices[1] = (i1 - 1);
        }
        return indices;
    }

    /**
     * Calling foward or backward function in a nextstep function.
     *
     * @param ac the IAtomContainer
     * @param symbolArrayCopy the symbolArrayCopy
     * @param a the a matrix
     * @param indices the indices
     * @param degrees the degrees
     * @param initialPartition the initial partition
     * @param partitionList the partitionList
     * @param callForward the callForward
     * @param connectivityIndices the connectivityIndices
     * @param learningFromConnectivity the learningFromConnectivity
     * @param nonCanonicalIndices the nonCanonicalIndices
     * @param formerPermutations the formerPermutations
     * @param hydrogens the hydrogens
     * @param partSize the partSize
     * @param r the r
     * @param y the y
     * @param z the z
     * @param max the max
     * @param l the l
     * @param c the c
     * @param ys the ys
     * @param zs the zs
     * @param learningFromCanonicalTest the learningFromCanonicalTest
     * @param flag the flag
     * @throws IOException in case of IOException
     * @throws CDKException in case of CDKException
     * @throws CloneNotSupportedException in case of CloneNotSupportedException
     */
    public void nextStep(
            IAtomContainer ac,
            String[] symbolArrayCopy,
            int[][] a,
            int[] indices,
            int[] degrees,
            int[] initialPartition,
            int[][] partitionList,
            boolean[] callForward,
            int[] connectivityIndices,
            boolean[] learningFromConnectivity,
            int[] nonCanonicalIndices,
            List<ArrayList<Permutation>> formerPermutations,
            int[] hydrogens,
            int[] partSize,
            int[] r,
            int[] y,
            int[] z,
            int[][][] max,
            int[][][] l,
            int[][][] c,
            int[][] ys,
            int[][] zs,
            boolean[] learningFromCanonicalTest,
            boolean[] flag)
            throws IOException, CloneNotSupportedException, CDKException {
        if (callForward[0]) {
            forward(
                    ac,
                    symbolArrayCopy,
                    a,
                    indices,
                    degrees,
                    initialPartition,
                    partitionList,
                    callForward,
                    connectivityIndices,
                    learningFromConnectivity,
                    nonCanonicalIndices,
                    formerPermutations,
                    hydrogens,
                    partSize,
                    r,
                    y,
                    z,
                    max,
                    l,
                    c,
                    ys,
                    zs,
                    learningFromCanonicalTest);
        } else {
            backward(a, indices, degrees, initialPartition, callForward, r, max, l, c, flag);
        }
    }

    /**
     * After generating matrices, adding the hydrogen with respect to the pre-hydrogen distribution.
     *
     * @param a the adjacency matrix
     * @param index int beginning index for the hydrogen setting
     * @param hydrogens the hydrogens
     * @return the adjacency matrix
     */
    public int[][] addHydrogens(int[][] a, int index, int[] hydrogens) {
        int localHIndex = index;
        if (singleAtom) {
            int hydrogen = valences.get(symbolArray[0]);
            for (int j = localHIndex; j < hydrogen + localHIndex; j++) {
                a[0][j] = 1;
                a[j][0] = 1;
            }
        } else if (callHydrogenDistributor) {
            int limit;
            int hydrogen;
            for (int i = 0; i < index; i++) {
                hydrogen = hydrogens[i];
                limit = localHIndex + hydrogen;
                for (int j = localHIndex; j < limit; j++) {
                    a[i][j] = 1;
                    a[j][i] = 1;
                }
                if (hydrogen != 0) {
                    localHIndex = localHIndex + hydrogen;
                }
            }
        }
        return a;
    }

    /**
     * Finding the R index of a block
     *
     * @param indices int[] entry indices
     * @param initialPartition int[] initial partition
     * @param r int[] r
     */
    public void findR(int[] indices, int[] initialPartition, int[] r) {
        int block = 0;
        int index = 0;
        int part;
        int rowIndex = indices[0];
        int limit = findZeros(initialPartition);
        for (int i = 0; i < limit; i++) {
            part = initialPartition[i];
            if (index <= rowIndex && rowIndex < (index + part)) {
                break;
            } else {
                block++;
            }
            index = index + part;
        }
        r[0] = block;
    }

    /**
     * The criteria to decide which function is needed: forward or backward.
     *
     * @param x the value in the adjacency matrix a[i][j]
     * @param lInverse lInverse value of indices {i,j}
     * @param l the l parameter
     * @return the criteria
     */
    public boolean backwardCriteria(int x, int lInverse, int l) {
        int newX = (x - 1);
        return lInverse - newX <= l;
    }

    /**
     * Backward step in the algorithm.
     *
     * @param a the adjacency matrix
     * @param indices the indices
     * @param degrees the degrees
     * @param initialPartition the initial partition
     * @param callForward the callForward
     * @param r the r
     * @param max the max
     * @param l the l
     * @param c the c
     * @param flag the flag
     * @return the int[][]
     */
    public int[][] backward(
            int[][] a,
            int[] indices,
            int[] degrees,
            int[] initialPartition,
            boolean[] callForward,
            int[] r,
            int[][][] max,
            int[][][] l,
            int[][][] c,
            boolean[] flag) {
        int i = indices[0];
        int j = indices[1];

        if (i == 0 && j == 1) {
            flag[0] = false;
        } else {
            indices = predecessor(indices, max[0].length);
            findR(indices, initialPartition, r);
            i = indices[0];
            j = indices[1];
            int x = a[i][j];
            int l2 = lInverse(i, j, a, degrees);
            int c2 = cInverse(i, j, a, degrees);

            if (x > 0
                    && (backwardCriteria((x), l2, l[0][i][j])
                            && backwardCriteria((x), c2, c[0][i][j]))) {
                a[i][j] = (x - 1);
                a[j][i] = (x - 1);
                indices = successor(indices, max[0].length);
                findR(indices, initialPartition, r);
                callForward[0] = true;
            } else {
                callForward[0] = false;
            }
        }
        return a;
    }

    /**
     * Setting successor indices entry if there is a possible filling.
     *
     * @param ac the IAtomContainer
     * @param symbolArrayCopy the symbolArrayCopy
     * @param a the adjacency matrix
     * @param indices the entry indices
     * @param degrees the degrees
     * @param initialPartition the initial partition
     * @param partitionList the partitionList
     * @param callForward the callForward
     * @param connectivityIndices the connectivityIndices
     * @param learningFromConnectivity the learningFromConnectivity
     * @param nonCanonicalIndices the nonCanonicalIndices
     * @param formerPermutations the formerPermutations
     * @param hydrogens the hydrogens
     * @param partSize the partSize
     * @param r the r
     * @param y the y
     * @param z the z
     * @param max the max
     * @param l the l
     * @param c the c
     * @param ys the ys
     * @param zs the zs
     * @param learningFromCanonicalTest the learningFromCanonicalTest
     * @return int[][]
     * @throws IOException in case of IOException
     * @throws CDKException in case of CDKException
     * @throws CloneNotSupportedException in case of CloneNotSupportedException
     */
    public int[][] forward(
            IAtomContainer ac,
            String[] symbolArrayCopy,
            int[][] a,
            int[] indices,
            int[] degrees,
            int[] initialPartition,
            int[][] partitionList,
            boolean[] callForward,
            int[] connectivityIndices,
            boolean[] learningFromConnectivity,
            int[] nonCanonicalIndices,
            List<ArrayList<Permutation>> formerPermutations,
            int[] hydrogens,
            int[] partSize,
            int[] r,
            int[] y,
            int[] z,
            int[][][] max,
            int[][][] l,
            int[][][] c,
            int[][] ys,
            int[][] zs,
            boolean[] learningFromCanonicalTest)
            throws IOException, CloneNotSupportedException, CDKException {
        int i = indices[0];
        int j = indices[1];
        int lInverse = lInverse(i, j, a, degrees);
        int cInverse = cInverse(i, j, a, degrees);
        int minimumValue = Math.min(max[0][i][j], Math.min(lInverse, cInverse));
        int maximumValue = maximalEntry(minimumValue, lInverse, l[0][i][j], cInverse, c[0][i][j]);
        callForward[0] = true;
        return forward(
                ac,
                symbolArrayCopy,
                lInverse,
                cInverse,
                maximumValue,
                i,
                j,
                a,
                indices,
                initialPartition,
                partitionList,
                callForward,
                connectivityIndices,
                learningFromConnectivity,
                nonCanonicalIndices,
                formerPermutations,
                hydrogens,
                partSize,
                r,
                y,
                z,
                max,
                l,
                c,
                ys,
                zs,
                learningFromCanonicalTest);
    }

    public int[][] forward(
            IAtomContainer ac,
            String[] symbolArrayCopy,
            int lInverse,
            int cInverse,
            int maximalX,
            int i,
            int j,
            int[][] a,
            int[] indices,
            int[] initialPartition,
            int[][] partitionList,
            boolean[] callForward,
            int[] connectivityIndices,
            boolean[] learningFromConnectivity,
            int[] nonCanonicalIndices,
            List<ArrayList<Permutation>> formerPermutations,
            int[] hydrogens,
            int[] partSize,
            int[] r,
            int[] y,
            int[] z,
            int[][][] max,
            int[][][] l,
            int[][][] c,
            int[][] ys,
            int[][] zs,
            boolean[] learningFromCanonicalTest)
            throws IOException, CloneNotSupportedException, CDKException {
        if (((lInverse - maximalX) <= l[0][i][j]) && ((cInverse - maximalX) <= c[0][i][j])) {
            a[i][j] = maximalX;
            a[j][i] = maximalX;
            if (i == (max[0].length - 2) && j == (max[0].length - 1)) {
                boolean boundaryCheck = true;
                if (boundary)
                    boundaryCheck = BoundaryConditions.boundaryConditionCheck(a, symbolArrayCopy);
                if (boundaryCheck
                        && canonicalTest(
                                a,
                                initialPartition,
                                partitionList,
                                nonCanonicalIndices,
                                formerPermutations,
                                partSize,
                                r,
                                y,
                                z,
                                ys,
                                zs,
                                learningFromCanonicalTest)) {
                    if (connectivityTest(a, connectivityIndices, learningFromConnectivity)) {
                        count.incrementAndGet();
                        if (ac.getAtomCount() != 0 ) {
                            IAtomContainer mol = buildAtomContainerFromMatrix(
                                    addHydrogens(a, hIndex, hydrogens),
                                    ac.clone());
                            emit(mol);
                        }
                        callForward[0] = false;
                    } else {
                        callForward[0] = false;
                        learningFromConnectivity[0] = true;
                    }
                } else {
                    if (!learningFromCanonicalTest[0]) {
                        callForward[0] = false;
                    }
                }
            } else {
                int value = indexYZ(initialPartition, r);
                if (indices[0] == zs[0][value] && indices[1] == (max[0].length - 1)) {
                    // We cant set boundary condition here. For example, a matrix can have triple
                    // bond. When we filter it,
                    // we also filter the other isomers that we get by decrementing the triple bond
                    // order of that matrix.
                    callForward[0] =
                            canonicalTest(
                                    a,
                                    initialPartition,
                                    partitionList,
                                    nonCanonicalIndices,
                                    formerPermutations,
                                    partSize,
                                    r,
                                    y,
                                    z,
                                    ys,
                                    zs,
                                    learningFromCanonicalTest);

                    if (callForward[0]) {
                        indices = successor(indices, max[0].length);
                        findR(indices, initialPartition, r);
                    } else {
                        callForward[0] = false;
                    }
                } else {
                    indices = successor(indices, max[0].length);
                    findR(indices, initialPartition, r);
                    callForward[0] = true;
                }
            }
        } else {
            callForward[0] = false;
        }
        return a;
    }

    /**
     * Calculating the maximal entry for the indices.
     *
     * @param min int minimum of l, c amd maximal matrices for {i,j} indices.
     * @param lInverse int Linverse value of {i,j}
     * @param l int l value of {i,j}
     * @param cInverse int Cinverse value of {i,j}
     * @param c int c value of {i,j}
     * @return int max
     */
    public int maximalEntry(int min, int lInverse, int l, int cInverse, int c) {
        int max = 0;
        for (int v = min; v >= 0; v--) {
            if (((lInverse - v) <= l) && ((cInverse - v) <= c)) {
                max = max + v;
                break;
            }
        }
        return max;
    }

    /**
     * Calculating the sum of the entries in the ith row until the jth column.
     *
     * @param i int row index
     * @param j int column index
     * @param a the adjacency matrix
     * @param degrees the degrees
     * @return int
     */
    public int lInverse(int i, int j, int[][] a, int[] degrees) {
        int sum = 0;
        if (hIndex == 2) {
            for (int s = 0; s <= j; s++) {
                sum = sum + a[i][s];
            }
        } else {
            for (int s = 0; s < j; s++) {
                sum = sum + a[i][s];
            }
        }
        return degrees[i] - sum;
    }

    /**
     * Calculating the sum of the entries in the jth column until the ith row.
     *
     * @param i int row index
     * @param j int column index
     * @param a the adjacency matrix
     * @param degrees the degrees
     * @return int
     */
    public int cInverse(int i, int j, int[][] a, int[] degrees) {
        int sum = 0;
        if (hIndex == 2) {
            for (int s = 0; s <= i; s++) {
                sum = sum + a[s][j];
            }
        } else {
            for (int s = 0; s < i; s++) {
                sum = sum + a[s][j];
            }
        }
        return degrees[j] - sum;
    }

    /**
     * Based on the new degrees and the former partition, getting the new atom partition.
     *
     * @param degrees int[] new atom valences
     * @return int[]
     */
    public int[] getPartition(int[] degrees) {
        int[] newPartition = new int[degrees.length];
        int i = 0;
        int p;
        int length;
        if (justH || noHydrogen) {
            length = firstOccurrences.length;
        } else {
            length = firstOccurrences.length - 1;
        }
        int index = 0;
        for (int part = 0; part < length; part++) {
            p = firstOccurrences[part];
            int[] subArray = getBlocks(degrees, i, p + i);
            for (Integer item : getSubPartition(subArray)) {
                newPartition[index] = item;
                index++;
            }
            i = i + p;
        }
        return newPartition;
    }

    /**
     * Calculating the sub partitions for a given group of degrees.
     *
     * @param degrees int[] valences
     * @return int[]
     */
    public int[] getSubPartition(int[] degrees) {
        int i = 0;
        int localSize = degrees.length;
        int[] partition = new int[localSize];
        int next;
        int index = 0;
        int[] result;
        while (i < localSize) {
            result = nextCount(index, i, localSize, degrees, partition);
            index = result[1];
            next = (i + result[0]);
            if (next == localSize) {
                break;
            } else {
                i = next;
            }
        }
        return partition;
    }

    /**
     * Counting the occurrence of a value in a degree.
     *
     * @param index the index
     * @param i the i
     * @param localSize int number
     * @param degrees int[] valences
     * @param partition int[] partition
     * @return int
     */
    public int[] nextCount(int index, int i, int localSize, int[] degrees, int[] partition) {
        int localCount = 1;
        if (i == (localSize - 1)) {
            partition[index] = 1;
            index++;
        } else {
            for (int j = i + 1; j < localSize; j++) {
                if (degrees[i] == degrees[j]) {
                    localCount++;
                    if (j == (localSize - 1)) {
                        partition[index] = localCount;
                        index++;
                        break;
                    }
                } else {
                    partition[index] = localCount;
                    index++;
                    break;
                }
            }
        }
        return new int[] {localCount, index};
    }

    /**
     * checking whether a molecular formula is length 2 or not. The length is counted based on the
     * number of isotopes.
     *
     * @param atoms String[] atom symbols
     * @return boolean
     */
    public boolean checkLengthTwoFormula(String[] atoms) {
        boolean check = true;
        String[] info2;
        if (atoms.length == 1) {
            String[] info = atoms[0].split(NUMBERS_FROM_0_TO_9, 2);
            if (atoms[0].contains("(")) {
                info2 = info[1].split("\\)");
                if (info2[1].equals("2") && Integer.parseInt(info2[0]) > 3) {
                    check = false;
                }
            } else {
                if (info[1].equals("2") && valences.get(info[0]) > 3) {
                    check = false;
                }
            }
        }
        return check;
    }

    /**
     * Main function to initialize the global variables and calling the generate function.
     *
     * @throws IOException in case of IOException
     * @throws CloneNotSupportedException in case of CloneNotSupportedException
     * @throws CDKException in case of CDKException
     */
    public void run() throws IOException, CDKException, CloneNotSupportedException {
        clearGlobals();
        if (Objects.nonNull(fuzzyFormula)) {
            if (!setElement) {
                fuzzyFormula = normalizeFormula(fuzzyFormula);
            }
            consumer.configure(fuzzyFormula);
            if (verbose)
                logger.info("MAYGEN is generating isomers of ", fuzzyFormula, "...");
            long startTime = System.nanoTime();
            fuzzyCount = 0;
            List<String> formulae = getFormulaList(fuzzyFormula);
            if (formulae.isEmpty()) {
                if (verbose)
                    logger.info(
                            THE_INPUT_FORMULA + fuzzyFormula + DOES_NOT_REPRESENT_ANY_MOLECULE);
            } else {
                for (String fuzzyFormulaItem : formulae) {
                    clearGlobals();
                    doRun(fuzzyFormulaItem);
                    fuzzyCount += count.get();
                }
                closeFilesAndDisplayStatistic(startTime);
            }
        } else {
            doRun(formula);
        }
        consumer.close();
    }

    public void closeFilesAndDisplayStatistic(long startTime) {
        if (verbose) {
            long endTime = System.nanoTime() - startTime;
            double seconds = endTime / 1000000000.0;
            DecimalFormat d = new DecimalFormat(".###");
            d.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            logger.info("The number of structures is: " + fuzzyCount);
            logger.info("Time: " + d.format(seconds) + " seconds");
        }
    }

    public void doRun(String localFormula)
            throws IOException, CDKException, CloneNotSupportedException {
        String normalizedLocalFormula = normalizeFormula(localFormula);
        if (setElement) normalizedLocalFormula = normalizedLocalFormula.replace("val=", "");
        String checkFormula = normalizedLocalFormula.replace("(", "");
        checkFormula = checkFormula.replace(")", "");
        String[] unsupportedSymbols = validateFormula(checkFormula);
        if (unsupportedSymbols.length > 0 && verbose) {
            logger.info(
                    "The input formula consists user defined element types: "
                            + String.join(", ", unsupportedSymbols));
        } else {
            long startTime = System.nanoTime();
            if (Objects.isNull(fuzzyFormula)) {
                if (verbose)
                    logger.info(
                            "MAYGEN is generating isomers of " + normalizedLocalFormula + "...");
                consumer.configure(normalizedLocalFormula);
            }
            processRun(normalizedLocalFormula, startTime);
        }
    }

    public void processRun(String normalizedLocalFormula, long startTime)
            throws IOException, CDKException, CloneNotSupportedException {
        String[] atoms = normalizedLocalFormula.split(LETTERS_FROM_A_TO_Z);
        if (setElement) getHigherValences(normalizedLocalFormula);
        if (checkLengthTwoFormula(atoms)) {
            singleAtomCheck(atoms);
            if (singleAtom) {
                if (canBuildIsomerSingle(normalizedLocalFormula)) {
                    getSingleAtomVariables(normalizedLocalFormula);
                    initSingleAC();
                    writeSingleAtom(new int[] {});
                    displayStatistic(startTime, normalizedLocalFormula);
                }
            } else {
                checkOxygenSulfur(atoms);
                processFormula(normalizedLocalFormula, startTime);
            }
        } else {
            if (verbose)
                logger.info(
                        THE_INPUT_FORMULA
                                + normalizedLocalFormula
                                + DOES_NOT_REPRESENT_ANY_MOLECULE);
        }
    }

    public void processFormula(String normalizedLocalFormula, long startTime)
            throws IOException, CDKException, CloneNotSupportedException {
        if (onlyDegree2) {
            if (oxygen == 0 || sulfur == 0) {
                degree2graph();
            } else {
                distributeSulfurOxygen(normalizedLocalFormula);
            }
            displayStatistic(startTime, normalizedLocalFormula);
        } else {
            if (canBuildIsomer(normalizedLocalFormula)) {
                getSymbolOccurrences(normalizedLocalFormula);
                initialDegrees();
                structureGenerator(normalizedLocalFormula);
                displayStatistic(startTime, normalizedLocalFormula);
            } else {
                if (Objects.isNull(fuzzyFormula) && verbose)
                    logger.info(
                            THE_INPUT_FORMULA
                                    + normalizedLocalFormula
                                    + DOES_NOT_REPRESENT_ANY_MOLECULE);
            }
        }
    }

    public void displayStatistic(long startTime, String localFormula) {
        long endTime = System.nanoTime() - startTime;
        double seconds = endTime / 1000000000.0;
        DecimalFormat d = new DecimalFormat(".###");
        d.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        if (Objects.isNull(fuzzyFormula) && verbose) {
            logger.info("The number of structures is: " + count);
            logger.info("Time: " + d.format(seconds) + " seconds");
        }

        if (tsvoutput) {
            System.out.println(
                    localFormula
                            + "\t"
                            + count
                            + "\t"
                            + d.format(seconds)
                            + "\t"
                            + (multiThread ? size : 1));
        }
    }

    /**
     * If there are hydrogens in the formula, calling the hydrogenDistributor. This is the
     * pre-hydrogen distribution. Then, the new list of degrees is defined for each hydrogen
     * distribution.
     *
     * @return the list of integer array
     */
    public List<int[]> distributeHydrogens() {
        List<int[]> degreeList = new ArrayList<>();
        if (!callHydrogenDistributor) {
            degreeList.add(firstDegrees);
        } else {
            List<int[]> distributions =
                    new HydrogenDistributor().run(firstOccurrences, firstDegrees);
            if (hIndex == 2) {
                fillDegreeListHindexIsTwo(degreeList, distributions);
            } else {
                for (int[] dist : distributions) {
                    int[] newDegree = new int[size];
                    for (int i = 0; i < size; i++) {
                        newDegree[i] = (firstDegrees[i] - dist[i]);
                    }
                    degreeList.add(newDegree);
                }
            }
        }
        return degreeList;
    }

    public void fillDegreeListHindexIsTwo(List<int[]> degreeList, List<int[]> distributions) {
        for (int[] dist : distributions) {
            int[] newDegree = new int[size];
            for (int i = 0; i < size; i++) {
                newDegree[i] = (firstDegrees[i] - dist[i]);
            }
            if (newDegree[0] == newDegree[1]) degreeList.add(newDegree);
        }
    }

    /**
     * Setting the y and z values for each block. y is the beginning index and z is the last index
     * of a block in the adjacency matrix.
     *
     * @param initialPartition the initial partition
     * @param ys the ys
     * @param zs the zs
     */
    public void setYZValues(int[] initialPartition, int[][] ys, int[][] zs) {
        ys[0] = new int[size];
        zs[0] = new int[size];
        int limit = findZeros(initialPartition);
        int value;
        int index = 0;
        int y;
        int z;
        for (int i = 0; i < limit; i++) {
            value = initialPartition[i];
            y = findY(i, initialPartition);
            z = findZ(i, initialPartition);
            for (int j = 0; j < value; j++) {
                ys[0][index] = y;
                zs[0][index] = z;
                index++;
            }
        }
    }

    /**
     * For a block index r, calculating its first row index.
     *
     * @param r int block index
     * @param initialPartition the initial partition
     * @return int
     */
    public int findY(int r, int[] initialPartition) {
        return sum(initialPartition, r - 1);
    }

    /**
     * For a block index r, calculating its last row index.
     *
     * @param r int block index
     * @param initialPartition the initial partition
     * @return int
     */
    public int findZ(int r, int[] initialPartition) {
        return (sum(initialPartition, r) - 1);
    }

    /**
     * Writing the single atom molecule in a output file.
     *
     * @param hydrogens int[] hydrogens
     * @throws IOException in case of IOException
     * @throws CDKException in case of CDKException
     * @throws CloneNotSupportedException in case of CloneNotSupportedException
     */
    public void writeSingleAtom(int[] hydrogens)
            throws IOException, CDKException, CloneNotSupportedException {
        int[][] a = new int[matrixSize][matrixSize];
        count.incrementAndGet();
        emit(buildContainer4SDF(atomContainer, addHydrogens(a, hIndex, hydrogens)));
    }

    /**
     * Calling the generate function for each degree values after the hydrogen distribution.
     *
     * @param degree int[] degree
     * @return int[]
     */
    public int[] setHydrogens(int[] degree) {
        int[] hydrogens = new int[size];
        for (int i = 0; i < size; i++) {
            hydrogens[i] = firstDegrees[i] - degree[i];
        }
        return hydrogens;
    }

    /**
     * After the hydrogen distribution, calling the structure generator functions.
     *
     * @param localFormula String localFormula
     */
    public void structureGenerator(String localFormula) {
        if (noHydrogen) {
            size = sum(firstOccurrences, firstOccurrences.length - 1);
        } else if (justH) {
            size = hIndex;
        } else {
            size = sum(firstOccurrences, firstOccurrences.length - 2);
        }
        List<int[]> newDegrees = distributeHydrogens();

        if (multiThread) {
            /* This idiom uses undocumented JDK behaviour that we can control the
               parallelization level by calling parallelStream inside a
               ForkJoinPool. */
            try {
                new ForkJoinPool(size).submit(() -> newDegrees.parallelStream()
                                                              .forEach(new Generation(this)::run)).get();
            } catch (InterruptedException | ExecutionException ex) {
                Thread.currentThread().interrupt();
                if (verbose)
                    logger.error("Failed during parallel generation: " + localFormula, ex);
            }
        } else {
            newDegrees.forEach(new Generation(this)::run);
        }
    }

    /** For several calls of the run function, setting the global variables. */
    public void clearGlobals() {
        singleAtom = true;
        onlyDegree2 = true;
        onSm = true;
        oxygen = 0;
        sulfur = 0;
        graphSize = 0;
        callHydrogenDistributor = false;
        total = 0;
        totalHydrogen = 0;
        size = 0;
        sizePart = 0;
        hIndex = 0;
        count.set(0);
        matrixSize = 0;
        justH = false;
        noHydrogen = false;
        oxygenSulfur = new ArrayList<>();
        symbols = new ArrayList<>();
        occurrences = null;
        symbolArray = null;
        firstSymbols = new ArrayList<>();
        symbols = new ArrayList<>();
        firstOccurrences = null;
    }

    /**
     * Finding the neighbors of a given index.
     *
     * @param index int row (atom) index
     * @param total int number of atoms.
     * @param mat the adjacency matrix
     * @return the nValues
     */
    public Set<Integer> nValues(int index, int total, int[][] mat) {
        Set<Integer> nValues = new HashSet<>();
        nValues.add(index);
        int[] theRow = mat[index];
        for (int i = (index + 1); i < total; i++) {
            if (theRow[i] > 0) {
                nValues.add(i);
            }
        }
        return nValues;
    }

    /**
     * Finding the W values of neighbors in the former connectivity partition.
     *
     * @param nValues the N values
     * @param kFormer the K values of the former step
     * @return the wValues
     */
    public Set<Integer> wValues(Set<Integer> nValues, int[] kFormer) {
        Set<Integer> wValues = new HashSet<>();
        for (Integer i : nValues) {
            wValues.add(kFormer[i]);
        }
        return wValues;
    }

    /**
     * Finding the connectivity partition, so the smallest index in the neighborhood.
     *
     * @param total the total
     * @param wValues the wValues
     * @param kFormer the K values of the former step
     * @return int[]
     */
    public int[] kValues(int total, Set<Integer> wValues, int[] kFormer) {
        int[] kValues = new int[total];
        int min = Collections.min(wValues);
        for (int i = 0; i < total; i++) {
            if (wValues.contains(kFormer[i])) {
                kValues[i] = min;
            } else {
                kValues[i] = kFormer[i];
            }
        }
        return kValues;
    }

    /**
     * Initializing the first connectivity partition.
     *
     * @param total int number of atoms.
     * @return int[]
     */
    public int[] initialKList(int total) {
        int[] k = new int[total];
        for (int i = 0; i < total; i++) {
            k[i] = i;
        }
        return k;
    }

    /* 3.6.2. Connectivity Test */

    /**
     * Test whether an adjacency matrix is connected or disconnected.
     *
     * @param mat int[][] adjacency matrix
     * @param connectivityIndices the connectivityIndices
     * @param learningFromConnectivity the learningFromConnectivity
     * @return boolean
     */
    public boolean connectivityTest(
            int[][] mat, int[] connectivityIndices, boolean[] learningFromConnectivity) {
        learningFromConnectivity[0] = false;
        boolean check = false;
        int[] kValues = initialKList(hIndex);
        Set<Integer> nValues;
        Set<Integer> wValues;
        Set<Integer> zValues = new HashSet<>();
        int zValue = 0;
        for (int i = 0; i < hIndex; i++) {
            nValues = nValues(i, hIndex, mat);
            wValues = wValues(nValues, kValues);
            zValue = Collections.min(wValues);
            zValues.add(zValue);
            kValues = kValues(hIndex, wValues, kValues);
        }
        if (zValue == 0 && allIs0(kValues)) {
            check = true;
        } else {
            setLearningFromConnectivity(
                    zValues, kValues, connectivityIndices, learningFromConnectivity);
        }
        return check;
    }

    /**
     * If matrix is not connected, setting learninfFromConnectivity global variables.
     *
     * @param zValues the minimum index values of each atom's neighborhoods.
     * @param kValues the connectivity partition
     * @param connectivityIndices the connectivityIndices
     * @param learningFromConnectivity the learningFromConnectivity
     */
    public void setLearningFromConnectivity(
            Set<Integer> zValues,
            int[] kValues,
            int[] connectivityIndices,
            boolean[] learningFromConnectivity) {
        learningFromConnectivity[0] = true;
        connectivityIndices[0] = minComponentIndex(zValues, kValues);
        connectivityIndices[1] = hIndex - 1;
    }

    /**
     * Getting the minimum component index. Here, components are compared based on their last
     * indices and sizes.
     *
     * @param zValues the minimum index values of each atom's neighborhoods.
     * @param kValues the connectivity partition
     * @return int
     */
    public int minComponentIndex(Set<Integer> zValues, int[] kValues) {
        int index = findMaximalIndexInComponent(kValues, 0);
        int value;
        for (Integer i : zValues) {
            value = findMaximalIndexInComponent(kValues, i);
            if (value < index) {
                index = value;
            }
        }
        return index;
    }

    /**
     * Finding the maximal index in a component to compare with other components.
     *
     * @param kValues int[] connectivity partition
     * @param value int minimum neighborhood index
     * @return int
     */
    public int findMaximalIndexInComponent(int[] kValues, int value) {
        int maxIndex = hIndex;
        for (int i = hIndex - 1; i > 0; i--) {
            if (kValues[i] == value) {
                maxIndex = i;
                break;
            }
        }
        return maxIndex;
    }

    /**
     * Checks whether all the entries are equal to 0 or not.
     *
     * @param list int[]
     * @return boolean
     */
    public boolean allIs0(int[] list) {
        boolean check = true;
        for (int j : list) {
            if (j != 0) {
                check = false;
                break;
            }
        }
        return check;
    }

    /**
     * Based on the molecules automorphisms, testing an adjacency matrix is canonical or not.
     *
     * @param initialPartition the initial partition
     * @param r the r
     * @return int
     */
    public int indexYZ(int[] initialPartition, int[] r) {
        int index = 0;
        for (int i = 0; i <= r[0]; i++) {
            index = index + initialPartition[i];
        }
        return index - 1;
    }

    public boolean canonicalTest(
            int[][] a,
            int[] initialPartition,
            int[][] partitionList,
            int[] nonCanonicalIndices,
            List<ArrayList<Permutation>> formerPermutations,
            int[] partSize,
            int[] r,
            int[] y,
            int[] z,
            int[][] ys,
            int[][] zs,
            boolean[] learningFromCanonicalTest) {
        boolean check = true;
        learningFromCanonicalTest[0] = false;
        int value = indexYZ(initialPartition, r);
        y[0] = ys[0][value];
        z[0] = zs[0][value];
        if (partSize[0] == r[0] && z[0] != 1) {
            z[0] = z[0] - 1;
        }
        clearFormers(false, y[0], partitionList, formerPermutations);
        boolean test;
        for (int i = y[0]; i <= z[0]; i++) {
            test =
                    rowCanonicalTest(
                            i,
                            r,
                            a,
                            partitionList[i],
                            canonicalPartition(i, partitionList[i]),
                            initialPartition,
                            partitionList,
                            nonCanonicalIndices,
                            formerPermutations,
                            y,
                            ys,
                            learningFromCanonicalTest);
            if (!test) {
                check = false;
                break;
            }
        }
        clearFormers(check, y[0], partitionList, formerPermutations);
        return check;
    }

    /**
     * When an adjacency matrix is non-canonical, cleaning the formerPermutations and partitionList
     * from the first row of the tested block.
     *
     * @param check boolean canonical test result
     * @param y int first row of the tested block
     * @param partitionList the partitionList
     * @param formerPermutations the formerPermutations
     */
    public void clearFormers(
            boolean check,
            int y,
            int[][] partitionList,
            List<ArrayList<Permutation>> formerPermutations) {
        if (!check) {
            int formerSize = formerPermutations.size() - 1;
            if (formerSize >= y) {
                formerPermutations.subList(y, formerSize + 1).clear();
            }

            int partitionSize = partitionList.length - 1;
            for (int i = partitionSize; i > y; i--) {
                partitionList[i] = null;
            }
        }
    }

    /**
     * Calculating all candidate permutations for row canonical test.
     *
     * <p>The DFS multiplication of former automorphisms list with the list of cycle transpositions
     * of the row.
     *
     * @param index int row index
     * @param cycles the cycle transpositions
     * @param formerPermutations the formerPermutations
     */
    public void candidatePermutations(
            int index, List<Permutation> cycles, List<ArrayList<Permutation>> formerPermutations) {
        ArrayList<Permutation> newList = new ArrayList<>(cycles);
        if (index != 0) {
            ArrayList<Permutation> formers = formerPermutations.get(index - 1);
            for (Permutation form : formers) {
                if (!form.isIdentity()) {
                    newList.add(form);
                }
            }
            ArrayList<Permutation> newForm = new ArrayList<>();
            for (Permutation frm : formers) {
                if (!frm.isIdentity()) {
                    newForm.add(frm);
                }
            }
            ArrayList<Permutation> newCycles = new ArrayList<>();
            if (cycles.size() != 1) {
                for (Permutation cyc : cycles) {
                    if (!cyc.isIdentity()) {
                        newCycles.add(cyc);
                    }
                }
            }
            for (Permutation perm : newForm) {
                for (Permutation cycle : newCycles) {
                    Permutation newPermutation = cycle.multiply(perm);
                    if (!newPermutation.isIdentity()) {
                        newList.add(newPermutation);
                    }
                }
            }
        }
        formerPermutations.add(index, newList);
    }

    /**
     * Canonical test for a row in the tested block.
     *
     * @param index int row index
     * @param r int block index
     * @param a int[][] adjacency matrix
     * @param partition int[] former partition
     * @param newPartition int[] canonical partition
     * @param initialPartition the initial partition
     * @param partitionList the partitionList
     * @param nonCanonicalIndices the nonCanonicalIndices
     * @param formerPermutations the formerPermutations
     * @param y the y
     * @param ys the ys
     * @param learningFromCanonicalTest the learningFromCanonicalTest
     * @return boolean
     */
    public boolean rowCanonicalTest(
            int index,
            int[] r,
            int[][] a,
            int[] partition,
            int[] newPartition,
            int[] initialPartition,
            int[][] partitionList,
            int[] nonCanonicalIndices,
            List<ArrayList<Permutation>> formerPermutations,
            int[] y,
            int[][] ys,
            boolean[] learningFromCanonicalTest) {
        boolean check;
        if (!rowDescendingTest(
                index, a, newPartition, nonCanonicalIndices, learningFromCanonicalTest)) {
            check = false;
        } else {
            int value = indexYZ(initialPartition, r);
            y[0] = ys[0][value];
            List<Permutation> cycles = new ArrayList<>();
            if (partition[size - 1] != 0) {
                Permutation id = new Permutation(size);
                cycles.add(id);
            } else {
                cycles = cycleTranspositions(index, partition);
            }
            candidatePermutations(index, cycles, formerPermutations);
            check = check(index, size, a, newPartition, formerPermutations);
            if (!check) {
                if (cycles.size() != 1) {
                    getLernenIndices(
                            index,
                            a,
                            cycles,
                            newPartition,
                            nonCanonicalIndices,
                            learningFromCanonicalTest);
                }
            } else {
                addPartition(index, newPartition, a, partitionList);
            }
        }
        return check;
    }

    /**
     * Updating canonical partition list.
     *
     * @param index row index
     * @param newPartition atom partition
     * @param a int[][] adjacency matrix
     * @param partitionList the partitionList
     */
    public void addPartition(int index, int[] newPartition, int[][] a, int[][] partitionList) {
        if (newPartition[size - 1] != 0) {
            partitionList[index + 1] = newPartition;
        } else {
            partitionList[index + 1] = refinedPartitioning(newPartition, a[index]);
        }
    }

    /**
     * Refining the input partition based on the row entries.
     *
     * @param partition int[] atom partition
     * @param row int[] row
     * @return int[]
     */
    public int[] refinedPartitioning(int[] partition, int[] row) {
        int[] refined = new int[size];
        int index = 0;
        int localCount = 1;
        int refinedIndex = 0;
        int limit = findZeros(partition);
        for (int s = 0; s < limit; s++) {
            if (partition[s] != 1) {
                for (int i = index; i < partition[s] + index - 1; i++) {
                    if (i + 1 < partition[s] + index - 1) {
                        if (row[i] == row[i + 1]) {
                            localCount++;
                        } else {
                            refined[refinedIndex] = localCount;
                            refinedIndex++;
                            localCount = 1;
                        }
                    } else {
                        if (row[i] == row[i + 1]) {
                            localCount++;
                            refined[refinedIndex] = localCount;
                        } else {
                            refined[refinedIndex] = localCount;
                            refinedIndex++;
                            refined[refinedIndex] = 1;
                        }
                        refinedIndex++;
                        localCount = 1;
                    }
                }
                index = index + partition[s];
            } else {
                index++;
                refined[refinedIndex] = 1;
                refinedIndex++;
                localCount = 1;
            }
        }
        return refined;
    }

    /**
     * For a row given by index, detecting the other row to compare in the block. For the detection
     * of the next row index, cycle transposition is used.
     *
     * @param index int row index
     * @param a int[][] adjacency matrix
     * @param cycleTransposition Permutation cycle transposition
     * @return int[]
     */
    public int[] row2compare(int index, int[][] a, Permutation cycleTransposition) {
        int[] array = cloneArray(a[findIndex(index, cycleTransposition)]);
        return actArray(array, cycleTransposition);
    }

    /**
     * With the cycle permutation, mapping the row index to another row in the block.
     *
     * @param index int row index
     * @param cycle Permutation cycle transposition
     * @return int
     */
    public int findIndex(int index, Permutation cycle) {
        int cycleSize = cycle.size();
        int output = 0;
        for (int i = 0; i < cycleSize; i++) {
            if (cycle.get(i) == index) {
                output = i;
                break;
            }
        }
        return output;
    }

    /**
     * Cloning int array
     *
     * @param array int[] array
     * @return int[]
     */
    public int[] cloneArray(int[] array) {
        return array.clone();
    }

    /**
     * Calculating the canonical permutation of a row.
     *
     * <p>In a block, the original and the other rows are compared; if there is a permutation
     * mapping rows to each other, canonical permutation, else id permutation is returned.
     *
     * @param originalRow int[] original row
     * @param rowToCheck int[] row to compare with
     * @param partition int[] partition
     * @return Permutation
     */
    public Permutation getCanonicalPermutation(
            int[] originalRow, int[] rowToCheck, int[] partition) {
        int[] cycles = getCanonicalPermutation2(partition, originalRow, rowToCheck);
        int[] perm = new int[size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == cycles[j]) {
                    perm[i] = j;
                }
            }
        }
        return new Permutation(perm);
    }

    /**
     * Calculating the canonical permutation of a row.
     *
     * <p>In a block, the original and the other rows are compared; if there is a permutation
     * mapping rows to each other, canonical permutation, else id permutation is returned.
     *
     * @param partition int[] partition
     * @param max int[] max
     * @param check int[] check
     * @return int[]
     */
    public int[] getCanonicalPermutation2(int[] partition, int[] max, int[] check) {
        int[] values = idValues(sum(partition));
        int i = 0;

        if (equalSetCheck(max, check, partition)) {
            int limit = findZeros(partition);
            for (int s = 0; s < limit; s++) {
                int[] can = getBlocks(max, i, partition[s] + i);
                int[] non = getBlocks(check, i, partition[s] + i);
                values = getCyclesList(can, non, i, values);
                i = i + partition[s];
            }
        }
        return values;
    }

    /**
     * For maximal and tested rows, getting the cycle lists
     *
     * @param max int[] maximal row
     * @param non int[] non maximal row to test
     * @param index int row index
     * @param values int[] values
     * @return int[]
     */
    public int[] getCyclesList(int[] max, int[] non, int index, int[] values) {
        int i = 0;
        int permutationIndex;
        while (i < max.length && max[i] != 0) {
            if (max[i] != non[i]) {
                permutationIndex = findMatch(max, non, max[i], i);
                if (i != permutationIndex) {
                    non = permuteArray(non, i, permutationIndex);
                }
                int temp = values[i + index];
                values[i + index] = values[permutationIndex + index];
                values[permutationIndex + index] = temp;
            }
            i++;
        }
        return values;
    }

    /**
     * Find the matching entries index in compared two rows.
     *
     * @param max int[] max
     * @param non int[] non
     * @param value int value
     * @param start int start
     * @return int
     */
    public int findMatch(int[] max, int[] non, int value, int start) {
        int length = non.length;
        int index = start;
        for (int i = start; i < length; i++) {
            if (non[i] == value && max[i] != non[i]) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Getting the permutation making two rows identical.
     *
     * @param cycleTransposition Permutation cycle transposition
     * @param index int row index
     * @param a int[][] adjacency matrices
     * @param newPartition int[] refined atom partition
     * @return Permutation
     */
    public Permutation getEqualPerm(
            Permutation cycleTransposition, int index, int[][] a, int[] newPartition) {
        int[] check = row2compare(index, a, cycleTransposition);
        return getCanonicalPermutation(a[index], check, newPartition);
    }

    /**
     * Getting the canonical cycle of a row.
     *
     * @param index int row index
     * @param total int matrix size
     * @param a int[][] adjacency matrices
     * @param newPartition int[] refined atom partition
     * @param cycleTransposition Permutation cycle transposition
     * @return Permutation
     */
    public Permutation getCanonicalCycle(
            int index, int total, int[][] a, int[] newPartition, Permutation cycleTransposition) {
        Permutation canonicalPermutation = idPermutation(total);
        if (!equalRowsCheck(index, a, cycleTransposition, canonicalPermutation)) {
            canonicalPermutation = getEqualPerm(cycleTransposition, index, a, newPartition);
        }
        return canonicalPermutation;
    }

    public boolean check(
            int index,
            int total,
            int[][] a,
            int[] newPartition,
            List<ArrayList<Permutation>> formerPermutations) {
        boolean check = true;
        ArrayList<Permutation> formerList = new ArrayList<>();
        ArrayList<Permutation> form = formerPermutations.get(index);
        for (Permutation permutation : form) {
            boolean biggest = setBiggest(index, a, permutation, newPartition);
            if (biggest) {
                Permutation canonicalPermutation =
                        getCanonicalCycle(index, total, a, newPartition, permutation);
                int[] test = row2compare(index, a, permutation);
                test = actArray(test, canonicalPermutation);
                if (descendingOrderUpperMatrixCheck(index, newPartition, a[index], test)) {
                    if (canonicalPermutation.isIdentity()) {
                        if (equalSetCheck2(newPartition, a[index], test)) {
                            formerList.add(permutation);
                        }
                    } else {
                        Permutation newPermutation = canonicalPermutation.multiply(permutation);
                        formerList.add(newPermutation);
                    }
                } else {
                    formerList.clear();
                    check = false;
                    break;
                }
            } else {
                formerList.clear();
                check = false;
                break;
            }
        }
        if (check) {
            formerPermutations.get(index).clear();
            formerPermutations.set(index, formerList);
        }
        return check;
    }

    /**
     * Getting the list of cycle transpositions for a given atom partition and the row index
     *
     * @param index int row index
     * @param partition int[] atom partition
     * @return the list of permutations
     */
    public List<Permutation> cycleTranspositions(int index, int[] partition) {
        List<Permutation> perms = new ArrayList<>();
        int lValue = lValue(partition, index);
        int[] values;
        int former;
        for (int i = 0; i < lValue; i++) {
            values = idValues(size);
            former = values[index];
            values[index] = values[index + i];
            values[index + i] = former;
            Permutation p = new Permutation(values);
            perms.add(p);
        }
        return perms;
    }

    /**
     * To calculate the number of conjugacy classes, used in cycle transposition calculation.
     *
     * @param partEx int[] former atom partition
     * @param degree the degree
     * @return the LValue
     */
    public int lValue(int[] partEx, int degree) {
        return (sum(partEx, (degree)) - (degree));
    }

    /**
     * To get the canonical partition.
     *
     * @param i int row index
     * @param partition int[] partition
     * @return int[]
     */
    public int[] canonicalPartition(int i, int[] partition) {
        return partitionCriteria(partition, i + 1);
    }

    /**
     * Add number of 1s into an ArrayList
     *
     * @param list the integer array
     * @param number the number
     */
    public void addOnes(int[] list, int number) {
        for (int i = 0; i < number; i++) {
            list[i] = 1;
        }
    }

    public int findZeros(int[] array) {
        int index = size;
        for (int i = 0; i < size; i++) {
            if (array[i] == 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Partitioning criteria
     *
     * @param partEx the former partition
     * @param degree degree of the partitioning.
     * @return the partition criteria
     */
    public int[] partitionCriteria(int[] partEx, int degree) {
        int[] partNew = new int[size];
        int limit = findZeros(partEx);
        if (zero(partEx)) {
            addOnes(partNew, degree);
            int index = degree;
            int oldValue = partEx[degree - 1];
            if (oldValue > 1) {
                partNew[index] = oldValue - 1;
                index++;
                for (int k = degree; k < limit; k++) {
                    partNew[index] = partEx[k];
                    index++;
                }
            } else if (oldValue == 1) {
                for (int k = degree; k < limit; k++) {
                    partNew[index] = partEx[k];
                    index++;
                }
            }
            return partNew;
        } else {
            return partEx;
        }
    }

    /**
     * Ordering degrees, hydrogens and symbols in ascending order
     *
     * @param degree int[] atom valences
     * @param symbol String[] atom symbols
     * @param index0 int first index
     * @param index1 int second index
     * @param hydrogens int[] hydrogens array
     */
    public void orderDegreeSymbols(
            int[] degree, String[] symbol, int index0, int index1, int[] hydrogens) {
        int temp;
        int temp2;
        for (int i = index0; i < index1; i++) {
            for (int j = i + 1; j < index1; j++) {
                if (degree[i] > degree[j]) {
                    swap(symbol, i, j);
                    temp = degree[i];
                    degree[i] = degree[j];
                    degree[j] = temp;
                    temp2 = hydrogens[i];
                    hydrogens[i] = hydrogens[j];
                    hydrogens[j] = temp2;
                }
            }
        }
    }

    public void swap(String[] array, int i, int j) {
        String temp;
        temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public void swap(int[] array, int i, int j) {
        int swapString = array[i];
        array[i] = array[j];
        array[j] = swapString;
    }

    /**
     * Sort arrays with partitions.
     *
     * @param partitionList int[] atom partition
     * @param degrees int[] atom valences
     * @param symbols String[] atom symbols
     * @param hydrogens int[] hydrogens
     * @return int array
     */
    public int[] sortWithPartition(
            int[] partitionList, int[] degrees, String[] symbols, int[] hydrogens) {
        int[] partition = buildArray(partitionList);
        int localSize = partition.length;
        for (int n = 0; n < localSize; n++) {
            for (int m = 0; m < (localSize - 1) - n; m++) {
                if ((partition[m] > partition[m + 1])) {
                    swap(partition, m, (m + 1));
                    swap(degrees, m, (m + 1));
                    swap(hydrogens, m, (m + 1));
                    swap(symbols, m, (m + 1));
                }
            }
        }
        reOrder(partition, degrees, symbols, hydrogens);
        return initialPartition(partition);
    }

    /**
     * Initial atom partition of the input molecular formula
     *
     * @param partition int[] atom partition
     * @return int[]
     */
    public int[] initialPartition(int[] partition) {
        int index = 0;
        int index2 = 0;
        int part;
        int[] init = new int[size];
        while (index != hIndex) {
            part = partition[index];
            init[index2++] = part;
            index += part;
        }
        return init;
    }

    /**
     * Building the copy of the partition array
     *
     * @param partition int[] atom partition
     * @return int[]
     */
    public int[] buildArray(int[] partition) {
        int[] partitionArray = new int[sum(partition)];
        int index = 0;
        for (int p : partition) {
            for (int i = 0; i < p; i++) {
                partitionArray[index] = p;
                index++;
            }
        }
        return partitionArray;
    }

    /**
     * Re-order all the global variables based on the refined new partitioning.
     *
     * @param partition int[] atom partition
     * @param degrees int[] atom valences
     * @param symbols String[] atom symbols
     * @param hydrogens int[] hydrogens
     */
    public void reOrder(int[] partition, int[] degrees, String[] symbols, int[] hydrogens) {
        int index = 0;
        int part;
        while (index != (hIndex)) {
            part = partition[index];
            orderDegreeSymbols(degrees, symbols, index, (index + part), hydrogens);
            index = index + part;
        }
    }

    /* Fuzzy formula functions */

    /**
     * To get the fuzzy formula ranges for each element type in the molecular formula
     *
     * @param localFormula String molecular localFormula
     * @param symbolList the symbol list
     * @return the map of string ant integer array
     */
    public Map<String, Integer[]> getFuzzyFormulaRanges(
            String localFormula, List<String> symbolList) {
        String[] atoms = localFormula.split(LETTERS_FROM_A_TO_Z);
        HashMap<String, Integer[]> symbolsMap = new HashMap<>();
        String[] info;
        String[] info2;
        String[] info3;
        for (String atom : atoms) {
            info = atom.split("\\[");
            String symbol;
            Integer[] n = new Integer[2];
            if (info.length == 1) {
                info2 = info[0].split(NUMBERS_FROM_0_TO_9, 2);
                symbol = info2[0];
                if (info2.length == 1) {
                    n[0] = 1;
                    n[1] = 1;
                } else {
                    n[0] = Integer.valueOf(info2[1]);
                    n[1] = Integer.valueOf(info2[1]);
                }
            } else {
                symbol = info[0];
                info3 = info[1].split("-");
                n[0] = Integer.valueOf(info3[0]);
                n[1] = Integer.valueOf(info3[1].split("]")[0]);
            }
            symbolList.add(symbol);
            symbolsMap.put(symbol, n);
        }
        return symbolsMap;
    }

    public Map<String, Integer[]> getFuzzyFormulaRangesWithNewElements(
            String localFormula, List<String> symbolList) {
        String[] atoms = localFormula.split(LETTERS_FROM_A_TO_Z);
        Map<String, Integer[]> symbolsMap = new HashMap<>();
        String[] info;
        String[] info4;
        String symbol;
        for (String atom : atoms) {
            info = atom.split("\\(val="); // to get the higher valence value
            Integer[] n = new Integer[2];
            if (info.length != 1) {
                symbol = getSymbol(info, n);
            } else {
                info4 = info[0].split(NUMBERS_FROM_0_TO_9);
                symbol = info4[0];
                if (info4.length == 1) {
                    n[0] = 1;
                    n[1] = 1;
                } else {
                    n[0] = Integer.valueOf(info4[1]);
                    n[1] = Integer.valueOf(info4[1]);
                }
            }
            symbolList.add(symbol);
            symbolsMap.put(symbol, n);
        }
        return symbolsMap;
    }

    public String getSymbol(String[] info, Integer[] n) {
        String symbol;
        String[] info2;
        String[] info3;
        symbol = info[0];
        info2 = info[1].split("\\)");
        symbol += "(" + info2[0] + ")";
        if (info2.length == 1) {
            n[0] = 1;
            n[1] = 1;
        } else {
            info3 = info2[1].split("-");
            if (info3.length == 1) {
                n[0] = Integer.valueOf(info3[0]);
                n[1] = Integer.valueOf(info3[0]);
            } else {
                n[0] = Integer.valueOf(info3[0].split("\\[")[1]);
                n[1] = Integer.valueOf(info3[1].split("]")[0]);
            }
        }
        return symbol;
    }

    /**
     * Formulae generator for each element ranges
     *
     * @param result the list of string
     * @param symbolList list of string
     * @param symbols the symbols map
     * @param localFormula String localFormula
     * @param index int index
     */
    public void generateFormulae(
            List<String> result,
            List<String> symbolList,
            Map<String, Integer[]> symbols,
            String localFormula,
            int index) {
        if (index == symbols.size()) {
            result.add(localFormula);
        } else {
            String symbol = symbolList.get(index);
            Integer[] range = symbols.get(symbol);
            for (int i = range[0]; i <= range[1]; i++) {
                generateFormulae(
                        result,
                        symbolList,
                        symbols,
                        extendFormula(localFormula, i, symbol),
                        index + 1);
            }
        }
    }

    /**
     * Adding new entry to the new molecular formula
     *
     * @param localFormula String localFormula
     * @param number int number
     * @param symbol String symbol
     * @return String
     */
    public String extendFormula(String localFormula, int number, String symbol) {
        String newFormula = localFormula;
        if (number == 1) {
            newFormula += symbol;
        } else if (number > 1) {
            newFormula += symbol + number;
        }
        return newFormula;
    }

    /**
     * Generating list of formulae for the input fuzzy formula
     *
     * @param normalizedLocalFuzzyFormula String normalizedLocalFuzzyFormula
     * @return the list of string
     */
    public List<String> getFormulaList(String normalizedLocalFuzzyFormula) {
        List<String> result = new ArrayList<>();
        String[] unsupportedSymbols = null;
        if (!setElement) {
            unsupportedSymbols = validateFuzzyFormula(normalizedLocalFuzzyFormula);
        }
        if (unsupportedSymbols != null && unsupportedSymbols.length > 0) {
            if (verbose)
                logger.info(
                        "The input fuzzyFormula consists user defined element types: "
                                + String.join(", ", unsupportedSymbols));
        } else {
            List<String> symbolList = new ArrayList<>();
            Map<String, Integer[]> localSymbols;
            if (setElement) {
                localSymbols =
                        getFuzzyFormulaRangesWithNewElements(
                                normalizedLocalFuzzyFormula, symbolList);
            } else {
                localSymbols = getFuzzyFormulaRanges(normalizedLocalFuzzyFormula, symbolList);
            }
            String newFormula = "";
            generateFormulae(result, symbolList, localSymbols, newFormula, 0);
        }
        return result;
    }

    /**
     * Emits a molecule to whomever is listening.
     * @param mol molecule to emit
     * @throws CDKException a CDK exception occurred
     * @throws IOException an IO exception occurred
     */
    public void emit(IAtomContainer mol) throws CDKException, IOException {
        consumer.consume(mol);
    }

    /** Setting the initial atom container of a molecular formula with a single heavy atom */
    public void initSingleAC() {
        atomContainer = builder.newAtomContainer();
        for (String s : symbolArray) {
            IAtom atom = builder.newAtom();
            atom.setSymbol(s);
            atomContainer.addAtom(atom);
        }
        for (IAtom atom : atomContainer.atoms()) {
            atom.setImplicitHydrogenCount(0);
        }
    }

    /**
     * Setting the initial atom container of a molecular formula
     *
     * @param formula the formula
     */
    public void intAC(String formula) {
        String[] atoms = formula.split(LETTERS_FROM_A_TO_Z);
        ArrayList<String> symbolList = new ArrayList<>();
        String[] info;
        int occur;
        String symbol;
        for (String atom : atoms) {
            info = atom.split(NUMBERS_FROM_0_TO_9, 2);
            symbol = info[0].split("\\(")[0];
            occur = atomOccurrence(info);
            for (int i = 0; i < occur; i++) {
                symbolList.add(symbol);
            }
        }
        atomContainer = builder.newAtomContainer();
        for (String s : symbolList) {
            IAtom atom = builder.newAtom();
            atom.setSymbol(s);
            atomContainer.addAtom(atom);
        }
        for (IAtom atom : atomContainer.atoms()) {
            atom.setImplicitHydrogenCount(0);
        }
    }

    /**
     * Building an atom container from a string of atom-implicit hydrogen information.
     *
     * @param ac IAtomContainer IAtomContainer
     * @param symbolArrayCopy String[] symbol array of atoms
     * @return IAtomContainer
     */
    public IAtomContainer initAC(IAtomContainer ac, String[] symbolArrayCopy) {
        for (String s : symbolArrayCopy) {
            IAtom atom = builder.newAtom();
            atom.setSymbol(s.split(NUMBERS_FROM_0_TO_9)[0]);
            ac.addAtom(atom);
        }
        for (IAtom atom : ac.atoms()) {
            atom.setImplicitHydrogenCount(0);
        }
        return ac;
    }

    /**
     * Building an atom container from a string of atom-implicit hydrogen information.
     *
     * @param symbol String symbol
     */
    public void initAC(String symbol) {
        atomContainer = builder.newAtomContainer();
        for (int i = 0; i < matrixSize; i++) {
            IAtom atom = builder.newAtom();
            atom.setSymbol(symbol);
            atomContainer.addAtom(atom);
        }

        for (IAtom atom : atomContainer.atoms()) {
            atom.setImplicitHydrogenCount(0);
        }
    }

    /**
     * Building an atom container for an adjacency matrix.
     *
     * @param mat int[][] adjacency matrix
     * @param atomContainer IAtomContainer atomContainer
     * @return IAtomContainer
     */
    public IAtomContainer buildAtomContainerFromMatrix(int[][] mat, IAtomContainer atomContainer) {
        for (int i = 0; i < mat.length; i++) {
            for (int j = i + 1; j < mat.length; j++) {
                if (mat[i][j] == 1) {
                    atomContainer.addBond(i, j, IBond.Order.SINGLE);
                } else if (mat[i][j] == 2) {
                    atomContainer.addBond(i, j, IBond.Order.DOUBLE);
                } else if (mat[i][j] == 3) {
                    atomContainer.addBond(i, j, IBond.Order.TRIPLE);
                }
            }
        }
        return AtomContainerManipulator.suppressHydrogens(atomContainer);
    }

    /**
     * Building an atom container for an adjacency matrix.
     *
     * @param ac IAtomContainer IAtomContainer
     * @param mat int[][] adjacency matrix
     * @return IAtomContainer
     * @throws CloneNotSupportedException in case of CloneNotSupportedException
     */
    public IAtomContainer buildContainer4SDF(IAtomContainer ac, int[][] mat)
            throws CloneNotSupportedException {
        IAtomContainer ac2 = ac.clone();
        for (int i = 0; i < mat.length; i++) {
            for (int j = i + 1; j < mat.length; j++) {
                if (mat[i][j] == 1) {
                    ac2.addBond(i, j, Order.SINGLE);
                } else if (mat[i][j] == 2) {
                    ac2.addBond(i, j, Order.DOUBLE);
                } else if (mat[i][j] == 3) {
                    ac2.addBond(i, j, Order.TRIPLE);
                }
            }
        }
        return AtomContainerManipulator.suppressHydrogens(ac2);
    }

    /**
     * Building an atom container for an adjacency matrix.
     *
     * @param mat int[][] adjacency matrix
     * @return IAtomContainer
     * @throws CloneNotSupportedException in case of CloneNotSupportedException
     */
    public IAtomContainer buildContainer4SDF(int[][] mat) throws CloneNotSupportedException {
        IAtomContainer ac2 = atomContainer.clone();
        for (int i = 0; i < mat.length; i++) {
            for (int j = i + 1; j < mat.length; j++) {
                if (mat[i][j] == 1) {
                    ac2.addBond(i, j, Order.SINGLE);
                } else if (mat[i][j] == 2) {
                    ac2.addBond(i, j, Order.DOUBLE);
                } else if (mat[i][j] == 3) {
                    ac2.addBond(i, j, Order.TRIPLE);
                }
            }
        }
        return AtomContainerManipulator.suppressHydrogens(ac2);
    }

    /**
     * Building an atom container for SDF output from its symbols
     *
     * @param symbols String[] atom symbols
     * @return IAtomContainer
     * @throws CloneNotSupportedException in case of CloneNotSupportedException
     */
    public IAtomContainer buildContainer4SDF(String[] symbols) throws CloneNotSupportedException {
        IAtomContainer ac = this.builder.newAtomContainer();
        String symbol;
        for (String s : symbols) {
            symbol = s.split(NUMBERS_FROM_0_TO_9)[0];
            IAtom atom = builder.newAtom();
            atom.setSymbol(symbol);
            ac.addAtom(atom);
        }
        for (IAtom atom : ac.atoms()) {
            atom.setImplicitHydrogenCount(0);
        }
        return buildContainer4SDF(ac, generateOnSmMat());
    }

    /**
     * Building an OnSm molecule for a given total number of atoms.
     *
     * @return int[][]
     */
    public int[][] generateOnSmMat() {
        int[][] ring = new int[matrixSize][matrixSize];
        ring[0][1] = 1;
        ring[0][matrixSize - 1] = 1;
        for (int i = 1; i < matrixSize - 1; i++) {
            ring[i][i + 1] = 1;
        }
        return ring;
    }

    /**
     * Building a degree 2 graph for a single element type.
     *
     * @throws IOException in case of IOException
     * @throws CDKException in case of CDKException
     * @throws CloneNotSupportedException in case of CloneNotSupportedException
     */
    public void degree2graph() throws IOException, CDKException, CloneNotSupportedException {
        int[][] mat = new int[matrixSize][matrixSize];
        mat[0][1] = 1;
        mat[0][2] = 1;
        for (int i = 1; i < matrixSize - 2; i++) {
            mat[i][i + 2] = 1;
        }
        mat[matrixSize - 2][matrixSize - 1] = 1;
        count.incrementAndGet();
        String symbol;
        if (oxygen == 0) {
            symbol = "S";
        } else {
            symbol = "O";
        }
        initAC(symbol);
        emit(buildContainer4SDF(mat));
    }

    /**
     * Reading the molecular formula and setting the higher valences.
     *
     * @param localFormula String formula
     */
    public void getHigherValences(String localFormula) {
        String[] atoms = localFormula.split(LETTERS_FROM_A_TO_Z);
        String[] info;
        String[] info2;
        String valence;
        String symbol;
        for (String atom : atoms) {
            // to get the higher valence value
            info = atom.split("\\(");
            if (info.length != 1) {
                symbol = info[0];
                // to get the valence and frequency from x)y
                info2 = info[1].split("\\)");
                valence = info2[0];
                valences.put(symbol + valence, Integer.valueOf(valence));
            }
        }
    }

    /*
     The following functions are for the distribution of given number of sulfurs and oxygens in
     regular graphs as node labelling. These functions are the Java implementation of Sawada's
     method[1][2] in mathematical chemistry.

     <p>References:

     <p>[1] Sawada J. Generating bracelets in constant amortized time. SIAM Journal on Computing.
     2001;31(1):259-68. [2] http://www.cis.uoguelph.ca/~sawada/prog/necklaces.c
    */
    /**
     * The function to compare a node labelling array when the number of left consecutive entries is
     * equal to the right consecutive entries.
     *
     * <p>From this comparison, function returns :
     *
     * <p>1: if node labelling is same as its reversal.
     *
     * <p>0: if node labelling is smaller than its reversal.
     *
     * <p>-1: if node labelling is bigger than its reversal.
     *
     * <p>Reversal check helps to avoid duplicates, easy way of rotational symmetry check.
     *
     * @param length int node labelling length
     * @param index int starting index in the array
     * @return the result
     */
    public int reverseComparison(int length, int index) {
        for (int i = index + 1; i <= (length + 1) / 2; i++) {
            if (nodeLabels[i] < nodeLabels[length - i + 1]) {
                return 0;
            } else if (nodeLabels[i] > nodeLabels[length - i + 1]) {
                return -1;
            }
        }
        return 1;
    }

    public String[] buildSymbolArray() {
        String[] arr = new String[graphSize];
        for (int i = 1; i < graphSize + 1; i++) {
            if (nodeLabels[i] == 0) {
                arr[i - 1] = "O";
            } else {
                arr[i - 1] = "S";
            }
        }
        return arr;
    }

    /**
     * Main function for the distribution of atom symbols: O and S for OnSm form formulae.
     *
     * @param oxy int number of oxygens to distribute
     * @param sul int number of sulfur to distribute
     * @param nextSize int length of the next labelling
     * @param currentSize int length of the current labelling
     * @param reversedLength int longest node labelling, equal to its reversal
     * @param leftEquivalents int the number of consequtively equivalent values at the left side of
     *     the array
     * @param rightEquivalents int the number of consequtively equivalent values at the left side of
     *     the array
     * @param reversalIsSmaller boolean from the reversal comparison, using the boolean variable to
     *     know reveral is smaller than the bode labelling or not.
     * @throws CloneNotSupportedException in case of CloneNotSupportedException
     * @throws CDKException in case of CDKException
     * @throws IOException in case of IOException
     */
    public void distributeSymbols(
            int oxy,
            int sul,
            int nextSize,
            int currentSize,
            int reversedLength,
            int leftEquivalents,
            int rightEquivalents,
            boolean reversalIsSmaller)
            throws CDKException, CloneNotSupportedException, IOException {

        if (2 * (nextSize - 1) > (graphSize + reversedLength)) {
            reversalIsSmaller = isReversalIsSmaller(nextSize, reversedLength, reversalIsSmaller);
        }
        if (nextSize > graphSize) {
            distributeSymbolsNextSizeAboveGraphSize(currentSize, reversalIsSmaller);
        } else {
            int oxy2 = oxy;
            int sul2 = sul;

            nodeLabels[nextSize] = nodeLabels[nextSize - currentSize];

            if (nodeLabels[nextSize] == 0) {
                oxy2--;
            } else {
                sul2--;
            }
            if (nodeLabels[nextSize] == nodeLabels[1]) {
                rightEquivalents++;
            } else {
                rightEquivalents = 0;
            }
            if ((leftEquivalents == (nextSize - 1))
                    && (nodeLabels[nextSize - 1] == nodeLabels[1])) {
                // left consecutive element number incremention.
                leftEquivalents++;
            }

            if (oxy2 >= 0
                    && sul2 >= 0
                    && !(nextSize == graphSize
                            && leftEquivalents != graphSize
                            && nodeLabels[graphSize] == nodeLabels[1])) {
                doDistributeSymbols(
                        nextSize,
                        currentSize,
                        reversedLength,
                        leftEquivalents,
                        rightEquivalents,
                        reversalIsSmaller,
                        oxy2,
                        sul2);
            }

            if (leftEquivalents == nextSize) {
                leftEquivalents--;
            }

            runDistributeSymbolsCheckNodeLabels(
                    oxy,
                    sul,
                    nextSize,
                    currentSize,
                    reversedLength,
                    leftEquivalents,
                    reversalIsSmaller);
        }
    }

    private void doDistributeSymbols(
            int nextSize,
            int currentSize,
            int reversedLength,
            int leftEquivalents,
            int rightEquivalents,
            boolean reversalIsSmaller,
            int oxy2,
            int sul2)
            throws CDKException, CloneNotSupportedException, IOException {
        if (leftEquivalents == rightEquivalents) {
            int reverse = reverseComparison(nextSize, leftEquivalents);
            runDistributeSymbols(
                    nextSize,
                    currentSize,
                    reversedLength,
                    leftEquivalents,
                    rightEquivalents,
                    reversalIsSmaller,
                    oxy2,
                    sul2,
                    reverse);
        } else {
            distributeSymbols(
                    oxy2,
                    sul2,
                    nextSize + 1,
                    currentSize,
                    reversedLength,
                    leftEquivalents,
                    rightEquivalents,
                    reversalIsSmaller);
        }
    }

    public void runDistributeSymbolsCheckNodeLabels(
            int oxy,
            int sul,
            int nextSize,
            int currentSize,
            int reversedLength,
            int leftEquivalents,
            boolean reversalIsSmaller)
            throws CDKException, CloneNotSupportedException, IOException {
        if (nodeLabels[nextSize - currentSize] == 0 && sul > 0) {
            nodeLabels[nextSize] = 1;

            if (nextSize == 1) {
                distributeSymbols(oxy, sul - 1, nextSize + 1, nextSize, 1, 1, 1, reversalIsSmaller);
            } else {
                distributeSymbols(
                        oxy,
                        sul - 1,
                        nextSize + 1,
                        nextSize,
                        reversedLength,
                        leftEquivalents,
                        0,
                        reversalIsSmaller);
            }
        }
    }

    public void runDistributeSymbols(
            int nextSize,
            int currentSize,
            int reversedLength,
            int leftEquivalents,
            int rightEquivalents,
            boolean reversalIsSmaller,
            int oxy2,
            int sul2,
            int reverse)
            throws CDKException, CloneNotSupportedException, IOException {
        if (reverse == 0) {
            distributeSymbols(
                    oxy2,
                    sul2,
                    nextSize + 1,
                    currentSize,
                    reversedLength,
                    leftEquivalents,
                    rightEquivalents,
                    reversalIsSmaller);

        } else if (reverse == 1) {
            distributeSymbols(
                    oxy2,
                    sul2,
                    nextSize + 1,
                    currentSize,
                    nextSize,
                    leftEquivalents,
                    rightEquivalents,
                    false);
        }
    }

    public void distributeSymbolsNextSizeAboveGraphSize(int currentSize, boolean reversalIsSmaller)
            throws CloneNotSupportedException, CDKException, IOException {
        if (!reversalIsSmaller && (graphSize % currentSize) == 0) {
            count.incrementAndGet();
            emit(buildContainer4SDF(buildSymbolArray()));
        }
    }

    public boolean isReversalIsSmaller(
            int nextSize, int reversedLength, boolean reversalIsSmaller) {
        if (nodeLabels[nextSize - 1] > nodeLabels[graphSize - nextSize + 2 + reversedLength])
            reversalIsSmaller = false;
        else if (nodeLabels[nextSize - 1] < nodeLabels[graphSize - nextSize + 2 + reversedLength])
            reversalIsSmaller = true;
        return reversalIsSmaller;
    }

    public void distributeSulfurOxygen(String localFormula)
            throws CDKException, CloneNotSupportedException, IOException {
        graphSize = oxygen + sulfur;
        nodeLabels = new int[graphSize + 1];
        nodeLabels[0] = 0;
        intAC(localFormula);
        distributeSymbols(oxygen, sulfur, 1, 1, 0, 0, 0, false);
    }
}
