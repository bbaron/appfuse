package org.appfuse.dao;

import org.appfuse.Constants;
import org.appfuse.model.Address;
import org.appfuse.model.User;
import org.appfuse.model.UserCookie;
import org.springframework.dao.DataAccessException;

/**
 * This class tests the current UserDAO implementation class
 * @author mraible
 */
public class UserDAOTest extends BaseDAOTestCase {
    private User user = null;
    private UserDAO dao = null;

    protected void setUp() throws Exception {
        super.setUp();
        dao = (UserDAO) ctx.getBean("userDAO");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
    }

    public void testGetUserInvalid() throws Exception {
        try {
            user = dao.getUser("badusername");
            fail("'badusername' found in database, failing test...");
        } catch (DataAccessException d) {
            if (log.isDebugEnabled()) {
                log.debug(d);
            }

            assertNotNull(d);
        }
    }

    public void testGetUser() throws Exception {
        user = dao.getUser("tomcat");

        assertTrue(user != null);
        assertTrue(user.getRoles().size() == 1);
    }

    public void testSaveUser() throws Exception {
        user = dao.getUser("tomcat");

        Address address = user.getAddress();
        address.setAddress("new address");
        user.setAddress(address);

        dao.saveUser(user);
        assertEquals(user.getAddress(), address);
        
        // verify that violation occurs when adding new user
        // with same username
        /*
        user.setId(null);
        try {
        	dao.saveUser(user);
            fail("saveUser didn't throw DataIntegrityViolationException");
        } catch (DataIntegrityViolationException e) {
        	assertNotNull(e);
            log.debug("expected exception: " + e.getMessage());
        }*/
    }

    public void testAddUserRole() throws Exception {
        user = dao.getUser("tomcat");

        assertTrue(user.getRoles().size() == 1);

        user.addRole(Constants.ADMIN_ROLE);
        user = dao.saveUser(user);

        assertTrue(user.getRoles().size() == 2);

        // add the same role twice - should result in no additional role
        user.addRole(Constants.ADMIN_ROLE);
        user = dao.saveUser(user);

        assertTrue("more than 2 roles [" + user.getRoles().size() + "]",
                   user.getRoles().size() == 2);

        user.getRoles().remove(1);
        user = dao.saveUser(user);

        assertTrue(user.getRoles().size() == 1);
    }

    public void testAddAndRemoveUser() throws Exception {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("testpass");
        user.setFirstName("Test");
        user.setLastName("Last");
        Address address = new Address();
        address.setCity("Denver");
        address.setProvince("CO");
        address.setCountry("USA");
        address.setPostalCode("80210");
        user.setAddress(address);
        user.setEmail("testuser@appfuse.org");
        user.setWebsite("http://raibledesigns.com");
        user.addRole(Constants.USER_ROLE);

        user = dao.saveUser(user);
        assertTrue(user.getUsername() != null);
        assertTrue(user.getPassword().equals("testpass"));

        dao.removeUser("testuser");

        try {
            user = dao.getUser("testuser");
            fail("Expected 'ObjectRetrievalFailureException' not thrown");
        } catch (DataAccessException d) {
            if (log.isDebugEnabled()) {
                log.debug(d);
            }

            assertNotNull(d);
        }
    }

    public void testSaveAndDeleteUserCookie() throws Exception {
        String cookieId = "BA67E786-C031-EA40-2769-863BB30B31EC";
        UserCookie cookie = new UserCookie();
        cookie.setUsername("tomcat");
        cookie.setCookieId(cookieId);
        dao.saveUserCookie(cookie);
        cookie = dao.getUserCookie(cookie);
        assertEquals(cookieId, cookie.getCookieId());

        dao.removeUserCookies(cookie.getUsername());

        cookie = dao.getUserCookie(cookie);
        assertNull(cookie);
    }
}
