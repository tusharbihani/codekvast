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
public class UserRoles implements java.io.Serializable {

	private static final long serialVersionUID = -715936902;

	private final java.lang.Long     userId;
	private final java.lang.String   role;
	private final java.sql.Timestamp createdAt;
	private final java.sql.Timestamp modifiedAt;

	public UserRoles(
		java.lang.Long     userId,
		java.lang.String   role,
		java.sql.Timestamp createdAt,
		java.sql.Timestamp modifiedAt
	) {
		this.userId = userId;
		this.role = role;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	public java.lang.Long getUserId() {
		return this.userId;
	}

	public java.lang.String getRole() {
		return this.role;
	}

	public java.sql.Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public java.sql.Timestamp getModifiedAt() {
		return this.modifiedAt;
	}
}