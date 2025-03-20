package com.baolong.pictures.infrastructure.utils;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.cglib.core.ReflectUtils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MyBatisPlus 的 SFunction 工具类
 * <p>
 * 适用于 MyBatisPlus
 *
 * @author Baolong 2025年03月07 13:32
 * @version 1.0
 * @since 1.8
 */
public class SFLambdaUtil {

	/**
	 * 获取类中所有字段名称
	 *
	 * @param clazz 类
	 * @return 字段名称列表
	 */
	public static List<String> getFieldNames(Class<?> clazz) {
		return getFieldNames(clazz, new HashSet<>());
	}

	/**
	 * 获取类中字段名称（排除指定字段）
	 *
	 * @param clazz         类
	 * @param excludeFields 需要排除的字段名
	 * @return 过滤后的字段名称列表
	 */
	public static List<String> getFieldNames(Class<?> clazz, String... excludeFields) {
		return getFieldNames(clazz, new HashSet<>(Arrays.asList(excludeFields)));
	}

	/**
	 * 获取类中字段名称（排除指定字段）
	 *
	 * @param clazz         类
	 * @param excludeFields 需要排除的字段名
	 * @return 过滤后的字段名称列表
	 */
	public static List<String> getFieldNames(Class<?> clazz, Set<String> excludeFields) {
		List<String> fieldNames = new ArrayList<>();

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals("serialVersionUID") && field.getType() == long.class
					&& Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			// 过滤用户指定字段
			if (excludeFields != null && !excludeFields.isEmpty()) {
				if (excludeFields.contains(field.getName())) continue;
			}
			fieldNames.add(field.getName());
		}
		return fieldNames;
	}

	// region 函数式接口引用指定字段

	@FunctionalInterface
	public interface SerializableGetter<T, R> extends Function<T, R>, Serializable {
	}

	/**
	 * 获取类中字段名称（函数式接口引用指定字段）
	 *
	 * @param clazz                 类
	 * @param excludeFunctionFields 需要排除的字段函数式接口引用
	 * @return 过滤后的字段名称列表
	 */
	public static <T, R> List<String> getFieldNames(Class<T> clazz, SerializableGetter<T, R> excludeFunctionFields) {
		return getFieldNames(clazz, analysis(excludeFunctionFields));
	}

	/**
	 * 获取类中字段名称（函数式接口引用指定字段）
	 *
	 * @param clazz                 类
	 * @param excludeFunctionFields 需要排除的字段函数式接口引用
	 * @return 过滤后的字段名称列表
	 */
	@SafeVarargs
	public static <T, R> List<String> getFieldNames(Class<T> clazz, SerializableGetter<T, R>... excludeFunctionFields) {
		return getFieldNames(clazz, Arrays.stream(excludeFunctionFields).map(SFLambdaUtil::analysis).collect(Collectors.toSet()));
	}

	/**
	 * 通过getter方法引用获取字段名
	 *
	 * @param getter 方法引用，如 User::getUsername
	 * @return 字段名，如 "username"
	 */
	public static <T, R> String analysis(SerializableGetter<T, R> getter) {
		try {
			// 通过序列化机制获取方法信息
			Method method = getter.getClass().getDeclaredMethod("writeReplace");
			method.setAccessible(true);
			SerializedLambda lambda = (SerializedLambda) method.invoke(getter);

			// 解析方法名并转换为字段名
			String methodName = lambda.getImplMethodName();
			if (methodName.startsWith("get")) {
				return Introspector.decapitalize(methodName.substring(3));
			} else if (methodName.startsWith("is")) {
				return Introspector.decapitalize(methodName.substring(2));
			}
			throw new IllegalArgumentException("无效的getter方法: " + methodName);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("解析字段名失败，请确保：\n"
					+ "1. 使用FieldNameUtils.SerializableFunction\n"
					+ "2. 添加JVM参数: -Djdk.serializableLambda=true", e);
		}
	}

	// endregion 函数式接口引用指定字段

	// region 根据属性获取到 MyBatisPlus 的 SFunction

	public static final Map<Class<?>, PropertyDescriptor[]> cache = new HashMap<>();

	/**
	 * 获取类中的 SFunction
	 *
	 * @param clazz 类
	 * @param prop  类中的属性名称
	 * @return SFunction
	 */
	public static <T> SFunction<T, ?> getSFunction(Class<T> clazz, String prop) {
		try {
			PropertyDescriptor[] beanGetters;
			if (cache.containsKey(clazz)) {
				beanGetters = cache.get(clazz);
			} else {
				beanGetters = ReflectUtils.getBeanGetters(clazz);
				cache.put(clazz, beanGetters);
			}
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			Optional<PropertyDescriptor> optional = Arrays.stream(beanGetters)
					.filter(pd -> pd.getName().equals(prop))
					.findFirst();
			if (optional.isPresent()) {
				// 反射获取getter方法
				Method readMethod = optional.get().getReadMethod();
				// 拿到方法句柄
				final MethodHandle methodHandle = lookup.unreflect(readMethod);
				// 创建动态调用链
				CallSite callSite = LambdaMetafactory.altMetafactory(
						lookup,
						"apply",
						MethodType.methodType(SFunction.class),
						MethodType.methodType(Object.class, Object.class),
						methodHandle,
						MethodType.methodType(readMethod.getReturnType(), clazz),
						LambdaMetafactory.FLAG_SERIALIZABLE
				);
				return (SFunction<T, ?>) callSite.getTarget().invokeExact();
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	// endregion 根据属性获取到 MyBatisPlus 的 SFunction
}
