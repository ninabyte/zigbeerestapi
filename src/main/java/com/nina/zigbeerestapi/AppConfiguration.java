package com.nina.zigbeerestapi;

import com.nina.zigbeerestapi.serialcomm.SerialCommunication;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
//import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AppConfiguration extends Configuration {
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    /*
    @NotNull
    private SerialCommunication serialComm = new SerialCommunication("COM15");
    */
    /*
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();
    */
    
    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    /*
    @JsonProperty("serialComm")
    public void setSerialCommunication(SerialCommunication serialComm){
        this.serialComm = serialComm;
    }

    @JsonProperty("serialComm")
    public SerialCommunication getSerialCommunication(){
        return serialComm;
    }
    */

}