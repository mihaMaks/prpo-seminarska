package si.fri.prpo.seminarska.dtos;

import si.fri.prpo.seminarska.entitete.Member;

import java.util.List;


public class PaginatedResponse<T> {
    private List<T> members;
    private int page;
    private int size;
    private long totalCount;
    private int totalPages;

    public PaginatedResponse(List<T> members, int page, int size, long totalCount, int totalPages) {
        this.members = members;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
    }

    // Getters and setters
    public List<T> getMembers() {
        return members;
    }

    public void setMembers(List<T> members) {
        this.members = members;
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

