package server

import exceptions.ProcessingException
import groovy.transform.CompileStatic
import server.beans.ParagraphBean
import server.beans.PathBean
import server.beans.PhraseBean

import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

import static Server.DATABASE_DIRECTORY

@CompileStatic
class SQLiteDatabaseHandler {
    private static final String DB_FILENAME = 'database.db'
    private static final String DB_PREFIX = "jdbc:sqlite:"
    private static final String DB_URL = "$DB_PREFIX$DATABASE_DIRECTORY$DB_FILENAME"

    SQLiteDatabaseHandler() {
        Files.createDirectories(Paths.get(DATABASE_DIRECTORY))

        executeStatement({ Statement statement ->
            statement.execute("""
                CREATE TABLE IF NOT EXISTS $PathTable.NAME (
                    $PathTable.Cols.UUID TEXT NOT NULL,
                    $PathTable.Cols.PATH TEXT NOT NULL,
                    UNIQUE($PathTable.Cols.UUID, $PathTable.Cols.PATH)
                );
            """)

            statement.execute("""
                CREATE TABLE IF NOT EXISTS $ParagraphTable.NAME (
                    $ParagraphTable.Cols.UUID TEXT NOT NULL,
                    $ParagraphTable.Cols.PATH_ID TEXT NOT NULL,
                    $ParagraphTable.Cols.PARAGRAPH TEXT NOT NULL,
                    UNIQUE($ParagraphTable.Cols.UUID)
                );
            """)

            statement.execute("""
                CREATE TABLE IF NOT EXISTS $PhrasesTable.NAME (
                    $PhrasesTable.Cols.UUID TEXT NOT NULL,
                    $PhrasesTable.Cols.PARAGRAPH_ID TEXT NOT NULL,
                    $PhrasesTable.Cols.PHRASE TEXT NOT NULL,
                    $PhrasesTable.Cols.CORRECTED_PHRASE TEXT NOT NULL,
                    $PhrasesTable.Cols.TYPE TEXT NOT NULL,
                    $PhrasesTable.Cols.TRANSLATION TEXT NOT NULL,
                    UNIQUE($PhrasesTable.Cols.UUID)
                );
            """)
        })
    }

    Collection<String> getPaths() {
        ArrayList<String> paths = new ArrayList<>()

        executeStatement({ Statement statement ->
            try (
                ResultSet pathResultSet = statement.executeQuery("""
                    SELECT $PathTable.Cols.PATH FROM $PathTable.NAME;
                """)
            ) {
                while (pathResultSet.next()) {
                    paths.add(pathResultSet.getString(PathTable.Cols.PATH))
                }
            }
        })

        paths
    }

    boolean exists(String path) {
        boolean result = false

        executeStatement({ Statement statement ->
            try (
                ResultSet pathCountResultSet = statement.executeQuery("""
                    SELECT count(*) FROM $PathTable.NAME WHERE $PathTable.Cols.PATH = '$path';
                """)
            ) {
                result = pathCountResultSet.getInt('count(*)') > 0
            }
        })

        result
    }

    void insertPath(PathBean pagePath) {
        executeStatement({ Statement statement ->
            statement.execute("""
                INSERT INTO $PathTable.NAME ($PathTable.Cols.UUID, $PathTable.Cols.PATH)
                VALUES ('$pagePath.uuid', '$pagePath.path');
            """)
        })
    }

    void deletePath(String path) {
        executeStatement({ Statement statement ->
            statement.execute("""
                DELETE FROM $PathTable.NAME WHERE $PathTable.Cols.PATH = '$path';
            """)
        })
    }

    void insertParagraph(ParagraphBean paragraph) {
        executeStatement({ Statement statement ->
            String pathId = getPathId(paragraph.path)

            statement.execute("""
                INSERT INTO $ParagraphTable.NAME ($ParagraphTable.Cols.UUID, $ParagraphTable.Cols.PATH_ID, $ParagraphTable.Cols.PARAGRAPH)
                VALUES ('$paragraph.uuid', '$pathId', '$paragraph.paragraph');
            """)

            paragraph.phrases.each {
                statement.execute("""
                    INSERT INTO $PhrasesTable.NAME (
                        $PhrasesTable.Cols.UUID,
                        $PhrasesTable.Cols.PARAGRAPH_ID,
                        $PhrasesTable.Cols.PHRASE,
                        $PhrasesTable.Cols.CORRECTED_PHRASE,
                        $PhrasesTable.Cols.TYPE,
                        $PhrasesTable.Cols.TRANSLATION
                    ) VALUES (
                        '$it.uuid',
                        '$paragraph.uuid',
                        '$it.phrase',
                        '$it.correctedPhrase',
                        '$it.type',
                        '$it.translation'
                    );
                """)
            }
        })
    }

