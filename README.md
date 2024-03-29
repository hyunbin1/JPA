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

**JPA란?**

JPA는 Java Persistence API의 준말이다. 이는 자바 진영의 ORM 기술 표준이다. ORM은 Object-relational mapping(개게 관계 매핑)으로 객체는 객체데로 설계를 하고, 관계형 DB는 관계형 DB대로 설계를 하면 ORM 프래임워크가 그 중간에서 서로 간의 매핑을 해준다. 대중적인 언어에는 대부분 ORM기술이 존재한다. TypeScript도 TypeORM으로 제공을 해준다. 

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
     으로 서로 차이가 있다.

3. **방언이란:** SQL 표준을 지키지 않는 특정데이터메이스만의 고유한 기능이다. 이것은 JPA 입장에서 방언이라고 표현하는 것이다. 현재 H2 데이터 베이스를 쓰고 있다. 이는 SQL 표준 문법을 사용하지 않고 H2만의 방언으로 쿼리를 작성한다면, JPA가 알아서 표준으로 번역해서 전달해 준다. 

![image](https://user-images.githubusercontent.com/63040492/149646624-8c25ae80-49a2-4e9b-b2ef-4e5c403ab297.png)

### JPA 구동 방식

1. Jpa는 Persistence라는 클래스에서 시작을 한다. 
2. Persistence는 가장 만저 META-INF/persistence.xml 에서 설정 정보를 조회한다.
3. 조회한 설정 정보를 기반으로 EntityManagerFactory라는 클래스를 만든다
4. 그리고 필요할 때마다 Factory(공장)에서 EntityManager이라는 것을 찍어내서 사용한다.

​	**주의!**

1.  JPA는 트렌젝션 단위로 작동시키는 것이 매우 중요하다. **데이터를 변경하는 모든 작업은 트렌젝션 안에서 실행되어야한다.** 따라서 em.getTransaction을 시작하고 끝나는 지점을 정해줘서 하나의 트렌젝션을 선언해주어야한다. 
2. EntitiyManagerFactory는 맨 처음 로딩 시점에 **딱 한번만** 만들어 놓아야한다. 

3. 엔티티 매니저는 쓰래드간 공유 X (사용하고 버려야 한다.) 따라서 DB에 저장되는 트랜젝션 단위를 할때마다 EntityManager을 만들어 주어야한다.  

​	 *트렌젝션:  ex) 고객이 들어와서 어떤 행위를 하고 나갈때마다 우리는 고객의 디비 커낵션을 얻어서 쿼리를 날리고 종료해야한다. 이렇게 어떤 행위를 할 때 디							    비를 가져오고 종료할 때까지를 한 묶음으로 일관되는 단위



