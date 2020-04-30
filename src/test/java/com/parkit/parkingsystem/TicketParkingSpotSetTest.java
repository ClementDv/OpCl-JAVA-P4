package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class TicketParkingSpotSetTest {

    private Ticket ticket;
    private ParkingSpot parkingSpot;

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void ticketSetTest() {
        ticket.setId(12);
        ticket.setVehicleRegNumber("ABCDEF");
        parkingSpot = new ParkingSpot(10, ParkingType.CAR, true);
        ticket.setParkingSpot(parkingSpot);
        ticket.setPrice(10);
        Date inTime = new Date(System.currentTimeMillis());
        ticket.setInTime(inTime);
        Date outTime = new Date(System.currentTimeMillis());
        ticket.setOutTime(outTime);
        ticket.setPaid(true);
        assertEquals(12, ticket.getId());
        assertSame(parkingSpot, ticket.getParkingSpot());
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertEquals(10, ticket.getPrice());
        assertEquals(inTime.getTime() / 1000L * 1000L, ticket.getInTime().getTime());
        assertEquals(outTime.getTime() / 1000L * 1000L, ticket.getOutTime().getTime());
        assertTrue(ticket.getPaid());
    }

    @Test
    public void ticketSetNullTimeTest() {
        Date inTime = null;
        Date outTime = null;
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        assertNull(ticket.getInTime());
        assertNull(ticket.getOutTime());
    }

    @Test
    public void ParkingSpotSetTest() {
        parkingSpot = new ParkingSpot(0, null, true);
        parkingSpot.setAvailable(false);
        parkingSpot.setId(10);
        parkingSpot.setParkingType(ParkingType.CAR);
        assertFalse(parkingSpot.isAvailable());
        assertEquals(10, parkingSpot.getId());
        assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
    }

}
