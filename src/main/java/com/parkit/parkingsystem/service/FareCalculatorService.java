package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket)
    {
        calculateFareReduce(ticket, 0);
    }

    public void calculateFareReduce(Ticket ticket, double reducePercent) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(new Date(ticket.getInTime().getTime())))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime());
        }
        ticket.setPaid(true);
        LocalDateTime inTime = LocalDateTime.ofInstant(ticket.getInTime().toInstant(), ZoneId.systemDefault());
        LocalDateTime outTime = LocalDateTime.ofInstant(ticket.getOutTime().toInstant(), ZoneId.systemDefault());

        //TODO: Some tests are failing here. Need to check if this logic is correct

        long durationMinutes = ChronoUnit.MINUTES.between(inTime, outTime);
        long durationHours = ChronoUnit.HOURS.between(inTime, outTime);
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                if (durationMinutes <= 30) {
                    ticket.setPrice(0);
                    break;
                }
                else if (durationMinutes <= 60) {
                    ticket.setPrice((durationMinutes * Fare.CAR_RATE_PER_MINUTE) * (1 - (reducePercent / 100)));
                    break;
                } else {
                    ticket.setPrice((durationHours * Fare.CAR_RATE_PER_HOUR) * (1 - (reducePercent / 100)));
                    break;
                }
            }
            case BIKE: {
                if (durationMinutes <= 30) {
                    ticket.setPrice(0);
                    break;
                }
                else if (durationMinutes <= 60) {
                    ticket.setPrice((durationMinutes * Fare.BIKE_RATE_PER_MINUTE) * (1 - (reducePercent / 100)));
                    break;
                } else {
                    ticket.setPrice((durationHours * Fare.BIKE_RATE_PER_HOUR) * (1 - (reducePercent / 100)));
                    break;
                }
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}
