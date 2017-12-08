package controllers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dtos.request.CreatePostDTO;
import dtos.request.SignUpDTO;
import jsonschemavalidation.ValidateJsonSchema;
import models.Posts;
import models.UserSession;
import models.Users;
import play.mvc.Result;
import utils.ApiResponseKeys;
import utils.CorsComposition;
import utils.EnumMessages;
import utils.YmlConstants;
import utils.YmlException;

public class UserController extends BaseController {
	
	// ******************* APIs with Raw queries
	
	/**
	 * API signs' up the user
	 * @return
	 */
	@CorsComposition.Cors
	@ValidateJsonSchema(YmlConstants.JsonSchemaFilePaths.USER_SIGN_UP)
	public CompletionStage<Result> userSignUpWithRawQuery(){
		JsonNode result = null;
		try {
			final JsonNode inputData = request().body().asJson();
			ObjectMapper mapper = new ObjectMapper();
			final SignUpDTO requestDTO = mapper.convertValue(inputData, SignUpDTO.class);
			
			if(requestDTO.email == null || requestDTO.userName == null || requestDTO.password == null) {
				throw new YmlException(EnumMessages.ENTER_ALL_DETAILS.toString());
			}
			
			Users user = new Users();
			Map<String, Object> signUpResponse = user.userSignUpWithRawQuery(requestDTO);
			if(signUpResponse == null){
				throw new YmlException(EnumMessages.USER_NOT_SIGNED_UP.toString());
			}
			
			result = mapper.convertValue(signUpResponse, JsonNode.class);
			
			return successResponsePromise(result);
		} catch(Exception e) {
			e.printStackTrace();
			return failureResponsePromise(e);
		}
	}
	
	/**
	 * Creates post made by the user in session
	 * @return
	 */
	@CorsComposition.Cors
	@ValidateJsonSchema(YmlConstants.JsonSchemaFilePaths.CREATE_POST)
	public CompletionStage<Result> createPostByUserWithRawQuery() {
		JsonNode result = null;
		try{
			final JsonNode inputData = request().body().asJson();
			ObjectMapper mapper = new ObjectMapper();
			final CreatePostDTO requestDTO = mapper.convertValue(inputData, CreatePostDTO.class);
			
			// Validate input
			if(requestDTO.description == null || requestDTO.description.trim().isEmpty()) {
				throw new YmlException(EnumMessages.ENTER_DESCRIPTION.toString());
			}
			if(requestDTO.token == null || requestDTO.token.trim().isEmpty()) {
				throw new YmlException(EnumMessages.INVALID_TOKEN.toString());
			}
			
			// Validate user and session
			UserSession userSession = new UserSession();
			SqlRow userAndSessionInfo = userSession.validateUserSessionRawQuery(requestDTO.token);
			
			// Create post
			Posts posts = new Posts();
			Map<String, Long> response = posts.createPostWithRawQuery(userAndSessionInfo.getLong(ApiResponseKeys.DB_USER_ID.toString()), requestDTO.description);
			if(response == null){
				throw new YmlException(EnumMessages.POST_COULD_NOT_BE_CREATED.toString());
			}
			
			result = mapper.convertValue(response, JsonNode.class);
			return successResponsePromise(result);
		} catch(Exception e) {
			e.printStackTrace();
			return failureResponsePromise(e);
		}
	}
	
	/**
	 * Gets the list of posts by user
	 * @param userId
	 * @return
	 */
	@CorsComposition.Cors
	public CompletionStage<Result> getPostsByUserRawQuery(final Long userId) {
		JsonNode result = null;
		try{
			Posts posts = new Posts();
			List<Map<String, Object>> userPosts = posts.getUserPostsRawQuery(userId);
			if(userPosts == null) {
				throw new YmlException(EnumMessages.USER_POSTS_NOT_LISTED.toString());
			}
			
			ObjectMapper mapper = new ObjectMapper();
			result = mapper.convertValue(userPosts, JsonNode.class);
		} catch(Exception e){
			e.printStackTrace();
			return failureResponsePromise(e);
		}
		
		return successResponsePromise(result);
	}
	
	// ******************* APIs with Ebean queries
	
	/**
	 * API signs' up the user
	 * @return
	 */
	@CorsComposition.Cors
	@ValidateJsonSchema(YmlConstants.JsonSchemaFilePaths.USER_SIGN_UP)
	public CompletionStage<Result> userSignUp(){
		JsonNode result = null;
		try {
			final JsonNode inputData = request().body().asJson();
			ObjectMapper mapper = new ObjectMapper();
			final SignUpDTO requestDTO = mapper.convertValue(inputData, SignUpDTO.class);
			
			if(requestDTO.email == null || requestDTO.userName == null || requestDTO.password == null) {
				throw new YmlException(EnumMessages.ENTER_ALL_DETAILS.toString());
			}
			
			Users user = new Users();
			Map<String, Object> signUpResponse= user.userSignUp(requestDTO);
			if(signUpResponse == null){
				throw new YmlException(EnumMessages.USER_NOT_SIGNED_UP.toString());
			}
			result = mapper.convertValue(signUpResponse, JsonNode.class);
			
			return successResponsePromise(result);
		} catch(Exception e) {
			e.printStackTrace();
			return failureResponsePromise(e);
		}
	}
	
	/**
	 * Creates post made by the user in session
	 * @return
	 */
	@CorsComposition.Cors
	@ValidateJsonSchema(YmlConstants.JsonSchemaFilePaths.CREATE_POST)
	public CompletionStage<Result> createPostByUser() {
		JsonNode result = null;
		try{
			final JsonNode inputData = request().body().asJson();
			ObjectMapper mapper = new ObjectMapper();
			final CreatePostDTO requestDTO = mapper.convertValue(inputData, CreatePostDTO.class);
			
			// Validate input
			if(requestDTO.description == null || requestDTO.description.trim().isEmpty()) {
				throw new YmlException(EnumMessages.ENTER_DESCRIPTION.toString());
			}
			if(requestDTO.token == null || requestDTO.token.trim().isEmpty()) {
				throw new YmlException(EnumMessages.INVALID_TOKEN.toString());
			}
			
			// Validate user and session
			UserSession userSession = new UserSession();
			Users user = userSession.validateUserSession(requestDTO.token);
			
			// Create post
			Posts posts = new Posts();
			Map<String, Long> response = posts.createPost(user, requestDTO.description);
			if(response == null){
				throw new YmlException(EnumMessages.POST_COULD_NOT_BE_CREATED.toString());
			}
			
			result = mapper.convertValue(response, JsonNode.class);
			return successResponsePromise(result);
		} catch(Exception e) {
			e.printStackTrace();
			return failureResponsePromise(e);
		}
	}
	
	/**
	 * Gets the list of posts by user
	 * @param userId
	 * @return
	 */
	@CorsComposition.Cors
	public CompletionStage<Result> getPostsByUser(final Long userId) {
		JsonNode result = null;
		try{
			Posts posts = new Posts();
			List<Map<String, Object>> userPosts = posts.getUserPosts(userId);
			if(userPosts == null) {
				throw new YmlException(EnumMessages.USER_POSTS_NOT_LISTED.toString());
			}
			ObjectMapper mapper = new ObjectMapper();
			result = mapper.convertValue(userPosts, JsonNode.class);
		} catch(Exception e){
			e.printStackTrace();
			return failureResponsePromise(e);
		}
		
		return successResponsePromise(result);
	}
	
	@CorsComposition.Cors
	public Result testLoaderIO() {
		return ok("loaderio-f3729088beb005536a895595fa826a74");

	}

}
