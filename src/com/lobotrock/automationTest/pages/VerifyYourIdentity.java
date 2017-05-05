package com.lobotrock.automationTest.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.lobotrock.email.EmailUtil;

public class VerifyYourIdentity extends Page{

	private static Logger logger = Logger.getLogger(VerifyYourIdentity.class);
	
	//Assuming the emc ID is static
	private By verificationCodeInput = By.xpath("//input[@id='emc']");
	//Assuming the editPage ID is static
	private By verifyButton = By.xpath("//input[@id='save']");
	//Assuming the emc-error ID is static
	private By emcError = By.xpath("//div[@id='emc-error']");
	
	public VerifyYourIdentity(WebDriver driver) {
		super(driver);
	}
	
	public void verifyIdentity(String gmail, String password){
		logger.debug("Loading VerifyYourIdentity page.");
		populateAndSubmit(gmail, password);		
		
		//checking to see if error message is displayed
		if(driver.findElements(emcError).size() > 0){
			logger.warn("Wrong verification code found, attempting to find verification code one more time.");
			populateAndSubmit(gmail, password);
		}
		
		waitForUrlChange();
	}
	
	private void populateAndSubmit(String gmail, String password){
		//Waiting for elements to show on page
		WebDriverWait wait = new WebDriverWait(driver, 20);
		WebElement verificationCodeElement = wait.until(ExpectedConditions.presenceOfElementLocated(verificationCodeInput));
		WebElement verifyButtonElement = wait.until(ExpectedConditions.presenceOfElementLocated(verifyButton));
		
		//Finding verification code from email
		String verificationCode = EmailUtil.getVerificationCode(gmail, password);
		
		//WebElement verificationCodeElement = driver.findElement(verificationCodeInput);
		verificationCodeElement.sendKeys(verificationCode);
		verifyButtonElement.click();
	}
	
}
