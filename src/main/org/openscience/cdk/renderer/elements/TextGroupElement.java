/* Copyright (C) 2009  Gilleain Torrance <gilleain@users.sf.net>
 *
 * Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.renderer.elements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @cdk.module renderbasic
 */
public class TextGroupElement extends TextElement {
    
    public enum Position { NW, SW, SE, NE, S, N, W, E }; 
    
    public class Child {
        
        public final String text;
        
        public final String subscript;
        
        public final Position position;
        
        public Child(String text, Position position) {
            this.text = text;
            this.position = position;
            this.subscript = null;
        }
        
        public Child(String text, String subscript, Position position) {
            this.text = text;
            this.position = position;
            this.subscript = subscript;
        }
        
    }

    public final List<Child> children;
    
    public TextGroupElement(double x, double y, String text, Color color) {
        super(x, y, text, color);
        this.children = new ArrayList<Child>();
    }
    
    public void addChild(String text, Position position) {
        this.children.add(new Child(text, position));
    }
    
    public void addChild(String text, String subscript, Position position) {
        this.children.add(new Child(text, subscript, position));
    }

    public void accept(IRenderingVisitor v) {
        v.visit(this);
    }

}
