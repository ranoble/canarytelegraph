package uk.co.tangent.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import uk.co.tangent.entities.Lane;
import uk.co.tangent.entities.Test;
import uk.co.tangent.entities.TestResult;
import uk.co.tangent.resources.wrappers.WrappedResults;
import uk.co.tangent.services.TestService;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
@Api("/test")
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
        Test test = service.getTest(id);
        return test;
    }

    @GET
    @Path("/{id}/results")
    @UnitOfWork
    @ApiOperation(value = "List the results of the test", response = WrappedResults.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = WrappedResults.class),
            @ApiResponse(code = 400, message = "Invalid JSON request"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public Response getTestResults(
            @Context UriInfo uriInfo,
            @PathParam("id") Long id,
            @ApiParam(value = "Number of results to return", example = "10") @QueryParam("limit") Optional<Integer> limit,
            @ApiParam(value = "Page to return, 0 based", example = "0") @QueryParam("page") Optional<Integer> page,
            @ApiParam(value = "ISO 8601 date format, date range start") @QueryParam("since") Optional<Date> since,
            @ApiParam(value = "ISO 8601 date format, date range end") @QueryParam("until") Optional<Date> until) {
        Test test = service.getTest(id);
        int _limit = limit.orElse(10);
        int _page = page.orElse(0);

        List<TestResult> results = service.getTestResults(test, _page, _limit,
                since, until);
        Long total = service.getTestResultsCount(test, since, until);
        WrappedResults<TestResult> res = new WrappedResults<TestResult>(
                uriInfo, total, results, _page, _limit);
        if (!results.isEmpty() && (!results.get(0).getHealthy().booleanValue())) {
            return Response.status(Status.EXPECTATION_FAILED).entity(res)
                    .build();
        }
        return Response.ok().entity(res).build();
    }

    // @Produces(MediaType.TEXT_HTML)
    // @UnitOfWork
    // @GET
    // @Path("/{id}/results")
    // public View getTestResultsView(@Context UriInfo uriInfo,
    // @PathParam("id") Long id,
    // @QueryParam("limit") Optional<Integer> limit,
    // @QueryParam("page") Optional<Integer> page,
    // @QueryParam("since") Optional<Date> since,
    // @QueryParam("until") Optional<Date> until) {
    // Test test = service.getTest(id);
    // int _limit = limit.orElse(10);
    // int _page = page.orElse(0);
    // List<TestResult> results = service.getTestResults(test, _page, _limit,
    // since, until);
    // return new TestResultListView(results);
    // }

    @POST
    @UnitOfWork
    @ApiOperation(value = "Create a new Test", notes = "See https://github.com/tangentlabs/canarytelegraph/test_definitions.md for test definition documentation", response = Test.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Test.class),
            @ApiResponse(code = 400, message = "Invalid JSON request"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public Response insert(Test test) {
        test.setId(null);
        return Response.status(Status.CREATED).entity(service.save(test))
                .build();
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @ApiOperation(value = "Update a Test", notes = "See https://github.com/tangentlabs/canarytelegraph/test_definitions.md for test definition documentation", response = Test.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Test.class),
            @ApiResponse(code = 404, message = "Resource not found"),
            @ApiResponse(code = 400, message = "Invalid JSON request"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public Response update(@PathParam("id") Long id, Test test) {
        if (service.getTest(id) == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        test.setId(id);
        return Response.ok(service.save(test)).build();
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    @ApiOperation(value = "Delete a Test", response = Test.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Test.class),
            @ApiResponse(code = 404, message = "Resource not found"),
            @ApiResponse(code = 400, message = "Invalid JSON request"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public Response update(@PathParam("id") Long id) {
        Test test = service.getTest(id);
        if (service.getTest(id) == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(service.delete(test)).build();
    }

    // @Produces(MediaType.TEXT_HTML)
    // @UnitOfWork
    // @GET
    // public View getTestForm() {
    // return new TestListView(service.getTests());
    // }

    @GET
    @UnitOfWork
    @ApiOperation(value = "List Test Lanes", response = Lane.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Lane.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid JSON request"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public List<Test> list() {
        return service.getTests();
    }
}
