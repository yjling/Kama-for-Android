package com.label305.kama.json.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.label305.kama.KamaParam;
import com.label305.kama.exceptions.KamaException;
import com.label305.kama.json.JsonDeleteExecutor;
import com.label305.stan.http.DeleteExecutor;
import com.label305.stan.http.StatusCodes;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked", "rawtypes", "DuplicateStringLiteralInspection"})
public class JsonDeleteExecutorTest extends TestCase {

    private static final String MISSING_EXCEPTION = "Missing exception";

    private static final String URL = "URL";
    private static final String JSON_SINGLE = "{ \"integer\":4}";
    private static final String JSON_LIST = "[{\"integer\":4}]";
    private static final String JSON_LIST_TITLE = "{\"list\":[{\"integer\":4}]}";
    private static final String TITLE = "list";

    private static final Class<ParseObject> RETURN_TYPE = ParseObject.class;


    private JsonDeleteExecutor mJsonDeleteExecutor;

    @Mock
    private DeleteExecutor mDeleteExecutor;

    @Mock
    private HttpResponse mHttpResponse;

    @Mock
    private HttpEntity mHttpEntity;

    @Mock
    private StatusLine mStatusLine;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        MockitoAnnotations.initMocks(this);

        mJsonDeleteExecutor = new JsonDeleteExecutor(mDeleteExecutor);

        when(mDeleteExecutor.delete(anyString(), any(Map.class), any(HttpEntity.class))).thenReturn(mHttpResponse);
        when(mHttpResponse.getEntity()).thenReturn(mHttpEntity);
    }

    public void testDeleteObject() throws Exception {
        mJsonDeleteExecutor.setUrl(URL);
        mJsonDeleteExecutor.setReturnTypeClass(RETURN_TYPE);

        when(mHttpResponse.getStatusLine()).thenReturn(mStatusLine);
        when(mStatusLine.getStatusCode()).thenReturn(StatusCodes.HTTP_OK);
        when(mHttpEntity.getContent()).thenReturn(IOUtils.toInputStream(JSON_SINGLE));

        Object result = mJsonDeleteExecutor.execute();

        verify(mDeleteExecutor).delete(eq(URL), any(Map.class), any(HttpEntity.class));

        assertThat(result, is(not(nullValue())));
        assertThat(result, is(instanceOf(RETURN_TYPE)));
        assertThat(((ParseObject) result).mInteger, is(4));
    }

    public void testDeleteObjectsList() throws Exception {
        mJsonDeleteExecutor.setUrl(URL);
        mJsonDeleteExecutor.setReturnTypeClass(RETURN_TYPE);

        when(mHttpResponse.getStatusLine()).thenReturn(mStatusLine);
        when(mStatusLine.getStatusCode()).thenReturn(StatusCodes.HTTP_OK);
        when(mHttpEntity.getContent()).thenReturn(IOUtils.toInputStream(JSON_LIST));

        Object result = mJsonDeleteExecutor.executeReturnsObjectsList();
        verify(mDeleteExecutor).delete(eq(URL), any(Map.class), any(HttpEntity.class));

        assertThat(result, is(not(nullValue())));
        assertThat(result, is(instanceOf(List.class)));

        assertThat(((Collection<?>) result).size(), is(1));
        assertThat(((List<?>) result).get(0), is(instanceOf(RETURN_TYPE)));
    }

    public void testDeleteObjectsListWithTitle() throws Exception {
        mJsonDeleteExecutor.setUrl(URL);
        mJsonDeleteExecutor.setReturnTypeClass(RETURN_TYPE);
        mJsonDeleteExecutor.setJsonTitle(TITLE);

        when(mHttpResponse.getStatusLine()).thenReturn(mStatusLine);
        when(mStatusLine.getStatusCode()).thenReturn(StatusCodes.HTTP_OK);
        when(mHttpEntity.getContent()).thenReturn(IOUtils.toInputStream(JSON_LIST_TITLE));

        Object result = mJsonDeleteExecutor.executeReturnsObjectsList();
        verify(mDeleteExecutor).delete(eq(URL), any(Map.class), any(HttpEntity.class));

        assertThat(result, is(not(nullValue())));
        assertThat(result, is(instanceOf(List.class)));

        List<?> list = (List<?>) result;
        assertThat(list.size(), is(1));
        assertThat(list.get(0), is(instanceOf(RETURN_TYPE)));
    }

    public void testExecuteAddsHeader() throws Exception {
        mJsonDeleteExecutor.setUrl(URL);
        mJsonDeleteExecutor.setReturnTypeClass(RETURN_TYPE);
        mJsonDeleteExecutor.setJsonTitle(TITLE);

        when(mHttpResponse.getStatusLine()).thenReturn(mStatusLine);
        when(mStatusLine.getStatusCode()).thenReturn(StatusCodes.HTTP_OK);
        when(mHttpEntity.getContent()).thenReturn(IOUtils.toInputStream(JSON_LIST_TITLE));

        mJsonDeleteExecutor.executeReturnsObjectsList();

        ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mDeleteExecutor).delete(eq(URL), mapArgumentCaptor.capture(), any(HttpEntity.class));

        Map<String, Object> usedHeaderData = mapArgumentCaptor.getValue();
        assertThat(usedHeaderData.size(), is(greaterThan(0)));
        assertThat(usedHeaderData.keySet(), contains(KamaParam.ACCEPT));
        assertThat(usedHeaderData.get(KamaParam.ACCEPT).toString(), is(KamaParam.APPLICATION_JSON));
    }

    public void testNoReturnTypeClass() throws Exception {
        mJsonDeleteExecutor.setUrl(URL);

        when(mHttpResponse.getStatusLine()).thenReturn(mStatusLine);
        when(mStatusLine.getStatusCode()).thenReturn(StatusCodes.HTTP_OK);
        when(mHttpEntity.getContent()).thenReturn(IOUtils.toInputStream(JSON_SINGLE));

        Object result = mJsonDeleteExecutor.execute();

        verify(mDeleteExecutor).delete(eq(URL), any(Map.class), any(HttpEntity.class));

        assertThat(result, is(nullValue()));
    }

    public void testNoUrlThrowsIllegalArgumentException() throws KamaException {
        mJsonDeleteExecutor.setReturnTypeClass(RETURN_TYPE);
        try {
            mJsonDeleteExecutor.execute();
            fail(MISSING_EXCEPTION);
        } catch (IllegalArgumentException ignored) {
            /* Success */
        }
    }


    public void testNonHttpOkResult() throws Exception {
        mJsonDeleteExecutor.setUrl(URL);
        mJsonDeleteExecutor.setReturnTypeClass(RETURN_TYPE);

        when(mHttpResponse.getStatusLine()).thenReturn(mStatusLine);
        when(mStatusLine.getStatusCode()).thenReturn(StatusCodes.HTTP_NOT_FOUND);
        when(mHttpEntity.getContent()).thenReturn(IOUtils.toInputStream(JSON_SINGLE));

        try {
            mJsonDeleteExecutor.execute();
            fail(MISSING_EXCEPTION);
        } catch (KamaException ignored) {
            /* Success */
        }
    }

    @SuppressWarnings("PublicField")
    public static class ParseObject {

        @JsonProperty("integer")
        public int mInteger;

    }
}
