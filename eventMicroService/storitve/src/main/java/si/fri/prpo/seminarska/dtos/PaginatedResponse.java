package si.fri.prpo.seminarska.dtos;

import si.fri.prpo.seminarska.entitete.Member;

import java.util.List;


public class PaginatedResponse<T> {
    private List<T> events;
    private int page;
    private int size;
    private long totalCount;
    private int totalPages;

    public PaginatedResponse(List<T> events, int page, int size, long totalCount, int totalPages) {
        this.events = events;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
    }

    // Getters and setters
    public List<T> getEvents() {
        return events;
    }

    public void setEvents(List<T> Events) {
        this.events = events;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}

