/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author hungld
 */
public class test {

    public static void main(String[] args) {
        test test = new test();
        System.out.println(test.getTest());
        test.change(test);
        System.out.println(test.getTest());
    }

    String test = "old value";

    static public void change(test obj) {
        obj.setTest("new value");
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

}
