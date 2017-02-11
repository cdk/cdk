/* $Revision$ $Author$ $Date$
 *
 * Copyright (c) 2015 Collaborative Drug Discovery, Inc. <alex@collaborativedrug.com>
 *
 * Implemented by Alex M. Clark, produced by Collaborative Drug Discovery, Inc.
 * Made available to the CDK community under the terms of the GNU LGPL.
 *
 *    http://collaborativedrug.com
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

package org.openscience.cdk.fingerprint.model;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.CircularFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  <ul>
 *	<li>Bayesian models using fingerprints: provides model creation, analysis, prediction and
 *  serialisation.</li>
 *  
 *  <li>Uses a variation of the classic Bayesian model, using a Laplacian correction, which sums log
 *  values of ratios rather than multiplying them together. This is an effective way to work with large
 *  numbers of fingerprints without running into extreme numerical precision issues, but it also means
 *  that the outgoing predictor is an arbitrary value rather than a probability, which introduces
 *  the need for an additional calibration step prior to interpretation.</li>
 *  
 *  <li>For more information about the method, see:
 *		J. Chem. Inf. Model, v.46, pp.1124-1133 (2006)
 *		J. Biomol. Screen., v.10, pp.682-686 (2005)
 *		Molec. Divers., v.10, pp.283-299 (2006)</li>
 *
 *  <li>Currently only the CircularFingerprinter fingerprints are supported (i.e. ECFP_n and FCFP_n).</li>
 *
 *  <li>Model building is done by selecting the fingerprinting method and folding size, then providing
 *  a series of molecules &amp; responses. Individual model contributions are kept around in order to
 *  produce the analysis data (e.g. the ROC curve), but is discarded during serialise/deserialise
 *  cycles.
 *  
 *  <li>Fingerprint "folding" is optional, but recommended, because it places an upper limit on the model
 *  size. If folding is not used (folding=0) then the entire 32-bits are used, which means that in the
 *  diabolical case, the number of Bayesian contributions that needs to be stored is 4 billion. In practice
 *  the improvement in predictivity tends to plateaux out at around 1024 bits, so values of 2048 or 4096
 *  are generally safe. Folding values must be integer powers of 2.</li>
 *
 *  </ul>
 *  
 * @author         am.clark
 * @cdk.created    2015-01-05
 * @cdk.keyword    fingerprint
 * @cdk.keyword    bayesian
 * @cdk.keyword    model
 * @cdk.module     standard
 * @cdk.githash
 */

public class Bayesian {

    private int                    classType;
    private int                    folding      = 0;

    // incoming hash codes: actual values, and subsumed values are {#active,#total}
    private int                    numActive    = 0;
    protected Map<Integer, int[]>  inHash       = new HashMap<Integer, int[]>();
    protected ArrayList<int[]>     training     = new ArrayList<int[]>();
    protected ArrayList<Boolean>   activity     = new ArrayList<Boolean>();

    // built model: contributions for each hash code
    protected Map<Integer, Double> contribs     = new HashMap<Integer, Double>();
    protected double               lowThresh    = 0, highThresh = 0;
    protected double               range        = 0, invRange = 0;                            // cached to speed up scaling calibration

    // self-validation metrics: can optionally be calculated after a build
    protected double[]             estimates    = null;
    protected float[]              rocX         = null, rocY = null;                          // between 0..1, and rounded to modest precision
    protected String               rocType      = null;
    protected double               rocAUC       = Double.NaN;
    protected int                  trainingSize = 0, trainingActives = 0;                     // this is serialised, while the actual training set is not

    // optional text attributes (serialisable)
    private String                 noteTitle    = null, noteOrigin = null;
    private String[]               noteComments = null;

    private static final Pattern   PTN_HASHLINE = Pattern.compile("^(-?\\d+)=([\\d\\.Ee-]+)");

    // ----------------- public methods -----------------

    /**
     * Instantiate a Bayesian model with no data.
     * 
     * @param classType one of the CircularFingerprinter.CLASS_* constants
     */
    public Bayesian(int classType) {
        this.classType = classType;
    }

