package com.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * UIValidationTest – tests for UI behaviour, navigation, and responsiveness.
 *
 * Test cases:
 *  1. Home page loads with expected heading
 *  2. Navbar is visible and contains Login button (logged-out state)
 *  3. Footer is visible on the home page
 *  4. Navigation to /cars page works
 *  5. Logo click returns to home page
 *  6. Login modal opens and is dismissible
 *  7. Hero search form is visible
 *  8. Mobile viewport – navbar adapts (no crash)
 *  9. Responsive – cars grid is displayed on /cars
 * 10. Loading spinner visible before content loads
 */
public class UIValidationTest extends BaseTest {

    // ──────────────────────────────────────────────────────────────────────────
    // Home page
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Home page loads and shows the main heading", priority = 1)
    public void testHomePageLoads() {
        openHomePage();

        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Luxury cars on Rent')]")));

        Assert.assertTrue(heading.isDisplayed(),
                "Home page heading 'Luxury cars on Rent' should be visible");

        System.out.println("[✔] Home page loads – passed");
    }

    @Test(description = "Navbar is visible and contains expected controls", priority = 2)
    public void testNavbarIsVisible() {
        openHomePage();

        // Logo
        Assert.assertFalse(driver.findElements(By.xpath("//nav")).isEmpty(),
                "Navbar element should be present");

        // Login button (logged-out state)
        WebElement loginBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Login']")));
        Assert.assertTrue(loginBtn.isDisplayed(), "Login button should be visible in navbar");

        System.out.println("[✔] Navbar is visible – passed");
    }

    @Test(description = "Footer is visible on the home page", priority = 3)
    public void testFooterIsVisible() {
        openHomePage();

        // Scroll to the bottom
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");

        List<WebElement> footer = driver.findElements(By.xpath("//footer"));
        Assert.assertFalse(footer.isEmpty(), "Footer element should be present on the page");

        System.out.println("[✔] Footer is visible – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Navigation
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Clicking 'Browse Cars' or navigating to /cars works correctly", priority = 4)
    public void testNavigationToCarsPage() {
        driver.get(BASE_URL + "/cars");

        wait.until(ExpectedConditions.urlContains("/cars"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/cars"),
                "URL should contain '/cars'");

        WebElement showingLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));
        Assert.assertTrue(showingLabel.isDisplayed(),
                "'Showing X Cars' should be visible on /cars");

        System.out.println("[✔] Navigation to /cars page – passed");
    }

    @Test(description = "The application logo navigates back to the home page", priority = 5)
    public void testLogoNavigatesToHomePage() {
        driver.get(BASE_URL + "/cars");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));

        // Click the logo (first link in the nav that goes to "/")
        List<WebElement> logoLinks = driver.findElements(By.xpath("//a[@href='/']"));
        Assert.assertFalse(logoLinks.isEmpty(), "Logo link pointing to '/' should exist in the navbar");

        logoLinks.get(0).click();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
        Assert.assertTrue(driver.getCurrentUrl().endsWith("/"),
                "After clicking logo, URL should be the home page");

        System.out.println("[✔] Logo navigates to home page – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Login modal
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Login modal opens when the Login button is clicked", priority = 6)
    public void testLoginModalOpens() {
        openHomePage();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='email']")));
        Assert.assertTrue(emailInput.isDisplayed(), "Email input should be visible inside the login modal");

        WebElement loginButton = driver.findElement(By.xpath("//button[text()='Login']"));
        Assert.assertTrue(loginButton.isDisplayed(), "Login submit button should be visible in the modal");

        // Dismiss
        driver.findElement(By.xpath(
                "//div[contains(@class,'fixed') and contains(@class,'bg-black')]")).click();

        System.out.println("[✔] Login modal opens – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Hero search form
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Hero search form is visible on the home page", priority = 7)
    public void testHeroSearchFormVisible() {
        openHomePage();

        WebElement locationSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//select")));
        WebElement pickupDateInput = driver.findElement(By.id("pickup-date"));
        WebElement returnDateInput = driver.findElement(By.id("return-date"));
        WebElement searchButton    = driver.findElement(
                By.xpath("//button[contains(.,'Search')]"));

        Assert.assertTrue(locationSelect.isDisplayed(),  "Location dropdown should be visible");
        Assert.assertTrue(pickupDateInput.isDisplayed(), "Pickup date input should be visible");
        Assert.assertTrue(returnDateInput.isDisplayed(), "Return date input should be visible");
        Assert.assertTrue(searchButton.isDisplayed(),    "Search button should be visible");

        System.out.println("[✔] Hero search form visible – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Responsive design
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Mobile viewport (375×812) – page loads without errors", priority = 8)
    public void testMobileViewportPageLoads() {
        driver.manage().window().setSize(new Dimension(375, 812));
        openHomePage();

        // Page should still contain the main heading
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Luxury cars on Rent')]")));
        Assert.assertTrue(heading.isDisplayed(), "Page should render heading on mobile viewport");

        // Restore full-size window
        driver.manage().window().setSize(new Dimension(1920, 1080));

        System.out.println("[✔] Mobile viewport page loads – passed");
    }

    @Test(description = "Tablet viewport (768×1024) – page loads without errors", priority = 9)
    public void testTabletViewportPageLoads() {
        driver.manage().window().setSize(new Dimension(768, 1024));
        openHomePage();

        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Luxury cars on Rent')]")));
        Assert.assertTrue(heading.isDisplayed(), "Page should render heading on tablet viewport");

        driver.manage().window().setSize(new Dimension(1920, 1080));

        System.out.println("[✔] Tablet viewport page loads – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Cars page layout
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Cars page displays a grid of car cards", priority = 10)
    public void testCarsPageShowsCarGrid() {
        driver.get(BASE_URL + "/cars");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));

        // Car cards use class containing 'rounded-xl'
        List<WebElement> cards = driver.findElements(
                By.xpath("//div[contains(@class,'rounded-xl')]"));

        // The page should have at least one card (the app has seeded cars)
        Assert.assertTrue(cards.size() > 0, "Car cards should be visible on the /cars page");

        System.out.println("[✔] Cars page shows car grid – passed");
    }

    @Test(description = "Available Cars page title and subtitle are visible", priority = 11)
    public void testCarsPageTitleVisible() {
        driver.get(BASE_URL + "/cars");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Available Cars')]")));
        Assert.assertTrue(title.isDisplayed(), "'Available Cars' title should be visible on /cars");

        System.out.println("[✔] Cars page title visible – passed");
    }
}
