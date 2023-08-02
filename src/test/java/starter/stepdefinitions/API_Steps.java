package starter.stepdefinitions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;

import com.jayway.jsonpath.JsonPath;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import starter.context.ScenarioContext;

public class API_Steps {
	ScenarioContext context = new ScenarioContext();
	Response response;
	String placeID;

	public String loadProp(String key) throws IOException {
		Properties prop = new Properties();
		String propertiesFilePath = new File(System.getProperty("user.dir") + "/serenity.properties").getAbsolutePath();
		prop.load(new FileInputStream(propertiesFilePath));
		return prop.get(key).toString().trim();
	}

	@Given("tester prepare {string}")
	public void testerPrepare(String apiKey) throws IOException {
		switch (apiKey) {
		case "VALID":
			apiKey = loadProp("api.key");
			context.setContext("apiKey", apiKey);
			break;
		case "INVALID":
			apiKey = loadProp("api.key");
			context.setContext("apiKey", apiKey + "a");
			break;
		}
	}

	@When("tester create a new {string} request to find place with {string}")
	public void testerCreateANewRequestToFindPlaceWith(String method, String requestParams)
			throws IOException {
		String[] parameterList = requestParams.split(",");
		StringBuilder builder = new StringBuilder();

		for (String parameter : parameterList) {
			builder.append(parameter.trim()).append("&");
		}
		requestParams = builder.toString();
		requestParams = requestParams.substring(0, requestParams.length() - 1);

		switch (method) {
		case "GET":
			response = RestAssured.given().when().get(loadProp("base.url") + "findplacefromtext/json?" + requestParams
					+ "&key=" + context.getContext("apiKey"));
			context.setContext("response", response);

			String jPath = "$.candidates.[0].place_id";
			try{
				placeID = JsonPath.read(response.getBody().asString(), jPath);
			}catch(Exception e){
				e.printStackTrace();
			}

			break;
		case "POST":
			// TO DO
			break;
		}
	}

	@Then("response status code should be {string}")
	public void responseStatusCodeShouldBe(String statusCode) {
		response = context.getContext("response");
		Assert.assertEquals(Integer.parseInt(statusCode), response.getStatusCode());
	}

	@And("the place search response should match with {string} and there are place_ids in response")
	public void thePlaceSearchResponseShouldMatchWithAndThereArePlace_idsInResponse(String expectedStatus) {
		String jPath;	
		response = context.getContext("response");
		jPath = "$.status";
		String status = JsonPath.read(response.getBody().asString(), jPath);
		Assert.assertEquals(expectedStatus, status);
		
		//incase the status is OK, verify that there is the place_id in response
		switch (expectedStatus) {
		case "OK":
			response = context.getContext("response");
			jPath = "$.candidates.length()";
			Assert.assertTrue(Integer.parseInt(JsonPath.read(response.getBody().asString(), jPath).toString()) > 0);
			break;
		}
		
	}

	@And("tester create a new {string} request to find place details with placeid got from place search")
	public void testerCreateANewRequestToFindPlaceDetailsWithPlaceidGotFromAbovePlaceSearch(String method)
			throws IOException {
		switch (method) {
			case "GET":
				response = RestAssured.given().when().get(loadProp("base.url") + "details/json?place_id=" + placeID
						+ "&key=" + context.getContext("apiKey"));
				context.setContext("response", response);
				break;
			case "POST":
				// TO DO
				break;
		}
	}

	@And("the place detail response should match with phone {string} and address {string}")
	public void thePlaceDetailResponseShouldMatchWithPhoneAndAddress(String expectedPhone, String expectedAddress) {
		String jPath;
		response = context.getContext("response");
		jPath = "$.result.formatted_phone_number";
		String phoneNumber = JsonPath.read(response.getBody().asString(), jPath);
		Assert.assertEquals(expectedPhone, phoneNumber);
		jPath = "$.result.formatted_address";
		String address = JsonPath.read(response.getBody().asString(), jPath);
		Assert.assertEquals(expectedAddress, address);
	}
}
