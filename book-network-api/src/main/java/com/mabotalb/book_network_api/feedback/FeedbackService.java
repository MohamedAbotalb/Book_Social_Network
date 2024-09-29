package com.mabotalb.book_network_api.feedback;

import com.mabotalb.book_network_api.book.Book;
import com.mabotalb.book_network_api.book.BookRepository;
import com.mabotalb.book_network_api.exception.OperationNotPermittedException;
import com.mabotalb.book_network_api.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;

    public Long save(FeedbackRequest request, Authentication connectedUser) {
        Book book = this.bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + request.getBookId()));
        if (book.isArchived() || !book.isSharable()) {
            throw new OperationNotPermittedException("You cannot give a feedback for and archived or not shareable book");
        }
        User user = (User) connectedUser.getPrincipal();

        // Check if the user is the owner of this book
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give a feedback to your own book");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);
        return this.feedbackRepository.save(feedback).getId();
    }
}
