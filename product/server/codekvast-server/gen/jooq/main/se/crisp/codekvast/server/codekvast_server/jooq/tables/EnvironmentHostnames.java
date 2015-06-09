/**
 * This class is generated by jOOQ
 */
package se.crisp.codekvast.server.codekvast_server.jooq.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EnvironmentHostnames extends org.jooq.impl.TableImpl<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord> {

	private static final long serialVersionUID = -940989153;

	/**
	 * The singleton instance of <code>PUBLIC.ENVIRONMENT_HOSTNAMES</code>
	 */
	public static final se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames ENVIRONMENT_HOSTNAMES = new se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord> getRecordType() {
		return se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord.class;
	}

	/**
	 * The column <code>PUBLIC.ENVIRONMENT_HOSTNAMES.ID</code>.
	 */
	public final org.jooq.TableField<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord, java.lang.Long> ID = createField("ID", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>PUBLIC.ENVIRONMENT_HOSTNAMES.ORGANISATION_ID</code>.
	 */
	public final org.jooq.TableField<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord, java.lang.Long> ORGANISATION_ID = createField("ORGANISATION_ID", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>PUBLIC.ENVIRONMENT_HOSTNAMES.ENVIRONMENT_ID</code>.
	 */
	public final org.jooq.TableField<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord, java.lang.Long> ENVIRONMENT_ID = createField("ENVIRONMENT_ID", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>PUBLIC.ENVIRONMENT_HOSTNAMES.HOST_NAME</code>.
	 */
	public final org.jooq.TableField<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord, java.lang.String> HOST_NAME = createField("HOST_NAME", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

	/**
	 * Create a <code>PUBLIC.ENVIRONMENT_HOSTNAMES</code> table reference
	 */
	public EnvironmentHostnames() {
		this("ENVIRONMENT_HOSTNAMES", null);
	}

	/**
	 * Create an aliased <code>PUBLIC.ENVIRONMENT_HOSTNAMES</code> table reference
	 */
	public EnvironmentHostnames(java.lang.String alias) {
		this(alias, se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames.ENVIRONMENT_HOSTNAMES);
	}

	private EnvironmentHostnames(java.lang.String alias, org.jooq.Table<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord> aliased) {
		this(alias, aliased, null);
	}

	private EnvironmentHostnames(java.lang.String alias, org.jooq.Table<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, se.crisp.codekvast.server.codekvast_server.jooq.Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord, java.lang.Long> getIdentity() {
		return se.crisp.codekvast.server.codekvast_server.jooq.Keys.IDENTITY_ENVIRONMENT_HOSTNAMES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord> getPrimaryKey() {
		return se.crisp.codekvast.server.codekvast_server.jooq.Keys.CONSTRAINT_8;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord>>asList(se.crisp.codekvast.server.codekvast_server.jooq.Keys.CONSTRAINT_8);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<se.crisp.codekvast.server.codekvast_server.jooq.tables.records.EnvironmentHostnamesRecord, ?>>asList(se.crisp.codekvast.server.codekvast_server.jooq.Keys.CONSTRAINT_80, se.crisp.codekvast.server.codekvast_server.jooq.Keys.CONSTRAINT_803);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames as(java.lang.String alias) {
		return new se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames(alias, this);
	}

	/**
	 * Rename this table
	 */
	public se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames rename(java.lang.String name) {
		return new se.crisp.codekvast.server.codekvast_server.jooq.tables.EnvironmentHostnames(name, null);
	}
}