import java.util.*;


class NBT_Parser {
  LinkedList<NBT_Tag> _tag_list = new LinkedList<NBT_Tag>();
  byte[] _nbt_data;
  int _len = 0;
  int _pos = 0;
  public NBT_Parser(byte[] nbt, int len) {
    _nbt_data = nbt;
    _len = len;
  }

  public NBT_Tag GetNextTag() {
    switch(_nbt_data[_pos]) {
      case 0:
        return parse_end();
      case 1:
        return parse_byte();
      case 2:
        return parse_short();
      case 3:
        return parse_int();
      case 4:
        return parse_long();
      case 5:
        return parse_float();
      case 6:
        return parse_double();
      case 7:
        return parse_byte_array();
      case 8:
        return parse_string();
      case 9:
        return parse_list();
      case 10:
        return parse_compound();
      case 11:
        return parse_int_array();
    }
    System.out.println("BAD TAG: " + _nbt_data[_pos]);
    System.out.println(" pos: " + _pos);
    int i = _pos - 20;
    if (i < 0) {
      i = 0;
    }
    while (i < _pos + 20 && i < _len) {
      System.out.println((i == _pos ? '*' : ' ') + String.format("[%05d] - 0x%02X (", i, _nbt_data[i]) + _nbt_data[i] + ")");
      i++;
    }
    _pos++;
    System.out.println(parse_string_inner());
    return null;
  }
  private NBT_Tag parse_end() {
    System.out.println("parse_end - XXX");
    return null;
  }
  private NBT_Tag parse_byte() {
    _pos = _pos + 1; // skip tag
    String name = parse_string_inner();

    TAG_Byte tb = new TAG_Byte(_nbt_data[_pos++]);
    tb.name = name;
    return tb;
  }
  private NBT_Tag parse_short() {
    System.out.println("parse_short - XXX");
    return null;
  }
  private NBT_Tag parse_int() {
    _pos = _pos + 1; // skip tag
    String name = parse_string_inner();
    int value = 0;
    for (int i = 3; i >= 0; i--) {
      value |= ((int)_nbt_data[_pos++] & 0xFF) << (i * 8);
    }
    TAG_Int i = new TAG_Int(value);
    i.name = name;
    return i;
  }

  private NBT_Tag parse_long() {
    _pos = _pos + 1; // skip tag
    String name = parse_string_inner();
    long value = 0;
    for (int i = 7; i >= 0; i--) {
      value |= ((long)_nbt_data[_pos++] & 0xFF) << (i * 8);
    }
    TAG_Long lng = new TAG_Long(value);
    lng.name = name;
    return lng;
  }
  private NBT_Tag parse_float() {
    System.out.println("parse_float - XXX");
    return null;
  }
  private NBT_Tag parse_double() {
    System.out.println("parse_double - XXX");
    return null;
  }
  private NBT_Tag parse_byte_array() {
    _pos = _pos + 1; // skip tag
    String name = parse_string_inner();
    int l = Region.bytes_to_int(_nbt_data[_pos], _nbt_data[_pos+1], _nbt_data[_pos+2], _nbt_data[_pos+3]);
    _pos += 4;

    TAG_Byte_Array ba = new TAG_Byte_Array(l);
    ba.name = name;

    for (int i = 0; i < l; i++) {
      ba.set(i, _nbt_data[_pos++]);
    }

    return ba;
  }
  private String parse_string_inner() {
    // TODO : check _pos against _len
    // get length
    int l = (int)(_nbt_data[_pos] & 0xff) << 8;
    _pos++;
    l = l + (int)(_nbt_data[_pos] & 0xff);
    _pos++;
    String s = "";
    for (int i = 0; i < l; i++) {
      s = s + (char)_nbt_data[_pos++];
    }
    return s;
  }

  private NBT_Tag parse_string() {
    _pos = _pos + 1; // skip tag
    return new TAG_String(parse_string_inner());
  }
  private NBT_Tag parse_list() {
    _pos = _pos + 1;
    String name = parse_string_inner();
    byte type = _nbt_data[_pos++];

    int l = Region.bytes_to_int(_nbt_data[_pos], _nbt_data[_pos+1], _nbt_data[_pos+2], _nbt_data[_pos+3]);
    _pos += 4;

    TAG_List list = new TAG_List(name, l, type);
    // dump the bytes for a clue of what is going on
    System.out.println("LIST('" + name + "'): type: " + NBT_Tag.tag_type_name(type) + " len=" + l);
    for (int i = 0; i < 100; i++) {
      System.out.println(String.format("[%5d] 0x%02x", (_pos + i), _nbt_data[(_pos + i)]));
    }
    for (int i = 0; i < l; i++) {
      // not quite sure how to do this
    }

    return list;
  }
  private NBT_Tag parse_compound() {
    _pos = _pos + 1; // skip tag
    String name = parse_string_inner();
    TAG_Compound compound = new TAG_Compound(name);
    NBT_Tag tag = GetNextTag();
    while(tag != null && tag.type != 0) {
      compound.add_element(tag);
      tag = GetNextTag();
    }
    return compound;
  }
  private NBT_Tag parse_int_array() {
    _pos = _pos + 1; // skip tag
    String name = parse_string_inner();
    int l = Region.bytes_to_int(_nbt_data[_pos], _nbt_data[_pos+1], _nbt_data[_pos+2], _nbt_data[_pos+3]);
    _pos += 4;

    TAG_Int_Array ia = new TAG_Int_Array(l);
    ia.name = name;

    for (int i = 0; i < l; i++) {
      int val = Region.bytes_to_int(_nbt_data[_pos], _nbt_data[_pos+1], _nbt_data[_pos+2], _nbt_data[_pos+3]);
      _pos += 4;
      ia.set(i, val);
    }

    return ia;
  }
}