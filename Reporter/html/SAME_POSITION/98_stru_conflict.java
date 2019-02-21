<<<<<<< MINE
{
                          for (Class face : valueClass.getInterfaces()) {
                            if (face.equals(StringCoercible.class)) {
                              serializer = StringCoercibleSerializer.get();
                            }
                          }
                          if (serializer == null) {
                            serializer = ObjectSerializer.get();
                          }
                        }
=======
if (valueClass.equals(Date.class)) {
                            serializer = DateSerializer.get();
                          }
                          else {
                            serializer = ObjectSerializer.get();
                          }
>>>>>>> YOURS