    /**
     * Instantiate a Bayesian model with no data.
     * 	 * @param classType one of the CircularFingerprinter.CLASS_* constants
     * @param folding the maximum number of fingerprint bits, which must be a power of 2 (e.g. 1024, 2048) or 0 for no folding
     */
    public Bayesian(int classType, int folding) {
        this.classType = classType;
        this.folding = folding;

        // make sure the folding is valid
        boolean bad = false;
        if (folding > 0) for (int f = folding; f > 0; f = f >> 1)
            if ((f & 1) == 1 && f != 1) {
                bad = true;
                break;
            }
        if (folding < 0 || bad)
            throw new ArithmeticException("Fingerprint folding " + folding + " invalid: must be 0 or power of 2.");
    }

    /**
     * Access to the fingerprint type.
     * 
     * @return fingerprint class, one of CircularFingerprinter.CLASS_*
     */
    public int getClassType() {
        return classType;
    }

    /**
     * Access to the fingerprint folding extent.
     * 
     * @return folding extent, either 0 (for none) or a power of 2
     */
    public int getFolding() {
        return folding;
    }

    /**
     * Appends a new row to the model source data, which consists of a molecule and whether or not it
     * is considered active.
     * 
     * @param mol molecular structure, which must be non-blank
     * @param active whether active or not
     */
    public void addMolecule(IAtomContainer mol, boolean active) throws CDKException {
        if (mol == null || mol.getAtomCount() == 0) throw new CDKException("Molecule cannot be blank or null.");

        CircularFingerprinter circ = new CircularFingerprinter(classType);
        circ.calculate(mol);

        // gather all of the (folded) fingerprints into a sorted set
        final int AND_BITS = folding - 1; // e.g. 1024/0x400 -> 1023/0x3FF: chop off higher order bits
        Set<Integer> hashset = new TreeSet<Integer>();
        for (int n = circ.getFPCount() - 1; n >= 0; n--) {
            int code = circ.getFP(n).hashCode;
            if (folding > 0) code &= AND_BITS;
            hashset.add(code);
        }

        // convert the set into a sorted primitive array
        int[] hashes = new int[hashset.size()];
        int p = 0;
        for (Integer h : hashset)
            hashes[p++] = h;

        // record the processed information for model building purposes		
        if (active) numActive++;
        training.add(hashes);
        activity.add(active);
        for (int h : hashes) {
            int[] stash = inHash.get(h);
            if (stash == null) stash = new int[]{0, 0};
            if (active) stash[0]++;
            stash[1]++;
            inHash.put(h, stash);
        }
    }

    /**
     * Performs that Bayesian model generation, using the {molecule:activity} pairs that have been submitted up to this
     * point. Once this method has finished, the object can be used to generate predictions, validation data or to
     * serialise for later use.
     */
    public void build() throws CDKException {
        trainingSize = training.size(); // for posterity
        trainingActives = numActive;

        contribs.clear();

        // the primary model building step: go over all of the hash codes that were discovered during the molecule
        // contributing phase, and convert their ratios into "contributions", each of which is basically the log
        // value of a ratio

        final int sz = training.size();
        final double invSz = 1.0 / sz;
        final double P_AT = numActive * invSz;

        for (Integer hash : inHash.keySet()) {
            final int[] AT = inHash.get(hash);
            final int A = AT[0], T = AT[1];
            final double Pcorr = (A + 1) / (T * P_AT + 1);
            final double P = Math.log(Pcorr);
            contribs.put(hash, P);
        }

        // note thresholds and ranges, for subsequent use

        lowThresh = Double.POSITIVE_INFINITY;
        highThresh = Double.NEGATIVE_INFINITY;
        for (int[] fp : training) {
            double val = 0;
            for (int hash : fp)
                val += contribs.get(hash);
            lowThresh = Math.min(lowThresh, val);
            highThresh = Math.max(highThresh, val);
        }
        range = highThresh - lowThresh;
        invRange = range > 0 ? 1 / range : 0;
    }

    /**
     * For a given molecule, determines its fingerprints and uses them to calculate a Bayesian prediction. Note that this
     * value is unscaled, and so it only has relative meaning within the confines of the model, i.e. higher is more likely to
     * be active.
     * 
     * @param mol molecular structure which cannot be blank or null
     * @return predictor value
     */
    public double predict(IAtomContainer mol) throws CDKException {
        if (mol == null || mol.getAtomCount() == 0) throw new CDKException("Molecule cannot be blank or null.");

        CircularFingerprinter circ = new CircularFingerprinter(classType);
        circ.calculate(mol);

        // gather all of the (folded) fingerprints (eliminating duplicates)
        final int AND_BITS = folding - 1; // e.g. 1024/0x400 -> 1023/0x3FF: chop off higher order bits
        Set<Integer> hashset = new HashSet<Integer>();
        for (int n = circ.getFPCount() - 1; n >= 0; n--) {
            int code = circ.getFP(n).hashCode;
            if (folding > 0) code &= AND_BITS;
            hashset.add(code);
        }

        // sums the corresponding contributor for each hash code generated from the molecule; note that if the
        // molecule generates hash codes not originally in the model, they are discarded (i.e. 0 contribution)
        double val = 0;
        for (int h : hashset) {
            Double c = contribs.get(h);
            if (c != null) val += c;
        }
        return val;
    }

