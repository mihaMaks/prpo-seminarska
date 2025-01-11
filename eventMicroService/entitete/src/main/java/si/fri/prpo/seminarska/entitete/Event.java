package si.fri.prpo.seminarska.entitete;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "events")
@NamedQueries(value =
        {
                @NamedQuery(name = "Events.getAll", query = "SELECT n FROM Event n"),
                @NamedQuery(name = "Events.getAllForId", query = "SELECT n FROM Event n WHERE n.id = :id")
                //@NamedQuery(name = "Events.getPast", query = "SELECT n FROM Member n WHERE n.pending = TRUE"),
                //@NamedQuery(name = "Events.findByNameAndSurname",
                  //      query = "SELECT m FROM Member m WHERE m.name LIKE :name AND m.surname LIKE :surname")
        })
public class Event {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @NotBlank
    @Column(name = "place")
    private String place;

    @NotNull
    @Column(name = "starts")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date starts;

    @NotNull
    @Column(name = "ends")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date ends;

    @ManyToMany
    @JsonbTransient
    @JoinTable(
            name = "event_member", // Name of the join table
            joinColumns = @JoinColumn(name = "event_id"), // Foreign key for Event
            inverseJoinColumns = @JoinColumn(name = "member_id") // Foreign key for Member
    )
    private List<Member> attendingMembers;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getStarts() {
        return starts;
    }

    public void setStarts(Date starts) {
        this.starts = starts;
    }

    public Date getEnds() {
        return ends;
    }

    public void setEnds(Date ends) {
        this.ends = ends;
    }

    public List<Member> getAttendingMembers() {
        return attendingMembers;
    }

    public void setAttendingMembers(List<Member> attendingMembers) {
        this.attendingMembers = attendingMembers;
    }
}
