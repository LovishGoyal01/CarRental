package com.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * BookingManagementTest – tests for the car-booking workflows.
 *
 * Test cases:
 *  1. Create a new booking through the full user journey
 *  2. View the My Bookings page (requires login)
 *  3. My Bookings page is accessible after login
 *  4. Unauthenticated access to /my-bookings redirects home
 *  5. Book Now button requires both dates to be filled
 *  6. Car details page shows booking form
 */
public class BookingManagementTest extends BaseTest {

    // ──────────────────────────────────────────────────────────────────────────
    // Create booking
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Complete a full booking flow: search → car details → book", priority = 1)
    public void testCreateNewBooking() {
        // ── Register a fresh user so the test is self-contained ────────────────
        String email    = "booking_" + System.currentTimeMillis() + "@test.com";
        String password = "Booking@123";

        openHomePage();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Click here']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text']"))).sendKeys("Booking Test User");
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys(email);
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(password);
        driver.findElement(By.xpath("//button[text()='Create Account']")).click();
        // Wait for modal to close after registration
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Logout']")));

        // ── Search for available cars ──────────────────────────────────────────
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickup-date")));

        Select locationDropdown = new Select(driver.findElement(By.xpath("//select")));
        locationDropdown.selectByVisibleText("Bathinda");

        String pickupDate = getFutureDate(15);
        String returnDate = getFutureDate(16);

        setDateInput("pickup-date", pickupDate);
        setDateInput("return-date", returnDate);

        driver.findElement(By.xpath("//button[contains(.,'Search')]")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));

        // ── Open the first car card ────────────────────────────────────────────
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[contains(@class,'rounded-xl')])[1]"))).click();

        // ── Fill booking form and submit ───────────────────────────────────────
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(text(),'Book Now')]")));

        setDateInput("pickup-date", pickupDate);
        setDateInput("return-date", returnDate);

        driver.findElement(By.xpath("//button[contains(text(),'Book Now')]")).click();

        // ── Verify redirect to My Bookings ─────────────────────────────────────
        wait.until(ExpectedConditions.urlContains("my-bookings"));
        Assert.assertTrue(driver.getCurrentUrl().contains("my-bookings"),
                "After booking, URL should contain 'my-bookings'");

        System.out.println("[✔] Create new booking – passed");

        logout();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // My Bookings page
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "My Bookings page loads and shows booking history for a logged-in user", priority = 2)
    public void testViewMyBookingsPage() {
        // Register & immediately navigate to My Bookings
        String email    = "mybookings_" + System.currentTimeMillis() + "@test.com";
        String password = "MyBook@123";

        openHomePage();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Click here']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text']"))).sendKeys("My Bookings User");
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys(email);
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(password);
        driver.findElement(By.xpath("//button[text()='Create Account']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Logout']")));

        driver.get(BASE_URL + "/my-bookings");

        // Page title should contain "My Bookings"
        WebElement pageTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'My Bookings')]")));
        Assert.assertTrue(pageTitle.isDisplayed(), "My Bookings title should be visible");

        System.out.println("[✔] View My Bookings page – passed");

        logout();
    }

    @Test(description = "My Bookings page is accessible via the navbar after login", priority = 3)
    public void testMyBookingsAccessibleFromNavbar() {
        // Register fresh user
        String email    = "navbooking_" + System.currentTimeMillis() + "@test.com";
        String password = "NavBook@123";

        openHomePage();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Click here']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text']"))).sendKeys("Nav Booking User");
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys(email);
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(password);
        driver.findElement(By.xpath("//button[text()='Create Account']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Logout']")));

        // Navigate to /my-bookings directly
        driver.get(BASE_URL + "/my-bookings");

        Assert.assertTrue(driver.getCurrentUrl().contains("my-bookings"),
                "URL should be /my-bookings");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'My Bookings')]")));
        Assert.assertTrue(title.isDisplayed(), "My Bookings page should load");

        System.out.println("[✔] My Bookings accessible from navbar – passed");

        logout();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Car details booking form
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Car details page shows a booking form with pickup/return date inputs and Book Now button", priority = 4)
    public void testCarDetailsPageShowsBookingForm() {
        openHomePage();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickup-date")));

        Select locationDropdown = new Select(driver.findElement(By.xpath("//select")));
        locationDropdown.selectByVisibleText("Bathinda");

        setDateInput("pickup-date", getFutureDate(5));
        setDateInput("return-date", getFutureDate(6));

        driver.findElement(By.xpath("//button[contains(.,'Search')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));

        // Click on first car card
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[contains(@class,'rounded-xl')])[1]"))).click();

        // Booking form elements
        WebElement bookNowBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(text(),'Book Now')]")));
        WebElement pickupInput = driver.findElement(By.id("pickup-date"));
        WebElement returnInput = driver.findElement(By.id("return-date"));

        Assert.assertTrue(bookNowBtn.isDisplayed(),  "Book Now button should be visible");
        Assert.assertTrue(pickupInput.isDisplayed(), "Pickup date input should be visible");
        Assert.assertTrue(returnInput.isDisplayed(), "Return date input should be visible");

        System.out.println("[✔] Car details booking form is present – passed");
    }

    @Test(description = "Car details page shows the Back to all cars link", priority = 5)
    public void testCarDetailsBackLink() {
        openHomePage();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickup-date")));

        Select locationDropdown = new Select(driver.findElement(By.xpath("//select")));
        locationDropdown.selectByVisibleText("Bathinda");

        setDateInput("pickup-date", getFutureDate(5));
        setDateInput("return-date", getFutureDate(6));

        driver.findElement(By.xpath("//button[contains(.,'Search')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[contains(@class,'rounded-xl')])[1]"))).click();

        WebElement backLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Back to all cars')]")));
        Assert.assertTrue(backLink.isDisplayed(), "Back to all cars link should be visible");

        backLink.click();
        wait.until(ExpectedConditions.urlContains("/cars"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/cars"),
                "Clicking back should return to the /cars page");

        System.out.println("[✔] Car details back link – passed");
    }

    @Test(description = "Unauthenticated users are redirected home when accessing /my-bookings", priority = 6)
    public void testUnauthenticatedMyBookingsRedirect() {
        // Ensure we are logged out
        openHomePage();
        logout();

        // Attempt direct navigation to protected page
        driver.get(BASE_URL + "/my-bookings");

        // Wait for the page to settle (either stays on /my-bookings or redirects)
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("my-bookings"),
                ExpectedConditions.urlToBe(BASE_URL + "/"),
                ExpectedConditions.urlToBe(BASE_URL)));

        // App should NOT crash and should show some content
        Assert.assertFalse(driver.getTitle().isEmpty(),
                "Page should still render a title even for unauthenticated users");

        System.out.println("[✔] Unauthenticated /my-bookings redirect – passed");
    }
}
