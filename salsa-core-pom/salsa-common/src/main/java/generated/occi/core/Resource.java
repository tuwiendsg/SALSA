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

package generated.occi.core;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resource")
public class Resource
        extends Entity {

    /**
     * A summarizing description of the Resource instance.
     */
   
    @XmlElement(name = "summary")
    private String summary;

    /**
     * A set of {@link Link} compositions.
     */
    @XmlElement(name = "links")
    private Set<Link> links;

    /**
     * Returns the summarizing description of this resource.
     *
     * @return The summarizing description of this resource.
     */    
    public String getSummary() {
        return summary;
    }

    /**
     * Modifies the summarizing description of this resource.
     *
     * @param summary
     *         The new value for the summarizing description of this
     *         resource.
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Returns a set of currently associated links for this resource.
     *
     * @return A set of currently associated links for this resource.
     */
    public Set<Link> getLinks() {
        return links;
    }

    /**
     * Changes the set of currently associated links for this resource.
     *
     * @param links
     *         The new set of currently associated links for this resource.
     */
    public void setLinks(Set<Link> links) {
        this.links = links;
    }

    
}
