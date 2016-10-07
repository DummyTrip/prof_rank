package com.diplomska.prof_rank.util;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by Aleksandar on 06-Oct-16.
 */
public class UTF8Filter implements Filter {
    private String encoding;

    @Override
    public void init(FilterConfig config) throws ServletException {
        encoding = config.getInitParameter("requestEncoding");
        if (encoding == null) {
            encoding = "UTF-8";
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain next) throws IOException, ServletException {
        // Respect the client-specified character encoding // (see HTTP
        // specification section 3.4.1)
        // if (null == request.getCharacterEncoding())
        request.setCharacterEncoding(encoding);
        next.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
