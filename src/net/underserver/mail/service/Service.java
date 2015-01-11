package net.underserver.mail.service;

import org.apache.log4j.Logger;

/**
* User: sergio
		* Date: 25/07/12
		* Time: 01:31 PM
		*/
public abstract class Service implements Runnable {
	private static final Logger logger = Logger.getLogger("main");

	private Thread currentThread;
	private String name;

	public enum STATUS {
		STOPPED,
		PAUSED,
		RUNNING
	}

	private volatile STATUS status;
	
	public Service(String name){
		this.name = name;
	}

	public void start(){
		start(false);
	}

	public void start(boolean newInstance){
		status = STATUS.RUNNING;
		if( currentThread == null || newInstance){
			currentThread = new Thread(this);
			currentThread.start();
			logger.debug("Started service: " + name);
		}
	}

	public void stop(){
		status = STATUS.STOPPED;
		currentThread = null;
		logger.debug("Stopped service: " + name);
	}

	public void pause() {
		status = STATUS.PAUSED;
		logger.debug("Paused service: " + name);
	}

	public void resume(){
		status = STATUS.RUNNING;
		logger.debug("Resumed service: " + name);
	}

	public boolean isAlive() {
		return status != STATUS.STOPPED;
	}

	public boolean isPaused(){
		return status == STATUS.PAUSED;
	}

	public String getName() {
		return name;
	}
}
