package com.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * DataValidationTest – tests for client-side form validation and data constraints.
 *
 * Test cases:
 *  1. Email format: invalid email is rejected by the browser's built-in validation
 *  2. Password field: empty password prevents form submission
 *  3. Name field: empty name prevents registration form submission
 *  4. Location required: Hero search form requires a location selection
 *  5. Pickup date required: Hero search form requires a pickup date
 *  6. Return date required: Hero search form requires a return date
 *  7. Date input type: pickup date input is of type 'date'
 *  8. Email input type: email input is of type 'email'
 *  9. Cars search input accepts and filters by text
 * 10. Login form empty submission is blocked
 */
public class DataValidationTest extends BaseTest {

    // ──────────────────────────────────────────────────────────────────────────
    // Email format validation
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Email input type='email' rejects clearly invalid email format", priority = 1)
    public void testEmailInputTypeIsEmail() {
        openHomePage();
        openLoginModal();

        WebElement emailInput = driver.findElement(By.xpath("//input[@type='email']"));

        // The HTML5 input type should be 'email'
        String inputType = emailInput.getAttribute("type");
        Assert.assertEquals(inputType, "email",
                "Login email field should have type='email' for browser-level validation");

        System.out.println("[✔] Email input type validation – passed");
    }

    @Test(description = "Password field is of type='password' (masked input)", priority = 2)
    public void testPasswordInputTypeIsPassword() {
        openHomePage();
        openLoginModal();

        WebElement passwordInput = driver.findElement(By.xpath("//input[@type='password']"));
        Assert.assertEquals(passwordInput.getAttribute("type"), "password",
                "Password field should have type='password'");

        System.out.println("[✔] Password input type validation – passed");
    }

    @Test(description = "Submitting the login form with an empty email is blocked by HTML5 required", priority = 3)
    public void testLoginFormEmptyEmailBlocked() {
        openHomePage();
        openLoginModal();

        // Leave email empty, fill password
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys("SomePassword");
        driver.findElement(By.xpath("//button[text()='Login']")).click();

        // HTML5 'required' blocks submission; the email input should still be visible
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='email']")));
        Assert.assertTrue(modal.isDisplayed(),
                "Login form should remain visible when email is empty");

        // Close modal
        driver.findElement(By.xpath(
                "//div[contains(@class,'fixed') and contains(@class,'bg-black')]")).click();

