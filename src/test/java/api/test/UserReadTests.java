package api.test;

import static io.restassured.RestAssured.given;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import api.endpoints.UserEndPoints;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class UserReadTests extends baseTest {

	private Logger logger = LogManager.getLogger(UserReadTests.class);
	
	@Test(priority=1, dependsOnMethods = {"testPostUser"})
	public void testGetUserByName() {
		logger.info("******* Reading user info *********");

		Response response = UserEndPoints.readUser(baseTest.userPayload.getId());
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);

		logger.info("******* User info is displayed *********");
	}

	@Test(priority=2)
	public void testGetUserWithInvalidId()
	{
		logger.info("********** Trying to get user with invalid/non-existent ID ***********");
		int invalidUserId = 999999;

		Response response=UserEndPoints.readUser(invalidUserId);
		response.then().log().all();

		Assert.assertEquals(response.getStatusCode(), 404);

		String message = response.jsonPath().getString("message");
		Assert.assertEquals(message, "Resource not found");

		logger.info("********** Non-existent user fetch test passed ***********");

	}
	
	@Test(priority = 3)
	public void testGetUserWithInvalidIdFormat() {
	    logger.info("********** Trying to get user with invalid ID format ***********");

	    String invalidUserId = "YGf";  // invalid format (not a number)

	    Response response = given()
	            .accept(ContentType.JSON)
	            .header("Authorization", "Bearer ed282f22fcd005370921a4f1b965e7ef5580a269f8925ea1791dfaa1d5b0c74e")
	        .when()
	            .get("https://gorest.co.in/public/v2/users/" + invalidUserId);

	    response.then().log().all();

	    // Validate status code
	    Assert.assertEquals(response.getStatusCode(), 404, "Expected status code 404");

	    // Validate message
	    String message = response.jsonPath().getString("message");
	    Assert.assertEquals(message, "Resource not found");

	    logger.info("********** Invalid ID format fetch test passed ***********");
	}
	
}
