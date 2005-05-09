/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The JChemPaint project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import javax.swing.*;
import javax.swing.JInternalFrame.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;
import javax.swing.text.*;
import org.openscience.cdk.*;
import org.openscience.cdk.applications.plugin.*;
import org.openscience.cdk.controller.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.io.listener.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.tools.manipulator.*;
import org.openscience.cdk.validate.*;
import org.openscience.cdk.applications.jchempaint.*;
import org.openscience.cdk.applications.jchempaint.action.*;
import org.openscience.cdk.applications.jchempaint.dialogs.*;
import org.openscience.cdk.applications.jchempaint.io.*;

/**
 *  JPanel that contains a full JChemPaint program, either viewer or full
 *  editor.
 *
 * @author        steinbeck
 * @created       16. Februar 2005
 * @cdk.module    jchempaint
 */
public abstract class JChemPaintPanel
		 extends JPanel
		 implements ChangeListener {

	//Static variables hold information if the application is embedded and keep track of instances of JCPPanel
	static boolean isEmbedded = false;
	static Vector instances = new Vector();
	/**  Description of the Field */
	protected JChemPaintModel jchemPaintModel;
	private LoggingTool logger;
	private File currentWorkDirectory = null;
	private File lastOpenedFile = null;
	private File lastSavedFile = null;
	private FileFilter currentOpenFileFilter = null;
	private FileFilter currentSaveFileFilter = null;
	/**  Description of the Field */
	protected CDKPluginManager pluginManager = null;
	JPanel mainContainer;
	StatusBar statusBar;
	JChemPaintMenuBar menu;
	JToolBar toolBar;
	boolean showMenuBar = true;
	boolean showToolBar = true;
	boolean showStatusBar = true;
	DrawingPanel drawingPanel;
	/**  Description of the Field */
	public JButton selectButton;
	JChemPaintPanel jcpp;
	JCPAction jcpaction = null;
	/**  Description of the Field */
	protected File isAlreadyAFile = null;
	/**  this is only needed in open action immediately after opening a file */
	public JChemPaintPanel lastUsedJCPP = null;
	/**  remembers last action in toolbar for switching on/off buttons */ 
  public JComponent lastAction;



	/**  Constructor for the JChemPaintPanel object */
	public JChemPaintPanel() {
		logger = new LoggingTool(this);
		setLayout(new BorderLayout());
		mainContainer = new JPanel();
		mainContainer.setLayout(new BorderLayout());
		drawingPanel = new DrawingPanel();
		drawingPanel.setOpaque(true);
		drawingPanel.setBackground(Color.white);
		JScrollPane scrollPane = new JScrollPane(drawingPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		

		mainContainer.add(scrollPane, BorderLayout.CENTER);

		add(mainContainer, BorderLayout.CENTER);
		customizeView();
		setSize(new Dimension(600, 400));
		setPreferredSize(new Dimension(600, 400));
	}


	/**
	 *  Return the JCPAction instance associated with this JCPPanel
	 *
	 * @return    The jCPAction value
	 */
	public JCPAction getJCPAction() {
		if (jcpaction == null)
			jcpaction = new JCPAction();
		return jcpaction;
	}

	
	/**  Description of the Method */
	public void customizeView() {
		if (showMenuBar) {
			if (menu == null) menu = new JChemPaintMenuBar(this);
			add(menu, BorderLayout.NORTH);
			revalidate();
		}
		else
		{
			try
			{
				remove(menu);
				revalidate();
			}
			catch(Exception exc)
			{
				
			}
		}
		if (showStatusBar) {
			if (statusBar == null) statusBar = new StatusBar();
			add(statusBar, BorderLayout.SOUTH);
			revalidate();
		}
		else
		{
			try
			{
				remove(statusBar);
				revalidate();
			}
			catch(Exception exc)
			{
				
			}
		}
	}


	/**
	 *  Tells if this JCPPanel is part of an embedded program or not.
	 *
	 * @return    The embedded value
	 */
	public boolean isEmbedded() {
		return isEmbedded;
	}


	/**
	 *  Tells if a toolbar is shown
	 *
	 * @return    The showToolBar value
	 */
	public boolean getShowToolBar() {
		return showToolBar;
	}



	/**
	 *  Sets if a toolbar is shown
	 *
	 * @param  showMenu  The value to assign showMenu.
	 */
	public void setShowToolBar(boolean showToolBar) {
		this.showToolBar = showToolBar;
		customizeView();
	}

	/**
	 *  Tells if a menu is shown
	 *
	 * @return    The showMenu value
	 */
	public boolean getShowMenuBar() {
		return showMenuBar;
	}


	/**
	 *  Sets if a menu is shown
	 *
	 * @param  showMenu  The value to assign showMenu.
	 */
	public void setShowMenuBar(boolean showMenuBar) {
		this.showMenuBar = showMenuBar;
		customizeView();
	}


	/**
	 *  Tells if a status bar is shown
	 *
	 * @return    The showStatusBar value
	 */
	public boolean getShowStatusBar() {
		return showStatusBar;
	}


	/**
	 *  return the toolbar of this JCPPanel
	 *
	 * @return    The toolBar value
	 */
	public JToolBar getToolBar() {
		return toolBar;
	}


	/**  Sets JCP as embedded application */
	public void setEmbedded() {
		isEmbedded = true;
	}


	/**  Sets JCP as standalone-program */
	public void setNotEmbedded() {
		isEmbedded = false;
	}


	/**
	 *  Sets if statusbar should be shown
	 *
	 * @param  showStatusBar  The value to assign showStatusBar.
	 */
	public void setShowStatusBar(boolean showStatusBar) {
		this.showStatusBar = showStatusBar;
		customizeView();
	}


	/**
	 *  Returns a vector containing all JFrames containing JCPPanels currently running
	 *
	 * @return    Vector of JFrames
	 */
	public Vector getInstances() {
		return instances;
	}


	/**
	 *  Sets the file currently used for saving this Panel.
	 *
	 * @param  value  The new isAlreadyAFile value
	 */
	public void setIsAlreadyAFile(File value) {
		isAlreadyAFile = value;
	}


	/**
	 *  Returns the file currently used for saving this Panel, null if not yet saved
	 *
	 * @return    The currently used file
	 */
	public File isAlreadyAFile() {
		return isAlreadyAFile;
	}

	/**
	 *  Creates a new localized string that can be used as a title for the new
	 *  frame.
	 *
	 * @return    The newFrameName value
	 */
	public static String getNewFrameName() {
		return JCPLocalizationHandler.getInstance().getString("Untitled-") + Integer.toString(instances.size() + 1);
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public Image takeSnapshot() {
		return null;
	}


	/**
	 *  Gets the currentWorkDirectory attribute of the JChemPaintPanel object
	 *
	 * @return    The currentWorkDirectory value
	 */
	public File getCurrentWorkDirectory() {
		return currentWorkDirectory;
	}


	/**
	 *  Sets the currentWorkDirectory attribute of the JChemPaintPanel object
	 *
	 * @param  cwd  The new currentWorkDirectory value
	 */
	public void setCurrentWorkDirectory(File cwd) {
		this.currentWorkDirectory = cwd;
	}


	/**
	 *  Gets the currentOpenFileFilter attribute of the JChemPaintPanel object
	 *
	 * @return    The currentOpenFileFilter value
	 */
	public FileFilter getCurrentOpenFileFilter() {
		return currentOpenFileFilter;
	}


	/**
	 *  Sets the currentOpenFileFilter attribute of the JChemPaintPanel object
	 *
	 * @param  ff  The new currentOpenFileFilter value
	 */
	public void setCurrentOpenFileFilter(FileFilter ff) {
		this.currentOpenFileFilter = ff;
	}


	/**
	 *  Gets the currentSaveFileFilter attribute of the JChemPaintPanel object
	 *
	 * @return    The currentSaveFileFilter value
	 */
	public FileFilter getCurrentSaveFileFilter() {
		return currentSaveFileFilter;
	}


	/**
	 *  Sets the currentSaveFileFilter attribute of the JChemPaintPanel object
	 *
	 * @param  ff  The new currentSaveFileFilter value
	 */
	public void setCurrentSaveFileFilter(FileFilter ff) {
		this.currentSaveFileFilter = ff;
	}


	/**
	 *  Gets the lastOpenedFile attribute of the JChemPaintPanel object
	 *
	 * @return    The lastOpenedFile value
	 */
	public File getLastOpenedFile() {
		return lastOpenedFile;
	}


	/**
	 *  Sets the lastOpenedFile attribute of the JChemPaintPanel object
	 *
	 * @param  lof  The new lastOpenedFile value
	 */
	public void setLastOpenedFile(File lof) {
		this.lastOpenedFile = lof;
	}


	/**
	 *  Gets the lastSavedFile attribute of the JChemPaintPanel object
	 *
	 * @return    The lastSavedFile value
	 */
	public File getLastSavedFile() {
		return lastSavedFile;
	}


	/**
	 *  Sets the lastSavedFile attribute of the JChemPaintPanel object
	 *
	 * @param  lsf  The new lastSavedFile value
	 */
	public void setLastSavedFile(File lsf) {
		this.lastSavedFile = lsf;
	}


	/**
	 *  Gets the pluginManager attribute of the JChemPaint object
	 *
	 * @return    The pluginManager value
	 */
	public CDKPluginManager getPluginManager() {
		return pluginManager;
	}


	/**  Description of the Method */
	/*
	 *  private void setupPluginManager(JChemPaintPanel jcpp)
	 *  {
	 *  try
	 *  {
	 *  / set up plugin manager
	 *  JCPPropertyHandler jcph = JCPPropertyHandler.getInstance();
	 *  pluginManager = new CDKPluginManager(jcph.getJChemPaintDir().toString(), jcpp);
	 *  / load the plugins that come with JCP itself
	 *  pluginManager.loadPlugin("org.openscience.cdkplugin.dirbrowser.DirBrowserPlugin");
	 *  / load the global plugins
	 *  if (!globalPluginDir.equals(""))
	 *  {
	 *  pluginManager.loadPlugins(globalPluginDir);
	 *  }
	 *  / load the user plugins
	 *  pluginManager.loadPlugins(new File(jcph.getJChemPaintDir(), "plugins").toString());
	 *  / load plugins given with -Dplugin.dir=bla
	 *  if (System.getProperty("plugin.dir") != null)
	 *  {
	 *  pluginManager.loadPlugins(System.getProperty("plugin.dir"));
	 *  }
	 *  } catch (Exception exc)
	 *  {
	 *  logger.error("Could not initialize Plugin-Manager. I might be in a sandbox.");
	 *  logger.debug(exc);
	 *  }
	 *  }
	 */
	/**  Description of the Method */
	private void setupWorkingDirectory() {
		try {
			if (System.getProperty("user.dir") != null) {
				setCurrentWorkDirectory(new File(System.getProperty("user.dir")));
			}
		} catch (Exception exc) {
			logger.error("Could not read a system property. I might be in a sandbox.");
		}
	}


	/**
	 *  Sets the jChemPaintModel attribute of the JChemPaintPanel object
	 *
	 * @param  model  The new jChemPaintModel value
	 */
	public void setJChemPaintModel(JChemPaintModel model) {
		lastUsedJCPP = this;
		this.jchemPaintModel = model;
		jchemPaintModel.addChangeListener(this);
		ChemModel chemModel = model.getChemModel();
		scaleAndCenterMolecule(chemModel);
		drawingPanel.setJChemPaintModel(model);
	}


	/**
	 *  Partitions a given String into separate words and writes them into an
	 *  array.
	 *
	 * @param  input  String The String to be cutted into pieces
	 * @return        String[] The array containing the separate words
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
	 * @param  file  Description of the Parameter
	 */
	public void showChemFile(Reader file) {
		ChemObjectReader cor = null;

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
		if (cor.accepts(new ChemFile())) {
			// try to read a ChemFile
			try {
				chemFile = (ChemFile) cor.read((ChemObject) new ChemFile());
				if (chemFile != null) {
					processChemFile(chemFile);
					return;
				}
				else {
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
		if (cor.accepts(new ChemModel())) {
			// try to read a ChemModel
			try {
				chemModel = (ChemModel) cor.read((ChemObject) new ChemModel());
				if (chemModel != null) {
					processChemModel(chemModel);
					return;
				}
				else {
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
	 * @return    The jChemPaintModel value
	 */
	public JChemPaintModel getJChemPaintModel() {
		return jchemPaintModel;
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public int showWarning() {
		if (jchemPaintModel.isModified()) {
			int answer = JOptionPane.showConfirmDialog(this, jchemPaintModel.getTitle() + " " + JCPLocalizationHandler.getInstance().getString("warning"), JCPLocalizationHandler.getInstance().getString("warningheader"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (answer == JOptionPane.YES_OPTION) {
				new SaveAction(this,false).actionPerformed(new ActionEvent(this,12,""));
			}
			return answer;
		}
		else {
			return JOptionPane.YES_OPTION;
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  chemFile  Description of the Parameter
	 */
	public void processChemFile(ChemFile chemFile) {
		logger.info("Information read from file:");

		int chemSequenceCount = chemFile.getChemSequenceCount();
		logger.info("  # sequences: ", chemSequenceCount);

		for (int i = 0; i < chemSequenceCount; i++) {
			ChemSequence chemSequence = chemFile.getChemSequence(i);

			int chemModelCount = chemSequence.getChemModelCount();
			logger.info("  # model in seq(" + i + "): ", chemModelCount);

			for (int j = 0; j < chemModelCount; j++) {
				ChemModel chemModel = chemSequence.getChemModel(j);
				processChemModel(chemModel);
			}
		}
	}
	

	/**
	 *  Scales and centers the structure in the dimensions of the DrawingPanel.
	 *
	 * @param  chemModel  The cheModel of the structure to be scaled and centered.
	 */
	public void scaleAndCenterMolecule(ChemModel chemModel) {
		JChemPaintModel jcpm = getJChemPaintModel();
		Renderer2DModel rendererModel = jcpm.getRendererModel();
		AtomContainer ac = ChemModelManipulator.getAllInOneContainer(chemModel);
		GeometryTools.translateAllPositive(ac);
		double scaleFactor = GeometryTools.getScaleFactor(ac, rendererModel.getBondLength());
		GeometryTools.scaleMolecule(ac, scaleFactor);
		Rectangle view = ( (JViewport) drawingPanel.getParent()).getViewRect();
		double x = view.getX() + view.getWidth();
		double y = view.getY() + view.getHeight();
		Renderer2DModel model = jchemPaintModel.getRendererModel();
		double relocatedY = model.getBackgroundDimension().getSize().getHeight() - (y + view.getY()/2);
		double relocatedX = view.getX()/2; 
		Dimension viewablePart = new Dimension( (int) x, (int) y);
		GeometryTools.center(ac, viewablePart);
		//fixing the coords regarding the position of the viewablePart
		Atom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint2d() != null) {
				atoms[i].getPoint2d().x = atoms[i].getPoint2d().x + relocatedX;
				atoms[i].getPoint2d().y = atoms[i].getPoint2d().y + relocatedY;
			}
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @param  chemModel  Description of the Parameter
	 */
	public void processChemModel(ChemModel chemModel) {
		// check for bonds
		if (ChemModelManipulator.getAllInOneContainer(chemModel).getBondCount() == 0) {
			String error = "Model does not have bonds. Cannot depict contents.";
			logger.warn(error);
			JOptionPane.showMessageDialog(this, error);
			return;
		}

		// check for coordinates
		if (!(GeometryTools.has2DCoordinates(ChemModelManipulator.getAllInOneContainer(chemModel)))) {
			String error = "Model does not have coordinates. Cannot open file.";
			logger.warn(error);
			JOptionPane.showMessageDialog(this, error);
			CreateCoordinatesForFileDialog frame = new CreateCoordinatesForFileDialog(chemModel);
			frame.pack();
			frame.show();
			return;
		}
		
		
		JChemPaintModel jcpm = new JChemPaintModel(chemModel);
		lastUsedJCPP = this;
		if (isEmbedded()) {
			if (showWarning() == JOptionPane.YES_OPTION) {
				((JChemPaintEditorPanel) this).registerModel(jcpm);
				setJChemPaintModel(jcpm);
				repaint();
			}
		}
		else if (getJChemPaintModel().getChemModel().getSetOfMolecules() == null || getJChemPaintModel().getChemModel().getSetOfMolecules().getMolecule(0).getAtoms().length == 0) {
			((JChemPaintEditorPanel) this).registerModel(jcpm);
			setJChemPaintModel(jcpm);
			repaint();
		}
		else {
			JFrame jcpf = ((JChemPaintEditorPanel) this).getNewFrame(jcpm);
			jcpf.show();
			scaleAndCenterMolecule(chemModel);
			jcpf.pack();
			lastUsedJCPP = (JChemPaintPanel) jcpf.getContentPane().getComponents()[0];
		}
	}


	/**
	 *  Gets the chemObjectReader attribute of the JChemPaintPanel object
	 *
	 * @param  reader           Description of the Parameter
	 * @return                  The chemObjectReader value
	 * @exception  IOException  Description of the Exception
	 */
	public ChemObjectReader getChemObjectReader(Reader reader) throws IOException {
		ReaderFactory factory = new ReaderFactory();
		ChemObjectReader coReader = factory.createReader(reader);
		if (coReader != null) {
			coReader.addChemObjectIOListener(new SwingGUIListener(this, 4));
		}
		return coReader;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  chemFile  Description of the Parameter
	 */
	public void showChemFile(ChemFile chemFile) {
		logger.info("Information read from file:");

		int chemSequenceCount = chemFile.getChemSequenceCount();
		logger.info("  # sequences: " + chemSequenceCount);

		for (int i = 0; i < chemSequenceCount; i++) {
			ChemSequence chemSequence = chemFile.getChemSequence(i);

			int chemModelCount = chemSequence.getChemModelCount();
			logger.info("  # model in seq(" + i + "): " + chemModelCount);

			for (int j = 0; j < chemModelCount; j++) {
				ChemModel chemModel = chemSequence.getChemModel(j);
				showChemModel(chemModel);
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  chemModel  Description of the Parameter
	 */
	public void showChemModel(ChemModel chemModel) {
		// check for bonds
		if (ChemModelManipulator.getAllInOneContainer(chemModel).getBondCount() == 0) {
			String error = "Model does not have bonds. Cannot depict contents.";
			logger.warn(error);
			JOptionPane.showMessageDialog(this, error);
			return;
		}

		// check for coordinates
		if (!(GeometryTools.has2DCoordinates(ChemModelManipulator.getAllInOneContainer(chemModel)))) {

			String error = "Model does not have coordinates. Will ask for coord generation.";
			logger.warn(error);

			CreateCoordinatesForFileDialog frame = new CreateCoordinatesForFileDialog(chemModel);
			frame.pack();
			frame.show();
			frame.moveToFront();
			return;
		}

		setJChemPaintModel(new JChemPaintModel(chemModel));
	}


	/**
	 *  Gets the chemModel attribute of the JChemPaint object
	 *
	 * @return    The chemModel value
	 */
	public ChemModel getChemModel() {
		return jchemPaintModel.getChemModel();
	}


	/**
	 *  Gets the chemFile attribute of the JChemPaint object
	 *
	 * @return    The chemFile value
	 */
	public ChemFile getChemFile() {
		ChemFile file = new ChemFile();
		ChemSequence sequence = new ChemSequence();
		sequence.addChemModel(getChemModel());
		file.addChemSequence(sequence);
		return file;
	}


	/**
	 *  Creates a JMenu which can be part of the menu of an application embedding jcp.
	 *
	 *@return           The created JMenu
	 */
  public JMenu getMenuForEmbedded(){
    return(menu.getMenuForEmbedded(this));
	}


	/**
	 *  Class for closing jcp
	 *
	 * @author     steinbeck
	 * @created    February 18, 2004
	 */
	public final static class AppCloser extends WindowAdapter {

		/**
		 *  closing Event. Shows a warning if this window has unsaved data and terminates jvm, if last window.
		 *
		 * @param  e  Description of the Parameter
		 */
		public void windowClosing(WindowEvent e) {
			int clear = ((JChemPaintPanel) ((JFrame) e.getSource()).getContentPane().getComponents()[0]).showWarning();
			if (JOptionPane.CANCEL_OPTION != clear) {
				for (int i = 0; i < instances.size(); i++) {
					if (instances.get(i) == e.getSource()) {
						instances.remove(i);
						break;
					}
				}
				((JFrame) e.getSource()).setVisible(false);
				((JFrame) e.getSource()).dispose();
				if (instances.size() == 0 && !isEmbedded) {
					System.exit(0);
				}
			}
		}
	}


	/**  Closes all currently opened JCP instances. */
	public static void closeAllInstances() {
		Iterator it = instances.iterator();
		while (it.hasNext()) {
			JFrame frame = (JFrame) it.next();
			WindowListener[] wls = (WindowListener[]) (frame.getListeners(WindowListener.class));
			wls[0].windowClosing(new WindowEvent(frame, 12));
			frame.setVisible(false);
			frame.dispose();
		}
	}
	public void registerModel(JChemPaintModel model) {
	}
	
	/**
	 *  Mandatory because JChemPaint is a ChangeListener. Used by other classes to
	 *  update the information in one of the three statusbar fields.
	 *
	 *@param  e  ChangeEvent
	 */
	public void stateChanged(ChangeEvent e)
	{

		if (jchemPaintModel != null)
		{
			for (int i = 0; i < 3; i++)
			{
				String status = jchemPaintModel.getStatus(i);
				statusBar.setStatus(i + 1, status);
			}
		} else
		{
			if (statusBar != null)
			{
				statusBar.setStatus(1, "no model");
			}
		}
		repaint();
		// send event to plugins
		/*if (pluginManager != null)
		{
			pluginManager.stateChanged(new ChemObjectChangeEvent(this));
		}*/
	}
		
}


