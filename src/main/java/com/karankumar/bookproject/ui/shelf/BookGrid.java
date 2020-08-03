package com.karankumar.bookproject.ui.shelf;

import com.karankumar.bookproject.backend.entity.Book;
<<<<<<< HEAD
<<<<<<< HEAD
import com.karankumar.bookproject.backend.entity.PredefinedShelf;
import com.karankumar.bookproject.backend.service.PredefinedShelfService;
import com.karankumar.bookproject.ui.shelf.listener.BookGridListener;
=======
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
>>>>>>> 90f05f5... Moved configureBookGrid to BookGrid
=======
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
>>>>>>> 90f05f5... Moved configureBookGrid to BookGrid
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import lombok.extern.java.Log;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
<<<<<<< HEAD
<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> 90f05f5... Moved configureBookGrid to BookGrid
=======
>>>>>>> 90f05f5... Moved configureBookGrid to BookGrid
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.karankumar.bookproject.ui.shelf.BooksInShelfView.*;

import static com.karankumar.bookproject.ui.shelf.BooksInShelfView.*;

import static com.karankumar.bookproject.ui.shelf.BooksInShelfView.*;


@Log
public class BookGrid {
    private final Grid<Book> bookGrid;

    BookGrid(Grid<Book> bookGrid) {
        this.bookGrid = bookGrid;
    }

<<<<<<< HEAD
<<<<<<< HEAD
    public void configure(BookGridListener listener) {
=======
    public void configure(HasValue.ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<Grid<Book>, Book>> listener) {
>>>>>>> 90f05f5... Moved configureBookGrid to BookGrid
=======
    public void configure(HasValue.ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<Grid<Book>, Book>> listener) {
>>>>>>> 90f05f5... Moved configureBookGrid to BookGrid
        addListener(listener);

        resetGridColumns();

        addTitleColumn();
        addAuthorColumn();
        bookGrid.addColumn(GENRE_KEY);
        addDateStartedColumn();
        addDateFinishedColumn();
        bookGrid.addColumn(RATING_KEY);
        bookGrid.addColumn(PAGES_KEY);
        bookGrid.addColumn(PAGES_READ_KEY);
    }

<<<<<<< HEAD
<<<<<<< HEAD
    private void addListener(BookGridListener listener) {
        bookGrid.asSingleSelect().addValueChangeListener(listener.getListener());
=======
    private void addListener(HasValue.ValueChangeListener listener) {
        bookGrid.asSingleSelect().addValueChangeListener(listener);
>>>>>>> 90f05f5... Moved configureBookGrid to BookGrid
=======
    private void addListener(HasValue.ValueChangeListener listener) {
        bookGrid.asSingleSelect().addValueChangeListener(listener);
>>>>>>> 90f05f5... Moved configureBookGrid to BookGrid
    }

    private void resetGridColumns() {
        bookGrid.setColumns();
    }

    private void addDateFinishedColumn() {
        bookGrid.addColumn(new LocalDateRenderer<>(
                Book::getDateFinishedReading, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                .setHeader("Date finished reading")
                .setComparator(Comparator.comparing(Book::getDateStartedReading))
                .setSortable(true)
                .setKey(DATE_FINISHED_KEY);
    }

    private void addDateStartedColumn() {
        bookGrid.addColumn(new LocalDateRenderer<>(
                Book::getDateStartedReading, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                .setHeader("Date started reading")
                .setComparator(Comparator.comparing(Book::getDateStartedReading))
                .setKey(DATE_STARTED_KEY);
    }

    private void addAuthorColumn() {
        bookGrid.addColumn(AUTHOR_KEY)
                .setComparator(Comparator.comparing(author -> author.getAuthor().toString()))
                .setSortable(true);
    }

    private void addTitleColumn() {
        bookGrid.addColumn(this::combineTitleAndSeries) // we want to display the series only if it is bigger than 0
                .setHeader("Title")
                .setKey(TITLE_KEY)
                .setSortable(true);
    }

    private String combineTitleAndSeries(Book book) {
        if (book.existsSeriesPosition()) {
            return String.format("%s (#%d)", book.getTitle(), book.getSeriesPosition());
        }

        return book.getTitle();
    }

    public void toggleColumnVisibility(String columnKey, boolean showColumn) {
        if (bookGrid.getColumnByKey(columnKey) == null) {
            LOGGER.log(Level.SEVERE, "Key is null:" + columnKey);
        } else {
            bookGrid.getColumnByKey(columnKey).setVisible(showColumn);
        }
    }

    public Grid<Book> get() {
        return bookGrid;
    }

    void update(PredefinedShelf.ShelfName chosenShelf, PredefinedShelfService shelfService, BookFilters bookFilters) {
        if (chosenShelf == null) {
            LOGGER.log(Level.FINEST, "Chosen shelf is null");
            return;
        }

        // Find the shelf that matches the chosen shelf's name
        List<PredefinedShelf> matchingShelves = shelfService.findAll(chosenShelf);

        if (matchingShelves.isEmpty()) {
            LOGGER.log(Level.SEVERE, "No matching shelves found for " + chosenShelf);
            return;
        }

        if (matchingShelves.size() != 1) {
            LOGGER.log(Level.SEVERE, matchingShelves.size() + " matching shelves found for " + chosenShelf);
            return;
        }

        LOGGER.log(Level.INFO, "Found 1 shelf: " + matchingShelves.get(0));
        bookGrid.setItems(filterShelf(matchingShelves.get(0), BookFilters bookFilters));
    }

    private List<Book> filterShelf(PredefinedShelf selectedShelf, BookFilters bookFilters) {
        return selectedShelf.getBooks().stream()
                .filter(book -> bookFilters.apply(book))
                .collect(Collectors.toList());
    }
}
