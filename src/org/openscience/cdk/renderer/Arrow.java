package org.openscience.cdk.renderer;

import org.openscience.cdk.interfaces.IAtom;

/**
 * @cdk.module render
 */
public class Arrow 
{
	IAtom start;
	IAtom end;
	
	public Arrow(IAtom a1, IAtom a2)
	{
		this.start = a1;
		this.end = a2;
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
