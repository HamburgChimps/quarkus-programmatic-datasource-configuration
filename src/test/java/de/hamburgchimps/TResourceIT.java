package de.hamburgchimps;

import io.quarkus.test.junit.QuarkusIntegrationTest;

// This test is called TResourceIT and not TestResourceIT because
// otherwise surefire would attempt to run it, which results in an error.
// This is because at the time surefire runs, the packaged jar that this
// test needs in order to run has not been built yet.
@QuarkusIntegrationTest
public class TResourceIT extends TestResourceTest {
    // Execute the same tests but in packaged mode.
}
