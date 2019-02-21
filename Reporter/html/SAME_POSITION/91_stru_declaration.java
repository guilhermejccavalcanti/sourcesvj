  public Object fromColumnThriftRow(Class<?> clazz, EntityMetadata m, ThriftRow thriftRow, List<String> relationNames, boolean isWrapperReq) throws Exception {
    Object entity = clazz.newInstance();
    Map<String, Object> relations = new HashMap<String, Object>();
    PropertyAccessorHelper.setId(entity, m, thriftRow.getId());
    for (Column c : thriftRow.getColumns()) {
      String thriftColumnName = PropertyAccessorFactory.STRING.fromBytes(c.getName());
      byte[] thriftColumnValue = c.getValue();
      if (null == thriftColumnValue) {
        continue ;
      }
      com.impetus.kundera.metadata.model.Column column = m.getColumn(thriftColumnName);
      if (column != null) {
        try {
          PropertyAccessorHelper.set(entity, column.getField(), thriftColumnValue);
        }
        catch (PropertyAccessException pae) {
          log.warn(pae.getMessage());
        }
      }
      else {
        if (relationNames != null && !relationNames.isEmpty() && relationNames.contains(thriftColumnName)) {
          String value = PropertyAccessorFactory.STRING.fromBytes(thriftColumnValue);
          relations.put(thriftColumnName, value);
        }
      }
    }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_84557c9_5c6ec4a\rev_left_84557c9\kundera-cassandra\src\main\java\com\impetus\client\cassandra\pelops\PelopsDataHandler.java
return isWrapperReq ? new EnhanceEntity(entity, thriftRow.getId(), relations) : entity;
=======
if (columns != null && (columns.size() == 1 ? columns.iterator().next() != null : true)) {
      for (Column c : thriftRow.getColumns()) {
        String thriftColumnName = PropertyAccessorFactory.STRING.fromBytes(c.getName());
        byte[] thriftColumnValue = c.getValue();
        if (null == thriftColumnValue) {
          continue ;
        }
        com.impetus.kundera.metadata.model.Column column = m.getColumn(thriftColumnName);
        if (column != null) {
          try {
            PropertyAccessorHelper.set(entity, column.getField(), thriftColumnValue);
          }
          catch (PropertyAccessException pae) {
            log.warn(pae.getMessage());
          }
        }
        else {
          if (relationNames != null && !relationNames.isEmpty() && relationNames.contains(thriftColumnName)) {
            String value = PropertyAccessorFactory.STRING.fromBytes(thriftColumnValue);
            relations.put(thriftColumnName, value);
          }
        }
      }
    }
    else {
      for (SuperColumn c : thriftRow.getSuperColumns()) {
        for (Column cc : c.getColumns()) {
          String thriftColumnName = PropertyAccessorFactory.STRING.fromBytes(cc.getName());
          byte[] thriftColumnValue = cc.getValue();
          if (null == thriftColumnValue) {
            continue ;
          }
          com.impetus.kundera.metadata.model.Column column = m.getColumn(thriftColumnName);
          if (column != null) {
            try {
              PropertyAccessorHelper.set(entity, column.getField(), thriftColumnValue);
            }
            catch (PropertyAccessException pae) {
              log.warn(pae.getMessage());
            }
          }
          else {
            if (relationNames != null && !relationNames.isEmpty() && relationNames.contains(thriftColumnName)) {
              String value = PropertyAccessorFactory.STRING.fromBytes(thriftColumnValue);
              relations.put(thriftColumnName, value);
            }
          }
        }
      }
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_84557c9_5c6ec4a\rev_right_5c6ec4a\kundera-cassandra\src\main\java\com\impetus\client\cassandra\pelops\PelopsDataHandler.java

  }


