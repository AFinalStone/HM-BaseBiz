package com.hm.iou.base.utils;

import com.hm.iou.network.exception.ApiException;
import com.hm.iou.sharedata.model.BaseResponse;

import io.reactivex.functions.Function;

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
                if (response.isSuccess() && response.getErrorCode() == 0) {
                    return response.getData();
                } else {
                    throw new ApiException("" + response.getErrorCode(), response.getMessage());
                }
            }
        };
    }
}
