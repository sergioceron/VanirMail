package net.underserver.mail.service;

import java.util.HashMap;
import java.util.Map;

/**
 * User: sergio
 * Date: 27/07/12
 * Time: 11:40 PM
 */
public class ServiceManager {
	private static final ServiceManager instance = new ServiceManager();

	private Map<String, Service> services;
	
	private ServiceManager(){
		this.services = new HashMap<String, Service>();
	}

	public static ServiceManager getInstance(){
		return instance;
	}

	public void startAll(){
		for( String serviceName : services.keySet() ){
			Service service = services.get(serviceName);
			service.start(); // TODO: add fail start service exception
		}
	}
	
	public void stopAll(){
		for( Service service : services.values() ){
			service.stop();
		}
	}

	public Map<String, Service> getServices() {
		return services;
	}
	
	public Service getService(String name){
		return services.get(name);
	}
	
	public void addService(Service service){
		this.services.put(service.getName(), service);
	}

	public void setServices(Map<String, Service> services) {
		this.services = services;
	}
}
