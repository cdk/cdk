package org.openscience.cdk.io;

/**
 * Helper class that facilitates the parsing of text data.
 */
final class CharIter {
    private final String string;
    private int position = 0;

    CharIter(String str) {
        this.string = str;
    }

    static boolean isSpace(char c) {
        return c == ' ';
    }

    static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    int position() {
        return position;
    }

    char next() {
        return string.charAt(position++);
    }

    char peek() {
        return position < string.length() ? string.charAt(position) : '\0';
    }

    boolean hasNext() {
        return position < string.length();
    }

    String rest() {
        return string.substring(position);
    }

    void skipWhiteSpace() {
        while (hasNext()) {
            if (isSpace(string.charAt(position)))
                position++;
            else
                break;
        }
    }

    int nextUnsignedNumber() {
        if (!hasNext())
            return -1;
        if (!isDigit(peek()))
            return -1;
        int num = next() - '0';
        while (hasNext() && isDigit(peek()))
            num = (10 * num) + (next() - '0');
        return num;
    }

    boolean consume(String substring) {
        if (position + substring.length() > string.length())
            return false;
        int mark = position;
        for (int i = 0; i < substring.length(); i++) {
            if (substring.charAt(i) != string.charAt(position)) {
                position = mark; // reset
                break;
            }
            position++;
        }
        return position - mark == substring.length();
    }

    String substring(int beg, int end) {
        return string.substring(beg, end);
    }

    void seek(int position) {
        this.position = position;
    }

    boolean nextIf(char c) {
        if (peek() == c) {
            next();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(string, 0, position);
        sb.append('|');
        sb.append(string.substring(position));
        return sb.toString();
    }
}