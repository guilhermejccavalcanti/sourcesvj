package processing.app;
import processing.app.contrib.ToolContribution;
import java.util.List;
import java.util.Timer;
import processing.app.syntax.*;
import processing.app.tools.*;
import processing.core.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;

public abstract class Editor extends JFrame implements RunnerListener {
  protected Base base;
  protected EditorState state;
  protected Mode mode;
  static protected final String EMPTY = "                                                                     " + "                                                                     " + "                                                                     ";
  private PageFormat pageFormat;
  private PrinterJob printerJob;
  private JMenu fileMenu;
  private JMenu sketchMenu;
  protected EditorHeader header;
  protected EditorToolbar toolbar;
  protected JEditTextArea textarea;
  protected EditorStatus status;
  protected JSplitPane splitPane;
  protected JPanel consolePanel;
  protected EditorConsole console;
  protected EditorLineStatus lineStatus;
  protected Sketch sketch;
  private Point sketchWindowLocation;
  private JMenuItem undoItem;
  private JMenuItem redoItem;
  protected UndoAction undoAction;
  protected RedoAction redoAction;
  private UndoManager undo;
  private CompoundEdit compoundEdit;
  private Timer timer;
  private TimerTask endUndoEvent;
  private boolean isInserting;
  private final Stack<Integer> caretUndoStack = new Stack<Integer>();
  private final Stack<Integer> caretRedoStack = new Stack<Integer>();
  private FindReplace find;
  JMenu toolsMenu;
  JMenu modeMenu;
  ArrayList<ToolContribution> coreTools;
  public ArrayList<ToolContribution> contribTools;
  protected Editor(final Base base, String path, EditorState state, final Mode mode) {
    super("Processing", state.checkConfig());
    this.base = base;
    this.state = state;
    this.mode = mode;
    Toolkit.setIcon(this);
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          base.handleClose(Editor.this, false);
        }
    });
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
        public void windowActivated(WindowEvent e) {
          base.handleActivated(Editor.this);
          fileMenu.insert(base.getRecentMenu(), 2);
          sketchMenu.insert(mode.getImportMenu(), 4);
          mode.insertToolbarRecentMenu();
          mode.insertToolbarRecentMenu();
        }
        public void windowDeactivated(WindowEvent e) {
          fileMenu.remove(base.getRecentMenu());
          sketchMenu.remove(mode.getImportMenu());
          mode.removeToolbarRecentMenu();
          mode.removeToolbarRecentMenu();
        }
    });
    timer = new Timer();
    buildMenuBar();
    Container contentPain = getContentPane();
    contentPain.setLayout(new BorderLayout());
    JPanel pain = new JPanel();
    pain.setLayout(new BorderLayout());
    contentPain.add(pain, BorderLayout.CENTER);
    Box box = Box.createVerticalBox();
    Box upper = Box.createVerticalBox();
    initModeMenu();
    toolbar = createToolbar();
    upper.add(toolbar);
    header = new EditorHeader(this);
    upper.add(header);
    textarea = createTextArea();
    textarea.setRightClickPopup(new TextAreaPopup());
    textarea.setHorizontalOffset(JEditTextArea.leftHandGutter);
    consolePanel = new JPanel();
    consolePanel.setLayout(new BorderLayout());
    status = new EditorStatus(this);
    consolePanel.add(status, BorderLayout.NORTH);
    console = new EditorConsole(this);
    console.setBorder(null);
    consolePanel.add(console, BorderLayout.CENTER);
    lineStatus = new EditorLineStatus(this);
    consolePanel.add(lineStatus, BorderLayout.SOUTH);
    upper.add(textarea);
    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upper, consolePanel);
    splitPane.setOneTouchExpandable(false);
    splitPane.setContinuousLayout(true);
    splitPane.setResizeWeight(1D);
    splitPane.setBorder(null);
    int dividerSize = Preferences.getInteger("editor.divider.size");
    if (dividerSize != 0) {
      splitPane.setDividerSize(dividerSize);
    }
    box.add(splitPane);
    pain.add(box);
    textarea.addKeyListener(toolbar);
    textarea.addCaretListener(new CaretListener() {
        String lastText = textarea.getText();
        public void caretUpdate(CaretEvent e) {
          String newText = textarea.getText();
          if (lastText.equals(newText) && isDirectEdit()) {
            endTextEditHistory();
          }
          lastText = newText;
        }
    });
    pain.setTransferHandler(new FileDropHandler());
    pack();
    state.apply(this);
    setMinimumSize(new Dimension(Preferences.getInteger("editor.window.width.min"), Preferences.getInteger("editor.window.height.min")));
    applyPreferences();
    addWindowFocusListener(new WindowAdapter() {
        public void windowGainedFocus(WindowEvent e) {
          textarea.requestFocusInWindow();
        }
    });
    boolean loaded = handleOpenInternal(path);
    if (!loaded) {
      sketch = null;
    }
  }
  protected JEditTextArea createTextArea() {
    return new JEditTextArea(new PdeTextAreaDefaults(mode));
  }
  public EditorState getEditorState() {
    return state;
  }
  public void removeRecent() {
    base.removeRecent(this);
  }
  public void addRecent() {
    base.handleRecent(this);
  }
  
  class FileDropHandler extends TransferHandler {
    public boolean canImport(TransferHandler.TransferSupport support, DataFlavor[] flavors) {
      return !sketch.isReadOnly();
    }
    @SuppressWarnings(value = {"unchecked", }) public boolean importData(TransferHandler.TransferSupport support, Transferable transferable) {
      int successful = 0;
      if (!canImport(support)) {
        return false;
      }
      try {
        Transferable transferable = support.getTransferable();
        DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
          java.util.List list = (java.util.List)transferable.getTransferData(DataFlavor.javaFileListFlavor);
          for (int i = 0; i < list.size(); i++) {
            File file = (File)list.get(i);
            if (sketch.addFile(file)) {
              successful++;
            }
          }
        }
        else 
          if (transferable.isDataFlavorSupported(uriListFlavor)) {
            String data = (String)transferable.getTransferData(uriListFlavor);
            String[] pieces = PApplet.splitTokens(data, "\r\n");
            for (int i = 0; i < pieces.length; i++) {
              if (pieces[i].startsWith("#")) 
                continue ;
              String path = null;
              if (pieces[i].startsWith("file:///")) {
                path = pieces[i].substring(7);
              }
              else 
                if (pieces[i].startsWith("file:/")) {
                  path = pieces[i].substring(5);
                }
              if (sketch.addFile(new File(path))) {
                successful++;
              }
            }
          }
      }
      catch (Exception e) {
        Base.showWarning("Drag & Drop Problem", "An error occurred while trying to add files to the sketch.", e);
        return false;
      }
      statusNotice(Language.pluralize("editor.status.drag_and_drop.files_added", successful));
      return true;
    }
  }
  public Base getBase() {
    return base;
  }
  public Mode getMode() {
    return mode;
  }
  protected void initModeMenu() {
    modeMenu = new JMenu();
    ButtonGroup modeGroup = new ButtonGroup();
    for (final Mode m : base.getModeList()) {
      JRadioButtonMenuItem item = new JRadioButtonMenuItem(m.getTitle());
      item.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (!sketch.isModified()) {
              base.changeMode(m);
            }
            else {
              Base.showWarning("Save", "Please save the sketch before changing the mode.", null);
              for (Component c : modeMenu.getPopupMenu().getComponents()) {
                if (c instanceof JRadioButtonMenuItem) {
                  if (((JRadioButtonMenuItem)c).getText() == mode.getTitle()) {
                    ((JRadioButtonMenuItem)c).setSelected(true);
                    break ;
                  }
                }
              }
            }
          }
      });
      modeMenu.add(item);
      modeGroup.add(item);
      if (mode == m) {
        item.setSelected(true);
        item.setSelected(true);
        modeMenu.add(item);
      }
      else {
        JMenuItem item = new JMenuItem(m.getTitle());
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              base.changeMode(m);
            }
        });
        modeMenu.add(item);
      }
    }
    modeMenu.addSeparator();
    JMenuItem addLib = new JMenuItem(Language.text("toolbar.add_mode"));
    addLib.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          base.handleOpenModeManager();
        }
    });
    modeMenu.add(addLib);
    Toolkit.setMenuMnemonics(modeMenu);
  }
  public void rebuildModeMenu() {
    initModeMenu();
  }
  public JMenu getModeMenu() {
    return modeMenu;
  }
  abstract public EditorToolbar createToolbar();
  abstract public Formatter createFormatter();
  protected void setDividerLocation(int pos) {
    splitPane.setDividerLocation(pos);
  }
  protected int getDividerLocation() {
    return splitPane.getDividerLocation();
  }
  protected void applyPreferences() {
    textarea.getPainter().updateAppearance();
    textarea.repaint();
    console.updateAppearance();
  }
  protected void buildMenuBar() {
    JMenuBar menubar = new JMenuBar();
    menubar = new JMenuBar();
    fileMenu = buildFileMenu();
    menubar.add(fileMenu);
    menubar.add(buildEditMenu());
    menubar.add(buildSketchMenu());
    rebuildToolMenu();
    menubar.add(getToolMenu());
    JMenu modeMenu = buildModeMenu();
    if (modeMenu != null) {
      menubar.add(modeMenu);
    }
    menubar.add(buildHelpMenu());
    Toolkit.setMenuMnemonics(menubar);
    setJMenuBar(menubar);
  }
  abstract public JMenu buildFileMenu();
  protected JMenu buildFileMenu(JMenuItem[] exportItems) {
    JMenuItem item;
    JMenu fileMenu = new JMenu(Language.text("menu.file"));
    item = Toolkit.newJMenuItem(Language.text("menu.file.new"), 'N');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          base.handleNew();
        }
    });
    fileMenu.add(item);
    item = Toolkit.newJMenuItem(Language.text("menu.file.open"), 'O');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          base.handleOpenPrompt();
        }
    });
    fileMenu.add(item);
    item = Toolkit.newJMenuItemShift(Language.text("menu.file.sketchbook"), 'K');
    item.addActionListener(new ActionListener() {
        @Override public void actionPerformed(ActionEvent e) {
          mode.showSketchbookFrame();
        }
    });
    fileMenu.add(item);
    item = Toolkit.newJMenuItemShift(Language.text("menu.file.examples"), 'O');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          mode.showExamplesFrame();
        }
    });
    fileMenu.add(item);
    item = Toolkit.newJMenuItem(Language.text("menu.file.close"), 'W');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          base.handleClose(Editor.this, false);
        }
    });
    fileMenu.add(item);
    item = Toolkit.newJMenuItem(Language.text("menu.file.save"), 'S');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handleSave(false);
        }
    });
    fileMenu.add(item);
    item = Toolkit.newJMenuItemShift(Language.text("menu.file.save_as"), 'S');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handleSaveAs();
        }
    });
    fileMenu.add(item);
    if (exportItems != null) {
      for (JMenuItem ei : exportItems) {
        fileMenu.add(ei);
      }
    }
    fileMenu.addSeparator();
    item = Toolkit.newJMenuItemShift(Language.text("menu.file.page_setup"), 'P');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handlePageSetup();
        }
    });
    fileMenu.add(item);
    item = Toolkit.newJMenuItem(Language.text("menu.file.print"), 'P');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handlePrint();
        }
    });
    fileMenu.add(item);
    if (!Base.isMacOS()) {
      fileMenu.addSeparator();
      item = Toolkit.newJMenuItem(Language.text("menu.file.preferences"), ',');
      item.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            base.handlePrefs();
          }
      });
      fileMenu.add(item);
      fileMenu.addSeparator();
      item = Toolkit.newJMenuItem(Language.text("menu.file.quit"), 'Q');
      item.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            base.handleQuit();
          }
      });
      fileMenu.add(item);
    }
    return fileMenu;
  }
  protected JMenu buildEditMenu() {
    JMenu menu = new JMenu(Language.text("menu.edit"));
    JMenuItem item;
    undoItem = Toolkit.newJMenuItem(Language.text("menu.edit.undo"), 'Z');
    undoItem.addActionListener(undoAction = new UndoAction());
    menu.add(undoItem);
    if (Base.isWindows()) {
      redoItem = Toolkit.newJMenuItem(Language.text("menu.edit.redo"), 'Y');
    }
    else {
      redoItem = Toolkit.newJMenuItemShift(Language.text("menu.edit.redo"), 'Z');
    }
    redoItem.addActionListener(redoAction = new RedoAction());
    menu.add(redoItem);
    menu.addSeparator();
    item = Toolkit.newJMenuItem(Language.text("menu.edit.cut"), 'X');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handleCut();
        }
    });
    menu.add(item);
    item = Toolkit.newJMenuItem(Language.text("menu.edit.copy"), 'C');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          textarea.copy();
        }
    });
    menu.add(item);
    item = Toolkit.newJMenuItemShift(Language.text("menu.edit.copy_as_html"), 'C');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handleCopyAsHTML();
        }
    });
    menu.add(item);
    item = Toolkit.newJMenuItem(Language.text("menu.edit.paste"), 'V');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          textarea.paste();
          sketch.setModified(true);
        }
    });
    menu.add(item);
    item = Toolkit.newJMenuItem(Language.text("menu.edit.select_all"), 'A');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          textarea.selectAll();
        }
    });
    menu.add(item);
    menu.addSeparator();
    item = Toolkit.newJMenuItem(Language.text("menu.edit.auto_format"), 'T');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handleAutoFormat();
        }
    });
    menu.add(item);
    item = Toolkit.newJMenuItem(Language.text("menu.edit.comment_uncomment"), '/');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handleCommentUncomment();
        }
    });
    menu.add(item);
    item = Toolkit.newJMenuItem("\u2192 " + Language.text("menu.edit.increase_indent"), ']');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handleIndentOutdent(true);
        }
    });
    menu.add(item);
    item = Toolkit.newJMenuItem("\u2190 " + Language.text("menu.edit.decrease_indent"), '[');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handleIndentOutdent(false);
        }
    });
    menu.add(item);
    menu.addSeparator();
    item = Toolkit.newJMenuItem(Language.text("menu.edit.find"), 'F');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (find == null) {
            find = new FindReplace(Editor.this);
          }
          find.setVisible(true);
        }
    });
    menu.add(item);
    item = Toolkit.newJMenuItem(Language.text("menu.edit.find_next"), 'G');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (find != null) {
            find.findNext();
          }
        }
    });
    menu.add(item);
    item = Toolkit.newJMenuItemShift(Language.text("menu.edit.find_previous"), 'G');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (find != null) {
            find.findPrevious();
          }
        }
    });
    menu.add(item);
    item = Toolkit.newJMenuItemAlt(Language.text("menu.edit.use_selection_for_find"), 'F');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (find == null) {
            find = new FindReplace(Editor.this);
          }
          find.setFindText(getSelectedText());
        }
    });
    menu.add(item);
    return menu;
  }
  abstract public JMenu buildSketchMenu();
  protected JMenu buildSketchMenu(JMenuItem[] runItems) {
    JMenuItem item;
    sketchMenu = new JMenu(Language.text("menu.sketch"));
    for (JMenuItem mi : runItems) {
      sketchMenu.add(mi);
    }
    if (runItems != null) 
      if (runItems.length != 0) 
        sketchMenu.addSeparator();
    sketchMenu.add(mode.getImportMenu());
    item = Toolkit.newJMenuItem(Language.text("menu.sketch.show_sketch_folder"), 'K');
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Base.openFolder(sketch.getFolder());
        }
    });
    sketchMenu.add(item);
    item.setEnabled(Base.openFolderAvailable());
    item = new JMenuItem(Language.text("menu.sketch.add_file"));
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          sketch.handleAddFile();
        }
    });
    sketchMenu.add(item);
    sketchMenu.addSeparator();
    sketchMenu.addMenuListener(new MenuListener() {
        List<JMenuItem> menuList = new ArrayList<JMenuItem>();
        @Override public void menuSelected(MenuEvent event) {
          JMenuItem item;
          for (final Editor editor : base.getEditors()) {
            if (getSketch().getMainFilePath().equals(editor.getSketch().getMainFilePath())) {
              item = new JCheckBoxMenuItem(editor.getSketch().getName());
              item.setSelected(true);
            }
            else {
              item = new JMenuItem(editor.getSketch().getName());
            }
            item.setText(editor.getSketch().getName() + " (" + editor.getMode().getTitle() + ")");
            item.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                  editor.setState(Frame.NORMAL);
                  editor.setVisible(true);
                  editor.toFront();
                }
            });
            sketchMenu.add(item);
            menuList.add(item);
          }
        }
        @Override public void menuDeselected(MenuEvent event) {
          for (JMenuItem item : menuList) {
            sketchMenu.remove(item);
          }
          menuList.clear();
        }
        @Override public void menuCanceled(MenuEvent event) {
          menuDeselected(event);
        }
    });
    return sketchMenu;
  }
  abstract public void handleImportLibrary(String jarPath);
  public JMenu getToolMenu() {
    if (toolsMenu == null) {
      rebuildToolMenu();
    }
    return toolsMenu;
  }
  public void rebuildToolMenu() {
    if (toolsMenu == null) {
      toolsMenu = new JMenu(Language.text("menu.tools"));
    }
    else {
      toolsMenu.removeAll();
    }
    coreTools = ToolContribution.loadAll(Base.getToolsFolder());
    contribTools = ToolContribution.loadAll(Base.getSketchbookToolsFolder());
    addInternalTools(toolsMenu);
    addTools(toolsMenu, coreTools);
    addTools(toolsMenu, contribTools);
    toolsMenu.addSeparator();
    JMenuItem item = new JMenuItem(Language.text("menu.tools.add_tool"));
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          base.handleOpenToolManager();
        }
    });
    toolsMenu.add(item);
  }
  public void clearToolMenu() {
    toolsMenu.removeAll();
    System.gc();
  }
  public void removeTool() {
    rebuildToolMenu();
  }
  void addToolItem(final Tool tool, HashMap<String, JMenuItem> toolItems) {
    String title = tool.getMenuTitle();
    final JMenuItem item = new JMenuItem(title);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            tool.run();
          }
          catch (NoSuchMethodError nsme) {
            statusError("\"" + tool.getMenuTitle() + "\" is not" + "compatible with this version of Processing");
            Base.log("Incompatible tool found during tool.run()", nsme);
            item.setEnabled(false);
          }
          catch (Exception ex) {
            statusError("An error occurred inside \"" + tool.getMenuTitle() + "\"");
            ex.printStackTrace();
            item.setEnabled(false);
          }
        }
    });
    toolItems.put(title, item);
  }
  protected void addTools(JMenu menu, ArrayList<ToolContribution> tools) {
    HashMap<String, JMenuItem> toolItems = new HashMap<String, JMenuItem>();
    for (final ToolContribution tool : tools) {
      try {
        tool.init(Editor.this);
        addToolItem(tool, toolItems);
      }
      catch (NoSuchMethodError nsme) {
        System.err.println("\"" + tool.getMenuTitle() + "\" is not " + "compatible with this version of Processing");
        System.err.println("The " + nsme.getMessage() + " method no longer exists.");
        Base.log("Incompatible Tool found during tool.init()", nsme);
      }
      catch (NoClassDefFoundError ncdfe) {
        System.err.println("\"" + tool.getMenuTitle() + "\" is not " + "compatible with this version of Processing");
        System.err.println("The " + ncdfe.getMessage() + " class is no longer available.");
        Base.log("Incompatible Tool found during tool.init()", ncdfe);
      }
      catch (Error err) {
        System.err.println("An error occurred inside \"" + tool.getMenuTitle() + "\"");
        err.printStackTrace();
      }
      catch (Exception ex) {
        System.err.println("An exception occurred inside \"" + tool.getMenuTitle() + "\"");
        ex.printStackTrace();
      }
    }
    ArrayList<String> toolList = new ArrayList<String>(toolItems.keySet());
    if (toolList.size() > 0) {
      menu.addSeparator();
      Collections.sort(toolList);
      for (String title : toolList) {
        menu.add(toolItems.get(title));
      }
    }
  }
  public JMenu buildModeMenu() {
    return null;
  }
  protected void addToolMenuItem(JMenu menu, String className) {
    try {
      Class<?> toolClass = Class.forName(className);
      final Tool tool = (Tool)toolClass.newInstance();
      JMenuItem item = new JMenuItem(tool.getMenuTitle());
      tool.init(Editor.this);
      item.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            EventQueue.invokeLater(tool);
          }
      });
      menu.add(item);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  protected JMenu addInternalTools(JMenu menu) {
    addToolMenuItem(menu, "processing.app.tools.CreateFont");
    addToolMenuItem(menu, "processing.app.tools.ColorSelector");
    addToolMenuItem(menu, "processing.app.tools.Archiver");
    if (Base.isMacOS()) {
      addToolMenuItem(menu, "processing.app.tools.InstallCommander");
    }
    return menu;
  }
  abstract public JMenu buildHelpMenu();
  public void showReference(String filename) {
    File file = new File(mode.getReferenceFolder(), filename);
    try {
      file = file.getCanonicalFile();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    Base.openURL(file.toURI().toString());
  }
  static public void showChanges() {
    if (!Base.isCommandLine()) {
      Base.openURL("http://wiki.processing.org/w/Changes");
    }
  }
  
  class UndoAction extends AbstractAction {
    public UndoAction() {
      super(Language.text("menu.edit.undo"));
      this.setEnabled(false);
    }
    public void actionPerformed(ActionEvent e) {
      stopCompoundEdit();
      try {
        final Integer caret = caretUndoStack.pop();
        caretRedoStack.push(caret);
        textarea.setCaretPosition(caret);
        textarea.scrollToCaret();
      }
      catch (Exception ignore) {
      }
      try {
        undo.undo();
      }
      catch (CannotUndoException ex) {
      }
      updateUndoState();
      redoAction.updateRedoState();
      if (sketch != null) {
        sketch.setModified(!getText().equals(sketch.getCurrentCode().getSavedProgram()));
      }
    }
    protected void updateUndoState() {
      if (undo.canUndo() || compoundEdit != null && compoundEdit.isInProgress()) {
        this.setEnabled(true);
        undoItem.setEnabled(true);
        undoItem.setText(undo.getUndoPresentationName());
        putValue(Action.NAME, undo.getUndoPresentationName());
      }
      else {
        this.setEnabled(false);
        undoItem.setEnabled(false);
        undoItem.setText(Language.text("menu.edit.undo"));
        putValue(Action.NAME, Language.text("menu.edit.undo"));
      }
    }
  }
  
  class RedoAction extends AbstractAction {
    public RedoAction() {
      super(Language.text("menu.edit.redo"));
      this.setEnabled(false);
    }
    public void actionPerformed(ActionEvent e) {
      stopCompoundEdit();
      try {
        undo.redo();
      }
      catch (CannotRedoException ex) {
      }
      try {
        final Integer caret = caretRedoStack.pop();
        caretUndoStack.push(caret);
        textarea.setCaretPosition(caret);
      }
      catch (Exception ignore) {
      }
      updateRedoState();
      undoAction.updateUndoState();
      if (sketch != null) {
        sketch.setModified(!getText().equals(sketch.getCurrentCode().getSavedProgram()));
      }
    }
    protected void updateRedoState() {
      if (undo.canRedo()) {
        redoItem.setEnabled(true);
        redoItem.setText(undo.getRedoPresentationName());
        putValue(Action.NAME, undo.getRedoPresentationName());
      }
      else {
        this.setEnabled(false);
        redoItem.setEnabled(false);
        redoItem.setText(Language.text("menu.edit.redo"));
        putValue(Action.NAME, Language.text("menu.edit.redo"));
      }
    }
  }
  public Sketch getSketch() {
    return sketch;
  }
  public JEditTextArea getTextArea() {
    return textarea;
  }
  public String getText() {
    return textarea.getText();
  }
  public String getText(int start, int stop) {
    return textarea.getText(start, stop - start);
  }
  public void setText(String what) {
    startCompoundEdit();
    textarea.setText(what);
    stopCompoundEdit();
  }
  public void insertText(String what) {
    startCompoundEdit();
    int caret = getCaretOffset();
    setSelection(caret, caret);
    textarea.setSelectedText(what);
    stopCompoundEdit();
  }
  public String getSelectedText() {
    return textarea.getSelectedText();
  }
  public void setSelectedText(String what) {
    textarea.setSelectedText(what);
  }
  public void setSelection(int start, int stop) {
    start = PApplet.constrain(start, 0, textarea.getDocumentLength());
    stop = PApplet.constrain(stop, 0, textarea.getDocumentLength());
    textarea.select(start, stop);
  }
  public int getCaretOffset() {
    return textarea.getCaretPosition();
  }
  public boolean isSelectionActive() {
    return textarea.isSelectionActive();
  }
  public int getSelectionStart() {
    return textarea.getSelectionStart();
  }
  public int getSelectionStop() {
    return textarea.getSelectionStop();
  }
  public String getLineText(int line) {
    return textarea.getLineText(line);
  }
  public void setLineText(int line, String what) {
    startCompoundEdit();
    textarea.select(getLineStartOffset(line), getLineStopOffset(line));
    textarea.setSelectedText(what);
    stopCompoundEdit();
  }
  public int getLineStartOffset(int line) {
    return textarea.getLineStartOffset(line);
  }
  public int getLineStopOffset(int line) {
    return textarea.getLineStopOffset(line);
  }
  public int getLineCount() {
    return textarea.getLineCount();
  }
  public void startCompoundEdit() {
    stopCompoundEdit();
    compoundEdit = new CompoundEdit();
  }
  public void stopCompoundEdit() {
    if (compoundEdit != null) {
      compoundEdit.end();
      undo.addEdit(compoundEdit);
      caretUndoStack.push(textarea.getCaretPosition());
      caretRedoStack.clear();
      undoAction.updateUndoState();
      redoAction.updateRedoState();
      compoundEdit = null;
    }
  }
  public int getScrollPosition() {
    return textarea.getVerticalScrollPosition();
  }
  protected void setCode(SketchCode code) {
    SyntaxDocument document = (SyntaxDocument)code.getDocument();
    if (document == null) {
      document = new SyntaxDocument();
      code.setDocument(document);
      document.setTokenMarker(mode.getTokenMarker());
      try {
        document.insertString(0, code.getProgram(), null);
      }
      catch (BadLocationException bl) {
        bl.printStackTrace();
      }
      document.addDocumentListener(new DocumentListener() {
          public void removeUpdate(DocumentEvent e) {
            if (isInserting && isDirectEdit()) {
              endTextEditHistory();
            }
            isInserting = false;
          }
          public void insertUpdate(DocumentEvent e) {
            if (!isInserting && isDirectEdit()) {
              endTextEditHistory();
            }
            isInserting = true;
          }
          public void changedUpdate(DocumentEvent e) {
            endTextEditHistory();
          }
      });
      document.addUndoableEditListener(new UndoableEditListener() {
          public void undoableEditHappened(UndoableEditEvent e) {
            if (endUndoEvent != null) {
              endUndoEvent.cancel();
              endUndoEvent = null;
              startTimerEvent();
            }
            if (compoundEdit == null) {
              startCompoundEdit();
              startTimerEvent();
            }
            compoundEdit.addEdit(e.getEdit());
            undoAction.updateUndoState();
            redoAction.updateRedoState();
          }
      });
    }
    textarea.setDocument(document, code.getSelectionStart(), code.getSelectionStop(), code.getScrollPosition());
    textarea.requestFocusInWindow();
    this.undo = code.getUndo();
    undoAction.updateUndoState();
    redoAction.updateRedoState();
  }
  boolean isDirectEdit() {
    return endUndoEvent != null;
  }
  void startTimerEvent() {
    endUndoEvent = new TimerTask() {
        public void run() {
          endTextEditHistory();
        }
    };
    timer.schedule(endUndoEvent, 3000);
    timer.purge();
  }
  void endTextEditHistory() {
    if (endUndoEvent != null) {
      endUndoEvent.cancel();
      endUndoEvent = null;
    }
    stopCompoundEdit();
  }
  public void handleCut() {
    textarea.cut();
    sketch.setModified(true);
  }
  public void handleCopy() {
    textarea.copy();
  }
  public void handleCopyAsHTML() {
    textarea.copyAsHTML();
    statusNotice("Code formatted as HTML has been copied to the clipboard.");
  }
  public void handlePaste() {
    textarea.paste();
    sketch.setModified(true);
  }
  public void handleSelectAll() {
    textarea.selectAll();
  }
  public void handleAutoFormat() {
    final String source = getText();
    try {
      final String formattedText = createFormatter().format(source);
      int selectionEnd = getSelectionStop();
      if (formattedText.length() < selectionEnd - 1) {
        selectionEnd = formattedText.length() - 1;
      }
      if (formattedText.equals(source)) {
        statusNotice(Language.text("editor.status.autoformat.no_changes"));
      }
      else {
        int scrollPos = textarea.getVerticalScrollPosition();
        setText(formattedText);
        setSelection(selectionEnd, selectionEnd);
        if (scrollPos != textarea.getVerticalScrollPosition()) {
          textarea.setVerticalScrollPosition(scrollPos);
        }
        getSketch().setModified(true);
        statusNotice(Language.text("editor.status.autoformat.finished"));
      }
    }
    catch (final Exception e) {
      statusError(e);
    }
  }
  abstract public String getCommentPrefix();
  protected void handleCommentUncomment() {
    startCompoundEdit();
    String prefix = getCommentPrefix();
    int prefixLen = prefix.length();
    int startLine = textarea.getSelectionStartLine();
    int stopLine = textarea.getSelectionStopLine();
    int lastLineStart = textarea.getLineStartOffset(stopLine);
    int selectionStop = textarea.getSelectionStop();
    if (selectionStop == lastLineStart) {
      if (textarea.isSelectionActive()) {
        stopLine--;
      }
    }
    int length = textarea.getDocumentLength();
    boolean commented = true;
    for (int i = startLine; commented && (i <= stopLine); i++) {
      int pos = textarea.getLineStartOffset(i);
      if (pos + prefixLen > length) {
        commented = false;
      }
      else {
        String begin = textarea.getText(pos, prefixLen);
        commented = begin.equals(prefix);
      }
    }
    for (int line = startLine; line <= stopLine; line++) {
      int location = textarea.getLineStartOffset(line);
      if (commented) {
        textarea.select(location, location + prefixLen);
        if (textarea.getSelectedText().equals(prefix)) {
          textarea.setSelectedText("");
        }
      }
      else {
        textarea.select(location, location);
        textarea.setSelectedText(prefix);
      }
    }
    textarea.select(textarea.getLineStartOffset(startLine), textarea.getLineStopOffset(stopLine) - 1);
    stopCompoundEdit();
    sketch.setModified(true);
  }
  public void handleIndent() {
    handleIndentOutdent(true);
  }
  public void handleOutdent() {
    handleIndentOutdent(false);
  }
  public void handleIndentOutdent(boolean indent) {
    int tabSize = Preferences.getInteger("editor.tabs.size");
    String tabString = Editor.EMPTY.substring(0, tabSize);
    startCompoundEdit();
    int startLine = textarea.getSelectionStartLine();
    int stopLine = textarea.getSelectionStopLine();
    int lastLineStart = textarea.getLineStartOffset(stopLine);
    int selectionStop = textarea.getSelectionStop();
    if (selectionStop == lastLineStart) {
      if (textarea.isSelectionActive()) {
        stopLine--;
      }
    }
    for (int line = startLine; line <= stopLine; line++) {
      int location = textarea.getLineStartOffset(line);
      if (indent) {
        textarea.select(location, location);
        textarea.setSelectedText(tabString);
      }
      else {
        int last = Math.min(location + tabSize, textarea.getDocumentLength());
        textarea.select(location, last);
        if (textarea.getSelectedText().equals(tabString)) {
          textarea.setSelectedText("");
        }
      }
    }
    textarea.select(textarea.getLineStartOffset(startLine), textarea.getLineStopOffset(stopLine) - 1);
    stopCompoundEdit();
    sketch.setModified(true);
  }
  static public boolean checkParen(char[] array, int index, int stop) {
    while (index < stop){
      switch (array[index]){
        case '(':
        return true;
        case ' ':
        case '\t':
        case '\n':
        case '\r':
        index++;
        break ;
        default:
        return false;
      }
    }
    return false;
  }
  protected boolean functionable(char c) {
    return (c == '_') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
  }
  protected String referenceCheck(boolean selectIfFound) {
    int start = textarea.getSelectionStart();
    int stop = textarea.getSelectionStop();
    if (stop < start) {
      int temp = stop;
      stop = start;
      start = temp;
    }
    char[] c = textarea.getText().toCharArray();
    if (start == stop) {
      while (start > 0 && functionable(c[start - 1])){
        start--;
      }
      while (stop < c.length && functionable(c[stop])){
        stop++;
      }
    }
    String text = new String(c, start, stop - start).trim();
    if (checkParen(c, stop, c.length)) {
      text += "_";
    }
    String ref = mode.lookupReference(text);
    if (selectIfFound) {
      textarea.select(start, stop);
    }
    return ref;
  }
  protected void handleFindReference() {
    String ref = referenceCheck(true);
    if (ref != null) {
      showReference(ref + ".html");
    }
    else {
      String text = textarea.getSelectedText().trim();
      if (text.length() == 0) {
        statusNotice(Language.text("editor.status.find_reference.select_word_first"));
      }
      else {
        statusNotice(Language.interpolate("editor.status.find_reference.not_available", text));
      }
    }
  }
  public void setSketchLocation(Point p) {
    sketchWindowLocation = p;
  }
  public Point getSketchLocation() {
    return sketchWindowLocation;
  }
  protected boolean checkModified() {
    if (!sketch.isModified()) 
      return true;
    toFront();
    String prompt = "Save changes to " + sketch.getName() + "?  ";
    if (!Base.isMacOS()) {
      int result = JOptionPane.showConfirmDialog(this, prompt, "Close", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (result == JOptionPane.YES_OPTION) {
        return handleSave(true);
      }
      else 
        if (result == JOptionPane.NO_OPTION) {
          return true;
        }
        else 
          if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
            return false;
          }
          else {
            throw new IllegalStateException();
          }
    }
    else {
      JOptionPane pane = new JOptionPane("<html> " + "<head> <style type=\"text/css\">" + "b { font: 13pt \"Lucida Grande\" }" + "p { font: 11pt \"Lucida Grande\"; margin-top: 8px }" + "</style> </head>" + "<b>" + Language.text("save.title") + "</b>" + "<p>" + Language.text("save.hint") + "</p>", JOptionPane.QUESTION_MESSAGE);
      String[] options = new String[]{ Language.text("save.btn.save"), Language.text("prompt.cancel"), Language.text("save.btn.dont_save") } ;
      pane.setOptions(options);
      pane.setInitialValue(options[0]);
      pane.putClientProperty("Quaqua.OptionPane.destructiveOption", new Integer(2));
      JDialog dialog = pane.createDialog(this, null);
      dialog.setVisible(true);
      Object result = pane.getValue();
      if (result == options[0]) {
        return handleSave(true);
      }
      else 
        if (result == options[2]) {
          return true;
        }
        else {
          return false;
        }
    }
  }
  protected boolean handleOpenInternal(String path) {
    final File file = new File(path);
    final File parentFile = new File(file.getParent());
    final String parentName = parentFile.getName();
    final String defaultName = parentName + "." + mode.getDefaultExtension();
    final File altFile = new File(file.getParent(), defaultName);
    if (defaultName.equals(file.getName())) {
    }
    else 
      if (altFile.exists()) {
        path = altFile.getAbsolutePath();
      }
      else 
        if (!mode.canEdit(file)) {
          final String modeName = (mode.getTitle().equals("Java")) ? "Processing" : mode.getTitle();
          Base.showWarning("Bad file selected", modeName + " can only open its own sketches\nand other files ending in " + mode.getDefaultExtension(), null);
          return false;
        }
        else {
          final String properParent = file.getName().substring(0, file.getName().lastIndexOf('.'));
          Object[] options = { Language.text("prompt.ok"), Language.text("prompt.cancel") } ;
          String prompt = "The file \"" + file.getName() + "\" needs to be inside\n" + "a sketch folder named \"" + properParent + "\".\n" + "Create this folder, move the file, and continue?";
          int result = JOptionPane.showOptionDialog(this, prompt, "Moving", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
          if (result == JOptionPane.YES_OPTION) {
            File properFolder = new File(file.getParent(), properParent);
            if (properFolder.exists()) {
              Base.showWarning("Error", "A folder named \"" + properParent + "\" " + "already exists. Can\'t open sketch.", null);
              return false;
            }
            if (!properFolder.mkdirs()) {
              Base.showWarning("Error", "Could not create the sketch folder.", null);
              return false;
            }
            File properPdeFile = new File(properFolder, file.getName());
            File origPdeFile = new File(path);
            try {
              Base.copyFile(origPdeFile, properPdeFile);
            }
            catch (IOException e) {
              Base.showWarning("Error", "Could not copy to a proper location.", e);
              return false;
            }
            origPdeFile.delete();
            path = properPdeFile.getAbsolutePath();
          }
          else 
            if (result == JOptionPane.NO_OPTION) {
              return false;
            }
        }
    try {
      sketch = new Sketch(path, this);
    }
    catch (IOException e) {
      Base.showWarning("Error", "Could not create the sketch.", e);
      return false;
    }
    if (Preferences.getBoolean("editor.watcher")) {
      initFileChangeListener();
    }
    header.rebuild();
    updateTitle();
    Preferences.save();
    return true;
  }
  private boolean watcherSave;
  private boolean watcherReloaded;
  private WatchKey watcherKey = null;
  private void initFileChangeListener() {
    try {
      WatchService watchService = FileSystems.getDefault().newWatchService();
      Path folderPath = sketch.getFolder().toPath();
      watcherKey = folderPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    final WatchKey finKey = watcherKey;
    if (finKey != null) {
      addWindowFocusListener(new WindowFocusListener() {
          @Override public void windowGainedFocus(WindowEvent arg0) {
            if (finKey.isValid()) {
              List<WatchEvent<?>> events = finKey.pollEvents();
              processFileEvents(events);
            }
          }
          @Override public void windowLostFocus(WindowEvent arg0) {
            List<WatchEvent<?>> events = finKey.pollEvents();
            if (!watcherSave) {
              processFileEvents(events);
            }
            watcherSave = false;
          }
      });
    }
  }
  private void processFileEvents(List<WatchEvent<?>> events) {
    watcherReloaded = false;
    for (WatchEvent<?> e : events) {
      if (watcherReloaded) {
        break ;
      }
      if (e.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
        int response = Base.showYesNoQuestion(Editor.this, "File Modified", "A file has been modified externally", "Would you like to reload the sketch?");
        if (response == 0) {
          sketch.reload();
          header.rebuild();
          watcherReloaded = true;
        }
      }
      else {
      }
    }
    watcherSave = false;
  }
  public void updateTitle() {
    setTitle(sketch.getName() + " | Processing " + Base.getVersionName());
    if (!sketch.isUntitled()) {
      File sketchFile = sketch.getMainFile();
      getRootPane().putClientProperty("Window.documentFile", sketchFile);
    }
    else {
      getRootPane().putClientProperty("Window.documentFile", null);
    }
  }
  public boolean handleSave(boolean immediately) {
    watcherSave = true;
    if (sketch.isUntitled()) {
      return handleSaveAs();
    }
    else 
      if (immediately) {
        handleSaveImpl();
      }
      else {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
              handleSaveImpl();
            }
        });
      }
    return true;
  }
  protected void handleSaveImpl() {
    statusNotice(Language.text("editor.status.saving"));
    try {
      if (sketch.save()) {
        statusNotice(Language.text("editor.status.saving.done"));
      }
      else {
        statusEmpty();
      }
    }
    catch (Exception e) {
      statusError(e);
    }
  }
  public boolean handleSaveAs() {
    statusNotice(Language.text("editor.status.saving"));
    try {
      if (sketch.saveAs()) {
        statusNotice("Done Saving.");
      }
      else {
        statusNotice(Language.text("editor.status.saving.canceled"));
        return false;
      }
    }
    catch (Exception e) {
      statusError(e);
    }
    return true;
  }
  public void handlePageSetup() {
    if (printerJob == null) {
      printerJob = PrinterJob.getPrinterJob();
    }
    if (pageFormat == null) {
      pageFormat = printerJob.defaultPage();
    }
    pageFormat = printerJob.pageDialog(pageFormat);
  }
  public void handlePrint() {
    statusNotice(Language.text("editor.status.printing"));
    if (printerJob == null) {
      printerJob = PrinterJob.getPrinterJob();
    }
    if (pageFormat != null) {
      printerJob.setPrintable(textarea.getPrintable(), pageFormat);
    }
    else {
      printerJob.setPrintable(textarea.getPrintable());
    }
    printerJob.setJobName(sketch.getCurrentCode().getPrettyName());
    if (printerJob.printDialog()) {
      try {
        printerJob.print();
        statusNotice(Language.text("editor.status.printing.done"));
      }
      catch (PrinterException pe) {
        statusError(Language.text("editor.status.printing.error"));
        pe.printStackTrace();
      }
    }
    else {
      statusNotice(Language.text("editor.status.printing.canceled"));
    }
  }
  public void prepareRun() {
    internalCloseRunner();
    statusEmpty();
    for (int i = 0; i < 10; i++) 
      System.out.println();
    if (Preferences.getBoolean("console.auto_clear")) {
      console.clear();
    }
    sketch.ensureExistence();
    sketch.getCurrentCode().setProgram(getText());
  }
  abstract public void internalCloseRunner();
  abstract public void deactivateRun();
  public void statusError(String what) {
    status.error(what);
  }
  public void statusError(Exception e) {
    e.printStackTrace();
    if (e instanceof SketchException) {
      SketchException re = (SketchException)e;
      if (re.hasCodeIndex()) {
        sketch.setCurrentCode(re.getCodeIndex());
      }
      if (re.hasCodeLine()) {
        int line = re.getCodeLine();
        if (line >= textarea.getLineCount()) {
          line = textarea.getLineCount() - 1;
          if (textarea.getLineText(line).length() == 0) {
            line--;
          }
        }
        if (line < 0 || line >= textarea.getLineCount()) {
          System.err.println("Bad error line: " + line);
        }
        else {
          textarea.select(textarea.getLineStartOffset(line), textarea.getLineStopOffset(line) - 1);
        }
      }
    }
    String mess = e.getMessage();
    if (mess != null) {
      String javaLang = "java.lang.";
      if (mess.indexOf(javaLang) == 0) {
        mess = mess.substring(javaLang.length());
      }
      String rxString = "RuntimeException: ";
      if (mess.startsWith(rxString)) {
        mess = mess.substring(rxString.length());
      }
      statusError(mess);
    }
  }
  public void statusNotice(String msg) {
    status.notice(msg);
  }
  public void clearNotice(String msg) {
    if (status.message.equals(msg)) {
      statusEmpty();
    }
  }
  public String getStatusMessage() {
    return status.message;
  }
  public int getStatusMode() {
    return status.mode;
  }
  public void statusEmpty() {
    statusNotice(EMPTY);
  }
  public void startIndeterminate() {
    status.startIndeterminate();
  }
  public void stopIndeterminate() {
    status.stopIndeterminate();
  }
  public void statusHalt() {
  }
  public boolean isHalted() {
    return false;
  }
  
  class TextAreaPopup extends JPopupMenu {
    JMenuItem cutItem;
    JMenuItem copyItem;
    JMenuItem discourseItem;
    JMenuItem pasteItem;
    JMenuItem selectAllItem;
    JMenuItem commUncommItem;
    JMenuItem incIndItem;
    JMenuItem decIndItem;
    JMenuItem referenceItem;
    public TextAreaPopup() {
      cutItem = new JMenuItem(Language.text("menu.edit.cut"));
      cutItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handleCut();
          }
      });
      this.add(cutItem);
      copyItem = new JMenuItem(Language.text("menu.edit.copy"));
      copyItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handleCopy();
          }
      });
      this.add(copyItem);
      discourseItem = new JMenuItem(Language.text("menu.edit.copy_as_html"));
      discourseItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handleCopyAsHTML();
          }
      });
      this.add(discourseItem);
      pasteItem = new JMenuItem(Language.text("menu.edit.paste"));
      pasteItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handlePaste();
          }
      });
      this.add(pasteItem);
      selectAllItem = new JMenuItem(Language.text("menu.edit.select_all"));
      selectAllItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handleSelectAll();
          }
      });
      this.add(selectAllItem);
      this.addSeparator();
      commUncommItem = new JMenuItem(Language.text("menu.edit.comment_uncomment"));
      commUncommItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handleCommentUncomment();
          }
      });
      this.add(commUncommItem);
      incIndItem = new JMenuItem("\u2192 " + Language.text("menu.edit.increase_indent"));
      incIndItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handleIndentOutdent(true);
          }
      });
      this.add(incIndItem);
      decIndItem = new JMenuItem("\u2190 " + Language.text("menu.edit.decrease_indent"));
      decIndItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handleIndentOutdent(false);
          }
      });
      this.add(decIndItem);
      this.addSeparator();
      referenceItem = new JMenuItem(Language.text("find_in_reference"));
      referenceItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            handleFindReference();
          }
      });
      this.add(referenceItem);
      Toolkit.setMenuMnemonics(cutItem, copyItem, discourseItem, pasteItem, selectAllItem, commUncommItem, incIndItem, decIndItem, referenceItem);
    }
    public void show(Component component, int x, int y) {
      boolean active = textarea.isSelectionActive();
      cutItem.setEnabled(active);
      copyItem.setEnabled(active);
      discourseItem.setEnabled(active);
      referenceItem.setEnabled(referenceCheck(false) != null);
      super.show(component, x, y);
    }
  }
}

