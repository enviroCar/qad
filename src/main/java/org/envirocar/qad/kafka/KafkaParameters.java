package org.envirocar.qad.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public class KafkaParameters {
    private String clientId;
    private String groupId;
    private Bootstrap bootstrap = new Bootstrap();

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Bootstrap getBootstrap() {
        return this.bootstrap;
    }

    public void setBootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public static class Bootstrap {
        private String servers;

        public String getServers() {
            return this.servers;
        }

        public void setServers(String servers) {
            this.servers = servers;
        }
    }
}
