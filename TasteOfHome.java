import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;


public class TasteOfHome
{
	private WebDriver browser;
	private String homePage;
	private String expectedTitle;
	
	//Used to quit browser for a specific test
	private boolean mainTest;
	
	public TasteOfHome()
	{
		super();
		
		System.setProperty("webdriver.chrome.driver", "/Users/andre/Documents/WebDrivers/chromedriver.exe");
		browser = new ChromeDriver();
		homePage = "http://www.tasteofhome.com";
		expectedTitle = "Taste of Home: Find Recipes, Appetizers, Desserts, Holiday Recipes & Healthy Cooking Tips";
		
		browser.get(homePage);
		
		//browser.manage().window().maximize();
		browser.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		
		mainTest = false;
	}
	
	/**
	 * Runs various tests to test page functionality.
	 * @param numTests the number of times to run the tests.
	 */
	public void testPage(int numTests)
	{	
		mainTest = true;
		
		if(numTests < 1) 
			numTests = 1;
		
		System.out.println("Starting " + numTests + " tests.");
		
		for(int i = 0; i < numTests; i++)
		{
			testLinks();
			testTitle();
			testLoginForm();
		}
		
		browser.quit();
		System.out.println("Completed");
	}
	
	@Test
	/**
	 * Tests whether the title of the page matches the expected title.
	 */
	public void testTitle()
	{	
		System.out.println("Testing page title.");
		String actualTitle = browser.getTitle();
				
		//Check if titles match
		for(int i = 0; i < actualTitle.length(); i++)
		{
			if(expectedTitle.charAt(i) != actualTitle.charAt(i))
			{
				System.err.println("Title different than expected at character position: " + i + ".");
				break;
			}
		}
		
		System.out.println("Page title testing completed.");
		//quitIfNeeded();
		
		//Does not work for some reason
		/*if(actualTitle.equals(expectedTitle))
			System.err.println("Titles do not match");
		*/
	}
	
	@Test
	/**
	 * Tests login form and logs in a user.
	 * Assumes username and password are correct.
	 */
	public void testLoginForm()
	{
		System.out.println("Testing login form.");

		WebElement loginButton = browser.findElement(By.id("menu-item-677178"));
		loginButton.click();
		
		login();
		
		System.out.println("Login form testing completed.");
		//quitIfNeeded();
	}
	
	
	@Test
	/**
	 * INCOMPLETE
	 * Tests the submit recipe function. Clicks submit recipe button, logs in if needed, and fills out the form.
	 */
	public void testSubmitRecipe()
	{
		System.out.println("Testing submit recipe.");
		WebElement submitRecipeButton = browser.findElement(By.id("menu-item-677176"));
		submitRecipeButton.click();
		
		//If we are not logged in, log in
		if(browser.getTitle().toLowerCase().contains("login"))
			login();
		
		
		WebElement recipeTitleInput = browser.findElement(By.id("RecipeTitle"));
		recipeTitleInput.sendKeys("Delicious Cheesecake recipe");
		
		WebElement recipeDescriptionInput = browser.findElement(By.id("RecipeDescription"));
		recipeDescriptionInput.sendKeys("This recipe is has been in my family for years. We make it every Thanksgiving.");
		
		WebElement ingredientsInput = browser.findElement(By.id("Ingredients"));
		ingredientsInput.sendKeys("1/2 cup of flower\n2 cups of milk\nCheese");
		
		WebElement directionsInput = browser.findElement(By.id("Directions"));
		directionsInput.sendKeys("Preheat oven to 300 degrees, mix ingredients in bowl for 5 minutes, pour into cake pan, place inside oven, remove from oven after 25 minutes");
		
		WebElement recipeNoteInput = browser.findElement(By.id("RecipeNote"));
		recipeNoteInput.sendKeys("Add peanut butter to ingredients to make peanut butter cheesecake!");
		
		Select quantityServedSelect = new Select(browser.findElement(By.id("QuantityServed")));
		quantityServedSelect.selectByValue("5");
		
		Select prepTimeSelect = new Select(browser.findElement(By.id("PreparationTimeMinutes")));
		prepTimeSelect.selectByValue("35");

		Select cookTimeSelect = new Select(browser.findElement(By.id("CookTimeMinutes")));
		cookTimeSelect.selectByValue("30");
		
		Select courseSelect = new Select(browser.findElement(By.id("SelectedCourse")));
		courseSelect.selectByValue("6");
		
		
		List<WebElement> checkBoxes = browser.findElements(By.className("rd_recipe_description_chkbox"));
		
		for(WebElement checkBox : checkBoxes)
			checkBox.click();

		WebElement agreementCheckBox = browser.findElement(By.cssSelector("input[name=agreement]"));
		agreementCheckBox.click();

		
		//Submit recipe..
		
		System.out.println("Submit recipe test completed.");
		//quitIfNeeded();
	}
	
	@Test
	/**
	 * Finds all <a> tags on the page that link to other taste of home pages, 
	 * and verifies whether they can be connected to or not.
	 */
	public void testLinks()
	{
		System.out.println("Testing all links.");
		
		//Find all <a> tags
		List<WebElement> links = browser.findElements(By.tagName("a"));
		System.out.println("Found " + links.size() + " links.");
		
		for(WebElement link : links)
		{
			String url = link.getAttribute("href");//Extract href url
			
			if(url == null || url.isEmpty())
				System.err.println("URL for <a> tag: " + link.getText() + "is either empty or null.");
			
			else if(!url.startsWith(homePage)) 
				//URL is another domain, skip it
				continue;
			
			else if(!validConnection(url))
			{
				System.err.println("Cannot establish valid connection for <a> tag " + link.getText() + ".");
			}	
		}
		
		System.out.println("Link testing completed.");
		
	}
	
	/**
	 * Attempts to establish an HTTP connection to the passed URL
	 * @param url The url to connect to
	 * @return true if HTTP response is 200, false otherwise
	 */
	private boolean validConnection(String url)
	{	
		int responseCode = -1;
		try
		{
			//Create Connection
			HttpURLConnection connection = (HttpURLConnection)(new URL(url).openConnection());
			
			//Specify Timeout
			//connection.setConnectTimeout(5000);
			
			connection.connect();
			
			responseCode = connection.getResponseCode();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return responseCode == 200; //or responseCode != 404
	}
	
	/**
	 * Logs in a user.
	 */
	private void login()
	{
		//Check if browser is on login page
		if(!browser.getTitle().toLowerCase().contains("login")) return;
		
		
		String username = "";
		String password = "";
		
		WebElement usernameInput = browser.findElement(By.id("txtUser"));
		usernameInput.sendKeys(username);
		WebElement passwordInput = browser.findElement(By.id("txtPass"));
		passwordInput.sendKeys(password);
		
		WebElement submitFormButton = browser.findElement(By.id("btnLoginMobile"));
		submitFormButton.click();
	}
	
	/**
	 * Logs out a user.
	 */
	private void logout()
	{
		WebElement logoutButton = browser.findElement(By.id("menu-item-677265"));
		logoutButton.click();
	}
	
	/**
	 * Quits the browser if a specific test is running. If a main test is running (full page test)
	 * the browser will quit after all components of the main test are completed, and will not quit here.
	 */
	private void quitIfNeeded()
	{
		if(!mainTest)
			browser.quit();
	}
}
