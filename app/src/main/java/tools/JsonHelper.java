package tools;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonHelper {

	private ObjectMapper mapper;

	private JsonHelper() {
		mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private static JsonHelper jsonHelper;

	public synchronized static JsonHelper getHelper() {

		if (null == jsonHelper) {
			jsonHelper = new JsonHelper();
		}
		return jsonHelper;
	}

	public ObjectMapper getMapper() {
		return mapper;
	}

	public <T> T genBeanByJson(String jsonString, Class<T> claz) {
		T bean = null;
		try {
			bean = mapper.readValue(jsonString, claz);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bean;
	}

	public <T> T genBeanByMap(Map map, Class<T> claz) {
		T bean = null;
		bean = mapper.convertValue(map, claz);
		return bean;
	}

	public String genJsonByMap(Map map) {

		return genJsonByBean(map);
	}

	public String genJsonByBean(Object obj) {
		String message = "";
		try {
			message = mapper.writeValueAsString(obj);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	public <T> List<T> genListByMap(Object obj, TypeReference<List<T>> typeReference) {
		List<T> convertValue = null;
		// convertValue = mapper.convertValue(obj, new TypeReference<List<T>>()
		// {
		// });
		convertValue = mapper.convertValue(obj, typeReference);
		return convertValue;
	}

	public Map getMapByJson(String json) {
		Map<String, Object> response = new HashMap();
		try {
			response = mapper.readValue(json, HashMap.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
}
