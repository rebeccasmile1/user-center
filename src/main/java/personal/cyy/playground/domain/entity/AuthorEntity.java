package personal.cyy.playground.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorEntity {
    private Integer id;

    private String name;

    private String type;

    private Integer age;

}
