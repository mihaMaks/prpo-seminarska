package si.fri.prpo.seminarska.api.v1.viri;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.cors.annotations.CrossOrigin;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.prpo.seminarska.dtos.PaginatedResponse;
import si.fri.prpo.seminarska.entitete.Event;
import si.fri.prpo.seminarska.entitete.Member;
import si.fri.prpo.seminarska.entitete.CertificateOfEnrollment;
import si.fri.prpo.seminarska.zrna.MembersListBean;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Path("members")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@CrossOrigin(supportedMethods = "GET, POST, HEAD, DELETE, OPTIONS, PATCH")
public class MembersListResources {

    @Inject
    private MembersListBean membersListBean;


    @Operation(
            summary = "Get list of members",
            description = "Fetches a list of all members from the database."
    )
    @APIResponses({
            @APIResponse(
                    description = "Successfully retrieved the list of members",
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Member.class)
                    )
            ),
            @APIResponse(
                    description = "No members found in the list",
                    responseCode = "200",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Members list not found",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembers() {
        List<Member> members = membersListBean.getMemberList();
        if(members != null) {
            if(members.isEmpty()){
                return Response.status(Response.Status.OK).entity("List is empty").build();
            }
            return Response.ok(members).build();

        }
        return Response.status(Response.Status.NOT_FOUND).entity("Members list not found").build();
    }

