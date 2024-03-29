package com.hm.iou.base.utils;

import com.hm.iou.network.exception.ApiException;
import com.hm.iou.sharedata.model.BaseResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hjy on 18/4/26.<br>
 */

public class RxUtil {

    /**
     * 统一处理请求结果
     *
     * @param <T>
     * @return
     */
    public static <T> Function<BaseResponse<T>, T> handleResponse() {
        return new Function<BaseResponse<T>, T>() {
            @Override
            public T apply(BaseResponse<T> response) throws Exception {
                if (response.getErrorCode() == 0) {
                    T data = response.getData();
                    if (data == null) {
                        throw new ResponseDataEmptyException();
                    }
                    return data;
                } else {
                    throw new ApiException("" + response.getErrorCode(), response.getMessage());
                }
            }
        };
    }

    /**
     * 统一处理请求结果，为了兼容以前老的接口协议，需要同时判断success和code值
     *
     * @param <T>
     * @return
     */
    public static <T> Function<BaseResponse<T>, T> handleResponseEx() {
        return new Function<BaseResponse<T>, T>() {
            @Override
            public T apply(BaseResponse<T> response) throws Exception {
                if (response.isSuccess() && response.getErrorCode() == 0) {
                    T data = response.getData();
                    if (data == null) {
                        throw new ResponseDataEmptyException();
                    }
                    return data;
                } else {
                    throw new ApiException("" + response.getErrorCode(), response.getMessage());
                }
            }
        };
    }

}
