
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user")
public class UserResource {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {
        if ("admin".equals(username) && "admin".equals(password)) {
            return Response.ok("{\"status\":\"success\"}").build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity("{\"status\":\"error\", \"message\":\"Invalid credentials\"}")
                           .build();
        }
    }
}
