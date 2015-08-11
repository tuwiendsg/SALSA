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
package at.ac.tuwien.dsg.cloud.salsa;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.eclipse.camf.carl.antlr4.CARLLexer;
import org.eclipse.camf.carl.antlr4.CARLParser;
import org.eclipse.camf.carl.antlr4.CARLParser.RequirementsContext;
import org.eclipse.camf.carl.antlr4.CARLProgramListener;
import org.eclipse.camf.carl.model.IRequirement;
import org.eclipse.camf.carl.model.OSRequirement;
import org.eclipse.camf.carl.model.RequirementCategory;
import org.eclipse.camf.carl.model.Requirements;

/**
 *
 * @author Duc-Hung LE
 */
public class TestCALF {
    

  public static void main( String[] args ) {
    CharStream stream = new ANTLRInputStream( "sys:cpu=[5-6];sys:disk=10G;sys:mem=24G;os=\"Ubuntu\";os:ver=12.04;os:arch=\"x86-64\";sw=\"java\":1.7.0;" );
    CARLLexer lexer = new CARLLexer(stream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CARLParser parser = new CARLParser(tokens);
    RequirementsContext requirementsContext = parser.requirements();
     
    ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
    CARLProgramListener extractor = new CARLProgramListener( parser );    
    walker.walk(extractor, requirementsContext); // initiate walk of tree with listener    
    Requirements requirements = extractor.getRequirements();
    
    System.out.println("-----------");
    // Iterating over all requirements
    
    for( IRequirement req : requirements.getRequirements() ) {
      //System.out.println( req.toString() );      
        if (req.getCategory().equals(RequirementCategory.OPERATING_SYSTEM)){
            OSRequirement osr = (OSRequirement) req;
            System.out.println(osr.getName());
            System.out.println(osr.getVersion());
        }
    }
    System.out.println("-----------");
  }

}
