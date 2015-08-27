/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.elise.master.RESTImp;

import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.ProviderRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.ProviderDAO;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.IdentificationDB;
import at.ac.tuwien.dsg.cloud.elise.model.provider.Provider;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 *
 * @author Duc-Hung Le
 */
public class ProviderDAOImp  implements ProviderDAO {
    /* MANAGE PROVIDERS */

    
    @Autowired
    ProviderRepository pdrepo;

    @Override
    public Provider getProviderByID(String uniqueID) {
        return pdrepo.findByUniqueID(uniqueID);
//        return new Provider("A new Provider", Provider.ProviderType.IAAS);
    }

    Logger logger = Logger.getLogger(ProviderDAOImp.class);

    @Override
    public String addProvider(Provider provider) {
        if (pdrepo == null) {
            logger.error("Cannot load ProviderRepository !");
            return null;
        }

        logger.debug("Prepare to add provider: ID=" + provider.getId());
        if (provider.getOffering() != null) {
            logger.debug("This provider has " + provider.getOffering().size() + " OSU(s)");
        }
//        for (GenericServiceUnit u : provider.getOffering()) {
//            logger.debug("Prepare to add offering: " + u.getId() + " - " + u.getCategory() );
//            offerServiceDAO.addOfferServiceUnitForProvider(u, provider.getId());
//        }
        Provider r = pdrepo.save(provider);
        return "Saved the provider to graph with id: " + r.getId();
    }

    @Override
    public Set<Provider> getProviders() {
        Set<Provider> providers = pdrepo.listProviders();
        return providers;
    }

    @Override
    public void test() {
        pdrepo.hashCode();
        if (pdrepo == null) {
            logger.debug("pdREPO is null");
        } else {
            logger.debug("pdrepo is CREATED");
        }

    }

    private synchronized void addToIdentificationDB(IdentificationDB idb) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            IdentificationDB currentDB = (IdentificationDB) mapper.readValue(new File(EliseConfiguration.IDENTIFICATION_MAPPING_FILE), IdentificationDB.class);
            currentDB.getIdentifications().addAll(idb.getIdentifications());
            mapper.writeValue(new File(EliseConfiguration.IDENTIFICATION_MAPPING_FILE), currentDB);
        } catch (IOException ex) {

        }
    }


    public void deleteProvider(String providerID) {
        this.pdrepo.deleteProviderCompletelyByID(providerID);
    }

}