    @Operation(
            summary = "Get paginated members with metadata",
            description = "Fetches a paginated list of members with additional metadata like total count and total pages."
    )
    @APIResponses({
            @APIResponse(
                    description = "Successfully retrieved paginated list of members",
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = PaginatedResponse.class)
                    )
            ),
            @APIResponse(
                    description = "Bad request due to invalid page or size parameters",
                    responseCode = "400",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "No members found for the given page",
                    responseCode = "204",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Internal server error",
                    responseCode = "500",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @GET
    @Path("paginated")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaginatedMembersWithMetadata(@QueryParam("page") int page, @QueryParam("size") int size) {
        if (page < 1 || size < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Page and size parameters must be greater than 0.")
                    .build();
        }

        int offset = (page - 1) * size; // Calculate the offset
        List<Member> members = membersListBean.getPaginatedMembers(offset, size);

        if (members.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("No members found for the given page.").build();
        }

        // Fetch total member count
        long totalCount = membersListBean.getTotalMemberCount();

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalCount / size);
        try {
            // Build the response with metadata
            PaginatedResponse response = new PaginatedResponse(members, page, size, totalCount, totalPages);
            return Response.ok()
                    .entity(response)
                    .build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/pending/paginated")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendingPaginatedMembersWithMetadata(@QueryParam("page") int page, @QueryParam("size") int size,
                                                           @QueryParam("name") String name,
                                                           @QueryParam("surname") String surname,
                                                           @QueryParam("email") String email,
                                                           @QueryParam("pending") String pending) {
        if (page < 1 || size < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Page and size parameters must be greater than 0.")
                    .build();
        }

        int offset = (page - 1) * size; // Calculate the offset
        // Base SQL query
        // Start the JPQL query
        // Initialize the JPQL query with a basic SELECT statement
        StringBuilder jpqlQuery = new StringBuilder("SELECT m FROM Member m WHERE 1=1");
        StringBuilder jpqlQueryCount = new StringBuilder("SELECT COUNT(m) FROM Member m WHERE 1=1");
        // Use default values for parameters if they are null or empty
        name = (name != null && !name.isEmpty()) ? "%" + name.toLowerCase() + "%" : "%"; // Default is '%' for all names
        surname = (surname != null && !surname.isEmpty()) ? "%" + surname.toLowerCase() + "%" : "%"; // Default is '%' for all surnames
        email = (email != null && !email.isEmpty()) ? "%" + email.toLowerCase() + "%" : "%"; // Default is '%' for all emails
        pending = (pending != null && !pending.isEmpty()) ? pending : null; // Default is null for pending

        // Add filters dynamically without using 'if' statements
        jpqlQuery.append(" AND LOWER(m.name) LIKE :name");
        jpqlQuery.append(" AND LOWER(m.surname) LIKE :surname");
        jpqlQuery.append(" AND LOWER(m.email) LIKE :email");

        jpqlQueryCount.append(" AND LOWER(m.name) LIKE :name");
        jpqlQueryCount.append(" AND LOWER(m.surname) LIKE :surname");
        jpqlQueryCount.append(" AND LOWER(m.email) LIKE :email");

        if (pending != null) {
            jpqlQuery.append(" AND m.pending = :pending");
            jpqlQueryCount.append(" AND m.pending = :pending");
        }

        // Add pagination to the query
        jpqlQuery.append(" ORDER BY m.surname");


        // Execute the native SQL query
        List<Member> members = membersListBean.getPendingPaginatedMembersList(jpqlQuery.toString(), name, surname, email, pending, size, offset);


        if (members.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("No members found for the given page.").build();
        }

        // Fetch total member count
        long totalCount = membersListBean.getTotalMemberFilterCount(jpqlQueryCount.toString(), name, surname, email, pending);

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalCount / size);
        try {
            // Build the response with metadata
            PaginatedResponse response = new PaginatedResponse(members, page, size, totalCount, totalPages);
            return Response.ok()
                    .entity(response)
                    .build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
    @Operation(
            summary = "Get member by ID",
            description = "Fetches a member's details by the given ID. Returns the member's information if found, otherwise returns a 'Not Found' error."
    )
    @APIResponses({
            @APIResponse(
                    description = "Member found and returned successfully",
                    responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Member.class))
            ),
            @APIResponse(
                    description = "Member not found for the given ID",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Bad request due to invalid ID format",
                    responseCode = "400",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @GET
    @Path("{memberId}")
    public Response getMember(@PathParam("memberId") long memberId) {
        try {
            Member member = membersListBean.getMemberById(memberId);

            // Check if member list is empty (i.e., no member with that ID exists)
            if (member == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Member not found for ID: " + memberId)
                        .build();
            }

            // Return the found member (assumes only one member will match the ID)
            return Response.ok(member).build();

        } catch (Exception e) {
            // Handle the case where memberId cannot be parsed to Long
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
    @GET
    @Path("pending")
    public Response getPendingMembers() {
        List<Member> pendingMembers = membersListBean.getPendingMembersList();

        if(pendingMembers != null) {
            if(pendingMembers.isEmpty()){
                return Response.status(Response.Status.NO_CONTENT).entity("No more pending members.\nGood job!").build();
            }
            return Response.ok(pendingMembers).build();

        }
        return Response.status(Response.Status.NOT_FOUND).entity("Pending members list not found").build();
    }
    @GET
    @Path("pending/{memberId}")
    public Response getPendingMember(@PathParam("memberId") long memberId) {
        try{
            Member member = membersListBean.getMemberById(memberId);
            List<Member> existingMembers = membersListBean.getExistingMembers(member);
            if (existingMembers.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No existing members like this exist" + memberId)
                        .build();
            }
            System.out.println(member.getName() + " "+ member.getSurname());
            System.out.println("\nexisting members count: " + existingMembers.size());
            return Response.ok(existingMembers).build();

        } catch (NumberFormatException e) {
            // Handle the case where memberId cannot be parsed to Long
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid ID format: " + memberId)
                    .build();
        }
    }
    @Operation(
            summary = "Delete member by ID",
            description = "Deletes the member with the given ID from the database. If the member is successfully deleted, a success message is returned. If the member is not found, a 'Not Found' error is returned."
    )
    @APIResponses({
            @APIResponse(
                    description = "Member successfully deleted",
                    responseCode = "200",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Member not found for the given ID",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Bad request due to invalid ID format",
                    responseCode = "400",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @DELETE
    @Path("{memberId}")
    @Transactional
    public Response deleteMember(@PathParam("memberId") long memberId) {
        try {
            boolean isDeleted = membersListBean.deleteMember(memberId);

            if (isDeleted) {
                return Response.ok("Member with ID " + memberId + " successfully deleted").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Member not found for ID: " + memberId)
                        .build();
            }
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid ID format: " + memberId)
                    .build();
        }
    }
    @Operation(
            summary = "Update member details",
            description = "Updates the details of an existing member with the given member ID. The entire member object is replaced with the updated data."
    )
    @APIResponses({
            @APIResponse(
                    description = "Updated member",
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Member.class)
                    )
            ),
            @APIResponse(
                    description = "Bad request due to invalid input",
                    responseCode = "400",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @PUT
    @Path("{memberId}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMember(@PathParam("memberId") long memberId, Member updatedMember) {
        try {

            // Update the member's details
            updatedMember.setId(memberId);
            Member member = membersListBean.updateMember(memberId, updatedMember);

            return Response.ok(member).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @Operation(summary = "Postes member", description = "Creates new memeber in database")
    @APIResponses({
            @APIResponse(description = "Member details", responseCode = "201", content = @Content(schema = @Schema(implementation =
                    Member.class)))
    })
    @POST
    @Transactional
    public Response addMember(Member member) {
        // Validate the incoming Member object
        Error memberIsValid = membersListBean.validateMember(member);

        // Call the MembersListBean to persist the member
        if(memberIsValid.getMessage() == null) {
            membersListBean.addToPending(member);

            // Return the created member with a success status
            return Response.status(Response.Status.CREATED)
                    .entity(member)
                    .build();
        }else{
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(memberIsValid.getMessage()).build();
        }


    }
    @Operation(
            summary = "Partially update member information",
            description = "Updates the details of an existing member with the given member ID. Only the fields that are not null in the request body will be updated."
    )
    @APIResponses({
            @APIResponse(
                    description = "Updated member",
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Member.class)
                    )
            ),
            @APIResponse(
                    description = "Member not found for the given ID",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Bad request",
                    responseCode = "400",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @PATCH
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response patchMember(@PathParam("id") Long id, Member updatedFields) {
        // Step 1: Fetch the existing member
        Member existingMember = membersListBean.getMemberById(id);
        if (existingMember == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Member with ID " + id + " not found.")
                    .build();
        }

        // Step 2: Update only the fields that are not null in the incoming data
        if (updatedFields.getName() != null) {
            existingMember.setName(updatedFields.getName());
        }
        if (updatedFields.getSurname() != null) {
            existingMember.setSurname(updatedFields.getSurname());
        }
        if (updatedFields.getDateOfBirth() != null) {
            existingMember.setDateOfBirth(updatedFields.getDateOfBirth());
        }
        if (updatedFields.getHomeAddress() != null) {
            existingMember.setHomeAddress(updatedFields.getHomeAddress());
        }
        if (updatedFields.getCity() != null) {
            existingMember.setCity(updatedFields.getCity());
        }
        if (updatedFields.getZipCode() != null) {
            existingMember.setZipCode(updatedFields.getZipCode());
        }
        if (updatedFields.getEmail() != null) {
            existingMember.setEmail(updatedFields.getEmail());
        }
        if (updatedFields.getPhoneNumber() != null) {
            existingMember.setPhoneNumber(updatedFields.getPhoneNumber());
        }
        if (updatedFields.getStatus() != null) {
            existingMember.setStatus(updatedFields.getStatus());
        }
        if (updatedFields.getPending() != null) {
            existingMember.setPending(updatedFields.getPending());
        }

        // Step 3: Persist the changes
        membersListBean.updateMember(id, existingMember);

        // Step 4: Return the updated member
        return Response.ok(existingMember).build();
    }
    @Operation(
            summary = "Get events for a specific member",
            description = "Retrieves all events attended by a member identified by their member ID. Calls an external service to fetch the events."
    )
    @APIResponses({
            @APIResponse(
                    description = "List of events attended by the member",
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Event.class)
                    )
            ),
            @APIResponse(
                    description = "Member not found for the given ID",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "No events found for the member",
                    responseCode = "200",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Error fetching events for the member",
                    responseCode = "500",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Bad request",
                    responseCode = "400",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @GET
    @Path("{memberId}/events")
    public Response getMemberEvents(@PathParam("memberId") long memberId) {
        try {
            if(membersListBean.getMemberById(memberId) == null){
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Member not found for ID: " + memberId)
                        .build();
            }
            //implement http call to http://localhost:8080/v1/{eventId}/{memberId}
            // HTTP client setup
            Client client = ClientBuilder.newClient();
            System.out.println("Calling URL: " + "http://event-service-1:8081/v1/events/for/" + memberId);
            WebTarget target = client.target("http://event-service-1:8081/v1/events/for/" + memberId);

            // Perform the HTTP GET request
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            int statusCode = response.getStatus();
            System.out.println("Response code: " + statusCode);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                // Read JSON response as String
                String json = response.readEntity(String.class);

                ObjectMapper mapper = new ObjectMapper();
                List<Event> events = mapper.readValue(json, new TypeReference<List<Event>>() {});
                if (events.isEmpty()) {
                    return Response.status(Response.Status.OK).entity("No events attended").build();
                }
                return Response.ok(events).build();
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                return Response.status(Response.Status.NOT_FOUND).entity("No events found for the member").build();
            } else {
                return Response.status(response.getStatus()).entity("Error fetching events for member").build();
            }

        } catch (Exception e) {
            // Handle the case where memberId cannot be parsed to Long
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @Operation(
            summary = "Add a certificate of enrollment to a member",
            description = "Associates a certificate of enrollment with a member identified by their ID. The certificate is provided in the request body."
    )
    @APIResponses({
            @APIResponse(
                    description = "Certificate of enrollment successfully added to the member",
                    responseCode = "201",
                    content = @Content(
                            schema = @Schema(implementation = Member.class),
                            mediaType = MediaType.APPLICATION_JSON
                    )
            ),
            @APIResponse(
                    description = "Member not found for the given ID",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Bad request when adding the certificate",
                    responseCode = "400",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Internal server error",
                    responseCode = "500",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @POST
    @Path("{memberId}/enrollments")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCertificateToMember(@PathParam("memberId") long memberId, CertificateOfEnrollment enrollment) {
        //System.out.println("addCertificateToMember reached!");
        try {
            // Fetch the member by ID
            Member member = membersListBean.getMemberById(memberId);
            if (member == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Member not found for ID: " + memberId)
                        .build();
            }

            // Persist the changes
            member = membersListBean.addCertificateOfEnrollment(member, enrollment);
            if (member != null) {
                return Response.status(Response.Status.CREATED)
                        .entity(member)
                        .build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("addCertificateOfEnrollment failed").build();


        }catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("{memberId}/enrollments")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCertificateForMember(@PathParam("memberId") long memberId) {
        try {
            // Fetch the member by ID
            Member member = membersListBean.getMemberById(memberId);

            if (member == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Member not found for ID: " + memberId)
                        .build();
            }

            // Persist the changes
            List<CertificateOfEnrollment> enrollments = member.getEnrollments();
            if (enrollments != null) {
                if(enrollments.isEmpty()) {
                    return Response.status(Response.Status.OK).entity("No enrollments yet.").build();
                }
                return Response.status(Response.Status.CREATED)
                        .entity(enrollments)
                        .build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("CertificateOfEnrollment list is null.").build();


        }catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @Operation(
            summary = "Associate a member with an event",
            description = "Associates an existing member with an event by their respective IDs. If the member or event does not exist, appropriate error responses are returned."
    )
    @APIResponses({
            @APIResponse(
                    description = "Event successfully associated with the member",
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Event.class)
                    )
            ),
            @APIResponse(
                    description = "Member not found for the given ID",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Event not found for the given ID",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Bad request",
                    responseCode = "400",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Error occurred while associating the member with the event",
                    responseCode = "500",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @POST
    @Transactional
    @Path("{memberId}/event/{eventId}")
    public Response addEventToMember(@PathParam("memberId") long memberId, @PathParam("eventId") long eventId) {
        try {
            // Fetch the member using membersListBean
            Member member = membersListBean.getMemberById(memberId);
            if (member == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Member not found for ID: " + memberId).build();
            }
            if (member.getVisitedEvents() == null) {
                member.setVisitedEvents(new ArrayList<>());
            }

            // Fetch the event using an HTTP GET request
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("http://event-service-1:8081/v1/events/" + eventId);
            Response eventResponse = target.request(MediaType.APPLICATION_JSON).get();

            if (eventResponse.getStatus() != Response.Status.OK.getStatusCode()) {
                return Response.status(Response.Status.NOT_FOUND).entity("Event not found for ID: " + eventId).build();
            }

            Event event = eventResponse.readEntity(Event.class);
            if (event.getAttendingMembers() == null) {
                event.setAttendingMembers(new ArrayList<>());
            }

            Member updatedMember = null;
            if (!event.getAttendingMembers().contains(member)) {
                event.getAttendingMembers().add(member);
                member.getVisitedEvents().add(event);


                updatedMember = membersListBean.updateMemberEvents(member.getId(), event);

                for(int i=0; i<updatedMember.getVisitedEvents().size(); i++) {
                    System.out.println("Member "+member.getId()+" visited events with id:"+member.getVisitedEvents().get(i).getId());
                }
                for(int i=0; i<event.getAttendingMembers().size(); i++) {
                    System.out.println("Event "+event.getId()+" visited member with id:"+event.getAttendingMembers().get(i).getId());
                }

            }else{
                System.out.println("Member "+member.getId()+" already visited event with id:"+ event.getId());
            }

            if (updatedMember != null) {
                return Response.ok().entity(updatedMember).build();
            }

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Something went wrong when associating member and event")
                    .build();

        } catch (Exception e) {
            // Handle any exception that occurs
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}/certificate")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getMemberCertificate(@PathParam("id") Long id) {
        Member member = membersListBean.getMemberById(id);
        System.out.println(member.getCertificateFile().toString());
        if (member == null || member.getCertificateFile() == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("File not found for the given member ID").build();
        }

        return Response.ok(member.getCertificateFile())
                .header("Content-Disposition", "attachment; filename=member-file." + member.getCertificateFT().split("/")[1])
                .type(member.getCertificateFT())
                .build();
    }
    @Operation(
            summary = "Get entry form for a member",
            description = "Retrieves the entry form (PDF or image) associated with a specific member, identified by their ID."
    )
    @APIResponses({
            @APIResponse(
                    description = "Entry form file retrieved successfully",
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/pdf",
                            schema = @Schema(type = SchemaType.STRING, format = "binary")
                    )
            ),
            @APIResponse(
                    description = "Entry form file not found for the given member ID",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @GET
    @Path("{id}/entry-form")
    @Produces({"application/pdf", "image/png", "image/jpeg"}) // Adjust MIME types accordingly
    public Response getEntryFormFile(@PathParam("id") Long id) {
        Member member = membersListBean.getMemberById(id);
        System.out.println(member.getEntryFormFile().toString());
        if (member != null && member.getEntryFormFile() != null) {
            byte[] fileData = member.getEntryFormFile();
            return Response.ok(fileData)
                    .header("Content-Disposition", "attachment; filename=entry-form.pdf") // Customize filename and extension
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
