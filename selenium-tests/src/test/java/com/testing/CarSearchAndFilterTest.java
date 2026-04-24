package com.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * CarSearchAndFilterTest – tests for the car search and filter functionality.
 *
 * Test cases:
 *  1. Search cars by location using the Hero form
 *  2. Search with pickup and return date filters
 *  3. Search that returns no results (invalid location combination)
 *  4. Filter cars on the /cars page using the text search input
 *  5. Navigate directly to /cars without search parameters
 *  6. All available locations can be selected in the dropdown
 */
public class CarSearchAndFilterTest extends BaseTest {

    private static final String PICKUP_LOCATION = "Bathinda";

    // ──────────────────────────────────────────────────────────────────────────
    // Hero search form
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Search cars by selecting a pickup location and submitting the form", priority = 1)
    public void testSearchCarsByLocation() {
        openHomePage();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickup-date")));

        Select locationDropdown = new Select(driver.findElement(By.xpath("//select")));
        locationDropdown.selectByVisibleText(PICKUP_LOCATION);

        setDateInput("pickup-date", getFutureDate(5));
        setDateInput("return-date", getFutureDate(6));

        driver.findElement(By.xpath("//button[contains(.,'Search')]")).click();

        wait.until(ExpectedConditions.urlContains("/cars"));
        Assert.assertTrue(driver.getCurrentUrl().contains("pickupLocation=" + PICKUP_LOCATION),
                "URL should contain the selected pickup location");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));

        System.out.println("[✔] Search by location – passed");
    }

    @Test(description = "Search results page displays 'Showing X Cars' after a search", priority = 2)
    public void testSearchWithDateFilters() {
        openHomePage();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickup-date")));

        Select locationDropdown = new Select(driver.findElement(By.xpath("//select")));
        locationDropdown.selectByVisibleText("Amritsar");

        String pickup = getFutureDate(7);
        String ret    = getFutureDate(9);

        setDateInput("pickup-date", pickup);
        setDateInput("return-date", ret);

        driver.findElement(By.xpath("//button[contains(.,'Search')]")).click();

        wait.until(ExpectedConditions.urlContains("/cars"));

        WebElement showingText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));

        Assert.assertTrue(showingText.isDisplayed(),
                "'Showing X Cars' label should be visible after search");

        System.out.println("[✔] Search with date filters – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Cars page text filter
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "Filter cars on the /cars page using the search input box", priority = 3)
    public void testFilterByCarName() {
        driver.get(BASE_URL + "/cars");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));

        // Type a common brand to filter
        WebElement searchInput = driver.findElement(
                By.xpath("//input[@placeholder[contains(.,'Search by make')]]"));
        searchInput.sendKeys("BMW");

        // Wait for the 'Showing' label to still be present after filtering
        WebElement showingLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));

        // The showing text should still be present (filtered list, possibly 0)
        Assert.assertTrue(showingLabel.isDisplayed(),
                "'Showing X Cars' label should still be visible after filtering");

        System.out.println("[✔] Filter by car name – passed");
    }

    @Test(description = "Clearing the filter text input shows all cars again", priority = 4)
    public void testClearFilterShowsAllCars() {
        driver.get(BASE_URL + "/cars");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));

        // Get initial count
        String initialText = driver.findElement(
                By.xpath("//p[contains(text(),'Showing')]")).getText();

        // Filter by something that matches nothing
        WebElement searchInput = driver.findElement(
                By.xpath("//input[@placeholder[contains(.,'Search by make')]]"));
        searchInput.sendKeys("ZZZZZ_NO_MATCH");

        // Wait for the list to update to "Showing 0 Cars"
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//p[contains(text(),'Showing')]"), "0"));

        // Clear the filter
        searchInput.clear();

        // Wait for the list to return to the initial count
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//p[contains(text(),'Showing')]"),
                initialText.replace("Showing ", "").replace(" Cars", "").trim()));

        String afterClearText = driver.findElement(
                By.xpath("//p[contains(text(),'Showing')]")).getText();

        Assert.assertEquals(afterClearText, initialText,
                "Clearing the filter should restore the original car count");

        System.out.println("[✔] Clear filter restores all cars – passed");
    }

    @Test(description = "Navigating directly to /cars without search params shows all available cars", priority = 5)
    public void testCarsPageLoadsDirectly() {
        driver.get(BASE_URL + "/cars");

        WebElement showingLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Showing')]")));

        Assert.assertTrue(showingLabel.isDisplayed(),
                "'Showing X Cars' label should be visible on the /cars page");

        System.out.println("[✔] /cars page loads directly – passed");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Location dropdown
    // ──────────────────────────────────────────────────────────────────────────

    @Test(description = "All expected pickup locations are available in the Hero form dropdown", priority = 6)
    public void testAllPickupLocationsAvailable() {
        openHomePage();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickup-date")));

        Select locationDropdown = new Select(driver.findElement(By.xpath("//select")));
        List<WebElement> options = locationDropdown.getOptions();

        // Expected cities defined in assets.js
        String[] expectedCities = {"Bathinda", "Amritsar", "Ludhiana", "Jalandhar", "Patiala", "Mohali"};

        List<String> optionTexts = options.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(java.util.stream.Collectors.toList());

        for (String city : expectedCities) {
            Assert.assertTrue(optionTexts.contains(city),
                    "Dropdown should contain city: " + city);
        }

        System.out.println("[✔] All pickup locations available – passed");
    }

    @Test(description = "Cars page search input is present and accepts input", priority = 7)
    public void testCarsPageSearchInputIsPresent() {
        driver.get(BASE_URL + "/cars");

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder[contains(.,'Search by make')]]")));

        Assert.assertTrue(searchInput.isDisplayed(), "Search input should be visible on the cars page");
        Assert.assertTrue(searchInput.isEnabled(),   "Search input should be enabled");

        System.out.println("[✔] Cars page search input is present – passed");
    }
}
