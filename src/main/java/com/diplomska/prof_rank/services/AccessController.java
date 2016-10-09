package com.diplomska.prof_rank.services;

import java.io.IOException;
import java.net.URL;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.annotations.PublicPage;
import com.diplomska.prof_rank.model.UserInfo;
import com.diplomska.prof_rank.pages.Index;
import com.diplomska.prof_rank.pages.Login;
import mk.ukim.finki.isis.model.nonpersistant.ModelConstants;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

public class AccessController implements ComponentRequestFilter {

    private ApplicationStateManager applicationStateManager;
    private final ComponentSource componentSource;

    @Inject
    private Logger logger;

    @Inject
    private Response response;

    @Inject
    private PageRenderLinkSource linkSource;

    public AccessController(ApplicationStateManager asm,
                            ComponentSource componentSource) {
        this.applicationStateManager = asm;
        this.componentSource = componentSource;
    }

    public boolean checkAccess(String pageName) throws IOException {
        boolean hasAccessAnnotation = false;

        if (pageName.equals("") || pageName.equals("/")) {
            pageName = ModelConstants.PAGE_INDEX;
        }

        Component page = null;
        page = componentSource.getPage(pageName);

        boolean publicPage = page.getClass()
                .getAnnotation(PublicPage.class) != null;
        boolean instructorPage = page.getClass()
                .getAnnotation(InstructorPage.class) != null;
        boolean adminPage = page.getClass()
                .getAnnotation(AdministratorPage.class) != null;

        hasAccessAnnotation = publicPage | instructorPage | adminPage;
        UserInfo userInfo = applicationStateManager.getIfExists(UserInfo.class);

        boolean canAccess = true;
        if (publicPage) {
            return true;
        }

        if (userInfo == null) {
            canAccess = false;
        } else {
            if (instructorPage && canAccess) {
                canAccess = canAccess || userInfo.isInstructor();
            }
            if (adminPage && canAccess) {
                canAccess = canAccess || userInfo.isAdmin();
            }
        }

        if (!canAccess || !hasAccessAnnotation) {
            logger.debug("checkAccess: ACCESS DENIED to " + pageName + " "
                    + canAccess + " " + hasAccessAnnotation);
            return false;
        }
        return true;
    }

    @Override
    public void handleComponentEvent(ComponentEventRequestParameters parameters,
                                     ComponentRequestHandler handler) throws IOException {
        boolean accessOK = checkAccess(parameters.getContainingPageName());
        if (accessOK) {
            handler.handleComponentEvent(parameters);
        } else {
            logger.error("handleComponentEvent: ACCESS DENIED TO "
                    + parameters.getEventType() + " "
                    + parameters.getNestedComponentId() + " "
                    + parameters.getContainingPageName());

            response.sendRedirect(linkSource.createPageRenderLink(Index.class));
        }
    }

    @Override
    public void handlePageRender(PageRenderRequestParameters parameters,
                                 ComponentRequestHandler handler) throws IOException {
        boolean accessOK = checkAccess(parameters.getLogicalPageName());
        if (accessOK) {
            handler.handlePageRender(parameters);
        } else {
            logger.error("handlePageRender: ACCESS DENIED TO "
                    + parameters.getLogicalPageName());

//            response.sendRedirect(linkSource.createPageRenderLink(Login.class));
            response.sendRedirect("https://velkoski-pc:8443/cas/login?service=http://localhost:9999/prof_rank/");

//            response.sendRedirect(linkSource.createPageRenderLink(Index.class));
        }
    }
}
