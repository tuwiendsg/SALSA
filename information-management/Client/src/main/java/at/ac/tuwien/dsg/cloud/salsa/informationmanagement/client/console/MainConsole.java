/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.console;



import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.console.handler.PrintHelp;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.console.handler.Syn;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class MainConsole {

    public static final String COMMAND_NAME = System.getProperty("app.name", "java -jar client.jar");
    public static final String COMMAND_DESCRIPTION = "SALSA Java command-line client";

    @Option(name = "-b", aliases = "--broker", metaVar = "<broker>", usage = "The URL of the queue broker")
    private static String broker = "amqp://128.130.172.215";    // default value

    @Option(name = "-t", aliases = "--brokerType", metaVar = "<brokerType>", usage = "The type of the broker")
    private static String brokerType = "amqp";

    @Option(name = "-h", aliases = "--help", usage = "Print the help and exit")
    private boolean callingHelp;

    @Argument(handler = SubCommandHandler.class)
    @SubCommands({        
        @SubCommand(name = "syn", impl = Syn.class),        
        @SubCommand(name = "help", impl = PrintHelp.class)
        })
    CommandHandler command;

    static CmdLineParser parserOpt;

    public static void main(String[] args) throws CmdLineException {
//        final String[] testArgs = {"-a", "128.130.172.215", "-p", "8888", "service-remove","test"};        
//        final String[] testArgs = {"-a", "128.130.172.215", "-p", "8888", "service-submit", "/home/hungld/test/wordpress-tosca/workpress.tosca.xml"};
        
        if (args.length>0 && args[0].equals("list-commands")){
            System.out.println(PrintHelp.getCommandList());
            return;
        }
        
        
        MainConsole opts = new MainConsole();
        opts.loadConfig();        
        try {
            parserOpt = new CmdLineParser(opts);
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


    public void loadConfig() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            File confFile = new File(System.getProperty("user.dir") + "/client.cnf");
            if (confFile.exists()) {
                input = new FileInputStream(confFile);
                prop.load(input);                
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
