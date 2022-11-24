package com.example.framework.common.utils;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * 基本消息转换器
 * implements HttpMessageConvert
 */
public class BasicMessageConvert {

    private HttpServletRequest request;


    public BasicMessageConvert(HttpServletRequest request) {
        this.request = request;
    }

    //@Override
    public MultiValueMap getParams() {
        MultiValueMap ret = new LinkedMultiValueMap();
        Map<String,String[]> map = request.getParameterMap();
        for(Iterator<Map.Entry<String, String[]>> itr = map.entrySet().iterator(); itr.hasNext();){
            Map.Entry<String, String[]> entry = itr.next();
            String key = entry.getKey();
            String[] value = entry.getValue();
            ret.put(key, new ArrayList(Arrays.asList(value[0])));
        }
        // 提取params参数
        //extractParams(ret);
        return ret;
    }
}
