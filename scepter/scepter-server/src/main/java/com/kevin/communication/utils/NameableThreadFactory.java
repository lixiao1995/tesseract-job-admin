package com.kevin.communication.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: kevin
 * @description: 线程工厂
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:26
 */
public class NameableThreadFactory implements ThreadFactory {
	
	private final ThreadGroup group;
	private final String namePrefix;
	private final AtomicInteger threadId;
	private final boolean isDaemon;

	public NameableThreadFactory(String namePrefix, boolean isDaemon) {
		SecurityManager s = System.getSecurityManager();
		this.group = (s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
		this.namePrefix = namePrefix;
		this.threadId = new AtomicInteger(0);
		this.isDaemon = isDaemon;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(this.group, r, this.namePrefix + "-" + this.threadId.getAndIncrement());
		t.setDaemon(this.isDaemon);
		if (t.getPriority() != 5) {
			t.setPriority(3);
		}
		return t;
	}
}
