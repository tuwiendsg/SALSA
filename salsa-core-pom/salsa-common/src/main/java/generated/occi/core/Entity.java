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


import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



/**
 * The root type for all resources as described in the OCCI Core Model.
 * <p/>
 * The <code>Entity</code> class represents the root type in the OCCI Core
 * Model type system. All OCCI-compliant resources are derived from this class.
 * It exposes mechanisms for managing an OCCI resource with respect to its
 * fundamental attributes, ({@link #id} and {@link #title}, and provides
 * facilities for managing a resource's mixins.
 * <p/>
 * This <code>Entity</code> exposes methods for the generic management of more
 * specific OCCI resource sub-types, including
 * <ul>
 * <li>attachment and detachment of mixins</li>
 * <li>retrieval and manipulation of attributes, and</li>
 * <li>invocation of actions.</li>
 * </ul>
 * In addition, it ensures the correct attribution in the OCCI term/scheme
 * model.
 *
 * @author <a href="mailto:alexander.papaspyrou@tu-dortmund.de">Alexander
 *         Papaspyrou</a>
 * @version $Id$
 * @see "Ralf Nyrén, Andy Edmonds, Alexander Papaspyrou, and Thijs Metsch, <a
 *      href="http://ogf.org/documents/GFD.183.pdf">Open Cloud Computing
 *      Interface &ndash; Core</a>, Open Grid Forum Proposed Recommendation,
 *      GFD-P-R.183, April 2011, Section 4.5.1"
 * @since 0.3 ("gordons")
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entity")
public abstract class Entity {

    /**
     * A unique identifier (within the service provider's namespace) of this
     * Entity.
     */
   
    @XmlElement(name = "id")
    private URI id;

    /**
     * The display name of this entity.
     */
    
    @XmlElement(name = "title")
    private String title;

    /**
     * A map of mixins currently attached to this entity. Objects contained in
     * this set can be expected to carry a {@link Mixin} annotation and
     * corresponding information for usage.
     * <p/>
     * The key to the map is a string concatenated as
     * <code>&lt;scheme&gt;&lt;term&gt;</code>.
     *
     * @see #attachMixin(Object)
     */
    private Map<String, Object> mixins;

    /**
     * Creates a new instance of this class.
     * <p/>
     * During creation, a random identifier for this entity is generated using
     * an <a href="http://www.ietf.org/rfc/rfc4122.txt">RFC
     * 4122-compliant UUID</a> in the corresponding URN namespace
     * (<code>urn:uuid:</code><i>uuid</i>).
     */
    protected Entity() {
        this(URI.create(new StringBuffer("urn:uuid:").append(UUID.randomUUID().toString()).toString()));
    }

    /**
     * Creates a new instance of this class, using the given parameters.
     *
     * @param id
     *         The unique identifier to be used for this class. It is
     *         <b>recommended</b> that an
     *         <a href="http://www.ietf.org/rfc/rfc4122.txt" RFC 4122-compliant
     *         UUID</a> is used and the appropriate namespace ("urn:uuid") is
     *         exposed.
     */
    protected Entity(URI id) {
        this.id = id;
        this.mixins = new HashMap<String, Object>();
    }

    /**
     * Returns the unique identifier of this entity.
     *
     * @return The unique identifier of this entity.
     */
    public URI getId() {
        return id;
    }

    /**
     * Returns the title of this entity.
     *
     * @return The title of this entity.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this entity.
     *
     * @param title
     *         The new title of this entity.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Provides a set of currently registered mixins. A copy which is
     * unmodifiable is returned.
     *
     * @return An unmodifiable set of currently registered mixins.
     */
    public Collection<Object> getMixins() {
        return Collections.unmodifiableCollection(this.mixins.values());
    }

    

}
