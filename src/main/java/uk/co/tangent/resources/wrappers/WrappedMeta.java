package uk.co.tangent.resources.wrappers;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.annotation.JsonGetter;

public class WrappedMeta {

    private Long total;
    private UriInfo uriInfo;
    private int page;
    private int limit;

    public WrappedMeta(UriInfo uriInfo, Long total, int page, int limit) {
        this.total = total;
        this.uriInfo = uriInfo;
        this.page = page;
        this.limit = limit;
    }

    @JsonGetter
    public Long getTotal() {
        return total;
    }

    @JsonGetter
    public int getPage() {
        return page;
    }

    @JsonGetter
    public int getLimit() {
        return limit;
    }

    @JsonGetter
    public URI getNext() {

        if (((page * limit) + limit) < total) {
            return buildPageURI(page + 1);
        }
        return null;
    }

    @JsonGetter
    public URI getPrevious() {
        if (page > 0) {
            return buildPageURI(page - 1);
        }
        return null;
    }

    private URI buildPageURI(int page) {
        UriBuilder builder = UriBuilder.fromUri(uriInfo.getRequestUri());
        builder.replaceQueryParam("page", page);
        return builder.build();
    }

}
