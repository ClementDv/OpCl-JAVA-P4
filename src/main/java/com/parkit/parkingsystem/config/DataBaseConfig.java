package com.parkit.parkingsystem.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DataBaseConfig {

    private static final Logger LOGGER = LogManager.getLogger("DataBaseConfig");

    @SuppressFBWarnings(value = "DMI_CONSTANT_DB_PASSWORD",
            justification = "Test version")
    public Connection getConnection()
            throws ClassNotFoundException, SQLException {
        LOGGER.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test?useUnicode=true"
                        + "&useJDBCCompliantTimezoneShift=true"
                        + "&useLegacyDatetimeCode=false"
                        + "&serverTimezone=Europe/Paris",
                "root", "rootroot");
    }
}
