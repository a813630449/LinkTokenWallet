package com.xiaoliang.wallet.model;

import com.xiaoliang.wallet.utils.JsonHelper;

public class BaseVo {
    public String toJson() {
        return JsonHelper.toJson(this);
    }
}
