package com.mabotalb.book_network_api.book;

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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "Create a new book", description = "Add a new book to the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BookResponse> saveBook(
            @Valid @RequestBody BookRequest request, Authentication connectedUser
    ) {
        return ResponseEntity.ok(this.bookService.save(request, connectedUser));
    }

    @GetMapping("/{book-id}")
    @Operation(summary = "Find a book by ID", description = "Retrieve book details by its ID")
    @Parameter(description = "The ID of the book to find", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BookResponse> findBook(@PathVariable("book-id") Long id) {
        return ResponseEntity.ok(this.bookService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Find all books", description = "Retrieve a paginated list of all books")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(this.bookService.findAllBooks(page, size, connectedUser));
    }

    @GetMapping("/owner")
    @Operation(summary = "Find all books for a specific owner", description = "Retrieve a list of books for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(this.bookService.findAllBooksByOwner(page, size, connectedUser));
    }

    @GetMapping("/borrowed")
    @Operation(summary = "Find all borrowed books", description = "Retrieve a list of borrowed books")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Borrowed books retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(this.bookService.findAllBorrowedBooks(page, size, connectedUser));
    }

    @GetMapping("/returned")
    @Operation(summary = "Find all returned books", description = "Retrieve a list of returned books")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returned books retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(this.bookService.findAllReturnedBooks(page, size, connectedUser));
    }

    @PatchMapping("/shareable/{book-id}")
    @Operation(summary = "Update shareable status", description = "Update the shareable status of a book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book shareable status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BookResponse> updateSharableStatus(
            @PathVariable("book-id") Long bookId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(this.bookService.updateSharableStatus(bookId, connectedUser));
    }

    @PatchMapping("/archived/{book-id}")
    @Operation(summary = "Update archived status", description = "Update the archived status of a book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book archived status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BookResponse> updateArchivedStatus(
            @PathVariable("book-id") Long bookId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(this.bookService.updateArchivedStatus(bookId, connectedUser));
    }

    @PostMapping("/borrow/{book-id}")
    @Operation(summary = "Borrow a book", description = "Borrow a book by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book borrowed successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> borrowBook(
            @PathVariable("book-id") Long bookId,
            Authentication connectedUser
    ) {
        this.bookService.borrowBook(bookId, connectedUser);
        return ResponseEntity.ok("Book borrowed successfully");
    }

    @PatchMapping("/borrow/return/{book-id}")
    @Operation(summary = "Return a borrowed book", description = "Return a borrowed book by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book returned successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> returnBorrowedBook(
            @PathVariable("book-id") Long bookId,
            Authentication connectedUser
    ) {
        this.bookService.returnBorrowedBook(bookId, connectedUser);
        return ResponseEntity.ok("Book returned successfully");
    }

    @PatchMapping("/borrow/return/approve/{book-id}")
    @Operation(summary = "Approve a returned book", description = "Approve the return of a borrowed book by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Return approved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> approveReturnBorrowedBook(
            @PathVariable("book-id") Long bookId,
            Authentication connectedUser
    ) {
        this.bookService.approveReturnBorrowedBook(bookId, connectedUser);
        return ResponseEntity.ok("Book return approved successfully");
    }

    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    @Operation(summary = "Upload book cover", description = "Upload a book cover picture")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Book cover uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> uploadBookCoverPicture(
            @PathVariable("book-id") Long bookId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
    ) {
        this.bookService.uploadBookCoverPicture(file, connectedUser, bookId);
        return ResponseEntity.accepted().build();
    }
}
