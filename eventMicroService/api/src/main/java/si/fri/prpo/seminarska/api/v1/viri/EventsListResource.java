package si.fri.prpo.seminarska.api.v1.viri;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.prpo.seminarska.dtos.PaginatedResponse;
import si.fri.prpo.seminarska.entitete.Event;
import si.fri.prpo.seminarska.entitete.Member;
import si.fri.prpo.seminarska.zrna.EventsListBean;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.Produces;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
@Path("events")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@CrossOrigin(supportedMethods = "GET, POST, HEAD, DELETE, OPTIONS, PATCH")
public class EventsListResource {

    @Inject
    EventsListBean eventsListBean;
    @Operation(
            summary = "Get all events",
            description = "Retrieves a list of all events available in the database."
    )
    @APIResponses({
            @APIResponse(
                    description = "List of all events",
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = Event.class),
                            mediaType = MediaType.APPLICATION_JSON
                    )
            ),
            @APIResponse(
                    description = "Events list not found",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "No events available",
                    responseCode = "204",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvents() {
        List<Event> events = eventsListBean.getEventsList();
        System.out.println(events.isEmpty());
        if(events != null) {
            if(events.isEmpty()){
                return Response.status(Response.Status.OK).entity("List is empty").build();
            }
            return Response.ok(events).build();

        }
        return Response.status(Response.Status.NOT_FOUND).entity("Events list not found").build();
    }
    @GET
    @Path("paginated")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaginatedEvents(@QueryParam("page") int page, @QueryParam("size") int size) {

        if (page < 1 || size < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Page and size parameters must be greater than 0.")
                    .build();
        }

        int offset = (page - 1) * size; // Calculate the offset
        List<Event> events = eventsListBean.getPaginatedEvents(offset, size);

        if (events.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("No events found for the given page.").build();
        }

        // Fetch total Event count
        long totalCount = eventsListBean.getTotalEventCount();

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalCount / size);

        // Build the response with metadata
        return Response.ok()
                .entity(new PaginatedResponse(events, page, size, totalCount, totalPages))
                .build();
    }

    @GET
    @Path("{eventId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventById(@PathParam("eventId") long eventId) {
        Event event = eventsListBean.getEventById(eventId);
        if(event != null) {
            return Response.ok(event).build();

        }
        return Response.status(Response.Status.NOT_FOUND).entity("Events list not found").build();
    }
    @Operation(
            summary = "Get events for a specific member",
            description = "Retrieves a list of events associated with a specific member, identified by their ID."
    )
    @APIResponses({
            @APIResponse(
                    description = "List of events for the specified member",
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = Event.class),
                            mediaType = MediaType.APPLICATION_JSON
                    )
            ),
            @APIResponse(
                    description = "Events not found for the given member ID",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @GET
    @Path("for/{memberId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventsForMember(@PathParam("memberId") long memberId) {

        List<Event> events = eventsListBean.getEventsListFor(memberId);
        if(events != null) {
            return Response.ok(events).build();

        }
        return Response.status(Response.Status.NOT_FOUND).entity("Events list not found").build();
    }
    @GET
    @Path("{eventId}/members")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembersForEvent(@PathParam("eventId") long eventId) {

        List<Member> members = eventsListBean.getMembersForEvent(eventId);
        if(members != null) {
            return Response.ok(members).build();

        }
        return Response.status(Response.Status.NOT_FOUND).entity("Events list not found").build();
    }

    @Operation(
            summary = "Update an event",
            description = "Updates an event identified by its ID with the provided details."
    )
    @APIResponses({
            @APIResponse(
                    description = "Event updated successfully",
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = Event.class),
                            mediaType = MediaType.APPLICATION_JSON
                    )
            ),
            @APIResponse(
                    description = "Event not found",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Invalid event data",
                    responseCode = "400",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @POST
    @Path("{eventId}/member")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEvent(@PathParam("eventId") long eventId, Member member) {
        if (member == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Event data cannot be null.").build();
        }

        Event existingEvent = eventsListBean.getEventById(eventId);
        if (existingEvent == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Event not found.").build();
        }

        // Save the updated event
        eventsListBean.updateEvent(eventId, member);

        return Response.ok(existingEvent).build();
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEvent(Event event) {
        if (event == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Event data cannot be null.").build();
        }

        try {
            // Persist the new event using the bean
            eventsListBean.createEvent(event);

            // Return the created event with status 201 Created
            return Response.status(Response.Status.CREATED).entity(event).build();
        } catch (Exception e) {
            // Handle errors (e.g., validation errors or persistence errors)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error creating event: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("{eventId}")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
            summary = "Delete an event",
            description = "Deletes an event identified by its ID."
    )
    @APIResponses({
            @APIResponse(
                    description = "Event deleted successfully",
                    responseCode = "200",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Event not found",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @Transactional
    public Response deleteEvent(@PathParam("eventId") long eventId) {
        boolean isDeleted = eventsListBean.deleteEvent(eventId);

        if (isDeleted) {
            return Response.ok("Event deleted successfully.").build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Event not found.").build();
    }

    @PUT
    @Path("{eventId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Update an event",
            description = "Updates the details of an event identified by its ID."
    )
    @APIResponses({
            @APIResponse(
                    description = "Event updated successfully",
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = Event.class),
                            mediaType = MediaType.APPLICATION_JSON
                    )
            ),
            @APIResponse(
                    description = "Event not found",
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Invalid event data",
                    responseCode = "400",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @Transactional
    public Response updateEvent(@PathParam("eventId") long eventId, Event updatedEvent) {
        if (updatedEvent == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Event data cannot be null.").build();
        }

        Event event = eventsListBean.updateEvent(eventId, updatedEvent);

        if (event != null) {
            return Response.ok(event).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Event not found.").build();
    }



}

