package si.fri.prpo.seminarska.api.v1.viri;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;


@Path("/email")
public class EmailResource {
    String just_a_string ="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";

    @Operation(summary = "Sends email to a member",
            description = "Sends email to member's email when his enrillment is processed.")
    @APIResponses({
            @APIResponse(description = "Mailing info", responseCode = "200", content = @org.eclipse.microprofile.openapi.annotations.media.Content(schema = @Schema(implementation =
                    String.class)))
    })
    @POST
    @Path("/send")
    @Consumes("application/json")
    @Produces("application/json")
    public Response sendEmail(EmailRequest emailRequest) {
        try {
            //System.out.println(emailRequest.getTo() + emailRequest.getSubject() + emailRequest.getContent());
            sendEmailThroughSendGrid(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getContent());
            return Response.ok("Mail sent to " + emailRequest.getTo()).build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error sending email: " + e.getMessage())
                    .build();
        }
    }

    private void sendEmailThroughSendGrid(String toEmail, String subject, String content) throws IOException {
        Email from = new Email("maksbertoncelj@gmail.com");
        Email to = new Email(toEmail);
        Content cnt = new Content("text/plain", content);
        Mail mail = new Mail(from, subject, to, cnt);

        SendGrid sg = new SendGrid(just_a_string);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            com.sendgrid.Response response = sg.api(request);
            System.out.println("Email sent with status code: " + response.getStatusCode());
            System.out.println(toEmail);
            //System.out.println(response.getBody());
            //System.out.println(response.getHeaders());

        } catch (IOException ex) {
            throw ex;
        }
    }

    public static class EmailRequest {
        private String to;
        private String subject;
        private String content;

        // Getters and setters
        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

