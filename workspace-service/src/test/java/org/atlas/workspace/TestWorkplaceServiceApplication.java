package org.atlas.workspace;

import org.springframework.boot.SpringApplication;

public class TestWorkplaceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(WorkspaceServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
