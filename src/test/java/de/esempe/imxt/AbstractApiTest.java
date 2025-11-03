package de.esempe.imxt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.TestInstance;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractApiTest
{
	protected final static String APPLICATION_JSON = "application/json;charset=UTF-8";
	protected final static String BASE_URL = "http://localhost:9090/imxt/";

	protected final HttpClient client;
	protected final String restUrl;

	private String token;

	protected AbstractApiTest(final String restUrl)
	{
		this.restUrl = restUrl;
		this.token = "";
		this.client = HttpClient.newBuilder() //
				.version(Version.HTTP_2) //
				.connectTimeout(Duration.ofSeconds(3)) //
				.build();
	}

	protected JsonObject doGETJsonObject(final String pathExtension, final HttpStatusCode statusCode)
	{
		final String url = BASE_URL + this.restUrl + pathExtension;
		final var request = HttpRequest.newBuilder() //
				.uri(URI.create(url)) //
				.header("Authorization", "Bearer " + this.token) //
				.header("Content-Type", "application/json") //
				.GET().//
				build();

		try
		{
			final var res = this.client.send(request, HttpResponse.BodyHandlers.ofString());
			assertAll("Result of 'get'data", //
					() -> assertThat(res).isNotNull(), //
					() -> assertThat(res.statusCode()).isEqualTo(statusCode.code()), //
					() -> assertThat(res.headers().allValues("content-type")).isNotEmpty(), //
					() -> assertThat(res.headers().allValues("content-type")).contains(APPLICATION_JSON) //
			);

			final var data = res.body();
			assertThat(data).isNotBlank();

			final var jsonObj = this.getJsonObjectFromString(data);
			assertThat(jsonObj).isNotNull();

			return jsonObj;

		}
		catch (final Exception e)
		{
			fail(e);
			return null;
		}
	}

	protected JsonArray doGETJsonArray(final String pathExtension, final HttpStatusCode statusCode)
	{
		final String url = BASE_URL + this.restUrl + pathExtension;
		final var request = HttpRequest.newBuilder() //
				.uri(URI.create(url)) //
				.header("Authorization", "Bearer " + this.token) //
				.header("Content-Type", "application/json") //
				.GET().//
				build();

		try
		{
			final var res = this.client.send(request, HttpResponse.BodyHandlers.ofString());
			assertAll("Result of 'get'", //
					() -> assertThat(res).isNotNull(), //
					() -> assertThat(res.statusCode()).isEqualTo(statusCode.code()), //
					() -> assertThat(res.headers().allValues("content-type")).isNotEmpty(), //
					() -> assertThat(res.headers().allValues("content-type")).contains(APPLICATION_JSON) //
			);

			final var data = res.body();
			assertThat(data).isNotBlank();

			final var jsonArray = this.getJsonArrrayFromString(data);
			assertThat(jsonArray).isNotNull();

			return jsonArray;
		}
		catch (final Exception e)
		{
			fail(e);
			return null;
		}
	}

	protected JsonObject doGET(final String url)
	{
		final var request = HttpRequest.newBuilder() //
				.uri(URI.create(url)) //
				.header("Authorization", "Bearer " + this.token) //
				.header("Content-Type", "application/json") //
				.GET().//
				build();
		try
		{
			final var res = this.client.send(request, HttpResponse.BodyHandlers.ofString());
			final var data = res.body();
			final var jsonObj = this.getJsonObjectFromString(data);
			return jsonObj;
		}
		catch (final Exception e)
		{
			fail(e);
			return null;
		}
	}

	protected String doPOSTJsonObject(final String pathExtension, final String payload, final HttpStatusCode statusCode)
	{
		final String url = BASE_URL + this.restUrl + pathExtension;

		// act
		final var request = HttpRequest.newBuilder() //
				.uri(URI.create(url)) //
				.header("Content-Type", "application/json") //
				.POST(BodyPublishers.ofString(payload)) //
				.build();
		try
		{
			final HttpResponse<?> res = this.client.send(request, HttpResponse.BodyHandlers.ofString());

			// assert
			assertAll("Result of 'post", //
					() -> assertThat(res).isNotNull(), //
					() -> assertThat(res.statusCode()).isEqualTo(statusCode.code()) //
			);

			String location = "";
			if (statusCode == HttpStatusCode.CREATED)
			{
				location = res.headers().firstValue("location").orElse("");
				assertThat(location).isNotEmpty();
			}
			return location;

		}
		catch (final Exception e)
		{
			fail(e);
			return "";
		}

	}

	protected void doDelete(final String url, final HttpStatusCode statusCode)
	{
		// final String url = BASE_URL + this.restUrl + pathExtension;

		// act
		final var request = HttpRequest.newBuilder() //
				.uri(URI.create(url)) //
				.header("Content-Type", "application/json") //
				.DELETE() //
				.build();
		try
		{
			final HttpResponse<?> res = this.client.send(request, HttpResponse.BodyHandlers.ofString());

			// assert
			assertAll("Result of 'delete", //
					() -> assertThat(res).isNotNull(), //
					() -> assertThat(res.statusCode()).isEqualTo(statusCode.code()) //
			);
		}
		catch (final Exception e)
		{
			fail(e);
		}

	}

	protected void loginAsReader() throws IOException, InterruptedException
	{
		this.login("read", "geheim123");
	}

	protected void loginAsWriter() throws IOException, InterruptedException
	{
		this.login("write", "geheim123");
	}

	protected void loginAsAdmin() throws IOException, InterruptedException
	{
		this.login("admin", "geheim123");
	}

	private void login(final String user, final String password) throws IOException, InterruptedException
	{
		final String url = BASE_URL + "auth/login";
		final String logindata = Json.createObjectBuilder() //
				.add("user", user) //
				.add("passwd", password) //
				.build() //
				.toString();

		final var request = HttpRequest.newBuilder() //
				.uri(URI.create(url)) //
				.header("Content-Type", "application/json") //
				.POST(BodyPublishers.ofString(logindata)) //
				.build();
		final var res = this.client.send(request, HttpResponse.BodyHandlers.ofString());

		assertAll("Verify meta data", //
				() -> assertThat(res).isNotNull(), //
				() -> assertThat(res.statusCode()).isEqualTo(HttpStatusCode.OK.code()), //
				() -> assertThat(res.headers().allValues("content-type")).isNotEmpty(), //
				() -> assertThat(res.body()).isNotEmpty() //
		);

		this.token = res.body();

	}

	protected JsonArray getJsonArrrayFromString(final String jsonString)
	{
		final var jsonReader = Json.createReader(new StringReader(jsonString));
		final var result = jsonReader.readArray();

		return result;
	}

	protected JsonObject getJsonObjectFromString(final String jsonString)
	{
		final var jsonReader = Json.createReader(new StringReader(jsonString));
		final var result = jsonReader.readObject();
		return result;
	}

}
