package si.fri.prpo.seminarska.zrna;

import si.fri.prpo.seminarska.entitete.CertificateOfEnrollment;
import si.fri.prpo.seminarska.entitete.Member;
import si.fri.prpo.seminarska.entitete.Event;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@ApplicationScoped
public class MembersListBean {
    private Logger log = Logger.getLogger(MembersListBean.class.getName());

    @PostConstruct
    private void init() {
        log.info("Inicializacija zrna " + MembersListBean.class.getSimpleName());

        // inicializacija virov
    }

    @PreDestroy
    private void destroy() {
        log.info("Deinicializacija zrna " + MembersListBean.class.getSimpleName());

        // zapiranje virov
    }
    @PersistenceContext(unitName = "seznam-clanov-jpa")
    private EntityManager em;

    public List<Member>getMemberList(){
        return em.createNamedQuery("Members.getAll", Member.class).getResultList();
    }

    public List<Member> getPaginatedMembers(int offset, int limit) {
        return em.createQuery("SELECT m FROM Member m", Member.class)
                .setFirstResult(offset) // Start position
                .setMaxResults(limit)   // Maximum number of results
                .getResultList();
    }

    public long getTotalMemberCount() {
        return em.createQuery("SELECT COUNT(m) FROM Member m", Long.class).getSingleResult();
    }


    public List<Member>getPendingMembersList(){
        return em.createNamedQuery("Members.getPending", Member.class).getResultList();
    }

    public List<Member> getPendingPaginatedMembersList(String jpqlQuery, String name, String surname, String email, String pending, int size, int offset) {
        TypedQuery<Member> query = em.createQuery(jpqlQuery.toString(), Member.class);

        // Set parameters dynamically
        query.setParameter("name", name);
        query.setParameter("surname", surname);
        query.setParameter("email", email);

        if (pending != null) {
            if (pending.equals("TRUE")) {
                query.setParameter("pending", true);
            }else if (pending.equals("FALSE")) {
                query.setParameter("pending", false);

            }
        }

        // Set pagination
        query.setMaxResults(size);
        query.setFirstResult(offset);

        // Execute the query and return the result
        return query.getResultList();
    }
    public long getTotalMemberFilterCount(String jpqlQuery, String name, String surname, String email, String pending) {
        TypedQuery<Long> query = em.createQuery(jpqlQuery, Long.class);

        // Set the parameters dynamically
        query.setParameter("name", name);
        query.setParameter("surname", surname);
        query.setParameter("email", email);

        // Set the 'pending' parameter only if it's not null
        if (pending != null) {
            if (pending.equals("TRUE")) {
                query.setParameter("pending", true);
            }else if (pending.equals("FALSE")) {
                query.setParameter("pending", false);

            }
        }

        // Execute the query and return the count
        return query.getSingleResult();
    }

    public Member getMemberById(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> getExistingMembers(Member member){

        String pname = member.getName(); String psurname = member.getSurname();
        List<Member> existingMembers = em.createNamedQuery("Members.findByNameAndSurname", Member.class)
                .setParameter("name", "%" + pname + "%")
                .setParameter("surname", "%" + psurname + "%")
                .setParameter("pending", false)
                .getResultList();
        return existingMembers;

    }


    public boolean deleteMember(Long id) {
        Member member = getMemberById(id);
        if (member != null) {
            em.remove(member);
            return true;
        }
        return false;
    }


    public Member updateMember(Long id, Member member) {
        Member existingMember = getMemberById(id);
        if (existingMember == null) {
            return null;
        }

        em.merge(member);
        return member;
    }
    public Member updateMemberEvents(Long id, Event event) {
        Member existingMember = null;
        Event  existingEvent = null;
        try{
            existingMember = getMemberById(id);
            existingEvent = em.find(Event.class, event.getId());

        }catch (Error e){
            System.out.println(e);
            return null;
        }
        existingEvent.getAttendingMembers().add(existingMember);
        existingMember.getVisitedEvents().add(event);
        if (existingMember == null) {
            return null;
        }
        try{
            em.merge(existingMember);
            em.merge(existingEvent);

        }catch (Error e){
            System.out.println(e);
            return null;
        }
        return existingMember;
    }
    public Event getEventById(Long id) {
        return em.find(Event.class, id);
    }
    public Member addCertificateOfEnrollment(Member member, CertificateOfEnrollment certificate) {
        try {
            // Associate the certificate with the member
            certificate.setMemeber(member);

            // Get the list of enrollments for the member (initialize if necessary)
            List<CertificateOfEnrollment> enrollments = member.getEnrollments();
            if (enrollments == null) {
                enrollments = new ArrayList<CertificateOfEnrollment>();
            }

            // Add the new certificate to the enrollments list
            enrollments.add(certificate);

            // Set the enrollments list back to the member
            member.setEnrollments(enrollments);

            // Set member's status and pending flags
            member.setStatus(true);   // Member is active
            member.setPending(false); // Member is no longer pending

            // Persist the certificate to the database
            em.persist(certificate);

            try {
                // Your update logic here
                em.merge(member);
            } catch (ConstraintViolationException e) {
                validateMember(member);
                for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                    System.out.println("Validation error: " + violation.getMessage());
                }
            }

            // Return the updated member object
            return member;
        } catch (Exception e) {
            // Log the error and return null if something goes wrong
            System.out.println(e.getMessage());
            return null;
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

    @Transactional
    public void addToPending(Member member){
        try {
            member.setPending(true);
            em.persist(member);
        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
