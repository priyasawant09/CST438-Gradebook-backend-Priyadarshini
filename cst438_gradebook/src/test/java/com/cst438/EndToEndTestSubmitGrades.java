package com.cst438;

import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * This example shows how to use selenium testing using the web driver 
 * with Chrome browser.
 * 
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *      
 *  In SpringBootTest environment, the test program may use Spring repositories to 
 *  setup the database for the test and to verify the result.
 */

@SpringBootTest
public class EndToEndTestSubmitGrades {

	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/priya/Downloads/chromedriver-mac-x64/chromedriver";

	public static final String URL = "http://localhost:3000";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "db design";
	public static final String NEW_GRADE = "99";
	public static final String UPDATED_ASSIGNMENT_NAME = "React Assignment";
	public static final String ADD_ASSIGNMENT_NAME = "Database Assignment";

	public static final String ADD_ASSIGNMENT_ID = "31045";

	public static final String ADD_ASSIGNMENT_DATE= "10/09/2023";





	@Test
	public void addCourseTest() throws Exception {


		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on

		/*
		 * initialize the WebDriver and get the home page.
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);

		WebElement w;


		try {

			List<WebElement> elements  = driver.findElements(By.xpath("//td"));
			boolean found = false;
			for (WebElement we : elements) {
				if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
					found=true;
					we.findElement(By.xpath("..//a")).click();
					break;
				}
			}
			assertThat(found).withFailMessage("The test assignment was not found.").isTrue();
			Thread.sleep(SLEEP_DURATION);

			ArrayList<String> originalGrades = new ArrayList<>();
			elements  = driver.findElements(By.xpath("//input"));
			for (WebElement element : elements) {
				originalGrades.add(element.getAttribute("value"));
				element.clear();
				element.sendKeys(NEW_GRADE);
				Thread.sleep(SLEEP_DURATION);
			}

			for (String s : originalGrades) {
				System.out.println("'"+s+"'");
			}
			driver.findElement(By.id("sgrade")).click();
			Thread.sleep(SLEEP_DURATION);

			w = driver.findElement(By.id("gmessage"));
			assertThat(w.getText()).withFailMessage("After saving grades, message should be \"Grades saved.\"").startsWith("Grades saved");

			driver.navigate().back();  // back button to last page
			Thread.sleep(SLEEP_DURATION);

			// find the assignment 'db design' again.
			elements  = driver.findElements(By.xpath("//td"));
			found = false;
			for (WebElement we : elements) {
				if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
					found=true;
					we.findElement(By.xpath("..//a")).click();
					break;
				}
			}
			Thread.sleep(SLEEP_DURATION);
			assertThat(found).withFailMessage("The test assignment was not found.").isTrue();

			// verify the grades. Change grades back to original values

			elements  = driver.findElements(By.xpath("//input"));
			for (int idx=0; idx < elements.size(); idx++) {
				WebElement element = elements.get(idx);
				assertThat(element.getAttribute("value")).withFailMessage("Incorrect grade value.").isEqualTo(NEW_GRADE);

				// clear the input value by backspacing over the value
				while(!element.getAttribute("value").equals("")){
					element.sendKeys(Keys.BACK_SPACE);
				}
				if (!originalGrades.get(idx).equals("")) element.sendKeys(originalGrades.get(idx));
				Thread.sleep(SLEEP_DURATION);
			}
			driver.findElement(By.id("sgrade")).click();
			Thread.sleep(SLEEP_DURATION);

			w = driver.findElement(By.id("gmessage"));
			assertThat(w.getText()).withFailMessage("After saving grades, message should be \"Grades saved.\"").startsWith("Grades saved");


		} catch (Exception ex) {
			throw ex;
		} finally {

			driver.quit();
		}

	}
	@Test
	public void addAssignmentTest() throws Exception {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);



		try {
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// Locate and click the "Add Assignment" button
			driver.findElement(By.id("add-button1")).click();
			Thread.sleep(SLEEP_DURATION);

			// Locate and click the "Add Assignment" button
			driver.findElement(By.id("add-button2")).click();
			Thread.sleep(SLEEP_DURATION);

			// Find the assignment name input field and enter a new assignment name
			WebElement assignmentNameElement = driver.findElement(By.id("assignment-name"));
			assignmentNameElement.sendKeys(ADD_ASSIGNMENT_NAME);
			Thread.sleep(SLEEP_DURATION);

			WebElement assignmentIdElement = driver.findElement(By.id("assignment-id"));
			assignmentIdElement.sendKeys(ADD_ASSIGNMENT_ID);

			WebElement assignmentDateElement = driver.findElement(By.id("assignment-date"));
			assignmentDateElement.sendKeys(ADD_ASSIGNMENT_DATE);


			// Locate and click the "Save" button to add the assignment
			driver.findElement(By.id("submit-button")).click();
			Thread.sleep(SLEEP_DURATION);


		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
			Thread.sleep(5000);
		}
	}

	@Test
	public void updateAssignmentTest() throws Exception {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// Locate the assignment to be updated (you may need to locate it based on a unique identifier)
			WebElement assignmentElement = driver.findElement(By.id("edit-button")); // Change this selector accordingly
			assignmentElement.click();
			Thread.sleep(SLEEP_DURATION);

			// Find the assignment name input field and update the assignment name
			WebElement assignmentNameElement = driver.findElement(By.id("assignment-name"));
			assignmentNameElement.clear();
			assignmentNameElement.sendKeys(UPDATED_ASSIGNMENT_NAME);
			Thread.sleep(SLEEP_DURATION);

			// Locate and click the "Save" button to update the assignment
			driver.findElement(By.id("save-assignment")).click();
			Thread.sleep(SLEEP_DURATION);


		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
			Thread.sleep(5000);
		}
	}

	@Test
	public void deleteAssignmentTest() throws Exception {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// Locate and click the "Delete" button
			driver.findElement(By.id("delete-button")).click();
			Thread.sleep(SLEEP_DURATION);

		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
			Thread.sleep(5000);
		}
	}
}










