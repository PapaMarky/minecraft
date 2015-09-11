import java.lang.Object;
import java.io.*;
import java.util.zip.Inflater;
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

  public void load_chunk_data(int x, int z) {
    ChunkHeader ch = chunk_header(x, z);
    if (ch.location == 0) {
      return;
    }

    long offset = (long)ch.location * 4096L;
    int size = ch.size * 4096;
    System.out.println("chunk " + x + " " + z + " offset: " + offset + ", size: " + size);
    byte[] data = new byte[size];
    byte[] compressed_data;
    int data_len = 0;
    int compression = 0;
    try {
      _f.seek(0);
      System.out.println(" - file size: " + _f.length());
      _f.seek(offset);
      data_len = bytes_to_int(_f.readByte(), _f.readByte(), _f.readByte(), _f.readByte());
      System.out.println(" Data Length: " + data_len);
      compression = _f.readByte();
      System.out.println(" Compression: " + compression);
      compressed_data = new byte[data_len - 1];
      _f.read(compressed_data);
    } catch (IOException ioe) {
      System.out.println("IOExecption reading chunk data: " + ioe.getMessage());
      return;
    } catch (Exception e) {
      System.out.println("Unhandled exception: " + e.getMessage());
      throw e;
    }
    if (data_len == 0) {
      return;
    }
    byte[] decompressed_data = new byte[1024*1024];
    int decompressed_length = 0;
    if (compression == 2) {
      try {
        System.out.println(" decompress");
        Inflater decompresser = new Inflater();
        System.out.println(" - setInput");
        decompresser.setInput(compressed_data, 0, data_len - 1);
        System.out.println(" - inflate");
        decompressed_length = decompresser.inflate(decompressed_data);
        decompresser.end();
      } catch (java.util.zip.DataFormatException ex) {
        System.out.println("Exception: " + ex.getMessage());
      }
      System.out.println(" GOT " + decompressed_length + " BYTES");
      System.out.println(" -- First Tag: " + decompressed_data[0]);
    }
  }

  private int bytes_to_int(byte b0, byte b1, byte b2, byte b3) {
    return ((int)(b0 & 0xff) << 24) + ((int)(b1 & 0xff) << 16) + ((int)(b2 & 0xff) << 8) + (int)(b3 & 0xff);
  }

  private ChunkHeader chunk_header(int x, int z) {
    ChunkHeader rval = new ChunkHeader();
    if (_locations == null) {
      System.out.println("no locations data");
      return rval;
    }
    int loc = 4 * ((x & 31) + (z & 31) * 32);
    rval.location = bytes_to_int((byte)0, _locations[loc], _locations[loc+1], _locations[loc+2]);
    rval.size = (int)(_locations[loc + 3] & 0xff);
    if (rval.location != 0) {
      rval.timestamp = bytes_to_int(_locations[loc], _locations[loc+1], _locations[loc+2], _locations[loc+3]);
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