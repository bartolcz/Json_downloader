package json.service;

public enum EContentDownloadSection {
    ALL_POSTS("https://jsonplaceholder.typicode.com/posts");
    private String url;

    EContentDownloadSection(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
