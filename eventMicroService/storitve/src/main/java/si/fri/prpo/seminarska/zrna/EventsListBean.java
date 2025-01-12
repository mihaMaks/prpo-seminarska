package si.fri.prpo.seminarska.zrna;

import si.fri.prpo.seminarska.entitete.Event;
import si.fri.prpo.seminarska.entitete.Member;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class EventsListBean {
    private Logger log = Logger.getLogger(EventsListBean.class.getName());

    @PostConstruct
    private void init() {
        log.info("Inicializacija zrna " + EventsListBean.class.getSimpleName());

        // inicializacija virov
    }

    @PreDestroy
    private void destroy() {
        log.info("Deinicializacija zrna " + EventsListBean.class.getSimpleName());

        // zapiranje virov
    }
    @PersistenceContext(unitName = "seznam-clanov-jpa")
    private EntityManager em;

    public List<Event> getEventsList(){
        List<Event> events = em.createNamedQuery("Events.getAll", Event.class).getResultList();
        return events;
    }

    public List<Event> getPaginatedEvents(int offset, int size){
        return em.createNamedQuery("Events.getAll", Event.class)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();
    }

    public Event getEventById(long id){
        return em.find(Event.class, id);
    }

    public long getTotalEventCount(){
        return em.createQuery("SELECT COUNT(e) FROM Event e", Long.class).getSingleResult();

    }

    public Member addEventToMember(long id, Event event) {
        Member member = em.find(Member.class, id);
        member.getVisitedEvents().add(event);
        em.persist(member);
        return member;
    }

    public Event updateEvent(long id, Member member) {
        Event existingEvent = getEventById(id);
        if (existingEvent == null) {
            return null;
        }
        try{
            existingEvent.getAttendingMembers().add(member);
            em.merge(existingEvent);
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
        return existingEvent;
    }

    public List<Event> getEventsListFor(long memberId) {
        return em.createNamedQuery("Members.getEventsForMember", Event.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<Member> getMembersForEvent(long eventId) {
        return em.createNamedQuery("Events.getMembersForEvent", Member.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    public Event createEvent(Event event) {
        try{
            em.persist(event);
            return event;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public boolean deleteEvent(long eventId) {
        Event event = em.find(Event.class, eventId);

        if (event != null) {
            em.remove(event);
            return true;
        }
        return false; // Event not found
    }

    public Event updateEvent(long eventId, Event updatedEvent) {
        Event existingEvent = em.find(Event.class, eventId);

        if (existingEvent != null) {
            // Update the fields of the existing event
            existingEvent.setName(updatedEvent.getName());
            existingEvent.setStarts(updatedEvent.getStarts());
            existingEvent.setPlace(updatedEvent.getPlace());
            existingEvent.setEnds(updatedEvent.getEnds());

            // Merge the updated entity
            em.merge(existingEvent);

            return existingEvent;
        }
        return null; // Event not found
    }



}
