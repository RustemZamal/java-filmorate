package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.FilmDirectorDao;
import ru.yandex.practicum.filmorate.model.Director;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final FilmDirectorDao filmDirectorDao;

    /**
     *
     * @param director сущность режиссер.
     * @return Возвращает созданый объект режиссер {@link FilmDirectorDao#addDirector(Director)}.
     */
    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        return filmDirectorDao.addDirector(director);
    }

    /**
     *
     * @param director сущность режиссер.
     * @return Возвращает обновленый объект режиссер.
     */
    @PutMapping
    public Director upadateDirector(@Valid @RequestBody Director director) {
        return filmDirectorDao.updateDirector(director);
    }

    /**
     *
     * @param id идентификатор режиссера
     * @return Возращает режиссера по его id.
     */
    @GetMapping("/{id}")
    public Director findDirectorById(@PathVariable Long id) {
        return filmDirectorDao.findDirectorById(id);
    }

    /**
     *
     * @return Возвращает всех режиссеров.
     */
    @GetMapping
    public List<Director> findAllDirectors() {
        return filmDirectorDao.findAllDirectors();
    }

    /**
     * Эндпоинт по удалению режиссера по его id.
     * @param id идентификатор режиссера.
     */
    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Long id) {
        filmDirectorDao.deleteDirector(id);
    }
}
