package ru.library.springcourse.util;

public class LibraryExceptionNotAcceptable extends RuntimeException{

    public LibraryExceptionNotAcceptable(String errorMsg){
        super(errorMsg);
    }

}
