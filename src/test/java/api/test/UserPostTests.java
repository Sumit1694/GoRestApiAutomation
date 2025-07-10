package api.test;

import static io.restassured.RestAssured.given;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import api.endpoints.UserEndPoints;
import api.payload.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class UserPostTests extends baseTest {

	private Logger logger = LogManager.getLogger(UserPostTests.class);
	
	@Test(priority=1)
	public void testPostUser() {
		logger.info("********** Creating user with valid details  ***********");

		Response response = UserEndPoints.createUser(userPayload);
		response.then().log().all();

		Assert.assertEquals(response.getStatusCode(), 201);
		logger.info("********** user is created  ***********");

		// Save created user ID for next tests
		int userId = response.jsonPath().getInt("id");
		userPayload.setId(userId);
		System.out.println("Created User ID: " + userId);
	}

	@Test(priority=2)
	public void testCreateUserWithDuplicateEmail()
	{
		logger.info("********** Creating user with duplicate email ***********");

		//Use same email as the first user
		User duplicateUser=new User();
		duplicateUser.setName(faker.name().fullName());
		duplicateUser.setEmail(userPayload.getEmail());
		duplicateUser.setGender("female");
		duplicateUser.setStatus("active");

		Response response = UserEndPoints.createUser(duplicateUser);
		response.then().log().all();

		// Expecting HTTP 422 due to duplicate email
		Assert.assertEquals(response.getStatusCode(), 422);

		//Validate error message from response
		String field = response.jsonPath().getString("field[0]");
		String message=response.jsonPath().getString("message[0]");

		Assert.assertEquals(field, "email");
		Assert.assertEquals(message, "has already been taken");
		logger.info("********** Duplicate email creation test passed ***********");
	}

	@Test(priority=3)
	public void testCreateUserWithMissingFields()
	{
		logger.info("********** Creating user with missing fields ***********");

		//Use same email as the first user
		User UserwithStatusMissing=new User();
		UserwithStatusMissing.setName(faker.name().fullName());
		UserwithStatusMissing.setEmail(faker.internet().emailAddress());
		UserwithStatusMissing.setGender("female");
		UserwithStatusMissing.setStatus("");

		Response response = UserEndPoints.createUser(UserwithStatusMissing);
		response.then().log().all();

		// Expecting HTTP 422 due to duplicate email
		Assert.assertEquals(response.getStatusCode(), 422);

		//Validate error message from response
		String field = response.jsonPath().getString("field[0]");
		String message=response.jsonPath().getString("message[0]");

		Assert.assertEquals(field, "status");
		Assert.assertEquals(message, "can't be blank");
		logger.info("********** Missing field creation test passed ***********");
	}

	@Test(priority=4)
	public void testCreateUserWithInvalidEmailFormat()
	{
		logger.info("********** Creating user with invalid email ***********");

		//Use same email as the first user
		User InvalidEmail=new User();
		InvalidEmail.setName(faker.name().fullName());
		InvalidEmail.setEmail("invalidEmail");
		InvalidEmail.setGender("female");
		InvalidEmail.setStatus("Inactive");

		Response response = UserEndPoints.createUser(InvalidEmail);
		response.then().log().all();

		// Expecting HTTP 422 due to duplicate email
		Assert.assertEquals(response.getStatusCode(), 422);

		//Validate error message from response
		String field = response.jsonPath().getString("field[0]");
		String message=response.jsonPath().getString("message[0]");

		Assert.assertEquals(field, "email");
		Assert.assertEquals(message, "is invalid");
		logger.info("********** Missing field creation test passed ***********");
	}

	@Test(priority = 5)
	public void testCreateUserWithoutAuthToken() {
		logger.info("********** Attempting to create user without auth token ***********");

		// Prepare user data
		User noAuthUser = new User();
		noAuthUser.setName(faker.name().fullName());
		noAuthUser.setEmail("user" + System.currentTimeMillis() + "@example.com");
		noAuthUser.setGender("male");
		noAuthUser.setStatus("active");

		// Send request WITHOUT token (don't use UserEndPoints.createUser)
		Response response = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(noAuthUser)
				.when()
				.post("https://gorest.co.in/public/v2/users");  // Direct URL call

		response.then().log().all();

		// Validate response
		Assert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");

		String message = response.jsonPath().getString("message");
		Assert.assertTrue(message.toLowerCase().contains("authentication") || message.toLowerCase().contains("unauthorized"));

		logger.info("********** Authorization check without token passed ***********");
	}

	@Test(priority=6, dependsOnMethods = {"testPostUser"})
	public void testGetUserByName() {
		logger.info("******* Reading user info *********");

		Response response = UserEndPoints.readUser(baseTest.userPayload.getId());
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);

		logger.info("******* User info is displayed *********");
	}

	@Test(priority=7)
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
	
	@Test(priority = 7)
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
	
	@Test(priority = 8)
	public void testUpdateUserNameAndStatus() {
	    logger.info("********** Updating user name and status ***********");

	    // Update the userPayload with new values
	    String updatedName = faker.name().fullName();
	    String updatedStatus = "inactive";

	    userPayload.setName(updatedName);
	    userPayload.setStatus(updatedStatus);

	    // Call update API
	    Response response = UserEndPoints.updateUser(userPayload.getId(), userPayload);
	    response.then().log().all();

	    // Validations
	    Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200");
	    Assert.assertEquals(response.jsonPath().getString("name"), updatedName);
	    Assert.assertEquals(response.jsonPath().getString("status"), updatedStatus);

	    logger.info("********** User name and status update test passed ***********");
	}

	@Test(priority = 9)
	public void testUpdateUserWithInvalidID() {
	    logger.info("********** Trying to update user with invalid ID ***********");

	    int invalidUserId = 99999999;  // A user ID that likely does not exist

	    // Prepare dummy update data
	    User invalidUserPayload = new User();
	    invalidUserPayload.setName(faker.name().fullName());
	    invalidUserPayload.setEmail("user" + System.currentTimeMillis() + "@example.com");
	    invalidUserPayload.setGender("male");
	    invalidUserPayload.setStatus("active");

	    // Send update request
	    Response response = UserEndPoints.updateUser(invalidUserId, invalidUserPayload);
	    response.then().log().all();

	    // Validate expected failure (404 Not Found)
	    Assert.assertEquals(response.getStatusCode(), 404);
	    Assert.assertEquals(response.jsonPath().getString("message"), "Resource not found");

	    logger.info("********** Update user with invalid ID test passed ***********");
	}
	
	@Test(priority = 10, dependsOnMethods = {"testPostUser"})
	public void testDeleteUserWithValidId() {
	    logger.info("********** Deleting user with valid ID ***********");

	    int userId = userPayload.getId(); // already set from testPostUser

	    Response response = UserEndPoints.deleteUser(userId);
	    response.then().log().all();

	    // Validate status code
	    Assert.assertEquals(response.getStatusCode(), 204, "Expected status code 204 - No Content");

	    // Validate response body is empty
	    Assert.assertTrue(response.getBody().asString().isEmpty(), "Expected empty response body");

	    logger.info("********** User deleted successfully ***********");
	}
	
	@Test(priority = 11, dependsOnMethods = {"testDeleteUserWithValidId"})
	public void testDeleteAlreadyDeletedUser() {
	    logger.info("********** Trying to delete an already deleted user ***********");

	    int deletedUserId = userPayload.getId(); // already deleted in previous test

	    Response response = UserEndPoints.deleteUser(deletedUserId);
	    response.then().log().all();

	    // Validate status code
	    Assert.assertEquals(response.getStatusCode(), 404, "Expected status code 404 - Not Found");

	    // Validate error message
	    String message = response.jsonPath().getString("message");
	    Assert.assertEquals(message, "Resource not found");

	    logger.info("********** Delete already deleted user test passed ***********");
	}

	@Test(priority = 12)
	public void testDeleteUserWithInvalidId() {
	    logger.info("********** Trying to delete user with invalid ID ***********");

	    int invalidUserId = 99999999; // Assumed to not exist

	    Response response = UserEndPoints.deleteUser(invalidUserId);
	    response.then().log().all();

	    // Validate status code
	    Assert.assertEquals(response.getStatusCode(), 404, "Expected status code 404 - Not Found");

	    // Validate message
	    String message = response.jsonPath().getString("message");
	    Assert.assertEquals(message, "Resource not found");

	    logger.info("********** Delete user with invalid ID test passed ***********");
	}
	
	@Test(priority = 13)
	public void testCreateUserWithLongName() {
	    logger.info("********** Creating user with a very long name (>255 chars) ***********");

	    // Generate a long name (e.g., 300 characters)
	    StringBuilder longName = new StringBuilder();
	    for (int i = 0; i < 300; i++) {
	        longName.append("a");
	    }

	    User longNameUser = new User();
	    longNameUser.setName(longName.toString());
	    longNameUser.setEmail("user" + System.currentTimeMillis() + "@example.com");
	    longNameUser.setGender("male");
	    longNameUser.setStatus("active");

	    Response response = UserEndPoints.createUser(longNameUser);
	    response.then().log().all();

	    // Validate status code
	    Assert.assertEquals(response.getStatusCode(), 422, "Expected 422 Unprocessable Entity");

	    // Validate response field and message
	    String field = response.jsonPath().getString("field[0]");
	    String message = response.jsonPath().getString("message[0]");

	    Assert.assertEquals(field, "name");
	    Assert.assertTrue(message.toLowerCase().contains("too long") || message.toLowerCase().contains("maximum"), 
	                      "Expected validation error for name length");

	    logger.info("********** Long name validation test passed ***********");
	}
}


