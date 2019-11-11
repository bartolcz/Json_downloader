package core;

import json.service.*;

public class Core {
    public static void main(String[] argc) {
        try {
            performPostDownload(new DownloadConfig(EContentDownloadSection.ALL_POSTS, EFileSaveDirectories.POST_SAVE_DIR, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void performPostDownload(DownloadConfig downloadConfig) {
        JsonDownloaderService jsonDownloaderService = new JsonDownloaderService();
        EJsonOperationStatus status = jsonDownloaderService.downloadAndSavePosts(downloadConfig);
        System.out.println("operation status: " + status.toString());
    }

}
