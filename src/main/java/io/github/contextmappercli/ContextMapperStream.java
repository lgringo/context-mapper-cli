package io.github.contextmappercli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.generator.contextmap.ContextMapFormat;
import org.contextmapper.dsl.standalone.ContextMapperStandaloneSetup;
import org.eclipse.xtext.generator.GeneratorContext;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class ContextMapperStream {

    private static final ContextMapperCliGenerator generator = new ContextMapperCliGenerator();

    public static void main(final String[] args) {
        // Setup injection system
        ContextMapperStandaloneSetup.getStandaloneAPI();

        configureCommandLineParser();
        parseCommandLine(args);

        // Our generator do not use fsa (may change in the future ...)
        // Mostly for generate inline cml block in asciidoc files
        final OutputStreamFsa fsa = new OutputStreamFsa();
        final CMLResource resource = generator.loadCml();

        File output = createOutputFile();

        try (var os = new FileOutputStream(output)) {
            fsa.addStore(resource.getURI(), os);
            generator.doGenerate(resource, fsa, new GeneratorContext());
        } catch (Exception e) {
            System.out.println("Error during diagram generation : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void configureGenerator(final String[] args) {
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helpFormatter.printHelp("java -jar context-mapper-cli.jar [OPTIONS] <FILE>", options);
            System.exit(1);
        }

        checkRequiredArgs(cmd);
        checkIncompatibleArgs(cmd);
        parseOptions(cmd);
    }

    private static void checkRequiredArgs(final CommandLine cmd) {
        if (cmd.getArgList().size() == 0) {
            System.out.println("<FILE> is required");
            helpFormatter.printHelp("java -jar context-mapper-cli.jar [OPTIONS] <FILE>", options);
            System.exit(1);
        }
    }

    private static void checkIncompatibleArgs(final CommandLine cmd) {
        if (cmd.hasOption(Args.HEIGHT.name) && cmd.hasOption(Args.WIDTH.name)) {
            System.out.println("Choose either width or height.");
            helpFormatter.printHelp("java -jar context-mapper-cli.jar [OPTIONS] <FILE>", ContextMapperStream.options);
            System.exit(1);
        }
        incompatibleBooleanArgs(cmd, Args.PRINTADDITIONALLABELS.name, Args.NOPRINTADDITIONALLABELS.name);
        incompatibleBooleanArgs(cmd, Args.CLUSTERTEAMS.name, Args.CLUSTERTEAMS.name);
    }

    private static void incompatibleBooleanArgs(final CommandLine cmd, final String name1, final String name2) {
        if (cmd.hasOption(name1) && cmd.hasOption(name2)) {
            System.out.println(name1 + " is incompatible with " + name2);
            helpFormatter.printHelp("java -jar context-mapper-cli.jar [OPTIONS] <FILE>", ContextMapperStream.options);
            System.exit(1);
        }
    }

    private static ContextMapFormat convertFormat(final CommandLine cmd, final String name) {
        String value = cmd.getOptionValue(name).toUpperCase().trim();
        ContextMapFormat format = ContextMapFormat.SVG;
        try {
            format = ContextMapFormat.valueOf(value);
        } catch (Exception e) {
            System.out.println(name + " value can only be SVG or PNG (case insensitive)");
            helpFormatter.printHelp("java -jar context-mapper-cli.jar [OPTIONS] <FILE>", options);
            System.exit(1);
        }
        return format;
    }

    private static int convertOptionToInt(final CommandLine cmd, final String name) {
        String rawValue = cmd.getOptionValue(name);
        if (!isNumeric(rawValue)) {
            System.out.println(name + " should be integer value");
            helpFormatter.printHelp("java -jar context-mapper-cli.jar [OPTIONS] <FILE>", options);
            System.exit(1);
        }
        int intValue = Integer.parseInt(rawValue);
        if (intValue <= 0) {
            System.out.println(name + " should be positive");
            helpFormatter.printHelp("java -jar context-mapper-cli.jar [OPTIONS] <FILE>", options);
            System.exit(1);
        }
        return intValue;
    }

    private static Boolean parseBooleanOption(CommandLine cmd, String trueOpt, String falseOpt) {
        Boolean opt = null;
        if (cmd.hasOption(trueOpt)) {
            opt = true;
        } else if (cmd.hasOption(falseOpt)) {
            opt = false;
        }
        return opt;
    }

    private static void parseOptions(final CommandLine cmd) {
        generator.setFile(cmd.getArgList().get(0));

        if (cmd.hasOption(Args.FORMAT.name)) {
            generator.setFormat(convertFormat(cmd, Args.FORMAT.name));
        } else {
            // Default format is SVG
            generator.setFormat(ContextMapFormat.SVG);
        }

        if (cmd.hasOption(Args.LABELSPACINGFACTOR.name)) {
            generator.setWidth(convertOptionToInt(cmd, Args.LABELSPACINGFACTOR.name));
        }

        if (cmd.hasOption(Args.WIDTH.name)) {
            generator.setWidth(convertOptionToInt(cmd, Args.WIDTH.name));
        }

        if (cmd.hasOption(Args.HEIGHT.name)) {
            generator.setHeight(convertOptionToInt(cmd, Args.HEIGHT.name));
        }

        Boolean boolOpt = parseBooleanOption(cmd, Args.PRINTADDITIONALLABELS.name, Args.NOPRINTADDITIONALLABELS.name);
        if (boolOpt != null) {
            generator.setPrintAdditionalLabels(boolOpt);
        }

        boolOpt = parseBooleanOption(cmd, Args.CLUSTERTEAMS.name, Args.NOCLUSTERTEAMS.name);
        if (boolOpt != null) {
            generator.setClusterTeams(boolOpt);
        }
    }

    private enum Args {
        FORMAT("format"),
        LABELSPACINGFACTOR("labelspacingfactor"),
        WIDTH("width"),
        HEIGHT("height"),
        PRINTADDITIONALLABELS("printadditionallabels"),
        CLUSTERTEAMS("clusterteams"),
        NOPRINTADDITIONALLABELS("noprintadditionallabels"),
        NOCLUSTERTEAMS("noclusterteams");

        String name;

        Args(final String name) {
            this.name = name;
        }
    }
}
