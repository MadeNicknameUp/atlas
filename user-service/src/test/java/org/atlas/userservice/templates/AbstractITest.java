package org.atlas.userservice.templates;

import org.atlas.userservice.config.TestType;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Tag(TestType.ITEST)
public abstract class AbstractITest {
}
