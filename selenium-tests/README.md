# CarRental Selenium Test Suite

End-to-end Selenium test suite for the [CarRental](https://car-rental-nine-chi.vercel.app/) application, built with Java, Selenium WebDriver, and TestNG.

---

## Project Structure

```
selenium-tests/
├── pom.xml                          # Maven build configuration
├── testng.xml                       # TestNG suite definition
├── README.md                        # This file
└── src/
    └── test/
        └── java/
            └── com/
                └── testing/
                    ├── BaseTest.java                  # Common setup, teardown & utilities
                    ├── CarRentalFullTest.java          # Full end-to-end smoke test
                    ├── UserAuthenticationTest.java     # Registration, login, logout
                    ├── CarSearchAndFilterTest.java     # Car search & filter flows
                    ├── BookingManagementTest.java      # Booking creation & management
                    ├── UIValidationTest.java           # UI, navigation & responsive tests
                    └── DataValidationTest.java         # Form & data validation tests
```

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 11 or higher |
| Apache Maven | 3.8 or higher |
| Google Chrome | Latest stable |

> **Note:** ChromeDriver is managed automatically by [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) — no manual driver download needed.

---

## Quick Start

```bash
# 1. Navigate to the test project
cd selenium-tests

# 2. Run all tests
mvn test

# 3. Run a specific test class
mvn test -Dtest=UserAuthenticationTest

# 4. Run a specific test method
mvn test -Dtest=UserAuthenticationTest#testLoginWithValidCredentials
```

---

## Test Classes

### `BaseTest.java`
Shared base class extended by all test classes.

- Configures headless ChromeDriver via WebDriverManager
- Provides `@BeforeClass` / `@AfterClass` lifecycle methods
- Captures a screenshot to `screenshots/` on any test failure (`@AfterMethod`)
- Helper utilities:
  - `login(email, password)` — full login via the navbar modal
  - `logout()` — safe logout
  - `openLoginModal()` — opens the login modal and returns the email input
  - `setDateInput(id, value)` — sets an HTML5 `<input type="date">` using the React-compatible native setter
  - `getFutureDate(days)` — returns a future date in `yyyy-MM-dd` format
  - `takeScreenshot(name)` — saves a PNG screenshot

---

### `CarRentalFullTest.java`
**Primary smoke test** — adapted from the original standalone test.

| Test | Description |
|------|-------------|
| `testFullUserJourney` | Register → logout → login → search → open car → book → verify redirect |

---

### `UserAuthenticationTest.java`

| Test | Description |
|------|-------------|
| `testRegistrationWithValidData` | New user registers successfully |
| `testRegistrationWithDuplicateEmail` | Duplicate email registration is rejected |
| `testLoginWithValidCredentials` | Valid credentials log the user in |
| `testLoginWithInvalidCredentials` | Wrong password shows an error |
| `testLogoutFunctionality` | Logout button logs the user out |
| `testLoginModalClosesOnBackdropClick` | Clicking the backdrop dismisses the modal |
| `testLoginRegisterToggle` | Toggling between Login and Register views works |

---

### `CarSearchAndFilterTest.java`

| Test | Description |
|------|-------------|
| `testSearchCarsByLocation` | Selecting a city and searching navigates to `/cars` |
| `testSearchWithDateFilters` | Search with pickup and return dates shows results |
| `testFilterByCarName` | Typing in the `/cars` search box filters the list |
| `testClearFilterShowsAllCars` | Clearing the filter restores the full car list |
| `testCarsPageLoadsDirectly` | Navigating directly to `/cars` shows all cars |
| `testAllPickupLocationsAvailable` | All six cities appear in the Hero dropdown |
| `testCarsPageSearchInputIsPresent` | Search input is visible and enabled on `/cars` |

---

### `BookingManagementTest.java`

| Test | Description |
|------|-------------|
| `testCreateNewBooking` | Full booking flow ends at `/my-bookings` |
| `testViewMyBookingsPage` | My Bookings page loads for a logged-in user |
| `testMyBookingsAccessibleFromNavbar` | `/my-bookings` is accessible after login |
| `testCarDetailsPageShowsBookingForm` | Car details page shows the booking form |
| `testCarDetailsBackLink` | "Back to all cars" link returns to `/cars` |
| `testUnauthenticatedMyBookingsRedirect` | Unauthenticated access to `/my-bookings` does not crash |

---

### `UIValidationTest.java`

| Test | Description |
|------|-------------|
| `testHomePageLoads` | Home page shows "Luxury cars on Rent" heading |
| `testNavbarIsVisible` | Navbar and Login button are visible |
| `testFooterIsVisible` | Footer element is present on the home page |
| `testNavigationToCarsPage` | `/cars` URL loads with the cars grid |
| `testLogoNavigatesToHomePage` | Logo link returns to home |
| `testLoginModalOpens` | Login modal opens on button click |
| `testHeroSearchFormVisible` | All Hero form controls are visible |
| `testMobileViewportPageLoads` | 375×812 mobile viewport renders correctly |
| `testTabletViewportPageLoads` | 768×1024 tablet viewport renders correctly |
| `testCarsPageShowsCarGrid` | `/cars` page displays car cards |
| `testCarsPageTitleVisible` | "Available Cars" title is visible on `/cars` |

---

### `DataValidationTest.java`

| Test | Description |
|------|-------------|
| `testEmailInputTypeIsEmail` | Login email field has `type="email"` |
| `testPasswordInputTypeIsPassword` | Password field has `type="password"` |
| `testLoginFormEmptyEmailBlocked` | Empty email prevents login form submission |
| `testLoginFormEmptyPasswordBlocked` | Empty password prevents login form submission |
| `testRegisterFormNameRequired` | Empty name prevents register form submission |
| `testHeroSearchRequiresLocation` | Missing location prevents search submission |
| `testHeroSearchRequiresPickupDate` | Missing pickup date prevents search |
| `testHeroSearchRequiresReturnDate` | Missing return date prevents search |
| `testPickupDateInputAttributes` | Pickup date has `type="date"` and a `min` attribute |
| `testReturnDateInputAttributes` | Return date has `type="date"` |
| `testCarsSearchInputSpecialCharacters` | Special characters in the search box don't crash the page |

---

## Screenshots

Failed tests automatically save screenshots to the `screenshots/` directory (created automatically). Screenshot file names follow the pattern:

```
screenshots/<TestMethodName>_FAILED_<timestamp>.png
```

You can also call `takeScreenshot("my-label")` from any test for ad-hoc captures.

---

## Configuration

The base URL and default test credentials are defined as constants in `BaseTest.java`:

```java
protected static final String BASE_URL      = "https://car-rental-nine-chi.vercel.app";
protected static final String TEST_EMAIL    = "seleniumtester@example.com";
protected static final String TEST_PASSWORD = "Test@123";
```

To run tests against a different environment, update `BASE_URL` accordingly.

To run in **headed** (non-headless) mode for debugging, remove or comment out the headless Chrome options in `BaseTest.setUp()`:

```java
// options.addArguments("--headless=new");
```

---

## Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| `selenium-java` | 4.20.0 | Browser automation |
| `webdrivermanager` | 6.1.0 | Automatic ChromeDriver management |
| `testng` | 7.9.0 | Test framework |
| `commons-io` | 2.15.1 | File utilities for screenshots |
