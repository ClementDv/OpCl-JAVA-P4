package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

public class ParkingSpot {
    private int number;
    private ParkingType parkingType;
    private boolean isAvailable;

    public ParkingSpot(final int pNumber, final ParkingType pParkingType,
                       final boolean pIsAvailable) {
        this.number = pNumber;
        this.parkingType = pParkingType;
        this.isAvailable = pIsAvailable;
    }

    public int getId() {
        return number;
    }

    public void setId(final int idNumber) {
        this.number = idNumber;
    }

    public ParkingType getParkingType() {
        return parkingType;
    }

    public void setParkingType(final ParkingType setParkingType) {
        this.parkingType = setParkingType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(final boolean available) {
        isAvailable = available;
    }
}
