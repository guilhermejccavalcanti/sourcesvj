public class A {

	@SuppressWarnings("Duplicates")
	private static void loadUserProperties() throws IOException{
		try{
			UserConfigurationService userConfigurationService = new UserConfigurationServiceImpl();
			userConfigurationService.loadUserProperties();
		} catch(Exception ex){
			System.out.println("error");
			ex.printStackTrace();
		}
	}

}