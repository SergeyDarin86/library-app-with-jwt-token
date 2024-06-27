package ru.library.springcourse.util;

import ru.library.springcourse.dto.PersonDTO;

import java.util.List;

public class PersonResponse {
    private List<PersonDTO> personDTOList;

    public PersonResponse(List<PersonDTO> personDTOList) {
        this.personDTOList = personDTOList;
    }

    public List<PersonDTO> getPersonDTOList() {
        return personDTOList;
    }

    public void setPersonDTOList(List<PersonDTO> personDTOList) {
        this.personDTOList = personDTOList;
    }
}