    /**
     * Converts a raw Bayesian prediction and transforms it into a probability-like range, i.e. most values within the domain
     * are between 0..1, and assigning a cutoff of activie = scaled_prediction &gt; 0.5 is reasonable. The transform (scale/translation)
     * is determined by the ROC-analysis, if any. The resulting value can be used as a probability by capping the values so that
     * 0 &le; p &le; 1.
     * 
     * @param pred raw prediction, as provided by the predict(..) method
     * @return scaled prediction	
     */
    public double scalePredictor(double pred) {
        // special case: if there is no differentiation scale, it's either above or below (typically happens only with tiny models)
        if (range == 0) return pred >= highThresh ? 1 : 0;

        return (pred - lowThresh) * invRange;
    }

    /**
     * Produces an ROC validation set, using the inputs provided prior to the model building, using leave-one-out. Note that
     * this should only be used for small datasets, since it is very thorough, and scales as O(N^2) relative to training set
     * size.
     */
    public void validateLeaveOneOut() {
        final int sz = training.size();
        estimates = new double[sz];
        for (int n = 0; n < sz; n++)
            estimates[n] = singleLeaveOneOut(n);
        calculateROC();
        rocType = "leave-one-out";
    }

    /**
     * Produces a ROC validation set by partitioning the inputs into 5 groups, and performing five separate 80% in/20% out
     * model simulations. This is quite efficient, and takes approximately 5 times as long as building the original
     * model: it should be used for larger datasets.
     */
    public void validateFiveFold() {
        rocType = "five-fold";
        validateNfold(5);
    }

    /**
     * Produces a ROC validation set by partitioning the inputs into 3 groups, and performing three separate 66% in/33% out
     * model simulations. This is quite efficient, and takes approximately 3 times as long as building the original
     * model: it should be used for larger datasets.
     */
    public void validateThreeFold() {
        rocType = "three-fold";
        validateNfold(3);
    }

    /**
     * Clears out the training set, to free up memory.
     */
    public void clearTraining() {
        training.clear();
        activity.clear();
    }

    /**
     * Returns the size of the training set, i.e. the total number of molecules used to create the model.
     * 
     * @return training set size
     */
    public int getTrainingSize() {
        return trainingSize;
    }

    /**
     * Returns the number of actives in the training set that was used to create the model.
     * 
     * @return actives in training set
     */
    public int getTrainingActives() {
        return trainingActives;
    }

    /**
     * Returns the integral of the area-under-the-curve of the receiver-operator-characteristic. A value of 1
     * means perfect recall, 0.5 is pretty much random.
     * 
     * @return ROC area under the curve, between 0 and 1
     */
    public double getROCAUC() {
        return rocAUC;
    }

    /**
     * Returns a string description of the method used to create the ROC curve (e.g. "leave-one-out" or "five-fold").
     * 
     * @return validation method
     */
    public String getROCType() {
        return rocType;
    }

    /**
     * Returns X-values that can be used to plot the ROC-curve.
     */
    public float[] getRocX() {
        return rocX;
    }

    /**
     * Returns Y-values that can be used to plot the ROC-curve.
     */
    public float[] getRocY() {
        return rocY;
    }

    /**
     * Returns the optional title used to describe the model.
     * 
     * @return title (may be null)
     */
    public String getNoteTitle() {
        return noteTitle;
    }

    /**
     * Provides an arbitrary title string that briefly summarises the model.
     * 
     * @param title short text description (no newlines or tabs); use null if none
     */
    public void setNoteTitle(String title) {
        if (title.indexOf('\n') >= 0 || title.indexOf('\t') >= 0)
            throw new IllegalArgumentException("Comments cannot contain newlines or tabs.");
        noteTitle = title;
    }

    /**
     * Returns the optional description of the source for the model.
     * 
     * @return origin (may be null)
     */
    public String getNoteOrigin() {
        return noteOrigin;
    }

