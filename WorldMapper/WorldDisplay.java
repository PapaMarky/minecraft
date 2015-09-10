import processing.core.*;
import java.awt.Rectangle;

class WorldDisplay {
  PApplet _applet;
  World _world;
  int _old_w = 0;
  int _old_h = 0;

  public WorldDisplay(PApplet applet) {
    _applet = applet;
    System.out.println("WorldDisplay ctor");
  }
  
  public void setup() {
  }
  
  public void draw() {
    if (_old_w != _applet.width || _old_h != _applet.height) {
      handleResize();
    }
    if (_world == null) {
      drawEmpty();
      return;
    }
    drawWorld();
  }

  private void handleResize() {
    _old_w = _applet.width;
    _old_h = _applet.height;
  }

  void drawRegion() {
  }

  private void drawWorld() {
    _applet.background(255);
    if (_world == null) {
      return;
    }
    Rectangle bounds = _world.getBounds();
    int x0 = bounds.x;
    int x1 = bounds.width - x0;
    for (int x = x0; x < x1; x++) {
      int z0 = bounds.y;
      int z1 = bounds.height - z0;
      for (int z = z0; z < z1; z++) {
        Region r = _world.getRegion(x, z);
      }
    }
  }

  private void drawEmpty() {
    _applet.background(128);
    _applet.textSize(48);
    _applet.g.textAlign(_applet.CENTER, _applet.CENTER);
    _applet.fill(0, 0, 0);
    String s= new String("Use Ctrl-O to Open A World");
    _applet.g.text(s, 0, 0, _applet.width, _applet.height);
  }
}