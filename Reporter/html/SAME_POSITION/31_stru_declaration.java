  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(genericUDF.getClass().getSimpleName());
    sb.append("(");
    if (chidren != null) {
      for (int i = 0; i < chidren.size(); i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append(
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_95bfd92_6c2d71c\rev_left_95bfd92\ql\src\java\org\apache\hadoop\hive\ql\plan\ExprNodeGenericFuncDesc.java
chidren.get(i).toString()
=======
chidren.get(i)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_95bfd92_6c2d71c\rev_right_6c2d71c\ql\src\java\org\apache\hadoop\hive\ql\plan\ExprNodeGenericFuncDesc.java
);
      }
    }
    sb.append(")");
    return sb.toString();
  }


