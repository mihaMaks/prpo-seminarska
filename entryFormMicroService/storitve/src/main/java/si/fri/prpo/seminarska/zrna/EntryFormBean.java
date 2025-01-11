package si.fri.prpo.seminarska.zrna;

import si.fri.prpo.seminarska.entitete.Member;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.logging.Logger;

@RequestScoped
public class EntryFormBean {
    private Logger log = Logger.getLogger(EntryFormBean.class.getName());

    @PostConstruct
    private void init() {
        log.info("Inicializacija zrna " + EntryFormBean.class.getSimpleName());

        // inicializacija virov
    }

    @PreDestroy
    private void destroy() {
        log.info("Deinicializacija zrna " + EntryFormBean.class.getSimpleName());

        // zapiranje virov
    }
    @PersistenceContext(unitName = "seznam-clanov-jpa")
    private EntityManager em;

    @Transactional
    public void addToPending(Member member){
        try {
            member.setPending(true);
            em.persist(member);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Error validateMember(Member member){
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors: ");
            for (ConstraintViolation<Member> violation : violations) {
                System.out.println(violation.getMessage());
                errorMessage.append(violation.getMessage()).append("\n");
            }
            System.out.println(errorMessage);
            return new Error(errorMessage.toString());
        }
        return new Error();
    }
}
