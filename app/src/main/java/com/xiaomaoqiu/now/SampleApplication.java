package com.xiaomaoqiu.now;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by long on 17/4/4.
 */

public class SampleApplication extends TinkerApplication {
    public SampleApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.xiaomaoqiu.now.PetAppLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}
