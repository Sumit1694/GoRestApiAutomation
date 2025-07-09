package api.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import api.endpoints.UserEndPoints;
import api.payload.User;
import api.utilities.DataProviders;
import io.restassured.response.Response;

public class DDTTest {
	
	 public static List<Integer> createdUserIds = new ArrayList<>();

	@Test(priority=1, dataProvider="Data", dataProviderClass=DataProviders.class)
	public void testPostuser(String name,String email,String gender,String status)
	{
		email=email.replace("@", System.currentTimeMillis()+"@");
		
		User userPayload=new User();
		userPayload.setName(name);
		userPayload.setEmail(email);
		userPayload.setGender(gender);
		userPayload.setStatus(status);
		
		Response response=UserEndPoints.createUser(userPayload);
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 201);
		
		//Capture ID and store
		int id=response.jsonPath().getInt("id");
		createdUserIds.add(id);
	}
	
	@Test(priority=2,dependsOnMethods= {"testPostuser"})
	public void testDeleteUsers()
	{
		for(int id:createdUserIds)
		{
			Response response=UserEndPoints.deleteUser(id);
			response.then().log().all();
			Assert.assertEquals(response.getStatusCode(), 204);
		}
	}
	
	
	
}
