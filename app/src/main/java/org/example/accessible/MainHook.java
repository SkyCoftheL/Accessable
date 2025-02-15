package org.example.accessible;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        //做数据库查询，以便确定是否需要开启...


//        public void init() throws NoSuchMethodException {
//            findAndHookMethod("com.android.systemui.statusbar.notification.collection.coordinator.FoldCoordinator$shadeExpansionListener$1",
//                    isMoreAndroidVersion(35) ? "onPanelExpansionChanged$1" : "onPanelExpansionChanged", "com.android.systemui.shade.ShadeExpansionChangeEvent",
//                    new MethodHook() {
//                        @Override
//                        protected void before(XC_MethodHook.MethodHookParam param) {
//                            Object FoldCoordinator = XposedHelpers.getObjectField(param.thisObject, "this$0");
//                            XposedHelpers.setObjectField(FoldCoordinator, "mPendingNotifications", new ArrayList<>());
//                        }
//                    }
//            );
//
    }

}
