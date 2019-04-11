/*
 * Note: I adapted this fingerprint from Yap Chun Wei's PaDEL source code, which can be found here:
 * http://www.yapcwsoft.com/dd/padeldescriptor/
 * 
 * Author: Lyle D. Burgoon, Ph.D. (lyle.d.burgoon@usace.army.mil)
 * 
 * This is the work of a US Government employee. This code is in the public domain.
 * 
 */


package org.openscience.cdk.fingerprint;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.AllPairsShortestPaths;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.openscience.cdk.graph.matrix.TopologicalMatrix;

/**
 * Generates an atom pair 2D fingerprint as implemented in PaDEL given an  {@link IAtomContainer}, that
 * extends the {@link Fingerprinter}.
 *
 * @author Lyle Burgoon (lyle.d.burgoon@usace.army.mil)
 * @cdk.created 2018-02-05
 * @cdk.keyword fingerprint
 * @cdk.keyword similarity
 * @cdk.module fingerprint
 * @cdk.githash
 * @see org.openscience.cdk.fingerprint.Fingerprinter
 */
public class AtomPairs2DFingerprinter extends AbstractFingerprinter implements IFingerprinter {

    private static final int      MAX_DISTANCE = 10;
    private static final String[] atypes       = {"C", "N", "O", "S", "P", "F", "Cl", "Br", "I", "B", "Si", "X"};

    private final Map<String, Integer> pathToBit = new HashMap<>();
    private final Map<Integer, String> bitToPath = new HashMap<>();

    public AtomPairs2DFingerprinter() {
        for (int dist = 1; dist <= MAX_DISTANCE; dist++) {
            for (int i = 0; i < atypes.length; i++) {
                for (int j = i; j < atypes.length; j++) {
                    final String key_name = dist + "_" + atypes[i] + "_" + atypes[j];
                    pathToBit.put(key_name, pathToBit.size());
                    bitToPath.put(bitToPath.size(), key_name);
                }
            }
        }
    }

    @Override
    public int getSize() {
        return pathToBit.size();
    }

    /**
     * Checks if an atom is a halogen
     * @param atom
     * @return
     */
    private static boolean isHalogen(final IAtom atom) {
        switch (atom.getAtomicNumber()) {
            case 9:  // F
            case 17: // Cl
            case 35: // Br
            case 53: // I
                return true;
            default:
                return false;
        }
    }

    /**
     * Atoms that we are using in the fingerprint
     * @param atom
     * @return
     */
    private static boolean include(final IAtom atom) {
        switch (atom.getAtomicNumber()) {
            case 5:  // B
            case 6:  // C
            case 7:  // N
            case 8:  // O
            case 14: // Si
            case 15: // P
            case 16: // S
            case 9:  // F
            case 17: // Cl
            case 35: // Br
            case 53: // I
                return true;
            default:
                return false;
        }
    }

    /**
     * Creates the fingerprint name which is used as a key in our hashes
     * @param dist
     * @param a
     * @param b
     * @return
     */
    private static String encodePath(int dist, IAtom a, IAtom b) {
        return dist + "_" + a.getSymbol() + "_" + b.getSymbol();
    }

    /**
     * Encodes name for halogen paths
     * @param dist
     * @param a
     * @param b
     * @return
     */
    private static String encodeHalPath(int dist, IAtom a, IAtom b) {
        return dist + "_" + (isHalogen(a) ? "X" : a.getSymbol()) + "_" +
               (isHalogen(b) ? "X" : b.getSymbol());
    }

    /**
     * This performs the calculations used to generate the fingerprint
     * @param paths
     * @param mol
     */
    private void calculate(List<String> paths, IAtomContainer mol) {
        AllPairsShortestPaths apsp     = new AllPairsShortestPaths(mol);
        int                   numAtoms = mol.getAtomCount();
        for (int i = 0; i < numAtoms; i++) {
            if (!include(mol.getAtom(i)))
                continue;
            for (int j = i + 1; j < numAtoms; j++) {
                if (!include(mol.getAtom(j))) continue;
                final int dist = apsp.from(i).distanceTo(j);
                if (dist > MAX_DISTANCE)
                    continue;
                final IAtom beg = mol.getAtom(i);
                final IAtom end = mol.getAtom(j);
                paths.add(encodePath(dist, beg, end));
                paths.add(encodePath(dist, end, beg));
                if (isHalogen(mol.getAtom(i)) || isHalogen(mol.getAtom(j))) {
                    paths.add(encodeHalPath(dist, beg, end));
                    paths.add(encodeHalPath(dist, end, beg));
                }
            }
        }
    }

    @Override
    public IBitFingerprint getBitFingerprint(IAtomContainer container) throws CDKException {
        BitSet fp = new BitSet(pathToBit.size());
        List<String> paths = new ArrayList<>();
        calculate(paths, container);
        for (String path : paths) {
        	if (!pathToBit.containsKey(path))
        		continue;
            fp.set(pathToBit.get(path));
        }
        return new BitSetFingerprint(fp);
    }

    @Override
    public Map<String, Integer> getRawFingerprint(IAtomContainer mol) throws
                                                                      CDKException {
        Map<String,Integer> raw = new HashMap<>();
        List<String> paths = new ArrayList<>();
        calculate(paths, mol);

        Collections.sort(paths);
        int count = 0;
        String prev = null;
        for (String path : paths) {
            if (prev == null || !path.equals(prev)) {
                if (count > 0)
                    raw.put(prev, count);
                count = 1;
                prev = path;
            } else {
                ++count;
            }
        }
        if (count > 0)
            raw.put(prev, count);

        return raw;
    }

    @Override
    public ICountFingerprint getCountFingerprint(IAtomContainer mol) throws CDKException {
        final Map<String,Integer> raw  = getRawFingerprint(mol);
        final List<String>        keys = new ArrayList<>(raw.keySet());
        return new ICountFingerprint() {
            @Override
            public long size() {
                return pathToBit.size();
            }

            @Override
            public int numOfPopulatedbins() {
                return keys.size();
            }

            @Override
            public int getCount(int index) {
                return raw.get(keys.get(index));
            }

            @Override
            public int getHash(int index) {
                return pathToBit.get(keys.get(index));
            }

            @Override
            public void merge(ICountFingerprint fp) {

            }

            @Override
            public void setBehaveAsBitFingerprint(
                boolean behaveAsBitFingerprint) {

            }

            @Override
            public boolean hasHash(int hash) {
                return bitToPath.containsKey(hash);
            }

            @Override
            public int getCountForHash(int hash) {
                return raw.get(bitToPath.get(hash));
            }
        };
    }
}
