package server

import exceptions.ProcessingException
import groovy.transform.CompileStatic

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

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

        connect()

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS $PathTable.NAME ($PathTable.Cols.PATH TEXT PRIMARY KEY)"
            )
        } catch (SQLException e) {
            throw new ProcessingException("Unable to create or open database")
        } finally {
            disconnect()
        }
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC") // TODO delete or replace?
            connection = DriverManager.getConnection(DB_URL)
        } catch (SQLException | ClassNotFoundException e) {
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
