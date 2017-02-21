package uk.co.tangent.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import uk.co.tangent.entities.Test;
import uk.co.tangent.resources.view.TestView;
import uk.co.tangent.services.TestService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {

    private final TestService service;

    @Inject
    public TestResource(TestService testService) {
        this.service = testService;
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Test getTest(@PathParam("id") Long id) {
        return service.getTest(id);
    }

    @POST
    @UnitOfWork
    public Test insert(Test test) {
        test.setId(null);
        return service.save(test);
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    public Test update(@PathParam("id") Long id, Test test) {
        test.setId(id);
        return service.save(test);
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Test update(@PathParam("id") Long id) {
        Test test = service.getTest(id);
        return service.delete(test);
    }

    @Produces(MediaType.TEXT_HTML)
    @GET
    public View getTestForm() {
        return new TestView();
    }

    @GET
    @UnitOfWork
    public List<Test> list() {
        return service.getTests();
    }
}
