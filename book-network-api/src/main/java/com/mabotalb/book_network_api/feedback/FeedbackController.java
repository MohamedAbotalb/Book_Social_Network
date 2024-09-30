package com.mabotalb.book_network_api.feedback;

import com.mabotalb.book_network_api.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @Operation(summary = "Create a new feedback", description = "Add feedback for a specific book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedback created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid feedback data provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> saveFeedback(
            @Valid @RequestBody FeedbackRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(this.feedbackService.save(request, connectedUser));
    }

    @GetMapping("/book/{book-id}")
    @Operation(summary = "Find all feedbacks for a specific book", description = "Retrieve a paginated list of feedbacks for a given book ID")
    @Parameter(description = "The ID of the book to find feedbacks for", name = "book-id", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedbacks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByBook(
            @PathVariable("book-id") Long bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(this.feedbackService.findAllFeedbacksByBook(bookId, page, size, connectedUser));
    }
}
