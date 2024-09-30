package com.mabotalb.book_network_api.book;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {

    private Long id;

    private String title;

    private String authorName;

    private String isbn;

    private String synopsis;

    private String owner;

    private byte[] cover;

    private double rate;

    private boolean archived;

    private boolean shareable;
}
