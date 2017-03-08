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
package at.ac.tuwien.dsg.salsa.engine.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.salsa.engine.exceptions.EngineMisconfiguredException;
import at.ac.tuwien.dsg.salsa.engine.services.enabler.PioneerManager;
import at.ac.tuwien.dsg.salsa.engine.utils.ActionIDManager;
import at.ac.tuwien.dsg.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.salsa.model.salsa.info.PioneerInfo;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import java.io.File;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Path("/manager")
@Api(value = "Salsa Pioneer Management API")
@SwaggerDefinition(
        info = @Info(
                title = "Salsa Pioneer Management API",
                version = "3.0",
                description = "This API provides functions manage salsa pioneers.",
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")),
        tags = @Tag(name = "Public", description = "This API is for public usage"),
        schemes = (SwaggerDefinition.Scheme.HTTP),
        consumes = {"application/json"},
        produces = {"application/json"}
)
public class InternalManagement {

    Logger LOGGER = LoggerFactory.getLogger("salsa");

    /**
     * Get the list of all pioneers which are engaged with this salsa-engine
     *
     * @return The human-readable text of pioneers
     */
    @GET
    @Path("/pioneers/nice")
    @ApiOperation(value = "Get list of pioneers saved in salsa engine cache.",
            notes = "This list may not be up-to-date.",
            response = Response.class,
            responseContainer = "String")
    public String getPioneers() {
        LOGGER.debug("Getting pioneer");
        return PioneerManager.describe();
    }

    @GET
    @Path("/pioneers/json")
    @ApiOperation(value = "Get pull list and pull information of all pioneers.",
            notes = "The list can be very long.",
            response = Response.class,
            responseContainer = "String")
    public String getPioneersJson() {
        LOGGER.debug("Getting pioneer");
        Map<String, PioneerInfo> pioneerMap = PioneerManager.getPioneerMap();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(pioneerMap);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return PioneerManager.describe();
    }

    @GET
    @Path("/pioneer/shutdown/{pioneerID}")
    @ApiOperation(value = "Send a signal to tell a pioneer to shutdown itself.",
            notes = "Used in some undeployment cases.",
            response = Response.class,
            responseContainer = "String")
    public String shutdownPioneer(@PathParam("pioneerID") String pioneerID) {
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_shutdownPioneer, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.getPioneerTopicByID(pioneerID), "", "");
        MessageClientFactory factory = MessageClientFactory.getFactory(SalsaConfiguration.getBroker(), SalsaConfiguration.getBrokerType());
        MessagePublishInterface publish = factory.getMessagePublisher();
        publish.pushMessage(msg);
        return "Sent shutdown message to pioneer: " + pioneerID;
    }

    /**
     * Get the list of pending actions
     *
     * @return
     */
    @GET
    @Path("/actions/cache")
    @ApiOperation(value = "Get the actions which are pending and waiting for pioneers to execute.",
            notes = "The actions can be sent or not be sent.",
            response = Response.class,
            responseContainer = "String")
    public String getActions() {
        return ActionIDManager.describe();
    }

    @GET
    @Path("/syn")
    @ApiOperation(value = "Send a message to discover pioneers in the communication space.",
            notes = "This function is asynchronous, so it take some time to get the result.",
            response = Response.class,
            responseContainer = "String")
    public String synPioneer() throws SalsaException {
        PioneerManager.removeAllPioneerInfo();
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.discover, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.PIONEER_REGISTER_AND_HEARBEAT, "", "toDiscoverPioneer");
        MessagePublishInterface publish = SalsaConfiguration.getMessageClientFactory().getMessagePublisher();
        publish.pushMessage(msg);
        return "Syn message published to the queue.";
    }

    /**
     * @return @throws SalsaException
     */
    @GET
    @Path("/artifacts/pioneer")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @ApiOperation(value = "Download the salsa-pioneer.jar from salsa engine.",
            notes = "Configuration script use this to bootstrap the infrastructure.",
            response = Response.class,
            responseContainer = "String")
    public Response getPioneerArtifact() throws SalsaException {
        LOGGER.debug("Getting pioneer artifact and return: " + SalsaConfiguration.getPioneerLocalFile());
        File file = new File(SalsaConfiguration.getPioneerLocalFile());
        String fileName = file.getName();
        if (file.exists()) {
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .build();
        }
        throw new EngineMisconfiguredException(fileName, "Not found the pioneer.jar artifact: " + SalsaConfiguration.getPioneerLocalFile());
    }

    /**
     * This return the bootstrap script for the piooner. The script usually
     * contains environment preparation, e.g. install suitable JRE
     *
     * @return The content of the script file
     * @throws SalsaException
     */
    @GET
    @Path("/artifacts/pioneerbootstrap")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @ApiOperation(value = "Get the bootstrap script of the pioneer.",
            notes = "The script contains Java installation",
            response = Response.class,
            responseContainer = "String")
    public Response getPioneerBootstrapScript() throws SalsaException {
        LOGGER.debug("Getting conductor artifact and return: " + SalsaConfiguration.getConductorLocalFile());
        File file = new File(SalsaConfiguration.getPioneerBootstrapScriptLocalFile());
        String fileName = file.getName();
        if (file.exists()) {
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .build();
        }
        throw new EngineMisconfiguredException(fileName, "Not found the bootstrap script artifact at: " + SalsaConfiguration.getPioneerBootstrapScript());
    }

}
