package com.mabotalb.book_network_api.book;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BorrowedBookResponse {

    private Long id;

    private String title;

    private String authorName;

    private String isbn;

    private double rate;

    private boolean returned;

    private boolean returnApproved;
}
