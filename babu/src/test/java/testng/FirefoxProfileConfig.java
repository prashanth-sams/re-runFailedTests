package testng;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * This class provides a FirefoxProfile which can be used to personalise
 * and edit the default Firefox profile.
 * 
 * @author Stefan Schurischuster
 */
public class FirefoxProfileConfig {
    private FirefoxProfile profile;
    private String downloadDir;
    private static final Logger logger = Logger.getLogger(FirefoxProfileConfig.class);    
    
    /**
     * Creates a new FireFoxProfile and set preferences.
     * 
     * @param downloadDir 
     *          The path of the Firefox download directory.
     */
    public FirefoxProfileConfig(String downloadDir) {
        //TODO change and keep profile over here
        //this.profile = new FirefoxProfile(new File("/home/karel/.mozilla/firefox/1xtgionb.auto-tester"));
        this.profile = new FirefoxProfile();
        this.downloadDir = downloadDir;
        setPreferences();
        // Sets native events to true: Can cause problems under Linux
        // @TODO: Monitor and keep in mind.
        boolean nativeEvents = true;
        profile.setEnableNativeEvents(nativeEvents);
        profile.setAcceptUntrustedCertificates(true);
        profile.setPreference("security.default_personal_cert", "Select Automatically");
        profile.setAssumeUntrustedCertificateIssuer(true);

        logger.info("Using native events: " +nativeEvents);
    }
    
    private void setPreferences()  {
        // Set download folder.
        logger.info("Download directory is: " + downloadDir);
        // Use the download folder to dowload files.
        profile.setPreference("browser.download.folderList", 2);
        // Set download folder
        profile.setPreference("browser.download.dir", downloadDir);
        profile.setPreference("browser.download.lastDir", downloadDir);
        // Set open new window preferences to allways open tabs when
        // triggered by javascript.
        profile.setPreference("browser.link.open_newwindow.restriction", 0);
        // Not allowed to override
        //profile.setPreference("browser.link.open_newwindow", 3);
        
        // Dont ask where at the following mime types.
        String mimeTypes = "application/rdf+xml,"
                        + "text/rdf+n3,"
                        + "text/turtle,"        
                        + "text/plain,"
                        + "application/zip,"
                        + "application/x-trig,"
                        + "application/trix,"
                        + "application/x-binary-rdf,"
                        + "application/xml";
       
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",mimeTypes);
        // Do not ask where to save.
        profile.setPreference("browser.download.useDownloadDir", true);
        // Don't show download window.
        profile.setPreference("browser.download.manager.showWhenStarting", false);
    }
    
    /**
     * Sets Firebug extension to be used when selenium starts a new 
     * browser session and configures it to avoid popping up a start-up
     * screen.
     * 
     * @param firebugPath
     *          Path to firebug xpi. File must have version inside its title
     *          with format: [TITLE]-[VERSION].xpi.
     * @throws IOException When file could not be found.
     */
    public void addFireBugExtension(String firebugPath) throws IOException {
        File extFile = new File(firebugPath);
        if(!extFile.canRead())  {
            throw new IOException("Could not find file: "+firebugPath);
        }
        String extTitle = extFile.getName();
        // Parse Version from file title 
        String extVersion = extTitle.substring(extTitle.lastIndexOf("-")+1, 
                extTitle.lastIndexOf("."));
        profile.addExtension(extFile);
        // Set current version to suppress startup screen.
        profile.setPreference("extensions.firebug.currentVersion", extVersion);
    }
    
    /**
     * Sets Firefox extension to be used when selenium starts a new 
     * browser session. 
     * 
     * @param extensionPath 
     *          The path to extension file. xpi-files are commonly used.
     */
    public void addExtension(String extensionPath) throws IOException  {
        File extFile = new File(extensionPath);
        String extTitle = extFile.getName();
        if(!extFile.canRead())  {
            throw new IOException("Could not find file: "+extensionPath);
        }
        profile.addExtension(extFile);
    }
    
    /**
     * @return 
     *      Returns a pre-configured Firefox profile to fit the needs of testing.
     */
    public FirefoxProfile getConfiguredProfile()  {
        return this.profile;
    }
}