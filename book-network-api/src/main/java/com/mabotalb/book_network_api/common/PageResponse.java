package com.mabotalb.book_network_api.common;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> content;

    private int number;

    private int size;

    private long totalElements;

    private int totalPages;

    private boolean first;

    private boolean last;
}
