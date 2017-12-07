package models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;

import dtos.request.SignUpDTO;
import utils.ApiResponseKeys;
import utils.EnumMessages;
import utils.SecureDigester;
import utils.YmlException;

@Entity(name="users")
@UniqueConstraint(columnNames={"email"})
public class Users extends Model{
	
	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Column(name = "user_name")
	public String userName;
	
	@Column(name = "password", nullable = false)
	public String password;
	
	@Column(name = "email", nullable = false)
	public String email;
	
	@Column(name="created_date", nullable=true)
	public Long createdDate;
	
	private final Finder<Integer, Users> find = new Finder<Integer, Users>(Users.class);
	
	//**************************** Raw Query ************************************
	
	/**
	 * Finds User by Email 
	 * @param email
	 * @return
	 */
	public SqlRow findUserByEmailWithRawQuery(final String email) {
		String selectQuery = "SELECT * FROM users WHERE email=:email";
		SqlQuery rawQuery = Ebean.createSqlQuery(selectQuery);
		rawQuery.setParameter("email", email);
		return rawQuery.findUnique();
	}
	
	/**
	 * Insert user for sign up 
	 * @throws Exception 
	 */
	public Map<String, Object> userSignUpWithRawQuery(final SignUpDTO requestDTO) throws Exception {
		SqlRow newUser = null;
		Map<String, Object> signUpResponse = new HashMap<>();
		// Validate inputs
		if(requestDTO.userName.isEmpty()) {
			throw new YmlException(EnumMessages.ENTER_USER_NAME.toString());
		}
		if(requestDTO.email.isEmpty()) {
			throw new YmlException(EnumMessages.ENTER_EMAIL.toString());
		}
		String hashedPassword = null;
		if(requestDTO.password.isEmpty()) {
			throw new YmlException(EnumMessages.ENTER_PASSWORD.toString());
		} else {
			hashedPassword = SecureDigester.digest(requestDTO.password);
		}
		// Check if user already exists
		SqlRow existingUser = findUserByEmailWithRawQuery(requestDTO.email);
		if(existingUser != null) {
			throw new YmlException(EnumMessages.USER_WITH_EMAIL_ALREADY_SIGNED_UP.toString());
		}
		String query = "INSERT INTO users (user_name,password,email,created_date) VALUES(:user_name,:password,:email,:created_date)";
		SqlUpdate insertUser = Ebean.createSqlUpdate(query);
		insertUser.setParameter("user_name", requestDTO.userName).setParameter("password", hashedPassword)
		.setParameter("email", requestDTO.email).setParameter("created_date", new Date().getTime());
		int rows = Ebean.execute(insertUser);
		
		if(rows > 0) {
			newUser = findUserByEmailWithRawQuery(requestDTO.email);
			// Create session for user
			UserSession userSession = new UserSession();
			String sessionToken = userSession.createSessionRawQuery(newUser.getLong(ApiResponseKeys.DB_ID.toString()));
			signUpResponse.put(ApiResponseKeys.USER_ID.toString(), newUser.getString(ApiResponseKeys.DB_ID.toString()));
			signUpResponse.put(ApiResponseKeys.USER_NAME.toString(), newUser.getString(ApiResponseKeys.DB_USER_NAME.toString()));
			signUpResponse.put(ApiResponseKeys.EMAIL.toString(), newUser.getString(ApiResponseKeys.DB_EMAIL.toString()));
			signUpResponse.put(ApiResponseKeys.SESSION_TOKEN.toString(), sessionToken);
		}
		return signUpResponse;
	}

	// ************************** Ebean Query ************************************
	
	/**
	 * Finds User by Email 
	 * @param email
	 * @return
	 */
	public Users findUserByEmail(final String email) {
		return find.where().eq("email", email).findUnique();
	}
	
	/**
	 * Insert user for sign up 
	 * @throws Exception 
	 */
	public Map<String, Object> userSignUp(final SignUpDTO requestDTO) throws Exception {
		Users newUser = null;
		// Validate inputs
		if(requestDTO.userName.isEmpty()) {
			throw new YmlException(EnumMessages.ENTER_USER_NAME.toString());
		}
		if(requestDTO.email.isEmpty()) {
			throw new YmlException(EnumMessages.ENTER_EMAIL.toString());
		}
		String hashedPassword = null;
		if(requestDTO.password.isEmpty()) {
			throw new YmlException(EnumMessages.ENTER_PASSWORD.toString());
		} else {
			hashedPassword = SecureDigester.digest(requestDTO.password);
		}
		// Check if user already exists
		Users existingUser = findUserByEmail(requestDTO.email);
		if(existingUser != null) {
			throw new YmlException(EnumMessages.USER_WITH_EMAIL_ALREADY_SIGNED_UP.toString());
		}

		newUser = new Users();
		newUser.email = requestDTO.email;
		newUser.userName = requestDTO.userName;
		newUser.password = hashedPassword;
		newUser.createdDate = new Date().getTime();
		Ebean.insert(newUser);
		
		// Create session
		UserSession userSession = new UserSession();
		String sessionToken = userSession.createSession(newUser);
		if(sessionToken == null){
			throw new YmlException(EnumMessages.USER_SESSION_NOT_CREATED.toString());
		}
		Map<String, Object> signUpResponse = new HashMap<>();
		signUpResponse.put(ApiResponseKeys.USER_ID.toString(), newUser.id);
		signUpResponse.put(ApiResponseKeys.USER_NAME.toString(), newUser.userName);
		signUpResponse.put(ApiResponseKeys.EMAIL.toString(), newUser.email);
		signUpResponse.put(ApiResponseKeys.SESSION_TOKEN.toString(), sessionToken);
		
		return signUpResponse;
	}
}
