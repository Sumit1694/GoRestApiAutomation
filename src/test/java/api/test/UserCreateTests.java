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

public class UserCreateTests extends baseTest {
	
	 private Logger logger = LogManager.getLogger(UserCreateTests.class);

	 @Test(priority=1)
	 public void testPostUser() {
	     logger.info("********** Creating user with valid details ***********");

	     User newUser = new User();
	     newUser.setName(faker.name().fullName());
	     newUser.setEmail("user" + System.currentTimeMillis() + "@example.com");
	     newUser.setGender("male");
	     newUser.setStatus("active");

	     Response response = UserEndPoints.createUser(newUser);
	     response.then().log().all();

	     Assert.assertEquals(response.getStatusCode(), 201);
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

		//Validate erro msg from response
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

		//Validate erro msg from response
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

		//Validate erro msg from response
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
	
	@Test(priority = 6)
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
