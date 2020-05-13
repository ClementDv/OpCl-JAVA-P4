package com.parkit.parkingsystem.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Date;

@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Test version")
public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private Date inTime;
    private Date outTime;
    private boolean paid;

    public static final long ONE_SECONDS_MILLIS = 1000;

    public int getId() {
        return id;
    }

    public void setId(final int ticketId) {
        this.id = ticketId;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(final ParkingSpot ticketParkingSpot) {
        this.parkingSpot = ticketParkingSpot;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(final String ticketVehicleRegNumber) {
        this.vehicleRegNumber = ticketVehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double ticketPrice) {
        this.price = ticketPrice;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(final Date ticketInTime) {
        this.inTime = ticketInTime == null ? null
                : new Date(ticketInTime.getTime()
                / ONE_SECONDS_MILLIS * ONE_SECONDS_MILLIS);
    }

    public Date getOutTime() {
        return outTime;
    }

    public void setOutTime(final Date ticketOutTime) {
        this.outTime = ticketOutTime == null ? null
                : new Date(ticketOutTime.getTime()
                / ONE_SECONDS_MILLIS * ONE_SECONDS_MILLIS);
    }

    public void setPaid(final boolean ticketPaid) {
        this.paid = ticketPaid;
    }

    public boolean getPaid() {
        return this.paid;
    }
}
