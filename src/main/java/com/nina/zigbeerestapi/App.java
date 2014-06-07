package com.nina.zigbeerestapi;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.nina.zigbeerestapi.core.Lights;
import com.nina.zigbeerestapi.serialcomm.SerialCommunication;
import com.nina.zigbeerestapi.resources.*;
import com.nina.zigbeerestapi.health.TemplateHealthCheck;

public class App extends Application<AppConfiguration> {
    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(AppConfiguration configuration,
                    Environment environment) {

        final Lights lights = new Lights();

        final SerialCommunication serialComm = new SerialCommunication("COM15", lights);
        serialComm.init();
        serialComm.startReading();

        final PermitJoinResource resource2 = new PermitJoinResource(
                serialComm, false);
        environment.jersey().register(resource2);

        final LightsResource resource3 = new LightsResource(
            serialComm, lights);
        environment.jersey().register(resource3);

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
    }

}