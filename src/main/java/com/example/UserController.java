package com.example;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/userController")
public class UserController {

	private Map<String, User> users;
	 
	public UserController(){
	    users = new HashMap<String, User>();
	    users.put("admin", new User(1,"Admin","myAdminName"));
	    users.put("common1", new User(2,"Common","simpleUser_1"));
	    users.put("common2", new User(3,"Common","simpleUser_2"));
	    users.put("common3", new User(4,"Common","simpleUser_3"));
	    users.put("common4", new User(5,"Common","simpleUser_4"));
	}
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    
    
    @GET
    @Path("testXML/{userkey}")
    @Produces(MediaType.APPLICATION_XML)
    public User getUserXML(@PathParam("userkey") String userkey) {
       	return users.get(userkey);
    }
    
    @GET
    @Path("testJSON")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserJSON(@QueryParam("userkey")String userkey) {
        
    	if( !users.containsKey(userkey)){
    		ResponseBuilder builder = Response.status(Response.Status.NOT_FOUND);
    		builder.entity("Requested user not found on server");
    		Response response = builder.build();
    		
    		throw new WebApplicationException(response);
    	}
    	
    	return users.get(userkey);
    }
    
    
    @POST
    @Path("testJSON/upload")
    @Produces(MediaType.APPLICATION_JSON)
    public User setUser(User newUser) {
    	newUser.setId(10);
    	newUser.setUsername("myChangedUserName");
    	
    	return newUser;
    }

    @GET
    @Path("testRedirect")
    @Produces(MediaType.APPLICATION_JSON)
    public String callWs() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://api.despegar.com/cities/tripplanning?includecity=true");
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            
            if ( entity == null ){
            	ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
        		builder.entity("Nothing found in despegar.com");
        		Response error = builder.build();
        		
        		throw new WebApplicationException(error);
            }

            String entityStr = EntityUtils.toString(entity);
            
        	if ( entityStr.contains("exceeded the daily limit") ){
        		
        		ResponseBuilder builder = Response.status(Response.Status.FORBIDDEN);
        		builder.entity("Daily limit of requests exceeded");
        		Response error = builder.build();
        		
        		throw new WebApplicationException(error);
        	}
        	
            return entityStr;
        
        } catch (IOException e) {
            e.printStackTrace();
            
            ResponseBuilder builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
    		builder.entity("Something went wrong while parsing the response");
    		Response error = builder.build();
    		
    		throw new WebApplicationException(error);
        }
    }

}
