package de.esempe.imxt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("REST-API Ping")
public class PingResourceTest
{
	private final static String APPLICATION_JSON = "application/json;charset=UTF-8";

	@Test
	void ping() throws IOException, InterruptedException
	{
		final HttpClient client = HttpClient.newBuilder().version(Version.HTTP_2).connectTimeout(Duration.ofSeconds(3)).build();

		final var request = HttpRequest.newBuilder().uri(URI.create("http://localhost:9090/imxt/ping")).header("Content-Type", "application/json").GET().build();
		final var res = client.send(request, HttpResponse.BodyHandlers.ofString());

		assertAll("Verify meta data", //
				() -> assertThat(res).isNotNull(), //
				() -> assertThat(res.statusCode()).isEqualTo(200), //
				() -> assertThat(res.headers().allValues("content-type")).isNotEmpty(), //
				() -> assertThat(res.headers().allValues("content-type")).contains(APPLICATION_JSON) //
		);
	}
}
