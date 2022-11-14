package de.hamburgchimps;

import io.quarkus.hibernate.orm.PersistenceUnitExtension;
import io.quarkus.hibernate.orm.runtime.tenant.TenantResolver;
import io.quarkus.logging.Log;
import io.vertx.ext.web.RoutingContext;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Random;
import java.util.UUID;

// As far as I can tell, this plumbing becomes necessary if quarkus
// detects that a class that implements TenantConnectionResolver
// Also as far as I can tell, quarkus only bothers calling resolveTenantId() if
// there is an active request context. Otherwise, only getDefaultTenantId() is invoked.
@PersistenceUnitExtension
@ApplicationScoped
public class ProgrammaticResolver implements TenantResolver {
    private final Random random;

    public ProgrammaticResolver() {
        this.random = new Random();
    }

    @Override
    public String getDefaultTenantId() {
        return "tenant-default";
    }

    @Override
    public String resolveTenantId() {
        return String.format("tenant-%d", random.nextInt());
    }
}
