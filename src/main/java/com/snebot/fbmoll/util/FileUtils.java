package com.snebot.fbmoll.util;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Esta clase contiene métodos estáticos que facilitan el trato con archivos.
 *
 * @author Serafi Nebot Ginard
 * @since 1.0
 */
public class FileUtils {
    private FileUtils() {
    }

    /**
     * Abre un archivo y comprueba que no es un directorio.
     *
     * @param path Ruta del archivo.
     * @return Objeto File.
     */
    public static File openFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) return null;
        return file;
    }

    /**
     * Devuelve el contenido de un archivo.
     *
     * @param file Archivo a abrir.
     * @return Contenido del archivo.
     */
    public static String readFile(File file) {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;

        try {
            FileReader fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
            String line = null;
            while ((line = reader.readLine()) != null) builder.append(line).append('\n');
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }

        return builder.toString();
    }

    /**
     * Guarda un Document a un archivo.
     *
     * @param document Objeto Document.
     * @param path     Ruta del archivo a generar.
     * @return Objeto File resultante.
     */
    public static File saveDocument(Document document, String path) {
        File file = null;
        try {
            file = new File(path);
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                String dirs = StringUtils.substringBeforeLast(path, System.getProperty("file.separator"));
                System.out.printf("Error: failed to create directories: %s", dirs);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(file);
            transformer.transform(domSource, streamResult);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return file;
    }

    /**
     * Devuelve el nombre del ultimo archivo/directorio de una ruta.
     *
     * @param path Ruta a procesar.
     * @return Nombre del ultimo archivo/directorio.
     */
    public static String getLastPathComponent(String path) {
        String separator = System.getProperty("file.separator");
        String ret = StringUtils.substringAfterLast(path, separator);
        if (ret.equals("")) ret = path;
        return ret;
    }

    /**
     * Elimina la extensión del archivo.
     *
     * @param path Ruta del archivo.
     * @return Ruta del archivo sin extensión.
     */
    public static String removeFileExtension(String path) {
        String ret = StringUtils.substringBeforeLast(path, ".");
        if (ret.equals("")) ret = path;
        return ret;
    }

}
