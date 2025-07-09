package api.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.github.javafaker.Faker;
import api.endpoints.UserEndPoints;
import api.endpoints.UserEndPoints2;
import api.payload.User;
import io.restassured.response.Response;

public class UserTests2 {

	Faker faker;
	User userPayload;
	
	public Logger logger;

	@BeforeClass
	public void setupData()
	{
		faker=new Faker();
		userPayload=new User();

		userPayload.setName(faker.name().fullName());
		userPayload.setGender("male"); // Must be "male" or "female"
		userPayload.setEmail(faker.internet().safeEmailAddress());
		userPayload.setStatus("active"); // Must be "active" or "inactive"
	
	    logger=LogManager.getLogger(this.getClass());
	}

	@Test(priority=1)
	public void testPostUser()
	{	
		
		logger.info("********** Creating user   ***********");
		Response response=UserEndPoints2.createUser(userPayload);
		response.then().log().all();

		Assert.assertEquals(response.getStatusCode(), 201);
		
		logger.info("********** user is created  ***********");

		int userId=response.jsonPath().getInt("id");
		userPayload.setId(userId);
		System.out.println("Created User ID: " + userId);
	}

	@Test(priority=2)
	public void testGetUserByName()
	{
		logger.info("******* Reading user info *********");
		
		Response response=UserEndPoints2.readUser(this.userPayload.getId());
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);
		
		logger.info("******* User info is displayed *********");
	}

	@Test(priority=3)
	public void testUpdateUserByName()
	{
		logger.info("******* Update user info *********");
		
		userPayload.setName(faker.name().fullName());
		userPayload.setGender("female"); 
		userPayload.setEmail(faker.internet().safeEmailAddress());
		userPayload.setStatus("inactive"); 
		
		logger.info("******* user info updated *********");

		Response response=UserEndPoints2.updateUser(this.userPayload.getId(), userPayload);
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);
	}
	
	@Test(priority=4)
	public void testDeleteUser()
	{
		logger.info("********User deleting**********");
		Response response=UserEndPoints2.deleteUser(this.userPayload.getId());
		Assert.assertEquals(response.getStatusCode(), 204);
		logger.info("********User is deleted**********");
	}
}
