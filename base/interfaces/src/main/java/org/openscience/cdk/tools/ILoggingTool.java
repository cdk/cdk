/* Copyright (C) 2002-2003  Christoph Steinbeck <steinbeck@users.sf.net>
 *               2002-2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.tools;

/**
 * Useful for logging messages. Often used as a class static variable
 * instantiated like (see {@link LoggingToolFactory}):
 * <pre>
 * public class SomeClass {
 *   private static ILoggingTool logger;
 *
 *   static {
 *     logger = LoggingToolFactory.createLoggingTool(SomeClass.class);
 *   }
 * }
 * </pre>
 * There is no special reason not to make the logger private and static, as the
 * logging information is closely bound to one specific Class, not subclasses
 * and not instances.
 *
 * <p>The logger has five logging levels:
 * <dl>
 *  <dt>DEBUG
 *  <dd>Default mode. Used for information you might need to track down the
 *      cause of a bug in the source code, or to understand how an algorithm
 *      works.
 *  <dt>WARNING
 *  <dd>This indicates a special situation which is unlike to happen, but for
 *      which no special actions need to be taken. E.g. missing information in
 *      files, or an unknown atom type. The action is normally something user
 *      friendly.
 *  <dt>INFO
 *  <dd>For reporting informative information to the user that he might easily
 *      disregard. Real important information should be given to the user using
 *      a GUI element.
 *  <dt>FATAL
 *  <dd>This level is used for situations that should not have happened *and*
 *      that lead to a situation where this program can no longer function
 *      (rare in Java).
 *  <dt>ERROR
 *  <dd>This level is used for situations that should not have happened *and*
 *      thus indicate a bug.
 * </dl>
 *
 * <p>Consider that the debugging will not always be turned on. Therefore, it is
 * better not to concatenate string in the logger.debug() call, but have the
 * ILoggingTool do this when appropriate. In other words, use:
 * <pre>
 * logger.debug("The String X has this value: ", someString);
 * logger.debug("The int Y has this value: ", y);
 * </pre>
 * instead of:
 * <pre>
 * logger.debug("The String X has this value: " + someString);
 * logger.debug("The int Y has this value: " + y);
 * </pre>
 *
 * <p>For logging calls that require even more computation you can use the
 * <code>isDebugEnabled()</code> method:
 * <pre>
 * if (logger.isDebugEnabled()) {
 *   logger.info("The 1056389822th prime that is used is: ",
 *     calculatePrime(1056389822));
 * }
 * </pre>
 *
 * <p>In addition to the methods specific in the interfance, implementations
 * must also implement the static method {@code create(Class<?>)} which
 * can be used by the {@link LoggingToolFactory} to instantiate the
 * implementation.
 *
 * @cdk.module  interfaces
 * @cdk.githash
 */
public interface ILoggingTool {

    /**
     * Default number of StackTraceElements to be printed by debug(Exception).
     */
    public final int DEFAULT_STACK_LENGTH = 5;

    /**
     * Outputs system properties for the operating system and the java
     * version. More specifically: os.name, os.version, os.arch, java.version
     * and java.vendor.
     */
    public void dumpSystemProperties();

    /**
     * Sets the number of StackTraceElements to be printed in DEBUG mode when
     * calling <code>debug(Throwable)</code>.
     * The default value is DEFAULT_STACK_LENGTH.
     *
     * @param length the new stack length
     *
     * @see #DEFAULT_STACK_LENGTH
     */
    public void setStackLength(int length);

    /**
     * Outputs the system property for java.class.path.
     */
    public void dumpClasspath();

    /**
     * Shows DEBUG output for the Object. If the object is an instanceof
     * {@link Throwable} it will output the trace. Otherwise it will use the
     * toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    public void debug(Object object);

    /**
     * Shows DEBUG output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() too and output
     * @param objects Object... to apply toString() too and output
     */
    public void debug(Object object, Object... objects);

    /**
     * Shows ERROR output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    public void error(Object object);

    /**
     * Shows ERROR output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() too and output
     * @param objects Object... to apply toString() too and output
     */
    public void error(Object object, Object... objects);

    /**
     * Shows FATAL output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    public void fatal(Object object);

    /**
     * Shows INFO output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    public void info(Object object);

    /**
     * Shows INFO output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() too and output
     * @param objects Object... to apply toString() too and output
     */
    public void info(Object object, Object... objects);

    /**
     * Shows WARN output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    public void warn(Object object);

    /**
     * Shows WARN output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param objects Object... to apply toString() too and output
     */
    public void warn(Object object, Object... objects);

    /**
     * Use this method for computational demanding debug info.
     * For example:
     * <pre>
     * if (logger.isDebugEnabled()) {
     *   logger.info("The 1056389822th prime that is used is: ",
     *                calculatePrime(1056389822));
     * }
     * </pre>
     *
     * @return true, if debug is enabled
     */
    public boolean isDebugEnabled();
}
