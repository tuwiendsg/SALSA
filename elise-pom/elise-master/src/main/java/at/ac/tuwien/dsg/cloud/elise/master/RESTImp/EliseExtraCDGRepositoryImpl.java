/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.RESTImp;

import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.ContractTemplateRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.ContractTermRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.ContractTermTypeRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.neo4jAccess.ScriptRepository;
import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseExtraCDGRepository;
import at.ac.tuwien.dsg.cloud.elise.model.extra.contract.ContractTemplate;
import at.ac.tuwien.dsg.cloud.elise.model.extra.contract.ContractTerm;
import at.ac.tuwien.dsg.cloud.elise.model.extra.contract.ContractTermType;
import at.ac.tuwien.dsg.cloud.elise.model.extra.contract.Script;
import java.util.Set;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author peter
 */
public class EliseExtraCDGRepositoryImpl implements EliseExtraCDGRepository {
    
    Logger logger = EliseConfiguration.logger;
    
    @Autowired
    ScriptRepository scriptRepo;
    
    @Autowired
    ContractTermTypeRepository contracttermtypeRepo;
    
    @Autowired
    ContractTermRepository contracttermRepo;
    
    @Autowired
    ContractTemplateRepository contracttemplateRepo;
    
    @Override
    public Script readScript(String name) {
        return scriptRepo.findByName(name);
    }

    @Override
    public Set<Script> readAllScripts() {
        return scriptRepo.listScripts();
    }

    @Override
    public String saveScript(Script script) {
        if (scriptRepo == null) {
            logger.error("Cannot load ScriptRepository !");
            return null;
        }
        
        Script existingScript = scriptRepo.findByName(script.getName());
        if (existingScript == null) {
            existingScript = script;
        } else {
            if (!(script.getCode() == null)) {
                existingScript.setCode(script.getCode());
            }
            if (!(script.getVersion() == null)) {
                existingScript.setVersion(script.getVersion());
            }
        }

        logger.debug("Prepare to write script: name=" + existingScript.getName());
        Script s = scriptRepo.save(existingScript);
        return "Saved the script to graph with name: " + s.getName();
    }

    @Override
    public void deleteScript(String name) {
        Script s = scriptRepo.findByName(name);
        if (s != null) {
            scriptRepo.delete(s);
        }
    }
    
    @Override
    public ContractTerm readContractTerm(String name) {
        return contracttermRepo.findByName(name);
    }

    @Override
    public Set<ContractTerm> readAllContractTerms() {
        return contracttermRepo.listContractTerms();
    }

    @Override
    public String saveContractTerm(ContractTerm ct) {
        if (contracttermRepo == null) {
            logger.error("Cannot load ContractTermRepository !");
            return null;
        }
        
        ContractTerm existingct = contracttermRepo.findByName(ct.getName());
        if (existingct == null) {
            existingct = ct;
        } else {
            if (!(ct.getType() == null)){
                existingct.setType(ct.getType());
            }
            if (!(ct.getConstraints() == null)){
                existingct.setConstraints(ct.getConstraints());
            }
        }

        logger.debug("Prepare to write contractterm: name=" + ct.getName());
        ContractTerm c = contracttermRepo.save(existingct);
        return "Saved the contractterm to graph with id: " + c.getName();
    }

    @Override
    public void deleteContractTerm(String name) {
        ContractTerm c = contracttermRepo.findByName(name);
        if (c != null) {
            contracttermRepo.delete(c);
        }
    }
    
    @Override
    public ContractTermType readContractTermType(String name) {
        return contracttermtypeRepo.findByName(name);
    }

    @Override
    public Set<ContractTermType> readAllContractTermTypes() {
        return contracttermtypeRepo.listContractTermTypes();
    }

    @Override
    public String saveContractTermType(ContractTermType ctt) {
        if (contracttermtypeRepo == null) {
            logger.error("Cannot load ContractTermTypeRepository !");
            return null;
        }
        
        ContractTermType existingctt = contracttermtypeRepo.findByName(ctt.getName());
        if (existingctt == null) {
            existingctt = ctt;
        } else {
            if (!(ctt.getDescription() == null)){
                existingctt.setDescription(ctt.getDescription());
            }
        }

        logger.debug("Prepare to write contracttermtype: name=" + existingctt.getName());
        ContractTermType c = contracttermtypeRepo.save(existingctt);
        return "Saved the contracttermtype to graph with name: " + c.getName();
    }

    @Override
    public void deleteContractTermType(String name) {
        //this.contracttermtypeRepo.deleteContractTermTypeCompletelyByID(name);
        ContractTermType c = contracttermtypeRepo.findByName(name);
        if (c != null) {
            contracttermtypeRepo.delete(c);
        }
    }
    
    @Override
    public ContractTemplate readContractTemplate(String name) {
        return contracttemplateRepo.findByName(name);
    }

    @Override
    public Set<ContractTemplate> readAllContractTemplates() {
        return contracttemplateRepo.listContractTemplates();
    }

    @Override
    public String saveContractTemplate(ContractTemplate ct) {
        if (contracttemplateRepo == null) {
            logger.error("Cannot load ContractTemplateRepository !");
            return null;
        }
        
        ContractTemplate existingct = contracttemplateRepo.findByName(ct.getName());
        if (existingct == null) {
            existingct = ct;
        } else {
            if (!(ct.getTerms() == null)){
                existingct.setTerms(ct.getTerms());
            }
        }

        logger.debug("Prepare to write contracttemplate: name=" + ct.getName());
        ContractTemplate c = contracttemplateRepo.save(existingct);
        return "Saved the contracttemplate to graph with id: " + c.getName();
    }

    @Override
    public void deleteContractTemplate(String name) {
        ContractTemplate c = contracttemplateRepo.findByName(name);
        if (c != null) {
            contracttemplateRepo.delete(c);
        }
    }
    
    @Override
    public String health() {
        System.out.println("ExtraCDG is up");
        return EliseConfiguration.getEliseID();
    }

}
