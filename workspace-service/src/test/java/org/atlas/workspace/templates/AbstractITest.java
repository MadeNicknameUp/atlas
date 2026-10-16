package org.atlas.workspace.templates;

import org.atlas.workspace.config.TestType;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Tag(TestType.ITEST)
public abstract class AbstractITest {
}

