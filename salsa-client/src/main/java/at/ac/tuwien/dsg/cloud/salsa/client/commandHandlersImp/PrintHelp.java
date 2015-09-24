/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.Main;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommands;

/**
 *
 * @author Duc-Hung LE
 */
public class PrintHelp implements CommandHandler {

    @Argument(index = 0)
    String command;

    public static final HashMap<String, Class<CommandHandler>> OPTIONS = new HashMap<>();

    @Override
    public void execute() {
        if (command == null) {
            // generic help
            printHelp();
        } else {
            try {
                // subcommand help
                if (OPTIONS.get(command) == null) {
                    System.out.println("Error. Command is not available: " + command);
                } else {
                    CommandHandler cmd = OPTIONS.get(command).newInstance();
                    System.out.println("The command " + command + " got following arguments:");
                    CmdLineParser parser = new CmdLineParser(cmd);
                    parser.printUsage(System.out);
                }
            } catch (InstantiationException ex) {
                System.out.println("Error. Command is not available: " + command);
            } catch (IllegalAccessException ex) {
                System.out.println("Error. Cannot initiate handler for command: " + command);
            }
        }
    }

    static {
        try {
            Field cmdField = Main.class.getDeclaredField("command");
            SubCommands subCommands = cmdField.getAnnotation(SubCommands.class);
            for (SubCommand sub : subCommands.value()) {
                if (CommandHandler.class.isAssignableFrom(sub.impl())) {
                    Class<CommandHandler> clazz = (Class<CommandHandler>) sub.impl();
                    OPTIONS.put(sub.name(), clazz);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void printHelp() {        
        PrintWriter out = new PrintWriter(System.out);
        System.out.println("Usage: " + Main.getCOMMAND_NAME());
        //Main.getParserOpt().printSingleLineUsage(out, null);                
        System.out.println(Main.getCOMMAND_DESCRIPTION());
        System.out.println();
        try {
            Main.getParserOpt().printUsage(out, null);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("");
        System.out.println("Type '" + Main.getCOMMAND_NAME() + " help <command>' for help on a specific command.");
        System.out.println();
        System.out.println("Available commands:");

        List<String> myList = new ArrayList(OPTIONS.keySet());
        myList.remove("help");
        Collections.sort(myList);

        for (String cmd : myList) {
            try {
                System.out.printf("  %-20s: %s \n", cmd , OPTIONS.get(cmd).newInstance().getCommandDescription());
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }        
        out.close();
    }

    @Override
    public String getCommandDescription() {
        return "Print help message";
    }

}
