package personal.cyy.playground.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;
import personal.cyy.playground.common.utils.MybatisUtils;
import personal.cyy.playground.domain.entity.AuthorEntity;
import personal.cyy.playground.mapper.AuthorMapper;

@Repository
public class AuthorDAO {
    SqlSession sqlSession;

    AuthorMapper authorMapper;

    public AuthorDAO() {
        sqlSession = MybatisUtils.getSqlSession();
        authorMapper = sqlSession.getMapper(AuthorMapper.class);
    }

    public AuthorEntity getAuthor(Integer id) {
        return authorMapper.selectAuthor(id);
    }
}
