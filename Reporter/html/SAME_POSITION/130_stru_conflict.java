<<<<<<< MINE
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
>>>>>>> YOURS

