package com.label305.kama;

import android.content.Context;

import com.label305.kama.exceptions.KamaException;
import com.label305.kama.http.HttpHelper;
import com.label305.kama.http.PostExecutor;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class KamaPoster<ReturnType> extends AbstractKamaRequester<ReturnType> {

    private final PostExecutor mPostExecutor;

    private AbstractHttpEntity mPostData;

    public KamaPoster(final Context context, final String apiKey) {
        super(context, apiKey);
        mPostExecutor = new HttpHelper();
    }

    public KamaPoster(final Context context, final String apiKey, final PostExecutor postExecutor) {
        super(context, apiKey);
        mPostExecutor = postExecutor;
    }

    public KamaPoster(final Class<ReturnType> clzz, final Context context, final String apiKey) {
        super(clzz, context, apiKey);
        mPostExecutor = new HttpHelper();
    }

    public KamaPoster(final Class<ReturnType> clzz, final Context context, final String apiKey, final PostExecutor postExecutor) {
        super(clzz, context, apiKey);
        mPostExecutor = postExecutor;
    }

    public void setPostData(final Map<String, Object> postData) throws KamaException {
        if (postData != null) {
            try {
                mPostData = new UrlEncodedFormEntity(HttpHelper.convert(postData));
            } catch (UnsupportedEncodingException e) {
                throw new KamaException(e);
            }
        }
    }

    public void setPostData(final String jsonData) throws KamaException {
        if (jsonData != null) {
            try {
                mPostData = new StringEntity(jsonData, HTTP.UTF_8);
            } catch (UnsupportedEncodingException e) {
                throw new KamaException(e);
            }
        }
    }

    public void setPostData(final AbstractHttpEntity postData) {
        mPostData = postData;
    }

    @Override
    protected HttpResponse executeRequest() throws KamaException {
        if (getUrl() == null) {
            throw new IllegalArgumentException("Provide an url!");
        }

        String finalUrl = addUrlParams(getUrl(), getUrlData());
        Map<String, Object> finalHeaderData = addNecessaryHeaders(getHeaderData());

        try {
            return mPostExecutor.post(finalUrl, finalHeaderData, mPostData);
        } catch (IOException e) {
            throw new KamaException(e);
        }
    }

}