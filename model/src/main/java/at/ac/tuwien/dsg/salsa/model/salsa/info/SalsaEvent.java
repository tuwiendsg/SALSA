
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model.salsa.info;

import java.util.Date;

/**
 *
 * @author hungld
 */
public class SalsaEvent {

    String name;
    Date start;
    Date end;
    String data;

    public SalsaEvent(String name, Date start, Date end, String data) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.data = data;
    }

    public SalsaEvent() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
