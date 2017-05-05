package com.lobotrock.automationTest.pages;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class Home extends Page{

	private static Logger logger = Logger.getLogger(Home.class);

	public Home(WebDriver driver) {
		super(driver);
	}

	//Assuming the toolbar ID is static
	private By toolBar = By.xpath("//div[@id='toolbar']");
	private By tabsBarLinks = By.xpath("//ul[@id='tabBar']//li//a");
	
	//Subselections under the Toolbar are public so it can be referenced for the selectSubSalesToolbar
	//These remain constant over the changes on the homepage so they can be set to this enum
	public static enum ToolbarSelections{	
		SALES(By.xpath("//div[@id='tsid-menuItems']//a[text()='Sales']")), 
		SALESFORCE_CHATTER(By.xpath("//div[@id='tsid-menuItems']//a[text()='Salesforce Chatter']")), 
		MARKETING(By.xpath("//div[@id='tsid-menuItems']//a[text()='Marketing']")), 
		SERVICE(By.xpath("//div[@id='tsid-menuItems']//a[text()='Service']")), 
		COMMUNITY(By.xpath("//div[@id='tsid-menuItems']//a[text()='Community']")), 
		CHECKOUT(By.xpath("//div[@id='tsid-menuItems']//a[text()='Checkout']")), 
		APP_EXCHANGE(By.xpath("//div[@id='tsid-menuItems']//a[text()='AppExchange']")), 
		DEVELOPER_COMMUNITY(By.xpath("//div[@id='tsid-menuItems']//a[text()='Developer Community']")), 
		SUCESS_COMMUNITY(By.xpath("//div[@id='tsid-menuItems']//a[text()='Success Community']"));
		
		private final By xpath;
		
		public By getXpath(){
			return this.xpath;
		}
		
		private ToolbarSelections(By xpath){
			this.xpath = xpath;
		}
	}

	public String getToolbarText(){
		logger.debug("clickSalesToolbar called.");
		//Waiting for elements to show on page
		WebElement toolBarElement = wait.until(ExpectedConditions.presenceOfElementLocated(toolBar));
		return toolBarElement.getText();
	}
	
	public void clickToolbar(){
		logger.debug("clickSalesToolbar called.");
		//Waiting for elements to show on page
		WebElement toolBarElement = wait.until(ExpectedConditions.presenceOfElementLocated(toolBar));
		toolBarElement.click();
	}
	
	public void clickSubToolbar(ToolbarSelections selection){
		logger.debug("clickSubSalesToolbar called.");

		WebElement subSelectionElement = wait.until(ExpectedConditions.presenceOfElementLocated(selection.getXpath()));
		Assert.assertTrue(subSelectionElement.isDisplayed());
		
		subSelectionElement.click();
	}
	
	public void clickTabByName(String name){
		logger.debug("clickTabByName called.");
		WebElement tab = null;
		for(WebElement element : driver.findElements(tabsBarLinks)){
			if(name.equals(element.getText())){
				tab = element;
			}
		}
		
		tab.click();
	}
}
