package ru.library.springcourse.util;

import io.swagger.annotations.ApiModel;
import ru.library.springcourse.dto.BookDTO;

import java.util.List;

@ApiModel(description = "Объект, который возвращает список \"BookDTO\"")
public class BookResponse {

    List<BookDTO>bookDTOList;

    public BookResponse(List<BookDTO> bookDTOList) {
        this.bookDTOList = bookDTOList;
    }

    public List<BookDTO> getBookDTOList() {
        return bookDTOList;
    }

    public void setBookDTOList(List<BookDTO> bookDTOList) {
        this.bookDTOList = bookDTOList;
    }
}
