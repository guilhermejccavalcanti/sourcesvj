public class A {
	
	public static <T> Collection<T> getServices(Class<T> serviceType){
		return getServiceProvider().getServices(serviceType);
	}


	public static <T> T getServices(Class <T> serviceType){
		List<T> services = getServiceProvider().getServices(serviceType);
		if(services.isEmpty()){
			throw new MonetaryException("No such service found: " + serviceType); 
		}
		return services.get(0);
	}
	
}