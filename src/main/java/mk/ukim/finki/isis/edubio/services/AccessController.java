package mk.ukim.finki.isis.edubio.services;

import java.io.IOException;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.annotations.InstructorPage;
import mk.ukim.finki.isis.edubio.annotations.PublicPage;
import mk.ukim.finki.isis.edubio.model.UserInfo;
import mk.ukim.finki.isis.edubio.pages.Index;
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
        UserInfo userInfo = applicationStateManager.get(UserInfo.class);

        boolean canAccess = true;
        if (publicPage) {
            return true;
        }

        if (userInfo == null) {
            canAccess = false;
        } else {
            // admin can access every page.
            if (userInfo.isAdmin()) {
                return true;
            }

            if (instructorPage && canAccess) {
                canAccess = userInfo.isInstructor();
            }
            if (adminPage && canAccess) {
                canAccess = userInfo.isAdmin();
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

            response.sendRedirect(linkSource.createPageRenderLink(Index.class));
        }
    }
}
