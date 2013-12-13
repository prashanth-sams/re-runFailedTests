package testng;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Contains methods that ease up the use of Webdriver. 
 * 
 * @author Stefan Schurischuster
 */
public class BasicFunctions {
    
    public static int PATIENCE_MILLI_SECONDS = 900;
    public static int MAX_PATIENCE_SECONDS = 30;
    public static int MAX_PATIENCE_SECONDS_EXTENDED = 45;
    public static int MAX_PATIENCE_SECONDS_REDUCED = 5;
    public static int REFRESH_WAIT_SECONDS = 5;
    public static int MAX_ATTEMPTS = 5;
    private static final Logger logger = Logger.getLogger(BasicFunctions.class);
    private WebDriver driver;
    
    /**
     * 
     * @param driver 
     */
    public BasicFunctions(WebDriver driver) {
        this.driver = driver;
    }
    
    /**
     * Sets the local Thread to sleep.
     * 
     * @param sleeptime
     *          Sleeping time in milli seconds.
     */
    public void bePatient(int sleeptime)  {
        try  {
            //driverWait.wait(sleeptime);
            synchronized(driver)  {
                driver.wait(sleeptime);
            }
        } catch(InterruptedException e)  {
            Assert.fail("Could not interrupt Thread: "+e.getMessage());
        } 
    }
    
    /**
     * Sets the local Thread to sleep for a predefined period.
     */
    public void bePatient()  {
        bePatient(PATIENCE_MILLI_SECONDS);
    }
    
    /**
     * Waits for an iframe to appear, switches into it and checks whether 
     * a frame specific element is visible.
     * 
     * @param frameIdentifier
     *          The identifier referring to the i-frame element.
     * @param contentIdentifier
     *          The identifier referring to an element inside the frame.
     * 
     * @Notice Maybe add collection for multiple content identifiers.
     */
    public void checkIFrame(By frameIdentifier, By contentIdentifier)  {
        driver.switchTo().defaultContent();
        /*
        if(isElementVisible(contentIdentifier))  {
            logger.info("Already on correct frame. Skipping switch to different frame.");
            return;
        }
        */
        WebElement iframe = waitUntilElementIsVisible("Could not find iframe.",frameIdentifier);
        driver.switchTo().frame(iframe);
        logger.info("Switched to different frame");
        waitUntilElementIsVisible(
                "Iframe content was not correctly displayed.",
                contentIdentifier, frameIdentifier, BasicFunctions.MAX_PATIENCE_SECONDS);
    }

    public void checkAndChooseDefaultGraph()  {
        checkAndChooseDefaultGraph("http://localhost/Geo");
    }
    
    public void checkAndChooseDefaultGraph(String defaultGraph)  {
        driver.switchTo().defaultContent();
        logger.info("Switching to default content.");
        String dg = waitUntilElementIsVisibleFast("Could not find default graph info field.",
                By.cssSelector("div.v-label-currentgraphlabel")).getText().trim();
        
        if(defaultGraph.equals(dg))  {
            logger.info("Already on correct default graph: "+dg);
        } else  {
            logger.info("Not correct default graph: "+dg+". Setting it to: "+defaultGraph);
            TestCase.navigator.navigateTo(new String[]{"Configuration", "Demonstrator configuration"});
            handleSelector(By.cssSelector("div.v-filterselect"), defaultGraph, false);
            getVisibleElement(By.cssSelector("div.v-form div.v-button")).click();
        }
        
    }
    
    
    /**
     * @return 
     *       Returns the locator of a vaadin error message.
     */
    public By getErrorPopupLocator()  {
        return By.xpath("//div[@class='gwt-HTML']/../..[contains(@class,'error')]");
    }

    /**
     * Returns an existing WebElement from the webpage. Throws an assert.fail if
     * the element is not present.
     *
     * @param failureMessage The failure message to be displayed when an error
     * occurs.
     * @param locator A By object locator.
     * @return The existing WebElement.
     */
    public WebElement getExisitingElement(String failureMessage, By locator) {
        WebElement element = null;
        try {
            element = driver.findElement(locator);
        } catch (NoSuchElementException e) {
            Assert.fail(failureMessage + " : " + e.getMessage());
        }
        return element;
    }
    
