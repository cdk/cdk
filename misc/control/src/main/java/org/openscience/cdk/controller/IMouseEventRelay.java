/* Copyright (C) 2007  Niels Out <nielsout@users.sf.net>
 *               2011  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net or nout@science.uva.nl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.controller;

/**
 * Widget toolkit-independent interface to relay for mouse events.
 *
 * @cdk.module control
 */
public interface IMouseEventRelay {

    /**
     * Event to signal that the left mouse button has been released.
     *
     * @param screenCoordX the x part of the screen coordinate where the event happened.
     * @param screenCoordY the y part of the screen coordinate where the event happened.
     */
    public abstract void mouseClickedUp(int screenCoordX, int screenCoordY);

    /**
     * Event to signal that the left mouse button has been pushed but not released yet.
     *
     * @param screenCoordX the x part of the screen coordinate where the event happened.
     * @param screenCoordY the y part of the screen coordinate where the event happened.
     */
    public abstract void mouseClickedDown(int screenCoordX, int screenCoordY);

    /**
     * Event to signal that the right mouse button has been pushed but not released yet.
     *
     * @param screenCoordX the x part of the screen coordinate where the event happened.
     * @param screenCoordY the y part of the screen coordinate where the event happened.
     */
    public abstract void mouseClickedDownRight(int screenCoordX, int screenCoordY);

    /**
     * Event to signal that the right mouse button has been released.
     *
     * @param screenCoordX the x part of the screen coordinate where the event happened.
     * @param screenCoordY the y part of the screen coordinate where the event happened.
     */
    public abstract void mouseClickedUpRight(int screenCoordX, int screenCoordY);

    /**
     * Event to signal that a mouse button has been double clicked.
     *
     * @param screenCoordX the x part of the screen coordinate where the event happened.
     * @param screenCoordY the y part of the screen coordinate where the event happened.
     */
    public abstract void mouseClickedDouble(int screenCoordX, int screenCoordY);

    /**
     * Event to signal that a mouse has been moved to the new coordinates.
     *
     * @param screenCoordX the x part of the latest screen coordinate.
     * @param screenCoordY the y part of the latest screen coordinate.
     */
    public abstract void mouseMove(int screenCoordX, int screenCoordY);

    /**
     *
     * @param screenCoordX
     * @param screenCoordY
     */
    public abstract void mouseEnter(int screenCoordX, int screenCoordY);

    /**
     *
     * @param screenCoordX
     * @param screenCoordY
     */
    public abstract void mouseExit(int screenCoordX, int screenCoordY);

    /**
     * Event to signal that a mouse has been dragged from one point to
     * a next.
     *
     * @param screenCoordXFrom the x part of the screen coordinate dragged from.
     * @param screenCoordYFrom the y part of the screen coordinate dragged from.
     * @param screenCoordXTo   the x part of the screen coordinate dragged to.
     * @param screenCoordYTo   the y part of the screen coordinate dragged to.
     */
    public abstract void mouseDrag(int screenCoordXFrom, int screenCoordYFrom, int screenCoordXTo, int screenCoordYTo);

    /**
     * Event to signal that the mouse wheel has been rotated a certain amount forward.
     *
     * @param rotation an platform-specific amount of rotation of the wheel
     * @see #mouseWheelMovedBackward(int)
     */
    public abstract void mouseWheelMovedForward(int rotation);

    /**
     * Event to signal that the mouse wheel has been rotated a certain amount backward.
     *
     * @param rotation an platform-specific amount of rotation of the wheel
     * @see #mouseWheelMovedForward(int)
     */
    public abstract void mouseWheelMovedBackward(int rotation);

}
