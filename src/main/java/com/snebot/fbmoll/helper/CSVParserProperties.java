package com.snebot.fbmoll.helper;

/**
 * Clase de configuración para un objeto CSVParser.
 *
 * @author Serafi Nebot Ginard
 * @since 1.0
 */
public class CSVParserProperties {
    private char separator = ',';
    private char lineSeparator = '\n';
    private char textSeparator = '\"';
    private boolean firstLineColumnName = false;
    private boolean lowerCaseTags = false;

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public char getLineSeparator() {
        return lineSeparator;
    }

    public void setLineSeparator(char lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public char getTextSeparator() {
        return textSeparator;
    }

    public void setTextSeparator(char textSeparator) {
        this.textSeparator = textSeparator;
    }

    public boolean isFirstLineColumnName() {
        return firstLineColumnName;
    }

    public boolean isLowerCaseTags() {
        return lowerCaseTags;
    }

    public void setLowerCaseTags(boolean lowerCaseTags) {
        this.lowerCaseTags = lowerCaseTags;
    }

    public void setFirstLineColumnName(boolean firstLineColumnName) {
        this.firstLineColumnName = firstLineColumnName;
    }

    public CSVParserProperties() {
    }

    public CSVParserProperties(Character separator, Character lineSeparator, Character textSeparator, boolean firstLineColumnName, boolean lowerCaseTags) {
        if (separator != null) this.separator = separator;
        if (lineSeparator != null) this.lineSeparator = lineSeparator;
        if (textSeparator != null) this.textSeparator = textSeparator;
        this.firstLineColumnName = firstLineColumnName;
        this.lowerCaseTags = lowerCaseTags;
    }
}
