package jsonschemavalidation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import controllers.BaseController;
import utils.YmlConstants.ApiFailureMessages;
import utils.YmlException;

import org.apache.commons.lang3.StringUtils;
import play.Environment;
import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.concurrent.CompletionStage;

public class JsonSchemaValidator extends Action<ValidateJsonSchema> {

    @Inject
    private BaseController baseController;

    @Inject
    Environment environment;

    @Override
    public CompletionStage<Result> call(Http.Context context) {
        CompletionStage<Result> result = validateJson();
        if (result == null) {
            result = delegate.call(context);
        }
        return result;
    }

    private CompletionStage<Result> validateJson() {

        try {
            ObjectNode inputJson = (ObjectNode) Http.Context.current().request().body().asJson();

            if (inputJson == null) {
                throw new YmlException(ApiFailureMessages.INVALID_JSON_REQUEST);
            }

            String filePath = environment.rootPath() + "/conf" + configuration.value();
            JsonNode jsonSchema = CustomJsonFactory.getjsonSchemaNode(filePath);
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            JsonSchema schema = factory.getJsonSchema(jsonSchema);
            ProcessingReport report = schema.validate(inputJson);

            if (!report.isSuccess()) {
                ProcessingMessage message;
                Iterator<ProcessingMessage> itr = report.iterator();
                while (itr.hasNext()) {
                    message = itr.next();
                    String levelKey = JsonSchemaKeys.JSONSCHEMA_LEVEL_KEY;
                    String level = message.asJson().get(levelKey).asText();
                    if (level.equals("error")) {
                        throw getFailureException(message.asJson());
                    }
                }
            }
        } catch (Exception e) {
            return baseController.failureResponsePromise(e);
        }

        return null;
    }

    private YmlException getFailureException(JsonNode json) {
        String missingResponseKey, instanceResponseKey, pointerResponseKey,
                minLengthKey, maxLengthKey, passwordkey;

        missingResponseKey = JsonSchemaKeys.JSONSCHEMA_MISSING_KEY;
        instanceResponseKey = JsonSchemaKeys.JSONSCHEMA_INSTANCE_KEY;
        pointerResponseKey = JsonSchemaKeys.JSONSCHEMA_POINTER_KEY;
        minLengthKey = JsonSchemaKeys.MIN_LENGTH_KEY;
        maxLengthKey = JsonSchemaKeys.MAX_LENGTH_KEY;
        passwordkey = JsonSchemaKeys.PASSWORD_KEY;
        String instanceKey = JsonSchemaKeys.INSTANCE_KEY;
        String pointerKey = JsonSchemaKeys.POINTER_KEY;
        String keywordKey = JsonSchemaKeys.KEYWORD_KEY;
        String expectedKey = JsonSchemaKeys.EXPECTED_KEY;
        YmlException ymlException;

        if (json.get(missingResponseKey) != null) {
            String missingField = json.get(missingResponseKey).get(0).asText();
            ymlException = new YmlException(ApiFailureMessages.FIELD_MISSING, formatString(missingField));
        } else if (json.has(minLengthKey)) {
            String field = json.get(instanceKey).get(pointerKey).asText();
            int minLengthValue = json.get(minLengthKey).asInt();
            ymlException = new YmlException(ApiFailureMessages.MIN_LENGTH_VALIDATION, formatString(field), minLengthValue);
        } else if (json.has(maxLengthKey)) {
            String field = json.get(instanceKey).get(pointerKey).asText();
            int maxLengthValue = json.get(maxLengthKey).asInt();
            ymlException = new YmlException(ApiFailureMessages.MAX_LENGTH_VALIDATION, formatString(field), maxLengthValue);
        } else if (json.has(keywordKey) && json.get(keywordKey).asText().equals("type")) {
            String field = json.get(instanceKey).get(pointerKey).asText();
            String type = json.get(expectedKey).get(0).asText();
            ymlException = new YmlException(ApiFailureMessages.TYPE_MISMATCH, formatString(field), type);
        } else if (json.has("minItems")) {
            String field = json.get(instanceKey).get(pointerKey).asText();
            ymlException = new YmlException(ApiFailureMessages.MIN_ITEMS_CONSTRAINT, formatString(field));
        } else if (json.get(instanceKey).get(pointerKey).asText().substring(1).equals(passwordkey)) {
            ymlException = new YmlException(ApiFailureMessages.INVALID_INPUT, "password");
        } else {
            String invalidField = json.get(instanceResponseKey).get(pointerResponseKey).asText();
            ymlException = new YmlException(ApiFailureMessages.INVALID_INPUT, formatString(invalidField));
        }

        return ymlException;
    }

    private static String formatString(String str) {
        if (str.charAt(0) == '/') {
            str = str.substring(1, 2).toUpperCase() + str.substring(2);
        } else {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        }

        if (str.indexOf('/') >= 0) {
            int occurrences = StringUtils.countMatches(str, '/');
            str = str.split("/")[occurrences];
        }
        return str;
    }

}
