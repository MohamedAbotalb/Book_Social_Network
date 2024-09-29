package com.mabotalb.book_network_api.feedback;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedbackRequest {

    @Positive(message = "Rate value should be positive")
    @Min(value = 0, message = "Rate value should be at least 0")
    @Max(value = 5, message = "Rate value should be at most 5")
    private Double rate;

    @NotEmpty(message = "Comment is required")
    @NotBlank(message = "Comment is required")
    @NotNull(message = "Comment is required")
    private String comment;

    @NotNull(message = "Book is required")
    private Long bookId;
}
