package utils;

import aquality.selenium.browser.AqualityServices;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtils {

    private static final String IMAGE_SOURCE_PATH = "src/test/resources/img.jpg";
    private static final String TARGET_PATH_TEMPLATE = "target/%s";

    private FileUtils() {
    }

    public static File createTempTextFile(String fileName, String content) {
        AqualityServices.getLogger().info(String.format("Creating temp text file '%s'", fileName));
        String relativePath = String.format(TARGET_PATH_TEMPLATE, fileName);
        File file = new File(new File(relativePath).getAbsolutePath());

        try {
            Files.writeString(file.toPath(), content);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to write to file: %s", file.getAbsolutePath()), e);
        }

        return file;
    }

    public static File copyTestImage(String targetFileName) {
        AqualityServices.getLogger().info(String.format("Copying test image to target folder as '%s'", targetFileName));
        File sourceFile = new File(new File(IMAGE_SOURCE_PATH).getAbsolutePath());

        String relativePath = String.format(TARGET_PATH_TEMPLATE, targetFileName);
        File targetFile = new File(new File(relativePath).getAbsolutePath());

        try {
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to copy image file from %s to %s", sourceFile.getAbsolutePath(), targetFile.getAbsolutePath()), e);
        }

        return targetFile;
    }
}