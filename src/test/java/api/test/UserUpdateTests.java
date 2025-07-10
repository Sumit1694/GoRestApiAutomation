package api.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import api.endpoints.UserEndPoints;
import api.payload.User;
import io.restassured.response.Response;

public class UserUpdateTests extends baseTest {

    @Test(priority = 1)
    public void testUpdateUserNameAndStatus() {
        logger.info("********** Updating user name and status ***********");

        String updatedName = faker.name().fullName();
        String updatedStatus = "inactive";

        userPayload.setName(updatedName);
        userPayload.setStatus(updatedStatus);

        Response response = UserEndPoints.updateUser(userPayload.getId(), userPayload);
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        Assert.assertEquals(response.jsonPath().getString("name"), updatedName);
        Assert.assertEquals(response.jsonPath().getString("status"), updatedStatus);

        logger.info("********** User name and status update test passed ***********");
    }

    @Test(priority = 2)
    public void testUpdateUserWithInvalidID() {
        logger.info("********** Trying to update user with invalid ID ***********");

        int invalidUserId = 99999999;

        User invalidUser = new User();
        invalidUser.setName(faker.name().fullName());
        invalidUser.setEmail("user" + System.currentTimeMillis() + "@example.com");
        invalidUser.setGender("male");
        invalidUser.setStatus("active");

        Response response = UserEndPoints.updateUser(invalidUserId, invalidUser);
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");

        logger.info("********** Update with invalid ID test passed ***********");
    }
}
