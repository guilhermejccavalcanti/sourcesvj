package org.gephi.data.attributes.api;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.regex.Pattern;
import org.gephi.data.attributes.type.DynamicByte;
import org.gephi.data.attributes.type.DynamicShort;
import org.gephi.data.attributes.type.DynamicInteger;
import org.gephi.data.attributes.type.DynamicLong;
import org.gephi.data.attributes.type.DynamicFloat;
import org.gephi.data.attributes.type.DynamicDouble;
import org.gephi.data.attributes.type.DynamicBoolean;
import org.gephi.data.attributes.type.DynamicCharacter;
import org.gephi.data.attributes.type.DynamicString;
import org.gephi.data.attributes.type.DynamicBigInteger;
import org.gephi.data.attributes.type.DynamicBigDecimal;
import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.data.attributes.type.ByteList;
import org.gephi.data.attributes.type.ShortList;
import org.gephi.data.attributes.type.IntegerList;
import org.gephi.data.attributes.type.LongList;
import org.gephi.data.attributes.type.FloatList;
import org.gephi.data.attributes.type.DoubleList;
import org.gephi.data.attributes.type.BooleanList;
import org.gephi.data.attributes.type.CharacterList;
import org.gephi.data.attributes.type.StringList;
import org.gephi.data.attributes.type.BigIntegerList;
import org.gephi.data.attributes.type.BigDecimalList;
import org.gephi.data.attributes.type.Interval;
import sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte0;
public enum AttributeType {
  BYTE(Byte.class),

  SHORT(Short.class),

  INT(Integer.class),

  LONG(Long.class),

  FLOAT(Float.class),

  DOUBLE(Double.class),

  BOOLEAN(Boolean.class),

  CHAR(Character.class),

  STRING(String.class),

  BIGINTEGER(BigInteger.class),

  BIGDECIMAL(BigDecimal.class),

  DYNAMIC_BYTE(DynamicByte.class),

  DYNAMIC_SHORT(DynamicShort.class),

  DYNAMIC_INT(DynamicInteger.class),

  DYNAMIC_LONG(DynamicLong.class),

  DYNAMIC_FLOAT(DynamicFloat.class),

  DYNAMIC_DOUBLE(DynamicDouble.class),

  DYNAMIC_BOOLEAN(DynamicBoolean.class),

  DYNAMIC_CHAR(DynamicCharacter.class),

  DYNAMIC_STRING(DynamicString.class),

  DYNAMIC_BIGINTEGER(DynamicBigInteger.class),

  DYNAMIC_BIGDECIMAL(DynamicBigDecimal.class),

  TIME_INTERVAL(TimeInterval.class),

  LIST_BYTE(ByteList.class),

  LIST_SHORT(ShortList.class),

  LIST_INTEGER(IntegerList.class),

  LIST_LONG(LongList.class),

  LIST_FLOAT(FloatList.class),

  LIST_DOUBLE(DoubleList.class),

  LIST_BOOLEAN(BooleanList.class),

  LIST_CHARACTER(CharacterList.class),

  LIST_STRING(StringList.class),

  LIST_BIGINTEGER(BigIntegerList.class),

