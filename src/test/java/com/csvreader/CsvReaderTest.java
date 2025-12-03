package com.csvreader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CsvReaderTest {

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
    void test1() throws Exception {
        CsvReader reader = CsvReader.parse("1,2");
        Assertions.assertEquals("", reader.getRawRecord());
        Assertions.assertEquals("", reader.get(0));
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals("2", reader.get(1));
        Assertions.assertEquals(',', reader.userSettings().delimiter());
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertEquals("1,2", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        Assertions.assertEquals("", reader.getRawRecord());
        reader.close();
    }

    @Test
    void test2() throws Exception {
        String data = "\"bob said, \"\"Hey!\"\"\",2, 3 ";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("bob said, \"Hey!\"", reader.get(0));
        Assertions.assertEquals("2", reader.get(1));
        Assertions.assertEquals("3", reader.get(2));
        Assertions.assertEquals(',', reader.userSettings().delimiter());
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(3, reader.getColumnCount());
        Assertions.assertEquals("\"bob said, \"\"Hey!\"\"\",2, 3 ", reader
                .getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        Assertions.assertEquals("", reader.getRawRecord());
        reader.close();
    }

    @Test
    void test3() throws Exception {
        String data = ",";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("", reader.get(0));
        Assertions.assertEquals("", reader.get(1));
        Assertions.assertEquals(',', reader.userSettings().delimiter());
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertEquals(",", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        Assertions.assertEquals("", reader.getRawRecord());
        reader.close();
    }

    @Test
    void test4() throws Exception {
        String data = "1\r2";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("2", reader.get(0));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("2", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        Assertions.assertEquals("", reader.getRawRecord());
        reader.close();
    }

    @Test
    void test5() throws Exception {
        String data = "1\n2";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("2", reader.get(0));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("2", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        Assertions.assertEquals("", reader.getRawRecord());
        reader.close();
    }

    @Test
    void test6() throws Exception {
        String data = "1\r\n2";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("2", reader.get(0));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("2", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        Assertions.assertEquals("", reader.getRawRecord());
        reader.close();
    }

    @Test
    void test7() throws Exception {
        String data = "1\r";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        Assertions.assertEquals("", reader.getRawRecord());
        reader.close();
    }

    @Test
    void test8() throws Exception {
        String data = "1\n";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        Assertions.assertEquals("", reader.getRawRecord());
        reader.close();
    }

    @Test
    void test9() throws Exception {
        String data = "1\r\n";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        Assertions.assertEquals("", reader.getRawRecord());
        reader.close();
    }

    @Test
    void test10() throws Exception {
        String data = "1\r2\n";

        CsvReader reader = CsvReader.parse(data);
        reader.userSettings().withDelimiter('\r');
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals("2", reader.get(1));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertEquals("1\r2", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        Assertions.assertEquals("", reader.getRawRecord());
        reader.close();
    }

    @Test
    void test11() throws Exception {
        String data = "\"July 4th, 2005\"";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("July 4th, 2005", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("\"July 4th, 2005\"", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test12() throws Exception {
        String data = " 1";

        CsvReader reader = CsvReader.parse(data);
        reader.setTrimWhitespace(false);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(" 1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals(" 1", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test13() throws Exception {
        String data = "";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test14() throws Exception {
        String data = "user_id,name\r\n1,Bruce";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readHeaders());
        Assertions.assertEquals("user_id,name", reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals("Bruce", reader.get(1));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertEquals(0, reader.getIndex("user_id"));
        Assertions.assertEquals(1, reader.getIndex("name"));
        Assertions.assertEquals("user_id", reader.getHeader(0));
        Assertions.assertEquals("name", reader.getHeader(1));
        Assertions.assertEquals("1", reader.get("user_id"));
        Assertions.assertEquals("Bruce", reader.get("name"));
        Assertions.assertEquals("1,Bruce", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test15() throws Exception {
        String data = "\"data \r\n here\"";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("data \r\n here", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("\"data \r\n here\"", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test16() throws Exception {
        String data = "\r\r\n1\r";

        CsvReader reader = CsvReader.parse(data);
        reader.userSettings().withDelimiter('\r');
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("", reader.get(0));
        Assertions.assertEquals("", reader.get(1));
        Assertions.assertEquals("", reader.get(2));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(3, reader.getColumnCount());
        Assertions.assertEquals("\r\r", reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals("", reader.get(1));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertEquals("1\r", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test17() throws Exception {
        String data = "\"double\"\"\"\"double quotes\"";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("double\"\"double quotes", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("\"double\"\"\"\"double quotes\"", reader
                .getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test18() throws Exception {
        String data = "1\r";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test19() throws Exception {
        String data = "1\r\n";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test20() throws Exception {
        String data = "1\n";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test21() throws Exception {
        String data = "'bob said, ''Hey!''',2, 3 ";

        CsvReader reader = CsvReader.parse(data);
        reader.setTextQualifier('\'');
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("bob said, 'Hey!'", reader.get(0));
        Assertions.assertEquals("2", reader.get(1));
        Assertions.assertEquals("3", reader.get(2));
        Assertions.assertEquals(',', reader.userSettings().delimiter());
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(3, reader.getColumnCount());
        Assertions
                .assertEquals("'bob said, ''Hey!''',2, 3 ", reader
                        .getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test22() throws Exception {
        String data = "\"data \"\" here\"";

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(false);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("\"data \"\" here\"", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("\"data \"\" here\"", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test23() throws Exception {
        String data = generateString('a', 75) + "," + generateString('b', 75);

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(false);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals(reader.get(0), generateString('a', 75));
        Assertions.assertEquals(reader.get(1), generateString('b', 75));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertEquals(generateString('a', 75) + ","
                + generateString('b', 75), reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test24() throws Exception {
        String data = "1\r\n\r\n1";

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(false);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test25() throws Exception {
        String data = "1\r\n# bunch of crazy stuff here\r\n1";

        CsvReader reader = CsvReader.parse(data);
        reader.setUseTextQualifier(false);
        reader.setUseComments(true);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("1", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test26() throws Exception {
        String data = "\"Mac \"The Knife\" Peter\",\"Boswell, Jr.\"";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("Mac ", reader.get(0));
        Assertions.assertEquals("Boswell, Jr.", reader.get(1));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertEquals("\"Mac \"The Knife\" Peter\",\"Boswell, Jr.\"",
                reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test27() throws Exception {
        String data = "\"1\",Bruce\r\n\"2\n\",Toni\r\n\"3\",Brian\r\n";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("1", reader.get(0));
        Assertions.assertEquals("Bruce", reader.get(1));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertEquals("\"1\",Bruce", reader.getRawRecord());
        Assertions.assertTrue(reader.skipRecord());
        Assertions.assertEquals("\"2\n\",Toni", reader.getRawRecord());
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("3", reader.get(0));
        Assertions.assertEquals("Brian", reader.get(1));
        Assertions.assertEquals(1L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertEquals("\"3\",Brian", reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test28() throws Exception {
        String data = "\"bob said, \\\"Hey!\\\"\",2, 3 ";

        CsvReader reader = CsvReader.parse(data);
        reader.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("bob said, \"Hey!\"", reader.get(0));
        Assertions.assertEquals("2", reader.get(1));
        Assertions.assertEquals("3", reader.get(2));
        Assertions.assertEquals(',', reader.userSettings().delimiter());
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(3, reader.getColumnCount());
        Assertions.assertEquals("\"bob said, \\\"Hey!\\\"\",2, 3 ", reader
                .getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test29() throws Exception {
        String data = "\"double\\\"\\\"double quotes\"";

        CsvReader reader = CsvReader.parse(data);
        reader.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("double\"\"double quotes", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("\"double\\\"\\\"double quotes\"", reader
                .getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test30() throws Exception {
        String data = "\"double\\\\\\\\double backslash\"";

        CsvReader reader = CsvReader.parse(data);
        reader.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("double\\\\double backslash", reader.get(0));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(1, reader.getColumnCount());
        Assertions.assertEquals("\"double\\\\\\\\double backslash\"", reader
                .getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }

    @Test
    void test32() throws Exception {
        String data = "\"Mac \"The Knife\" Peter\",\"Boswell, Jr.\"";

        CsvReader reader = CsvReader.parse(data);
        Assertions.assertTrue(reader.readRecord());
        Assertions.assertEquals("Mac ", reader.get(0));
        Assertions.assertEquals("Boswell, Jr.", reader.get(1));
        Assertions.assertEquals(0L, reader.getCurrentRecord());
        Assertions.assertEquals(2, reader.getColumnCount());
        Assertions.assertEquals("\"Mac \"The Knife\" Peter\",\"Boswell, Jr.\"",
                reader.getRawRecord());
        Assertions.assertFalse(reader.readRecord());
        reader.close();
    }
}
