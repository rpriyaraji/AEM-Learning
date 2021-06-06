package com.adobe.training.core.core;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
//import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.component.annotations.Reference;

//import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//@SlingServlet(paths = "/bin/bulkPublish", methods = "POST")
@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=Bulk Publish Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths="+ "/bin/bulkPublish"
})
public class BulkPublishServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = -2134797834770579080L;
	private static final Logger log = LoggerFactory.getLogger(BulkPublishServlet.class);
	@Reference
	private transient Replicator replicator;
	String msg;
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		log.info("----------< Processing starts >----------");
		
		log.info("request.getParameter(\"data\") {}", request.getParameter("data"));
		String data = request.getParameter("data");
		String[] arrSplit_3 = data.split("\\s");
		try {
			
		    for (int i=0; i < arrSplit_3.length; i++){
		    	String path = arrSplit_3[i];

				ResourceResolver resolver = request.getResourceResolver();
		
				Session session = resolver.adaptTo(Session.class);
		
				replicateContent(session, path);
		
				activatePageAssets(resolver, path);
		      System.out.println("Replication done for path -->" + arrSplit_3[i]);
		    }
			
			
			log.info("----------< Processing ends >----------");
			
			
			msg = "Publish done!";
			response.getWriter().append(msg);
		} catch (Exception e) {

			log.error(e.getMessage(), e);
		}
	}
	private void replicateContent(Session session, String path) {

		try {
			replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
			log.info("Replicated: {}", path);
		} catch (ReplicationException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private void activatePageAssets(ResourceResolver resolver, String path) {

		Set<String> pageAssetPaths = getPageAssetsPaths(resolver, path);

		if (pageAssetPaths == null) {

			return;
		}

		Session session = resolver.adaptTo(Session.class);

		for (String assetPath : pageAssetPaths) {
			replicateContent(session, assetPath);
		}
	}
	
	private Set<String> getPageAssetsPaths(ResourceResolver resolver, String pagePath) {

		PageManager pageManager = resolver.adaptTo(PageManager.class);

		Page page = pageManager.getPage(pagePath);

		if (page == null) {
			return new LinkedHashSet<>();
		}

		Resource resource = page.getContentResource();
		AssetReferenceSearch assetReferenceSearch = new AssetReferenceSearch(resource.adaptTo(Node.class),
				DamConstants.MOUNTPOINT_ASSETS, resolver);
		Map<String, Asset> assetMap = assetReferenceSearch.search();

		return assetMap.keySet();
	}
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
	log.info("----------< Processing starts >----------");
		
		Map<String, String> paramMap = handleRequest(request);
		//log.info("request.getParameter(\"data\") {}", request.getParameter("data"));
		String data = paramMap.get("data");
		String[] arrSplit_3 = data.split("\\s");
		try {
			
		    for (int i=0; i < arrSplit_3.length; i++){
		    	String path = arrSplit_3[i];

				ResourceResolver resolver = request.getResourceResolver();
		
				Session session = resolver.adaptTo(Session.class);
		
				replicateContent(session, path);
		
				activatePageAssets(resolver, path);
		      System.out.println("Replication done for path -->" + arrSplit_3[i]);
		    }
			
			
			log.info("----------< Processing ends >----------");
			
			
			msg = "Publish Done!";
			response.getWriter().append(msg);
		} catch (Exception e) {

			log.error(e.getMessage(), e);
		}
	}
	
	private Map<String, String> handleRequest(SlingHttpServletRequest request) throws IOException {
		String requestPayload = IOUtils.toString(request.getReader());
		if (StringUtils.isBlank(requestPayload)) {
			return null;
		}
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> paramMap = new Gson().fromJson(requestPayload, type);
		return paramMap;
	}

}