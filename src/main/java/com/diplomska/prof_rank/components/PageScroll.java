package com.diplomska.prof_rank.components;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.corelib.components.Loop;
import org.apache.tapestry5.internal.services.ArrayEventContext;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(stylesheet="context:css/main.css")
public class PageScroll implements ClientElement {
    @Component(
            publishParameters = "encoder, formState, element, index, empty"
    )
    private Loop<?> loop;
    @Parameter
    @Property
    private Object row;
    @Parameter(
            value = "prop:componentResources.id",
            defaultPrefix = "literal"
    )
    private String clientId;
    @Parameter(
            required = true
    )
    private int pageNumber;
    @Parameter(
            required = true,
            allowNull = false,
            defaultPrefix = "literal"
    )
    private String zone;
    @Parameter(
            required = true,
            allowNull = false,
            defaultPrefix = "literal"
    )
    private String scroller;
    @Parameter
    private JSONObject params;
    @Parameter("literal:[]")
    private Object[] context;
    private String assignedClientId;
    @Inject
    private JavaScriptSupport javaScriptSupport;
    @Inject
    private ComponentResources resources;
    @Inject
    private Block nextPageBlock;
    @Inject
    private TypeCoercer typeCoercer;
    @Inject
    private Request request;
    private EventContext eventContext;

    public PageScroll() {
    }

    @BeginRender
    void initialize() {
        this.assignedClientId = this.javaScriptSupport.allocateClientId(this.clientId);
        this.eventContext = new ArrayEventContext(this.typeCoercer, this.context);
    }

    @AfterRender
    void addJavaScript() {
        JSONObject specs = (new JSONObject()).put("scroller", this.scroller).put("scrollURI", this.getScrollURI()).put("zoneId", this.zone).put("params", this.params);
        this.javaScriptSupport.require("prof_rank/PageScroll").with(new Object[]{specs});
    }

    @OnEvent("scroll")
    Object scroll(EventContext context, @RequestParameter("pageNumber") int index) {
        this.pageNumber = index;
        this.eventContext = context;
        return this.nextPageBlock;
    }

    public List<?> getNextPage() {
        CaptureResultCallback resultCallback = new CaptureResultCallback();
        this.resources.triggerContextEvent("nextPage", this.eventContext, resultCallback);
        List result = (List)resultCallback.getResult();
        Object result1 = result == null?new ArrayList():result;
        return (List)result1;
    }

    public String getClientId() {
        return this.assignedClientId;
    }

    public String getScrollURI() {
        return this.resources.createEventLink("scroll", this.context).toAbsoluteURI();
    }
}
