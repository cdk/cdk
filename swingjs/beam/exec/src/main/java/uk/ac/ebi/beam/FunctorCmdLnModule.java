/*
 * Copyright (c) 2015. John May
 */

package uk.ac.ebi.beam;

import joptsimple.OptionSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * An abstract class for mapping one-to-one between input and output of a piping module.
 * Functionality is implemented using the {@link #createFunctor(joptsimple.OptionSet)} method.
 *
 * By default the functor is mapped over multiple threads.
 */
abstract class FunctorCmdLnModule extends PipingCmdLnModule {

    /**
     * How much input we process at once, could be adjustable.
     */
    final int WORK_UNIT_SIZE = 15000;
    static final boolean DEBUG = false;

    FunctorCmdLnModule(String name) {
        super(name);
        optparser.accepts("t", "number of threads")
                 .withRequiredArg()
                 .ofType(Integer.class)
                 .defaultsTo(Runtime.getRuntime().availableProcessors());
        optparser.accepts("no-warn", "suppress warnings");
    }

    /**
     * Create a new functor for mapping some input (consumed) to an output (produced). The function
     * will be shared between threads and should not hold modify state during mapping.
     *
     * @param optionSet options
     * @return the functor
     */
    abstract Functor createFunctor(OptionSet optionSet);

    /**
     * Consumes
     *
     * @param brdr   input reader (UTF-8)
     * @param bwtr   output reader (UTF-8)
     * @param optset options for the module
     * @throws IOException
     */
    @Override void process(BufferedReader brdr, BufferedWriter bwtr, InputCounter inputCounter, OptionSet optset) throws IOException {

        final int numThreads = (Integer) optset.valueOf("t");

        if (!optset.has("prog-off"))
            report("num_threads: %d\n", numThreads);

        if (numThreads > 1) {
            processMultiThreaded(brdr, bwtr, inputCounter, optset, numThreads);
        }
        else {
            processSingle(brdr, bwtr, inputCounter, optset);
        }
    }

    private void processSingle(BufferedReader brdr, BufferedWriter bwtr, InputCounter inputCounter, OptionSet optset) throws IOException {
        final long tStart = System.nanoTime();
        final boolean showProgress = !optset.has("prog-off");
        final boolean showWarnings    = !optset.has("no-warn");

        final Functor functor = createFunctor(optset);

        String line;
        int cnt = 0;
        while ((line = brdr.readLine()) != null) {
            try {
                bwtr.write(functor.map(line));
                bwtr.newLine();
                if (showProgress && ++cnt % 2500 == 0) {
                    report("%d " + makeProgStr(inputCounter.count(),
                                               inputCounter.total(),
                                               elapsedMilli(tStart)), cnt);
                }
            } catch (Exception | InternalError e) {
                if (showWarnings) {
                    report("error, " + e.getMessage() + "\nline:" + escapeForPrintf(line) + "\n");
                }
                if (showProgress)
                    report("%d " + makeProgStr(inputCounter.count(),
                                               inputCounter.total(),
                                               elapsedMilli(tStart)), cnt);
            }
        }
        if (showProgress)
            report("%d " + makeProgStr(inputCounter.count(), inputCounter.total(), elapsedMilli(tStart)) + "\n", cnt);
    }

    private void processMultiThreaded(BufferedReader brdr, BufferedWriter bwtr,
                                      InputCounter inputCounter, OptionSet optset,
                                      int numThreads) throws IOException {

        final long tStart = System.nanoTime();
        final boolean showProgress = !optset.has("prog-off");
        final boolean showWarnings = !optset.has("no-warn");

        final ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // collect first workable unit
        long currInputCount = inputCounter.count();
        List<String> lines = nextLines(brdr);
        currInputCount = inputCounter.count() - currInputCount;
        final Set<Future<Result>> running = new HashSet<>();
        final List<Future<Result>> completed = new ArrayList<>();

        final Functor functor = createFunctor(optset);

        int cnt = 0;
        long inputCount = 0;

        while (!lines.isEmpty()) {

            running.add(executor.submit(new CallableFunctor(functor, lines, showWarnings, currInputCount)));

            // spin round while all threads are occupied
            for (; running.size() >= numThreads; ) {

                for (Future<Result> future : running) {
                    if (future.isDone()) {
                        Result result = getResult(future);
                        if (result == null)
                            throw new InternalError("Could not get thread result");
                        cnt += output(bwtr, result);
                        inputCount = undateInputCount(inputCount, result);
                        if (showProgress)
                            report("%d " + makeProgStr(inputCount,
                                                       inputCounter.total(),
                                                       elapsedMilli(tStart)), cnt);
                        completed.add(future);
                    }
                }
                running.removeAll(completed);
                completed.clear();
                sleepSafe(2);
            }

            // get more lines
            currInputCount = inputCounter.count();
            lines = nextLines(brdr);
            currInputCount = inputCounter.count() - currInputCount;
        }

        // wait for running threads to finish
        for (; !running.isEmpty(); ) {
            for (Future<Result> future : running) {
                if (future.isDone()) {
                    Result result = getResult(future);
                    if (result == null)
                        throw new InternalError("Could not get thread result");
                    cnt += output(bwtr, result);
                    inputCount = undateInputCount(inputCount, result);
                    if (showProgress)
                        report("%d " + makeProgStr(inputCount, inputCounter.total(), elapsedMilli(tStart)), cnt);
                    completed.add(future);
                }
            }
            running.removeAll(completed);
            completed.clear();
            sleepSafe(2);
        }

        if (showProgress)
            report("%d " + makeProgStr(inputCount, inputCounter.total(), elapsedMilli(tStart)) + "\n", cnt);

        executor.shutdown();
    }

    private static void sleepSafe(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private long elapsedMilli(long tStart) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - tStart);
    }
    
    private Result getResult(Future<Result> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println(e.getMessage());

        }
        return null;
    }

    private long undateInputCount(long inputCount, Result result) {
        inputCount += result.inputSize;
        return inputCount;
    }

    private int output(BufferedWriter bwtr, Result res) {
        try {
            int cnt = 0;
            for (String str : res.lines) {
                if (str == null) continue;
                bwtr.write(str);
                bwtr.newLine();
                ++cnt;
            }
            return cnt;
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        }
        return 0;
    }

    private List<String> nextLines(BufferedReader brdr) throws IOException {
        String line;
        List<String> lines = new ArrayList<>(WORK_UNIT_SIZE);
        while ((line = brdr.readLine()) != null && lines.size() < WORK_UNIT_SIZE) {
            lines.add(line);
        }
        return lines;
    }

    private final class Result {
        List<String> lines;
        long         inputSize;

        public Result(List<String> lines, long inputSize) {
            this.lines = lines;
            this.inputSize = inputSize;
        }
    }

    final class CallableFunctor implements Callable<Result> {

        private final Functor      functor;
        private final List<String> lines;
        private final long         inputSize;
        private final boolean warn;

        public CallableFunctor(Functor functor, List<String> lines, boolean showWarnings, long inputSize) {
            this.functor = functor;
            this.lines = lines;    
            this.warn = showWarnings;
            this.inputSize = inputSize;
        }

        @Override
        public Result call() {
            int n = lines.size();
            for (int i = 0; i < n; i++) {
                String line = lines.get(i);
                try {
                    lines.set(i,
                              functor.map(line));
                } catch (Exception e) {
                    if (warn) {
                        report("\nerror, " + e.getMessage() + "\nline:" + escapeForPrintf(lines.get(i)) + "\n");
                    }
                    lines.set(i, null);
                }
            }
            return new Result(lines, inputSize);
        }
    }
    
    static String escapeForPrintf(String str) {
        return str.replaceAll("%", "%%");
    }

    abstract class Functor {
        abstract String map(String str) throws IOException;
    }
}
