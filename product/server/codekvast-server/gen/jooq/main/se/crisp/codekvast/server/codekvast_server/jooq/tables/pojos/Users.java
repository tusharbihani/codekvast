/**
 * This class is generated by jOOQ
 */
package se.crisp.codekvast.server.codekvast_server.jooq.tables.pojos;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Users implements java.io.Serializable {

	private static final long serialVersionUID = 1477058226;

	private final java.lang.Long     id;
	private final java.lang.String   username;
	private final java.lang.String   encodedPassword;
	private final java.lang.String   plaintextPassword;
	private final java.lang.Boolean  enabled;
	private final java.lang.String   emailAddress;
	private final java.lang.String   fullName;
	private final java.sql.Timestamp createdAt;
	private final java.sql.Timestamp modifiedAt;

	public Users(
		java.lang.Long     id,
		java.lang.String   username,
		java.lang.String   encodedPassword,
		java.lang.String   plaintextPassword,
		java.lang.Boolean  enabled,
		java.lang.String   emailAddress,
		java.lang.String   fullName,
		java.sql.Timestamp createdAt,
		java.sql.Timestamp modifiedAt
	) {
		this.id = id;
		this.username = username;
		this.encodedPassword = encodedPassword;
		this.plaintextPassword = plaintextPassword;
		this.enabled = enabled;
		this.emailAddress = emailAddress;
		this.fullName = fullName;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	public java.lang.Long getId() {
		return this.id;
	}

	public java.lang.String getUsername() {
		return this.username;
	}

	public java.lang.String getEncodedPassword() {
		return this.encodedPassword;
	}

	public java.lang.String getPlaintextPassword() {
		return this.plaintextPassword;
	}

	public java.lang.Boolean getEnabled() {
		return this.enabled;
	}

	public java.lang.String getEmailAddress() {
		return this.emailAddress;
	}

	public java.lang.String getFullName() {
		return this.fullName;
	}

	public java.sql.Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public java.sql.Timestamp getModifiedAt() {
		return this.modifiedAt;
	}
}