package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
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
                calculateCarFare(ticket, durationMinutes, durationHours, reducePercent);
                break;
            }
            case BIKE: {
                calculateBikeFare(ticket, durationMinutes, durationHours, reducePercent);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    public void calculateCarFare(Ticket ticket, long durationMinutes, long durationHours, double reducePercent) {
        double price;
        if (durationMinutes <= 30) {
            price = 0;
        } else if (durationMinutes < 60) {
            price = (durationMinutes * Fare.CAR_RATE_PER_MINUTE) * (1 - (reducePercent / 100));
        } else {
            price = (durationHours * Fare.CAR_RATE_PER_HOUR) * (1 - (reducePercent / 100));
        }
        ticket.setPrice(roundToHundred(price));
    }

    public void calculateBikeFare(Ticket ticket, long durationMinutes, long durationHours, double reducePercent) {
        double price;
        if (durationMinutes <= 30) {
            price = 0;
        } else if (durationMinutes < 60) {
            price = (durationMinutes * Fare.BIKE_RATE_PER_MINUTE) * (1 - (reducePercent / 100));
        } else {
            price = (durationHours * Fare.BIKE_RATE_PER_HOUR) * (1 - (reducePercent / 100));
        }
        ticket.setPrice(roundToHundred(price));
    }

    public static double roundToHundred(double nb) {
        BigDecimal bd = new BigDecimal(nb);
        bd = bd.setScale(2, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

}
