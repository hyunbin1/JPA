
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

#### 실전예제3 - 다양한 연관관계 매핑
##### 23/03/16

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

