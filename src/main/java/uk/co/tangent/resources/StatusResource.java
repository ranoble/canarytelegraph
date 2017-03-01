package uk.co.tangent.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import uk.co.tangent.entities.Test;
import uk.co.tangent.entities.TestResult;
import uk.co.tangent.services.TestService;

@Path("/status")
@Produces(MediaType.APPLICATION_JSON)
@Api("/status")
public class StatusResource {
    TestService testService;

    @Inject
    public StatusResource(TestService testService) {
        this.testService = testService;
    }

    @GET
    @ApiOperation(value = "Get Overview Status", response = TestResult.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All healthy.", response = TestResult.class),
            @ApiResponse(code = 412, message = "One or more of your tests are in a failing state", response = TestResult.class),
            @ApiResponse(code = 500, message = "Internal server error") })
    public Response results() {
        List<TestResult> results = new ArrayList<>();
        List<Test> tests = testService.getTests();
        boolean healthy = true;
        for (Test test : tests) {
            Optional<TestResult> result = testService.getLatestResult(test);
            if (result.isPresent()) {
                results.add(result.get());
                if (!result.get().getHealthy()) {
                    healthy = false;
                }
            }
        }
        if (healthy) {
            return Response.ok(results).build();
        } else {
            return Response.status(Status.PRECONDITION_FAILED).entity(results)
                    .build();
        }

    }

}
