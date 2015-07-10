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
package at.ac.tuwien.dsg.quelle.cloudServicesModel.util.conversions.helper;

import at.ac.tuwien.dsg.mela.common.monitoringConcepts.Metric;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.MetricValue;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
public class PropertiesAdapter extends XmlAdapter<PropertyEntries, Map<Metric, MetricValue>> {

    @Override
    public Map<Metric, MetricValue> unmarshal(PropertyEntries in) throws Exception {
        HashMap<Metric, MetricValue> hashMap = new HashMap<Metric, MetricValue>();
        for (PropertyEntry entry : in.entries()) {
            hashMap.put(entry.getMetric(), entry.getValue());
        }
        return hashMap;
    }

    @Override
    public PropertyEntries marshal(Map<Metric, MetricValue> map) throws Exception {
        PropertyEntries props = new PropertyEntries();
        for (Map.Entry<Metric, MetricValue> entry : map.entrySet()) {
            props.addEntry(new PropertyEntry(entry.getKey(), entry.getValue()));
        }
        return props;
    }
}