    /**
     * Provides an arbitrary string that briefly describes the model origin, which may include authors, data
     * source keywords, or other pertinent information.
     * 
     * @param origin short text description (no newlines or tabs); use null if none
     */
    public void setNoteOrigin(String origin) {
        noteOrigin = origin;
    }

    /**
     * Returns the optional comments, which is a list of arbitrary text strings.
     * 
     * @return comment list (may be null)
     */
    public String[] getNoteComments() {
        return noteComments == null ? null : Arrays.copyOf(noteComments, noteComments.length);
    }

    /**
     * Sets the comments for the model, which is a list of strings containing arbitrary content. This may embellish
     * upon the title or origin, or provide other human-readable information, such as references and links.
     * 
     * @param comments list of strings; use null or empty array if none
     */
    public void setNoteComments(String[] comments) {
        if (comments != null)
            for (String comment : comments)
                if (comment.indexOf('\n') >= 0 || comment.indexOf('\t') >= 0)
                    throw new IllegalArgumentException("Comments cannot contain newlines or tabs.");
        noteComments = comments == null ? null : Arrays.copyOf(comments, comments.length);
    }

    /**
     * Converts the current model into a serialised string representation. The serialised form omits the original data
     * that was used to build the model, but otherwise contains all of the information necessary to recreate the model
     * and use it to make predictions against new molecules. The format used is a concise text-based format that is
     * easy to recognise by its prefix, and is reasonably efficient with regard to storage space.
     * 
     * @return serialised model
     */
    public String serialise() {
        StringBuffer buff = new StringBuffer();

        String fpname = classType == CircularFingerprinter.CLASS_ECFP0 ? "ECFP0"
                : classType == CircularFingerprinter.CLASS_ECFP2 ? "ECFP2"
                        : classType == CircularFingerprinter.CLASS_ECFP4 ? "ECFP4"
                                : classType == CircularFingerprinter.CLASS_ECFP6 ? "ECFP6"
                                        : classType == CircularFingerprinter.CLASS_FCFP0 ? "FCFP0"
                                                : classType == CircularFingerprinter.CLASS_FCFP2 ? "FCFP2"
                                                        : classType == CircularFingerprinter.CLASS_FCFP4 ? "FCFP4"
                                                                : classType == CircularFingerprinter.CLASS_FCFP6 ? "FCFP6"
                                                                        : "?";

        buff.append("Bayesian!(" + fpname + "," + folding + "," + lowThresh + "," + highThresh + ")\n");

        // primary payload: the bit contributions
        Set<Integer> sorted = new TreeSet<Integer>();
        for (Integer hash : contribs.keySet())
            sorted.add(hash);
        for (Integer hash : sorted) {
            double c = contribs.get(hash);
            buff.append(hash + "=" + c + "\n");
        }

        // other information
        buff.append("training:size=").append(trainingSize).append('\n');
        buff.append("training:actives=").append(trainingActives).append('\n');

        if (!Double.isNaN(rocAUC)) buff.append("roc:auc=").append(rocAUC).append('\n');
        if (rocType != null) buff.append("roc:type=").append(rocType).append('\n');
        if (rocX != null && rocY != null) {
            buff.append("roc:x=");
            for (int n = 0; n < rocX.length; n++)
                buff.append((n == 0 ? "" : ",") + rocX[n]);
            buff.append('\n');

            buff.append("roc:y=");
            for (int n = 0; n < rocY.length; n++)
                buff.append((n == 0 ? "" : ",") + rocY[n]);
            buff.append('\n');
        }

        if (noteTitle != null) buff.append("note:title=").append(noteTitle).append('\n');
        if (noteOrigin != null) buff.append("note:origin=").append(noteOrigin).append('\n');
        if (noteComments != null) for (String comment : noteComments)
            buff.append("note:comment=").append(comment).append('\n');

        buff.append("!End\n");

        return buff.toString();
    }

    /**
     * Converts a given string into a Bayesian model instance, or throws an exception if it is not valid.
     * 
     * @param str string containing the serialised model
     * @return instantiated model that can be used for predictions
     */
    public static Bayesian deserialise(String str) throws IOException {
        BufferedReader rdr = new BufferedReader(new StringReader(str));
        Bayesian model = deserialise(rdr);
        rdr.close();
        return model;
    }

