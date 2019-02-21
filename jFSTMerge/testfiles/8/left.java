public class A {

	private static void loadUserProperties() throws IOException{
		File propertyFile = new File("path");
		Properties rawProperties = new Properties();
		if(propertyFile.isFile()){
			try(InputStream propertiesStream = new FileInputStream("path")){
				rawProperties.load(propertiesStream);
			}		
		}
		Set<Map,Entry<Object,Object>> propertyEntries = rawProperties.entrySet();
		for(Map.Entry<Object,Object> property : propertyEntries){
			String key = (String) property.getKey();
			String value = (String) property.getValue();
			value = substitutePropertyReferences(value);
			setProperty(key,value);
		}
	}

}