![image](https://user-images.githubusercontent.com/63040492/149646657-26bb8b17-b638-4315-87f6-0d508219921b.png)

#### **객체와 테이블 생성하고 매핑하기**

**<H2>**

```sql
create table Member(
  id bigint not null,
  name varchar(255),
  primary key (id)
);MEMBER 
```

<java Class Member>;

```java
@Entity
public class Member {

    @Id
    private Long id; // pk 값
    private String name;
}
```



### **JpaMain 클래스**

1. **초기 작성 형식**

```java
// Entity 공장 생성 - 공장 생성은 한번만.
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");// 파라미터 안에는 persistense.xml에 있는 unit 이름을 넣어준다.

        // Entity Manager 객체 생성 - 트렌젝션 단위로 매번 생성.
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // 필요한 Entity Manager 코드 작성
        EntityTransaction transaction = entityManager.getTransaction(); // 트렌젝션 선언

        try { // 오류를 대비하기 위해 try, catch문 사용.
            transaction.begin(); // 트렌젝션 시작.
            // 코드 작성하기
            transaction.commit(); // 트렌젝션 종료 - 커밋 시점에 영속성 컨텍스트에 있는 내용이 DB에 저장된다.
        } catch(Exception e){
            transaction.rollback(); // 오류 발생시 롤백 해주기
        } finally{
            entityManager.close();
        }
```

2. 멤버 저장

```java
       try { // 오류를 대비하기 위해 try, catch문 사용.
            transaction.begin(); // 트렌젝션 시작.
            Member member = new Member();
            member.setId(3L);
            member.setName("HelloC");

            entityManager.persist(member); // 객체를 영속성 컨텍스트에 저장. 그후 디비 저장.

            transaction.commit(); // 트렌젝션 종료 - 커밋 시점에 영속성 컨텍스트에 있는 내용이 DB에 저장된다.
        } catch(Exception e){
            transaction.rollback(); // 오류 발생시 롤백 해주기
        } finally{
            entityManager.close();
        }
```

3. 멤버 조회

```java
try { // 오류를 대비하기 위해 try, catch문 사용.
            transaction.begin(); // 트렌젝션 시작.

            Member findMember = entityManager.find(Member.class, 1L); // 클레스 이름, ID값을 넣어주면 찾는다
            System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.Name = " + findMember.getName());

            transaction.commit(); // 트렌젝션 종료 - 커밋 시점에 영속성 컨텍스트에 있는 내용이 DB에 저장된다.
        } catch(Exception e){
            transaction.rollback();
        } finally{
            entityManager.close();
        }
```

4. 멤버 수정

```java
try { // 오류를 대비하기 위해 try, catch문 사용.
            transaction.begin(); // 트렌젝션 시작.

            Member findMember = entityManager.find(Member.class, 1L); // 클레스 이름, ID값을 넣어주면 찾는다
            findMember.setName("HelloJPA"); // 변경된 것이 있으면 jPA가 업데이트 쿼리를 작성해서 알아서 처리한다.

    transaction.commit(); // 트렌젝션 종료 - 커밋 시점에 영속성 컨텍스트에 있는 내용이 DB에 저장된다.
        } catch(Exception e){
            transaction.rollback();
        } finally{
            entityManager.close();
        }
```



5. 멤버 삭제

```java
try { // 오류를 대비하기 위해 try, catch문 사용.
    transaction.begin(); // 트렌젝션 시작.
    entityManager.remove(findMember);
	
    transaction.commit(); // 트렌젝션 종료 - 커밋 시점에 영속성 컨텍스트에 있는 내용이 DB에 저장된다.
    } catch(Exception e){
    	transaction.rollback();
	} finally{
		entityManager.close();
	}
```





## JPQL 소개

JPA에서는 SQL을 추상화한  JPQL이라는 객체 지향 쿼리 언어를 제공하여  쿼리를 쉽게 작성하게 해준다. 이는 SQL과 문법이 유사하다. SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 등을 지원한다. 

**차이점**:

1. JPQL은 엔티티 객체를 대상으로 쿼리를 작성한다.
2. SQL은 데이터베이스 테이블을 대상으로 쿼리를 작성한다. 

**JPQL을 사용하면 무엇이 메리트인가?**

1. 페이징 등 다양한 작업을 할 때 DB 방언을 알아서 맞춰서 사용해준다. 따라서 DB를 바꿔도 코드를 거의 변경할 필요가 없다.

2. 객체 지향 쿼리 작성이다.

3. 항상 문제는 검색 쿼리에서 생는데, DB가 아닌 Entity 객체를 대상으로 검색하기 때문에 문제가 발생하지 않는다.

4. 기존에는 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능하다

5. 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요한다.
   하지만 RDB의 실제 물리적인 테이블을 가져오면 종속적으로 설계가 되어 안좋다.
   따라서 JPA는 SQl을 추상화한 JPQL이라는 객체 지향 쿼리 언어를 제공하여 종속성 문제를 해결한다. 



- 가장 단순한 조회 방법
  - EntityManager.find()
  - 객체 그래프 탐색(a.getB().getC())
- 하지만 나이가 18살 이상인 회원을 모두 검색하고 싶다면? 



**실습**

JPA 입장에서는 DB 테이블을 기준으로 절때 코드를 짜지 않는다. JPA는 객체를 대상으로 코드를 짠다.

1. 조회: `List<Member> result = em.createQuery("select m from Member as m", Member.class).getResultList();`





## 영속성 관리 - JPA 내부 구조에 대해서 알아보자.

JPA를 이해하려면 **영속성 컨텍스트**라는 것을 먼저 이해해야 한다. 

**JPA에서 가장 중요한 2가지:**

- 객체와 관계형 데이터베이스 매핑하기 - 설계/정적인 부분

- **<u>영속성 컨텍스트</u>** - 실제 JPA 내부 동작 방식에 대한 부분

**데이터베이스에 접근하는 순서**

1. 엔티티 매니저 팩토리에서는 고객의 요청이 들어올 때마다 엔티티 매니저를 생성한다.

2. 만들어 진 엔티티 메이저는 데이터베이스의 커넥션 풀에 있는 커낵션을 사용해서 디비를 사용한다.

### **영속성 컨텍스트란?**

**JPA를 이해하는데 가장 중요한 용어이다.**

**의미:** "엔티티를 영구 저장하는 환경"이라는 뜻. 하지만 논리적인 개념이기 때문에 눈에 보이지 않는다. 

**코드:** EntityManager.persist(entity); // 엔티티를 영속성 컨테스트라는 곳에 저장을 한다는 의미.



**엔티티의 생명주기**

1. **비영속(new/transient):** 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태

   - 객체만 생성한 상태

   - ```java
     Member member = new Member();
     member.setId("member1");
     member.setUsername("회원1");
     ```

2. **영속(managed):** 영속성 컨텍스트에 관리되는 상태

   - 객체를 생성한 후 객체를 **<u>저장한 상태</u>(영속)**

   - ```java
     Member member = new Member();
     member.setId("member1");
     member.setUsername("회원1");
     
     EntityManager em = emf.createEntityManager();
     em.getTransaction().begin();
     
     em.persist(member); // 객체를 저잗한 상태(영속) 
     ```

   - DB저장은 트렌젝션을 commit 하는 경우에 디비에 쿼리가 날라가게된다.

3. **준영속(detached):** 영속성 컨텍스트에 저장되었다가 분리된 상태

   - ```java
     em.detach(member);
     ```

4. **삭제(removed):** 삭제된 상태

   - ```java
     em.remove(member);
     ```



이는 DB하고 어플리케이션 중간에 무언가가 하나 있는 것이다.

**영속성 컨텍스트의 이점:**

1. 1차 캐시
2. 동일성 보장
3. 트랜잭션을 지원하는 쓰기 지연
4. 변경 감지
5. 지연 로딩



## 엔티티 매핑

### 1. 객체와 테이블 매핑

**@Entiti**: @Entity가 붙은 클래스는 JPA가 관리, 엔티티라 한다. JPA를 사용해서 테이블과 매핑할 클래스는 @Entity가 필수로 작성되어야 한다.

**주의**:

1. **기본 생성자 필수**(파라미터가 없는 public 또는 protected 생성자)
2. final 클래스, enum, interface, inner 클래스는 사용할 수 없다.
3. 저장할 필드에 final을 사용하는 것도 금지!!



### 2. 데이터베이스 스키마 자동 생성 

|    옵션     |                    설명                    |
| :---------: | :----------------------------------------: |
|   create    | 기존테이블 삭제 후 다시 생성(DROP+CREATE)  |
| create-drop |  create와 같으나 종료 시점에 테이블 DROP   |
|   update    |  변경분만 반영(운영DB에는 사용하면 안됨)   |
|  validate   | 엔티티와 테이블이 정상 매핑되었는지만 확인 |
|    none     |               사용하지 않음                |

**주의**: **운영 장비에는 절대 Create, Create-drop, update를 사용하면 안된다!!!!**

- 개발 초기 단계는 create 또는 update

- 테스트 서버는 update 또는 validate

- 스테이징과 운영 서버는 validate 또는 none - 하지만 최대한 쓰지말 것


## 상속관계 매핑
**상속관계 매핑** : 객체의 상속 구조와 DB의 슈퍼타입 서브타입 관계를 매핑하는 것이다. 아래 그림과 같이 논리 모델을 보면, 물품이라는 슈퍼 타입에는 음반, 영화, 책이라는 서브타입들 간의 공통적인 속성 활용하여 매핑할 수 있다. 객체는 명확하게 상속 관계가 있지만, 관계형 데이터베이스는 상속 관계가 없다. 따라서 이처럼 슈퍼타입, 서브타입이라는 관계를 활용하여 데이터베이스 모델링 기법이 객체 상속과 최대한 유사하게 구현할 수 있도록 해주는 것이다. 다시말하지만 여기서 포인트는, 슈퍼 타입 테이블이 서브 타입 테이블의 내용 혹은 서브 타입 테이블이 슈퍼타입 테이블의 내용을 어떻게 가져올수 있느냐이다. 공통점을 슈퍼타입에 다 몰아넣고 서브 타입에 그 공통점을 사용하지 않는 것만이 방법은 아니다.  

![image](https://user-images.githubusercontent.com/63040492/232179427-42938369-0999-4766-a99c-97015090be6e.png)

### 상속관계 매핑 구현하는 3가지 방법(=논리 모델을 물리 모델로 구현하는 방법)
1. **조인 전략**: 각각 테이블로 변환 - DB JOIN을 활용하여 서브모델의 내용을 가져온다. 이는 서브 타입의 공통점을 슈퍼타입에만 저장해 놓고 사용하는, 우리가 아는 가장 흔한 방법이다. 이때 서브 테이블들을 구별하기 위해서 슈퍼타입의 속성에는 Dtype과 같은 속성을 추가해준다. 
![image](https://user-images.githubusercontent.com/63040492/232179838-fa46063b-515d-4ba1-81ad-55affdb34235.png)

```java 
@Entity
@Inheritance(strategy = InheritanceType.JOINED) // join 전략을 사용하는 법
@DiscriminatorColumn // 슈퍼 타입의 Dtype 속성을 추가해주고 서브 타입의 테이블 이름을 내용으로 추가해주는 어노테이션이다.
// 서브 타입에는 @DiscriminatorValue()를 사용해주어야 한다. 기본 값은 클레스 명이지만, 만약에 dtype 내용을 특정한 내용으로 바꾸고 싶다면 @DiscriminatorValue("A")와 같이 써주면 된다.

public abstract class Item {
    @Id @GeneratedValue
    private Long id;
    
    private String name; 
    private int price; 
    ...
```

- 조인 전략의 장점: 
	1. 테이블이 정규화 되어있고
  2. 외래키 참조 무결성 제약조건 활용이 가능하다. - 주문 아이템이 필요할때 아이템 테이블만 봐도 된다. 
  3. 저장공간의 효율화 
- 조인 전략의 단점 : 
  1. 조회시 조인을 많이 사용하게 되어 성능이 저하된다.
  2. 조회 쿼리가 복잡하다.
  3. 데이터 저장 시 Insert SQL이 2번 호출된다. 
  4. 단일 테이블에 비해서 관리하기 복잡하다는 단점이 있다. 하지만 다 모두 큰 단점이 아니기 때문에, 조인 전략을 메인으로 설계하는 것이 좋다. 

2. **단일 테이블 전략**(@inheritance(strategy=InheritanceType.SINGLE_TABLE): 통합 테이블로 변환 - 서브 타입의 모든 속성을 슈퍼타입에 모두 넣어서 테이블 하나로 사용하는 것이다. 이는 dtype으로 서브클래스를 구분한다. 단일 테이블은 @DiscriminatorColumn이 없어도 필수로 dtype이 생성된다. 
- 단일 테이블 전략의 장점: 
  1. 조인이 필요없고 select 한번만 사용하면 되므로 일반적으로 조회 성능이 빠르다. 
  2. 조회 쿼리가 단순하다. 
- 단일 테이블 전략의 단점: 
  1. **자식 엔티티가 매핑한 컬럼은 모두 null 허용해야한다는 치명적인 단점이 있다. 
    따라서 데이터 무결성 입장에서는 애매하다. 
  2. 단일 테이블에 모든 것을 저장하므로 테이블이 커질수 있어서, 상황에 따라 조회 전략보다 성능이 안좋아질 수 있다. 

3. **구현 클래스마다 테이블 전략**(@inheritance(strategy=InheritanceType.TABLE_PER_CLASS): 서브타입 테이블로 변환 - 각 서브 타입들이 공통점을 슈퍼 타입에 넣어놓는 것이 아니라, 각자 모두 다 가지도록 중복을 허용해주는 것이다.  하지만 이 전략을 서로 묶이는 것이 없어서 사용하지 않는 것이 좋다. **데이터베이스 설계자와 ORM 전문가 모두 추천하지 않는 전략이다**
- 구현 클래스 전략의 장점:
  1. 서브 타입을 명확하게 구분해서 처리할 때 효과적이다.
  2. not null 제약조건을 사용할 수 있다.
- 구현 클래스 전략의 단점: 
  1. 여러 자식 테이블을 함께 조회할 때 성능이 느리다(UNION SQL)
  2. 자식 테이블을 통합해서 쿼리하기 어렵다. 
  3. 새로운 타입이 추가가 될때(=변경이 될때) 변경하기가 매우 번거롭고 힘들다. 
  
  
  
=> 이 중 어떤 방법으로 관계매핑을 하더라도 JPA는 모두 활용할 수 있게 해준다. 

#### @MappedSuperclass
@MappedSuperclass는 객체 입장에서 name, id와 같은 속성이 많은 클래스에서 계속 나올 경우, 이 불편함을 해결하기 위해 공통 매핑 정보를 사용할 때 사용되는 어노테이션이다. 딱 공통 매핑 정보를 뿌려주기 위한 도구 정도일 뿐이다. 
- 테이블과 관계가 없고, 단순히 엔티티가 공통으로 사용하는 매핑 정보를 모으는 역할이다. 
- 주로 등록일, 수정일, 등록자, 수정자 같은 전체 엔티티에서 공통적으로 적용하는 정보를 모을 때 사용한다. 
- cf) **@Entity 클래스는 엔티티나 @MappedSuperclass로 지정한 클래스만 상속 가능하다.**
	
- 특징: 
	1. 상속관계에 매핑되지 않는다
	2. 엔티티가 아니기때문에, 테이블과 매핑되지 않는다.
	3. 부모 클래스를 상속 받는 **자식 클래스에 매핑 정보만을 제공**한다.
	4. 조회, 검색이 불가능하다. em.find(BaseEntity) 사용 불가능하다.
	5. 직접 생성해서 사용할 일이 없으므로 추상 클래스로 만들길 권장한다. 
	
	
```java
@MappedSuperclass
public abstract class BaseEntity {
	private String createBy;
	private LocalDateTime createdDate;
	private String lastModifiedBy;
	private LocalDateTime lastModifiedDate;

	public String getCreatedBy() {
		return createBy;
	}
	// 각 속성의 getter, setter 기입
	...
}
	
```
	
```java
@Entity
public class Member extends BaseEntity {
	// 이전 내용과 동일
	...
	
```
	
```java
// JpaMain.class
// 이전 내용과 동일
...
try {
	Member member = new Member();
	member.setUsername("user1");
	member.setCreatedBy("kim");
	member.setCreatedDate(localDateTime.new());
	
	em.persist(member);
	em.flush();
	em.clear();
	
	tx.commit();
}
	
// 이전 내용과 동일 
...	
```
	
이렇게 하나의 클래스에 필요한 속성을 집어넣고 각 클래스에서 사용하게 되면, 각 클래스는 추가적인 코드가 필요없이 해당 속성들을 사용하기만 하면 된다. 코딩할때 굉장히 사용하기 좋은 것 같다. 
	
![image](https://user-images.githubusercontent.com/63040492/232199243-46546bea-9062-4122-a378-55d199464c35.png)

##프록시!!
프록시를 왜 사용해야할까? 우리는 코드를 짤때 Member과 Team으로(처럼) 연관되어 있는 테이블이 있을 것이다. 하지만 우리가 Member만 출력하면 되는 비즈니스 모델일 경우가 있고, Member와 Team을 함께 사용해야되는 비즈니스 모델일 경우가 있다. Member만 출력해야될때는 그럼 낭비가 발생한다. 이 낭비를 막기 위해 프록시라는 것이 존재한다. 

#### 프록시의 기초 - em.getReference()를 사용하면 된다
- 기존의 em.find()와 em.**getReference()**를 비교해보자.
- em.find()는 데이터베이스를 통해서 실제 엔티티 객체를 조회하는 기능을 한다. 
- em.getReference()는 **데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체를 조회하는 역할을 한다. 이는 디비에 쿼리가 안나가는데도 객체가 조회가 된다. 이는 하이버네이트가 자기 내부의 객체를 사용해서 만든다. 

![image](https://user-images.githubusercontent.com/63040492/232203150-359e450e-731a-40df-b5bc-c645434acb92.png)

초기에는 위 사진 처럼 target에 텅텅 빈 껍데기만 있는 채로 생성이 되고 반환이 된다. 

**프록시의 특징**
- 프록시는 내부 하이버네이트의 라이브러리를 사용해서 실제 클래스를 자동으로 상속 받아서 만들어진다. 
- 따라서 실제 클래스와 겉 모양이 같다. 
- 사용하는 입장에서는 진짜 객체와 프록시 객체를 구분하지 않고 사용만 하면 된다(이론상)
- 프록시 객체는 실제 객체의 참조(target)를 보관하고 있다.
- 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드를 호출한다. 

**프록시 객체의 초기화**
Member member = em.getReference(Member.class, "id1");
member.getName();
위와 같은 코드가 있을 경우, 프록시는 영속성 컨텍스트에 target을 초기화 하기위해 초기화를 요청한다. 그 후 영속성 컨텍스트는 데이터베이스를 조회하여 실제 Entity를 생성한다. 그리고 target은 memberName 내용물을 가져와서 초기화 하게된다. 
![image](https://user-images.githubusercontent.com/63040492/232203936-a6f2d075-e3f0-44a9-9a24-a4e7932e9a41.png)

#### 중요한 프록시의 특징
- **프록시 객체는 처음 사용할 때 한 번만 초기화 된다!** - 두번 호출할 경우 디비를 더이상 호출하지 않고도 불러온다
- 프록시 객체를 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는 것은 아니다. 초기화되면 프록시 객체를 통해서 실제 엔티티에 접근이 가능한 것이다. 
- 프록시 객체는 원본 엔티티를 상속받는다. 따라서 타입 체크시 주의해야한다. == 비교 대신 instance of를 사용해야한다. 
- 영속성 컨텍스트를 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티를 반환한다. 
- 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 문제가 발생한다. (하이버네이트는 org.hibernate.LazyInitializationException 예외를 터트린다. 이문제는 실제로 실무에서 많이 맞딱뜨리게 된다. em.close() 혹은 em.detach()를 하고 프록시를 초기화 하려고하면 에러가 나는것이다. 


```java
public class JpaMain{
  public class void main(String[] args) {
  	...
    try {
			Member member = new Member();
			member.setUsername("Hello);
			
			em.persist(member);
			em.flush();
			em.clear();
			
			Member findMember = em.find(Member.class, member.getId());
		  System.out.println("FindMember = " + findMember.getUId());
			System.out.println("FindMember = " + findMember.getUsername());
			
			tx.commit();
			}
...
```
![image](https://user-images.githubusercontent.com/63040492/232202809-3734ce0f-119c-45f8-aa73-136276da2f26.png)

이렇게 쿼리를 보면, 조인 쿼리를 미리 해와서 member만 필요함에도 불구하고, Team 쿼리도 같이 가져온 것을 볼 수 잇다. 
하지만 em.find() 부분을 아래와 같이 고치면, select 쿼리가 안나가는 것을 볼 수 잇다. 
```java
	// Member findMember = em.find(Member.class, member.getId());
	Member findMember = em.getReference(Member.class, member.getId());
  // System.out.println("FindMember = " + findMember.getUId());
	// System.out.println("FindMember = " + findMember.getUsername());
```
이렇게 getID 등 실제로 사용하는 코드가 없으면 member, team의 쿼리가 하나도 나오지 않는 것을 볼 수 있다. 

![image](https://user-images.githubusercontent.com/63040492/232202918-2d68992d-ca47-48e3-967f-c56bb08c173a.png)

하지만 실제로 system.out.println 의 주석을 풀고 사용하게되면, 아래와 같이 쿼리가 불러와 지는 것을 알 수 있다.

![image](https://user-images.githubusercontent.com/63040492/232203015-e111e5ed-ad9a-467c-a9fe-7b42535d1448.png)

**프록시는 실제 사용되는 시점에 디비에 쿼리를 조회한다**

#### 생성된 프록시를 확인하는 방법
- 프록시 인스턴스의 초기화 여부 확인: PersistenceUil.isLoaded(Object 필요한 entity)
- 프록시 클래스 확인 방법: System.out.println(필요한 entity.getClass().getName());
- 프록시 강제 초기화: org.hibernate.Hibernate.initialize(필요한 entity);
cf) JPA 표준은 강제 초기화가 없다. 강제 호출: member.getName()

### 지연 로딩 & 즉시 로딩
- 지연 로딩: 연관관계가 매핑 되어있는 TEAM과 MEMBER 이 있을 때 TEAM이 많이 사용되지 않는다고 하면 지연로딩을 사용한다. 
Entity에서 연관관계 설정 시 fetch를 사용하면 된다. 지연로딩으로 세팅하면 프록시로 이 내용을 가져오게 되는 것이다. 로직이 실제로 사용될 때까지 기다렸다가 프록시가 디비에 접근해서 초기화를 한다. 팀을 가져올때가 아니라 팀을 사용할때다!! team.getName(); 할때 초기화가 이루어진다. 

```java
@Entity
public class Member {
	@Id
	@GenerateValue
	private Long id;
	
	@Column(name = "USERNAME")
	private String name;
	
	@ManyToOne(fetch= FetchType.LAZY) // 지연 로딩
	@JoinColumn(name "TEAM_ID")
	private TEAM team;
	}
```

- 즉시 로딩: MEMBER와 TEAM이 동시에 사용되는 경우가 많을 경우에 즉시 로딩을 사용한다. 
즉시 로딩은 EAGER를 사용해서 조회 할 수 있다. 즉시로딩은 모든 데이터를 한번에 가져오기 때문에 모든 프록시가 한번에 즉시 초기화 된다. 
```java
@Entity
public class Member {
	@Id
	@GenerateValue
	private Long id;
	
	@Column(name = "USERNAME")
	private String name;
	
	@ManyToOne(fetch= FetchType.EAGER) // 즉시 로딩
	@JoinColumn(name "TEAM_ID")
	private TEAM team;
	}
```
#### 프록시와 즉시로딩을 사용할 때 주의할점
- **가급적 지연 로딩만 사용하기!!(특히 실무에서는 더더욱이 그렇다)**
- 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생되기 때문이다. 
- **즉시 로딩은 JPQL에서 N+1 문제를 일으킨다.**
	ex) sql에서 member을 탐색하다가 TEAM이라는 속성이 있고, eager타입으로 되어있으면 TEAM도 호출하게 된다. 이렇게 되면 중복되는 쿼리를 많이 생성되게 된다. 최초 쿼리를 날렸는데 추가적으로 똑같은 쿼리가 나가는 것을 위에 말한 N+1쿼리라고 한다. 여기서 1이 최초 쿼리, n이 중복 쿼리이다. 
	cf) 즉시로딩을 사용하고 싶지만 N+1문제를 해결하기 위해서는 모두 지연로딩으로 깐다음에, jpql에서 fetchJoin을 사용하는 것이다. 이는 join되어있는 모든 쿼리를 1번만 가져와서 사용하게 된다.
- **@ManyToOne, @OneToOne은 기본이 즉시 로딩이기때문에 LAZY로 설정해주기!!!**
- @OneToMany, @ManyToMany는 기본이 지연 로딩이다. 

#### 지연로딩을 활용하는 방법! 이것은 이론적인 것이고, 실무에서는 모두 다 지연로딩으로 사용해야한다.
- Member와 Team이 자주 사용되면 즉시 로딩
- Member와 order는 가끔 사용되면 지연로딩
- order와 product는 자주 사용되면 즉시로딩
![image](https://user-images.githubusercontent.com/63040492/232283330-184d808c-a787-468a-b9e0-0635f4f2294e.png)

#### 지연 로딩을 실무에서 활용할때!!
- **모든 연관관계에 지연 로딩을 사용해라!**
- **실무에서 즉시로딩을 사용하지 마라!**
- jpql fetch 조인이나, 엔티티 그래프 기능을 사용하기(뒤에서 설명)
- 즉시 로딩은 상상하지 못한 쿼리가 나온다. 

### 영속성 전이:CASCADE
cf) 지연 로딩, 즉시로딩과 전혀 관계가 없다.
- 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶을 때 사용한다. 
- ex) 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장하고 싶을 때이다. == **부모를 저장할때, 자식도 모두 persist로 호출하고 싶을 때 사용한다**
- 영속성 전이(저장) : @OneToMany(mappedBy="parent", cascade=CascadeType.PERSIST)

```java
@Entity
public class Parent {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	@OneToMany(mappedBy="parent", cascade = CascadeType.ALL)
	private List<Child> childList = new ArrayList<>();
	
	public void addChild(Child child){
		childList.add(child);
		child.setParent(this);
	}
	
	//getter setter
	... 
```
```java
@Entity
public class Child{
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Parent parent;
	// getter setter
	...
```
```java
//JpaMain
public class JpaMain{
 public static void main(String[] args){
 	EntityManagerFactory emf = Persistence.createEntityManageFactory("hello');
	EntityManager em = emf.createEntityManager();
	
	EntityTransaction tx - em.getTransaction();
	
	try{
		Child child1 = new Child();
		Child child2 = new Child();
		
		Parant parent = new Parent();
		parent.addChild(child1);
		parent.addChild(child2);
		
		//  이때 persist가 3번이나 필요하다. 
		em.persist(parent);
		em.persist(child1);
		em.persist(child2);
    // cascade를 사용하면 아래 코드만 사용하면 된다.
		em.persist(parent);
		
		tx.commmit();
	}
	...
```

우리는 parent가 persist 가 되었을 때 child도 굳이 persist 안해줘도 저절로 되었으면 좋겠다!
이때 사용하는 것이 영속성 전이(CASCADE)이다. 

#### 영속성 전이: CASCADE - 주의!
- 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없다.
- 엔티티를 영속화할 때 연관된 엔티티도 함께 영속화하는 편리함을 제공할 뿐이다. 
- 일대다일때 항상 하는 것이 아니다. 하나의 부모가 모두 자식들을 관리할때 의미가 있다. (단일 소유자일때)
- 하지만 여러 파일에서 자녀의 엔티티를 관리할때, 즉 여러 부모가 같은 자식을 관리할때는 사용하면 안된다. 

#### CASCADE의 종류
- **ALL: 항상 같이 저장되어야 할때 사용**
- **PERSIST: 그냥 저장할때 영속용으로 사용**
- **REMOVE: 삭제**
- MERGE: 병합
- REFRESH: REFRESH
- DETACH: DETACH

#### 고아 객체
- 고아 객체 제거: 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제하는 기능이다. 
- orphanRemoval = true
- Parent parent1 = em.find(Parent.class, id);
	parent1.getChildren().remove(0);
	// 자식 엔티티를 컬렉션에서 제거
- DELETE FROM CHILD WHERE ID=?

#### 고아 객체 사용할 때 주의할 점
- 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능이다. 
- 참조하는 곳이 하나일 때 사용해야한다!!
- 특정 엔티티가 개인 소유할 때 사용
- @OneToOne, @OneToMany만 가능하다
- cf) 개념적으로 부모를 제거하면 자식은 고아가 된다. 따라서 고아 객체 제거 기능을 활성화 하면 부모를 제거할때 자식도 함께 제거된다. 이것은 CascadeType.REMOVE처럼 동작한다. 

#### 영속성 전이+고아 객체, **생명주기**
- CascadeType.ALL + orphanRemovel=true
- 스스로 생명 주기를 관리하는 엔티티는 em.persist()로 영속화, em.remove()로 제거한다.
- 두 옵션을 모두 활성화 하면 부모 엔티티를 통해서 자식의 생명 주기를 관리할 수 있다. 
- 도매인 주도 설계(DDD)의 Aggregate Root 개념을 수현할 때 유용하다


### 기본값 입
JPA는 데이터 타입을 엔티티 타입과 값 타입으로 크게 2가지로 분류한다. 
- 엔티티 타입이란 
	- @Entity로 정의하는 클래스 객체를 말한다.
	- 이는 데이터가 변해도 식별자(pk)로 지속해서 **추적이 가능**하다는 특징이 있다. 
	- ex) 회원 엔티티의 키나 나이 값을 다 변경해도 이 데이터가 특정 유저의 데이터라는 것을 인식 가능하다.
	
- 값 타입이란
	- int, Integer, String처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체를 말한다.
	- 이는 식별자가 없고 값만 있으므로 변경시 추적이 불가능하다.
	- ex) 숫자 100을 200으로 변경하면 완전히 다른 값을 대체된다. 
	
#### 값타입 분류(3가지)
- **기본값 타입**
	- 자바 기본 타입(int, double)
	- 래퍼 클래스(Integer, Long)
	- String
- **임베이드 타입(embedded type, 복합 값 타입)
- **컬렉션 값 타입**

**기본값 타입**
- ex) String name, int age
- 생명주기를 엔티티에 의존한다.
	- ex) 회원을 삭제하면 이름, 나이 필드도 함께 삭제된다. 
- 값 타입은 공유하면 절대 안된다. 
	- ex) 회원 이름 변경 시 다른 회원의 이름도 함께 변경되면 안된다. == sideEffect
	cf) 자바의 기본 타입은 절대 공유되지 않는다.
		- int, double 같은 기본 타입(primitive type)은 절대 공유되지 않는다. 
		- 기본 타입은 항상 값을 복사한다. 
		- Integer같은 래퍼 클래스나 String 같은 틀수한 클래스는 공유 가능한 객체이지만 변경은 되지 않는다. 
		


