package api.endpoints;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import api.payload.User;
import io.restassured.http.ContentType;

//UserEndPoints.java
//Create perform create,read,update,delete requests to the users API

public class UserEndPoints {

	static String token="Bearer ed282f22fcd005370921a4f1b965e7ef5580a269f8925ea1791dfaa1d5b0c74e";
	
	public static Response createUser(User payload)
	{
		Response response=given()	
			.header("Authorization",token)	
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(payload)
		.when()
		    .post(Routes.post_url);
		 
		    return response;
	}
	
	public static Response readUser(int id)
	{
		Response response=given()
				.header("Authorization",token)
				.pathParam("id", id)
				
				.when()
				   .get(Routes.get_url);
		
		return response;
	}
	
	public static Response updateUser(int id,User payload)
	{
		Response response=given()
			.header("Authorization", token)	
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.pathParam("id", id)
			.body(payload)
		.when()
		    .put(Routes.put_url);
		    
		    return response;
	}
	
	public static Response deleteUser(int id)
	{
		Response response=given()
				.header("Authorization", token)	
				.pathParam("id", id)
				
				.when()
				   .delete(Routes.delete_url);
		
		return response;
	}
}
