package org.example;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.util.IOUtil;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class UploadFile {
    private static final String FOLDER_SEPARATOR = "/";
    private static String appName = "mboptions";
    private static String tokenFile = "dropBoxToken";
    private static String folder = "TEST_FOLDER";

    public static void main(String[] args) {
            sendFile("test.txt");
    }

    public static void sendFile(String filePath) {

        DbxCredential credential;
        try {
            credential = DbxCredential.Reader.readFromFile(tokenFile);
        } catch (JsonReader.FileLoadException e) {
            throw new RuntimeException(e);
        }

        DbxRequestConfig requestConfig = new DbxRequestConfig(appName);
        DbxClientV2 dbxClient = new DbxClientV2(requestConfig, credential);

        File localFile = new File(filePath);
        String dropboxPath = FOLDER_SEPARATOR + folder + FOLDER_SEPARATOR + filePath;
            uploadFile(dbxClient, localFile, dropboxPath);


        try {
            DbxCredential.Writer.writeToFile(credential, tokenFile);
        } catch (IOException e) {
            System.out.println("Didn't save the dropbox token file : " + e.getMessage());
        }
    }

    private static void uploadFile(DbxClientV2 dbxClient, File localFile, String dropboxPath) {
        try (InputStream in = new FileInputStream(localFile)) {
            final double[] tenthOfPercent = {0};
            IOUtil.ProgressListener progressListener = uploaded -> {
                long size = localFile.length();
                double uploadedProgress = 100 * (uploaded / (double) size);
                double integerPart = getFirstDigit(uploadedProgress);
                if (integerPart == tenthOfPercent[0]) {
                    return;
                }
                tenthOfPercent[0] = integerPart;
                System.out.printf("Uploaded %12d / %12d bytes (%5.2f%%)\n", uploaded, size, uploadedProgress);
            };

            FileMetadata metadata = dbxClient.files().uploadBuilder(dropboxPath)
                    .withMode(WriteMode.ADD)
                    .withClientModified(new Date(localFile.lastModified()))
                    .uploadAndFinish(in, progressListener);

            System.out.println(metadata.toStringMultiline());
        } catch (DbxException ex) {
            System.err.println("Error uploading to Dropbox: " + ex.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("Error reading from file \"" + localFile + "\": " + ex.getMessage());
            System.exit(1);
        }
    }

    public static int getFirstDigit(double value) {
        if (value < 10) {
            return (int)value;
        }
        return (int)value / 10;
    }
}