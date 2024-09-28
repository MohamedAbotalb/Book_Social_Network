package com.mabotalb.book_network_api.feedback;

import com.mabotalb.book_network_api.book.Book;
import com.mabotalb.book_network_api.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "feedbacks")
public class Feedback extends BaseEntity {

    private Double rate;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
