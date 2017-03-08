/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model.salsa.info;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author hungld
 */
public class SalsaEvents {

    List<SalsaEvent> events = new ArrayList<>();
    Date minStart;
    Date maxEnd;

    public SalsaEvents hasEvent(SalsaEvent event) {
        if (minStart == null) {
            minStart = event.getStart();
        }
        maxEnd = event.getEnd();
        events.add(event);
        return this;
    }

    public List<SalsaEvent> getEvents() {
        return events;
    }

    public void setEvents(List<SalsaEvent> events) {
        this.events = events;
    }

    public Date getMinStart() {
        return minStart;
    }

    public void setMinStart(Date minStart) {
        this.minStart = minStart;
    }

    public Date getMaxEnd() {
        return maxEnd;
    }

    public void setMaxEnd(Date maxEnd) {
        this.maxEnd = maxEnd;
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

    public static SalsaEvents fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, SalsaEvents.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
