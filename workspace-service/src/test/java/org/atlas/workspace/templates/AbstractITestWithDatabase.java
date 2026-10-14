package org.atlas.workspace.templates;

import org.atlas.workspace.config.TestcontainersConfiguration;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
public abstract class AbstractITestWithDatabase extends AbstractITest {

}
