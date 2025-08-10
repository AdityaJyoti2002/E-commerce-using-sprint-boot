package com.inventory.backend.embeddable;

import java.io.Serializable;
import java.util.Objects;

public class SalesReportCompositeKey implements Serializable {

    private Integer day;
    private Integer month;
    private Integer year;

    public SalesReportCompositeKey() {
    }

    public SalesReportCompositeKey(Integer day, Integer month, Integer year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SalesReportCompositeKey)) return false;
        SalesReportCompositeKey that = (SalesReportCompositeKey) obj;
        return Objects.equals(day, that.day) &&
               Objects.equals(month, that.month) &&
               Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month, year);
    }

    @Override
    public String toString() {
        return "SalesReportCompositeKey{" +
                "day=" + day +
                ", month=" + month +
                ", year=" + year +
                '}';
    }
}
