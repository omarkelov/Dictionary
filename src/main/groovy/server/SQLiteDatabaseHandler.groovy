package server

import exceptions.ProcessingException
import groovy.transform.CompileStatic

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

import static Server.SITE_NAME

@CompileStatic
class SQLiteDatabaseHandler {
    private static final String DB_DIRECTORY = 'src/main/resources/database/'
    private static final String DB_FILENAME = 'database.db'
    private static final String DB_PREFIX = "jdbc:sqlite:"
    private static final String DB_URL = "$DB_PREFIX$DB_DIRECTORY$DB_FILENAME"

    private Connection connection

    SQLiteDatabaseHandler() {
        File dir = new File(DB_DIRECTORY)
        if (!dir.exists()) {
            dir.mkdir()
        }

        connect() // TODO private method execute()
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS $PathTable.NAME (
                    $PathTable.Cols.PATH TEXT NOT NULL,
                    UNIQUE($PathTable.Cols.PATH)
                );
            """)
        } catch (SQLException e) {
            throw new ProcessingException("Unable to create or open database")
        } finally {
            disconnect()
        }

        /*try { TODO delete
            insertPath(SITE_NAME)
        } catch (ProcessingException ignored) {}*/
    }

    Collection<String> getPaths() {
        ArrayList<String> paths = new ArrayList<>()

        connect() // TODO private method
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("""
                SELECT $PathTable.Cols.PATH FROM $PathTable.NAME;
            """)

            while (resultSet.next()) {
                paths.add(resultSet.getString(PathTable.Cols.PATH))
            }
        } catch (SQLException e) {
            throw new ProcessingException(e.getMessage())
        } finally {
            disconnect()
        }

        paths
    }

    void insertPath(String path) {
        connect() // TODO private method
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                INSERT INTO $PathTable.NAME ($PathTable.Cols.PATH)
                VALUES('$path');
            """)
        } catch (SQLException e) {
            throw new ProcessingException(e.getMessage())
        } finally {
            disconnect()
        }
    }

    void deletePath(String path) {
        connect() // TODO private method
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                DELETE FROM $PathTable.NAME WHERE $PathTable.Cols.PATH = '$path';
            """)
        } catch (SQLException e) {
            throw new ProcessingException(e.getMessage())
        } finally {
            disconnect()
        }
    }

    private void connect() {
        try {
//            Class.forName("org.sqlite.JDBC") // TODO delete or replace?
            connection = DriverManager.getConnection(DB_URL)
        } catch (SQLException/* | ClassNotFoundException*/ e) { // TODO delete
            e.printStackTrace()
        }
    }

    private void disconnect() {
        try {
            connection.close()
        } catch (SQLException e) {
            e.printStackTrace()
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
