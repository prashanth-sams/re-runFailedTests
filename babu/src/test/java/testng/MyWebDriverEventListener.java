package testng;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
 
/**
 * This class creates basic log entries for WebDriver actions.
 * 
 * @author Stefan Schurischuster
 */
public class MyWebDriverEventListener implements WebDriverEventListener {
    private static final Logger logger = Logger.getLogger(MyWebDriverEventListener.class);   
    private By lastFindBy;
    private WebElement lastElement;
    private String originalValue;
 
    @Override
    public void beforeNavigateTo(String url, WebDriver driver){
        logger.debug("Navigating to:'" +url+ "'");
    } 

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver){
        lastElement = element;
        originalValue = element.getText();       
        
        // What if the element is not visible anymore?
        if(originalValue.isEmpty())  {            
            originalValue = element.getAttribute("value");
        }
    }
 
    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver) {
        String changedValue = "";
        try {
            changedValue = element.getText();
        } catch (StaleElementReferenceException e) {
            logger.error("Could not log change of element, because of a stale"
                    + " element reference exception.");
            return;
        }
        // What if the element is not visible anymore?
        if (changedValue.isEmpty()) {
            changedValue = element.getAttribute("value");
        }
        logger.debug("Changing value in element found " + lastFindBy
                + " from '" + originalValue + "' to '" + changedValue + "'");
    }
 
    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver driver){
        lastFindBy = by;
        logger.debug("Trying to find: '" +lastFindBy+ "'.");
    }
    
    @Override
    public void afterFindBy(By by, WebElement element, WebDriver driver){
        logger.debug("Found: '" +lastFindBy+ "'.");
    }
    
    @Override
    public void onException(Throwable error, WebDriver driver){}
    
    @Override
    public void beforeClickOn(WebElement element, WebDriver driver){
        logger.debug("Trying to click: '" +element+ "'");
    }
    
    @Override
    public void afterClickOn(WebElement element, WebDriver driver){
        logger.debug("Clicked: '" +element+ "'");
    }
 
    @Override
    public void beforeScript(String script, WebDriver driver){
        //logger.debug("Try execute script: "+script);
    }
    
    @Override
    public void afterScript(String script, WebDriver driver){
        //logger.debug("Finished executing script: "+script);
    }
    
    @Override
    public void beforeNavigateBack(WebDriver driver){}
    @Override
    public void beforeNavigateForward(WebDriver driver){}
    @Override
    public void afterNavigateBack(WebDriver driver){}
    @Override
    public void afterNavigateForward(WebDriver driver){}
    @Override
    public void afterNavigateTo(String url, WebDriver driver){}
    
}