# Quarkus Programmatic Datasource Configuration

:rotating_light: **We recommend not using this repository as an example as there appears to be buggy behavior with programmatially created datasources in regards to transactions. See [this issue](https://github.com/HamburgChimps/quarkus-programmatic-datasource-configuration/issues/1) for more information.

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

## Quarkus auto-generated docs

## quarkus-programmatic-datasource-configuration Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

### Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

### Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/quarkus-programmatic-datasource-configuration-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

### Provided Code

#### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
