package cloud.peteinthe.staticsiteui.test;

import java.io.*;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

class ChromeTest {

    static final Boolean OVERWRITE = true;

    WebDriver driver;

    @BeforeSuite
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
        
        String sharedStoreConnectionString = System.getenv("SHARED_STORAGE_CONNECTION_STRING");
        String protectTheEnvironment = System.getenv("ENVIRONMENT");
        String workingDir = System.getenv("WORKING_DIR");
      
        String recordsFilename = String.format("records_%s.txt", protectTheEnvironment);

        BlobServiceClient storageClient = new BlobServiceClientBuilder().connectionString(sharedStoreConnectionString).buildClient();
        BlobContainerClient blobContainerClient = storageClient.getBlobContainerClient(protectTheEnvironment);

        try {
          blobContainerClient.create();
        } catch (BlobStorageException err) {
          if (err.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
            System.out.println("Container exists - carrying on...");
          } else {
            throw err;
          }
        }

        Path recordsFile = FileSystems.getDefault().getPath("azdo_shared", recordsFilename);

        try {
          Files.deleteIfExists(recordsFile);
          BlobClient blobClient = blobContainerClient.getBlobClient(recordsFilename);
          System.out.println(recordsFile.toString());
          blobClient.downloadToFile(recordsFile.toString());

          DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

          BufferedWriter bw = Files.newBufferedWriter(recordsFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND,StandardOpenOption.CREATE);
          bw.write(String.format("%s %s", "OLS_Suite", ZonedDateTime.now().format(dtf)));
          bw.newLine();
       	  bw.close();
        } catch (Exception err) {
          System.out.println(err);
        }
    }

    @AfterSuite
    static void endClass() {
        String sharedStoreConnectionString = System.getenv("SHARED_STORAGE_CONNECTION_STRING");
        String protectTheEnvironment = System.getenv("ENVIRONMENT");
        String workingDir = System.getenv("WORKING_DIR");

        String recordsFilename = String.format("records_%s.txt", protectTheEnvironment);

        BlobServiceClient storageClient = new BlobServiceClientBuilder().connectionString(sharedStoreConnectionString).buildClient();
        BlobContainerClient blobContainerClient = storageClient.getBlobContainerClient(protectTheEnvironment);

        BlobClient blobClient = blobContainerClient.getBlobClient(recordsFilename);
        Path recordsFile = FileSystems.getDefault().getPath("azdo_shared", recordsFilename);
        blobClient.uploadFromFile(recordsFile.toString(), OVERWRITE);
    }

    @BeforeTest
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
	options.addArguments("--headless"); 
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
