package org.openscience.cdk.test.dataclasses;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Debugging data class.
 * 
 * @author     egonw
 * @cdk.module data-debug
 */
public class DebugAtom extends org.openscience.cdk.Atom 
    implements Atom {

	LoggingTool logger = new LoggingTool();
	
	public DebugAtom() {
		super();
	}
	
	public DebugAtom(String symbol) {
		super(symbol);
	}
	
	public DebugAtom(String symbol, Point2d point2d) {
		super(symbol, point2d);
	}
	
	public DebugAtom(String symbol, Point3d point3d) {
		super(symbol, point3d);
	}
	
	public void setCharge(double charge) {
		logger.debug("Setting charge: ", charge);
		super.setCharge(charge);
	}

	public double getCharge() {
		logger.debug("Setting charge: ", super.getCharge());
		return super.getCharge();
	}

	public void setHydrogenCount(int hydrogenCount) {
		logger.debug("Setting hydrogen count: ", hydrogenCount);
		super.setHydrogenCount(hydrogenCount);
	}

	public int getHydrogenCount() {
		logger.debug("Getting hydrogen count: ", super.getHydrogenCount());
		return super.getHydrogenCount();
	}

	public void setPoint2d(Point2d point2d) {
		logger.debug("Setting point2d: x=" + point2d.x + 
				     ", y=" + point2d.y);
		super.setPoint2d(point2d);
	}

	public void setPoint3d(Point3d point3d) {
		logger.debug("Setting point3d: x=" + point3d.x + 
			     ", y=" + point3d.y, ", z=" + point3d.z);
		super.setPoint3d(point3d);
	}

	public void setFractionalPoint3d(Point3d point3d) {
		logger.debug("Setting fractional point3d: x=" + point3d.x + 
			     ", y=" + point3d.y, ", z=" + point3d.z);
		super.setFractionalPoint3d(point3d);
	}

	public void setStereoParity(int stereoParity) {
		logger.debug("Setting stereoParity: ", stereoParity);
		super.setStereoParity(stereoParity);
	}

	public Point2d getPoint2d() {
		Point2d point2d = super.getPoint2d();
		logger.debug("Getting fractional point2d: x=" + point2d.x + 
			     ", y=" + point2d.y);
		return point2d;
	}

	public Point3d getPoint3d() {
		Point3d point3d = super.getPoint3d();
		logger.debug("Getting fractional point3d: x=" + point3d.x + 
			     ", y=" + point3d.y, ", z=" + point3d.z);
		return point3d;
	}

	public Point3d getFractionalPoint3d() {
		Point3d point3d = super.getFractionalPoint3d();
		logger.debug("Getting fractional point3d: x=" + point3d.x + 
			     ", y=" + point3d.y, ", z=" + point3d.z);
		return point3d;
	}

	public double getX2d() {
		logger.debug("Getting X2d: ", super.getX2d());
		return super.getX2d();
	}

	public double getY2d() {
		logger.debug("Getting Y2d: ", super.getY2d());
		return super.getY2d();
	}

	public double getX3d() {
		logger.debug("Getting X3d: ", super.getX3d());
		return super.getX3d();
	}

	public double getY3d() {
		logger.debug("Getting Y3d: ", super.getY3d());
		return super.getY3d();
	}

	public double getZ3d() {
		logger.debug("Getting Z3d: ", super.getZ3d());
		return super.getZ3d();
	}

	public void setFractX3d(double xFract) {
		logger.debug("Setting fractional X3d: ", xFract);
		super.setFractX3d(xFract);
	}

	public void setFractY3d(double yFract) {
		logger.debug("Setting fractional Y3d: ", yFract);
		super.setFractY3d(yFract);
	}

	public void setFractZ3d(double zFract) {
		logger.debug("Setting fractional Z3d: ", zFract);
		super.setFractZ3d(zFract);
	}

	public double getFractX3d() {
		logger.debug("Getting fractional X3d: ", super.getFractX3d());
		return super.getFractX3d();
	}

	public double getFractY3d() {
		logger.debug("Getting fractional Y3d: ", super.getFractY3d());
		return super.getFractY3d();
	}

	public double getFractZ3d() {
		logger.debug("Getting fractional Z3d: ", super.getFractZ3d());
		return super.getFractZ3d();
	}

	public void setX2d(double xCoord) {
		logger.debug("Setting X2d: ", xCoord);
		super.setX2d(xCoord);
	}

	public void setY2d(double yCoord) {
		logger.debug("Setting Y2d: ", yCoord);
		super.setY2d(yCoord);
	}

	public void setX3d(double xCoord) {
		logger.debug("Setting X3d: ", xCoord);
		super.setX3d(xCoord);
	}

	public void setY3d(double yCoord) {
		logger.debug("Setting Y3d: ", yCoord);
		super.setY3d(yCoord);
	}

	public void setZ3d(double zCoord) {
		logger.debug("Setting Z3d: ", zCoord);
		super.setZ3d(zCoord);
	}

	public int getStereoParity() {
		logger.debug("Getting stereo parity: ", super.getStereoParity());
		return super.getStereoParity();
	}

}
