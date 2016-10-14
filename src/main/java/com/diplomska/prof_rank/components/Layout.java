package com.diplomska.prof_rank.components;

import com.diplomska.prof_rank.model.UserInfo;
import com.diplomska.prof_rank.pages.Index;
import com.diplomska.prof_rank.util.AppConfig;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.*;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.Session;

import java.net.URL;

/**
 * Layout component for pages of application test-project.
 */
@Import(module="bootstrap/collapse", stylesheet="context:css/main.css")
public class Layout
{
    @Inject
    private ComponentResources resources;

    /**
    * The page title, for the <title> element and the <h1> element.
    */
    @Property
    @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
    private String title;

    @Property
    private String pageName;

    @Property
    @Inject
    @Symbol(SymbolConstants.APPLICATION_VERSION)
    private String appVersion;

    @SessionState
    UserInfo userInfo;

    @Property
    Person person;

    @Inject
    private Messages messages;

    public String getClassForPageName()
    {
    return resources.getPageName().equalsIgnoreCase(pageName)
            ? "active"
            : null;
    }

    public String[] getPageNames()
  {
    return new String[]{"Index"};
  }

    public void setupRender() {
        person = userInfo.getPerson();
    }

    public boolean isLoggedIn() {
        return person != null ? true : false;
    }


    @Inject
    private Request request;

    public Object onActionFromSignOut() throws Exception{
        Session session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            userInfo = null;
            person = null;
        }

        return new URL(AppConfig.getString("cas.server")+"/cas/logout?service="+AppConfig.getString("app.server")+"/");
    }

}
