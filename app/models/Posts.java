package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import utils.ApiResponseKeys;

@Entity(name="posts")
public class Posts extends Model {
	
	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	public Users user;
	
	@Column(name = "post_description")
	public String postDescription;
	
	@Column(name = "number_of_likes", columnDefinition = "int default 0")
	public int numberOfLikes;
	
	@Column(name = "number_of_comments", columnDefinition = "int default 0")
	public int numberOfComments;
	
	@Column(name="created_date", nullable=true)
	public Long createdDate;
	
	private final Finder<Integer, Posts> find = new Finder<Integer, Posts>(Posts.class);
	
	//**************************** Raw Query ************************************
	
	/**
	 * Creates Posts By user in session
	 * @param userId
	 * @param description
	 * @return
	 */
	public Map<String, Long> createPostWithRawQuery(Long userId, String description) {
		Map<String, Long> response = null;
		Ebean.beginTransaction();
		
		try{
			String query = "INSERT INTO posts (user_id,post_description,created_date) VALUES(:user_id,:post_description,:created_date)";
			SqlUpdate insertPost = Ebean.createSqlUpdate(query);
			insertPost.setParameter("user_id", userId).setParameter("post_description", description.trim())
			.setParameter("created_date", new Date().getTime());
			int rows = Ebean.execute(insertPost);
			
			if(rows > 0){
				response = new HashMap<String, Long>();
				SqlRow latestPost = findPostByUserIdDescriptionRawQuery(userId,description);
				response.put(ApiResponseKeys.POST_ID.toString(), latestPost.getLong(ApiResponseKeys.DB_ID.toString())); 
			}
			Ebean.commitTransaction();
		} finally {
			if (Ebean.currentTransaction() != null) {
				Ebean.currentTransaction().rollback();
			}
		}
	
		
		return response;
	}
	
	/**
	 * Finds latest post for user with description
	 * @param userId
	 * @param description
	 * @return
	 */
	public SqlRow findPostByUserIdDescriptionRawQuery(Long userId, String description) {
		String selectQuery = "SELECT * FROM posts WHERE user_id=:userId AND post_description=:description ORDER BY created_date DESC LIMIT 1";
		SqlQuery rawQuery = Ebean.createSqlQuery(selectQuery);
		rawQuery.setParameter("userId", userId).setParameter("description", description);
		return rawQuery.findUnique();
	}
	
	/**
	 * Gets the list of posts by user
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> getUserPostsRawQuery(Long userId) {
		List<Map<String, Object>> userPosts = new ArrayList<>();
		Map<String, Object> eachPost = null;
		
		String selectQuery = "SELECT * FROM posts WHERE user_id=:userId";
		SqlQuery rawQuery = Ebean.createSqlQuery(selectQuery);
		rawQuery.setParameter("userId", userId);
		List<SqlRow> postsList = rawQuery.findList();
		
		if(postsList != null && !postsList.isEmpty()){
			for(SqlRow eachRow: postsList) {
				eachPost = new HashMap<>();
				eachPost.put(ApiResponseKeys.POST_ID.toString(), eachRow.getLong(ApiResponseKeys.DB_ID.toString()));
				eachPost.put(ApiResponseKeys.POST_DESCRIPTION.toString(), eachRow.getString(ApiResponseKeys.DB_POST_DESCRIPTION.toString()));
				eachPost.put(ApiResponseKeys.NUMBER_OF_LIKES.toString(), eachRow.getInteger(ApiResponseKeys.DB_NUMBER_OF_LIKES.toString()));
				eachPost.put(ApiResponseKeys.NUMBER_OF_COMMENTS.toString(), eachRow.getInteger(ApiResponseKeys.DB_NUMBER_OF_COMMENTS.toString()));
				eachPost.put(ApiResponseKeys.CREATED_TIME.toString(), eachRow.getLong(ApiResponseKeys.DB_CREATED_DATE.toString()));
				userPosts.add(eachPost);
			}
		}
		return userPosts;
	}
	
	// ************************** Ebean Query ************************************
	
	/**
	 * Creates Posts By user in session
	 * @param user
	 * @param description
	 * @return
	 */
	public Map<String, Long> createPost(Users user, String description){
		Map<String, Long> response = null;
		Ebean.beginTransaction();
		
		try{
			Posts insertPost = new Posts();
			insertPost.user = user;
			insertPost.postDescription = description.trim();
			insertPost.createdDate = new Date().getTime();
			Ebean.insert(insertPost);
			
			response = new HashMap<String, Long>();
			response.put(ApiResponseKeys.POST_ID.toString(), findPostByUserIdDescription(user.id, description).id);
			Ebean.commitTransaction();
		} finally {
			if (Ebean.currentTransaction() != null) {
				Ebean.currentTransaction().rollback();
			}
		}
		
		return response;
	}
	
	/**
	 * Finds latest post for user with description
	 * @param userId
	 * @param description
	 * @return
	 */
	public Posts findPostByUserIdDescription(Long userId, String description) {
		return find.where().eq("user_id", userId).eq("post_description", description).orderBy("created_date DESC").setMaxRows(1).findUnique();
	}
	
	/**
	 * Gets the list of posts by user
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> getUserPosts(Long userId) {
		List<Map<String, Object>> userPosts = new ArrayList<>();
		Map<String, Object> eachPost = null;
		
		List<Posts> postsList = find.where().eq("user_id", userId).findList();
		
		if(postsList != null && !postsList.isEmpty()){
			for(Posts eachRow: postsList) {
				eachPost = new HashMap<>();
				eachPost.put(ApiResponseKeys.POST_ID.toString(), eachRow.id);
				eachPost.put(ApiResponseKeys.POST_DESCRIPTION.toString(), eachRow.postDescription);
				eachPost.put(ApiResponseKeys.NUMBER_OF_LIKES.toString(), eachRow.numberOfLikes);
				eachPost.put(ApiResponseKeys.NUMBER_OF_COMMENTS.toString(), eachRow.numberOfComments);
				eachPost.put(ApiResponseKeys.CREATED_TIME.toString(), eachRow.createdDate);
				userPosts.add(eachPost);
			}
		}
		return userPosts;
	}
}
