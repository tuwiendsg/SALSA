/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.console.handler;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.console.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.console.MainConsole;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
        System.out.println("Printing help");
        if (command == null || command.equals("")) {
            // generic help            
            printHelp();
            printGuide();
        } else {
            try {
                // subcommand help
                if (OPTIONS.get(command) == null) {
                    System.out.println("Error. Command is not available: " + command);
                } else {
                    CommandHandler cmd = OPTIONS.get(command).newInstance();
                    System.out.println(cmd.getCommandDescription());
                    CmdLineParser parser = new CmdLineParser(cmd);
                    System.out.print("Usage: " + MainConsole.getCOMMAND_NAME() + " " + command);
                    parser.printSingleLineUsage(System.out);
                    System.out.println("");
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
            Field cmdField = MainConsole.class.getDeclaredField("command");
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

    public static void printGuide() {
        System.out.println("\nThe common steps to use this client are:");
        System.out.println(" 1. Use 'service-submit' to submit a TOSCA and get back an serviceId.");
        System.out.println(" 2. Use 'instance-list <serviceId>' to list all instances and their states.");
        System.out.println(" 3. Use 'instance-query <instanceId>' to get the properties and capabilities.");
    }

    public static void printHelp() {
        PrintWriter out = new PrintWriter(System.out);
        System.out.println("Usage: " + MainConsole.getCOMMAND_NAME());
        //Main.getParserOpt().printSingleLineUsage(out, null);                
        System.out.println(MainConsole.getCOMMAND_DESCRIPTION());
        System.out.println();
        try {
            MainConsole.getParserOpt().printUsage(out, null);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("");
        System.out.println("Type '" + MainConsole.getCOMMAND_NAME() + " help <command>' for help on a specific command.");
        System.out.println();
        System.out.println("Available commands:");

        List<String> myList = new ArrayList(OPTIONS.keySet());
        myList.remove("help");
        Collections.sort(myList);

        for (String cmd : myList) {
            try {
                System.out.printf("  %-20s: %s \n", cmd, OPTIONS.get(cmd).newInstance().getCommandDescription());
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        out.flush();
    }

    /**
     * List commands separated by space
     *
     * @return a String of the list
     */
    public static String getCommandList() {
        List<String> myList = new ArrayList(OPTIONS.keySet());
        String result = "";
        StringBuilder sb = new StringBuilder();
        for (String s : myList) {
            sb.append(s).append(" ");
        }
        return sb.toString().trim();
    }

    @Override
    public String getCommandDescription() {
        return "Print help message";
    }

}
