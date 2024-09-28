package com.mabotalb.book_network_api.history;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {
}
