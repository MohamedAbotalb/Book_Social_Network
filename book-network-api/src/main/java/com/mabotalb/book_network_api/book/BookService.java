package com.mabotalb.book_network_api.book;

import com.mabotalb.book_network_api.common.PageResponse;
import com.mabotalb.book_network_api.exception.OperationNotPermittedException;
import com.mabotalb.book_network_api.file.FileStorageService;
import com.mabotalb.book_network_api.history.BookTransactionHistory;
import com.mabotalb.book_network_api.history.BookTransactionHistoryRepository;
import com.mabotalb.book_network_api.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.mabotalb.book_network_api.book.BookSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("image/png", "image/jpeg", "image/jpg");

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    @CachePut(value = "books", key = "#result.id")
    public BookResponse save(BookRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        Book savedBook = this.bookRepository.save(book);
        return bookMapper.toBookResponse(savedBook);
    }

    @Cacheable(value = "book", key = "#id")
    public BookResponse findById(Long id) {
        return this.bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + id));
    }

    @Cacheable(value = "books", key = "{#page, #size}")
    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = this.bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    @Cacheable(value = "booksByOwner", key = "{#page, #size, #connectedUser.name}")
    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = this.bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    @Cacheable(value = "borrowedBooks", key = "{#page, #size, #connectedUser.name}")
    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = this.transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    @Cacheable(value = "returnedBooks", key = "{#page, #size, #connectedUser.name}")
    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allReturnedBooks = this.transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allReturnedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                allReturnedBooks.getNumber(),
                allReturnedBooks.getSize(),
                allReturnedBooks.getTotalElements(),
                allReturnedBooks.getTotalPages(),
                allReturnedBooks.isFirst(),
                allReturnedBooks.isLast()
        );
    }

    @CacheEvict(value = "books", key = "#bookId")
    public Long updateSharableStatus(Long bookId, Authentication connectedUser) {
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books sharable status");
        }
        book.setShareable(!book.isShareable());
        this.bookRepository.save(book);
        return bookId;
    }

    @CacheEvict(value = "books", key = "#bookId")
    public Long updateArchivedStatus(Long bookId, Authentication connectedUser) {
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
        User user = (User) connectedUser.getPrincipal();

        // Check if the user isn't the owner of this book
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books archived status");
        }
        book.setArchived(!book.isArchived());
        this.bookRepository.save(book);
        return bookId;
    }

    @CacheEvict(value = "books", key = "#bookId")
    public Long borrowBook(Long bookId, Authentication connectedUser) {
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("This requested book cannot be borrowed since it is archived or not sharable");
        }
        User user = (User) connectedUser.getPrincipal();

        // Check if the user is the owner of this book
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        final boolean isAlreadyBorrowed = this.transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("This requested book is already borrowed");
        }
        BookTransactionHistory transactionHistory = BookTransactionHistory.builder()
                .book(book)
                .user(user)
                .returned(false)
                .returnApproved(false)
                .build();
        return this.transactionHistoryRepository.save(transactionHistory).getId();
    }

    @CacheEvict(value = "books", key = "#bookId")
    public Long returnBorrowedBook(Long bookId, Authentication connectedUser) {
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("This requested book cannot be returned since it is archived or not sharable");
        }
        User user = (User) connectedUser.getPrincipal();

        // Check if the user is the owner of this book
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }
        BookTransactionHistory transactionHistory = this.transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You didn't borrow this book"));

        return this.transactionHistoryRepository.save(transactionHistory).getId();
    }

    @CacheEvict(value = "books", key = "#bookId")
    public Long approveReturnBorrowedBook(Long bookId, Authentication connectedUser) {
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("This requested book cannot be returned since it is archived or not sharable");
        }
        User user = (User) connectedUser.getPrincipal();

        // Check if the user is the owner of this book
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot approve the return of a book you do not own");
        }
        BookTransactionHistory transactionHistory = this.transactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return"));
        transactionHistory.setReturnApproved(true);
        return this.transactionHistoryRepository.save(transactionHistory).getId();
    }

    @CacheEvict(value = "books", key = "#bookId")
    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Long bookId) {
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
        User user = (User) connectedUser.getPrincipal();

        // Check if the file is valid image type
        if (!ALLOWED_FILE_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG and PNG images are allowed.");
        }

        // Check if the user is the owner of this book
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot upload a cover picture for another book");
        }
        var bookCover = this.fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);
        this.bookRepository.save(book);
    }
}
