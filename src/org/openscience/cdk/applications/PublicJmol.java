/*
 * Copyright 2002 The Jmol Development Team
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.jmol;

import java.io.File;

import com.obrador.JpegEncoder;
import java.awt.Container;
import java.awt.Image;
import java.awt.PrintJob;
import java.awt.Color;
import java.awt.Window;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Insets;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.MissingResourceException;
import java.util.EventObject;
import javax.swing.JToolBar;
import javax.swing.JFileChooser;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JPanel;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.JMenu;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;

/**
 * An adapted Jmol version with menu, toolbars etc for simple viewing.
 *
 * @author Bradley A. Smith (bradley@baysmith.com)
 * @author Peter Murray-Rust
 * @authro Egon Willighagen
 */
public class PublicJmol extends JPanel {

  private JScrollPane scroller;
  private JViewport port;
  static DisplayPanel display;
  StatusBar status;
  private AtomPropsMenu apm;
  static AtomTypeTable atomTypeTable;
  private Preferences prefs;
  private Animate anim;
  private Vibrate vib;
  private PropertyGraph pg;
  private Measure meas;
  private MeasurementList mlist;
  private RecentFilesDialog recentFiles;
  protected ScriptWindow scriptWindow;
  protected static JFrame frame;
  private ChemFile chemFile;
  private JFileChooser openChooser;
  private JFileChooser saveChooser;
  private FileTyper fileTyper;
  private JFileChooser exportChooser;

  /**
   * Button group for toggle buttons in the toolbar.
   */
  ButtonGroup toolbarButtonGroup = new ButtonGroup();

  static File UserPropsFile;
  static File UserAtypeFile;
  private static HistoryFile historyFile;

  Splash splash;

  public static HistoryFile getHistoryFile() {
    return historyFile;
  }

  private static JFrame consoleframe;

  protected DisplaySettings settings = new DisplaySettings();

  /** The name of the currently open file **/
  public String currentFileName = "";

  static {
    if (System.getProperty("javawebstart.version") != null) {

      // If the property is found, Jmol is running with Java Web Start. To fix
      // bug 4621090, the security manager is set to null.
      System.setSecurityManager(null);
    }
    if (System.getProperty("user.home") == null) {
      System.err.println(
          "Error starting Jmol: the property 'user.home' is not defined.");
      System.exit(1);
    }
    File ujmoldir = new File(new File(System.getProperty("user.home")),
                      ".jmol");
    ujmoldir.mkdirs();
    UserPropsFile = new File(ujmoldir, "properties");
    UserAtypeFile = new File(ujmoldir, "AtomTypes");
    historyFile = new HistoryFile(new File(ujmoldir, "history"),
        "Jmol's persistent values");
  }

