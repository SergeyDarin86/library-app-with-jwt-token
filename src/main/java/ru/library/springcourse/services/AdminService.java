package ru.library.springcourse.services;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    // в случае более сложной логики можно ставить дополнительные условия
    // для доступа к различным страницам(and/or)
//    @PreAuthorize("hasRole('ROLE_ADMIN') and hasRole('ROLE_USER')")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void doAdmin(){
        System.out.println("Только администратор может заходить на эту страницу");
    }

}
