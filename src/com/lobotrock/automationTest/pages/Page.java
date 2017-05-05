package com.lobotrock.automationTest.pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Page {

	protected WebDriver driver;
	WebDriverWait wait;

	public Page(WebDriver driver){
		this.driver = driver;
		//Wait used for elements to load
		wait = new WebDriverWait(driver, 20);
	}

	/**
	 * This method can be used after a submit to make sure the URL is changed
	 */
	public void waitForUrlChange(){
		String previousURL = driver.getCurrentUrl();
		WebDriverWait wait = new WebDriverWait(driver, 10);

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				return (!webDriver.getCurrentUrl().equals(previousURL));
			}
		};

		wait.until(expectedCondition);
	}
}