  LIST_BIGDECIMAL(BigDecimalList.class),

;
  private final Class type;
AttributeType(Class type) {
    this.type = type;
}
  @Override public String toString() {
    return type.getSimpleName();
  }
  public String getTypeString() {
    return super.toString();
  }
  public Class getType() {
    return type;
  }
  public Object parse(String str) {
    switch (this){
      case BYTE:
      return new Byte(removeDecimalDigitsFromString(str));
      case SHORT:
      return new Short(removeDecimalDigitsFromString(str));
      case INT:
      return new Integer(removeDecimalDigitsFromString(str));
      case LONG:
      return new Long(removeDecimalDigitsFromString(str));
      case FLOAT:
      return new Float(str);
      case DOUBLE:
      return new Double(str);
      case BOOLEAN:
      return new Boolean(str);
      case CHAR:
      return new Character(str.charAt(0));
      case BIGINTEGER:
      return new BigInteger(removeDecimalDigitsFromString(str));
      case BIGDECIMAL:
      return new BigDecimal(str);
      case DYNAMIC_BYTE:
      return new DynamicByte(new Interval<Byte>(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, new Byte(str)));
      case DYNAMIC_SHORT:
      return new DynamicShort(new Interval<Short>(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, new Short(str)));
      case DYNAMIC_INT:
      return new DynamicInteger(new Interval<Integer>(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, new Integer(str)));
      case DYNAMIC_LONG:
      return new DynamicLong(new Interval<Long>(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, new Long(str)));
      case DYNAMIC_FLOAT:
      return new DynamicFloat(new Interval<Float>(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, new Float(str)));
      case DYNAMIC_DOUBLE:
      return new DynamicDouble(new Interval<Double>(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, new Double(str)));
      case DYNAMIC_BOOLEAN:
      return new DynamicBoolean(new Interval<Boolean>(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, new Boolean(str)));
      case DYNAMIC_CHAR:
      return new DynamicCharacter(new Interval<Character>(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, new Character(str.charAt(0))));
      case DYNAMIC_STRING:
      return new DynamicString(new Interval<String>(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, str));
      case DYNAMIC_BIGINTEGER:
      return new DynamicBigInteger(new Interval<BigInteger>(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, new BigInteger(str)));
      case DYNAMIC_BIGDECIMAL:
      return new DynamicBigDecimal(new Interval<BigDecimal>(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, new BigDecimal(str)));
      case TIME_INTERVAL:
      throw new UnsupportedOperationException("Not supported.");
      case LIST_BYTE:
      return new ByteList(removeDecimalDigitsFromString(str));
      case LIST_SHORT:
      return new ShortList(removeDecimalDigitsFromString(str));
      case LIST_INTEGER:
      return new IntegerList(removeDecimalDigitsFromString(str));
      case LIST_LONG:
      return new LongList(removeDecimalDigitsFromString(str));
      case LIST_FLOAT:
      return new FloatList(str);
      case LIST_DOUBLE:
      return new DoubleList(str);
      case LIST_BOOLEAN:
      return new BooleanList(str);
      case LIST_CHARACTER:
      return new CharacterList(str);
      case LIST_STRING:
      return new StringList(str);
      case LIST_BIGINTEGER:
      return new BigIntegerList(removeDecimalDigitsFromString(str));
      case LIST_BIGDECIMAL:
      return new BigDecimalList(str);
    }
    return str;
  }
  public static AttributeType parse(Object obj) {
    if (obj == null) {
      return null;
    }
    Class<?> c = obj.getClass();
    for (AttributeType attributeType : AttributeType.values()) {
      if (c.equals(attributeType.getType())) {
        return attributeType;
      }
    }
    return null;
  }
  public static AttributeType parseDynamic(Object obj) {
    if (obj == null) {
      return null;
    }
    Class<?> c = obj.getClass();
    if (c.equals(Byte.class)) {
      return DYNAMIC_BYTE;
    }
    if (c.equals(Short.class)) {
      return DYNAMIC_SHORT;
    }
    if (c.equals(Integer.class)) {
      return DYNAMIC_INT;
    }
    if (c.equals(Long.class)) {
      return DYNAMIC_LONG;
    }
    if (c.equals(Float.class)) {
      return DYNAMIC_FLOAT;
    }
    if (c.equals(Double.class)) {
      return DYNAMIC_DOUBLE;
    }
    if (c.equals(Boolean.class)) {
      return DYNAMIC_BOOLEAN;
    }
    if (c.equals(Character.class)) {
      return DYNAMIC_CHAR;
    }
    if (c.equals(String.class)) {
      return DYNAMIC_STRING;
    }
    if (c.equals(BigInteger.class)) {
      return DYNAMIC_BIGINTEGER;
    }
    if (c.equals(BigDecimal.class)) {
      return DYNAMIC_BIGDECIMAL;
    }
    return null;
  }
  public boolean isDynamicType() {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\gephi\revisions\rev_677b35d_bd6cca6\rev_left_677b35d\AttributesAPI\src\org\gephi\data\attributes\api\AttributeType.java
switch (this){
      case DYNAMIC_BYTE:
      case DYNAMIC_SHORT:
      case DYNAMIC_INT:
      case DYNAMIC_LONG:
      case DYNAMIC_FLOAT:
      case DYNAMIC_DOUBLE:
      case DYNAMIC_BOOLEAN:
      case DYNAMIC_CHAR:
      case DYNAMIC_STRING:
      case DYNAMIC_BIGINTEGER:
      case DYNAMIC_BIGDECIMAL:
      case TIME_INTERVAL:
      return true;
      default:
      return false;
    }
=======
if (this.equals(DYNAMIC_BYTE) || this.equals(DYNAMIC_SHORT) || this.equals(DYNAMIC_INT) || this.equals(DYNAMIC_LONG) || this.equals(DYNAMIC_FLOAT) || this.equals(DYNAMIC_DOUBLE) || this.equals(DYNAMIC_BOOLEAN) || this.equals(DYNAMIC_CHAR) || this.equals(DYNAMIC_STRING) || this.equals(DYNAMIC_BIGINTEGER) || this.equals(DYNAMIC_BIGDECIMAL) || this.equals(TIME_INTERVAL)) {
      return true;
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\gephi\revisions\rev_677b35d_bd6cca6\rev_right_bd6cca6\AttributesAPI\src\org\gephi\data\attributes\api\AttributeType.java

  }
  private String removeDecimalDigitsFromString(String s) {
    return removeDecimalDigitsFromStringPattern.matcher(s).replaceAll("");
  }
  private static final Pattern removeDecimalDigitsFromStringPattern = Pattern.compile("\\.[0-9]*");
}

