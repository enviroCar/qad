package org.envirocar.qad.reanalyze;

import org.envirocar.qad.AlgorithmParameters;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "org.envirocar.qad")
@EnableConfigurationProperties({AlgorithmParameters.class})
public class ReanalyzeApplication {

    public static final String REANALYZE_PROFILE = "reanalyze";

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", REANALYZE_PROFILE);
        SpringApplication.run(ReanalyzeApplication.class, args);
    }

}
