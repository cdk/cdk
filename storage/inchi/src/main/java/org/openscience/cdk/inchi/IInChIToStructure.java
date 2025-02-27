package org.openscience.cdk.inchi;

import io.github.dan2097.jnainchi.InchiStatus;
import net.sf.jniinchi.INCHI_RET;

public interface IInChIToStructure {
	
	@Deprecated
	INCHI_RET getReturnStatus();

	/**
	 * Access the status of the InChI output.
	 * 
	 * @return the status
	 */
	InchiStatus getStatus();

	/**
	 * Gets generated (error/warning) messages.
	 */
	String getMessage();

	/**
	 * Gets generated log.
	 */
	String getLog();

	/**
	 * <p>
	 * Returns warning flags, see INCHIDIFF in inchicmp.h.
	 *
	 * <p>
	 * [x][y]: <br>
	 * x=0 =&gt; Reconnected if present in InChI otherwise Disconnected/Normal <br>
	 * x=1 =&gt; Disconnected layer if Reconnected layer is present <br>
	 * y=1 =&gt; Main layer or Mobile-H <br>
	 * y=0 =&gt; Fixed-H layer
	 */
	long[][] getWarningFlags();


	void initializeInchiModel(String inchi);

	// for-loop setters
	void setAtom(int i);

	void setBond(int i);

	void setStereo0D(int i);

	// general counts
	int getNumAtoms();

	int getNumBonds();

	int getNumStereo0D();

	// Atom Methods
	String getElementType();

	double getX();

	double getY();

	double getZ();

	int getCharge();

	int getImplicitH();

	int getIsotopicMass();

	int getImplicitDeuterium();

	int getImplicitTritium();

	String getRadical();

	// Bond Methods
	int getIndexOriginAtom();

	int getIndexTargetAtom();

	String getInchiBondType();

	String getInchIBondStereo();

	// Stereo Methods
	String getParity();

	String getStereoType();

	int getCenterAtom();

	int[] getNeighbors();

	default String uc(Object o) {
		return o.toString().toUpperCase();
	}


}