  public PublicJmol(Splash splash) {

    super(true);
    this.splash = splash;
    splash.showStatus("Initializing Swing...");
    try {
      UIManager
          .setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception exc) {
      System.err.println("Error loading L&F: " + exc);
    }

    setBorder(BorderFactory.createEtchedBorder());
    setLayout(new BorderLayout());

    scroller = new JScrollPane();
    port = scroller.getViewport();

    try {
      String vpFlag =
        JmolResourceHandler.getInstance()
          .getString("Jmol.ViewportBackingStore");
      Boolean bs = new Boolean(vpFlag);
      port.setBackingStoreEnabled(bs.booleanValue());
    } catch (MissingResourceException mre) {

      // just use the viewport default
    }

    status = (StatusBar) createStatusBar();
    splash.showStatus("Initializing 3D display...");
    display = new DisplayPanel(status, settings);
    splash.showStatus("Initializing Preferences...");
    prefs = new Preferences(frame, display);
    splash.showStatus("Initializing Animate...");
    anim = new Animate(frame, display);
    splash.showStatus("Initializing Vibrate...");
    vib = new Vibrate(frame, display);
    splash.showStatus("Initializing Recent Files...");
    recentFiles = new RecentFilesDialog(frame);
    splash.showStatus("Initializing Property Graph...");
    pg = new PropertyGraph(frame);
    splash.showStatus("Initializing Measurements...");
    mlist = new MeasurementList(frame, display);
    meas = new Measure(frame, display);
    meas.setMeasurementList(mlist);
    display.setMeasure(meas);
    mlist.addMeasurementListListener(display);
    port.add(display);
    splash.showStatus("Initializing Chemical Shifts...");
    chemicalShifts.initialize();

    // install the command table
    splash.showStatus("Building Command Hooks...");
    commands = new Hashtable();
    Action[] actions = getActions();
    for (int i = 0; i < actions.length; i++) {
      Action a = actions[i];
      commands.put(a.getValue(Action.NAME), a);
    }

    // Fix for actions that confict with the operation of
    // vibration animations
    Action[] animActions = anim.getActions();
    for (int i = 0; i < animActions.length; ++i) {
      vib.addConflictingAction(animActions[i]);
    }
    vib.addConflictingAction(getAction(openAction));
    menuItems = new Hashtable();
    splash.showStatus("Building Menubar...");
    menubar = createMenubar();
    // add("North", menubar);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    // panel.add("North", createToolbar());

    JPanel ip = new JPanel();
    ip.setLayout(new BorderLayout());
    ip.add("Center", scroller);
    panel.add("Center", ip);
    add("Center", panel);
    // add("South", status);

    splash.showStatus("Starting display...");
    display.start();

    splash.showStatus("Reading AtomTypes...");
    atomTypeTable = new AtomTypeTable(frame, UserAtypeFile);
    splash.showStatus("Setting up File Choosers...");
    File currentDir = getUserDirectory();
    openChooser = new JFileChooser();
    openChooser.setCurrentDirectory(currentDir);
    saveChooser = new JFileChooser();
    fileTyper = new FileTyper();
    saveChooser.addPropertyChangeListener(fileTyper);
    saveChooser.setAccessory(fileTyper);
    saveChooser.setCurrentDirectory(currentDir);
    exportChooser = new JFileChooser();
    exportChooser.setCurrentDirectory(currentDir);

    addPropertyChangeListener(moleculeProperty, saveAction);
    addPropertyChangeListener(moleculeProperty, exportAction);
    addPropertyChangeListener(moleculeProperty, povrayAction);
    addPropertyChangeListener(moleculeProperty, printAction);
  }

  public static PublicJmol getJmol(JFrame frame) {

    JmolResourceHandler jrh = JmolResourceHandler.getInstance();
    ImageIcon splash_image = jrh.getIcon("Jmol.splash");
    Splash splash = new Splash(frame, splash_image);
    splash.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    splash.showStatus("Creating main window...");
    frame.setTitle(jrh.getString("Jmol.Title"));
    frame.setBackground(Color.lightGray);
    frame.getContentPane().setLayout(new BorderLayout());
    splash.showStatus("Initializing Jmol...");
    PublicJmol window = new PublicJmol(splash);
    frame.getContentPane().add("Center", window);
    frame.addWindowListener(new PublicJmol.AppCloser());
    frame.pack();
    frame.setSize(400, 400);
    ImageIcon jmolIcon =
      JmolResourceHandler.getInstance().getIcon("Jmol.icon");
    Image iconImage = jmolIcon.getImage();
    frame.setIconImage(iconImage);
    splash.showStatus("Launching main frame...");
    frame.show();
    return window;
  }

