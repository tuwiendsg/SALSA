
package com.extl.jade.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for jobStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="jobStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SUCCESSFUL"/>
 *     &lt;enumeration value="IN_PROGRESS"/>
 *     &lt;enumeration value="FAILED"/>
 *     &lt;enumeration value="CANCELLED"/>
 *     &lt;enumeration value="SUSPENDED"/>
 *     &lt;enumeration value="NOT_STARTED"/>
 *     &lt;enumeration value="WAITING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "jobStatus")
@XmlEnum
public enum JobStatus {


    /**
     * The job completed successfully
     * 
     */
    SUCCESSFUL,

    /**
     * The job is currently in progress
     * 
     */
    IN_PROGRESS,

    /**
     * The job failed
     * 
     */
    FAILED,

    /**
     * The job was cancelled
     * 
     */
    CANCELLED,

    /**
     * The job has been suspended
     * 
     */
    SUSPENDED,

    /**
     * The job has not yet started (for instance because it has been scheduled for a future time)
     * 
     */
    NOT_STARTED,

    /**
     * The job is waiting for a subsidiary action to complete (e.g. a child job)
     * 
     */
    WAITING;

    public String value() {
        return name();
    }

    public static JobStatus fromValue(String v) {
        return valueOf(v);
    }

}
