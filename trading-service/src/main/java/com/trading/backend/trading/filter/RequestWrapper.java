package com.google.backend.trading.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 针对post请求，截取body参数
 * 
 * @author borkes.mao
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     *            The request to wrap
     * @throws IllegalArgumentException
     *             if the request is null
     */
    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        int offset = 0;
        byte[] buff = new byte[200];
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        InputStream inputStream = request.getInputStream();
        while ((offset = inputStream.read(buff)) != -1) {
            bao.write(buff, 0, offset);
        }
        body = bao.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {}

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    public String getBody() {
        return new String(this.body);
    }
}
