package si.fri.prpo.seminarska.entitete;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.time.LocalDate;

@Entity
@Table(name = "members")
@NamedQueries(value =
        {
                @NamedQuery(name = "Members.getAll", query = "SELECT n FROM Member n"),
                @NamedQuery(name = "Members.getAllForId", query = "SELECT n FROM Member n WHERE n.id = :id"),
                @NamedQuery(name = "Members.getPending", query = "SELECT n FROM Member n WHERE n.pending = TRUE"),
                @NamedQuery(name = "Members.findByNameAndSurname",
                            query = "SELECT m FROM Member m WHERE m.name LIKE :name " +
                                    "AND m.surname LIKE :surname AND m.pending = :pending"),
                @NamedQuery(
                        name = "Members.getEventsForMember",
                        query = "SELECT e FROM Event e JOIN e.attendingMembers m WHERE m.id = :memberId"
                )

        })

public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Surname is required")
    @Column(name = "surname")
    private String surname;

    @Column(name = "dateOfBirth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Home address is required")
    @Column(name = "homeAddress")
    private String homeAddress;

    @NotBlank(message = "City is required")
    @Column(name = "city")
    private String city;

    @Pattern(regexp = "\\d{4}", message = "ZIP code must be 4 digits")
    @Column(name = "zipCode")
    private String zipCode;

    @Email(message = "Email should be valid")
    @Column(name = "email")
    private String email;

    @Pattern(regexp = "\\+?[0-9]{9,15}", message = "Phone number is invalid")
    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "pending")
    private Boolean pending;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient
    private List<CertificateOfEnrollment> enrollments;

    @ManyToMany(mappedBy = "attendingMembers")
    @JsonbTransient
    @JoinColumn(name = "visitedEvents")
    private List<Event> visitedEvents;

    @Lob
    @Column(name = "certificate_file")
    private byte[] certificateFile;  // Stores the image or PDF file

    @Column(name = "certificate_ft")
    private String certificateFT; // Stores the file type (e.g., "image/png", "application/pdf")

    @Lob
    @Column(name = "entry_form")
    private byte[] entryFormFile;  // Stores the image or PDF file

    @Column(name = "entry_form_ft")
    private String entryFormFT;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Name is required") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name is required") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Surname is required") String getSurname() {
        return surname;
    }

    public void setSurname(@NotBlank(message = "Surname is required") String surname) {
        this.surname = surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public @NotBlank(message = "Home address is required") String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(@NotBlank(message = "Home address is required") String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public @NotBlank(message = "City is required") String getCity() {
        return city;
    }

    public void setCity(@NotBlank(message = "City is required") String city) {
        this.city = city;
    }

    public @Pattern(regexp = "\\d{4}", message = "ZIP code must be 4 digits") String getZipCode() {
        return zipCode;
    }

    public void setZipCode(@Pattern(regexp = "\\d{4}", message = "ZIP code must be 4 digits") String zipCode) {
        this.zipCode = zipCode;
    }

    public @Email(message = "Email should be valid") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Email should be valid") String email) {
        this.email = email;
    }

    public @Pattern(regexp = "\\+?[0-9]{9,15}", message = "Phone number is invalid") String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Pattern(regexp = "\\+?[0-9]{9,15}", message = "Phone number is invalid") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
    public Boolean getPending() {
        return pending;
    }
    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public List<CertificateOfEnrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<CertificateOfEnrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public List<Event> getVisitedEvents() {
        return visitedEvents;
    }

    public void setVisitedEvents(List<Event> visitedEvents) {
        this.visitedEvents = visitedEvents;
    }

    public byte[] getCertificateFile() {
        return certificateFile;
    }

    public void setCertificateFile(byte[] certificateFile) {
        this.certificateFile = certificateFile;
    }

    public void setCertificateFT(String fileType) {
        this.certificateFT = fileType;
    }

    public String getCertificateFT() {
        return certificateFT;
    }

    public byte[] getEntryFormFile() {
        return entryFormFile;
    }

    public void setEntryFormFile(byte[] entryFormFile) {
        this.entryFormFile = entryFormFile;
    }

    public String getEntryFormFT() {
        return entryFormFT;
    }

    public void setEntryFormFT(String entryFormFT) {
        this.entryFormFT = entryFormFT;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Member{\n");
        sb.append("\tmember id: ").append(id).append("\n");
        sb.append("\tname: ").append(name).append("\n");
        sb.append("\tsurname: ").append(surname).append("\n");
        sb.append("\tzip code: ").append(zipCode).append("\n");
        //sb.append("\tmember=").append(member.getId().toString()).append("\n");
        sb.append("\t}\n");
        return sb.toString();
    }
}