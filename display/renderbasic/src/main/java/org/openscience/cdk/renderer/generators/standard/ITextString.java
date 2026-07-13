package org.openscience.cdk.renderer.generators.standard;

import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;


/**
 * 
 * This interface, for {@link TextShape}.TextString, allows for preserving the
 * text string for implementations such as JavaScript/SwingJS where we do not
 * have (or want!) access to font libraries for stroked fonts.
 * {@link GeneralPath} saves the TextString as a field, which then can be passed
 * to {@link AWTDrawVisitor} and {@link SVGDrawVisitor} in visit(GeneralPath)
 * 
 * It works in Java and JavaScript, but is implemented in {@link AWTDrawVisitor}
 * and {@link SVGDrawVisitor} only for JavaScript, so it could in principle be an
 * option in Java as well.
 * 
 * @author Bob Hanson
 *
 */
public interface ITextString {

    void setScale(AffineTransform transform);

    Point2D getTextPosition(double x, double y);

    String getText();

    Font getFont();

}
