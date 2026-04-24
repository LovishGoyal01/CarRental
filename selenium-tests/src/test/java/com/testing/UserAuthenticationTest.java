package com.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * UserAuthenticationTest – tests for the registration, login, and logout flows.
 *
 * Test cases:
 *  1. Successful registration with valid data
 *  2. Attempt to register with an already-used email
 *  3. Successful login with valid credentials
 *  4. Failed login with an incorrect password
 *  5. Logout functionality
 *  6. Login modal closes when clicking the backdrop
 *  7. Toggle between Login and Register views inside the modal
 */
public class UserAuthenticationTest extends BaseTest {

    // ──────────────────────────────────────────────────────────────────────────
    // Registration
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Register a new account with valid data", priority = 1)
    public void testRegistrationWithValidData() throws InterruptedException {
        openHomePage();

        String uniqueEmail = "newuser_" + System.currentTimeMillis() + "@test.com";

        // Open login modal → switch to register view
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();

        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Click here']"))).click();

        // Fill form
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text']"))).sendKeys("New Test User");
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys(uniqueEmail);
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys("NewPass@123");

        driver.findElement(By.xpath("//button[text()='Create Account']")).click();

        // Wait for the modal to close; Logout button appearing confirms success
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Logout']")));

        boolean logoutVisible = !driver.findElements(
                By.xpath("//button[text()='Logout']")).isEmpty();
        Assert.assertTrue(logoutVisible, "Logout button should be visible after successful registration");

        logout();
        System.out.println("[✔] Registration with valid data – passed");
    }

