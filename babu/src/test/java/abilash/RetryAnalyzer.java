
package abilash;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;


public class RetryAnalyzer implements IRetryAnalyzer  { 
    private static final Logger logger = Logger.getLogger(RetryAnalyzer.class);
    int retryCount = 0;
    int retryMaxCount = 6;
    
    static {
    PropertyConfigurator.configure("C:\\Users\\prashanth_sams\\workspacekepler\\babu\\log4j.properties");
    }
    
    @Override
    public boolean retry(ITestResult tr) {
    	
        if (tr.getAttributeNames().contains("retry") == false) {
        	if(retryCount < retryMaxCount){
                logger.info("Retrying " + tr.getName() + " with status "
                        + tr.getStatus() + " for the " + (retryCount+1) + " of "
                        + retryMaxCount + " times.");
                
                retryCount++;
                
                return true;
        	}        	
    	}
        logger.debug("Skipping retry!");
        return false;
    }
}