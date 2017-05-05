package com.lobotrock.automationTest.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class Leads extends Page{


	private static Logger logger = Logger.getLogger(Leads.class);
	
	//Assuming there is only one select in bFilterView class divs, and there is only one bFilterView div
	private By leadsFilterSelect = By.xpath("//div[@class='bFilterView']//select");
	private By leadsFilterSubmitInput = By.xpath("//div[@class='bFilterView']//input");

	private By leadsTableHeaders = By.xpath("//div[contains(@class,'hd-inner')]");

	//Xpath String appended for find specific buttons
	private String deleteLinkXpath = "//div//a//span[contains(text(), 'Del')]";
	private String editLinkXpath = "//div//a//span[contains(text(), 'Edit')]";
	private String followLinkXpath = "//div//a[@title='Follow this lead to receive updates in your feed.']";
	private String checkboxXpath = "//div//input[@class='checkbox']";
	
	
	private List<String> leadsHeaders;


	public Leads(WebDriver driver) {
		super(driver);
		leadsHeaders = new ArrayList<String>();
	}

	/**
	 * 
	 * @param selection
	 * @return returns true if selection is valid, false if not
	 */
	public void selectLeadsFilter(String selection){
		logger.debug("selectLeadsFilter called.");
		Select leadsFilterSelectElement = new Select(wait.until(ExpectedConditions.presenceOfElementLocated(leadsFilterSelect)));
		WebElement leadsFilterSubmitInputElement = wait.until(ExpectedConditions.presenceOfElementLocated(leadsFilterSubmitInput));
		leadsFilterSelectElement.selectByVisibleText(selection);
		//Submit the change if it hasn't already
		if(leadsFilterSubmitInputElement.isDisplayed()){
			leadsFilterSubmitInputElement.click();
		}
		waitForUrlChange();
	}

	public void buildHeaderData(){
		logger.debug("buildHeaderData called.");
		//Finding all the element headers
		List<WebElement> headerElements = driver.findElements(leadsTableHeaders);
		for(WebElement headerElement : headerElements){
			//Getting the class
			String headerClass = headerElement.getAttribute("class");
			//The last section of class corresponds to the data in the columns below
			String identifier = headerClass.substring(headerClass.lastIndexOf("-") + 1);
			logger.debug("Found header identifier " + identifier + " for the leads table");
			leadsHeaders.add(identifier);
		}
	}
	
	/**
	 * This method will return null if multiple results are found, and log a warning
	 * that the search needs to be more specific.
	 * @param query
	 * @return
	 */
	public WebElement findButtonByTableSearch(String query, String actionXPath){	
		logger.debug("findDeleteButtonByTableSearch called.");
		//Build the header list if it hasn't been done already
		if(leadsHeaders.size() == 0 ){
			buildHeaderData();
		}
		
		StringBuilder queryXpath =  new StringBuilder("//td//div["); // + headerIdentifier + "')][contains(text(), '" + query + "')]";
		
		for(String headerIdentifier : leadsHeaders){
			//The header Identifiers are used because only valid row data should be searched
			queryXpath.append("contains(@class, '" + headerIdentifier + "') or ");
		}
		//Removing the last " or "
		String queryXpathString = queryXpath.substring(0, queryXpath.length() - 3);
		//Searching for plain text
		String queryXpathStringPlainText = queryXpathString + ("][contains(text(), '" + query + "')]/ancestor::tr[1]//td" + actionXPath);
		//Searching for link text contents
		String queryXpathStringLinkText = queryXpathString + ("]//a//span[contains(text(), '" + query + "')]/ancestor::tr[1]//td" + actionXPath);
		List<WebElement> foundElements = driver.findElements(By.xpath(queryXpathStringPlainText + " | " + queryXpathStringLinkText));
		
		if(foundElements.size() == 0){
			logger.warn("No results found in leads table for:: " + query);
			return null;
		}
		else if(foundElements.size() > 1){
			logger.warn("Multiple results returned, make more specific:: " + query);
			return null;
		}
		
		//Getting Delete button
		return foundElements.get(0);
	}
	
	public void clickDeleteByTableSearch(String query, boolean confirmDelete){
		logger.debug("deleteByTableSearch called.");
		WebElement deleteButton = findButtonByTableSearch(query, deleteLinkXpath);
		if(deleteButton != null){
			deleteButton.click();
			wait.until(ExpectedConditions.alertIsPresent());
			if(confirmDelete){
				//WARNING: Untested code:
				driver.switchTo().alert().accept();
				//END Untestd code
			}
			else{
				driver.switchTo().alert().dismiss();
			}
		}
	}

	public void clickCheckboxByTableSearch(String query){
		logger.debug("clickCheckboxByTableSearch called.");
		WebElement checkBox = findButtonByTableSearch(query, checkboxXpath);
		if(checkBox != null){
			checkBox.click();
		}
	}
	
	public void clickEditByTableSearch(String query){
		logger.debug("clickEditByTableSearch called.");
		WebElement editButton = findButtonByTableSearch(query, editLinkXpath);
		if(editButton != null){
			editButton.click();
		}
	}
	
	public void clickFollowByTableSearch(String query){
		logger.debug("clickFollowByTableSearch called.");
		WebElement followButton = findButtonByTableSearch(query, followLinkXpath);
		if(followButton != null){
			followButton.click();
		}
	}

}