    @Test(description = "Attempt to register with an email that is already in use", priority = 2)
    public void testRegistrationWithDuplicateEmail() {
        openHomePage();

        // Open login modal → switch to register view
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();

        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Click here']"))).click();

        // Use an email we already know is registered (the shared test account)
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text']"))).sendKeys("Duplicate User");
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys(TEST_EMAIL);
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(TEST_PASSWORD);

        driver.findElement(By.xpath("//button[text()='Create Account']")).click();

        // Wait up to 5 s for either the modal to stay open (Create Account still visible)
        // or an error toast to appear — either confirms the duplicate was rejected
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.or(
                            org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                                    By.xpath("//button[text()='Create Account']")),
                            org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                                    By.xpath("//div[contains(@class,'toast')]"))));
        } catch (Exception ignored) {
            // Condition may already be satisfied; proceed to assertion
        }

        // Modal should still be open OR an error toast should be shown
        // Either way the Logout button should NOT appear
        boolean modalStillPresent = !driver.findElements(
                By.xpath("//button[text()='Create Account']")).isEmpty()
                || !driver.findElements(By.xpath("//div[contains(@class,'toast')]")).isEmpty();
        Assert.assertTrue(modalStillPresent,
                "Modal should still be open or error toast shown on duplicate-email registration");

        // Close modal by pressing Escape or clicking backdrop
        driver.findElements(By.xpath("//div[contains(@class,'fixed') and contains(@class,'bg-black')]"))
              .stream().findFirst().ifPresent(WebElement::click);

        System.out.println("[✔] Registration with duplicate email – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Login
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Login with valid credentials", priority = 3)
    public void testLoginWithValidCredentials() {
        openHomePage();

        // First ensure we have a registered account – create one if needed
        String email    = "validlogin_" + System.currentTimeMillis() + "@test.com";
        String password = "Login@123";

        // Register
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Click here']"))).click();
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text']"))).sendKeys("Login Test User");
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys(email);
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(password);
        driver.findElement(By.xpath("//button[text()='Create Account']")).click();
        // Wait for the modal to close (Logout button appearing confirms success)
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Logout']")));
        logout();

        // Now login with those credentials
        login(email, password);

        boolean logoutVisible = !driver.findElements(
                By.xpath("//button[text()='Logout']")).isEmpty();
        Assert.assertTrue(logoutVisible, "Logout button should appear after successful login");

        logout();
        System.out.println("[✔] Login with valid credentials – passed");
    }

    @Test(description = "Login with an incorrect password should fail", priority = 4)
    public void testLoginWithInvalidCredentials() {
        openHomePage();

        openLoginModal();
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys("nonexistent@nowhere.com");
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys("WrongPassword!");
        driver.findElement(By.xpath("//button[text()='Login']")).click();

        // Wait for either a toast or the modal to remain visible
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.or(
                            org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                                    By.xpath("//div[contains(@class,'toast')]")),
                            org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                                    By.xpath("//input[@type='email']"))));
        } catch (Exception ignored) {
            // proceed to assertion
        }

        // Modal should still be visible OR an error toast should appear
        boolean modalOrError = !driver.findElements(
                By.xpath("//button[text()='Login' and not(@class)]")).isEmpty()
                || !driver.findElements(By.xpath("//div[contains(@class,'toast')]")).isEmpty();
        Assert.assertTrue(modalOrError,
                "After invalid login, error feedback should be shown");

        // Dismiss modal
        driver.findElements(By.xpath("//div[contains(@class,'fixed') and contains(@class,'bg-black')]"))
              .stream().findFirst().ifPresent(WebElement::click);

        System.out.println("[✔] Login with invalid credentials – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Logout
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Logout button should log the user out and show Login button", priority = 5)
    public void testLogoutFunctionality() {
        openHomePage();

        // Register a fresh user so this test is self-contained
        String email    = "logout_" + System.currentTimeMillis() + "@test.com";
        String password = "Logout@123";

        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Click here']"))).click();
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text']"))).sendKeys("Logout Test User");
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys(email);
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(password);
        driver.findElement(By.xpath("//button[text()='Create Account']")).click();

        // Wait for modal to close (Logout visible = registration succeeded)
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Logout']")));

        // Confirm logged in
        Assert.assertFalse(driver.findElements(
                By.xpath("//button[text()='Logout']")).isEmpty(),
                "Logout button should be present before logging out");

        logout();

        // Confirm logged out – Login button should reappear (logout() already waits for it)
        Assert.assertFalse(driver.findElements(
                By.xpath("//button[text()='Login']")).isEmpty(),
                "Login button should reappear after logout");

        System.out.println("[✔] Logout functionality – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Modal behaviour
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Login modal should close when the dark backdrop is clicked", priority = 6)
    public void testLoginModalClosesOnBackdropClick() {
        openHomePage();

        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();

        WebElement backdrop = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'fixed') and contains(@class,'bg-black')]")));
        Assert.assertTrue(backdrop.isDisplayed(), "Backdrop overlay should be visible");

        backdrop.click();

        // Wait for the modal form to disappear
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class,'fixed') and contains(@class,'bg-black')]")));

        boolean modalGone = driver.findElements(
                By.xpath("//button[text()='Create Account']")).isEmpty()
                && driver.findElements(By.xpath("//p[text()='User Login']")).isEmpty();
        Assert.assertTrue(modalGone, "Login modal should close after clicking backdrop");

        System.out.println("[✔] Login modal closes on backdrop click – passed");
    }

    @Test(description = "Toggle between Login and Register views inside the modal", priority = 7)
    public void testLoginRegisterToggle() {
        openHomePage();

        // Open modal in login state
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();

        // Should show Login button inside modal
        WebElement loginBtn = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//button[text()='Login']")));
        Assert.assertTrue(loginBtn.isDisplayed(), "Login button should be visible inside modal");

        // Switch to register
        driver.findElement(By.xpath("//span[text()='Click here']")).click();

        // Create Account button should now appear
        WebElement createBtn = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//button[text()='Create Account']")));
        Assert.assertTrue(createBtn.isDisplayed(), "Create Account button should be visible after toggling to register");

        // Name field should be visible only in register mode
        Assert.assertFalse(driver.findElements(By.xpath("//input[@type='text']")).isEmpty(),
                "Name input should be shown in register mode");

        // Close modal
        driver.findElement(By.xpath(
                "//div[contains(@class,'fixed') and contains(@class,'bg-black')]")).click();

        System.out.println("[✔] Login/Register toggle – passed");
    }
}
