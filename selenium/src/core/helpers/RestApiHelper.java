package core.helpers;

import org.json.JSONArray;
import org.json.JSONException;

import core.apiCore.interfaces.restApiInterface;
import core.support.configReader.Config;
import core.support.logger.TestLog;
import core.support.objects.ApiObject;
import core.support.objects.TestObject;
import io.restassured.response.Response;

public class RestApiHelper {

	/**
	 * runApiContaining("name", "zzz_","getCompanies",
	 * "id","companyId","deleteCompany") get all companies with name containing
	 * zzz_, then gets id of these companies, stores them in companyId variable and
	 * calls deleteCompany
	 * 
	 * @param getApi:
	 *            api to search for identifier. eg. name containing "zzz"
	 * @param prefix:
	 *            value containing in getApi. eg. name containing "zzz"
	 * @param identifier:
	 *            api to call to get all values. eg. getCompanies
	 * @param targetApiId:
	 *            id used to call target api.eg. id for deleteCompany api
	 * @param variable:
	 *            variable the id is stored in csv keyword file. eg companyId
	 * @param targerApi:
	 *            api to call. eg. deleteCompany
	 * @throws JSONException
	 */
	protected static void runApiContaining(String identifier, String prefix, String getApi, String targetApiId,
			String variable, String targerApi) throws JSONException {
		// gets all api values
		ApiObject api = TestObject.getApiDef(getApi);
		Response response = restApiInterface.RestfullApiInterface(api);
		JSONArray valueArray = new JSONArray(response.body().asString());

		for (int i = 0; i < valueArray.length(); i++) {

			String name = valueArray.getJSONObject(i).getString(identifier);
			String id = valueArray.getJSONObject(i).getString(targetApiId);

			if (name.contains(prefix)) {
				TestLog.logPass("calling: " + targerApi + ": with identifier: " + name);
				Config.putValue(variable, id);
				api = TestObject.getApiDef(targerApi);

				restApiInterface.RestfullApiInterface(api);
			}
		}
	}

	/**
	 * runApiEquals("name", "test123","getCompanies",
	 * "id","companyId","deleteCompany") get all companies with name equals test123,
	 * then gets id of these companies and calls delete with id
	 * 
	 * @param getApi:
	 *            api to search for identifier. eg. name equals "test123"
	 * @param value:
	 *            value containing in getApi. eg. name equals "test123"
	 * @param identifier:
	 *            api to call to get all values. eg. getCompanies
	 * @param targetApiId:
	 *            id used to call target api.eg. id for deleteCompany api
	 * @param variable:
	 *            variable the id is stored in csv keyword file. eg companyId
	 * @param targerApi:
	 *            api to call. eg. deleteCompany
	 * @throws JSONException
	 */
	protected static void runApiEquals(String identifier, String value, String getApi, String targetApiId,
			String variable, String targerApi) throws JSONException {
		// gets all api values
		ApiObject api = TestObject.getApiDef(getApi);
		Response response = restApiInterface.RestfullApiInterface(api);
		JSONArray valueArray = new JSONArray(response.body().asString());

		for (int i = 0; i < valueArray.length(); i++) {

			String name = valueArray.getJSONObject(i).getString(identifier);
			String id = valueArray.getJSONObject(i).getString(targetApiId);

			if (name.equals(value)) {
				TestLog.logPass("calling: " + targerApi + ": with identifier: " + name);
				Config.putValue(variable, id);
				api = TestObject.getApiDef(targerApi);

				restApiInterface.RestfullApiInterface(api);
			}
		}
	}
}
