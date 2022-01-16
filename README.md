# JPA

### SQL 중심적인 개발의 문제점

한번 우리가 객체를 데이터에 저장을 한다고 해보자. 우리는 관계형 DB, NoSQL, 파일 등 다양한 형식으로 객체를 영구적으로 보관할 수 있다. 하지만 가장 현실적이고 현명한 방법은 관계형 DB이다. No SQL도 방법이 될 수 있지만, 아직까지는 주 메인으로 두지는 않는다. 

이제 우리가 관계형 데이터베이스를 사용하여 객체를 저장해본다고 생각해보자. 우선 흐름은 '1. 객체를 SQL문으로 변환 -> 2. SQL을 RDB(관계형 DB)에 저장' 순이다. 이를 객체를 SQL로 매핑한다고 말한다. 이러한 매핑 과정은 매우 방복적인 방법이기 때문에 다른 로직을 짤 시간에 매핑을 하며 시간을 낭비하는 문제점이 생긴다. 

**첫 번째 이유:** 무한 반복과 지루한 코드 - Java 객체와 sql 서로 전환 하기, CRUD짜기 등 매우 많은 코드를 일일히 작성해야한다. 또한 수정하기도 매우 번거롭다. 이러다 보면 코드를 빼놓는 경우처럼 실수가 생긴다.

**두 번째 이유:** 패러다임의 불일치. - 객체가 나온 사상과 관계형 데이터 베이스가 나온 이유(사상)이 다르다. 관계형 DB는 정교화해서 보관을 하는 것이 목적이지만, 객체는 필드와 메서드를 잘 캡슐화해서 사용하는 것이 문제이다. 하지만 이를 서로 융합하려다보니 패러다임이 일치하지 않아 문제가 생긴다. 어떤 문제가 있을까?

**객체와 관계형 데이터베이스의 차이**: 

1. **상속**: 객체에는 상속 관계가 있다. 하지만 DB는 상속 관계와 유사한 개념이 있지만 엄연히 다른 개념이기 때문에 없다고 보는 것이 좋다.

   이렇게 상속 개념 간의 차이에도 불구하고 객체를 DB에 밀어 넣으려면 어떻게 해야될까? 대부분 이를 포기해버린다. DB에서 객체의 상속 관계와 가장 유사한 방법은 Table 슈퍼타입 서브타입 관계이다. 

   예를 한번 들어보자.  우리는 쇼핑몰을 운영하려고 한다. 그러면 Java의 객체로 구조를 짤 떄, 우리는 'Item(상품)-id, 이름, 가격'이라는 부모 클레스를 만든 후, 이를 상속받아 'Album-작곡가', 'Movie-감독, 배'우,' Book-작가, ISBN' 등 서브 클래스를 만들 것이다. 이러한 객체 구조를 DB로 모델링해보자. 모델링을 하면서 발생하는 문제가 있다. 

   1. 반복 문제: Item ID를 모든 테이블마다 삽입해야하는 반복 문제가 생긴다. 
   2. 조회 문제: DB에서는 각각의 테이블에 따른 JOIN SQL을 작성해야하고, 각각의 객체를 생성해야한다. 하지만 자바는 get만 사용해서 손쉽게 데이터를 조회할 수 있다. 

​		=> **즉 RDB에 넣는 순간 SQL매핑 작업이 너무 복잡해지기 때문에, DB에 저장할 객체는 상속 관계를 사용하지 않는다.**

