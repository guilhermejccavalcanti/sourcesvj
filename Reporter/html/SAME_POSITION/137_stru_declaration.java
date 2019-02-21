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


