/*
 * Copyright (C) 2024 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.cli;

import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smarts.SmartsPattern;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tautomer.TautSubSearch2;
import org.openscience.cdk.tautomer.TautSubSearch4;
import org.openscience.cdk.tautomer.Tautomers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SmiGrep {

    public static final int COUNT_FLAG     = 0x01;
    public static final int NEGATE_FLAG    = 0x02;
    public static final int TIMING_FLAG    = 0x04;
    public static final int TAUT_GEN       = 0x08;
    public static final int FAST_FLAG      = 0x10;
    public static final int TAUT_MATCH     = 0x20;

    private static String       query   = null;
    private static List<String> inpnames = new ArrayList<>();
    private static int          njobs = 1;
    private static int          limit = 0;
    private static int          flags = 0;

    private static void DisplayUsage(String error) {
        if (error != null)
            System.err.println("ERROR: " + error);
        System.err.println("Usage: org.openscience.cdk.cli.SmiGrep [-v -c -ht -t -j <num>] {query} {input}");
        System.exit(1);
    }

    private static void ProcessCommandLine(String[] args) {
        int j = 0;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.charAt(0) == '-') {

                // parse -j <num> flags
                if (arg.startsWith("-j")) {
                    String val = arg.substring(2);
                    if (val.startsWith("="))
                        val = val.substring(1);
                    if (val.isEmpty()) {
                        if (++i == args.length)
                            DisplayUsage("No value provided for -j <num>");
                        val = args[i];
                    }
                    njobs = Integer.parseInt(val);
                    continue;
                }
                else if (arg.startsWith("-n")) {
                    String val = arg.substring(2);
                    i = processLimit(args, val, i);
                    continue;
                }
                else if (arg.startsWith("--limit")) {
                    String val = arg.substring(7);
                    i = processLimit(args, val, i);
                    continue;
                }

                switch (arg) {
                    case "-f":
                    case "--fast":   flags |= FAST_FLAG; break;
                    case "-c":
                    case "--count":  flags |= COUNT_FLAG; break;
                    case "-v":
                    case "--negate": flags |= NEGATE_FLAG; break;
                    case "-t":
                    case "--timing": flags |= TIMING_FLAG; break;
                    case "-ht":      flags |= TAUT_GEN; break;
                    case "--tmatch": flags |= TAUT_MATCH; break;
                    default: DisplayUsage("Unknown flag: " + arg);
                }
            } else {
                switch (j++) {
                    case 0: query = arg; break;
                    default: inpnames.add(arg);
                }
            }
        }
        if (j == 0)
            DisplayUsage("No query or dbname provided");
        if (j == 1)
            DisplayUsage("No target molecule/file provided");
        if ((flags & FAST_FLAG) != 0 && (flags & TAUT_GEN) != 0)
            System.err.println("Warning: -f cannot be safely used with -ht");
    }

    private static int processLimit(String[] args, String val, int i) {
        if (val.startsWith("="))
            val = val.substring(1);
        if (val.isEmpty()) {
            if (++i == args.length)
                DisplayUsage("No value provided for -n <num>");
            val = args[i];
        }
        limit = Integer.parseInt(val);
        return i;
    }

    private static void ProcessInput(Pattern pat, Reader rdr, String source, String query) {

        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(builder);
        int count = 0;
        boolean display   = (flags&COUNT_FLAG) == 0;
        boolean negate    = (flags&NEGATE_FLAG) != 0;
        boolean findRings = (flags&FAST_FLAG) != 0;
        boolean checkTaut = (flags& TAUT_GEN) != 0;
        long tMatch = 0;
        String prevMatch = "{no_match}";
        long t0 = System.nanoTime();
        try (BufferedReader brdr = new BufferedReader(rdr)) {
            String line;
            while ((line = brdr.readLine()) != null) {
                try {
                    if (sameMolTitle(line, prevMatch))
                        continue;
                    IAtomContainer mol = smipar.parseSmiles(line);
                    if (findRings) Cycles.markRingAtomsAndBonds(mol);
                    boolean matched = checkMatches(pat, mol, checkTaut) != negate;
                    prevMatch = matched ? mol.getTitle() : "{no_match}";
                    if (matched && display) System.out.println(line);
                    count += matched ? 1 : 0;
                    if (limit != 0 && count == limit) break;
                } catch (InvalidSmilesException e) {
                    System.err.println("ERROR: Bad SMILES " + line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder report = new StringBuilder();
        if (source != null)
            report.append(source).append(":").append('\t');
        if (query != null)
            report.append(getTitle(query)).append('\t');
        if (!display)
            report.append(count).append('\t');
        long t1 = System.nanoTime();
        if ((flags&TIMING_FLAG) != 0)
            report.append(Math.round((t1-t0)/1e6));
        System.err.println(report);
    }

    private static String getTitle(String query) {
        String[] parts = query.split("[\t ]", 2);
        return parts.length == 2 ? parts[1] : parts[0];
    }

    private static boolean checkMatches(Pattern pat,
                                        IAtomContainer mol,
                                        boolean tautomers) {
        if (!tautomers)
            return pat.matches(mol);
        for (IAtomContainer tmp : Tautomers.hetero(mol)) {
            if (pat.matches(tmp))
                return true;
        }
        return false;
    }

    private static boolean sameMolTitle(String line, String prevTitle) {
        if (!line.endsWith(prevTitle))
            return false;
        if (line.length() == prevTitle.length())
            return false;
        char delim = line.charAt(line.length() - prevTitle.length() - 1);
        return delim == ' ' || delim == '\t';
    }

    private static void ProcessInput(Pattern pat, InputStream in, String source, String query) {
        try (InputStreamReader rdr = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            ProcessInput(pat, rdr, source, query);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ProcessCommandLine(args);
        if (new File(query).exists()) {
            try (Stream<String> lines = Files.lines(new File(query).toPath())) {
                lines.forEach(SmiGrep::processQuery);
            } catch (IOException e) {
                System.err.println("IO Error: " + e.getMessage());
            }
        } else {
            processQuery(query);
        }
    }

    private static void processQuery(String query) {
        if (query.isEmpty() || query.startsWith("#"))
            return;

        Pattern pattern;
        if ((flags & TAUT_MATCH) != 0) {
            pattern = new TautSubSearch2(SilentChemObjectBuilder.getInstance(),
                                         query);
        } else {
            pattern = SmartsPattern.create(query);
            if ((flags&FAST_FLAG) != 0)
                ((SmartsPattern)pattern).setPrepare(false);
        }

        for (String inpname : inpnames) {
            if (inpname.equals("-")) {
                ProcessInput(pattern, System.in, "STDIN", query);
            } else if (new File(inpname).exists()) {
                try (InputStream in = Files.newInputStream(Paths.get(inpname))) {
                    ProcessInput(pattern,
                                 in,
                                 inpnames.size() == 1 ? null : inpname, query);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                ProcessInput(pattern, new StringReader(inpname), "ARGS", query);
            }
        }
    }
}
