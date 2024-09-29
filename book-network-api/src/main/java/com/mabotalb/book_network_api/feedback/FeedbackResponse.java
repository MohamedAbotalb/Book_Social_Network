package com.mabotalb.book_network_api.feedback;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {

    private Double rate;

    private String comment;

    private boolean ownFeedback;
}
