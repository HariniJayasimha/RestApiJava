package utils;

import play.api.Play;

public class YmlException extends Exception {

	private String message;

    private String systemMessage;

    private Languages languages = Play.current().injector().instanceOf(Languages.class);

	public YmlException(String message) {
        this.message = languages.getLocalizedString(message);
	}

	public YmlException(String message, Object... args) {
		this.message = languages.getLocalizedString(message, args);
	}

	public YmlException(String message, String sysMsg) {
		this.message = languages.getLocalizedString(message);
        this.systemMessage = sysMsg;
	}

	public String getMessage() {
		return this.message;
	}

    public String getSystemMessage() {
        return this.systemMessage;
    }
}
