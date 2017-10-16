package com.gardner;

import org.apache.log4j.Logger;

import com.gardner.service.DataProviderService;

public class Main {

    private final static Logger log = Logger.getLogger(Main.class);

    /**
     * 
     * Purpose of this is to test the concept of a gateway for microservices with graceful degradation
     * 
     * @param args
     */
    public static void main(String[] args) {
        log.info("Starting main process");

        //Wrapper and controller - change time out between 1-3 seconds to alternate the result :)
        Timeout<String> timeout = new Timeout<>(3);
        
        // Service that provides data
        final DataProviderService dataProviderService = new DataProviderService();
        //Execute the code
        log.info(timeout.execute(dataProviderService));

        log.info("Finishing main process");
    }

}
