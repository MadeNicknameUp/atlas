package org.atlas.workplaceservice;

import org.springframework.boot.SpringApplication;

public class TestWorkplaceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(WorkplaceServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
