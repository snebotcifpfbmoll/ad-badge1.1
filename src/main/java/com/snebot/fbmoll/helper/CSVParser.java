package com.snebot.fbmoll.helper;

import com.snebot.fbmoll.data.CSVObject;
import com.snebot.fbmoll.util.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Esta clase se encarga de leer un archivo CSV y
 * procesarlo para poder guardar los datos en un archivo XML.
 *
 * @author Serafi Nebot Ginard
 * @since 1.0
 */
public class CSVParser {
    private CSVParserProperties properties = null;

    public CSVParserProperties getProperties() {
        if (properties == null) properties = new CSVParserProperties();
        return properties;
    }

    public void setProperties(CSVParserProperties properties) {
        this.properties = properties;
    }

    public CSVParser() {
    }

    public CSVParser(CSVParserProperties properties) {
        this.properties = properties;
    }

    /**
     * Añadir un valor en un objeto CSVObject.
     *
     * @param element     Objeto CSVObject
     * @param columnNames Nombre de las columnas.
     * @param name        Nombre del elemento.
     * @param value       Valor del elemento.
     * @param lineIndex   Índice de la linea.
     * @param valueIndex  Índice del valor.
     */
    private void add(CSVObject element, ArrayList<String> columnNames, String name, String value, int lineIndex, int valueIndex) {
        CSVParserProperties properties = getProperties();

        if (properties.isFirstLineColumnName()) {
            if (lineIndex == 0) {
                columnNames.add(value);
            } else if (valueIndex < columnNames.size()) {
                name = columnNames.get(valueIndex);
            }
        }

        element.add(name, value);
    }

    /**
     * Analiza el contenido del CSV y lo guarda en una lista de objetos CSVObject.
     *
     * @param content Contenido del archivo CSV.
     * @return Lista de objetos CSVObject
     */
    public List<CSVObject> parse(String content) {
        CSVParserProperties properties = getProperties();
        char separator = properties.getSeparator();
        char lineSeparator = properties.getLineSeparator();
        char textSeparator = properties.getTextSeparator();

        CSVObject element = new CSVObject();
        ArrayList<CSVObject> elements = new ArrayList<>();
        ArrayList<String> columnNames = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        int lineIndex = 0;
        int valueIndex = 0;
        boolean text = false;
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char character = chars[i];
            if (character == textSeparator) {
                text = !text;
            } else if (character == separator && !text) {
                String value = stringBuilder.toString();
                String name = String.format("value-%d", valueIndex);
                add(element, columnNames, name, value, lineIndex, valueIndex);
                stringBuilder.setLength(0);
                valueIndex += 1;
            } else if ((character == lineSeparator || i == chars.length - 1) && !text) {
                String value = stringBuilder.toString();
                String name = String.format("value-%d", valueIndex);
                add(element, columnNames, name, value, lineIndex, valueIndex);
                elements.add(element);
                stringBuilder.setLength(0);
                valueIndex = 0;
                lineIndex++;
                element = new CSVObject();
            } else {
                stringBuilder.append(character);
            }
        }

        return elements;
    }

    /**
     * Analiza el contenido del CSV y lo guarda en una lista de objetos CSVObject.
     *
     * @param file Archivo a procesar.
     * @return Lista de objetos CSVObject.
     */
    public List<CSVObject> parse(File file) {
        String content = FileUtils.readFile(file);
        if (content.isEmpty()) return null;
        return parse(content);
    }

    /**
     * Genera un Document XML a partir de una lista de CSVObject.
     *
     * @param objects  Lista de CSVObject.
     * @param rootName Nombre del elemento root del archivo XML resultante.
     * @return Objeto Document con la estructura de XML generada.
     */
    public Document convertToXML(List<CSVObject> objects, String rootName) {
        Document document = null;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();

            Element root = document.createElement(rootName);
            document.appendChild(root);

            CSVParserProperties properties = getProperties();
            // if first line is column name we want to start from the second line
            int initialValue = properties.isFirstLineColumnName() ? 1 : 0;
            // for every object inside CSVObject list, create a xml element with subelements inside it
            for (int i = initialValue; i < objects.size(); i++) {
                CSVObject object = objects.get(i);
                Element objectElement = document.createElement("element");

                // for every key inside HashMap create a new xml element with value as its text
                HashMap<String, String> elements = object.getElements();
                String[] keySet = elements.keySet().toArray(new String[0]);
                for (int j = 0; j < keySet.length; j++) {
                    String key = keySet[j];
                    String str = StringEscapeUtils.escapeXml10(elements.get(key));
                    String tagName = stripCharacters(key);
                    if (properties.isLowerCaseTags()) tagName = tagName.toLowerCase();
                    if (tagName == null || tagName.equals("")) tagName = String.format("value-%d", j);

                    Element value = document.createElement(tagName);
                    value.setTextContent(str);
                    objectElement.appendChild(value);
                }

                root.appendChild(objectElement);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }

        return document;
    }

    /*
     * regex: ^[^a-z]*|[^\w-]|[^\w]$
     *
     * ^[^a-z]* -> Selecciona todos los caracteres que no estén entre el rango a-z a principio del texto.
     * [^\w-] -> Selecciona todos los caracteres que no sean alfanuméricos o '-'.
     * [^\w]$ -> Selecciona todos los caracteres que no sean alfanuméricos a final del texto.
     *
     * Una vez seleccionado estos caracteres los reemplazamos por "" (se eliminan).
     * */
    /**
     * Modifica el texto para que cumpla con los requisitos:
     * - Solo caracteres alfanumericos y '-' (los espacios se convierten en '-').
     * - A principio del texto solo puede haber un caracter de A-Z.
     * - A final del texto solo puede haber un caracter alfanumerico.
     *
     * @param str Texto a procesar.
     * @return Texto resultante.
     */
    public String stripCharacters(String str) {
        String result = null;
        str = StringUtils.replace(str, " ", "-");
        try {
            String regex = "^[^a-z]*|[^\\w-]|[^\\w]$";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher m = pattern.matcher(str);
            result = m.replaceAll(StringUtils.EMPTY);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return result;
    }
}