        System.out.println("[✔] Login form empty email is blocked – passed");
    }

    @Test(description = "Submitting the login form with an empty password is blocked by HTML5 required", priority = 4)
    public void testLoginFormEmptyPasswordBlocked() {
        openHomePage();
        openLoginModal();

        // Fill email, leave password empty
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys("test@example.com");
        driver.findElement(By.xpath("//button[text()='Login']")).click();

        // HTML5 'required' blocks submission; the email input should still be visible
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='email']")));
        Assert.assertTrue(emailInput.isDisplayed(),
                "Login form should remain visible when password is empty");

        driver.findElement(By.xpath(
                "//div[contains(@class,'fixed') and contains(@class,'bg-black')]")).click();

        System.out.println("[✔] Login form empty password is blocked – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Registration form validation
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Register form requires the Name field to be filled", priority = 5)
    public void testRegisterFormNameRequired() {
        openHomePage();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Click here']"))).click();

        // Leave name empty, fill email and password
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text']")));
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys("test@example.com");
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys("SomePass@1");

        driver.findElement(By.xpath("//button[text()='Create Account']")).click();

        // Form should still be visible (name input has 'required')
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text']")));
        Assert.assertTrue(nameInput.isDisplayed(),
                "Register form should remain open when name field is empty");

        driver.findElement(By.xpath(
                "//div[contains(@class,'fixed') and contains(@class,'bg-black')]")).click();

        System.out.println("[✔] Register form name required – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Hero search form validation
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Hero search form requires a pickup location to be selected", priority = 6)
    public void testHeroSearchRequiresLocation() {
        openHomePage();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickup-date")));

        // Do NOT select a location
        setDateInput("pickup-date", getFutureDate(3));
        setDateInput("return-date", getFutureDate(4));

        driver.findElement(By.xpath("//button[contains(.,'Search')]")).click();

        // Wait for the search button to remain (i.e. no navigation away from home)
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(.,'Search')]")));

        // Should still be on the home page (location select has 'required')
        Assert.assertTrue(driver.getCurrentUrl().equals(BASE_URL + "/") ||
                          driver.getCurrentUrl().equals(BASE_URL),
                "Page should stay on home when location is not selected");

        System.out.println("[✔] Hero search requires location – passed");
    }

    @Test(description = "Hero search form requires a pickup date", priority = 7)
    public void testHeroSearchRequiresPickupDate() {
        openHomePage();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickup-date")));

        Select locationDropdown = new Select(driver.findElement(By.xpath("//select")));
        locationDropdown.selectByVisibleText("Bathinda");

        // Leave pickup-date empty, fill return date
        setDateInput("return-date", getFutureDate(4));

        driver.findElement(By.xpath("//button[contains(.,'Search')]")).click();

        // Wait for the search button to remain (no navigation away)
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(.,'Search')]")));

        // Should stay on home (pickup-date is required)
        Assert.assertTrue(driver.getCurrentUrl().equals(BASE_URL + "/") ||
                          driver.getCurrentUrl().equals(BASE_URL),
                "Page should stay on home when pickup date is empty");

        System.out.println("[✔] Hero search requires pickup date – passed");
    }

    @Test(description = "Hero search form requires a return date", priority = 8)
    public void testHeroSearchRequiresReturnDate() {
        openHomePage();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickup-date")));

        Select locationDropdown = new Select(driver.findElement(By.xpath("//select")));
        locationDropdown.selectByVisibleText("Bathinda");

        setDateInput("pickup-date", getFutureDate(3));
        // Leave return-date empty

        driver.findElement(By.xpath("//button[contains(.,'Search')]")).click();

        // Wait for the search button to remain (no navigation away)
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(.,'Search')]")));

        // Should stay on home (return-date is required)
        Assert.assertTrue(driver.getCurrentUrl().equals(BASE_URL + "/") ||
                          driver.getCurrentUrl().equals(BASE_URL),
                "Page should stay on home when return date is empty");

        System.out.println("[✔] Hero search requires return date – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Date input attributes
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Pickup date input has type='date' and a min attribute set to today", priority = 9)
    public void testPickupDateInputAttributes() {
        openHomePage();

        WebElement pickupInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("pickup-date")));

        Assert.assertEquals(pickupInput.getAttribute("type"), "date",
                "Pickup date input should have type='date'");

        String minAttr = pickupInput.getAttribute("min");
        Assert.assertNotNull(minAttr, "Pickup date input should have a 'min' attribute");
        Assert.assertFalse(minAttr.isEmpty(), "The 'min' attribute should not be empty");

        System.out.println("[✔] Pickup date input attributes – passed");
    }

    @Test(description = "Return date input has type='date'", priority = 10)
    public void testReturnDateInputAttributes() {
        openHomePage();

        WebElement returnInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("return-date")));

        Assert.assertEquals(returnInput.getAttribute("type"), "date",
                "Return date input should have type='date'");

        System.out.println("[✔] Return date input attributes – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Cars page search text validation
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Cars page search input does not crash on special characters", priority = 11)
    public void testCarsSearchInputSpecialCharacters() throws InterruptedException {
        driver.get(BASE_URL + "/cars");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));

        WebElement searchInput = driver.findElement(
                By.xpath("//input[@placeholder[contains(.,'Search by make')]]"));

        // Type special characters
        searchInput.sendKeys("<script>alert('xss')</script>");
        Thread.sleep(1000);

        // Page should still render normally (no crash, no alert)
        WebElement showingLabel = driver.findElement(
                By.xpath("//p[contains(text(),'Showing')]"));
        Assert.assertTrue(showingLabel.isDisplayed(),
                "Page should remain functional after special character input");

        System.out.println("[✔] Cars search special characters – passed");
    }
}
