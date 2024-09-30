package com.mabotalb.book_network_api.book;

import com.mabotalb.book_network_api.file.FileUtils;
import com.mabotalb.book_network_api.history.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public static Book toBook(BookRequest request) {
        return Book.builder()
                .title(request.getTitle())
                .authorName(request.getAuthorName())
                .isbn(request.getIsbn())
                .synopsis(request.getSynopsis())
                .shareable(request.isShareable())
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
                .shareable(book.isShareable())
                .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();
    }
}
