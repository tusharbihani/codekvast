/**
 * This class is generated by jOOQ
 */
package se.crisp.codekvast.server.codekvast_server.jooq.tables.pojos;

/**
 * This class is generated by jOOQ.
 *
 * Groups hostnames into environments
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Environments implements java.io.Serializable {

	private static final long serialVersionUID = -2052428412;

	private final java.lang.Long   id;
	private final java.lang.Long   organisationId;
	private final java.lang.String name;

	public Environments(
		java.lang.Long   id,
		java.lang.Long   organisationId,
		java.lang.String name
	) {
		this.id = id;
		this.organisationId = organisationId;
		this.name = name;
	}

	public java.lang.Long getId() {
		return this.id;
	}

	public java.lang.Long getOrganisationId() {
		return this.organisationId;
	}

	public java.lang.String getName() {
		return this.name;
	}
}