/*  */

package org.openscience.cdk.renderer.font;

public class GlyphMetrics {

    public int xMin, xMax, yMin, yMax, adv;
    public String outline;
    public GlyphMetrics (int a, int b, int c, int d, int e, String s) {
        xMin=a; xMax=b; yMin=c; yMax=d; adv=e; outline=s;
    }

}
