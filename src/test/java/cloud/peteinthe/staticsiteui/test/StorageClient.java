package cloud.peteinthe.staticsiteui.test;

import java.io.*;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

class StorageClient {

    static final Boolean OVERWRITE = true;

    private static final String CONNECTION_STRING = System.getenv("SHARED_STORAGE_CONNECTION_STRING");
    private static final String PROTECT_THE = System.getenv("ENVIRONMENT");
    private static final String WORKING_DIR = System.getenv("WORKING_DIR");
    public static final String SHARED_FOLDER_NAME = "azdo_shared"; 

    private static BlobServiceClient serviceClient;
    private static BlobContainerClient containerClient;
    
    public StorageClient() throws IOException {
        this.serviceClient = new BlobServiceClientBuilder().connectionString(CONNECTION_STRING).buildClient();
        this.containerClient = serviceClient.getBlobContainerClient(PROTECT_THE);
        Path recordsDir = Paths.get(WORKING_DIR, SHARED_FOLDER_NAME);
        Files.createDirectories(recordsDir);
    }

    public static Path getFromStorage(String filename) {
        Path pathOfDownload = createPath(filename);

        BlobClient blobClient = StorageClient.containerClient.getBlobClient(filename);
        blobClient.downloadToFile(pathOfDownload.toString());

        return pathOfDownload;
    }

    public static void store(String filename) {
        Path pathOfUpload = createPath(filename);
        BlobClient blobClient = StorageClient.containerClient.getBlobClient(filename);
        blobClient.uploadFromFile(pathOfUpload.toString(), OVERWRITE);
    }

    private static Path createPath(String filename) {
      return Paths.get(WORKING_DIR, SHARED_FOLDER_NAME, filename);
    }
}
