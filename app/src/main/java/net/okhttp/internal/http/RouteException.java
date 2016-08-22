/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.okhttp.internal.http;

/**
 * An exception thrown to indicate a problem connecting via a single Route. Multiple attempts may
 * have been made with alternative protocols, none of which were successful.
 */
public final class RouteException extends Exception {
  private static final java.lang.reflect.Method addSuppressedExceptionMethod;
  static {
	  java.lang.reflect.Method m;
    try {
      m = Throwable.class.getDeclaredMethod("addSuppressed", Throwable.class);
    } catch (Exception e) {
      m = null;
    }
    addSuppressedExceptionMethod = m;
  }
  private java.io.IOException lastException;

  public RouteException(java.io.IOException cause) {
    super(cause);
    lastException = cause;
  }

  public java.io.IOException getLastConnectException() {
    return lastException;
  }

  public void addConnectException(java.io.IOException e) {
    addSuppressedIfPossible(e, lastException);
    lastException = e;
  }

  private void addSuppressedIfPossible(java.io.IOException e, java.io.IOException suppressed) {
    if (addSuppressedExceptionMethod != null) {
      try {
        addSuppressedExceptionMethod.invoke(e, suppressed);
      } catch (java.lang.reflect.InvocationTargetException | IllegalAccessException ignored) {
      }
    }
  }
}