    /**
     * For example when an upload has completed.
     * 
     * @return 
     *      Returns the locator of a vaadin info message.
     */
    public By getInfoPopupLocator()  {
        return By.xpath("//div[@class='gwt-HTML']/../..[@class='v-Notification']");
    }
    
    /**
     * Returns the value of a web element using javascript.
     *
     * @param element The Element to retrieve value from.
     * @return The value of a field as String.
     */
    public String getValueViaJavaScript(WebElement element) {
        return ((JavascriptExecutor) driver).
                executeScript("return arguments[0].value", element).toString();
    }    

    /**
     * Returns an existing and visible WebElement from the parent. Throws an
     * assert.fail if the element is not present or not visible.
     *
     * @param failureMessage The failure message to be displayed when an error
     * occurs.
     * @param parent The parent WebElement from which a relativ search is
     * started.
     * @param locator A By object locator.
     * @return The existing and visible child WebElement.
     */
    public WebElement getVisibleChildElement(String failureMessage, WebElement parent, By locator) {
        WebElement element = null;
        try {
            element = parent.findElement(locator);
        } catch (NoSuchElementException e) {
            Assert.fail(failureMessage + " : " + e.getMessage());
        }
        assertTrue("Element is not visible: '" + element + "' : " + failureMessage, element.isDisplayed());
        return element;
    }    
    
        /**
     * Returns a list of existing and visible WebElements of a parent
     * WebElement.
     *
     * @param failureMessage The failure message to be displayed.
     * @param parent A WebElement to start the relative search from.
     * @param locator The locator of the child elements.
     * @return List of visible child WebElements.
     */
    public List<WebElement> getVisibleChildElements(String failureMessage, WebElement parent, By locator) {
        List<WebElement> children = null;
        try {
            children = parent.findElements(locator);
        } catch (NoSuchElementException e) {
            Assert.fail(failureMessage + " : " + e.getMessage());
        }
        for (WebElement element : children) {
            // MAYBE THIS SHOULD NOT BE AN ASSERT...
            assertTrue("Element is not visible: '" + element + "' : "
                    + failureMessage, element.isDisplayed());
        }
        return children;
    }
    
        /**
     * Returns an existing and visible WebElement from the webpage. Throws an
     * assert.fail if the element is not present or not visible.
     *
     * @param locator A By object locator.
     * @return The existing and visible WebElement.
     */
    public WebElement getVisibleElement(By locator) {
        return getVisibleElement("", locator);
    }

    /**
     * Returns an existing and visible WebElement from the webpage. Throws an
     * assert.fail if the element is not present or not visible.
     *
     * @param failureMessage The failure message to be displayed when an error
     * occurs.
     * @param locator A By object locator.
     * @return The existing and visible WebElement.
     */
    public WebElement getVisibleElement(String failureMessage, By locator) {
        WebElement element = getExisitingElement(failureMessage, locator);
        assertTrue("Element is not visible: '" + element + "' : " + failureMessage, element.isDisplayed());
        return element;

    }
    
    /**
     * Returns a list of existing and visible WebElements from the web-page.
     * Throws an assert.fail if an element is not present. Returns only present
     * fields.
     *
     * @param locator A By object locator.
     * @return List of visible WebElements.
     */
    public List<WebElement> getVisibleElements(String failureMessage, By locator) {
        List<WebElement> elements = null;
        List<WebElement> visibleElements = new ArrayList<WebElement>();
        try {
            elements = driver.findElements(locator);
        } catch (NoSuchElementException e) {
            Assert.fail(failureMessage + " : " + e.getMessage());
        }
        for (WebElement element : elements) {
            logger.warn("Element is not visible although it should be!");
            if (element.isDisplayed()) {
                visibleElements.add(element);
            }
            /*assertTrue("Element is not visible: '" + element + "' : " 
             + failureMessage, element.isDisplayed());
             */
        }
        return visibleElements;
    }    

