package uk.co.tangent.data.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import uk.co.tangent.data.annotations.PluginType;
import uk.co.tangent.data.steps.auth.Auth;
import uk.co.tangent.data.steps.confirmations.Confirmation;
import uk.co.tangent.data.steps.confirmations.FailedResult;
import uk.co.tangent.data.steps.confirmations.Result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.RequestBodyEntity;

@JsonDeserialize(as = HttpStep.class)
@PluginType(name = "http")
public class HttpStep extends Step {

    @JsonProperty(required = true)
    private String url;
    private List<Map<String, String>> headers;
    @JsonProperty(required = true)
    private String method;
    private String payload;
    private List<Confirmation> confirm;

    private Auth auth;

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Map<String, String>> getHeaders() {
        return Optional.ofNullable(headers).orElse(
                new ArrayList<Map<String, String>>());
    }

    public void setHeaders(List<Map<String, String>> headers) {
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public List<Confirmation> getConfirm() {
        return Optional.ofNullable(confirm).orElse(
                new ArrayList<Confirmation>());
    }

    public void setConfirm(List<Confirmation> confirm) {
        this.confirm = confirm;
    }

    protected GetRequest get(String url) {
        return Unirest.get(bind(url));
    }

    protected GetRequest head(String url) {
        GetRequest request = Unirest.head(bind(url));
        request = applyHeaders(request);
        request = applyAuth(request);
        return request;
    }

    protected RequestBodyEntity post(String url, String payload) {
        HttpRequestWithBody request = Unirest.post(bind(url));
        request = applyHeaders(request);
        request = applyAuth(request);
        return request.body(bind(payload));
    }

    protected RequestBodyEntity put(String url, String payload) {
        HttpRequestWithBody request = Unirest.put(bind(url));
        request = applyHeaders(request);
        request = applyAuth(request);
        return request.body(bind(payload));
    }

    protected RequestBodyEntity patch(String url, String payload) {
        HttpRequestWithBody request = Unirest.post(bind(url));
        request = applyHeaders(request);
        request = applyAuth(request);
        return request.body(bind(payload));
    }

    protected RequestBodyEntity options(String url, String payload) {
        HttpRequestWithBody request = Unirest.options(bind(url));
        request = applyHeaders(request);
        request = applyAuth(request);
        return request.body(bind(payload));
    }

    protected RequestBodyEntity delete(String url, String payload) {
        HttpRequestWithBody request = Unirest.delete(bind(url));
        request = applyHeaders(request);
        request = applyAuth(request);
        return request.body(bind(payload));

    }

    @Override
    public List<Result> call() {
        List<Result> results = new ArrayList<Result>();
        try {
            BaseRequest request = null;
            switch (this.getMethod().toLowerCase().trim()) {
            case "get":
                request = get(this.getUrl());
                break;
            case "head":
                request = head(this.getUrl());
                break;
            case "put":
                request = put(this.getUrl(), this.getPayload());
                break;
            case "post":
                request = post(this.getUrl(), this.getPayload());
                break;
            case "options":
                request = options(this.getUrl(), this.getPayload());
                break;
            case "delete":
                request = options(this.getUrl(), this.getPayload());
                break;
            default:
                request = get(this.getUrl());
            }

            HttpResponse<String> response = request.asString();

            for (Confirmation confirmation : this.getConfirm()) {
                results.add(confirmation.validate(response));
            }

        } catch (UnirestException e) {
            for (Confirmation confirmation : this.getConfirm()) {
                results.add(new FailedResult(confirmation, e));
            }
        }
        return results;
    }

    protected HttpRequestWithBody applyHeaders(HttpRequestWithBody request) {
        for (Map<String, String> header : this.getHeaders()) {
            for (Entry<String, String> entry : header.entrySet()) {
                request = request
                        .header(entry.getKey(), bind(entry.getValue()));
            }
        }
        return request;
    }

    private GetRequest applyHeaders(GetRequest request) {
        for (Map<String, String> header : this.getHeaders()) {
            for (Entry<String, String> entry : header.entrySet()) {
                request = request
                        .header(entry.getKey(), bind(entry.getValue()));
            }
        }
        return request;
    }

    private GetRequest applyAuth(GetRequest request) {
        if (auth != null) {
            request.basicAuth(bind(auth.getName()), bind(auth.getPassword()));
        }
        return request;
    }

    private HttpRequestWithBody applyAuth(HttpRequestWithBody request) {
        if (auth != null) {
            request.basicAuth(bind(auth.getName()), bind(auth.getPassword()));
        }
        return request;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        // TODO Auto-generated method stub
        this.name = name;
    }

}
