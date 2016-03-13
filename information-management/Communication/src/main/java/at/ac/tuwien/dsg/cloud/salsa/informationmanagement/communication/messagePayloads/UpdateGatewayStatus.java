/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Capability;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungld
 */
public class UpdateGatewayStatus {

        List<Capability> appear = new ArrayList<>();
        List<Capability> disappear = new ArrayList<>();
        long timeStamp;

        public UpdateGatewayStatus() {
        }

        public List<Capability> getAppear() {
            return appear;
        }

        public void setAppear(List<Capability> appear) {
            this.appear = appear;
        }

        public List<Capability> getDisappear() {
            return disappear;
        }

        public void setDisappear(List<Capability> disappear) {
            this.disappear = disappear;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public static UpdateGatewayStatus fromJson(String json) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(json, UpdateGatewayStatus.class);
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        public String toJson() {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
                return null;
            }
        }

    }
