
package com.aitest.tests;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.By;
import com.aitest.automation.AITestAutomation;

public class FacebookTest {
    private static final String FACEBOOK_URL = "https://www.facebook.com";
    private final AITestAutomation automation;
    private static final Logger logger = LoggerFactory.getLogger(FacebookTest.class);

    public FacebookTest(ChromeOptions options) {
        this.automation = new AITestAutomation(options);
    }

    public boolean loginToFacebook(String email, String password) {
        return automation.selfHealingTest(() -> {
            try {
                WebDriver driver = automation.getDriver();
                driver.get(FACEBOOK_URL);

                // Find and fill email field
                WebElement emailField = automation.smartFindElement("email");
                if (emailField == null) {
                    throw new RuntimeException("Email field not found");
                }
                emailField.clear();
                emailField.sendKeys(email);

                // Find and fill password field
                WebElement passwordField = automation.smartFindElement("pass");
                if (passwordField == null) {
                    throw new RuntimeException("Password field not found");
                }
                passwordField.clear();
                passwordField.sendKeys(password);

                // Find and click login button using updated locator
                // Try multiple strategies to find the login button
                WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
                if (loginButton == null) {
                    throw new RuntimeException("Login button not found");
                }
                loginButton.click();

                // Wait for successful login (check for a common element on the home page)
                Thread.sleep(3000); // Basic wait to allow page load
                
                // Verify successful login by checking URL or presence of home page element
                String currentUrl = driver.getCurrentUrl();
                if (currentUrl.contains("facebook.com/home") || currentUrl.contains("facebook.com/?sk=h_chr")) {
                    logger.info("Successfully logged into Facebook");
                    return true;
                } else {
                    logger.error("Login unsuccessful");
                    return false;
                }

            } catch (Exception e) {
                logger.error("Failed to login to Facebook", e);
                throw e;
            }
        });
    }

    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        FacebookTest test = new FacebookTest(options);
        try {
            // Replace with your actual Facebook credentials
            String email = "rrr";
            String password = "rrr";
            
            boolean success = test.loginToFacebook(email, password);
            if (success) {
                System.out.println("Facebook login successful");
            } else {
                System.out.println("Facebook login failed");
            }

        } finally {
            test.automation.cleanup();
        }
    }
}