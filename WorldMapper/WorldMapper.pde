import java.lang.Object;
import java.awt.event.*;
import java.awt.Rectangle;



void createWorld(String dirPath) {
  World new_world = new World(dirPath);
  if (new_world.isEmpty()) {
    println("New World Is Empty");
    return;
  }
  
  world = new_world;
  println("Bounds of New World:", new_world.getBounds());
}

World world = null;
WorldDisplay world_display = null;
void setup(){
  size (1024, 960);
  surface.setResizable(true);
  surface.setSize(2 * displayWidth / 3, 2 * displayHeight / 3);

  world_display = new WorldDisplay(this);
  world_display.setup();
}

void setFolder(){
  selectFolder("Select a folder to process:", "folderSelected");
}

void keyTyped() {
  println("typed " + int(key) + " " + keyCode);
  if (int(key) == 15) { // ctl-o
    setFolder();
  }
  if (int(key) == 16) {
    Rectangle bounds = world.getBounds();
    int x0 = bounds.x;
    int x1 = bounds.width - x0;
    for (int x = x0; x < x1; x++) {
      int z0 = bounds.y;
      int z1 = bounds.height - z0;
      for (int z = z0; z < z1; z++) {
        Region r = world.getRegion(x, z);
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
}

void folderSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else { 
    String path = selection.getAbsolutePath();
    println("User selected " + path);
    createWorld(path);
  }
}

void draw() {
  world_display.draw();
}