2. **연관관계:** 객체는 참조(레퍼런스)값을 가지고 객체를 가져온다. 즉 아래 그림과 같이 memer.getTeam() 을 사용하여 간편하게 데이터 조회를 할 수 있다. 하지만 DB는 다른 테이블의 FK를  Join을 하여 필요한 데이터를 찾을 수 있다. 이는 무엇이 문제일까? 

   자바의 객체는 일방향성을 가지고 있기 때문에 Member에서 Team의 데이터를 조회할 수 있지만, Team에서는 Member를 조회할 수 없다. 반면, DB는 양방향성을 가지고 있기 때문에 서로 간의 조회가 가능하다. 

   이러한 특징을 반영하기 위해 우리는 객체를 모델링할 때 Member에 Team 필드를 가져와 참조로 연관관계를 맺는다. 이를 INSERT INTO MEMBER에서 TEAM_ID에 값을 넣을 때 우리는 또 문제가 발생한다. 우리가 필드에 저장한 TEAM의 값은 참조 값이지 DB의 PK값이 아니기 때문이다. 이러한 문제는 member.getTema().getId()를 사용하여 어찌어찌 해서 해결할 수 있다. '

   // 사진

   하지만, 이를 조회할 때도 문제가 발생한다. 과정은 다음과 같다. sql을 실행한 후 데이터베이스에서 조회환 회원, 팀에 관련된 정보를 모두 입력한다. 그다음에 회원과 팀 간의 관계를 설정해 준 후 member를 반환해준다. 

   // 사진

   그러나 객체의 자바 컬렉션을 사용하면 간단한 코드만을 사용해도 조회가 가능하다. 즉, 위와 같이 DB와 연관 시키려는 순간 커다란 문제들이 많이 발생한다는 것이다. 

   더 나아가, 객체는 자유롭게 객체 그래프를 탐색할 수 있어야한다. member.getTeam()처럼 다른 객체를 모두 쉽게 탐색할 수 있어야 한다는 것이다. 그러나 우리는 아까 전제에서 실행한 SQL에 따라서 객체를 생성하였기 때문에, member.getTeam()은 작동하지만 아직 매핑하지 않은 다른 객체는 조회할 수 없다.  이는 **엔티티의 신뢰 문제**가 발생한다. 우리가 getTeam() 메서드도 있고, getOrder 메서드도 있지만, 이 반환된 객체가 실제로 매핑이 되었는지 모르기 때문에 직접 다 까보지 않는 이상 객체(엔티티)에 대한 실뢰할 수 없어 메소드를 생각없이 사용할 수 없다. 

   **따라서 계층형 아키테처가 필요한 경우에 진정한 의미의 계층 분할이 어렵다. 객체답게 모델링 할수록 매핑 작업만 늘어나느 것이다. **



## JPA를 사용하는 이유

그럼 **객체를 자바 컬렉션에 저장 하듯이 DB에 저장할 수는 없을까?** 라는 물음이 나올 것이다.

**JAP란?**

JAP는 Java Persistence API의 준말이다. 이는 자바 진영의 ORM 기술 표준이다. ORM은 Object-relational mapping(개게 관계 매핑)으로 객체는 객체데로 설계를 하고, 관계형 DB는 관계형 DB대로 설계를 하면 ORM 프래임워크가 그 중간에서 서로 간의 매핑을 해준다. 대중적인 언어에는 대부분 ORM기술이 존재한다. TypeScript도 TypeORM으로 제공을 해준다. 

JPA는 Java 애플리케이션과 JDBC API 사이에서 동작을 한다. 하지만 개발자는 JDBC를 직접 사용하지는 않고 JPA를 사용하여 JDBC API에 접근하여 DB와 상호작용을 한다. JDBC API는 SQL문을 DB에 제공하고 DB는 결과를 JDBC API에 반환해준다. 

**JPA 동작 방식**

1. **저장:** MemberDAO(java 애플리케이션)에서 객체를 저장하고 싶어서 멤버 객체를 넘긴다. 그러면 JPA가 Entity를 분석하고, INSERT SQL을 생성하여 JDBC API를 사용한다. 하지만 중요한 것은 패러다임의 불일치를 해결한다는 점이 가장 큰 목적이자 장점이다.

2. **조회:** java 애플리케이션에서 find(id)를 요구하면 JPA는 SELECT SQL를 생성하여 JDBC API를 통하여 SQL을 DB에 보내고 결과를 받아 ResultSet을 매핑하여 반환해준다. 

**JPA를 사용하는 이유**

1. **생산성:** JPA로 CRUD를 하는 것은 코드 한줄로 할 수 있을 정도로 매우 간단하다.

2. **유지보수:** 기존의 필드를 변경해도 모든 SQL를 수정할 필요가 없다. 필드만 추가하면 sql은 jpa가 알아서 처리해준다.

3. **패러다임의 불일치 해결:** JPA가 상속처리를 기가막히게 한다.  신뢰할 수 있는 엔티티, 계층. - 자유로운 객체 탐색이 가능해진다. 또한 동일한 트랜젝션에서 조회한 엔티티는 같음을 보장해준다.

4. **성능:** JPA의 성능 최적화 기능이 있다. 

   1. 1차 개시와 동일성 보장:

      - 같은 트랜젝션 안에서는 같은 엔티티를 반환한다 - 약간의 조회 성능 향상
      - DB Isolation Level이 Read Commit이어도 애플리케이션에서 Repeatable Read를 보장한다.

   2. 트랜잭션을 지원하는 쓰기

      - 트랜잭션을 커밋할 때가지 INSERT SQL을 모은다
      - 그후 JDBC BAT SQL 기능을 사용해서 한번에 SQL에 전송한다. 

      따라서 여러번의 트랜직션이 발생하지 않고 1번만 발생하여 성능이 좋다.

   3. 즉시 로딩과 지연 로딩

      - 지연 로딩: 객체가 실제 사용될 때 로딩된다. 
      - 즉시 로딩: JOIN SQL로 한번에 연관된 객체까지 미리 조회한다. 예를 들어 멤버를 조회할 때 항상 팀도 조회해야된다면 설정을 켜서 항상 둘이 한번에 가져올 수 있도록 해준다. 

