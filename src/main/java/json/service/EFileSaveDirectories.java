package json.service;

public enum EFileSaveDirectories {
    POST_SAVE_DIR("posts");

    private String directory;

    EFileSaveDirectories(String path) {
        this.directory = path;
    }

    public String getPath() {
        return this.directory;
    }
}
