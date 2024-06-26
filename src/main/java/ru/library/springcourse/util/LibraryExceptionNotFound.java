package ru.library.springcourse.util;

public class LibraryExceptionNotFound extends RuntimeException{
    public LibraryExceptionNotFound(String errorMsg){
        super(errorMsg);
    }
}
