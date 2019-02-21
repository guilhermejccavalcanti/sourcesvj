package com.fasterxml.jackson.databind.deser.std;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

public class NumberDeserializers {
  private final static HashSet<String> _classNames = new HashSet<String>();
  static {
    Class<?>[] numberTypes = new Class<?>[]{ Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Number.class, BigDecimal.class, BigInteger.class } ;
    for (Class<?> cls : numberTypes) {
      _classNames.add(cls.getName());
    }
  }
  public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
    if (rawType.isPrimitive()) {
      if (rawType == Integer.TYPE) {
        return IntegerDeserializer.primitiveInstance;
      }
      if (rawType == Boolean.TYPE) {
        return BooleanDeserializer.primitiveInstance;
      }
      if (rawType == Long.TYPE) {
        return LongDeserializer.primitiveInstance;
      }
      if (rawType == Double.TYPE) {
        return DoubleDeserializer.primitiveInstance;
      }
      if (rawType == Character.TYPE) {
        return CharacterDeserializer.primitiveInstance;
      }
      if (rawType == Byte.TYPE) {
        return ByteDeserializer.primitiveInstance;
      }
      if (rawType == Short.TYPE) {
        return ShortDeserializer.primitiveInstance;
      }
      if (rawType == Float.TYPE) {
        return FloatDeserializer.primitiveInstance;
      }
    }
    else 
      if (_classNames.contains(clsName)) {
        if (rawType == Integer.class) {
          return IntegerDeserializer.wrapperInstance;
        }
        if (rawType == Boolean.class) {
          return BooleanDeserializer.wrapperInstance;
        }
        if (rawType == Long.class) {
          return LongDeserializer.wrapperInstance;
        }
        if (rawType == Double.class) {
          return DoubleDeserializer.wrapperInstance;
        }
        if (rawType == Character.class) {
          return CharacterDeserializer.wrapperInstance;
        }
        if (rawType == Byte.class) {
          return ByteDeserializer.wrapperInstance;
        }
        if (rawType == Short.class) {
          return ShortDeserializer.wrapperInstance;
        }
        if (rawType == Float.class) {
          return FloatDeserializer.wrapperInstance;
        }
        if (rawType == Number.class) {
          return NumberDeserializer.instance;
        }
        if (rawType == BigDecimal.class) {
          return BigDecimalDeserializer.instance;
        }
        if (rawType == BigInteger.class) {
          return BigIntegerDeserializer.instance;
        }
      }
      else {
        return null;
      }
    throw new IllegalArgumentException("Internal error: can\'t find deserializer for " + rawType.getName());
  }
  protected abstract static class PrimitiveOrWrapperDeserializer<T extends java.lang.Object> extends StdScalarDeserializer<T> {
    private static final long serialVersionUID = 1L;
    protected final T _nullValue;
    protected final T _emptyValue;
    protected final boolean _primitive;
    protected PrimitiveOrWrapperDeserializer(Class<T> vc, T nvl, T empty) {
      super(vc);
      _nullValue = nvl;
      _emptyValue = empty;
      _primitive = vc.isPrimitive();
    }
    @Override public AccessPattern getNullAccessPattern() {
      if (_primitive) {
        return AccessPattern.DYNAMIC;
      }
      if (_nullValue == null) {
        return AccessPattern.ALWAYS_NULL;
      }
      return AccessPattern.CONSTANT;
    }
    @Override public final T getNullValue(DeserializationContext ctxt) throws JsonMappingException {
      if (_primitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
        ctxt.reportInputMismatch(this, "Can not map `null` into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to \'false\' to allow)", handledType().toString());
      }
      return _nullValue;
    }
    @Override public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
      return _emptyValue;
    }
  }
  
  @JacksonStdImpl public final static class BooleanDeserializer extends PrimitiveOrWrapperDeserializer<Boolean> {
    private static final long serialVersionUID = 1L;
    final static BooleanDeserializer primitiveInstance = new BooleanDeserializer(Boolean.TYPE, Boolean.FALSE);
    final static BooleanDeserializer wrapperInstance = new BooleanDeserializer(Boolean.class, null);
    public BooleanDeserializer(Class<Boolean> cls, Boolean nvl) {
      super(cls, nvl, Boolean.FALSE);
    }
    @Override public Boolean deserialize(JsonParser j, DeserializationContext ctxt) throws IOException {
      return _parseBoolean(j, ctxt);
    }
    @Override public Boolean deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return _parseBoolean(p, ctxt);
    }
    protected final Boolean _parseBoolean(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_TRUE) {
        return Boolean.TRUE;
      }
      if (t == JsonToken.VALUE_FALSE) {
        return Boolean.FALSE;
      }
      if (t == JsonToken.VALUE_NUMBER_INT) {
        return Boolean.valueOf(_parseBooleanFromInt(p, ctxt));
      }
      if (t == JsonToken.VALUE_NULL) {
        return (Boolean)_coerceNullToken(ctxt, _primitive);
      }
      if (t == JsonToken.VALUE_STRING) {
        String text = p.getText().trim();
        if ("true".equals(text) || "True".equals(text)) {
          return Boolean.TRUE;
        }
        if ("false".equals(text) || "False".equals(text)) {
          return Boolean.FALSE;
        }
        if (text.length() == 0) {
          return (Boolean)_coerceEmptyString(ctxt, _primitive);
        }
        if (_hasTextualNull(text)) {
          return (Boolean)_coerceTextualNull(ctxt, _primitive);
        }
        return (Boolean)ctxt.handleWeirdStringValue(_valueClass, text, "only \"true\" or \"false\" recognized");
      }
      if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
        p.nextToken();
        final Boolean parsed = _parseBoolean(p, ctxt);
        t = p.nextToken();
        if (t != JsonToken.END_ARRAY) {
          handleMissingEndArrayForSingle(p, ctxt);
        }
        return parsed;
      }
      return (Boolean)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  
  @JacksonStdImpl public static class ByteDeserializer extends PrimitiveOrWrapperDeserializer<Byte> {
    private static final long serialVersionUID = 1L;
    final static ByteDeserializer primitiveInstance = new ByteDeserializer(Byte.TYPE, (byte)0);
    final static ByteDeserializer wrapperInstance = new ByteDeserializer(Byte.class, null);
    public ByteDeserializer(Class<Byte> cls, Byte nvl) {
      super(cls, nvl, (byte)0);
    }
    @Override public Byte deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return _parseByte(p, ctxt);
    }
    protected Byte _parseByte(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_NUMBER_INT) {
        return p.getByteValue();
      }
      if (t == JsonToken.VALUE_STRING) {
        String text = p.getText().trim();
        if (_hasTextualNull(text)) {
          return (Byte)_coerceTextualNull(ctxt, _primitive);
        }
        int value;
        try {
          int len = text.length();
          if (len == 0) {
            return (Byte)_coerceEmptyString(ctxt, _primitive);
          }
          value = NumberInput.parseInt(text);
        }
        catch (IllegalArgumentException iae) {
          return (Byte)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid Byte value");
        }
        if (_byteOverflow(value)) {
          return (Byte)ctxt.handleWeirdStringValue(_valueClass, text, "overflow, value can not be represented as 8-bit value");
        }
        return Byte.valueOf((byte)value);
      }
      if (t == JsonToken.VALUE_NUMBER_FLOAT) {
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
          _failDoubleToIntCoercion(p, ctxt, "Byte");
        }
        return p.getByteValue();
      }
      if (t == JsonToken.VALUE_NULL) {
        return (Byte)_coerceNullToken(ctxt, _primitive);
      }
      if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
        p.nextToken();
        final Byte parsed = _parseByte(p, ctxt);
        t = p.nextToken();
        if (t != JsonToken.END_ARRAY) {
          handleMissingEndArrayForSingle(p, ctxt);
        }
        return parsed;
      }
      return (Byte)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  
  @JacksonStdImpl public static class ShortDeserializer extends PrimitiveOrWrapperDeserializer<Short> {
    private static final long serialVersionUID = 1L;
    final static ShortDeserializer primitiveInstance = new ShortDeserializer(Short.TYPE, Short.valueOf((short)0));
    final static ShortDeserializer wrapperInstance = new ShortDeserializer(Short.class, null);
    public ShortDeserializer(Class<Short> cls, Short nvl) {
      super(cls, nvl, (short)0);
    }
    @Override public Short deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return _parseShort(p, ctxt);
    }
    protected Short _parseShort(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_NUMBER_INT) {
        return p.getShortValue();
      }
      if (t == JsonToken.VALUE_STRING) {
        String text = p.getText().trim();
        int value;
        try {
          int len = text.length();
          if (len == 0) {
            return (Short)_coerceEmptyString(ctxt, _primitive);
          }
          if (_hasTextualNull(text)) {
            return (Short)_coerceTextualNull(ctxt, _primitive);
          }
          value = NumberInput.parseInt(text);
        }
        catch (IllegalArgumentException iae) {
          return (Short)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid Short value");
        }
        if (_shortOverflow(value)) {
          return (Short)ctxt.handleWeirdStringValue(_valueClass, text, "overflow, value can not be represented as 16-bit value");
        }
        return Short.valueOf((short)value);
      }
      if (t == JsonToken.VALUE_NUMBER_FLOAT) {
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
          _failDoubleToIntCoercion(p, ctxt, "Short");
        }
        return p.getShortValue();
      }
      if (t == JsonToken.VALUE_NULL) {
        return (Short)_coerceNullToken(ctxt, _primitive);
      }
      if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
        p.nextToken();
        final Short parsed = _parseShort(p, ctxt);
        t = p.nextToken();
        if (t != JsonToken.END_ARRAY) {
          handleMissingEndArrayForSingle(p, ctxt);
        }
        return parsed;
      }
      return (Short)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  
  @JacksonStdImpl public static class CharacterDeserializer extends PrimitiveOrWrapperDeserializer<Character> {
    private static final long serialVersionUID = 1L;
    final static CharacterDeserializer primitiveInstance = new CharacterDeserializer(Character.TYPE, '\u0000');
    final static CharacterDeserializer wrapperInstance = new CharacterDeserializer(Character.class, null);
    public CharacterDeserializer(Class<Character> cls, Character nvl) {
      super(cls, nvl, '\u0000');
    }
    @Override public Character deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      switch (p.getCurrentTokenId()){
        case JsonTokenId.ID_NUMBER_INT:
        int value = p.getIntValue();
        if (value >= 0 && value <= 0xFFFF) {
          return Character.valueOf((char)value);
        }
        break ;
        case JsonTokenId.ID_STRING:
        String text = p.getText();
        if (text.length() == 1) {
          return Character.valueOf(text.charAt(0));
        }
        if (text.length() == 0) {
          return (Character)_coerceEmptyString(ctxt, _primitive);
        }
        break ;
        case JsonTokenId.ID_NULL:
        return 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\jackson-databind\revisions\rev_ab494a1_23d49e3\rev_left_ab494a1\src\main\java\com\fasterxml\jackson\databind\deser\std\NumberDeserializers.java
(Character)_coerceNullToken(ctxt, _primitive)
=======
_deserializeFromArray(p, ctxt)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\jackson-databind\revisions\rev_ab494a1_23d49e3\rev_right_23d49e3\src\main\java\com\fasterxml\jackson\databind\deser\std\NumberDeserializers.java
;
        default:
        if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
          p.nextToken();
          final Character C = deserialize(p, ctxt);
          if (p.nextToken() != JsonToken.END_ARRAY) {
            handleMissingEndArrayForSingle(p, ctxt);
          }
          return C;
        }
        default:
      }
      return (Character)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  
  @JacksonStdImpl public final static class IntegerDeserializer extends PrimitiveOrWrapperDeserializer<Integer> {
    private static final long serialVersionUID = 1L;
    final static IntegerDeserializer primitiveInstance = new IntegerDeserializer(Integer.TYPE, 0);
    final static IntegerDeserializer wrapperInstance = new IntegerDeserializer(Integer.class, null);
    public IntegerDeserializer(Class<Integer> cls, Integer nvl) {
      super(cls, nvl, 0);
    }
    @Override public boolean isCachable() {
      return true;
    }
    @Override public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
        return p.getIntValue();
      }
      return _parseInteger(p, ctxt);
    }
    @Override public Integer deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
        return p.getIntValue();
      }
      return _parseInteger(p, ctxt);
    }
    protected final Integer _parseInteger(JsonParser p, DeserializationContext ctxt) throws IOException {
      switch (p.getCurrentTokenId()){
        case JsonTokenId.ID_NUMBER_INT:
        return Integer.valueOf(p.getIntValue());
        case JsonTokenId.ID_NUMBER_FLOAT:
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
          _failDoubleToIntCoercion(p, ctxt, "Integer");
        }
        return Integer.valueOf(p.getValueAsInt());
        case JsonTokenId.ID_STRING:
        String text = p.getText().trim();
        try {
          int len = text.length();
          if (_hasTextualNull(text)) {
            return (Integer)_coerceTextualNull(ctxt, _primitive);
          }
          if (len > 9) {
            long l = Long.parseLong(text);
            if (_intOverflow(l)) {
              return (Integer)ctxt.handleWeirdStringValue(_valueClass, text, String.format("Overflow: numeric value (%s) out of range of Integer (%d - %d)", text, Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            return Integer.valueOf((int)l);
          }
          if (len == 0) {
            return (Integer)_coerceEmptyString(ctxt, _primitive);
          }
          return Integer.valueOf(NumberInput.parseInt(text));
        }
        catch (IllegalArgumentException iae) {
          return (Integer)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid Integer value");
        }
        case JsonTokenId.ID_NULL:
        return (Integer)_coerceNullToken(ctxt, _primitive);
        case JsonTokenId.ID_START_ARRAY:
        if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
          p.nextToken();
          final Integer parsed = _parseInteger(p, ctxt);
          if (p.nextToken() != JsonToken.END_ARRAY) {
            handleMissingEndArrayForSingle(p, ctxt);
          }
          return parsed;
        }
        break ;
      }
      return (Integer)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  
  @JacksonStdImpl public final static class LongDeserializer extends PrimitiveOrWrapperDeserializer<Long> {
    private static final long serialVersionUID = 1L;
    final static LongDeserializer primitiveInstance = new LongDeserializer(Long.TYPE, 0L);
    final static LongDeserializer wrapperInstance = new LongDeserializer(Long.class, null);
    public LongDeserializer(Class<Long> cls, Long nvl) {
      super(cls, nvl, 0L);
    }
    @Override public boolean isCachable() {
      return true;
    }
    @Override public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
        return p.getLongValue();
      }
      return _parseLong(p, ctxt);
    }
    protected final Long _parseLong(JsonParser p, DeserializationContext ctxt) throws IOException {
      switch (p.getCurrentTokenId()){
        case JsonTokenId.ID_NUMBER_INT:
        return p.getLongValue();
        case JsonTokenId.ID_NUMBER_FLOAT:
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
          _failDoubleToIntCoercion(p, ctxt, "Long");
        }
        return p.getValueAsLong();
        case JsonTokenId.ID_STRING:
        String text = p.getText().trim();
        if (text.length() == 0) {
          return (Long)_coerceEmptyString(ctxt, _primitive);
        }
        if (_hasTextualNull(text)) {
          return (Long)_coerceTextualNull(ctxt, _primitive);
        }
        try {
          return Long.valueOf(NumberInput.parseLong(text));
        }
        catch (IllegalArgumentException iae) {
        }
        return (Long)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid Long value");
        case JsonTokenId.ID_NULL:
        return (Long)_coerceNullToken(ctxt, _primitive);
        case JsonTokenId.ID_START_ARRAY:
        if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
          p.nextToken();
          final Long parsed = _parseLong(p, ctxt);
          JsonToken t = p.nextToken();
          if (t != JsonToken.END_ARRAY) {
            handleMissingEndArrayForSingle(p, ctxt);
          }
          return parsed;
        }
        break ;
      }
      return (Long)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  
  @JacksonStdImpl public static class FloatDeserializer extends PrimitiveOrWrapperDeserializer<Float> {
    private static final long serialVersionUID = 1L;
    final static FloatDeserializer primitiveInstance = new FloatDeserializer(Float.TYPE, 0.f);
    final static FloatDeserializer wrapperInstance = new FloatDeserializer(Float.class, null);
    public FloatDeserializer(Class<Float> cls, Float nvl) {
      super(cls, nvl, 0.f);
    }
    @Override public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return _parseFloat(p, ctxt);
    }
    protected final Float _parseFloat(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_NUMBER_FLOAT || t == JsonToken.VALUE_NUMBER_INT) {
        return p.getFloatValue();
      }
      if (t == JsonToken.VALUE_STRING) {
        String text = p.getText().trim();
        if ((text.length() == 0)) {
          return (Float)_coerceEmptyString(ctxt, _primitive);
        }
        if (_hasTextualNull(text)) {
          return (Float)_coerceTextualNull(ctxt, _primitive);
        }
        switch (text.charAt(0)){
          case 'I':
          if (_isPosInf(text)) {
            return Float.POSITIVE_INFINITY;
          }
          break ;
          case 'N':
          if (_isNaN(text)) {
            return Float.NaN;
          }
          break ;
          case '-':
          if (_isNegInf(text)) {
            return Float.NEGATIVE_INFINITY;
          }
          break ;
        }
        try {
          return Float.parseFloat(text);
        }
        catch (IllegalArgumentException iae) {
        }
        return (Float)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid Float value");
      }
      if (t == JsonToken.VALUE_NULL) {
        return (Float)_coerceNullToken(ctxt, _primitive);
      }
      if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
        p.nextToken();
        final Float parsed = _parseFloat(p, ctxt);
        t = p.nextToken();
        if (t != JsonToken.END_ARRAY) {
          handleMissingEndArrayForSingle(p, ctxt);
        }
        return parsed;
      }
      return (Float)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  
  @JacksonStdImpl public static class DoubleDeserializer extends PrimitiveOrWrapperDeserializer<Double> {
    private static final long serialVersionUID = 1L;
    final static DoubleDeserializer primitiveInstance = new DoubleDeserializer(Double.TYPE, 0.d);
    final static DoubleDeserializer wrapperInstance = new DoubleDeserializer(Double.class, null);
    public DoubleDeserializer(Class<Double> cls, Double nvl) {
      super(cls, nvl, 0.d);
    }
    @Override public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return _parseDouble(p, ctxt);
    }
    @Override public Double deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return _parseDouble(p, ctxt);
    }
    protected final Double _parseDouble(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
        return p.getDoubleValue();
      }
      if (t == JsonToken.VALUE_STRING) {
        String text = p.getText().trim();
        if ((text.length() == 0)) {
          return (Double)_coerceEmptyString(ctxt, _primitive);
        }
        if (_hasTextualNull(text)) {
          return (Double)_coerceTextualNull(ctxt, _primitive);
        }
        switch (text.charAt(0)){
          case 'I':
          if (_isPosInf(text)) {
            return Double.POSITIVE_INFINITY;
          }
          break ;
          case 'N':
          if (_isNaN(text)) {
            return Double.NaN;
          }
          break ;
          case '-':
          if (_isNegInf(text)) {
            return Double.NEGATIVE_INFINITY;
          }
          break ;
        }
        try {
          return parseDouble(text);
        }
        catch (IllegalArgumentException iae) {
        }
        return (Double)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid Double value");
      }
      if (t == JsonToken.VALUE_NULL) {
        return (Double)_coerceNullToken(ctxt, _primitive);
      }
      if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
        p.nextToken();
        final Double parsed = _parseDouble(p, ctxt);
        t = p.nextToken();
        if (t != JsonToken.END_ARRAY) {
          handleMissingEndArrayForSingle(p, ctxt);
        }
        return parsed;
      }
      return (Double)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  
  @SuppressWarnings(value = {"serial", }) @JacksonStdImpl public static class NumberDeserializer extends StdScalarDeserializer<Object> {
    public final static NumberDeserializer instance = new NumberDeserializer();
    public NumberDeserializer() {
      super(Number.class);
    }
    @Override public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      switch (p.getCurrentTokenId()){
        case JsonTokenId.ID_NUMBER_INT:
        if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
          return _coerceIntegral(p, ctxt);
        }
        return p.getNumberValue();
        case JsonTokenId.ID_NUMBER_FLOAT:
        if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
          if (!p.isNaN()) {
            return p.getDecimalValue();
          }
        }
        return p.getNumberValue();
        case JsonTokenId.ID_STRING:
        String text = p.getText().trim();
        if ((text.length() == 0)) {
          return getNullValue(ctxt);
        }
        if (_hasTextualNull(text)) {
          return getNullValue(ctxt);
        }
        if (_isPosInf(text)) {
          return Double.POSITIVE_INFINITY;
        }
        if (_isNegInf(text)) {
          return Double.NEGATIVE_INFINITY;
        }
        if (_isNaN(text)) {
          return Double.NaN;
        }
        try {
          if (!_isIntNumber(text)) {
            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
              return new BigDecimal(text);
            }
            return Double.valueOf(text);
          }
          if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
            return new BigInteger(text);
          }
          long value = Long.parseLong(text);
          if (!ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS)) {
            if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
              return Integer.valueOf((int)value);
            }
          }
          return Long.valueOf(value);
        }
        catch (IllegalArgumentException iae) {
          return ctxt.handleWeirdStringValue(_valueClass, text, "not a valid number");
        }
        case JsonTokenId.ID_START_ARRAY:
        return _deserializeFromArray(p, ctxt);
        break ;
      }
      return ctxt.handleUnexpectedToken(_valueClass, p);
    }
    @Override public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      switch (p.getCurrentTokenId()){
        case JsonTokenId.ID_NUMBER_INT:
        case JsonTokenId.ID_NUMBER_FLOAT:
        case JsonTokenId.ID_STRING:
        return deserialize(p, ctxt);
      }
      return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
    }
  }
  
  @SuppressWarnings(value = {"serial", }) @JacksonStdImpl public static class BigIntegerDeserializer extends StdScalarDeserializer<BigInteger> {
    public final static BigIntegerDeserializer instance = new BigIntegerDeserializer();
    public BigIntegerDeserializer() {
      super(BigInteger.class);
    }
    @Override public Object getEmptyValue(DeserializationContext ctxt) {
      return BigInteger.ZERO;
    }
    @SuppressWarnings(value = {"incomplete-switch", }) @Override public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      switch (p.getCurrentTokenId()){
        case JsonTokenId.ID_NUMBER_INT:
        switch (p.getNumberType()){
          case INT:
          case LONG:
          case BIG_INTEGER:
          return p.getBigIntegerValue();
        }
        break ;
        case JsonTokenId.ID_NUMBER_FLOAT:
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
          _failDoubleToIntCoercion(p, ctxt, "java.math.BigInteger");
        }
        return p.getDecimalValue().toBigInteger();
        case JsonTokenId.ID_START_ARRAY:
        return _deserializeFromArray(p, ctxt);
        case JsonTokenId.ID_STRING:
        String text = p.getText().trim();
        if (_isEmptyOrTextualNull(text)) {
          return getNullValue(ctxt);
        }
        try {
          return new BigInteger(text);
        }
        catch (IllegalArgumentException iae) {
          return (BigInteger)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid representation");
        }
        try {
          return new BigInteger(text);
        }
        catch (IllegalArgumentException iae) {
          return (BigInteger)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid representation");
        }
      }
      return (BigInteger)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
  
  @SuppressWarnings(value = {"serial", }) @JacksonStdImpl public static class BigDecimalDeserializer extends StdScalarDeserializer<BigDecimal> {
    public final static BigDecimalDeserializer instance = new BigDecimalDeserializer();
    public BigDecimalDeserializer() {
      super(BigDecimal.class);
    }
    @Override public Object getEmptyValue(DeserializationContext ctxt) {
      return BigDecimal.ZERO;
    }
    @Override public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      switch (p.getCurrentTokenId()){
        case JsonTokenId.ID_NUMBER_INT:
        case JsonTokenId.ID_NUMBER_FLOAT:
        return p.getDecimalValue();
        case JsonTokenId.ID_STRING:
        String text = p.getText().trim();
        if (_isEmptyOrTextualNull(text)) {
          return getNullValue(ctxt);
        }
        try {
          return new BigDecimal(text);
        }
        catch (IllegalArgumentException iae) {
          return (BigDecimal)ctxt.handleWeirdStringValue(_valueClass, text, "not a valid representation");
        }
        case JsonTokenId.ID_START_ARRAY:
        return _deserializeFromArray(p, ctxt);
        break ;
      }
      return (BigDecimal)ctxt.handleUnexpectedToken(_valueClass, p);
    }
  }
}
