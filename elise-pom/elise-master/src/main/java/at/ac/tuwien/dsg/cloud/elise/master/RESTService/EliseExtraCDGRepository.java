/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.RESTService;

import at.ac.tuwien.dsg.cloud.elise.model.extra.contract.ContractTemplate;
import at.ac.tuwien.dsg.cloud.elise.model.extra.contract.ContractTerm;
import at.ac.tuwien.dsg.cloud.elise.model.extra.contract.ContractTermType;
import at.ac.tuwien.dsg.cloud.elise.model.extra.contract.Script;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author peter
 */
@Path("/extracdg")
public interface EliseExtraCDGRepository {
    
/** CRUD for scripts **/
    
    /**
     * Read information about script
     * @param name the name of script
     * @return An script object
     */
    @GET
    @Path("/script/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    Script readScript(@PathParam("name") String name);

    /**
     * Get a list of all available script
     * @return A list of scripts
     */
    @GET
    @Path("/script")
    @Produces(MediaType.APPLICATION_JSON)
    Set<Script> readAllScripts();

    /**
     * To save a script
     * @param script the information
     * @return the assigned id of script 
     */
    @POST
    @Path("/script")
    @Consumes(MediaType.APPLICATION_JSON)
    String saveScript(Script script);

    /**
     * Delete information of script from DB
     * @param name The name of the script to be deleted
     */
    @DELETE
    @Path("/script/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    void deleteScript(@PathParam("name") String name);
    
    /** CRUD for contract term **/
    
    /**
     * Read information about contract term
     * @param name the name of contract term
     * @return A contract term object
     */
    @GET
    @Path("/contractterm/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    ContractTerm readContractTerm(@PathParam("name") String name);

    /**
     * Get a list of all available contract term
     * @return A list of contract term
     */
    @GET
    @Path("/contractterm")
    @Produces(MediaType.APPLICATION_JSON)
    Set<ContractTerm> readAllContractTerms();

    /**
     * To save a contractterm
     * @param contractterm the information
     * @return the assigned id of contractterm 
     */
    @POST
    @Path("/contractterm")
    @Consumes(MediaType.APPLICATION_JSON)
    String saveContractTerm(ContractTerm contractterm);

    /**
     * Delete information of contractterm from DB
     * @param name The name of the contractterm to be deleted
     */
    @DELETE
    @Path("/contractterm/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    void deleteContractTerm(@PathParam("name") String name);
    
    /** CRUD for contract term type **/
    
    /**
     * Read information about contract term type
     * @param name the name of contract term type
     * @return A contract term type object
     */
    @GET
    @Path("/contracttermtype/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    ContractTermType readContractTermType(@PathParam("name") String name);

    /**
     * Get a list of all available contract term type
     * @return A list of contract term type
     */
    @GET
    @Path("/contracttermtype")
    @Produces(MediaType.APPLICATION_JSON)
    Set<ContractTermType> readAllContractTermTypes();

    /**
     * To save a contracttermtype
     * @param contracttermtype the information
     * @return the assigned id of contracttermtype 
     */
    @POST
    @Path("/contracttermtype")
    @Consumes(MediaType.APPLICATION_JSON)
    String saveContractTermType(ContractTermType contracttermtype);

    /**
     * Delete information of contracttermtype from DB
     * @param name The name of the contracttermtype to be deleted
     */
    @DELETE
    @Path("/contracttermtype/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    void deleteContractTermType(@PathParam("name") String name);
    
    /** CRUD for contract template**/
    
    /**
     * Read information about contract template
     * @param name the name of contract template
     * @return A contract template object
     */
    @GET
    @Path("/contracttemplate/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    ContractTemplate readContractTemplate(@PathParam("name") String name);

    /**
     * Get a list of all available contract template
     * @return A list of contract template
     */
    @GET
    @Path("/contracttemplate")
    @Produces(MediaType.APPLICATION_JSON)
    Set<ContractTemplate> readAllContractTemplates();

    /**
     * To save a contracttemplate
     * @param contracttemplate the information
     * @return the assigned id of contracttemplate 
     */
    @POST
    @Path("/contracttemplate")
    @Consumes(MediaType.APPLICATION_JSON)
    String saveContractTemplate(ContractTemplate contracttemplate);

    /**
     * Delete information of contracttemplate from DB
     * @param name The name of the contracttemplate to be deleted
     */
    @DELETE
    @Path("/contracttemplate/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    void deleteContractTemplate(@PathParam("name") String name);
    
    // health and generic stuff
    @GET
    @Path("/health")
    public String health();
        
}