5. 데이터 접근 추상화와 벤더 독립성

6. 표준



### 데이터베이스 방언

1. **JPA는 특정 데이터베이스에 종속되어 있지 않다**. 극단적인 예로 MySQL에서 ORACLE로 DB를 바꿔도 크게 손 볼 것이 없다.
2. 기존에는 각각의 데이터베이스가 제공하는 SQL 문법과 함수는 서로 조금씩 다르다. 예를 들어,
   - 가변 문자: MySQL은 <u>VARCHAR</u> - Oracle은 <u>VARCHAR2</u>
   - 문자열을 자르는 함수: SQL 표준은 <u>SUBSTRING()</u>, Oracle은 <u>SUBSTR()</u>
   - 페이징: MySQL은 LIMIT, Oracle은 ROWNUM

​	 으로 서로 차이가 있다.

3. **방언이란:** SQL 표준을 지키지 않는 특정데이터메이스만의 고유한 기능이다. 이것은 JPA 입장에서 방언이라고 표현하는 것이다. 현재 H2 데이터 베이스를 쓰고 있다. 이는 SQL 표준 문법을 사용하지 않고 H2만의 방언으로 쿼리를 작성한다면, JPA가 알아서 표준으로 번역해서 전달해 준다. 
![image](https://user-images.githubusercontent.com/63040492/149646624-8c25ae80-49a2-4e9b-b2ef-4e5c403ab297.png)


### JPA 구동 방식

![image](https://user-images.githubusercontent.com/63040492/149646657-26bb8b17-b638-4315-87f6-0d508219921b.png)



# JPA 활용1

### MemberRepository

@PsersistenceContext: entity 매니저 - spring boot가 이 어노태이션이 있다면 entity 매니저를 주입해준다.



**return type 주의하기**

```java
@PersistenceContext // entity 매니저 - spring boot가 이 어노태이션이 있다면 entity 매니저를 주입해준다.
    private EntityManager em;

    public Long save(Member member){
        em.persist(member);
        return member.getId();
    }
```

로직 설명: member라는 객체를 저장하고 멤버의 아이디를 리턴한다. 

왜 Member 자체로 리턴하지 않고 아이디 값만 리턴 했을까? 

커멘드와 쿼리를 분리하라는 원칙에 의해서이다. 저장 로직은 사이드 이펙트를 일으키는 커멘드 성격이 높기 때문에  가급적  리턴 값은 안만드는 것이 좋다. 하지만 아이디만 있다면 조회가 가능하니 아이디 값만 적는다.



Entity manage를 통한 모든 데이터 변경은 트렌젝션에서 이루어져야한다.

---

### 도메인 분석 설계

**요구사항 분석**: 매우 간단한 쇼핑몰이다.

1. 회원 가입, 목록

   |  ID  |   속성   |
   | :--: | :------: |
   |  1   |   이름   |
   |  2   |   도시   |
   |  3   |   거리   |
   |  4   | 우편번호 |

2. 상품 등록, 목록 조회, 수정, 삭제

|  ID  |  속성  |
| :--: | :----: |
|  1   | 상품명 |
|  2   |  가격  |
|  3   |  수량  |
|  4   |  저자  |
|  5   |  ISBN  |

3. 상품 주문, 내역 조회 삭제(상품 재고 량 조절 기능 필요), 검색

|  ID  |   속성    |
| :--: | :-------: |
|  1   | 주문회원  |
|  2   | 상품 선택 |
|  3   | 주문 수량 |



**기능 목록:**

- 회원 기능
  - 회원 등록 
  - 회원 조회
- 상품 기능
  - 상품 등록
  - 상품 수정
  - 상품 조회
- 주문 기능
  - 상품 주문
  - 주문 내역 조회
  - 주문 취소
- 기타 요구 사항
  - 상품은 제고 관리가 필요하다.
  - 상품의 종료는 도서, 음반, 영화가 있다.
  - 상품을 카테고리로 구분할 수 있다.
  - 상품 주문시 배송 정보를 입력할 수 있다. 



