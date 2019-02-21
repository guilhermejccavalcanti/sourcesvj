<<<<<<< MINE
if (childClazz.equals(e.getEntity().getClass())) {
                  childs = (List<Object>)childClient.find(childClazz, rsSet.toArray(new String[]{  } ));
                }
                else {
                  childs = (List<Object>)persistenceDelegeator.find(childClazz, rsSet.toArray(new String[]{  } ));
                }
=======
childs = (List<Object>)(childClazz.equals(e.getEntity().getClass()) ? childClient.findAll(childClazz, rsSet.toArray(new String[]{  } )) : persistenceDelegeator.find(childClazz, rsSet.toArray(new String[]{  } )));
>>>>>>> YOURS

