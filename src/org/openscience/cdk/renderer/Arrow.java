package org.openscience.cdk.renderer;

import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @cdk.module renderer
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