    /**
     * Reads the incoming stream and attempts to convert it into an instantiated model. The input most be compatible
     * with the format used by the serialise() method, otherwise an exception will be thrown.
     * 
     * @param rdr reader
     * @return instantiated model that can be used for predictions
     */
    public static Bayesian deserialise(BufferedReader rdr) throws IOException {
        String line = rdr.readLine();
        if (line == null || !line.startsWith("Bayesian!(") || !line.endsWith(")"))
            throw new IOException("Not a serialised Bayesian model.");
        String[] bits = line.substring(10, line.length() - 1).split(",");
        if (bits.length < 4) throw new IOException("Invalid header content");

        int classType = bits[0].equals("ECFP0") ? CircularFingerprinter.CLASS_ECFP0
                : bits[0].equals("ECFP2") ? CircularFingerprinter.CLASS_ECFP2
                        : bits[0].equals("ECFP4") ? CircularFingerprinter.CLASS_ECFP4
                                : bits[0].equals("ECFP6") ? CircularFingerprinter.CLASS_ECFP6
                                        : bits[0].equals("FCFP0") ? CircularFingerprinter.CLASS_FCFP0 : bits[0]
                                                .equals("FCFP2") ? CircularFingerprinter.CLASS_FCFP2 : bits[0]
                                                .equals("FCFP4") ? CircularFingerprinter.CLASS_FCFP4 : bits[0]
                                                .equals("FCFP6") ? CircularFingerprinter.CLASS_FCFP6 : 0;
        if (classType == 0) throw new IOException("Unknown fingerprint type: " + bits[0]);

        int folding = Integer.valueOf(bits[1]);
        if (folding > 0) for (int f = folding; f > 0; f = f >> 1)
            if ((f & 1) == 1 && f != 1) {
                folding = -1;
                break;
            }
        if (folding < 0)
            throw new IOException("Fingerprint folding " + bits[1] + " invalid: must be 0 or power of 2.");

        Bayesian model = new Bayesian(classType, folding);
        model.lowThresh = Double.valueOf(bits[2]);
        model.highThresh = Double.valueOf(bits[3]);
        model.range = model.highThresh - model.lowThresh;
        model.invRange = model.range > 0 ? 1 / model.range : 0;

        while (true) {
            line = rdr.readLine();
            if (line == null) throw new IOException("Missing correct terminator line.");
            if (line.equals("!End")) break;

            Matcher m = PTN_HASHLINE.matcher(line);
            if (m.find()) {
                int hash = Integer.valueOf(m.group(1));
                double c = Double.valueOf(m.group(2));
                model.contribs.put(hash, c);
            } else if (line.startsWith("training:size=")) {
                try {
                    model.trainingSize = Integer.valueOf(line.substring(14));
                } catch (NumberFormatException ex) {
                    throw new IOException("Invalid training info line: " + line);
                }
            } else if (line.startsWith("training:actives=")) {
                try {
                    model.trainingActives = Integer.valueOf(line.substring(17));
                } catch (NumberFormatException ex) {
                    throw new IOException("Invalid training info line: " + line);
                }
            } else if (line.startsWith("roc:auc=")) {
                try {
                    model.rocAUC = Double.valueOf(line.substring(8));
                } catch (NumberFormatException ex) {
                    throw new IOException("Invalid AUC line: " + line);
                }
            } else if (line.startsWith("roc:type="))
                model.rocType = line.substring(9);
            else if (line.startsWith("roc:x=")) {
                String[] nums = line.substring(6).split(",");
                model.rocX = new float[nums.length];
                for (int n = 0; n < nums.length; n++) {
                    try {
                        model.rocX[n] = Float.valueOf(nums[n]);
                    } catch (NumberFormatException ex) {
                        throw new IOException("Invalid ROC X coordinates, number=" + nums[n]);
                    }
                }
            } else if (line.startsWith("roc:y=")) {
                String[] nums = line.substring(6).split(",");
                model.rocY = new float[nums.length];
                for (int n = 0; n < nums.length; n++) {
                    try {
                        model.rocY[n] = Float.valueOf(nums[n]);
                    } catch (NumberFormatException ex) {
                        throw new IOException("Invalid ROC Y coordinates, number=" + nums[n]);
                    }
                }
            } else if (line.startsWith("note:title="))
                model.noteTitle = line.substring(11);
            else if (line.startsWith("note:origin="))
                model.noteOrigin = line.substring(12);
            else if (line.startsWith("note:comment=")) {
                model.noteComments = model.noteComments == null ? new String[1] : Arrays.copyOf(model.noteComments,
                        model.noteComments.length + 1);
                model.noteComments[model.noteComments.length - 1] = line.substring(13);
            }
            // (else... silently ignore)
        }

        return model;
    }

