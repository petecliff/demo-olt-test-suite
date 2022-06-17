package cloud.peteinthe.staticsiteui.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

class ChromeTest {

    WebDriver driver;

    @BeforeSuite
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeTest
    void setupTest() {
        driver = new ChromeDriver();
    }

    @AfterTest
    void teardown() {
        driver.quit();
    }

    @Test(groups= { "functest" })
    void test() {
        driver.get("https://azurerockstest.z33.web.core.windows.net/");

        String title = driver.getTitle();
        assertThat(title).contains("Home");
    }

}