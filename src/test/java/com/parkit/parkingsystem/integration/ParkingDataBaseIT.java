package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import static com.parkit.parkingsystem.constants.Fare.CAR_RATE_PER_HOUR;
import static com.parkit.parkingsystem.constants.Fare.CAR_RATE_PER_MINUTE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private static final String vehicleRegNumberTest = "ABCDEF";
    private static final long minutesParkingTime = 60;
    private static final double reducePercent = 5;

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

    @AfterEach
    private void tearDownPerTest() {
        dataBasePrepareService.clearDataBaseEntries();
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
    }

    @Test
    public void testParkingLotExit() {
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        Ticket ticketTest = ticketDAO.getTicket(vehicleRegNumberTest);
        assertNotNull(ticketTest);
        checkCalculateFarFromDB(ticketTest);
        assertTrue(ticketTest.getPaid());
        assertTrue(ticketTest.getParkingSpot().isAvailable());
    }

    public void checkCalculateFarFromDB(Ticket ticketTest) {
        switch (ticketTest.getParkingSpot().getParkingType()) {
            case CAR: {
                if (ticketDAO.isRecurrentUser(vehicleRegNumberTest, 3)) {
                    checkCalculateRecurrentCarFarFromDB(ticketTest);
                } else {
                    checkCalculateCarFareFromDB(ticketTest);
                }
                break;
            }
            case BIKE: {
                break;
            }
        }
    }

    private void checkCalculateRecurrentCarFarFromDB(Ticket ticketTest) {
        double price;
        if (minutesParkingTime <= 30) {
            price = 0;
        } else if (minutesParkingTime < 60) {
            price = (minutesParkingTime * CAR_RATE_PER_MINUTE) * (1 - (reducePercent / 100));
        } else {
            price = ((double) (minutesParkingTime / 60) * CAR_RATE_PER_HOUR) * (1 - (reducePercent / 100));
        }
        price = roundToHundred(price);
        assertEquals(price, ticketTest.getPrice());
    }

    public void checkCalculateCarFareFromDB(Ticket ticketTest) {
        double price;
        if (minutesParkingTime <= 30) {
            price = 0;
        } else if (minutesParkingTime < 60) {
            price = minutesParkingTime * CAR_RATE_PER_MINUTE;
        } else {
            price = (double) (minutesParkingTime / 60) * CAR_RATE_PER_HOUR;
        }
        price = roundToHundred(price);
        assertEquals(price, ticketTest.getPrice());
    }

    public static double roundToHundred(double nb) {
        BigDecimal bd = new BigDecimal(nb);
        bd = bd.setScale(2, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }


    @Test
    public void recurrentThreeTimeCarUserTest() {
        testParkingLotExit();
        testParkingLotExit();
        testParkingLotExit();
    }
}
