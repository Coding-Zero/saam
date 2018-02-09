package com.codingzero.saam.presentation;

import com.codingzero.saam.app.APIKeyAddRequest;
import com.codingzero.saam.app.APIKeyUpdateRequest;
import com.codingzero.saam.app.ApplicationAddRequest;
import com.codingzero.saam.app.ApplicationUpdateRequest;
import com.codingzero.saam.app.CredentialLoginRequest;
import com.codingzero.saam.app.EmailPolicyAddRequest;
import com.codingzero.saam.app.EmailPolicyUpdateRequest;
import com.codingzero.saam.app.IdentifierAddRequest;
import com.codingzero.saam.app.IdentifierRemoveRequest;
import com.codingzero.saam.app.IdentifierUpdateRequest;
import com.codingzero.saam.app.IdentifierVerificationCodeGenerateRequest;
import com.codingzero.saam.app.IdentifierVerifyRequest;
import com.codingzero.saam.app.OAuthAccessTokenRequest;
import com.codingzero.saam.app.OAuthAuthorizationUrlRequest;
import com.codingzero.saam.app.OAuthIdentifierConnectRequest;
import com.codingzero.saam.app.OAuthIdentifierPolicyAddRequest;
import com.codingzero.saam.app.OAuthIdentifierPolicyUpdateRequest;
import com.codingzero.saam.app.OAuthLoginRequest;
import com.codingzero.saam.app.PasswordChangeRequest;
import com.codingzero.saam.app.PasswordPolicyUpdateRequest;
import com.codingzero.saam.app.PasswordResetCodeGenerateRequest;
import com.codingzero.saam.app.PasswordResetRequest;
import com.codingzero.saam.app.PermissionStoreRequest;
import com.codingzero.saam.app.ResourceStoreRequest;
import com.codingzero.saam.app.RoleAddRequest;
import com.codingzero.saam.app.RoleUpdateRequest;
import com.codingzero.saam.app.UserRegisterRequest;
import com.codingzero.saam.app.UserRoleUpdateRequest;
import com.codingzero.saam.app.UserSessionCreateRequest;
import com.codingzero.saam.app.UsernamePolicyAddRequest;
import com.codingzero.saam.app.UsernamePolicyUpdateRequest;
import com.codingzero.saam.common.Action;
import com.codingzero.saam.common.IdentifierVerificationCode;
import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.saam.common.PasswordResetCode;
import com.codingzero.saam.common.mixin.ActionModel;
import com.codingzero.saam.common.mixin.IdentifierVerificationCodeModel;
import com.codingzero.saam.common.mixin.PasswordPolicyModel;
import com.codingzero.saam.common.mixin.PasswordResetCodeModel;
import com.codingzero.saam.presentation.mixin.APIKeyAddRequestModel;
import com.codingzero.saam.presentation.mixin.APIKeyUpdateRequestModel;
import com.codingzero.saam.presentation.mixin.ApplicationAddRequestModel;
import com.codingzero.saam.presentation.mixin.ApplicationUpdateRequestModel;
import com.codingzero.saam.presentation.mixin.CredentialLoginRequestModel;
import com.codingzero.saam.presentation.mixin.EmailPolicyAddRequestModel;
import com.codingzero.saam.presentation.mixin.EmailPolicyUpdateRequestModel;
import com.codingzero.saam.presentation.mixin.IdentifierAssignRequestModel;
import com.codingzero.saam.presentation.mixin.IdentifierRemoveRequestModel;
import com.codingzero.saam.presentation.mixin.IdentifierUpdateRequestModel;
import com.codingzero.saam.presentation.mixin.IdentifierVerificationCodeGenerateRequestModel;
import com.codingzero.saam.presentation.mixin.IdentifierVerifyRequestModel;
import com.codingzero.saam.presentation.mixin.OAuthAccessTokenRequestModel;
import com.codingzero.saam.presentation.mixin.OAuthAuthorizationUrlRequestModel;
import com.codingzero.saam.presentation.mixin.OAuthIdentifierConnectRequestModel;
import com.codingzero.saam.presentation.mixin.OAuthIdentifierPolicyAddRequestModel;
import com.codingzero.saam.presentation.mixin.OAuthIdentifierPolicyUpdateRequestModel;
import com.codingzero.saam.presentation.mixin.OAuthLoginRequestModel;
import com.codingzero.saam.presentation.mixin.PasswordChangeRequestModel;
import com.codingzero.saam.presentation.mixin.PasswordPolicyUpdateRequestModel;
import com.codingzero.saam.presentation.mixin.PasswordResetCodeGenerateRequestModel;
import com.codingzero.saam.presentation.mixin.PasswordResetRequestModel;
import com.codingzero.saam.presentation.mixin.PermissionStoreRequestModel;
import com.codingzero.saam.presentation.mixin.ResourceAddRequestModel;
import com.codingzero.saam.presentation.mixin.RoleAddRequestModel;
import com.codingzero.saam.presentation.mixin.RoleUpdateRequestModel;
import com.codingzero.saam.presentation.mixin.UserRegisterRequestModel;
import com.codingzero.saam.presentation.mixin.UserRegisterRequestOAuthIdentifierModel;
import com.codingzero.saam.presentation.mixin.UserRoleUpdateRequestModel;
import com.codingzero.saam.presentation.mixin.UserSessionCreateRequestModel;
import com.codingzero.saam.presentation.mixin.UsernamePolicyAddRequestModel;
import com.codingzero.saam.presentation.mixin.UsernamePolicyUpdateRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MixinRegister {

    public void register(ObjectMapper mapper) {
        mapper.configure(SerializationFeature.
                WRITE_DATES_AS_TIMESTAMPS , false);
        mapper.addMixIn(IdentifierVerificationCode.class, IdentifierVerificationCodeModel.class);
        mapper.addMixIn(PasswordPolicy.class, PasswordPolicyModel.class);
        mapper.addMixIn(PasswordResetCode.class, PasswordResetCodeModel.class);
        mapper.addMixIn(ApplicationAddRequest.class, ApplicationAddRequestModel.class);
        mapper.addMixIn(ApplicationUpdateRequest.class, ApplicationUpdateRequestModel.class);
        mapper.addMixIn(PasswordPolicyUpdateRequest.class, PasswordPolicyUpdateRequestModel.class);
        mapper.addMixIn(OAuthAuthorizationUrlRequest.class, OAuthAuthorizationUrlRequestModel.class);
        mapper.addMixIn(OAuthAccessTokenRequest.class, OAuthAccessTokenRequestModel.class);
        mapper.addMixIn(UsernamePolicyAddRequest.class, UsernamePolicyAddRequestModel.class);
        mapper.addMixIn(UsernamePolicyUpdateRequest.class, UsernamePolicyUpdateRequestModel.class);
        mapper.addMixIn(EmailPolicyAddRequest.class, EmailPolicyAddRequestModel.class);
        mapper.addMixIn(EmailPolicyUpdateRequest.class, EmailPolicyUpdateRequestModel.class);
        mapper.addMixIn(OAuthIdentifierPolicyAddRequest.class, OAuthIdentifierPolicyAddRequestModel.class);
        mapper.addMixIn(OAuthIdentifierPolicyUpdateRequest.class, OAuthIdentifierPolicyUpdateRequestModel.class);
        mapper.addMixIn(UserRegisterRequest.class, UserRegisterRequestModel.class);
        mapper.addMixIn(UserRegisterRequest.OAuthIdentifier.class, UserRegisterRequestOAuthIdentifierModel.class);
        mapper.addMixIn(IdentifierAddRequest.class, IdentifierAssignRequestModel.class);
        mapper.addMixIn(IdentifierUpdateRequest.class, IdentifierUpdateRequestModel.class);
        mapper.addMixIn(IdentifierRemoveRequest.class, IdentifierRemoveRequestModel.class);
        mapper.addMixIn(
                IdentifierVerificationCodeGenerateRequest.class, IdentifierVerificationCodeGenerateRequestModel.class);
        mapper.addMixIn(IdentifierVerifyRequest.class, IdentifierVerifyRequestModel.class);
        mapper.addMixIn(UserRoleUpdateRequest.class, UserRoleUpdateRequestModel.class);
        mapper.addMixIn(PasswordChangeRequest.class, PasswordChangeRequestModel.class);
        mapper.addMixIn(PasswordResetCodeGenerateRequest.class, PasswordResetCodeGenerateRequestModel.class);
        mapper.addMixIn(PasswordResetRequest.class, PasswordResetRequestModel.class);
        mapper.addMixIn(OAuthIdentifierConnectRequest.class, OAuthIdentifierConnectRequestModel.class);
        mapper.addMixIn(APIKeyAddRequest.class, APIKeyAddRequestModel.class);
        mapper.addMixIn(APIKeyUpdateRequest.class, APIKeyUpdateRequestModel.class);
        mapper.addMixIn(CredentialLoginRequest.class, CredentialLoginRequestModel.class);
        mapper.addMixIn(OAuthLoginRequest.class, OAuthLoginRequestModel.class);
        mapper.addMixIn(RoleAddRequest.class, RoleAddRequestModel.class);
        mapper.addMixIn(RoleUpdateRequest.class, RoleUpdateRequestModel.class);
        mapper.addMixIn(ResourceStoreRequest.class, ResourceAddRequestModel.class);
        mapper.addMixIn(PermissionStoreRequest.class, PermissionStoreRequestModel.class);
        mapper.addMixIn(UserSessionCreateRequest.class, UserSessionCreateRequestModel.class);
        mapper.addMixIn(Action.class, ActionModel.class);
    }
}