    Collection<ParagraphBean> getParagraphs(String path) {
        ArrayList<ParagraphBean> paragraphs = new ArrayList<>()

        executeStatement({ Statement paragraphStatement ->
            String pathId = getPathId(path)

            try (
                ResultSet paragraphResultSet = paragraphStatement.executeQuery("""
                    SELECT * FROM $ParagraphTable.NAME WHERE $ParagraphTable.Cols.PATH_ID = '$pathId';
                """)
            ) {
                while (paragraphResultSet.next()) {
                    ParagraphBean paragraph = new ParagraphBean(
                        uuid: paragraphResultSet.getString(ParagraphTable.Cols.UUID),
                        path: path,
                        paragraph: paragraphResultSet.getString(ParagraphTable.Cols.PARAGRAPH)
                    )

                    executeStatement({ Statement phrasesStatement ->
                        try (
                            ResultSet phrasesResultSet = phrasesStatement.executeQuery("""
                                SELECT * FROM $PhrasesTable.NAME WHERE $PhrasesTable.Cols.PARAGRAPH_ID = '$paragraph.uuid';
                            """)
                        ) {
                            ArrayList<PhraseBean> phrases = new ArrayList<>()

                            while (phrasesResultSet.next()) {
                                phrases.add(new PhraseBean(
                                    uuid: phrasesResultSet.getString(PhrasesTable.Cols.UUID),
                                    phrase: phrasesResultSet.getString(PhrasesTable.Cols.PHRASE),
                                    correctedPhrase: phrasesResultSet.getString(PhrasesTable.Cols.CORRECTED_PHRASE),
                                    type: phrasesResultSet.getString(PhrasesTable.Cols.TYPE),
                                    translation: phrasesResultSet.getString(PhrasesTable.Cols.TRANSLATION)
                                ))
                            }

                            paragraph.phrases = phrases.toArray(new PhraseBean[0])
                        }
                    })

                    paragraphs.add(paragraph)
                }
            }
        })

        paragraphs
    }

    private String getPathId(String path) {
        String pathId

        executeStatement({ Statement statement ->
            try (
                ResultSet resultSet = statement.executeQuery("""
                    SELECT $PathTable.Cols.UUID FROM $PathTable.NAME WHERE $PathTable.Cols.PATH = '$path';
                """)
            ) {
                pathId = resultSet.getString(PathTable.Cols.UUID)
            }
        })

        pathId
    }

    private void executeStatement(Closure closure) {
        try (
            Connection connection = DriverManager.getConnection(DB_URL);
            Statement statement = connection.createStatement()
        ) {
            closure.call(statement)
        } catch (SQLException e) {
//            e.printStackTrace()
            throw new ProcessingException(e.getMessage())
        }
    }

    private static final class PathTable {
        public static final String NAME = 'Paths'

        static final class Cols {
            public static final String UUID = 'UUID'
            public static final String PATH = 'Path'
        }
    }

    private static final class ParagraphTable {
        public static final String NAME = 'Paragraph'

        static final class Cols {
            public static final String UUID = 'UUID'
            public static final String PATH_ID = 'PathId'
            public static final String PARAGRAPH = 'Paragraph'
        }
    }

    private static final class PhrasesTable {
        public static final String NAME = 'Phrases'

        static final class Cols {
            public static final String UUID = 'UUID'
            public static final String PARAGRAPH_ID = 'ParagraphId'
            public static final String PHRASE = 'Phrase'
            public static final String CORRECTED_PHRASE = 'CorrectedPhrase'
            public static final String TYPE = 'Type'
            public static final String TRANSLATION = 'Translation'
        }
    }
}
