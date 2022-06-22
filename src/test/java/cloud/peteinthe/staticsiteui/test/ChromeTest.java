package cloud.peteinthe.staticsiteui.test;

import java.io.*;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import cloud.peteinthe.staticsiteui.test.StorageClient;

import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

class ChromeTest {

    public static final String SUITE_NAME = "OLT_Suite";

    static final Boolean OVERWRITE = true;
    static final String RECORD_FILE = "records.txt";

    WebDriver driver;
    Boolean runHeadless = false;
    StorageClient storageClient;

    @BeforeSuite
    void setupClass() throws IOException {
        WebDriverManager.chromedriver().setup();
        storageClient = new StorageClient();
        
        Path recordsFile = storageClient.getFromStorage(RECORD_FILE);
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        BufferedWriter bw = Files.newBufferedWriter(recordsFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        bw.write(String.format("%s %s", SUITE_NAME, ZonedDateTime.now().format(dtf)));
        bw.newLine();
     	  bw.close();    
    }

    @AfterSuite
    void endClass() throws IOException {
      storageClient.store(RECORD_FILE);
    }

    @BeforeTest
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        if (this.runHeadless) {
          options.addArguments("--headless"); 
        }
        driver = new ChromeDriver(options);
    }

    @AfterTest
    void teardown() {
        driver.quit();
    }

    @Test(groups= { "functest" })
    void test() {
        String homepageUrl = System.getenv("HOMEPAGE_URL");

        assertThat(homepageUrl).isNotNull();

        driver.get(homepageUrl);

        String title = driver.getTitle();

        assertThat(title).contains("Home");
    }

}
