/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper;

import at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.impl.CloudServiceMapper;
import at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.impl.ServiceInstanceMapper;
import at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.impl.ServiceTopologyMapper;
import at.ac.tuwien.dsg.salsa.database.orientdb.DTOMapper.impl.ServiceUnitMapper;
import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;

/**
 *
 * @author hungld
 */
public class MapperFactory {

    public static DTOMapperInterface getMapper(Class clazz) {
        String name = clazz.getSimpleName();
        System.out.println(name);

        if (clazz.equals(CloudService.class)) {
            return new CloudServiceMapper();
        }

        if (clazz.equals(ServiceTopology.class)) {
            return new ServiceTopologyMapper();
        }

        if (clazz.equals(ServiceUnit.class)) {
            return new ServiceUnitMapper();
        }

        if (clazz.equals(ServiceInstance.class)) {
            return new ServiceInstanceMapper();
        }

        System.out.println("Do not found the mapper to persist DB for: " + name);
        return null;

    }

}
