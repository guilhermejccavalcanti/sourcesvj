  public boolean isDynamicType() {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\gephi\revisions\rev_677b35d_bd6cca6\rev_left_677b35d\AttributesAPI\src\org\gephi\data\attributes\api\AttributeType.java
switch (this){
      case DYNAMIC_BYTE:
      case DYNAMIC_SHORT:
      case DYNAMIC_INT:
      case DYNAMIC_LONG:
      case DYNAMIC_FLOAT:
      case DYNAMIC_DOUBLE:
      case DYNAMIC_BOOLEAN:
      case DYNAMIC_CHAR:
      case DYNAMIC_STRING:
      case DYNAMIC_BIGINTEGER:
      case DYNAMIC_BIGDECIMAL:
      case TIME_INTERVAL:
      return true;
      default:
      return false;
    }
=======
if (this.equals(DYNAMIC_BYTE) || this.equals(DYNAMIC_SHORT) || this.equals(DYNAMIC_INT) || this.equals(DYNAMIC_LONG) || this.equals(DYNAMIC_FLOAT) || this.equals(DYNAMIC_DOUBLE) || this.equals(DYNAMIC_BOOLEAN) || this.equals(DYNAMIC_CHAR) || this.equals(DYNAMIC_STRING) || this.equals(DYNAMIC_BIGINTEGER) || this.equals(DYNAMIC_BIGDECIMAL) || this.equals(TIME_INTERVAL)) {
      return true;
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\gephi\revisions\rev_677b35d_bd6cca6\rev_right_bd6cca6\AttributesAPI\src\org\gephi\data\attributes\api\AttributeType.java

  }


