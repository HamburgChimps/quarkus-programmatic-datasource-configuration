package de.hamburgchimps;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.narayana.NarayanaTransactionIntegration;
import io.quarkus.hibernate.orm.PersistenceUnitExtension;
import io.quarkus.hibernate.orm.runtime.customized.QuarkusConnectionProvider;
import io.quarkus.hibernate.orm.runtime.tenant.TenantConnectionResolver;
import org.flywaydb.core.Flyway;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.TransactionSynchronizationRegistry;
import java.sql.SQLException;
import java.time.Duration;
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

    final TransactionManager txManager;
    final TransactionSynchronizationRegistry txSyncRegistry;

    public ProgrammaticDatasourceConfigurer(TransactionManager txManager, TransactionSynchronizationRegistry txSyncRegistry) {
        this.txManager = txManager;
        this.txSyncRegistry = txSyncRegistry;
    }

    @Override
    public ConnectionProvider resolve(String tenantId) {
        var jdbcUrl = String.format("jdbc:h2:./%s-datasource-%s", tenantId, UUID.randomUUID());

        try {
            // Run flyway migrations on dynamically created db
            // Even if you have enabled hibernate schema generation,
            // it won't work with programmatically generated datasources.
            // Quarkus only runs the Hibernate schema generation, if enabled,
            // on the default datasource.

            // We need a separate flywayDatasourceConfiguration and actual dataSourceConfiguration
            // even though they talk to the same database, because flyway does not play nice with
            // datasource connections that are configured for use with transactions.
            var flywayDataSourceConfiguration = new AgroalDataSourceConfigurationSupplier();
            flywayDataSourceConfiguration
                    .connectionPoolConfiguration()
                    // I don't know why, but the connection pool for flyway has to at least be able to hold
                    // two connections otherwise the thread gets blocked indefinitely.
                    .maxSize(2)
                    .connectionFactoryConfiguration()
                    .jdbcUrl(jdbcUrl);
            var flyway = Flyway
                    .configure()
                    .dataSource(AgroalDataSource.from(flywayDataSourceConfiguration))
                    .cleanDisabled(false)
                    .load();

            flyway.clean();
            flyway.migrate();

            var dataSourceConfiguration = new AgroalDataSourceConfigurationSupplier();
            var poolConfiguration = dataSourceConfiguration.connectionPoolConfiguration();
            var connectionFactoryConfiguration = poolConfiguration.connectionFactoryConfiguration();

            var txIntegration = new NarayanaTransactionIntegration(txManager, txSyncRegistry, null, false, null);

            poolConfiguration
                    .maxSize(1)
                    .minSize(1)
                    .initialSize(1)
                    .maxLifetime(Duration.ofSeconds(10))
                    .acquisitionTimeout(Duration.ofSeconds(3))
                    .transactionIntegration(txIntegration);

            connectionFactoryConfiguration
                    .jdbcUrl(jdbcUrl);

            return new QuarkusConnectionProvider(AgroalDataSource.from(dataSourceConfiguration.get()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
