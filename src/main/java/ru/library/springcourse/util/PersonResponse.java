package ru.library.springcourse.util;

import io.swagger.annotations.ApiModel;
import ru.library.springcourse.dto.PersonDTO;

import java.util.List;

@ApiModel(description = "Объект, который возвращает список \"PersonDTO\"")
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
