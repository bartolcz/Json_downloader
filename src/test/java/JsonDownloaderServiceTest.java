import json.service.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonDownloaderServiceTest {
    private JsonDownloaderService jsonDownloaderService;

    @BeforeAll
    static void setup() {
        System.out.println("Starting json.service.JsonDownloaderService tests");
    }

    @BeforeEach
    private void init() {
        jsonDownloaderService = new JsonDownloaderService();
    }

    @Test
    void downloadAndSaveProcessSuccessful() throws Exception {
        DownloadConfig downloadConfig = new DownloadConfig(EContentDownloadSection.ALL_POSTS, EFileSaveDirectories.POST_SAVE_DIR, true);
        assertEquals(EJsonOperationStatus.SUCCESS, jsonDownloaderService.downloadAndSavePosts(downloadConfig));
    }

    @Test
    void existsConfigFile() {
        String test = null;
        assertEquals(EJsonOperationStatus.INVALID_ARGUMENT, jsonDownloaderService.downloadAndSavePosts(null));
    }

    @Test
    void noChangesAfterDuplicatedCall() {
        DownloadConfig downloadConfig = new DownloadConfig(EContentDownloadSection.ALL_POSTS, EFileSaveDirectories.POST_SAVE_DIR, true);
        assertEquals(EJsonOperationStatus.SUCCESS, jsonDownloaderService.downloadAndSavePosts(downloadConfig));
        downloadConfig = new DownloadConfig(EContentDownloadSection.ALL_POSTS, EFileSaveDirectories.POST_SAVE_DIR, false);
        assertEquals(EJsonOperationStatus.SUCCESS_NO_CHANGES, jsonDownloaderService.downloadAndSavePosts(downloadConfig));
    }

    @Test
    void missingDirectoryPath() throws Exception {
        DownloadConfig downloadConfig = new DownloadConfig(EContentDownloadSection.ALL_POSTS, null, true);
        assertEquals(EJsonOperationStatus.INVALID_CONFIGURATION, jsonDownloaderService.downloadAndSavePosts(downloadConfig));
    }

    @Test
    void missingUrl() throws Exception {
        DownloadConfig downloadConfig = new DownloadConfig(null, EFileSaveDirectories.POST_SAVE_DIR, true);
        assertEquals(EJsonOperationStatus.INVALID_CONFIGURATION, jsonDownloaderService.downloadAndSavePosts(downloadConfig));
    }


    @Test
    void checkIfCreatesFolderWithContent() throws Exception {
        Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
        File directory = new File(absolutePath + File.separator + EFileSaveDirectories.POST_SAVE_DIR.getPath());
        if (directory.exists()) {
            FileUtils.deleteDirectory(directory);
        }
        DownloadConfig downloadConfig = new DownloadConfig(EContentDownloadSection.ALL_POSTS, EFileSaveDirectories.POST_SAVE_DIR, true);
        assertEquals(EJsonOperationStatus.SUCCESS, jsonDownloaderService.downloadAndSavePosts(downloadConfig));

        assertTrue(directory.exists());
        assertTrue(FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size() > 0);

    }
}