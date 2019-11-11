package json.service;

public class DownloadConfig {

    private EContentDownloadSection content;
    private EFileSaveDirectories fileSaveDirectory;
    private boolean overrideFiles;

    public DownloadConfig(EContentDownloadSection content, EFileSaveDirectories fileSaveDirectory, boolean overrideFiles) {
        this.content = content;
        this.fileSaveDirectory = fileSaveDirectory;
        this.overrideFiles = overrideFiles;
    }

    public EContentDownloadSection getContent() {
        return this.content;
    }

    public EFileSaveDirectories getFileSaveDirectory() {
        return this.fileSaveDirectory;
    }

    public boolean overrideFiles() {
        return this.overrideFiles;
    }
}
