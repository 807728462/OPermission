package com.oyf.permission.aspectj;

import android.content.Context;
import android.os.Build;

import com.oyf.permission.PermissionAspectActivity;
import com.oyf.permission.annotation.PermissionDenied;
import com.oyf.permission.annotation.PermissionDeniedForever;
import com.oyf.permission.annotation.PermissionNeed;
import com.oyf.permission.utils.PermissionUtil;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @创建者 oyf
 * @创建时间 2019/12/5 17:04
 * @描述 权限申请拦截器
 **/
@Aspect
public class PermissionAspectJ {

    private final static String POINTCUT = "execution(@com.oyf.permission.annotation.PermissionNeed * *(..))";
    //直接方法中携带参数的
    private final static String POINTCUT_PARAMS = "execution(@com.oyf.permission.annotation.PermissionNeed * *(..))&& @annotation(permissionNeed)";

    //切入点，并且带参数
    @Pointcut(POINTCUT_PARAMS)
    public void permissionParams(PermissionNeed permissionNeed) {
    }
    //切入点
    @Pointcut(POINTCUT)
    public void permission() {
    }


    @Around("permission()")
    public void permissionAspectj(final ProceedingJoinPoint joinPoint) throws Throwable {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            joinPoint.proceed();
            return;
        }
        Context context = getContext(joinPoint);
        Signature signature = joinPoint.getSignature();
        //如果不是方法签名
        if (!(signature instanceof MethodSignature)) {
            joinPoint.proceed();
            return;
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        final Method method = methodSignature.getMethod();
        PermissionNeed permissionNeed = method.getAnnotation(PermissionNeed.class);
        if (permissionNeed == null) {
            joinPoint.proceed();
            return;
        }
        String[] permissions = permissionNeed.permissions();
        int requestCode = permissionNeed.requestCode();
        PermissionAspectActivity.startActivity(context, permissions, requestCode, new PermissionAspectActivity.PermissionCallback() {
            @Override
            public void PermissionGranted(int requestCode) {
                try {
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onPermissionDeniedForever(int code) {
                invokeAnnotation(joinPoint.getThis(), PermissionDeniedForever.class, code);
            }

            @Override
            public void onPermissionDenied(int code) {
                invokeAnnotation(joinPoint.getThis(), PermissionDenied.class, code);
            }
        });

    }
    /**
     * 执行权限请求拒绝结果
     *
     * @param object
     * @param aClass
     * @param code
     */
    public void invokeAnnotation(Object object, Class aClass, int code) {
        try {
            Class<?> objectClass = object.getClass();
            //获取类中的所有方法
            Method[] declaredMethods = objectClass.getDeclaredMethods();
            if (declaredMethods.length == 0) {
                return;
            }
            for (Method declaredMethod : declaredMethods) {
                //判断是否存在当前注解
                boolean annotationPresent = declaredMethod.isAnnotationPresent(aClass);
                if (annotationPresent) {
                    // 判断是否有且仅有一个 int 参数
                    Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                    if (parameterTypes.length != 1) {
                        throw new RuntimeException("只能存在一个参数");
                    }
                    //设置允许访问私有方法
                    declaredMethod.setAccessible(true);
                    declaredMethod.invoke(object, code);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取context     如果不是context 则通过反射获取application
     *
     * @param joinPoint
     * @return
     */
    public Context getContext(final ProceedingJoinPoint joinPoint) {
        final Object obj = joinPoint.getThis();
        if (obj instanceof Context) {// 如果切入点是一个类？那么这个类的对象是不是context？
            return (Context) obj;
        } else {// 如果切入点不是Context的子类呢？ //jointPoint.getThis，其实是得到切入点所在类的对象
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {//
                if (args[0] instanceof Context) {//看看第一个参数是不是context
                    return (Context) args[0];
                } else {
                    return PermissionUtil.getApplication();//如果不是，那么就只好hook反射了
                }
            } else {
                return PermissionUtil.getApplication();//如果不是，那么就只好hook反射了
            }
        }
    }

}
