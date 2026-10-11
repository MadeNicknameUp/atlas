package org.atlas.userservice.templates;

import org.atlas.userservice.config.TestcontainersConfiguration;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
public abstract class AbstractITestWithDatabase extends AbstractITest {
}
