  @SuppressWarnings(value = {"unchecked", "rawtypes", }) protected  <> T deserialze(DefaultJSONParser parser, Type type, Object fieldName, Object object, int features, int[] setFlags) {
    if (type == JSON.class || type == JSONObject.class) {
      return (T)parser.parse();
    }
    final JSONLexerBase lexer = (JSONLexerBase)parser.lexer;
    int token = lexer.token();
    if (token == JSONToken.NULL) {
      lexer.nextToken(JSONToken.COMMA);
      return null;
    }
    ParseContext context = parser.getContext();
    if (object != null && context != null) {
      context = context.parent;
    }
    ParseContext childContext = null;
    try {
      Map<String, Object> fieldValues = null;
      if (token == JSONToken.RBRACE) {
        lexer.nextToken(JSONToken.COMMA);
        if (object == null) {
          object = createInstance(parser, type);
        }
        return (T)object;
      }
      if (token == JSONToken.LBRACKET) {
        final int mask = Feature.SupportArrayToBean.mask;
        boolean isSupportArrayToBean = (beanInfo.parserFeatures & mask) != 0 || lexer.isEnabled(Feature.SupportArrayToBean) || (features & mask) != 0;
        if (isSupportArrayToBean) {
          return deserialzeArrayMapping(parser, type, fieldName, object);
        }
      }
      if (token != JSONToken.LBRACE && token != JSONToken.COMMA) {
        if (lexer.isBlankInput()) {
          return null;
        }
        if (token == JSONToken.LITERAL_STRING) {
          String strVal = lexer.stringVal();
          if (strVal.length() == 0) {
            lexer.nextToken();
            return null;
          }
        }
        if (token == JSONToken.LBRACKET && lexer.getCurrent() == ']') {
          lexer.next();
          lexer.nextToken();
          return null;
        }
        StringBuffer buf = (new StringBuffer()).append("syntax error, expect {, actual ").append(lexer.tokenName()).append(", pos ").append(lexer.pos());
        if (fieldName instanceof String) {
          buf.append(", fieldName ").append(fieldName);
        }
        throw new JSONException(buf.toString());
      }
      if (parser.resolveStatus == DefaultJSONParser.TypeNameRedirect) {
        parser.resolveStatus = DefaultJSONParser.NONE;
      }
      for (int fieldIndex = 0; true; fieldIndex++) {
        String key = null;
        FieldDeserializer fieldDeser = null;
        FieldInfo fieldInfo = null;
        Class<?> fieldClass = null;
        JSONField feildAnnotation = null;
        if (fieldIndex < sortedFieldDeserializers.length) {
          fieldDeser = sortedFieldDeserializers[fieldIndex];
          fieldInfo = fieldDeser.fieldInfo;
          fieldClass = fieldInfo.fieldClass;
          feildAnnotation = fieldInfo.getAnnotation();
        }
        boolean matchField = false;
        boolean valueParsed = false;
        Object fieldValue = null;
        if (fieldDeser != null) {
          char[] name_chars = fieldInfo.name_chars;
          if (fieldClass == int.class || fieldClass == Integer.class) {
            fieldValue = lexer.scanFieldInt(name_chars);
            if (lexer.matchStat > 0) {
              matchField = true;
              valueParsed = true;
            }
            else 
              if (lexer.matchStat == JSONLexer.NOT_MATCH_NAME) {
                continue ;
              }
          }
          else 
            if (fieldClass == long.class || fieldClass == Long.class) {
              fieldValue = lexer.scanFieldLong(name_chars);
              if (lexer.matchStat > 0) {
                matchField = true;
                valueParsed = true;
              }
              else 
                if (lexer.matchStat == JSONLexer.NOT_MATCH_NAME) {
                  continue ;
                }
            }
            else 
              if (fieldClass == String.class) {
                fieldValue = lexer.scanFieldString(name_chars);
                if (lexer.matchStat > 0) {
                  matchField = true;
                  valueParsed = true;
                }
                else 
                  if (lexer.matchStat == JSONLexer.NOT_MATCH_NAME) {
                    continue ;
                  }
              }
              else 
                if (fieldClass == boolean.class || fieldClass == Boolean.class) {
                  fieldValue = lexer.scanFieldBoolean(name_chars);
                  if (lexer.matchStat > 0) {
                    matchField = true;
                    valueParsed = true;
                  }
                  else 
                    if (lexer.matchStat == JSONLexer.NOT_MATCH_NAME) {
                      continue ;
                    }
                }
                else 
                  if (fieldClass == float.class || fieldClass == Float.class) {
                    fieldValue = lexer.scanFieldFloat(name_chars);
                    if (lexer.matchStat > 0) {
                      matchField = true;
                      valueParsed = true;
                    }
                    else 
                      if (lexer.matchStat == JSONLexer.NOT_MATCH_NAME) {
                        continue ;
                      }
                  }
                  else 
                    if (fieldClass == double.class || fieldClass == Double.class) {
                      fieldValue = lexer.scanFieldDouble(name_chars);
                      if (lexer.matchStat > 0) {
                        matchField = true;
                        valueParsed = true;
                      }
                      else 
                        if (lexer.matchStat == JSONLexer.NOT_MATCH_NAME) {
                          continue ;
                        }
                    }
                    else 
                      if (fieldClass.isEnum() && parser.getConfig().getDeserializer(fieldClass) instanceof EnumDeserializer && (feildAnnotation == null || feildAnnotation.deserializeUsing() == Void.class)) {
                        if (fieldDeser instanceof DefaultFieldDeserializer) {
                          ObjectDeserializer fieldValueDeserilizer = ((DefaultFieldDeserializer)fieldDeser).fieldValueDeserilizer;
                          fieldValue = this.scanEnum(lexer, name_chars, fieldValueDeserilizer);
                          if (lexer.matchStat > 0) {
                            matchField = true;
                            valueParsed = true;
                          }
                          else 
                            if (lexer.matchStat == JSONLexer.NOT_MATCH_NAME) {
                              continue ;
                            }
                        }
                      }
                      else 
                        if (fieldClass == int[].class) {
                          fieldValue = lexer.scanFieldIntArray(name_chars);
                          if (lexer.matchStat > 0) {
                            matchField = true;
                            valueParsed = true;
                          }
                          else 
                            if (lexer.matchStat == JSONLexer.NOT_MATCH_NAME) {
                              continue ;
                            }
                        }
                        else 
                          if (fieldClass == float[].class) {
                            fieldValue = lexer.scanFieldFloatArray(name_chars);
                            if (lexer.matchStat > 0) {
                              matchField = true;
                              valueParsed = true;
                            }
                            else 
                              if (lexer.matchStat == JSONLexer.NOT_MATCH_NAME) {
                                continue ;
                              }
                          }
                          else 
                            if (fieldClass == float[][].class) {
                              fieldValue = lexer.scanFieldFloatArray2(name_chars);
                              if (lexer.matchStat > 0) {
                                matchField = true;
                                valueParsed = true;
                              }
                              else 
                                if (lexer.matchStat == JSONLexer.NOT_MATCH_NAME) {
                                  continue ;
                                }
                            }
                            else 
                              if (lexer.matchField(name_chars)) {
                                matchField = true;
                              }
                              else {
                                continue ;
                              }
        }
        if (!matchField) {
          key = lexer.scanSymbol(parser.symbolTable);
          if (key == null) {
            token = lexer.token();
            if (token == JSONToken.RBRACE) {
              lexer.nextToken(JSONToken.COMMA);
              break ;
            }
            if (token == JSONToken.COMMA) {
              if (lexer.isEnabled(Feature.AllowArbitraryCommas)) {
                continue ;
              }
            }
          }
          if ("$ref" == key) {
            lexer.nextTokenWithColon(JSONToken.LITERAL_STRING);
            token = lexer.token();
            if (token == JSONToken.LITERAL_STRING) {
              String ref = lexer.stringVal();
              if ("@".equals(ref)) {
                object = context.object;
              }
              else 
                if ("..".equals(ref)) {
                  ParseContext parentContext = context.parent;
                  if (parentContext.object != null) {
                    object = parentContext.object;
                  }
                  else {
                    parser.addResolveTask(new ResolveTask(parentContext, ref));
                    parser.resolveStatus = DefaultJSONParser.NeedToResolve;
                  }
                }
                else 
                  if ("$".equals(ref)) {
                    ParseContext rootContext = context;
                    while (rootContext.parent != null){
                      rootContext = rootContext.parent;
                    }
                    if (rootContext.object != null) {
                      object = rootContext.object;
                    }
                    else {
                      parser.addResolveTask(new ResolveTask(rootContext, ref));
                      parser.resolveStatus = DefaultJSONParser.NeedToResolve;
                    }
                  }
                  else {
                    parser.addResolveTask(new ResolveTask(context, ref));
                    parser.resolveStatus = DefaultJSONParser.NeedToResolve;
                  }
            }
            else {
              throw new JSONException("illegal ref, " + JSONToken.name(token));
            }
            lexer.nextToken(JSONToken.RBRACE);
            if (lexer.token() != JSONToken.RBRACE) {
              throw new JSONException("illegal ref");
            }
            lexer.nextToken(JSONToken.COMMA);
            parser.setContext(context, object, fieldName);
            return (T)object;
          }
          if (JSON.DEFAULT_TYPE_KEY == key) {
            lexer.nextTokenWithColon(JSONToken.LITERAL_STRING);
            if (lexer.token() == JSONToken.LITERAL_STRING) {
              String typeName = lexer.stringVal();
              lexer.nextToken(JSONToken.COMMA);
              if (typeName.equals(beanInfo.typeName) || parser.isEnabled(Feature.IgnoreType)) {
                if (lexer.token() == JSONToken.RBRACE) {
                  lexer.nextToken();
                  break ;
                }
                continue ;
              }
              ParserConfig config = parser.getConfig();
              ObjectDeserializer deserializer = getSeeAlso(config, this.beanInfo, typeName);
              Class<?> userType = null;
              if (deserializer == null) {
                Class<?> expectClass = TypeUtils.getClass(type);
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\fastjson\revisions\rev_c76d44b_a12ff69\rev_left_c76d44b\src\main\java\com\alibaba\fastjson\parser\deserializer\JavaBeanDeserializer.java
userType = config.checkAutoType(typeName, expectClass);
=======
if (expectClass == null || (userType != null && expectClass.isAssignableFrom(userType))) {
                  deserializer = parser.getConfig().getDeserializer(userType);
                }
                else {
                  throw new JSONException("type not match");
                }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\fastjson\revisions\rev_c76d44b_a12ff69\rev_right_a12ff69\src\main\java\com\alibaba\fastjson\parser\deserializer\JavaBeanDeserializer.java

                deserizer = parser.getConfig().getDeserializer(userType);
              }
              return (T)deserizer.deserialze(parser, userType, fieldName);
            }
          }
        }
        if (object == null && fieldValues == null) {
          object = createInstance(parser, type);
          if (object == null) {
            fieldValues = new HashMap<String, Object>(this.fieldDeserializers.length);
          }
          childContext = parser.setContext(context, object, fieldName);
        }
        if (matchField) {
          if (!valueParsed) {
            fieldDeser.parseField(parser, object, type, fieldValues);
          }
          else {
            if (object == null) {
              fieldValues.put(fieldInfo.name, fieldValue);
            }
            else 
              if (fieldValue == null) {
                if (fieldClass != int.class && fieldClass != long.class && fieldClass != float.class && fieldClass != double.class && fieldClass != boolean.class) {
                  fieldDeser.setValue(object, fieldValue);
                }
              }
              else {
                fieldDeser.setValue(object, fieldValue);
              }
            if (lexer.matchStat == JSONLexer.END) {
              break ;
            }
          }
        }
        else {
          boolean match = parseField(parser, key, object, type, fieldValues, setFlags);
          if (!match) {
            if (lexer.token() == JSONToken.RBRACE) {
              lexer.nextToken();
              break ;
            }
            continue ;
          }
          else 
            if (lexer.token() == JSONToken.COLON) {
              throw new JSONException("syntax error, unexpect token \':\'");
            }
        }
        if (lexer.token() == JSONToken.COMMA) {
          continue ;
        }
        if (lexer.token() == JSONToken.RBRACE) {
          lexer.nextToken(JSONToken.COMMA);
          break ;
        }
        if (lexer.token() == JSONToken.IDENTIFIER || lexer.token() == JSONToken.ERROR) {
          throw new JSONException("syntax error, unexpect token " + JSONToken.name(lexer.token()));
        }
      }
      if (object == null) {
        if (fieldValues == null) {
          object = createInstance(parser, type);
          if (childContext == null) {
            childContext = parser.setContext(context, object, fieldName);
          }
          return (T)object;
        }
        FieldInfo[] fieldInfoList = beanInfo.fields;
        int size = fieldInfoList.length;
        Object[] params = new Object[size];
        for (int i = 0; i < size; ++i) {
          FieldInfo fieldInfo = fieldInfoList[i];
          Object param = fieldValues.get(fieldInfo.name);
          if (param == null) {
            Type fieldType = fieldInfo.fieldType;
            if (fieldType == byte.class) {
              param = (byte)0;
            }
            else 
              if (fieldType == short.class) {
                param = (short)0;
              }
              else 
                if (fieldType == int.class) {
                  param = 0;
                }
                else 
                  if (fieldType == long.class) {
                    param = 0L;
                  }
                  else 
                    if (fieldType == float.class) {
                      param = 0F;
                    }
                    else 
                      if (fieldType == double.class) {
                        param = 0D;
                      }
                      else 
                        if (fieldType == boolean.class) {
                          param = Boolean.FALSE;
                        }
          }
          params[i] = param;
        }
        if (beanInfo.creatorConstructor != null) {
          try {
            object = beanInfo.creatorConstructor.newInstance(params);
          }
          catch (Exception e) {
            throw new JSONException("create instance error, " + beanInfo.creatorConstructor.toGenericString(), e);
          }
        }
        else 
          if (beanInfo.factoryMethod != null) {
            try {
              object = beanInfo.factoryMethod.invoke(null, params);
            }
            catch (Exception e) {
              throw new JSONException("create factory method error, " + beanInfo.factoryMethod.toString(), e);
            }
          }
      }
      Method buildMethod = beanInfo.buildMethod;
      if (buildMethod == null) {
        return (T)object;
      }
      Object builtObj;
      try {
        builtObj = buildMethod.invoke(object);
      }
      catch (Exception e) {
        throw new JSONException("build object error", e);
      }
      return (T)builtObj;
    }
  }


