import java.lang.Object;
import java.io.*;
import processing.core.*;

class Region {
  public Region(int x, int y, String path) {
    _x = x;
    _y = y;
    try {
      _f = new RandomAccessFile(path, "r");
    } catch (Exception ex) {
      System.out.println("Exception opening" + path);
      System.out.println(ex.getMessage());
      _f = null;
      _x = _y = 0;
      return;
    }
    
    if (_f != null) {
      _locations = new byte[4096];
      try {
        int l = _f.read(_locations, 0, 4096);
        System.out.println("-- read" + l + "bytes from" + path);
      } catch (IOException ex) {
        System.out.println("IO Exception reading region" + path);
      }
    }
  }
  
  public boolean chunk_exists(int x, int z) {
    if (_locations == null) {
      System.out.println("no locations data");
      return false;
    }
    int loc = 4 * ((x & 31) + (z & 31) * 32);
    //println("chunk", x, z, "loc:", loc);
    if (_locations[loc] != 0 ||_locations[loc+1] != 0 ||_locations[loc+2] != 0 ||_locations[loc+3] != 0) {
      System.out.println(" *** REGION" + _x + _y + "CHUNK" + x + "," + z + "***");
      int chunk_off = ((int)(_locations[loc] & 0xff) << 16) + ((int)(_locations[loc+1] & 0xff) << 8) + (int)(_locations[loc+2] & 0xff);
      System.out.println(" - offset:" + chunk_off + "sector count:" + _locations[loc+3]);

      System.out.println((int)(_locations[loc] & 0xff));
      System.out.println((int)(_locations[loc + 1] & 0xff));
      System.out.println((int)(_locations[loc + 2] & 0xff));
      System.out.println((int)(_locations[loc + 3] & 0xff));
      return true;
    }
    return false;
  }
  
  int _x;
  int _y;
  RandomAccessFile _f;
  
  byte[] _locations;
  
}