    // ----------------- private methods -----------------

    // estimate leave-one-out predictor for a given training entry
    private double singleLeaveOneOut(int N) {
        final boolean exclActive = activity.get(N);
        final int[] exclFP = training.get(N);

        final int sz = training.size(), szN = sz - 1;
        final double invSzN = 1.0 / szN;
        final int activeN = exclActive ? numActive - 1 : numActive;
        final double P_AT = activeN * invSzN;

        double val = 0;
        for (Integer hash : inHash.keySet()) {
            if (Arrays.binarySearch(exclFP, hash) < 0) continue;
            final int[] AT = inHash.get(hash);
            final int A = AT[0] - (exclActive ? 1 : 0), T = AT[1] - 1;

            final double Pcorr = (A + 1) / (T * P_AT + 1);
            final double P = Math.log(Pcorr);
            val += P;
        }
        return val;
    }

    // performs cross-validation, splitting into N different segments
    private void validateNfold(int nsegs) {
        final int sz = training.size();
        final int[] order = new int[sz];
        int p = 0;
        for (int n = 0; n < sz; n++)
            if (activity.get(n)) order[p++] = n;
        for (int n = 0; n < sz; n++)
            if (!activity.get(n)) order[p++] = n;

        // build 5 separate contribution models: each one of them build from the 80% that are *not* in the segment
        final Map<Integer, Double>[] segContribs = new Map[nsegs];
        for (int n = 0; n < nsegs; n++)
            segContribs[n] = buildPartial(order, n, nsegs);

        // use the separate models to estimate the cases that were not covered
        estimates = new double[sz];
        for (int n = 0; n < sz; n++)
            estimates[order[n]] = estimatePartial(order, n, segContribs[n % nsegs]);
        calculateROC();
    }

    // generates a contribution model based on all the training set for which (n%div)!=seg; e.g. for 5-fold, it would use the 80% of the training set
    // that is not implied by the current skein
    private Map<Integer, Double> buildPartial(final int order[], int seg, int div) {
        final int sz = training.size();
        int na = 0, nt = 0;
        final Map<Integer, int[]> ih = new HashMap<Integer, int[]>();
        for (int n = 0; n < sz; n++)
            if (n % div != seg) {
                final boolean active = activity.get(order[n]);
                if (active) na++;
                nt++;
                for (int h : training.get(order[n])) {
                    int[] stash = ih.get(h);
                    if (stash == null) stash = new int[]{0, 0};
                    if (active) stash[0]++;
                    stash[1]++;
                    ih.put(h, stash);
                }
            }

        Map<Integer, Double> segContribs = new HashMap<Integer, Double>();

        final double invSz = 1.0 / nt;
        final double P_AT = na * invSz;
        for (Integer hash : ih.keySet()) {
            final int[] AT = ih.get(hash);
            final int A = AT[0], T = AT[1];
            final double Pcorr = (A + 1) / (T * P_AT + 1);
            final double P = Math.log(Pcorr);
            segContribs.put(hash, P);
        }

        return segContribs;
    }

    // using contributions build from some partial section of the training set, uses that to estimate for an untrained entry
    private double estimatePartial(final int order[], int N, Map<Integer, Double> segContrib) {
        double val = 0;
        for (int h : training.get(order[N])) {
            Double c = segContrib.get(h);
            if (c != null) val += c;
        }
        return val;
    }

