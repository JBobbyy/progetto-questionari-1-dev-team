package it.unimib.unimibmodules.service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;

import java.util.HashMap;

import it.unimib.unimibmodules.controller.AWSToken;
import org.springframework.stereotype.Component;

/**
 * Service class used to get an User's token from AWS Cognito.
 * @author Khalil Mohamed
 * @author Luca Milazzo
 * @version 0.3.0
 */
@Component("awsToken")
public class AWSTokenImpl implements AWSToken {
    public static final Region REGION = Region.getRegion(Regions.EU_CENTRAL_1);
    public static final String ACCESS_KEY_ID = "ACCESS_KEY_ID_COGNITO";
    public static final String ACCESS_KEY_VALUE = "SECRET_ACESS_KEY_COGNITO";
    public static final String IDENTITY_POOL_ID = "eu-central-1:581b95ad-2144-4e38-b112-028abe2bac0a";
    public static final String LOGIN_PROVIDER = "login.progettoquestionari.dev";
    public static final String BUCKET_NAME = "questionari-images";
    /**
     * Get the User's token from AWS Cognito
     * @param	idUser the id of the logged user.
     * @return				GetOpenIdTokenForDeveloperIdentityResult instance
     */
    @Override
    public GetOpenIdTokenForDeveloperIdentityResult getToken(int idUser){
        AmazonCognitoIdentity identityClient = new AmazonCognitoIdentityClient(
                new BasicAWSCredentials(ACCESS_KEY_ID, ACCESS_KEY_VALUE)
        );
        identityClient.setRegion(REGION);
        GetOpenIdTokenForDeveloperIdentityRequest request =
                new GetOpenIdTokenForDeveloperIdentityRequest();
        request.setIdentityPoolId(IDENTITY_POOL_ID);
        HashMap<String,String> logins = new HashMap<>();
        logins.put(LOGIN_PROVIDER, ""+idUser);
        request.setLogins(logins);
        request.setTokenDuration(60 * 5L);
        GetOpenIdTokenForDeveloperIdentityResult response =
                identityClient.getOpenIdTokenForDeveloperIdentity(request);
        return response;
    }

}