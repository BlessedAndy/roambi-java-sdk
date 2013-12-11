/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.client;

import com.mellmo.roambi.api.RoambiApiApplication;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.Account;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/12/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoambiClientWrapper implements RoambiApiApplication {

    static private Logger logger = Logger.getLogger(RoambiClientWrapper.class);

    protected String serviceUrl;
    protected int apiVersion=1;
    protected String consumerKey;
    protected String consumerSecret;
    protected String redirectUri;
    protected String username;
    protected String password;
    protected String account;

    public RoambiApiClient roambiApiClient;

    public RoambiClientWrapper(String propsPath) {

        try {
            Properties props = new Properties();
            props.load(new FileInputStream(propsPath));

            serviceUrl = props.getProperty("server.url");
            consumerKey = props.getProperty("consumer.key");
            consumerSecret = props.getProperty("consumer.secret");
            redirectUri = props.getProperty("redirect.uri");
            username = props.getProperty("username");
            password = props.getProperty("password");
            account = props.getProperty("account");

            roambiApiClient = new RoambiApiClient(serviceUrl, apiVersion, consumerKey, consumerSecret, redirectUri, this);
            if(account == null)
            {
                List<Account> accounts = roambiApiClient.getUserAccounts();
                if(accounts.size() > 0) {
                    //just get the first one
                    account = accounts.get(0).getUid();
                }
            }
            roambiApiClient.setCurrentAccount(account);

        } catch (IOException e) {
            logger.error("Could not load client properties.");
        } catch (ApiException e) {
            logger.error("Could not get user account.");
        }
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public RoambiApiClient getClient() {
        return roambiApiClient;
    }

}