### 임베디드 타입(복합 값 타입)
#### 임베디드 타입
- 새로운 값 타입을 직접 정의할 수 있다
- JPA는 임베디드 타입(embedded type)이라고 한다.
- 주로 기본 값 타입을 모아서 만들어서 복합 값 타입이라고도 한다. 
- 이는 int, String과 같은 값 타입이므로 추적이 안된다. 변경하면 끝!

언제 사용하는지 예를 한번 들어보자. 우리는 회원 엔티티를 생성할때, 이름, 근무 시작일, 근무 종료일, 주소 도시, 주소 번지, 주소 우편번호를 가진다. == id, name, startDate, endDate, city, street, zipcode 
하지만 여기에는 비슷한 속성인 것들이 있다. startDate와 endDate는 기간에 관련된 것이고, city, street, zipcode는 집 주소와 관련되어있다. 이를 하나의 관련된 것으로 묶고 싶을 때 우리는 임베디드 타입을 사용한다. startPeriod와 endPeriod는 workPeriod로, city, street, zipcode는 homeAddress로 묶고싶다. 그렇게 되면 아래와 같이 member의 속성이 매우 간단해진다. 
![image](https://user-images.githubusercontent.com/63040492/233086371-f4ec09d2-c60c-4190-9cf1-fea70f08b5df.png)  ![image](https://user-images.githubusercontent.com/63040492/233085422-42053dbe-6f1b-48f0-9837-54b43c8a8fc6.png)

#### 임베디드 타입 사용법
1. @Embeddable: 값 타입을 정의하는 곳에 표시한다.
2. @Embedded: 값 타입을 사용하는 곳에 표시한다. 
3. 기본 생성자는 필수이다!

#### 임베디드 타입의 장점
- 재사용이 가능하다.
- 재사용이 가능하기 때문에 응집도가 높다.
- Period.isWork()처럼 해당 값 타입만 사용하는 의미 있는 메소드를 만들 수 있다. == 객체 지향에 좋음!!
- 임베디드 타입을 포함한 모든 값 타입은, 값 타입을 소유한 엔티티에 생명주기를 의존한다. 


```java
@Entity
public class Member {
	@Id
	@GeneratedValue
	@Column(name="MEMBER_ID")
	private Long id;
	
	@Column(name = "USERNAME")
	private String username;
	
	//기간 period
	@Embedded
	private Period period;
	
	//주소
	@Embedded
	private Address address;
	
	//getter setter 
	...
}
```
```java

@Embeddable
public class Period {
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	
	public Period() {} // 기본 생성자
	//getter setter
	...
}
```
```java
@Embeddable
public class Address {
	private String city;
	private String street;
	private String zipcode;
	
	public Address() {} // 기본 생성자
	
	public Address(String city, String street, String zipcode){
		this.city = city;
		this.street = street;
		this.zipcode = zipcode;
	}
	
	//getter setter 
	...
}
```

```java
public class JpaMain {
	public static void main(String[] args) {
		//emf, em
		...
		try{
			Member member = new Memeber();
			member.setUsername("Hello");
			member.setHomeAddress(new Address("city", "street", "zipcode");
			member.setWordPeriod(new Period());
			...
		}
		...
	}
}
```

#### 임베디드 타입과 테이블 매핑
- 임베디드 타입은 엔티티의 값일 뿐이다. 
- 임베디드 타입을 사용하기 전과 후에 **매핑하는 테이블은 같다.**
- 객체와 테이블을 아주 세밀하게(find-grained) 매핑하는 것이 가능하다. 
- 잘 설계한 ORM 애플리케이션은 매핑한 테이블의 수보다 클래스의 수가 더 많다. 
- 이를 쓰면 용어, 코드, 도메인에 언어들을 공통으로 공유하면서 사용할 수 있는 장점이 있다. 
- 엔티티 하위에 값 타입(ADDRESS)이 들어올텐데 이 하위에 엔티티가 들어올 수도 있다. 

#### AttributeOverride: 속성 재정의
- 만약에 한 엔티티에서 같은 값 타입을 사용하게 되면 어떻게 될까? 
```java
	@Embedded
	private Address homeAddress;
	@Embedded
	private Address workAddress;
```
- 이때 오류를 해결하기 위해 사용되는 것이 AttributeOverride이다. 
- 여러개면 AttributeOverrides, 하나면 @AttributeOverride를 사용해서 컬러 명 속성을 재정의 해주면 된다. 


#### 임베디드 타입과 null
- 임베디드 타입의 값이 null이면 매핑한 컬럼 값은 모두 null이 된다. 


### 값 타입과 불변 객체 = 값 타입은 무조건 불변으로 만들자!!!
- 값 타입은 복잡한 객체 세상을 조금이라도 단순화하려고 만든 개념이다. 따라서 값 타입은 단순하고 안전하게 다룰 수 있어야 한다. 
우리는 엔티티를 복사하는 것은 부담스러워 하면서 값을 복사할 때는 크게 신경을 안쓰면서 코딩을 하게 된다. - 이는 값 타입이 자바 세상에서는 안전하게 설계되어 있어서 괜찮은 것이다. 하지만 값 타입을 복사할 때 항상 안전한 것은 아니다.

#### 값 타입 공유 참조
- 임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 매우 위험하다. 
- 부작용(Side Effect)가 발생한다. 
- 아래 코드와 같이 member1만 거주지를 바꾸려고 setcity를 하려고 하면, sql은 insert 쿼리를 2번 보내서 member2의 도시도 newCity가 되어버린다. 

```java
try {
	Address address = new Address("city", "street", "10000");
	
	Member member = new Member();
	member.setUsername("member1");
	member.setHomeAddress(address);
	em.persist(member);

	Member member2 = new Member();
	member2.setUsername("member2");
	member2.setHomeAddress(address);
	em.persist(member2);
	
	member.getHomeAddress().setCity("newCity");	
```



#### 값 타입 복사
- 값 타입의 실제 인스턴스인 값을 공유하는 것은 매우 위험하다. 
- 따라서 아래 코드와 같이 **값을 공유 하는 것 대신 인스턴스 값을 복사해서 사용해야된다.** 

```java
try {
	Address address = new Address("city", "street", "10000");
	
	Member member = new Member();
	member.setUsername("member1");
	member.setHomeAddress(address);
	em.persist(member);

	Address copyAddresss = new Address(address.getCity(), address.getStreet(), address.getZipcode());

	Member member2 = new Member();
	member2.setUsername("member2");
	member2.setHomeAddress(copyAddress);
	em.persist(member2);
	
	member.getHomeAddress().setCity("newCity");	
```

#### 객체 타입의 한계
- 항상 값을 복사해서 사용하면 공유 참조(위와 같은 상황)로 인해 발생하는 부작용을 피할 수 있다.
- 문제는 임베디드 타입처럼 **직접 정의한 값 타입은 자바의 기본 타입이 아니라 객체 타입이다.**
- 자바 기본 타입에 값을 대입하면 값을 복사한다. 
- **객체 타입은 참조 값을 직접 대입하는 것을 막을 방법이 없다.**
- **객체의 공유 참조는 피할 수 없다.**
- 객체 공유는 서로 같은 인스턴스(주소값)을 공유하고 있기 때문에 값이 변경이 되는 것이다. 

#### 불변 객체
- 이러한 문제를 해결하는 방법은 없다. 따라서 이러한 문제를 방지하기 위해서 객체 타입을 수정할 수 없게 만들어 부작용을 원천 차단시켜버려야 한다.
- 값 타입은 불변 객체로 설계해야한다.
- 생성자로만 값을 설정하고 수정자(Setter)를 만들지 않으면 된다. 
- 참고로, Integer, String은 자바가 제공하는 대표적인 불변 객체이다. 
**불변이라는 작은 제약으로 부작용이라는 큰 재앙을 막을 수 있다. 

그렇다면 setter가 없다면 값은 어떻게 바꿀것인가?
	
```java
try {
	Address address = new Address("city", "street", "10000");
	
	Member member = new Member();
	member.setUsername("member1");
	member.setHomeAddress(address);
	em.persist(member);

	// address를 통째로 갈아끼워야한다. 
	Address newAddresss = new Address("newCity", address.getStreet(), address.getZipcode()); 
	member.setHomeASddress(newAddress);
}
	
```


### 값 타입의 비교
- 값 타입: 인스턴스가 달라도 그 안에 값이 같은면 같은 것으로 봐야 한다. 
- 하지만 객체는 인스턴스가 다르면 그 안에 값이 같아도 다른것으로 본다. 
- **동일성(identity)의 비교**: 인스턴스의 참조 값을 비교한다. == 사용.
- **동등성()의 비교**: 인스턴스의 값을 비교한다. equals() 사용
- 값 타입은 a.equals(b)를 사용해서 동등성 비교를 해야한다. 
- 값 타입의 equals() 메소드를 적절하게 재정의(주로 모든 필드 사용)
		

### 값 타입 컬렉션
- 값 타입을 컬렉션에 담아서 사용하는 것을 말한다. 우리는 기존에 일대다 등을 빌드할 때 엔티티를 컬렉션으로 사용하였다. 하지만 컬렉션에 값 타임을 담아서 사용하려고 한다. 
값 타입을 컬렉션으로 가지고 있을 때, 여기서 발생하는 문제는 관계형 데이터베이스는 컬렉션을 테이블 안에 담을 수 있는 구조가 없고 값만 넣을 수 있다는 특성이다. 

#### 값 타입 컬렉션 적용 방법                      
- 값 타입을 하나 이상 저장할 때 사용한다.
- @ElemnetCollection, @CollectionTable을 사용한다.
- 데이터베이스는 컬렉션을 같은 데이블에 저장할 수 없다.
- 따라서 컬렉션을 저장하기 위한 별도의 테이블이 필요하다. 
- cf) 값 타입 컬렉션은 영속성 전이(Cascade) + 고아 객체 제거 기능을 필수로 가진다고 볼 수 있다. 
![image](https://user-images.githubusercontent.com/63040492/233769778-4019bb20-945c-481c-affe-d060fd03c5fd.png)


```java
@Entity
public class Member {
	...
	@Embedded
	private Address homeAddress;
	
	@ElementCollection
	@CollectionTable(name = "FAVORIT_FOOD", joinColumns = @JoinColumn(name = "MEMBER_ID"))
	private Set<String> favoriteFoods = new HashSet<>();
	
	@ElementCollection
	@CollectionTable(name = "ADDRESS" , joinColumns = @JoinColumn(name = "MEMBER_ID"))
	// address 값 타입을 여러개 저장
	private List<Address> addressHistory = new ArrayList<>();
	...
```

- 값 타입 저장 예제
```java
public class JpaMain {
	public static void main(String[] args) {
		...
		try{
			Member member = new Member();
			member.setUsername("member1");
			member.setAddress(new Address("HomeCity", "street", "10000"));
			
			member.getFavoritFoods().add("치킨");
			member.getFavoritFoods().add("피자");
			member.getFavoritFoods().add("족발");

			member.getAddressHistory().add(new Address("old1", "street", "10000"));
			member.getAddressHistory().add(new Address("old2", "street", "10000"));
}}}
```

- 값 타입 조회 + 값 타입 수정 방법 + 값 타입 컬렉션도 지연 로딩 전략 사용
```java
public class JpaMain {
	public static void main(String[] args) {
		...
		try{
			// 이전 코드들
			...
			Member findMember = em.find(Member.class, member.getId()); // 컬렉션은 지연로딩, address 는 
			List<Address> addressHistory = findMember.getAddressHistory();
			for(Address address : addressHistory) {
				System.out.println("address = " + address. getCity());
			}
			
			Set<String> favoriteFoods = findMember.getFavoritFoods();
			for (String favoriteFood : favoriteFoods) {
				System.out.println("favoriteFood = " + favoriteFood);
			}
			
			// 값 타입 내용 수정 
			// findMember.getHomeAddress().setCity("newcity"); -> 잘못된 방법 = 사이드 이팩트
			Address a = findMember = em.find(Member.class, member.getId());
			// 통째로 갈아끼우기!! = 올바른 방법
			findMember.setHomeAddress(new Address("newCity", "a.getStreet(), a.getZipcode()));
			
			// 치킨을 한식으로 바꾸고 싶다면? foodFavorit은 set<String>이기 때문에 애초에 값타입이므로 갈아낄 수가 없다.
			findMember.getFavoriteFoods().remove("치킨");
			findMember.getFavoriteFoods().add("한식");
			// 이런식으로 아예 없애줬다가 더해주어야한다. 
			
			// 주소를 바꿀 경우
			findMember.getAddressHistory().remove(new Address("old1", "street", "10000"));
			findMember.getAddressHistory().add(new Address("newCity", "street", "1000"));			
			
}}}
```

#### 값 타입 컬렉션의 제약사항
- 값 타입은 엔티티와 다르게 식별자 개념이 없다. 
- 값은 변경하면 추적이 어렵다.
- **값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다.**
- 값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본 키를 구성해야 함 : **null 입력X, 중복 저장 X**

=> 따라서 위 코드처럼 값 타입을 수정하면 쿼리가 엄청 많이 삭제되었다가 다시 생성되는 단점이 있다. 이를 해결하는 방법은 추후 강의에 나옴.


#### 값 타입 컬렉션 대신 사용할 대안
- 실무에서는 상황에 따라 값 타입 컬렉션 대신에 **일대다 관계**를 고려하는 것이 좋다
- 일대다 관계를 위한 엔티티를 만들고, 여기에서 값 타입을 사용하는 것이다.
- 영속성 전이 + 고아 객체 제거를 사용해서 값 타입 컬렉션 처럼 사용하면 된다. 
- EX)
```java
@Entity
public class AddressEntity{
	@Id @GeneratedValue
	private Long id;
	private Address address;
	pulbic AddressEntity(String city, String street, String zipcode) {
		this.address = new Address(city, street, zipcode);
	}
	public AddressEntity(Address address) {this.address = address;)
	... // getter setter
}
```
```java
@Entity
public class Member {
	...
	@Embedded
	private Address homeAddress;
	
	@ElementCollection
	@CollectionTable(name = "FAVORIT_FOOD", joinColumns = @JoinColumn(name = "MEMBER_ID"))
	private Set<String> favoriteFoods = new HashSet<>();
	
//	@ElementCollection
//	@CollectionTable(name = "ADDRESS" , joinColumns = @JoinColumn(name = "MEMBER_ID"))
//  // address 값 타입을 여러개 저장
//	private List<Address> addressHistory = new ArrayList<>();
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "MEMBER_ID")
	private List<AddressEntity> addressHistory = new ArrayList<>();
		
	...
```
```java
public class JpaMain {
	public static void main(String[] args) {
		...
		try{
			Member member = new Member();
			member.setUsername("member1");
			member.setAddress(new Address("HomeCity", "street", "10000"));
			
			member.getFavoritFoods().add("치킨");
			member.getFavoritFoods().add("피자");
			member.getFavoritFoods().add("족발");

			member.getAddressHistory().add(new AddressEntity("old1", "street", "10000"));
			member.getAddressHistory().add(new AddressEntity("old2", "street", "10000"));
}}}
```

