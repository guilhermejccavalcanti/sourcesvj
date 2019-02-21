  public boolean getBoolean(String key, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\nutz\revisions\rev_0ef7278_2efd2b8\rev_left_0ef7278\src\org\nutz\ioc\impl\PropertiesProxy.java
boolean defaultValue
=======
boolean dfval
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\nutz\revisions\rev_0ef7278_2efd2b8\rev_right_2efd2b8\src\org\nutz\ioc\impl\PropertiesProxy.java
) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\nutz\revisions\rev_0ef7278_2efd2b8\rev_left_0ef7278\src\org\nutz\ioc\impl\PropertiesProxy.java
try {
      return Boolean.parseBoolean(get(key));
    }
    catch (Exception e) {
      return defaultValue;
    }
=======
String val = get(key);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\nutz\revisions\rev_0ef7278_2efd2b8\rev_right_2efd2b8\src\org\nutz\ioc\impl\PropertiesProxy.java

  }


