 package hellojpa;

import javax.persistence.*;
import java.util.Date;

 @Entity
// 만일 데이터베이스에 테이블 이름이 이 클레스 이름과 다르다면
// @Table(naem = "USER") 형식으로 작성해주면된다.
public class Member {

    @Id
    private Long id; // pk 값

    // DB의 속성 이름이 username이 아닐 때,
    // @Column(name ="name") 형식으로 작성해주면 된다.
     @Column(name ="name", nullable = false)
    private String username;
    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

     @Temporal(TemporalType.TIMESTAMP)
     private Date lastModifiedDate;

     @Lob
     private String description;

     public Long getId() {
         return id;
     }

     public void setId(Long id) {
         this.id = id;
     }

     public String getUsername() {
         return username;
     }

     public void setUsername(String username) {
         this.username = username;
     }

     public Integer getAge() {
         return age;
     }

     public void setAge(Integer age) {
         this.age = age;
     }

     public RoleType getRoleType() {
         return roleType;
     }

     public void setRoleType(RoleType roleType) {
         this.roleType = roleType;
     }

     public Date getCreateDate() {
         return createDate;
     }

     public void setCreateDate(Date createDate) {
         this.createDate = createDate;
     }

     public Date getLastModifiedDate() {
         return lastModifiedDate;
     }

     public void setLastModifiedDate(Date lastModifiedDate) {
         this.lastModifiedDate = lastModifiedDate;
     }

     public String getDescription() {
         return description;
     }

     public void setDescription(String description) {
         this.description = description;
     }

     public Member(){

     }
 }