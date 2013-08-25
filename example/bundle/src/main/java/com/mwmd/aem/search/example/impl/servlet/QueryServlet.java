/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.example.impl.servlet;

import com.mwmd.aem.search.example.IndexFields;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.io.JSONWriter;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

/**
 *
 * @author Matthias Wermund
 */
@Properties({
    @Property(name = "url", label = "Solr URL", description = "Address of Apache Solr instance")
})
@SlingServlet(paths = "/bin/aem-search-example/query", metatype = true)
public class QueryServlet extends SlingSafeMethodsServlet {

    private String url;

    @Activate
    protected void activate(Map<String, Object> properties) {

        this.url = (String) properties.get("url");
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        String q = request.getParameter("q");
        if (q == null || q.trim().length() == 0) {
            q = "*:*";
        }
        try {
            SolrQuery query = new SolrQuery(q);
            query.addFacetField(IndexFields.LANGUAGE, IndexFields.TAGS);
            query.setFacetMinCount(1);
            query.setRows(Integer.MAX_VALUE);
            query.setFields(IndexFields.PATH, IndexFields.TITLE, "score");
            long time = System.currentTimeMillis();
            QueryResponse result = new HttpSolrServer(url).query(query);
            time = System.currentTimeMillis() - time;
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            JSONWriter json = new JSONWriter(response.getWriter());
            json.object();
            json.key("time").value(time);
            json.key("total").value(result.getResults().getNumFound());
            json.key("facets").object();
            for (FacetField facet : result.getFacetFields()) {
                if (facet.getValueCount() > 0) {
                    json.key(facet.getName()).array();
                    for (FacetField.Count value : facet.getValues()) {
                        json.object();
                        json.key("value").value(value.getName());
                        json.key("count").value(value.getCount());
                        json.endObject();
                    }
                    json.endArray();
                }
            }
            json.endObject();
            json.key("items").array();
            for (SolrDocument doc : result.getResults()) {
                json.object();
                json.key("path").value(doc.getFieldValue(IndexFields.PATH));
                json.key("title");
                Object titleValues = doc.getFieldValue(IndexFields.TITLE);
                String title = null;
                if (titleValues instanceof List) {
                    title = ((List) titleValues).get(0).toString();
                } else if (titleValues != null) {
                    title = titleValues.toString();
                }
                json.value(title != null && !title.isEmpty()
                        ? title : doc.getFieldValue(IndexFields.PATH));
                json.key("score").value(doc.getFieldValue("score"));
                json.endObject();
            }
            json.endArray();
            json.endObject();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(e.getMessage());
        }
    }
}
