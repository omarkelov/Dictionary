package server

import exceptions.ProcessingException
import groovy.transform.CompileStatic

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
                    $PathTable.Cols.PATH TEXT NOT NULL,
                    UNIQUE($PathTable.Cols.PATH)
                );
            """)
        })
    }

    Collection<String> getPaths() {
        ArrayList<String> paths = new ArrayList<>()

        executeStatement({ Statement statement ->
            ResultSet resultSet = statement.executeQuery("""
                SELECT $PathTable.Cols.PATH FROM $PathTable.NAME;
            """)

            while (resultSet.next()) {
                paths.add(resultSet.getString(PathTable.Cols.PATH))
            }
        })

        paths
    }

    void insertPath(String path) {
        executeStatement({ Statement statement ->
            statement.execute("""
                INSERT INTO $PathTable.NAME ($PathTable.Cols.PATH)
                VALUES('$path');
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

    private void executeStatement(Closure closure) {
        try (
            Connection connection = DriverManager.getConnection(DB_URL);
            Statement statement = connection.createStatement()
        ) {
            closure.call(statement)
        } catch (SQLException e) {
            throw new ProcessingException(e.getMessage())
        }
    }

    private static final class PathTable {
        public static final String NAME = "paths"

        static final class Cols {
            public static final String PATH = "path"
            public static final String DATE = "date"
        }
    }
}
