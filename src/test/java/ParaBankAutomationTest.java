import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ChromeTest {
    WebDriver driver;

    @BeforeMethod
    public void setup() {

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @Test
    public void checkGoogleTitle() {

        driver.get("https://www.google.com");
        String title = driver.getTitle();
        System.out.println("Address path " + title);
        Assert.assertEquals(title, "Google");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Successful Closed");
        }
    }
}