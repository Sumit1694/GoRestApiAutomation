package api.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeSuite;

import com.github.javafaker.Faker;

import api.endpoints.UserEndPoints;
import api.payload.User;
import io.restassured.response.Response;

public class baseTest {

    public static Faker faker;
    public static User userPayload;
    public static int createdUserId;
    public static Logger logger;

    @BeforeSuite
    public void globalSetup() {
        logger = LogManager.getLogger(this.getClass());

        faker = new Faker();
        userPayload = new User();

        userPayload.setName(faker.name().fullName());
        userPayload.setEmail("user" + System.currentTimeMillis() + "@example.com");
        userPayload.setGender("male");
        userPayload.setStatus("active");

        logger.info("==== Creating user before all tests ====");
        Response response = UserEndPoints.createUser(userPayload);
        response.then().log().all();

        int userId = response.jsonPath().getInt("id");
        userPayload.setId(userId);
        createdUserId = userId;

        logger.info("==== User created successfully with ID: " + createdUserId + " ====");
    }
}