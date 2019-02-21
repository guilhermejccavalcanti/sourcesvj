<<<<<<< MINE
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
>>>>>>> YOURS

