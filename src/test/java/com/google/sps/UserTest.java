package com.google.sps;

import com.google.sps.user.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class UserTest {
    private final String id = "userid";

    @Test
    public void getId(){
        User newUser = new User.Builder(id).build();
        Assert.assertEquals(newUser.getId(), id);
    }
}