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
    System.out.println(" reading " + path);
    if (_f != null) {
      _locations = new byte[4096];
      System.out.println("read locations");
      try {
        System.out.println("File Length: " + _f.length() + " bytes");
        int l = _f.read(_locations, 0, 4096);
        System.out.println("-- read " + l + " bytes from " + path);
      } catch (IOException ex) {
        System.out.println("IO Exception reading region locations " + path);
      }
      _timestamps = new byte[4096];
      System.out.println("read timestamps, length: " + _timestamps.length + "_f = " + _f);
      try {
        _f.seek(4096);
        //int l = _f.read(_timestamps, 4096, 4096);
        int i = 0;
        while (i < 4096) {
          _timestamps[i] = _f.readByte();
          i++;
        }
        System.out.println("-- read " + i + " ts bytes from " + path);
      } catch (IOException ex) {
        System.out.println("IO Exception reading region timestamps " + path);
      } catch (Exception e) {
        System.out.println("Unhandled Exception: " + e.getMessage());
        throw e;
      }
    }
  }
  
  class ChunkHeader {
    public int location = 0;
    public int size = 0;
    public int timestamp = 0;
    public ChunkHeader() {
    }

    public ChunkHeader(int l, int s, int t) {
      location = l;
      size = s;
      timestamp = t;
    }
  }

  private ChunkHeader chunk_header(int x, int z) {
    ChunkHeader rval = new ChunkHeader();
    if (_locations == null) {
      System.out.println("no locations data");
      return rval;
    }
    int loc = 4 * ((x & 31) + (z & 31) * 32);
    rval.location = ((int)(_locations[loc] & 0xff) << 16) + ((int)(_locations[loc+1] & 0xff) << 8) + (int)(_locations[loc+2] & 0xff);
    rval.size = (int)(_locations[loc + 3] & 0xff);
    if (rval.location != 0) {
      rval.timestamp =
        ((int)(_locations[loc] & 0xff) << 24) + ((int)(_locations[loc+1] & 0xff) << 16) + ((int)(_locations[loc+2] & 0xff) << 8) + (int)(_locations[loc+3] & 0xff);
    }
    return rval;
  }

  public boolean chunk_exists(int x, int z) {
    ChunkHeader ch = chunk_header(x, z);

    if (ch.location == 0) {
      return false;
    }
    System.out.println(" REGION " + _x + ", " + _y + ": CHUNK " + x + ", " + z + " offset(" + ch.location +
      ") size(" + ch.size + ") timestamp(" + ch.timestamp + ")");
    return true;
  }
  
  int _x;
  int _y;
  RandomAccessFile _f;
  
  byte[] _locations;
  byte[] _timestamps;
  
}