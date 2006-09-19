package org.openscience.cdk.renderer;

import org.openscience.cdk.interfaces.IAtom;

/**
 * @cdk.module render
 */
public class Arrow 
{
	IAtom start;
	IAtom end;
	
	public Arrow(IAtom atom1, IAtom atom2)
	{
		this.start = atom1;
		this.end = atom2;
	}
	
	public IAtom getEnd() {
		return end;
	}

	public void setEnd(IAtom end) {
		this.end = end;
	}

	public IAtom getStart() {
		return start;
	}

	public void setStart(IAtom start) {
		this.start = start;
	}
		
	
}
