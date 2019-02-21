  public  <> T getProperty(final String key) {
    if (key.equals(Tokens._COUNT)) 
      return 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_7d3ea8d_340c929\rev_left_7d3ea8d\src\main\java\com\thinkaurelius\faunus\FaunusElement.java
(T)Long.valueOf(this.pathCount())
=======
(T)(Object)this.pathCount()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_7d3ea8d_340c929\rev_right_340c929\src\main\java\com\thinkaurelius\faunus\FaunusElement.java
;
    return null == this.properties ? null : (T)this.properties.get(key);
  }


