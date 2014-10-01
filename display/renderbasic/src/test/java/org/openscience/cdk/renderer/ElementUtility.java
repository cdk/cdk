/* Copyright (C) 2010  Gilleain Torrance <gilleain.torrance@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer;

import java.awt.geom.AffineTransform;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;

/**
 * Utility class for testing.
 *
 * @author     maclean
 * @cdk.module test-renderbasic
 */
public class ElementUtility implements IDrawVisitor {

    private List<IRenderingElement> elements         = new ArrayList<IRenderingElement>();

    private AffineTransform         transform;

    private RendererModel           model;

    private boolean                 getElementGroups = false;

    public int numberOfElements() {
        return this.elements.size();
    }

    @Override
    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }

    @Override
    public void visit(IRenderingElement element) {
        if (element instanceof ElementGroup) {
            if (getElementGroups) {
                this.elements.add(element);
            }
            ((ElementGroup) element).visitChildren(this);
        } else {
            this.elements.add(element);
        }
    }

    public List<IRenderingElement> getElements() {
        return this.elements;
    }

    public List<IRenderingElement> getAllSimpleElements(IRenderingElement root) {
        elements.clear();
        getElementGroups = false;
        root.accept(this);
        return new ArrayList<IRenderingElement>(elements);
    }

    public int[] transformPoint(double x, double y) {
        double[] src = new double[]{x, y};
        double[] dest = new double[2];
        this.transform.transform(src, 0, dest, 0, 1);
        return new int[]{(int) dest[0], (int) dest[1]};
    }

    @Override
    public void setFontManager(IFontManager fontManager) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRendererModel(RendererModel rendererModel) {
        this.model = rendererModel;
    }

    public RendererModel getModel() {
        return this.model;
    }

    public String toString(int[] p) {
        return String.format("(%d, %d)", p[0], p[1]);
    }

    public String toString(double x, double y) {
        return String.format("(%+3.1f, %+3.1f)", x, y);
    }

    public String toString(double x, double y, double r) {
        return String.format("(%+3.1f, %+3.1f, %+3.1f)", x, y, r);
    }

    public String toString(IRenderingElement element) {
        if (element instanceof LineElement) {
            LineElement e = (LineElement) element;
            String p1 = toString(e.firstPointX, e.firstPointY);
            String p2 = toString(e.secondPointX, e.secondPointY);
            String p1T = toString(transformPoint(e.firstPointX, e.firstPointY));
            String p2T = toString(transformPoint(e.secondPointX, e.secondPointY));
            String lineFormat = "Line [%s, %s] -> [%s, %s]\n";
            return String.format(lineFormat, p1, p2, p1T, p2T);
        } else if (element instanceof OvalElement) {
            OvalElement e = (OvalElement) element;
            double r = e.radius;
            String c = toString(e.xCoord, e.yCoord, r);
            String p1 = toString(transformPoint(e.xCoord - r, e.yCoord - r));
            String p2 = toString(transformPoint(e.xCoord + r, e.yCoord + r));
            return String.format("Oval [%s] -> [%s, %s]\n", c, p1, p2);
        } else if (element instanceof AtomSymbolElement) {
            AtomSymbolElement e = (AtomSymbolElement) element;
            return String.format("AtomSymbol [%s]\n", e.text);
        } else if (element instanceof ElementGroup) {
            return "Element Group\n";
        } else {
            return "Unknown element\n";
        }
    }

    public void printToStream(IRenderingElement root, PrintStream stream) {
        root.accept(this);
        for (IRenderingElement element : this.elements) {
            stream.print(toString(element));
        }
    }

}
