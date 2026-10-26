package tests;

import api.ApiResponse;
import api.DiskApi;
import aquality.selenium.browser.AqualityServices;
import constants.AuthConstants;
import io.qameta.allure.*;
import io.restassured.response.Response;
import models.LinkResponse;
import org.apache.http.HttpStatus;
import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.DiskPage;
import pages.ImageViewerPage;
import pages.MainPage;
import pages.TextEditorPage;
import tests.base.BaseTest;
import utils.ConfigManager;
import utils.FileUtils;
import utils.TestDataUtils;

import java.io.File;
import java.util.Optional;

@Epic("Yandex Disk")
@Feature("File management")
public class DiskTest extends BaseTest {

    private static final String DISK_PATH_TEMPLATE = "disk:/%s";

    @Test(description = "Upload, edit, trash and restore a file via API with UI verification")
    @Story("Upload, rename, move to trash and restore a file")
    @Severity(SeverityLevel.CRITICAL)
    @Link(name = "Yandex Disk REST API", url = "https://yandex.ru/dev/disk-api/doc/ru/")
    @Description(value = "End-to-end scenario combining API and UI. Files are created, renamed, moved to trash and restored through the Yandex Disk REST API, and every change is verified in the web interface without page refresh. The test also checks the content of the uploaded text file and image in the built-in viewers.")
    @Owner("Maksim Dubynin")
    public void testYandexDisk() {
        MainPage mainPage = new MainPage();
        DiskPage diskPage = new DiskPage();
        TextEditorPage textEditorPage = new TextEditorPage();
        ImageViewerPage imageViewerPage = new ImageViewerPage();
        DiskApi diskApi = new DiskApi();
        String textExt = ConfigManager.getTextExtension();
        String textFileName = String.format("%s%s", TestDataUtils.generateRandomWord(), textExt);
        String textContent = TestDataUtils.generateRandomSentence();
        File textFile = FileUtils.createTempTextFile(textFileName, textContent);
        String fileNameWithoutExtension = textFileName.replace(textExt, "");
        String imgExt = ConfigManager.getImageExtension();
        String imageFileName = String.format("%s%s", fileNameWithoutExtension, imgExt);
        String imageDiskPath = String.format(DISK_PATH_TEMPLATE, imageFileName);
        String textDiskPath = String.format(DISK_PATH_TEMPLATE, textFileName);
        File imageFile = FileUtils.copyTestImage(imageFileName);

        Allure.step("[UI] Authenticate user", () -> {
            Cookie sessionIdCookie = new Cookie.Builder(AuthConstants.SESSION_COOKIE_NAME, ConfigManager.getSessionId())
                    .domain(AuthConstants.SESSION_COOKIE_DOMAIN)
                    .build();
            AqualityServices.getBrowser().getDriver().manage().addCookie(sessionIdCookie);

            AqualityServices.getLogger().info("Refreshing page to apply authentication");
            AqualityServices.getBrowser().refresh();
        });
        Allure.step("[UI] Open Disk", () -> {
            Assert.assertTrue(
                    mainPage.state().waitForDisplayed(),
                    String.format("Page '%s' is not displayed", mainPage.getName())
            );

            mainPage.clickGoToYandexDiskButton();
            AqualityServices.getBrowser().tabs().switchToLast();

            Assert.assertTrue(
                    diskPage.state().waitForDisplayed(),
                    String.format("Page '%s' is not displayed", diskPage.getName())
            );
        });

        Allure.step("[API] Upload text file", () -> {
            ApiResponse<LinkResponse> textLinkResponse = diskApi.getUploadLink(textDiskPath);
            Assert.assertEquals(
                    textLinkResponse.getStatusCode(),
                    HttpStatus.SC_OK,
                    String.format("Failed to get upload link for text file. Expected status %d but got %d", HttpStatus.SC_OK, textLinkResponse.getStatusCode())
            );

            Response textUploadResponse = diskApi.uploadFile(textLinkResponse.getBody().getHref(), textFile);
            Assert.assertEquals(
                    textUploadResponse.statusCode(),
                    HttpStatus.SC_CREATED,
                    String.format("File upload failed. Expected status %d but got %d", HttpStatus.SC_CREATED, textUploadResponse.statusCode())
            );
        });

        Allure.step("[UI] Verify text file is displayed", () -> {
            Assert.assertTrue(
                    diskPage.isFileDisplayed(textFileName),
                    String.format("File '%s' is not displayed on page '%s'", textFileName, diskPage.getName())
            );
        });

        Allure.step("[UI] Verify text file content", () -> {
            diskPage.openFile(textFileName);
            AqualityServices.getBrowser().tabs().switchToLast();

            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(
                    textEditorPage.state().waitForDisplayed(),
                    String.format("Page '%s' is not displayed", textEditorPage.getName())
            );
            softAssert.assertEquals(
                    textEditorPage.getFileText(),
                    textContent,
                    String.format("Text content in the editor does not match the uploaded file on page '%s'", textEditorPage.getName())
            );
            softAssert.assertAll();

            AqualityServices.getBrowser().tabs().close();
            AqualityServices.getBrowser().tabs().switchToLast();
        });

        Allure.step("[API] Rename file to image", () -> {
            Response renameResponse = diskApi.renameFile(textDiskPath, imageDiskPath);
            Assert.assertEquals(
                    renameResponse.statusCode(),
                    HttpStatus.SC_CREATED,
                    String.format("File renaming failed. Expected status %d but got %d", HttpStatus.SC_CREATED, renameResponse.statusCode())
            );
        });


        Allure.step("[UI] Verify renamed file is displayed", () -> {
            Assert.assertTrue(
                    diskPage.isFileDisplayed(imageFileName),
                    String.format("File '%s' is not displayed on page '%s'", imageFileName, diskPage.getName())
            );
        });

        Allure.step("[API] Overwrite file with image", () -> {
            ApiResponse<LinkResponse> overwriteLinkResponse = diskApi.getUploadLink(imageDiskPath, true);
            Assert.assertEquals(
                    overwriteLinkResponse.getStatusCode(),
                    HttpStatus.SC_OK,
                    String.format("Failed to get overwrite upload link for image. Expected status %d but got %d", HttpStatus.SC_OK, overwriteLinkResponse.getStatusCode())
            );

            Response imageUploadResponse = diskApi.uploadFile(overwriteLinkResponse.getBody().getHref(), imageFile);
            Assert.assertEquals(
                    imageUploadResponse.statusCode(),
                    HttpStatus.SC_CREATED,
                    String.format("Image upload with overwrite failed. Expected status %d but got %d", HttpStatus.SC_CREATED, imageUploadResponse.statusCode())
            );

            AqualityServices.getLogger().info("Refreshing page to synchronize UI preview cache.");
            AqualityServices.getBrowser().refresh();
            Assert.assertTrue(diskPage.state().waitForDisplayed(),
                    String.format("Page '%s' is not displayed", diskPage.getName()));
        });

        Allure.step("[UI] Verify image preview", () -> {
            diskPage.openFile(imageFileName);
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(
                    imageViewerPage.state().waitForDisplayed(),
                    String.format("Page '%s' is not displayed", imageViewerPage.getName())
            );
            softAssert.assertTrue(
                    imageViewerPage.isImageDisplayed(),
                    String.format("Image preview is not displayed on page '%s'", imageViewerPage.getName())
            );
            softAssert.assertAll();

            imageViewerPage.clickCloseButton();
        });

        Allure.step("[UI] Move image to trash", () -> {
            diskPage.moveFileToTrash(imageFileName);

            Assert.assertTrue(
                    diskPage.isFileNotDisplayed(imageFileName),
                    String.format("File '%s' is still displayed on page '%s' after moving to trash", imageFileName, diskPage.getName())
            );
        });

        Allure.step("[API] Restore image from trash", () -> {
            boolean isRestored = AqualityServices.getConditionalWait().waitFor(() -> {
                Optional<String> trashPath = diskApi.findTrashPath(imageDiskPath);
                if (trashPath.isEmpty()) {
                    return false;
                }

                Response response = diskApi.restoreTrash(trashPath.get());
                return response.statusCode() == HttpStatus.SC_CREATED || response.statusCode() == HttpStatus.SC_ACCEPTED;
            });

            Assert.assertTrue(
                    isRestored,
                    String.format("Failed to restore file '%s' from trash within timeout", imageFileName)
            );
        });


        Allure.step("[UI] Verify restored image is displayed", () -> {
            Assert.assertTrue(
                    diskPage.isFileDisplayed(imageFileName),
                    String.format("File '%s' is not displayed on page '%s' after restoration", imageFileName, diskPage.getName())
            );
        });

        Allure.step("[API] Delete image permanently", () -> {
            Response deleteResponse = diskApi.deletePermanently(imageDiskPath);
            int deleteStatusCode = deleteResponse.statusCode();
            Assert.assertTrue(
                    deleteStatusCode == HttpStatus.SC_NO_CONTENT || deleteStatusCode == HttpStatus.SC_ACCEPTED,
                    String.format("File permanent deletion failed. Expected status 204 or 202 but got %d", deleteStatusCode)
            );
        });

        Allure.step("[UI] Verify image is removed", () -> {
            Assert.assertTrue(
                    diskPage.isFileNotDisplayed(imageFileName),
                    String.format("File '%s' is still displayed on page '%s' after deletion", imageFileName, diskPage.getName())
            );
        });
    }
}