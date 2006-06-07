/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2006  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications.jchempaint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;
import javax.vecmath.Point2d;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.applications.jchempaint.action.JCPAction;
import org.openscience.cdk.applications.jchempaint.action.SaveAction;
import org.openscience.cdk.applications.jchempaint.dialogs.CreateCoordinatesForFileDialog;
import org.openscience.cdk.applications.plugin.ICDKEditBus;
import org.openscience.cdk.applications.undoredo.ClearAllEdit;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.listener.SwingGUIListener;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
/**
 *  JPanel that contains a full JChemPaint program, either viewer or full
 *  editor.
 *
 *@author        steinbeck
 *@cdk.created       16. Februar 2005
 *@cdk.module    jchempaint
 */
public abstract class JChemPaintPanel
		 extends JPanel
		 implements ChangeListener, ICDKEditBus {

	//Static variables hold information if the application is embedded and keep track of instances of JCPPanel
	boolean isEmbedded = false;
	boolean isOpenedByViewer	= false;
	boolean isViewerOnly = false;
	static Vector instances = new Vector();
	/**
	 *  Description of the Field
	 */
	protected JChemPaintModel jchemPaintModel;
	private LoggingTool logger;
	private File currentWorkDirectory = null;
	private File lastOpenedFile = null;
	private File lastSavedFile = null;
	private FileFilter currentOpenFileFilter = null;
	private FileFilter currentSaveFileFilter = null;
	/**
	 *  Description of the Field
	 */
	JPanel mainContainer;
	StatusBar statusBar;
	JChemPaintMenuBar menu;
	JToolBar toolBar;
	DrawingPanel drawingPanel;
	/**
	 *  Description of the Field
	 */
	public JButton selectButton;
	JCPAction jcpaction = null;
	/**
	 *  Description of the Field
	 */
	protected File isAlreadyAFile = null;
	/**
	 *  this is only needed in open action immediately after opening a file
	 */
	public JChemPaintPanel lastUsedJCPP = null;
	/**
	 *  remembers last action in toolbar for switching on/off buttons
	 *  This is a vector containing only one element, the button last used
	 */
	public Vector lastAction=new Vector();
    Dimension viewerDimension;
    //private UndoManager undoManager;
    //private UndoableEditSupport undoSupport;
    String guiString = "stable";
    //we remember the moveButton since this is special
    protected JButton moveButton=null;
    private JScrollPane scrollPane;
    
	/**
	 *  Constructor for the JChemPaintPanel object
	 */
	public JChemPaintPanel() {
		logger = new LoggingTool(this);
        
//        undoManager = new UndoManager();
//        undoManager.setLimit(10);
//        undoSupport = new UndoableEditSupport();
//        undoSupport.addUndoableEditListener(new UndoAdapter(undoManager));
        
		setLayout(new BorderLayout());
		mainContainer = new JPanel();
		mainContainer.setLayout(new BorderLayout());
		drawingPanel = new DrawingPanel();
		drawingPanel.setOpaque(true);
		drawingPanel.setBackground(Color.white);
		scrollPane = new JScrollPane(drawingPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		mainContainer.add(scrollPane, BorderLayout.CENTER);

		add(mainContainer, BorderLayout.CENTER);
		setSize(new Dimension(900, 400));
		setPreferredSize(new Dimension(900, 400));
        instances.add(this);
	}


	/**
	 *  Return the JCPAction instance associated with this JCPPanel
	 *
	 *@return    The jCPAction value
	 */
	public JCPAction getJCPAction() {
		if (jcpaction == null) {
			jcpaction = new JCPAction();
		}
		return jcpaction;
	}


	/**
	 *  Tells if this JCPPanel is part of an embedded program or not.
	 *
	 *@return    The embedded value
	 */
	public boolean isEmbedded() {
		return isEmbedded;
	}


	/**
	 *  Gets the viewerOnly attribute of the JChemPaintPanel object
	 *
	 *@return    The viewerOnly value
	 */
	public boolean isViewerOnly() {
		return isViewerOnly;
	}


	/**
	 *  Sets JCP as embedded application
	 */
	public void setEmbedded() {
		isEmbedded = true;
	}
	
	/**
	 * Returns the value of isOpenedByViewer.
	 */
	public boolean getIsOpenedByViewer()
	{
		return isOpenedByViewer;
	}

	/**
	 * Sets the value of isOpenedByViewer.
	 * @param isOpenedByViewer The value to assign isOpenedByViewer.
	 */
	public void setIsOpenedByViewer(boolean isOpenedByViewer)
	{
		this.isOpenedByViewer = isOpenedByViewer;
	}
	
	
	/**
	 *  Sets the viewerOnly attribute of the JChemPaintPanel object
	 */
	public void setViewerOnly() {
		isEmbedded = true;
		isViewerOnly = true;
	}


	/**
	 *  Sets JCP as standalone-program
	 */
	public void setNotEmbedded() {
		isEmbedded = false;
	}


	/**
	 *  Returns a vector containing all JFrames containing JCPPanels currently
	 *  running
	 *
	 *@return    Vector of JFrames
	 */
	public Vector getInstances() {
		return instances;
	}


	/**
	 *  Sets the file currently used for saving this Panel.
	 *
	 *@param  value  The new isAlreadyAFile value
	 */
	public void setIsAlreadyAFile(File value) {
		isAlreadyAFile = value;
	}


	/**
	 *  Returns the file currently used for saving this Panel, null if not yet
	 *  saved
	 *
	 *@return    The currently used file
	 */
	public File isAlreadyAFile() {
		return isAlreadyAFile;
	}


	/**
	 *  Creates a new localized string that can be used as a title for the new
	 *  frame.
	 *
	 *@return    The newFrameName value
	 */
	public static String getNewFrameName() {
		return JCPLocalizationHandler.getInstance().getString("Untitled-") + Integer.toString(instances.size() + 1);
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Image takeSnapshot() {
		return null;
	}


	/**
	 *  Gets the currentWorkDirectory attribute of the JChemPaintPanel object
	 *
	 *@return    The currentWorkDirectory value
	 */
	public File getCurrentWorkDirectory() {
		return currentWorkDirectory;
	}


	/**
	 *  Sets the currentWorkDirectory attribute of the JChemPaintPanel object
	 *
	 *@param  cwd  The new currentWorkDirectory value
	 */
	public void setCurrentWorkDirectory(File cwd) {
		this.currentWorkDirectory = cwd;
	}


	/**
	 *  Gets the currentOpenFileFilter attribute of the JChemPaintPanel object
	 *
	 *@return    The currentOpenFileFilter value
	 */
	public FileFilter getCurrentOpenFileFilter() {
		return currentOpenFileFilter;
	}


	/**
	 *  Sets the currentOpenFileFilter attribute of the JChemPaintPanel object
	 *
	 *@param  ff  The new currentOpenFileFilter value
	 */
	public void setCurrentOpenFileFilter(FileFilter ff) {
		this.currentOpenFileFilter = ff;
	}


	/**
	 *  Gets the currentSaveFileFilter attribute of the JChemPaintPanel object
	 *
	 *@return    The currentSaveFileFilter value
	 */
	public FileFilter getCurrentSaveFileFilter() {
		return currentSaveFileFilter;
	}


	/**
	 *  Sets the currentSaveFileFilter attribute of the JChemPaintPanel object
	 *
	 *@param  ff  The new currentSaveFileFilter value
	 */
	public void setCurrentSaveFileFilter(FileFilter ff) {
		this.currentSaveFileFilter = ff;
	}


	/**
	 *  Gets the lastOpenedFile attribute of the JChemPaintPanel object
	 *
	 *@return    The lastOpenedFile value
	 */
	public File getLastOpenedFile() {
		return lastOpenedFile;
	}


	/**
	 *  Sets the lastOpenedFile attribute of the JChemPaintPanel object
	 *
	 *@param  lof  The new lastOpenedFile value
	 */
	public void setLastOpenedFile(File lof) {
		this.lastOpenedFile = lof;
	}


	/**
	 *  Gets the lastSavedFile attribute of the JChemPaintPanel object
	 *
	 *@return    The lastSavedFile value
	 */
	public File getLastSavedFile() {
		return lastSavedFile;
	}


	/**
	 *  Sets the lastSavedFile attribute of the JChemPaintPanel object
	 *
	 *@param  lsf  The new lastSavedFile value
	 */
	public void setLastSavedFile(File lsf) {
		this.lastSavedFile = lsf;
	}


	/**
	 *  Description of the Method
	 */
//	private void setupWorkingDirectory() {
//		try {
//			if (System.getProperty("user.dir") != null) {
//				setCurrentWorkDirectory(new File(System.getProperty("user.dir")));
//			}
//		} catch (Exception exc) {
//			logger.error("Could not read a system property. I might be in a sandbox.");
//		}
//	}


	/**
	 *  Sets the jChemPaintModel attribute of the JChemPaintPanel object
	 *
	 *@param  model  The new jChemPaintModel value
	 */
	public void setJChemPaintModel(JChemPaintModel model) {
		lastUsedJCPP = this;
		if (model != null && jchemPaintModel != null && model.getChemModel().getSetOfMolecules() != null) {
			model.getRendererModel().setBackgroundDimension(jchemPaintModel.getRendererModel().getBackgroundDimension());
			Dimension molDim = GeometryTools.get2DDimension(model.getChemModel().getSetOfMolecules().getAtomContainer(0));
			if (isViewerOnly) {
				Dimension viewerDim = null;
				//for some reason an EditorPanel opened by a ViewerPanel gets thet isViewerOnly flag set to true -- to be solved!!
				try {
					viewerDim = getViewerDimension();
				}
				catch (ClassCastException cce) {}
				if (viewerDim != null) {
					//sets BackgroundDim to default dim if using ViewerOnlyPanel
					model.getRendererModel().setBackgroundDimension(viewerDim);
				}
			}
			Dimension backDim = model.getRendererModel().getBackgroundDimension();
			int height = (int) backDim.getHeight();
			int width = (int) backDim.getWidth();
			if (molDim.getHeight() >= backDim.getHeight()) {
				height = (int) molDim.getHeight() + 10;
			}
			else if (molDim.getWidth() >= backDim.getWidth()) {
				width = (int) molDim.getWidth() + 10;
			}
			model.getRendererModel().setBackgroundDimension(new Dimension(width, height));
		}
		else if (model.getChemModel().getSetOfMolecules() == null && isViewerOnly) {
			Dimension viewerDim = null;
			try {
				viewerDim = getViewerDimension();
			}
			catch (ClassCastException cce) {}
			if (viewerDim != null) {
				//sets BackgroundDim to default dim if using ViewerOnlyPanel
				model.getRendererModel().setBackgroundDimension(viewerDim);
				}
		}
		this.jchemPaintModel = model;
		jchemPaintModel.addChangeListener(this);
		org.openscience.cdk.interfaces.IChemModel chemModel = model.getChemModel();
		scaleAndCenterMolecule(chemModel);
		drawingPanel.setJChemPaintModel(model);
	}


	/**
	 *  Partitions a given String into separate words and writes them into an
	 *  array.
	 *
	 *@param  input  String The String to be cutted into pieces
	 *@return        String[] The array containing the separate words
	 */
	public String[] tokenize(String input) {
		Vector v = new Vector();
		StringTokenizer t = new StringTokenizer(input);
		String cmd[];
		while (t.hasMoreTokens()) {
			v.addElement(t.nextToken());
		}
		cmd = new String[v.size()];
		for (int i = 0; i < cmd.length; i++) {
			cmd[i] = (String) v.elementAt(i);
		}
		return cmd;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  file  Description of the Parameter
	 */
	public void showChemFile(Reader file) {
		IChemObjectReader cor = null;

		/*
		 *  Have the ReaderFactory determine the file format
		 */
		try {
			cor = getChemObjectReader(file);
		} catch (IOException ioExc) {
			logger.warn("IOException while determining file format.");
			logger.debug(ioExc);
		} catch (Exception exc) {
			logger.warn("Exception while determining file format.");
			logger.debug(exc);
		}

		if (cor == null) {
			JOptionPane.showMessageDialog(this, "Could not determine file format.");
			return;
		}

		String error = null;
		ChemFile chemFile = null;
		ChemModel chemModel = null;
		if (cor.accepts(ChemFile.class)) {
			// try to read a ChemFile
			try {
				chemFile = (ChemFile) cor.read(new ChemFile());
				if (chemFile != null) {
					processChemFile(chemFile);
					return;
				} else {
					logger.warn("The object chemFile was empty unexpectedly!");
				}
			} catch (Exception exception) {
				error = "Error while reading file: " + exception.getMessage();
				logger.warn(error);
				logger.debug(exception);
			}
		}
		if (error != null) {
			JOptionPane.showMessageDialog(this, error);
			return;
		}
		if (cor.accepts(ChemModel.class)) {
			// try to read a ChemModel
			try {
				chemModel = (ChemModel) cor.read((ChemObject) new ChemModel());
				if (chemModel != null) {
					processChemModel(chemModel);
					return;
				} else {
					logger.warn("The object chemModel was empty unexpectedly!");
				}
				error = null;
				// overwrite previous problems, it worked now
			} catch (Exception exception) {
				error = "Error while reading file: " + exception.getMessage();
				logger.error(error);
				logger.debug(exception);
			}
		}
		if (error != null) {
			JOptionPane.showMessageDialog(this, error);
		}
	}


	/**
	 *  Gets the jChemPaintModel attribute of the JChemPaintPanel object
	 *
	 *@return    The jChemPaintModel value
	 */
	public JChemPaintModel getJChemPaintModel() {
		return jchemPaintModel;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public int showWarning() {
		if (jchemPaintModel.isModified() && !getIsOpenedByViewer() && !guiString.equals("applet")) {
			int answer = JOptionPane.showConfirmDialog(this, jchemPaintModel.getTitle() + " " + JCPLocalizationHandler.getInstance().getString("warning"), JCPLocalizationHandler.getInstance().getString("warningheader"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (answer == JOptionPane.YES_OPTION) {
				new SaveAction(this, false).actionPerformed(new ActionEvent(this, 12, ""));
			}
			return answer;
		} else if(guiString.equals("applet")){
			//In case of the applet we do not ask for save but put the clear into the undo stack
			ClearAllEdit coa = null;
			try {
				coa = new ClearAllEdit(this.getChemModel(),(ISetOfMolecules)this.getChemModel().getSetOfMolecules().clone(),this.getChemModel().getSetOfReactions());
				this.jchemPaintModel.getControllerModel().getUndoSupport().postEdit(coa);
			} catch (CloneNotSupportedException e) {
				logger.error("Clone of ISetOfMolecules failed: ", e.getMessage());
            	logger.debug(e);
			}
			return JOptionPane.YES_OPTION;
		} else {
			return JOptionPane.YES_OPTION;
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  chemFile  Description of the Parameter
	 */
	public void processChemFile(org.openscience.cdk.interfaces.IChemFile chemFile) {
		logger.info("Information read from file:");

		int chemSequenceCount = chemFile.getChemSequenceCount();
		logger.info("  # sequences: ", chemSequenceCount);

		for (int i = 0; i < chemSequenceCount; i++) {
			org.openscience.cdk.interfaces.IChemSequence chemSequence = chemFile.getChemSequence(i);

			int chemModelCount = chemSequence.getChemModelCount();
			logger.info("  # model in seq(" + i + "): ", chemModelCount);

			for (int j = 0; j < chemModelCount; j++) {
				org.openscience.cdk.interfaces.IChemModel chemModel = chemSequence.getChemModel(j);
				processChemModel(chemModel);
			}
		}
	}


	/**
	 *  Scales and centers the structure in the dimensions of the DrawingPanel.
	 *
	 *@param  chemModel  The cheModel of the structure to be scaled and centered.
	 */
	public void scaleAndCenterMolecule(org.openscience.cdk.interfaces.IChemModel chemModel) {
    scaleAndCenterMolecule(chemModel, null);
  }

	public void scaleAndCenterMolecule(org.openscience.cdk.interfaces.IChemModel chemModel, Dimension dim) {
		IAtomContainer ac = ChemModelManipulator.getAllInOneContainer(chemModel);
		scaleAndCenterMolecule(ac,dim);
	}
	
	/**
	 *  Scales and centers the structure in the dimensions of the DrawingPanel.
	 *
	 *@param  chemModel  The cheModel of the structure to be scaled and centered.
	 */
	public void scaleAndCenterMolecule(IAtomContainer ac, Dimension dim){
		((JViewport) drawingPanel.getParent()).setViewPosition(new Point((drawingPanel.getWidth()-getWidth())/2>0 ? (drawingPanel.getWidth()-getWidth())/2 : 0 ,(drawingPanel.getHeight()-getHeight())/2>0 ? (drawingPanel.getHeight()-getHeight())/2 : 0));
	    JChemPaintModel jcpm = getJChemPaintModel();
	    Renderer2DModel rendererModel = jcpm.getRendererModel();
	    org.openscience.cdk.interfaces.IAtom[] atoms = ac.getAtoms();
	    double scaleFactor = GeometryTools.getScaleFactor(ac, rendererModel.getBondLength(),jchemPaintModel.getRendererModel().getRenderingCoordinates());
	    GeometryTools.scaleMolecule(ac, scaleFactor, jchemPaintModel.getRendererModel().getRenderingCoordinates());
	    Rectangle view = ((JViewport) drawingPanel.getParent()).getViewRect();
	    double x = view.getX() + view.getWidth();
	    double y = view.getY() + view.getHeight();
	    Renderer2DModel model = jchemPaintModel.getRendererModel();
	    double relocatedY = model.getBackgroundDimension().getSize().getHeight() - (y + view.getY() / 2);
	    double relocatedX = view.getX() / 2;
	    Dimension viewablePart = new Dimension((int) x, (int) y);
	   //GeometryTools.center(ac, viewablePart);
	    //to be fixed - check if molDim is reaching over viewablePart borders...
	    if (this instanceof JChemPaintViewerOnlyPanel) {
	    	GeometryTools.center(ac, model.getBackgroundDimension(),jchemPaintModel.getRendererModel().getRenderingCoordinates());
	        relocatedX=0;
	        relocatedY=0;
	    } else {
	        if(dim==null){
	            if(viewablePart.getWidth()==0 && viewablePart.getHeight()==0){
	                relocatedX=0;
	                relocatedY=0;
	                GeometryTools.center(ac, model.getBackgroundDimension(),jchemPaintModel.getRendererModel().getRenderingCoordinates());
	            }else{
	                 GeometryTools.center(ac, viewablePart,jchemPaintModel.getRendererModel().getRenderingCoordinates());
	            }
	        }else{
	            relocatedY = model.getBackgroundDimension().getSize().getHeight() - (dim.getHeight());
	            relocatedX = 0;
	            GeometryTools.center(ac, dim,jchemPaintModel.getRendererModel().getRenderingCoordinates());
	        }
	    }
	    //fixing the coords regarding the position of the viewablePart
	    for (int i = 0; i < atoms.length; i++) {
	        if (jchemPaintModel.getRendererModel().getRenderingCoordinate(atoms[i]) != null) {
	        	jchemPaintModel.getRendererModel().setRenderingCoordinate(atoms[i],new Point2d(((Point2d)jchemPaintModel.getRendererModel().getRenderingCoordinate(atoms[i])).x + relocatedX,((Point2d)jchemPaintModel.getRendererModel().getRenderingCoordinate(atoms[i])).y + relocatedY));
	        }
	    }
	}


	/**
	 *  Description of the Method
	 *
	 *@param  chemModel  Description of the Parameter
	 */
	public void processChemModel(org.openscience.cdk.interfaces.IChemModel chemModel) {
		// check for bonds
		if (ChemModelManipulator.getAllInOneContainer(chemModel).getBondCount() == 0) {
			String error = "Model does not have bonds. Cannot depict contents.";
			logger.warn(error);
			JOptionPane.showMessageDialog(this, error);
			return;
		}

		// check for coordinates
		if ((GeometryTools.has2DCoordinatesNew(ChemModelManipulator.getAllInOneContainer(chemModel))==0)) {
			String error = "Model does not have 2D coordinates. Cannot open file.";
			logger.warn(error);
			JOptionPane.showMessageDialog(this, error);
			CreateCoordinatesForFileDialog frame = new CreateCoordinatesForFileDialog(chemModel);
			frame.pack();
			frame.show();
			return;
		} else if ((GeometryTools.has2DCoordinatesNew(ChemModelManipulator.getAllInOneContainer(chemModel))==0)) {
			int result=JOptionPane.showConfirmDialog(this,"Model has some 2d coordinates. Do you want to show only the atoms with 2d coordiantes?","Only some 2d cooridantes",JOptionPane.YES_NO_OPTION);
			if(result>1){
				CreateCoordinatesForFileDialog frame = new CreateCoordinatesForFileDialog(chemModel);
				frame.pack();
				frame.show();
				return;
			}else{
				for(int i=0;i<chemModel.getSetOfMolecules().getAtomContainerCount();i++){
					for(int k=0;i<chemModel.getSetOfMolecules().getAtomContainers()[i].getAtomCount();k++){
						if(chemModel.getSetOfMolecules().getAtomContainers()[i].getAtomAt(k).getPoint2d()==null)
							chemModel.getSetOfMolecules().getAtomContainers()[i].removeAtomAndConnectedElectronContainers(chemModel.getSetOfMolecules().getAtomContainers()[i].getAtomAt(k));
					}						
				}
			}
		}

		JChemPaintModel jcpm = new JChemPaintModel(chemModel);
		lastUsedJCPP = this;
		if (isEmbedded()) {
			if (showWarning() != JOptionPane.CANCEL_OPTION) {
				registerModel(jcpm);
				setJChemPaintModel(jcpm);
				repaint();
			}
		} else if (getJChemPaintModel().getChemModel().getSetOfMolecules() == null || getJChemPaintModel().getChemModel().getSetOfMolecules().getMolecule(0).getAtoms().length == 0) {
			registerModel(jcpm);
			setJChemPaintModel(jcpm);
			repaint();
		} else {
			JFrame jcpf = JChemPaintEditorPanel.getNewFrame(jcpm);
			jcpf.show();
			scaleAndCenterMolecule(chemModel);
			jcpf.pack();
			lastUsedJCPP = (JChemPaintPanel) jcpf.getContentPane().getComponents()[0];
		}
	}


	/**
	 *  Gets the chemObjectReader attribute of the JChemPaintPanel object
	 *
	 *@param  reader           Description of the Parameter
	 *@return                  The chemObjectReader value
	 *@exception  IOException  Description of the Exception
	 */
	public IChemObjectReader getChemObjectReader(Reader reader) throws IOException {
		ReaderFactory factory = new ReaderFactory();
		IChemObjectReader coReader = factory.createReader(reader);
		if (coReader != null) {
			coReader.addChemObjectIOListener(new SwingGUIListener(this, 4));
		}
		return coReader;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  chemFile  Description of the Parameter
	 */
	public void showChemFile(org.openscience.cdk.interfaces.IChemFile chemFile) {
		logger.info("Information read from file:");

		int chemSequenceCount = chemFile.getChemSequenceCount();
		logger.info("  # sequences: " + chemSequenceCount);

		for (int i = 0; i < chemSequenceCount; i++) {
			org.openscience.cdk.interfaces.IChemSequence chemSequence = chemFile.getChemSequence(i);

			int chemModelCount = chemSequence.getChemModelCount();
			logger.info("  # model in seq(" + i + "): " + chemModelCount);

			for (int j = 0; j < chemModelCount; j++) {
				org.openscience.cdk.interfaces.IChemModel chemModel = chemSequence.getChemModel(j);
				showChemModel(chemModel);
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  chemModel  Description of the Parameter
	 */
	public void showChemModel(org.openscience.cdk.interfaces.IChemModel chemModel) {
		// check for bonds
		if (ChemModelManipulator.getAllInOneContainer(chemModel).getBondCount() == 0) {
			String error = "Model does not have bonds. Cannot depict contents.";
			logger.warn(error);
			JOptionPane.showMessageDialog(this, error);
			return;
		}

		// check for coordinates
		if (!(GeometryTools.has2DCoordinatesNew(ChemModelManipulator.getAllInOneContainer(chemModel))==0)) {

			String error = "Model does not have coordinates. Will ask for coord generation.";
			logger.warn(error);

			CreateCoordinatesForFileDialog frame = new CreateCoordinatesForFileDialog((ChemModel)chemModel);
			frame.pack();
			frame.show();
			frame.moveToFront();
			return;
		} else if ((GeometryTools.has2DCoordinatesNew(ChemModelManipulator.getAllInOneContainer(chemModel))==0)) {
			int result=JOptionPane.showConfirmDialog(this,"Model has some 2d coordinates. Do you want to show only the atoms with 2d coordiantes?","Only some 2d cooridantes",JOptionPane.YES_NO_OPTION);
			if(result>1){
				CreateCoordinatesForFileDialog frame = new CreateCoordinatesForFileDialog(chemModel);
				frame.pack();
				frame.show();
				return;
			}else{
				for(int i=0;i<chemModel.getSetOfMolecules().getAtomContainerCount();i++){
					for(int k=0;i<chemModel.getSetOfMolecules().getAtomContainers()[i].getAtomCount();k++){
						if(chemModel.getSetOfMolecules().getAtomContainers()[i].getAtomAt(k).getPoint2d()==null)
							chemModel.getSetOfMolecules().getAtomContainers()[i].removeAtomAndConnectedElectronContainers(chemModel.getSetOfMolecules().getAtomContainers()[i].getAtomAt(k));
					}						
				}
			}
		}

		setJChemPaintModel(new JChemPaintModel((ChemModel)chemModel));
	}


	/**
	 *  Gets the chemModel attribute of the JChemPaint object. This method
	 *  implements part of the CDKEditBus interface.
	 *
	 *@return    The chemModel value
	 */
	public org.openscience.cdk.interfaces.IChemModel getChemModel() {
		return jchemPaintModel.getChemModel();
	}


	/**
	 *  Gets the chemFile attribute of the JChemPaint object. This method
	 *  implements part of the CDKEditBus interface.
	 *
	 *@return    The chemFile value
	 */
	public org.openscience.cdk.interfaces.IChemFile getChemFile() {
		ChemFile file = new ChemFile();
		ChemSequence sequence = new ChemSequence();
		sequence.addChemModel(getChemModel());
		file.addChemSequence(sequence);
		return file;
	}


	/**
	 *  Creates a JMenu which can be part of the menu of an application embedding
	 *  jcp.
	 *
	 *@return    The created JMenu
	 */
	public JMenu getMenuForEmbedded() {
		return (menu.getMenuForEmbedded(this));
	}


	/**
	 *  Gets the drawingPanel attribute of the JChemPaintPanel object
	 *
	 *@return    The drawingPanel value
	 */
	public JPanel getDrawingPanel() {
		return drawingPanel;
	}
	
	
	public String getMenuResourceString(String key) {
		String str;
		try {
			str = JCPPropertyHandler.getInstance().getGUIDefinition().getString(key);
		} catch (MissingResourceException mre) {
			str = null;
		}
		return str;
	}
	
	/**
	 * adds a popupmenu for embedded use of jcp and a mouseListener which lets 
	 * pop up a floating jcp-frame with the actual model
	 * 
	 */
	public void addFilePopUpMenu() {
		if(guiString.equals("applet"))
			return;
	    String key = "popupmenubar";
		String[] itemKeys = StringHelper.tokenize(getMenuResourceString(key));
		JPopupMenu popupMenu = new JPopupMenu();
		for (int i = 0; i < itemKeys.length; i++) {
			String cmd = itemKeys[i];
			if (cmd.equals("-")) {
				popupMenu.addSeparator();
				continue;
			}
			String translation = "***" + cmd + "***";
			try {
				translation = JCPLocalizationHandler.getInstance().getString(cmd);
			} catch (MissingResourceException mre) {
			}
			JMenuItem mi = new JMenuItem(translation);
			String astr = JCPPropertyHandler.getInstance().getResourceString(cmd + JCPAction.actionSuffix);
			if (astr == null) {
				astr = cmd;
			}
			mi.setActionCommand(astr);
			JCPAction action = this.getJCPAction().getAction(this, astr);
			if (action != null) {
				// sync some action properties with menu
				mi.setEnabled(action.isEnabled());
				mi.addActionListener(action);
			} else {
				logger.error("Could not find JCPAction class for:" + astr);
				mi.setEnabled(false);
			}
			popupMenu.add(mi);
		}
		getDrawingPanel().add(popupMenu);
		MouseListener popupListener = new PopupListener(this, popupMenu);
		getDrawingPanel().addMouseListener(popupListener);
	}


    /**
     *  For showing the emdedded context menu and 
     *  sync the JChemPaintModels
     *
     *@author     thelmus
     *@cdk.created    18. Mai 2005
     */
    class PopupListener extends MouseAdapter {
    
    JPopupMenu popupMenu;
    JChemPaintPanel panel;
    Container parent;
    
    public PopupListener(JChemPaintPanel panel, JPopupMenu popupMenu){
      this.popupMenu = popupMenu;
      this.panel = panel;
    }
    
    	/**
    	 *  Description of the Method
    	 *
    	 *@param  e  Description of the Parameter
    	 */
    	public void mousePressed(MouseEvent e) {
    		maybeShowPopup(e);
    	}
    
    
    	/**
    	 *  Description of the Method
    	 *
    	 *@param  e  Description of the Parameter
    	 */
    	public void mouseReleased(MouseEvent e) {
    		//maybeShowPopup(e);
    	}
    
    
    	/**
    	 *  Description of the Method
    	 *
    	 *@param  e  Description of the Parameter
    	 */
    	private void maybeShowPopup(MouseEvent e) {
    	    if (e.isPopupTrigger()) {
    			popupMenu.show(e.getComponent(),
    					e.getX(), e.getY());
    		} else {
    		    JFrame frame = null;
    		    if (e.getButton() == 1 && e.getClickCount() == 2) {
    		        if (panel instanceof JChemPaintViewerOnlyPanel) {
	    		        frame = new JFrame();
	    		        frame.addWindowListener(
	    		                new WindowAdapter() {
	    		                    public void windowClosing(WindowEvent e) {
	    		                        parent.add(panel);
	    		                        parent.repaint();
	    		                    }
	    		                });
	    		        parent=panel.getParent();
	    		        panel.getParent().remove(panel);
	    		        frame.getContentPane().add(panel);
	    		       
    		        }
    		        else if (panel instanceof JChemPaintEditorPanel) {
    		          panel = (JChemPaintEditorPanel) panel;
    		          ChemModel model = (ChemModel)panel.getChemModel();
    		          frame =JChemPaintEditorPanel.getNewFrame(new JChemPaintModel(model));
    		          JChemPaintEditorPanel newPanel = (JChemPaintEditorPanel) frame.getContentPane().getComponent(0);
    		          newPanel.scaleAndCenterMolecule(model);
    		          newPanel.addChangeListener(panel);
    		          newPanel.setEmbedded();
    		          newPanel.setIsOpenedByViewer(true);
    		          JViewport viewPort =((JScrollPane) ((Container) panel.getComponent(0)).getComponent(0)).getViewport();
    		          DrawingPanel draw = (DrawingPanel) (viewPort.getView());
    		          panel.drawingPanel = draw;
    		          viewPort.remove(draw);
    		        }
    		        frame.show();
    		        frame.pack();
    		    }
    		}
    	}
    }


	/**
	 *  Class for closing jcp
	 *
	 *@author     steinbeck
	 *@cdk.created    February 18, 2004
	 */
	public final static class AppCloser extends WindowAdapter {

		/**
		 *  closing Event. Shows a warning if this window has unsaved data and
		 *  terminates jvm, if last window.
		 *
		 *@param  e  Description of the Parameter
		 */
		public void windowClosing(WindowEvent e) {
			JFrame rootFrame = (JFrame) e.getSource();
			if (rootFrame.getContentPane().getComponent(0) instanceof JChemPaintEditorPanel) {
				JChemPaintEditorPanel panel = (JChemPaintEditorPanel) rootFrame.getContentPane().getComponent(0);
				panel.fireChange(JChemPaintEditorPanel.JCP_CLOSING);
			}
			int clear = ((JChemPaintPanel) ((JFrame) e.getSource()).getContentPane().getComponents()[0]).showWarning();
			if (JOptionPane.CANCEL_OPTION != clear) {
				for (int i = 0; i < instances.size(); i++) {
					if (((JPanel)instances.get(i)).getParent().getParent().getParent().getParent() == (JFrame)e.getSource()) {
						instances.remove(i);
						break;
					}
				}
				((JFrame) e.getSource()).setVisible(false);
				((JFrame) e.getSource()).dispose();
				if (instances.size() == 0 && !((JChemPaintPanel)rootFrame.getContentPane().getComponent(0)).isEmbedded()) {
					System.exit(0);
				}
			}
		}
	}


	/**
	 *  Closes all currently opened JCP instances.
	 */
	public static void closeAllInstances() {
		Iterator it = instances.iterator();
		while (it.hasNext()) {
			JFrame frame = (JFrame) ((JPanel)it.next()).getParent().getParent().getParent().getParent();
			WindowListener[] wls = (WindowListener[]) (frame.getListeners(WindowListener.class));
			wls[0].windowClosing(new WindowEvent(frame, 12));
			frame.setVisible(false);
			frame.dispose();
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  model  Description of the Parameter
	 */
	public void registerModel(JChemPaintModel model) {
	}


	/**
	 *  Mandatory because JChemPaint is a ChangeListener. Used by other classes to
	 *  update the information in one of the three statusbar fields.
	 *
	 *@param  e  ChangeEvent
	 */
	public void stateChanged(ChangeEvent e) {
		repaint();
	}


	// Here are the CDKEditBus methods

	/**
	 *  Gets the aPIVersion attribute of the JChemPaintPanel object
	 *
	 *@return    The aPIVersion value
	 */
	public String getAPIVersion() {
		return "1.11";
	}


	/**
	 *  Description of the Method
	 *
	 *@param  mimeType  Description of the Parameter
	 *@param  script    Description of the Parameter
	 */
	public void runScript(String mimeType, String script) {
		logger.error("JChemPaintPanel's CDKEditBus.runScript() implementation called but not implemented!");
	}

	
	/**
	 * Returns the value of viewerDimension.
	 */
	public Dimension getViewerDimension()
	{
		return viewerDimension;
	}

	/**
	 * Sets the value of viewerDimension.
	 * @param viewerDimension The value to assign viewerDimension.
	 */
	public void setViewerDimension(Dimension viewerDimension)
	{
		this.viewerDimension = viewerDimension;
	}


    /**
     * @return Returns the undoManager.
     */
    public UndoManager getUndoManager() {
        return this.jchemPaintModel.getControllerModel().getUndoManager();
    }


    /**
     * @param undoManager The undoManager to set.
     */
    public void setUndoManager(UndoManager undoManager) {
    	this.jchemPaintModel.getControllerModel().setUndoManager(undoManager);
    }


    /**
     * @return Returns the undoSupport.
     */
    public UndoableEditSupport getUndoSupport() {
        return this.jchemPaintModel.getControllerModel().getUndoSupport();
    }


    /**
     * @param undoSupport The undoSupport to set.
     */
    public void setUndoSupport(UndoableEditSupport undoSupport) {
    	this.jchemPaintModel.getControllerModel().setUndoSupport(undoSupport);
    }


	public JButton getMoveButton() {
		return moveButton;
	}


	public void setMoveButton(JButton moveButton) {
		this.moveButton = moveButton;
	}


	public JScrollPane getScrollPane() {
		return scrollPane;
	}


	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

}


