package cloud.peteinthe.staticsiteui.test;

import java.io.*;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

class StorageClient {

    private String sharedFolderName = "azdo_shared"; 
    private String connectionString;
    private String protectThe;
    private String workingDir;

    private BlobServiceClient serviceClient;
    private BlobContainerClient containerClient;
    
    public StorageClient() {
        this.connectionString =  System.getenv("SHARED_STORAGE_CONNECTION_STRING");
        this.protectThe = System.getenv("ENVIRONMENT");
        this.workingDir = System.getenv("WORKING_DIR");

        System.out.println(this.connectionString.substring(0,5));
        System.out.println(this.protectThe);
        System.out.println(this.workingDir);

        this.serviceClient = new BlobServiceClientBuilder().connectionString(this.connectionString).buildClient();
        this.containerClient = serviceClient.getBlobContainerClient(this.protectThe);
    }

    public Path getFromStorage(String filename) throws IOException {
        Path recordsDir = Paths.get(this.workingDir, this.sharedFolderName);
        Files.createDirectories(recordsDir);

        Path pathOfDownload = createPath(filename);

        BlobClient blobClient = this.containerClient.getBlobClient(filename);
        blobClient.downloadToFile(pathOfDownload.toString());

        return pathOfDownload;
    }

    public void store(String filename) throws IOException {
        Path pathOfUpload = createPath(filename);

        BlobClient blobClient = this.containerClient.getBlobClient(filename);
        blobClient.uploadFromFile(pathOfUpload.toString(), true);
    }

    private Path createPath(String filename) {
      return Paths.get(this.workingDir, this.sharedFolderName, filename);
    }
}