## 객체지향 쿼리 언어 - 기본문법
JPA는 다양한 쿼리 방법을 지원한다.
- **JPQL**, JPA Criteria, **QueryDSL**, 네이티브 SQL, (JPQL이 안될 때)JDBC API 직접 사용하거나 MyBatis, SpringJdbcTemplate을 사용하면 된다.

### JPQL
- 필요 이유:
	- 가장 단순한 조회 방법은 EntityManager.find() 혹은 객체 그래프 탐색(a.getB().getC())이다.
	- 하지만,,, 나이가 18살 이상인 회원을 모두 검색하고 싶다면 어떻게 하지? 라는 것이 출발점이다. 
	- JPQL은 sql과 매우 유사한 문법이 제공이 된다.
	- JPA를 사용하게되면 우리는 모든 코드를 엔티티 객체 중심으로 개발을 해야한다. 
	- 이때 발생하는 문제는 검색 쿼리이다. 우리는 검색을 할 때도 **테이블이 아닌 엔티티 객체를 대상으로 검색**할 수 있어야한다.
	- **모든 DB 데이터를 객체로 변환해서 검색하는 것은 현실적으로 불가능하다.**
	- 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 **검색 조건이 포함된 SQL이 필요하다.** 
- JPQL이란?
	- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어를 제공한다.
	- 이는 SQL 문법 SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 등의 기능을 지원해준다.
	- JPQL은 엔티티 객체를 대상으로 쿼리를 날린다. 즉, 객제 지향 SQL이다.
	- SQL 은 데이터베이스 테이블을 대상으로 쿼리를 날린다는 차이가 있다. 
	- 따라서 데이터베이스 Sql에 의존하지 않는다는 장점이 있다.
	
	```java
	try{
		List<Member> result = em.createQuery(
			"select m From Member as m where m.username like "%kim%", Member.class).getResultList();
			}
	```
		
	이렇게 사용한 Member은 테이블이 아니라 엔티티 객체를 가리키고 있다. 

