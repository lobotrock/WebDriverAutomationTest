package com.lobotrock.automationTest.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition; 

public class Login  extends Page{

	private static Logger logger = Logger.getLogger(Login.class);

	//Assuming username ID is static
	private By usernameInput = By.xpath("//input[@id='username']");
	//Assuming password ID is static
	private By passwordInput = By.xpath("//input[@id='password']");
	//Assuming login_form ID is static
	private By loginForm = By.xpath("//form[@id='login_form']");
	//Assuming error ID is static
	private By errorDiv = By.xpath("//div[@id='error']");

	public Login(WebDriver driver) {
		super(driver);
	}


	public void setUsername(String username){
		logger.debug("setUsername called.");	
		WebElement usernameInputElement = driver.findElement(usernameInput);
		usernameInputElement.sendKeys(username);
	}

	public String getUsername(){
		logger.debug("getUsername called.");	
		WebElement usernameInputElement = driver.findElement(usernameInput);
		return usernameInputElement.getText();
	}

	public void setPassword(String password){
		logger.debug("setPassword called.");	
		WebElement passwordInputElement = driver.findElement(passwordInput);
		passwordInputElement.sendKeys(password);
	}

	public String getPassword(){
		logger.debug("setPassword called.");	
		WebElement passwordInputElement = driver.findElement(passwordInput);
		return passwordInputElement.getText();
	}

	/**
	 * Gets the error displayed on the login page.
	 * 
	 * @return message of error in String form, or null if no error found
	 */
	public String getError(){
		logger.debug("getError called.");

		String previousURL = driver.getCurrentUrl();

		//The goal is to wait until the URL changes due to a successful login
		// or read the error message if the login failed
		ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver){
				logger.trace("Current URL:: " + driver.getCurrentUrl());
				logger.trace("Previous URL:: " + previousURL);
				return !driver.getCurrentUrl().equals(previousURL) ||
						driver.findElement(errorDiv).isDisplayed() ;
			}
		};
		wait.until(expectedCondition);

		if(driver.findElements(errorDiv).size() > 0){
			WebElement errorDivElement = driver.findElement(errorDiv);
			return errorDivElement.getText();
		}
		else{
			return null;
		}
	}

	public void doLogin(){	
		logger.debug("doLogin called.");		
		WebElement loginFormElement = driver.findElement(loginForm);		
		loginFormElement.submit();
	}


}
