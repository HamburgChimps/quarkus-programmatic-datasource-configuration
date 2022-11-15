package de.hamburgchimps;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalPropertiesReader;
import io.quarkus.hibernate.orm.PersistenceUnitExtension;
import io.quarkus.hibernate.orm.runtime.customized.QuarkusConnectionProvider;
import io.quarkus.hibernate.orm.runtime.tenant.TenantConnectionResolver;
import org.flywaydb.core.Flyway;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import javax.enterprise.context.ApplicationScoped;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Implement the quarkus TenantConnectionResolver interface to determine the jdbc
// datasource at runtime as opposed to at compile-time. Since we are using h2, quarkus
// will autoconfigure a default datasource for us in dev mode via dev services.
// If we are not in dev mode or are don't use a database for which quarkus supports dev services,
// then quarkus still needs a default datasource defined in application.properties in order to run without errors.
// It feels like a lot of work just to dynamically/programmatically define a datasource.
// Using props for datasource initialization as demonstrated here ->
// https://github.com/quarkusio/quarkus/issues/7019#issuecomment-645548780
@PersistenceUnitExtension
@ApplicationScoped
public class ProgrammaticDatasourceConfigurer implements TenantConnectionResolver {

    @Override
    public ConnectionProvider resolve(String tenantId) {
        Map<String, String> props = new HashMap<>();


        var jdbcUrl = String.format("jdbc:h2:./%s-datasource-%s", tenantId, UUID.randomUUID());

        props.put(AgroalPropertiesReader.MAX_SIZE, "1");
        props.put(AgroalPropertiesReader.MIN_SIZE, "1");
        props.put(AgroalPropertiesReader.INITIAL_SIZE, "1");
        props.put(AgroalPropertiesReader.MAX_LIFETIME_S, "10");
        props.put(AgroalPropertiesReader.ACQUISITION_TIMEOUT_S, "3");
        props.put(AgroalPropertiesReader.JDBC_URL, jdbcUrl);

        // Run flyway migrations on dynamically created db
        // Even if you have enabled hibernate schema generation,
        // it won't work with programmatically generated datasources.
        // Quarkus only runs the Hibernate schema generation, if enabled,
        // on the default datasource.
        var flyway = Flyway
                .configure()
                .dataSource(jdbcUrl, "", "")
                .cleanDisabled(false)
                .load();

        flyway.clean();
        flyway.migrate();

        try {
            return new QuarkusConnectionProvider(AgroalDataSource.from(new AgroalPropertiesReader().readProperties(props).get()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
