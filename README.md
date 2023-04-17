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



