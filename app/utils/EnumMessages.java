package utils;

public enum EnumMessages {
	
	ENTER_ALL_DETAILS("enter.all.details"),
	USER_NOT_SIGNED_UP("user.not.signed.up"),
	ENTER_USER_NAME("enter.user.name"),
	ENTER_EMAIL("enter.email"),
	ENTER_PASSWORD("enter.password"),
	USER_WITH_EMAIL_ALREADY_SIGNED_UP("user.eith.email.already.signed.up"),
	USER_SESSION_NOT_CREATED("user.session.not.created"),
	ENTER_DESCRIPTION("enter.description"),
	INVALID_TOKEN("session.invalid"),
	POST_COULD_NOT_BE_CREATED("post.could.not.be.created"),
	USER_POSTS_NOT_LISTED("user.posts.could.not.be.listed"),
	;

    private String message;
 
    private EnumMessages(String message) {
        this.message = message;
    }
 
    @Override
    public String toString() {
        return this.message;
    }
}
