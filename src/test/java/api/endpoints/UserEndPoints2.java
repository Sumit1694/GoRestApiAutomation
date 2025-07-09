package api.endpoints;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

import java.util.ResourceBundle;

import api.payload.User;
import io.restassured.http.ContentType;

//UserEndPoints.java
//Create perform create,read,update,delete requests to the users API

public class UserEndPoints2 {

	static String token="Bearer ed282f22fcd005370921a4f1b965e7ef5580a269f8925ea1791dfaa1d5b0c74e";
	
	static ResourceBundle getURL()
	{
		ResourceBundle routes=ResourceBundle.getBundle("routes");
		return routes;
	}
	
	public static Response createUser(User payload)
	{
		String post_url=getURL().getString("post_url");
		
		Response response=given()	
			.header("Authorization",token)	
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(payload)
		.when()
		    .post(post_url);
		 
		    return response;
	}
	
	public static Response readUser(int id)
	{
		String get_url=getURL().getString("get_url");
		
		Response response=given()
				.header("Authorization",token)
				.pathParam("id", id)
				
				.when()
				   .get(get_url);
		
		return response;
	}
	
	public static Response updateUser(int id,User payload)
	{
		String update_url=getURL().getString("update_url");
		
		Response response=given()
			.header("Authorization", token)	
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.pathParam("id", id)
			.body(payload)
		.when()
		    .put(update_url);
		    
		    return response;
	}
	
	public static Response deleteUser(int id)
	{
		String delete_url=getURL().getString("delete_url");
		
		Response response=given()
				.header("Authorization", token)	
				.pathParam("id", id)
				
				.when()
				   .delete(delete_url);
		
		return response;
	}
}
