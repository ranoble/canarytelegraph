package uk.co.tangent.resources.wrappers;

import java.util.List;

import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.annotation.JsonGetter;

public class WrappedResults<T> {
    private List<T> results;

    private WrappedMeta meta;

    public WrappedResults(UriInfo uriInfo, Long total, List<T> pageResults,
            int page, int limit) {
        this.results = pageResults;
        this.meta = new WrappedMeta(uriInfo, total, page, limit);
    }

    @JsonGetter
    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> pageResults) {
        this.results = pageResults;
    }

    public WrappedMeta getMeta() {
        return meta;
    }

    public void setMeta(WrappedMeta meta) {
        this.meta = meta;
    }

}
