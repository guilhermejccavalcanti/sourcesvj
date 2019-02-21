<<<<<<< MINE
userType = config.checkAutoType(typeName, expectClass);
=======
if (expectClass == null || (userType != null && expectClass.isAssignableFrom(userType))) {
                  deserializer = parser.getConfig().getDeserializer(userType);
                }
                else {
                  throw new JSONException("type not match");
                }
>>>>>>> YOURS

