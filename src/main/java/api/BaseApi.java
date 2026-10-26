package api;

import aquality.selenium.browser.AqualityServices;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.util.List;
import java.util.Map;

public abstract class BaseApi {

    protected final RequestSpecification baseSpec;

    protected BaseApi(String baseUrl, Map<String, String> headers) {
        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured());

        if (headers != null) {
            headers.forEach(specBuilder::addHeader);
        }

        this.baseSpec = specBuilder.build();
    }

    protected <T> ApiResponse<T> get(String endpoint, Class<T> responseClass) {
        AqualityServices.getLogger().info(String.format("Sending GET request to endpoint: '%s'", endpoint));
        Response response = RestAssured.given()
                .spec(baseSpec)
                .get(endpoint);
        AqualityServices.getLogger().info(String.format("Received response with status code: %s", response.statusCode()));

        return new ApiResponse<>(response.statusCode(), response.contentType(), response.as(responseClass));
    }

    protected <T> ApiResponse<T> get(String endpoint, Map<String, Object> queryParams, Class<T> responseClass) {
        AqualityServices.getLogger().info(String.format("Sending GET request to endpoint: '%s' with params: %s", endpoint, queryParams));
        Response response = RestAssured.given()
                .spec(baseSpec)
                .queryParams(queryParams)
                .get(endpoint);
        AqualityServices.getLogger().info(String.format("Received response with status code: %s", response.statusCode()));

        return new ApiResponse<>(response.statusCode(), response.contentType(), response.as(responseClass));
    }

    protected <T> ApiResponse<List<T>> getList(String endpoint, Class<T> responseClass) {
        AqualityServices.getLogger().info(String.format("Sending GET request to endpoint: '%s' to retrieve list", endpoint));
        Response response = RestAssured.given()
                .spec(baseSpec)
                .get(endpoint);
        AqualityServices.getLogger().info(String.format("Received response with status code: %s", response.statusCode()));

        List<T> body = response.jsonPath().getList("", responseClass);
        return new ApiResponse<>(response.statusCode(), response.contentType(), body);
    }

    protected <T> ApiResponse<T> post(String endpoint, Object requestBody, Class<T> responseClass) {
        AqualityServices.getLogger().info(String.format("Sending POST request to endpoint: '%s'", endpoint));
        Response response = RestAssured.given()
                .spec(baseSpec)
                .body(requestBody)
                .post(endpoint);
        AqualityServices.getLogger().info(String.format("Received response with status code: %s", response.statusCode()));

        return new ApiResponse<>(response.statusCode(), response.contentType(), response.as(responseClass));
    }

    protected Response post(String endpoint) {
        AqualityServices.getLogger().info(String.format("Sending POST request without body to endpoint: '%s'", endpoint));
        Response response = RestAssured.given()
                .spec(baseSpec)
                .post(endpoint);
        AqualityServices.getLogger().info(String.format("Received response with status code: %s", response.statusCode()));

        return response;
    }

    protected Response post(String endpoint, Map<String, Object> queryParams) {
        AqualityServices.getLogger().info(String.format("Sending POST request to endpoint: '%s' with params: %s", endpoint, queryParams));
        Response response = RestAssured.given()
                .spec(baseSpec)
                .queryParams(queryParams)
                .post(endpoint);
        AqualityServices.getLogger().info(String.format("Received response with status code: %s", response.statusCode()));

        return response;
    }

    protected Response delete(String endpoint) {
        AqualityServices.getLogger().info(String.format("Sending DELETE request to endpoint: '%s'", endpoint));
        Response response = RestAssured.given()
                .spec(baseSpec)
                .delete(endpoint);
        AqualityServices.getLogger().info(String.format("Received response with status code: %s", response.statusCode()));

        return response;
    }

    protected Response delete(String endpoint, Map<String, Object> queryParams) {
        AqualityServices.getLogger().info(String.format("Sending DELETE request to endpoint: '%s' with params: %s", endpoint, queryParams));
        Response response = RestAssured.given()
                .spec(baseSpec)
                .queryParams(queryParams)
                .delete(endpoint);
        AqualityServices.getLogger().info(String.format("Received response with status code: %s", response.statusCode()));

        return response;
    }

    protected Response put(String endpoint) {
        AqualityServices.getLogger().info(String.format("Sending PUT request to endpoint: '%s'", endpoint));
        Response response = RestAssured.given()
                .spec(baseSpec)
                .put(endpoint);
        AqualityServices.getLogger().info(String.format("Received response with status code: %s", response.statusCode()));

        return response;
    }

    protected Response put(String endpoint, Map<String, Object> queryParams) {
        AqualityServices.getLogger().info(String.format("Sending PUT request to endpoint: '%s' with params: %s", endpoint, queryParams));
        Response response = RestAssured.given()
                .spec(baseSpec)
                .queryParams(queryParams)
                .put(endpoint);
        AqualityServices.getLogger().info(String.format("Received response with status code: %s", response.statusCode()));

        return response;
    }

    protected Response putFile(String uploadEndpoint, File file) {
        AqualityServices.getLogger().info(String.format("Sending PUT request to upload file to URL: '%s'", uploadEndpoint));
        Response response = RestAssured.given()
                .filter(new AllureRestAssured())
                .contentType("application/octet-stream")
                .body(file)
                .put(uploadEndpoint);
        AqualityServices.getLogger().info(String.format("Received response with status code: %s", response.statusCode()));

        return response;
    }
}