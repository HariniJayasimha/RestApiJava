package jsonschemavalidation;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

import play.Logger;
import play.libs.Json;

public class CustomJsonFactory {
	private static HashMap<String, JsonNode> jsonNodeMap = new HashMap<String, JsonNode>();

	public static JsonNode getjsonSchemaNode(String fileName)
			throws JsonParseException, JsonMappingException, IOException {

		if (jsonNodeMap.containsKey(fileName)) {
			return jsonNodeMap.get(fileName);
		} else {
			try {
				Logger.info("====Loading " + fileName + " file======");
				JsonNode temp = Json.newObject();
				temp = JsonLoader.fromPath(fileName);
				jsonNodeMap.put(fileName, temp);
				return jsonNodeMap.get(fileName);
			} catch (Exception e) {
				Logger.info("====Error in Loading " + fileName + " file======");
				throw e;
			}
		}
	}

}
