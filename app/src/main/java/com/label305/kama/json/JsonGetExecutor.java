package com.label305.kama.json;

import com.label305.kama.exceptions.KamaException;
import com.label305.stan.http.GetExecutor;
import com.label305.stan.http.HttpHelper;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

public class JsonGetExecutor extends JsonRequestExecutor {

    private final GetExecutor mGetExecutor;

    public JsonGetExecutor() {
        mGetExecutor = new HttpHelper();
    }

    public JsonGetExecutor(final GetExecutor getExecutor) {
        mGetExecutor = getExecutor;
    }


    @Override
    protected HttpResponse executeRequest() throws KamaException {
        if (getUrl() == null) {
            throw new IllegalArgumentException("Provide an url!");
        }

        if (getReturnTypeClass() == null) {
            throw new IllegalArgumentException("Provide a return type class!");
        }

        String finalUrl = addUrlParams(getUrl(), getUrlData());
        Map<String, Object> finalHeaderData = addNecessaryHeaders(getHeaderData());

        try {
            return mGetExecutor.get(finalUrl, finalHeaderData);
        } catch (IOException e) {
            throw new KamaException(e);
        }
    }
}