하지만 이렇게 사용하게 되면 동적 쿼리를 만들기 매우 어렵다는 단점이 있다. 
이러한 문제는 어떻게 해결할 수 있을까?
이를 해결하기 위해서 Criteria를 사용할 수도 있다. 
- Criteria이란?
	- 문자가 아닌 자바코드로 jpql을 작성할 수 있다.
	- jpql 빌더 역할을 한다
	- jpq 공식 기능이다.
	- 단점: 너무 복잡하고 실용성이 없다. 
	- > 대신 **QueryDSL을 사용하길 권장한다.**
	
```java
try{
	// criteria 사용 준비
	CriteriaBuilder cb = em.getCriteriaBuilder();
	CriteriaQuery<Member> query = cb.createQuery(Member.class);
	
	Root<Member> m = query.from(Member.class);
	
	CriteriaQuery<Member> cq = query.select(m)
	
	String username = "hyunbins";
	if(username != null){
		cq = cq.where(cb.equal(m.get("username"), "kim"));
	}
	List<Member> resultList = em.createQuery(cq).getresultLIst();
}
```
이렇게 코드를 짜면 오류를 쉽게 잡고, 동적 커리를 짜기가 비교적 매우 쉽다는 장점이 있다. 하지만 너무 복잡하다고 느낄수도 있다는 단점도 있다. 그리고 sql스럽지가 않다. 영한 아저씨도 유지 보수가 안되기 때문에 실무에서 사용하지 않는다고 한다. 

