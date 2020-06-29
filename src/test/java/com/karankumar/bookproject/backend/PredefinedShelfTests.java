/*
    The book project lets a user keep track of different books they've read, are currently reading or would like to read
    Copyright (C) 2020  Karan Kumar

    This program is free software: you can redistribute it and/or modify it under the terms of the
    GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
    warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program.
    If not, see <https://www.gnu.org/licenses/>.
 */

package com.karankumar.bookproject.backend;

import com.karankumar.bookproject.backend.model.PredefinedShelf;
import com.karankumar.bookproject.backend.service.BookService;
import com.karankumar.bookproject.backend.service.PredefinedShelfService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PredefinedShelfTests {

    private static PredefinedShelfService shelfService;

    @BeforeAll
    public static void setup(@Autowired PredefinedShelfService shelfService, @Autowired BookService bookService) {
        Assumptions.assumeTrue(shelfService != null && bookService != null);
        PredefinedShelfTests.shelfService = shelfService;
        bookService.deleteAll(); // reset
    }

    /**
     * A {@link com.karankumar.bookproject.backend.model.PredefinedShelf} without any books should still exist
     */
    @Test
    public void orphanShelfExists() {
        Assumptions.assumeTrue(shelfService != null);
        List<PredefinedShelf> shelves = PredefinedShelfTests.shelfService.findAll();

        Assertions.assertEquals(4, shelves.size());
        Assertions.assertEquals(shelves.get(0).getShelfName(), PredefinedShelf.ShelfName.TO_READ);
        Assertions.assertEquals(shelves.get(1).getShelfName(), PredefinedShelf.ShelfName.READING);
        Assertions.assertEquals(shelves.get(2).getShelfName(), PredefinedShelf.ShelfName.READ);
        Assertions.assertEquals(shelves.get(3).getShelfName(), PredefinedShelf.ShelfName.DID_NOT_FINISH);
    }
}
