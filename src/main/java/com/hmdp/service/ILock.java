package com.hmdp.service;

import java.util.concurrent.TimeUnit;

/**
 * @AUTHOR: qiukui
 * @CREATE: 2023-08-25-20:47
 */
public interface ILock {

    public boolean tryLock(long timeout, TimeUnit unit);

    public void unLock();
}
