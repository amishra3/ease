package com.mwmd.aem.search.example.impl.servlet;

import com.mwmd.aem.search.core.indexing.IndexService;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

/**
 *
 * @author Matthias Wermund
 */
@SlingServlet(paths = "/bin/ease-example/fullindex")
public class IndexServlet extends SlingSafeMethodsServlet {

    @Reference
    private IndexService indexService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        indexService.all();
        response.getWriter().print("Index generation started.");
    }
}
