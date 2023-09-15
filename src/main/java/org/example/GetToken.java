package org.example;

import com.dropbox.core.*;
import com.dropbox.core.oauth.DbxCredential;

import java.io.IOException;


//ToDo
// link for getting tokens
// https://www.dropbox.com/oauth2/authorize?client_id=< appKey  >&response_type=code&token_access_type=offline
public class GetToken {
    private static final String TOKEN_FILE = "";
    private static final String APP_NAME = "";
    private static final String ACCESS_CODE = "";
    private static final String APP_KEY = "";
    private static final String APP_SECRET = "";

    public static void main(String[] args) throws DbxException, IOException {
        DbxRequestConfig config = new DbxRequestConfig(APP_NAME);
        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);
        DbxAuthFinish authFinish = webAuth.finishFromCode(ACCESS_CODE);
        DbxCredential dbxCredential = new DbxCredential(authFinish.getAccessToken(), 0L, authFinish.getRefreshToken(), APP_KEY, APP_SECRET);
        DbxCredential.Writer.writeToFile(dbxCredential, TOKEN_FILE);
    }
}