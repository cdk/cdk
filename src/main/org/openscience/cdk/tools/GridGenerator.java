/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.vecmath.Point3d;

/**
 * Generates a grid of points in 3D space within given boundaries.
 * 
 * @author cho
 * @cdk.svnrev  $Revision$
 * @cdk.created 2005-09-30
 */
public class GridGenerator {

	double latticeConstant = 0.5;
	double extendGrid = 2;
	double[][][] grid = null;
	double[] gridArray = null;
	double maxx = 0;
	double maxy = 0;
	double maxz = 0;
	double minx = 0;
	double miny = 0;
	double minz = 0;
	int[] dim = { 0, 0, 0 };

	public GridGenerator() {
	}

	public GridGenerator(double min, double max) {
		setDimension(min, max);
		generateGrid();
	}

	/**
	 * @param initialValue used as initial value for the grid points
	 */
	public GridGenerator(double min, double max, double initialValue) {
		setDimension(min, max);
		generateGrid();
		initializeGrid(initialValue);
	}

	public GridGenerator(double[] minMax, double initialValue,
			boolean cubicGridFlag) {
		setDimension(minMax, cubicGridFlag);
		generateGrid();
		initializeGrid(initialValue);
	}

	/**
	 * Method sets the maximal 3d dimensions to given min and max values.
	 */
	public void setDimension(double min, double max) {
		this.minx = min;
		this.maxx = max;
		this.miny = min;
		this.maxy = max;
		this.minz = min;
		this.maxz = max;
	}

	/**
	 * Method sets the maximal 3d dimensions to given min and max values.
	 */
	public void setDimension(double[] minMax, boolean cubicGridFlag) {
		if (cubicGridFlag) {
			double min = minMax[0];
			double max = minMax[0];
			for (int i = 0; i < minMax.length; i++) {
				if (minMax[i] < min) {
					min = minMax[i];
				} else if (minMax[i] > max) {
					max = minMax[i];
				}
			}
			setDimension(min, max);
		} else {
			this.minx = minMax[0];
			this.maxx = minMax[1];
			this.miny = minMax[2];
			this.maxy = minMax[3];
			this.minz = minMax[4];
			this.maxz = minMax[5];
		}
	}

	/**
	 * Method sets the maximal 3d dimensions to given min and max values.
	 */
	public void setDimension(double minx, double maxx, double miny,
			double maxy, double minz, double maxz) {
		this.minx = minx;
		this.maxx = maxx;
		this.miny = miny;
		this.maxy = maxy;
		this.minz = minz;
		this.maxz = maxz;
	}

	/**
	 * Main method creates a grid between given boundaries (dimensions).
	 * The grid my be extended over the given boundaries with the 
	 * variable extendGrid.
	 */
	public void generateGrid() {
		minx = minx - extendGrid;
		maxx = maxx + extendGrid;
		miny = miny - extendGrid;
		maxy = maxy + extendGrid;
		minz = minz - extendGrid;
		maxz = maxz + extendGrid;

		dim[0] = (int) Math.round(Math.abs(maxx - minx) / latticeConstant);
		dim[1] = (int) Math.round(Math.abs(maxy - miny) / latticeConstant);
		dim[2] = (int) Math.round(Math.abs(maxz - minz) / latticeConstant);

		grid = new double[dim[0] + 1][dim[1] + 1][dim[2] + 1];
	}

