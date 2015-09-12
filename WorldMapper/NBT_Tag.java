import java.util.*;

public class NBT_Tag {
  public byte type;
  public String name;

  public String toString() {
    return toString(0);
  }

  public String toString(int indent) {
    return indent_string(indent) + "TAG";
  }

  protected String indent_string(int depth) {
    String s = "";
    for (int i = 0; i < depth; i++) {
      s += "  ";
    }
    return s;
  }

  public static String tag_type_name(byte t) {
    switch(t) {
      case 0: return "end";
      case 1: return "byte";
      case 2: return "short";
      case 3: return "int";
      case 4: return "long";
      case 5: return "float";
      case 6: return "double";
      case 7: return "byte[]";
      case 8: return "string";
      case 9: return "list";
      case 10: return "compound";
      case 11: return "int[]";
    }
    return "bad type";
  }
}

class TAG_End extends NBT_Tag {
  public TAG_End() {
    type = 0;
  }
  
  public String toString(int indent) {
    return indent_string(indent) + "TAG_End;";
  }
}

class TAG_Byte extends NBT_Tag {
  public TAG_Byte(byte b) {
    type = 1;
    value = b;
  }
  public String toString(int indent) {
    return indent_string(indent) + "TAG_Byte(\"" + name + "\"): [" + String.format("0x%02X", value) + "]: " + value;
  }
  private byte value;
}

class TAG_Short extends NBT_Tag {
  public TAG_Short(short b) {
    type = 2;
    value = b;
  }
  public String toString(int indent) {
    return indent_string(indent) + "TAG_Short(\"" + name + "\"): " + value;
  }
  private short value;
}

class TAG_Int extends NBT_Tag {
  public TAG_Int(int b) {
    type = 3;
    value = b;
  }
  public String toString(int indent) {
    return indent_string(indent) + "TAG_Int(\"" + name + "\"): " + value;
  }
  private int value;
}

class TAG_Long extends NBT_Tag {
  public TAG_Long(long v) {
    type = 4;
    value = v;
  }
  public String toString(int indent) {
    return indent_string(indent) + "TAG_Long(\"" + name + "\"): " + value;
  }
  private long value;
}

class TAG_Float extends NBT_Tag {
  public TAG_Float(float b) {
    type = 5;
    value = b;
  }
  public String toString(int indent) {
    return indent_string(indent) + "TAG_Float(\"" + name + "\"): " + value;
  }
  private float value;
}

class TAG_Double extends NBT_Tag {
  public TAG_Double(double b) {
    type = 6;
    value = b;
  }
  public String toString(int indent) {
    return indent_string(indent) + "TAG_Double(\"" + name + "\"): " + value;
  }
  private double value;
}

class TAG_Byte_Array extends NBT_Tag {
  public TAG_Byte_Array(int length) {
    type = 7;
    _length = length;
    value = new byte[length];
  }

  public void set(int index, byte val) {
    if (value != null && index < _length) {
      value[index] = val;
    }
  }

  public String toString(int indent) {
    String s = indent_string(indent) + "TAG_Byte_Array(\"" + name + "\")[" + _length +"]: [\n";
    boolean start_line = true;
    int n = 0;
    for (int i = 0; i < _length; i++) {
      if (start_line) {
        s += indent_string(indent + 1);
        start_line = false;
      }
      s += String.format("0x%02X, ", value[i]);
      if ((i + 1) % 10 == 0) {
        s += "\n";
        start_line = true;
      }
    }

    s += "\n" + indent_string(indent) + "];";
    return s;
  }
  int _length;
  private byte[] value;
}

class TAG_String extends NBT_Tag {
  public TAG_String(String b) {
    type = 8;
    value = b;
  }
  public String toString(int indent) {
    return indent_string(indent) + "TAG_String(\"" + name + "\"): " + value;
  }
  private String value;
}

class TAG_List extends NBT_Tag {
  public TAG_List(String n, int len, byte item_type) {
    type = 9;
    name = n;
    _length = len;
    _item_type = item_type;
  }
  public String toString(int indent) {
    String s = indent_string(indent) + "TAG_List(\"" + name + "\") type: " + tag_type_name(_item_type) + " [" + _length +"]: [\n";
    // ???
    s += indent_string(indent) +"];";
    return s;
  }
  byte _item_type;
  int _length;
  //private byte value;
}

class TAG_Compound extends NBT_Tag {
  public TAG_Compound(String n) {
    name = n;
    type = 10;
  }

  public void add_element(NBT_Tag tag) {
    _contents.add(tag);
  }

  // Should contents be a hash?
  LinkedList<NBT_Tag> _contents = new LinkedList<NBT_Tag>();

  public String toString(int indent) {
    String s = indent_string(indent) + "TAG_Compound(\"" + name + "\"): {\n";
    ListIterator<NBT_Tag> it = _contents.listIterator(0);
    while (it.hasNext()) {
      s += it.next().toString(indent + 1) + "\n";
    }
    s += indent_string(indent) + "};\n";
    return s;
  }
}

class TAG_Int_Array extends NBT_Tag {
  public TAG_Int_Array(int length) {
    type = 11;
    _length = length;
    value = new int[length];
  }

  public void set(int index, int val) {
    if (value != null && index < _length) {
      value[index] = val;
    }
  }
  public String toString(int indent) {
    String s = indent_string(indent) + "TAG_Int_Array(\"" + name + "\")[" + _length +"]: [\n";
    boolean start_line = true;
    int n = 0;
    for (int i = 0; i < _length; i++) {
      if (start_line) {
        s += indent_string(indent + 1);
        start_line = false;
      }
      s += String.format("%10d, ", value[i]);
      if ((i + 1) % 10 == 0) {
        s += "\n";
        start_line = true;
      }
    }
    s += "\n" + indent_string(indent) + "];";
    return s;
  }
  private int _length;
  private int[] value;
}