/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.transformopeniotsensor;

import java.util.Map;

/**
 *
 * @author hungld
 */
public class SensorData {

    Measure ms;
    String st;

    public static class Measure {

        int v;
        String p;
        String u;

        public Measure() {
        }

        public int getV() {
            return v;
        }

        public void setV(int v) {
            this.v = v;
        }

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }

        public String getU() {
            return u;
        }

        public void setU(String u) {
            this.u = u;
        }
    }

    public SensorData() {
    }

    public Measure getMs() {
        return ms;
    }

    public void setMs(Measure ms) {
        this.ms = ms;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

}
