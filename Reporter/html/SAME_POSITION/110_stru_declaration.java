  private void addCompoundSortField(XContentParser parser, SearchContext context, List<SortField> sortFields) throws Exception {
    XContentParser.Token token;
    while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT){
      if (token == XContentParser.Token.FIELD_NAME) {
        String fieldName = parser.currentName();
        boolean reverse = false;
        String missing = null;
        String innerJsonName = null;
        String unmappedType = null;
        MultiValueMode sortMode = null;
        NestedInnerQueryParseSupport nestedFilterParseHelper = null;
        token = parser.nextToken();
        if (token == XContentParser.Token.VALUE_STRING) {
          String direction = parser.text();
          if (direction.equals("asc")) {
            reverse = SCORE_FIELD_NAME.equals(fieldName);
          }
          else 
            if (direction.equals("desc")) {
              reverse = !SCORE_FIELD_NAME.equals(fieldName);
            }
            else {
              throw new IllegalArgumentException("sort direction [" + fieldName + "] not supported");
            }
          addSortField(context, sortFields, fieldName, reverse, unmappedType, missing, sortMode, nestedFilterParseHelper);
        }
        else {
          if (PARSERS.containsKey(fieldName)) {
            sortFields.add(PARSERS.get(fieldName).parse(parser, context.getQueryShardContext()));
          }
          else {
            while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT){
              if (token == XContentParser.Token.FIELD_NAME) {
                innerJsonName = parser.currentName();
              }
              else 
                if (token.isValue()) {
                  if ("reverse".equals(innerJsonName)) {
                    reverse = parser.booleanValue();
                  }
                  else 
                    if ("order".equals(innerJsonName)) {
                      if ("asc".equals(parser.text())) {
                        reverse = SCORE_FIELD_NAME.equals(fieldName);
                      }
                      else 
                        if ("desc".equals(parser.text())) {
                          reverse = !SCORE_FIELD_NAME.equals(fieldName);
                        }
                    }
                    else 
                      if ("missing".equals(innerJsonName)) {
                        missing = parser.textOrNull();
                      }
                      else 
                        if (context.parseFieldMatcher().match(innerJsonName, UNMAPPED_TYPE)) {
                          unmappedType = parser.textOrNull();
                        }
                        else 
                          if ("mode".equals(innerJsonName)) {
                            sortMode = MultiValueMode.fromString(parser.text());
                          }
                          else 
                            if ("nested_path".equals(innerJsonName) || "nestedPath".equals(innerJsonName)) {
                              if (nestedFilterParseHelper == null) {
                                nestedFilterParseHelper = new NestedInnerQueryParseSupport(parser, context);
                              }
                              nestedFilterParseHelper.setPath(parser.text());
                            }
                            else 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\elasticsearch\revisions\rev_7d35db6_a4b5fbe\rev_left_7d35db6\core\src\main\java\org\elasticsearch\search\sort\SortParseElement.java
{
                              throw new IllegalArgumentException("sort option [" + innerJsonName + "] not supported");
                            }
=======
if ("nested_path".equals(innerJsonName) || "nestedPath".equals(innerJsonName)) {
                                if (nestedFilterParseHelper == null) {
                                  nestedFilterParseHelper = new NestedInnerQueryParseSupport(parser, context.getQueryShardContext());
                                }
                                nestedFilterParseHelper.setPath(parser.text());
                              }
                              else {
                                throw new IllegalArgumentException("sort option [" + innerJsonName + "] not supported");
                              }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\elasticsearch\revisions\rev_7d35db6_a4b5fbe\rev_right_a4b5fbe\core\src\main\java\org\elasticsearch\search\sort\SortParseElement.java

                }
            }
            addSortField(context, sortFields, fieldName, reverse, unmappedType, missing, sortMode, nestedFilterParseHelper);
          }
        }
      }
    }
  }


