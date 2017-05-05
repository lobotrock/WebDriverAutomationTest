package com.lobotrock.automationTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.lobotrock.automationTest.pages.Home;
import com.lobotrock.automationTest.pages.Leads;
import com.lobotrock.automationTest.pages.Login;
import com.lobotrock.automationTest.pages.VerifyYourIdentity;
import com.lobotrock.logging.LoggingUtil;

public class TestCase1 {

	static {
		DOMConfigurator.configureAndWatch("log4j.xml");
	}
	
	private static Logger logger = Logger.getLogger(TestCase1.class);
	public static Properties prop;

	public static void main(String[] args){
		String propertyFile = "";
		if(args.length == 1){
			propertyFile = args[0];
		}
		else{
			System.out.println("Property file required!");
			System.exit(1);
		}
		
		prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(propertyFile);

			// load a properties file
			prop.load(input);

		} catch (IOException ex) {
			LoggingUtil.writeErrorStack(logger, ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					LoggingUtil.writeErrorStack(logger, e);
				}
			}
		}
		
		testCaseClassic();
	}
	

	private static void testCaseClassic(){
		//Setting path to gecko driver, ideally I'd like to make this determined by a property file
		System.setProperty("webdriver.gecko.driver", "webDrivers/geckodriver");
		WebDriver driver = new FirefoxDriver();
		
		//Establishing the entry point for the test
		driver.get(prop.getProperty("ENTRY_URL"));
		
		//Logging in
		Login login = new Login(driver);
		login.setUsername(prop.getProperty("SALESFORCE_USERNAME"));
		login.setPassword(prop.getProperty("SALESFORCE_PASSWORD"));
		login.doLogin();
		//Exit if login information is not correct
		Assert.assertNull(login.getError());

		
		//If the home page doesn't load, load the verifyYourIdentity Page
		//Assuming verification url always contains EmailVerificationFinishUi
		if(driver.getCurrentUrl().contains("EmailVerificationFinishUi")){
			VerifyYourIdentity verifyYourIdentity = new VerifyYourIdentity(driver);
			//This email check only works for Gmail accounts with POP3 enabled
			verifyYourIdentity.verifyIdentity(prop.getProperty("GMAIL_USERNAME"), prop.getProperty("GMAIL_PASSWORD"));
		}
		//Loading home page
		Home home = new Home(driver);
		//Make sure Sales is selected
		if(!home.getToolbarText().equals("Sales")){
			//If the sales is not selected, select it
			home.clickToolbar();
			home.clickSubToolbar(Home.ToolbarSelections.SALES);
		}
		
		//Go to the Leads tab, which should be visible now that sales is selected
		home.clickTabByName("Leads");
		
		//Loading Leads page
		Leads leads = new Leads(driver);
		//Going to All Open Leads
		leads.selectLeadsFilter("All Open Leads");
		
		//Checking a few boxes
		leads.clickCheckboxByTableSearch("Georgia");
		leads.clickCheckboxByTableSearch("New York");
		//Following a row
		leads.clickFollowByTableSearch("BigLife");
		//Deleting a row, but canceling the confirmation
		leads.clickDeleteByTableSearch("California", false);
		
		driver.quit();
	}
}
