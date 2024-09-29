package com.mabotalb.book_network_api.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@Builder
public class BookRequest {

    private Long id;

    @NotNull(message = "Book title is required")
    @NotEmpty(message = "Book title is required")
    private String title;

    @NotNull(message = "Book author name is required")
    @NotEmpty(message = "Book author name is required")
    private String authorName;

    @NotNull(message = "Book isbn is required")
    @NotEmpty(message = "Book isbn is required")
    private String isbn;

    @NotNull(message = "Book synopsis is required")
    @NotEmpty(message = "Book synopsis is required")
    private String synopsis;

    @NotNull(message = "Book shareable is required")
    @NotEmpty(message = "Book shareable is required")
    private boolean sharable;
}
