package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args){
        // Entity 공장 생성 - 공장 생성은 한번만.
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");// 파라미터 안에는 persistense.xml에 있는 unit 이름을 넣어준다.

        // Entity Manager 객체 생성 - 트렌젝션 단위로 매번 생성.
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // 필요한 Entity Manager 코드 작성
        EntityTransaction transaction = entityManager.getTransaction(); // 트렌젝션 선언
        transaction.begin(); // 트렌젝션 시작.

        //JPQL 회원 조회
        try {
            List<Member> result = entityManager.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(5)
                    .setMaxResults(8)
                    .getResultList(); // member Entity를 선택해서 조회한다.

            for(Member member : result){
                System.out.println("Member.name = " + member.getName());
            }

            transaction.commit(); // 트렌젝션 종료 - 커밋 시점에 영속성 컨텍스트에 있는 내용이 DB에 저장된다.
        } catch(Exception e){
            transaction.rollback(); // 오류 발생시 롤백 해주기
        } finally{
            entityManager.close();
        }


        entityManagerFactory.close();
    }
}
