package personal.cyy.playground.mapper;

import personal.cyy.playground.domain.entity.AuthorEntity;

public interface AuthorMapper {
    AuthorEntity selectAuthor(Integer id);
}
