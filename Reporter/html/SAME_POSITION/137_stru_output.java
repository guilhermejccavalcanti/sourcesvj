package processing.core;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JFrame;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class PSurfaceAWT implements PSurface {
  GraphicsDevice displayDevice;
  boolean resizable;
  JFrame frame;
  Rectangle screenRect;
  boolean useActive = true;
  boolean useStrategy = false;
  Canvas canvas;
  PApplet sketch;
  PGraphics graphics;
  int sketchWidth;
  int sketchHeight;
  Thread thread;
  boolean paused;
  Object pauseObject = new Object();
  protected float frameRateTarget = 60;
  protected long frameRatePeriod = 1000000000L / 60L;
  public PSurfaceAWT(PGraphics graphics) {
    this.graphics = graphics;
    if (checkRetina()) {
      useActive = false;
    }
    createCanvas();
    addListeners();
  }
  void createCanvas() {
    canvas = new SmoothCanvas();
    canvas.setIgnoreRepaint(true);
    canvas.setFocusTraversalKeysEnabled(false);
    canvas.addComponentListener(new ComponentAdapter() {
        @Override public void componentResized(ComponentEvent e) {
          if (!sketch.looping) {
            sketch.redraw();
          }
        }
    });
  }
  public void requestFocus() {
    EventQueue.invokeLater(new Runnable() {
        public void run() {
          if (canvas != null) {
            canvas.requestFocusInWindow();
          }
        }
    });
  }
  
  class SmoothCanvas extends Canvas {
    private Dimension oldSize = new Dimension(0, 0);
    private Dimension newSize = new Dimension(0, 0);
    @Override public Dimension getPreferredSize() {
      return new Dimension(sketchWidth, sketchHeight);
    }
    @Override public Dimension getMinimumSize() {
      return getPreferredSize();
    }
    @Override public Dimension getMaximumSize() {
      return resizable ? super.getMaximumSize() : getPreferredSize();
    }
    @Override public void validate() {
      super.validate();
      newSize.width = getWidth();
      newSize.height = getHeight();
      if (oldSize.equals(newSize)) {
        return ;
      }
      else {
        oldSize = newSize;
        render();
      }
    }
    @Override public void update(Graphics g) {
      paint(g);
    }
    @Override public void paint(Graphics g) {
      render();
    }
    @Override public void addNotify() {
      super.addNotify();
    }
    protected synchronized void render() {
      if (!canvas.isDisplayable()) {
        return ;
      }
      if (canvas.getBufferStrategy() == null) {
        canvas.createBufferStrategy(2);
      }
      BufferStrategy strategy = canvas.getBufferStrategy();
      if (strategy == null) {
        return ;
      }
      do {
        do {
          Graphics draw = strategy.getDrawGraphics();
          draw.drawImage(graphics.image, 0, 0, sketchWidth, sketchHeight, null);
          draw.dispose();
        }while (strategy.contentsRestored());
        strategy.show();
      }while (strategy.contentsLost());
    }
  }
  public void blit() {
    ((SmoothCanvas)canvas).render();
  }
  public void initOffscreen() {
  }
  public Canvas initCanvas(PApplet sketch) {
    this.sketch = sketch;
    sketchWidth = sketch.sketchWidth();
    sketchHeight = sketch.sketchHeight();
    return canvas;
  }
  public Frame initFrame(PApplet sketch, Color backgroundColor, int deviceIndex, boolean fullScreen, boolean spanDisplays) {
    this.sketch = sketch;
    GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if (deviceIndex >= 0) {
      GraphicsDevice[] devices = environment.getScreenDevices();
      if (deviceIndex < devices.length) {
        displayDevice = devices[deviceIndex];
      }
      else {
        System.err.format("Display %d does not exist, " + "using the default display instead.", deviceIndex);
        for (int i = 0; i < devices.length; i++) {
          System.err.format("Display %d is %s\n", i, devices[i]);
        }
      }
    }
    if (displayDevice == null) {
      displayDevice = environment.getDefaultScreenDevice();
    }
    screenRect = spanDisplays ? getDisplaySpan() : displayDevice.getDefaultConfiguration().getBounds();
    sketch.displayWidth = screenRect.width;
    sketch.displayHeight = screenRect.height;
    sketchWidth = sketch.sketchWidth();
    sketchHeight = sketch.sketchHeight();
    if (screenRect.width == sketchWidth && screenRect.height == sketchHeight) {
      fullScreen = true;
    }
    if (fullScreen || spanDisplays) {
      sketchWidth = screenRect.width;
      sketchHeight = screenRect.height;
    }
    frame = new JFrame(displayDevice.getDefaultConfiguration());
    if (backgroundColor == null) {
      backgroundColor = WINDOW_BGCOLOR;
    }
    frame.getContentPane().setBackground(backgroundColor);
    setIconImage(frame);
    frame.add(canvas);
    setSize(sketchWidth, sketchHeight);
    if (fullScreen) {
      PApplet.hideMenuBar();
      frame.setUndecorated(true);
      frame.setBounds(screenRect);
      frame.setVisible(true);
    }
    frame.setLayout(null);
    if (fullScreen) {
      frame.invalidate();
    }
    else {
    }
    frame.setResizable(false);
    return frame;
  }
  public void setTitle(String title) {
    frame.setTitle(title);
  }
  public void setResizable(boolean resizable) {
    this.resizable = resizable;
    if (frame != null) {
      frame.setResizable(resizable);
    }
  }
  public void setVisible(boolean visible) {
    frame.setVisible(visible);
    if (visible && PApplet.platform == PConstants.LINUX) {
      if (PApplet.platform == PConstants.LINUX) {
        Insets insets = frame.getInsets();
        frame.setSize(Math.max(sketchWidth, MIN_WINDOW_WIDTH) + insets.left + insets.right, Math.max(sketchHeight, MIN_WINDOW_HEIGHT) + insets.top + insets.bottom);
      }
    }
  }
  public void placePresent(Color stopColor) {
    frame.setBounds(screenRect);
    canvas.setBounds((screenRect.width - sketchWidth) / 2, (screenRect.height - sketchHeight) / 2, sketchWidth, sketchHeight);
    if (stopColor != null) {
      Label label = new Label("stop");
      label.setForeground(stopColor);
      label.addMouseListener(new MouseAdapter() {
          @Override public void mousePressed(java.awt.event.MouseEvent e) {
            sketch.exit();
          }
      });
      frame.add(label);
      Dimension labelSize = label.getPreferredSize();
      labelSize = new Dimension(100, labelSize.height);
      label.setSize(labelSize);
      label.setLocation(20, screenRect.height - labelSize.height - 20);
    }
  }
  public void placeWindow(int[] location) {
    setFrameSize();
    if (location != null) {
      frame.setLocation(location[0], location[1]);
    }
    else {
      frame.setLocation(screenRect.x + (screenRect.width - sketchWidth) / 2, screenRect.y + (screenRect.height - sketchHeight) / 2);
    }
    Point frameLoc = frame.getLocation();
    if (frameLoc.y < 0) {
      frame.setLocation(frameLoc.x, 30);
    }
    setCanvasSize();
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
    });
    setupFrameResizeListener();
    if (sketch.getGraphics().displayable()) {
      setVisible(true);
    }
  }
  private void setCanvasSize() {
    int contentW = Math.max(sketchWidth, MIN_WINDOW_WIDTH);
    int contentH = Math.max(sketchHeight, MIN_WINDOW_HEIGHT);
    canvas.setBounds((contentW - sketchWidth) / 2, (contentH - sketchHeight) / 2, sketchWidth, sketchHeight);
  }
  private Dimension setFrameSize() {
    Insets insets = frame.getInsets();
    int windowW = Math.max(sketchWidth, MIN_WINDOW_WIDTH) + insets.left + insets.right;
    int windowH = Math.max(sketchHeight, MIN_WINDOW_HEIGHT) + insets.top + insets.bottom;
    frame.setSize(windowW, windowH);
    return new Dimension(windowW, windowH);
  }
  private void setFrameCentered() {
    frame.setLocation(screenRect.x + (screenRect.width - sketchWidth) / 2, screenRect.y + (screenRect.height - sketchHeight) / 2);
  }
  public void placeWindow(int[] location, int[] editorLocation) {
    Dimension window = setFrameSize();
    int contentW = Math.max(sketchWidth, MIN_WINDOW_WIDTH);
    int contentH = Math.max(sketchHeight, MIN_WINDOW_HEIGHT);
    if (location != null) {
      frame.setLocation(location[0], location[1]);
    }
    else 
      if (editorLocation != null) {
        int locationX = editorLocation[0] - 20;
        int locationY = editorLocation[1];
        if (locationX - window.width > 10) {
          frame.setLocation(locationX - window.width, locationY);
        }
        else {
          locationX = editorLocation[0] + 66;
          locationY = editorLocation[1] + 66;
          if ((locationX + window.width > sketch.displayWidth - 33) || (locationY + window.height > sketch.displayHeight - 33)) {
            locationX = (sketch.displayWidth - window.width) / 2;
            locationY = (sketch.displayHeight - window.height) / 2;
          }
          frame.setLocation(locationX, locationY);
        }
      }
      else {
        setFrameCentered();
      }
    Point frameLoc = frame.getLocation();
    if (frameLoc.y < 0) {
      frame.setLocation(frameLoc.x, 30);
    }
    canvas.setBounds((contentW - sketchWidth) / 2, (contentH - sketchHeight) / 2, sketchWidth, sketchHeight);
    setupFrameResizeListener();
    if (sketch.getGraphics().displayable()) {
      setVisible(true);
    }
  }
  public void startThread() {
    if (thread == null) {
      thread = new AnimationThread();
      thread.start();
    }
    else {
      throw new IllegalStateException("Thread already started in PSurfaceAWT");
    }
  }
  public boolean stopThread() {
    if (thread == null) {
      return false;
    }
    thread = null;
    return true;
  }
  public boolean isStopped() {
    return thread == null;
  }
  public void pauseThread() {
    PApplet.debug("PApplet.run() paused, calling object wait...");
    paused = true;
  }
  protected void checkPause() {
    if (paused) {
      synchronized(pauseObject) {
        try {
          pauseObject.wait();
        }
        catch (InterruptedException e) {
        }
      }
    }
  }
  public void resumeThread() {
    paused = false;
    synchronized(pauseObject) {
      pauseObject.notifyAll();
    }
  }
  public void setSize(int wide, int high) {
    sketchWidth = wide;
    sketchHeight = high;
    if (frame != null) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\processing\revisions\rev_01e5d5b_e443529\rev_left_01e5d5b\core\src\processing\core\PSurfaceAWT.java
setFrameSize();
=======
frame.setLocationRelativeTo(null);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\processing\revisions\rev_01e5d5b_e443529\rev_right_e443529\core\src\processing\core\PSurfaceAWT.java

    }
    setCanvasSize();
    GraphicsConfiguration gc = canvas.getGraphicsConfiguration();
    if (gc == null) {
      System.err.println("GraphicsConfiguration null in setSize()");
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
    }
    int factor = graphics.pixelFactor;
    graphics.image = gc.createCompatibleImage(wide * factor, high * factor);
    sketch.width = wide;
    sketch.height = high;
    graphics.setSize(wide, high);
  }
  public void setSmooth(int level) {
  }
  private boolean checkRetina() {
    if (PApplet.platform == PConstants.MACOSX) {
      final String javaVendor = System.getProperty("java.vendor");
      if (javaVendor.contains("Apple")) {
        Float prop = (Float)canvas.getToolkit().getDesktopProperty("apple.awt.contentScaleFactor");
        if (prop != null) {
          return prop == 2;
        }
      }
      else 
        if (javaVendor.contains("Oracle")) {
          String version = System.getProperty("java.version");
          String[] m = PApplet.match(version, "1.(\\d).*_(\\d+)");
          if (m != null && PApplet.parseInt(m[1]) >= 7 && PApplet.parseInt(m[1]) >= 40) {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            try {
              Field field = device.getClass().getDeclaredField("scale");
              if (field != null) {
                field.setAccessible(true);
                Object scale = field.get(device);
                if (scale instanceof Integer && ((Integer)scale).intValue() == 2) {
                  return true;
                }
              }
            }
            catch (Exception ignore) {
            }
          }
        }
    }
    return false;
  }
  static Rectangle getDisplaySpan() {
    Rectangle bounds = new Rectangle();
    GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    for (GraphicsDevice device : environment.getScreenDevices()) {
      for (GraphicsConfiguration config : device.getConfigurations()) {
        Rectangle2D.union(bounds, config.getBounds(), bounds);
      }
    }
    return bounds;
  }
  public void setupExternalMessages() {
    frame.addComponentListener(new ComponentAdapter() {
        @Override public void componentMoved(ComponentEvent e) {
          Point where = ((Frame)e.getSource()).getLocation();
          System.err.println(PApplet.EXTERNAL_MOVE + " " + where.x + " " + where.y);
          System.err.flush();
        }
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosing(WindowEvent e) {
          sketch.exit();
        }
    });
  }
  public void setupFrameResizeListener() {
    frame.addWindowStateListener(new WindowStateListener() {
        @Override public void windowStateChanged(WindowEvent e) {
          if (Frame.MAXIMIZED_BOTH == e.getNewState()) {
            frame.pack();
          }
        }
    });
    frame.addComponentListener(new ComponentAdapter() {
        @Override public void componentResized(ComponentEvent e) {
          if (frame.isResizable()) {
            Frame farm = (Frame)e.getComponent();
            if (farm.isVisible()) {
              Insets insets = farm.getInsets();
              Dimension windowSize = farm.getSize();
              setSize(windowSize.width - insets.left - insets.right, windowSize.height - insets.top - insets.bottom);
            }
          }
        }
    });
  }
  static ArrayList<Image> iconImages;
  static protected void setIconImage(Frame frame) {
    if (PApplet.platform != PConstants.MACOSX) {
      try {
        if (iconImages == null) {
          iconImages = new ArrayList<Image>();
          final int[] sizes = { 16, 32, 48, 64 } ;
          for (int sz : sizes) {
            URL url = PApplet.class.getResource("/icon/icon-" + sz + ".png");
            Image image = Toolkit.getDefaultToolkit().getImage(url);
            iconImages.add(image);
          }
        }
        frame.setIconImages(iconImages);
      }
      catch (Exception e) {
      }
    }
  }
  protected void nativeMouseEvent(java.awt.event.MouseEvent nativeEvent) {
    int peCount = nativeEvent.getClickCount();
    int peAction = 0;
    switch (nativeEvent.getID()){
      case java.awt.event.MouseEvent.MOUSE_PRESSED:
      peAction = MouseEvent.PRESS;
      break ;
      case java.awt.event.MouseEvent.MOUSE_RELEASED:
      peAction = MouseEvent.RELEASE;
      break ;
      case java.awt.event.MouseEvent.MOUSE_CLICKED:
      peAction = MouseEvent.CLICK;
      break ;
      case java.awt.event.MouseEvent.MOUSE_DRAGGED:
      peAction = MouseEvent.DRAG;
      break ;
      case java.awt.event.MouseEvent.MOUSE_MOVED:
      peAction = MouseEvent.MOVE;
      break ;
      case java.awt.event.MouseEvent.MOUSE_ENTERED:
      peAction = MouseEvent.ENTER;
      break ;
      case java.awt.event.MouseEvent.MOUSE_EXITED:
      peAction = MouseEvent.EXIT;
      break ;
      case java.awt.event.MouseEvent.MOUSE_WHEEL:
      peAction = MouseEvent.WHEEL;
      peCount = ((MouseWheelEvent)nativeEvent).getWheelRotation();
      break ;
    }
    int modifiers = nativeEvent.getModifiers();
    int peModifiers = modifiers & (InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK | InputEvent.META_MASK | InputEvent.ALT_MASK);
    int peButton = 0;
    if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
      peButton = PConstants.LEFT;
    }
    else 
      if ((modifiers & InputEvent.BUTTON2_MASK) != 0) {
        peButton = PConstants.CENTER;
      }
      else 
        if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
          peButton = PConstants.RIGHT;
        }
    if (PApplet.platform == PConstants.MACOSX) {
      if ((modifiers & InputEvent.CTRL_MASK) != 0) {
        peButton = PConstants.RIGHT;
      }
    }
    sketch.postEvent(new MouseEvent(nativeEvent, nativeEvent.getWhen(), peAction, peModifiers, nativeEvent.getX(), nativeEvent.getY(), peButton, peCount));
  }
  protected void nativeKeyEvent(java.awt.event.KeyEvent event) {
    int peAction = 0;
    switch (event.getID()){
      case java.awt.event.KeyEvent.KEY_PRESSED:
      peAction = KeyEvent.PRESS;
      break ;
      case java.awt.event.KeyEvent.KEY_RELEASED:
      peAction = KeyEvent.RELEASE;
      break ;
      case java.awt.event.KeyEvent.KEY_TYPED:
      peAction = KeyEvent.TYPE;
      break ;
    }
    int peModifiers = event.getModifiers() & (InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK | InputEvent.META_MASK | InputEvent.ALT_MASK);
    sketch.postEvent(new KeyEvent(event, event.getWhen(), peAction, peModifiers, event.getKeyChar(), event.getKeyCode()));
  }
  protected void addListeners() {
    canvas.addMouseListener(new MouseListener() {
        public void mousePressed(java.awt.event.MouseEvent e) {
          nativeMouseEvent(e);
        }
        public void mouseReleased(java.awt.event.MouseEvent e) {
          nativeMouseEvent(e);
        }
        public void mouseClicked(java.awt.event.MouseEvent e) {
          nativeMouseEvent(e);
        }
        public void mouseEntered(java.awt.event.MouseEvent e) {
          nativeMouseEvent(e);
        }
        public void mouseExited(java.awt.event.MouseEvent e) {
          nativeMouseEvent(e);
        }
    });
    canvas.addMouseMotionListener(new MouseMotionListener() {
        public void mouseDragged(java.awt.event.MouseEvent e) {
          nativeMouseEvent(e);
        }
        public void mouseMoved(java.awt.event.MouseEvent e) {
          nativeMouseEvent(e);
        }
    });
    canvas.addMouseWheelListener(new MouseWheelListener() {
        public void mouseWheelMoved(MouseWheelEvent e) {
          nativeMouseEvent(e);
        }
    });
    canvas.addKeyListener(new KeyListener() {
        public void keyPressed(java.awt.event.KeyEvent e) {
          nativeKeyEvent(e);
        }
        public void keyReleased(java.awt.event.KeyEvent e) {
          nativeKeyEvent(e);
        }
        public void keyTyped(java.awt.event.KeyEvent e) {
          nativeKeyEvent(e);
        }
    });
    canvas.addFocusListener(new FocusListener() {
        public void focusGained(FocusEvent e) {
          sketch.focused = true;
          sketch.focusGained();
        }
        public void focusLost(FocusEvent e) {
          sketch.focused = false;
          sketch.focusLost();
        }
    });
  }
  public void setFrameRate(float fps) {
    frameRateTarget = fps;
    frameRatePeriod = (long)(1000000000.0 / frameRateTarget);
  }
  
  class AnimationThread extends Thread {
    public AnimationThread() {
      super("Animation Thread");
    }
    @Override public void run() {
      long beforeTime = System.nanoTime();
      long overSleepTime = 0L;
      int noDelays = 0;
      final int NO_DELAYS_PER_YIELD = 15;
      if (graphics.image == null) {
        setSize(sketchWidth, sketchHeight);
      }
      sketch.start();
      while ((Thread.currentThread() == thread) && !sketch.finished){
        checkPause();
        sketch.handleDraw();
        if (sketch.frameCount == 1) {
          requestFocus();
        }
        long afterTime = System.nanoTime();
        long timeDiff = afterTime - beforeTime;
        long sleepTime = (frameRatePeriod - timeDiff) - overSleepTime;
        if (sleepTime > 0) {
          try {
            Thread.sleep(sleepTime / 1000000L, (int)(sleepTime % 1000000L));
            noDelays = 0;
          }
          catch (InterruptedException ex) {
          }
          overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
        }
        else {
          overSleepTime = 0L;
          noDelays++;
          if (noDelays > NO_DELAYS_PER_YIELD) {
            Thread.yield();
            noDelays = 0;
          }
        }
        beforeTime = System.nanoTime();
      }
      sketch.dispose();
      if (sketch.exitCalled) {
        sketch.exitActual();
      }
    }
  }
  int cursorType = PConstants.ARROW;
  boolean cursorVisible = true;
  Cursor invisibleCursor;
  public void setCursor(int kind) {
    if (PApplet.platform == PConstants.MACOSX && kind == PConstants.MOVE) {
      kind = PConstants.HAND;
    }
    canvas.setCursor(Cursor.getPredefinedCursor(kind));
    cursorVisible = true;
    this.cursorType = kind;
  }
  public void setCursor(PImage img, int x, int y) {
    Image jimage = canvas.getToolkit().createImage(new MemoryImageSource(img.width, img.height, img.pixels, 0, img.width));
    Point hotspot = new Point(x, y);
    Toolkit tk = Toolkit.getDefaultToolkit();
    Cursor cursor = tk.createCustomCursor(jimage, hotspot, "Custom Cursor");
    canvas.setCursor(cursor);
    cursorVisible = true;
  }
  public void showCursor() {
    if (!cursorVisible) {
      cursorVisible = true;
      canvas.setCursor(Cursor.getPredefinedCursor(cursorType));
    }
  }
  public void hideCursor() {
    if (invisibleCursor == null) {
      BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
      invisibleCursor = canvas.getToolkit().createCustomCursor(cursorImg, new Point(8, 8), "blank");
    }
    canvas.setCursor(invisibleCursor);
    cursorVisible = false;
  }
  void debug(String format, Object ... args) {
    System.out.format(format + "%n", args);
  }
}

