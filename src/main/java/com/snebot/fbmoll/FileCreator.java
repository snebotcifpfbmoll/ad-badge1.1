package com.snebot.fbmoll;

import com.snebot.fbmoll.data.CSVObject;
import com.snebot.fbmoll.helper.CSVParser;
import com.snebot.fbmoll.helper.CSVParserProperties;
import com.snebot.fbmoll.util.FileUtils;
import org.apache.commons.cli.*;
import org.w3c.dom.Document;

import java.io.File;
import java.util.List;

/**
 * Esta clase es el punto de entrada del programa.
 * Se encarga de procesar los argumentos pasados por consola y
 * de hacer las conversiones necesaria para pasar un archivo CSV a XML.
 *
 * @author Serafi Nebot Ginard
 * @since 1.0
 */
public class FileCreator {
    private static final String APP_NAME = "file-creator-1";
    private static final String INPUT_FILE_OPTION = "input";
    private static final String OUTPUT_FILE_OPTION = "output";
    private static final String SEPARATOR_OPTION = "separator";
    private static final String LINE_SEPARATOR_OPTION = "line-separator";
    private static final String TEXT_SEPARATOR_OPTION = "text-separator";
    private static final String FIRST_LINE_OPTION = "first-line-name";
    private static final String LOWERCASE_TAGS_OPTION = "lowercase-tags";

    /**
     * Procesa los argumentos pasados por consola.
     * Utiliza la librería org.apache.commons.cli para facilitar el procesamiento los argumentos.
     *
     * @param args Argumentos a procesar.
     * @return Objeto CommandLine.
     */
    public static CommandLine parseArgs(String[] args) {
        Options options = new Options();
        Option inputFileOption = new Option("i", INPUT_FILE_OPTION, true, "archivo de entrada");
        inputFileOption.setRequired(true);
        options.addOption(inputFileOption);

        Option outputFileOption = new Option("o", OUTPUT_FILE_OPTION, true, "archivo de salida");
        outputFileOption.setRequired(false);
        options.addOption(outputFileOption);

        Option separatorOption = new Option("s", SEPARATOR_OPTION, true, "separador de valores (',' por defecto)");
        separatorOption.setRequired(false);
        options.addOption(separatorOption);

        Option lineSeparatorOption = new Option("l", LINE_SEPARATOR_OPTION, true, "separador de lineas ('\\n' por defecto");
        lineSeparatorOption.setRequired(false);
        options.addOption(lineSeparatorOption);

        Option textSeparatorOption = new Option("t", TEXT_SEPARATOR_OPTION, true, "separador de texto ('\"' por defecto)");
        textSeparatorOption.setRequired(false);
        options.addOption(textSeparatorOption);

        Option firstLineOption = new Option(null, FIRST_LINE_OPTION, false, "indica si la primera linea del archivo son las columnas");
        firstLineOption.setRequired(false);
        options.addOption(firstLineOption);

        Option lowerCaseTagsOption = new Option(null, LOWERCASE_TAGS_OPTION, false, "genera los tags del xml en minúscula");
        lowerCaseTagsOption.setRequired(false);
        options.addOption(lowerCaseTagsOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine commandLine = null;

        try {
            commandLine = parser.parse(options, args);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            formatter.printHelp(APP_NAME, options);
        }

        return commandLine;
    }

    public static void main(String... args) {
        CommandLine commandLine = parseArgs(args);
        if (commandLine == null) {
            System.out.println("Error al procesar los argumentos");
            System.exit(1);
        }

        String inputPath = commandLine.getOptionValue(INPUT_FILE_OPTION);
        String outputPath = commandLine.getOptionValue(OUTPUT_FILE_OPTION);
        if (outputPath == null) {
            String filePathExt = FileUtils.removeFileExtension(inputPath);
            outputPath = String.format("%s.xml", filePathExt);
        }

        File file = FileUtils.openFile(inputPath);
        if (file == null) {
            System.out.printf("Error al abrir el archivo: %s\n", inputPath);
            System.exit(1);
        }

        Character separator = null;
        Character lineSeparator = null;
        Character textSeparator = null;
        if (commandLine.hasOption(SEPARATOR_OPTION))
            separator = commandLine.getOptionValue(SEPARATOR_OPTION).toCharArray()[0];
        if (commandLine.hasOption(LINE_SEPARATOR_OPTION))
            lineSeparator = commandLine.getOptionValue(LINE_SEPARATOR_OPTION).toCharArray()[0];
        if (commandLine.hasOption(TEXT_SEPARATOR_OPTION))
            textSeparator = commandLine.getOptionValue(TEXT_SEPARATOR_OPTION).toCharArray()[0];
        boolean firstLineNames = commandLine.hasOption(FIRST_LINE_OPTION);
        boolean lowerCaseTags = commandLine.hasOption(LOWERCASE_TAGS_OPTION);

        CSVParserProperties properties = new CSVParserProperties(separator, lineSeparator, textSeparator, firstLineNames, lowerCaseTags);
        CSVParser parser = new CSVParser(properties);
        List<CSVObject> objects = parser.parse(file);
        String fileName = FileUtils.getLastPathComponent(inputPath);
        String rootElementName = FileUtils.removeFileExtension(fileName);
        Document document = parser.convertToXML(objects, rootElementName);
        if (document == null) {
            System.out.println("Error al convertir CSV a XML");
            System.exit(1);
        }

        File outFile = FileUtils.saveDocument(document, outputPath);
        if (outFile == null) {
            System.out.printf("Error al guardar el archivo: %s\n", outputPath);
            System.exit(1);
        }
    }
}