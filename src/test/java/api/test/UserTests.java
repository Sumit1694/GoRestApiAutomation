package api.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import api.endpoints.UserEndPoints;
import io.restassured.response.Response;

public class UserTests extends baseTest {
	
	private Logger logger = LogManager.getLogger(UserTests.class);

    @Test(priority=1)
    public void testPostUser() {
        logger.info("********** Creating user   ***********");

        Response response = UserEndPoints.createUser(userPayload);
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 201);
        logger.info("********** user is created  ***********");

        // Save created user ID for next tests
        int userId = response.jsonPath().getInt("id");
        userPayload.setId(userId);
        System.out.println("Created User ID: " + userId);
    }

    @Test(priority=2, dependsOnMethods = {"testPostUser"})
    public void testGetUserByName() {
        logger.info("******* Reading user info *********");

        Response response = UserEndPoints.readUser(baseTest.userPayload.getId());
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);

        logger.info("******* User info is displayed *********");
    }

    @Test(priority=3, dependsOnMethods = {"testPostUser"})
    public void testUpdateUserByName() {
        logger.info("******* Update user info *********");

        userPayload.setName(faker.name().fullName());
        userPayload.setEmail("updated" + System.currentTimeMillis() + "@mail.com");
        userPayload.setGender("female");
        userPayload.setStatus("inactive");

        Response response = UserEndPoints.updateUser(baseTest.userPayload.getId(), userPayload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);

        logger.info("******* User info updated *********");
    }

    @Test(priority=4, dependsOnMethods = {"testPostUser"})
    public void testDeleteUser() {
        logger.info("********User deleting**********");

        Response response = UserEndPoints.deleteUser(baseTest.userPayload.getId());
        Assert.assertEquals(response.getStatusCode(), 204);

        logger.info("********User is deleted**********");
    }
}