package com.mabotalb.book_network_api.book;

import com.mabotalb.book_network_api.file.FileUtils;
import com.mabotalb.book_network_api.history.BookTransactionHistory;
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

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .owner(book.getOwner().fullName())
                .cover(FileUtils.readFileLocation(book.getBookCover()))
                .rate(book.getRate())
                .archived(book.isArchived())
                .sharable(book.isSharable())
                .build();
    }
}
