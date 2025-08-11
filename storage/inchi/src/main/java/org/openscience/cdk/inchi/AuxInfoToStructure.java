package org.openscience.cdk.inchi;

import io.github.dan2097.jnainchi.InchiInputFromAuxinfoOutput;
import io.github.dan2097.jnainchi.InchiStatus;
import io.github.dan2097.jnainchi.JnaInchi;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * This class generates an {@link IAtomContainer} from an InChI AuxInfo (auxiliary information) string.
 *
 * <p>
 * It acts as a wrapper around the JNI-InChI library functions,
 * parsing the AuxInfo string to regenerate the molecular structure,
 * including optional handling of implicit hydrogen suppression and stereo differences.
 * </p>
 *
 * <p>
 * It should be noted that issues such as the potential absence of hydrogen atoms
 * may arise during the process of reconversion within the InChI API.
 * It is important to note that the result should be checked.
 * </p>
 *
 * <p>
 * The AuxInfo string must be a valid string as produced by the InChI generation process.
 * The resulting IAtomContainer will have the same 2D or 3D coordinates as given in the
 * InChI AuxInfo string. If there are no coordinates given they are set to 0.0,
 * if only 2D coordinates are given the z-coordinate is 0.0.
 * In some cases are no implicit hydrogens returned by the InChI API. To set hydrogens
 * the {@link org.openscience.cdk.atomtype.CDKAtomTypeMatcher} is intended.
 * </p>
 *
 * <br>
 * <b>Example usage</b>
 * <br>
 *
 * <pre>{@code
 * String auxInfo = "/AuxInfo=1/0/N:1,2,3,4,5,6/E:(1,2),(2,3),(3,4),(4,5),(5,6),(6,1)/rA:6C..."
 * IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
 *
 * AuxInfoToStructure converter = new AuxInfoToStructure(auxInfo, builder);
 * IAtomContainer molecule = converter.getAtomContainer();
 *
 * if (converter.getStatus() == InchiStatus.SUCCESS) {
 *    // Use the molecule as needed
 *    System.out.println("Successfully parsed structure from AuxInfo");
 * } else {
 *    System.err.println("Error parsing AuxInfo: " + converter.getMessage());
 * }
 * }</pre>
 *
 * @author Felix Bänsch
 * @see JnaInchi#getInchiInputFromAuxInfo(String, boolean, boolean)
 * @see InChIOutputToStructure
 * @see IAtomContainer
 */
public class AuxInfoToStructure {

  protected InchiInputFromAuxinfoOutput output;
  protected IAtomContainer molecule;

  /**
   * Constructor. Generates IAtomContainer from InChI AuxInfo.
   *
   * @param auxInfo  the InChI AuxInfo string (must not be {@code null})
   * @param builder  the {@link IChemObjectBuilder} to create {@link IAtomContainer}
   * @throws CDKException if parsing the AuxInfo fails
   * @throws IllegalArgumentException if {@code auxInfo} is {@code null}
   */
  protected AuxInfoToStructure(String auxInfo, IChemObjectBuilder builder)
          throws CDKException {
    this(auxInfo, builder, false);
  }


  /**
   * Constructor. Generates IAtomContainer from InChI AuxInfo with
   * optional suppression of implicit hydrogen atoms and
   * differentiation of unknown and undefined stereochemistry.
   *
   * @param auxInfo  the InChI AuxInfo string (must not be {@code null})
   * @param builder  the {@link IChemObjectBuilder} to create {@link IAtomContainer}
   * @param diffUnkUndfStereo if {@code true}, differentiates unknown and undefined stereo
   * @throws CDKException if parsing the AuxInfo fails
   * @throws IllegalArgumentException if {@code auxInfo} is {@code null}
   */
  protected AuxInfoToStructure(String auxInfo, IChemObjectBuilder builder, boolean diffUnkUndfStereo)
          throws CDKException {
    if (auxInfo == null)
      throw new IllegalArgumentException("Null AuxInfo string provided");
    this.output = JnaInchi.getInchiInputFromAuxInfo(auxInfo, false, diffUnkUndfStereo);
    this.molecule = InChIOutputToStructure.generateAtomContainerFromAuxInfo(this.output, builder);
  }

  /**
   * Returns generated molecule.
   * @return An AtomContainer object
   */
  public IAtomContainer getAtomContainer() {
    return (molecule);
  }

  /**
   * Access the status of the InChI output.
   * @return the status
   */
  public InchiStatus getStatus() {
    return output.getStatus();
  }

  /**
   * Gets generated (error/warning) messages.
   */
  public String getMessage() {
    return output.getMessage();
  }

  /**
   * Gets chiral flag.
   */
  public Boolean getChiralFlag() {
    return output.getChiralFlag();
  }
}
