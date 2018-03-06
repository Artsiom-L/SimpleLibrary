package edu.itstep.library.dto;

import java.math.BigDecimal;
import java.util.Date;

public class BookFilterDto {
    private String title;
    private String description;
    private BigDecimal minYear = BigDecimal.ZERO;
    private BigDecimal maxYear = BigDecimal.valueOf(3_000);
    private Date startDate;
    private Date endDate;
    private String sortBy = "modified";
    private boolean onlyWithImages = false;
    private boolean strict = true;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMinYear() {
        return minYear;
    }

    public void setMinYear(BigDecimal minYear) {
        this.minYear = minYear;
    }

    public BigDecimal getMaxYear() {
        return maxYear;
    }

    public void setMaxYear(BigDecimal maxYear) {
        this.maxYear = maxYear;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isOnlyWithImages() {
        return onlyWithImages;
    }

    public void setOnlyWithImages(boolean onlyWithImages) {
        this.onlyWithImages = onlyWithImages;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }
}
