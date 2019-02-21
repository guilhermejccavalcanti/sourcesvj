public class A {
	
	public static <T> Collection<T> getServices(Class<T> serviceType){
		return getServiceProvider().getServices(serviceType);
	}

	public static <T> List<T> getServices(Class <T> serviceType, List<T> defaultServices){
		return getServiceProvider().getServices(serviceType, defaultServices);
	}

	public static <T> T getServices(Class <T> serviceType){
		List<T> services = getServiceProvider().getServices(serviceType);
		if(services.isEmpty()){
			throw new MonetaryException("No such service found: " + serviceType); 
		}
		return services.get(0);
	}
	
	public static <T> T getServices(Class <T> serviceType, T defaultService){
		List<T> services = getServiceProvider().getServices(serviceType);
		if(services.isEmpty()){
			return defaultService; 
		}
		return services.get(0);
	}
}