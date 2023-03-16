
#### 다대다
##### 23/03/14 
1. 다대다: 관계형 데이터베이스는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없다. 따라서 연결 테이블을 추가해서 
일대다, 다대일 관계로 전환하여 풀어내야한다.

객체는 컬랙션을 이용하여 다대다가 되지만 테이블은 다대다가 안된다 따라서 ORM에서는 이러한 충돌문제를 해결해주기 위하여 하나의 방안을 모색해야된다. 
<br/>-> 해결 방법: @ManyToMany를 사용하고, @JoinTable로 연결 테이블을 지정하여 다대다 매핑(단방향, 양뱡향 모두 가능)을 하면 된다. 
<br/>
! 하지만 실무에서는 절대 사용하지 않는다 ! 
연결테이블이 단순히 연결만 하고 끝나지 않기 때문이다. <br/> 예를 들어, 주문시간, 수량 같이 추가적인 데이터 정보가 들어올 수 있다. 하지만 중간 테이블은 추가적인 데이터 항목을 추가할 수 없다. 
<br/>
![image](https://user-images.githubusercontent.com/63040492/224833085-072a0520-2ee3-47eb-9dce-95f4d8e836c9.png)
<br/>
다대다의 한계를 극복하기 위해 우리는 연결 테이블용 엔티티를 추가(연결 테이블을 엔티티로 승격)하여 문제를 해결할 수 있다. 
@ManyToMany 를 @OneToMany, @ManyToOne으로 변경해준다. 
<br/>
왠만하면 pk는 의미 없는 값을 쓰는 것이 좋다. 그래야 나중에 유연하게 사용할 수 있다. 아이디가 어디에 종속되면 나중에 새로운 비지니스를 추가할때 힘들어진다. \
<br/>

##### 23/03/16
#### 실전예제3 - 다양한 연관관계 매핑


![image](https://user-images.githubusercontent.com/63040492/225431875-342a650f-3638-4512-b9fc-e5aeea113aec.png)
![image](https://user-images.githubusercontent.com/63040492/225431805-11ffd68b-aab1-4b5f-82e3-a1e765833f3a.png)
![image](https://user-images.githubusercontent.com/63040492/225431833-3bd6d2f5-55f2-470d-b22d-2000815430f5.png)

``` java
// Domain.Delivery.class
@Entity
public class Delivery {
  @Id @GeneratedValue
  private Long id;
  
  private String city;
  private String street;
  private String zipcode;
  private DeliveryStatus status;
  
  // 딜리버리와 오더 일대일 관계 맺어주기
  @OneToOne(mappedBy ="delivery")
  private Order order;
  
}
```

``` java
// Domain.Category.class
@Entity
public class Category {
  @Id @GeneratedValue
  private Long id;
  private String name;
  
  // 양방향 설정하기
  @ManyToOne(name = "PARENT_ID")
  private Category parent;
  
  @OneToMany(mappedBy = "parent")
  private List<Category> child = new ArrayList<>();
  
  // 다대다 설정해주기 (실제론 사용안하지만 수업내용상 시도)
  @ManyToMany 
  // 서로 조인하는 곳이 어디인지 알려주기 인버스 컬럼은 반대쪽에서 조인하느 곳을 알려줌
  @JoinTable(name = "CATEGORY_ITEM", joinClumns = @JoinColumn(name = "CATEGORY_ID) ,inverseJoinColumns = @JoinCoulumn(name = "ITEM_ID")
  private List<Item> items = new ArrayList<>();
  
  
  
}
```

``` java
// Domain.Order.class
// .. 생략(나중에 추가 - 이 강의에서 중요한 부분만 첨가)
public class Delivery {
  // 주문과 배달 양방향 관계 설정해주기 
  @OneToOne
  @JoinColumn(name = "DELIVERY_ID")
  private Delevery delivery;

// .. 생략(나중에 추가)
}
```

**다대다 관계는 일대다, 다대일로 변경해라!
- 실전에서는 중간 테이블이 단순하지 않다.
- ManyToMany는 제약이 많다: 필드 추가 불가능, 엔티티 테이블 불일치
-> 따라서 실전에서는 절대 사용하지 않는다. 

cf) @JoinColumn 참고 속성
1. (referencedColumnName="") : 외래키가 참조하는 컬럼명이 다를 경우 사용한다. 없으면 테이블의 기본명을 사용한다. 
2. optional: false로 설정하면 연관된 엔티티가 항상 있어야한다. 기본값 : True
3. fetch: 글로벌 패치 전략을 설정한다. ex) ManyToMany = FetchType.EAGER, @OneToMany = FetchType.LAZY
4. cascade : 영속성 전이 기능을 사용한다. 
5. tergetEntity: 연관된 엔티티의 타임 정보를 설정한다. 하지만 이 기능은 거의 사용하지 않는다. 컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있다. 

일대다는 Mappedby가 있지만 다대일는 Mappedby가 없다. 
**다대일을 쓰면 이는 꼭 연관관계의 주인이 되어야 한다는 것이다. 
