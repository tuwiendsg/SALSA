/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group E184

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package generated.JaxbGangliaEntities;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EXTRA_DATA")
public class GangliaExtraDataInfo {

    @XmlElement(name = "EXTRA_ELEMENT")
    private Collection<GangliaExtraElementInfo> gangliaExtraElementInfo;

    public Collection<GangliaExtraElementInfo> getGangliaExtraElementInfo() {
        return gangliaExtraElementInfo;
    }

    public void setGangliaExtraElementInfo(Collection<GangliaExtraElementInfo> gangliaExtraElementInfo) {
        this.gangliaExtraElementInfo = gangliaExtraElementInfo;
    }

    @Override
    public String toString() {
        String info = "ExtraDataInfo{" +
                "ExtraElementInfo=";
        for (GangliaExtraElementInfo elementInfo : gangliaExtraElementInfo) {
            info += "\t " + elementInfo + "\n";
        }
        info += '}';
        return info;
    }
}