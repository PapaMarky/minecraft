import java.lang.Object;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.regex.*;
import java.io.RandomAccessFile;

class Region {
  public Region(int x, int y, String path) {
    _x = x;
    _y = y;
    try {
      _f = new RandomAccessFile(path, "r");
    } catch (Exception ex) {
      println("Exception opening", path);
      println(ex.getMessage());
      _f = null;
      _x = _y = 0;
      return;
    }
    
    if (_f != null) {
      _locations = new byte[4096];
      try {
        int l = _f.read(_locations, 0, 4096);
        println("-- read",l,"bytes from", path);
      } catch (IOException ex) {
        println("IO Exception reading region", path);
      }
    }
  }
  
  public boolean chunk_exists(int x, int z) {
    if (_locations == null) {
      println("no locations data");
      return false;
    }
    int loc = 4 * ((x & 31) + (z & 31) * 32);
    //println("chunk", x, z, "loc:", loc);
    if (_locations[loc] != 0 ||_locations[loc+1] != 0 ||_locations[loc+2] != 0 ||_locations[loc+3] != 0) {
      println(" *** REGION",_x,_y,"CHUNK", x, ",", z, "***");
      int chunk_off = ((int)(_locations[loc] & 0xff) << 16) + ((int)(_locations[loc+1] & 0xff) << 8) + (int)(_locations[loc+2] & 0xff);
      println(" - offset:",chunk_off,"sector count:",_locations[loc+3]);

      println((int)(_locations[loc] & 0xff));
      println((int)(_locations[loc + 1] & 0xff));
      println((int)(_locations[loc + 2] & 0xff));
      println((int)(_locations[loc + 3] & 0xff));
      return true;
    }
    return false;
  }
  
  int _x;
  int _y;
  RandomAccessFile _f;
  
  byte[] _locations;
  
}

class World {
  public World(String dirPath) {
    mca_pattern = Pattern.compile(".*r.([-0-9]+).([-0-9]+).mca");
    world_path = dirPath;
    loadRegions();
  }
  
  public Rectangle getBounds() {
    if (max_x == null || max_z == null) {
      return null;
    }
    //int x0 = min_x * 512;
    //int x1 = max_x * 512 + 512;
    //int z0 = min_z * 512;
    //int z1 = max_z * 512 + 512;
    //return new Rectangle(x0, z0, x1 - x0, z1 - z0);
    return new Rectangle(min_x, min_z, max_x - min_x + 1, max_z - min_z + 1);
  }
  
  public Region getRegion(int x, int z) {
    if (! region_map.containsKey(x)) {
      return null;
    }
    Hashtable<Integer, Region> northSouthMap = region_map.get(x);
    if (! northSouthMap.containsKey(z)) {
      return null;
    }
    return northSouthMap.get(z);
  }
  // A region is 32 x 32 chunks (512 x 512 horizontal squares)
  // A chunk is 16 x 256 x 16 squares

  void loadRegions() {
    // Look inside the folder for *.mcr files
    File worldDir = new File(world_path);
    File[] files = worldDir.listFiles();
    for (int i = 0; i < files.length; i++) {
      String filename = files[i].getName();
      print(filename);
      if (filename.endsWith(".mca")) {
        println(" -- BINGO!");
        addRegionFile(files[i].getAbsolutePath());
      } else {
        println("");
      }
    }
  }
  
  void addRegionFile(String path) {
    //
    Matcher m = mca_pattern.matcher(path);
    if (! m.matches()) {
      println("Not a region file: ", path);
      return;
    }
    String k = m.group(0);
    // X => increasing West to East
    // Z => increasing North to South
    int x = Integer.parseInt(m.group(1));
    int z = Integer.parseInt(m.group(2));
    println(" -- KEY: '", k, "' -> ", x, ", ", z);
    print("'"); print(k); println("'");
    if (!region_map.containsKey(x)) {
      region_map.put(x, new Hashtable<Integer, Region>());
    }
    Hashtable<Integer, Region> northSouthMap = region_map.get(x);
    
    if (northSouthMap.containsKey(z)) {
      return;
    }
    if (min_x == null || x < min_x) {
      min_x = x;
    }
    if (max_x == null || x > max_x) {
      max_x = x;
    }
    if (min_z == null || z < min_z) {
      min_z = z;
    }
    if (max_z == null || z > max_z) {
      max_z = z;
    }
    northSouthMap.put(z, new Region(x, z, path));
  }
  
  public boolean isEmpty() {
    return region_map.isEmpty();
  }
  
  Pattern mca_pattern = null;
  Hashtable<Integer, Hashtable<Integer, Region> > region_map = new Hashtable<Integer, Hashtable<Integer, Region> >();
  Integer max_x = null;
  Integer min_x = null;
  Integer max_z = null;
  Integer min_z = null;
  String world_path = "";
}

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

void setup(){
  size (1024, 960);
  surface.setResizable(true);
}

void handleResize() {
  Dimension d = frame.getSize();
  old_w = width;
  old_h = height;
  println(" RESIZE to", width, "x", height);
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

void drawRegion() {
}

void drawWorld() {
  background(255);
  if (world == null) {
    return;
  }
  Rectangle bounds = world.getBounds();
  int x0 = bounds.x;
  int x1 = bounds.width - x0;
  for (int x = x0; x < x1; x++) {
    int z0 = bounds.y;
    int z1 = bounds.height - z0;
    for (int z = z0; z < z1; z++) {
      Region r = world.getRegion(x, z);
    }
  }
}

void drawEmpty() {
  background(128);
  textSize(48);
  textAlign(CENTER, CENTER);
  fill(0, 0, 0);
  String s= new String("Use Ctrl-O to Open A World");
  text(s, 0, 0, width, height);
  //println(s, "@ ", width, ",", height);
}

int old_w = 0;
int old_h = 0;

void draw() {
  if (old_w != width || old_h != height) {
    handleResize();
  }
  /*
  Dimension d = frame.getSize();
  if (width != d.width || height != d.height) {
    println("Resize in Draw");
    handleResize();
  }
  */
  if (world == null) {
    drawEmpty();
    return;
  }
  drawWorld();
}