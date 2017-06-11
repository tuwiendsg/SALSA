/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author hungld
 */
public class DeploymentFileElement {

    int num;
    List<String> pioneers;

    public DeploymentFileElement() {
    }

    public DeploymentFileElement(int num, String... pioneers) {
        this.num = num;
        this.pioneers = Arrays.asList(pioneers);
    }

    public DeploymentFileElement(int num, List<String> pioneers) {
        this.num = num;
        this.pioneers = pioneers;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<String> getPioneers() {
        return pioneers;
    }

    public void setPioneers(List<String> pioneers) {
        this.pioneers = pioneers;
    }

}
