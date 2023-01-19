package cn.wildfirechat.app.tools;

import cn.wildfirechat.app.RestResult;
import cn.wildfirechat.sdk.model.IMResult;

public class ResponseConverterUtils {

    public static RestResult converter(IMResult imResult){
        return RestResult.result(imResult.code, imResult.msg, imResult.result);
    }
}
