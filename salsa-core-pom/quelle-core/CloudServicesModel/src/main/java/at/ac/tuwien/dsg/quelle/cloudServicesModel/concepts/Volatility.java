/**
 * Copyright 2013 Technische Universitaet Wien (TUW), Distributed Systems Group
 * E184
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package at.ac.tuwien.dsg.quelle.cloudServicesModel.concepts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Daniel Moldovan E-Mail: d.moldovan@dsg.tuwien.ac.at
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Volatility")
public class Volatility extends Unit implements Cloneable {

    @XmlAttribute(name = "minimumLifetimeInHours", required = true)
    private int minimumLifetimeInHours;

    @XmlAttribute(name = "maxNrOfChanges", required = true)
    private double maxNrOfChanges;

    public Volatility() {
    }

    public Volatility(String name) {
        super(name);
    }

    public Volatility(int minimumLifetimeInHours, double maxNrOfChanges) {
        this.minimumLifetimeInHours = minimumLifetimeInHours;
        this.maxNrOfChanges = maxNrOfChanges;
    }

    public int getMinimumLifetimeInHours() {
        return minimumLifetimeInHours;
    }

    public void setMinimumLifetimeInHours(int minimumLifetimeInHours) {
        this.minimumLifetimeInHours = minimumLifetimeInHours;
    }

    public double getMaxNrOfChanges() {
        return maxNrOfChanges;
    }

    public void setMaxNrOfChanges(double maxNrOfChanges) {
        this.maxNrOfChanges = maxNrOfChanges;
    }

    /**
     *
     * @return 1 if we have NO volatility, and maxNrOfChanges /
     * minimumLifetimeInHours otherwise
     */
    public double getVolatility() {
        return (maxNrOfChanges > 0 && minimumLifetimeInHours > 0) ? maxNrOfChanges / minimumLifetimeInHours : 1;
    }

    public Volatility withMinimumLifetime(int minimumLifetimeInHours) {
        this.minimumLifetimeInHours = minimumLifetimeInHours;
        return this;
    }

    public Volatility withMaxNrOfChanges(double maxNrOfChanges) {
        this.maxNrOfChanges = maxNrOfChanges;
        return this;
    }

}
