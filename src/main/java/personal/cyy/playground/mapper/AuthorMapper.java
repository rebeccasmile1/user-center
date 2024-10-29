package personal.cyy.playground.mapper;

import org.apache.ibatis.annotations.Param;
import personal.cyy.playground.domain.entity.AuthorEntity;

import java.util.List;

public interface AuthorMapper {
    AuthorEntity selectAuthor(Integer id);

    List<AuthorEntity> selectAllAuthors(@Param("ids") List<Integer> ids);

}
