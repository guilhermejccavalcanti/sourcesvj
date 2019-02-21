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
        case JsonTokenId.ID_START_ARRAY:
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


