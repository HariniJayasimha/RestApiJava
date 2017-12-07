package models;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;

import play.Logger;
import utils.EnumMessages;
import utils.YmlException;

@Entity(name="user_session")
public class UserSession  extends Model {
	
	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	public Users user;
	
	@Column(nullable = false, unique = true)
	public String sessionToken;
	
	@Column(name="login_time", nullable = false)
	public Long loginTime;
	
	private final Finder<Integer, UserSession> find = new Finder<Integer, UserSession>(UserSession.class);
	
	//**************************** Raw Query ************************************
	
	/**
	 * Delete user's previous session
	 * @param userId
	 * @return
	 */
	public int deleteSessionRawQuery(Long userId) {
		String query = "DELETE FROM user_session WHERE user_id =:userId";
		SqlUpdate deleteUser = Ebean.createSqlUpdate(query);
		deleteUser.setParameter("userId", userId);
		return Ebean.execute(deleteUser);
	}
	
	/**
	 * Create session 
	 * @param userId
	 * @return
	 * @throws YmlException
	 */
	public String createSessionRawQuery(Long userId) throws YmlException {
		// Delete previous session if any 
		int deletedRows = deleteSessionRawQuery(userId);
		Logger.info("deletedRows: ",deletedRows);
		// Create Session
		String sessionToken = UUID.randomUUID().toString();
		String query = "INSERT INTO user_session (user_id, session_token, login_time) VALUES(:user_id,:session_token,:login_time)";
		SqlUpdate insertUser = Ebean.createSqlUpdate(query);
		insertUser.setParameter("user_id", userId).setParameter("session_token", sessionToken)
		.setParameter("login_time", new Date().getTime());
		int rows = Ebean.execute(insertUser);
		
		if(rows > 0) {
			return sessionToken;
		} else {
			throw new YmlException(EnumMessages.USER_SESSION_NOT_CREATED.toString());
		}
	}
	
	/**
	 * Validate User session
	 * @param sessionToken
	 * @return
	 */
	public SqlRow validateUserSessionRawQuery(String sessionToken) {
		String selectQuery = "SELECT U.id AS user_id,US.id AS session_id FROM users U "
				+ "LEFT JOIN user_session US ON US.user_id = U.id WHERE US.session_token=:sessionToken";
		SqlQuery rawQuery = Ebean.createSqlQuery(selectQuery);
		rawQuery.setParameter("sessionToken", sessionToken);
		return rawQuery.findUnique();
	}
	
	// ************************** Ebean Query ************************************
	
	/**
	 * User Session by userId
	 * @param userId
	 * @return
	 */
	public UserSession findSessionByUserId(Long userId){
		return find.where().eq("user_id", userId).findUnique();
	}
	
	/**
	 * Delete user's previous session
	 * @param user
	 * @return
	 */
	public boolean deleteSession(Users user){
		boolean isDeleted = true;
		UserSession existingSession = findSessionByUserId(user.id);
		if(existingSession != null) {
			isDeleted = Ebean.delete(existingSession);
		}
		return isDeleted;
	}
	
	/**
	 * Create session 
	 * @param user
	 * @return
	 */
	public String createSession(Users user){
		// Delete previous session if any
		boolean isDeleted = deleteSession(user);
		// Create Session
		UserSession session = new UserSession();
		session.user = user;
		session.sessionToken = UUID.randomUUID().toString();
		session.loginTime = new Date().getTime();
		Ebean.insert(session);
		
		return session.sessionToken;
	}
	
	/**
	 * Validate User session
	 * @param sessionToken
	 * @return
	 */
	public Users validateUserSession(String sessionToken){
		UserSession userSession = find.where().eq("session_token", sessionToken).findUnique();
		return userSession.user;
	}
	
}