  /**
   * Opens a file with a hint to use a particular reader, defaulting to
   * the ReaderFactory if the hint doesn't match any known file types.
   */
  public void openFile(File theFile, String typeHint) {

    if (theFile != null) {
      frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      ChemFile newChemFile = null;

      try {
        FileInputStream is = new FileInputStream(theFile);

        if (typeHint.equals("PDB")) {
          ChemFileReader reader = new PDBReader(new InputStreamReader(is));
          newChemFile = reader.read();
        } else if (typeHint.equals("CML")) {
          ChemFileReader reader = new CMLReader(theFile.toURL());
          newChemFile = ((CMLReader) reader).readValidated();
        } else if (typeHint.equals("XYZ (xmol)")) {
          ChemFileReader reader = new XYZReader(new InputStreamReader(is));
          newChemFile = reader.read();
        } else if (typeHint.equals("Ghemical Molecular Dynamics")) {
          ChemFileReader reader =
            new GhemicalMMReader(new InputStreamReader(is));
          newChemFile = reader.read();
        } else {

          // Try to automagically determine file type:
          ChemFileReader reader =
            ReaderFactory.createReader(new InputStreamReader(is));
          if (reader == null) {
            throw new JmolException("openFile", "Unknown file type");
          }
          newChemFile = reader.read();
        }

        if (newChemFile != null) {
          if (newChemFile.getNumberFrames() > 0) {
            setChemFile(newChemFile);

            frame.setTitle(theFile.getName());
            currentFileName = theFile.getName();

            // Add the file to the recent files list
            recentFiles.addFile(theFile.toString(), typeHint);
          } else {
            JOptionPane.showMessageDialog(PublicJmol.this,
                "The file \"" + theFile + "\" appears to be empty."
                  + "\nIf this is in error, please contact the Jmol development team.",
                    "Empty file", JOptionPane.ERROR_MESSAGE);
          }
        } else {
          JOptionPane.showMessageDialog(PublicJmol.this,
              "Unknown error reading file \"" + theFile + "\"."
                + "\nPlease contact the Jmol development team.",
                  "Unknown error", JOptionPane.ERROR_MESSAGE);
        }

      } catch (java.io.FileNotFoundException ex) {
        JOptionPane.showMessageDialog(PublicJmol.this,
            "Unable to find file \"" + theFile + "\"", "File not found",
              JOptionPane.ERROR_MESSAGE);
      } catch (JmolException ex) {
        JOptionPane.showMessageDialog(PublicJmol.this,
            "Unable to determine type for file \"" + theFile + "\"",
              "Unknown file type", JOptionPane.ERROR_MESSAGE);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(PublicJmol.this,
            "Unexpected exception: " + ex.getMessage()
              + "\nPlease contact the Jmol development team.",
                "Unexpected error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
      } finally {
        frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }
      return;
    }
  }

  // transfer molecule to Jmol as serialized XML string
  public void readCML(String s) {
    StringReader sr = new StringReader(s);
    readCML(sr);
  }

  // transfer CML molecule to Jmol as string representation
  public void readCML(StringReader sr) {

    ChemFileReader reader = new CMLReader(sr);
    try {
      chemFile = reader.read();
    } catch (Exception e) {
      System.out.println("Exception: " + e);
      return;
    }

    System.out.println("READING CML");
    String name = "from Jumbo";
    if (chemFile != null) {
      display.setChemFile(chemFile);
      anim.setChemFile(chemFile);
      vib.setChemFile(chemFile);
      pg.setChemFile(chemFile);
      apm.replaceList(chemFile.getAtomPropertyList());
      mlist.clear();

      chemicalShifts.setChemFile(chemFile, apm);
    }
  }

  /**
   * returns the ChemFile that we are currently working with
   *
   * @see ChemFile
   */
  public ChemFile getCurrentFile() {
    return chemFile;
  }


  void setChemFile(ChemFile chemFile) {

    ChemFile oldChemFile = this.chemFile;
    this.chemFile = chemFile;
    display.setChemFile(chemFile);
    anim.setChemFile(chemFile);
    vib.setChemFile(chemFile);
    pg.setChemFile(chemFile);
    apm.replaceList(chemFile.getAtomPropertyList());
    mlist.clear();

    chemicalShifts.setChemFile(chemFile, apm);

    firePropertyChange(moleculeProperty, oldChemFile, chemFile);
  }

  /**
   * Returns whether the application has a molecule loaded.
   */
  public boolean hasMolecule() {
    return chemFile != null;
  }

  /**
   * returns a list of Actions that is understood by the upper level
   * application
   */
  public Action[] getActions() {

    Action[] displayActions = display.getActions();
    Action[] prefActions = prefs.getActions();
    Action[] animActions = anim.getActions();
    Action[] measActions = meas.getActions();
    Action[] mlistActions = mlist.getActions();
    Action[] vibActions = vib.getActions();
    Action[] pgActions = pg.getActions();

    int nactions = defaultActions.length + displayActions.length
                     + prefActions.length + animActions.length
                     + vibActions.length + measActions.length
                     + mlistActions.length + pgActions.length;

    Action[] theActions = new Action[nactions];

    // YARG.  This is way ugly.  Clean this up!

    System.arraycopy(defaultActions, 0, theActions, 0, defaultActions.length);
    System.arraycopy(displayActions, 0, theActions, defaultActions.length,
        displayActions.length);
    System.arraycopy(prefActions, 0, theActions,
        defaultActions.length + displayActions.length, prefActions.length);
    System.arraycopy(animActions, 0, theActions,
        defaultActions.length + displayActions.length + prefActions.length,
          animActions.length);
    System.arraycopy(measActions, 0, theActions,
        defaultActions.length + displayActions.length + prefActions.length
          + animActions.length, measActions.length);
    System.arraycopy(mlistActions, 0, theActions,
        defaultActions.length + displayActions.length + prefActions.length
          + animActions.length + measActions.length, mlistActions.length);
    System.arraycopy(vibActions, 0, theActions,
        defaultActions.length + displayActions.length + prefActions.length
          + animActions.length + measActions.length + mlistActions.length,
            vibActions.length);
    System.arraycopy(pgActions, 0, theActions,
        defaultActions.length + displayActions.length + prefActions.length
          + animActions.length + measActions.length + mlistActions.length
            + vibActions.length, pgActions.length);
    return theActions;
  }

  /**
   * To shutdown when run as an application.  This is a
   * fairly lame implementation.   A more self-respecting
   * implementation would at least check to see if a save
   * was needed.
   */
  protected static final class AppCloser extends WindowAdapter {

    public void windowClosing(WindowEvent e) {
      System.exit(0);
    }
  }

  /**
   * Find the hosting frame, for the file-chooser dialog.
   */
  protected Frame getFrame() {

    for (Container p = getParent(); p != null; p = p.getParent()) {
      if (p instanceof Frame) {
        return (Frame) p;
      }
    }
    return null;
  }

  /**
   * This is the hook through which all menu items are
   * created.  It registers the result with the menuitem
   * hashtable so that it can be fetched with getMenuItem().
   * @see #getMenuItem
   */
  protected JMenuItem createMenuItem(String cmd, boolean isRadio) {

    JMenuItem mi;
    if (isRadio) {
      mi = new JRadioButtonMenuItem(JmolResourceHandler.getInstance()
          .getString("Jmol." + cmd + labelSuffix));
    } else {
      String checked = JmolResourceHandler.getInstance().getString("Jmol."
                         + cmd + checkSuffix);
      if (checked != null) {
        boolean c = false;
        if (checked.equals("true")) {
          c = true;
        }
        mi = new JCheckBoxMenuItem(JmolResourceHandler.getInstance()
            .getString("Jmol." + cmd + labelSuffix), c);
      } else {
        mi = new JMenuItem(JmolResourceHandler.getInstance().getString("Jmol."
            + cmd + labelSuffix));
      }
    }
    String mnem = JmolResourceHandler.getInstance().getString("Jmol." + cmd
                    + mnemonicSuffix);
    if (mnem != null) {
      char mn = mnem.charAt(0);
      mi.setMnemonic(mn);
    }

    /*        String accel = JmolResourceHandler.getInstance().getString("Jmol." + cmd + acceleratorSuffix);
    if (accel != null) {
            if (accel.startsWith("Ctrl-")) {
                    char ac = accel.charAt(5);
                    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
                                                                                                     ActionEvent.CTRL_MASK));
            }
            if (accel.startsWith("Alt-")) {
                    char ac = accel.charAt(4);
                    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
                                                                                                     ActionEvent.ALT_MASK));
            }
    }
    */
    ImageIcon f = JmolResourceHandler.getInstance().getIcon("Jmol." + cmd
                    + imageSuffix);
    if (f != null) {
      mi.setHorizontalTextPosition(JButton.RIGHT);
      mi.setIcon(f);
    }
    String astr = JmolResourceHandler.getInstance().getString("Jmol." + cmd
                    + actionSuffix);
    if (astr == null) {
      astr = cmd;
    }
    mi.setActionCommand(astr);
    Action a = getAction(astr);
    if (a != null) {
      mi.addActionListener(a);
      a.addPropertyChangeListener(new ActionChangedListener(mi));
      mi.setEnabled(a.isEnabled());
    } else {
      mi.setEnabled(false);
    }
    menuItems.put(cmd, mi);
    return mi;
  }

  /**
   * Fetch the menu item that was created for the given
   * command.
   * @param cmd  Name of the action.
   * @return item created for the given command or null
   *  if one wasn't created.
   */
  protected JMenuItem getMenuItem(String cmd) {
    return (JMenuItem) menuItems.get(cmd);
  }

  /**
   * Fetch the action that was created for the given
   * command.
   * @param cmd  Name of the action.
   */
  protected Action getAction(String cmd) {
    return (Action) commands.get(cmd);
  }

  /**
   * Create the toolbar.  By default this reads the
   * resource file for the definition of the toolbars.
   */
  private Component createToolbar() {

    /* toolbar = new JToolBar();
    String[] tool1Keys =
      tokenize(JmolResourceHandler.getInstance().getString("Jmol.toolbar"));
    for (int i = 0; i < tool1Keys.length; i++) {
      if (tool1Keys[i].equals("-")) {
        toolbar.addSeparator();
      } else {
        toolbar.add(createTool(tool1Keys[i]));
      }
    }

    //Action handler implementation would go here.
    toolbar.add(Box.createHorizontalGlue());

    */
    return toolbar;
  }

  /**
   * Hook through which every toolbar item is created.
   */
  protected Component createTool(String key) {
    return createToolbarButton(key);
  }

  /**
   * Create a button to go inside of the toolbar.  By default this
   * will load an image resource.  The image filename is relative to
   * the classpath (including the '.' directory if its a part of the
   * classpath), and may either be in a JAR file or a separate file.
   *
   * @param key The key in the resource file to serve as the basis
   *  of lookups.
   */
  protected AbstractButton createToolbarButton(String key) {

    ImageIcon ii = JmolResourceHandler.getInstance().getIcon("Jmol." + key
                     + imageSuffix);
    AbstractButton b = new JButton(ii);
    String isToggleString =
      JmolResourceHandler.getInstance().getString("Jmol." + key + "Toggle");
    if (isToggleString != null) {
      boolean isToggle = Boolean.valueOf(isToggleString).booleanValue();
      if (isToggle) {
        b = new JToggleButton(ii);
        toolbarButtonGroup.add(b);
        String isSelectedString =
          JmolResourceHandler.getInstance().getString("Jmol." + key
            + "ToggleSelected");
        if (isSelectedString != null) {
          boolean isSelected =
            Boolean.valueOf(isSelectedString).booleanValue();
          b.setSelected(isSelected);
        }
      }
    }
    b.setRequestFocusEnabled(false);
    b.setMargin(new Insets(1, 1, 1, 1));

    String astr = JmolResourceHandler.getInstance().getString("Jmol." + key
                    + actionSuffix);
    if (astr == null) {
      astr = key;
    }
    Action a = getAction(astr);
    if (a != null) {

      // b = new JButton(a);
      b.setActionCommand(astr);
      b.addActionListener(a);
      a.addPropertyChangeListener(new ActionChangedListener(b));
      b.setEnabled(a.isEnabled());
    } else {
      b.setEnabled(false);
    }

    String tip = JmolResourceHandler.getInstance().getString("Jmol." + key
                   + tipSuffix);
    if (tip != null) {
      b.setToolTipText(tip);
    }

    return b;
  }

  /**
   * Take the given string and chop it up into a series
   * of strings on whitespace boundries.  This is useful
   * for trying to get an array of strings out of the
   * resource file.
   */
  protected String[] tokenize(String input) {

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

  protected Component createStatusBar() {
    return new StatusBar();
  }

  /**
   * Create the menubar for the app.  By default this pulls the
   * definition of the menu from the associated resource file.
   */
  protected JMenuBar createMenubar() {

    JMenuItem mi;
    JMenuBar mb = new JMenuBar();

    String[] menuKeys =
      tokenize(JmolResourceHandler.getInstance().getString("Jmol.menubar"));
    for (int i = 0; i < menuKeys.length; i++) {
      if (menuKeys[i].equals("-")) {
        mb.add(Box.createHorizontalGlue());
      } else {
        JMenu m = createMenu(menuKeys[i], false);

        if (m != null) {
          mb.add(m);
        }
        String mnem = JmolResourceHandler.getInstance().getString("Jmol."
                        + menuKeys[i] + mnemonicSuffix);
        if (mnem != null) {
          char mn = mnem.charAt(0);
          m.setMnemonic(mn);
        }

      }
    }
    return mb;
  }

  /**
   * Create a menu for the app.  By default this pulls the
   * definition of the menu from the associated resource file.
   */
  protected JMenu createMenu(String key, boolean isPopup) {

    // Get list of items from resource file:
    String[] itemKeys;
    if (isPopup) {
      itemKeys = tokenize(JmolResourceHandler.getInstance().getString("Jmol."
          + key + popupSuffix));
    } else {
      itemKeys = tokenize(JmolResourceHandler.getInstance().getString("Jmol."
          + key));
    }

    // Get label associated with this menu:
    JMenu menu = new JMenu(JmolResourceHandler.getInstance().getString("Jmol."
                   + key + "Label"));

    // Loop over the items in this menu:
    for (int i = 0; i < itemKeys.length; i++) {

      // Check to see if it is a radio group:
      String radiogroup = JmolResourceHandler.getInstance().getString("Jmol."
                            + itemKeys[i] + radioSuffix);
      if (radiogroup != null) {

        // Get the list of items in the radio group:
        String[] radioKeys = tokenize(radiogroup);

        // See what is the selected member of the radio group:
        String si = JmolResourceHandler.getInstance().getString("Jmol."
                      + itemKeys[i] + selectedSuffix);

        // Create the button group:
        ButtonGroup bg = new ButtonGroup();

        // Loop over the items in the radio group:
        for (int j = 0; j < radioKeys.length; j++) {
          JRadioButtonMenuItem mi =
            (JRadioButtonMenuItem) createMenuItem(radioKeys[j], true);
          menu.add(mi);
          bg.add(mi);
          if (radioKeys[j].equals(si)) {
            mi.setSelected(true);
          }
        }
      } else {
        if (itemKeys[i].equals("-")) {
          menu.addSeparator();
        } else {

          // Check to see if it is a popup menu:
          String popup = JmolResourceHandler.getInstance().getString("Jmol."
                           + itemKeys[i] + popupSuffix);
          if (popup != null) {
            if (popup.equals("prop")) {
              apm = new AtomPropsMenu(JmolResourceHandler.getInstance()
                  .getString("Jmol." + itemKeys[i] + "Label"), settings);
              menu.add(apm);
            } else {
              JMenu pm;
              pm = createMenu(itemKeys[i], true);
              menu.add(pm);
            }
          } else {
            JMenuItem mi = createMenuItem(itemKeys[i], false);
            menu.add(mi);
          }
        }
      }
    }
    return menu;
  }


  private class ActionChangedListener implements PropertyChangeListener {

    AbstractButton button;

    ActionChangedListener(AbstractButton button) {
      super();
      this.button = button;
    }

    public void propertyChange(PropertyChangeEvent e) {

      String propertyName = e.getPropertyName();
      if (e.getPropertyName().equals(Action.NAME)) {
        String text = (String) e.getNewValue();
        if (button.getText() != null) {
          button.setText(text);
        }
      } else if (propertyName.equals("enabled")) {
        Boolean enabledState = (Boolean) e.getNewValue();
        button.setEnabled(enabledState.booleanValue());
      }
    }
  }

  private Hashtable commands;
  private Hashtable menuItems;
  private JMenuBar menubar;
  private JToolBar toolbar;


  /**
   * Suffix applied to the key used in resource file
   * lookups for an image.
   */
  private static final String imageSuffix = "Image";

  /**
   * Suffix applied to the key used in resource file
   * lookups for a label.
   */
  private static final String labelSuffix = "Label";

  /**
   * Suffix applied to the key used in resource file
   * lookups for a checkbox menu item.
   */
  private static final String checkSuffix = "Check";

  /**
   * Suffix applied to the key used in resource file
   * lookups for a radio group.
   */
  private static final String radioSuffix = "Radio";

  /**
   * Suffix applied to the key used in resource file
   * lookups for a selected member of a radio group.
   */
  private static final String selectedSuffix = "Selected";

  /**
   * Suffix applied to the key used in resource file
   * lookups for a popup menu.
   */
  private static final String popupSuffix = "Popup";

  /**
   * Suffix applied to the key used in resource file
   * lookups for an action.
   */
  private static final String actionSuffix = "Action";

  /**
   * Suffix applied to the key used in resource file
   * lookups for tooltip text.
   */
  private static final String tipSuffix = "Tooltip";

  /**
   * Suffix applied to the key used in resource file
   * lookups for Mnemonics.
   */
  private static final String mnemonicSuffix = "Mnemonic";

  private static final String openAction = "open";
  private static final String newAction = "new";
  private static final String saveasAction = "saveas";
  private static final String exportActionProperty = "export";
  private static final String exitAction = "exit";
  private static final String aboutAction = "about";
  private static final String vibAction = "vibrate";
  private static final String whatsnewAction = "whatsnew";
  private static final String uguideAction = "uguide";
  private static final String atompropsAction = "atomprops";
  private static final String printActionProperty = "print";
  private static final String recentFilesAction = "recentFiles";
  private static final String povrayActionProperty = "povray";
  private static final String scriptAction = "script";


  // --- action implementations -----------------------------------

  private CalculateChemicalShifts chemicalShifts =
    new CalculateChemicalShifts();

  private SaveAction saveAction = new SaveAction();
  private ExportAction exportAction = new ExportAction();
  private PovrayAction povrayAction = new PovrayAction();
  private PrintAction printAction = new PrintAction();

  /**
   * Actions defined by the Jmol class
   */
  private Action[] defaultActions = {
    new NewAction(), new OpenAction(), saveAction, printAction, exportAction,
    new ExitAction(), new AboutAction(), new WhatsNewAction(),
    new UguideAction(), new AtompropsAction(), new ConsoleAction(),
    chemicalShifts, new RecentFilesAction(), povrayAction, new ScriptAction()
  };

  class ConsoleAction extends AbstractAction {

    public ConsoleAction() {
      super("console");
    }

    public void actionPerformed(ActionEvent e) {
      consoleframe.show();
    }

  }

  class AboutAction extends AbstractAction {

    public AboutAction() {
      super(aboutAction);
    }

    public void actionPerformed(ActionEvent e) {
      AboutDialog ad = new AboutDialog(frame);
      ad.show();
    }

  }

  class WhatsNewAction extends AbstractAction {

    public WhatsNewAction() {
      super(whatsnewAction);
    }

    public void actionPerformed(ActionEvent e) {
      WhatsNewDialog wnd = new WhatsNewDialog(frame);
      wnd.show();
    }

  }

  class UguideAction extends AbstractAction {

    public UguideAction() {
      super(uguideAction);
    }

    public void actionPerformed(ActionEvent e) {
      HelpDialog hd = new HelpDialog(frame);
      hd.show();
    }
  }

  class AtompropsAction extends AbstractAction {

    public AtompropsAction() {
      super(atompropsAction);
    }

    public void actionPerformed(ActionEvent e) {
      atomTypeTable.show();
    }
  }

  class PrintAction extends MoleculeDependentAction {

    public PrintAction() {
      super(printActionProperty);
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
      print();
    }

  }

  /**
   * added print command, so that it can be used by RasmolScriptHandler
   **/
  protected void print() {

    Toolkit tk = Toolkit.getDefaultToolkit();
    PrintJob pJob = tk.getPrintJob(frame, "Jmol Print Job", null);
    Graphics pg = pJob.getGraphics();

    if (pg != null) {
      display.print(pg);
      pg.dispose();    // Flushes the print job
    }
  }

  class OpenAction extends NewAction {

    OpenAction() {
      super(openAction);
    }

    public void actionPerformed(ActionEvent e) {

      int retval = openChooser.showOpenDialog(PublicJmol.this);
      if (retval == 0) {
        File theFile = openChooser.getSelectedFile();
        openFile(theFile, "");
        return;
      }
    }
  }

  class NewAction extends AbstractAction {

    NewAction() {
      super(newAction);
    }

    NewAction(String nm) {
      super(nm);
    }

    public void actionPerformed(ActionEvent e) {
      revalidate();
    }
  }

  /**
   * Really lame implementation of an exit command
   */
  class ExitAction extends AbstractAction {

    ExitAction() {
      super(exitAction);
    }

    public void actionPerformed(ActionEvent e) {
      System.exit(0);
    }
  }

  class SaveAction extends MoleculeDependentAction {

    SaveAction() {
      super(saveasAction);
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {

      Frame frame = getFrame();
      int retval = saveChooser.showSaveDialog(PublicJmol.this);
      if (retval == 0) {
        File theFile = saveChooser.getSelectedFile();
        if (theFile != null) {
          try {
            FileOutputStream os = new FileOutputStream(theFile);

            if (fileTyper.getType().equals("XYZ (xmol)")) {
              XYZSaver xyzs = new XYZSaver(getCurrentFile(), os);
              xyzs.writeFile();
            } else if (fileTyper.getType().equals("PDB")) {
              PdbSaver ps = new PdbSaver(getCurrentFile(), os);
              ps.writeFile();
            } else if (fileTyper.getType().equals("CML")) {
              CMLSaver cs = new CMLSaver(getCurrentFile(), os);
              cs.writeFile();
            } else {
            }

            os.flush();
            os.close();

          } catch (Exception exc) {
            status.setStatus(1, "Exception:");
            status.setStatus(2, exc.toString());
            exc.printStackTrace();
          }
          return;
        }
      }
    }

  }

  class ExportAction extends MoleculeDependentAction {

    ExportAction() {
      super(exportActionProperty);
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {

      Frame frame = getFrame();


      ImageTyper it = new ImageTyper(exportChooser);

      // GIF doesn't support more than 8 bits:
      if (settings.getAtomDrawMode() == DisplaySettings.SHADING) {
        it.disableGIF();
      }
      exportChooser.setAccessory(it);

      int retval = exportChooser.showSaveDialog(PublicJmol.this);
      if (retval == 0) {
        File theFile = exportChooser.getSelectedFile();

        if (theFile != null) {
          try {
            Image eImage = display.takeSnapshot();
            FileOutputStream os = new FileOutputStream(theFile);

            if (it.getType().equals("JPEG")) {
              int qual = 10 * it.getQuality();
              JpegEncoder jc = new JpegEncoder(eImage, qual, os);
              jc.Compress();
            } else if (it.getType().equals("PNG")) {
              PngEncoder png = new PngEncoder(eImage);
              byte[] pngbytes = png.pngEncode();
              os.write(pngbytes);
            } else if (it.getType().equals("BMP")) {
              BMPFile bmp = new BMPFile();
              bmp.saveBitmap(os, eImage);
            } else {

              // Do nothing
            }

            os.flush();
            os.close();

          } catch (IOException exc) {
            status.setStatus(1, "IO Exception:");
            status.setStatus(2, exc.toString());
            System.out.println(exc.toString());
          }
          return;
        }
      }
    }

  }

  class RecentFilesAction extends AbstractAction {

    public RecentFilesAction() {
      super(recentFilesAction);
    }

    public void actionPerformed(ActionEvent e) {

      recentFiles.show();
      String selection = recentFiles.getFile();
      if (selection != null) {
        System.out.println("Recent File: " + selection + " ("
            + recentFiles.getFileType() + ")");
        openFile(new File(selection), recentFiles.getFileType());
      }
    }
  }

  class ScriptAction extends AbstractAction {

    public ScriptAction() {
      super(scriptAction);
    }

    public void actionPerformed(ActionEvent e) {
      scriptWindow.show();
    }
  }

  class PovrayAction extends MoleculeDependentAction {

    public PovrayAction() {
      super(povrayActionProperty);
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
      String basename = currentFileName.substring(0,
                          currentFileName.lastIndexOf("."));
      PovrayDialog pvsd = new PovrayDialog(frame, display, getCurrentFile(),
                            basename);
    }

  }

  /**
   * Returns a new File referenced by the property 'user.dir', or null
   * if the property is not defined.
   *
   * @return  a File to the user directory
   */
  static File getUserDirectory() {
    if (System.getProperty("user.dir") == null) {
      return null;
    }
    return new File(System.getProperty("user.dir"));
  }

  public static final String moleculeProperty = "molecule";

  private abstract class MoleculeDependentAction extends AbstractAction
      implements PropertyChangeListener {

    public MoleculeDependentAction(String name) {
      super(name);
      setEnabled(false);
    }

    public void propertyChange(PropertyChangeEvent event) {

      if (event.getSource() instanceof PublicJmol) {
        PublicJmol jmol = (PublicJmol) event.getSource();
        if (jmol.hasMolecule()) {
          setEnabled(true);
        } else {
          setEnabled(false);
        }
      }
    }

  }

}
