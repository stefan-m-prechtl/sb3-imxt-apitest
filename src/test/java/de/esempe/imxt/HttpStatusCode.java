package de.esempe.imxt;

public enum HttpStatusCode
{
	OK(200), //
	CREATED(201), //
	NO_CONTENT(204), //
	BAD_REQUEST(400), //
	UNAUTHORIZED(401), //
	FORBIDDEN(403), //
	NOT_FOUND(404), //
	CONFLICT(409), //
	INTERNAL_SERVER_ERROR(500);

	private final int code;

	HttpStatusCode(final int code)
	{
		this.code = code;
	}

	public int code()
	{
		return this.code;
	}

	public static HttpStatusCode fromCode(final int code)
	{
		for (final HttpStatusCode s : values())
		{
			if (s.code == code)
			{
				return s;
			}
		}
		return null;
	}
}
