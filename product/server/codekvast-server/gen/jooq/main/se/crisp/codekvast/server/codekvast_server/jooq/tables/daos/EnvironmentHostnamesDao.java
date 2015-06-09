/**
 * This class is generated by jOOQ
 */
package se.crisp.codekvast.server.codekvast_server.jooq.tables.daos;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EnvironmentHostnamesDao extends org.jooq.impl.DAOImpl<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord, se.crisp.codekvast.server.codekvast_server.jooq.tables.pojos.EnvironmentHostnames, java.lang.Long> {

	/**
	 * Create a new EnvironmentHostnamesDao without any configuration
	 */
	public EnvironmentHostnamesDao() {
		super(se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames.ENVIRONMENT_HOSTNAMES, se.crisp.codekvast.server.codekvast_server.jooq.tables.pojos.EnvironmentHostnames.class);
	}

	/**
	 * Create a new EnvironmentHostnamesDao with an attached configuration
	 */
	public EnvironmentHostnamesDao(org.jooq.Configuration configuration) {
		super(se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames.ENVIRONMENT_HOSTNAMES, se.crisp.codekvast.server.codekvast_server.jooq.tables.pojos.EnvironmentHostnames.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected java.lang.Long getId(se.crisp.codekvast.server.codekvast_server.jooq.tables.pojos.EnvironmentHostnames object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>ID IN (values)</code>
	 */
	public java.util.List<se.crisp.codekvast.server.codekvast_server.jooq.tables.pojos.EnvironmentHostnames> fetchById(java.lang.Long... values) {
		return fetch(se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames.ENVIRONMENT_HOSTNAMES.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>ID = value</code>
	 */
	public se.crisp.codekvast.server.codekvast_server.jooq.tables.pojos.EnvironmentHostnames fetchOneById(java.lang.Long value) {
		return fetchOne(se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames.ENVIRONMENT_HOSTNAMES.ID, value);
	}

	/**
	 * Fetch records that have <code>ORGANISATION_ID IN (values)</code>
	 */
	public java.util.List<se.crisp.codekvast.server.codekvast_server.jooq.tables.pojos.EnvironmentHostnames> fetchByOrganisationId(java.lang.Long... values) {
		return fetch(se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames.ENVIRONMENT_HOSTNAMES.ORGANISATION_ID, values);
	}

	/**
	 * Fetch records that have <code>ENVIRONMENT_ID IN (values)</code>
	 */
	public java.util.List<se.crisp.codekvast.server.codekvast_server.jooq.tables.pojos.EnvironmentHostnames> fetchByEnvironmentId(java.lang.Long... values) {
		return fetch(se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames.ENVIRONMENT_HOSTNAMES.ENVIRONMENT_ID, values);
	}

	/**
	 * Fetch records that have <code>HOST_NAME IN (values)</code>
	 */
	public java.util.List<se.crisp.codekvast.server.codekvast_server.jooq.tables.pojos.EnvironmentHostnames> fetchByHostName(java.lang.String... values) {
		return fetch(se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames.ENVIRONMENT_HOSTNAMES.HOST_NAME, values);
	}
}