package ru.library.springcourse.util;

public class LibraryException extends RuntimeException{

    public LibraryException(String errorMsg){
        super(errorMsg);
    }

}
