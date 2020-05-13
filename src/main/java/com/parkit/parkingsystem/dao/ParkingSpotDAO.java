package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
        justification = "Jacoco issue Try with ressources")
public class ParkingSpotDAO {
    private static final Logger LOGGER = LogManager.getLogger("ParkingSpotDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public int getNextAvailableSlot(final ParkingType parkingType) {
        int result = -1;
        try (Connection con = dataBaseConfig.getConnection()) {
            try (PreparedStatement ps =
                         con.prepareStatement(
                                 DBConstants.GET_NEXT_PARKING_SPOT)) {
                ps.setString(1, parkingType.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result = rs.getInt(1);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error fetching next available slot", ex);
        }
        return result;
    }

    public boolean updateParking(final ParkingSpot parkingSpot) {
        //update the availability fo that parking slot
        try (Connection con = dataBaseConfig.getConnection()) {
            try (PreparedStatement ps =
                         con.prepareStatement(
                                 DBConstants.UPDATE_PARKING_SPOT)) {
                ps.setBoolean(1, parkingSpot.isAvailable());
                ps.setInt(2, parkingSpot.getId());
                int updateRowCount = ps.executeUpdate();
                return (updateRowCount == 1);
            }
        } catch (Exception ex) {
            LOGGER.error("Error updating parking info", ex);
            return false;
        }
    }
}
