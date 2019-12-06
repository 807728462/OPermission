# OPermission
一个使用AspectJ的权限申请框架学习，主要学习内部的AOP拦截使用


使用时候一定要注意  
@Pointcut("execution(@com.oyf.permission.annotation.PermissionNeed * *(..))")
这中间的字符串不能打错了 否则可能会出错或者无效果

异常一：com.android.build.api.transform.TransformException: java.util.zip.ZipException: zip file is empty
解决办法：检查字符串是否出错再三检查
