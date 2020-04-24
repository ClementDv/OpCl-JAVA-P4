package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.FareCalculatorServiceTest;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static com.parkit.parkingsystem.constants.Fare.CAR_RATE_PER_HOUR;
import static com.parkit.parkingsystem.constants.Fare.CAR_RATE_PER_MINUTE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private static final String vehicleRegNumberTest = "ABCDEF";
    private static final long minutesParkingTime = 48;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumberTest);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {
    }

    @Test
    public void testParkingACar() {
        Date inTime = new Date(System.currentTimeMillis() - minutesParkingTime * 60 * 1000);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle(inTime);
        Ticket ticketTest = ticketDAO.getTicket(vehicleRegNumberTest);
        assertNotNull(ticketTest);
        assertEquals(vehicleRegNumberTest, ticketTest.getVehicleRegNumber());
        assertFalse(ticketTest.getParkingSpot().isAvailable());
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
    }

    @Test
    public void testParkingLotExit() {
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        Ticket ticketTest = ticketDAO.getTicket(vehicleRegNumberTest);
        assertNotNull(ticketTest);
        if (minutesParkingTime <= 30) {
            assertEquals(0, ticketTest.getPrice());
        } else if (minutesParkingTime <= 60) {
            assertEquals(minutesParkingTime * CAR_RATE_PER_MINUTE, ticketTest.getPrice());
        } else {
            assertEquals(((double) minutesParkingTime / 60) * CAR_RATE_PER_HOUR, ticketTest.getPrice());
        }
        assertTrue(ticketTest.getPaid());
        assertTrue(ticketTest.getParkingSpot().isAvailable());
        //TODO: check that the fare generated and out time are populated correctly in the database
    }
}
