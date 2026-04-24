package com.testing;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Base class for all CarRental Selenium tests.
 * Provides shared WebDriver setup/teardown, screenshot-on-failure,
 * and common helper utilities used across every test class.
 */
public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // Application URL
    protected static final String BASE_URL = "https://car-rental-nine-chi.vercel.app";

    // Shared test credentials – a pre-existing account used for read-only flows
    protected static final String TEST_EMAIL    = "seleniumtester@example.com";
    protected static final String TEST_PASSWORD = "Test@123";
    protected static final String TEST_NAME     = "Selenium Tester";

    // Directory where screenshots are saved
    protected static final String SCREENSHOTS_DIR = "screenshots/";

    // ──────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ──────────────────────────────────────────────────────────────────────────

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(25));

        new File(SCREENSHOTS_DIR).mkdirs();
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Automatically captures a screenshot when a test method fails.
     */
    @AfterMethod
    public void captureScreenshotOnFailure(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result.getName() + "_FAILED");
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Utilities
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Saves a PNG screenshot to the {@code screenshots/} directory.
     *
     * @param name base file name (timestamp appended automatically)
     */
    protected void takeScreenshot(String name) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String dest = SCREENSHOTS_DIR + name + "_" + System.currentTimeMillis() + ".png";
            Files.copy(src.toPath(), Paths.get(dest));
            System.out.println("[Screenshot] saved: " + dest);
        } catch (IOException e) {
            System.err.println("[Screenshot] failed to save: " + e.getMessage());
        }
    }

    /** Navigates to the application home page. */
    protected void openHomePage() {
        driver.get(BASE_URL);
    }

    /**
     * Performs a full login flow starting from the home page.
     * Opens the login modal, fills credentials, and submits.
     */
    protected void login(String email, String password) {
        openHomePage();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='email']"))).sendKeys(email);

        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(password);
        driver.findElement(By.xpath("//button[text()='Login']")).click();

        // Wait until modal closes, indicating successful login
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//p[contains(@class,'text-2xl') and contains(.,'Login')]")));
    }

    /**
     * Clicks the Logout button and waits for the Login button to reappear.
     * Safe to call even if already logged out.
     */
    protected void logout() {
        try {
            WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[text()='Logout']")));
            logoutBtn.click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[text()='Login']")));
        } catch (Exception e) {
            System.out.println("[logout] skipped – already logged out or element not found");
        }
    }

    /**
     * Returns a date string {@code daysFromNow} days in the future, formatted
     * as {@code yyyy-MM-dd} (the value accepted by HTML5 date inputs).
     */
    protected String getFutureDate(int daysFromNow) {
        return LocalDate.now()
                .plusDays(daysFromNow)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * Sets the value of an HTML5 {@code <input type="date">} element by using
     * the native value setter so that React's synthetic event system is notified.
     *
     * @param elementId the {@code id} attribute of the date input
     * @param dateValue date in {@code yyyy-MM-dd} format
     */
    protected void setDateInput(String elementId, String dateValue) {
        WebElement input = driver.findElement(By.id(elementId));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
            "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
            "setter.call(arguments[0], arguments[1]);" +
            "arguments[0].dispatchEvent(new Event('input',  { bubbles: true }));" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            input, dateValue
        );
    }

    /**
     * Opens the Login modal from the navbar.
     * Returns the email input element once the modal is visible.
     */
    protected WebElement openLoginModal() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Login']"))).click();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='email']")));
    }
}
