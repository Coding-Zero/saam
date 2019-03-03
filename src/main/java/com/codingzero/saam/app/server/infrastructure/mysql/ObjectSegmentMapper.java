package com.codingzero.saam.app.server.infrastructure.mysql;

import com.codingzero.saam.common.Action;
import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.IdentifierKey;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.IdentifierVerificationCode;
import com.codingzero.saam.common.OAuthIdentifierKey;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.saam.common.PasswordResetCode;
import com.codingzero.saam.common.PrincipalId;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.common.UsernameFormat;
import com.codingzero.saam.common.mixin.ActionModel;
import com.codingzero.saam.common.mixin.IdentifierVerificationCodeModel;
import com.codingzero.saam.common.mixin.PasswordPolicyModel;
import com.codingzero.saam.common.mixin.PasswordResetCodeModel;
import com.codingzero.saam.infrastructure.data.APIKeyOS;
import com.codingzero.saam.infrastructure.data.ApplicationOS;
import com.codingzero.saam.infrastructure.data.EmailPolicyOS;
import com.codingzero.saam.infrastructure.data.IdentifierOS;
import com.codingzero.saam.infrastructure.data.IdentifierPolicyOS;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierOS;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierPolicyOS;
import com.codingzero.saam.infrastructure.data.PermissionOS;
import com.codingzero.saam.infrastructure.data.PrincipalOS;
import com.codingzero.saam.infrastructure.data.ResourceOS;
import com.codingzero.saam.infrastructure.data.RoleOS;
import com.codingzero.saam.infrastructure.data.UserOS;
import com.codingzero.saam.infrastructure.data.UserSessionOS;
import com.codingzero.saam.infrastructure.data.UsernamePolicyOS;
import com.codingzero.utilities.key.Key;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class ObjectSegmentMapper {

    private ObjectMapper objectMapper;

    public ObjectSegmentMapper() {
        this.objectMapper = initObjectMapper();
    }

    private ObjectMapper initObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.addMixIn(IdentifierVerificationCode.class, IdentifierVerificationCodeModel.class);
        mapper.addMixIn(PasswordPolicy.class, PasswordPolicyModel.class);
        mapper.addMixIn(PasswordResetCode.class, PasswordResetCodeModel.class);
        mapper.addMixIn(Action.class, ActionModel.class);
        return mapper;
    }

    public ApplicationOS toApplicationOS(ResultSet rs) throws SQLException, IOException {
        PasswordPolicy passwordPolicy = objectMapper.readValue(
                rs.getString("password_policy"), PasswordPolicy.class);
        return new ApplicationOS (
                Key.fromBytes(rs.getBytes("id")).toHexString(),
                rs.getString("name"),
                rs.getString("description"),
                new Date(rs.getTimestamp("creation_time").getTime()),
                passwordPolicy,
                ApplicationStatus.valueOf(rs.getString("status"))
        );
    }

    public PrincipalOS toPrincipalOS(ResultSet rs) throws SQLException, IOException {
        return new PrincipalOS (
                new PrincipalId(
                        Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                        Key.fromBytes(rs.getBytes("id")).toHexString()),
                PrincipalType.valueOf(rs.getString("type")),
                new Date(rs.getTimestamp("creation_time").getTime())
        );
    }

    public UserOS toUserOS(ResultSet rs) throws SQLException, IOException {
        PasswordResetCode passwordResetCode = objectMapper.readValue(
                rs.getString("password_reset_code"), PasswordResetCode.class);
        List<String> roleIds = objectMapper.readValue(
                rs.getString("role_ids"), new TypeReference<List<String>>() {});
        return new UserOS(
                new PrincipalId(
                        Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                        Key.fromBytes(rs.getBytes("id")).toHexString()),
                new Date(rs.getTimestamp("creation_time").getTime()),
                rs.getString("password"),
                passwordResetCode,
                roleIds
        );
    }

    public UserOS toUserOS(PrincipalOS principalOS, ResultSet rs) throws SQLException, IOException {
        PasswordResetCode passwordResetCode = objectMapper.readValue(
                rs.getString("password_reset_code"), PasswordResetCode.class);
        List<String> roleIds = objectMapper.readValue(
                rs.getString("role_ids"), new TypeReference<List<String>>() {});
        return new UserOS(
                principalOS.getId(),
                principalOS.getCreationTime(),
                rs.getString("password"),
                passwordResetCode,
                roleIds
                );
    }

    public RoleOS toRoleOS(ResultSet rs) throws SQLException, IOException {
        return new RoleOS(
                new PrincipalId(
                        Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                        Key.fromBytes(rs.getBytes("id")).toHexString()),
                new Date(rs.getTimestamp("creation_time").getTime()),
                rs.getString("name")
        );
    }

    public RoleOS toRoleOS(PrincipalOS principalOS, ResultSet rs) throws SQLException, IOException {
        return new RoleOS(
                principalOS.getId(),
                principalOS.getCreationTime(),
                rs.getString("name")
        );
    }

    public APIKeyOS toAPIKeyOS(ResultSet rs) throws SQLException, IOException {
        return new APIKeyOS(
                new PrincipalId(
                        Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                        Key.fromBytes(rs.getBytes("id")).toHexString()),
                new Date(rs.getTimestamp("creation_time").getTime()),
                rs.getString("secret_key"),
                rs.getString("name"),
                Key.fromBytes(rs.getBytes("user_id")).toHexString(),
                rs.getBoolean("is_active")
        );
    }

    public APIKeyOS toAPIKeyOS(PrincipalOS principalOS, ResultSet rs) throws SQLException {
        return new APIKeyOS(
                principalOS.getId(),
                principalOS.getCreationTime(),
                rs.getString("secret_key"),
                rs.getString("name"),
                Key.fromBytes(rs.getBytes("user_id")).toHexString(),
                rs.getBoolean("is_active")
        );
    }

    public APIKeyOS toAPIKeyOSForV1(PrincipalOS principalOS, ResultSet rs) throws SQLException {
        return new APIKeyOS(
                principalOS.getId(),
                principalOS.getCreationTime(),
                rs.getString("key"),
                rs.getString("name"),
                Key.fromBytes(rs.getBytes("user_id")).toHexString(),
                rs.getBoolean("is_active")
        );
    }

    public ResourceOS toResourceOS(ResultSet rs) throws SQLException, IOException {
        return new ResourceOS(
                Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                rs.getString("key"),
                rs.getString(Key.fromBytes(rs.getBytes("parent_key_hash")).toHexString()),
                Key.fromBytes(rs.getBytes("principal_id")).toHexString(),
                new Date(rs.getTimestamp("creation_time").getTime())
        );
    }

    public IdentifierPolicyOS toIdentifierPolicyOS(ResultSet rs) throws SQLException, IOException {
        return new IdentifierPolicyOS(
                Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                IdentifierType.valueOf(rs.getString("type").toUpperCase()),
                rs.getBoolean("is_verification_required"),
                rs.getInt("min_length"),
                rs.getInt("max_length"),
                rs.getBoolean("is_active"),
                new Date(rs.getTimestamp("creation_time").getTime()),
                new Date(rs.getTimestamp("update_time").getTime())
                );
    }

    public IdentifierPolicyOS toIdentifierPolicyOSV1(ResultSet rs) throws SQLException {
        return new IdentifierPolicyOS(
                Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                IdentifierType.valueOf(rs.getString("type").toUpperCase()),
                rs.getBoolean("is_need_to_verify"),
                rs.getInt("min_length"),
                rs.getInt("max_length"),
                rs.getBoolean("is_active"),
                new Date(rs.getTimestamp("creation_time").getTime()),
                new Date(rs.getTimestamp("creation_time").getTime())
        );
    }

    public EmailPolicyOS toEmailPolicyOS(IdentifierPolicyOS identifierPolicyOS, ResultSet rs)
            throws SQLException, IOException {
        List<String> domains = objectMapper.readValue(
                rs.getString("domains"), new TypeReference<List<String>>() {});
        return new EmailPolicyOS(
                identifierPolicyOS.getApplicationId(),
                identifierPolicyOS.isVerificationRequired(),
                identifierPolicyOS.getMinLength(),
                identifierPolicyOS.getMaxLength(),
                identifierPolicyOS.isActive(),
                identifierPolicyOS.getCreationTime(),
                identifierPolicyOS.getUpdateTime(),
                domains);
    }

    public EmailPolicyOS toEmailPolicyOS(ResultSet rs) throws SQLException, IOException {
        List<String> domains = objectMapper.readValue(
                rs.getString("domains"), new TypeReference<List<String>>() {});
        return new EmailPolicyOS(
                Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                rs.getBoolean("is_verification_required"),
                rs.getInt("min_length"),
                rs.getInt("max_length"),
                rs.getBoolean("is_active"),
                new Date(rs.getTimestamp("creation_time").getTime()),
                new Date(rs.getTimestamp("update_time").getTime()),
                domains);
    }

    public EmailPolicyOS toEmailPolicyOSV1(ResultSet rs) throws SQLException, IOException {
        List<String> domains = objectMapper.readValue(
                rs.getString("domains"), new TypeReference<List<String>>() {});
        return new EmailPolicyOS(
                Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                rs.getBoolean("is_need_to_verify"),
                rs.getInt("min_length"),
                rs.getInt("max_length"),
                rs.getBoolean("is_active"),
                new Date(rs.getTimestamp("creation_time").getTime()),
                new Date(rs.getTimestamp("creation_time").getTime()),
                domains);
    }

    public UsernamePolicyOS toUsernamePolicyOS(IdentifierPolicyOS identifierPolicyOS, ResultSet rs)
            throws SQLException, IOException {
        return new UsernamePolicyOS(
                identifierPolicyOS.getApplicationId(),
                identifierPolicyOS.getMinLength(),
                identifierPolicyOS.getMaxLength(),
                identifierPolicyOS.isActive(),
                identifierPolicyOS.getCreationTime(),
                identifierPolicyOS.getUpdateTime(),
                UsernameFormat.valueOf(rs.getString("format"))
        );
    }

    public UsernamePolicyOS toUsernamePolicyOS(ResultSet rs) throws SQLException, IOException {
        return new UsernamePolicyOS(
                Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                rs.getInt("min_length"),
                rs.getInt("max_length"),
                rs.getBoolean("is_active"),
                new Date(rs.getTimestamp("creation_time").getTime()),
                new Date(rs.getTimestamp("update_time").getTime()),
                UsernameFormat.valueOf(rs.getString("format"))
        );
    }

    public IdentifierOS toIdentifierOS(ResultSet rs) throws SQLException, IOException {
        IdentifierVerificationCode verificationCode = objectMapper.readValue(
                rs.getString("verification_code"), IdentifierVerificationCode.class);
        return new IdentifierOS(
                new IdentifierKey(
                        Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                        rs.getString("content")
                ),
                IdentifierType.valueOf(rs.getString("identifier_type")),
                Key.fromBytes(rs.getBytes("user_id")).toHexString(),
                rs.getBoolean("is_verified"),
                verificationCode,
                new Date(rs.getTimestamp("creation_time").getTime()),
                new Date(rs.getTimestamp("update_time").getTime())
        );
    }

    public IdentifierOS toIdentifierOSV1(ResultSet rs) throws SQLException, IOException {
        IdentifierVerificationCode verificationCode = objectMapper.readValue(
                rs.getString("verification_code"), IdentifierVerificationCode.class);
        return new IdentifierOS(
                new IdentifierKey(
                        Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                        rs.getString("content")
                ),
                IdentifierType.valueOf(rs.getString("type")),
                Key.fromBytes(rs.getBytes("user_id")).toHexString(),
                rs.getBoolean("is_verified"),
                verificationCode,
                new Date(rs.getTimestamp("creation_time").getTime()),
                new Date(rs.getTimestamp("creation_time").getTime())
        );
    }

    public PermissionOS toPermissionOS(ResultSet rs) throws SQLException, IOException {
        List<Action> actions;
        if (rs.getString("actions") == null) {
            List<String> actionCodes = objectMapper.readValue(
                    rs.getString("action_codes"), new TypeReference<List<String>>() {});
            actions = new ArrayList<>(actionCodes.size());
            for (String code: actionCodes) {
                actions.add(new Action(code, true));
            }
        } else {
            actions = objectMapper.readValue(
                    rs.getString("actions"), new TypeReference<List<Action>>() {});
        }
        return new PermissionOS(
                Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                rs.getString("resource_key"),
                Key.fromBytes(rs.getBytes("principal_id")).toHexString(),
                new Date(rs.getTimestamp("creation_time").getTime()),
                actions
        );
    }

    public OAuthIdentifierPolicyOS toOAuthIdentifierPolicyOS(ResultSet rs) throws SQLException, IOException {
        Map<String, Object> configurations = objectMapper.readValue(
                rs.getString("configurations"), new TypeReference<Map<String, Object>>() {});
        return new OAuthIdentifierPolicyOS(
                Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                OAuthPlatform.valueOf(rs.getString("platform")),
                configurations,
                rs.getBoolean("is_active"),
                new Date(rs.getTimestamp("creation_time").getTime()),
                new Date(rs.getTimestamp("update_time").getTime())
        );
    }

    public OAuthIdentifierOS toOAuthIdentifierOS(ResultSet rs) throws SQLException, IOException {
        Map<String, Object> properties = objectMapper.readValue(
                rs.getString("properties"), new TypeReference<Map<String, Object>>() {});
        return new OAuthIdentifierOS(
                new OAuthIdentifierKey(
                        Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                        OAuthPlatform.valueOf(rs.getString("platform")),
                        rs.getString("content")
                ),
                Key.fromBytes(rs.getBytes("user_id")).toHexString(),
                properties,
                new Date(rs.getTimestamp("creation_time").getTime()),
                new Date(rs.getTimestamp("update_time").getTime())
        );
    }

    public OAuthIdentifierOS toOAuthIdentifierOSV1(ResultSet rs) throws SQLException, IOException {
        Map<String, Object> properties = objectMapper.readValue(
                rs.getString("properties"), new TypeReference<Map<String, Object>>() {});
        return new OAuthIdentifierOS(
                new OAuthIdentifierKey(
                        Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                        OAuthPlatform.valueOf(rs.getString("platform")),
                        rs.getString("content")
                ),
                Key.fromBytes(rs.getBytes("user_id")).toHexString(),
                properties,
                new Date(rs.getTimestamp("creation_time").getTime()),
                new Date(rs.getTimestamp("creation_time").getTime())
        );
    }

    public UserSessionOS toUserSessionOS(ResultSet rs) throws SQLException, IOException {
        Map<String, Object> details = objectMapper.readValue(
                rs.getString("details"), new TypeReference<Map<String, Object>>() {});
        return new UserSessionOS(
                Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                rs.getString("key"),
                Key.fromBytes(rs.getBytes("user_id")).toHexString(),
                new Date(rs.getTimestamp("expiration_time").getTime()),
                new Date(rs.getTimestamp("creation_time").getTime()),
                details
        );
    }

    public UserSessionOS toUserSessionOSV1(ResultSet rs) throws SQLException, IOException {
        Map<String, Object> details = objectMapper.readValue(
                rs.getString("metadata"), new TypeReference<Map<String, Object>>() {});
        return new UserSessionOS(
                Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                rs.getString("key"),
                Key.fromBytes(rs.getBytes("user_id")).toHexString(),
                new Date(rs.getTimestamp("expiration_time").getTime()),
                new Date(rs.getTimestamp("creation_time").getTime()),
                details
        );
    }

    public String toJson(Object source) throws JsonProcessingException {
        return objectMapper.writeValueAsString(source);
    }

}
