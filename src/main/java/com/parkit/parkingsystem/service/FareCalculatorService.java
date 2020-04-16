package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        LocalDateTime inTime = LocalDateTime.ofInstant(ticket.getInTime().toInstant(), ZoneId.systemDefault());
        LocalDateTime outTime = LocalDateTime.ofInstant(ticket.getOutTime().toInstant(), ZoneId.systemDefault());

        //TODO: Some tests are failing here. Need to check if this logic is correct

        long durationMinutes = ChronoUnit.MINUTES.between(inTime, outTime);
        long durationHours = ChronoUnit.HOURS.between(inTime, outTime);
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                if (durationMinutes <= 60) {
                    ticket.setPrice(durationMinutes * (Fare.CAR_RATE_PER_MINUTE));
                    break;
                } else {
                    ticket.setPrice(durationHours * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
            }
            case BIKE: {
                if (durationMinutes <= 60) {
                    ticket.setPrice(durationMinutes * (Fare.BIKE_RATE_PER_MINUTE));
                    break;
                } else {
                    ticket.setPrice(durationHours * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}