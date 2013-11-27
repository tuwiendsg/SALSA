/*
 * This file is part of tuOCCI.
 *
 *     tuOCCI is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     tuOCCI is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with tuOCCI.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This file is part of tuOCCI.
 *
 *     tuOCCI is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     tuOCCI is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with tuOCCI.  If not, see <http://www.gnu.org/licenses/>.
 */

// $Id$ //

package generated.occi.infrastructure.compute;

import generated.occi.core.Resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Compute")
@XmlRootElement(name = "Compute")
public class Compute
        extends Resource {

    @XmlElement(name = "architecture")
    private Architecture architecture;

    @XmlElement(name = "cores")
    private Integer cores;

    @XmlElement(name = "hostname")
    private String hostname;
    
    @XmlElement(name = "speed")
    private Float speed;

    @XmlElement(name = "memory")
    private Float memory;

    @XmlElement(name = "state")
    private State state;

    public Architecture getArchitecture() {
        return architecture;
    }

    public void setArchitecture(Architecture architecture) {
        this.architecture = architecture;
    }

    public Integer getCores() {
        return cores;
    }

    public void setCores(Integer cores) {
        this.cores = cores;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getMemory() {
        return memory;
    }

    public void setMemory(Float memory) {
        this.memory = memory;
    }

    public State getState() {
        return state;
    }

    public void start() {

    }

    public void stop( StateChangeMethod method) {
        switch (method) {
            case GRACEFUL:
                // TODO: implement
            case ACPIOFF:
                // TODO: implement
            case POWEROFF:
                // TODO: implement
            default:
                String message = new StringBuffer("not allowed: method '")
                        .append(method).append("'.").toString();
                throw new IllegalArgumentException(message);
        } // switch
    }

    public void restart(StateChangeMethod method) {
        switch (method) {
            case GRACEFUL:
                // TODO: implement
            case WARM:
                // TODO: implement
            case COLD:
                // TODO: implement
            default:
                String message = new StringBuffer("not allowed: method '")
                        .append(method).append("'.").toString();
                throw new IllegalArgumentException(message);
        } // switch
    }

    public void suspend(StateChangeMethod method) {
        switch (method) {
            case HIBERNATE:
                // TODO: implement
            case SUSPEND:
                // TODO: implement
            default:
                String message = new StringBuffer("not allowed: method '")
                        .append(method).append("'.").toString();
                throw new IllegalArgumentException(message);
        } // switch
    }

    /**
     * TODO: not yet commented.
     *
     * @author <a href="mailto:alexander.papaspyrou@tu-dortmund.de>Alexander
     *         Papaspyrou</a>
     * @version $Revision$ (as of $Date$)
     */
    public static enum Architecture {

        X86(),

        X64()

    }

    /**
     * TODO: not yet commented.
     *
     * @author <a href="mailto:alexander.papaspyrou@tu-dortmund.de>Alexander
     *         Papaspyrou</a>
     * @version $Revision$ (as of $Date$)
     */
    public static enum State {

        ACTIVE(),

        INACTIVE(),

        SUSPENDED()

    }


}
