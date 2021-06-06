package com.adobe.training.core.core.servlets;



import java.io.IOException;

import javax.jcr.Repository;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.apache.sling.api.SlingHttpServletRequest;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import org.apache.sling.commons.json.JSONException;

import org.apache.sling.commons.json.JSONObject;



@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=Simple Sling Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.resourceTypes="+ "trainingproject/components/structure/page",
        "sling.servlet.selectors=" + "abc"
})
public class MySafeMethodServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = -3960692666512058118L;

	@Reference

	private Repository repository;

	@Override

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

		response.setHeader("Content-Type", "application/json");

		//response.getWriter().print("{\"coming\" : \"soon\"}");

		String[] keys = repository.getDescriptorKeys();

		JSONObject jsonObject = new JSONObject();

		for (int i = 0; i< keys.length; i++) {

			try{

				jsonObject.put(keys[i], repository.getDescriptor(keys[i]));

			}

			catch (JSONException e) {

				e.printStackTrace();

			}

		}

		response.getWriter().print(jsonObject.toString()); 

	}

}

