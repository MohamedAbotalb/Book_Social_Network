package com.mabotalb.book_network_api.feedback;

import com.mabotalb.book_network_api.book.Book;
import org.springframework.stereotype.Service;

@Service
public class FeedbackMapper {

    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .rate(request.getRate())
                .comment(request.getComment())
                .book(Book.builder()
                        .id(request.getBookId())
                        .build()
                )
                .build();
    }
}
