package com.miro.widgets.domain.dto;

import lombok.Getter;

@Getter
public class Pagination {

    private static final Integer MIN_PAGE = 1;
    private static final Integer MIN_COUNT = 1;
    private static final Integer MAX_COUNT = 500;

    private Integer page;
    private Integer count;

    public Pagination(Integer page, Integer count) {
        this.page = page;
        this.count = count;
        sanitizeValues();
    }

    private void sanitizeValues() {
        if (this.page < MIN_PAGE) {
            this.page = MIN_PAGE;
        }
        if (this.count < MIN_COUNT) {
            this.count = MIN_COUNT;
        }
        if (this.count > MAX_COUNT) {
            this.count = MAX_COUNT;
        }
        this.page--;
    }

    public Integer skip() {
        return page * count;
    }
}
