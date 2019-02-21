  public static Scheme getScheme(String inputFormatClass, Properties properties, List<String> fieldNames) {
    Scheme scheme;
    if (isNotEmpty(inputFormatClass)) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\storm\revisions\rev_f14e0b2_f357d84\rev_left_f14e0b2\external\sql\storm-sql-runtime\src\jvm\org\apache\storm\sql\runtime\utils\SerdeUtils.java
switch (inputFormatClass){
        case "org.apache.storm.sql.runtime.serde.json.JsonScheme":
        scheme = new JsonScheme(fieldNames);
        break ;
        case "org.apache.storm.sql.runtime.serde.json.TsvScheme":
        String delimiter = properties.getProperty("tsv.delimiter", "\t");
        scheme = new TsvScheme(fieldNames, delimiter.charAt(0));
        break ;
        case "org.apache.storm.sql.runtime.serde.json.CsvScheme":
        scheme = new CsvScheme(fieldNames);
        break ;
        case "org.apache.storm.sql.runtime.serde.avro.AvroScheme":
        String schemaString = properties.getProperty("avro.schema");
        Preconditions.checkArgument(isNotEmpty(schemaString), "avro.schema can not be empty");
        scheme = new AvroScheme(schemaString, fieldNames);
        break ;
        default:
        scheme = Utils.newInstance(inputFormatClass);
      }
=======
if (JsonScheme.class.getName().equals(inputFormatClass)) {
        scheme = new JsonScheme(fieldNames);
      }
      else 
        if (AvroScheme.class.getName().equals(inputFormatClass)) {
          String schemaString = properties.getProperty("input.avro.schema");
          Preconditions.checkArgument(isNotEmpty(schemaString), "input.avro.schema can not be empty");
          scheme = new AvroScheme(schemaString, fieldNames);
        }
        else {
          scheme = Utils.newInstance(inputFormatClass);
        }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\storm\revisions\rev_f14e0b2_f357d84\rev_right_f357d84\external\sql\storm-sql-runtime\src\jvm\org\apache\storm\sql\runtime\utils\SerdeUtils.java

    }
    return scheme;
  }


