package com.baolong.pictures.infrastructure.manager.task;

/**
 * 任务接口（统一任务的标准）
 * <p>
 * 实现本接口的类需定义具体的任务逻辑。作为函数式接口，可通过Lambda表达式或方法引用实现。
 * <p>
 * 默认将Runnable的run()方法委托给doWork()实现，简化任务定义。
 *
 * @author Baolong 2025年03月19 13:54
 * @version 1.0
 * @since 1.8
 */
@FunctionalInterface
public interface Task extends Runnable {
	/**
	 * 定义具体的任务执行逻辑
	 */
	void doTask();

	/**
	 * 实现 Runnable 接口的 run 方法，默认调用 doWork 方法
	 */
	default void run() {
		doTask();
	}
}
