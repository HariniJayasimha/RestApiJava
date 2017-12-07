package utils;

import play.api.Play;
import play.i18n.MessagesApi;
import play.mvc.Http;

public class Languages {
	
	private MessagesApi messagesApi;

    public Languages() {
        this.messagesApi = Play.current().injector().instanceOf(MessagesApi.class);
    }

    public String getLocalizedString(String message) {
        return messagesApi.get(Http.Context.current().lang(), message);
    }

    public String getLocalizedString(String message, Object... args) {
        return messagesApi.get(Http.Context.current().lang(), message, args);
    }
}
