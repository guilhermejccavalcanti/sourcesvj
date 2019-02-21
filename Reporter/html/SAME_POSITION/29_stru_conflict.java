<<<<<<< MINE
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
=======
return isWrapperReq ? new EnhanceEntity(entity, thriftRow.getId(), relations) : entity;
>>>>>>> YOURS

