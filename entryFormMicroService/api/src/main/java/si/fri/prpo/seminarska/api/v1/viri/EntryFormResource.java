package si.fri.prpo.seminarska.api.v1.viri;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import si.fri.prpo.seminarska.entitete.Member;
import si.fri.prpo.seminarska.zrna.EntryFormBean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

@RequestScoped
@Path("")
public class EntryFormResource {

    @Inject
    EntryFormBean entryFormBean;
    @Operation(
            summary = "Handles form submission",
            description = "Handles multipart form data for a member, including entry form PDF, enrollment certificate file, and digital signature."
    )
    @APIResponses({
            @APIResponse(
                    description = "Form processed successfully",
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            mediaType = MediaType.APPLICATION_JSON
                    )
            ),
            @APIResponse(
                    description = "Unsupported Media Type",
                    responseCode = "415",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            ),
            @APIResponse(
                    description = "Bad Request",
                    responseCode = "400",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @POST
    @Path("entry-form")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleForm(@FormDataParam("member") String memberJson, @FormDataParam("file") InputStream fileInputStream,
                               @FormDataParam("file") FormDataContentDisposition fileDetail,
                               @FormDataParam("signature") String signatureBase64) throws IOException {
        System.out.println(memberJson);
        System.out.println(fileInputStream);
        // Create an ObjectMapper with JavaTimeModule registered
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        JsonFormat.Value format = JsonFormat.Value
                .forPattern("dd-MMM-yy");
        mapper.configOverride(LocalDate.class).setFormat(format);

        // Deserialize in java object
        Member member = mapper.readValue(memberJson, Member.class);
        // Validate the incoming Member object
        Error memberIsValid = entryFormBean.validateMember(member);
        try {
            byte[] fileBytes = fileInputStream.readAllBytes();
            String fileType = fileDetail.getFileName().endsWith(".pdf") ? "application/pdf" : "image/" + fileDetail.getFileName().split("\\.")[1];

            member.setCertificateFile(fileBytes);
            member.setCertificateFT(fileType);

            byte[] signatureBytes = java.util.Base64.getDecoder().decode(signatureBase64);
            //member.setSignatureFile(signatureBytes);
            //member.setSignatureFT("image/png");

            // Generate PDF
            byte[] pdfBytes = generatePdfWithMemberDetails(member, signatureBytes);
            member.setEntryFormFile(pdfBytes);
            member.setEntryFormFT("application/pdf");
        }catch (Exception e){
            return  Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
        // Call the EntryFormBean to persist the member
        if(memberIsValid.getMessage() == null) {
            entryFormBean.addToPending(member);

            // Return the created member with a success status
            return Response.status(Response.Status.CREATED)
                    .entity(member)
                    .build();
        }else{
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(memberIsValid.getMessage()).build();
        }


    }
    private byte[] generatePdfWithMemberDetails(Member member, byte[] signatureBytes) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Add text
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Member Details");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Name: " + member.getName() + " " + member.getSurname());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Date of Birth: " + member.getDateOfBirth());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Address: " + member.getHomeAddress() + ", " + member.getCity());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("ZIP Code: " + member.getZipCode());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Email: " + member.getEmail());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Phone Number: " + member.getPhoneNumber());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("I agree to terms and conditions:");
            contentStream.endText();

            // Add signature image
            PDImageXObject signatureImage = PDImageXObject.createFromByteArray(document, signatureBytes, "signature");
            contentStream.drawImage(signatureImage, 50, 650, 200, 50); // Adjust position and size
            // try y: 500
            contentStream.close();

            // Save to byte array
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                document.save(outputStream);
                return outputStream.toByteArray();
            }
        }

    }
    @GET
    @Path("test")
    public Response testEndpoint() {
        System.out.println("Test endpoint hit");
        return Response.ok("It works!").build();
    }
}
