package de.esempe.imxt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import jakarta.json.JsonObject;

@DisplayName("REST-API Ping")
@TestMethodOrder(OrderAnnotation.class)
class PingResourceTest extends AbstractApiTest
{
	PingResourceTest()
	{
		super("ping");
	}

	@Test
	@Order(10)
	@DisplayName("GET /ping: OK")
	void pingOk() throws IOException, InterruptedException
	{
		final JsonObject jsonContent = this.doGETJsonObject("", HttpStatusCode.OK);

		assertAll("Verify meta data", //
				() -> assertThat(jsonContent).isNotNull(), //
				() -> assertThat(jsonContent).containsKey("msg"), //
				() -> assertThat(jsonContent).containsKey("ts")//
		);
	}

	@Test
	@Order(11)
	@DisplayName("GET /ping/reader: OK")
	void pingReaderOk() throws IOException, InterruptedException
	{
		this.loginAsReader();

		final JsonObject jsonContent = this.doGETJsonObject("/reader", HttpStatusCode.OK);

		assertAll("Verify meta data", //
				() -> assertThat(jsonContent).isNotNull(), //
				() -> assertThat(jsonContent).containsKey("msg"), //
				() -> assertThat(jsonContent).containsKey("ts")//
		);
	}

	@Test
	@Order(12)
	@DisplayName("GET /ping/writer: OK")
	void pingWriterOk() throws IOException, InterruptedException
	{
		this.loginAsWriter();

		final JsonObject jsonContent = this.doGETJsonObject("/writer", HttpStatusCode.OK);

		assertAll("Verify meta data", //
				() -> assertThat(jsonContent).isNotNull(), //
				() -> assertThat(jsonContent).containsKey("msg"), //
				() -> assertThat(jsonContent).containsKey("ts")//
		);
	}

	@Test
	@Order(13)
	@DisplayName("GET /ping/admin: OK")
	void pingAdminOk() throws IOException, InterruptedException
	{
		this.loginAsAdmin();

		final JsonObject jsonContent = this.doGETJsonObject("/admin", HttpStatusCode.OK);

		assertAll("Verify meta data", //
				() -> assertThat(jsonContent).isNotNull(), //
				() -> assertThat(jsonContent).containsKey("msg"), //
				() -> assertThat(jsonContent).containsKey("ts")//
		);
	}

	@Test
	@Order(20)
	@DisplayName("GET /ping: wrong URL extension")
	void pingWrongUrl() throws IOException, InterruptedException
	{
		final JsonObject jsonContent = this.doGETJsonObject("/all", HttpStatusCode.NOT_FOUND);
		this.assertAllExeption(jsonContent);
	}

	@Test
	@Order(21)
	@DisplayName("GET /ping/writer: wrong user")
	void pingWriterWrongUser() throws IOException, InterruptedException
	{
		this.loginAsReader();

		final JsonObject jsonContent = this.doGETJsonObject("/writer", HttpStatusCode.FORBIDDEN);
		this.assertAllExeption(jsonContent);
	}

	private void assertAllExeption(final JsonObject jsonContent)
	{
		assertAll("Verify meta data", //
				() -> assertThat(jsonContent).isNotNull(), //
				() -> assertThat(jsonContent).containsKey("timestamp"), //
				() -> assertThat(jsonContent).containsKey("status"), //
				() -> assertThat(jsonContent).containsKey("exception"), //
				() -> assertThat(jsonContent).containsKey("message"), //
				() -> assertThat(jsonContent).containsKey("path") //
		);
	}
}
