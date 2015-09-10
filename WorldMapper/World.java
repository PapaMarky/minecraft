import java.util.regex.*;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.io.*;

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
      System.out.print(filename);
      if (filename.endsWith(".mca")) {
        System.out.println(" -- BINGO!");
        addRegionFile(files[i].getAbsolutePath());
      } else {
        System.out.println("");
      }
    }
  }
  
  void addRegionFile(String path) {
    //
    Matcher m = mca_pattern.matcher(path);
    if (! m.matches()) {
      System.out.println("Not a region file: " + path);
      return;
    }
    String k = m.group(0);
    // X => increasing West to East
    // Z => increasing North to South
    int x = Integer.parseInt(m.group(1));
    int z = Integer.parseInt(m.group(2));
    System.out.println(" -- KEY: '" + k + "' -> " + x + ", " + z);
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