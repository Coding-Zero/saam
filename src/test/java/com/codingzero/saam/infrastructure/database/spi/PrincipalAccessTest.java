package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.database.PrincipalAccess;
import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.saam.infrastructure.database.RoleAccess;
import com.codingzero.saam.infrastructure.database.RoleOS;
import com.codingzero.saam.infrastructure.database.UserAccess;
import com.codingzero.saam.infrastructure.database.UserOS;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public abstract class PrincipalAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PrincipalAccess access;
    private UserAccess userAccess;
    private RoleAccess roleAccess;
    private List<PrincipalOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
        userAccess = getUserAccess();
        roleAccess = getRoleAccess();
    }

    @After
    public void clean() {
        for (PrincipalOS os: generatedObjectSegments) {
            try {
                if (os.getType() == PrincipalType.USER) {
                    userAccess.delete((UserOS) os);
                }
                if (os.getType() == PrincipalType.ROLE) {
                    roleAccess.delete((RoleOS) os);
                }
            } catch (Exception e) {
                System.err.println("sometime wrong during clean up apikey, " + os);
            }
        }
    }

    @Test
    public void testGenerateId() {
        String applicationId = getApplicationId();
        int size = 10;
        Set<String> userIds = new HashSet<>(size);
        for (int i = 0; i < size; i ++) {
            userIds.add(access.generateId(applicationId, PrincipalType.USER).toLowerCase());
        }
        Set<String> roleIds = new HashSet<>(size);
        for (int i = 0; i < size; i ++) {
            roleIds.add(access.generateId(applicationId, PrincipalType.ROLE).toLowerCase());
        }

        assertEquals(size, userIds.size());
        assertEquals(size, roleIds.size());
    }

    @Test
    public void testSelectById() {
        UserOS userOS = createUserOS();
        RoleOS roleOS = createRoleOS();
        userAccess.insert(userOS);
        roleAccess.insert(roleOS);

        PrincipalOS actualUserOS = access.selectById(userOS.getApplicationId(), userOS.getId());
        assertOS(userOS, actualUserOS);

        PrincipalOS actualRoleOS = access.selectById(roleOS.getApplicationId(), roleOS.getId());
        assertOS(roleOS, actualRoleOS);
    }

    @Test
    public void testSelectByApplicationIdAndType() {
        String applicationId = getApplicationId();
        List<UserOS> users = createUserOSList(applicationId, 3);
        for (UserOS os: users) {
            userAccess.insert(os);
        }
        List<RoleOS> roles = createRoleOSList(applicationId, 2);
        for (RoleOS os: roles) {
            roleAccess.insert(os);
        }

        PaginatedResult<List<PrincipalOS>> actualUsersResult =
                access.selectByApplicationIdAndType(applicationId, PrincipalType.USER);
        List<PrincipalOS> actualUsers =
                actualUsersResult.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(3, actualUsers.size());
        for (PrincipalOS os: users) {
            for (PrincipalOS actualOS: actualUsers) {
                if (os.getId().equalsIgnoreCase(actualOS.getId())) {
                    assertOS(os, actualOS);
                }
            }
        }

        PaginatedResult<List<PrincipalOS>> actualRolesResult =
                access.selectByApplicationIdAndType(applicationId, PrincipalType.ROLE);
        List<PrincipalOS> actualRoles =
                actualRolesResult.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(2, actualRoles.size());
        for (PrincipalOS os: roles) {
            for (PrincipalOS actualOS: actualRoles) {
                if (os.getId().equalsIgnoreCase(actualOS.getId())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    @Test
    public void testSelectByApplicationId() {
        String applicationId = getApplicationId();
        List<UserOS> users = createUserOSList(applicationId, 3);
        for (UserOS os: users) {
            userAccess.insert(os);
        }
        List<RoleOS> roles = createRoleOSList(applicationId, 2);
        for (RoleOS os: roles) {
            roleAccess.insert(os);
        }

        PaginatedResult<List<PrincipalOS>> actualPrincipalsResult = access.selectByApplicationId(applicationId);
        List<PrincipalOS> actualPrincipals =
                actualPrincipalsResult.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(5, actualPrincipals.size());
        for (PrincipalOS os: users) {
            for (PrincipalOS actualOS: actualPrincipals) {
                if (os.getId().equalsIgnoreCase(actualOS.getId())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    private void assertOS(PrincipalOS expectedOS, PrincipalOS actualOS) {
        assertEquals(expectedOS.getApplicationId(), actualOS.getApplicationId());
        assertEquals(expectedOS.getId(), actualOS.getId());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getType(), actualOS.getType());
    }

    private List<RoleOS> createRoleOSList(String applicationId, int size) {
        List<RoleOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            osList.add(createRoleOS(applicationId));
        }
        return osList;
    }

    private RoleOS createRoleOS() {
        String applicationId = getApplicationId();
        String principalId = getPrincipalId(applicationId, PrincipalType.ROLE);
        String name = generateRoleName();
        RoleOS os = new RoleOS(
                applicationId,
                principalId,
                new Date(),
                name);
        generatedObjectSegments.add(os);
        return os;
    }

    private RoleOS createRoleOS(String applicationId) {
        String principalId = getPrincipalId(applicationId, PrincipalType.ROLE);
        String name = generateRoleName();
        RoleOS os = new RoleOS(
                applicationId,
                principalId,
                new Date(),
                name);
        generatedObjectSegments.add(os);
        return os;
    }

    private List<UserOS> createUserOSList(String applicationId, int size) {
        List<UserOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String principalId = getPrincipalId(applicationId, PrincipalType.USER);
            osList.add(createUserOS(applicationId, principalId));
        }
        return osList;
    }

    private UserOS createUserOS() {
        String applicationId = getApplicationId();
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);
        UserOS os = new UserOS(
                applicationId,
                principalId,
                new Date(),
                null,
                null,
                Collections.emptyList());
        generatedObjectSegments.add(os);
        return os;
    }

    private UserOS createUserOS(String applicationId, String principalId) {
        UserOS os = new UserOS(
                applicationId,
                principalId,
                new Date(),
                null,
                null,
                Collections.emptyList());
        generatedObjectSegments.add(os);
        return os;
    }

    private String generateRoleName() {
        return "role-" + new Random().nextInt(10000);
    }

    protected abstract PrincipalAccess getAccess();
    protected abstract UserAccess getUserAccess();
    protected abstract RoleAccess getRoleAccess();
    protected abstract String getApplicationId();
    protected abstract String getPrincipalId(String applicationId, PrincipalType type);

}
