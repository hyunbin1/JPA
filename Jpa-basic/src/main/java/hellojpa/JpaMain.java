package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args){
        // Entity 공장 생성 - 공장 생성은 한번만.
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");// 파라미터 안에는 persistense.xml에 있는 unit 이름을 넣어준다.

        // Entity Manager 객체 생성 - 트렌젝션 단위로 매번 생성.
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // 필요한 Entity Manager 코드 작성
        EntityTransaction transaction = entityManager.getTransaction(); // 트렌젝션 선언

        // 1. 멤버 저장
        try {
            transaction.begin(); // 트렌젝션 시작.
            Member member = new Member();
            member.setId(2L);
            member.setName("HelloB");

            entityManager.persist(member); // 객체를 영속성 컨텍스트에 저장. 그후 디비 저장.

            transaction.commit(); // 트렌젝션 종료 - 커밋 시점에 영속성 컨텍스트에 있는 내용이 DB에 저장된다.
        } catch(Exception e){
            transaction.rollback();
        } finally{
            entityManager.close();
        }

        entityManagerFactory.close();
    }
}
