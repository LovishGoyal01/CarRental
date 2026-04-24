package com.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * CarRentalFullTest – end-to-end smoke test.
 *
 * Covers the complete primary user journey:
 *   Registration → Logout → Login → Search → Car Details → Booking
 *
 * This class is preserved from the original standalone test and has been
 * adapted to use the TestNG + BaseTest infrastructure for consistency.
 */
public class CarRentalFullTest extends BaseTest {

    @Test(description = "Full end-to-end user journey: register, login, search, and book a car")
    public void testFullUserJourney() {

        String uniqueEmail = "test" + System.currentTimeMillis() + "@gmail.com";
        String password    = "Test@123";
        String pickupDate  = getFutureDate(10);
        String returnDate  = getFutureDate(11);

        // ── 1. Open website ────────────────────────────────────────────────────
        openHomePage();
        Assert.assertTrue(driver.getTitle().contains("Car"), "Home page title should contain 'Car'");

        // ── 2. Open Login modal ────────────────────────────────────────────────
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();

        // ── 3. Switch to Register ──────────────────────────────────────────────
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Click here']"))).click();

        // ── 4. Fill Registration form ──────────────────────────────────────────
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text']"))).sendKeys("Test User");

        driver.findElement(By.xpath("//input[@type='email']")).sendKeys(uniqueEmail);
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(password);
        driver.findElement(By.xpath("//button[text()='Create Account']")).click();

        // Wait for the modal to close (Logout button signals successful registration)
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Logout']")));
        System.out.println("[✔] Registration successful");

        // ── 5. Logout ──────────────────────────────────────────────────────────
        driver.findElement(By.xpath("//button[text()='Logout']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Login']")));
        System.out.println("[✔] Logout successful");

        // ── 6. Login again ─────────────────────────────────────────────────────
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='email']"))).sendKeys(uniqueEmail);
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(password);
        driver.findElement(By.xpath("//button[text()='Login']")).click();

        // Wait for modal to close (Logout button visible = login succeeded)
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Logout']")));
        System.out.println("[✔] Login successful");

        // ── 7. Fill Hero search form ───────────────────────────────────────────
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickup-date")));

        Select locationDropdown = new Select(driver.findElement(By.xpath("//select")));
        locationDropdown.selectByVisibleText("Bathinda");
        System.out.println("[✔] Pickup location selected: Bathinda");

        setDateInput("pickup-date", pickupDate);
        setDateInput("return-date", returnDate);

        // ── 8. Submit search ───────────────────────────────────────────────────
        driver.findElement(By.xpath("//button[contains(.,'Search')]")).click();
        System.out.println("[✔] Search submitted");

        // ── 9. Wait for Cars page ──────────────────────────────────────────────
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));
        System.out.println("[✔] Cars page loaded");

        // ── 10. Click first car card ───────────────────────────────────────────
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[contains(@class,'rounded-xl')])[1]"))).click();
        System.out.println("[✔] Car details opened");

        // ── 11. Fill booking form and submit ───────────────────────────────────
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(text(),'Book Now')]")));

        setDateInput("pickup-date", pickupDate);
        setDateInput("return-date", returnDate);

        driver.findElement(By.xpath("//button[contains(text(),'Book Now')]")).click();
        System.out.println("[✔] Booking submitted");

        // ── 12. Verify redirect to My Bookings ─────────────────────────────────
        wait.until(ExpectedConditions.urlContains("my-bookings"));
        System.out.println("[✔] Booking completed – redirected to /my-bookings");

        Assert.assertTrue(driver.getCurrentUrl().contains("my-bookings"),
                "After booking, URL should contain 'my-bookings'");
    }
}
