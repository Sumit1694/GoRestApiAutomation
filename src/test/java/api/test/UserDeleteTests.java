package api.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import api.endpoints.UserEndPoints;
import io.restassured.response.Response;

public class UserDeleteTests extends baseTest {

    @Test(priority = 1)
    public void testDeleteUserWithValidId() {
        logger.info("********** Deleting user with valid ID ***********");

        int userId = userPayload.getId();

        Response response = UserEndPoints.deleteUser(userId);
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 204, "Expected status code 204 - No Content");
        Assert.assertTrue(response.getBody().asString().isEmpty(), "Expected empty response body");

        logger.info("********** User deleted successfully ***********");
    }

    @Test(priority = 2)
    public void testDeleteAlreadyDeletedUser() {
        logger.info("********** Trying to delete an already deleted user ***********");

        int userId = userPayload.getId(); // same ID used above

        Response response = UserEndPoints.deleteUser(userId);
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 404, "Expected status code 404 - Not Found");
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");

        logger.info("********** Delete already deleted user test passed ***********");
    }

    @Test(priority = 3)
    public void testDeleteUserWithInvalidId() {
        logger.info("********** Trying to delete user with invalid ID ***********");

        int invalidUserId = 99999999;

        Response response = UserEndPoints.deleteUser(invalidUserId);
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 404, "Expected status code 404 - Not Found");
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");

        logger.info("********** Delete user with invalid ID test passed ***********");
    }
}
