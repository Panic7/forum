package com.example.forum.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class DateService {

    public String actualizeSinceCreation(LocalDateTime startDateTime) {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime tempDateTime = LocalDateTime.from(startDateTime);

        long years = tempDateTime.until(endDateTime, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);

        long months = tempDateTime.until(endDateTime, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);

        long days = tempDateTime.until(endDateTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);

        long hours = tempDateTime.until(endDateTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(endDateTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        if (years > 0) {
            if (months > 0) {
                return years + " years " + months + " months ago";
            }
            return years + " years ago";

        }
        if (months > 0) {
            return months + " months ago";
        }
        if (days > 0) {
            return days + " days ago";
        }
        if (hours > 0) {
            return hours + " hours ago";
        }
        if (minutes > 0) {
            return minutes + " minutes ago";
        }
        return "just now";
    }

}
