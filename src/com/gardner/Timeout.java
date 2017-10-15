package com.gardner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.gardner.annotation.backupAction;

public class Timeout<T> {

	final static Logger log = Logger.getLogger(Timeout.class);

	final ExecutorService service;
	final int timeoutInSeconds;
	
	public Timeout(int timeoutInSeconds) {
		this.service = Executors.newFixedThreadPool(4);
		this.timeoutInSeconds = timeoutInSeconds;
	}
	
	/**
	 * Function that gives the Callable services the a time limit to complete
	 * If it does not complete in time it will call the backAction defined 
	 * determined by the annotation
	 * 
	 * @param callableService
	 * @return Type<T> the return value of the callableService
	 */
	public T execute(Callable<T> callableService) {
		
		Future<T> futureResult = service.submit(callableService);
		T result = null;
		try {
			result = futureResult.get(timeoutInSeconds, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			log.error("Times up", e);
			log.warn("No response after " + timeoutInSeconds  + " second");
			
			//TODO: consider sending info or a record of the broken service	
			//TODO: considering supporting more than one backup - making this somewhat recursive :/
			futureResult.cancel(true);
			for (Method method : callableService.getClass().getDeclaredMethods()) {
				if (method.isAnnotationPresent(backupAction.class)) {
					log.info("Found (with backupAction) : " + callableService.getClass());
					try {
						result = (T) method.invoke(callableService, null);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
						log.error("Unable to execute backupAction " + method.getName() ,e);
					}
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			log.error("Unable to execute happy path" ,e);
		}
		return result;
	}

	public void destroy() {
		service.shutdown();
	}
	
}
