/*
    The book project lets a user keep track of different books they've read, are currently reading or would like to read
    Copyright (C) 2020  Karan Kumar

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.karankumar.bookproject.ui.shelf;

import com.karankumar.bookproject.backend.model.Book;
import com.karankumar.bookproject.backend.model.PredefinedShelf;
import com.karankumar.bookproject.backend.service.BookService;
import com.karankumar.bookproject.backend.service.PredefinedShelfService;
import com.karankumar.bookproject.ui.MainView;
import com.karankumar.bookproject.ui.book.BookForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains a {@code BookForm} and a Grid containing a list of books in a given {@code Shelf}
 */

@Route(value = "", layout = MainView.class)
@RouteAlias(value = "myBooks", layout = MainView.class)
@PageTitle("My Books | Book Project")
public class BooksInShelfView extends VerticalLayout {

//    private final BookForm bookForm;
    private final BookForm bookForm;

    private final BookService bookService;
    private final PredefinedShelfService shelfService;

    private final Grid<Book> bookGrid = new Grid<>(Book.class);
    private final TextField filterByTitle;
    private final ComboBox<PredefinedShelf.ShelfName> whichShelf;
    private PredefinedShelf.ShelfName chosenShelf;
    private String bookTitle; // the book to filter by (if specified)

    private static final Logger LOGGER = Logger.getLogger(BooksInShelfView.class.getName());

    public BooksInShelfView(BookService bookService, PredefinedShelfService shelfService) {
        this.bookService = bookService;
        this.shelfService = shelfService;

        whichShelf = new ComboBox<>();
        configureChosenShelf();

        filterByTitle = new TextField();
        configureFilter();

        bookForm = new BookForm(shelfService);

        Button addBook = new Button("Add book");
        addBook.addClickListener(e -> {
            bookForm.addBook();
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout(whichShelf, filterByTitle, addBook);
        horizontalLayout.setAlignItems(Alignment.END);

        configureBookGrid();
        add(horizontalLayout, bookGrid);

        add(bookForm);

        bookForm.addListener(BookForm.SaveEvent.class, this::saveBook);
        bookForm.addListener(BookForm.DeleteEvent.class, this::deleteBook);

        bookGrid
                .asSingleSelect()
                .addValueChangeListener(
                        event -> {
                            if (event == null) {
                                LOGGER.log(Level.FINE, "Event is null");
                            } else if (event.getValue() == null) {
                                LOGGER.log(Level.FINE, "Event value is null");
                            } else {
                                editBook(event.getValue());
                            }
                        });
    }

    private void configureChosenShelf() {
        whichShelf.setPlaceholder("Select shelf");
        whichShelf.setItems(PredefinedShelf.ShelfName.values());
        whichShelf.setRequired(true);
        whichShelf.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                LOGGER.log(Level.FINE, "No choice selected");
            } else {
                chosenShelf = event.getValue();
                updateList();
                showOrHideGridColumns(chosenShelf);
            }
        });
    }

    private void showOrHideGridColumns(PredefinedShelf.ShelfName shelfName) {
        switch (shelfName) {
            case TO_READ:
                toggleColumn("rating", false);
                toggleColumn("dateStartedReading", false);
                toggleColumn("dateFinishedReading", false);
                break;
            case READING:
            case DNF:
                toggleColumn("rating", false);
                toggleColumn("dateStartedReading", true);
                toggleColumn("dateFinishedReading", false);
                break;
            case READ:
                toggleColumn("rating", true);
                break;

        }
    }

    private void toggleColumn(String columnKey, boolean isOn) {
        bookGrid.getColumnByKey(columnKey).setVisible(isOn);
    }

    private void configureBookGrid() {
        addClassName("book-grid");
        bookGrid.setColumns("title", "author", "genre", "dateStartedReading", "dateFinishedReading", "rating",
                "numberOfPages");
    }

    private void updateList() {
        if (chosenShelf == null) {
            LOGGER.log(Level.FINEST, "Chosen shelf is null");
            return;
        }

        // Find the shelf that matches the chosen shelf's name
        List<PredefinedShelf> matchingShelves = shelfService.findAll(chosenShelf);

        if (!matchingShelves.isEmpty()) {
            if (bookTitle != null && !bookTitle.isEmpty()) {
                LOGGER.log(Level.INFO, "Searching for the filter " + bookTitle);
                bookGrid.setItems(bookService.findAll(bookTitle));
            } else if (matchingShelves.size() == 1) {
                LOGGER.log(Level.INFO, "Found 1 shelf: " + matchingShelves.get(0));
                PredefinedShelf selectedShelf = matchingShelves.get(0);
                bookGrid.setItems(selectedShelf.getBooks());
            } else {
                LOGGER.log(Level.SEVERE, matchingShelves.size() + " matching shelves found for " + chosenShelf);
            }
        } else {
            LOGGER.log(Level.SEVERE, "No matching shelves found for " + chosenShelf);
        }
    }

    private void configureFilter() {
        filterByTitle.setPlaceholder("Filter by book title");
        filterByTitle.setClearButtonVisible(true);
        filterByTitle.setValueChangeMode(ValueChangeMode.LAZY);
        filterByTitle.addValueChangeListener(event -> {
            if (event.getValue() != null && !event.getValue().isEmpty()) {
                bookTitle = event.getValue();
            }
            updateList();
        });
    }

    private void editBook(Book book) {
        if (book != null && bookForm != null) {
            bookForm.setBook(book);
            bookForm.openForm();
        }
    }

    private void deleteBook(BookForm.DeleteEvent event) {
        LOGGER.log(Level.INFO, "Deleting book...");
        bookService.delete(event.getBook());
        updateList();
    }

    private void saveBook(BookForm.SaveEvent event) {
        LOGGER.log(Level.INFO, "Saving book...");
        if (event.getBook() == null) {
            LOGGER.log(Level.SEVERE, "Retrieved book from event is null");
        } else {
            LOGGER.log(Level.INFO, "Book is not null");
            bookService.save(event.getBook());
            updateList();
        }
    }
}
