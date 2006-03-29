/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2006  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
package org.openscience.cdk.renderer;

import java.awt.Color;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;
 
/**
 * 3D Renderer using Java3D.
 *
 * @cdk.module java3d
 * 
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.created 2001-07-20
 *
 * @cdk.keyword viewer, 3D-viewer
 */
public class AcceleratedRenderer3D extends Canvas3D
{
	//private Canvas3D canvas;
	private BranchGroup scene; 
  private SimpleUniverse u;
	private MouseRotate behavior;
  private MouseZoom behavior2;
  //private PickTranslateBehavior behavior3;
	private MouseTranslate behavior3;

	private TransformGroup root;

	private Background background;

	private AcceleratedRenderer3DModel model;

	public AcceleratedRenderer3D()
	{
		super(SimpleUniverse.getPreferredConfiguration());
	}

	public AcceleratedRenderer3D(AcceleratedRenderer3DModel model)
  {
		super(SimpleUniverse.getPreferredConfiguration()); 
		this.model = model;

		scene = createSceneGraph(model.getRoot());
    
    scene.compile();
    
    u = new SimpleUniverse(this);
    
    // This will move the ViewPlatform back a bit so the
    // objects in the scene can be viewed.
    u.getViewingPlatform().setNominalViewingTransform();
    
    // Add everthing to the scene graph - it will now be displayed.
    u.addBranchGraph(scene);
  }

	public void setBackground(Color color)
	{
		background.setColor(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f);
	}

	public BranchGroup createSceneGraph(TransformGroup modelroot)
  { // Create the root of the branch graph
    BranchGroup objRoot = new BranchGroup();
    
    // Create a Transformgroup to scale all objects so they
    // appear in the scene.
    TransformGroup objScale = new TransformGroup();
    Transform3D t3d = new Transform3D();
    t3d.setScale(0.4);
    objScale.setTransform(t3d);
    objRoot.addChild(objScale);

    // This Transformgroup is used by the mouse manipulators
    TransformGroup objTrans = new TransformGroup();
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    objScale.addChild(objTrans);

		objTrans.addChild(modelroot);
    
		//root = objTrans;
      
    BoundingSphere bounds =
      new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
      
    // Create the rotate behavior node
    behavior = new MouseRotate(objTrans);
    objTrans.addChild(behavior);
    behavior.setSchedulingBounds(bounds);
    behavior.setEnable(true);
    
    // Create the zoom behavior node
    behavior2 = new MouseZoom(objTrans);
    objTrans.addChild(behavior2);
    behavior2.setSchedulingBounds(bounds);
    behavior2.setEnable(true);

    /*behavior3 = new PickTranslateBehavior(objRoot, canvas, bounds,
                PickObject.USE_BOUNDS);
    objRoot.addChild(behavior3);
    behavior3.setEnable(true);*/
		behavior3 = new MouseTranslate(objTrans);
    objTrans.addChild(behavior3);
    behavior3.setSchedulingBounds(bounds);
    behavior3.setEnable(true);
    
    //Shine it with two colored lights.
    Color3f lColor1 = new Color3f(0.7f, 0.7f, 0.7f);
    Vector3f lDir1  = new Vector3f(-1.0f, -1.0f, -1.0f);
    DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
    lgt1.setInfluencingBounds(bounds);
    objScale.addChild(lgt1);

		//AmbientLight lgt2 = new AmbientLight(new Color3f(0.2f, 0.2f, 0.2f));
		Color3f lColor2 = new Color3f(0.7f, 0.7f, 0.7f);
    Vector3f lDir2  = new Vector3f(+1f, 0f, 0f);
    DirectionalLight lgt2 = new DirectionalLight(lColor2, lDir2);
		lgt2.setInfluencingBounds(bounds);
		objScale.addChild(lgt2);

		background = new Background(new Color3f(0f, 0f, 0f));
		background.setCapability(Background.ALLOW_COLOR_WRITE);
    background.setApplicationBounds(bounds);
    objRoot.addChild(background);
    
    // Let Java 3D perform optimizations on this scene graph.
    //objRoot.compile();
    
    return objRoot;
  }
}
