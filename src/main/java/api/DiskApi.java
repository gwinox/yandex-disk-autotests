package api;

import aquality.selenium.browser.AqualityServices;
import constants.ApiConstants;
import enums.HttpHeader;
import enums.QueryParam;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.LinkResponse;
import models.TrashEmbedded;
import models.TrashItem;
import models.TrashResourceList;
import utils.ConfigManager;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DiskApi extends BaseApi {

    private static final int TRASH_LIMIT = 100;
    private static final String TRASH_ROOT_PATH = "/";
    private static final String TRASH_SORT_PARAM = "-deleted";
    private static final String TRASH_PREFIX_MASK = "^trash:";

    public DiskApi() {
        super(
                ConfigManager.getBaseUrlApi(),
                Map.of(
                        HttpHeader.AUTHORIZATION.getName(),
                        String.format(ApiConstants.OAUTH_TOKEN_TEMPLATE, ConfigManager.getToken())
                )
        );
    }

    @Step("Get upload link for {filePath}")
    public ApiResponse<LinkResponse> getUploadLink(String filePath) {
        return get(Endpoints.UPLOAD, Map.of(QueryParam.PATH.getValue(), filePath), LinkResponse.class);
    }

    @Step("Get upload link for {filePath} with overwrite = {overwrite}")
    public ApiResponse<LinkResponse> getUploadLink(String filePath, boolean overwrite) {
        return get(
                Endpoints.UPLOAD,
                Map.of(
                        QueryParam.PATH.getValue(), filePath,
                        QueryParam.OVERWRITE.getValue(), overwrite
                ),
                LinkResponse.class
        );
    }

    @Step("Upload file {file}")
    public Response uploadFile(String uploadUrl, File file) {
        return putFile(uploadUrl, file);
    }

    @Step("Rename {currentPath} to {newPath}")
    public Response renameFile(String currentPath, String newPath) {
        return post(
                Endpoints.MOVE,
                Map.of(
                        QueryParam.FROM.getValue(), currentPath,
                        QueryParam.PATH.getValue(), newPath
                )
        );
    }

    @Step("Get trash list")
    public ApiResponse<TrashResourceList> getTrashList() {
        Map<String, Object> queryParams = Map.of(
                QueryParam.PATH.getValue(), TRASH_ROOT_PATH,
                QueryParam.LIMIT.getValue(), TRASH_LIMIT,
                QueryParam.SORT.getValue(), TRASH_SORT_PARAM
        );
        return get(Endpoints.TRASH, queryParams, TrashResourceList.class);
    }

    @Step("Find {originPath} in trash")
    public Optional<String> findTrashPath(String originPath) {
        ApiResponse<TrashResourceList> response = getTrashList();
        List<TrashItem> items = Optional.ofNullable(response.getBody())
                .map(TrashResourceList::getEmbedded)
                .map(TrashEmbedded::getItems)
                .orElse(Collections.emptyList());

        return items.stream()
                .filter(item -> originPath.equals(item.getOriginPath()))
                .map(TrashItem::getPath)
                .map(path -> path.replaceFirst(TRASH_PREFIX_MASK, ""))
                .findFirst();
    }

    @Step("Restore {path} from trash")
    public Response restoreTrash(String path) {
        return put(Endpoints.RESTORE_TRASH, Map.of(QueryParam.PATH.getValue(), path));
    }

    @Step("Delete {filePath} permanently")
    public Response deletePermanently(String filePath) {
        delete(Endpoints.RESOURCES, Map.of(QueryParam.PATH.getValue(), filePath));

        AqualityServices.getConditionalWait().waitFor(() -> findTrashPath(filePath).isPresent());
        Optional<String> trashPath = findTrashPath(filePath);

        if (trashPath.isPresent()) {
            return delete(Endpoints.TRASH, Map.of(QueryParam.PATH.getValue(), trashPath.get()));
        }

        throw new RuntimeException(String.format("Failed to locate file '%s' in trash for permanent deletion", filePath));
    }
}