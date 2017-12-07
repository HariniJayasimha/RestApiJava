package utils;

public class YmlConstants {
	
	public class ApiFailureMessages {
		
		public static final String INVALID_JSON_REQUEST = "invalid.json.request";
		public static final String INVALID_API_CALL = "invalid.api.call";
		public static final String ACCESS_FORBIDDEN = "access.forbidden";
		public static final String TECHNICAL_ERROR = "technical.error";
		public static final String INVALID_INPUT = "invalid.input";
        public static final String TYPE_MISMATCH = "type.mismatch";
        public static final String MIN_ITEMS_CONSTRAINT = "min.items";
        public static final String MIN_LENGTH_VALIDATION = "min.length.validation";
        public static final String MAX_LENGTH_VALIDATION = "max.length.validation";
        public static final String PATTERN_VALIDATION = "pattern.validation";
		public static final String FIELD_MISSING = "field.missing";
		public static final String INVALID_EMAIL = "invalid.email.format";
        public static final String SESSION_INVALID = "session.invalid";
		public static final String INVALID_PASSWORD = "password.invalid";
		public static final String INVALID_AUTHERIZATION_CODE = "Invalid.authorization.code";
		public static final String API_ACCESS_TIME_EXPIRED = "api.access.time.expired";
		public static final String HMAC_AUTHENTICATION_FAILED = "hmac.authentication.failed";

	}
	
	public class JsonSchemaFilePaths{
		public static final String USER_SIGN_UP= "/jsonschemas/UserSignUp.json";
		public static final String CREATE_POST= "/jsonschemas/CreatePost.json";
	}
}
