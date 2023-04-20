# Quarkus Programmatic Datasource Configuration

I couldn't find any direct examples on how to programmatically configure a datasource with Quarkus in the docs, only
a reference to [TenantConnectionResolver][0] and a [GitHub issue asking about it][1]. Armed with these helpful, albeit
vague tips, I figured out how to do it and made this example app to demonstrate it.

Every time a request is made to the `/test` endpoint, the app generates a new H2 database and inserts a `TestEntity` into it.

The Quarkus docs and terminology refer to this capability in the context of multi-tenancy, which makes sense. However, being
able to programmatically create/resolve datasources is conceivably useful for other purposes as well. For example, the whole
reason I started researching how to do this was because I wanted to create an app which used sqlite as a datasource, and the sqlite
file itself could be located in multiple places.

I have tried to annotate the respective configuration and code with comments for specific details of why that code or configuration is
necessary. If you still have questions after reading through them, feel free to open up an issue and I will attempt
to help clarify any confusing parts.

[0]: https://quarkus.io/guides/hibernate-orm#programmatically-resolving-tenants-connections
[1]: https://github.com/quarkusio/quarkus/issues/7019
