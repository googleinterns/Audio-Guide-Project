package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.sps.user.User;
import com.google.sps.servlets.UserServlet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.HashMap;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.google.gson.Gson;

@RunWith(JUnit4.class)
public final class UserServletTest {
    private static final String ID = "userid";
    private static final String EMAIL = "user@gmail.com";
    private static final String EMAIL_B = "userB@gmail.com";
    private static final String NAME = "username";
    private static final String SELF_INTRODUCTION = "I am the user";
    private static final String IMG_URL = "/img.com";

    private static final User toSaveUser = new User.Builder(ID, EMAIL).setName(NAME).addSelfIntroduction(SELF_INTRODUCTION).addImgUrl(IMG_URL).build();
    private static User toGetUser; 
   
    private UserServlet userServlet;
    private Map<String, Object> attributeToValue = new HashMap<>();
    private LocalServiceTestHelper helper;

    @Before
    public void setup() {
        attributeToValue.put("com.google.appengine.api.users.UserService.user_id_key", (Object) ID);
        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())
          .setEnvIsLoggedIn(true)
          .setEnvAuthDomain("localhost")
          .setEnvEmail(EMAIL)
          .setEnvAttributes(attributeToValue);
        helper.setUp();
        userServlet = new UserServlet();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
 
    @Test
    public void doPostDoGet() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("name")).thenReturn(NAME);
        when(request.getParameter("selfIntroduction")).thenReturn(SELF_INTRODUCTION);
        when(request.getParameter("publicPortfolio")).thenReturn("public");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
         
        when(response.getWriter()).thenReturn(pw);

        userServlet.doPost(request, response);
        userServlet.doGet(request, response);
        pw.flush();
        System.out.println("Response: " + sw.toString());
        Assert.assertTrue(sw.toString().contains(ID));
        Assert.assertTrue(sw.toString().contains(EMAIL));
        Assert.assertTrue(sw.toString().contains(NAME));
        Assert.assertTrue(sw.toString().contains(SELF_INTRODUCTION));
        Assert.assertTrue(sw.toString().contains("true")); //for public portfolio
    }
}