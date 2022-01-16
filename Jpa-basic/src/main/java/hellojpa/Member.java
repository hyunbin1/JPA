package hellojpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
// 만일 데이터베이스에 테이블 이름이 이 클레스 이름과 다르다면
// @Table(naem = "USER") 형식으로 작성해주면된다.
public class Member {

    @Id
    private Long id; // pk 값

    // DB의 속성 이름이 name이 아닐 때,
    // @Column(name ="username") 형식으로 작성해주면 된다.
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