    /**
     * Handles an vaadin file upload and checks whether the describing text
     * contains the uploaded file after clicking the button.
     * 
     * @param locator
     *              A By object referencing to the "form"- tag.
     * @param pathToFile 
     *              Path of the file to upload.
     */
    public void handleFileUpload(By locator, String pathToFile)  {
        WebElement field = waitUntilElementIsVisible(locator);
        
        WebElement input = null;
        WebElement button = null;
        
        try { // This should search relativly to selector.
            input = field.findElement(By.className("gwt-FileUpload"));
            button = field.findElement(By.className("v-button"));
        } catch (NoSuchElementException e) {
            Assert.fail(e.getMessage());
        }
        
        assertTrue("File not found: " +pathToFile, isLocalFileAvailable(pathToFile));
        
        input.sendKeys(pathToFile);
        File file = new File(pathToFile);
        
        button.click();
        
        WebElement uploaded = waitUntilElementIsVisible(
                "File was not successfully uploaded.", By.xpath(
                "//div[@class='v-captiontext'][contains(.,'" +file.getName()+ "')]"));
    }
    
    /**
     * Handles vaadin filter selects, chooses and writes values.
     * @TODO Is there a way to get the value of the selector?
     * 
     * @param locator
     *          The locator of the div - element representing the filter selector.
     * @param value
     *          The value to be chosen or written into the selector.
     * @param typeValue 
     *          true types value. false chooses value from popup.
     */
    public void handleSelector(By locator, String value, boolean typeValue)  {
         WebElement selector = waitUntilElementIsVisible(locator);
         WebElement input = null;
         WebElement button = null;
         
         try { // This should search relativly to selector.
             input = selector.findElement(By.className("v-filterselect-input"));
             button = selector.findElement(By.className("v-filterselect-button"));
         } catch(NoSuchElementException e)  {
             Assert.fail(e.getMessage());
         }
         
         if(typeValue)  {
            input.sendKeys(value);
         } else  {
             button.click();
             WebElement popUpElement = waitUntilElementIsVisible(
                     "Slector Element not found.", 
                     By.xpath("//div[contains(@class,'popupContent')]//"
                     + "td[contains(@class,'gwt-MenuItem')]"
                     + "/span[text() = '" +value+ "']"));
             
             popUpElement.click();
             bePatient();
         }
    }
    
    /**
     * Tries to find a descendant element from a parent WebElement.
     *
     * @param parent The WebElement from which there is a relative search.
     * @param locator The locator of the child element. Be careful when using
     * xpath, you have to use a dot before the slashes (.//) to perform a
     * relative search.
     * @return
     */
    public boolean isChildElementVisible(WebElement parent, By locator) {
        WebElement element = null;
        try {
            element = parent.findElement(locator);
        } catch (NoSuchElementException e) {
            return false;
        }
        return element.isDisplayed();
    }
    
