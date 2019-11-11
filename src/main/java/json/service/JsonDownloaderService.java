package json.service;

import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonDownloaderService {

    public EJsonOperationStatus downloadAndSavePosts(DownloadConfig downloadConfig) {
        try {
            checkInitialParameters(downloadConfig);
            JsonArray jsonData = downloadDataFromUrl(downloadConfig);
            String path = preparePathAndDirectory(downloadConfig.getFileSaveDirectory());
            checkProcessedData(jsonData, path);
            int savedFiles = saveJsonArrayToFileSystem(jsonData, path, downloadConfig.overrideFiles());
            System.out.println("saved/overwritten " + savedFiles + " files ");
            return savedFiles > 0 ? EJsonOperationStatus.SUCCESS : EJsonOperationStatus.SUCCESS_NO_CHANGES;

        } catch (RestClientException e) {
            e.printStackTrace();
            return EJsonOperationStatus.REST_CLIENT_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            return EJsonOperationStatus.IO_ERROR;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return EJsonOperationStatus.INVALID_ARGUMENT;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return EJsonOperationStatus.INVALID_CONFIGURATION;
        } catch (SecurityException e) {
            e.printStackTrace();
            return EJsonOperationStatus.DICTIONARY_CREATION_PROBLEM;
        } catch (Exception e) {
            e.printStackTrace();
            return EJsonOperationStatus.UNKNOWN_ERROR;
        }
    }

    private String preparePathAndDirectory(EFileSaveDirectories fileSaveDirectory) throws IOException {
        Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
        String finalPath = absolutePath + File.separator + fileSaveDirectory.getPath();
        if (StringUtils.isNotBlank(finalPath)) {
            File directory = new File(absolutePath + File.separator + fileSaveDirectory.getPath());
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    throw new IOException("couldn't create directory ");
                }
            }
        }
        return finalPath;
    }

    private void checkProcessedData(JsonArray jsonDAta, String path) {
        if (jsonDAta == null) {
            throw new NullPointerException("JSONData is null ");
        }
        if (StringUtils.isBlank(path)) {
            throw new NullPointerException("save path is null");
        }
    }


    private void checkInitialParameters(DownloadConfig downloadConfig) {
        if (downloadConfig == null) {
            throw new NullPointerException("Config file is null ");
        }
        if (downloadConfig.getFileSaveDirectory() == null) {
            throw new IllegalArgumentException("missing save file directory for config");
        }
        if (StringUtils.isBlank(downloadConfig.getFileSaveDirectory().getPath())) {
            throw new IllegalArgumentException("missing save file directory for config ");
        }
        if (downloadConfig.getContent() == null) {
            throw new IllegalArgumentException("missing content url");
        }
        if (StringUtils.isBlank(downloadConfig.getFileSaveDirectory().getPath())) {
            throw new IllegalArgumentException("missing content url ");
        }

    }


    private JsonArray downloadDataFromUrl(DownloadConfig downloadConfig) {
        String connectionUrl = downloadConfig.getContent().getUrl();
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(connectionUrl, String.class);

        return JsonParser.parseString(result).getAsJsonArray();
    }

    private int saveJsonArrayToFileSystem(JsonArray jsonDAta, String directoryPath, boolean overrideFiles) throws IOException {
        int saveResult = 0;
        Gson prettifies = new GsonBuilder().setPrettyPrinting().create();
        for (JsonElement jsonElement : jsonDAta) {
            Pair<String, String> saveData = prepareDataToSave(prettifies, jsonElement);
            if (StringUtils.isNotBlank(saveData.getKey()) && StringUtils.isNotBlank(saveData.getValue())) {
                String finalPath = prepareFinalPath(saveData, directoryPath);
                if (checkIfFileExistsWithOverride(overrideFiles, finalPath)) {
                    saveJsonFile(saveData, finalPath);
                    saveResult++;
                }
            }
        }
        return saveResult;
    }

    private boolean checkIfFileExistsWithOverride(boolean overrideFiles, String finalPath) {
        if (!overrideFiles) {
            if (new File(finalPath).exists()) {
                System.out.println("File with path " + finalPath + " already exists");
                return false;
            }
        }
        return true;
    }

    private String prepareFinalPath(Pair<String, String> saveData, String directoryPath) {
        StringBuilder finalPath = new StringBuilder();
        return finalPath.append(directoryPath).append(File.separator).append(saveData.getKey()).append(".json").toString();
    }

    private Pair<String, String> prepareDataToSave(Gson gson, JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return Pair.of(jsonObject.get("id").toString(), gson.toJson(jsonElement));
    }


    private void saveJsonFile(Pair<String, String> saveData, String finalPath) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(finalPath))) {
            writer.write(saveData.getRight());
            System.out.println("saved data to a file: " + finalPath);
        }
    }
}
