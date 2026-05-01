package com.fabulous.orchestrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrchestratorServiceApplication {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(OrchestratorServiceApplication.class);

        SpringApplication.run(OrchestratorServiceApplication.class, args);

        logger.info("Orchestrator Service is running...");
    }
}
