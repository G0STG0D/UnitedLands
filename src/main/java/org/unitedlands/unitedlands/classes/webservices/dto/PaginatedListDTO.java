package org.unitedlands.unitedlands.classes.webservices.dto;

import java.util.LinkedHashSet;

public class PaginatedListDTO {
    public int pageSize;
    public int page;
    public String order;
    public String dir;
    public int numPages;
    public LinkedHashSet<?> elements = new LinkedHashSet<>();
    
    public PaginatedListDTO(int pageSize, int page, String order, String dir, int numPages, LinkedHashSet<?> elements) {
        this.pageSize = pageSize;
        this.page = page;
        this.order = order;
        this.dir = dir;
        this.numPages = numPages;
        this.elements = elements;
    }

}
