package controllers;

import java.util.concurrent.CompletableFuture;

import java.util.concurrent.CompletionStage;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import utils.YmlException;

import dtos.response.FailureResponseTemplate;
import utils.ApiResponseKeys;
import utils.MessageUtil;
import utils.YmlConstants.ApiFailureMessages;

public class BaseController extends Controller {

	private int status;
	

	public BaseController() {

    }

    public Result successResponse(Object object) {
		return ok(json(object));
	}

    public Result successResponse(String string) {
		return ok(json(string));
	}

	public Result successResponse() {
		return noContent();
	}

	public CompletionStage<Result> successResponsePromise(Object object) {
		return promise(successResponse(object));
	}
	
	public CompletionStage<Result> successResponsePromise(JsonNode data) {
		final ObjectNode result = Json.newObject();
		result.set(ApiResponseKeys.DATA.toString(), data);
		return promise(successResponse(result));
	}
	
	public CompletionStage<Result> successResponsePromise(ObjectNode data) {
		final ObjectNode result = Json.newObject();
		if (data.has(ApiResponseKeys.SUCCESS_MESSAGE.toString())) {
			final String messageKey = ApiResponseKeys.SUCCESS_MESSAGE.toString();
			data.put(messageKey, data.get(messageKey).asText());
		}

		result.set(ApiResponseKeys.DATA.toString(), data);
		return promise(successResponse(result));
	}
//	
//	public CompletionStage<Result> successResponsePromise(ObjectNode data, ObjectNode otherInfo) {
//		final ObjectNode result = Json.newObject();
//		if (data.has(ApiResponseKeys.SUCCESS_MESSAGE.toString())) {
//			final String messageKey = ApiResponseKeys.SUCCESS_MESSAGE.toString();
//			data.put(messageKey, data.get(messageKey).asText());
//		}
//		
//		result.set(ApiResponseKeys.DATA.toString(), data);
//		result.set(ApiResponseKeys.OTHER_INFORMATION.toString(), otherInfo);
//		return promise(successResponse(result));
//	}
//
//	
//	public CompletionStage<Result> successResponsePromise(JsonNode data,ObjectNode otherInfo ) {
//		final ObjectNode result = Json.newObject();
//		result.set(ApiResponseKeys.DATA.toString(), data);
//		result.set(ApiResponseKeys.OTHER_INFORMATION.toString(), otherInfo);
//		return promise(successResponse(result));
//	}
	

	
	
	public CompletionStage<Result> successResponsePromise(String message) {
		final ObjectNode result = Json.newObject();
		result.put(ApiResponseKeys.SUCCESS_MESSAGE.toString(), message);
		return promise(successResponse(result));
	}
	
	public CompletionStage<Result> successResponsePromise() {
		return promise(successResponse());
	}

	public Result failureResponse(Exception e) {
        FailureResponseTemplate failureResponseTemplate = new FailureResponseTemplate();
        String message;
		String systemMessage;
		Result result;
		if (e instanceof YmlException) {
			message = e.getMessage();
            systemMessage = ((YmlException) e).getSystemMessage();

		} else {
			message = getLocalizedMessage(ApiFailureMessages.TECHNICAL_ERROR);
			systemMessage = e.getMessage();
		}

        classifyResponse(message);
        failureResponseTemplate.errorMessage = message;
        if(systemMessage != null && !systemMessage.isEmpty()){
        	failureResponseTemplate.systemError = systemMessage;
        }
        
		
		Logger.info("status",status);
		
		switch (status) {
		case Http.Status.BAD_REQUEST:
			result = badRequest(json(failureResponseTemplate));
			break;
		case Http.Status.UNAUTHORIZED:
			result = unauthorized(json(failureResponseTemplate));
			break;
		case Http.Status.FORBIDDEN:
			result = forbidden(json(failureResponseTemplate));
			break;
		case Http.Status.NOT_FOUND:
			result = notFound(json(failureResponseTemplate));
			break;
		default:
			result = internalServerError(json(failureResponseTemplate));
		}

		return result;
	}

	public CompletionStage<Result> failureResponsePromise(Exception e) {
		return promise(failureResponse(e));
	}

	
	public CompletionStage<Result> promise(Result result) {
		return CompletableFuture.supplyAsync(() -> result);
	}

	private void classifyResponse(String message) {
		if (message.equals(getLocalizedMessage(ApiFailureMessages.ACCESS_FORBIDDEN))) {
			status = Http.Status.FORBIDDEN;
		} else if (message.equals(getLocalizedMessage(ApiFailureMessages.INVALID_API_CALL))) {
			status = Http.Status.NOT_FOUND;
		}else if(message.equals(getLocalizedMessage(ApiFailureMessages.SESSION_INVALID))){
			status = Http.Status.UNAUTHORIZED;
		} else if( message.equals(getLocalizedMessage(ApiFailureMessages.TYPE_MISMATCH)) || 
				message.equals(getLocalizedMessage(ApiFailureMessages.MIN_LENGTH_VALIDATION)) ||
				message.equals(getLocalizedMessage(ApiFailureMessages.MAX_LENGTH_VALIDATION)) ||
				message.equals(getLocalizedMessage(ApiFailureMessages.FIELD_MISSING)) ||
				message.equals(getLocalizedMessage(ApiFailureMessages.INVALID_INPUT)) ||
				message.equals(getLocalizedMessage(ApiFailureMessages.INVALID_JSON_REQUEST))){
			status = Http.Status.BAD_REQUEST;
		}
	}

	private JsonNode json(Object data) {
		return Json.toJson(data);
    }
	
	private String getLocalizedMessage(final String message){
		MessageUtil getMessage = MessageUtil.getMessageUtilInstance();
		return getMessage.getMessage(message);
	}


	public Result sendResponse(final Wrap<JsonNode> result) {
		if(result.success){
			final ObjectNode resultNode = Json.newObject();
			resultNode.set(ApiResponseKeys.DATA.toString(), result.entity);
			return successResponse(resultNode);
		}else{
			return failureResponse((Exception)result.throwable);
		}
	}

	
	public Result sendResponseNew(final Wrap<JsonNode> result) {
		return result.success ? successResponse(result.entity) : failureResponse((Exception)result.throwable);
	}
}