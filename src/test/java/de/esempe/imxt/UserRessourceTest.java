package de.esempe.imxt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

@DisplayName("REST-API User")
@TestMethodOrder(OrderAnnotation.class)
class UserRessourceTest extends AbstractApiTest
{
	private String locationOfCreatedUser;
	private JsonObject jsonFirstUser;

	UserRessourceTest()
	{
		super("user");
		this.locationOfCreatedUser = "";
	}

	@Test
	@Order(10)
	@DisplayName("GET /user: OK")
	void readAllUsersOk() throws IOException, InterruptedException
	{
		final JsonArray jsonContent = this.doGETJsonArray("", HttpStatusCode.OK);
		System.out.println(jsonContent);

		assertAll("Verify meta data", //
				() -> assertThat(jsonContent).isNotNull(), //
				() -> assertThat(jsonContent).hasSizeGreaterThanOrEqualTo(3) //
		);

		this.jsonFirstUser = (JsonObject) jsonContent.get(0);

	}

	@Test
	@Order(11)
	@DisplayName("GET /user/{id}: OK")
	void readOneUsersOk() throws IOException, InterruptedException
	{
		final String pathExtentions = "/" + this.jsonFirstUser.getString("id");
		final JsonObject jsonContent = this.doGETJsonObject(pathExtentions, HttpStatusCode.OK);
		System.out.println(jsonContent);

		assertAll("Verify meta data", //
				() -> assertThat(jsonContent).isNotNull() //
		);
	}

	@Test
	@Order(20)
	@DisplayName("POST /user: OK")
	void createUsersOk() throws IOException, InterruptedException
	{
		this.loginAsAdmin();
		final var jsonUser = this.createEntity("test", "Berta", "Test");
		final String location = this.doPOSTJsonObject("", jsonUser.toString(), HttpStatusCode.CREATED);

		assertAll("Verify meta data", //
				() -> assertThat(location).isNotNull(), //
				() -> assertThat(location).isNotEmpty(), //
				() -> assertThat(location).startsWith(BASE_URL + this.restUrl) //
		);

		this.locationOfCreatedUser = location;
		final JsonObject jsonCreatedUser = this.doGET(location);
		assertAll("Verify meta data", //
				() -> assertThat(jsonCreatedUser).isNotNull(), //
				() -> assertThat(jsonCreatedUser).containsEntry("name", Json.createValue("test")) //
		);
	}

	@Test
	@Order(21)
	@DisplayName("POST /user: duplicated username")
	void createUsersAgagin() throws IOException, InterruptedException
	{
		this.loginAsAdmin();
		final var jsonUser = this.createEntity("test", "Berta", "Huber");
		final String location = this.doPOSTJsonObject("", jsonUser.toString(), HttpStatusCode.CONFLICT);
		assertAll("Verify meta data", //
				() -> assertThat(location).isEmpty() //
		);

	}

	@Test
	@Order(40)
	@DisplayName("DELETE /user: OK")
	void deleteUsersOk() throws IOException, InterruptedException
	{
		this.loginAsAdmin();
		this.doDelete(this.locationOfCreatedUser, HttpStatusCode.NO_CONTENT);

	}

	private JsonObject createEntity(final String username, final String firstname, final String lastname)
	{
		final var result = Json.createObjectBuilder() //
				.add("id", UUID.randomUUID().toString()) //
				.add("name", username) //
				.add("firstname", firstname) //
				.add("lastname", lastname) //
				.build();

		return result;
	}

}
