package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.sps.user.User;
import com.google.sps.servlets.UserServlet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
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
        // Set the userdata that the Userservice will return. 
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
    public void doPostDoGet_existingUserWithAllDataSet_returnsUserWithAllData() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter(UserServlet.NAME_INPUT)).thenReturn(NAME);
        when(request.getParameter(UserServlet.SELF_INTRODUCTION_INPUT)).thenReturn(SELF_INTRODUCTION);
        when(request.getParameter(UserServlet.PUBLIC_PORTFOLIO_INPUT)).thenReturn(UserServlet.PUBLIC_PORTFOLIO_INPUT_PUBLIC_VALUE);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
         
        when(response.getWriter()).thenReturn(pw);

        // Save the currenly logged in user's data
        userServlet.doPost(request, response);
        // Get the currenly logged in user's previously saved data
        userServlet.doGet(request, response);

        pw.flush();
        Gson gson = new Gson();
        User resultUser = gson.fromJson(sw.toString(), User.class);
        assertEquals(ID, resultUser.getId());
        assertEquals(EMAIL, resultUser.getEmail());
        assertEquals(NAME, resultUser.getName());
        assertTrue(resultUser.portfolioIsPublic());
        assertEquals(SELF_INTRODUCTION, resultUser.getSelfIntroduction());
    }

    @Test
    public void doPostDoGet_existingUserWithNoDataSet_returnsUserWithMissingData() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter(UserServlet.NAME_INPUT)).thenReturn("");
        when(request.getParameter(UserServlet.SELF_INTRODUCTION_INPUT)).thenReturn("");
        when(request.getParameter(UserServlet.PUBLIC_PORTFOLIO_INPUT)).thenReturn("private");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
         
        when(response.getWriter()).thenReturn(pw);

        // Save the currenly logged in user's data
        userServlet.doPost(request, response);
        // Get the currenly logged in user's previously saved data
        userServlet.doGet(request, response);

        pw.flush();
        Gson gson = new Gson();
        User resultUser = gson.fromJson(sw.toString(), User.class);
        assertEquals(ID, resultUser.getId());
        assertEquals(EMAIL, resultUser.getEmail());
        assertEquals(null, resultUser.getName());
        assertFalse(resultUser.portfolioIsPublic());
        assertEquals(null, resultUser.getSelfIntroduction());
    }

     @Test
    public void doGet_inexistentUser_returnsNull() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
         
        when(response.getWriter()).thenReturn(pw);

        // Try to get the currenly logged in user's data. 
        // It is not saved.
        userServlet.doGet(request, response);

        pw.flush();
        Gson gson = new Gson();
        User resultUser = gson.fromJson(sw.toString(), User.class);
        assertEquals(null, resultUser);
    }
}