	/**
	 * Method initialise the given grid points with a value.
	 */
	public void initializeGrid(double value) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				for (int k = 0; k < grid[0][0].length; k++) {
					grid[k][j][i] = value;
				}
			}
		}
	}

	/**
	 * Method initialise the given grid points with a value.
	 */
	public double[][][] initializeGrid(double grid[][][], double value) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				for (int k = 0; k < grid[0][0].length; k++) {
					grid[k][j][i] = value;
				}
			}
		}
		return grid;
	}

	/**
	 * Method transforms the grid to an array.
	 */
	public double[] gridToGridArray(double[][][] grid) {
		if (grid == null) {
			grid = this.grid;
		}
		gridArray = new double[dim[0] * dim[1] * dim[2] + 3];
		int dimCounter = 0;
		for (int z = 0; z < grid[0][0].length; z++) {
			for (int y = 0; y < grid[0].length; y++) {
				for (int x = 0; x < grid.length; x++) {
					gridArray[dimCounter] = grid[x][y][z];
					dimCounter++;
				}
			}
		}
		return gridArray;
	}

	/**
	 * Method calculates coordiantes from a given grid point.
	 */
	public Point3d getCoordinatesFromGridPoint(Point3d gridPoint) {
		double dx = minx + latticeConstant * gridPoint.x;
		double dy = miny + latticeConstant * gridPoint.y;
		double dz = minz + latticeConstant * gridPoint.z;
		return new Point3d(dx, dy, dz);
	}

	/**
	 * Method calculates coordiantes from a given grid array position.
	 */
	public Point3d getCoordinatesFromGridPoint(int gridPoint) {
		int dimCounter = 0;
		Point3d point = new Point3d(0, 0, 0);
		for (int z = 0; z < grid[0][0].length; z++) {
			for (int y = 0; y < grid[0].length; y++) {
				for (int x = 0; x < grid.length; x++) {
					if (dimCounter == gridPoint) {
						point.x = minx + latticeConstant * x;
						point.y = miny + latticeConstant * y;
						point.z = minz + latticeConstant * z;
						return point;
					}
					dimCounter++;
				}
			}
		}
		return point;
	}

	/**
	 * Method calculates the nearest grid point from given coordinates.
	 */
	public Point3d getGridPointFrom3dCoordinates(Point3d coord)
			throws Exception {
		Point3d gridPoint = new Point3d();

		if (coord.x >= minx & coord.x <= maxx) {
			gridPoint.x = (int) Math.round(Math.abs(minx - coord.x)
					/ latticeConstant);
		} else {
			throw new Exception(
					"CDKGridError: Given coordinates are not in grid");
		}
		if (coord.y >= miny & coord.y <= maxy) {
			gridPoint.y = (int) Math.round(Math.abs(miny - coord.y)
					/ latticeConstant);
		} else {
			throw new Exception(
					"CDKGridError: Given coordinates are not in grid");
		}
		if (coord.z >= minz & coord.z <= maxz) {
			gridPoint.z = (int) Math.round(Math.abs(minz - coord.z)
					/ latticeConstant);
		} else {
			throw new Exception(
					"CDKGridError: Given coordinates are not in grid");
		}

		return gridPoint;
	}

	/**
	 * Method transforms the grid into pmesh format.
	 */
	public void writeGridInPmeshFormat(String outPutFileName)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				outPutFileName + ".pmesh"));
		int numberOfGridPoints = grid.length * grid[0].length
				* grid[0][0].length;
		writer.write(numberOfGridPoints + "\n");
		for (int z = 0; z < grid[0][0].length; z++) {
			for (int y = 0; y < grid[0].length; y++) {
				for (int x = 0; x < grid.length; x++) {
					Point3d coords = getCoordinatesFromGridPoint(new Point3d(x,
							y, z));
					writer.write(coords.x + "\t" + coords.y + "\t" + coords.z
							+ "\n");
				}
			}
		}
		writer.close();
	}

	/**
	 * Method transforms the grid into pmesh format. Only grid points
	 * with specific value defined with cutoff are considered.
	 * <pre>
	 * cutoff <0, the values considered must be <=cutoff
	 * cutoff >0, the values considered must be >=cutoff
	 * </pre>
	 */
	public void writeGridInPmeshFormat(String outPutFileName, double cutOff)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				outPutFileName + ".pmesh"));
		boolean negative = false;
		if (cutOff < 0) {
			negative = true;
		} else {
			negative = false;
		}
		int numberOfGridPoints = 0;
		for (int z = 0; z < grid[0][0].length; z++) {
			for (int y = 0; y < grid[0].length; y++) {
				for (int x = 0; x < grid.length; x++) {
					if (negative) {
						if (grid[x][y][z] <= cutOff) {
							numberOfGridPoints++;
						}
					} else {
						if (grid[x][y][z] >= cutOff) {
							numberOfGridPoints++;
						}
					}
				}
			}
		}
		writer.write(numberOfGridPoints + "\n");
		for (int z = 0; z < grid[0][0].length; z++) {
			for (int y = 0; y < grid[0].length; y++) {
				for (int x = 0; x < grid.length; x++) {
					Point3d coords = getCoordinatesFromGridPoint(new Point3d(x,
							y, z));
					if (negative) {
						if (grid[x][y][z] <= cutOff) {
							writer.write(coords.x + "\t" + coords.y + "\t"
									+ coords.z + "\n");
						}
					} else {
						if (grid[x][y][z] >= cutOff) {
							writer.write(coords.x + "\t" + coords.y + "\t"
									+ coords.z + "\n");
						}
					}
				}
			}
		}
		writer.close();
	}

	public String toString() {
		return "Dim:" + dim + " SizeX:" + grid.length + " SizeY:"
				+ grid[0].length + " SizeZ:" + grid[0][0].length + "\nminx:"
				+ minx + " maxx:" + maxx + "\nminy:" + miny + " maxy:" + maxy
				+ "\nminz:" + minz + " maxz:" + maxz;
	}

	
	/**
	 * @return Returns the dim.
	 */
	public int[] getDim() {
		return dim;
	}

	
	/**
	 * @param dim The dim to set.
	 */
	public void setDim(int[] dim) {
		this.dim = dim;
	}

	
	/**
	 * @return Returns the extendGrid.
	 */
	public double getExtendGrid() {
		return extendGrid;
	}

	
	/**
	 * @param extendGrid The extendGrid to set.
	 */
	public void setExtendGrid(double extendGrid) {
		this.extendGrid = extendGrid;
	}

	
	/**
	 * @return Returns the grid.
	 */
	public double[][][] getGrid() {
		return grid;
	}

	
	/**
	 * @param grid The grid to set.
	 */
	public void setGrid(double[][][] grid) {
		this.grid = grid;
	}

	
	/**
	 * @return Returns the latticeConstant.
	 */
	public double getLatticeConstant() {
		return latticeConstant;
	}

	
	/**
	 * @param latticeConstant The latticeConstant to set.
	 */
	public void setLatticeConstant(double latticeConstant) {
		this.latticeConstant = latticeConstant;
	}

	
	/**
	 * @return Returns the gridArray.
	 */
	public double[] getGridArray() {
		return gridArray;
	}

	
	/**
	 * @return Returns the maxx.
	 */
	public double getMaxx() {
		return maxx;
	}

	
	/**
	 * @return Returns the maxy.
	 */
	public double getMaxy() {
		return maxy;
	}

	
	/**
	 * @return Returns the maxz.
	 */
	public double getMaxz() {
		return maxz;
	}

	
	/**
	 * @return Returns the minx.
	 */
	public double getMinx() {
		return minx;
	}

	
	/**
	 * @return Returns the miny.
	 */
	public double getMiny() {
		return miny;
	}

	
	/**
	 * @return Returns the minz.
	 */
	public double getMinz() {
		return minz;
	}

}