    /**
     * Tries to create a WebElement using the passed locator and checks whether
     * it exists on the web-page.
     *
     * @param locator The locator of the element.
     * @return If an exception is thrown returns false, true otherwise.
     */
    public boolean isElementPresent(By locator) {
        WebElement element = null;
        try {
            element = driver.findElement(locator);
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }
    

    /**
     * Tries to create a WebElement using the passed locator and checks whether
     * it is visible and displayed on the web-page.
     *
     * @param locator The locator of the element.
     * @return If an exception is thrown or element is hidden it returns false,
     * true otherwise.
     */
    public boolean isElementVisible(By locator) {
        WebElement element = null;
        try {
            element = driver.findElement(locator);
        } catch (NoSuchElementException e) {
            return false;
        }
        return element.isDisplayed();
    }

    /**
     * Tries to create a WebElement using the passed locator and checks whether
     * it is visible and displayed on the web-page. Standard waiting time is
     * MAX_PACIENCE_SECONDS_REDUCED
     *
     * @param locator The locator of the element.
     * @return If an exception is thrown or element is hidden it returns false,
     * true otherwise.
     */
    public boolean isElementVisibleAfterWait(By locator) {
        return isElementVisibleAfterWait(locator, MAX_PATIENCE_SECONDS_REDUCED);
    }

    
    /**
     * Tries to create a WebElement using the passed locator and checks whether
     * it is visible and displayed on the web-page.
     *
     * @param locator The locator of the element.
     * @param maxPatienceSeconds The maximum time to wait before throwing an
     * exception.
     * @return If an exception is thrown or element is hidden it returns false,
     * true otherwise.
     */
    public boolean isElementVisibleAfterWait(By locator, int maxPatienceSeconds) {
        WebElement element = null;
        WebDriverWait pageWait = new WebDriverWait(driver, maxPatienceSeconds);
        int attempts = 0;
        boolean rep = true;

        while (attempts < MAX_ATTEMPTS && rep == true) {
            attempts++;
            rep = false;
            try {
                element = pageWait.until(
                        ExpectedConditions.visibilityOfElementLocated(locator));
            } catch (NoSuchElementException e) {
                return false;
            } catch (StaleElementReferenceException se) {
                rep = true;
                logger.error("isElementVisible failed and will be repeated for the  "
                        + attempts + " time. " + se.getMessage());
            } catch (TimeoutException te) {
                return false;
            }
        }
        assertFalse("To many stale reference exceptions during waiting"
                + "for element to be visible.", attempts == MAX_ATTEMPTS);
        return true;
    }
    
    /**
     * Checks whether a file is available under the given file path.
     */
    public boolean isLocalFileAvailable(String filepath) {
        try {
            if (new File(filepath).exists()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Reads a file from resources.
     * 
     * @param file
     *          The name of the file to read.
     * @return 
     *          An ArrayList containing the lines of the file.
     */
    public ArrayList<String> readFile(String file, boolean fromResource) {
        ArrayList<String> lines = new ArrayList<String>();
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {    
            if (fromResource) {
                isr = new InputStreamReader(getClass().getResourceAsStream(file));
            } else {
                isr = new InputStreamReader(new FileInputStream(file));
            }
            br = new BufferedReader(isr);
            String x;
            while ((x = br.readLine()) != null) {
                lines.add(x);
            }
        } catch (IOException e) {
            Assert.fail("An error occured trying to read a file: "+e.getMessage());
        } finally {
            try {
                if(isr != null)  {
                    isr.close();
                }
                if(br != null)  {
                    br.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException("Could not close Readers.");
            }
        }
        return lines;
    }
    
    /**
     * Scrolls to position.
     * 
     * @param x
     * @param y 
     */
    public void scrollTo(int x, int y)  {
        ((JavascriptExecutor) driver).executeScript("javascript:window.scrollBy(" +x+ "," +y+ ");");
    }
    
    /**
     * Scrolls into view.
     * 
     * @param element 
     *          The element to be scrolled to.
     */
    public void scrollIntoView(WebElement element)  {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
    
    /**
     * Sets the value of a field using javascript. This may not trigger the same
     * events as sendKeys does!
     *
     * @param element 
     *              The WebElement to be set.
     * @param value 
     *              The value that is used.
     */
    public void setValueViaJavaScript(WebElement element, String value) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1]", element, value);
    } 
    
      /**
     * Sets the current browser session to sleep until an element is present.
     * This element must be identified over its xpath.
     *
     * @param locator The identifying By object.
     * @return If the element was found before patience has ran out it is
     * returned.
     */
    public WebElement waitUntilElementIsVisible(By locator) {
        return waitUntilElementIsVisible("", locator);
    }

    /**
     * Sets the current browser session to sleep until an element is present.
     * This element must be identified over its xpath.
     *
     * @param failureMessage The failure message to be displayed when an error
     * appears.
     * @param locator The identifying By object.
     * @return If the element was found before patience has ran out it is
     * returned.
     */
    public WebElement waitUntilElementIsVisible(String failureMessage, By locator) {
        return waitUntilElementIsVisible(failureMessage, locator, MAX_PATIENCE_SECONDS);
    }

    /**
     * Sets the current browser session to sleep until an element is present.
     * This element must be identified over its xpath.
     *
     * @param failureMessage The failure message to be displayed when an error
     * appears.
     * @param locator The identifying By object.
     * @param maxPatienceSeconds Maximum time to wait before throwing an error
     * if the element is not visible.
     * @return If the element was found before patience has ran out it is
     * returned.
     */
    public WebElement waitUntilElementIsVisible(String failureMessage, By locator, int maxPatienceSeconds) {
        WebDriverWait pageWait = new WebDriverWait(driver, maxPatienceSeconds);
        if (!failureMessage.isEmpty()) {
            pageWait.withMessage("Time expired: " + failureMessage);
        }
        WebElement element = null;
        int attempts = 0;
        boolean rep = true;
        while (attempts < MAX_ATTEMPTS && rep == true) {
            attempts++;
            rep = false;
            try {
                element = pageWait.until(
                        ExpectedConditions.visibilityOfElementLocated(locator));
            } catch (NoSuchElementException e) {
                if (!failureMessage.isEmpty()) {
                    Assert.fail("Element not found: " + failureMessage
                            + " Stack trace: " + e.getMessage());
                } else {
                    Assert.fail(e.getMessage());
                }
            } catch (StaleElementReferenceException se) {
                rep = true;
                logger.error("Navigation failed and will be repeated for the "
                        + attempts + " time. " + se.getMessage());
            }
        }
        assertFalse("To many stale reference exceptions during waiting"
                + "for element to be visible.", attempts == MAX_ATTEMPTS);

        return element;
    }
    
    
    public WebElement waitUntilElementIsVisible(String failureMessage, By locator, By frameIdentifier, int maxPatienceSeconds)  {
        WebDriverWait pageWait = new WebDriverWait(driver, maxPatienceSeconds);
        if (!failureMessage.isEmpty()) {
            pageWait.withMessage("Time expired: " + failureMessage);
        }
        WebElement element = null;
        int attempts = 0;
        boolean rep = true;
        while (attempts < MAX_ATTEMPTS && rep == true) {
            attempts++;
            rep = false;
            try {
                element = pageWait.until(
                        ExpectedConditions.visibilityOfElementLocated(locator));
            } catch (TimeoutException e) {
                rep = true;
                driver.switchTo().defaultContent();
                WebElement iframe = waitUntilElementIsVisible("Could not find iframe.",frameIdentifier);
                driver.switchTo().frame(iframe);
                
                logger.error("Element not found, switching I frame: "+frameIdentifier+" and trying again for "
                        + attempts + " time.");
                
            } catch (StaleElementReferenceException se) {
                rep = true;
                logger.error("StaleElementReferenceException appeared. Retrying find for "
                        + attempts + " time. " + se.getMessage());
            }
        }
        assertFalse("To many stale reference exceptions during waiting"
                + "for element to be visible.", attempts == MAX_ATTEMPTS);
        
        return element;
    }
    
    public WebElement waitUntilElementIsVisible(String failureMessage, By locator, By frameIdentifier)  {
        return waitUntilElementIsVisible(failureMessage, locator, frameIdentifier, MAX_PATIENCE_SECONDS);
    }
    
    public WebElement waitUntilElementIsVisibleFast(String failureMessage, By locator, By frameIdentifier) {
        return waitUntilElementIsVisible(failureMessage, locator, frameIdentifier, MAX_PATIENCE_SECONDS_REDUCED);
    }
    
    
    /**
     * Sets the current browser session to sleep until an element is present.
     * This element must be identified over its xpath.
     *
     * @param failureMessage The failure message to be displayed when an error
     * appears.
     * @param locator The identifying By object.
     * @return If the element was found before patience has ran out it is
     * returned.
     */
    public WebElement waitUntilElementIsVisibleFast(String failureMessage, By locator) {
        return waitUntilElementIsVisible(failureMessage, locator, MAX_PATIENCE_SECONDS_REDUCED);
    }
    
    /**
     * Sets the current browser session to sleep until an element is present.
     * This element must be identified over its xpath.
     *
     * @param failureMessage The failure message to be displayed when an error
     * appears.
     * @param locator The identifying By object.
     * @return All elements of the locator
     */
    public List<WebElement> waitUntilElementsAreVisible(String failureMessage, By locator) {
        WebDriverWait pageWait = new WebDriverWait(driver, MAX_PATIENCE_SECONDS);
        if (!failureMessage.isEmpty()) {
            pageWait.withMessage("Time expired: " + failureMessage);
        }
        List<WebElement> elements = null;
        WebElement firstElement = null;
        int attempts = 0;
        boolean rep = true;

        while (attempts < MAX_ATTEMPTS && rep == true) {
            attempts++;
            rep = false;
            try {
                firstElement = pageWait.until(
                        ExpectedConditions.visibilityOfElementLocated(locator));

                // Get multiple WebElements
                elements = driver.findElements(locator);

            } catch (NoSuchElementException e) {
                if (!failureMessage.isEmpty()) {
                    Assert.fail("Element not found: " + failureMessage
                            + " Stack trace: " + e.getMessage());
                } else {
                    Assert.fail(e.getMessage());
                }
            } catch (StaleElementReferenceException se) {
                rep = true;
                logger.error("Navigation failed and will be repeated for the "
                        + attempts + " time. " + se.getMessage());
            }
        }
        assertFalse("To many stale reference exceptions during waiting"
                + "for elements to be visible.", attempts == MAX_ATTEMPTS);

        return elements;
    }

    /**
     * @param failureMessage
     * @param locator The locator of the element to disappear.
     * @return
     */
    public WebElement waitUntilElementIsPresent(String failureMessage, By locator) {
        WebDriverWait pageWait = new WebDriverWait(driver, MAX_PATIENCE_SECONDS);
        pageWait.withMessage("Time expired: " + failureMessage);

        WebElement element = null;
        try {
            element = pageWait.until(
                    ExpectedConditions.presenceOfElementLocated(locator));
        } catch (NoSuchElementException e) {
            Assert.fail("Element not found: " + failureMessage
                    + " Stack trace: " + e.getMessage());
        }
        return element;
    }

    /**
     * Waits until a certain element disappears.
     *
     * @param locator The locator of the element to disappear.
     * @param failureMessage The message to be displayed if an error occurs.
     * @param maxPatienceSeconds The max time to wait before throwing an error.
     */
    public void waitUntilElementDisappears(String failureMessage, By locator, int maxPatienceSeconds) {
        WebDriverWait pageWait = new WebDriverWait(driver, maxPatienceSeconds);
        try {
            pageWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (NoSuchElementException e) {
            Assert.fail(e.getMessage());
        } catch (Exception e) {
            Assert.fail(failureMessage);
        }
    }

    /**
     * Waits until a certain element disappears. Maximum patience is predefined
     * in BasicFunctions.class.
     *
     * @param locator The locator of the element to disappear.
     * @param failureMessage The message to be displayed if an error occurs.
     */
    public void waitUntilElementDisappears(String failureMessage, By locator) {
        waitUntilElementDisappears(failureMessage, locator, MAX_PATIENCE_SECONDS);
    }

    /**
     * Waits until a certain element disappears. Maximum patience is predefined
     * in BasicFunctions.class. No specific failure message will be displayed,
     * only the message of the exception.
     *
     * @param locator The locator of the element to disappear.
     */
    public void waitUntilElementDisappears(By locator) {
        waitUntilElementDisappears("", locator, MAX_PATIENCE_SECONDS);
    }

    
    public void hoverOverElement(WebElement element) {
        // hack for 'hover'
        Actions actions= new Actions(driver);
        actions.clickAndHold(element).perform();
        actions.moveByOffset(5, 5).perform();
    }
    
    /**
     * Returns an xpath module which represents a ends-with method.
     *
     * @param element The element to end-with a certain value. Typically it is
     * "." or "@id".
     * @param value The value to end with.
     * @return An xpath filter.
     */
    public String xpathEndsWith(String element, String value) {
        return "substring(normalize-space(" + element + "), string-length(normalize-space("
                + element + ")) - string-length('" + value + "') +1) = '" + value + "'";
    }
}