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

    private static final int FREE_TIME = 30;
    private static final int HOUR_MINS_TIME = 60;
    private static final int DIVIDE_HUNDRED_PERCENT = 100;

    public void calculateFare(final Ticket ticket) {
        calculateFareReduce(ticket, 0);
    }

    public void calculateFareReduce(
            final Ticket ticket, final double reducePercent) {
        if ((ticket.getOutTime() == null)
                || (ticket.getOutTime().before(
                        new Date(ticket.getInTime().getTime())))) {
            throw new IllegalArgumentException(
                    "Out time provided is incorrect:" + ticket.getOutTime());
        }
        ticket.setPaid(true);
        LocalDateTime inTime = LocalDateTime.ofInstant(
                ticket.getInTime().toInstant(), ZoneId.systemDefault());
        LocalDateTime outTime = LocalDateTime.ofInstant(
                ticket.getOutTime().toInstant(), ZoneId.systemDefault());

        long durationMinutes = ChronoUnit.MINUTES.between(inTime, outTime);
        long durationHours = ChronoUnit.HOURS.between(inTime, outTime);
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                calculateCarFare(
                        ticket, durationMinutes, durationHours, reducePercent);
                break;
            }
            case BIKE: {
                calculateBikeFare(
                        ticket, durationMinutes, durationHours, reducePercent);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    public void calculateCarFare(
           final Ticket ticket, final long durationMinutes,
            final long durationHours, final double reducePercent) {
        double price;
        if (durationMinutes <= FREE_TIME) {
            price = 0;
        } else if (durationMinutes < HOUR_MINS_TIME) {
            price = (durationMinutes * Fare.CAR_RATE_PER_MINUTE)
                    * (1 - (reducePercent / DIVIDE_HUNDRED_PERCENT));
        } else {
            price = (durationHours * Fare.CAR_RATE_PER_HOUR)
                    * (1 - (reducePercent / DIVIDE_HUNDRED_PERCENT));
        }
        ticket.setPrice(roundToHundred(price));
    }

    public void calculateBikeFare(
            final Ticket ticket, final long durationMinutes,
            final long durationHours, final double reducePercent) {
        double price;
        if (durationMinutes <= FREE_TIME) {
            price = 0;
        } else if (durationMinutes < HOUR_MINS_TIME) {
            price = (durationMinutes * Fare.BIKE_RATE_PER_MINUTE)
                    * (1 - (reducePercent / DIVIDE_HUNDRED_PERCENT));
        } else {
            price = (durationHours * Fare.BIKE_RATE_PER_HOUR)
                    * (1 - (reducePercent / DIVIDE_HUNDRED_PERCENT));
        }
        ticket.setPrice(roundToHundred(price));
    }

    public static double roundToHundred(final double nb) {
        BigDecimal bd = new BigDecimal(nb);
        bd = bd.setScale(2, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }
}
