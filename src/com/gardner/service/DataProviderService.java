package com.gardner.service;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.gardner.annotation.backupAction;

public class DataProviderService implements Callable<String> {

	final static Logger log = Logger.getLogger(DataProviderService.class);
	
	public String call() throws Exception {
		String returnValue=null;
		log.info("DataProviderService - Started - plan A");
        try{
        	//Three Second
        	returnValue = "We did something normal";
            Thread.sleep(2000);
        }catch(InterruptedException e){
        	log.error("DataProviderService - InterruptedException - plan A", e);
        }
        return returnValue;

	}
	
	/**
	 * 
	 * Can be called something relevant as we use annotations to work out the plan B
	 * 
	 */
	@backupAction
	public String getAlernativeData() {
		log.info("DataProviderService - Started - plan B");
		//Do something clever
		log.info("DataProviderService - Finished - plan B");
		return "We did something else clever";	
	}

	
	
}