### QueryDSL
- 문자가 아닌 자바코드로 JPQL을 작성할 수 있다. 
- JPQL 빌더 역할을 한다
- 컴파일 시점에 문법 오류를 찾을 수 있다.
- 동적 쿼리 작성이 매우 편리하다. 
- 단순하고 쉽다.
- 실무 사용하길 권장한다. 

```java
@Override
public List<Order> findAllByQuerydsl(OrderSearch orderSearch){
	return queryFactory
		.select(order)
		.from(order)
		.join(order.member, member)
		.where(statusEq(orderSearch), memberNameEq(orderSearch))
		.fetch();
}

### 네이티브 SQL
- JPA가 제공하는 SQL을 직접 사용하는 기능이다. 
- JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능이다.
- ex) 오라클 CONNECT BY, 특정 db만 사용하는 SQL 힌트

```java
try{
	em.createNativeQuery("select MEMBER_ID, city, street, zipcode, USERNAME from MEMBER").getResultList();
}
```

### JDBC 직접 사용, SpringJdbcTemplate 등의 방법
- JPA를 사용하면서 JDBC 커넥션을 직접 사용하거나, 스프링 JdbcTemplate, 마이바티스 등을 함께 사용 가능하다.
- 하지만 영속성 컨텍스트를 적절한 시점에 강제로 플러시 해야한다.
- EX) JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트를 수동으로 플러시해야한다. 
- 영한 아저씨도 주로 네이티브 쿼리보다는 이걸 더 많이 사용한다고 한다. 



### JPQL - 기본 문법과 기능
JPQL = Java Persistence Query Language
- JPQL은 SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않는다. 
- JPQL은 결국 SQL로 변환된다. 

#### JPQL 문법
- select m from Member as m where m.age> 18
- 엔티티와 속성은 대소문자 구분 O (Member, age)
- JPQL 키워드는 대소문자 구분 X (SELECT, FROM, where)
- 엔티티 이름 사용, 테이블 이름 아님(Member)
- **별칭은 필수(m)** (as는 생략 가능)

#### TypeQuery, Query
- TypeQuery: 반환 타입이 명활할 때 사용
- Query: 반환 타입이 명확하지 않을 때 사용

```java
TypeQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class);
Query query = em.createQuery("SELECT m.username, m.age from Member m");























	
	

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



