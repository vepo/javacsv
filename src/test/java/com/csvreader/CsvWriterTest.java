package com.csvreader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CsvWriterTest {

    private static void assertException(Exception expected, Exception actual) {
        Assertions.assertEquals(expected.getClass(), actual.getClass());
        Assertions.assertEquals(expected.getMessage(), actual.getMessage());
    }

    private static String generateString(char letter, int count) {
        StringBuilder buffer = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            buffer.append(letter);
        }
        return buffer.toString();
    }

    @BeforeAll
    static void setup() {
        // this library was developed in Window
        System.setProperty("line.separator", "\r\n");
    }

    @Test
    void test31() throws Exception {
        try (CsvWriter writer = new CsvWriter(new PrintWriter(
                new OutputStreamWriter(new FileOutputStream("temp.csv"),
                        Charset.forName("UTF-8"))),
                ',')) {
            // writer will trim all whitespace and put this in quotes to preserve
            // it's existence
            writer.write(" \t \t");
        }

        try (CsvReader reader = new CsvReader(new InputStreamReader(
                new FileInputStream("temp.csv"), Charset.forName("UTF-8")))) {
            Assertions.assertTrue(reader.readRecord());
            Assertions.assertEquals("", reader.get(0));
            Assertions.assertEquals(1, reader.getColumnCount());
            Assertions.assertEquals(0L, reader.getCurrentRecord());
            Assertions.assertEquals("\"\"", reader.getRawRecord());
            Assertions.assertFalse(reader.readRecord());
        }

        new File("temp.csv").delete();
    }

    @Test
    void test33() throws Exception {
        // tests for an old bug where an exception was
        // thrown if Dispose was called without other methods
        // being called. this should not throw an
        // exception
        String fileName = "somefile.csv";

        new File(fileName).createNewFile();

        try {
            CsvReader reader = new CsvReader(fileName);
            reader.close();
        } finally {
            new File(fileName).delete();
        }
    }

    @Test
    void test34() throws Exception {
        String data = "\"Chicane\", \"Love on the Run\", \"Knight Rider\", \"This field contains a comma, but it doesn't matter as the field is quoted\"\r\n"
                + "\"Samuel Barber\", \"Adagio for Strings\", \"Classical\", \"This field contains a double quote character, \"\", but it doesn't matter as it is escaped\"";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("Chicane", reader.get(0));
        Assertions.assertEquals("Love on the Run", reader.get(1));
        Assertions.assertEquals("Knight Rider", reader.get(2));
        Assertions
                .assertEquals(
                        "This field contains a comma, but it doesn't matter as the field is quoted",
                        reader.get(3));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(4, reader.getColumnCount());
        Assertions
                .assertEquals(
                        "\"Chicane\", \"Love on the Run\", \"Knight Rider\", \"This field contains a comma, but it doesn't matter as the field is quoted\"",
                        reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("Samuel Barber", reader.get(0));
        Assertions.assertEquals("Adagio for Strings", reader.get(1));
        Assertions.assertEquals("Classical", reader.get(2));
        Assertions
                .assertEquals(
                        "This field contains a double quote character, \", but it doesn't matter as it is escaped",
                        reader.get(3));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertEquals(4, reader.getColumnCount());
        Assertions
                .assertEquals(
                        "\"Samuel Barber\", \"Adagio for Strings\", \"Classical\", \"This field contains a double quote character, \"\", but it doesn't matter as it is escaped\"",
                        reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test35() throws Exception {
        String data = "Chicane, Love on the Run, Knight Rider, \"This field contains a comma, but it doesn't matter as the field is quoted\"";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("Chicane", reader.get(0));
        Assertions.assertEquals("Love on the Run", reader.get(1));
        Assertions.assertEquals("Knight Rider", reader.get(2));
        Assertions
                .assertEquals(
                        "This field contains a comma, but it doesn't matter as the field is quoted",
                        reader.get(3));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(4, reader.getColumnCount());
        Assertions
                .assertEquals(
                        "Chicane, Love on the Run, Knight Rider, \"This field contains a comma, but it doesn't matter as the field is quoted\"",
                        reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test36() throws Exception {
        String data = "\"some \\stuff\"";

        CsvReader reader = CsvReader.parse(data);
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("some stuff", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("\"some \\stuff\"", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test37() throws Exception {
        String data = "  \" Chicane\"  junk here  , Love on the Run, Knight Rider, \"This field contains a comma, but it doesn't matter as the field is quoted\"";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(" Chicane", reader.get(0));
        Assertions.assertEquals("Love on the Run", reader.get(1));
        Assertions.assertEquals("Knight Rider", reader.get(2));
        Assertions
                .assertEquals(
                        "This field contains a comma, but it doesn't matter as the field is quoted",
                        reader.get(3));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(4, reader.getColumnCount());
        Assertions
                .assertEquals(
                        "  \" Chicane\"  junk here  , Love on the Run, Knight Rider, \"This field contains a comma, but it doesn't matter as the field is quoted\"",
                        reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test38() throws Exception {
        String data = "1\r\n\r\n\"\"\r\n \r\n2";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("", reader.get(0));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("\"\"", reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("", reader.get(0));
        Assertions.assertEquals(2L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals(" ", reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("2", reader.get(0));
        Assertions.assertEquals(3L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("2", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test39() throws Exception {
        CsvReader reader = CsvReader.parse("user_id,name\r\n1,Bruce");
        Assertions.assertTrue(reader.getSafetySwitch());
        reader.setSafetySwitch(false);
        Assertions.assertFalse(reader.getSafetySwitch());

        Assertions.assertEquals('#', reader.getComment());
        reader.setComment('!');
        Assertions.assertEquals('!', reader.getComment());

        Assertions.assertEquals(EscapeMode.DOUBLED, reader.userSettings().escapeMode());
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertEquals(EscapeMode.BACKSLASH, reader.userSettings().escapeMode());

        Assertions.assertEquals('\0', reader.getRecordDelimiter());
        reader.setRecordDelimiter(';');
        Assertions.assertEquals(';', reader.getRecordDelimiter());

        Assertions.assertEquals('\"', reader.userSettings().textQualifier());
        reader.userSettings().withTextQualifier('\'');
        Assertions.assertEquals('\'', reader.userSettings().textQualifier());

        Assertions.assertTrue(reader.userSettings().trimWhitespace());
        reader.userSettings().withTrimWhitespace(false);
        Assertions.assertFalse(reader.userSettings().trimWhitespace());

        Assertions.assertFalse(reader.getUseComments());
        reader.setUseComments(true);
        Assertions.assertTrue(reader.getUseComments());

        Assertions.assertTrue(reader.getUseTextQualifier());
        reader.setUseTextQualifier(false);
        Assertions.assertFalse(reader.getUseTextQualifier());
        reader.close();
    }

    @Test
    void test40() throws Exception {
        String data = "Chicane, Love on the Run, Knight Rider, This field contains a comma\\, but it doesn't matter as the delimiter is escaped";

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(false);
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("Chicane", reader.get(0));
        Assertions.assertEquals("Love on the Run", reader.get(1));
        Assertions.assertEquals("Knight Rider", reader.get(2));
        Assertions
                .assertEquals(
                        "This field contains a comma, but it doesn't matter as the delimiter is escaped",
                        reader.get(3));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(4, reader.getColumnCount());
        Assertions
                .assertEquals(
                        "Chicane, Love on the Run, Knight Rider, This field contains a comma\\, but it doesn't matter as the delimiter is escaped",
                        reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test41() throws Exception {
        String data = "double\\\\\\\\double backslash";

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(false);
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("double\\\\double backslash", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test42() throws Exception {
        String data = "some \\stuff";

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(false);
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("some stuff", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test43() throws Exception {
        String data = "\"line 1\\nline 2\",\"line 1\\\nline 2\"";

        CsvReader reader = CsvReader.parse(data);
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("line 1\nline 2", reader.get(0));
        Assertions.assertEquals("line 1\nline 2", reader.get(1));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test44() throws Exception {
        String data = "line 1\\nline 2,line 1\\\nline 2";

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(false);
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("line 1\nline 2", reader.get(0));
        Assertions.assertEquals("line 1\nline 2", reader.get(1));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test45() throws Exception {
        String data = "\"Chicane\", \"Love on the Run\", \"Knight Rider\", \"This field contains a comma, but it doesn't matter as the field is quoted\"i"
                + "\"Samuel Barber\", \"Adagio for Strings\", \"Classical\", \"This field contains a double quote character, \"\", but it doesn't matter as it is escaped\"";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.getCaptureRawRecord());
        reader.setCaptureRawRecord(false);
        Assertions.assertFalse(reader.getCaptureRawRecord());
        Assertions.assertEquals("", reader.getRawRecord());
        reader.setRecordDelimiter('i');
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("Chicane", reader.get(0));
        Assertions.assertEquals("Love on the Run", reader.get(1));
        Assertions.assertEquals("Knight Rider", reader.get(2));
        Assertions
                .assertEquals(
                        "This field contains a comma, but it doesn't matter as the field is quoted",
                        reader.get(3));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(4, reader.getColumnCount());
        Assertions.assertEquals("", reader.getRawRecord());
        Assertions.assertFalse(reader.getCaptureRawRecord());
        reader.setCaptureRawRecord(true);
        Assertions.assertTrue(reader.getCaptureRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions
                .assertEquals(
                        "\"Samuel Barber\", \"Adagio for Strings\", \"Classical\", \"This field contains a double quote character, \"\", but it doesn't matter as it is escaped\"",
                        reader.getRawRecord());
        Assertions.assertEquals("Samuel Barber", reader.get(0));
        Assertions.assertEquals("Adagio for Strings", reader.get(1));
        Assertions.assertEquals("Classical", reader.get(2));
        Assertions
                .assertEquals(
                        "This field contains a double quote character, \", but it doesn't matter as it is escaped",
                        reader.get(3));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertEquals(4, reader.getColumnCount());
        Assertions.assertFalse(reader.readRecord());
        Assertions.assertTrue(reader.getCaptureRawRecord());
        Assertions.assertEquals("", reader.getRawRecord());
        reader.close();
    }

    @Test
    void test46() throws Exception {
        String data = "Ch\\icane, Love on the Run, Kn\\ight R\\ider, Th\\is f\\ield conta\\ins an \\i\\, but \\it doesn't matter as \\it \\is escapedi"
                + "Samuel Barber, Adag\\io for Str\\ings, Class\\ical, Th\\is f\\ield conta\\ins a comma \\, but \\it doesn't matter as \\it \\is escaped";

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(false);
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        reader.setRecordDelimiter('i');
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("Chicane", reader.get(0));
        Assertions.assertEquals("Love on the Run", reader.get(1));
        Assertions.assertEquals("Knight Rider", reader.get(2));
        Assertions
                .assertEquals(
                        "This field contains an i, but it doesn't matter as it is escaped",
                        reader.get(3));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(4, reader.getColumnCount());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("Samuel Barber", reader.get(0));
        Assertions.assertEquals("Adagio for Strings", reader.get(1));
        Assertions.assertEquals("Classical", reader.get(2));
        Assertions
                .assertEquals(
                        "This field contains a comma , but it doesn't matter as it is escaped",
                        reader.get(3));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertEquals(4, reader.getColumnCount());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test47() throws Exception {
        byte[] buffer;

        String test = "M�nchen";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream,
                Charset.forName("UTF-8")));
        writer.println(test);
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        CsvReader reader = new CsvReader(new InputStreamReader(
                new ByteArrayInputStream(buffer), Charset.forName("UTF-8")));
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(test, reader.get(0));
        reader.close();
    }

    @Test
    void test48() throws Exception {
        byte[] buffer;

        String test = "M�nchen";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream,
                Charset.forName("UTF-8")));
        writer.write(test);
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        CsvReader reader = new CsvReader(new InputStreamReader(
                new ByteArrayInputStream(buffer), Charset.forName("UTF-8")));
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(test, reader.get(0));
        reader.close();
    }

    @Test
    void test49() throws Exception {
        String data = "\"\\n\\r\\t\\b\\f\\e\\v\\a\\z\\d065\\o101\\101\\x41\\u0041\"";

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(true);
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions
                .assertEquals("\n\r\t\b\f\u001B\u000B\u0007zAAAAA", reader
                        .get(0));
        Assertions.assertEquals(
                "\"\\n\\r\\t\\b\\f\\e\\v\\a\\z\\d065\\o101\\101\\x41\\u0041\"",
                reader.getRawRecord());
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test50() throws Exception {
        String data = "\\n\\r\\t\\b\\f\\e\\v\\a\\z\\d065\\o101\\101\\x41\\u0041";

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(false);
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions
                .assertEquals("\n\r\t\b\f\u001B\u000B\u0007zAAAAA", reader
                        .get(0));
        Assertions.assertEquals(
                "\\n\\r\\t\\b\\f\\e\\v\\a\\z\\d065\\o101\\101\\x41\\u0041",
                reader.getRawRecord());
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test51() throws Exception {
        String data = "\"\\xfa\\u0afa\\xFA\\u0AFA\"";

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(true);
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("\u00FA\u0AFA\u00FA\u0AFA", reader.get(0));
        Assertions.assertEquals("\"\\xfa\\u0afa\\xFA\\u0AFA\"", reader
                .getRawRecord());
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test52() throws Exception {
        String data = "\\xfa\\u0afa\\xFA\\u0AFA";

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(false);
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("\u00FA\u0AFA\u00FA\u0AFA", reader.get(0));
        Assertions.assertEquals("\\xfa\\u0afa\\xFA\\u0AFA", reader.getRawRecord());
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test54() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        writer.write("1,2");
        writer.write("3");
        writer.write("blah \"some stuff in quotes\"");
        writer.endRecord();
        Assertions.assertFalse(writer.getForceQualifier());
        writer.setForceQualifier(true);
        Assertions.assertTrue(writer.getForceQualifier());
        writer.write("1,2");
        writer.write("3");
        writer.write("blah \"some stuff in quotes\"");
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions
                .assertEquals(
                        "\"1,2\",3,\"blah \"\"some stuff in quotes\"\"\"\r\n\"1,2\",\"3\",\"blah \"\"some stuff in quotes\"\"\"",
                        data);
    }

    @Test
    void test55() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        writer.write("");
        writer.write("1");
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals("\"\",1", data);
    }

    @Test
    void test56() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, '\t', StandardCharsets.ISO_8859_1);
        writer.write("1,2");
        writer.write("3");
        writer.write("blah \"some stuff in quotes\"");
        writer.endRecord();
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals(
                "1,2\t3\t\"blah \"\"some stuff in quotes\"\"\"\r\n", data);
    }

    @Test
    void test57() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, '\t', StandardCharsets.ISO_8859_1);
        Assertions.assertTrue(writer.getUseTextQualifier());
        writer.setUseTextQualifier(false);
        Assertions.assertFalse(writer.getUseTextQualifier());
        writer.write("1,2");
        writer.write("3");
        writer.write("blah \"some stuff in quotes\"");
        writer.endRecord();
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals("1,2\t3\tblah \"some stuff in quotes\"\r\n", data);
    }

    @Test
    void test58() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (CsvWriter writer = new CsvWriter(stream, '\t', StandardCharsets.ISO_8859_1)) {
            writer.write("data\r\nmore data");
            writer.write(" 3\t", false);
            writer.write(" 3\t");
            writer.write(" 3\t", true);
            writer.endRecord();
        }

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals("\"data\r\nmore data\"\t3\t3\t\" 3\t\"\r\n", data);
    }

    @Test
    void test70() throws Exception {
        String data = "\"1\",Bruce\r\n\"2\",Toni\r\n\"3\",Brian\r\n";

        CsvReader reader = CsvReader.parse(data);
        reader.setHeaders(new String[] { "userid", "name" });
        Assertions.assertEquals(2, reader.getHeaderCount());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get("userid"));
        Assertions.assertEquals("Bruce", reader.get("name"));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("2", reader.get("userid"));
        Assertions.assertEquals("Toni", reader.get("name"));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("3", reader.get("userid"));
        Assertions.assertEquals("Brian", reader.get("name"));
        Assertions.assertEquals(2L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test71() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        writer.setForceQualifier(true);
        writer.write(" data ");
        writer.endRecord();
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals("\"data\"\r\n", data);
    }

    @Test
    void test72() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        Assertions.assertEquals('\0', writer.getRecordDelimiter());
        writer.setRecordDelimiter(';');
        Assertions.assertEquals(';', writer.getRecordDelimiter());
        writer.write("a;b");
        writer.endRecord();
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals("\"a;b\";", data);
    }

    @Test
    void test73() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        Assertions.assertEquals(EscapeMode.DOUBLED, writer.userSettings().escapeMode());
        writer.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertEquals(EscapeMode.BACKSLASH, writer.userSettings().escapeMode());
        writer.write("1,2");
        writer.write("3");
        writer.write("blah \"some stuff in quotes\"");
        writer.endRecord();
        writer.setForceQualifier(true);
        writer.write("1,2");
        writer.write("3");
        writer.write("blah \"some stuff in quotes\"");
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();
        Assertions
                .assertEquals(
                        "\"1,2\",3,\"blah \\\"some stuff in quotes\\\"\"\r\n\"1,2\",\"3\",\"blah \\\"some stuff in quotes\\\"\"",
                        data);
    }

    @Test
    void test74() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        writer.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        writer.setUseTextQualifier(false);
        writer.write("1,2");
        writer.write("3");
        writer.write("blah \"some stuff in quotes\"");
        writer.endRecord();
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();
        Assertions.assertEquals("1\\,2,3,blah \"some stuff in quotes\"\r\n", data);
    }

    @Test
    void test75() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1)) {
            writer.write("1");
            writer.endRecord();
            writer.writeComment("blah");
            writer.write("2");
            writer.endRecord();
        }

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();
        Assertions.assertEquals("1\r\n#blah\r\n2\r\n", data);
    }

    @Test
    void test76() throws Exception {
        CsvReader reader = CsvReader.parse("user_id,name\r\n1,Bruce");
        Assertions.assertNull(reader.getHeaders());
        Assertions.assertEquals(-1, reader.getIndex("user_id"));
        Assertions.assertEquals("", reader.getHeader(0));
        Assertions.assertTrue(reader.readHeaders());
        Assertions.assertEquals(0, reader.getIndex("user_id"));
        Assertions.assertEquals("user_id", reader.getHeader(0));
        String[] headers = reader.getHeaders();
        Assertions.assertEquals(2, headers.length);
        Assertions.assertEquals("user_id", headers[0]);
        Assertions.assertEquals("name", headers[1]);
        reader.setHeaders(null);
        Assertions.assertNull(reader.getHeaders());
        Assertions.assertEquals(-1, reader.getIndex("user_id"));
        Assertions.assertEquals("", reader.getHeader(0));
        reader.close();
    }

    @Test
    void test77() {
        try {
            CsvReader.parse(null);
        } catch (Exception ex) {
            assertException(new IllegalArgumentException(
                    "Parameter data can not be null."), ex);
        }
    }

    @Test
    void test78() throws Exception {
        CsvReader reader = CsvReader.parse("1,Bruce");
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertFalse(reader.isQualified(999));
        reader.close();
    }

    @Test
    void test79() {
        CsvReader reader;
        reader = CsvReader.parse("");
        reader.close();
        try {
            reader.readRecord();
        } catch (Exception ex) {
            assertException(
                    new IOException(
                            "This instance of the CsvReader class has already been closed."),
                    ex);
        }
    }

    @Test
    void test81() throws Exception {
        CsvReader reader = CsvReader.parse(generateString('a', 100001));
        try {
            reader.readRecord();
        } catch (Exception ex) {
            assertException(
                    new IOException(
                            "Maximum column length of 100,000 exceeded in column 0 in record 0. Set the SafetySwitch property to false if you're expecting column lengths greater than 100,000 characters to avoid this error."),
                    ex);
        }
        reader.close();
    }

    @Test
    void test82() throws Exception {
        StringBuilder holder = new StringBuilder(200010);

        for (int i = 0; i < 100000; i++) {
            holder.append("a,");
        }

        holder.append("a");

        CsvReader reader = CsvReader.parse(holder.toString());
        try {
            reader.readRecord();
        } catch (Exception ex) {
            assertException(
                    new IOException(
                            "Maximum column count of 100,000 exceeded in record 0. Set the SafetySwitch property to false if you're expecting more than 100,000 columns per record to avoid this error."),
                    ex);
        }
        reader.close();
    }

    @Test
    void test83() throws Exception {
        CsvReader reader = CsvReader.parse(generateString('a', 100001));
        reader.setSafetySwitch(false);
        reader.readRecord();
        reader.close();
    }

    @Test
    void test84() throws Exception {
        StringBuilder holder = new StringBuilder(200010);

        for (int i = 0; i < 100000; i++) {
            holder.append("a,");
        }

        holder.append("a");

        CsvReader reader = CsvReader.parse(holder.toString());
        reader.setSafetySwitch(false);
        reader.readRecord();
        reader.close();
    }

    @Test
    void test85() throws Exception {
        CsvReader reader = CsvReader.parse(generateString('a', 100000));
        reader.readRecord();
        reader.close();
    }

    @Test
    void test86() throws Exception {
        StringBuilder holder = new StringBuilder(200010);

        for (int i = 0; i < 99999; i++) {
            holder.append("a,");
        }

        holder.append("a");

        CsvReader reader = CsvReader.parse(holder.toString());
        reader.readRecord();
        reader.close();
    }

    @Test
    void test87() throws Exception {
        CsvWriter writer = new CsvWriter("temp.csv");
        writer.write("1");
        writer.close();

        CsvReader reader = new CsvReader("temp.csv");
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();

        new File("temp.csv").delete();
    }

    @Test
    void test88() throws Exception {
        try {
            CsvReader reader = new CsvReader((String) null, ',', StandardCharsets.ISO_8859_1);
            reader.close();
        } catch (Exception ex) {
            assertException(new IllegalArgumentException("Parameter fileName can not be null."), ex);
        }
    }

    @Test
    void test89() throws Exception {
        try {
            CsvReader reader = new CsvReader("temp.csv", ',', null);
            reader.close();
        } catch (Exception ex) {
            assertException(new IllegalArgumentException("Parameter charset can not be null."), ex);
        }
    }

    @Test
    void test90() throws Exception {
        try {
            CsvReader reader = new CsvReader((Reader) null, ',');
            reader.close();
        } catch (Exception ex) {
            assertException(new IllegalArgumentException("Parameter inputStream can not be null."), ex);
        }
    }

    @Test
    void test91() throws Exception {
        byte[] buffer;

        String test = "test";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(stream);
        writer.println(test);
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        CsvReader reader = new CsvReader(new ByteArrayInputStream(buffer),
                StandardCharsets.ISO_8859_1);
        reader.readRecord();
        Assertions.assertEquals(test, reader.get(0));
        reader.close();
    }

    @Test
    void test92() throws Exception {
        byte[] buffer;

        String test = "test";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(stream);
        writer.println(test);
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        CsvReader reader = new CsvReader(new ByteArrayInputStream(buffer), ',',
                StandardCharsets.ISO_8859_1);
        reader.readRecord();
        Assertions.assertEquals(test, reader.get(0));
        reader.close();
    }

    @Test
    void test112() throws Exception {
        try {
            CsvWriter writer = new CsvWriter((String) null, ',', StandardCharsets.ISO_8859_1);
            writer.close();
        } catch (Exception ex) {
            assertException(new IllegalArgumentException("Parameter fileName can not be null."), ex);
        }
    }

    @Test
    void test113() throws Exception {
        try {
            CsvWriter writer = new CsvWriter("test.csv", ',', (Charset) null);
            writer.close();
        } catch (Exception ex) {
            assertException(new IllegalArgumentException("Parameter charset can not be null."), ex);
        }
    }

    @Test
    void test114() throws Exception {
        try {
            CsvWriter writer = new CsvWriter((Writer) null, ',');
            writer.close();
        } catch (Exception ex) {
            assertException(new IllegalArgumentException("Parameter outputStream can not be null."), ex);
        }
    }

    @Test
    void test115() throws Exception {
        try {
            CsvWriter writer = new CsvWriter("test.csv");

            writer.close();

            writer.write("");
        } catch (Exception ex) {
            assertException(new IOException("This instance of the CsvWriter class has already been closed."), ex);
        }
    }

    @Test
    void test117() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        Assertions.assertEquals('#', writer.getComment());
        writer.setComment('~');
        Assertions.assertEquals('~', writer.getComment());

        writer.setRecordDelimiter(';');

        writer.write("1");
        writer.endRecord();
        writer.writeComment("blah");

        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals("1;~blah;", data);
    }

    @Test
    void test118() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (CsvWriter writer = new CsvWriter(stream, '\t', StandardCharsets.ISO_8859_1)) {
            Assertions.assertEquals('\"', writer.userSettings().textQualifier());
            writer.userSettings().withTextQualifier('\'');
            Assertions.assertEquals('\'', writer.userSettings().textQualifier());

            writer.write("1,2");
            writer.write("3");
            writer.write("blah \"some stuff in quotes\"");
            writer.write("blah \'some stuff in quotes\'");
            writer.endRecord();
        }

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(ByteBuffer.wrap(buffer)).toString();
        Assertions.assertEquals("1,2\t3\tblah \"some stuff in quotes\"\t\'blah \'\'some stuff in quotes\'\'\'\r\n",
                data);
    }

    @Test
    void test119() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        writer.write("1,2");
        writer.write("3");
        writer.endRecord();

        Assertions.assertEquals(',', writer.userSettings().delimiter());
        writer.userSettings().withDelimiter('\t');
        Assertions.assertEquals('\t', writer.userSettings().delimiter());

        writer.write("1,2");
        writer.write("3");
        writer.endRecord();
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals("\"1,2\",3\r\n1,2\t3\r\n", data);
    }

    @Test
    void test120() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        writer.write("1,2");
        writer.endRecord();

        buffer = stream.toByteArray();
        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();
        Assertions.assertEquals("", data);

        writer.flush(); // testing that flush flushed to stream

        buffer = stream.toByteArray();
        stream.close();
        data = StandardCharsets.ISO_8859_1.decode(ByteBuffer.wrap(buffer))
                .toString();
        Assertions.assertEquals("\"1,2\"\r\n", data);
        writer.close();
    }

    @Test
    void test121() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        writer.writeRecord(new String[] { " 1 ", "2" }, false);
        writer.writeRecord(new String[] { " 1 ", "2" });
        writer.writeRecord(new String[] { " 1 ", "2" }, true);
        writer.writeRecord(new String[0], true);
        writer.writeRecord(null, true);
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();
        Assertions.assertEquals("1,2\r\n1,2\r\n\" 1 \",2\r\n", data);
    }

    @Test
    void test122() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        writer.write("1,2");
        writer.write(null);
        writer.write("3 ", true);
        writer.endRecord();
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals("\"1,2\",,\"3 \"\r\n", data);
    }

    @Test
    void test123() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        writer.write("#123");
        writer.endRecord();

        writer.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        writer.setUseTextQualifier(false);

        writer.write("#123");
        writer.endRecord();

        writer.write("#");
        writer.endRecord();
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals("\"#123\"\r\n\\#123\r\n\\#\r\n", data);
    }

    @Test
    void test124() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        writer.setRecordDelimiter(';');
        writer.setUseTextQualifier(false);
        writer.userSettings().withEscapeMode(EscapeMode.BACKSLASH);

        writer.write("1;2");
        writer.endRecord();
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals("1\\;2;", data);
    }

    @Test
    void test131() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1)) {
            writer.setUseTextQualifier(false);
            writer.userSettings().withEscapeMode(EscapeMode.BACKSLASH);

            writer.write("1,\\\r\n2");
            writer.endRecord();

            writer.setRecordDelimiter(';');

            writer.write("1,\\;2");
            writer.endRecord();
        }

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals("1\\,\\\\\\\r\\\n2\r\n1\\,\\\\\\;2;", data);
    }

    @Test
    void test132() throws Exception {
        byte[] buffer;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(stream, ',', StandardCharsets.ISO_8859_1);
        writer.userSettings().withEscapeMode(EscapeMode.BACKSLASH);

        writer.write("1,\\2");
        writer.endRecord();
        writer.close();

        buffer = stream.toByteArray();
        stream.close();

        String data = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(buffer)).toString();

        Assertions.assertEquals("\"1,\\\\2\"\r\n", data);
    }

    @Test
    void test135() throws Exception {
        CsvReader reader = CsvReader.parse("1\n\n1\r\r1\r\n\r\n1\n\r1");
        Assertions.assertTrue(reader.getSkipEmptyRecords());
        reader.setSkipEmptyRecords(false);
        Assertions.assertFalse(reader.getSkipEmptyRecords());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("", reader.get(0));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(2L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("", reader.get(0));
        Assertions.assertEquals(3L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(4L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("", reader.get(0));
        Assertions.assertEquals(5L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(6L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("", reader.get(0));
        Assertions.assertEquals(7L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(8L, reader.getCurrentRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test136() throws Exception {
        CsvReader reader = CsvReader.parse("1\n\n1\r\r1\r\n\r\n1\n\r1");
        Assertions.assertTrue(reader.getSkipEmptyRecords());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(2L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(3L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(4L, reader.getCurrentRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test137() throws Exception {
        CsvReader reader = CsvReader.parse("1;; ;1");
        reader.setRecordDelimiter(';');
        Assertions.assertTrue(reader.getSkipEmptyRecords());
        reader.setSkipEmptyRecords(false);
        Assertions.assertFalse(reader.getSkipEmptyRecords());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("", reader.get(0));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("", reader.get(0));
        Assertions.assertEquals(2L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(3L, reader.getCurrentRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test138() throws Exception {
        CsvReader reader = CsvReader.parse("1;; ;1");
        reader.setRecordDelimiter(';');
        Assertions.assertTrue(reader.getSkipEmptyRecords());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("", reader.get(0));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(2L, reader.getCurrentRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test143() throws Exception {
        CsvReader reader = CsvReader.parse("\"" + generateString('a', 100001)
                + "\"");
        try {
            reader.readRecord();
        } catch (Exception ex) {
            assertException(new IOException(
                    "Maximum column length of 100,000 exceeded in column 0 in record 0. Set the SafetySwitch property to false if you're expecting column lengths greater than 100,000 characters to avoid this error."),
                    ex);
        }
        reader.close();
    }

    @Test
    void test144() throws Exception {
        CsvReader reader = CsvReader.parse("\"" + generateString('a', 100000)
                + "\"");
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(generateString('a', 100000), reader.get(0));
        Assertions.assertEquals("\"" + generateString('a', 100000) + "\"", reader
                .getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test145() throws Exception {
        CsvReader reader = CsvReader.parse("\"" + generateString('a', 100001)
                + "\"");
        reader.setSafetySwitch(false);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(generateString('a', 100001), reader.get(0));
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test146() throws Exception {
        // testing SkipLine's buffer
        CsvReader reader = CsvReader.parse("\"" + generateString('a', 10000)
                + "\r\nb");
        Assertions.assertEquals("", reader.getRawRecord());
        Assertions.assertTrue(reader.skipLine());
        Assertions.assertEquals("", reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("b", reader.get(0));
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test147() throws Exception {
        // testing AppendLetter's buffer
        StringBuilder data = new StringBuilder(20000);
        for (int i = 0; i < 10000; i++) {
            data.append("\\b");
        }

        CsvReader reader = CsvReader.parse(data.toString());
        reader.setUseTextQualifier(false);
        reader.userSettings().withEscapeMode(EscapeMode.BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(generateString('\b', 10000), reader.get(0));
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test148() throws Exception {
        // testing a specific case in GetRawRecord where the result is what's in
        // the data buffer
        // plus what's in the raw buffer
        CsvReader reader = CsvReader.parse("\"" + generateString('a', 100000)
                + "\"\r\n" + generateString('a', 100000));
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(generateString('a', 100000), reader.get(0));
        Assertions.assertEquals("\"" + generateString('a', 100000) + "\"", reader
                .getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(generateString('a', 100000), reader.get(0));
        Assertions.assertEquals(generateString('a', 100000), reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test149() throws Exception {
        try {
            new CsvReader("C:\\somefilethatdoesntexist.csv");
        } catch (Exception ex) {
            assertException(new FileNotFoundException("File C:\\somefilethatdoesntexist.csv does not exist."), ex);
        }
    }

    @Test
    void test173() throws Exception {
        FailingReader fail = new FailingReader();

        CsvReader reader = new CsvReader(fail);
        boolean exceptionThrown = false;

        Assertions.assertFalse(fail.disposeCalled);
        try {
            // need to test IO exception block logic while trying to read
            reader.readRecord();
        } catch (IOException ex) {
            // make sure stream that caused exception
            // has been sent a dispose call
            Assertions.assertTrue(fail.disposeCalled);
            exceptionThrown = true;
            Assertions.assertEquals("Read failed.", ex.getMessage());
        } finally {
            reader.close();
        }

        Assertions.assertTrue(exceptionThrown);

        // test to make sure object has been marked
        // internally as disposed
        try {
            reader.getHeaders();
        } catch (Exception ex) {
            assertException(new IOException("This instance of the CsvReader class has already been closed."), ex);
        }
    }

    @Test
    void Test174() throws IOException {
        // verifies that data is eventually automatically flushed
        CsvWriter writer = new CsvWriter("temp.csv");

        for (int i = 0; i < 10000; i++) {
            writer.write("stuff");
            writer.endRecord();
        }

        CsvReader reader = new CsvReader("temp.csv");

        Assertions.assertTrue(reader.readRecord());

        Assertions.assertEquals("stuff", reader.get(0));

        writer.close();
        reader.close();

        new File("temp.csv").delete();
    }
}
