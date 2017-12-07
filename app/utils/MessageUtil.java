package utils;

import utils.Languages;
import utils.MessageUtil;

import play.api.Play;

public class MessageUtil {

	private final static Languages languages = Play.current().injector().instanceOf(Languages.class);

	private static MessageUtil messageUtil = new MessageUtil();

	public static MessageUtil getMessageUtilInstance(){
		return messageUtil;
	}

	public String getMessage(final String message) {
		return languages.getLocalizedString(message);

	}
}
