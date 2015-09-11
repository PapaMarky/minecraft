import java.lang.Object;
import java.io.*;
import processing.core.*;
import java.awt.Rectangle;

public class WorldDisplay {
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

  private void createWorld(String dirPath) {
    World new_world = new World(dirPath);
    if (new_world.isEmpty()) {
      _applet.println("New World Is Empty");
      return;
    }

    _world = new_world;
    _applet.println("Bounds of New World:", new_world.getBounds());
  }

  public void folderSelected(File selection) {
    if (selection == null) {
      _applet.println("Window was closed or the user hit cancel.");
    } else {
      String path = selection.getAbsolutePath();
      _applet.println("User selected " + path);
      createWorld(path);
    }
  }

  private void setFolder(){
    _applet.selectFolder("Select a folder to process:", "folderSelected", null, this);
  }

  public void keyTyped() {
    System.out.println("typed " + _applet.key + " " + _applet.keyCode);
    if (_applet.key == 15) { // ctl-o
      setFolder();
    }
    if (_applet.key == 16) {
      Rectangle bounds = _world.getBounds();
      int x0 = bounds.x;
      int x1 = bounds.width - x0;
      for (int x = x0; x < x1; x++) {
        int z0 = bounds.y;
        int z1 = bounds.height - z0;
        for (int z = z0; z < z1; z++) {
          Region r = _world.getRegion(x, z);
          if (r == null) {
            continue;
          }
          for (int cx = 0; cx < 32; cx++) {
            for (int cz = 0; cz < 32; cz++) {
              r.chunk_exists(cx, cz);
            }
          }
        }
      }
    }
    if (_applet.key == 17) {
      Rectangle bounds = _world.getBounds();
      Region r = _world.getRegion(0, 0);
      r.load_chunk_data(0, 3);
    }
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