    // assumes estimates already calculated, fills in the ROC data
    private void calculateROC() {
        // sort the available estimates, and take midpoints 
        int sz = training.size();

        //int[] idx=Vec.idxSort(estimates);
        Integer[] idx = new Integer[sz];
        for (int n = 0; n < sz; n++)
            idx[n] = n;
        Arrays.sort(idx, new Comparator<Integer>() {

            public int compare(Integer i1, Integer i2) {
                final double v1 = estimates[i1], v2 = estimates[i2];
                return v1 < v2 ? -1 : v1 > v2 ? 1 : 0;
            }
        });

        double[] thresholds = new double[sz + 1];
        int tsz = 0;
        thresholds[tsz++] = lowThresh - 0.01 * range;
        for (int n = 0; n < sz - 1; n++) {
            final double th1 = estimates[idx[n]], th2 = estimates[idx[n + 1]];
            if (th1 == th2) continue;
            thresholds[tsz++] = 0.5 * (th1 + th2);
        }
        thresholds[tsz++] = highThresh + 0.01 * range;

        // x = false positives / actual negatives
        // y = true positives / actual positives
        rocX = new float[tsz];
        rocY = new float[tsz];
        double[] rocT = new double[tsz];

        int posTrue = 0, posFalse = 0, ipos = 0;
        float invPos = 1.0f / numActive, invNeg = 1.0f / (sz - numActive);
        int rsz = 0;
        for (int n = 0; n < tsz; n++) {
            final double th = thresholds[n];
            for (; ipos < sz; ipos++) {
                if (th < estimates[idx[ipos]]) break;
                if (activity.get(idx[ipos]))
                    posTrue++;
                else
                    posFalse++;
            }
            float x = posFalse * invNeg;
            float y = posTrue * invPos;
            if (rsz > 0 && x == rocX[rsz - 1] && y == rocY[rsz - 1]) continue;

            rocX[rsz] = 1 - x;
            rocY[rsz] = 1 - y;
            rocT[rsz] = th;
            rsz++;
        }

        rocX = reverse(resize(rocX, rsz));
        rocY = reverse(resize(rocY, rsz));
        rocT = reverse(resize(rocT, rsz));

        calibrateThresholds(rocX, rocY, rocT);

        // calculate area-under-curve
        rocAUC = 0;
        for (int n = 0; n < rsz - 1; n++) {
            double w = rocX[n + 1] - rocX[n], h = 0.5 * (rocY[n] + rocY[n + 1]);
            rocAUC += w * h;
        }

        // collapse the {x,y} coords: no sensible reason to have huge number of points
        final float DIST = 0.002f, DSQ = DIST * DIST;
        float[] gx = new float[rsz], gy = new float[rsz];
        gx[0] = rocX[0];
        gy[0] = rocY[0];
        int gsz = 1;
        for (int i = 1; i < rsz - 1; i++) {
            float dx = rocX[i] - gx[gsz - 1], dy = rocY[i] - gy[gsz - 1];
            if (dx * dx + dy * dy < DSQ) continue;
            gx[gsz] = rocX[i];
            gy[gsz] = rocY[i];
            gsz++;
        }
        gx[gsz] = rocX[rsz - 1];
        gy[gsz] = rocY[rsz - 1];
        gsz++;
        rocX = resize(gx, gsz);
        rocY = resize(gy, gsz);
    }

    // rederives the low/high thresholds, using ROC curve data: once the analysis is complete, the midpoint will be the optimum balance 
    private void calibrateThresholds(float[] x, float[] y, double[] t) {
        final int sz = t.length;
        int idx = 0;
        for (int n = 1; n < sz; n++)
            if (y[n] - x[n] > y[idx] - x[idx]) idx = n;
        double midThresh = t[idx];
        int idxX = 0, idxY = sz - 1;
        for (; idxX < idx - 1; idxX++)
            if (x[idxX] > 0) break;
        for (; idxY > idx + 1; idxY--)
            if (y[idxY] < 1) break;
        double delta = Math.min(t[idxX] - midThresh, midThresh - t[idxY]);
        lowThresh = midThresh - delta;
        highThresh = midThresh + delta;
        range = 2 * delta;
        invRange = range > 0 ? 1 / range : 0;
    }

    // convenience functions	
    private double[] resize(final double[] arr, int sz) {
        double[] ret = new double[sz];
        for (int n = (arr == null ? 0 : Math.min(sz, arr.length)) - 1; n >= 0; n--)
            ret[n] = arr[n];
        return ret;
    }

    private float[] resize(final float[] arr, int sz) {
        float[] ret = new float[sz];
        for (int n = (arr == null ? 0 : Math.min(sz, arr.length)) - 1; n >= 0; n--)
            ret[n] = arr[n];
        return ret;
    }

    private double[] reverse(double[] arr) {
        double[] ret = new double[arr.length];
        for (int i = 0, j = arr.length - 1; j >= 0; i++, j--)
            ret[j] = arr[i];
        return ret;
    }

    private float[] reverse(float[] arr) {
        float[] ret = new float[arr.length];
        for (int i = 0, j = arr.length - 1; j >= 0; i++, j--)
            ret[j] = arr[i];
        return ret;
    }
}
