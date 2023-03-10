package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FilmDirectorDao;
import ru.yandex.practicum.filmorate.model.Director;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final FilmDirectorDao filmDirectorDao;


    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        return filmDirectorDao.addDirector(director);
    }

    @PutMapping
    public Director upadateDirector(@Valid @RequestBody Director director) {
        return filmDirectorDao.updateDirector(director);
    }

    @GetMapping("/{id}")
    public Director findDirectorById(@PathVariable Long id) {
        return filmDirectorDao.findDirectorById(id);
    }

    @GetMapping
    public List<Director> findAllDirectors() {
        return filmDirectorDao.findAllDirectors();
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Long id) {
        filmDirectorDao.deleteDirector(id);
    }
}
