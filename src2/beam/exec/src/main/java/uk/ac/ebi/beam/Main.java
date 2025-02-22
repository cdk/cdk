/*
 * Copyright (c) 2015. John May
 */

package uk.ac.ebi.beam;

import java.util.*;

public class Main {

    private final static Map<String, CmdLnModule> modules = new TreeMap<>();

    static void addModule(CmdLnModule module) {
        modules.put(module.name().toLowerCase(Locale.ROOT),
                    module);
    }

    static {
        for (CmdLnModule m : ServiceLoader.load(CmdLnModule.class))
            addModule(m);
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            printUsage();
            return;
        }

        final String cmd = args[0].toLowerCase(Locale.ROOT);

        if ("help".equals(cmd)) {
            printUsage();
            printHelp();
        } else if (modules.containsKey(cmd)) {
            modules.get(cmd).exec(Arrays.copyOfRange(args, 1, args.length));
        } else {
            printUsage();
        }
    }

    private static void printUsage() {
        StringBuilder sb = new StringBuilder();
        sb.append("usage: beam [help");
        for (String key : modules.keySet())
            sb.append('|').append(key);
        sb.append("] {ARGS}\n");
        System.err.println(sb.toString());
    }

    private static void printHelp() {
        StringBuilder sb = new StringBuilder();
        for (CmdLnModule module : modules.values())
            sb.append(module.getHelpInfo());
        System.err.println(sb.toString());
    }
}
