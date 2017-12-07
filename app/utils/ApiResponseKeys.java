package utils;

public enum ApiResponseKeys {
	DATA("data"),
	TOKEN("token"),
	LANGUAGE("language"),
	MESSAGE("message"),
	ERROR_MESSAGE("errorMessage"),
	SUCCESS_MESSAGE("successMessage"),
	SYSTEM_MESSAGE("systemMessage"),
	ERROR_CODE("errorCode"),
	OTHER_INFORMATION("otherInfo"),
	
	USER_ID("userId"),
	USER_NAME("userName"),
	EMAIL("email"),
	CREATED_DATE("createdDate"),
	SESSION_TOKEN("sessionToken"),
	POST_ID("postId"),
	POST_DESCRIPTION("postDescription"),
	NUMBER_OF_LIKES("numberOfLikes"),
	NUMBER_OF_COMMENTS("numberOfComment"),
	CREATED_TIME("createdTime"),
	
	// Api Keys for fields from database
	DB_ID("id"),
	DB_USER_NAME("user_name"),
	DB_EMAIL("email"),
	DB_USER_ID("user_id"),
	DB_POST_DESCRIPTION("post_description"),
	DB_NUMBER_OF_LIKES("number_of_likes"),
	DB_NUMBER_OF_COMMENTS("number_of_comments"),
	DB_CREATED_DATE("created_date"),
	;
	
	private String message;

	private ApiResponseKeys(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return this.message;
	}
}
