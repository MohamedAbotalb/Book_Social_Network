package com.mabotalb.book_network_api.book;

import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public static Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.getId())
                .title(request.getTitle())
                .authorName(request.getAuthorName())
                .isbn(request.getIsbn())
                .synopsis(request.getSynopsis())
                .sharable(request.isSharable())
                .archived(false)
                .build();
    }
}
