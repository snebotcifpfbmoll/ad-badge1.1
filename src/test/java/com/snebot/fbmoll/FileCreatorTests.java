package com.snebot.fbmoll;

import com.snebot.fbmoll.data.CSVObject;
import com.snebot.fbmoll.helper.CSVParser;
import com.snebot.fbmoll.helper.CSVParserProperties;
import com.snebot.fbmoll.util.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class FileCreatorTests {
    private static final String COMMA_CSV = "comma.csv";
    private static final String SEMICOLON_CSV = "semicolon.csv";
    private static final String SAMPLE_CSV = "sample.csv";
    private static final String INVALID_CSV = "invalid.csv";
    private static final String MADNESS_CSV = "madness.csv";
    private static final String CSV_DIRECTORY = "CSV";
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String RESOURCE_DIRECTORY = System.getProperty("user.home");

    private String getTestFile(String name) {
        return String.format("%s%s%s%s%s", RESOURCE_DIRECTORY, FILE_SEPARATOR, CSV_DIRECTORY, FILE_SEPARATOR, name);
    }

    @Test
    void tryOpenFile() {
        String validPath = getTestFile(COMMA_CSV);
        File validFile = FileUtils.openFile(validPath);
        Assert.notNull(validFile, String.format("failed to open file: %s", validPath));

        String dirPath = getTestFile(StringUtils.EMPTY);
        File dirFile = FileUtils.openFile(dirPath);
        Assert.isTrue(dirFile == null, "failed to detect directory");
    }

    @Test
    void tryReadFile() {
        File file = FileUtils.openFile(getTestFile(COMMA_CSV));
        String content = FileUtils.readFile(file);
        Assert.isTrue(!content.isEmpty(), "file content is empty");
    }

    @Test
    void tryCSVParse() {
        String content = "col1,col2,col3,col4\ntest,\"this is, a test\",final test\n\"test, number, 2,\",ok,maybe,it works!";
        CSVParserProperties properties = new CSVParserProperties(',', '\n', '\"', true, false);
        CSVParser parser = new CSVParser(properties);
        List<CSVObject> objects = parser.parse(content);
        for (CSVObject object : objects) {
            HashMap<String, String> elements = object.getElements();
            for (String key : elements.keySet()) {
                System.out.printf("%s: %s\n", key, elements.get(key));
            }
            System.out.println();
        }

        Document doc = parser.convertToXML(objects, "root");
        Assert.notNull(doc, "failed to convert to xml document");
        FileUtils.saveDocument(doc, getTestFile("test.xml"));
    }

    @Test
    void tryCSVParseFile() {
        CSVParserProperties properties = new CSVParserProperties(',', '\n', '\"', true, false);
        CSVParser parser = new CSVParser(properties);
        File file = FileUtils.openFile(getTestFile(COMMA_CSV));
        List<CSVObject> elements = parser.parse(file);

        for (CSVObject element : elements) {
            HashMap<String, String> hashMap = element.getElements();
            for (String key : hashMap.keySet()) {
                System.out.printf("%s: %s\n", key, hashMap.get(key));
            }
            System.out.println();
        }

        Document doc = parser.convertToXML(elements, "root");
        Assert.notNull(doc, "failed to convert to xml document");
        FileUtils.saveDocument(doc, getTestFile("test.xml"));
    }

    @Test
    void tryRegEx() {
        CSVParser parser = new CSVParser();
        String text = " <5TEsT>/-0-and& test1-";
        String stripped = parser.stripCharacters(text);
        Assert.notNull(stripped, "failed to strip characters.");
        System.out.printf("result: \"%s\"", stripped);
    }

    @BeforeAll
    static void beforeAll() {

    }

    @Test
    void tryFileCreatorTask() {
        /*
         * -i, --input : file input (CSV)
         * -o, --output : file output (XML)
         * -s, --separator : character separator in CSV file
         * -l, --line-separator : character line separator in CSV file
         * -t, --text-separator : character text separator in CSV file
         * --first-line-name : first line as tag name
         * --lowercase-tags : make XML tags lowercase
         */

        String commaPath = getTestFile(COMMA_CSV);
        FileCreator.main("-i", commaPath, "--first-line-name", "--lowercase-tags");

        String samplePath = getTestFile(SAMPLE_CSV);
        FileCreator.main("-i", samplePath, "--first-line-name");

        String semicolonPath = getTestFile(SEMICOLON_CSV);
        FileCreator.main("-i", semicolonPath, "--first-line-name", "-s", ";");

        String invalidPath = getTestFile(INVALID_CSV);
        FileCreator.main("-i", invalidPath, "--first-line-name");

        String madnessPath = getTestFile(MADNESS_CSV);
        FileCreator.main("-i", madnessPath, "--first-line-name", "-s", "(", "-l", "|", "-t", "{");
    }
}
