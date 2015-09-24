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
package at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.SALSA;

import at.ac.tuwien.dsg.cloud.salsa.common.artifact.Artifacts;
import at.ac.tuwien.dsg.cloud.salsa.common.artifact.Repositories;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.ToscaXmlProcess;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TServiceTemplate;
import generated.oasis.tosca.TTopologyTemplate;
import java.io.IOException;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Duc-Hung Le
 */
public class ToscaEnricherCEclipse {
    TDefinitions toscaDef;
    TDefinitions knowledgeBase;
    TTopologyTemplate knowledgeTopo;
    Artifacts artifactList = new Artifacts();
    Repositories repoList = new Repositories();
    
    
    public ToscaEnricherCEclipse(TDefinitions def) {
        this.toscaDef = def;
        try {
            knowledgeBase = ToscaXmlProcess.readToscaFile(ToscaEnricherSALSA.class
                    .getResource("/data/salsa.knowledge.xml").getFile());

            knowledgeTopo = ((TServiceTemplate) knowledgeBase.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0)).getTopologyTemplate();

            artifactList.importFromXML(ToscaEnricherSALSA.class.getResource("/data/salsa.artifacts.xml").getFile());
            repoList.importFromXML(ToscaEnricherSALSA.class.getResource("/data/salsa.repo.xml").getFile());

        } catch (IOException e1) {
            EngineLogger.logger.error("Not found knowledge base file. " + e1);
        } catch (JAXBException e2) {
            EngineLogger.logger.error("Error when parsing knowledge base file. " + e2);
        }
    }
}
