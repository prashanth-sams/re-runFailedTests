package abilash;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
//import org.junit.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({abilash.RetryTestListener.class})
public class AppTest{

	WebDriver driver;
	private String baseUrl;
	  
@BeforeTest
public void setUp()
	{
		driver = new FirefoxDriver();
		baseUrl = "https://www.google.co.in";
	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}


@Test(retryAnalyzer=RetryAnalyzer.class)
public void test01() throws Exception
	{
	
	driver.get(baseUrl + "/");	
	String save = driver.findElement(By.id("als")).getText();
	Assert.assertEquals(save, "qwerty");
	}

@AfterTest
	  public void tearDown() throws Exception {		
	    driver.quit();
		}
}