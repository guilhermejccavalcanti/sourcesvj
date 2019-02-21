package org.apache.storm.sql.runtime.utils;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import com.google.common.base.Preconditions;
import org.apache.avro.generic.GenericData;
import org.apache.avro.util.Utf8;
import org.apache.storm.spout.Scheme;
import org.apache.storm.sql.runtime.IOutputSerializer;
import org.apache.storm.sql.runtime.serde.csv.CsvScheme;
import org.apache.storm.sql.runtime.serde.avro.AvroScheme;
import org.apache.storm.sql.runtime.serde.csv.CsvSerializer;
import org.apache.storm.sql.runtime.serde.avro.AvroSerializer;
import org.apache.storm.sql.runtime.serde.json.JsonScheme;
import org.apache.storm.sql.runtime.serde.json.JsonSerializer;
import org.apache.storm.sql.runtime.serde.tsv.TsvScheme;
import org.apache.storm.utils.Utils;
import org.apache.storm.sql.runtime.serde.tsv.TsvSerializer;
import java.util.ArrayList;
import org.apache.storm.utils.Utils;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Map;
import java.util.Properties;

public final class SerdeUtils {
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
    else {
      scheme = new JsonScheme(fieldNames);
    }
    return scheme;
  }
  public static IOutputSerializer getSerializer(String outputFormatClass, Properties properties, List<String> fieldNames) {
    IOutputSerializer serializer;
    if (isNotEmpty(outputFormatClass)) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\storm\revisions\rev_f14e0b2_f357d84\rev_left_f14e0b2\external\sql\storm-sql-runtime\src\jvm\org\apache\storm\sql\runtime\utils\SerdeUtils.java
switch (outputFormatClass){
        case "org.apache.storm.sql.runtime.serde.json.JsonSerializer":
        serializer = new JsonSerializer(fieldNames);
        break ;
        case "org.apache.storm.sql.runtime.serde.json.TsvSerializer":
        String delimiter = properties.getProperty("tsv.delimiter", "\t");
        serializer = new TsvSerializer(fieldNames, delimiter.charAt(0));
        break ;
        case "org.apache.storm.sql.runtime.serde.json.CsvSerializer":
        serializer = new CsvSerializer(fieldNames);
        break ;
        case "org.apache.storm.sql.runtime.serde.avro.AvroSerializer":
        String schemaString = properties.getProperty("avro.schema");
        Preconditions.checkArgument(isNotEmpty(schemaString), "avro.schema can not be empty");
        serializer = new AvroSerializer(schemaString, fieldNames);
        break ;
        default:
        serializer = Utils.newInstance(outputFormatClass);
      }
=======
if (JsonSerializer.class.getName().equals(outputFormatClass)) {
        serializer = new JsonSerializer(fieldNames);
      }
      else 
        if (AvroSerializer.class.getName().equals(outputFormatClass)) {
          String schemaString = properties.getProperty("output.avro.schema");
          Preconditions.checkArgument(isNotEmpty(schemaString), "output.avro.schema can not be empty");
          serializer = new AvroSerializer(schemaString, fieldNames);
        }
        else {
          serializer = Utils.newInstance(outputFormatClass);
        }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\storm\revisions\rev_f14e0b2_f357d84\rev_right_f357d84\external\sql\storm-sql-runtime\src\jvm\org\apache\storm\sql\runtime\utils\SerdeUtils.java

    }
    else {
      serializer = new JsonSerializer(fieldNames);
    }
    return serializer;
  }
  public static Object convertAvroUtf8(Object value) {
    Object ret;
    if (value instanceof Utf8) {
      ret = value.toString();
    }
    else 
      if (value instanceof Map<?, ?>) {
        ret = convertAvroUtf8Map((Map<Object, Object>)value);
      }
      else 
        if (value instanceof GenericData.Array) {
          ret = convertAvroUtf8Array((GenericData.Array)value);
        }
        else {
          ret = value;
        }
    return ret;
  }
  public static Object convertAvroUtf8Map(Map<Object, Object> value) {
    Map<Object, Object> map = new HashMap<>(value.size());
    for (Map.Entry<Object, Object> entry : value.entrySet()) {
      Object k = convertAvroUtf8(entry.getKey());
      Object v = convertAvroUtf8(entry.getValue());
      map.put(k, v);
    }
    return map;
  }
  public static Object convertAvroUtf8Array(GenericData.Array value) {
    List<Object> ls = new ArrayList<>(value.size());
    for (Object o : value) {
      ls.add(convertAvroUtf8(o));
    }
    return ls;
  }
}

