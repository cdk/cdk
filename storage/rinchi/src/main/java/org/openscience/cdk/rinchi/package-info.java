/**
 * This package provides functionality related to <a href="https://dx.doi.org/10.1186/s13321-018-0277-8">RInChI</a>.
 * <h2>Overview</h2>
 * <ol>
 *     <li>{@link org.openscience.cdk.rinchi.RInChIGenerator}: Given an {@link org.openscience.cdk.interfaces.IReaction}
 *     generates RInChI, RAuxInfo, Long-RInChIKey, Short-RInChIKey and Web-RInChIKey.</li>
 *     <li>{@link org.openscience.cdk.rinchi.RInChIToReaction}: Consumes a RInChI and produces an
 *     {@link org.openscience.cdk.interfaces.IReaction}.</li>
 *     <li>{@link org.openscience.cdk.rinchi.RInChIDecomposition}: Given a RInChI and optionally an associated RAuxInfo
 *     decompose the RInChI into its constituent InChIs and AuxInfos.</li>
 * </ol>
 * To provide this functionality the library <a href="https://github.com/dan2097/jna-inchi">jna-inchi</a> is used
 * which in turn places calls to methods of the <a href="https://github.com/IUPAC-InChI/InChI">native InChI library</a>
 * by means of a <a href="https://github.com/java-native-access/jna">JNA</a> wrapper.
 * <p>
 *    The usage of "aromatic" bonds is strongly discouraged. Instead, <b>Kekule</b> structures are <b>recommended</b>.
 * </p>
 */
package org.openscience.cdk.rinchi;