package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

@SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
        justification = "Jacoco issue Try with ressources")
public class TicketDAO {

    private static final Logger LOGGER = LogManager.getLogger("TicketDAO");

    private static final int TICKET_ID_SAVE_DB = 1;
    private static final int VEHICLE_REG_NUMBER_SAVE_DB = 2;
    private static final int TICKET_PRICE_SAVE_DB = 3;
    private static final int TICKET_IN_TIME_SAVE_DB = 4;
    private static final int TICKET_OUT_TIME_SAVE_DB = 5;
    private static final int TICKET_PAID_SAVE_DB = 6;

    private static final int SET_REQUEST_REG_NUMBER_GET_BD = 1;

    private static final int PARKING_NUMBER_GET_DB = 1;
    private static final int VEHICLE_REG_NUMBER_GET_DB = 2;
    private static final int TICKET_ID_GET_DB = 3;
    private static final int TICKET_PRICE_GET_DB = 4;
    private static final int TICKET_IN_TIME_GET_DB = 5;
    private static final int TICKET_OUT_TIME_GET_DB = 6;
    private static final int TICKET_PAID_GET_DB = 7;
    private static final int PARKING_AVAILABLE_GET_DB = 8;
    private static final int PARKING_TYPE_GET_DB = 9;

    private static final int SET_REQUEST_PRICE_UP_BD = 1;
    private static final int SET_REQUEST_OUT_TIME_UP_BD = 2;
    private static final int SET_REQUEST_PAID_UP_BD = 3;
    private static final int SET_REQUEST_WHERE_ID_UP_BD = 4;

    private static final int SET_REQUEST_REG_NUMBER_RECURRENT_BD = 1;
    private static final int GET_COUNT_RECURRENT_BD = 1;

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public boolean saveTicket(final Ticket ticket) {
        try (Connection con = dataBaseConfig.getConnection()) {
            try (PreparedStatement ps =
                    con.prepareStatement(DBConstants.SAVE_TICKET)) {
                ps.setInt(TICKET_ID_SAVE_DB, ticket.getParkingSpot().getId());
                ps.setString(
                        VEHICLE_REG_NUMBER_SAVE_DB,
                        ticket.getVehicleRegNumber());
                ps.setDouble(
                        TICKET_PRICE_SAVE_DB, ticket.getPrice());
                ps.setTimestamp(
                        TICKET_IN_TIME_SAVE_DB, new Timestamp(
                                ticket.getInTime().getTime()));
                ps.setTimestamp(
                        TICKET_OUT_TIME_SAVE_DB, (
                                ticket.getOutTime() == null) ? null
                                : (new Timestamp(
                                        ticket.getOutTime().getTime())));
                ps.setBoolean(TICKET_PAID_SAVE_DB, ticket.getPaid());
                return ps.execute();
            }
        } catch (Exception ex) {
            LOGGER.error("Error fetching next available slot", ex);
            return false;
        }
    }

    public Ticket getTicket(final String vehicleRegNumber) {
        Ticket ticket = null;
        try (Connection con = dataBaseConfig.getConnection()) {
            try (PreparedStatement ps =
                         con.prepareStatement(DBConstants.GET_TICKET)) {
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE,
                //IN_TIME, OUT_TIME, IS_PAID, P_AVAILABLE ,P_TYPE)
                ps.setString(SET_REQUEST_REG_NUMBER_GET_BD, vehicleRegNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ticket = new Ticket();
                        ParkingSpot parkingSpot = new ParkingSpot(
                                rs.getInt(PARKING_NUMBER_GET_DB),
                                ParkingType.valueOf(rs.getString(
                                        PARKING_TYPE_GET_DB)),
                                rs.getBoolean(PARKING_AVAILABLE_GET_DB));
                        ticket.setParkingSpot(parkingSpot);
                        ticket.setId(rs.getInt(TICKET_ID_GET_DB));
                        ticket.setVehicleRegNumber(rs.getString(
                                VEHICLE_REG_NUMBER_GET_DB));
                        ticket.setPrice(rs.getDouble(TICKET_PRICE_GET_DB));
                        ticket.setInTime(rs.getTimestamp(
                                TICKET_IN_TIME_GET_DB));
                        ticket.setOutTime(rs.getTimestamp(
                                TICKET_OUT_TIME_GET_DB));
                        ticket.setPaid(rs.getBoolean(TICKET_PAID_GET_DB));
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error fetching next available slot", ex);
        }
        return ticket;
    }

    public boolean updateTicket(final Ticket ticket) {
        try (Connection con = dataBaseConfig.getConnection()) {
            try (PreparedStatement ps =
                         con.prepareStatement(DBConstants.UPDATE_TICKET)) {
                ps.setDouble(SET_REQUEST_PRICE_UP_BD, ticket.getPrice());
                ps.setTimestamp(SET_REQUEST_OUT_TIME_UP_BD, new Timestamp(
                        ticket.getOutTime().getTime()));
                ps.setBoolean(SET_REQUEST_PAID_UP_BD, ticket.getPaid());
                ps.setInt(SET_REQUEST_WHERE_ID_UP_BD, ticket.getId());
                ps.execute();
                return true;
            }
        } catch (Exception ex) {
            LOGGER.error("Error saving ticket info", ex);
        }
        return false;
    }

    public boolean isRecurrentUser(
            final String vehicleRegNumber, final int nbTicketToBeRecurrent) {
        try (Connection con = dataBaseConfig.getConnection()) {
            try (PreparedStatement ps =
                         con.prepareStatement(DBConstants.GET_RECURRENT_USER)) {
                //ID
                ps.setString(SET_REQUEST_REG_NUMBER_RECURRENT_BD,
                        vehicleRegNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getInt(GET_COUNT_RECURRENT_BD)
                                >= nbTicketToBeRecurrent) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error fetching next available slot", ex);
        }
        return false;
    }
}
