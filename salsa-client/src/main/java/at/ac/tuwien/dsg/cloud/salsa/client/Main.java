/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client;

import at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.eliseAPI.InstanceList;
import at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.eliseAPI.InstanceQuery;
import at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.InstanceRemove;
import at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.InstanceStatus;
import at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.Meta;
import at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.PrintHelp;
import at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.ServiceList;
import at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.ServiceRemove;
import at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.ServiceStatus;
import at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.ServiceSubmit;
import at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.Syn;
import at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp.UnitDeploy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.Properties;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;

/**
 *
 * @author Duc-Hung LE ref: http://fahdshariff.blogspot.co.at/2011/12/args4j-vs-jcommander-for-parsing.html
 */
public class Main {

    public static final String COMMAND_NAME = System.getProperty("app.name", "java -jar salsa-client.jar");
    public static final String COMMAND_DESCRIPTION = "SALSA Java command-line client";

    @Option(name = "-a", aliases = "--address", metaVar = "<address>", usage = "The address of the salsa engine")
    private static String address = "localhost";

    @Option(name = "-p", aliases = "--port", metaVar = "<port>", usage = "The port of the salsa engine")
    private static String port = "8080";

    @Option(name = "-h", aliases = "--help", usage = "Print the help and exit")
    private boolean callingHelp;

    @Argument(handler = SubCommandHandler.class)
    @SubCommands({
        @SubCommand(name = "meta", impl = Meta.class),
        @SubCommand(name = "syn", impl = Syn.class),
        @SubCommand(name = "help", impl = PrintHelp.class),

        @SubCommand(name = "service-submit", impl = ServiceSubmit.class),
        @SubCommand(name = "service-list", impl = ServiceList.class),
        @SubCommand(name = "service-status", impl = ServiceStatus.class),
        @SubCommand(name = "service-remove", impl = ServiceRemove.class),

        @SubCommand(name = "unit-deploy", impl = UnitDeploy.class),

        @SubCommand(name = "instance-status", impl = InstanceStatus.class),
        @SubCommand(name = "instance-remove", impl = InstanceRemove.class),
        @SubCommand(name = "instance-list", impl = InstanceList.class),
        @SubCommand(name = "instance-query", impl = InstanceQuery.class),})
    CommandHandler command;

    static CmdLineParser parserOpt;

    public static void main(String[] args) throws CmdLineException {
//        final String[] testArgs = {"-a", "128.130.172.215", "-p", "8888", "service-remove","test"};        
//        final String[] testArgs = {"-a", "128.130.172.215", "-p", "8888", "service-submit", "/home/hungld/test/wordpress-tosca/workpress.tosca.xml"};
        Main opts = new Main();
        opts.loadConfig();
        parserOpt = new CmdLineParser(opts);
        try {
            CmdLineParser parserOpt = new CmdLineParser(opts);
            parserOpt.parseArgument(args);
            if (opts.command == null) {
                System.out.println("Empty parameters !");
                PrintHelp.printHelp();
            } else {
                opts.command.execute();
            }
        } catch (CmdLineException e) {
            System.out.println("Wrong parameters !");
            PrintHelp.printHelp();
        }
    }

    public static String getSalsaAPI(String path) {
        return "http://" + address + ":" + port + "/salsa-engine/rest" + path;
    }

    public static String getEliseAPI(String path) {
        return "http://" + address + ":" + port + "/salsa-engine/rest/elise" + path;
    }

    public void loadConfig() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            File confFile = new File(System.getProperty("user.dir") + "/salsa-client.cnf");
            if (confFile.exists()) {
                input = new FileInputStream(confFile);
                prop.load(input);
                address = prop.getProperty("address");
                port = prop.getProperty("port");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static CmdLineParser getParserOpt() {
        return parserOpt;
    }

    public static String getCOMMAND_NAME() {
        return COMMAND_NAME;
    }

    public static String getCOMMAND_DESCRIPTION() {
        return COMMAND_DESCRIPTION;
    }

}
