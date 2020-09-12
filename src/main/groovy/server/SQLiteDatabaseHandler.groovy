package server

import exceptions.ProcessingException
import groovy.transform.CompileStatic
import server.beans.PathBean
import server.beans.PhrasesBean

import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

@CompileStatic
class SQLiteDatabaseHandler {
    private static final String DB_DIRECTORY = 'src/main/resources/database/'
    private static final String DB_FILENAME = 'database.db'
    private static final String DB_PREFIX = "jdbc:sqlite:"
    private static final String DB_URL = "$DB_PREFIX$DB_DIRECTORY$DB_FILENAME"

    SQLiteDatabaseHandler() {
        Files.createDirectories(Paths.get(DB_DIRECTORY))

        executeStatement({ Statement statement ->
            statement.execute("""
                CREATE TABLE IF NOT EXISTS $PathTable.NAME (
                    $PathTable.Cols.UUID TEXT NOT NULL,
                    $PathTable.Cols.PATH TEXT NOT NULL,
                    UNIQUE($PathTable.Cols.UUID, $PathTable.Cols.PATH)
                );
            """)

            statement.execute("""
                CREATE TABLE IF NOT EXISTS $TextTable.NAME (
                    $TextTable.Cols.UUID TEXT NOT NULL,
                    $TextTable.Cols.PATH_ID TEXT NOT NULL,
                    $TextTable.Cols.TEXT TEXT NOT NULL,
                    UNIQUE($TextTable.Cols.UUID)
                );
            """)

            statement.execute("""
                CREATE TABLE IF NOT EXISTS $PhrasesTable.NAME (
                    $PhrasesTable.Cols.UUID TEXT NOT NULL,
                    $PhrasesTable.Cols.TEXT_ID TEXT NOT NULL,
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
                ResultSet resultSet = statement.executeQuery("""
                    SELECT $PathTable.Cols.PATH FROM $PathTable.NAME;
                """)
            ) {
                while (resultSet.next()) {
                    paths.add(resultSet.getString(PathTable.Cols.PATH))
                }
            }
        })

        paths
    }

    boolean exists(String path) {
        boolean result = false

        executeStatement({ Statement statement ->
            try (
                ResultSet resultSet = statement.executeQuery("""
                    SELECT count(*) FROM $PathTable.NAME WHERE $PathTable.Cols.PATH = '$path';
                """)
            ) {
                result = resultSet.getInt('count(*)') > 0
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

    void insertPhrases(PhrasesBean phrasesBean) {
        /*println phrasesBean.path
        println phrasesBean.text
        phrasesBean.phrases.each {
            println it.phrase
            println it.correctedPhrase
            println it.type
            println it.translation
        }*/
        executeStatement({ Statement statement ->
            String pathId

            try (
                ResultSet resultSet = statement.executeQuery("""
                    SELECT $PathTable.Cols.UUID FROM $PathTable.NAME WHERE $PathTable.Cols.PATH = '$phrasesBean.path';
                """)
            ) {
                pathId = resultSet.getString(PathTable.Cols.UUID)
            }

            statement.execute("""
                INSERT INTO $TextTable.NAME ($TextTable.Cols.UUID, $TextTable.Cols.PATH_ID, $TextTable.Cols.TEXT)
                VALUES ('$phrasesBean.uuid', '$pathId', '$phrasesBean.text');
            """)

            phrasesBean.phrases.each {
                statement.execute("""
                    INSERT INTO $PhrasesTable.NAME (
                        $PhrasesTable.Cols.UUID,
                        $PhrasesTable.Cols.TEXT_ID,
                        $PhrasesTable.Cols.PHRASE,
                        $PhrasesTable.Cols.CORRECTED_PHRASE,
                        $PhrasesTable.Cols.TYPE,
                        $PhrasesTable.Cols.TRANSLATION
                    ) VALUES (
                        '$it.uuid',
                        '$phrasesBean.uuid',
                        '$it.phrase',
                        '$it.correctedPhrase',
                        '$it.type',
                        '$it.translation'
                    );
                """)
            }
        })
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

    private static final class TextTable {
        public static final String NAME = 'Text'

        static final class Cols {
            public static final String UUID = 'UUID'
            public static final String PATH_ID = 'PathId'
            public static final String TEXT = 'Text'
        }
    }

    private static final class PhrasesTable {
        public static final String NAME = 'Phrases'

        static final class Cols {
            public static final String UUID = 'UUID'
            public static final String TEXT_ID = 'TextId'
            public static final String PHRASE = 'Phrase'
            public static final String CORRECTED_PHRASE = 'CorrectedPhrase'
            public static final String TYPE = 'Type'
            public static final String TRANSLATION = 'Translation'
        }
    }
}
